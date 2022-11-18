package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.cart.feign.CartFeignClient;
import com.atguigu.gmall.common.exception.GmallException;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.util.OrderThreadLocalUtil;
import com.atguigu.gmall.product.feign.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName OrderInfoServiceImpl
 * @Description
 * @Author yzchao
 * @Date 2022/11/9 19:08
 * @Version V1.0
 */
@Service
public class  OrderInfoServiceImpl implements OrderInfoService {

    @Autowired
    private CartFeignClient cartFeignClient;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private ProductFeignClient productFeignClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * @Description 取消订单 消息触发没有http请求 人触发是有的
     * 1.主动  2.超时   超时消息触发是不会有令牌的 人是会有的  超时触发ThreadLocal是没有用户名的 人是有的
     * 不用传状态
     * @Date 16:34 2022/11/13
     * @Param [orderId]
     * @return void
     */
    @Override
    public void cancelOrder(Long orderId) {
        //参数校验
        if (orderId == null) {
            return;
        }

        //用redis自增 进行重复取消的记录
        Long increment =  //锁的粒度是订单号  只要不是同一笔订单 都不会卡住
                redisTemplate.opsForValue().increment("User_CancelOrder_Count:" + orderId, 1);

        if(increment > 1){
            throw new GmallException("取消订单失败，请勿重复取消");
        }

        try {
        //走到这 肯定是第一次 设置10秒钟过期时间
        redisTemplate.expire("User_CancelOrder_Count:" + orderId,10, TimeUnit.SECONDS);

        //判断是主动还是超时取消的订单 根据ThreadLocal中是否有用户名判断 是主动还是超时 主动有username 超时没有
        //获取用户名
        String username = OrderThreadLocalUtil.get();
        //默认true是超时取消  false为主动取消
        Boolean flag = true;
        //声明条件查询 查询未支付的订单 订单号等于参数指定的订单号
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        //比如 update orderInfo set status = "取消订单" where id = 1  做同样的事情多次  最终多次的效果和一次的效果一样 只需一次
        // 这里的幂等性用消息的前置状态判断
        wrapper.eq(OrderInfo::getId, orderId).eq(OrderInfo::getOrderStatus, OrderStatus.UNPAID.getComment());
        if (!StringUtils.isEmpty(username)) {
            //主动取消 --订单号和用户名
            flag = false;
            wrapper.eq(OrderInfo::getUserId, username);
        }
            //查询订单
            OrderInfo orderInfo = orderInfoMapper.selectOne(wrapper);
            if (orderId == null || orderInfo.getId() == null) {
                //未支付的订单不存在 不讲道理 直接返回
                return;
            }

            //判断订单的前置状态 只取消未支付的订单  (幂等性)
            if (flag) {
                orderInfo.setOrderStatus(OrderStatus.TIME_OUT.getComment());
                orderInfo.setProcessStatus(ProcessStatus.TIME_OUT.getComment());
            } else {
                orderInfo.setOrderStatus(OrderStatus.CANCEL.getComment());
                orderInfo.setProcessStatus(OrderStatus.CANCEL.getComment());
            }

            //数据库修改为主动取消 超时取消
            int update = orderInfoMapper.updateById(orderInfo);
            if (update < 0) {
                throw new GmallException("取消订单失败，请重拾");
            }
            //回滚库存 --TODO--
            rollBackStock(orderId);
        }catch (Exception e){
            e.printStackTrace();
            throw new GmallException("主动抛异常 取消订单失败，要让事务回滚");
        }finally {
            //释放key 无论取消订单成功或失败  都删除 否则不能取消这笔订单
            redisTemplate.delete("User_CancelOrder_Count:" + orderId);
        }
    }

    
    /**
     * @Description 新增订单
     * @Date 19:09 2022/11/9
     * @Param [orderInfo]
     * @return void
     */
    @Override
    public void addOrder(OrderInfo orderInfo) {
        //参数校验 工作中都得校验
        if(orderInfo == null){
            throw new GmallException("订单不能为空");
        }
        //用redis自增 进行重复提交的记录
        Long increment =
                redisTemplate.opsForValue().increment("User_AddOrder_Count:" + OrderThreadLocalUtil.get(), 1);

        if(increment > 1){
            throw new GmallException("新增订单失败，请勿重复下单");
        }

        try {
            //走到这 肯定是第一次 设置10秒钟过期时间
            redisTemplate.expire("User_AddOrder_Count:" + OrderThreadLocalUtil.get(),10, TimeUnit.SECONDS);

            //远程调用购物车微服务,获取本次购买的购物车数据和总金额
            Map<String, Object> cartFeignResultMap = cartFeignClient.getOrderAddInfo();
            if(cartFeignResultMap == null || cartFeignResultMap.isEmpty()){
                throw new GmallException("购物车 未作任何勾选");
            }

            //补全订单对象缺少的内容
            orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());//新建订单 设置订单未支付状态
            orderInfo.setUserId(OrderThreadLocalUtil.get());//设置 用户名
            orderInfo.setCreateTime(new Date());
            orderInfo.setExpireTime(new Date(System.currentTimeMillis() + 1800000));//设置订单过期时间
            orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());

