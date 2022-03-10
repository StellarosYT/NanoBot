package net.mythigame.nanobot.slashCommands.userCommands.musicCommands;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import static net.mythigame.nanobot.utils.Libs.canAccessToVoiceChannel;

@SuppressWarnings("ConstantConditions")
public class joinCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        GuildVoiceState memberVoiceState = member.getVoiceState();
        AudioChannel memberChannel = memberVoiceState.getChannel();

        final AudioManager audioManager = event.getGuild().getAudioManager();

        if(!memberVoiceState.inAudioChannel()){
            event.reply("Vous devez être dans un salon vocal pour me faire fonctionner.").queue();
            return;
        }

        if(selfVoiceState.inAudioChannel()){
            event.reply("Je suis déjà dans un autre salon vocal...").queue();
            return;
        }

        if(!canAccessToVoiceChannel(self, memberVoiceState.getChannel())){
            event.reply("Je n'ai pas la permission de rejoindre ce salon vocal...").queue();
            return;
        }

        audioManager.openAudioConnection(memberChannel);
        event.reply("Je viens de rejoindre votre salon vocal.").queue();
    }
}
