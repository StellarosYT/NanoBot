package net.mythigame.nanobot.slashCommands.utils;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.mythigame.nanobot.NanoBot;
import net.mythigame.nanobot.Storage.MySQL.MySQLManager;

import java.sql.*;

import static net.mythigame.nanobot.NanoBot.update15m;

public class voteGameUserManager {

    public static void addVoteGameUser(String id, String userid, long time){
        final Connection connection;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO votegameguild (id, userid, time) VALUES (?, ?, ?)");
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, userid);
            preparedStatement.setLong(3, time);

            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeVoteGameUser(String id, String userid){
        final Connection connection;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM votegameguild WHERE id = ? AND userid = ?");
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, userid);

            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getVotesNumber(String id){
        final Connection connection;
        int votesNumber = 0;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT Count(*) AS id FROM votegameguild WHERE id = ?");
            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                votesNumber = resultSet.getInt("id");
            }

            connection.close();
            return votesNumber;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return votesNumber;
    }

    public static void dropTooLateVoteGame(long time){
        final Connection connection;
        long timepassed = System.currentTimeMillis()-time;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT userid FROM votegameguild WHERE time <= ?");
            preparedStatement.setLong(1, timepassed);

            String userId;
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                for (int i = 1; i <= resultSet.getRow(); i++) {
                    userId = resultSet.getString(1);
                    dropTooLateVoteGameAction(userId);
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        update15m();
    }

    public static void dropTooLateVoteGameAction(String userId){
        final Connection connection;

        try{
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM votegame");

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                for(int i = 1; i <= resultSet.getRow(); i++){
                    String guildId;
                    String channelId;
                    String messageId;
                    String emoteId;

                    guildId = resultSet.getString(1);
                    channelId = resultSet.getString(2);
                    messageId = resultSet.getString(3);
                    emoteId = resultSet.getString(4);
                    removeReaction(guildId, channelId, messageId, emoteId, userId);
                }
            }

            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void clearVoteGameUser(String id){
        final Connection connection;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM votegameguild WHERE id = ?");
            preparedStatement.setString(1, id);

            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearAllVoteGameUser(){
        final Connection connection;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("TRUNCATE TABLE votegameguild");

            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isVoteGameExist(String guildId, String channelId, String messageId, String emoteId){
        final Connection connection;
        boolean ExistNumber = false;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT Count(*) AS emote FROM votegame WHERE guildid = ? AND channelid = ? AND messageid = ? AND emote = ?"
            );
            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, channelId);
            preparedStatement.setString(3, messageId);
            preparedStatement.setString(4, emoteId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                ExistNumber = true;
            }

            connection.close();
            return ExistNumber;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ExistNumber;
    }

    public static boolean isVoteGame(String guildId, String channelId, String messageId){
        final Connection connection;
        boolean result = false;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM votegame WHERE guildid = ? AND channelid = ? AND messageid = ?");
            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, channelId);
            preparedStatement.setString(3, messageId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                result = true;
            }

            connection.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void deleteVoteGame(String guildId, String channelId, String messageId){
        final Connection connection;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM votegame WHERE guildid = ? AND channelid = ? AND messageid = ?");
            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, channelId);
            preparedStatement.setString(3, messageId);

            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void addReaction(String guild, String channel, String messageId, MessageReaction.ReactionEmote emote){
        if(emote.isEmoji()){
            String finalEmote;
            finalEmote = emote.getEmoji();
            NanoBot.getJda().getGuildById(guild).getTextChannelById(channel).retrieveMessageById(messageId).queue(e -> e.addReaction(finalEmote).queue());
        }else if(emote.isEmote()){
            Emote finalEmote;
            finalEmote = NanoBot.getJda().getEmoteById(emote.getId());
            NanoBot.getJda().getGuildById(guild).getTextChannelById(channel).retrieveMessageById(messageId).queue(e -> e.addReaction(finalEmote).queue());
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void clearReaction(String guild, String channel, String messageId, MessageReaction.ReactionEmote emote) {
        if (emote.isEmoji()) {
            String finalEmote;
            finalEmote = emote.getEmoji();
            NanoBot.getJda().getGuildById(guild).getTextChannelById(channel).retrieveMessageById(messageId).queue(e -> e.clearReactions(finalEmote).queue());
        } else if (emote.isEmote()) {
            Emote finalEmote;
            finalEmote = NanoBot.getJda().getEmoteById(emote.getId());
            NanoBot.getJda().getGuildById(guild).getTextChannelById(channel).retrieveMessageById(messageId).queue(e -> e.clearReactions(finalEmote).queue());
        }
    }
    @SuppressWarnings({"ConstantConditions"})
    public static void removeReaction(String guild, String channel, String messageId, String emote, String user) {
        if(EmojiManager.isEmoji(EmojiParser.parseToUnicode(emote))){
            String finalEmote = EmojiParser.parseToUnicode(emote);
            NanoBot.getJda().getGuildById(guild).getTextChannelById(channel).retrieveMessageById(messageId).queue(e -> e.removeReaction(finalEmote, User.fromId(user)).queue());
        }else{
            Emote finalEmote = NanoBot.getJda().getEmoteById(emote);
            NanoBot.getJda().getGuildById(guild).getTextChannelById(channel).retrieveMessageById(messageId).queue(e -> e.removeReaction(finalEmote, User.fromId(user)).queue());
        }
    }

}
