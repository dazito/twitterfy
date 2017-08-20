package com.dazito.twitterfy.actor;

import akka.actor.UntypedActor;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.db.DbClient;
import com.dazito.twitterfy.model.TweetModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daz on 16/04/2017.
 */
public class DatabaseActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseActor.class);

    private boolean isActive;
    private DbClient dbClient;

    @Override
    public void preStart() throws Exception {
        super.preStart();

        isActive = TwitterfyConfiguration.getConfiguration().isDbActive();
        if(isActive) {
            dbClient = new DbClient();
        }

    }

    @Override
    public void postStop() throws Exception {
        // Avoid connection leaks
        if(isActive) {
            dbClient.close();
            dbClient = null;
        }

        super.postStop();
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof TweetModel) {
            TweetModel tweetModel = (TweetModel) message;

            handleTweetModelEvent(tweetModel);
        }
    }

    private void handleTweetModelEvent(TweetModel tweetModel) {
        if(isActive) {
            // Persist
            dbClient.insertTweet(tweetModel.getTweet(), tweetModel.getScreenName(), tweetModel.getTimestamp());
        }
    }
}
