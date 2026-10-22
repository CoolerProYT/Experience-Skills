package com.coolerpromc.experienceskills.effect;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.event.SkillEvents;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

public class ModEffects {
    public static final RegistryHandler<MobEffect, MobEffect> SKILL_INSIGHT = Services.REGISTRY.registerMobEffect("skill_insight", () -> new SkillInsightEffect(MobEffectCategory.BENEFICIAL, 0x7CFC00));

    public static final RegistryHandler<Potion, Potion> SKILL_INSIGHT_POTION = Services.REGISTRY.registerPotion("skill_insight", () -> new Potion("skill_insight", new MobEffectInstance(SKILL_INSIGHT.holder(), 3600)));
    public static final RegistryHandler<Potion, Potion> LONG_SKILL_INSIGHT_POTION = Services.REGISTRY.registerPotion("long_skill_insight", () -> new Potion("skill_insight", new MobEffectInstance(SKILL_INSIGHT.holder(), 9600)));

    public static void init(){
        Constants.LOGGER.info("Registering effects and potions.");

        SkillEvents.registerModifyGain((player, type, amount) -> {
            MobEffectInstance insight = player.getEffect(SKILL_INSIGHT.holder());
            return insight == null ? amount : amount * (insight.getAmplifier() + 2);
        });
    }

    private static class SkillInsightEffect extends MobEffect {
        SkillInsightEffect(MobEffectCategory category, int color){
            super(category, color);
        }
    }
}
