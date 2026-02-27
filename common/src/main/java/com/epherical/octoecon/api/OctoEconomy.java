package com.epherical.octoecon.api;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface OctoEconomy extends Economy {

    void setServer(@Nullable MinecraftServer server);

    void onPlayerJoin(UUID uuid);

    void onPlayerLeave(UUID uuid);

    void savePlayers();

    void close();
}
