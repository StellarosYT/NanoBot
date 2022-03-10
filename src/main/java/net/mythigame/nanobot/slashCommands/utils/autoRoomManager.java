package net.mythigame.nanobot.slashCommands.utils;

import net.dv8tion.jda.api.entities.*;
import net.mythigame.nanobot.Storage.MySQL.MySQLManager;

import java.sql.*;

public class autoRoomManager {

    public static boolean isRoomExist(Guild guild, Category category, AudioChannel channel){
        final Connection connection;
        boolean exist = false;
        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rooms WHERE guildid = ? AND categoryid = ? AND voicechannelid = ?");
            preparedStatement.setString(1, guild.getId());
            preparedStatement.setString(2, category.getId());
            preparedStatement.setString(3, channel.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                exist = true;
            }

            connection.close();
            return exist;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return exist;
    }

    public static boolean isAActiveVoiceRoom(Guild guild, Category category, AudioChannel channel){
        final Connection connection;
        boolean exist = false;
        String id = guild.getId() + ":" + category.getId() + ":" + channel.getId();

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM audiorooms WHERE id = ? AND channelid = ?");
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, channel.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                exist = true;
            }

            connection.close();
            return exist;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return exist;
    }

    public static TextChannel getManageChannelVoiceRoom(Guild guild, Category category, AudioChannel channel){
        final Connection connection;
        TextChannel manageChannel = null;
        String id = guild.getId() + ":" + category.getId() + ":" + channel.getId();

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM audiorooms WHERE id = ? AND channelid = ?");
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, channel.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                manageChannel = guild.getTextChannelById(resultSet.getString("managechannelid"));
            }

            connection.close();
            return manageChannel;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return manageChannel;
    }

    public static boolean isSVAManager(GuildMessageChannel channel, Member member){
        final Connection connection;
        boolean result = false;
        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM audiorooms WHERE ownerid = ? AND managechannelid = ?");
            preparedStatement.setString(1, member.getId());
            preparedStatement.setString(2, channel.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                result = true;
            }

            connection.close();
            return result;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    public static void makeNewSVAManager(GuildMessageChannel channel, Member member){
        final Connection connection;
        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE audiorooms SET ownerid = ? WHERE managechannelid = ?");
            preparedStatement.setString(1, member.getId());
            preparedStatement.setString(2, channel.getId());

            preparedStatement.executeUpdate();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static VoiceChannel getSVAVChannelByManageChannel(GuildMessageChannel channel){
        final Connection connection;
        VoiceChannel voiceChannel = null;
        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM audiorooms WHERE managechannelid = ?");
            preparedStatement.setString(1, channel.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                voiceChannel = channel.getGuild().getVoiceChannelById(resultSet.getString("channelid"));
            }

            connection.close();
            return voiceChannel;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return voiceChannel;
    }

    public static String getVoiceRoomType(Guild guild, Category category, AudioChannel channel){
        final Connection connection;
        String type = null;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rooms WHERE guildid = ? AND categoryid = ? AND voicechannelid = ?");
            preparedStatement.setString(1, guild.getId());
            preparedStatement.setString(2, category.getId());
            preparedStatement.setString(3, channel.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                type = resultSet.getString("type");
            }

            connection.close();
            return type;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return type;
    }

    public static void addActiveVoiceRoom(Guild guild, Category category, AudioChannel channel, GuildMessageChannel manageChannel, Member member){
        final Connection connection;
        String id = guild.getId() + ":" + category.getId() + ":" + channel.getId();
        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO audiorooms (id, channelid, managechannelid, ownerid) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, channel.getId());
            preparedStatement.setString(3, manageChannel.getId());
            preparedStatement.setString(4, member.getId());

            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Une erreur est survenue ! Veuillez contacter un administrateur.");
        }
    }

    public static void removeActiveVoiceRoom(Guild guild, Category category, AudioChannel channel){
        final Connection connection;
        String id = guild.getId() + ":" + category.getId() + ":" + channel.getId();
        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM audiorooms WHERE id = ? AND channelid = ?");
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, channel.getId());

            preparedStatement.executeUpdate();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
