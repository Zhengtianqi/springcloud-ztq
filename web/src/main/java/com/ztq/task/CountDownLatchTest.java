package com.ztq.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author zhengtianqi
 */
public class CountDownLatchTest {
    public static void main(String[] args) throws Exception {

        /*创建CountDownLatch实例,计数器的值初始化为3*/
        final CountDownLatch downLatch = new CountDownLatch(7);

        /*创建三个线程,每个线程等待1s,表示执行比较耗时的任务*/
        // 创建线程工厂实例
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("pool-%d").build();
        // 创建线程池，核心线程数、最大线程数、空闲保持时间、队列长度、拒绝策略可自行定义
        ExecutorService pool = new ThreadPoolExecutor(2, 20, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        pool.execute(new MyTask("选手1到达终点", downLatch));
        pool.execute(new MyTask("选手2到达终点", downLatch));
        pool.execute(new MyTask("选手3到达终点", downLatch));
        pool.execute(new MyTask("选手4到达终点", downLatch));
        pool.execute(new MyTask("选手5到达终点", downLatch));
        pool.execute(new MyTask("选手6到达终点", downLatch));
        pool.execute(new MyTask("选手7到达终点", downLatch));

        /*主线程调用await()方法,等到其他三个线程执行完后才继续执行*/
        downLatch.await();
        System.out.println("赛跑结束");
        pool.shutdown();

    }


    static class MyTask extends Thread {
        private String name;
        private CountDownLatch downLatch;

        public MyTask(String name, CountDownLatch downLatch) {
            this.name = name;
            this.downLatch = downLatch;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();

            }

            System.out.println(name);

            /*任务完成后调用CountDownLatch的countDown()方法*/
            downLatch.countDown();
        }
    }
}
