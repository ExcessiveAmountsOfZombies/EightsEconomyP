package com.epherical.octoecon.api.event;

import org.jetbrains.annotations.NotNull;

import java.util.ServiceLoader;

public class EconomyEventDispatchers {

    private static final EconomyEventDispatcher NO_OP = new EconomyEventDispatcher() {
        @Override
        public void fireEconomyProviderPre(EconomyProviderEvent.Pre event) {
        }

        @Override
        public void fireEconomyProviderPost(EconomyProviderEvent.Post event) {
        }

        @Override
        public void fireDataLoaderPre(DataLoaderEvent.Pre event) {
        }

        @Override
        public void fireDataLoaderPost(DataLoaderEvent.Post event) {
        }
    };

    @NotNull
    static final EconomyEventDispatcher DISPATCHER = ServiceLoader.load(EconomyEventDispatcher.class, EconomyEventDispatchers.class.getClassLoader())
            .findFirst()
            .orElse(NO_OP);
}
