package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ItemServiceImpl
 * @Description 商品详情页面使用的接口的实现类
 * @Author yzchao
 * @Date 2022/10/26 18:56
 * @Version V1.0
 */
@Service
@Log4j2
public class ItemServiceImpl implements ItemService {

    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private BaseTradeMarkMapper baseTradeMarkMapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    /**
     * @param skuId
     * @return com.atguigu.gmall.model.product.SkuInfo
     * @Description 远程调用微服务查询skuInfo信息
     * @Date 18:56 2022/10/26
     * @Param [skuId]
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return skuInfoMapper.selectById(skuId);
    }

    /**
     * @param skuId
     * @return com.atguigu.gmall.model.product.SkuInfo
     * @Description 使用缓存优化查询
     * @Date 20:35 2022/10/28
     * @Param [skuId]
     */
    @Override
    public SkuInfo getSkuInfoFromRedisOrMysql(Long skuId) {
        //查询redis中是否存在商品的数据
        SkuInfo skuInfo = (SkuInfo) redisTemplate.opsForValue().get("sku:" + skuId + ":info");

        //redis中若有商品的数据 则返回
        if (skuInfo != null) {
            return skuInfo;
        }
        //若没有商品的数据，则获取锁
        RLock lock = redissonClient.getLock("sku:" + skuId + ":info");
        //尝试加锁
        try {
            if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                log.info("加锁成功了");
                try {
                    //加锁成功的线程去数据库中查询数据
                    skuInfo = skuInfoMapper.selectById(skuId);
                    //判断数据库中商品的数据是否存在，1.数据库中有 2.数据库中没有
                    if (skuInfo == null || skuInfo.getId() == null) {
                        //数据库没有
                        skuInfo = new SkuInfo();
                        redisTemplate.opsForValue().set("sku:" + skuId + ":info", skuInfo, 5 * 60, TimeUnit.SECONDS);
                    } else {
                        //数据库中有  写入redis
                        redisTemplate.opsForValue().set("sku:" + skuId + ":info", skuInfo, 24 * 60 * 60, TimeUnit.SECONDS);
                    }
                    return skuInfo;
                } catch (Exception e) {
                    log.error("商品加锁成功，处理查询商品数据库的时候出错，商品的skuId为" + skuId);
                } finally {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("商品加锁失败，商品的skuId为" + skuId);
        }
        return null;
    }
    /**
     * @param category3Id
     * @return com.atguigu.gmall.model.product.BaseCategoryView
     * @Description 远程分类信息查询 根据三级分类ID 查询一级分类 二级分类 三级分类
     * @Date 20:48 2022/10/26
     * @Param [category3Id]
     */
    @Override
    public BaseCategoryView getBaseCategory(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    /**
     * @param skuId
     * @return java.util.List<com.atguigu.gmall.model.product.SkuImage>
     * @Description 远程调用微服务查询skuImage图片信息
     * @Date 21:15 2022/10/26
     * @Param [skuId]
     */
    @Override
    public List<SkuImage> SkuImageList(Long skuId) {
        return skuImageMapper.selectList(new LambdaQueryWrapper<SkuImage>().
                eq(SkuImage::getSkuId, skuId));
    }

    /**
     * @param skuId
     * @return java.math.BigDecimal
     * @Description 远程调用查询sku价格
     * @Date 22:32 2022/10/26
     * @Param [skuId]
     */
    @Override
    public BigDecimal getSkuInfoPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo.getPrice();
    }

    /**
     * @param skuId
     * @param spuId
     * @return java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     * @Description 查询销售属性名称和值列表同时标识出当前选中的值是哪几个的sql实现
     * @Date 10:10 2022/10/27
     * @Param [skuId, spuId]
     */
    @Override
    public List<SpuSaleAttr> getSpuAttrBySpuIdAndSkuId(Long skuId,Long spuId){
        return spuSaleAttrMapper.getSpuAttrBySpuIdAndSkuId(skuId,spuId);
    }

    /**
     * @param spuId
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Description 键值对的查询实现
     * @Date 11:50 2022/10/27
     * @Param [spuId]
     */
    @Override
    public Map<Object,Object> getSkuValuesList(Long spuId) {
        List<Map<Object, Object>> maps = skuSaleAttrValueMapper.getSkuValuesList(spuId);

        Map<Object, Object> concurrentMap = new ConcurrentHashMap<>();
        maps.stream().forEach(skuSaleAttrValue -> {
            Object skuId = skuSaleAttrValue.get("sku_id");
            Object valuesId = skuSaleAttrValue.get("values_id");
            concurrentMap.put(valuesId,skuId);
        });
        return concurrentMap;
    }

    /**
     * @param id
     * @return com.atguigu.gmall.model.product.BaseTrademark
     * @Description 查询品牌
     * @Date 20:28 2022/11/2
     * @Param [id]
     */
    @Override
    public BaseTrademark getBaseTrademark(Long id) {
        BaseTrademark baseTrademark = baseTradeMarkMapper.selectById(id);
        if (baseTrademark == null) {
            return null;
        }
        return baseTrademark;
    }

    /**
     * @param skuId
     * @return java.util.List<com.atguigu.gmall.model.product.BaseAttrInfo>
     * @Description 查询指定skuId的skuInfo的平台属性
     * @Date 20:50 2022/11/2
     * @Param []
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfoBySkuId(Long skuId) {
        return baseAttrInfoMapper.selectBaseAttrInfoBySkuId(skuId);

    }
}

