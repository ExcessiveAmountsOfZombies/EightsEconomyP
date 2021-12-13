package com.epherical.eights.user;

import com.epherical.eights.transaction.BasicTransaction;
import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.transaction.Transaction;
import com.epherical.octoecon.api.user.User;
import com.google.common.collect.Maps;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;

import java.util.Map;

import static com.epherical.octoecon.api.transaction.Transaction.Response.SUCCESS;
import static com.epherical.octoecon.api.transaction.Transaction.Type.*;

public abstract class AbstractUser implements User {
    private final String identifier;
    private final Map<Currency, Double> balances;

    public AbstractUser(String name, Map<Currency, Double> balances) {
        this.identifier = name;
        this.balances = balances;
    }

    @Override
    public Component getDisplayName() {
        return Component.nullToEmpty(identifier);
    }

    @Override
    public double getBalance(Currency currency) {
        return balances.get(currency);
    }

    @Override
    public Map<Currency, Double> getAllBalances() {
        return balances;
    }

    @Override
    public boolean hasAmount(Currency currency, double amount) {
        return getBalance(currency) >= amount;
    }

    @Override
    public Transaction resetBalance(Currency currency) {
        Double currentValue = balances.get(currency);
        balances.put(currency, 0.0D);
        return new BasicTransaction(currentValue, currency, this, "Reset balance of User", SUCCESS, currentValue <= 0 ? DEPOSIT : WITHDRAW);
    }

    @Override
    public Map<Currency, Transaction> resetAllBalances() {
        Map<Currency, Transaction> balanceResetResult = Maps.newHashMap();
        for (Currency currency : balances.keySet()) {
            balanceResetResult.put(currency, resetBalance(currency));
        }
        return balanceResetResult;
    }

    @Override
    public Transaction setBalance(Currency currency, double amount) {
        balances.put(currency, amount);
        return new BasicTransaction(amount, currency, this, "Set balance of user", SUCCESS, SET);
    }

    @Override
    public Transaction sendTo(User user, Currency currency, double amount) {
        Validate.isTrue(amount >= 0, "Values are required to be positive, %.2f was given.", amount);
        Transaction transaction = withdrawMoney(currency, amount, "Sending money from " + this.getIdentity() + " to " + user.getIdentity() + ".");
        user.depositMoney(currency, amount, user.getIdentity() + " received money from " + this.getIdentity() + ".");
        return transaction;
    }

    @Override
    public Transaction depositMoney(Currency currency, double amount, String reason) {
        Validate.isTrue(amount >= 0, "Values are required to be positive, %.2f was given.", amount);
        Transaction transaction = new BasicTransaction(amount, currency, this, reason, SUCCESS, DEPOSIT);
        this.addTransaction(transaction);
        return transaction;
    }

    @Override
    public Transaction withdrawMoney(Currency currency, double amount, String reason) {
        Validate.isTrue(amount >= 0, "Values are required to be positive, %.2f was given.", amount);
        Transaction transaction = new BasicTransaction(amount, currency, this, reason, SUCCESS, WITHDRAW);
        this.addTransaction(transaction);
        return transaction;
    }

    @Override
    public String getIdentity() {
        return identifier;
    }

    public void addTransaction(Transaction transaction) {
        balances.put(transaction.getCurrency(), transactionBalance(transaction));
    }

    public double transactionBalance(Transaction transaction) {
        double sum = balances.get(transaction.getCurrency());
        if (transaction.getTransactionType().equals(DEPOSIT)) {
            sum += transaction.getTransactionDelta();
        } else if (transaction.getTransactionType().equals(WITHDRAW)) {
            sum -= transaction.getTransactionDelta();
        } else if (transaction.getTransactionType().equals(SET)) {
            sum = transaction.getTransactionDelta();
        }
        return sum;
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
