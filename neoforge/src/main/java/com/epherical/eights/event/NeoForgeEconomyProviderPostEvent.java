package com.epherical.eights.event;

import com.epherical.octoecon.api.OctoEconomy;
import com.epherical.octoecon.api.event.EconomyProviderEvent;
import com.epherical.octoecon.api.user.FakeUser;
import com.epherical.octoecon.api.user.UniqueUser;
import net.neoforged.bus.api.Event;

public class NeoForgeEconomyProviderPostEvent extends Event {

    private final EconomyProviderEvent.Post apiEvent;

    public NeoForgeEconomyProviderPostEvent(EconomyProviderEvent.Post apiEvent) {
        this.apiEvent = apiEvent;
    }

    public OctoEconomy<? extends UniqueUser, ? extends FakeUser> getEconomy() {
        return apiEvent.getEconomy();
    }

    public EconomyProviderEvent.Post getApiEvent() {
        return apiEvent;
    }
}
