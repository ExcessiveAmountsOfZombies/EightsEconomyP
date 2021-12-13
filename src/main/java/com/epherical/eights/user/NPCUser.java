package com.epherical.eights.user;

import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.user.FakeUser;
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
}
