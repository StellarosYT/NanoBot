package net.mythigame.nanobot.utils;

import net.mythigame.nanobot.json.JSONReader;
import net.mythigame.nanobot.json.JSONWriter;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class Configuration {

    private final JSONObject object;
    private final File file;

    public Configuration(String path) throws IOException {

        this.file = new File(path);
        if(file.exists())
            this.object = new JSONReader(file).toJSONObject();
        else
            object = new JSONObject();
    }

    public String getString(String key, String defaultValue){
        if(!object.has(key))
            object.put(key, defaultValue);
        return object.getString(key);
    }

    public void save(){
        try (JSONWriter writer = new JSONWriter(file)) {
            writer.write(this.object);
            writer.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
