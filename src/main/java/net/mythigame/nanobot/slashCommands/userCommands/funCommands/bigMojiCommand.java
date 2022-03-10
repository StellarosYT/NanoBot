package net.mythigame.nanobot.slashCommands.userCommands.funCommands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.NanoBot;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ConstantConditions")
public class bigMojiCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        String emoteString = event.getOption("emoji").getAsString();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(emoteString);
        while (matcher.find())
        event.reply(NanoBot.getJda().getEmoteById(matcher.group()).getImageUrl()).queue();
    }
}
