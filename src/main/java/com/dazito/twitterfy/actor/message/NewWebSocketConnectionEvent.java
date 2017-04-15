package com.dazito.twitterfy.actor.message;

import io.vertx.core.http.ServerWebSocket;

/**
 * Created by daz on 15/04/2017.
 */
public class NewWebSocketConnectionEvent {

    private final ServerWebSocket webSocket;

    public NewWebSocketConnectionEvent(ServerWebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public ServerWebSocket getWebSocket() {
        return webSocket;
    }
}
