package com.epherical.eights.event;

import com.epherical.octoecon.api.event.DataLoaderEvent;
import com.epherical.octoecon.api.event.EconomyEventDispatcher;
import com.epherical.octoecon.api.event.EconomyProviderEvent;
import net.minecraftforge.common.MinecraftForge;

public class ForgeEconomyEventDispatcher implements EconomyEventDispatcher {

    @Override
    public void fireEconomyProviderPre(EconomyProviderEvent.Pre event) {
        MinecraftForge.EVENT_BUS.post(new ForgeEconomyProviderPreEvent(event));
    }

    @Override
    public void fireEconomyProviderPost(EconomyProviderEvent.Post event) {
        MinecraftForge.EVENT_BUS.post(new ForgeEconomyProviderPostEvent(event));
    }

    @Override
    public void fireDataLoaderPre(DataLoaderEvent.Pre event) {
        MinecraftForge.EVENT_BUS.post(new ForgeDataLoaderPreEvent(event));
    }

    @Override
    public void fireDataLoaderPost(DataLoaderEvent.Post event) {
        MinecraftForge.EVENT_BUS.post(new ForgeDataLoaderPostEvent(event));
    }
}
