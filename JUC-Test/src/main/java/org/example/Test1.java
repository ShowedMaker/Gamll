package org.example;

import javax.xml.crypto.Data;
import java.util.PrimitiveIterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName Test1
 * @Description
 * @Author yzchao
 * @Date 2023/4/18 18:15
 * @Version V1.0
 */
public class Test1 {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public void add(){
        System.out.println(Thread.currentThread().getName() + ", data = " + atomicInteger.incrementAndGet());
    }



    public static void main(String[] args) {

        // 创建线程共享对象
        Test1 test1 = new Test1();

        // 创建第一个线程执行累加操作
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                test1.add();
            }
        },"thread-01").start();


        // 创建第二个线程执行累加操作
        new Thread(() ->{
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                test1.add();
            }
        },"thread-02").start();

    }
}
