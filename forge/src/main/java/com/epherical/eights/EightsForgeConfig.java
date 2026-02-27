package com.epherical.eights;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class EightsForgeConfig {

    private ForgeConfigSpec configSpec;

    private final ForgeConfigSpec.ConfigValue<Boolean> useSaveThread;
    private final ForgeConfigSpec.ConfigValue<Double> providePlayersMoneyOnFirstLogin;

    public EightsForgeConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        useSaveThread = builder.comment("determines if you want to use a dedicated thread to save player data. Defaults to true")
                .define("useSaveThread", ConfigConstants.getInstance().useSaveThread);
        providePlayersMoneyOnFirstLogin = builder.comment("If you want to provide the player with money the first time they log in, change this value. Only applies to NEW players, not retroactive. Default: 0.0")
                .define("providePlayersMoneyOnFirstLogin", ConfigConstants.getInstance().providedMoneyOnFirstLogin);
        this.configSpec = builder.build();
    }

    public void initConfig(ModConfigEvent event) {
        if (event.getConfig().getSpec() == configSpec) {
            ConfigConstants.getInstance().useSaveThread = useSaveThread.get();
            ConfigConstants.getInstance().providedMoneyOnFirstLogin = providePlayersMoneyOnFirstLogin.get();
        }
    }

    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }
}
