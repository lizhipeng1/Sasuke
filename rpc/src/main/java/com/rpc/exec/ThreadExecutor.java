package com.rpc.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadExecutor {
    private static final Logger log= LoggerFactory.getLogger(ThreadExecutor.class);

    private ExecutorService exec = Executors.newFixedThreadPool(4);
    private ArrayList<ThreadRunnable> runnables = new ArrayList<ThreadRunnable>();

    public void shutdown() {
        log.info("执行 线程池关闭程序,当前的线程池中任务数量："+runnables.size());
        for (ThreadRunnable runnable : runnables) {
            runnable.shutDown();
        }
        exec.shutdown();
    }

    public void execute(ThreadRunnable r) {
        exec.execute(r);
        runnables.add(r);
        log.info("线程："+runnables.size());
    }

    public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        exec.awaitTermination(timeout, unit);
    }
}
