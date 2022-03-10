package net.mythigame.nanobot.slashCommands.userCommands.musicCommands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.mythigame.nanobot.utils.lavaplayer.GuildMusicManager;
import net.mythigame.nanobot.utils.lavaplayer.PlayerManager;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import static net.mythigame.nanobot.utils.Libs.isDJ;

@SuppressWarnings("ConstantConditions")
public class leaveCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        Guild guild = event.getGuild();
        Member self = guild.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        GuildVoiceState memberVoiceState = member.getVoiceState();

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);

        if(!selfVoiceState.inAudioChannel()){
            event.reply("Je ne suis actuellement dans aucun salon.").queue();
            return;
        }

        if(!memberVoiceState.inAudioChannel()){
            event.reply("Vous devez être dans un salon vocal pour me faire fonctionner.").queue();
            return;
        }

        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            event.reply("Nous devons être dans le même salon pour que puissiez faire ceci.").queue();
            return;
        }
        PlayerManager player = PlayerManager.getInstance();
        final AudioManager audioManager = guild.getAudioManager();

        if(isDJ(guild, member)){
            musicManager.scheduler.queue.clear();
            musicManager.scheduler.repeating = false;
            musicManager.audioPlayer.stopTrack();
            musicManager.audioPlayer.setVolume(100);
            musicManager.audioPlayer.destroy();
            audioManager.closeAudioConnection();
            event.reply("J'ai bien quitté le salon vocal.").queue();
            return;
        }

        boolean voteRegistered = player.voteLeave(member.getUser());
        if(player.getVoteLeaveCount() >= player.getRequiredVotes(selfVoiceState)){
            musicManager.scheduler.queue.clear();
            musicManager.scheduler.repeating = false;
            musicManager.audioPlayer.stopTrack();
            musicManager.audioPlayer.setVolume(100);
            musicManager.audioPlayer.destroy();
            audioManager.closeAudioConnection();
            event.reply("Je quitte le salon !").queue();
        }else if (voteRegistered){
            event.reply("Votre vote a bien été enregistré !\nVotez pour me faire quitter le salon : "+ player.getVoteLeaveCount() + "/" + player.getRequiredVotes(selfVoiceState)).queue();
        }
    }
}
