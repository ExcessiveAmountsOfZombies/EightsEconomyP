package com.epherical.eights;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class EightsForgeConfig {

    private ForgeConfigSpec configSpec;

    private final ForgeConfigSpec.ConfigValue<Boolean> useSaveThread;

    public EightsForgeConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        useSaveThread = builder.comment("determines if you want to use a dedicated thread to save player data. Defaults to true")
                .define("useSaveThread", ConfigConstants.useSaveThread);
        this.configSpec = builder.build();
    }

    public void initConfig(ModConfigEvent event) {
        if (event.getConfig().getSpec().equals(configSpec)) {
            ConfigConstants.useSaveThread = useSaveThread.get();
        }
    }

    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }
}
