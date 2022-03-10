package net.mythigame.nanobot.slashCommands.userCommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import java.util.EnumSet;

import static net.mythigame.nanobot.slashCommands.utils.autoRoomManager.*;

@SuppressWarnings("ConstantConditions")
public class svaCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        if(!isSVAManager(channel, member)){
            event.reply("Vous devez executer cette commande dans un salon de gestion de SVA et en être le manager.").queue();
            return;
        }

        VoiceChannel SVAChannel = getSVAVChannelByManageChannel(channel);

        String subcommand = event.getSubcommandName();
        Member target = event.getOption("target").getAsMember();

        if(target == null){
            event.reply("Le membre séléctionné n'existe pas !").queue();
            return;
        }

        if (subcommand != null) {
            switch (subcommand){
                case "ban": {
                    if(isSVAManager(channel, target)){
                        event.reply("Vous ne pouvez pas vous bannir vous même !").queue();
                        return;
                    }

                    if(SVAChannel.upsertPermissionOverride(target).getDeniedPermissions().contains(Permission.VOICE_CONNECT)){
                        event.reply("Cette personne est déjà banni.").queue();
                        return;
                    }

                    SVAChannel.upsertPermissionOverride(target).deny(Permission.VOICE_CONNECT).queue();
                    if(target.getVoiceState().inAudioChannel() && target.getVoiceState().getChannel().equals(SVAChannel)){
                        SVAChannel.getParentCategory().createVoiceChannel(target.getEffectiveName() + "-kick").queue(e -> event.getGuild().moveVoiceMember(target, e).queue(a -> e.delete().queue()));
                    }

                    event.reply("Vous avez bien banni " + target.getAsMention()).queue();

                }
                case "unban": {
                    if(isSVAManager(channel, target)){
                        event.reply("Vous ne pouvez pas vous dé-bannir vous même !").queue();
                        return;
                    }

                    if(SVAChannel.upsertPermissionOverride(target).getAllowedPermissions().contains(Permission.VOICE_CONNECT)){
                        event.reply("Cette personne n'est pas banni.").queue();
                        return;
                    }
                    SVAChannel.getManager().removePermissionOverride(target).complete();
                    event.reply("Vous avez bien dé-banni " + target.getAsMention()).queue();
                }
                case "add": {
                    if(isSVAManager(channel, target)){
                        event.reply("Vous n'avez pas besoin de vous whitelist vous même !").queue();
                        return;
                    }

                    if(SVAChannel.upsertPermissionOverride(target).getAllowedPermissions().contains(Permission.VIEW_CHANNEL)){
                        event.reply("Cette personne est déjà whitelist.").queue();
                        return;
                    }
                    SVAChannel.upsertPermissionOverride(target).grant(Permission.VIEW_CHANNEL).queue();
                    event.reply("Vous avez bien whitelist " + target.getAsMention()).queue();
                }
                case "remove": {
                    if(isSVAManager(channel, target)){
                        event.reply("Vous ne pouvez pas vous unwhitelist vous même !").queue();
                        return;
                    }

                    EnumSet<Permission> remove = EnumSet.of(Permission.VIEW_CHANNEL);
                    if(SVAChannel.upsertPermissionOverride(target).getDeniedPermissions().contains(Permission.VIEW_CHANNEL)){
                        event.reply("Cette personne n'est pas whitelist.").queue();
                        return;
                    }
                    SVAChannel.getManager().removePermissionOverride(target).complete();
                    event.reply("Vous avez bien unwhitelist " + target.getAsMention()).queue();
                }
                case "makeprivate": {
                    String type = event.getOption("type").getAsString();
                    if(type.equalsIgnoreCase("true")){
                        if(SVAChannel.upsertPermissionOverride(event.getGuild().getPublicRole()).getAllowedPermissions().contains(Permission.VIEW_CHANNEL)){
                            event.reply("Ce salon est déjà public").queue();
                            return;
                        }
                        SVAChannel.upsertPermissionOverride(event.getGuild().getPublicRole()).grant(Permission.VIEW_CHANNEL).queue();
                        event.reply("Le salon est désormais public.").queue();
                    }else if(type.equalsIgnoreCase("false")){
                        if(SVAChannel.upsertPermissionOverride(event.getGuild().getPublicRole()).getDeniedPermissions().contains(Permission.VIEW_CHANNEL)){
                            event.reply("Ce salon est déjà privé").queue();
                            return;
                        }
                        SVAChannel.upsertPermissionOverride(event.getGuild().getPublicRole()).deny(Permission.VIEW_CHANNEL).queue();
                        event.reply("Le salon est désormais privé.").queue();
                    }
                }
                case "transfer": {
                    if(isSVAManager(channel, target)){
                        event.reply("Vous êtes déjà le manager de se SVA.").queue();
                        return;
                    }

                    makeNewSVAManager(channel, target);
                    SVAChannel.getManager().removePermissionOverride(member).complete();
                    SVAChannel.upsertPermissionOverride(target).grant(Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS, Permission.VIEW_CHANNEL).queue();
                    channel.getManager().removePermissionOverride(member).complete();
                    channel.upsertPermissionOverride(target).grant(Permission.VIEW_CHANNEL, Permission.USE_APPLICATION_COMMANDS).queue();
                    event.reply(target.getAsMention() + " est le nouveau manager de se SVA.").queue();
                }
            }
        }

    }
}
