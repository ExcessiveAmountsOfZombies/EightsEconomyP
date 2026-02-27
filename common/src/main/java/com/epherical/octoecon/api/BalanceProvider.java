package com.epherical.octoecon.api;

import com.epherical.octoecon.api.transaction.Transaction;
import com.epherical.octoecon.api.user.User;

public interface BalanceProvider {

    double getBalance(User user);

    Transaction setBalance(User user, double amount, Currency currency);

    Transaction sendTo(User source, User target, double amount, Currency currency);

    Transaction deposit(User user, double amount, String reason, Currency currency);

    Transaction withdraw(User user, double amount, String reason, Currency currency);
}
