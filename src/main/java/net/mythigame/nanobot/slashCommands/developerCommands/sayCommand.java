package net.mythigame.nanobot.slashCommands.developerCommands;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import static net.mythigame.nanobot.utils.Libs.isAdmin;

@SuppressWarnings("ConstantConditions")
public class sayCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        if(!isAdmin(member.getUser())){
            event.reply("Vous n'avez pas la permission d'utiliser cette commande").queue();
            return;
        }

        String message = event.getOption("message").getAsString();
        OptionMapping option = event.getOption("channel");
        if(option != null){
            GuildChannel toChannel = option.getAsGuildChannel();
            event.getGuild().getTextChannelById(toChannel.getId()).sendMessage(message).queue();
        }else{
            event.getGuild().getTextChannelById(channel.getId()).sendMessage(message).queue();
        }
        event.reply("Voici :").queue(e -> e.deleteOriginal().queue());
    }
}
