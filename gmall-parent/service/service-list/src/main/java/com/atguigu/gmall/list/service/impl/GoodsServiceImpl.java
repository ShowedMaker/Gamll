package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.list.dao.GoodsDao;
import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsServiceImpl
 * @Description
 * @Author yzchao
 * @Date 2022/11/2 16:43
 * @Version V1.0
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * @return void
     * @Description 上架接口
     * @Date 16:43 2022/11/2
     * @Param []
     */
    @Override
    public void goodsFromDBToEs(Long skuId) {
        //查询
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        //判断
        if (skuInfo == null || skuInfo.getId() == null) {
            return;
        }
        //转换
        Goods goods = new Goods();
        goods.setId(skuInfo.getId());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        //查询实时价格
        BigDecimal skuInfoPrice = productFeignClient.getSkuInfoPrice(skuId);
        goods.setPrice(skuInfoPrice.doubleValue());
        goods.setCreateTime(new Date());
        BaseTrademark baseTradeMark = productFeignClient.getBaseTradeMark(skuInfo.getTmId());
        if (baseTradeMark == null){
            return;
        }
        goods.setTmId(baseTradeMark.getId());
        goods.setTmLogoUrl(baseTradeMark.getLogoUrl());
        goods.setTmName(baseTradeMark.getTmName());

        BaseCategoryView baseCategoryView = productFeignClient.getBaseCategory(skuInfo.getCategory3Id());
        goods.setCategory1Id(baseCategoryView.getCategory1Id());
        goods.setCategory2Id(baseCategoryView.getCategory2Id());
        goods.setCategory3Id(baseCategoryView.getCategory3Id());
        goods.setCategory1Name(baseCategoryView.getCategory1Name());
        goods.setCategory2Name(baseCategoryView.getCategory2Name());
        goods.setCategory3Name(baseCategoryView.getCategory3Name());

        List<BaseAttrInfo> baseAttrInfoList = productFeignClient.getBaseAttrInfoBySkuId(skuId);

        List<SearchAttr> searchAttrList = baseAttrInfoList.stream().map(baseAttrInfo -> {
            //初始化
            SearchAttr searchAttr = new SearchAttr();
            searchAttr.setAttrId(baseAttrInfo.getId());
            searchAttr.setAttrName(baseAttrInfo.getAttrName());
            searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
            //返回
            return searchAttr;
        }).collect(Collectors.toList());//平台属性

        goods.setAttrs(searchAttrList);
        //写入es
        goodsDao.save(goods);
    }

    /**
     * @return void
     * @Description 下架接口
     * @Date 16:43 2022/11/2
     * @Param []
     */
    @Override
    public void goodsRemoveFromEs(Long goodsId) {
        //将商品从es中删除掉
        goodsDao.deleteById(goodsId);
    }


    @Resource
    private RedisTemplate redisTemplate;

    /**
     * @param goodsId
     * @return void
     * @Description 热度值
     * @Date 21:59 2022/11/5
     * @Param [goodsId]
     */
    @Override
    public void addHotScore(Long goodsId) {

//        Long increment =
//                redisTemplate.opsForValue().increment("Goods_Hot_Score_" + goodsId, 1);


        //参数校验
        if(goodsId == null){return;}

        //查商品
        Optional<Goods> optionalGoods = goodsDao.findById(goodsId);
        if (optionalGoods.isPresent()) {
            //使用zset进行热度值增加
            Double hotScore = redisTemplate.opsForZSet().incrementScore("Goods_Hot_Score_",
                    goodsId+"", 1);

        //每200更新一次
        if(hotScore.intValue()  % 200 == 0) {
            Goods goods = optionalGoods.get();
            goods.setHotScore(hotScore.longValue());

            //保存
            goodsDao.save(goods);
        }
   }

    }
}
