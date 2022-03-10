package net.mythigame.nanobot.slashCommands.userCommands.funCommands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import static net.mythigame.nanobot.NanoBot.CONFIGURATION;

@SuppressWarnings("ConstantConditions")
public class bangCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        int randomGif = 1 + (int)(Math.random() * ((50 - 1) + 1));

        OptionMapping option = event.getOption("target");
        if(option != null){
            Member target = option.getAsMember();
            event.reply(member.getAsMention() + " vient de tirer sur " + target.getAsMention()).queue();
        }
        event.reply("Voici :").queue(e -> e.deleteOriginal().queue());
        channel.sendMessage(CONFIGURATION.getString("bangGifAPI", "Lien du répertoire de gifs bang") + randomGif + ".gif").queue();
    }
}
