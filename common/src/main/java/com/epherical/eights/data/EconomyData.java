package com.epherical.eights.data;

import com.epherical.eights.ConfigConstants;
import com.epherical.eights.EightsEconomyProvider;
import com.epherical.eights.exception.EconomyException;
import com.epherical.eights.user.NPCUser;
import com.epherical.eights.user.PlayerUser;
import com.epherical.octoecon.api.user.FakeUser;
import com.epherical.octoecon.api.user.UniqueUser;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class EconomyData {

    protected final ScheduledExecutorService saveSchedule = Executors.newSingleThreadScheduledExecutor();

    private final EightsEconomyProvider provider;

    public EconomyData(EightsEconomyProvider provider) {
        this.provider = provider;
        if (ConfigConstants.getInstance().useSaveThread) {
            saveSchedule.scheduleAtFixedRate(this::savePlayers, 1L, 1L, TimeUnit.MINUTES);
        }
    }

    public void close() {
        if (ConfigConstants.getInstance().useSaveThread) {
            List<Runnable> remaining = saveSchedule.shutdownNow();
            remaining.forEach(Runnable::run);
        }
    }

    public void savePlayers() {
        for (UniqueUser uniqueUser : provider.getUniqueUsers()) {
            if (uniqueUser instanceof PlayerUser playerUser && playerUser.isDirty()) {
                try {
                    saveUser(playerUser);
                } catch (EconomyException ignored) {
                }
            }
        }

        for (FakeUser fakeUser : provider.getFakeUsers()) {
            if (fakeUser.isDirty()) {
                try {
                    saveUser((NPCUser) fakeUser);
                } catch (EconomyException ignored) {
                }
            }
        }
    }

    public abstract PlayerUser loadUser(UUID uuid) throws IOException;

    public abstract FakeUser loadUser(ResourceLocation name) throws IOException;

    public abstract List<PlayerUser> loadAllPlayerUsers() throws IOException;

    public abstract boolean userExists(ResourceLocation name) throws EconomyException;

    public abstract boolean userExists(UUID uuid) throws EconomyException;

    public abstract boolean userExists(String name, boolean player) throws EconomyException;

    public boolean saveUser(PlayerUser user) throws EconomyException {
        return saveUser(user, false);
    }

    public abstract boolean saveUser(PlayerUser user, boolean setBalance) throws EconomyException;

    public boolean saveUser(NPCUser user) throws EconomyException {
        return saveUser(user, false);
    }

    public abstract boolean saveUser(NPCUser user, boolean setBalance) throws EconomyException;
}
