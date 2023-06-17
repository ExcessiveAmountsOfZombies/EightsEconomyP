package com.epherical.eights;

import com.epherical.eights.commands.ForgeBalanceCommand;
import com.epherical.octoecon.api.event.CurrencyAddEvent;
import com.epherical.octoecon.api.event.EconomyChangeEvent;
import com.epherical.octoecon.api.user.UniqueUser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.UUID;

@Mod("eights_economy_p")
public class EightsEconModForge extends EightsEconMod {
    private static final Logger LOGGER = LoggerFactory.getLogger(EightsEconModForge.class);

    private EightsEconomyProvider provider;
    private EightsForgeConfig config;

    private int time = 0;

    public EightsEconModForge() {
        config = new EightsForgeConfig();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EightsPermissions());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config.getConfigSpec());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(config::initConfig);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        ForgeBalanceCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void serverStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        CurrencyAddEvent addEvent = new CurrencyAddEvent(new ArrayList<>());
        MinecraftForge.EVENT_BUS.post(addEvent);
        provider = new EightsEconomyProvider(this, server.getWorldPath(LevelResource.ROOT), addEvent.getCurrencyList());
        provider.setServer(server);
        MinecraftForge.EVENT_BUS.post(new EconomyChangeEvent(provider));
        ForgeBalanceCommand.applyProviders(provider, provider.getData());
    }

    @SubscribeEvent
    public void serverStopping(ServerStoppingEvent event) {
        provider.close();
    }

    @SubscribeEvent
    public void endTickEvnet(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (time % 1200 == 0 && !ConfigConstants.getInstance().useSaveThread) {
                LOGGER.debug("saving online players on main thread.");
                provider.savePlayers();
                time = 0;
            } else {
                time++;
            }
        }
    }

    @SubscribeEvent
    public void playerJoinEvent(PlayerEvent.PlayerLoggedInEvent event) {
        UUID uuid = event.getEntity().getUUID();
        UniqueUser user = provider.getOrCreatePlayerAccount(uuid);
        if (user != null) {
            provider.cachePlayer(user);
        }
    }

    @SubscribeEvent
    public void playerLeaveEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        provider.removePlayer(event.getEntity().getUUID());
    }


}
