package net.mythigame.nanobot.consoleCommands;

import net.dv8tion.jda.api.OnlineStatus;
import net.mythigame.nanobot.NanoBot;
import net.mythigame.nanobot.Storage.MySQL.MySQLManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static net.mythigame.nanobot.NanoBot.*;

@SuppressWarnings("ConstantConditions")
public class consoleCommands {

    @Command(name = "setstatus", type = Command.ExecutorType.CONSOLE, description = "Permet de définir le status du robot à partir de la console.")
    private void setStatusConsole(String[] args){
        if(args[0].equalsIgnoreCase("ONLINE")){
            NanoBot.getJda().getPresence().setStatus(OnlineStatus.ONLINE);
            System.out.println("[CONSOLE] NanoBot est désormais affiché : EN LIGNE");
        }else if(args[0].equalsIgnoreCase("DND")){
            NanoBot.getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
            System.out.println("[CONSOLE] NanoBot est désormais affiché : NE PAS DERANGER");
        }else if(args[0].equalsIgnoreCase("IDLE")){
            NanoBot.getJda().getPresence().setStatus(OnlineStatus.IDLE);
            System.out.println("[CONSOLE] NanoBot est désormais affiché : ABSENT");
        }else if(args[0].equalsIgnoreCase("OFFLINE")){
            NanoBot.getJda().getPresence().setStatus(OnlineStatus.INVISIBLE);
            System.out.println("[CONSOLE] NanoBot est désormais affiché : HORS LIGNE");
        }else System.out.println("Veuillez préciser : online / dnd / idle / offline.");
    }

    @Command(name = "stop", type = Command.ExecutorType.CONSOLE, description = "Permet d'arrêter le robot à partir de la console.")
    private void quit(){
        System.out.println("[COMMAND-CONSOLE] NanoBot est en cours d'arrêt.");
        NanoBot.stop();
    }

    @Command(name = "sendmessage", type = Command.ExecutorType.CONSOLE, description = "Vous permet d'envoyer un message avec le bot")
    private void sendMessage(String[] args){
        final List<String> messageContent = Arrays.asList(args);

        if(args.length < 3){
            System.out.println("[CONSOLE] Veuillez précisez un guildId, channelId ainsi qu'un message");
            return;
        }

        String guildid = args[0];
        String channelId = args[1];
        String toSendMessage = String.join(" ", messageContent.subList(2, messageContent.size()));

        NanoBot.getJda().getGuildById(guildid).getTextChannelById(channelId).sendMessage(toSendMessage).queue(e ->
                System.out.println("["+ e.getGuild().getName() + " (" + e.getGuild().getId() + ") | NanoBot )] a envoyé le message '" + toSendMessage + "' !")
        );

    }

    @Command(name = "restart", type = Command.ExecutorType.CONSOLE, description = "Permet de redémarrer le robot à partir de la console.")
    private void reload(){
        System.out.println("[COMMAND-CONSOLE] NanoBot est en cours de redémarrage...");
        NanoBot.restart();
    }

    @Command(name = "addadmin", type = Command.ExecutorType.CONSOLE, description = "Permet d'ajouter un utilisateur aux administrateurs du robot à partir de la console.")
    private void addAdminConsole(String[] args){

        if(args.length == 0){
            System.out.println("[CONSOLE] Veuillez préciser un userID !");
        }

        String userId = args[0];

        final Connection connection;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement( "INSERT INTO adminlist (userid, administrator) VALUES (?, '1') ON DUPLICATE KEY UPDATE userid = ? ");
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, userId);

            preparedStatement.executeUpdate();
            connection.close();
            System.out.println(ANSI_RED + "[ADMINISTRATION] Un nouvel administrateur vient d'être ajouté (" + userId + ")" + ANSI_RESET);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Une erreur est survenue ! Veuillez contacter un administrateur.");
        }
    }

    @Command(name = "removeadmin", type = Command.ExecutorType.CONSOLE, description = "Permet de supprimer un administrateur du robot à partir de la console.")
    private void removeAdminConsole(String[] args){

        if(args.length == 0){
            System.out.println("[CONSOLE] Veuillez préciser un userID !");
        }

        String userId = args[0];

        final Connection connection;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM adminlist WHERE userid = ?");
            preparedStatement.setString(1, userId);

            preparedStatement.executeUpdate();
            connection.close();
            System.out.println(ANSI_RED + "[ADMINISTRATION] Un administrateur vient d'être retiré (" + userId + ")" + ANSI_RESET);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Une erreur est survenue ! Veuillez contacter un administrateur.");
        }
    }

    @Command(name = "addblacklist", type = Command.ExecutorType.CONSOLE, description = "Permet d'ajouter un utilisateur à la blacklist à partir de la console.")
    private void addBlacklistConsole(String[] args){

        if(args.length == 0){
            System.out.println("[CONSOLE] Veuillez préciser un userID !");
        }

        String userId = args[0];

        final Connection connection;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO blacklist (userid, banned) VALUES (?, '1') ON DUPLICATE KEY UPDATE userid = ? ");
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, userId);

            preparedStatement.executeUpdate();
            connection.close();
            System.out.println(ANSI_GREEN_BACKGROUND + "[MODÉRATION] Un utilisateur vient d'être ajouté blacklist (" + userId + ")" + ANSI_RESET);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Une erreur est survenue ! Veuillez contacter un administrateur.");
        }
    }

    @Command(name = "removeblacklist", type = Command.ExecutorType.CONSOLE, description = "Permet de supprimer un utilisateur de la blacklist à partir de la console.")
    private void removeBlacklistConsole(String[] args){

        if(args.length == 0){
            System.out.println("[CONSOLE] Veuillez préciser un userID !");
        }

        String userId = args[0];

        final Connection connection;

        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM blacklist WHERE userid = ?");
            preparedStatement.setString(1, userId);

            preparedStatement.executeUpdate();
            connection.close();
            System.out.println(ANSI_GREEN_BACKGROUND + "[MODÉRATION] Un utilisateur vient d'être retiré de la blacklist (" + userId + ")" + ANSI_RESET);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Une erreur est survenue ! Veuillez contacter un administrateur.");
        }
    }

}
