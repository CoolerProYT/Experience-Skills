package com.coolerpromc.experienceskills.client.model.tint;

import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.data.component.ModDataComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record ExperienceBottleTintSource(int defaultColor) implements ItemTintSource {
    public static final MapCodec<ExperienceBottleTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Codec.INT.fieldOf("defaultColor").forGetter(ExperienceBottleTintSource::defaultColor)
    ).apply(i, ExperienceBottleTintSource::new));

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        ExperienceType experienceType = itemStack.get(ModDataComponents.EXPERIENCE_TYPE.get());
        if (experienceType != null){
            return ARGB.color(0xFF, experienceType.color());
        }
        return defaultColor;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
