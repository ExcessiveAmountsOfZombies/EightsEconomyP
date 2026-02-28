package com.epherical.eights;

import net.neoforged.neoforge.common.ModConfigSpec;

public class EightsNeoForgeConfig {

    private final ModConfigSpec configSpec;
    private final ModConfigSpec.ConfigValue<Boolean> useSaveThread;
    private final ModConfigSpec.ConfigValue<Double> providePlayersMoneyOnFirstLogin;
    private final ModConfigSpec.ConfigValue<String> baltopFont;

    public EightsNeoForgeConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        useSaveThread = builder.comment("determines if you want to use a dedicated thread to save player data. Defaults to true")
                .define("useSaveThread", ConfigConstants.getInstance().useSaveThread);
        providePlayersMoneyOnFirstLogin = builder.comment("If you want to provide the player with money the first time they log in, change this value. Only applies to NEW players, not retroactive. Default: 0.0")
                .define("providePlayersMoneyOnFirstLogin", ConfigConstants.getInstance().providedMoneyOnFirstLogin);
        baltopFont = builder.comment("Font id used by /baltop text. Leave blank to use minecraft:default. Example: minecraft:uniform")
                .define("baltopFont", ConfigConstants.getInstance().baltopFont);
        this.configSpec = builder.build();
    }

    public void apply() {
        ConfigConstants.getInstance().useSaveThread = useSaveThread.get();
        ConfigConstants.getInstance().providedMoneyOnFirstLogin = providePlayersMoneyOnFirstLogin.get();
        ConfigConstants.getInstance().baltopFont = baltopFont.get();
    }

    public ModConfigSpec getConfigSpec() {
        return configSpec;
    }
}
