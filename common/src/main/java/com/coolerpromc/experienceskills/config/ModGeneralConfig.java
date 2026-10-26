package com.coolerpromc.experienceskills.config;

import com.coolerpromc.coolerconfig.config.ConfigBuilder;
import com.coolerpromc.coolerconfig.config.ConfigFormat;
import com.coolerpromc.coolerconfig.config.ConfigSide;
import com.coolerpromc.coolerconfig.config.ConfigSpec;
import com.coolerpromc.coolerconfig.config.ConfigValue;
import com.coolerpromc.experienceskills.Constants;

public class ModGeneralConfig {
    public static ConfigSpec CONFIG;

    public static ConfigValue<Double> SHARED_XP_FRACTION;
    public static ConfigValue<Double> SHARED_XP_RADIUS;
    public static ConfigValue<Boolean> TRANSFER_ENABLED;
    public static ConfigValue<Double> TRANSFER_EFFICIENCY;
    public static ConfigValue<Boolean> SCOREBOARD_ENABLED;
    public static ConfigValue<Boolean> STEP_UP_MILESTONES;

    public static void init(){
        ConfigBuilder builder = ConfigSpec.builder(Constants.MODID, ConfigFormat.JSON5).side(ConfigSide.COMMON).suffix("general")
            .comment("General Experience Skills settings that apply to every skill.");

        SHARED_XP_FRACTION = builder.defineDouble("sharedXp.fraction", 0.0, 0.0, 1.0,
            "When a player collects skill experience, nearby players also receive this fraction of it (the collector keeps the full amount). 0 turns sharing off.");
        SHARED_XP_RADIUS = builder.defineDouble("sharedXp.radius", 16.0, 0.0, 256.0,
            "How close, in blocks, another player must be to receive shared experience.");
        TRANSFER_ENABLED = builder.defineBoolean("transfer.enabled", true,
            "Whether players may move experience from one of their skills to another with /experienceskills transfer or the skills screen.");
        TRANSFER_EFFICIENCY = builder.defineDouble("transfer.efficiency", 0.5, 0.0, 1.0,
            "The fraction of transferred experience that arrives in the target skill; the rest is lost.");
        SCOREBOARD_ENABLED = builder.defineBoolean("scoreboard.enabled", false,
            "Mirror every player's skill levels into scoreboard objectives named es_<skill>, so datapacks and command blocks can read them.");

        STEP_UP_MILESTONES = builder.defineBoolean("milestones.stepUp", true,
            "Allow skill milestones to increase step height. Turn off to disable automatic one-block step-up. Restart the server/world after changing this setting.");

        CONFIG = builder.build();
    }
}
