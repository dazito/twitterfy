/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dazito.twitterfy.actor;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.dazito.twitterfy.actor.message.CloseWebSocketConnectionEvent;
import com.dazito.twitterfy.actor.message.NewWebSocketConnectionEvent;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * @author pmachado
 */
public class HttpActor extends UntypedActor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpActor.class);
    
    private Map<ServerWebSocket, ActorRef> actorRefMap;
    
    public static Props props() {
        return Props.create(HttpActor.class);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        actorRefMap = new HashMap<>();
    }

    @Override
    public void postStop() throws Exception {
        actorRefMap.clear();
        actorRefMap = null;
        super.postStop(); 
    }

    
    
    
    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof NewWebSocketConnectionEvent) {
            final NewWebSocketConnectionEvent event = (NewWebSocketConnectionEvent) message;
            final ServerWebSocket ws = event.getWebSocket();

            final ActorRef actorRef = getContext().actorOf(WebSocketActor.props(ws));
            actorRefMap.put(ws, actorRef);
        }
        else if(message instanceof String) {
            final String msg = (String) message;

            // Let each WebSocketActor push down the message to the client
            actorRefMap
                    .keySet()
                    .forEach(serverWebSocket -> actorRefMap.get(serverWebSocket).tell(msg, getSelf()));
        }
        else if(message instanceof CloseWebSocketConnectionEvent) {
            final CloseWebSocketConnectionEvent event = (CloseWebSocketConnectionEvent) message;
            final ServerWebSocket ws = event.getWebSocket();

            actorRefMap
                    .keySet()
                    .stream()
                    .filter(serverWebSocket -> Objects.equals(serverWebSocket, ws))
                    .findFirst()
                    .ifPresent(serverWebSocket -> {
                        actorRefMap.get(serverWebSocket).tell(PoisonPill.getInstance(), getSelf());
                        actorRefMap.remove(serverWebSocket);
                    });
        }
        else {
            unhandled(message);
        }
    }
    
}
