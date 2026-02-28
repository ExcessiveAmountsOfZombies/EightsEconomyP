package com.epherical.eights;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class EightsForgeConfig {

    private ForgeConfigSpec configSpec;

    private final ForgeConfigSpec.ConfigValue<Boolean> useSaveThread;
    private final ForgeConfigSpec.ConfigValue<Double> providePlayersMoneyOnFirstLogin;
    private final ForgeConfigSpec.ConfigValue<String> baltopFont;

    public EightsForgeConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        useSaveThread = builder.comment("determines if you want to use a dedicated thread to save player data. Defaults to true")
                .define("useSaveThread", ConfigConstants.getInstance().useSaveThread);
        providePlayersMoneyOnFirstLogin = builder.comment("If you want to provide the player with money the first time they log in, change this value. Only applies to NEW players, not retroactive. Default: 0.0")
                .define("providePlayersMoneyOnFirstLogin", ConfigConstants.getInstance().providedMoneyOnFirstLogin);
        baltopFont = builder.comment("Font id used by /baltop text. Leave blank to use minecraft:default. Example: minecraft:uniform")
                .define("baltopFont", ConfigConstants.getInstance().baltopFont);
        this.configSpec = builder.build();
    }

    public void initConfig(ModConfigEvent event) {
        if (event.getConfig().getSpec() == configSpec) {
            ConfigConstants.getInstance().useSaveThread = useSaveThread.get();
            ConfigConstants.getInstance().providedMoneyOnFirstLogin = providePlayersMoneyOnFirstLogin.get();
            ConfigConstants.getInstance().baltopFont = baltopFont.get();
        }
    }

    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }
}
