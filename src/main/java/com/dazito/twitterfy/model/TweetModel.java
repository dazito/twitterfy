package com.dazito.twitterfy.model;

/**
 * Created by daz on 02/04/2017.
 */
public class TweetModel {

    private final String tweet;
    private final String screenName;
    private final long timestamp;
    private final Long id;

    public TweetModel(Long id, String tweet, String screenName, long timestamp) {
        this.id = id;
        this.tweet = tweet;
        this.screenName = screenName;
        this.timestamp = timestamp;
    }

    public TweetModel(String tweet, String screenName, long timestamp) {
        this(null, tweet, screenName, timestamp);
    }

    public String getTweet() {
        return tweet;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(id)
                .append("-||-")
                .append(screenName)
                .append("-||-")
                .append(tweet)
                .append("-||-")
                .append(timestamp)
                .toString();
    }
}
