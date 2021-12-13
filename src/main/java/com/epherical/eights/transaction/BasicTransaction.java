package com.epherical.eights.transaction;

import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.transaction.Transaction;
import com.epherical.octoecon.api.user.User;

public class BasicTransaction implements Transaction {

    private final double delta;
    private final Currency currency;
    private final User user;
    private final String message;
    private Response response;
    private final Type type;

    public BasicTransaction(double delta, Currency currency, User user, String message, Response response, Type type) {
        this.delta = delta;
        this.currency = currency;
        this.user = user;
        this.message = message;
        this.response = response;
        this.type = type;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public double getTransactionDelta() {
        return delta;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Response getTransactionResponse() {
        return response;
    }

    @Override
    public void setTransactionResponse(Response response) {
        this.response = response;
    }

    @Override
    public Type getTransactionType() {
        return type;
    }
}
