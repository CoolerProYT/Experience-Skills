package com.coolerpromc.experienceskills.config.value;

import com.coolerpromc.experienceskills.Constants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
 * @param dropOnDeath        whether dying costs experience in this skill
 * @param additionalStats    extra stats that also feed the skill, each with a weight
 * @param curve              how many points each level costs
 * @param scaling            how the bonus grows with level
 * @param deathPenalty       fraction of points lost on death, from 0 to 1
 * @param deathDropCap       most points dropped as orbs on death
 * @param milestones         perks unlocked at specific levels
 */
public record SkillsConfig(boolean enabled, float incrementPerLevel, int xpAwardActionCount, int xpPointToAward, int rgbColor, AttributeModifier.Operation operation, int maxLevel, Identifier stat, boolean dropOnDeath,
                           List<WeightedStat> additionalStats, XpCurve curve, BonusScaling scaling, float deathPenalty, int deathDropCap, List<Milestone> milestones){
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
        Codec.BOOL.fieldOf("dropOnDeath").forGetter(SkillsConfig::dropOnDeath),
        visible(WeightedStat.CODEC.listOf(), "additionalStats", List.of()).forGetter(SkillsConfig::additionalStats),
        visible(XpCurve.CODEC, "curve", XpCurve.DEFAULT).forGetter(SkillsConfig::curve),
        visible(BonusScaling.CODEC, "scaling", BonusScaling.LINEAR).forGetter(SkillsConfig::scaling),
        visible(Codec.floatRange(0, 1), "deathPenalty", 1.0f).forGetter(SkillsConfig::deathPenalty),
        visible(Codec.INT, "deathDropCap", 100).forGetter(SkillsConfig::deathDropCap),
        visible(Milestone.CODEC.listOf(), "milestones", List.of()).forGetter(SkillsConfig::milestones)
    ).apply(i, SkillsConfig::new));

    public static final StreamCodec<ByteBuf, SkillsConfig> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    /** Placeholder configuration for the special {@code vanilla} HUD entry; not a real, earnable skill. */
    public static final SkillsConfig VANILLA = new SkillsConfig(true, 0, 0, 0, -1, AttributeModifier.Operation.ADD_VALUE, Integer.MAX_VALUE, Constants.id("empty"), true);

    public static <T> MapCodec<T> visible(Codec<T> codec, String name, T defaultValue){
        return codec.optionalFieldOf(name).xmap(o -> o.orElse(defaultValue), Optional::of);
    }

    public SkillsConfig {
        additionalStats = List.copyOf(additionalStats);
        milestones = List.copyOf(milestones);
    }

    public SkillsConfig(boolean enabled, float incrementPerLevel, int xpAwardActionCount, int xpPointToAward, int rgbColor, AttributeModifier.Operation operation, int maxLevel, Identifier stat, boolean dropOnDeath){
        this(enabled, incrementPerLevel, xpAwardActionCount, xpPointToAward, rgbColor, operation, maxLevel, stat, dropOnDeath, List.of(), XpCurve.DEFAULT, BonusScaling.LINEAR, 1.0f, 100, List.of());
    }

    public SkillsConfig withAdditionalStat(Identifier stat, double weight){
        List<WeightedStat> stats = new ArrayList<>(additionalStats);
        stats.add(new WeightedStat(stat, weight));
        return new SkillsConfig(enabled, incrementPerLevel, xpAwardActionCount, xpPointToAward, rgbColor, operation, maxLevel, this.stat, dropOnDeath, stats, curve, scaling, deathPenalty, deathDropCap, milestones);
    }

    public SkillsConfig withCurve(XpCurve curve){
        return new SkillsConfig(enabled, incrementPerLevel, xpAwardActionCount, xpPointToAward, rgbColor, operation, maxLevel, stat, dropOnDeath, additionalStats, curve, scaling, deathPenalty, deathDropCap, milestones);
    }

    public SkillsConfig withScaling(BonusScaling scaling){
        return new SkillsConfig(enabled, incrementPerLevel, xpAwardActionCount, xpPointToAward, rgbColor, operation, maxLevel, stat, dropOnDeath, additionalStats, curve, scaling, deathPenalty, deathDropCap, milestones);
    }

    public SkillsConfig withDeathPenalty(float deathPenalty, int deathDropCap){
        return new SkillsConfig(enabled, incrementPerLevel, xpAwardActionCount, xpPointToAward, rgbColor, operation, maxLevel, stat, dropOnDeath, additionalStats, curve, scaling, deathPenalty, deathDropCap, milestones);
    }

    public SkillsConfig withMilestones(Milestone... milestones){
        List<Milestone> list = new ArrayList<>(this.milestones);
        list.addAll(List.of(milestones));
        return new SkillsConfig(enabled, incrementPerLevel, xpAwardActionCount, xpPointToAward, rgbColor, operation, maxLevel, stat, dropOnDeath, additionalStats, curve, scaling, deathPenalty, deathDropCap, list);
    }

    public List<WeightedStat> allStats(){
        List<WeightedStat> stats = new ArrayList<>(additionalStats.size() + 1);
        stats.add(new WeightedStat(stat, 1.0));
        stats.addAll(additionalStats);
        return stats;
    }

    public double bonusForLevel(int level){
        return scaling.apply(level, maxLevel, incrementPerLevel);
    }
}
