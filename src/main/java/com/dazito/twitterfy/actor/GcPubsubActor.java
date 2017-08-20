package com.dazito.twitterfy.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import com.dazito.twitterfy.Publisher;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.gcloud.GoogleCloudPubSubClient;
import com.dazito.twitterfy.model.TweetModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daz on 16/04/2017.
 */
public class GcPubsubActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcPubsubActor.class);

    public static Props props() {
        return Props.create(GcPubsubActor.class);
    }

    private boolean isActive;
    private Publisher pubSubClient;

    @Override
    public void preStart() throws Exception {
        super.preStart();
        isActive = TwitterfyConfiguration.getConfiguration().isGcPubsubActive();

        if(isActive) {
            pubSubClient = GoogleCloudPubSubClient.defaultClient();
        }
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof TweetModel) {
            final TweetModel tweetModel = (TweetModel) message;
            handleTweetModelMessage(tweetModel);
        }
        else {
            unhandled(message);
        }
    }

    private void handleTweetModelMessage(TweetModel tweetModel) {
        if(isActive ) {
            pubSubClient.publish(tweetModel);
        }
    }
}
