package net.mythigame.nanobot.consoleCommands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.mythigame.nanobot.NanoBot;
import net.mythigame.nanobot.consoleCommands.Command.ExecutorType;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class CommandMap {

    private final NanoBot nanoBot;
    private final Map<String, SimpleCommand> commands = new HashMap<>();
    private final String tag = NanoBot.CONFIGURATION.getString("prefix", "Veuillez définir le préfix a utiliser !");

    public CommandMap(NanoBot nanoBot) {
        this.nanoBot = nanoBot;
        registerCommands(new consoleCommands());
    }

    public String getTag() {
        return tag;
    }

    public Collection<SimpleCommand> getCommands(){
        return commands.values();
    }

    public void registerCommands(Object...objects){
        for(Object object : objects) registerCommand(object);
    }

    public void registerCommand(Object object){
        for(Method method : object.getClass().getDeclaredMethods()){
            if(method.isAnnotationPresent(Command.class)){
                Command command = method.getAnnotation(Command.class);
                method.setAccessible(true);
                SimpleCommand simpleCommand = new SimpleCommand(command.name(), command.description(), command.type(), object, method);
                commands.put(command.name(), simpleCommand);
            }
        }
    }

    public void commandConsole(String command){
        Object[] object = getCommand(command);
        if(object[0] == null || ((SimpleCommand)object[0]).getExecutorType() == ExecutorType.USER){
            System.out.println("Commande inconnue.");
            return;
        }
        try{
            execute(((SimpleCommand)object[0]), command, (String[])object[1], null);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    public boolean commandUser(User user, String command, Message message){
        Object[] object = getCommand(command);
        if(object[0] == null || ((SimpleCommand)object[0]).getExecutorType() == ExecutorType.CONSOLE) return false;
        try{
            execute(((SimpleCommand)object[0]), command,(String[])object[1], message);
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @SuppressWarnings("ManualArrayCopy")
    private Object[] getCommand(String command){
        String[] commandSplit = command.split(" ");
        String[] args = new String[command.split(" ").length-1];
        for(int i = 1; i < commandSplit.length; i++) args[i-1] = commandSplit[i];
        SimpleCommand simpleCommand = commands.get(commandSplit[0].toLowerCase());
        return new Object[]{simpleCommand, args};
    }

    @SuppressWarnings({"AccessStaticViaInstance", "ConstantConditions"})
    private void execute(SimpleCommand simpleCommand, String command, String[] args, Message message) throws Exception{
        Parameter[] parameters = simpleCommand.getMethod().getParameters();
        Object[] objects = new Object[parameters.length];
        for(int i = 0; i < parameters.length; i++){
            if(parameters[i].getType() == String[].class) objects[i] = args;
            else if(parameters[i].getType() == User.class) objects[i] = message == null ? null : message.getAuthor();
            else if(parameters[i].getType() == Member.class) objects[i] = message == null ? null : message.getMember();
            else if(parameters[i].getType() == TextChannel.class) objects[i] = message == null ? null : message.getTextChannel();
            else if(parameters[i].getType() == PrivateChannel.class) objects[i] = message == null ? null : message.getPrivateChannel();
            else if(parameters[i].getType() == Guild.class) objects[i] = message == null ? null : message.getGuild();
            else if(parameters[i].getType() == String.class) objects[i] = command;
            else if(parameters[i].getType() == Message.class) objects[i] = message;
            else if(parameters[i].getType() == JDA.class) objects[i] = nanoBot.getJda();
            else if(parameters[i].getType() == MessageChannel.class) objects[i] = message.getChannel();
            else if(parameters[i].getType() == Emote.class) objects[i] = message.getEmotes();
        }
        simpleCommand.getMethod().invoke(simpleCommand.getObject(), objects);
    }
}
