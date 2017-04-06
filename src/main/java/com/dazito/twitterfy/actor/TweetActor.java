package com.dazito.twitterfy.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import com.dazito.twitterfy.db.DbClient;
import com.dazito.twitterfy.model.TweetModel;
import org.jooq.exception.DetachedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daz on 02/04/2017.
 */
public class TweetActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TweetActor.class);

    private DbClient dbClient;

    public static Props props() {
        return Props.create(TweetActor::new);
    }

    @Override
    public void preStart() throws Exception {
        dbClient = new DbClient();
        LOGGER.info("*** Tweet Actor Created  ***");
        super.preStart();
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

            try {
                dbClient.insertTweet(tweetModel.getTweet(), tweetModel.getScreenName(), tweetModel.getTimestamp());
            }
            catch (DetachedException ex) {
                LOGGER.error("Could not perform INSERT operation - reason: " + ex.getMessage());
            }
        }
    }
}
