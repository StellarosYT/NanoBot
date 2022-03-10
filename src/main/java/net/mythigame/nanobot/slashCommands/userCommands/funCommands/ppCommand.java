package net.mythigame.nanobot.slashCommands.userCommands.funCommands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.NanoBot;
import net.mythigame.nanobot.slashCommands.SlashCommand;

@SuppressWarnings("ConstantConditions")
public class ppCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        String id = event.getOption("id").getAsString();
        NanoBot.getJda().retrieveUserById(id).queue(e -> event.reply(e.getAvatarUrl()).queue());
    }
}
