package com.dazito.twitterfy;

import akka.actor.ActorRef;
import com.dazito.twitterfy.actor.*;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.http.HttpServer;
import com.dazito.twitterfy.http.HttpServerImpl;
import com.dazito.twitterfy.twitter.TwitterClient;
import com.dazito.twitterfy.twitter.TwitterProducer;
import com.dazito.twitterfy.util.AkkaUtil;
import com.dazito.twitterfy.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by daz on 30/03/2017.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);



    public static void main(String args[]) throws Exception {

        // Load configurations
        try {
            TwitterfyConfiguration.getConfiguration().loadConfiguration();
        }
        catch (IOException e) {
            LOGGER.error("Could not load properties configuration file - reason: {}", e.getMessage());
            return;
        }

        // Setup and start the HTTP server - Websocket
        HttpServer httpServer = new HttpServerImpl();
        httpServer.start();

        final String key = TwitterfyConfiguration.getConfiguration().getTwitterApiKey();
        final String secret = TwitterfyConfiguration.getConfiguration().getTwitterApiSecret();
        final String token = TwitterfyConfiguration.getConfiguration().getTwitterApiToken();
        final String tokenSecret = TwitterfyConfiguration.getConfiguration().getTwitterApiTokenSecret();
        final String[] keywords = TwitterfyConfiguration.getConfiguration().getSubscribeKeywords();

        // Start scheduler actor
        ActorSystemContainer.getInstance().getActorSystem().actorOf(SchedulerActor.props(), "scheduler");

        // Create actor system on startup
        ActorRef awsSnsActor = AkkaUtil.configureRouter(AwsSnsActor.class, Constant.AWS_SNS_ROUTER_NAME);
        ActorRef awsSqsActor = AkkaUtil.configureRouter(AwsSqsActor.class, Constant.AWS_SQS_ROUTER_NAME);
        ActorRef gcPubsubActor = AkkaUtil.configureRouter(GcPubsubActor.class, Constant.GC_PUBSUB_ROUTER_NAME);
        ActorRef databaseActor = AkkaUtil.configureRouter(DatabaseActor.class, Constant.DATABASE_ROUTER_NAME);

        final ActorRef tweeterRouter = AkkaUtil.configureTwitterRouter(TweetActor.class, Constant.TWEET_ROUTER_NAME, awsSnsActor, awsSqsActor, gcPubsubActor, databaseActor);

        final Publisher publisher = new TwitterProducer(
                ActorSystemContainer.getInstance().getActorSystem(), tweeterRouter
        );

        final TwitterClient twitterClient = new TwitterClient.Builder()
                .key(key)
                .secret(secret)
                .token(token)
                .tokenSecret(tokenSecret)
                .isJsonStoreEnabled(true)
                .publisher(publisher)
                .build();

        try {
            twitterClient.setConfiguration(keywords);
            twitterClient.filter();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }



}
