package com.dazito.twitterfy.actor;

import akka.actor.*;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.db.DbClient;
import com.dazito.twitterfy.model.TweetModel;
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

    private static final String HTTP_ACTOR_PATH = "/user/" + HttpActor.class.getSimpleName();

    private final static int HTTP_ACTOR_IDENTIFY_ID = 1;

    private DbClient dbClient;
    private Set<String> filterKeywords;
    private ActorRef httpActor;
    private ActorRef awsSnsActor;
    private ActorRef awsSqsActor;
    private ActorRef gcPubsubActor;
    private ActorRef databaseActor;

    public static Props props(ActorRef awsSnsActor, ActorRef awsSqsActor, ActorRef gcPubsubActor, ActorRef databaseActor) {
        return Props.create(TweetActor.class, awsSnsActor, awsSqsActor, gcPubsubActor, databaseActor);
    }

    public TweetActor(ActorRef awsSnsActor, ActorRef awsSqsActor, ActorRef gcPubsubActor, ActorRef databaseActor) {
        this.awsSnsActor = awsSnsActor;
        this.awsSqsActor = awsSqsActor;
        this.gcPubsubActor = gcPubsubActor;
        this.databaseActor = databaseActor;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        dbClient = new DbClient();
        filterKeywords = TwitterfyConfiguration.getConfiguration().getFilterKeywords();

        context().actorSelection(HTTP_ACTOR_PATH).tell(new Identify(HTTP_ACTOR_IDENTIFY_ID), getSelf());
        LOGGER.info("*** Tweet Actor Created  ***");
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

            if(containsKeyword(tweetModel.getTweet())) {
                if(httpActor != null) {
                    httpActor.tell(tweetModel, getSelf());
                }

                awsSnsActor.tell(tweetModel, getSelf());
                awsSqsActor.tell(tweetModel, getSelf());
                gcPubsubActor.tell(tweetModel, getSelf());
                databaseActor.tell(tweetModel, getSelf());
            }
        }
        else if(message instanceof ActorIdentity) {
            ActorIdentity actorIdentity = (ActorIdentity) message;
            if(actorIdentity.correlationId().equals(HTTP_ACTOR_IDENTIFY_ID)) {
                httpActor = actorIdentity.getRef();
            }
        }
        else {
            unhandled(message);
        }
    }

    private boolean containsKeyword(String tweet) {
        final String[] splittedTweet = tweet.trim().toLowerCase().split(" +");
        final Set<String> tweetSet = new HashSet<>(Arrays.asList(splittedTweet));

        return tweetSet.stream().anyMatch(this::streamFilter);
    }

    private boolean streamFilter(String str) {
        return filterKeywords.contains(str.toLowerCase());
    }
}
