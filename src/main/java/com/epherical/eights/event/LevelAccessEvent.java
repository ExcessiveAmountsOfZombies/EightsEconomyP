package com.epherical.eights.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.storage.LevelStorageSource;

public final class LevelAccessEvent {

    public static final Event<Created> CREATED_EVENT = EventFactory.createArrayBacked(Created.class, calls -> (access) -> {
        for (Created call : calls) {
            call.onCreated(access);
        }
    });


    public interface Created {
        void onCreated(LevelStorageSource.LevelStorageAccess access);
    }

}
