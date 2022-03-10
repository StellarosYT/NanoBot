package net.mythigame.nanobot.slashCommands.userCommands.funCommands.nsfw;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.mythigame.nanobot.slashCommands.SlashCommand;

import static net.mythigame.nanobot.NanoBot.CONFIGURATION;

public class cumCommand implements SlashCommand {
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
                case "lesbian" -> channel.sendMessage(CONFIGURATION.getString("lcumGifAPI", "Lien du répertoire de gifs lcum") + randomGif + ".gif").queue();
                case "gay" -> channel.sendMessage(CONFIGURATION.getString("gcumGifAPI", "Lien du répertoire de gifs gcum") + randomGif + ".gif").queue();
                case "bi" -> channel.sendMessage(CONFIGURATION.getString("bcumGifAPI", "Lien du répertoire de gifs bcum") + randomGif + ".gif").queue();
                case "straight" -> channel.sendMessage(CONFIGURATION.getString("cumGifAPI", "Lien du répertoire de gifs cum") + randomGif + ".gif").queue();
            }
        }else{
            channel.sendMessage(CONFIGURATION.getString("cumGifAPI", "Lien du répertoire de gifs cum") + randomGif + ".gif").queue();
        }
        event.reply("Voici :").queue(e -> e.deleteOriginal().queue());
    }
}
