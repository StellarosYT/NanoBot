package net.mythigame.nanobot.consoleCommands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String name();
    String description() default "Cette commande n'a pas de description";
    ExecutorType type() default ExecutorType.ALL;

    enum ExecutorType{
        ALL, USER, CONSOLE
    }

}
