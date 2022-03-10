package net.mythigame.nanobot.slashCommands.developerCommands;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.NanoBot;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import static net.mythigame.nanobot.utils.Libs.isAdmin;

@SuppressWarnings("ConstantConditions")
public class setStatusCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        if(!isAdmin(member.getUser())){
            event.reply("Vous n'avez pas la permission d'utiliser cette commande").queue();
            return;
        }

        String status = event.getOption("status").getAsString();
        switch (status) {
            case "online":
                NanoBot.getJda().getPresence().setStatus(OnlineStatus.ONLINE);
                break;
            case "dnb":
                NanoBot.getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                break;
            case "idle":
                NanoBot.getJda().getPresence().setStatus(OnlineStatus.IDLE);
                break;
            case "offline":
                NanoBot.getJda().getPresence().setStatus(OnlineStatus.INVISIBLE);
                break;
        }
        event.reply("Le status de NanoBot a bien été changé !").queue();
    }
}
