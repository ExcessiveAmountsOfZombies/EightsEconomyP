package com.epherical.eights.event;

import com.epherical.eights.EightsEconomyProvider;
import com.epherical.eights.data.EconomyData;
import com.epherical.octoecon.api.event.DataLoaderEvent;
import net.neoforged.bus.api.Event;

import java.nio.file.Path;

public class NeoForgeDataLoaderPreEvent extends Event {

    private final DataLoaderEvent.Pre apiEvent;

    public NeoForgeDataLoaderPreEvent(DataLoaderEvent.Pre apiEvent) {
        this.apiEvent = apiEvent;
    }

    public EightsEconomyProvider getProvider() {
        return apiEvent.getProvider();
    }

    public Path getWorldDirectory() {
        return apiEvent.getWorldDirectory();
    }

    public EconomyData getDataLoader() {
        return apiEvent.getDataLoader();
    }

    public void setDataLoader(EconomyData dataLoader) {
        apiEvent.setDataLoader(dataLoader);
    }

    public DataLoaderEvent.Pre getApiEvent() {
        return apiEvent;
    }
}
