package org.example;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName Demo15CanSeeTest
 * @Description
 * @Author yzchao
 * @Date 2023/4/18 18:53
 * @Version V1.0
 */
public class Demo15CanSeeTest {
    private int data = 100;

    public static void main(String[] args) {

        Demo15CanSeeTest demo = new Demo15CanSeeTest();

        new Thread(()->{

            while (demo.getData() == 100) {

            }

            System.out.println("AAA 线程发现 data 新值：" + demo.getData());

        }, "AAA").start();

        new Thread(()->{

            try {
                TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {}

            demo.setData(200);

            System.out.println("BBB 线程修改 data，新值是：" + demo.getData());

        }, "BBB").start();

    }



    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

}