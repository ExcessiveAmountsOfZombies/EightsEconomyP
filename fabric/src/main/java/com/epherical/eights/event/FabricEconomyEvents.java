package com.epherical.eights.event;

import com.epherical.octoecon.api.event.DataLoaderEvent;
import com.epherical.octoecon.api.event.EconomyProviderEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class FabricEconomyEvents {

    @FunctionalInterface
    public interface EconomyProviderPre {
        void onEconomyProviderPre(EconomyProviderEvent.Pre event);
    }

    @FunctionalInterface
    public interface EconomyProviderPost {
        void onEconomyProviderPost(EconomyProviderEvent.Post event);
    }

    @FunctionalInterface
    public interface DataLoaderPre {
        void onDataLoaderPre(DataLoaderEvent.Pre event);
    }

    @FunctionalInterface
    public interface DataLoaderPost {
        void onDataLoaderPost(DataLoaderEvent.Post event);
    }

    public static final Event<EconomyProviderPre> ECONOMY_PROVIDER_PRE = EventFactory.createArrayBacked(EconomyProviderPre.class,
            listeners -> event -> {
                for (EconomyProviderPre listener : listeners) {
                    listener.onEconomyProviderPre(event);
                }
            });

    public static final Event<EconomyProviderPost> ECONOMY_PROVIDER_POST = EventFactory.createArrayBacked(EconomyProviderPost.class,
            listeners -> event -> {
                for (EconomyProviderPost listener : listeners) {
                    listener.onEconomyProviderPost(event);
                }
            });

    public static final Event<DataLoaderPre> DATA_LOADER_PRE = EventFactory.createArrayBacked(DataLoaderPre.class,
            listeners -> event -> {
                for (DataLoaderPre listener : listeners) {
                    listener.onDataLoaderPre(event);
                }
            });

    public static final Event<DataLoaderPost> DATA_LOADER_POST = EventFactory.createArrayBacked(DataLoaderPost.class,
            listeners -> event -> {
                for (DataLoaderPost listener : listeners) {
                    listener.onDataLoaderPost(event);
                }
            });
}
