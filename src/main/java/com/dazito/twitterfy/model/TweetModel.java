package com.dazito.twitterfy.model;

/**
 * Created by daz on 02/04/2017.
 */
public class TweetModel {

    private final String tweet;
    private final String screenName;

    public TweetModel(String tweet, String screenName) {
        this.tweet = tweet;
        this.screenName = screenName;
    }

    public TweetModel(String tweet) {
        this(tweet, "");
    }

    public String getTweet() {
        return tweet;
    }

    public String getScreenName() {
        return screenName;
    }
}
