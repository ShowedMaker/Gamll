package org.example;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName Demo17AtomicTest
 * @Description
 * @Author yzchao
 * @Date 2023/4/18 19:12
 * @Version V1.0
 */
public class Demo17AtomicTest {

    private volatile AtomicInteger  data = new AtomicInteger();

    public void add() {
        System.out.println(Thread.currentThread().getName() + " data = " + data.incrementAndGet());
    }

    public static void main(String[] args) {

        Demo17AtomicTest demo = new Demo17AtomicTest();

        new Thread(()->{

            for (int i = 0; i < 100; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);} catch (InterruptedException e) {}
                demo.add();
            }

        }, "AAA").start();

        new Thread(()->{

            for (int i = 0; i < 100; i++) {
                try {TimeUnit.MILLISECONDS.sleep(10);} catch (InterruptedException e) {}
                demo.add();
            }

        }, "BBB").start();

    }

}
