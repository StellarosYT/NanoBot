package net.mythigame.nanobot.slashCommands.userCommands.funCommands.nsfw;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import static net.mythigame.nanobot.NanoBot.CONFIGURATION;

public class fuckCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        if(!channel.isNSFW()){
            event.reply("Je suis désolé mais vous devez être dans un salon **NSFW** pour que j'envoie ce type de contenu").queue();
            return;
        }

        int randomGif = 1 + (int)(Math.random() * ((50 - 1) + 1));

        OptionMapping option = event.getOption("type");
        if(option != null){
            String type = option.getAsString();
            switch (type) {
                case "lesbian" -> channel.sendMessage(CONFIGURATION.getString("lfuckGifAPI", "Lien du répertoire de gifs lfuck") + randomGif + ".gif").queue();
                case "gay" -> channel.sendMessage(CONFIGURATION.getString("gfuckGifAPI", "Lien du répertoire de gifs gfuck") + randomGif + ".gif").queue();
                case "bi" -> channel.sendMessage(CONFIGURATION.getString("bfuckGifAPI", "Lien du répertoire de gifs bfuck") + randomGif + ".gif").queue();
                case "straight" -> channel.sendMessage(CONFIGURATION.getString("fuckGifAPI", "Lien du répertoire de gifs fuck") + randomGif + ".gif").queue();
            }
        }else{
            channel.sendMessage(CONFIGURATION.getString("fuckGifAPI", "Lien du répertoire de gifs fuck") + randomGif + ".gif").queue();
        }
        event.reply("Voici :").queue(e -> e.deleteOriginal().queue());
    }
}
