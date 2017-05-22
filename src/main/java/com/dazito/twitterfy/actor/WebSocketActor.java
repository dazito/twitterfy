package com.dazito.twitterfy.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import com.dazito.twitterfy.model.TweetModel;
import com.google.gson.Gson;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daz on 11/04/2017.
 */
public class WebSocketActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketActor.class);

    private ServerWebSocket serverWebSocket;
    private final Gson gson = new Gson();

    public WebSocketActor(ServerWebSocket serverWebSocket) {
        this.serverWebSocket = serverWebSocket;
    }

    public static Props props(final ServerWebSocket serverWebSocket) {
        return Props.create(WebSocketActor.class, serverWebSocket);
    }


    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof TweetModel) {
            final TweetModel tweetModel = (TweetModel) message;
            final String tweetModelJson = gson.toJson(tweetModel, TweetModel.class);

            serverWebSocket.writeTextMessage(tweetModelJson);
        }
        else if(message instanceof String) {
            String msg = (String) message;

            // Push down the message to the client
            serverWebSocket.writeTextMessage(msg);
        }
        else {
            unhandled(message);
        }
    }
}
