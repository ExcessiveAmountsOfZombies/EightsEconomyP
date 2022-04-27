package com.epherical.eights;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

public class Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private File optionsFile;


    public boolean useSaveThread = true;

    public Config(String modID) {
        File configDirectory = new File(FabricLoader.getInstance().getConfigDir().toFile(), modID);

        if (!configDirectory.exists()) {
            configDirectory.mkdirs();
        }

        optionsFile = new File(configDirectory, "options.json");

        if (!optionsFile.exists()) {
            try (Writer writer = new FileWriter(optionsFile)) {
                GSON.toJson(new JsonObject(), writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        checkForDefaults(optionsFile);
        readOptionsFile(optionsFile);
    }

    private void checkForDefaults(File file) {
        try (Reader reader = new FileReader(file)) {
            JsonObject mainObject = GSON.fromJson(reader, JsonObject.class);

            JsonObject options = new JsonObject();
            if (!mainObject.has("options")) {
                options.add("_comment", new JsonPrimitive("Check the github page for information"));
                options.add("use-save-thread", new JsonPrimitive(true));
            } else {
                options = mainObject.getAsJsonObject("options");
            }

            mainObject.add("options", options);

            try (Writer writer = new FileWriter(file)) {
                GSON.toJson(mainObject, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readOptionsFile(File file) {
        try {
            FileReader reader = new FileReader(file);
            JsonObject mainObject = GSON.fromJson(reader, JsonObject.class);
            JsonObject options = mainObject.getAsJsonObject("options");

            useSaveThread = options.getAsJsonPrimitive("use-save-thread").getAsBoolean();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public File getOptionsFile() {
        return optionsFile;
    }
}

