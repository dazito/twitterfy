package com.dazito.twitterfy.db;

import com.dazito.twitterfy.db.tables.Tweet;
import com.dazito.twitterfy.model.TweetModel;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by daz on 01/04/2017.
 */
public class DbClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbClient.class);

    private final Connection connection;
    private final DSLContext ctx;

    public DbClient () {
        this.connection = ConnectionPool.getConnection();
        this.ctx = DSL.using(connection, SQLDialect.MYSQL);
    }

    public void insertTweet(String tweet, String screenName, long createdAt) {
        ctx.insertInto(Tweet.TWEET,
                Tweet.TWEET.TWEET_, Tweet.TWEET.SCREENNAME, Tweet.TWEET.TIMESTAMP)
                .values(tweet, screenName, createdAt)
                .execute();
    }

    public List<TweetModel> getNotProcessedTweets() throws SQLException {
        final List<TweetModel> list;
        try(Stream<Record> result = ctx.select().from(Tweet.TWEET.getName()).where(Tweet.TWEET.PROCESSED.isFalse()).fetchStream()) {
            list = result
                    .map(this::getTweetModel)
                    .collect(Collectors.toList());
        }

        return list;
    }

    public void close() {
        try {
            connection.close();
        }
        catch (SQLException e) {
            LOGGER.error("Could not close connection - reason: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private TweetModel getTweetModel(Record record) {
        Long id = record.getValue(Tweet.TWEET.ID);
        String tweet = record.getValue(Tweet.TWEET.TWEET_);
        String screenName = record.getValue(Tweet.TWEET.SCREENNAME);
        Long timestamp = record.getValue(Tweet.TWEET.TIMESTAMP);
        Timestamp timestampStr = record.getValue(Tweet.TWEET.TIMESTAMPSTR);

        return new TweetModel(tweet, screenName, timestamp);
    }
}
