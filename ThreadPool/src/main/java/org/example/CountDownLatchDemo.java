package org.example;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName CountDownLatchDemo
 * @Description 6个同学陆续离开教室后值班同学才可以关门    --减少计数CountDownLatch
 * @Author yzchao
 * @Date 2023/3/2 21:10
 * @Version V1.0
 */
public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {

        //定义一个数值为6的计数器
        CountDownLatch countDownLatch = new CountDownLatch(6);

        //创建6个同学
        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                try {

                    if (Thread.currentThread().getName().equals("同学---6")) {
//                        Thread.sleep(2000);
                    }
                    System.out.println(Thread.currentThread().getName() + "离开了");
                    //计数器减一,不会阻塞
                    countDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }, "同学---" + (i + 1)).start();
        }
            //主线程await休息
            System.out.println("主线程睡觉");
            countDownLatch.await();

            //全部离开后自动唤醒主线程
            System.out.println("全部离开了,现在的计数器为" + countDownLatch.getCount());

        }
    }




