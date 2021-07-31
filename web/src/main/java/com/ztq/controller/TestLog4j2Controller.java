package com.ztq.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ztq.task.PrintLogTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;


@RestController
@RequestMapping(value = "/api/v1")
public class TestLog4j2Controller {
    private final Logger logger = LoggerFactory.getLogger(TestLog4j2Controller.class);

    @GetMapping(value = "/printLog")
    public void printLog() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("printLog" + "-%d").build();
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(8, 16, 1000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1000), threadFactory, new ThreadPoolExecutor.AbortPolicy());

        Future<Integer> future1 = threadPool.submit(new PrintLogTask("1"));
        Future<Integer> future2 = threadPool.submit(new PrintLogTask("2"));
        Future<Integer> future3 = threadPool.submit(new PrintLogTask("3"));

        System.out.println("测试future 1");
        try {
            System.out.println("执行1：" + future1.get() + ", " + future2.get() + ", " + future3.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (future1.isDone() || future2.isDone() || future3.isDone()) {
            try {
                System.out.println("测试future 2");
                System.out.println("执行2：" + future1.get() + ", " + future2.get() + ", " + future3.get());
                threadPool.shutdown();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }


    }
}
