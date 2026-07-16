package com.coolerpromc.experienceskills.client.model.tint;

import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.data.component.ModDataComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.*;

public record ExperienceBottleBorderTintSource(int defaultColor) implements ItemTintSource {
    public static final MapCodec<ExperienceBottleBorderTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Codec.INT.fieldOf("defaultColor").forGetter(ExperienceBottleBorderTintSource::defaultColor)
    ).apply(i, ExperienceBottleBorderTintSource::new));

    @Override
    public int calculate(@NonNull ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        ExperienceType experienceType = itemStack.get(ModDataComponents.EXPERIENCE_TYPE.get());
        if (experienceType != null) {
            int color = experienceType.color();
            return computeBorderColor(color);
        }
        return defaultColor;
    }

    private int computeBorderColor(int baseColor) {
        int baseR = (baseColor >> 16) & 0xFF;
        int baseG = (baseColor >> 8) & 0xFF;
        int baseB = baseColor & 0xFF;

        float[] hsb = Color.RGBtoHSB(baseR, baseG, baseB, null);

        float hue = hsb[0] + 0.08F; // shift hue slightly (0.08 ≈ 30° on the color wheel)
        if (hue > 1.0F) hue -= 1.0F;

        float saturation = Math.max(0F, hsb[1] - 0.3F); // desaturate a bit
        float brightness = Math.min(1F, hsb[2] + 0.3F);  // brighten toward the "highlight" look

        return Color.HSBtoRGB(hue, saturation, brightness) | 0xFF000000;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
