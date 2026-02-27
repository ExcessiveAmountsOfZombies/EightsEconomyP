package com.epherical.eights.user;

import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.user.UniqueUser;

import java.util.Map;
import java.util.UUID;

public class PlayerUser extends AbstractUser implements UniqueUser {

    private final UUID uuid;

    public PlayerUser(UUID uuid, String name, Map<Currency, Double> balances) {
        super(name, balances);
        this.uuid = uuid;
    }

    @Override
    public UUID getUserID() {
        return uuid;
    }
}
