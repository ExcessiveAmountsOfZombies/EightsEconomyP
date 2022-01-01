package com.epherical.eights.mixin;

import com.epherical.eights.AdvancementRewardInterface;
import com.epherical.eights.EightsEconomyProvider;
import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.user.UniqueUser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AdvancementRewards.class)
public class AdvancementRewardMixin implements AdvancementRewardInterface {

    @Unique
    private int currencyAmount;
    @Unique
    private ResourceLocation currencyName;


    @Inject(method = "deserialize", at = @At("RETURN"))
    private static void deserialization(JsonObject jsonObject, CallbackInfoReturnable<AdvancementRewards> cir) {
        AdvancementRewardInterface reward = (AdvancementRewardInterface) cir.getReturnValue();
        if (jsonObject.has("currency")) {
            JsonObject currency = GsonHelper.getAsJsonObject(jsonObject, "currency");
            for (String currencyName : currency.keySet()) {
                ResourceLocation location = new ResourceLocation(currencyName);
                reward.oei$setCurrencyReward(currency.get(currencyName).getAsInt(), location);
                break;
            }
        }
    }

    @Inject(method = "grant", at = @At("HEAD"))
    public void grantCurrency(ServerPlayer serverPlayer, CallbackInfo ci) {
        UniqueUser user = EightsEconomyProvider.getInstance().getOrCreatePlayerAccount(serverPlayer.getUUID());
        Currency currency = EightsEconomyProvider.getInstance().getCurrency(currencyName);
        if (user != null) {
            if (currency != null) {
                user.depositMoney(currency, currencyAmount, "advancement reward");
            } else if ((currency = EightsEconomyProvider.getInstance().getDefaultCurrency()) != null) {
                user.depositMoney(currency, currencyAmount, "advancement reward (fallback default)");
            }
        }
    }


    /*@Inject(method = "serializeToJson", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;<init>()V", shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD, require = 0)
    public void serializeReward(CallbackInfoReturnable<JsonElement> cir, JsonObject object) {
        if (this.currencyName != null && EightsEconomyProvider.getInstance().getCurrency(currencyName) != null) {
            object.addProperty(this.currencyName.toString(), this.currencyAmount);
        }
    }*/

    @Override
    public void oei$setCurrencyReward(int currencyAmount, ResourceLocation currencyName) {
        this.currencyAmount = currencyAmount;
        this.currencyName = currencyName;
    }

    @Override
    public ResourceLocation oei$getCurrency() {
        return currencyName;
    }

    @Override
    public int oei$getCurrencyAmount() {
        return currencyAmount;
    }
}
