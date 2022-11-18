package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.ProductStatusConst;
import com.atguigu.gmall.common.exception.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.list.GoodsFeignClient;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName ManageServiceImpl
 * @Description 商品管理的接口类的实现类
 * @Author yzchao
 * @Date 2022/10/24 15:54
 * @Version V1.0
 */
@Service
@Transactional(rollbackFor = Exception.class) //不指定就是runtime级别
@Log4j2
public class ManageServiceImpl implements ManageService {

    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;
    @Resource
    private BaseCategory2Mapper baseCategory2Mapper;
    @Resource
    private BaseCategory3Mapper baseCategory3Mapper;
    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;
    @Resource
    private BaseTradeMarkMapper baseTradeMarkMapper;
    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Resource
    private SpuInfoMapper spuInfoMapper;
    @Resource
    private SpuImageMapper spuImageMapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Resource
    private SkuAttrValueMapper skuAttrValueMapper;
    @Resource
    private GoodsFeignClient goodsFeignClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @return java.util.List<com.atguigu.gmall.model.product.BaseCategory1>
     * @Description 查询所有的一级分类
     * @Date 18:04 2022/10/24
     * @Param []
     */
    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    /**
     * @return java.util.List<com.atguigu.gmall.model.product.BaseCategory2>
     * @Description 根据一级分类查询所有的二级分类
     * @Date 18:04 2022/10/24
     * @Param [category1Id]
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        return baseCategory2Mapper.selectList(new LambdaQueryWrapper<BaseCategory2>()
                .eq(BaseCategory2::getCategory1Id, category1Id));

    }

    /**
     * @return java.util.List<com.atguigu.gmall.model.product.BaseCategory3>
     * @Description 根据二级分类查询所有的三级分类
     * @Date 18:05 2022/10/24
     * @Param [category2Id]
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        return baseCategory3Mapper.selectList(new LambdaQueryWrapper<BaseCategory3>()
                .eq(BaseCategory3::getCategory2Id, category2Id));
    }

    /**
     * @param baseAttrInfo
     * @return void
     * @Description 保存平台属性信息
     * @Date 18:16 2022/10/24
     * @Param [baseAttrInfo]
     */
    @Override
    public void save(BaseAttrInfo baseAttrInfo) {
        //参数校验
        if (null == baseAttrInfo || null == baseAttrInfo.getAttrName()) {
            throw new RuntimeException("参数错误");
        }

        //保存平台属性名称标的数据
        if(null != baseAttrInfo.getId()){
            //修改
            int update = baseAttrInfoMapper.updateById(baseAttrInfo);
            if (update < 0) {
                throw new RuntimeException("修改错误");
            }

            //删除指定旧值
            int delete = baseAttrValueMapper.delete(new LambdaQueryWrapper<BaseAttrValue>()
                    .eq(BaseAttrValue::getAttrId,baseAttrInfo.getId()));
            if (delete < 0) {
                throw new RuntimeException("修改错误");
            }
        }else{
            //新增
            int result = baseAttrInfoMapper.insert(baseAttrInfo);
            if (result <= 0) {
                throw new RuntimeException("新增平台属性失败");
            }
        }


        //保存完成后，平台属性对象就有id
        Long baseAttrInfoId = baseAttrInfo.getId();


 /*       //将id补充到值的数据中去
            //获取用户给的值列表
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            //判断值是否为空
           if (!StringUtils.isEmpty(baseAttrValue.getValueName())) {
               //将baseAttrInfoId设置到baseAttrValue表中的
               baseAttrValue.setAttrId(baseAttrInfoId);
               int insertValue = baseAttrValueMapper.insert(baseAttrValue);
               if (insertValue <= 0) {
                   throw new RuntimeException("新增平台属性值失败");
               }
           }
        }
*/
        //将id补充到值的数据中去 流式编程
        //获取用户给的值列表
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        attrValueList.stream().forEach(baseAttrValue -> {
            //判断值是否为空
            if (!StringUtils.isEmpty(baseAttrValue.getValueName())) {
                //将baseAttrInfoId设置到baseAttrValue表中的
                baseAttrValue.setAttrId(baseAttrInfoId);
                int insertValue = baseAttrValueMapper.insert(baseAttrValue);
                if (insertValue <= 0) {
                    throw new RuntimeException("新增平台属性值失败");
                }
            }
        });
    }

