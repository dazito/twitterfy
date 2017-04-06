package com.dazito.twitterfy.model;

/**
 * Created by daz on 02/04/2017.
 */
public class TweetModel {

    private final String tweet;
    private final String screenName;
    private final long timestamp;

    public TweetModel(String tweet, String screenName, long timestamp) {
        this.tweet = tweet;
        this.screenName = screenName;
        this.timestamp = timestamp;
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
}
