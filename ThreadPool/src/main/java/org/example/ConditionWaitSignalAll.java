package org.example;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName DemoClass
 * @Description 多个线程，一个线程对当前数值加1，另一个线程对当前数值减1,要求用线程间通信
 * @Author yzchao
 * @Date 2023/3/2 20:47
 * @Version V1.0
 */
public class ConditionWaitSignalAll {
    //加减对象
    private int number = 0;

    //声明锁
    private Lock lock = new ReentrantLock();

    //声明钥匙
    private Condition condition = lock.newCondition();

    /**
     * 加1
     */
    public void increment() {
        try {
            lock.lock();
            while (number != 0){
                condition.await();
            }
            number++;
            System.out.println("--------" + Thread.currentThread().getName() + "加一成功----------,值为:" + number);
            condition.signalAll();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    /**
     * 减一
     */
    public void decrement(){
        try {
            lock.lock();
            while (number == 0){
                condition.await();
            }
            number--;
            System.out.println("--------" + Thread.currentThread().getName() + "减一成功----------,值为:" + number);
            condition.signalAll();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ConditionWaitSignalAll demoClass = new ConditionWaitSignalAll();

        for (;;) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    demoClass.increment();
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    demoClass.decrement();
                }
            }).start();

        }
    }

}
