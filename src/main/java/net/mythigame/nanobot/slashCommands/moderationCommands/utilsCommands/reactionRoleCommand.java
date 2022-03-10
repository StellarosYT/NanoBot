package net.mythigame.nanobot.slashCommands.moderationCommands.utilsCommands;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.NanoBot;
import net.mythigame.nanobot.Storage.MySQL.MySQLManager;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static net.mythigame.nanobot.slashCommands.utils.reactionRoleManager.isReactionRoleExist;
import static net.mythigame.nanobot.utils.Libs.isGuildStaff;

public class reactionRoleCommand implements SlashCommand {
        @SuppressWarnings("ConstantConditions")
        @Override
        public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
                if (!isGuildStaff(member)) {
                        event.reply("Vous n'avez pas la permission d'utiliser cette commande !").queue();
                }

                String subcommand = event.getSubcommandName();
                switch (subcommand) {
                        case "add":
                                GuildChannel targetChannel = event.getOption("channel").getAsGuildChannel();
                                String message = event.getOption("messageid").getAsString();
                                String emoji = event.getOption("emoji").getAsString();
                                String role = event.getOption("role").getAsRole().getId();

                                final String originEmoji = emoji;
                                String emojistr = emoji;

                                if(emoji.contains(":") && emoji.contains(">")){
                                        emojistr = emoji.substring(emoji.lastIndexOf(":")+1).replace(">", "");
                                }

                                boolean isEmote = false;

                                if(EmojiManager.containsEmoji(emoji)){
                                        System.out.println("1");
                                        emoji = EmojiParser.parseToAliases(emoji);
                                        isEmote = false;
                                }
                                else if (NanoBot.getJda().getEmoteById(emojistr).getId() != null && NanoBot.getJda().getEmoteById(emojistr).isAvailable()){
                                        System.out.println("2");
                                        emoji = NanoBot.getJda().getEmoteById(emojistr).getId();
                                        isEmote = true;
                                }
                                else{
                                        event.reply("Je ne peux pas accéder à cet emoji...").queue();
                                }

                                if (isReactionRoleExist(event.getGuild().getId(), targetChannel.getId(), message, emoji)) {
                                        event.reply("Ce ReactionRole existe déjà !").queue();
                                        return;
                                }

                                final Connection connection;
                                try{
                                        connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
                                        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO reactionrole (guildid, channelid, messageid, emote, roleid) VALUES (?, ?, ?, ?, ?)");
                                        preparedStatement.setString(1, event.getGuild().getId());
                                        preparedStatement.setString(2, targetChannel.getId());
                                        preparedStatement.setString(3, message);
                                        preparedStatement.setString(4, emoji);
                                        preparedStatement.setString(5, role);

                                        preparedStatement.executeUpdate();
                                        connection.close();
                                        event.reply("Le ReactionRole a bien été créé").queue();

                                        if(isEmote){
                                                String finalEmoji = emoji;
                                                NanoBot.getJda().getGuildById(event.getGuild().getId()).getTextChannelById(targetChannel.getId()).retrieveMessageById(message).queue(e -> e.addReaction(NanoBot.getJda().getEmoteById(finalEmoji)).queue());
                                        }else{
                                                NanoBot.getJda().getGuildById(event.getGuild().getId()).getTextChannelById(targetChannel.getId()).retrieveMessageById(message).queue(e -> e.addReaction(originEmoji).queue());
                                        }
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