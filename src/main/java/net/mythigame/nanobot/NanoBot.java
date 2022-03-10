package net.mythigame.nanobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.mythigame.nanobot.Storage.Guilds;
import net.mythigame.nanobot.Storage.MySQL.MySQLManager;
import net.mythigame.nanobot.Storage.Redis.RedisManager;
import net.mythigame.nanobot.consoleCommands.CommandMap;
import net.mythigame.nanobot.event.Listeners;
import net.mythigame.nanobot.slashCommands.SlashCommandManager;
import net.mythigame.nanobot.utils.Configuration;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;
import org.redisson.api.RedissonClient;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.mythigame.nanobot.slashCommands.utils.voteGameUserManager.*;
import static net.mythigame.nanobot.utils.lavaplayer.PlayerManager.disconnectAllVocalChannel;
import static net.mythigame.nanobot.utils.Libs.*;

@SuppressWarnings({"unused", "CommentedOutCode"})
public class NanoBot implements Runnable {

    private static JDA client;
    public static final Configuration CONFIGURATION;
    private final CommandMap commandMap = new CommandMap(this);
    private boolean running;
    private final Scanner scanner = new Scanner(System.in);
    private static final OkHttpClient httpClient = new OkHttpClient();
    public static long globalCount = 0;

    static {
        Configuration configuration = null;
        try{
            configuration = new Configuration("config.json");
        }catch (IOException e){
            e.printStackTrace();
        }

        CONFIGURATION = configuration;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    public NanoBot() throws LoginException, IllegalArgumentException, RateLimitedException {
        System.out.println(ANSI_GREEN + "[STATUS] NANOBOT EST EN COURS DE DÉMARRAGE..." + ANSI_RESET);
        if(CONFIGURATION == null){
            System.out.println(ANSI_RED + "[CONSOLE] Le fichier de configuration n'a pas été chargé !" + ANSI_RESET);
            return;
        }
        JDABuilder builder = JDABuilder.createDefault("");
        builder.setToken(CONFIGURATION.getString("token", "Veuillez preciser un token ici !"));
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.enableIntents(GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_INVITES, GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_WEBHOOKS);
        builder.addEventListeners(new Listeners(commandMap));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        builder.setActivity(Activity.watching("+ 100 000 utilisateurs | En cours de développement"));

        try{
            MySQLManager.initAllConnection();
            RedisManager.initAllConnection();
            client = builder.build().awaitReady();
            client.addEventListener(new SlashCommandManager());
            System.out.println(ANSI_GREEN + "[STATUS] NANOBOT EST EN LIGNE !" + ANSI_RESET);
            CONFIGURATION.save();
            System.out.println("En ligne sur : " + getTotalGuilds() + " discords.");
            System.out.println("Membres : " + getTotalMembers() + " au total.");
        }catch (Exception e){
            System.out.println(ANSI_RED + "[STATUS] UNE ERREUR EST SURVENUE PENDANT LE DÉMARRAGE !" + ANSI_RESET);
            CONFIGURATION.save();
            e.printStackTrace();
        }
        clearAllVoteGameUser();
        update15m();
        update12h();
        update24h();
        update();
    }

    public static void update(){
        reloadOnlineDiscordSQL();
        updateSite();
        updateStats();
    }

    public static void update15m(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                dropTooLateVoteGame(1800000);
            }
        }, 900000);
    }

    public static void update12h(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                updateSite();
                updateStats();
            }
        }, 43200000);
    }

    public static void update24h(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                reloadOnlineDiscordSQL();
                restart();
            }
        }, 86400000);
    }

    public static void clearStats(){
        final Connection connection;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement( "DELETE FROM stats");

            preparedStatement.executeUpdate();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void updateStats(){
        clearStats();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        final Connection connection;

        try {
            Date dateAvant = sdf.parse("01/02/2021");
            Date dateApres = new Date();
            long diff = dateApres.getTime() - dateAvant.getTime();
            long res = (diff / (1000*60*60*24));
            List<Command> commandsList = getJda().retrieveCommands().complete();

            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO stats (guilds, members, commands, dayofdev) VALUES (?, ?, ?, ?)");
            preparedStatement.setLong(1, getTotalGuilds());
            preparedStatement.setLong(2, getTotalMembers());
            preparedStatement.setInt(3, commandsList.size());
            preparedStatement.setLong(4, res);

            preparedStatement.executeUpdate();
            connection.close();
        }catch (SQLException | ParseException e){
            e.printStackTrace();
        }
    }

    public static void updateSite(){
        JSONObject payload = new JSONObject().put("server_count", getJda().getGuilds().size());
        JDA.ShardInfo info = getJda().getShardInfo();
        payload.put("shard_id", info.getShardId()).put("shard_count", info.getShardTotal());
        try {
            httpClient.newCall(new Request.Builder()
                    .url("https://discordbots.org/api/bots/" + getJda().getSelfUser().getId() + "/stats")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjMzNzEzNTc5MTE0MDc2NTcwMCIsImJvdCI6dHJ1ZSwiaWF0IjoxNTE0NDExNjk0fQ.0bJT6xWY4G63qrA8pIK2A97h3CVjBlPVlcfjEQyKp5o")
                    .post(RequestBody.create(MediaType.parse("application/json"), payload.toString()))
                    .build()
            ).execute().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JDA getJda(){
        return client;
    }

    @Override
    public void run(){
        running = true;

        while (running){
            if(scanner.hasNextLine()) commandMap.commandConsole(scanner.nextLine());
        }

        try{
            shutdownSave();
            RedisManager.closeAllConnection();
            MySQLManager.closeAllConnection();
            stop();
            scanner.close();
        }catch (RuntimeException e){
            System.out.println(ANSI_RED + "[STATUS] UNE ERREUR EST SURVENUE PENDANT L'ARRÊT !" + ANSI_RESET);
            e.printStackTrace();
        }
    }

    private static void shutdownSave(){
        final RedissonClient redissonClient = RedisManager.GUILDS.getRedisAccess().getRedissonClient();
        Set<Guilds> guilds = new HashSet<>();
        Iterable<String> keys = redissonClient.getKeys().getKeysByPattern("guild:*");
        for(String key : keys){
            guilds.add((Guilds) redissonClient.getBucket(key).get());
        }
        if(!guilds.isEmpty()){
            guilds.forEach(g -> {
                g.update(g.getUuid());
                g.sendToMySQL(g.getUuid());
                g.removeFromRedis(g.getUuid());
            });
        }
    }

    public static void main(String[] args){
        try{
            long start = System.nanoTime();
            NanoBot nanoBot = new NanoBot();
            new Thread(nanoBot, "bot").start();
            long end = System.nanoTime();
            double result = (end - start) / 1e9;
            System.out.println("Démarrage effectué en "+ result + " secondes !");
        }catch (LoginException | IllegalArgumentException | RateLimitedException e){
            e.printStackTrace();
        }
    }

    public static void reloadOnlineDiscordSQL() {
        System.out.println(ANSI_CYAN + "[CACHE/SQL] Récupérations des discord en cours..." + ANSI_RESET);
        client.getGuilds().forEach(guild -> {
            Guilds guilds = new Guilds();
            guilds.getGuild(guild).sendToMySQL();
        });
        System.out.println(ANSI_CYAN + "[CACHE/SQL] Opérationnel !" + ANSI_RESET);
    }

    public static void restart(){
        long start = System.nanoTime();
        System.out.println(ANSI_GREEN + "[STATUS] NANOBOT EST EN COURS DE REDÉMARRAGE..." + ANSI_RESET);
        totalMembers = 0;
        disconnectAllVocalChannel();
        try{
            System.out.println(ANSI_GREEN + "[STATUS] NANOBOT EST EN COURS D'ARRÊT..." + ANSI_RESET);
            shutdownSave();
            RedisManager.closeAllConnection();
            MySQLManager.closeAllConnection();
            client.shutdown();
            System.out.println(ANSI_RED + "[STATUS] NANOBOT EST HORS LIGNE !" + ANSI_RESET);
            new NanoBot();
        }catch (Exception e){
            System.out.println(ANSI_RED + "[STATUS] UNE ERREUR EST SURVENUE PENDANT LE REDÉMARRAGE !" + ANSI_RESET);
            e.printStackTrace();
        }
        long end = System.nanoTime();
        double result = (end - start) / 1e9;
        System.out.println("Redémarrage effectué en "+ result + " secondes !");
    }

    public static void stop(){
        System.out.println(ANSI_GREEN + "[STATUS] NANOBOT EST EN COURS D'ARRÊT..." + ANSI_RESET);
        shutdownSave();
        disconnectAllVocalChannel();
        RedisManager.closeAllConnection();
        MySQLManager.closeAllConnection();
        client.shutdown();
        System.out.println(ANSI_RED + "[STATUS] NANOBOT EST HORS LIGNE !" + ANSI_RESET);
        System.exit(0);
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    //public static final String ANSI_BLUE = "\u001B[34m";
    //public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    //public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    //public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    //public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    //public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    //public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    //public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    //public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

}
