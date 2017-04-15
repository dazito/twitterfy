package com.dazito.twitterfy.http;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.dazito.twitterfy.actor.ActorSystemContainer;
import com.dazito.twitterfy.actor.HttpActor;
import com.dazito.twitterfy.actor.message.CloseWebSocketConnectionEvent;
import com.dazito.twitterfy.actor.message.NewWebSocketConnectionEvent;
import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daz on 11/04/2017.
 */
public class HttpServerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);
    private static final String WS_SUBSCRIBE_PATH = "/ws/subscribe";
    
    private final ActorSystem actorSystem = ActorSystemContainer.getInstance().getActorSystem();
    private ActorRef httpActor;

    @Override
    public void start() throws Exception {
        httpActor = actorSystem.actorOf(HttpActor.props(), HttpActor.class.getSimpleName());
        createHttpServer();
    }

    public void createHttpServer() {
        vertx.createHttpServer().websocketHandler(serverWebSocket -> {
            if(WS_SUBSCRIBE_PATH.equals(serverWebSocket.path())) {
                httpActor.tell(new NewWebSocketConnectionEvent(serverWebSocket), ActorRef.noSender());

                // Handle socket closed on the HttpActor
                serverWebSocket.closeHandler(event -> {
                    httpActor.tell(new CloseWebSocketConnectionEvent(serverWebSocket), ActorRef.noSender());
                });
            }
            else {
                serverWebSocket.reject();
            }
        })
        .listen(9191);
    }
}
