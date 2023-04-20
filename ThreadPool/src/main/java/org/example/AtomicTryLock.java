package org.example;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * @ClassName AtomicTryLock
 * @Description
 * @Author yzchao
 * @Date 2023/3/1 20:42
 * @Version V1.0
 */
public class AtomicTryLock {
    /**
     * @Description 利用 cas 手写 锁
     * @Date 20:42 2023/3/1
     * @Param
     * @return
     */

    private AtomicLong atomicLong = new AtomicLong(0);
    private Thread lockCurrentThread;
    /**
     * 1 表示锁已经被获取 0 表示锁没有获取 利用 cas 将 0 改为 1 成功则表示获取锁
     * @return
     */
    public boolean lock(){
        return atomicLong.compareAndSet(0, 1); }
    public boolean unlock(){
        if(lockCurrentThread!=Thread.currentThread()){
            return false;
        }
        return atomicLong.compareAndSet(1, 0); }




    public static void main(String[] args) {

        AtomicTryLock atomicTryLock = new AtomicTryLock();

        IntStream.range(1, 10).forEach((i) -> new Thread(() -> {
            try {
                boolean result = atomicTryLock.lock();
                if (result) {
                    atomicTryLock.lockCurrentThread=Thread.currentThread();
                    System.out.println(Thread.currentThread().getName() + ",获取锁成功~"); }
                else {
                    System.out.println(Thread.currentThread().getName() + ",获取锁失败~"); }
            } catch (Exception e) {

            } finally {
//                if(atomicTryLock!=null){
//                    atomicTryLock.unlock(); }
            }

        }).start()); }
}

