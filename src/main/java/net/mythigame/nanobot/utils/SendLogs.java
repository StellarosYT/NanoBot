package net.mythigame.nanobot.utils;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.NanoBot;
import net.mythigame.nanobot.Storage.MySQL.MySQLManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static net.mythigame.nanobot.NanoBot.*;


@SuppressWarnings({"ConstantConditions", "DuplicatedCode"})
public class SendLogs {

    private static final List<String> guildsIds = new ArrayList<>();
    private static final List<String> moderationChannelsIds = new ArrayList<>();
    private static final List<String> administrationChannelsIds = new ArrayList<>();

    public static void sendModsLog(MessageEmbed message){
        getLogsChannels();
        try {
            for(int i = 0; i < guildsIds.size(); i++){
                Guild guild = NanoBot.getJda().getGuildById(guildsIds.get(i));
                if(guild != null){
                    if(!guild.isMember(NanoBot.getJda().getSelfUser())) return;
                    TextChannel channel = guild.getTextChannelById(moderationChannelsIds.get(i));
                    if(channel !=null){
                        if(!channel.canTalk(channel.getGuild().getMember(NanoBot.getJda().getSelfUser()))) return;
                        channel.sendMessage((Message) message).queue();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendAdminLog(MessageEmbed message){
        getLogsChannels();
        try {
            for(int i = 0; i < guildsIds.size(); i++){
                Guild guild = NanoBot.getJda().getGuildById(guildsIds.get(i));
                if(guild != null){
                    if(!guild.isMember(NanoBot.getJda().getSelfUser())) return;
                    TextChannel channel = guild.getTextChannelById(administrationChannelsIds.get(i));
                    if(channel !=null){
                        if(!channel.canTalk(channel.getGuild().getMember(NanoBot.getJda().getSelfUser()))) return;
                        channel.sendMessage((Message) message).queue();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void getLogsChannels() {
        final Connection connection;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM logs");

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                for(int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++){

                    if(!guildsIds.contains(resultSet.getString(1))) guildsIds.add(resultSet.getString(1));
                    if(!moderationChannelsIds.contains(resultSet.getString(2))) moderationChannelsIds.add(resultSet.getString(2));
                    if(!administrationChannelsIds.contains(resultSet.getString(3))) administrationChannelsIds.add(resultSet.getString(3));
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void printDeveloperLog(SlashCommandEvent event){
        String guildName = event.getGuild().getName();
        String guildId = event.getGuild().getId();
        String userName = event.getUser().getName();
        String userId = event.getUser().getId();
        String commandName = event.getName();
        System.out.println(ANSI_RED + "[ADMINCOMMAND] " + ANSI_WHITE + "[" + guildName + " ("+ guildId + ") | " + userName + " (" + userId +  ")] a executé la commande '" + commandName + "' !");
    }

    public static void printLog(SlashCommandEvent event){
        String guildName = event.getGuild().getName();
        String guildId = event.getGuild().getId();
        String userName = event.getUser().getName();
        String userId = event.getUser().getId();
        String commandName = event.getName();
        System.out.println("[" + guildName + " (" + guildId + ") | " + userName + " (" + userId + ")] a executé la commande '" + commandName + "' !");
        addCount();
    }

    public static void addCount(){
        globalCount = globalCount + 1;
    }
}
