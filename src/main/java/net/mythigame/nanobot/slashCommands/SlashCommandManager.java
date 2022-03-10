package net.mythigame.nanobot.slashCommands;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.mythigame.nanobot.slashCommands.developerCommands.*;
import net.mythigame.nanobot.slashCommands.moderationCommands.banCommand;
import net.mythigame.nanobot.slashCommands.moderationCommands.kickCommand;
import net.mythigame.nanobot.slashCommands.moderationCommands.musicCommands.clearQueueCommand;
import net.mythigame.nanobot.slashCommands.moderationCommands.musicCommands.repeatCommand;
import net.mythigame.nanobot.slashCommands.moderationCommands.musicCommands.volumeCommand;
import net.mythigame.nanobot.slashCommands.moderationCommands.unbanCommand;
import net.mythigame.nanobot.slashCommands.moderationCommands.utilsCommands.autoRoomCommand;
import net.mythigame.nanobot.slashCommands.moderationCommands.utilsCommands.reactionRoleCommand;
import net.mythigame.nanobot.slashCommands.moderationCommands.utilsCommands.voteGameCommand;
import net.mythigame.nanobot.slashCommands.userCommands.funCommands.*;
import net.mythigame.nanobot.slashCommands.userCommands.funCommands.nsfw.cumCommand;
import net.mythigame.nanobot.slashCommands.userCommands.funCommands.nsfw.fuckCommand;
import net.mythigame.nanobot.slashCommands.userCommands.funCommands.nsfw.suckCommand;
import net.mythigame.nanobot.slashCommands.userCommands.helpCommand;
import net.mythigame.nanobot.slashCommands.userCommands.musicCommands.*;
import net.mythigame.nanobot.slashCommands.userCommands.pingCommand;
import net.mythigame.nanobot.slashCommands.userCommands.svaCommand;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;
import static net.mythigame.nanobot.NanoBot.getJda;
import static net.mythigame.nanobot.utils.SendLogs.printLog;

@SuppressWarnings({"ResultOfMethodCallIgnored", "NullableProblems"})
public class SlashCommandManager extends ListenerAdapter {

    private final Map<String, SlashCommand> commandsMap;

