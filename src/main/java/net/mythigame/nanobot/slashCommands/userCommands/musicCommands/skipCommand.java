package net.mythigame.nanobot.slashCommands.userCommands.musicCommands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
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
public class skipCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        Guild guild = event.getGuild();
        Member self = guild.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        GuildVoiceState memberVoiceState = member.getVoiceState();

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        if(!memberVoiceState.inAudioChannel()){
            event.reply("Vous devez être dans un salon vocal pour me faire fonctionner.").queue();
            return;
        }

        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            event.reply("Nous devons être dans le même salon pour que vous puissiez faire ceci.").queue();
            return;
        }

        if(audioPlayer.getPlayingTrack() == null){
            event.reply("Je ne joue aucun titre pour le moment...").queue();
            return;
        }

        if(musicManager.scheduler.queue.isEmpty()){
            event.reply("Je n'ai aucun titre à jouer ensuite...").queue();
            return;
        }

        if(isDJ(guild, member)){
            musicManager.scheduler.nextTrack();
            event.reply("Je passe à la piste suivante !").queue();
            return;
        }

        PlayerManager player = PlayerManager.getInstance();

        if(player.getMusicManager(guild).scheduler.queue.size() < 1){
            event.reply("Désolé mais aucune piste n'est en attente !").queue();
            return;
        }

        boolean voteRegistered = player.voteSkip(member.getUser());
        if(player.getVoteCount() >= player.getRequiredVotes(selfVoiceState)){
            musicManager.scheduler.nextTrack();
            event.reply("Je passe à la piste suivante !").queue();
            return;
        }else if (voteRegistered){
            event.reply("Votre vote a bien été enregistré !\nVotez pour passer à la prochaine piste : "+ player.getVoteCount() + "/" + player.getRequiredVotes(selfVoiceState)).queue();
            return;
        }

        event.reply("Une erreur s'est produite.").queue();
    }
}
