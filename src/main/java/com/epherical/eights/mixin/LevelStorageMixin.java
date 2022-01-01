package com.epherical.eights.mixin;

import com.epherical.eights.event.LevelAccessEvent;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelStorageSource.class)
public class LevelStorageMixin {


    @Inject(method = "createAccess", at = @At("RETURN"))
    public void grabWorldAccess(String string, CallbackInfoReturnable<LevelStorageSource.LevelStorageAccess> cir) {
        LevelAccessEvent.CREATED_EVENT.invoker().onCreated(cir.getReturnValue());
    }

}
