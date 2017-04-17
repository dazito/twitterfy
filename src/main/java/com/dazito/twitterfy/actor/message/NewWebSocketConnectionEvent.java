package com.dazito.twitterfy.actor.message;

import io.vertx.core.http.ServerWebSocket;

import java.io.Serializable;

/**
 * Created by daz on 15/04/2017.
 */
public class NewWebSocketConnectionEvent implements Serializable {

    private final ServerWebSocket webSocket;

    public NewWebSocketConnectionEvent(ServerWebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public ServerWebSocket getWebSocket() {
        return webSocket;
    }
}
