package com.epherical.octoecon.api;

import net.minecraft.network.chat.Component;

public interface Currency {

    Component getCurrencySingularName();

    Component getCurrencyPluralName();

    Component getCurrencySymbol();

    Component format(double value);

    Component format(double value, int decimalPlaces);

    BalanceProvider balanceProvider();

    String getIdentity();
}
