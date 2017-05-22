package com.dazito.twitterfy.util;

import io.vertx.core.http.HttpServerResponse;

/**
 * Created by daz on 11/05/2017.
 */
public final class VertxUtil {

    private VertxUtil() {}

    public static HttpServerResponse setContentTypeJson(HttpServerResponse response) {
        return response.putHeader("Content-Type", "application/json");
    }
}
