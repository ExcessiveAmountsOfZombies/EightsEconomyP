package com.epherical.eights.currency;

import com.epherical.octoecon.api.BalanceProvider;
import com.epherical.octoecon.api.transaction.Transaction;
import com.epherical.octoecon.api.user.User;

public class BasicBalanceProvider implements BalanceProvider {
    @Override
    public double getBalance(User user) {

        return 0;
    }

    @Override
    public Transaction setBalance(User user, double v) {
        return null;
    }

    @Override
    public Transaction sendTo(User user, User user1, double v) {
        return null;
    }

    @Override
    public Transaction deposit(User user, double v, String s) {
        return null;
    }

    @Override
    public Transaction withdraw(User user, double v, String s) {
        return null;
    }
}
