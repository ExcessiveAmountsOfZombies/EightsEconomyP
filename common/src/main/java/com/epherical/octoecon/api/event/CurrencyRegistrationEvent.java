package com.epherical.octoecon.api.event;

import com.epherical.octoecon.api.Currency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrencyRegistrationEvent {

    private final List<Currency> currencies;

    public CurrencyRegistrationEvent(List<Currency> currencies) {
        this.currencies = currencies;
    }

    public CurrencyRegistrationEvent() {
        this(new ArrayList<>());
    }

    public void addCurrency(Currency currency) {
        currencies.add(currency);
    }

    public List<Currency> getCurrencies() {
        return Collections.unmodifiableList(currencies);
    }

    List<Currency> mutableCurrencies() {
        return currencies;
    }
}
