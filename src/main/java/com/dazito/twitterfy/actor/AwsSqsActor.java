package com.dazito.twitterfy.actor;

import akka.actor.UntypedActor;
import com.dazito.twitterfy.Publisher;
import com.dazito.twitterfy.aws.sqs.SqsPublisher;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.model.TweetModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daz on 16/04/2017.
 */
public class AwsSqsActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsSqsActor.class);

    private boolean isActive;
    private Publisher sqsPublisher;

    @Override
    public void preStart() throws Exception {
        super.preStart();

        isActive = TwitterfyConfiguration.getConfiguration().isAwsSqsActive();
        sqsPublisher = new SqsPublisher();
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof TweetModel) {
            final TweetModel tweetModel = (TweetModel) message;
            handleTweetModelEvent(tweetModel);
        }
        else {
            unhandled(message);
        }
    }

    private void handleTweetModelEvent(TweetModel tweetModel) {
        if(isActive) {
            sqsPublisher.publish(tweetModel);
        }
    }
}