    public SlashCommandManager(){
        commandsMap = new ConcurrentHashMap<>();

        CommandListUpdateAction commands = getJda().updateCommands();

        commandsMap.put("ping", new pingCommand());
        commands.addCommands(new CommandData("ping", "Vous permet d'obtenir le temps de latence entre le robot et l'API Discord"));

        commandsMap.put("help", new helpCommand());
        commands.addCommands(new CommandData("help", "Vous permet d'obtenir un lien vers le site internet de NanoBot."));

        commandsMap.put("cp", new currentPlayingCommand());
        commands.addCommands(new CommandData("cp", "Vous permet de connaître la piste en cours de lecture."));

        commandsMap.put("join", new joinCommand());
        commands.addCommands(new CommandData("join", "Vous permet de faire rejoindre votre salon à NanoBot."));

        commandsMap.put("leave", new leaveCommand());
        commands.addCommands(new CommandData("leave", "Vous permet de faire quitter votre salon à NanoBot."));

        commandsMap.put("pause", new pauseCommand());
        commands.addCommands(new CommandData("pause", "Vous permet de mettre la piste en cours sur pause."));

        commandsMap.put("play", new playCommand());
        commands.addCommands(new CommandData("play", "Vous permet de jouer un titre.")
                .addOptions(new OptionData(STRING, "link", "La recherche ou le lien du titre.", true))
        );

        commandsMap.put("queue", new queueCommand());
        commands.addCommands(new CommandData("queue", "Vous permet de connaître les pistes en attentes."));

        commandsMap.put("resume", new resumeCommand());
        commands.addCommands(new CommandData("resume", "Vous permet de reprendre une piste mise sur pause."));

        commandsMap.put("skip", new skipCommand());
        commands.addCommands(new CommandData("skip", "Vous permet de passer une piste en cours."));

        commandsMap.put("ban", new banCommand());
        commands.addCommands(new CommandData("ban", "Vous permet de bannir un membre du discord.")
                    .addOptions(new OptionData(USER, "banned", "Membre à bannir.", true))
                    .addOptions(new OptionData(STRING, "reason", "Motif du bannissement.", false))
        );

        commandsMap.put("kick", new kickCommand());
        commands.addCommands(new CommandData("kick", "Vous permet d'expulser un membre du discord.")
                    .addOptions(new OptionData(USER, "kicked", "Membre à expulser du discord.", true))
                    .addOptions(new OptionData(STRING, "reason", "Motif de l'expulsion.", false))
        );

        commandsMap.put("unban", new unbanCommand());
        commands.addCommands(new CommandData("unban", "Vous permet de dé-bannir un membre banni du discord.")
                    .addOptions(new OptionData(STRING, "id", "ID du membre à dé-bannir du discord.", true))
                    .addOptions(new OptionData(STRING, "reason", "Raison ou note lié au dé-bannissement.", false))
        );

        commandsMap.put("reactionrole", new reactionRoleCommand());
        commands.addCommands(new CommandData("reactionrole", "Vous permet de gérer les 'reactionrole' de votre discord.")
                    .addSubcommands(new SubcommandData("add", "Vous permet de créer un 'reactionrole'.")
                        .addOptions(new OptionData(CHANNEL, "channel", "Vous permet de choisir le salon dans lequel le 'reactionrole' sera créé.", true))
                        .addOptions(new OptionData(STRING, "messageid", "ID du message sous lequel apparaîtra le 'reactionrole'.", true))
                        .addOptions(new OptionData(STRING, "emoji", "Emoji qui sera affiché sous le message du 'reactionrole'.", true))
                        .addOptions(new OptionData(ROLE, "role", "Rôle qui sera ajouté par le 'reactionrole'.", true)))
                    .addSubcommands(new SubcommandData("remove", "Vous permet de supprimer un 'reactionrole'.")
                        .addOptions(new OptionData(INTEGER, "id", "ID du 'reactionrole' à supprimer.", true)))
                    .addSubcommands(new SubcommandData("list", "Vous permet d'obtenir la liste des 'reactionrole' sur votre discord."))
        );

        commandsMap.put("votegame", new voteGameCommand());
        commands.addCommands(new CommandData("votegame", "Vous permet de gérer les 'votegame' de votre discord.")
                    .addSubcommands(new SubcommandData("add", "Vous permet de créer un 'votegame'.")
                        .addOptions(new OptionData(CHANNEL, "channel", "Vous permet de choisir le salon dans lequel le 'votegame' sera créé.", true))
                        .addOptions(new OptionData(STRING, "messageid", "ID du message sous lequel apparaîtra le 'votegame'.", true))
                        .addOptions(new OptionData(STRING, "emoji", "Emoji qui sera affiché sous le message du 'votegame'.", true))
                        .addOptions(new OptionData(INTEGER, "requirednumber", "Nombre de vote requis avant l'envoi du message et la réinitialisation du 'votegame'.", true))
                        .addOptions(new OptionData(ROLE, "role", "Rôle à mentionner une fois le nombre de vote requis atteint.", true))
                        .addOptions(new OptionData(STRING, "announce", "Message à envoyer une fois le nombre de vote requis atteint pour mentionner les membres concernés.", true)))
                    .addSubcommands(new SubcommandData("remove", "Vous permet de supprimer un 'votegame'.")
                        .addOptions(new OptionData(INTEGER, "id", "ID du 'votegame' à supprimer.", true)))
                    .addSubcommands(new SubcommandData("list", "Vous permet d'obtenir la liste des 'votegame' sur votre discord."))
        );

        commandsMap.put("autoroom", new autoRoomCommand());
        commands.addCommands(new CommandData("autoroom", "Vous permet de gérer les salons vocaux automatiques (SVA) de votre discord.")
                    .addSubcommands(new SubcommandData("add", "Vous permet de créer un salon vocal automatique")
                        .addOptions(new OptionData(STRING, "type", "Vous permet de choisir entre un SVA public ou privé (public/private)", true)
                            .addChoice("public", "public")
                            .addChoice("private", "private"))
                        .addOptions(new OptionData(STRING, "channelid", "ID du salon vocal qui sera rattaché à votre SVA", true))
                        .addOptions(new OptionData(STRING, "categoryid", "ID de la catégorie ou seront créé les SVA", false)))
                    .addSubcommands(new SubcommandData("remove", "Vous permet de supprimer un SVA")
                        .addOptions(new OptionData(INTEGER, "id", "ID du SVA à supprimer.", true)))
                    .addSubcommands(new SubcommandData("list", "Vous permet d'obtenir la liste des SVA de votre discord."))
        );

        commandsMap.put("sva", new svaCommand());
        commands.addCommands(new CommandData("sva", "Vous permet de gérer votre salon vocal automatique (SVA)")
                .addSubcommands(new SubcommandData("ban", "Vous permet de bannir un membre de votre SVA.")
                        .addOptions(new OptionData(USER, "target", "Choisissez le joueur à bannir.", true)))
                .addSubcommands(new SubcommandData("unban", "Vous permet de dé-bannir un membre banni de votre SVA.")
                        .addOptions(new OptionData(USER, "target", "Choisissez le joueur à dé-bannir.", true)))
                .addSubcommands(new SubcommandData("add", "Vous permet d'ajouter la possibilité à un membre de rejoindre votre salon.")
                        .addOptions(new OptionData(USER, "target", "Choisissez le joueur à whitelist.", true)))
                .addSubcommands(new SubcommandData("remove", "Vous permet de retirer un joueur de la whitelist de votre SVA.")
                        .addOptions(new OptionData(USER, "target", "Choisissez le joueur à qui retirer la whitelist.", true)))
                .addSubcommands(new SubcommandData("makeprivate", "Vous permet de rendre votre salon privé ou public.")
                        .addOptions(new OptionData(STRING, "type", "true = privé / false = public", true)
                                .addChoice("true", "true")
                                .addChoice("false", "false")))
                .addSubcommands(new SubcommandData("transfer", "Vous permet de donné les droits de gérer le SVA. (VOUS PERDREZ LES VOTRES !)")
                        .addOptions(new OptionData(USER, "target", "Choissiez le joueur qui pourra gérer votre SVA.")))
        );

        commandsMap.put("clearqueue", new clearQueueCommand());
        commands.addCommands(new CommandData("clearqueue", "Vous permet de supprimer la file d'attente des pistes en attentes."));

        commandsMap.put("repeat", new repeatCommand());
        commands.addCommands(new CommandData("repeat", "Vous permet d'activer/désactiver la répétition de la piste en cours."));

        commandsMap.put("volume", new volumeCommand());
        commands.addCommands(new CommandData("volume", "Vous permet de définir le volume de NanoBot.")
                    .addOptions(new OptionData(INTEGER, "volume", "Définissez le volume (entre 1-200).", false))
        );

        commandsMap.put("bang", new bangCommand());
        commands.addCommands(new CommandData("bang", "Envoi un gif de tir.")
                    .addOptions(new OptionData(USER, "target", "Choisissez votre cible.", false))
        );

        commandsMap.put("bigmoji", new bigMojiCommand());
        commands.addCommands(new CommandData("bigmoji", "Vous permet d'obtenir un lien vers l'emoji inscrit dans la commande.")
                    .addOptions(new OptionData(STRING, "emoji", "Emoji dont vous souhaitez récupérer le lien (NE MARCHE PAS SUR LES EMOJIS PAR DÉFAUT DE DISCORD !", true))
        );

        commandsMap.put("compliment", new complimentCommand());
        commands.addCommands(new CommandData("compliment", "Vous permet de dire un compliment")
                    .addOptions(new OptionData(USER, "target", "Choisissez quelqu'un à complimenter", false))
        );

        commandsMap.put("cuddle", new cuddleCommand());
        commands.addCommands(new CommandData("cuddle", "Envoi un gif de câlin.")
                    .addOptions(new OptionData(USER, "target", "Choisissez à qui faire un câlin.", false))
        );

        commandsMap.put("feed", new feedCommand());
        commands.addCommands(new CommandData("feed", "Envoi un gif nourrissant.")
                    .addOptions(new OptionData(USER, "target", "Choisissez quelqu'un à nourrir.", false))
        );

        commandsMap.put("highfive", new highfiveCommand());
        commands.addCommands(new CommandData("highfive", "Envoi un gif d'un highfive.")
                    .addOptions(new OptionData(USER, "target", "Choisissez quelqu'un à qui faire un check.", false))
        );

        commandsMap.put("insult", new insultCommand());
        commands.addCommands(new CommandData("insult", "Vous permet de dire une insulte.")
                    .addOptions(new OptionData(USER, "target", "Choisissez quelqu'un à insulter.", false))
        );

        commandsMap.put("kiss", new kissCommand());
        commands.addCommands(new CommandData("kiss", "Envoi un gif d'un baiser.")
                    .addOptions(new OptionData(USER, "target", "Choisissez quelqu'un à embrasser.", false))
        );

        commandsMap.put("pat", new patCommand());
        commands.addCommands(new CommandData("pat", "Envoi un gif d'une tape sur la tête.")
                    .addOptions(new OptionData(USER, "target", "Choisissez quelqu'un à qui donner une tape sur la tête.", false))
        );

        commandsMap.put("pp", new ppCommand());
        commands.addCommands(new CommandData("pp", "Vous permet d'obtenir la photo de profil de l'id indiqué.")
                    .addOptions(new OptionData(STRING, "id", "ID de la personne dont vous souhaitez obtenir la photo de profil.", true))
        );

        commandsMap.put("slap", new slapCommand());
        commands.addCommands(new CommandData("slap", "Envoi un gif d'une gifle.")
                    .addOptions(new OptionData(USER, "target", "Définissez quelqu'un à frapper.", false))
        );

        commandsMap.put("cum", new cumCommand());
        commands.addCommands(new CommandData("cum", "Envoi un gif d'une éjaculation.")
                    .addOptions(new OptionData(STRING, "type", "Choisissez le type de pornographie vous souhaitez.", false)
                        .addChoice("straight", "straight")
                        .addChoice("lesbian", "lesbian")
                        .addChoice("gay", "gay")
                        .addChoice("bi", "bi"))
        );

        commandsMap.put("fuck", new fuckCommand());
        commands.addCommands(new CommandData("fuck", "Envoi un gif d'une relation sexuelle.")
                    .addOptions(new OptionData(STRING, "type", "Choisissez le type de pornographie vous souhaitez.", false)
                        .addChoice("straight", "straight")
                        .addChoice("lesbian", "lesbian")
                        .addChoice("gay", "gay")
                        .addChoice("bi", "bi"))
        );

        commandsMap.put("suck", new suckCommand());
        commands.addCommands(new CommandData("suck", "Envoi un gif d'un rapport sexuelle buccal.")
                    .addOptions(new OptionData(STRING, "type", "Choisissez le type de pornographie vous souhaitez.", false)
                        .addChoice("straight", "straight")
                        .addChoice("lesbian", "lesbian")
                        .addChoice("gay", "gay")
                        .addChoice("bi", "bi"))
        );

        commandsMap.put("restart", new restartCommand());
        commands.addCommands(new CommandData("restart", "Permet de redémarrer NanoBot."));

        commandsMap.put("say", new sayCommand());
        commands.addCommands(new CommandData("say", "Permet d'envoyer un message avec NanoBot.")
                    .addOptions(new OptionData(STRING, "message", "Message à envoyer avec NanoBot.", true))
                    .addOptions(new OptionData(CHANNEL, "channel", "Permet de définir le salon dans lequel envoyer le message.", false))
        );

        commandsMap.put("setstatus", new setStatusCommand());
        commands.addCommands(new CommandData("setstatus", "Permet de définir le status de NanoBot.")
                    .addOptions(new OptionData(STRING, "status", "Définissez le status à actualiser.", true)
                        .addChoice("online", "online")
                        .addChoice("do not disturb", "dnb")
                        .addChoice("idle", "idle")
                        .addChoice("offline", "offline"))
        );

        commandsMap.put("stop", new stopCommand());
        commands.addCommands(new CommandData("stop", "Permet d'arrêter NanoBot."));

        commandsMap.put("developer", new developerCommand());
        commands.addCommands(new CommandData("developer", "Permet de gérer les développeurs de NanoBot.")
                    .addSubcommands(new SubcommandData("add", "Permet d'ajouter un utilisateur aux développeurs de NanoBot.")
                        .addOptions(new OptionData(USER, "target", "Définissez la personne à ajouter aux développeurs.", true)))
                    .addSubcommands(new SubcommandData("remove", "Permet de retirer un utilisateur des développeurs de NanoBot.")
                        .addOptions(new OptionData(USER, "target", "Définissez la personne à retirer des développeurs.", true)))
                    .addSubcommands(new SubcommandData("list", "Permet d'obtenir la liste des développeurs de NanoBot"))
        );

        commandsMap.put("blacklist", new blacklistCommand());
        commands.addCommands(new CommandData("blacklist", "Permet de gérer les utilisateurs blacklistez de NanoBot.")
                    .addSubcommands(new SubcommandData("add", "Permet de blacklister un utilisateur de NanoBot.")
                        .addOptions(new OptionData(USER, "target", "Définissez la personne à blacklister", true)))
                    .addSubcommands(new SubcommandData("remove", "Permet de retirer de la blacklist un utilisateur de NanoBot.")
                        .addOptions(new OptionData(USER, "target", "Définissez la personne à retirer de la blacklist.", true)))
                    .addSubcommands(new SubcommandData("list", "Permet d'obtenir la liste des personnes blacklist de NanoBot."))
        );

        commands.queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event){
        String commandName = event.getName();

        SlashCommand command;

        if((command = commandsMap.get(commandName)) != null){
            command.performCommand(event, event.getMember(), event.getTextChannel());
            printLog(event);
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event){
    }


}
