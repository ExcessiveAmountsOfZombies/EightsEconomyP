package com.epherical.eights.event;

import com.epherical.octoecon.api.event.DataLoaderEvent;
import com.epherical.octoecon.api.event.EconomyEventDispatcher;
import com.epherical.octoecon.api.event.EconomyProviderEvent;
import net.neoforged.neoforge.common.NeoForge;

public class NeoForgeEconomyEventDispatcher implements EconomyEventDispatcher {

    @Override
    public void fireEconomyProviderPre(EconomyProviderEvent.Pre event) {
        NeoForge.EVENT_BUS.post(new NeoForgeEconomyProviderPreEvent(event));
    }

    @Override
    public void fireEconomyProviderPost(EconomyProviderEvent.Post event) {
        NeoForge.EVENT_BUS.post(new NeoForgeEconomyProviderPostEvent(event));
    }

    @Override
    public void fireDataLoaderPre(DataLoaderEvent.Pre event) {
        NeoForge.EVENT_BUS.post(new NeoForgeDataLoaderPreEvent(event));
    }

    @Override
    public void fireDataLoaderPost(DataLoaderEvent.Post event) {
        NeoForge.EVENT_BUS.post(new NeoForgeDataLoaderPostEvent(event));
    }
}
