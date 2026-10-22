package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.api.ExperienceSkillsAPI;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import com.coolerpromc.experienceskills.entity.custom.ModExperienceOrb;
import com.coolerpromc.experienceskills.skill.SkillManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gamerules.GameRules;

public class LivingDeathEvent {
    public static void onLivingDeath(LivingEntity livingEntity, DamageSource damageSource){
        if (!(livingEntity instanceof ServerPlayer player) || player.isSpectator()) return;
        if (player.level().getGameRules().get(GameRules.KEEP_INVENTORY)) return;

        for (ExperienceType type : ExperienceSkillsAPI.getEnabledTypes()){
            SkillsConfig config = type.getConfig();
            if (!config.dropOnDeath()) continue;

            int points = SkillManager.getPoints(player, type);
            if (points <= 0) continue;

            int level = type.levelFor(points);
            int lost = Math.round(points * config.deathPenalty());
            int dropped = Math.min(Math.min(level * 7, config.deathDropCap()), lost);

            SkillManager.setPoints(player, type, points - lost, false);
            if (dropped > 0){
                ModExperienceOrb.award(player.level(), player.position(), dropped, type);
            }
        }
    }
}
