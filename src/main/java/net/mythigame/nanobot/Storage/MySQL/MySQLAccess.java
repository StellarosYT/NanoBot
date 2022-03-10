package net.mythigame.nanobot.Storage.MySQL;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLAccess {

    private final MySQLCredentials credentials;
    private HikariDataSource hikariDataSource;

    public MySQLAccess(MySQLCredentials credentials){
        this.credentials = credentials;
    }

    private void setupHikariCP(){
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setJdbcUrl(credentials.toURI());
        hikariConfig.setUsername(credentials.getUser());
        hikariConfig.setPassword(credentials.getPassword());
        hikariConfig.setMaxLifetime(600000L); // 10 minutes
        // PAS NECESSAIRE hikariConfig.setIdleTimeout(300000L); // 5 minutes
        hikariConfig.setLeakDetectionThreshold(300000L);
        hikariConfig.setConnectionTimeout(10000L); // 10 secondes

        this.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    public void initPool(){
        setupHikariCP();
    }

    public void closePool(){
        this.hikariDataSource.close();
    }

    public Connection getConnection() throws SQLException {
        if(this.hikariDataSource == null){
            setupHikariCP();
        }
        return this.hikariDataSource.getConnection();
    }
}
