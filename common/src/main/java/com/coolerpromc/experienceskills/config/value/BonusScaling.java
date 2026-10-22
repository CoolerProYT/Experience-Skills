package com.coolerpromc.experienceskills.config.value;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum BonusScaling implements StringRepresentable {
    LINEAR("linear"),
    SQRT("sqrt"),
    LOGARITHMIC("logarithmic");

    public static final Codec<BonusScaling> CODEC = StringRepresentable.fromEnum(BonusScaling::values);
    private final String name;

    BonusScaling(String name){
        this.name = name;
    }

    public double apply(int level, int maxLevel, double incrementPerLevel){
        if (level <= 0) return 0;
        if (maxLevel <= 0) return incrementPerLevel * level;

        return switch (this){
            case LINEAR -> incrementPerLevel * level;
            case SQRT -> incrementPerLevel * Math.sqrt((double) level * maxLevel);
            case LOGARITHMIC -> incrementPerLevel * maxLevel * Math.log1p(level) / Math.log1p(maxLevel);
        };
    }

    @Override
    public @NonNull String getSerializedName(){
        return name;
    }
}
