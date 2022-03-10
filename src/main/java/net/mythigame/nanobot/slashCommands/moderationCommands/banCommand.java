package net.mythigame.nanobot.slashCommands.moderationCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.mythigame.nanobot.slashCommands.SlashCommand;
import net.mythigame.nanobot.utils.Libs;

import static net.mythigame.nanobot.utils.Libs.isGuildStaff;

@SuppressWarnings("ConstantConditions")
public class banCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member moderator, TextChannel channel) {
        Member banned = event.getOption("banned").getAsMember();

        if (!isGuildStaff(moderator)) {
            event.reply("Vous n'avez pas la permission de bannir un membre !").queue();
            return;
        }

        String reason;
        OptionMapping option = event.getOption("reason");
        if(option != null){
            reason = option.getAsString();
        }else {
            reason = "Aucun motif spécifié";
        }

        banMethod(event, moderator, banned, event.getGuild(), reason);
    }

    private void banMethod(SlashCommandEvent event, Member moderator, Member banned, Guild guild, String reason){

        if(!isGuildStaff(guild.getSelfMember())){
            event.reply("Veuillez contacter un administrateur du serveur ! (Problème de permission : BAN_PERMISSION)").queue();
            return;
        }

        if(moderator == banned){
            event.reply("Vous ne pouvez pas vous sanctionner vous même !").queue();
            return;
        }

        if(!moderator.isOwner()){
            if(isGuildStaff(banned)){
                event.reply("Vous ne pouvez pas appliquer des sanctions à cette personne.").queue();
                return;
            }
        }

        try {

            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("Modération", "https://discord.com/invite/Txe9z6T").setColor(0xa30000);
            eb.setAuthor("Vous avez été banni du discord  '"+ guild.getName()+"'");
            eb.addField("Par:", ""+moderator.getUser().getName(), true);
            eb.addField("Date:", Libs.Date(), true);
            eb.addField("Motif:", reason, false);
            eb.setFooter("NanoBot - Modération", "https://mythigame.net/data/assets/logo/nbicon.png");

            banned.getUser().openPrivateChannel().queue(qued -> qued.sendMessage((Message) eb.build()).queue());

            guild.getManager().getGuild().ban(banned, 0, reason).queue();
            event.reply(banned.getAsMention() + " a bien été banni du serveur !").queue();
        }catch (Exception e){
            event.reply("Une erreur est survenue ! Veuillez contacter un administrateur.").queue();
            e.printStackTrace();
        }
    }
}
