package net.mythigame.nanobot.Storage;

import net.dv8tion.jda.api.entities.*;
import net.mythigame.nanobot.NanoBot;

public class Guilds implements Cloneable {
    private Guild guild;
    private String uuid;
    private String lang;

    private String name;
    private int size;
    private boolean isSetup;


    public Guilds() {
    }

    public Guilds(String uuid, String lang, String name, int size, boolean isSetup) {
        this.uuid = uuid;
        this.lang = lang;
        this.isSetup = isSetup;
    }

    public Guild getGuild(String uuid){
        return NanoBot.getJda().getGuildById(uuid);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getName() {
        return getGuild(uuid).getName();
    }

    public int getSize() {
        return getGuild(uuid).getMemberCount();
    }

    public boolean isSetup(){
        return isSetup;
    }

    public void setSetup(boolean isSetup){
        this.isSetup = isSetup;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Guilds)) {
            return false;
        } else {
            return ((Guilds) o).getUuid().equals(this.uuid);
        }
    }

    public Guilds clone() {
        try {
            return (Guilds) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void update() {
        new GuildProvider(guild).updateRedisGuild(this);
    }

    public void update(String uuid) {
        new GuildProvider(uuid).updateRedisGuild(this);
    }

    public void removeFromRedis() {
        new GuildProvider(guild).removeFromRedis();
    }

    public void removeFromRedis(String uuid) {
        new GuildProvider(uuid).removeFromRedis();
    }

    public void sendToMySQL() {
        new GuildProvider(guild).updateMySQLGuild(this);
    }

    public void sendToMySQL(String uuid) {
        new GuildProvider(uuid).updateMySQLGuild(this);
    }

    public Guilds getGuild(Guild guild) {
        return new GuildProvider(guild).getGuild();
    }

}
