package net.mythigame.nanobot.slashCommands.moderationCommands.utilsCommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.Storage.MySQL.MySQLManager;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static net.mythigame.nanobot.slashCommands.utils.autoRoomManager.isRoomExist;
import static net.mythigame.nanobot.utils.Libs.isGuildStaff;

public class autoRoomCommand implements SlashCommand {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        if (!isGuildStaff(member)) {
            event.reply("Vous n'avez pas la permission d'utiliser cette commande !").queue();
        }

        String subcommand = event.getSubcommandName();
        switch (subcommand) {
            case "add":
                String type = event.getOption("type").getAsString();
                VoiceChannel targetChannel = event.getGuild().getVoiceChannelById(event.getOption("channelid").getAsString());
                Category categoryChannel = event.getGuild().getCategoryById(event.getOption("categoryid").getAsString());

                if(categoryChannel == null){
                    categoryChannel = targetChannel.getParentCategory();
                }

                if(categoryChannel != null){
                    if(!event.getGuild().getSelfMember().hasPermission(categoryChannel, Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS)){
                        event.reply("Je n'ai pas les permissions adéquates.").queue();
                        return;
                    }
                }
                else{
                    if(!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS)){
                        event.reply("Je n'ai pas les permissions adéquates.").queue();
                        return;
                    }
                }

                if(isRoomExist(event.getGuild(), categoryChannel, targetChannel)){
                    event.reply("Cette VoiceRoom existe déjà !").queue();
                    return;
                }

                final Connection connection;
                try{
                    connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO rooms (guildid, categoryid, voicechannelid, type) VALUES (?, ?, ?, ?)");
                    preparedStatement.setString(1, event.getGuild().getId());
                    preparedStatement.setString(2, categoryChannel.getId());
                    preparedStatement.setString(3, targetChannel.getId());
                    preparedStatement.setString(4, type);

                    preparedStatement.executeUpdate();
                    connection.close();
                    event.reply("La VoiceRoom a bien été créé").queue();
                }catch (SQLException e){
                    e.printStackTrace();
                    event.reply("Une erreur est survenue ! Veuillez contacter un administrateur.").queue();
                }

            case "remove":
                //@TODO
                event.reply("En cours de développement...").queue();
            case "list":
                event.reply("En cours de développement...").queue();
                //@TODO
        }
    }
}
