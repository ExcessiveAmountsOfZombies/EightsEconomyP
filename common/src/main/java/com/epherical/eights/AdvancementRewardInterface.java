package com.epherical.eights;


import net.minecraft.resources.ResourceLocation;

public interface AdvancementRewardInterface {

    void oei$setCurrencyReward(int currencyAmount, ResourceLocation currencyName);

    ResourceLocation oei$getCurrency();

    int oei$getCurrencyAmount();
}
