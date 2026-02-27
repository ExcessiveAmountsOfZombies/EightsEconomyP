package com.epherical.eights.event;

import com.epherical.eights.EightsEconomyProvider;
import com.epherical.eights.data.EconomyData;
import com.epherical.octoecon.api.event.DataLoaderEvent;
import net.neoforged.bus.api.Event;

public class NeoForgeDataLoaderPostEvent extends Event {

    private final DataLoaderEvent.Post apiEvent;

    public NeoForgeDataLoaderPostEvent(DataLoaderEvent.Post apiEvent) {
        this.apiEvent = apiEvent;
    }

    public EightsEconomyProvider getProvider() {
        return apiEvent.getProvider();
    }

    public EconomyData getDataLoader() {
        return apiEvent.getDataLoader();
    }

    public DataLoaderEvent.Post getApiEvent() {
        return apiEvent;
    }
}
