package net.mythigame.nanobot.slashCommands.utils;

import net.mythigame.nanobot.Storage.MySQL.MySQLManager;

import java.sql.*;

public class reactionRoleManager {

    public static boolean isReactionRoleExist(String guildId, String channelId, String messageId, String emoteId){
        final Connection connection;
        boolean result = false;
        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM reactionrole WHERE guildid = ? AND channelid = ? AND messageid = ? AND emote = ?");
            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, channelId);
            preparedStatement.setString(3, messageId);
            preparedStatement.setString(4, emoteId);

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

    public static boolean isReactionRole(String guildId, String channelId, String messageId){
        final Connection connection;
        boolean result = false;
        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM reactionrole WHERE guildid = ? AND channelid = ? AND messageid = ?");
            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, channelId);
            preparedStatement.setString(3, messageId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                result = true;
            }

            connection.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void deleteReactionRole(String guildId, String channelId, String messageId){
        final Connection connection;
        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM reactionrole WHERE guildid = ? AND channelid = ? AND messageid = ?"
            );
            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, channelId);
            preparedStatement.setString(3, messageId);

            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
