package net.mythigame.nanobot.Storage.Redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class RedisAccess {
    private final RedisCredentials credentials;
    private RedissonClient redissonClient;

    public RedisAccess(RedisCredentials credentials) {
        this.credentials = credentials;
    }

    private void setupRedis(){
        final Config config = new Config();

        config.setCodec(new JsonJacksonCodec());
        config.setThreads(2 * 2); // nombre de coeur x 2
        config.setNettyThreads(2 * 2); // pareil qu'au dessus
        config.useSingleServer()
                .setAddress(credentials.toRedisURI())
                .setPassword(credentials.getPassword())
                .setDatabase(credentials.getDatabase())
                .setClientName(credentials.getClientName());

        this.redissonClient = Redisson.create(config);
    }

    public void initPool(){
        setupRedis();
    }

    public void closePool(){
        this.redissonClient.shutdown();
    }

    public RedissonClient initRedisson(RedisCredentials credentials){
        final Config config = new Config();

        config.setCodec(new JsonJacksonCodec());
        config.setThreads(2 * 2); // nombre de coeur x 2
        config.setNettyThreads(2 * 2); // pareil qu'au dessus
        config.useSingleServer()
                .setAddress(credentials.toRedisURI())
                .setPassword(credentials.getPassword())
                .setDatabase(credentials.getDatabase())
                .setClientName(credentials.getClientName());

        return Redisson.create(config);
    }

    public RedissonClient getRedissonClient() throws RedisException {
        if(this.redissonClient == null){
            setupRedis();
        }
        return this.redissonClient;
    }
}
