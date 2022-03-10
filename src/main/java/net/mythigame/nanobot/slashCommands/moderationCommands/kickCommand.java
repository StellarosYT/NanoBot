package net.mythigame.nanobot.slashCommands.moderationCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.mythigame.nanobot.slashCommands.SlashCommand;
import net.mythigame.nanobot.utils.Libs;

import static net.mythigame.nanobot.utils.Libs.isGuildStaff;

@SuppressWarnings("ConstantConditions")
public class kickCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member moderator, TextChannel channel) {
        Member kicked = event.getOption("kicked").getAsMember();
        OptionMapping option = event.getOption("reason");
        String reason;


        if (!isGuildStaff(moderator)) {
            event.reply("Vous n'avez pas la permission d'exclure un membre !").queue();
            return;
        }

        if (option != null) {
            reason = option.getAsString();
        }else {
            reason = "Aucun motif spécifié";
        }

        kickMethod(event, moderator, kicked, event.getGuild(), reason);
    }

    private void kickMethod(SlashCommandEvent event, Member moderator, Member kicked, Guild guild, String reason){

        if(!isGuildStaff(guild.getSelfMember())){
            event.reply("Veuillez contacter un administrateur du serveur ! (Problème de permission : KICK_MEMBERS)").queue();
            return;
        }

        if(moderator == kicked){
            event.reply("Vous ne pouvez pas vous sanctionner vous même !").queue();
            return;
        }

        if(isGuildStaff(kicked)){
            event.reply("Vous ne pouvez pas appliquer des sanctions à cette personne.").queue();
            return;
        }

        try {

            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("Modération", "https://discord.com/invite/Txe9z6T").setColor(0xa30000);
            eb.setAuthor("Vous avez été expulsé du discord  '"+ guild.getName()+"'");
            eb.addField("Par:", ""+moderator.getUser().getName(), true);
            eb.addField("Date:", Libs.Date(), true);
            eb.addField("Motif:", reason, false);
            eb.setFooter("NanoBot - Modération", "http://mythigame.net/data/assets/logo/nbicon.png");

            kicked.getUser().openPrivateChannel().queue(qued -> qued.sendMessage((Message) eb.build()).queue());

            guild.getManager().getGuild().kick(kicked).reason(reason).queue();
            event.reply(kicked.getAsMention() + " a bien été expulsé du serveur !").queue();
        }catch (Exception e){
            event.reply("Une erreur est survenue ! Veuillez contacter un administrateur.").queue();
            e.printStackTrace();
        }
    }
}
