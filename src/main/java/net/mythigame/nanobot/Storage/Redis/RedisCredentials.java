package net.mythigame.nanobot.Storage.Redis;

public class RedisCredentials {
    private final String ip;
    private final String password;
    private final int port;
    private final int database;
    private final String clientName;

    public RedisCredentials(String ip, String password, int port, int database) {
        this.ip = ip;
        this.password = password;
        this.port = port;
        this.database = database;
        this.clientName = "nanobot_storage";
    }

    public RedisCredentials(String ip, String password, int port, int database, String clientName) {
        this.ip = ip;
        this.password = password;
        this.port = port;
        this.database = database;
        this.clientName = clientName;
    }

    public String getIp() {
        return ip;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public int getDatabase(){
        return database;
    }

    public String getClientName() {
        return clientName;
    }

    public String toRedisURI(){
        return "redis://"+ ip +":" + port;
    }
}
