package net.mythigame.nanobot.event;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.managers.AudioManager;
import net.mythigame.nanobot.NanoBot;
import net.mythigame.nanobot.Storage.MySQL.MySQLManager;
import net.mythigame.nanobot.consoleCommands.CommandMap;
import net.mythigame.nanobot.utils.lavaplayer.GuildMusicManager;
import net.mythigame.nanobot.utils.lavaplayer.PlayerManager;
import net.mythigame.nanobot.utils.Libs;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.EnumSet;

import static net.mythigame.nanobot.NanoBot.*;
import static net.mythigame.nanobot.slashCommands.utils.autoRoomManager.*;
import static net.mythigame.nanobot.slashCommands.utils.reactionRoleManager.*;
import static net.mythigame.nanobot.slashCommands.utils.voteGameUserManager.*;
import static net.mythigame.nanobot.utils.Libs.sendPrivateMessage;
import static net.mythigame.nanobot.utils.SendLogs.sendAdminLog;
import static net.mythigame.nanobot.utils.SendLogs.sendModsLog;

@SuppressWarnings("ConstantConditions")
public class Listeners implements EventListener
{
    private final CommandMap commandMap;

    public Listeners(CommandMap commandMap) {
        this.commandMap = commandMap;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event)
    {
        if(event instanceof MessageReceivedEvent) onMessageReceivedEvent((MessageReceivedEvent) event);
        else if(event instanceof GuildMemberJoinEvent) onGuildMemberJoin((GuildMemberJoinEvent) event);
        else if(event instanceof GuildMemberRemoveEvent) onGuildMemberRemove((GuildMemberRemoveEvent) event);
        else if(event instanceof GuildBanEvent) onGuildBan((GuildBanEvent) event);
        else if(event instanceof GuildUnbanEvent) onGuildUnBan((GuildUnbanEvent) event);
        else if(event instanceof GuildJoinEvent) onGuildJoin((GuildJoinEvent) event);
        else if(event instanceof GuildLeaveEvent) onGuildLeave((GuildLeaveEvent) event);
        else if(event instanceof MessageReactionAddEvent) onGuildMessageReactionAddEvent((MessageReactionAddEvent) event);
        else if(event instanceof MessageReactionRemoveEvent) onGuildMessageReactionRemoveEvent((MessageReactionRemoveEvent) event);
        else if(event instanceof MessageDeleteEvent) onGuildMessageDeleteEvent((MessageDeleteEvent) event);
        else if(event instanceof GuildVoiceLeaveEvent) onGuildVoiceLeaveEvent((GuildVoiceLeaveEvent) event);
        else if(event instanceof GuildVoiceMoveEvent) onGuildVoiceMoveEvent((GuildVoiceMoveEvent) event);
        else if(event instanceof GuildVoiceJoinEvent) onGuildVoiceJoinEvent((GuildVoiceJoinEvent) event);
        else if(event instanceof ChannelDeleteEvent) onVoiceChannelDeleteEvent((ChannelDeleteEvent) event);

        if (event instanceof ReadyEvent){
            System.out.println(ANSI_GREEN + "[STATUS] API CHARGÉ !" + ANSI_RESET);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void onMessageReceivedEvent(MessageReceivedEvent event){
        User user = event.getAuthor();
        Message message = event.getMessage();
        Guild guild = event.getGuild();
        if(guild != null) return;
        if(user.isBot()) return;
        System.out.println(ANSI_YELLOW + "[PRIVATE-MESSAGE-LOGS] "+ user.getName() + " (" + user.getId() + ") : " + message.getContentRaw() + ANSI_RESET);
    }

    public void onGuildMemberJoin(GuildMemberJoinEvent event){
        User user = event.getUser();
        Guild guild = event.getGuild();
        if(user.isBot()) return;
        System.out.println(ANSI_BLACK + "[LOGS] "+ user.getName() + " (" + user.getId() + ") a rejoint le discord " + guild.getName() + " (" + guild.getId() + ")" + ANSI_RESET);
    }

    public void onGuildMemberRemove(GuildMemberRemoveEvent event){
        User user = event.getUser();
        Guild guild = event.getGuild();
        if(user.isBot()) return;
        System.out.println(ANSI_BLACK + "[LOGS] "+ user.getName() + " (" + user.getId() + ") a quitté le discord " + guild.getName() + " (" + guild.getId() + ")" + ANSI_RESET);
    }

    public void onGuildBan(GuildBanEvent event){
        User user = event.getUser();
        Guild guild = event.getGuild();
        if(user.isBot()) return;
        event.getGuild().retrieveBan(event.getUser()).queue(target -> {
            String reason = "Aucune raison spécifié";
            if(target != null && !target.getReason().isEmpty()){
               reason = target.getReason();
            }

            EmbedBuilder builder = new EmbedBuilder();
            builder.appendDescription(":cyclone: Ban Notification");
            builder.addField("User :", event.getUser().getAsMention(), true);
            builder.addField("Guild :", event.getGuild().getName(), true);
            builder.addField("Raison :", reason, false);
            builder.setFooter(event.getUser().getName()+"#"+event.getUser().getDiscriminator()+" | "+ Libs.Today()+" - "+Libs.Hour(), event.getUser().getAvatarUrl());
            sendModsLog(builder.build());

            System.out.println(ANSI_GREEN + "[MODÉRATION] "+ user.getName() + " (" + user.getId() + ") a été banni du discord " + guild.getName() + " (" + guild.getId() + ") pour : " + reason + "." + ANSI_RESET);
        });
    }

    public void onGuildUnBan(GuildUnbanEvent event){
        User user = event.getUser();
        Guild guild = event.getGuild();
        if(user.isBot()) return;

        EmbedBuilder builder = new EmbedBuilder();
        builder.appendDescription(":no_entry_sign: Unban Notification");
        builder.addField("User :", event.getUser().getAsMention(), true);
        builder.addField("Guild :", event.getGuild().getName(), true);
        builder.setFooter(event.getUser().getName()+"#"+event.getUser().getDiscriminator()+" | "+ Libs.Today()+" - "+Libs.Hour(), event.getUser().getAvatarUrl());
        sendModsLog(builder.build());

        System.out.println(ANSI_GREEN + "[MODÉRATION] "+ user.getName() + " (" + user.getId() + ") a été dé-banni du discord " + guild.getName() + " (" + guild.getId() + ")" + ANSI_RESET);
    }

    public void onGuildJoin(GuildJoinEvent event) {
        System.out.println(ANSI_CYAN + "[SQL] Mise à jour de la liste des discord en cours..." + ANSI_RESET);
        String Nom = event.getGuild().getName();
        String IDS = event.getGuild().getId();
        String IURL = event.getGuild().getIconUrl();
        String VLVL = event.getGuild().getVerificationLevel().toString();
        int Users = event.getGuild().getMemberCount();

        if (IURL == null) IURL = "";

        final Connection connection;
        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO servers (name, idserver, prefix, users, iconurl, verificationlvl) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE idserver = ? ");
            preparedStatement.setString(1, Nom.replace("'", "\\'"));
            preparedStatement.setString(2, IDS);
            preparedStatement.setString(3, commandMap.getTag());
            preparedStatement.setInt(4, Users);
            preparedStatement.setString(5, IURL.replace("'", "\\'"));
            preparedStatement.setString(6, VLVL.replace("'", "\\'"));
            preparedStatement.setString(7, IDS);

            preparedStatement.executeUpdate();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.appendDescription(":white_check_mark: Guild Join Notification");
        builder.addField("Guild ID :", event.getGuild().getId(), true);
        builder.addField("Guild Name :", event.getGuild().getName(), true);
        builder.setFooter(event.getGuild().getName()+"#"+event.getGuild().getId()+" | "+ Libs.Today()+" - "+Libs.Hour(), event.getGuild().getIconUrl());
        sendAdminLog(builder.build());

        System.out.println(ANSI_CYAN + "[SQL] Un discord viens d'être ajouté à la base de données !" + ANSI_RESET);
    }

    public void onGuildLeave(GuildLeaveEvent event){
        System.out.println(ANSI_CYAN + "[SQL] Mise à jour de la liste des discord en cours..." + ANSI_RESET);
        String IDS = event.getGuild().getId();

        final Connection connection;
        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM servers WHERE idserver = ?");
            preparedStatement.setString(1, IDS);

            preparedStatement.executeUpdate();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.appendDescription(":x: Guild Leave Notification");
        builder.addField("Guild ID :", event.getGuild().getId(), true);
        builder.addField("Guild Name :", event.getGuild().getName(), true);
        builder.setFooter(event.getGuild().getName()+"#"+event.getGuild().getId()+" | "+ Libs.Today()+" - "+Libs.Hour(), event.getGuild().getIconUrl());
        sendAdminLog(builder.build());

        System.out.println(ANSI_CYAN + "[SQL] Un discord viens d'être supprimé de la base de données !" + ANSI_RESET);
    }

    public void onGuildMessageReactionAddEvent(MessageReactionAddEvent event) {
        Guild guild = event.getGuild();
        String userId = event.getUser().getId();
        MessageChannel channel = event.getChannel();
        String message = event.getMessageId();
        String emote;

        if(event.getReactionEmote().isEmoji()){
            emote = EmojiParser.parseToAliases(event.getReactionEmote().getEmoji());
        }else{
            emote = event.getReactionEmote().getId();
        }

        if(getJda().getSelfUser().getId().equals(userId)) return;

        final Connection connection;
        try {
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM votegame WHERE guildid = ? AND channelid = ? AND messageid = ? AND emote = ?");
            preparedStatement.setString(1, guild.getId());
            preparedStatement.setString(2, channel.getId());
            preparedStatement.setString(3, message);
            preparedStatement.setString(4, emote);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                for(int i = 1; i <= resultSet.getRow(); i++){
                    String guildId = resultSet.getString(1);
                    String channelId = resultSet.getString(2);
                    String messageId = resultSet.getString(3);
                    String emoteId = resultSet.getObject(4).toString();
                    String id = guildId+":"+channelId+":"+messageId+":"+emoteId;
                    int reactNumber = resultSet.getInt(5);
                    String roleId = resultSet.getString(6);
                    String annonceMessage = resultSet.getString(7);
                    int userVoteSize = getVotesNumber(id)+1;

                    if(guildId.equals(guild.getId()) &&
                            channelId.equals(channel.getId()) &&
                            messageId.equals(message) &&
                            emoteId.equals(emote)
                    ){
                        if(userVoteSize == reactNumber){
                            NanoBot.getJda().getGuildById(guild.getId()).getTextChannelById(channel.getId()).sendMessage(guild.getRoleById(roleId).getAsMention() +" "+ annonceMessage).queue();
                            clearReaction(guildId, channelId, messageId, event.getReactionEmote());
                            addReaction(guildId, channelId, messageId, event.getReactionEmote());
                            clearVoteGameUser(id);
                            System.out.println("[REACTION-COMMAND] ["+ guild.getId() +"] "+ event.getUser().getName() + " ("+event.getUserId() + ") a executé le VoteGame "+ id + ". ("+userVoteSize+"/"+reactNumber+")");
                        }else{
                            addVoteGameUser(id, userId, System.currentTimeMillis());
                            System.out.println("[REACTION-COMMAND] ["+ guild.getId() +"] "+ event.getUser().getName() + " ("+event.getUserId() + ") a été ajouté au VoteGame "+ id + ". ("+userVoteSize+"/"+reactNumber+")");
                        }
                        break;
                    }
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final Connection connection1;
        try {
            connection1 = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection1.prepareStatement("SELECT * FROM reactionrole WHERE guildid = ? AND channelid = ? AND messageid = ? AND emote = ?"
            );
            preparedStatement.setString(1, guild.getId());
            preparedStatement.setString(2, channel.getId());
            preparedStatement.setString(3, message);
            preparedStatement.setString(4, emote);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                for(int i = 1; i <= resultSet.getRow(); i++){
                    String guildId = resultSet.getString(1);
                    String channelId = resultSet.getString(2);
                    String messageId = resultSet.getString(3);
                    String emoteId = resultSet.getObject(4).toString();
                    String roleId = resultSet.getString(5);
                    String id = guildId+":"+channelId+":"+messageId+":"+emoteId;

                    if(guildId.equals(guild.getId()) &&
                            channelId.equals(channel.getId()) &&
                            messageId.equals(message) &&
                            emoteId.equals(emote)
                    ){
                        Role role = event.getGuild().getRoleById(roleId);
                        event.getGuild().addRoleToMember(event.getMember(), role).queue();
                        sendPrivateMessage(event.getUser(), "Vous avez reçu le rôle : " + role.getName());
                        System.out.println("[REACTION-COMMAND] ["+ guild.getId() +"] "+ event.getUser().getName() + " ("+event.getUserId() + ") a executé le ReactionRole " + id);
                        break;
                    }
                }
            }
            connection1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void onGuildMessageReactionRemoveEvent(MessageReactionRemoveEvent event) {
        Guild guild = event.getGuild();
        String userId = event.getUserId();
        MessageChannel channel = event.getChannel();
        String message = event.getMessageId();
        String emote;

        if(NanoBot.getJda().getSelfUser().getId().equals(userId)) return;

        final Connection connection;
        try {
            if(event.getReactionEmote().isEmoji()){
                emote = EmojiParser.parseToAliases(event.getReactionEmote().getEmoji());
            }else{
                emote = event.getReactionEmote().getId();
            }

            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM votegame WHERE guildid = ? AND channelid = ? AND messageid = ? AND emote = ?");
            preparedStatement.setString(1, guild.getId());
            preparedStatement.setString(2, channel.getId());
            preparedStatement.setString(3, message);
            preparedStatement.setString(4, emote);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                for(int i = 1; i <= resultSet.getRow(); i++){

                    String guildId = resultSet.getString(1);
                    String channelId = resultSet.getString(2);
                    String messageId = resultSet.getString(3);
                    String emoteId = resultSet.getObject(4).toString();
                    String id = guildId+":"+channelId+":"+messageId+":"+emoteId;
                    if(guildId.equals(guild.getId()) &&
                            channelId.equals(channel.getId()) &&
                            messageId.equals(message) &&
                            emoteId.equals(emote)
                    ){
                        removeVoteGameUser(id, userId);
                        guild.retrieveMemberById(event.getUserId()).queue(e -> {
                            String userName = e.getUser().getName();
                            System.out.println("[REACTION-COMMAND] ["+ guild.getId() +"] "+ userName + " ("+event.getUserId() + ") a été supprimé du VoteGame "+ id + ".");
                        });
                        break;
                    }
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final Connection connection1;
        try {
            if(event.getReactionEmote().isEmoji()){
                emote = EmojiParser.parseToAliases(event.getReactionEmote().getEmoji());
            }else{
                emote = event.getReactionEmote().getId();
            }

            connection1 = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection1.prepareStatement("SELECT * FROM reactionrole WHERE guildid = ? AND channelid = ? AND messageid = ? AND emote = ?");
            preparedStatement.setString(1, guild.getId());
            preparedStatement.setString(2, channel.getId());
            preparedStatement.setString(3, message);
            preparedStatement.setString(4, emote);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                for(int i = 1; i <= resultSet.getRow(); i++){

                    String guildId = resultSet.getString(1);
                    String channelId = resultSet.getString(2);
                    String messageId = resultSet.getString(3);
                    String emoteId = resultSet.getObject(4).toString();
                    String roleId = resultSet.getString(5);
                    String id = guildId+":"+channelId+":"+messageId+":"+emoteId;
                    if(guildId.equals(guild.getId()) &&
                            channelId.equals(channel.getId()) &&
                            messageId.equals(message) &&
                            emoteId.equals(emote)
                    ){
                        Role role = guild.getRoleById(roleId);
                        guild.removeRoleFromMember(event.getMember(), role).queue();
                        sendPrivateMessage(event.getUser(), "Le rôle " + role.getName() + " vous a été retiré !");
                        System.out.println("[REACTION-COMMAND] ["+ guild.getId() +"] "+ event.getUser().getName() + " ("+event.getUserId() + ") a été supprimé du ReactionRole "+ id + ".");
                    }
                }
            }
            connection1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void onGuildMessageDeleteEvent(MessageDeleteEvent event) {
        String guildId = event.getGuild().getId();
        String channelId = event.getChannel().getId();
        String messageId = event.getMessageId();

        if(isVoteGame(guildId, channelId, messageId)){
            deleteVoteGame(guildId, channelId, messageId);
        }
        if(isReactionRole(guildId, channelId, messageId)){
            deleteReactionRole(guildId, channelId, messageId);
        }
    }

    public void onGuildVoiceLeaveEvent(GuildVoiceLeaveEvent event){
        Guild guild = event.getGuild();
        AudioChannel channel = event.getChannelLeft();
        AudioChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        if(botChannel == channel){
            int voiceChannelSize = botChannel.getMembers().size() - 1;
            if(voiceChannelSize == 0){
                musicManager.scheduler.queue.clear();
                musicManager.scheduler.repeating = false;
                musicManager.audioPlayer.stopTrack();
                musicManager.audioPlayer.setVolume(100);
                musicManager.audioPlayer.destroy();
                final AudioManager audioManager = guild.getAudioManager();
                audioManager.setAutoReconnect(false);
                audioManager.closeAudioConnection();
            }
        }
        if(isAActiveVoiceRoom(guild, null, channel)){
            if(channel.getMembers().isEmpty()){
                getManageChannelVoiceRoom(guild, null, channel).delete().queue();
                removeActiveVoiceRoom(guild, null, channel);
                channel.delete().queue();
            }
        }
    }

    public void onGuildVoiceMoveEvent(GuildVoiceMoveEvent event){
        Guild guild = event.getGuild();
        AudioChannel channel = event.getChannelLeft();
        AudioChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        final AudioManager audioManager = guild.getAudioManager();

        if(isAActiveVoiceRoom(guild, null, channel)){
            if(channel.getMembers().isEmpty()){
                getManageChannelVoiceRoom(guild, null, channel).delete().queue();
                removeActiveVoiceRoom(guild, null, channel);
                channel.delete().queue();
            }
        }

        if(event.getMember().equals(event.getGuild().getSelfMember())){
            if(event.getChannelJoined().getMembers().size() <= 1){
                musicManager.scheduler.queue.clear();
                musicManager.scheduler.repeating = false;
                musicManager.audioPlayer.stopTrack();
                musicManager.audioPlayer.setVolume(100);
                musicManager.audioPlayer.destroy();
                audioManager.setAutoReconnect(false);
                audioManager.closeAudioConnection();
            }
        }

        if(botChannel == channel){
            if(botChannel.getMembers().size() <= 1){
                musicManager.scheduler.queue.clear();
                musicManager.scheduler.repeating = false;
                musicManager.audioPlayer.stopTrack();
                musicManager.audioPlayer.setVolume(100);
                musicManager.audioPlayer.destroy();
                audioManager.setAutoReconnect(false);
                audioManager.closeAudioConnection();
            }
        }

        if(event.getMember().getUser().isBot()) return;
        Member member = event.getMember();
        AudioChannel joinedchannel = event.getChannelJoined();
        Category category = null;

        if(isRoomExist(guild, category, joinedchannel)){
            EnumSet<Permission> allow = EnumSet.of(Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS, Permission.VIEW_CHANNEL);
            if(getVoiceRoomType(guild, category, joinedchannel).equalsIgnoreCase("public")){
                category.createVoiceChannel("\uD83D\uDD0A " + member.getEffectiveName()).addMemberPermissionOverride(member.getIdLong(), allow, null).queue(e -> guild.moveVoiceMember(member, e).queue(a -> category.createTextChannel("\uD83D\uDD0A " + member.getEffectiveName()).addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL)).addPermissionOverride(member, EnumSet.of(Permission.VIEW_CHANNEL, Permission.USE_APPLICATION_COMMANDS), null).queue(r -> addActiveVoiceRoom(guild, category, member.getVoiceState().getChannel(), r, member))));
            }else if(getVoiceRoomType(guild, category, joinedchannel).equalsIgnoreCase("private")){
                category.createVoiceChannel("\uD83D\uDD0A " + member.getEffectiveName()).addMemberPermissionOverride(member.getIdLong(), allow, null).addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL)).queue(e -> guild.moveVoiceMember(member, e).queue(a -> category.createTextChannel("\uD83D\uDD0A " + member.getEffectiveName()).addPermissionOverride(guild.getPublicRole(), null , EnumSet.of(Permission.MESSAGE_HISTORY, Permission.MESSAGE_SEND)).addPermissionOverride(member, EnumSet.of(Permission.VIEW_CHANNEL, Permission.USE_APPLICATION_COMMANDS), null).queue(r -> addActiveVoiceRoom(guild, category, member.getVoiceState().getChannel(), r, member))));
            }
        }
    }

    public void onGuildVoiceJoinEvent(GuildVoiceJoinEvent event){
        Guild guild = event.getGuild();
        Member member = event.getMember();
        AudioChannel channel = event.getChannelJoined();
        Category category = null;

        if(isRoomExist(guild, category, channel)){
            EnumSet<Permission> allow = EnumSet.of(Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS, Permission.VIEW_CHANNEL);
            if(getVoiceRoomType(guild, category, channel).equalsIgnoreCase("public")){
                category.createVoiceChannel("\uD83D\uDD0A " + member.getEffectiveName()).addMemberPermissionOverride(member.getIdLong(), allow, null).queue(e -> guild.moveVoiceMember(member, e).queue(a -> category.createTextChannel("\uD83D\uDD0A " + member.getEffectiveName()).addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL)).addPermissionOverride(member, EnumSet.of(Permission.VIEW_CHANNEL, Permission.USE_APPLICATION_COMMANDS), null).queue(r -> addActiveVoiceRoom(guild, category, member.getVoiceState().getChannel(), r, member))));
            }else if(getVoiceRoomType(guild, category, channel).equalsIgnoreCase("private")){
                category.createVoiceChannel("\uD83D\uDD0A " + member.getEffectiveName()).addMemberPermissionOverride(member.getIdLong(), allow, null).addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL)).queue(e -> guild.moveVoiceMember(member, e).queue(a -> category.createTextChannel("\uD83D\uDD0A " + member.getEffectiveName()).addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.MESSAGE_HISTORY, Permission.MESSAGE_SEND)).addPermissionOverride(member, EnumSet.of(Permission.VIEW_CHANNEL, Permission.USE_APPLICATION_COMMANDS), null).queue(r -> addActiveVoiceRoom(guild, category, member.getVoiceState().getChannel(), r, member))));
            }
        }
    }

    public void onVoiceChannelDeleteEvent(ChannelDeleteEvent event){
        Guild guild = event.getGuild();
        Channel channel = event.getChannel();
        Category category = null;

        if(channel instanceof VoiceChannel){
            if(isRoomExist(guild, category, (VoiceChannel) channel)){
                if(getManageChannelVoiceRoom(guild, category, (VoiceChannel) channel) != null){
                    getManageChannelVoiceRoom(guild, category, (VoiceChannel) channel).delete().queue();
                }
                removeActiveVoiceRoom(guild, category, (VoiceChannel) channel);
            }
        }

    }

}