package com.ztq.task;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

/**
 * @author zhengtianqi
 */
public class PrintLogTask implements Callable<Integer> {

    private final static Logger logger = LoggerFactory.getLogger(PrintLogTask.class);

    private final static int MAX = 10000;
    public String name;

    public PrintLogTask(String name) {
        this.name = name;
    }

    @Override
    public Integer call() throws Exception {
        int i = 0;
        while (i < MAX) {
            logger.info("info log print success");
            logger.debug("debug log print success");
            logger.warn("warning log print success");
            logger.error("error log print success");
            // 很低的日志级别，一般不会使用
            logger.trace("trace log print success");
            i++;
        }
        return i;
    }
}
