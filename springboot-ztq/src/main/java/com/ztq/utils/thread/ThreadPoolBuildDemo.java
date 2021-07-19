package com.ztq.utils.thread;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.elasticsearch.tasks.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

import static org.apache.lucene.search.TimeLimitingCollector.TimerThread.THREAD_NAME;

/**
 * 线程池创建工具，利用guava
 * 参照： https://blog.csdn.net/livovil/article/details/104353594
 *
 * @author zhengtianqi
 */
public class ThreadPoolBuildDemo {
    private final static Logger logger = LoggerFactory.getLogger(ThreadPoolBuildDemo.class);

    /**
     * 创建和初始化线程池
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param workQueue       任务队列容量（阻塞队列）
     * @param threadName      线程的名称
     * @param keepAliveTime   线程空闲时间
     * @return
     */
    private static ThreadPoolExecutor doThreadPoolExecutor(int corePoolSize, int maximumPoolSize, int workQueue, String threadName, long keepAliveTime) {
        //线程名
        final String threadNameStr = threadName + "-%d";
        //**ThreadFactoryBuilder**：线程工厂类就是将一个线程的执行单元包装成为一个线程对象，比如线程的名称,线程的优先级,线程是否是守护线程等线程；guava为了我们方便的创建出一个ThreadFactory对象,我们可以使用ThreadFactoryBuilder对象自行创建一个线程.
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(threadNameStr).build();
        //线程池
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(workQueue), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    private static final int QUEUE_SIZE = 1000;
    private static volatile ThreadPoolExecutor executor;
    private static volatile ListeningExecutorService listeningExecutor;

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    /**
     * 当前可用CPU数
     * 如何合理地估算线程池大小？http://ifeve.com/how-to-calculate-threadpool-size/
     * ListeningExecutorService** :由于普通的线程池，返回的Future，功能比较单一；
     * Guava 定义了 ListenableFuture接口并继承了JDK concurrent包下的Future 接口
     * ListenableFuture 允许你注册回调方法(callbacks)，在运算（多线程执行）完成的时候进行调用。
     */
    private void initTaskThreadPool() {
        executor = doThreadPoolExecutor(PROCESSORS, PROCESSORS * 4, QUEUE_SIZE, THREAD_NAME, 400L);
        listeningExecutor = MoreExecutors.listeningDecorator(executor);
    }

    /**
     * 初始化 开启线程@PostConstruct
     */
    private void init() {
        //执行抓取任务线程池
        initTaskThreadPool();
        logger.info("初始化通用线程池成功...");
    }


}
