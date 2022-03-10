package net.mythigame.nanobot.slashCommands.userCommands.musicCommands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.utils.lavaplayer.PlayerManager;
import net.mythigame.nanobot.slashCommands.SlashCommand;

@SuppressWarnings("ConstantConditions")
public class currentPlayingCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer;

        if(audioPlayer == null || audioPlayer.getPlayingTrack() == null){
            event.reply("Je ne joue aucun titre pour le moment.").queue();
            return;
        }

        String url = audioPlayer.getPlayingTrack().getInfo().uri;
        String name = audioPlayer.getPlayingTrack().getInfo().title;

        event.reply("La piste en cours est : " + name + " / " + url).queue();
    }
}
