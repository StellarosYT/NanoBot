package net.mythigame.nanobot.Storage;

import net.dv8tion.jda.api.entities.Guild;
import net.mythigame.nanobot.NanoBot;
import net.mythigame.nanobot.Storage.MySQL.MySQLManager;
import net.mythigame.nanobot.Storage.Redis.RedisManager;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildProvider {
    public final String REDIS_KEY = "guild:";
    public static final String GUILD_TABLE = "servers";
    public static final String GUILD_COMMANDS = "servers_commands";
    public static final Guilds DEFAULT_GUILD = new Guilds(
            null, "en", null, 0, false
    );

    private final Guild guild;

    public GuildProvider(Guild guild) {
        this.guild = guild;
    }

    public GuildProvider(String uuid) {
        this.guild = NanoBot.getJda().getGuildById(uuid);
    }

    public Guilds getGuild() {
        Guilds guilds = getGuildFromRedis();

        if (guilds == null) {
            guilds = getGuildFromMySQL();
            if (guilds == null) {
                guilds = createNewGuild(guild);
            }
            updateRedisGuild(guilds);
        }
        return guilds;
    }

    public void updateRedisGuild(Guilds guilds) {
        final RedissonClient redissonClient = RedisManager.GUILDS.getRedisAccess().getRedissonClient();
        final String key = REDIS_KEY + this.guild.getId();
        final RBucket<Guilds> accountRBucket = redissonClient.getBucket(key);
        accountRBucket.set(guilds);
    }

    public void removeFromRedis() {
        final RedissonClient redissonClient = RedisManager.GUILDS.getRedisAccess().getRedissonClient();
        final String key = REDIS_KEY + this.guild.getId();
        final RBucket<Guilds> accountRBucket = redissonClient.getBucket(key);
        accountRBucket.deleteAsync();
    }

    public void updateMySQLGuild(Guilds guilds) {
        final Connection connection;
        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + GUILD_TABLE + " SET lang = ?, name = ?, size = ?, isSetup = ? WHERE uuid = ?");
            preparedStatement.setString(1, guilds.getLang());
            preparedStatement.setString(2, guilds.getName());
            preparedStatement.setInt(3, guilds.getSize());
            preparedStatement.setBoolean(4, guilds.isSetup());
            preparedStatement.setString(5, guilds.getUuid());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Guilds getGuildFromRedis() {
        final RedissonClient redissonClient = RedisManager.GUILDS.getRedisAccess().getRedissonClient();
        final String key = REDIS_KEY + this.guild.getId();
        final RBucket<Guilds> accountRBucket = redissonClient.getBucket(key);

        return accountRBucket.get();
    }

    public Guilds getGuildFromMySQL() {
        Guilds guilds = null;
        try {
            final Connection connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + GUILD_TABLE + " WHERE uuid = ?");
            preparedStatement.setString(1, guild.getId());
            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();
            if (resultSet.next()) {
                final String uuid = resultSet.getString("uuid");
                final String lang = resultSet.getString("lang");

                final String name = resultSet.getString("name");
                final int size = resultSet.getInt("size");
                final boolean isSetup = resultSet.getBoolean("isSetup");

                guilds = new Guilds(uuid, lang, name, size, isSetup);
                preparedStatement.close();
                connection.close();
                return guilds;
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guilds;
    }

    private Guilds createNewGuild(Guild guild) {
        try {

            final Guilds guilds = DEFAULT_GUILD.clone();
            final Connection connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + GUILD_TABLE + " (uuid, lang, name, size, isSetup) VALUES (?, ?, ?, ?, ?) ");
            preparedStatement.setString(1, guild.getId());
            preparedStatement.setString(2, guilds.getLang());
            preparedStatement.setString(3, guild.getName());
            preparedStatement.setInt(4, guild.getMemberCount());
            preparedStatement.setBoolean(5, guilds.isSetup());

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

            guilds.setUuid(guild.getId());
            return guilds;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createGuildsTables() {
        try {
            final Connection connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + GUILD_TABLE + "` "
                    + "(`uuid` VARCHAR(500) NOT NULL,"
                    + "`lang` VARCHAR(255) NOT NULL DEFAULT 'en',"
                    + "`name` VARCHAR(255) NOT NULL,"
                    + "`size` INT NOT NULL,"
                    + "`isSetup` TINYINT NOT NULL DEFAULT '0',"
                    + "PRIMARY KEY (`uuid`), UNIQUE KEY `uuid_UNIQUE` (`uuid`))");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}