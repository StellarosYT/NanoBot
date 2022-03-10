package net.mythigame.nanobot.slashCommands.userCommands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.slashCommands.SlashCommand;

public class pingCommand implements SlashCommand {

    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        long time = System.currentTimeMillis();
        event.reply("Pong!").queue(response -> response.editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time).queue());
    }
}
