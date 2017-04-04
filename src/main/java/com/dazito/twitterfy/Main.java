package com.dazito.twitterfy;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import akka.routing.RoundRobinGroup;
import com.dazito.twitterfy.actor.ActorSystemContainer;
import com.dazito.twitterfy.actor.TweetActor;
import com.dazito.twitterfy.scheduler.Scheduler;
import com.dazito.twitterfy.twitter.TwitterClient;
import com.dazito.twitterfy.twitter.TwitterProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by daz on 30/03/2017.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String PROPERTIES_FILE_NAME = "configuration.properties";
    private static final String TWITTER_API_KEY = "key";
    private static final String TWITTER_API_SECRET = "secret";
    private static final String TWITTER_API_TOKEN = "token";
    private static final String TWITTER_API_TOKEN_SECRET = "tokenSecret";
    private static final String TWITTER_KEYWORDS = "keywords";
    public static final String TWEET_ROUTER_NAME = "tweet-router";

    public static void main(String args[]) {

        // Create actor system on startup
        final ActorRef tweeterRouter = configureAkkaRouter();

        Scheduler scheduler = new Scheduler();
        scheduler.setScheduler();

        final Properties properties = loadProperties();

        if(properties == null) {
            return;
        }

        final String key = properties.getProperty(TWITTER_API_KEY, "");
        final String secret = properties.getProperty(TWITTER_API_SECRET, "");

        final String token = properties.getProperty(TWITTER_API_TOKEN, "");
        final String tokenSecret = properties.getProperty(TWITTER_API_TOKEN_SECRET, "");

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

        final String[] keywords = loadKeywords(properties);

        try {
            twitterClient.setConfiguration(keywords);
            twitterClient.filter();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Properties loadProperties() {
        try {
            final Properties properties = new Properties();
            properties.load(Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME));

            return properties;
        }
        catch (IOException e) {
            LOGGER.error("Could not load properties file - reason: " + e.getMessage());
            return null;
        }
    }

    private static String[] loadKeywords(final Properties properties) {
        final String keywords = properties.getProperty(TWITTER_KEYWORDS, "");

        final String[] keywordArray = keywords.split(",");

        for(String kw : keywordArray) {
            kw = kw.trim();
        }

        return keywordArray;
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
