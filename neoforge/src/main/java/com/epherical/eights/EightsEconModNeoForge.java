package com.epherical.eights;

import com.epherical.eights.commands.NeoForgeBalanceCommand;
import com.epherical.eights.permissions.CommonPermissionBridge;
import com.epherical.octoecon.api.OctoEconomy;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("eights_economy_p")
public class EightsEconModNeoForge extends EightsEconMod {
    private static final Logger LOGGER = LoggerFactory.getLogger(EightsEconModNeoForge.class);

    private final EightsNeoForgeConfig neoForgeConfig = new EightsNeoForgeConfig();
    private OctoEconomy<?, ?> economy;
    private int time = 0;

    @SuppressWarnings("removal")
    public EightsEconModNeoForge() {
        CommonPermissionBridge.setChecker(PermissionsNeoForge::has);
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, neoForgeConfig.getConfigSpec());
        NeoForge.EVENT_BUS.addListener(PermissionsNeoForge::registerPermissions);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        NeoForgeBalanceCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void serverStarting(ServerStartingEvent event) {
        neoForgeConfig.apply();
        economy = EconomyBootstrap.createEconomy(this, event.getServer());
        NeoForgeBalanceCommand.applyProvider(economy);
    }

    @SubscribeEvent
    public void serverStopping(ServerStoppingEvent event) {
        if (economy != null) {
            economy.close();
        }
    }

    @SubscribeEvent
    public void endTickEvent(ServerTickEvent.Post event) {
        if (economy != null) {
            if (time % 1200 == 0 && !ConfigConstants.getInstance().useSaveThread) {
                LOGGER.debug("Saving online players on main thread.");
                economy.savePlayers();
                time = 0;
            } else {
                time++;
            }
        }
    }

    @SubscribeEvent
    public void playerJoinEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (economy != null) {
            economy.onPlayerJoin(event.getEntity().getUUID());
        }
    }

}
