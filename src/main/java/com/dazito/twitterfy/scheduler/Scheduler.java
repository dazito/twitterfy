package com.dazito.twitterfy.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by daz on 01/04/2017.
 */
public class Scheduler {

    private final ScheduledExecutorService scheduledExecutorService;

    public Scheduler() {
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void setScheduler() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                final DbClient dbClient = new DbClient();
//                dbClient.getNotProcessedTweets();
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.MINUTES);
    }
}
