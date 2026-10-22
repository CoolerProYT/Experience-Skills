package com.coolerpromc.experienceskills.config;

import com.coolerpromc.coolerconfig.config.ConfigBuilder;
import com.coolerpromc.coolerconfig.config.ConfigFormat;
import com.coolerpromc.coolerconfig.config.ConfigSide;
import com.coolerpromc.coolerconfig.config.ConfigSpec;
import com.coolerpromc.coolerconfig.config.ConfigValue;
import com.coolerpromc.experienceskills.Constants;

public class ModClientConfig {
    public static ConfigSpec CONFIG;

    public static ConfigValue<Boolean> LEVEL_UP_TOASTS;

    public static void init(){
        ConfigBuilder builder = ConfigSpec.builder(Constants.MODID, ConfigFormat.JSON5).side(ConfigSide.CLIENT)
            .comment("Experience Skills client settings.");

        LEVEL_UP_TOASTS = builder.defineBoolean("levelUpToasts", true, "Show a toast in the corner of the screen whenever one of your skills levels up.");

        CONFIG = builder.build();
    }
}
