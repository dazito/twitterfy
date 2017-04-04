package com.dazito.twitterfy.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by daz on 02/04/2017.
 */
public class ConnectionPool {
    private static ConnectionPool connectionPool = new ConnectionPool();

    private final HikariDataSource dataSource;

    private ConnectionPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://your-database-server-ip:port/database-name");
        config.setUsername("your-database-username");
        config.setPassword("your-database-password");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() {
        try {
            return connectionPool.dataSource.getConnection();
        }
        catch (SQLException e) {
            System.err.println("Could not get a connection from the pool - reason: " + e.getMessage());
            return null;
        }
    }
}
