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


    @Override
    public void onStatus(Status status) {
        final String text = status.getText();
        final String screenName = status.getUser().getScreenName();
        final long createdAt = status.getCreatedAt().toInstant().toEpochMilli();

        publisher.publish(new TweetModel(text, screenName, createdAt));
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        LOGGER.info("onDeletionNotice" + statusDeletionNotice.getStatusId());
    }

    @Override
    public void onTrackLimitationNotice(int i) {

    }

    @Override
    public void onScrubGeo(long l, long l1) {
        LOGGER.info("onScrubGeo");
    }

    @Override
    public void onStallWarning(StallWarning stallWarning) {
        LOGGER.info("onStallWarning: " + stallWarning.getMessage() + " | percentage: " + stallWarning.getPercentFull());
    }

    @Override
    public void onException(Exception e) {
        LOGGER.info("Exception: " + e.getMessage());
    }
}
