package com.dazito.twitterfy.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.db.DbClient;
import com.dazito.twitterfy.model.TweetModel;
import org.jooq.exception.DetachedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by daz on 02/04/2017.
 */
public class TweetActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TweetActor.class);

    private DbClient dbClient;
    private Set<String> filterKeywords;

    public static Props props() {
        return Props.create(TweetActor::new);
    }

    @Override
    public void preStart() throws Exception {
        dbClient = new DbClient();
        filterKeywords = TwitterfyConfiguration.getConfiguration().getFilterKeywords();
        LOGGER.info("*** Tweet Actor Created  ***");
        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        // Avoid connection leaks
        dbClient.close();
        LOGGER.info("--- Tweet Actor Removed ---");
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof TweetModel) {
            final TweetModel tweetModel = (TweetModel) message;

            if(filterTweet(tweetModel.getTweet())) {
                persistTweet(tweetModel);
            }
        }
        else {
            unhandled(message);
        }
    }

    private void persistTweet(TweetModel tweetModel) {
        try {
            dbClient.insertTweet(tweetModel.getTweet(), tweetModel.getScreenName(), tweetModel.getTimestamp());
        }
        catch (DetachedException ex) {
            LOGGER.error("Could not perform INSERT operation - reason: {}", ex.getMessage());
        }
    }

    private boolean filterTweet(String tweet) {
        final String[] splittedTweet = tweet.trim().toLowerCase().split(" +");
        final Set<String> tweetSet = new HashSet<>(Arrays.asList(splittedTweet));

        return tweetSet.stream().anyMatch(this::streamFilter);
    }

    private boolean streamFilter(String str) {
        return filterKeywords.contains(str.toLowerCase());
    }
}
