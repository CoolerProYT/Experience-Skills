package com.coolerpromc.experienceskills.config.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SkillsConfig(boolean enabled, float incrementPerLevel, int xpAwardActionCount, int xpPointToAward, int rgbColor) {
    public static final Codec<SkillsConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.BOOL.fieldOf("enabled").forGetter(SkillsConfig::enabled),
        Codec.FLOAT.fieldOf("incrementPerLevel").forGetter(SkillsConfig::incrementPerLevel),
        Codec.INT.fieldOf("xpAwardActionCount").forGetter(SkillsConfig::xpAwardActionCount),
        Codec.INT.fieldOf("xpPointToAward").forGetter(SkillsConfig::xpPointToAward),
        Codec.INT.fieldOf("rgbColor").forGetter(SkillsConfig::rgbColor)
    ).apply(i, SkillsConfig::new));
}
