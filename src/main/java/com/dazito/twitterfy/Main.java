package com.dazito.twitterfy;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import akka.routing.RoundRobinGroup;
import com.dazito.twitterfy.actor.ActorSystemContainer;
import com.dazito.twitterfy.actor.SchedulerActor;
import com.dazito.twitterfy.actor.TweetActor;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.twitter.TwitterClient;
import com.dazito.twitterfy.twitter.TwitterProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by daz on 30/03/2017.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String TWEET_ROUTER_NAME = "tweet-router";

    public static void main(String args[]) {

        // Load configurations
        try {
            TwitterfyConfiguration.getConfiguration().loadConfiguration();
        }
        catch (IOException e) {
            LOGGER.error("Could not load properties configuration file - reason: {}", e.getMessage());
        }

        // Start scheduler actor
        ActorSystemContainer.getInstance().getActorSystem().actorOf(SchedulerActor.props(), "scheduler");

        // Create actor system on startup
        final ActorRef tweeterRouter = configureAkkaRouter();

        final String key = TwitterfyConfiguration.getConfiguration().getTwitterApiKey();
        final String secret = TwitterfyConfiguration.getConfiguration().getTwitterApiSecret();
        final String token = TwitterfyConfiguration.getConfiguration().getTwitterApiToken();
        final String tokenSecret = TwitterfyConfiguration.getConfiguration().getTwitterApiTokenSecret();
        final String[] keywords = TwitterfyConfiguration.getConfiguration().getSubscribeKeywords();

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


    private static ActorRef configureAkkaRouter() {
        // Create actor system on startup
        final ActorSystem actorSystem = ActorSystemContainer.getInstance().getActorSystem();

        // Simulate a list of actor paths
        Iterable<String> actors = new ArrayList<>();

        // Create Router actor
        ActorRef router = actorSystem.actorOf(new RoundRobinGroup(actors).props());

        ActorRef tweetRouter = actorSystem.actorOf(
                FromConfig.getInstance().props(Props.create(TweetActor.class)), TWEET_ROUTER_NAME
        );

        return tweetRouter;
    }
}
