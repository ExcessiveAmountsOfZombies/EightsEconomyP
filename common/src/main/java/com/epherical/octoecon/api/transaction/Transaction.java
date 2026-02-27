package com.epherical.octoecon.api.transaction;

import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.user.User;

public interface Transaction {

    Currency getCurrency();

    double getTransactionDelta();

    User getUser();

    String getMessage();

    Response getTransactionResponse();

    void setTransactionResponse(Response response);

    Type getTransactionType();

    enum Response {
        SUCCESS,
        FAILURE,
        INSUFFICIENT_FUNDS
    }

    enum Type {
        DEPOSIT,
        WITHDRAW,
        SET
    }
}
