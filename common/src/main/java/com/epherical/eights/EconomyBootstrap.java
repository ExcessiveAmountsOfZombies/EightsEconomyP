package com.epherical.eights;

import com.epherical.octoecon.api.OctoEconomy;
import com.epherical.octoecon.api.event.EconomyEvents;
import com.epherical.octoecon.api.event.EconomyProviderEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

public class EconomyBootstrap {

    public static OctoEconomy<?, ?> createEconomy(EightsEconMod mod, MinecraftServer server) {
        EconomyProviderEvent.Pre preEvent = new EconomyProviderEvent.Pre(mod, server.getWorldPath(LevelResource.ROOT));
        EconomyEvents.fireEconomyProviderPre(preEvent);

        OctoEconomy<?, ?> economy = preEvent.getEconomy();
        if (economy == null) {
            economy = new EightsEconomyProvider(preEvent.getWorldDirectory());
        }

        economy = EconomyEvents.finalizeEconomy(economy);
        economy.setServer(server);
        return economy;
    }
}
