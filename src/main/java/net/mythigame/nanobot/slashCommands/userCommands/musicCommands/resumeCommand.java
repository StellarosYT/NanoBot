package net.mythigame.nanobot.slashCommands.userCommands.musicCommands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.utils.lavaplayer.GuildMusicManager;
import net.mythigame.nanobot.utils.lavaplayer.PlayerManager;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import static net.mythigame.nanobot.utils.Libs.isDJ;

@SuppressWarnings("ConstantConditions")
public class resumeCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        Guild guild = event.getGuild();
        Member self = guild.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inAudioChannel()){
            event.reply("Vous devez être dans un salon vocal pour me faire fonctionner").queue();
            return;
        }

        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            event.reply("Je ne joue aucun titre pour le moment...").queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);

        if(!musicManager.scheduler.player.isPaused()){
            event.reply("Je n'ai aucune piste en pause.").queue();
            return;
        }

        if(isDJ(guild, member)){
            musicManager.scheduler.player.setPaused(false);
            event.reply("La piste en cours reprend.").queue();
            return;
        }

        PlayerManager player = PlayerManager.getInstance();

        boolean voteRegistered = player.voteResume(member.getUser());
        if(player.getVoteResumeCount() >= player.getRequiredVotes(selfVoiceState)){
            musicManager.scheduler.player.setPaused(false);
            event.reply("La piste en cours reprend.").queue();
        }else if (voteRegistered){
            event.reply("Votre vote a bien été enregistré !\nVotez pour reprendre la piste mise en pause : "+ player.getVoteResumeCount() + "/" + player.getRequiredVotes(selfVoiceState)).queue();
        }
    }
}
