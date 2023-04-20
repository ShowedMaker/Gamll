package org.example;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @Description 线程池核心点：复用机制 ------ 1. 提前创建好固定的线程一直在运行状态----死循环实现
 *                                        2. 提交的线程任务缓存到一个并发队列集合中，交给我们正在运行的线程执行
 *                                         3. 正在运行的线程就从队列中获取该任务执行
 * @Date 15:14 2023/2/27
 * @Param
 * @return
 */
@SuppressWarnings("all")
public class MyExecutors {

    private List<MyExecutors> workThreads;


    private BlockingDeque<Runnable> runnableDeque;

    Boolean isRun = true;

    public MyExecutors(int maxThreadCount,int dequeSize){
        //1.限制队列容量缓存
        runnableDeque = new LinkedBlockingDeque<Runnable>(dequeSize);
    //2. 提前创建好固定的线程一直在运行状态----死循环实现
    workThreads = new ArrayList<MyExecutors>(maxThreadCount);
        for (int i = 0; i < maxThreadCount; i++) {
            new WorkThreads().start();
        }


    }


    public Boolean exectue(Runnable command){
        return runnableDeque.offer(command);
    }



    class WorkThreads extends Thread{


        @Override
        public void run() {
           while(isRun || runnableDeque.size() > 0){
               Runnable poll = runnableDeque.poll();
               if (poll != null ){
                   poll.run();
               }
           }
        }
    }


    public static void main(String[] args) {
        MyExecutors myExecutors = new MyExecutors(3, 20);
        for (int i = 0; i < 10; i++) {
            myExecutors.exectue(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "," + Thread.currentThread().getState());
                }
            });
        }
        myExecutors.isRun = false;
    }

}