    /**
     * @return java.util.List
     * @Description 获得平台属性列表
     * @Date 20:13 2022/10/24
     * @Param []
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfo(Long category3Id) {
        List<BaseAttrInfo> list = baseAttrInfoMapper.selectBaseAttrInfoByCategoryId(category3Id);
        return list ;
    }

    /**
     * @param id
     * @return void
     * @Description 删除平台属性和值
     * @Date 22:54 2022/10/24
     * @Param [id]
     */
    @Override
    public void deleteBaseAttrInfo(Long id) {
        //参数校验
        if (id == null) {
            return;
        }
        int delete = baseAttrInfoMapper.deleteById(id);

        //删除平台属性名
        if (delete < 0) {
            throw new RuntimeException("删除平台属性名称失败");
        }
        //删除平台属性值
        int deleteValue = baseAttrValueMapper.delete(new LambdaQueryWrapper<BaseAttrValue>()
                .eq(BaseAttrValue::getAttrId, id));
        if (deleteValue < 0) {
            throw new RuntimeException("删除平台属性值失败");
        }
    }



    /**
     * @return void
     * @Description 获取添加SPU时品牌值（所有值）
     * @Date 11:56 2022/10/25
     * @Param []
     */
    @Override
    public List<BaseTrademark> getBaseTradeMark() {
        return baseTradeMarkMapper.selectList(null);
    }


