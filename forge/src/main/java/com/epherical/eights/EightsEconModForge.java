package com.epherical.eights;

import com.epherical.eights.commands.ForgeBalanceCommand;
import com.epherical.eights.permissions.CommonPermissionBridge;
import com.epherical.octoecon.api.OctoEconomy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("eights_economy_p")
public class EightsEconModForge extends EightsEconMod {
    private static final Logger LOGGER = LoggerFactory.getLogger(EightsEconModForge.class);

    private final EightsForgeConfig forgeConfig = new EightsForgeConfig();
    private OctoEconomy<?, ?> economy;
    private int time = 0;

    public EightsEconModForge() {
        CommonPermissionBridge.setChecker(PermissionsForge::has);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, forgeConfig.getConfigSpec());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigReload);
        MinecraftForge.EVENT_BUS.addListener(PermissionsForge::registerPermissions);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onConfigReload(ModConfigEvent event) {
        forgeConfig.initConfig(event);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        ForgeBalanceCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void serverStarting(ServerStartingEvent event) {
        economy = EconomyBootstrap.createEconomy(this, event.getServer());
        ForgeBalanceCommand.applyProvider(economy);
    }

    @SubscribeEvent
    public void serverStopping(ServerStoppingEvent event) {
        if (economy != null) {
            economy.close();
        }
    }

    @SubscribeEvent
    public void endTickEvent(TickEvent.ServerTickEvent event) {
        if (economy != null && event.phase == TickEvent.Phase.END) {
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
