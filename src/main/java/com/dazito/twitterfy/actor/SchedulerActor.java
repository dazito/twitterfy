package com.dazito.twitterfy.actor;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.dazito.twitterfy.actor.message.BatchNotifyMessage;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.db.DbClient;
import com.dazito.twitterfy.mail.EmailClient;
import com.dazito.twitterfy.model.TweetModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by daz on 05/04/2017.
 */
public class SchedulerActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerActor.class);
    public static final String OTHERS = "Others";

    private DbClient dbClient;
    private Cancellable tick;
    private ActorRef emailDispatcherActor;

    public static Props props() {
        return Props.create(SchedulerActor.class);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        dbClient = new DbClient();
        boolean isActive = TwitterfyConfiguration.getConfiguration().isSchedulerActivated();
        emailDispatcherActor = ActorSystemContainer.getInstance().getActorSystem().actorOf(EmailDispatcherActor.props());

        if(isActive) {
            int frequency = TwitterfyConfiguration.getConfiguration().getSchedulerFrequency();
            String frequencyUnit = TwitterfyConfiguration.getConfiguration().getSchedulerFrequencyUnit();
            tick = getContext()
                    .system()
                    .scheduler()
                    .schedule(
                            Duration.create(5, TimeUnit.SECONDS),
                            Duration.create(frequency, TimeUnit.valueOf(frequencyUnit)),
                            getSelf(),
                            new BatchNotifyMessage(),
                            getContext().dispatcher(),
                            ActorRef.noSender()
                    );
        }
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
            LOGGER.info("* * * Processing batch * * *");

            List<TweetModel> tweetModelList = dbClient.getNotProcessedTweets();

            if(tweetModelList.isEmpty()) {
                LOGGER.info("empty list to process - skipping");
                return;
            }

            Map<String, List<TweetModel>> groupedByKeywordTweets = tweetModelList
                    .stream()
                    .collect(Collectors.groupingBy(this::filter));

            EmailClient emailClient = new EmailClient();
            final String emailBody = emailClient.buildEmailBody(groupedByKeywordTweets);
            emailClient.sendEmail(emailBody);

            dbClient.setTweetsAsProcessed();

            LOGGER.info("* * * * List processed * * *");
        }
        else {
            unhandled(message);
        }
    }

    private String filter(TweetModel tweetModel) {
        final String[] splitTweet = tweetModel.getTweet().trim().toLowerCase().split(" +");
        final Set<String> tweetSet = new HashSet<>(Arrays.asList(splitTweet));
        Set<String> filterKeywords = TwitterfyConfiguration.getConfiguration().getFilterKeywords();

        for(String tweet : tweetSet) {
            if(filterKeywords.contains(tweet.toLowerCase())) {
                return tweet;
            }
        }

        return OTHERS;
    }
}
