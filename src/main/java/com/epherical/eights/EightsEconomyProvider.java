package com.epherical.eights;

import com.epherical.eights.currency.BasicCurrency;
import com.epherical.eights.data.EconomyData;
import com.epherical.eights.data.EconomyDataFlatFile;
import com.epherical.eights.exception.EconomyException;
import com.epherical.eights.user.NPCUser;
import com.epherical.eights.user.PlayerUser;
import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.user.FakeUser;
import com.epherical.octoecon.api.user.UniqueUser;
import com.epherical.octoecon.api.user.User;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EightsEconomyProvider implements Economy {

    private final EightsEconMod mod;
    public final Map<ResourceLocation, Currency> currencyMap = Maps.newHashMap();
    public final Map<UUID, UniqueUser> players = Maps.newHashMap();
    public final Map<ResourceLocation, FakeUser> fakeUsers = Maps.newHashMap();
    private final EconomyData data;
    @Nullable
    private MinecraftServer server;

    private final ResourceLocation currencyName = new ResourceLocation("eights_economy", "dollars");

    private static EightsEconomyProvider INSTANCE;

    public EightsEconomyProvider(EightsEconMod mod, Path access, List<Currency> currencyList) {
        this.mod = mod;
        this.data = new EconomyDataFlatFile(this, access);
        INSTANCE = this;
        currencyMap.put(currencyName, new BasicCurrency(currencyName));
        for (Currency currency : currencyList) {
            currencyMap.put(new ResourceLocation(currency.getIdentity()), currency);
        }
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
        return currencyMap.get(currencyName);
    }

    @Override
    public Currency getCurrency(ResourceLocation identifier) {
        return currencyMap.get(identifier);
    }

    @Override
    public FakeUser getOrCreateAccount(ResourceLocation identifier) {
        FakeUser user = fakeUsers.get(identifier);
        if (user == null) {
            try {
                return data.loadUser(identifier);
            } catch (IOException e) {
                user = new NPCUser(identifier, createAccount(Maps.newHashMap()));
                try {
                    data.saveUser(new NPCUser(identifier, createAccount(Maps.newHashMap())));
                } catch (EconomyException economyException) {
                    economyException.printStackTrace();
                }
                return user;
            }
        }
        return user;
    }

    @Override
    public UniqueUser getOrCreatePlayerAccount(UUID identifier) {
        UniqueUser user = players.get(identifier);
        if (user == null) {
            try {
                PlayerUser user1 = data.loadUser(identifier);
                cachePlayer(user1);
                return user1;
            } catch (IOException e) {
                if (server != null) {
                    Optional<GameProfile> profile = server.getProfileCache().get(identifier);
                    GameProfile gameProfile = profile.orElse(null);
                    if (gameProfile != null) {
                        user = new PlayerUser(identifier, gameProfile.getName(), createAccount(Maps.newHashMap()));
                        try {
                            data.saveUser((PlayerUser) user);
                        } catch (EconomyException economyException) {
                            economyException.printStackTrace();
                        }
                        return user;
                    } else if (identifier.equals(Util.NIL_UUID)) {
                        user = new PlayerUser(identifier, "admin", createAccount(Maps.newHashMap()));
                        try {
                            data.saveUser((PlayerUser) user);
                        } catch (EconomyException economyException) {
                            economyException.printStackTrace();
                        }
                        return user;
                    }
                }
            }
        }
        return user;
    }

    @Override
    public @Nullable UniqueUser getPlayerAccountByName(String s) {
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
        } catch (EconomyException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean hasAccount(ResourceLocation identifier) {
        try {
            return data.userExists(identifier);
        } catch (EconomyException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteAccount(UUID identifier) {
        return false;
    }

    @Override
    public boolean deleteAccount(ResourceLocation identifier) {
        return false;
    }

    public Map<Currency, Double> createAccount(Map<Currency, Double> map) {
        for (Currency currency : currencyMap.values()) {
            map.put(currency, 0.0d);
        }
        return map;
    }

    public void cachePlayer(UniqueUser user) {
        players.put(user.getUserID(), user);
    }

    public void cacheNPC(FakeUser user) {
        fakeUsers.put(user.getResourceLocation(), user);
    }

    public void removePlayer(UUID user) {
        UniqueUser uniqueUser = players.remove(user);
        try {
            data.saveUser((PlayerUser) uniqueUser);
        } catch (EconomyException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        data.close();
    }

    public void savePlayers() {
        data.savePlayers();
    }

    public static EightsEconomyProvider getInstance() {
        return INSTANCE;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public EconomyData getData() {
        return data;
    }
}
