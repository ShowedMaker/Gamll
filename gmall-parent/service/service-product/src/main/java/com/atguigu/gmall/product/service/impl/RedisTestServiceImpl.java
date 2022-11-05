package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.RedisTestService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisTestServiceImpl
 * @Description
 * @Author yzchao
 * @Date 2022/10/29 16:09
 * @Version V1.0
 */
@Service
public class RedisTestServiceImpl implements RedisTestService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissionClient;

    @Override
    public  void setRedis() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        //使用redis的setNX尝试加锁 只有加锁成功的线程才能操作java这个key             先加锁 后取值 保证自己再操作的时候没有其他人操作
        //大家用的锁是同一把锁
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("lock", uuid,5, TimeUnit.SECONDS);
        if (ifAbsent) {
            try{
                //加锁成功
            //从redis中获取键为java的值
            Integer java = (Integer) redisTemplate.opsForValue().get("java");
            //如果不为空 进行加一
            if (java != null) {
                java++;
                //再设置到redis中
                redisTemplate.opsForValue().set("java",java);
            }
            }catch (Exception e) {
                e.printStackTrace();
            }finally {

//                String lock = (String) redisTemplate.opsForValue().get("lock");
//                //判断锁是否是自己的 只有自己才能释放自己
//                if(lock.equals(uuid)) {
//                    //释放锁
//                    redisTemplate.delete("lock");
//                }
                //使用lua脚本是查询判断删除 一起  避免时间差带来的错误 保证安全 实现原子性
            DefaultRedisScript script = new DefaultRedisScript();
            script.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
            //设置返回类型
            script.setResultType(Long.class);
            //执行释放锁
            redisTemplate.execute(script, Arrays.asList("lock"),uuid);
            }
        }else{
            //加锁失败
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            setRedis();;
        }




    }

    @Override
    public void setRedisByRedisson() {
        //尝试加锁
        RLock lock = redissionClient.getLock("lock");
        try {
            //加锁成功 操作++1
            if (lock.tryLock(10,10,TimeUnit.SECONDS)) {
                //加锁成功
                try {
                //从redis中获取键为java的值
                Integer java = (Integer) redisTemplate.opsForValue().get("java");
                //如果不为空 进行加一
                if (java != null) {
                    java++;
                    //再设置到redis中
                    redisTemplate.opsForValue().set("java", java);
                }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("加锁陈工，但是执行过程中出现异常");
                }finally {
                    //释放锁
                    lock.unlock();
                }
            }
        }catch (Exception e) {
            //加锁失败
            System.out.println("加锁失败");
        }










    }

}
