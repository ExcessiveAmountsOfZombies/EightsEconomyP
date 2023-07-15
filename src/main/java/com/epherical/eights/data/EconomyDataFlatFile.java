package com.epherical.eights.data;

import com.epherical.eights.EightsEconomyProvider;
import com.epherical.eights.currency.BasicCurrency;
import com.epherical.eights.data.serializer.CurrencySerializer;
import com.epherical.eights.data.serializer.NPCSerializer;
import com.epherical.eights.data.serializer.PlayerUserSerializer;
import com.epherical.eights.exception.EconomyException;
import com.epherical.eights.user.NPCUser;
import com.epherical.eights.user.PlayerUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class EconomyDataFlatFile extends EconomyData {

    private final Path userFolder;

    private final Gson gson;

    public EconomyDataFlatFile(EightsEconomyProvider provider, Path dataFolder) {
        super(provider);
        this.userFolder = dataFolder.resolve("eights_economy");
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(PlayerUser.class, new PlayerUserSerializer())
                .registerTypeAdapter(BasicCurrency.class, new CurrencySerializer())
                .registerTypeAdapter(NPCUser.class, new NPCSerializer())
                .create();
        try {
            Files.createDirectories(userFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlayerUser loadUser(UUID uuid) throws IOException, NullPointerException {
        Path path = userFolder.resolve(uuid.toString() + ".json");
        File file = path.toFile();
        try (FileReader reader = new FileReader(file)) {
            PlayerUser user = gson.fromJson(reader, PlayerUser.class);
            if (user == null) {
                //Files.deleteIfExists(path);
                throw new NullPointerException("PlayerUser was loaded, but the file was malformed");
            }
            reader.close();
            return user;
        }
    }

    @Override
    public NPCUser loadUser(ResourceLocation name) throws IOException {
        File file = new File(userFolder.resolve(name.toString().replaceAll(":", "/")).toFile() + ".json");
        try (FileReader reader = new FileReader(file)) {
            NPCUser user = gson.fromJson(reader, NPCUser.class);
            reader.close();
            return user;
        }
    }

    @Override
    public boolean userExists(ResourceLocation name) throws EconomyException {
        return userExists(name.toString().replaceAll(":", "/") + ".json", false);
    }

    @Override
    public boolean userExists(UUID uuid) throws EconomyException {
        return userExists(uuid.toString() + ".json", true);
    }

    @Override
    public boolean userExists(String name, boolean player) throws EconomyException {
        return Files.exists(userFolder.resolve(name));
    }

    @Override
    public boolean saveUser(PlayerUser user, boolean setBalance) throws EconomyException {
        File file = new File(userFolder.resolve(user.getUserID().toString()).toFile() + ".json");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(gson.toJson(user, PlayerUser.class));
            user.setDirty(false);
        } catch (IOException e) {
            throw new EconomyException("Could not save user " + user.getIdentity() + " " + user.getUserID());
        }
        return true;
    }

    @Override
    public boolean saveUser(NPCUser user, boolean setBalance) throws EconomyException {
        File file = new File(userFolder.resolve(user.getIdentity().replaceAll(":", "/")).toFile() + ".json");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(gson.toJson(user, NPCUser.class));
            user.setDirty(false);
        } catch (IOException e) {
            throw new EconomyException("Could not save user " + user.getIdentity());
        }
        return true;
    }
}
