package net.mythigame.nanobot.slashCommands.developerCommands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.Storage.MySQL.MySQLManager;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static net.mythigame.nanobot.utils.Libs.isAdmin;

@SuppressWarnings("ConstantConditions")
public class blacklistCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        if(!isAdmin(member.getUser())){
            event.reply("Vous n'avez pas la permission d'utiliser cette commande").queue();
            return;
        }
        String subcommand = event.getSubcommandName();
        switch (subcommand) {
            case "add" -> {
                Member target = event.getOption("target").getAsMember();
                if (target.getUser().isBot()) {
                    event.reply("Vous ne pouvez pas définir un robot en tant que développeur !").queue();
                    return;
                }

                final Connection connection;
                try {
                    connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO blacklist (userid, banned) VALUES (?, '1') ON DUPLICATE KEY UPDATE userid = ?");
                    preparedStatement.setString(1, target.getId());
                    preparedStatement.setString(2, target.getId());

                    preparedStatement.executeUpdate();
                    connection.close();
                    event.reply(target.getAsMention() + " a bien été ajouté à la blacklist de NanoBot.").queue();
                } catch (SQLException e) {
                    e.printStackTrace();
                    event.reply("Une erreur est survenue ! Veuillez contacter un développeur.").queue();
                }
            }
            case "remove" -> {
                Member target2 = event.getOption("target").getAsMember();
                if (target2.getUser().isBot()) {
                    event.reply("Vous ne pouvez pas définir un robot en tant que développeur !").queue();
                    return;
                }

                final Connection connection;
                try {
                    connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM blacklist WHERE userid = ?");
                    preparedStatement.setString(1, target2.getId());

                    preparedStatement.executeUpdate();
                    connection.close();
                    event.reply(target2.getAsMention() + " a bien été retiré de la blacklist de NanoBot.").queue();
                } catch (SQLException e) {
                    e.printStackTrace();
                    event.reply("Une erreur est survenue ! Veuillez contacter un développeur.").queue();
                }
            }
            case "list" -> event.reply("En cours de développement...").queue();
        }
    }
}
