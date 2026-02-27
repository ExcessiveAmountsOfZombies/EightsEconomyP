package com.epherical.octoecon.api.user;

import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.transaction.Transaction;
import net.minecraft.network.chat.Component;

import java.util.Map;

public interface User {

    Component getDisplayName();

    double getBalance(Currency currency);

    Map<Currency, Double> getAllBalances();

    boolean hasAmount(Currency currency, double amount);

    Transaction resetBalance(Currency currency);

    Map<Currency, Transaction> resetAllBalances();

    Transaction setBalance(Currency currency, double amount);

    Transaction sendTo(User user, Currency currency, double amount);

    Transaction depositMoney(Currency currency, double amount, String reason);

    Transaction withdrawMoney(Currency currency, double amount, String reason);

    String getIdentity();
}
