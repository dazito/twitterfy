package com.dazito.twitterfy.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daz on 11/04/2017.
 */
public class WebsocketActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketActor.class);

    private ServerWebSocket serverWebSocket;

    public WebsocketActor(ServerWebSocket serverWebSocket) {
        this.serverWebSocket = serverWebSocket;
    }

    public static Props props(final ServerWebSocket serverWebSocket) {
        return Props.create(WebsocketActor.class, serverWebSocket);
    }


    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof String) {
            String msg = (String) message;

            // Push down the message to the client
            serverWebSocket.writeTextMessage(msg);
        }
    }
}
