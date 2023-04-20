package org.example;

import java.util.concurrent.Semaphore;

/**
 * @ClassName SemaphoreDemo
 * @Description 信号灯Semaphore
 * @Author yzchao
 * @Date 2023/3/2 21:28
 * @Version V1.0
 */
public class SemaphoreDemo {

    /**
     * @Description 抢车位, 6部汽车3个停车位
     * @Date 21:28 2023/3/2
     * @Param [args]
     * @return void
     */
    public static void main(String[] args) throws InterruptedException {
        //定义3个停车位
        Semaphore semaphore = new Semaphore(3);

        //模拟6辆汽车停车
        for (int i = 1; i <= 6; i++) {
            Thread.sleep(1000);
            //停车
            new Thread(() ->{
                try {
                    System.out.println(Thread.currentThread().getName() + "找车位ing");
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + "汽车停车成功!");
                    Thread.sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    System.out.println(Thread.currentThread().getName() + "溜了溜了");
                    semaphore.release();
                }
            }, "汽车" + i).start();
        }
    }
}
