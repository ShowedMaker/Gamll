package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.cart.util.CartThreadLocalUtil;
import com.atguigu.gmall.common.constant.CartConst;
import com.atguigu.gmall.common.exception.GmallException;
import com.atguigu.gmall.model.base.BaseEntity;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.util.concurrent.AtomicDouble;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @ClassName CartServiceImpl
 * @Description
 * @Author yzchao
 * @Date 2022/11/8 15:20
 * @Version V1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CartServiceImpl implements CartService {

    @Resource
    private CartInfoMapper cartInfoMapper;
    @Resource
    private ProductFeignClient productFeignClient;

    /**
     * @param skuId
     * @param number
     * @param username
     * @return void
     * @Description 添加购物车
     * @Date 15:19 2022/11/8
     * @Param [skuId, number, username]
     */
    @Override
    public void addCart(Long skuId, Integer number) {

//        if(number <= 0){
//            throw new RuntimeException("购物车商品数量不合法");
//        }

        //1.参数校验
        if (null == skuId){ return;}
        //2.查询商品是否存在
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if(null == skuInfo || null == skuInfo.getId()){ //之前查数据库的时候 没有就new了个空的skuInfo 有这个对象 但是具体的属性值为空 所以要检验getId()
            throw new RuntimeException("商品不存在");
        }

        //判断用户购物车中是否已有这个商品  新增或者数量合并
        LambdaQueryWrapper<CartInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CartInfo::getSkuId,skuId).eq(CartInfo::getUserId,CartThreadLocalUtil.get());
        CartInfo cartInfo = cartInfoMapper.selectOne(lambdaQueryWrapper);

        if (cartInfo == null){
            if(number <= 0){return;}
            //3.构建购物车对象
            cartInfo = new CartInfo();
            //新增
            //补全数据
            cartInfo.setUserId(CartThreadLocalUtil.get());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuId(skuId);
            cartInfo.setSkuNum(number);
            cartInfo.setCartPrice(productFeignClient.getSkuInfoPrice(skuId));
            cartInfo.setSkuName(skuInfo.getSkuName());
            //4.新增购物车
            int insert = cartInfoMapper.insert(cartInfo);
            if(insert <= 0){
                throw new RuntimeException("添加购物车失败！");
            }
        }else{
            //数量合并
            number = number + cartInfo.getSkuNum();
            if (number <= 0){
                //删除
                int delete = cartInfoMapper.deleteById(cartInfo.getId());
                if (delete <= 0 ){
                    throw new RuntimeException("删除购车是啊比！");
                }
            }else{
                //新增
                cartInfo.setSkuNum(number);
                int update = cartInfoMapper.updateById(cartInfo);
                if(update < 0){
                    throw new RuntimeException("购物车修改失败");
                }
            }
        }
    }


    /**
     * @param username
     * @return java.util.List<com.atguigu.gmall.model.cart.CartInfo>
     * @Description 查询购物车
     * @Date 16:31 2022/11/8
     * @Param [username]
     */
    @Override
    public List<CartInfo> getCartInfo() {
        return cartInfoMapper.selectList(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, CartThreadLocalUtil.get()));
    }


    /**
     * @param id
     * @return void
     * @Description 删除购物车
     * @Date 16:40 2022/11/8
     * @Param [id]
     */
    @Override
    public void removeCart(Long id) {

        //条件删除
        int delete = cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>().eq(BaseEntity::getId,id).eq(CartInfo::getUserId,CartThreadLocalUtil.get()));
        if (delete < 0){
            throw new RuntimeException("删除失败");
        }
    }


    /**
     * @param skuId
     * @param number
     * @param username
     * @return void
     * @Description 修改购物车中商品的数量
     * @Date 18:03 2022/11/8
     * @Param [skuId, number, username]
     */
    @Override
    public void updateCartNum(Long id, Integer number) {
        if(id == null ){
            throw new RuntimeException("更改数量失败");
        }
        //如果用户输入的number<= 0 直接给购物车商品给我删了
        if(number <= 0){ //删除
            int delete = cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>()
                    .eq(BaseEntity::getId,id)
                    .eq(CartInfo::getUserId,CartThreadLocalUtil.get()));

            if(delete < 0){
                throw new RuntimeException("删除失败");
            }
        }else{
            //更新
            int updateCartNum = cartInfoMapper.updateCartNum(id, number, CartThreadLocalUtil.get());
            if(updateCartNum < 0){
                throw new RuntimeException("修改失败");
            }
        };
    }


    /**
     * @param id
     * @param number
     * @param username
     * @return void
     * @Description 选中状态切换
     * @Date 18:40 2022/11/8
     * @Param [id, number, username]
     */
    @Override
    //选中状态    cartId status username
    public void check(Long cartId, Short status) {
        int i = 0;
        if(null == cartId){
            //cartId为空 则全部选中状态
            i = cartInfoMapper.updateCheckAll(status, CartThreadLocalUtil.get());
            if(i <= 0){
                throw new RuntimeException("选中状态全部失败");
            }
        }else{
            //cartId不为空 则单个选中状态
            i = cartInfoMapper.updateCheck(cartId,status, CartThreadLocalUtil.get());
            if(i <= 0){
                throw new RuntimeException("选中状态单个失败");
            }
        }

    }


    /**
     * @param cartInfoList
     * @return void
     * @Description 合并用户在未登录的情况下加入的购物车的数据
     * @Date 23:31 2022/11/8
     * @Param [cartInfoList]
     */
    @Override
    public void mergeCart(List<CartInfo> cartInfoList) {
        cartInfoList.stream().forEach(cartInfo ->{
            addCart(cartInfo.getSkuId(),cartInfo.getSkuNum());
        });
    }


    /**
     * @return java.util.List<com.atguigu.gmall.model.cart.CartInfo>
     * @Description 查询用户本次购买的购物车数据
     * @Date 16:51 2022/11/9
     * @Param []
     */
    @Override
    public Map<String, Object> getOrderConfirmCart() {
        //查询数据
        List<CartInfo> cartInfoList = cartInfoMapper.selectList(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, CartThreadLocalUtil.get())
                .eq(CartInfo::getIsChecked, CartConst.CHECKED));

        //计算价格 数量
        AtomicInteger totalNumber = new AtomicInteger(0);
        AtomicDouble totalPrice = new AtomicDouble(0.0);

        List<CartInfo> cartInfoListNew = cartInfoList.stream().map(cartInfo -> {

            //循环获取每个商品的数量
            Integer skuNum = cartInfo.getSkuNum();
            totalNumber.getAndAdd(skuNum);
            //循环获取每个商品的价格(实时价格)
            BigDecimal skuInfoPrice = productFeignClient.getSkuInfoPrice(cartInfo.getSkuId());
            totalPrice.getAndAdd(skuInfoPrice.doubleValue()*skuNum);
            cartInfo.setSkuPrice(skuInfoPrice);
            return cartInfo;
        }).collect(Collectors.toList());

        //返回结果初始化
        Map<String,Object> map = new ConcurrentHashMap<>();
        map.put("cartInfoListNew",cartInfoListNew);
        map.put("totalPrice",totalPrice);
        map.put("totalNumber",totalNumber);
        return map;
    }


    /**
     * @return void
     * @Description 清空购物车
     * @Date 23:36 2022/11/9
     * @Param []
     */
    @Override
    public void deleteCart() {
        int delete = cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, CartThreadLocalUtil.get())
                .eq(CartInfo::getIsChecked, CartConst.CHECKED));
        if(delete < 0){
            throw new GmallException("清空购物车数据失败");
        }
    }
}
