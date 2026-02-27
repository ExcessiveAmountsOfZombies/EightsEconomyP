package com.epherical.octoecon.api.event;

import com.epherical.octoecon.api.OctoEconomy;
import com.epherical.octoecon.api.user.FakeUser;
import com.epherical.octoecon.api.user.UniqueUser;

public class EconomyEvents {

    public static void fireEconomyProviderPre(EconomyProviderEvent.Pre event) {
        EconomyEventDispatchers.DISPATCHER.fireEconomyProviderPre(event);
    }

    public static void fireEconomyProviderPost(EconomyProviderEvent.Post event) {
        EconomyEventDispatchers.DISPATCHER.fireEconomyProviderPost(event);
    }

    public static void fireDataLoaderPre(DataLoaderEvent.Pre event) {
        EconomyEventDispatchers.DISPATCHER.fireDataLoaderPre(event);
    }

    public static void fireDataLoaderPost(DataLoaderEvent.Post event) {
        EconomyEventDispatchers.DISPATCHER.fireDataLoaderPost(event);
    }

    public static OctoEconomy<? extends UniqueUser, ? extends FakeUser> finalizeEconomy(OctoEconomy<? extends UniqueUser, ? extends FakeUser> economy) {
        EconomyProviderEvent.Post post = new EconomyProviderEvent.Post(economy);
        fireEconomyProviderPost(post);
        return post.getEconomy();
    }
}
