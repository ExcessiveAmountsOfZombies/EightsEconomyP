package com.epherical.eights.user;

import com.epherical.eights.EightsEconomyProvider;
import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.VirtualCurrency;
import com.epherical.octoecon.api.user.FakeUser;
import com.google.common.collect.Maps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class NPCUser extends AbstractUser implements FakeUser {

    private final ResourceLocation location;

    public NPCUser(ResourceLocation location, Map<Currency, Double> balances) {
        super(location.toString(), balances);
        this.location = location;
    }

    @Override
    public Component getDisplayName() {
        return Component.nullToEmpty(location.getPath());
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return location;
    }

    @Override
    public String getIdentity() {
        return location.toString();
    }

    @Override
    public Map<Currency, Double> getAllBalances() {
        Map<Currency, Double> currencyDoubleMap = Maps.newHashMap(balances);
        for (Currency value : EightsEconomyProvider.getInstance().currencyMap.values()) {
            if (value instanceof VirtualCurrency) {
                currencyDoubleMap.remove(value);
            }
        }
        return currencyDoubleMap;
    }
}
