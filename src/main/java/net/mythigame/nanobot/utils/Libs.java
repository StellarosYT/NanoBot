package net.mythigame.nanobot.utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.mythigame.nanobot.Storage.MySQL.MySQLManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static net.mythigame.nanobot.NanoBot.*;

@SuppressWarnings("ConstantConditions")
public class Libs {

    public static long totalMembers = 0;

    public static String Date(){
        return ""+Today() + " "+ Hour()+"";
    }

    public static String Today(){
        SimpleDateFormat formater;
        Date aujourdhui = new Date();
        formater = new SimpleDateFormat("dd/MM/yyyy");
        return formater.format(aujourdhui);
    }

    public static String Hour(){
        SimpleDateFormat formater;
        Date aujourdhui = new Date();
        formater = new SimpleDateFormat("HH:mm:ss");
        return formater.format(aujourdhui);
    }

    @SuppressWarnings("unused")
    public static String MS(){
        SimpleDateFormat formater;
        Date aujourdhui = new Date();
        formater = new SimpleDateFormat("SS");
        return formater.format(aujourdhui);
    }

    public static boolean isAdmin(User user) {
        if(user.isBot()) return false;
        boolean admin = false;

        final Connection connection;

        try{
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT administrator FROM adminlist WHERE userid = ?");
            preparedStatement.setString(1, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                admin = true;
            }

            connection.close();
            return admin;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return admin;
    }

    public static long getTotalMembers(){
        totalMembers = 0;
        for(Guild guild : getJda().getGuilds()){
            totalMembers = totalMembers + guild.getMemberCount();
        }
        return totalMembers;
    }

    public static long getTotalGuilds(){
        return getJda().getGuilds().size();
    }

    public static void sendPrivateMessage(User user, String content) {
        user.openPrivateChannel().queue((channel) ->
                channel.sendMessage(content).queue());
    }

    public static boolean canAccessToVoiceChannel(Member member, AudioChannel channel){
        return member.hasPermission(channel, Permission.VOICE_CONNECT);
    }

    public static boolean isGuildStaff(Member member) {
        if(isAdmin(member.getUser())){
            return true;
        }
        else return member.isOwner() ||
                member.hasPermission(Permission.KICK_MEMBERS) ||
                member.hasPermission(Permission.ADMINISTRATOR) ||
                member.hasPermission(Permission.BAN_MEMBERS) ||
                member.hasPermission(Permission.MANAGE_ROLES) ||
                member.hasPermission(Permission.MANAGE_SERVER) ||
                member.hasPermission(Permission.MESSAGE_MANAGE);
    }

    public static boolean isDJ(Guild guild, Member member){
        if(isAdmin(member.getUser())){
            return true;
        }
        else if(member.hasPermission(Permission.VOICE_MUTE_OTHERS) ||
                member.hasPermission(Permission.VOICE_DEAF_OTHERS) ||
                isGuildStaff(member))
            return true;
        else if(!guild.getRolesByName("DJ", true).isEmpty() && guild.getRolesByName("DJ", true).size() <= 1){
            for(Role role : guild.getRolesByName("DJ", true)){
                if (guild.getMember(member.getUser()).getRoles().contains(role)) return true;
            }
        }
        return false;
    }

    public static String callComSulteAPI(String type){

        if(type.equals("compliment")){
            List<String> com = new ArrayList<>();
            com.add("Tu es une personne extrêmement appréciable !");
            com.add("Tu compte beaucoup pour moi !");
            com.add("Tu es une belle personne !");
            com.add("Ta sympathie me fait vivre de meilleurs jours !");
            com.add("Tu es vraiment une personne sympathique !");
            com.add("Je t'adore !");
            com.add("Ta gentillesse me réconforte !");
            com.add("Tu es toujours là pour moi !");
            com.add("J'adore parler avec toi !");
            com.add("Ton sourir est éclatant !");
            com.add("Tu es magnifique !");
            com.add("Tu es une personne fiable sur qui je peux compter !");
            com.add("Plus je te connais, plus je t'apprécie !");
            com.add("Tu es aussi magnifique à l'intérieur qu'à l'extérieur !");
            com.add("Merci d'être toi !");
            com.add("Avec toi, on ne s'ennuie jamais !");
            com.add("J'aime tellement ton humour !");
            com.add("J'admire ta personnalité");
            com.add("Tu me fais voir le monde comme personne ne me l'a jamais fait voir !");
            com.add("J'adore la manière avec laquelle tu me fais réfléchir !");
            com.add("J'aime la manière dont tu me défies !");
            com.add("A tes côtés, je suis la meilleure version possible de moi-même !");
            com.add("Ton énergie est communicative !");
            com.add("Tu es tout pour moi !");
            com.add("Tu me rappelles constamment que les gens peuvent être bons !");
            com.add("Il y a de la douceur dans tes yeux !");
            com.add("Ton rire est mon bruit préféré !");
            com.add("Le monde serait tellement ennuyeux sans toi !");
            com.add("Tu rends importantes les petites choses !");
            com.add("Quand nous sommes ensemble, le reste du monde disparaît !");
            com.add("J'aimerais être au moins la moitié de l'être humain que tu es !");
            com.add("Je ne savais pas à quel point l'amitié était importante avant de te rencontrer !");
            com.add("Ce serait une torture de te perdre !");
            com.add("Ma vie avant toi était ennuyeuse à mourir !");
            com.add("Ton âme est magnifique !");
            com.add("J'aime que tu sois bizarre !");
            com.add("Tu m'inspires !");
            com.add("Ta voix m'apaise !");
            com.add("Je n'ai jamais rencontré quelqu'un d'aussi attentionné que toi !");
            com.add("Personne d'autre ne peut me faire rire comme toi !");
            com.add("Tu as si bon coeur !");
            com.add("Tu es vraiment inoubliable !");
            com.add("Wahou. Toi !");
            com.add("Te connaître me fait me sentir bien !");
            com.add("N'arrête jamais d'être toi, s'il te plaît !");
            com.add("Tu fais que je veux être une meilleure personne !");
            com.add("Je ne comprenais pas l'intérêt de prendre des photos avant de te rencontrer. Je veux garder trace de nos souvenirs pour toujours !");
            com.add("Je pense que tu es la seule personne à qui je peux parler ouvertement !");
            com.add("Quand j'ai une dure journée, je pense à toi et cela me donne de la force et de l'espoir !");
            com.add("Tu es la lumière de ma vie !");
            int randomNumber = (int)(Math.random() * ((com.size() - 1) + 1));

            return com.get(randomNumber);

        }else if (type.equals("insult")){
            List<String> sulte = new ArrayList<>();

            sulte.add("Sale bouffon'ne !");
            sulte.add("Espèce de bordille !");
            sulte.add("Pauvre buse !");
            sulte.add("Espèce de chauffard !");
            sulte.add("Crevure !");
            sulte.add("Sale enfoiré'e !");
            sulte.add("Tu es un étron !");
            sulte.add("Espèce de flaque de parvo !");
            sulte.add("Petite fripouille !");
            sulte.add("Espèce de fumier !");
            sulte.add("Mange-merde va !");
            sulte.add("Merde !");
            sulte.add("Pauvre naze !");
            sulte.add("Pauvre pourriture !");
            sulte.add("Sale raclure !");
            sulte.add("Espèce de sagouin !");
            sulte.add("Salaud'e !");
            sulte.add("Saleté !");
            sulte.add("Pauvre tache !");
            sulte.add("Sale tas de pruin !");
            sulte.add("Espèce de vaurien'ne !");
            sulte.add("Pignouf !");
            sulte.add("Trou du cul !");
            sulte.add("Pauvre petite chaussette pleine de foutre !");
            sulte.add("Tu es un pauvre résidu de capote !");
            int randomNumber = (int)(Math.random() * ((sulte.size() - 1) + 1));

            return sulte.get(randomNumber);

        }
        return null;
    }

    public static boolean isBlacklisted(User user){
        if(user.isBot()) return false;
        boolean banned = false;

        final Connection connection;

        try{
            connection = MySQLManager.NANOBOT.getMySQLAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT banned FROM blacklist WHERE userid = ?");
            preparedStatement.setString(1, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                banned = true;
                System.out.println(ANSI_RED + "[BLACKLIST] " + ANSI_RESET + user.getName() + " (" + user.getId() + ") " + "a tenté d'éxecuter une commande !");
            }

            connection.close();
            return banned;
        }catch (Exception e){
            e.printStackTrace();
        }

        return banned;
    }

}
