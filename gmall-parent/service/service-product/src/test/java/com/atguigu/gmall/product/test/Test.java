package com.atguigu.gmall.product.test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName Test
 * @Description
 * @Author yzchao
 * @Date 2022/10/31 23:12
 * @Version V1.0
 */
public class Test {


    public static void main1(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("主线程执行。主线程为：" + Thread.currentThread().getName());
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("子线程执行任务一，子线程为：" + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("任务的执行结果：" + future.get());
        System.out.println("主线程工作结束！");
    }


    public static void main2(String[] args) throws Exception {
        System.out.println("主线程执行, 主线程为:" + Thread.currentThread().getName());
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("子线程执行任务一, 子线程为:" + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //返回
            return 10086;
        });
        System.out.println("任务的执行结果为: " + future.get());
        System.out.println("主线程工作结束!");
    }


    /**
     * thenRun的说明
     *
     * @param args
     */
    public static void main3(String[] args) throws Exception {
        for (int i = 0; i < 100; i++) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                try {
                    System.out.println("子线程执行任务一, 子线程为:" + Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //返回
                return 10086;
            });
            //thenRun: 可能有子线程执行,也可能由调用线程执行
            //thenRunAsync: 绝对由子线程执行
            CompletableFuture<Void> future1 = future.thenRunAsync(() -> {
                try {
                    System.out.println("子线程执行任务二, 子线程为:" + Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            future1.get();
        }
    }


    /**
     * thenApplyAsync:方法的回顾
     *
     * @param args
     * @throws Exception
     */
    public static void main4(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                try {
                    System.out.println("子线程执行任务一, 子线程为:" + Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //返回
                return 10086;
            });
            //thenRun: 可能有子线程执行,也可能由调用线程执行
            //thenRunAsync: 绝对由子线程执行
            CompletableFuture<Void> future1 = future.thenAcceptAsync((a) -> {
                try {
                    System.out.println("接受到的上一步的结果为:" + a);
                    System.out.println("子线程执行任务二, 子线程为:" + Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            System.out.println("任务二的结果为: " + future1.get());
        }
    }


    /**
     * exceptionally: 相当于try catch中的catch,可以影响最终的返回结果
     *
     * @param args
     * @throws Exception
     */
    public static void main5(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                try {
                    System.out.println("子线程执行任务一, 子线程为:" + Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int a = 1 / 0;
                //返回
                return 10086;
            });
            //thenRun: 可能有子线程执行,也可能由调用线程执行
            //thenRunAsync: 绝对由子线程执行
            CompletableFuture<Integer> future1 = future.exceptionally((a) -> {
                System.out.println("任务二执行");
                a.printStackTrace();
                return -1;
            });
            System.out.println("任务二的结果为: " + future1.get());
        }
    }

        /**
         * thenApplyAsync:方法的回顾
         *
         * @param args
         * @throws Exception
         */
        public static void main(String[] args) throws Exception{
            for (int i = 0; i < 10; i++) {
                CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        System.out.println("子线程执行任务一, 子线程为:" + Thread.currentThread().getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //返回
                    return 10086;
                });
                //thenRun: 可能有子线程执行,也可能由调用线程执行
                //thenRunAsync: 绝对由子线程执行
                CompletableFuture<Integer> future1 = future.thenApplyAsync((a) ->{
                    try {
                        System.out.println("接受到的上一步的结果为:" + a);
                        System.out.println("子线程执行任务二, 子线程为:" + Thread.currentThread().getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return a + 1;
                });
                System.out.println("任务二的结果为: " + future1.get());
            }
        }


    /**
     * whenCompleteAsync: 无论有没有异常都会最终执行!try catch finally中的finally,不影响最终的结果
     *
     * @param args
     * @throws Exception
     */
    public static void main6(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                try {
                    System.out.println("子线程执行任务一, 子线程为:" + Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int a = 1 / 0;
                //返回
                return 10086;
            });
            CompletableFuture<Integer> future1 = future.whenCompleteAsync((a, b) -> {
                System.out.println("任务二执行");
                if (b != null) {
                    //有异常
                } else {
                    //没异常
                }
            });
            System.out.println("任务二的结果为: " + future1.get());
        }
    }


    /**
     * handleAsync: 有没有异常都执行,返回结果会影响最终结果
     *
     * @param args
     * @throws Exception
     */
    public static void main7(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                try {
                    System.out.println("子线程执行任务一, 子线程为:" + Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int a = 1 / 0;
                //返回
                return 10086;
            });
            CompletableFuture<Integer> future1 = future.handleAsync((a, b) -> {
                System.out.println("第一参数为：" + a);
                System.out.println("第二参数为：" + b);
                System.out.println("子线程二执行：" + Thread.currentThread().getName());
                if (b != null) {
                    //前面出现了异常
                    return -1;
                }
                return a;
            });
            System.out.println("任务二的结果为: " + future1.get());
            System.out.println("主线程结束执行" + Thread.currentThread().getName());
        }
    }


    public static void main8(String[] args) {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("子线程执行任务一, 子线程为:" + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //返回
            return 10086;
        });

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
                System.out.println("子线程执行任务二, 子线程为:" + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //返回
            return 10086;
        });

        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
                System.out.println("子线程执行任务三, 子线程为:" + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //返回
            return 10086;
        });

        CompletableFuture<Integer> future4 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(4000);
                System.out.println("子线程执行任务四, 子线程为:" + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //返回
            return 10086;
        });
        CompletableFuture.allOf(future1, future2, future3, future4).join();
    }

}




