package com.epherical.eights;

import com.epherical.eights.currency.BasicCurrency;
import com.epherical.eights.data.EconomyData;
import com.epherical.eights.data.EconomyDataFlatFile;
import com.epherical.eights.exception.EconomyException;
import com.epherical.eights.user.NPCUser;
import com.epherical.eights.user.PlayerUser;
import com.epherical.octoecon.api.Currency;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EightsEconomyProvider implements OctoEconomy {

    private final Map<ResourceLocation, Currency> currencyMap = new HashMap<>();
    private final Map<UUID, UniqueUser> players = new HashMap<>();
    private final Map<ResourceLocation, FakeUser> fakeUsers = new HashMap<>();
    private final EconomyData data;
    @Nullable
    private MinecraftServer server;

    private final ResourceLocation defaultCurrencyId = ResourceLocation.fromNamespaceAndPath("eights_economy_p", "dollars");

    public EightsEconomyProvider(Path worldDirectory, List<Currency> currencyList) {
        currencyMap.put(defaultCurrencyId, new BasicCurrency(defaultCurrencyId));
        for (Currency currency : currencyList) {
            currencyMap.put(ResourceLocation.parse(currency.getIdentity()), currency);
        }

        DataLoaderEvent.Pre pre = new DataLoaderEvent.Pre(this, worldDirectory);
        EconomyEvents.fireDataLoaderPre(pre);
        EconomyData resolvedData = pre.getDataLoader();
        if (resolvedData == null) {
            resolvedData = new EconomyDataFlatFile(this, worldDirectory);
        }
        this.data = resolvedData;
        EconomyEvents.fireDataLoaderPost(new DataLoaderEvent.Post(this, this.data));
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public Collection<Currency> getCurrencies() {
        return currencyMap.values();
    }

    @Override
    public Currency getDefaultCurrency() {
        return currencyMap.get(defaultCurrencyId);
    }

    @Override
    public @Nullable Currency getCurrency(ResourceLocation identifier) {
        return currencyMap.get(identifier);
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
            loaded = new NPCUser(identifier, createAccount(new HashMap<>()));
            try {
                data.saveUser((NPCUser) loaded);
            } catch (EconomyException ignored) {
            }
        }

        cacheNPC(loaded);
        return loaded;
    }

    @Override
    public UniqueUser getOrCreatePlayerAccount(UUID identifier) {
        UniqueUser existing = players.get(identifier);
        if (existing != null) {
            return existing;
        }

        UniqueUser loaded = null;
        try {
            loaded = data.loadUser(identifier);
        } catch (IOException ignored) {
        }

        if (loaded == null) {
            loaded = createPlayerAccount(identifier);
            try {
                data.saveUser((PlayerUser) loaded);
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
                return new PlayerUser(identifier, profile.get().getName(), createAccount(new HashMap<>()));
            }
        }

        if (identifier.equals(Util.NIL_UUID)) {
            return new PlayerUser(identifier, "admin", createAccount(new HashMap<>()));
        }

        return new PlayerUser(identifier, identifier.toString(), createAccount(new HashMap<>()));
    }

    @Override
    public @Nullable UniqueUser getPlayerAccountByName(String name) {
        for (UniqueUser user : players.values()) {
            if (user.getIdentity().equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public Collection<UniqueUser> getUniqueUsers() {
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
        return players.remove(identifier) != null;
    }

    @Override
    public boolean deleteAccount(ResourceLocation identifier) {
        return fakeUsers.remove(identifier) != null;
    }

    public Map<Currency, Double> createAccount(Map<Currency, Double> map) {
        for (Currency currency : currencyMap.values()) {
            map.put(currency, ConfigConstants.getInstance().providedMoneyOnFirstLogin);
        }
        return map;
    }

    public void cachePlayer(UniqueUser user) {
        players.put(user.getUserID(), user);
    }

    public void cacheNPC(FakeUser user) {
        fakeUsers.put(user.getResourceLocation(), user);
    }

    public void removePlayer(UUID userId) {
        UniqueUser uniqueUser = players.remove(userId);
        if (uniqueUser instanceof PlayerUser playerUser) {
            try {
                data.saveUser(playerUser);
            } catch (EconomyException ignored) {
            }
        }
    }

    @Override
    public void onPlayerJoin(UUID uuid) {
        cachePlayer(getOrCreatePlayerAccount(uuid));
    }

    @Override
    public void onPlayerLeave(UUID uuid) {
        removePlayer(uuid);
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
    }

    public EconomyData getData() {
        return data;
    }
}
