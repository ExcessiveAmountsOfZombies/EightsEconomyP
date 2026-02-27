package com.epherical.octoecon.api;

import com.epherical.octoecon.api.user.FakeUser;
import com.epherical.octoecon.api.user.UniqueUser;
import com.epherical.octoecon.api.user.User;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface Economy {

    boolean enabled();

    Collection<Currency> getCurrencies();

    Currency getDefaultCurrency();

    @Nullable Currency getCurrency(ResourceLocation identifier);

    FakeUser getOrCreateAccount(ResourceLocation identifier);

    UniqueUser getOrCreatePlayerAccount(UUID identifier);

    @Nullable UniqueUser getPlayerAccountByName(String name);

    Collection<UniqueUser> getUniqueUsers();

    Collection<User> getAllUsers();

    Collection<FakeUser> getFakeUsers();

    boolean hasAccount(UUID identifier);

    boolean hasAccount(ResourceLocation identifier);

    boolean deleteAccount(UUID identifier);

    boolean deleteAccount(ResourceLocation identifier);
}
