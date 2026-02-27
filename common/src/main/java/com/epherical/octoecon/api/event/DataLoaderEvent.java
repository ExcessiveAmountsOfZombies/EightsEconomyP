package com.epherical.octoecon.api.event;

import com.epherical.eights.EightsEconomyProvider;
import com.epherical.eights.data.EconomyData;

import java.nio.file.Path;

public final class DataLoaderEvent {

    private DataLoaderEvent() {
    }

    public static class Pre {

        private final EightsEconomyProvider provider;
        private final Path worldDirectory;
        private EconomyData dataLoader;

        public Pre(EightsEconomyProvider provider, Path worldDirectory) {
            this.provider = provider;
            this.worldDirectory = worldDirectory;
        }

        public EightsEconomyProvider getProvider() {
            return provider;
        }

        public Path getWorldDirectory() {
            return worldDirectory;
        }

        public EconomyData getDataLoader() {
            return dataLoader;
        }

        public void setDataLoader(EconomyData dataLoader) {
            this.dataLoader = dataLoader;
        }
    }

    public static class Post {

        private final EightsEconomyProvider provider;
        private final EconomyData dataLoader;

        public Post(EightsEconomyProvider provider, EconomyData dataLoader) {
            this.provider = provider;
            this.dataLoader = dataLoader;
        }

        public EightsEconomyProvider getProvider() {
            return provider;
        }

        public EconomyData getDataLoader() {
            return dataLoader;
        }
    }
}
