package com.coolerpromc.experienceskills.config.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Optional;

public record Milestone(int level, Optional<Identifier> effect, int amplifier, Optional<Identifier> attribute, double amount, AttributeModifier.Operation operation){
    public static final Codec<Milestone> CODEC = RecordCodecBuilder.<Milestone>create(i -> i.group(
        Codec.INT.fieldOf("level").forGetter(Milestone::level),
        Identifier.CODEC.optionalFieldOf("effect").forGetter(Milestone::effect),
        Codec.INT.optionalFieldOf("amplifier", 0).forGetter(Milestone::amplifier),
        Identifier.CODEC.optionalFieldOf("attribute").forGetter(Milestone::attribute),
        Codec.DOUBLE.optionalFieldOf("amount", 0.0).forGetter(Milestone::amount),
        AttributeModifier.Operation.CODEC.optionalFieldOf("operation", AttributeModifier.Operation.ADD_VALUE).forGetter(Milestone::operation)
    ).apply(i, Milestone::new)).validate(Milestone::validate);

    public static Milestone effect(int level, Identifier effect, int amplifier){
        return new Milestone(level, Optional.of(effect), amplifier, Optional.empty(), 0, AttributeModifier.Operation.ADD_VALUE);
    }

    public static Milestone attribute(int level, Identifier attribute, double amount, AttributeModifier.Operation operation){
        return new Milestone(level, Optional.empty(), 0, Optional.of(attribute), amount, operation);
    }

    private static DataResult<Milestone> validate(Milestone milestone){
        if (milestone.effect.isPresent() == milestone.attribute.isPresent()){
            return DataResult.error(() -> "A milestone must set exactly one of 'effect' or 'attribute' (level " + milestone.level + ")");
        }
        return DataResult.success(milestone);
    }
}
