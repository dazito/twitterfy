package com.dazito.twitterfy.twitter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.dazito.twitterfy.Publisher;
import com.dazito.twitterfy.model.TweetModel;

/**
 * Created by daz on 01/12/2015.
 */
public class TwitterProducer implements Publisher{

    private final ActorRef router;
    private final ActorSystem actorSystem;

    public TwitterProducer(ActorSystem actorSystem, ActorRef router) {
        this.router = router;
        this.actorSystem = actorSystem;
    }

    @Override
    public void publish(TweetModel tweetModel) {
        router.tell(tweetModel, ActorRef.noSender());
    }
}
