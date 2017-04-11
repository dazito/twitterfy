package com.dazito.twitterfy.http;

import akka.actor.Props;
import akka.actor.UntypedActor;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daz on 11/04/2017.
 */
public class WebSocketActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketActor.class);

    private ServerWebSocket serverWebSocket;

    public WebSocketActor(ServerWebSocket serverWebSocket) {
        this.serverWebSocket = serverWebSocket;
    }

    public static Props props(final ServerWebSocket serverWebSocket) {
        return Props.create(WebSocketActor.class, new WebSocketActor(serverWebSocket));
    }

    @Override
    public void preStart() throws Exception {
        LOGGER.info("WS prestart");
        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        LOGGER.info("WS POSTSTOP");
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        LOGGER.info("WS ACTOR received a message");

        if(message instanceof String) {
            String msg = (String) message;
            LOGGER.info("WS - message: {}", msg);

            serverWebSocket.writeTextMessage("Reply: " + msg + " .|.");
        }
    }
}
