package com.dazito.twitterfy.actor;

import akka.actor.UntypedActor;
import com.dazito.twitterfy.Publisher;
import com.dazito.twitterfy.aws.sns.SnsPublisher;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.model.TweetModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daz on 16/04/2017.
 */
public class AwsSnsActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsSnsActor.class);

    private boolean isActive;
    private Publisher snsPublisher;

    @Override
    public void preStart() throws Exception {
        super.preStart();

        isActive = TwitterfyConfiguration.getConfiguration().isAwsSnsActive();
        snsPublisher = new SnsPublisher();
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof TweetModel) {
            TweetModel tweetModel = (TweetModel) message;
            handleTweetModelEvent(tweetModel);
        }
        else {
            unhandled(message);
        }
    }

    private void handleTweetModelEvent(TweetModel tweetModel) {
        if(isActive) {
            snsPublisher.publish(tweetModel);
        }
    }
}
