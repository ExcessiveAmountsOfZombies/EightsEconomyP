package com.epherical.eights.data;

import com.epherical.eights.EightsEconomyProvider;
import com.epherical.eights.exception.EconomyException;
import com.epherical.eights.user.NPCUser;
import com.epherical.eights.user.PlayerUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EconomyDataCodec extends EconomyData {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path userFolder;

    public EconomyDataCodec(EightsEconomyProvider provider, Path dataFolder) {
        super(provider);
        this.userFolder = dataFolder.resolve("eights_economy_p");
        try {
            Files.createDirectories(userFolder);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize economy data folder", e);
        }
    }

    @Override
    public PlayerUser loadUser(UUID uuid) throws IOException {
        Path path = userFolder.resolve(uuid + ".json");
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            return parsePlayer(element, path);
        }
    }

    @Override
    public NPCUser loadUser(ResourceLocation name) throws IOException {
        Path path = userFolder.resolve(name.toString().replace(":", "/") + ".json");
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            return parseNpc(element, path);
        }
    }

    @Override
    public List<PlayerUser> loadAllPlayerUsers() throws IOException {
        List<PlayerUser> players = new ArrayList<>();
        if (!Files.exists(userFolder)) {
            return players;
        }

        try (var stream = Files.walk(userFolder)) {
            for (Path path : stream.filter(Files::isRegularFile).toList()) {
                if (!path.getFileName().toString().endsWith(".json")) {
                    continue;
                }

                String stem = path.getFileName().toString().substring(0, path.getFileName().toString().length() - 5);
                try {
                    UUID uuid = UUID.fromString(stem);
                    players.add(loadUser(uuid));
                } catch (IllegalArgumentException ignored) {
                    // Not a player file.
                } catch (IOException ignored) {
                    // Skip malformed player entry.
                }
            }
        }

        return players;
    }

    @Override
    public boolean userExists(ResourceLocation name) {
        return Files.exists(userFolder.resolve(name.toString().replace(":", "/") + ".json"));
    }

    @Override
    public boolean userExists(UUID uuid) {
        return Files.exists(userFolder.resolve(uuid + ".json"));
    }

    @Override
    public boolean userExists(String name, boolean player) {
        return Files.exists(userFolder.resolve(name));
    }

    @Override
    public boolean saveUser(PlayerUser user, boolean setBalance) throws EconomyException {
        Path path = userFolder.resolve(user.getUserID() + ".json");
        try {
            write(path, encodePlayer(user, path));
            user.setDirty(false);
            return true;
        } catch (IOException e) {
            throw new EconomyException("Could not save user " + user.getIdentity() + " " + user.getUserID());
        }
    }

    @Override
    public boolean saveUser(NPCUser user, boolean setBalance) throws EconomyException {
        Path path = userFolder.resolve(user.getIdentity().replace(":", "/") + ".json");
        try {
            write(path, encodeNpc(user, path));
            user.setDirty(false);
            return true;
        } catch (IOException e) {
            throw new EconomyException("Could not save user " + user.getIdentity());
        }
    }

    private void write(Path path, JsonElement element) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            GSON.toJson(element, writer);
        }
    }

    private PlayerUser parsePlayer(JsonElement element, Path path) throws IOException {
        DataResult<PlayerUser> result = PlayerUser.CODEC.parse(JsonOps.INSTANCE, element);
        return result.result().orElseThrow(() -> new IOException("Failed to decode player data: " + path + " | " + errorMessage(result)));
    }

    private NPCUser parseNpc(JsonElement element, Path path) throws IOException {
        DataResult<NPCUser> result = NPCUser.CODEC.parse(JsonOps.INSTANCE, element);
        return result.result().orElseThrow(() -> new IOException("Failed to decode npc data: " + path + " | " + errorMessage(result)));
    }

    private JsonElement encodePlayer(PlayerUser user, Path path) throws IOException {
        DataResult<JsonElement> result = PlayerUser.CODEC.encodeStart(JsonOps.INSTANCE, user);
        return result.result().orElseThrow(() -> new IOException("Failed to encode player data: " + path + " | " + errorMessage(result)));
    }

    private JsonElement encodeNpc(NPCUser user, Path path) throws IOException {
        DataResult<JsonElement> result = NPCUser.CODEC.encodeStart(JsonOps.INSTANCE, user);
        return result.result().orElseThrow(() -> new IOException("Failed to encode npc data: " + path + " | " + errorMessage(result)));
    }

    private static String errorMessage(DataResult<?> result) {
        return result.error().map(DataResult.Error::message).orElse("Unknown codec error");
    }
}
