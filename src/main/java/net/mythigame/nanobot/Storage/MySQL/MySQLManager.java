package net.mythigame.nanobot.Storage.MySQL;

public enum MySQLManager {

    NANOBOT(new MySQLCredentials("localhost", "nanobot", "WW1up0IB0pDhziFWZmqS", "nanobot", 3306)),
    INTRANET(new MySQLCredentials("localhost", "intranet", "eHQkZL1KGq0&AYFq@Un3", "intranet", 3306));

    private final MySQLAccess mySQLAccess;

    MySQLManager(MySQLCredentials credentials){
        this.mySQLAccess = new MySQLAccess(credentials);
    }

    public MySQLAccess getMySQLAccess() {
        return mySQLAccess;
    }

    public static void initAllConnection(){
        for(MySQLManager mySQLManager : values()){
            mySQLManager.mySQLAccess.initPool();
        }
    }

    public static void closeAllConnection(){
        for(MySQLManager mySQLManager : values()){
            mySQLManager.mySQLAccess.closePool();
        }
    }

}
