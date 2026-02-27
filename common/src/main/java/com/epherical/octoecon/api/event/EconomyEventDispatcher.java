package com.epherical.octoecon.api.event;

public interface EconomyEventDispatcher {

    void fireEconomyProviderPre(EconomyProviderEvent.Pre event);

    void fireEconomyProviderPost(EconomyProviderEvent.Post event);

    void fireDataLoaderPre(DataLoaderEvent.Pre event);

    void fireDataLoaderPost(DataLoaderEvent.Post event);
}
