package net.mythigame.nanobot.Storage.MySQL;

public class MySQLCredentials {
    private final String host;
    private final String user;
    private final String password;
    private final String database;
    private final int port;

    public MySQLCredentials(String host, String user, String password, String database, int port){
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;
        this.port = port;
    }

    public String toURI(){

        return "jdbc:mysql://" +
                host +
                ":" +
                port +
                "/" +
                database +
                "?useSSL=false";
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
