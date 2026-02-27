package com.epherical.octoecon.api.event;

import com.epherical.eights.EightsEconMod;
import com.epherical.octoecon.api.OctoEconomy;
import com.epherical.octoecon.api.user.FakeUser;
import com.epherical.octoecon.api.user.UniqueUser;

import java.nio.file.Path;

public class EconomyProviderEvent {

    public static class Pre {

        private final EightsEconMod mod;
        private final Path worldDirectory;
        private OctoEconomy<? extends UniqueUser, ? extends FakeUser> economy;

        public Pre(EightsEconMod mod, Path worldDirectory) {
            this.mod = mod;
            this.worldDirectory = worldDirectory;
        }

        public EightsEconMod getMod() {
            return mod;
        }

        public Path getWorldDirectory() {
            return worldDirectory;
        }

        public OctoEconomy<? extends UniqueUser, ? extends FakeUser> getEconomy() {
            return economy;
        }

        public void setEconomy(OctoEconomy<? extends UniqueUser, ? extends FakeUser> economy) {
            this.economy = economy;
        }
    }

    public static class Post {

        private final OctoEconomy<? extends UniqueUser, ? extends FakeUser> economy;

        public Post(OctoEconomy<? extends UniqueUser, ? extends FakeUser> economy) {
            this.economy = economy;
        }

        public OctoEconomy<? extends UniqueUser, ? extends FakeUser> getEconomy() {
            return economy;
        }
    }
}
