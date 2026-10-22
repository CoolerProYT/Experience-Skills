package com.coolerpromc.experienceskills.config.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record WeightedStat(Identifier stat, double weight){
    public static final Codec<WeightedStat> CODEC = RecordCodecBuilder.create(i -> i.group(
        Identifier.CODEC.fieldOf("stat").forGetter(WeightedStat::stat),
        Codec.DOUBLE.optionalFieldOf("weight", 1.0).forGetter(WeightedStat::weight)
    ).apply(i, WeightedStat::new));
}
