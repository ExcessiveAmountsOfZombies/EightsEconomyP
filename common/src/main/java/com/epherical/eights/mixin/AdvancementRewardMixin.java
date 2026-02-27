package com.epherical.eights.mixin;

import com.epherical.eights.BalanceMethods;
import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.OctoEconomy;
import com.epherical.octoecon.api.user.UniqueUser;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementRewards.class)
public class AdvancementRewardMixin {

    @Unique
    private int oei$currencyAmount;
    @Unique
    private ResourceLocation oei$currencyName;

    @Inject(method = "grant", at = @At("HEAD"))
    public void grantCurrency(ServerPlayer serverPlayer, CallbackInfo ci) {
        if (oei$currencyAmount <= 0) {
            return;
        }

        OctoEconomy provider = BalanceMethods.getProvider();
        if (provider == null) {
            return;
        }

        UniqueUser user = provider.getOrCreatePlayerAccount(serverPlayer.getUUID());
        Currency currency = oei$currencyName != null ? provider.getCurrency(oei$currencyName) : provider.getDefaultCurrency();
        if (user != null && currency != null) {
            user.depositMoney(currency, oei$currencyAmount, "advancement reward");
        }
    }
}
