package com.dazito.twitterfy.http;

import akka.actor.ActorRef;
import com.dazito.twitterfy.actor.message.CloseWebSocketConnectionEvent;
import com.dazito.twitterfy.actor.message.NewWebSocketConnectionEvent;
import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.dazito.twitterfy.util.StringUtil;
import com.dazito.twitterfy.util.VertxUtil;
import com.google.gson.JsonObject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by daz on 11/04/2017.
 */
public class HttpServerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);
    private static final String WS_SUBSCRIBE_PATH = "/api/ws/subscribe";

    private final Router router;

    private ActorRef httpActor;

    public HttpServerVerticle(ActorRef httpActor) {
        this.router = Router.router(vertx);
        this.httpActor = httpActor;
    }

    @Override
    public void start() throws Exception {
        setUpRouter(router);
        HttpServer server = createHttpServer();
        setWebsocketHandler(server);
        server.requestHandler(router::accept).listen(8080);
    }

    private HttpServer createHttpServer(){
        return vertx.createHttpServer();
    }

    private void setWebsocketHandler(HttpServer httpServer) {
        httpServer.websocketHandler(serverWebSocket -> {
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
        });
    }

    private void setUpRouter(Router router) {
        router.route().handler(BodyHandler.create());
        router.get("/api/configuration/twitter/keywords").handler(this::handleGetConfigurationTwitterKeywords);
        router.get("/api/configuration/twitter/keywords/filter").handler(this::handleGetConfigurationTwitterKeywordsFilter);
        router.get("/api/livefeed").handler(this::handleLifeFeed);
    }

//    private void createHttpServer() {
//        vertx.createHttpServer().websocketHandler(serverWebSocket -> {
//            if(WS_SUBSCRIBE_PATH.equals(serverWebSocket.path())) {
//                httpActor.tell(new NewWebSocketConnectionEvent(serverWebSocket), ActorRef.noSender());
//
//                // Handle socket closed on the HttpActor
//                serverWebSocket.closeHandler(event -> {
//                    httpActor.tell(new CloseWebSocketConnectionEvent(serverWebSocket), ActorRef.noSender());
//                });
//            }
//            else {
//                serverWebSocket.reject();
//            }
//        })
//        .listen(9191);
//    }

    private void handleGetConfigurationTwitterKeywords(RoutingContext routingContext) {
        final HttpServerResponse response = routingContext.response();

        final String[] keywordsArray = TwitterfyConfiguration.getConfiguration().getSubscribeKeywords();
        final String keywords = StringUtil.stringArrayToString(keywordsArray, ", ");

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("keywords", keywords);

        VertxUtil.setContentTypeJson(response).end(jsonObject.toString());
    }

    private void handleGetConfigurationTwitterKeywordsFilter(RoutingContext routingContext) {
        final HttpServerResponse response = routingContext.response();

        final Set<String> keywordsSet = TwitterfyConfiguration.getConfiguration().getFilterKeywords();
        final String keywords = StringUtil.stringSetToString(keywordsSet, ", ");

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("filterKeywords", keywords);

        VertxUtil.setContentTypeJson(response).end(jsonObject.toString());
    }

    private void handleLifeFeed(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        ClassLoader classLoader = getClass().getClassLoader();
        String htmlFilePath  = classLoader.getResource("websocketSubscribe.html").getPath();
        
        response.sendFile(htmlFilePath);
    }
}
