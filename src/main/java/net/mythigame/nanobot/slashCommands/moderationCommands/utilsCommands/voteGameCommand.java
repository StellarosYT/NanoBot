package net.mythigame.nanobot.slashCommands.moderationCommands.utilsCommands;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.NanoBot;
import net.mythigame.nanobot.Storage.MySQL.MySQLManager;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static net.mythigame.nanobot.slashCommands.utils.voteGameUserManager.isVoteGameExist;
import static net.mythigame.nanobot.utils.Libs.isGuildStaff;

public class voteGameCommand implements SlashCommand {
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
                int requiredNumber = (int) event.getOption("requirednumber").getAsLong();
                Role role = event.getOption("role").getAsRole();
                String announce = event.getOption("announce").getAsString();

                final String originEmoji = emoji;
                String emojistr = emoji.substring(emoji.lastIndexOf(":")+1).replace(">", "");

                boolean isEmote = false;

                if(EmojiManager.containsEmoji(emoji)){
                    emoji = EmojiParser.parseToAliases(emoji);
                    isEmote = false;
                }
                else if (NanoBot.getJda().getEmoteById(emojistr).getId() != null && NanoBot.getJda().getEmoteById(emojistr).isAvailable()){
                    emoji = NanoBot.getJda().getEmoteById(emojistr).getId();
                    isEmote = true;
                }
                else{
                    event.reply("Je ne peux pas accéder à cet emoji...").queue();
                }

                if(isVoteGameExist(event.getGuild().getId(), targetChannel.getId(), message, emoji)){
                    event.reply("Ce VoteGame existe déjà !").queue();
                    return;
                }

                final Connection connection;
                try {
                    connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO votegame (guildid, channelid, messageid, emote, reactnumber, roleid, annoncemessage) VALUES (?, ?, ?, ?, ?, ?, ?)");
                    preparedStatement.setString(1, event.getGuild().getId());
                    preparedStatement.setString(2, targetChannel.getId());
                    preparedStatement.setString(3, message);
                    preparedStatement.setString(4, emoji);
                    preparedStatement.setInt(5, requiredNumber);
                    preparedStatement.setString(6, role.getId());
                    preparedStatement.setString(7, announce);

                    preparedStatement.executeUpdate();
                    connection.close();

                    if(isEmote){
                        String finalEmoji = emoji;
                        NanoBot.getJda().getGuildById(event.getGuild().getId()).getTextChannelById(targetChannel.getId()).retrieveMessageById(message).queue(e -> e.addReaction(NanoBot.getJda().getEmoteById(finalEmoji)).queue());
                    }else{
                        NanoBot.getJda().getGuildById(event.getGuild().getId()).getTextChannelById(targetChannel.getId()).retrieveMessageById(message).queue(e -> e.addReaction(originEmoji).queue());
                    }
                } catch (SQLException e) {
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
