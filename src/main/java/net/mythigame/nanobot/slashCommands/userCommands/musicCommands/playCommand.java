package net.mythigame.nanobot.slashCommands.userCommands.musicCommands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.mythigame.nanobot.utils.lavaplayer.PlayerManager;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import static net.mythigame.nanobot.utils.Libs.canAccessToVoiceChannel;

@SuppressWarnings("ConstantConditions")
public class playCommand implements SlashCommand {

    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();
        AudioManager audioManager = event.getGuild().getAudioManager();

        GuildVoiceState memberVoiceState = member.getVoiceState();
        if(!memberVoiceState.inAudioChannel()){
            event.reply("Vous devez être dans un salon vocal pour me faire fonctionner").queue();
            return;
        }

        String link = event.getOption("link").getAsString();

        if(!canAccessToVoiceChannel(self, memberVoiceState.getChannel())){
            event.reply("Je n'ai pas la permission de rejoindre ce salon vocal...").queue();
            return;
        }

        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            audioManager.openAudioConnection(memberVoiceState.getChannel());
        }

        if(!link.contains("http://") && !link.contains("https://") && !link.contains("www.")){
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance()
                .loadAndPlay(event, link);
    }

}
