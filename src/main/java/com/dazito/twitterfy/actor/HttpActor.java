/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dazito.twitterfy.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pmachado
 */
public class HttpActor extends UntypedActor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpActor.class);
    
    private List<ActorRef> actorList;
    
    public static Props props() {
        return Props.create(HttpActor.class);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        actorList = new ArrayList<>();
    }

    @Override
    public void postStop() throws Exception {
        actorList.clear();
        actorList = null;
        super.postStop(); 
    }

    
    
    
    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof ServerWebSocket) {
            ServerWebSocket ws = (ServerWebSocket) message;
            final ActorRef actorRef = getContext().actorOf(WebsocketActor.props(ws));
            actorList.add(actorRef);
        }
        else if(message instanceof String) {
            final String msg = (String) message;
            actorList.forEach(actor -> actor.tell(msg, getSelf()));
        }
        else {
            unhandled(message);
        }
    }
    
}
