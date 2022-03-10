package net.mythigame.nanobot.slashCommands.developerCommands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.NanoBot;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import static net.mythigame.nanobot.utils.Libs.isAdmin;

public class restartCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        if(!isAdmin(member.getUser())){
            event.reply("Vous n'avez pas la permission d'utiliser cette commande").queue();
            return;
        }

        event.reply("NanoBot redémarre !").queue();
        NanoBot.restart();
    }
}
