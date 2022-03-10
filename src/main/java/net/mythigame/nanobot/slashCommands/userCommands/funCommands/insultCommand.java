package net.mythigame.nanobot.slashCommands.userCommands.funCommands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import static net.mythigame.nanobot.utils.Libs.callComSulteAPI;

@SuppressWarnings("ConstantConditions")
public class insultCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        String insult = callComSulteAPI("insult");
        if(insult == null){
            event.reply("Une erreure est survenue ! Veuillez contacter un administrateur !").queue();
            return;
        }

        OptionMapping option = event.getOption("target");
        if(option != null){
            Member target = option.getAsMember();
            event.reply(target.getAsMention() + " ! " + target.getAsMention() + " te dit : " + insult).queue();
        } else {
            event.reply(insult).queue();
        }
    }
}
