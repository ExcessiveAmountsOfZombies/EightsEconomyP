package com.epherical.eights;

import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.OctoEconomy;
import com.epherical.octoecon.api.event.EconomyEvents;
import com.epherical.octoecon.api.event.EconomyProviderEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.util.ArrayList;
import java.util.List;

public final class EconomyBootstrap {

    private EconomyBootstrap() {
    }

    public static OctoEconomy createEconomy(EightsEconMod mod, MinecraftServer server) {
        List<Currency> currencies = new ArrayList<>(EconomyEvents.collectCurrencies());
        EconomyProviderEvent.Pre preEvent = new EconomyProviderEvent.Pre(mod, server.getWorldPath(LevelResource.ROOT), currencies);
        EconomyEvents.fireEconomyProviderPre(preEvent);

        OctoEconomy economy = preEvent.getEconomy();
        if (economy == null) {
            economy = new EightsEconomyProvider(preEvent.getWorldDirectory(), preEvent.getCurrencies());
        }

        economy = EconomyEvents.finalizeEconomy(economy);
        economy.setServer(server);
        return economy;
    }
}
