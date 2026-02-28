package com.epherical.eights;

import com.epherical.eights.data.EconomyData;
import com.epherical.eights.data.EconomyDataCodec;
import com.epherical.eights.exception.EconomyException;
import com.epherical.eights.user.NPCUser;
import com.epherical.eights.user.PlayerUser;
import com.epherical.octoecon.api.OctoEconomy;
import com.epherical.octoecon.api.event.DataLoaderEvent;
import com.epherical.octoecon.api.event.EconomyEvents;
import com.epherical.octoecon.api.user.FakeUser;
import com.epherical.octoecon.api.user.UniqueUser;
import com.epherical.octoecon.api.user.User;
import com.mojang.authlib.GameProfile;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EightsEconomyProvider implements OctoEconomy<PlayerUser, FakeUser> {

    private final Map<UUID, PlayerUser> players = new HashMap<>();
    private final Map<String, PlayerUser> usersByIdentity = new HashMap<>();
    private final Map<ResourceLocation, FakeUser> fakeUsers = new HashMap<>();
    private final EconomyData data;
    @Nullable
    private MinecraftServer server;

    private boolean enabled = true;

    public EightsEconomyProvider(Path worldDirectory) {
        DataLoaderEvent.Pre pre = new DataLoaderEvent.Pre(this, worldDirectory);
        EconomyEvents.fireDataLoaderPre(pre);
        EconomyData resolvedData = pre.getDataLoader();
        if (resolvedData == null) {
            resolvedData = new EconomyDataCodec(this, worldDirectory);
        }
        this.data = resolvedData;
        EconomyEvents.fireDataLoaderPost(new DataLoaderEvent.Post(this, this.data));
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public FakeUser getOrCreateAccount(ResourceLocation identifier) {
        FakeUser existing = fakeUsers.get(identifier);
        if (existing != null) {
            return existing;
        }

        FakeUser loaded = null;
        try {
            loaded = data.loadUser(identifier);
        } catch (IOException ignored) {
        }

        if (loaded == null) {
            loaded = new NPCUser(identifier, createInitialBalance());
            try {
                data.saveUser((NPCUser) loaded);
            } catch (EconomyException ignored) {
            }
        }

        cacheNPC(loaded);
        return loaded;
    }

    @Override
    public PlayerUser getOrCreatePlayerAccount(UUID identifier) {
        PlayerUser existing = players.get(identifier);
        if (existing != null) {
            return existing;
        }

        PlayerUser loaded = null;
        try {
            loaded = data.loadUser(identifier);
        } catch (IOException ignored) {
        }

        if (loaded == null) {
            loaded = createPlayerAccount(identifier);
            try {
                data.saveUser(loaded);
            } catch (EconomyException ignored) {
            }
        }

        cachePlayer(loaded);
        return loaded;
    }

    private PlayerUser createPlayerAccount(UUID identifier) {
        if (server != null && server.getProfileCache() != null) {
            Optional<GameProfile> profile = server.getProfileCache().get(identifier);
            if (profile.isPresent()) {
                return new PlayerUser(identifier, profile.get().getName(), createInitialBalance());
            }
        }

        if (identifier.equals(Util.NIL_UUID)) {
            return new PlayerUser(identifier, "admin", createInitialBalance());
        }

        return new PlayerUser(identifier, identifier.toString(), createInitialBalance());
    }

    @Override
    public @Nullable PlayerUser getPlayerAccountByName(String name) {
        return usersByIdentity.get(normalizeIdentity(name));
    }

    @Override
    public Collection<PlayerUser> getUniqueUsers() {
        return players.values();
    }

    @Override
    public Collection<User> getAllUsers() {
        return Stream.concat(getFakeUsers().stream(), getUniqueUsers().stream()).collect(Collectors.toList());
    }

    @Override
    public Collection<FakeUser> getFakeUsers() {
        return fakeUsers.values();
    }

    @Override
    public boolean hasAccount(UUID identifier) {
        try {
            return data.userExists(identifier);
        } catch (EconomyException ignored) {
            return false;
        }
    }

    @Override
    public boolean hasAccount(ResourceLocation identifier) {
        try {
            return data.userExists(identifier);
        } catch (EconomyException ignored) {
            return false;
        }
    }

    @Override
    public boolean deleteAccount(UUID identifier) {
        PlayerUser removed = players.remove(identifier);
        if (removed != null) {
            usersByIdentity.remove(normalizeIdentity(removed.getIdentity()));
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteAccount(ResourceLocation identifier) {
        return fakeUsers.remove(identifier) != null;
    }

    public double createInitialBalance() {
        return ConfigConstants.getInstance().providedMoneyOnFirstLogin;
    }

    public void cachePlayer(PlayerUser user) {
        players.put(user.getUserID(), user);
        usersByIdentity.values().removeIf(existing -> existing.getUserID().equals(user.getUserID()));
        usersByIdentity.put(normalizeIdentity(user.getIdentity()), user);
    }

    public void cacheNPC(FakeUser user) {
        fakeUsers.put(user.getResourceLocation(), user);
    }

    public Map<String, PlayerUser> getUsersByIdentity() {
        return usersByIdentity;
    }

    public List<PlayerUser> getTopPlayersByBalance() {
        return usersByIdentity.values().stream()
                .distinct()
                .sorted((a, b) -> Double.compare(b.getBalance("command.baltop.sort"), a.getBalance("command.baltop.sort")))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void preloadAllPlayers() {
        try {
            for (PlayerUser user : data.loadAllPlayerUsers()) {
                cachePlayer(user);
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public void onPlayerJoin(UUID uuid) {
        PlayerUser user = getOrCreatePlayerAccount(uuid);
        if (server != null) {
            var serverPlayer = server.getPlayerList().getPlayer(uuid);
            if (serverPlayer != null && !serverPlayer.getGameProfile().getName().equals(user.getIdentity())) {
                user.setIdentifier(serverPlayer.getGameProfile().getName());
                user.setDirty(true);
            }
        }
        cachePlayer(user);
    }

    @Override
    public void onPlayerLeave(UUID uuid) {
        // Intentionally unused; users remain cached for baltop and global lookups.
    }

    @Override
    public void close() {
        data.close();
    }

    @Override
    public void savePlayers() {
        data.savePlayers();
    }

    @Override
    public void setServer(@Nullable MinecraftServer server) {
        this.server = server;
        if (server != null) {
            preloadAllPlayers();
        }
    }

    public EconomyData getData() {
        return data;
    }

    private static String normalizeIdentity(String identity) {
        return identity.toLowerCase(java.util.Locale.ROOT);
    }
}
