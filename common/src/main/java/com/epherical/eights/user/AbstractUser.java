package com.epherical.eights.user;

import com.epherical.octoecon.api.user.User;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;

public abstract class AbstractUser implements User {

    private final String identifier;
    protected double balance;
    private boolean dirty = false;
    private String lastReasonCode = "";

    public AbstractUser(String name, double balance) {
        this.identifier = name;
        this.balance = balance;
    }

    @Override
    public Component getDisplayName() {
        return Component.nullToEmpty(identifier);
    }

    @Override
    public double getBalance(String reasonCode) {
        recordReason(reasonCode);
        return balance;
    }

    @Override
    public boolean hasAmount(double amount, String reasonCode) {
        return getBalance(reasonCode) >= amount;
    }

    @Override
    public void resetBalance(String reasonCode) {
        this.balance = 0.0D;
        markDirty(reasonCode);
    }

    @Override
    public void setBalance(double amount, String reasonCode) {
        this.balance = amount;
        markDirty(reasonCode);
    }

    @Override
    public void sendTo(User user, double amount, String reasonCode) {
        Validate.isTrue(amount >= 0, "Values are required to be positive, %.2f was given.", amount);
        if (!hasAmount(amount, reasonCode)) {
            throw new IllegalStateException("Insufficient balance for transfer.");
        }
        withdrawMoney(amount, reasonCode + ":send");
        user.depositMoney(amount, reasonCode + ":receive");
        markDirty(reasonCode);
    }

    @Override
    public void depositMoney(double amount, String reasonCode) {
        Validate.isTrue(amount >= 0, "Values are required to be positive, %.2f was given.", amount);
        this.balance += amount;
        markDirty(reasonCode);
    }

    @Override
    public void withdrawMoney(double amount, String reasonCode) {
        Validate.isTrue(amount >= 0, "Values are required to be positive, %.2f was given.", amount);
        this.balance -= amount;
        markDirty(reasonCode);
    }

    @Override
    public String getIdentity() {
        return identifier;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public double getRawBalance() {
        return balance;
    }

    public String getLastReasonCode() {
        return lastReasonCode;
    }

    private void markDirty(String reasonCode) {
        this.dirty = true;
        recordReason(reasonCode);
    }

    private void recordReason(String reasonCode) {
        this.lastReasonCode = reasonCode == null ? "" : reasonCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractUser that = (AbstractUser) o;

        return identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
