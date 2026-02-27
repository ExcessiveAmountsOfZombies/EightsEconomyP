package com.epherical.octoecon.api;

import com.epherical.octoecon.api.user.FakeUser;
import com.epherical.octoecon.api.user.UniqueUser;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface OctoEconomy<R extends UniqueUser, F extends FakeUser> extends Economy<R, F> {

    void setServer(@Nullable MinecraftServer server);

    void onPlayerJoin(UUID uuid);

    void onPlayerLeave(UUID uuid);

    void savePlayers();

    void close();
}
