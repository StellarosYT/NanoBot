package net.mythigame.nanobot.Storage.Redis;

public enum RedisManager {

    GUILDS(new RedisCredentials("localhost", "tYLt202DoUOboU87YTjy", 6379, 1)),
    DEVELOPERS(new RedisCredentials("localhost", "tYLt202DoUOboU87YTjy", 6379, 2));

    private final RedisAccess redisAccess;

    RedisManager(RedisCredentials credentials){
        this.redisAccess = new RedisAccess(credentials);
    }

    public RedisAccess getRedisAccess() {
        return redisAccess;
    }

    public static void initAllConnection(){
        for(RedisManager redisManager : values()){
            redisManager.redisAccess.initPool();
        }
    }

    public static void closeAllConnection(){
        for(RedisManager redisManager : values()){
            redisManager.redisAccess.getRedissonClient().getKeys().flushdb();
            redisManager.redisAccess.closePool();
        }
    }

}
