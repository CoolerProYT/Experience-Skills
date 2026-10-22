package com.coolerpromc.experienceskills.skill;

import com.coolerpromc.experienceskills.api.ExperienceSkillsAPI;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.config.ModGeneralConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class SkillScoreboard {
    public static void onLevelChange(ServerPlayer player, ExperienceType type, int oldLevel, int newLevel){
        if (ModGeneralConfig.SCOREBOARD_ENABLED.get()){
            write(player, type, newLevel);
        }
    }

    public static void syncAll(ServerPlayer player){
        if (!ModGeneralConfig.SCOREBOARD_ENABLED.get()) return;
        for (ExperienceType type : ExperienceSkillsAPI.getEnabledTypes()){
            write(player, type, SkillManager.getLevel(player, type));
        }
    }

    private static void write(ServerPlayer player, ExperienceType type, int level){
        ServerScoreboard scoreboard = player.level().getServer().getScoreboard();
        String name = "es_" + type.getSerializedName();
        Objective objective = scoreboard.getObjective(name);
        if (objective == null){
            Component displayName = Component.translatableWithFallback("skill.experienceskills." + type.getSerializedName(), type.getSerializedName());
            objective = scoreboard.addObjective(name, ObjectiveCriteria.DUMMY, displayName, ObjectiveCriteria.RenderType.INTEGER, false, null);
        }
        scoreboard.getOrCreatePlayerScore(player, objective).set(level);
    }
}
