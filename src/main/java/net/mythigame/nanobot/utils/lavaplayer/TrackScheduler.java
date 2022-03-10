package net.mythigame.nanobot.utils.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static net.mythigame.nanobot.utils.lavaplayer.PlayerManager.*;

public class TrackScheduler extends AudioEventAdapter {

    public final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;
    public boolean repeating = false;

    public TrackScheduler(AudioPlayer player){
        this.player = player;
        this.queue = new LinkedBlockingDeque<>();
    }

    public void queue(AudioTrack track){
        if(!player.startTrack(track, true)){
            queue.offer(track);
        }
    }

    public void nextTrack() {
        skipVotes.clear();
        leaveVotes.clear();
        pauseVotes.clear();
        resumeVotes.clear();
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason.mayStartNext){
            if(this.repeating){
                this.player.startTrack(track.makeClone(), false);
                return;
            }

            nextTrack();
        }
    }

}
