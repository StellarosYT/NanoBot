package net.mythigame.nanobot.slashCommands.moderationCommands;

import com.mysql.cj.util.StringUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import static net.mythigame.nanobot.utils.Libs.isGuildStaff;

@SuppressWarnings("ConstantConditions")
public class unbanCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member moderator, TextChannel channel) {
        String unbanned = event.getOption("id").getAsString();
        if (!isGuildStaff(moderator)) {
            event.reply("Vous n'avez pas la permission de dé-bannir des personnes bannies !").queue();
            return;
        }

        if (StringUtils.isStrictlyNumeric(unbanned)) {
            unbanMethod(event, unbanned, event.getGuild());
        }else{
            event.reply("Veuillez spécifier un utilisateur correct.").queue();
        }
    }

    private void unbanMethod(SlashCommandEvent event, String unbanned, Guild guild){

        if(!isGuildStaff(guild.getSelfMember())){
            event.reply("Veuillez contacter un administrateur du serveur ! (Problème de permission : UNBAN_PERMISSION)").queue();
            return;
        }

        try {
            guild.getManager().getGuild().unban(unbanned).queue();
            event.reply("<@!"+unbanned +"> a bien été dé-banni du serveur !").queue();
        }catch (Exception e){
            event.reply("Une erreur est survenue ! Veuillez contacter un administrateur.").queue();
            e.printStackTrace();
        }
    }
}
