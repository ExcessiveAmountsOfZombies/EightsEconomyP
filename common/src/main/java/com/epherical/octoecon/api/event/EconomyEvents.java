package com.epherical.octoecon.api.event;

import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.OctoEconomy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EconomyEvents {

    public interface CurrencyRegistrationListener {
        void onRegister(CurrencyRegistrationEvent event);
    }

    public interface EconomyProviderPreListener {
        void onPre(EconomyProviderEvent.Pre event);
    }

    public interface EconomyProviderPostListener {
        void onPost(EconomyProviderEvent.Post event);
    }

    public interface DataLoaderPreListener {
        void onPre(DataLoaderEvent.Pre event);
    }

    public interface DataLoaderPostListener {
        void onPost(DataLoaderEvent.Post event);
    }

    private static final List<CurrencyRegistrationListener> CURRENCY_REGISTRATION = new CopyOnWriteArrayList<>();
    private static final List<EconomyProviderPreListener> ECONOMY_PROVIDER_PRE = new CopyOnWriteArrayList<>();
    private static final List<EconomyProviderPostListener> ECONOMY_PROVIDER_POST = new CopyOnWriteArrayList<>();
    private static final List<DataLoaderPreListener> DATA_LOADER_PRE = new CopyOnWriteArrayList<>();
    private static final List<DataLoaderPostListener> DATA_LOADER_POST = new CopyOnWriteArrayList<>();

    private EconomyEvents() {
    }

    public static void registerCurrencyRegistration(CurrencyRegistrationListener listener) {
        CURRENCY_REGISTRATION.add(listener);
    }

    public static void registerEconomyProviderPre(EconomyProviderPreListener listener) {
        ECONOMY_PROVIDER_PRE.add(listener);
    }

    public static void registerEconomyProviderPost(EconomyProviderPostListener listener) {
        ECONOMY_PROVIDER_POST.add(listener);
    }

    public static void registerDataLoaderPre(DataLoaderPreListener listener) {
        DATA_LOADER_PRE.add(listener);
    }

    public static void registerDataLoaderPost(DataLoaderPostListener listener) {
        DATA_LOADER_POST.add(listener);
    }

    public static CurrencyRegistrationEvent fireCurrencyRegistration(CurrencyRegistrationEvent event) {
        for (CurrencyRegistrationListener listener : CURRENCY_REGISTRATION) {
            listener.onRegister(event);
        }
        return event;
    }

    public static EconomyProviderEvent.Pre fireEconomyProviderPre(EconomyProviderEvent.Pre event) {
        for (EconomyProviderPreListener listener : ECONOMY_PROVIDER_PRE) {
            listener.onPre(event);
        }
        return event;
    }

    public static EconomyProviderEvent.Post fireEconomyProviderPost(EconomyProviderEvent.Post event) {
        for (EconomyProviderPostListener listener : ECONOMY_PROVIDER_POST) {
            listener.onPost(event);
        }
        return event;
    }

    public static DataLoaderEvent.Pre fireDataLoaderPre(DataLoaderEvent.Pre event) {
        for (DataLoaderPreListener listener : DATA_LOADER_PRE) {
            listener.onPre(event);
        }
        return event;
    }

    public static DataLoaderEvent.Post fireDataLoaderPost(DataLoaderEvent.Post event) {
        for (DataLoaderPostListener listener : DATA_LOADER_POST) {
            listener.onPost(event);
        }
        return event;
    }

    public static List<Currency> collectCurrencies() {
        return fireCurrencyRegistration(new CurrencyRegistrationEvent()).mutableCurrencies();
    }

    public static OctoEconomy finalizeEconomy(OctoEconomy economy) {
        return fireEconomyProviderPost(new EconomyProviderEvent.Post(economy)).getEconomy();
    }
}
