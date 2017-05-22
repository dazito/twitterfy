package com.dazito.twitterfy.actor;

import akka.actor.UntypedActor;
import com.dazito.twitterfy.Publisher;
import com.dazito.twitterfy.aws.dynamodb.DynamoDbClient;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.model.TweetModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daz on 22/05/2017.
 */
public class DynamoDbActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbActor.class);

    private boolean isActive;
    private Publisher dynamoDbPublisher;

    @Override
    public void preStart() throws Exception {
        super.preStart();

        isActive = TwitterfyConfiguration.getConfiguration().isAwsSnsActive();
        dynamoDbPublisher = new DynamoDbClient();
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof TweetModel) {
            final TweetModel tweetModel = (TweetModel) message;

            dynamoDbPublisher.publish(tweetModel);
        }
        else {
            unhandled(message);
        }
    }
}
