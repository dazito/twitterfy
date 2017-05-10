package com.dazito.twitterfy.util;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import com.dazito.twitterfy.actor.ActorSystemContainer;

/**
 * Created by daz on 16/04/2017.
 */
public final class AkkaUtil {

    private AkkaUtil() {}

    public static ActorRef configureTwitterRouter(Class actorClass, String actorName, ActorRef awsSns, ActorRef awsSqs, ActorRef gcPubsub, ActorRef db) {
        // Create actor system on startup
        final ActorSystem actorSystem = ActorSystemContainer.getInstance().getActorSystem();

        return actorSystem.actorOf(
                FromConfig.getInstance().props(Props.create(actorClass, awsSns, awsSqs, gcPubsub, db)),
                actorName
        );
    }

    public static ActorRef configureRouter(Class actorClass, String actorName) {
        // Create actor system on startup
        final ActorSystem actorSystem = ActorSystemContainer.getInstance().getActorSystem();

        return actorSystem.actorOf(
                FromConfig.getInstance().props(Props.create(actorClass)), actorName
        );
    }
}
