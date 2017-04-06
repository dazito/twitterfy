package com.dazito.twitterfy.actor;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.dazito.twitterfy.actor.message.BatchNotifyMessage;
import com.dazito.twitterfy.db.DbClient;
import com.dazito.twitterfy.model.TweetModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by daz on 05/04/2017.
 */
public class SchedulerActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerActor.class);

    private DbClient dbClient;

    public static Props props() {
        return Props.create(SchedulerActor.class);
    }

    private Cancellable tick = getContext()
            .system()
            .scheduler()
            .schedule(
                    Duration.create(5, TimeUnit.SECONDS),
                    Duration.create(5, TimeUnit.HOURS),
                    getSelf(),
                    new BatchNotifyMessage(),
                    getContext().dispatcher(),
                    ActorRef.noSender()
            );

    @Override
    public void preStart() throws Exception {
        super.preStart();
        dbClient = new DbClient();
    }

    @Override
    public void postStop() throws Exception {
        tick.cancel();
        dbClient.close();
        super.postStop();
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof BatchNotifyMessage) {
            LOGGER.info("Processing batch");

            List<TweetModel> tweetModelList = dbClient.getNotProcessedTweets();

            tweetModelList
                    .stream()
                    .forEach(tweetModel -> LOGGER.info("Scheduler: Tweet: {} | Timestamp: {}", tweetModel.getTweet(), tweetModel.getScreenName()));
        }
    }
}
