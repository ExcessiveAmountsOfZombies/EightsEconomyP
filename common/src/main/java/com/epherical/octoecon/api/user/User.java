package com.epherical.octoecon.api.user;

import net.minecraft.network.chat.Component;

public interface User {

    Component getDisplayName();

    double getBalance(String reasonCode);

    boolean hasAmount(double amount, String reasonCode);

    void resetBalance(String reasonCode);

    void setBalance(double amount, String reasonCode);

    void sendTo(User user, double amount, String reasonCode);

    void depositMoney(double amount, String reasonCode);

    void withdrawMoney(double amount, String reasonCode);

    String getIdentity();

    boolean isDirty();
}
