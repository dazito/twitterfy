package com.dazito.twitterfy.http;

import akka.actor.ActorRef;
import io.vertx.core.Vertx;

/**
 * Created by daz on 12/04/2017.
 */
public class HttpServerImpl implements HttpServer{

    private ActorRef httpActor;

    public HttpServerImpl(ActorRef httpActor) {
        this.httpActor = httpActor;
    }

    @Override
    public void start() {
        Vertx.vertx().deployVerticle(new HttpServerVerticle(httpActor));
    }
}
