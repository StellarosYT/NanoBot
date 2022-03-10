package net.mythigame.nanobot.slashCommands.moderationCommands.musicCommands;

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
public class repeatCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        Guild guild = event.getGuild();
        Member self = guild.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        GuildVoiceState memberVoiceState = member.getVoiceState();

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        if(!isDJ(guild, member)){
            event.reply("Vous n'avez pas la permission d'utiliser cette commande").queue();
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

        if(audioPlayer.getPlayingTrack() == null){
            event.reply("Je ne joue aucun titre pour le moment...").queue();
            return;
        }

        final boolean newRepeating = !musicManager.scheduler.repeating;
        musicManager.scheduler.repeating = newRepeating;

        event.replyFormat("Le robot jouera la piste **%s**", newRepeating ? "en boucle." : "normalement.").queue();
    }
}
