package com.epherical.eights;

public class ConfigConstants {

    private static ConfigConstants INSTANCE = new ConfigConstants();


    public ConfigConstants() {
        // On forge, the constructor is never called, so we use the default INSTANCE
        // but for fabric, we load a config and assign the values to a new configConstants.
        INSTANCE = this;
    }

    private String _comment = "Check the github for more information";
    public boolean useSaveThread = true;
    public double providedMoneyOnFirstLogin = 0.0D;


    public static ConfigConstants getInstance() {
        return INSTANCE;
    }

}
