package com.coolerpromc.experienceskills.config.value;

import com.coolerpromc.experienceskills.util.XpMath;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public record XpCurve(Type type, double multiplier){
    public static final XpCurve DEFAULT = new XpCurve(Type.VANILLA, 1.0);

    public static final Codec<XpCurve> CODEC = RecordCodecBuilder.create(i -> i.group(
        SkillsConfig.visible(Type.CODEC, "type", Type.VANILLA).forGetter(XpCurve::type),
        SkillsConfig.visible(Codec.DOUBLE, "multiplier", 1.0).forGetter(XpCurve::multiplier)
    ).apply(i, XpCurve::new));

    private static final int MAX_LEVEL_SEARCH = 100_000;

    public int xpNeededForNextLevel(int level){
        double base = switch (type){
            case VANILLA -> XpMath.getXpNeededForNextLevel(level);
            case LINEAR -> 7 + level * 2;
            case FLAT -> 10;
            case EXPONENTIAL -> 7 * Math.pow(1.08, level);
        };
        double scaled = base * multiplier;
        if (scaled >= Integer.MAX_VALUE / 4.0) return Integer.MAX_VALUE / 4;
        return Math.max(1, (int) Math.round(scaled));
    }

    public int totalForLevel(int level){
        if (isPlainVanilla()) return XpMath.getTotalForLevel(level);

        long total = 0;
        for (int l = 0; l < level; l++){
            total += xpNeededForNextLevel(l);
            if (total >= Integer.MAX_VALUE) return Integer.MAX_VALUE;
        }
        return (int) total;
    }

    public int level(int totalPoints){
        if (totalPoints <= 0) return 0;
        if (isPlainVanilla()) return XpMath.getLevel(totalPoints);

        long total = 0;
        int level = 0;
        while (level < MAX_LEVEL_SEARCH){
            total += xpNeededForNextLevel(level);
            if (total > totalPoints) break;
            level++;
        }
        return level;
    }

    public float progress(int totalPoints){
        int level = level(totalPoints);
        return (totalPoints - totalForLevel(level)) / (float) xpNeededForNextLevel(level);
    }

    private boolean isPlainVanilla(){
        return type == Type.VANILLA && multiplier == 1.0;
    }

    public enum Type implements StringRepresentable {
        VANILLA("vanilla"),
        LINEAR("linear"),
        FLAT("flat"),
        EXPONENTIAL("exponential");

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        private final String name;

        Type(String name){
            this.name = name;
        }

        @Override
        public @NonNull String getSerializedName(){
            return name;
        }
    }
}
