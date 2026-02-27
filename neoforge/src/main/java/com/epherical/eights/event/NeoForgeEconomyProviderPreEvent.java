package com.epherical.eights.event;

import com.epherical.eights.EightsEconMod;
import com.epherical.octoecon.api.OctoEconomy;
import com.epherical.octoecon.api.event.EconomyProviderEvent;
import com.epherical.octoecon.api.user.FakeUser;
import com.epherical.octoecon.api.user.UniqueUser;
import net.neoforged.bus.api.Event;

import java.nio.file.Path;

public class NeoForgeEconomyProviderPreEvent extends Event {

    private final EconomyProviderEvent.Pre apiEvent;

    public NeoForgeEconomyProviderPreEvent(EconomyProviderEvent.Pre apiEvent) {
        this.apiEvent = apiEvent;
    }

    public EightsEconMod getMod() {
        return apiEvent.getMod();
    }

    public Path getWorldDirectory() {
        return apiEvent.getWorldDirectory();
    }

    public OctoEconomy<? extends UniqueUser, ? extends FakeUser> getEconomy() {
        return apiEvent.getEconomy();
    }

    public void setEconomy(OctoEconomy<? extends UniqueUser, ? extends FakeUser> economy) {
        apiEvent.setEconomy(economy);
    }

    public EconomyProviderEvent.Pre getApiEvent() {
        return apiEvent;
    }
}
