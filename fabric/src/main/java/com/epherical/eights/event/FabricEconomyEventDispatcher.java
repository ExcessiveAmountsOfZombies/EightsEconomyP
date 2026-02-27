package com.epherical.eights.event;

import com.epherical.octoecon.api.event.DataLoaderEvent;
import com.epherical.octoecon.api.event.EconomyEventDispatcher;
import com.epherical.octoecon.api.event.EconomyProviderEvent;

public class FabricEconomyEventDispatcher implements EconomyEventDispatcher {

    @Override
    public void fireEconomyProviderPre(EconomyProviderEvent.Pre event) {
        FabricEconomyEvents.ECONOMY_PROVIDER_PRE.invoker().onEconomyProviderPre(event);
    }

    @Override
    public void fireEconomyProviderPost(EconomyProviderEvent.Post event) {
        FabricEconomyEvents.ECONOMY_PROVIDER_POST.invoker().onEconomyProviderPost(event);
    }

    @Override
    public void fireDataLoaderPre(DataLoaderEvent.Pre event) {
        FabricEconomyEvents.DATA_LOADER_PRE.invoker().onDataLoaderPre(event);
    }

    @Override
    public void fireDataLoaderPost(DataLoaderEvent.Post event) {
        FabricEconomyEvents.DATA_LOADER_POST.invoker().onDataLoaderPost(event);
    }
}
