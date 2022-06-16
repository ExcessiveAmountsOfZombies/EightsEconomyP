package com.epherical.eights.currency;

import com.epherical.eights.EightsEconMod;
import com.epherical.octoecon.api.Currency;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.text.DecimalFormat;

public class BasicCurrency implements Currency {

    private final ResourceLocation identifier;
    private final Component currencySingularName;
    private final Component currencyPluralName;
    private final Component currencySymbol;
    private static final DecimalFormat df = new DecimalFormat();

    public BasicCurrency(ResourceLocation identifier) {
        this.identifier = identifier;

        this.currencySingularName = Component.literal("Dollar").setStyle(EightsEconMod.CONSTANTS_STYLE);
        this.currencyPluralName = Component.literal("Dollars").setStyle(EightsEconMod.CONSTANTS_STYLE);
        this.currencySymbol = Component.literal("$").setStyle(EightsEconMod.CONSTANTS_STYLE);
    }

    @Override
    public Component getCurrencySingularName() {
        return currencySingularName;
    }

    @Override
    public Component getCurrencyPluralName() {
        return currencyPluralName;
    }

    @Override
    public Component getCurrencySymbol() {
        return currencySymbol;
    }

    @Override
    public Component format(double value) {
        return format(value, 2);
    }

    @Override
    public Component format(double value, int decimalPlaces) {
        String format = "%s%s %s";
        MutableComponent component;
        df.setMaximumFractionDigits(decimalPlaces);
        Component money = Component.literal(df.format(value)).setStyle(EightsEconMod.VARIABLE_STYLE);
        if (value > 1.0d || value < 1.0d) {
            component = Component.translatable(format, currencySymbol, money, currencyPluralName);
        } else {
            component = Component.translatable(format, currencySymbol, money, currencySingularName);
        }
        return component;
    }

    @Override
    public String getIdentity() {
        return identifier.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasicCurrency that = (BasicCurrency) o;

        return identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
