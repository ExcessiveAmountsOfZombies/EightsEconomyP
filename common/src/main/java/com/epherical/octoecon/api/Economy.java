package com.epherical.octoecon.api;

import com.epherical.octoecon.api.user.FakeUser;
import com.epherical.octoecon.api.user.UniqueUser;
import com.epherical.octoecon.api.user.User;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface Economy<R extends UniqueUser, F extends FakeUser> {

    boolean enabled();

    F getOrCreateAccount(ResourceLocation identifier);

    R getOrCreatePlayerAccount(UUID identifier);

    @Nullable R getPlayerAccountByName(String name);

    Collection<R> getUniqueUsers();

    Collection<User> getAllUsers();

    Collection<F> getFakeUsers();

    boolean hasAccount(UUID identifier);

    boolean hasAccount(ResourceLocation identifier);

    boolean deleteAccount(UUID identifier);

    boolean deleteAccount(ResourceLocation identifier);
}
