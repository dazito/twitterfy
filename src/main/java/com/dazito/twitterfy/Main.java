package com.dazito.twitterfy;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
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
import java.util.ArrayList;
import java.util.List;

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

        final String key = TwitterfyConfiguration.getConfiguration().getTwitterApiKey();
        final String secret = TwitterfyConfiguration.getConfiguration().getTwitterApiSecret();
        final String token = TwitterfyConfiguration.getConfiguration().getTwitterApiToken();
        final String tokenSecret = TwitterfyConfiguration.getConfiguration().getTwitterApiTokenSecret();
        final String[] keywords = TwitterfyConfiguration.getConfiguration().getSubscribeKeywords();

        // Create Actor System
        final ActorSystem actorSystem = ActorSystemContainer.getInstance().getActorSystem();

        // Start scheduler actor
        actorSystem.actorOf(SchedulerActor.props(), "scheduler");

        // Create actor system on startup
        ActorRef httpActor = actorSystem.actorOf(HttpActor.props(), HttpActor.class.getSimpleName());
        ActorRef awsSnsActor = AkkaUtil.configureRouter(AwsSnsActor.class, Constant.AWS_SNS_ROUTER_NAME);
        ActorRef awsSqsActor = AkkaUtil.configureRouter(AwsSqsActor.class, Constant.AWS_SQS_ROUTER_NAME);
        ActorRef gcPubsubActor = AkkaUtil.configureRouter(GcPubsubActor.class, Constant.GC_PUBSUB_ROUTER_NAME);
        ActorRef databaseActor = AkkaUtil.configureRouter(DatabaseActor.class, Constant.DATABASE_ROUTER_NAME);
        ActorRef dynamoDbActor = AkkaUtil.configureRouter(DynamoDbActor.class, Constant.AWS_DYNAMO_DB_ROUTER_NAME);

        List<ActorRef> actorRefList = new ArrayList<>();
        actorRefList.add(httpActor);
        actorRefList.add(awsSnsActor);
        actorRefList.add(awsSqsActor);
        actorRefList.add(gcPubsubActor);
        actorRefList.add(databaseActor);
        actorRefList.add(dynamoDbActor);

        // Setup and start the HTTP server - Websocket
        HttpServer httpServer = new HttpServerImpl(httpActor);
        httpServer.start();

        final ActorRef tweeterRouter = AkkaUtil.configureRouter(TweetActor.class, Constant.TWEET_ROUTER_NAME, actorRefList);

        final Publisher publisher = new TwitterProducer(tweeterRouter);

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
            LOGGER.error("Could not subscribe to Twitter's feed - reason: {}", e.getMessage());
        }
    }

    private static List<ActorRef> setUpActors() {
        // Start scheduler actor
//        ActorSystemContainer.getInstance().getActorSystem().actorOf(SchedulerActor.props(), "scheduler");

        // Create actor system on startup
        ActorRef awsSnsActor = AkkaUtil.configureRouter(AwsSnsActor.class, Constant.AWS_SNS_ROUTER_NAME);
        ActorRef awsSqsActor = AkkaUtil.configureRouter(AwsSqsActor.class, Constant.AWS_SQS_ROUTER_NAME);
        ActorRef gcPubsubActor = AkkaUtil.configureRouter(GcPubsubActor.class, Constant.GC_PUBSUB_ROUTER_NAME);
        ActorRef databaseActor = AkkaUtil.configureRouter(DatabaseActor.class, Constant.DATABASE_ROUTER_NAME);
        ActorRef dynamoDbActor = AkkaUtil.configureRouter(DynamoDbActor.class, Constant.AWS_DYNAMO_DB_ROUTER_NAME);
        
        List<ActorRef> actorRefList = new ArrayList<>();
        actorRefList.add(awsSnsActor);
        actorRefList.add(awsSqsActor);
        actorRefList.add(gcPubsubActor);
        actorRefList.add(databaseActor);
        actorRefList.add(dynamoDbActor);
        
        return actorRefList;
    }



}
