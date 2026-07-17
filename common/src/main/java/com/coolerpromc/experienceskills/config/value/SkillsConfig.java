package com.coolerpromc.experienceskills.config.value;

import com.coolerpromc.experienceskills.Constants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * The tunable definition of a skill. What a plugin passes to
 * {@link com.coolerpromc.experienceskills.api.type.IExperienceTypeRegistry#register} is the <i>default</i>:
 * it is written to the config file on first run, and players may override every field afterwards.
 *
 * @param enabled            whether the skill is active; a disabled skill awards no experience and its
 *                           attribute bonus is removed
 * @param incrementPerLevel  how much the attribute changes per level, applied via {@code operation};
 *                           with {@code ADD_MULTIPLIED_BASE}, {@code 0.02} means +2% of the base value
 *                           per level. May be negative.
 * @param xpAwardActionCount how far {@code stat} must advance before an award fires
 * @param xpPointToAward     experience points awarded each time {@code xpAwardActionCount} is reached
 * @param rgbColor           orb and HUD-bar tint, as a packed RGB integer
 * @param operation          how the level bonus combines with the attribute
 * @param maxLevel           the level cap; awards stop once it is reached
 * @param stat               the {@link Identifier} of the <b>custom</b> stat that drives this skill; the
 *                           skill gains XP as this stat increases. Registry stat types (blocks mined,
 *                           items used) cannot be used directly — pool them into a custom stat first.
 * @param dropOnDeath        whether the experiences should be dropped like vanilla on player death.
 */
public record SkillsConfig(boolean enabled, float incrementPerLevel, int xpAwardActionCount, int xpPointToAward, int rgbColor, AttributeModifier.Operation operation, int maxLevel, Identifier stat, boolean dropOnDeath) {
    /** Codec used to read and write a skill's configuration to disk. */
    public static final Codec<SkillsConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.BOOL.fieldOf("enabled").forGetter(SkillsConfig::enabled),
        Codec.FLOAT.fieldOf("incrementPerLevel").forGetter(SkillsConfig::incrementPerLevel),
        Codec.INT.fieldOf("xpAwardActionCount").forGetter(SkillsConfig::xpAwardActionCount),
        Codec.INT.fieldOf("xpPointToAward").forGetter(SkillsConfig::xpPointToAward),
        Codec.INT.fieldOf("rgbColor").forGetter(SkillsConfig::rgbColor),
        AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(SkillsConfig::operation),
        Codec.INT.fieldOf("maxLevel").forGetter(SkillsConfig::maxLevel),
        Identifier.CODEC.fieldOf("stat").forGetter(SkillsConfig::stat),
        Codec.BOOL.fieldOf("dropOnDeath").forGetter(SkillsConfig::dropOnDeath)
    ).apply(i, SkillsConfig::new));

    /** Placeholder configuration for the special {@code vanilla} HUD entry; not a real, earnable skill. */
    public static final SkillsConfig VANILLA = new SkillsConfig(true, 0, 0, 0, -1, AttributeModifier.Operation.ADD_VALUE, Integer.MAX_VALUE, Constants.id("empty"), true);
}
