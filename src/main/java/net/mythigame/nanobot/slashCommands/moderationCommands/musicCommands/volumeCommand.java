package net.mythigame.nanobot.slashCommands.moderationCommands.musicCommands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.mythigame.nanobot.utils.lavaplayer.GuildMusicManager;
import net.mythigame.nanobot.utils.lavaplayer.PlayerManager;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import static net.mythigame.nanobot.utils.Libs.isDJ;

@SuppressWarnings("ConstantConditions")
public class volumeCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        Guild guild = event.getGuild();
        Member self = guild.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!isDJ(guild, member)){
            event.reply("Vous n'avez pas la permission d'utiliser cette commande").queue();
            return;
        }

        if(!memberVoiceState.inAudioChannel()){
            event.reply("Vous devez être dans un salon vocal pour me faire fonctionner").queue();
            return;
        }

        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            event.reply("Je ne joue aucun titre pour le moment...").queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);

        OptionMapping option = event.getOption("volume");
        int volume;
        if(option != null){
            volume = Integer.parseInt(option.getAsString());
            if(volume == 0){ volume = 100; }
        }else {
            volume = 100;
        }

        if(volume >=1 && volume <= 200){
            musicManager.scheduler.player.setVolume(volume);
            event.reply("Le volume du bot musique est défini à : " + volume).queue();
        }else{
            event.reply("Veuillez spécifier un volume compris entre 1 et 200").queue();
        }
    }
}
