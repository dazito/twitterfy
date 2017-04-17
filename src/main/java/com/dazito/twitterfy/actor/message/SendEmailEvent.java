package com.dazito.twitterfy.actor.message;

import com.dazito.twitterfy.model.TweetModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by daz on 16/04/2017.
 */
public class SendEmailEvent implements Serializable {

    private List<TweetModel> tweetModelList;

    public SendEmailEvent(List<TweetModel> tweetModelList) {
        this.tweetModelList = tweetModelList;
    }

    public List<TweetModel> getTweetModelList() {
        return tweetModelList;
    }
}