    /**
     * @return java.util.List<com.atguigu.gmall.model.product.BaseSaleAttr>
     * @Description 后台管理系统销售属性查询
     * @Date 14:10 2022/10/25
     * @Param []
     */
    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null) ;
    }


    /**
     * @param baseSaleAttr
     * @return void
     * @Description 上传spu信息
     * @Date 15:37 2022/10/25
     * @Param [baseSaleAttr]
     */
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //参数校验
        if (spuInfo == null) {
            throw new RuntimeException("参数错误");
        }

        //判断是新增还是修改
        if(null != spuInfo.getId()) {
            //修改
            int update = spuInfoMapper.updateById(spuInfo);
            if (update < 0) {
                throw new RuntimeException("spuinfo修改失败");
            }

            //删除旧的值 图片 销售属性名称 销售属性值
            int delete = spuInfoMapper.delete(new LambdaQueryWrapper<SpuInfo>()
                    .eq(SpuInfo::getId, spuInfo.getId()));
            if(delete < 0) {
                throw new RuntimeException("spuinfo修改失败");
            }

            int deleteImage = spuImageMapper.delete(new LambdaQueryWrapper<SpuImage>()
                    .eq(SpuImage::getSpuId, spuInfo.getId()));
            if (deleteImage < 0) {
                throw new RuntimeException("spuImage修改失败");
            }

            int deleteSaleAttr = spuSaleAttrMapper.delete(new LambdaQueryWrapper<SpuSaleAttr>()
                    .eq(SpuSaleAttr::getSpuId, spuInfo.getId()));
            if (deleteSaleAttr < 0) {
                throw new RuntimeException("deleteSaleAttr修改失败");
            }

            int deleteSaleAttrValue = spuSaleAttrValueMapper.delete(new LambdaQueryWrapper<SpuSaleAttrValue>()
                    .eq(SpuSaleAttrValue::getSpuId, spuInfo.getId()));
            if (deleteSaleAttrValue < 0) {
                throw new RuntimeException("deleteSaleAttrValue修改失败");
            }
        }else{
            //新增
            int insert = spuInfoMapper.insert(spuInfo);
            if(insert <= 0) {
                throw new RuntimeException("spuinfo新增失败");
            }
        }

        Long spuInfoId = spuInfo.getId();
        //新增图片
        addSpuImage(spuInfoId,spuInfo.getSpuImageList());
        //新增销售属性名称
        addSpuSaleAttrList(spuInfoId,spuInfo.getSpuSaleAttrList());
    }


    /**
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.atguigu.gmall.model.product.SpuInfo>
     * @Description 分页条件查询spuinfo信息
     * @Date 18:28 2022/10/25
     * @Param []
     */
    @Override
    public IPage<SpuInfo> getSpuInfoByPage(Integer page,Integer size,Long category3Id){
        IPage<SpuInfo> spuInfoPage = new Page<>();
        IPage<SpuInfo> selectPage = spuInfoMapper.selectPage(spuInfoPage, new LambdaQueryWrapper<SpuInfo>()
                .eq(SpuInfo::getCategory3Id, category3Id));
        return selectPage;
    }


    /**
     * @param spuId
     * @return void
     * @Description 根据spu的id查询这个spu的全部销售属性和每个销售属性对应的值列表
     * @Date 19:25 2022/10/25
     * @Param [spuId]
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
        List<SpuSaleAttr> list = spuSaleAttrMapper.getSpuSaleAttr(spuId);
        return list;
    }


    /**
     * @param spuId
     * @return java.util.List<com.atguigu.gmall.model.product.SpuImage>
     * @Description 根据spu的id查询这个spu的全部图片列表
     * @Date 20:05 2022/10/25
     * @Param [spuId]
     */
    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        List<SpuImage> spuImageList = spuImageMapper.selectList(new LambdaQueryWrapper<SpuImage>()
                .eq(SpuImage::getSpuId, spuId));
        return spuImageList;
    }

    /**
     * @param skuInfo
     * @return void
     * @Description 上传sku信息
     * @Date 21:10 2022/10/25
     * @Param [skuInfo]
     */
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //参数校验
        if (skuInfo == null || StringUtils.isEmpty(skuInfo.getSkuDefaultImg())) {
            throw new RuntimeException("参数错误");
        }
        //判断是新增还是修改
        if(null != skuInfo.getId()) {
            //修改
            int update = skuInfoMapper.updateById(skuInfo);
            if (update < 0) {
                throw new RuntimeException("skuInfo修改失败");
            }

            //todo 删除sku的图片信息
            int delete = skuImageMapper.delete(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId, skuInfo.getId()));
            //todo 删除sku的销售属性信息
            int deleteSkuSaleAttr = skuSaleAttrValueMapper.delete(new LambdaQueryWrapper<SkuSaleAttrValue>().eq(SkuSaleAttrValue::getSkuId, skuInfo.getId()));
            //todo 删除sku的平台属性信息
            int deleteSkuAttrValue = skuAttrValueMapper.delete(new LambdaQueryWrapper<SkuAttrValue>().eq(SkuAttrValue::getSkuId, skuInfo.getId()));

            if(delete < 0 || deleteSkuAttrValue < 0 || deleteSkuSaleAttr < 0){
                throw new RuntimeException("skuinfo修改失败");
            }

        }else{
            //新增
            int insert = skuInfoMapper.insert(skuInfo);
            if(insert <= 0) {
                throw new RuntimeException("skuinfo新增失败");
            }
        }

        //获取skuid
        Long skuInfoId = skuInfo.getId();
        //新增sku的图片
        addSkuImage(skuInfoId,skuInfo.getSkuImageList());
        //新增sku的销售属性信息
        addSkuSaleAttrValue(skuInfoId,skuInfo.getSkuSaleAttrValueList(),skuInfo.getSpuId());
        //新增sku的平台属性信息
        saveSkuAttrValue(skuInfoId,skuInfo.getSkuAttrValueList());
    }


    /**
     * @param page
     * @param size
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.atguigu.gmall.model.product.SpuInfo>
     * @Description 分页查询sku的信息
     * @Date 23:38 2022/10/25
     * @Param [page, size]
     */
    @Override
    public IPage<SkuInfo> getSkuInfoByPage(Integer page, Integer size) {
        IPage<SkuInfo> skuInfoPage = new Page<>();
        return skuInfoMapper.selectPage(skuInfoPage, null);
    }


    /**
     * @param skuId
     * @param status
     * @return void
     * @Description 上架或者下架sku商品
     * @Date 13:56 2022/10/26
     * @Param [skuId, status]
     */
    @Override
    public void OnSaleOrCancelSale(Long skuId, Short status) {
        //参数校验
        if(null == skuId){
            return;
        }
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(null == skuInfo || null == skuInfo.getId()){
            return;
        }
        skuInfo.setIsSale(status);
        int update = skuInfoMapper.updateById(skuInfo);
        if (update < 0) {
            throw new RuntimeException("上下架失败");
        }


        //将数据写入es 或者删掉  TODO  feign调用是一次成功或失败 (如果出现数据不一致，待mq优化，现在是强一致性，会失败)
        if(status.equals(ProductStatusConst.SKU_CANCEL_SALE)){
            //下架
            rabbitTemplate.convertAndSend("product_exchange1223","sku.down",skuId+"");
        }else{
            //上架
            rabbitTemplate.convertAndSend("product_exchange123","sku.upper",skuId+"");
        }
    }

    /**
     * @param baseTrademark
     * @return void
     * @Description 新增品牌
     * @Date 14:43 2022/10/26
     * @Param [baseTrademark]
     */
    @Override
    public void addOrUpdateBaseTrademark(BaseTrademark baseTrademark) {
        if (null == baseTrademark) {
            throw new RuntimeException("参数错误");
        }

        if(null != baseTrademark.getId()){
            //修改
            int update = baseTradeMarkMapper.updateById(baseTrademark);
            if (update < 0) {
                throw new RuntimeException("修改品牌失败");
            }
        }else{
            //新增
            int insert = baseTradeMarkMapper.insert(baseTrademark);
            if (insert <= 0) {
                throw new RuntimeException("新增品牌失败");
            }
        }
    }

    /**
     * @param page
     * @param size
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.atguigu.gmall.model.product.BaseTrademark>
     * @Description 分页查询品牌列表
     * @Date 14:55 2022/10/26
     * @Param [page, size]
     */
    @Override
    public IPage<BaseTrademark> getBaseTradeMarkByPage(Integer page, Integer size) {
        if(page == null){
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        IPage<BaseTrademark> baseTrademarkPage = new Page<>(page, size);
        IPage<BaseTrademark> pageModel = baseTradeMarkMapper.selectPage(baseTrademarkPage, null);
        return pageModel;
    }

    /**
     * @param id
     * @return com.atguigu.gmall.model.product.BaseTrademark
     * @Description 根据品牌ID查询品牌
     * @Date 15:36 2022/10/26
     * @Param [id]
     */
    @Override
    public BaseTrademark getBaseTradeMarkById(Long id) {
        if (id == null) {
            throw new RuntimeException("参数错误");
        }
        BaseTrademark baseTrademark = baseTradeMarkMapper.selectById(id);
        if (baseTrademark == null) {
            throw new RuntimeException("查询品牌不存在");
        }
        return baseTrademark;
    }

    /**
     * @param id
     * @return void
     * @Description 根据品牌ID删除品牌
     * @Date 15:43 2022/10/26
     * @Param [id]
     */
    @Override
    public void removeBaseTradeMark(Long id) {
        if (id == null) {
            throw new RuntimeException("参数错误");
        }
        int delete = baseTradeMarkMapper.deleteById(id);
        if (delete <= 0) {
            throw new RuntimeException("删除品牌失败");
        }
    }

    //新增sku的平台属性信息
    private void saveSkuAttrValue(Long skuInfoId, List<SkuAttrValue> skuAttrValueList) {
        skuAttrValueList.stream().forEach(skuAttrValue -> {
            skuAttrValue.setSkuId(skuInfoId);
            int insert = skuAttrValueMapper.insert(skuAttrValue);
            if (insert <= 0) {
                throw new RuntimeException("新增sku平台属性信息失败");
            }
        });
    }

    //新增sku的销售属性信息
    private void addSkuSaleAttrValue(Long skuInfoId, List<SkuSaleAttrValue> skuSaleAttrValueList,Long spuId) {
        skuSaleAttrValueList.stream().forEach(skuSaleAttrValue->{
            skuSaleAttrValue.setSkuId(skuInfoId);
            skuSaleAttrValue.setSpuId(spuId);
            int insert = skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            if (insert <= 0) {
                throw new RuntimeException("新增sku销售属性信息失败");
            }
        });

    }

    //新增sku的图片
    private void addSkuImage(Long skuInfoId, List<SkuImage> skuImageList) {
        skuImageList.stream().forEach(skuImage -> {
            skuImage.setSkuId(skuInfoId);
            int insert = skuImageMapper.insert(skuImage);
            if (insert <= 0) {
            throw new RuntimeException("新增sku销售属性图片失败");
            }
        });
    }

    //新增spu销售属性名称
    private void addSpuSaleAttrList(Long spuInfoId, List<SpuSaleAttr> spuSaleAttrList) {
        spuSaleAttrList.stream().forEach(spuSaleAttr->{
            spuSaleAttr.setSpuId(spuInfoId);
            int insert = spuSaleAttrMapper.insert(spuSaleAttr);
            if (insert <= 0) {
                throw new RuntimeException("新增属性名称失败");
            }
            //新增销售属性值
            addSpuSaleAttrValue(spuInfoId,spuSaleAttr.getSpuSaleAttrValueList(),spuSaleAttr.getSaleAttrName());

        });
    }

    //新增spu销售属性值
    private void addSpuSaleAttrValue(Long spuInfoId, List<SpuSaleAttrValue> spuSaleAttrValueList, String saleAttrName){
        spuSaleAttrValueList.stream().forEach(spuSaleAttrValue -> {
            spuSaleAttrValue.setSpuId(spuInfoId);
            spuSaleAttrValue.setSaleAttrName(saleAttrName);
            int insert = spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            if (insert <= 0) {
                throw new GmallException("新增销售属性失败");
            }
        });

    }

    //新增spu图片
    private void addSpuImage(Long spuInfoId, List<SpuImage> spuImageList) {
        spuImageList.stream().forEach(spuImage -> {
            spuImage.setSpuId(spuInfoId);
            int insert = spuImageMapper.insert(spuImage);
            if (insert <= 0) {
                throw new GmallException("新增spu销售属性图片失败");
            }
        });
    }
}
