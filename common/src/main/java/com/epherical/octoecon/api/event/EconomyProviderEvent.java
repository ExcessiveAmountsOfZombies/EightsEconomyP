package com.epherical.octoecon.api.event;

import com.epherical.eights.EightsEconMod;
import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.OctoEconomy;

import java.nio.file.Path;
import java.util.List;

public final class EconomyProviderEvent {

    private EconomyProviderEvent() {
    }

    public static class Pre {

        private final EightsEconMod mod;
        private final Path worldDirectory;
        private final List<Currency> currencies;
        private OctoEconomy economy;

        public Pre(EightsEconMod mod, Path worldDirectory, List<Currency> currencies) {
            this.mod = mod;
            this.worldDirectory = worldDirectory;
            this.currencies = currencies;
        }

        public EightsEconMod getMod() {
            return mod;
        }

        public Path getWorldDirectory() {
            return worldDirectory;
        }

        public List<Currency> getCurrencies() {
            return currencies;
        }

        public OctoEconomy getEconomy() {
            return economy;
        }

        public void setEconomy(OctoEconomy economy) {
            this.economy = economy;
        }
    }

    public static class Post {

        private final OctoEconomy economy;

        public Post(OctoEconomy economy) {
            this.economy = economy;
        }

        public OctoEconomy getEconomy() {
            return economy;
        }
    }
}
