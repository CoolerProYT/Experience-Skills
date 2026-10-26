package com.coolerpromc.experienceskills.skill;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.ExperienceSkillsAPI;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.config.ModGeneralConfig;
import com.coolerpromc.experienceskills.config.value.Milestone;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SkillMilestones {
    private static final Set<Identifier> WARNED = new HashSet<>();


    public static void apply(ServerPlayer player, ExperienceType type, int level){
        List<Milestone> milestones = type.getConfig().milestones();
        for (int i = 0; i < milestones.size(); i++){
            Milestone milestone = milestones.get(i);
            Identifier modifierId = modifierId(type, i);
            boolean active = type.isEnabled() && level >= milestone.level();

            milestone.attribute().flatMap(id -> lookup(BuiltInRegistries.ATTRIBUTE.get(id).map(h -> (Holder<Attribute>) h), id)).ifPresent(attribute -> {
                AttributeInstance instance = player.getAttribute(attribute);
                if (instance == null) return;

                instance.removeModifier(modifierId);
                if (active && (!attribute.equals(Attributes.STEP_HEIGHT) || ModGeneralConfig.STEP_UP_MILESTONES.get())){
                    instance.addTransientModifier(new AttributeModifier(modifierId, milestone.amount(), milestone.operation()));
                }
            });

            milestone.effect().flatMap(id -> lookup(BuiltInRegistries.MOB_EFFECT.get(id).map(h -> (Holder<MobEffect>) h), id)).ifPresent(effect -> {
                if (active){
                    grantEffect(player, effect, milestone.amplifier());
                } else {
                    revokeEffect(player, effect);
                }
            });
        }
    }

    public static void refreshEffects(ServerPlayer player){
        for (ExperienceType type : ExperienceSkillsAPI.getEnabledTypes()){
            if (type.getConfig().milestones().stream().noneMatch(m -> m.effect().isPresent())) continue;
            apply(player, type, SkillManager.getLevel(player, type));
        }
    }

    private static Identifier modifierId(ExperienceType type, int index){
        return Constants.id(type.getSerializedName() + "_milestone_" + index);
    }

    private static void grantEffect(ServerPlayer player, Holder<MobEffect> effect, int amplifier){
        MobEffectInstance existing = player.getEffect(effect);
        if (existing != null && (existing.getAmplifier() > amplifier || (existing.isInfiniteDuration() && existing.getAmplifier() == amplifier))){
            return;
        }
        player.addEffect(new MobEffectInstance(effect, MobEffectInstance.INFINITE_DURATION, amplifier, true, false, true));
    }

    private static void revokeEffect(ServerPlayer player, Holder<MobEffect> effect){
        MobEffectInstance existing = player.getEffect(effect);
        if (existing != null && existing.isInfiniteDuration() && existing.isAmbient()){
            player.removeEffect(effect);
        }
    }

    private static <T> Optional<Holder<T>> lookup(Optional<Holder<T>> holder, Identifier id){
        if (holder.isEmpty() && WARNED.add(id)){
            Constants.LOGGER.warn("Skill milestone refers to unknown registry entry {}; it will be ignored.", id);
        }
        return holder;
    }
}
