package net.mythigame.nanobot.utils.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.NanoBot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "ConstantConditions"})
public class PlayerManager {

    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;
    public static HashSet<User> skipVotes = new HashSet<>();
    public static HashSet<User> leaveVotes = new HashSet<>();
    public static HashSet<User> pauseVotes = new HashSet<>();
    public static HashSet<User> resumeVotes = new HashSet<>();

    public PlayerManager(){
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public synchronized int getRequiredVotes(GuildVoiceState selfVoiceState) {
        return Math.max(1, (selfVoiceState.getChannel().getMembers().size() -1) / 2);
    }

    public GuildMusicManager getMusicManager(Guild guild){
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
           final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

           guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

           return guildMusicManager;
        });
    }

    public synchronized void unregisterVoteSkip(User user) { skipVotes.remove(user); }

    public synchronized void unregisterVoteLeave(User user) { leaveVotes.remove(user); }

    public synchronized void unregisterVotePause(User user) { pauseVotes.remove(user); }

    public synchronized void unregisterVoteResume(User user) { resumeVotes.remove(user); }

    public synchronized boolean voteSkip(User user) {
        if (skipVotes.contains(user)) {
            return false;
        }
        skipVotes.add(user);
        return true;
    }

    public synchronized boolean voteLeave(User user) {
        if (leaveVotes.contains(user)){
            return false;
        }
        leaveVotes.add(user);
        return true;
    }

    public synchronized boolean votePause(User user) {
        if (pauseVotes.contains(user)){
            return false;
        }
        pauseVotes.add(user);
        return true;
    }

    public synchronized boolean voteResume(User user) {
        if (resumeVotes.contains(user)){
            return false;
        }
        resumeVotes.add(user);
        return true;
    }

    public synchronized int getVoteCount() { return skipVotes.size(); }

    public synchronized int getVoteLeaveCount() { return  leaveVotes.size(); }

    public synchronized int getVotePauseCount() { return pauseVotes.size(); }

    public synchronized int getVoteResumeCount() { return resumeVotes.size(); }

    public void loadAndPlay(final SlashCommandEvent event, String trackUrl){

        GuildMusicManager musicManager = getMusicManager(event.getGuild());

        audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {

                event.reply("Ajout de la piste : `" + track.getInfo().title + "` à la file. " + track.getInfo().uri).queue();

                musicManager.scheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();

                if(trackUrl.contains("youtube.com") && trackUrl.contains("?list")){
                    event.reply("Ajout d'une playlist de : `" + playlist.getTracks().size() + " pistes` aux pistes à jouer. " + trackUrl).queue();

                    for (final AudioTrack track : tracks) {
                        musicManager.scheduler.queue(track);
                    }
                }else{
                    AudioTrack track = playlist.getTracks().get(0);
                    trackLoaded(track);
                }
            }

            @Override
            public void noMatches() {
                event.reply("Aucune piste n'a été trouvé").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                event.reply("Une erreur s'est produite lors du chargement de la piste : " + e.getMessage()).queue();
            }
        });
    }

    public static PlayerManager getInstance(){
        if(INSTANCE == null){
            INSTANCE = new PlayerManager();
        }


        return INSTANCE;
    }

    public static void disconnectAllVocalChannel(){
        NanoBot.getJda().getGuilds().forEach(g -> g.getAudioManager().closeAudioConnection());
    }

}
