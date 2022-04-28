package com.epherical.eights;

import com.epherical.eights.commands.BalanceCommand;
import com.epherical.eights.event.LevelAccessEvent;
import com.epherical.octoecon.api.event.EconomyEvents;
import com.epherical.octoecon.api.user.UniqueUser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class EightsEconMod implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EightsEconMod.class);

    private EightsEconomyProvider provider;

    public static final Style CONSTANTS_STYLE = Style.EMPTY.withColor(TextColor.parseColor("#999999"));
    public static final Style VARIABLE_STYLE = Style.EMPTY.withColor(TextColor.parseColor("#ffd500"));
    public static final Style APPROVAL_STYLE = Style.EMPTY.withColor(TextColor.parseColor("#6ba4ff"));
    public static final Style ERROR_STYLE = Style.EMPTY.withColor(TextColor.parseColor("#b31717"));

    public static final Config CONFIG = new Config("eights_economy_p");

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            BalanceCommand.register(dispatcher);
        });

        LevelAccessEvent.CREATED_EVENT.register(access -> {
            provider = new EightsEconomyProvider(this, access);
            EconomyEvents.ECONOMY_CHANGE_EVENT.invoker().onEconomyChanged(provider);
        });

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            provider.setServer(server);
            registerListeners();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            provider.close();
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 1200 == 0 && !CONFIG.useSaveThread) {
                LOGGER.debug("saving online players on main thread");
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
