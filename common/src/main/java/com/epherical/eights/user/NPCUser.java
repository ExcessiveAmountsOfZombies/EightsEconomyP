package com.epherical.eights.user;

import com.epherical.octoecon.api.user.FakeUser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class NPCUser extends AbstractUser implements FakeUser {

    public static final Codec<NPCUser> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("name").forGetter(NPCUser::getResourceLocation),
            Codec.DOUBLE.fieldOf("balance").forGetter(NPCUser::getRawBalance)
    ).apply(instance, NPCUser::new));

    private final ResourceLocation location;

    public NPCUser(ResourceLocation location, double balance) {
        super(location.toString(), balance);
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
