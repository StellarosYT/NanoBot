package net.mythigame.nanobot.slashCommands.userCommands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.mythigame.nanobot.slashCommands.SlashCommand;

public class helpCommand implements SlashCommand {

    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        event.reply("Cliquez sur le bouton !").addActionRow(Button.link("https://www.mythigame.net/", "Obtenir de l'aide")).queue();
    }
}