            //设置刚从购物车查到的价格
            orderInfo.setTotalAmount(new BigDecimal(cartFeignResultMap.get("totalPrice").toString()));
            //将订单保存到数据库中去
            int insert = orderInfoMapper.insert(orderInfo);
            if(insert <= 0){
                throw new GmallException("新增订单失败");
            }
            //获取订单id
            Long orderId = orderInfo.getId();
            //根据购物车数据包装订单详情
            List orderDetailInfoList = (List) cartFeignResultMap.get("cartInfoListNew");

            //清空购物车
//            cartFeignClient.deleteCart();
            //扣除库存
            //新增订单详情数据同时统计需要扣减库存的商品id和商品的扣减数量
            Map<String, String> decountMap = addOrderDetail(orderId, orderDetailInfoList);
            productFeignClient.decountStock(decountMap);

            //对这笔订单进行30分钟倒计时
            rabbitTemplate.convertAndSend("order_normal_exchange","order.normal",orderId + "",(message -> {
               //获取消息的属性
                MessageProperties messageProperties = message.getMessageProperties();
                //设置消息的过期时间
                messageProperties.setExpiration("20000");
                //返回
                return message;
            }));

            System.out.println("发送订单延迟消息的时间戳为：" + System.currentTimeMillis());

        } catch (GmallException e) {
            e.printStackTrace();
            throw new GmallException("新增订单过程失败:" + e.getMessage());
        } finally {
            //释放key 无论下单成功或失败  都删除 否则不能下新订单
            redisTemplate.delete("User_AddOrder_Count:" + OrderThreadLocalUtil.get());
        }
    }


    //回滚库存
    private void rollBackStock(Long orderId) {

        Map<String,String> map = new ConcurrentHashMap();

        //查询订单详情列表信息
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getOrderId, orderId));
        //统计商品ID和数量
        orderDetailList.stream().forEach(orderDetail -> {
            //获取商品ID
            Long skuId = orderDetail.getSkuId();
            //获取商品数量
            Integer skuNum = orderDetail.getSkuNum();
            //保存商品ID和需要回滚的库存数量
            map.put(skuId + "",skuNum+"");
        });

        //远程调用product微服务，扣除库存
        productFeignClient.rollBackStock(map);
    }


    /**
     * @Description 新曾订单的时候新增订单详情
     * @Date 16:36 2022/11/13
     * @Param [orderId, orderDetailInfoList]
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    private Map<String, String> addOrderDetail(Long orderId, List orderDetailInfoList) {
        //记录扣减的商品数据
        Map<String,String> decountMap = new ConcurrentHashMap<>();
        //开始遍历
        orderDetailInfoList.stream().forEach(object->{
            String s = JSONObject.toJSONString(object);
            CartInfo cartInfo = JSONObject.parseObject(s, CartInfo.class);
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setOrderPrice(cartInfo.getSkuPrice());
            orderDetail.setImgUrl("http://192.168.200.128:8080/group1/M00/00/01/wKjIgF5viHWEMAqfAAAAACY3A9Q189.png");
            int insert = orderDetailMapper.insert(orderDetail);
            if(insert <= 0){
                throw new GmallException("新增订单详情失败");
            }
            //记录每一个skun商品的数量 key为cartInfo.getSkuId() value 为每个商品的数量
            decountMap.put(cartInfo.getSkuId() + "",cartInfo.getSkuNum()+"");
        });
        return decountMap;
    }
}
