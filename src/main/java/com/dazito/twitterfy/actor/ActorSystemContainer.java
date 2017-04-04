package com.dazito.twitterfy.actor;

import akka.actor.ActorSystem;

/**
 * Created by daz on 02/04/2017.
 */
public class ActorSystemContainer {

    private final ActorSystem actorSystem;

    private static ActorSystemContainer instance = new ActorSystemContainer();

    private ActorSystemContainer() {
        actorSystem = ActorSystem.create("twitterfy");
    }

    public static ActorSystemContainer getInstance() {
        return instance;
    }

    public ActorSystem getActorSystem() {
        return actorSystem;
    }
}
