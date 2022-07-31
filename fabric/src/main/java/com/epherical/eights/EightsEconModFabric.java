package com.epherical.eights;

import com.epherical.eights.commands.BalanceCommand;
import com.epherical.octoecon.api.event.EconomyEvents;
import com.epherical.octoecon.api.user.UniqueUser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class EightsEconModFabric extends EightsEconMod implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EightsEconModFabric.class);

    private EightsEconomyProvider provider;

    public static final EightsEconFabricConfig CONFIG = new EightsEconFabricConfig("eights_economy_p");

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            BalanceCommand.register(dispatcher);
        });

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            provider = new EightsEconomyProvider(this, server.getWorldPath(LevelResource.ROOT));
            provider.setServer(server);
            EconomyEvents.ECONOMY_CHANGE_EVENT.invoker().onEconomyChanged(provider);
            BalanceCommand.applyProviders(provider, provider.getData());
            registerListeners();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            provider.close();
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 1200 == 0 && !ConfigConstants.useSaveThread) {
                LOGGER.debug("saving online players on main thread.");
                provider.savePlayers();
            }
        });

    }

    private void registerListeners() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            UUID uuid = handler.getPlayer().getUUID();
            UniqueUser user = provider.getOrCreatePlayerAccount(uuid);
            if (user != null) {
                provider.cachePlayer(user);
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            provider.removePlayer(handler.getPlayer().getUUID());
        });
    }
}
