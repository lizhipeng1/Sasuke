package com.redis;

import com.redis.listener.ExpireKeyRun;
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

    private ExecutorService exec = Executors.newFixedThreadPool(10);
    private ArrayList<Runnable> runnables = new ArrayList<Runnable>();

    public void execute(Runnable r) {
        exec.execute(r);
        runnables.add(r);
        log.info("当前的线程池中任务数量："+runnables.size());
    }

    public void shutdown() {
        log.info("执行 线程池关闭程序,当前的线程池中任务数量："+runnables.size());
        for (Runnable runnable : runnables) {
            if (runnable instanceof ExpireKeyRun) {
                ExpireKeyRun expireKeyListener = (ExpireKeyRun)runnable;
                expireKeyListener.unsubscribe();
            }
        }
        exec.shutdown();
    }

    public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        exec.awaitTermination(timeout, unit);
    }
}
