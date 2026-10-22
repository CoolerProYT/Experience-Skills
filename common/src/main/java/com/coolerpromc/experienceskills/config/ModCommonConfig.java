package com.coolerpromc.experienceskills.config;

import com.coolerpromc.coolerconfig.config.ConfigBuilder;
import com.coolerpromc.coolerconfig.config.ConfigFormat;
import com.coolerpromc.coolerconfig.config.ConfigSide;
import com.coolerpromc.coolerconfig.config.ConfigSpec;
import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.type.ExperienceTypeRegistry;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;

import java.util.Optional;

public class ModCommonConfig {
    public static ConfigSpec CONFIG;

    public static void init(){
        ConfigBuilder builder = ConfigSpec.builder(Constants.MODID, ConfigFormat.JSON5).side(ConfigSide.COMMON)
            .comment("""
                 Every skill is driven by a stat: each time that stat advances by xpAwardActionCount, the player is awarded xpPointToAward experience points.

                 enabled: Whether the skill is active. A disabled skill awards no experience and its attribute bonus is removed.
                 incrementPerLevel: How much the attribute changes per level, applied according to 'operation'. With add_multiplied_base, 0.02 means +2% of the base value per level. May be negative.
                 xpAwardActionCount: How much the stat below must advance before experience is awarded, e.g. 100 block_walked.
                 xpPointToAward: Experience points to award to the player each time xpAwardActionCount is reached.
                 rgbColor: Colour of the experience orb and bar, as an RGB integer (0xFF0000 / 16711680 is red).
                 operation: How the bonus is applied to the attribute. One of add_value (flat), add_multiplied_base (percentage of the attribute's base value) or add_multiplied_total (percentage of the final value).
                 maxLevel: Level cap. Once reached, the skill stops awarding experience.
                 stat: The stat that drives this skill. Built-in ones are experienceskills:block_walked, experienceskills:block_broken and experienceskills:entity_killed. Any vanilla custom stat from the Statistics screen also works, e.g. minecraft:jump or minecraft:fish_caught — mind the units, as distances are counted in centimetres and damage in tenths of a heart.
                 dropOnDeath: Whether dying costs experience in this skill. Ignored when the keepInventory gamerule is on.
                 additionalStats: Extra stats that also feed the skill, e.g. [{ stat: "minecraft:sprint_one_cm", weight: 0.5 }]. Progress is stat + the sum of weight x each extra stat.
                 curve: How many points each level costs. type is one of vanilla (the player XP curve), linear (7 + 2 x level), flat (10 per level) or exponential (7 x 1.08^level); multiplier scales every level's cost.
                 scaling: How the bonus grows with level: linear, sqrt or logarithmic. All three give the same bonus at maxLevel; sqrt and logarithmic give more of it early on.
                 deathPenalty: With dropOnDeath, the fraction of points lost on death, from 0.0 to 1.0. 1.0 loses everything, like vanilla.
                 deathDropCap: With dropOnDeath, the most points dropped as orbs that can be picked back up (vanilla drops at most level x 7, capped at 100).
                 milestones: Perks unlocked at a level and kept while at or above it. Each is either { level: 50, effect: "minecraft:dolphins_grace", amplifier: 0 } or { level: 50, attribute: "minecraft:step_height", amount: 0.5, operation: "add_value" }.""");

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
