package com.epherical.eights.mixin;

import com.epherical.eights.BalanceMethods;
import com.epherical.octoecon.api.OctoEconomy;
import com.epherical.octoecon.api.user.UniqueUser;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementRewards.class)
public class AdvancementRewardMixin {

    @Unique
    private int oei$balanceAmount;

    @Inject(method = "grant", at = @At("HEAD"))
    public void grantBalance(ServerPlayer serverPlayer, CallbackInfo ci) {
        if (oei$balanceAmount <= 0) {
            return;
        }

        OctoEconomy provider = BalanceMethods.getProvider();
        if (provider == null) {
            return;
        }

        UniqueUser user = provider.getOrCreatePlayerAccount(serverPlayer.getUUID());
        user.depositMoney(oei$balanceAmount, "advancement reward");
    }
}
