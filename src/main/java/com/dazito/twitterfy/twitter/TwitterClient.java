package com.dazito.twitterfy.twitter;

import com.dazito.twitterfy.Publisher;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;

/**
 * Created by daz on 30/03/2017.
 */
public class TwitterClient {

    private final String key;
    private final String secret;
    private final String token;
    private final String tokenSecret;
    private final boolean isJsonStoreEnabled;
    private final ConfigurationBuilder configuration;
    private final FilterQuery filterQuery = new FilterQuery();
    private final Publisher publisher;

    private TwitterStream twitterStream;

    public static class Builder {
        private String key;
        private String secret;
        private String token;
        private String tokenSecret;
        private boolean isJsonStoreEnabled;
        private Publisher publisher;

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder secret(String secret) {
            this.secret = secret;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder isJsonStoreEnabled(boolean isJsonStoreEnabled) {
            this.isJsonStoreEnabled = isJsonStoreEnabled;
            return this;
        }

        public Builder tokenSecret(String tokenSecret) {
            this.tokenSecret = tokenSecret;
            return this;
        }

        public Builder publisher(Publisher publisher) {
            this.publisher = publisher;
            return this;
        }

        public TwitterClient build() {
            return new TwitterClient(this);
        }
    }


    private TwitterClient(Builder builder) {
        this.key = builder.key;
        this.secret = builder.secret;
        this.token = builder.token;
        this.tokenSecret = builder.tokenSecret;
        this.isJsonStoreEnabled = builder.isJsonStoreEnabled;
        configuration = new ConfigurationBuilder()
                .setOAuthConsumerKey(key)
                .setOAuthConsumerSecret(secret)
                .setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(tokenSecret)
                .setJSONStoreEnabled(isJsonStoreEnabled);
        this.publisher = builder.publisher;
    }

    // Build the config with credentials for twitter 4j
    public void setConfiguration(String[] keywords) throws IOException {
        // create the twitter stream factory with the config
        TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(configuration.build());

        // get an instance of twitter stream
        twitterStream = twitterStreamFactory.getInstance();

        // provide the handler for twitter stream
        twitterStream.addListener(new TweetListener(publisher));

        filterQuery.track(keywords);
    }

    public void sample() {
        twitterStream.sample();
    }

    public void filter() {
        twitterStream.filter(filterQuery);
    }
}
