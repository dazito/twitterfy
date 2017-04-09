package com.dazito.twitterfy.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created by daz on 05/04/2017.
 */
public final class TwitterfyConfiguration {

    private static final String PROPERTIES_FILE_NAME = "configuration.properties";
    private static final String TWITTER_API_KEY = "key";
    private static final String TWITTER_API_SECRET = "secret";
    private static final String TWITTER_API_TOKEN = "token";
    private static final String TWITTER_API_TOKEN_SECRET = "tokenSecret";
    private static final String TWITTER_KEYWORDS = "twitter-keywords";
    private static final String FILTER_KEYWORDS = "filter-keywords";
    private static final String JDBC_URL = "JdbcUrl";
    private static final String DB_USERNAME = "db-username";
    private static final String DB_PASSWORD = "db-password";

    private String twitterApiKey;
    private String twitterApiSecret;
    private String twitterApiToken;
    private String twitterApiTokenSecret;
    private String[] subscribeKeywords;
    private String connectionPoolJdbcUrl;
    private String connectionPoolDbUsername;
    private String connectionPoolDbPassword;
    private Set<String> filterKeywords;

    final Properties properties = new Properties();

    private static TwitterfyConfiguration instance = new TwitterfyConfiguration();
    public static TwitterfyConfiguration getConfiguration() {
        return instance;
    }

    private TwitterfyConfiguration() {
    }

    public void loadConfiguration() throws IOException {
        properties.load(TwitterfyConfiguration.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME));

        twitterApiKey = properties.getProperty(TWITTER_API_KEY, "");
        twitterApiSecret = properties.getProperty(TWITTER_API_SECRET, "");

        twitterApiToken = properties.getProperty(TWITTER_API_TOKEN, "");
        twitterApiTokenSecret = properties.getProperty(TWITTER_API_TOKEN_SECRET, "");
        connectionPoolJdbcUrl = properties.getProperty(JDBC_URL, "");
        connectionPoolDbUsername = properties.getProperty(DB_USERNAME, "");
        connectionPoolDbPassword = properties.getProperty(DB_PASSWORD, "");
        subscribeKeywords = parseCommaSeparatedStringToArray(properties.getProperty(TWITTER_KEYWORDS, ""));
        filterKeywords = parseCommaSeparatedStringToSet(properties.getProperty(FILTER_KEYWORDS, ""));
    }

    private String[] parseCommaSeparatedStringToArray(String csvString) {
        final String[] strArray = csvString.toLowerCase().split(",");

        for(int i = 0; i < strArray.length; i++) {
            strArray[i] = strArray[i].trim();
        }

        return strArray;
    }

    private Set<String> parseCommaSeparatedStringToSet(String csvString) {
        final String[] strArray = parseCommaSeparatedStringToArray(csvString.toLowerCase());

        return new HashSet<>(Arrays.asList(strArray));
    }

    public String getTwitterApiKey() {
        return twitterApiKey;
    }

    public String getTwitterApiSecret() {
        return twitterApiSecret;
    }

    public String getTwitterApiToken() {
        return twitterApiToken;
    }

    public String getTwitterApiTokenSecret() {
        return twitterApiTokenSecret;
    }

    public String[] getSubscribeKeywords() {
        return subscribeKeywords;
    }

    public String getConnectionPoolJdbcUrl() {
        return connectionPoolJdbcUrl;
    }

    public String getConnectionPoolDbUsername() {
        return connectionPoolDbUsername;
    }

    public String getConnectionPoolDbPassword() {
        return connectionPoolDbPassword;
    }

    public Set<String> getFilterKeywords() {
        return filterKeywords;
    }
}
