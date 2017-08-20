package com.dazito.twitterfy.db;

import com.dazito.twitterfy.configuration.TwitterfyConfiguration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by daz on 02/04/2017.
 */
public class ConnectionPool {
    private static ConnectionPool connectionPool = new ConnectionPool();
    private static final TwitterfyConfiguration twitterfyConfiguration = TwitterfyConfiguration.getConfiguration();

    private HikariDataSource dataSource;


    private ConnectionPool() {
        if(twitterfyConfiguration.isDbActive()) {
            final TwitterfyConfiguration configuration = TwitterfyConfiguration.getConfiguration();
            HikariConfig config = new HikariConfig();

            config.setJdbcUrl(configuration.getConnectionPoolJdbcUrl());
            config.setUsername(configuration.getConnectionPoolDbUsername());
            config.setPassword(configuration.getConnectionPoolDbPassword());

            dataSource = new HikariDataSource(config);;
        }
    }

    public static Connection getConnection() {
        if(twitterfyConfiguration.isDbActive() == false) {
            return null;
        }
        try {
            return connectionPool.dataSource.getConnection();
        }
        catch (SQLException e) {
            System.err.println("Could not get a connection from the pool - reason: " + e.getMessage());
            return null;
        }
    }
}
