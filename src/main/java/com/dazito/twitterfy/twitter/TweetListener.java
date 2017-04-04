package com.dazito.twitterfy.twitter;

import com.dazito.twitterfy.Publisher;
import com.dazito.twitterfy.model.TweetModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 * Created by daz on 30/11/2015.
 */
public class TweetListener implements StatusListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TweetListener.class);

    private final Publisher publisher;

    public TweetListener(Publisher publisher) {
        this.publisher = publisher;
    }


    public void onStatus(Status status) {
        LOGGER.info("New status received: " + status.getText());
        publisher.publish(new TweetModel(status.getText(), status.getUser().getScreenName()));
    }

    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        LOGGER.info("onDeletionNotice" + statusDeletionNotice.getStatusId());
    }

    public void onTrackLimitationNotice(int i) {

    }

    public void onScrubGeo(long l, long l1) {
        LOGGER.info("onScrubGeo");
    }

    public void onStallWarning(StallWarning stallWarning) {
        LOGGER.info("onStallWarning: " + stallWarning.getMessage() + " | percentage: " + stallWarning.getPercentFull());
    }

    public void onException(Exception e) {
        LOGGER.info("Exception: " + e.getMessage());
    }
}
