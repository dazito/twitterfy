package com.dazito.twitterfy.http;

import io.vertx.core.Vertx;

/**
 * Created by daz on 12/04/2017.
 */
public class HttpServerImpl implements HttpServer{


    @Override
    public void start() {
        Vertx.vertx().deployVerticle(new HttpServerVerticle());
    }
}
