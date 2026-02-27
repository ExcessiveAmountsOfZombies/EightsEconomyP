package com.epherical.eights;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;


public class EightsFabricConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path path;

    public EightsFabricConfig(String modID) {
        path = FabricLoader.getInstance().getConfigDir().resolve(modID).resolve("options.json");
        loadConfig();
    }

    public ConfigConstants loadConfig() {
        ConfigConstants settings = null;

        if (Files.exists(path)) {
            try {
                String json = new String(Files.readAllBytes(path));
                settings = GSON.fromJson(json, ConfigConstants.class);
                saveFile(settings);
                return settings;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            settings = new ConfigConstants();
        }

        try {
            saveFile(settings);
            return settings;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return settings;
    }

    public void saveFile(ConfigConstants settings) throws IOException {
        Files.writeString(path, GSON.toJson(settings), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
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
}

