package com.epherical.eights.user;

import com.epherical.octoecon.api.user.UniqueUser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.UUID;

public class PlayerUser extends AbstractUser implements UniqueUser {

    private static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);

    public static final Codec<PlayerUser> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUID_CODEC.fieldOf("uuid").forGetter(PlayerUser::getUserID),
            Codec.STRING.fieldOf("name").forGetter(PlayerUser::getIdentity),
            Codec.DOUBLE.fieldOf("balance").forGetter(PlayerUser::getRawBalance)
    ).apply(instance, PlayerUser::new));

    private final UUID uuid;

    public PlayerUser(UUID uuid, String name, double balance) {
        super(name, balance);
        this.uuid = uuid;
    }

    @Override
    public UUID getUserID() {
        return uuid;
    }

}
