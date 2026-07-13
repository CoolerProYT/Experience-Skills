package com.coolerpromc.experienceskills.config;

import com.coolerpromc.coolerconfig.Constants;
import com.coolerpromc.coolerconfig.config.*;
import com.coolerpromc.experienceskills.api.type.ExperienceTypeRegistry;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class ModCommonConfig {
    public static ConfigValue<SkillsConfig> SPEED;

    public static ConfigSpec CONFIG;

    public static void init(){
        ConfigBuilder builder = ConfigSpec.builder(Constants.MOD_ID, ConfigFormat.JSON5).side(ConfigSide.COMMON)
            .comment("Disclaimer: Some skills from addon mods by other author might not use these config values, only report to original author if there is a bug related to built-in skills. \n incrementPerLevel: Percentage of the skills increase per level based on base value.\n xpAwardActionCount: Number of actions done before awarding xp to player, e.g. walk 100 blocks\n xpPointToAward: Xp point to award to the player after reached xpAwardActionCount.");

        SPEED = builder.defineCodec("speed", SkillsConfig.CODEC, new SkillsConfig(true, 0.02f,  100,  10, 16711680), "Speed skill configuration");

        // TODO: Temporarily code to test API
        ExperienceTypeRegistry.REGISTERED_TYPES.forEach((s, registryHolder) -> {
            builder.defineCodec(s, SkillsConfig.CODEC, registryHolder.config(), s.substring(0, 1).toUpperCase() + s.substring(1) + " configuration");
        });

        CONFIG = builder.build();
    }

    public static Optional<SkillsConfig> getConfigByPath(String path) {
        try {
            Object value = CONFIG.get(path);
            if (value instanceof SkillsConfig config) {
                return Optional.of(config);
            }
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
