package com.epherical.eights;

import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.user.FakeUser;
import com.epherical.octoecon.api.user.UniqueUser;
import com.epherical.octoecon.api.user.User;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class EightsEconomyProvider implements Economy {


    @Override
    public boolean enabled() {
        return false;
    }

    @Override
    public Collection<Currency> getCurrencies() {
        return null;
    }

    @Override
    public Currency getDefaultCurrency() {
        return null;
    }

    @Override
    public @Nullable Currency getCurrency(ResourceLocation identifier) {
        return null;
    }

    @Override
    public @Nullable FakeUser getOrCreateAccount(ResourceLocation identifier) {
        return null;
    }

    @Override
    public @Nullable UniqueUser getOrCreatePlayerAccount(UUID identifier) {
        return null;
    }

    @Override
    public Collection<UniqueUser> getUniqueUsers() {
        return null;
    }

    @Override
    public Collection<User> getAllUsers() {
        return null;
    }

    @Override
    public Collection<FakeUser> getFakeUsers() {
        return null;
    }

    @Override
    public boolean hasAccount(UUID identifier) {
        return false;
    }

    @Override
    public boolean hasAccount(ResourceLocation identifier) {
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
}
