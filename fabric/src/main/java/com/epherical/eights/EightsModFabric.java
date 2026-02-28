package com.epherical.eights;

import com.epherical.eights.commands.FabricBalanceCommand;
import com.epherical.eights.permissions.CommonPermissionBridge;
import com.epherical.octoecon.api.OctoEconomy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EightsModFabric extends EightsEconMod implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EightsModFabric.class);

    private OctoEconomy<?, ?> economy;

    public static final EightsFabricConfig CONFIG = new EightsFabricConfig("eights_economy_p");

    @Override
    public void onInitialize() {
        CommonPermissionBridge.setChecker(FabricPermissions::has);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> FabricBalanceCommand.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            economy = EconomyBootstrap.createEconomy(this, server);
            FabricBalanceCommand.applyProvider(economy);
            registerListeners();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> economy.close());

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (economy != null && server.getTickCount() % 1200 == 0 && !ConfigConstants.getInstance().useSaveThread) {
                LOGGER.debug("Saving online players on main thread.");
                economy.savePlayers();
            }
        });
    }

    private void registerListeners() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> economy.onPlayerJoin(handler.getPlayer().getUUID()));
    }
}
