package com.coolerpromc.experienceskills.skill;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.ExperienceSkillsAPI;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.config.value.Milestone;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class SkillAdvancements {
    public static void onLevelChange(ServerPlayer player, ExperienceType type, int oldLevel, int newLevel){
        if (newLevel > oldLevel){
            check(player);
        }
    }

    public static void check(ServerPlayer player){
        List<ExperienceType> types = ExperienceSkillsAPI.getEnabledTypes();
        if (types.isEmpty()) return;

        int highest = 0;
        boolean anyMaxed = false;
        boolean allAtTen = true;
        boolean anyMilestone = false;
        boolean anyPoints = false;

        for (ExperienceType type : types){
            int level = SkillManager.getLevel(player, type);
            highest = Math.max(highest, level);
            anyMaxed |= level >= type.getMaxLevel();
            allAtTen &= level >= 10;
            anyPoints |= SkillManager.getPoints(player, type) > 0;
            for (Milestone milestone : type.getConfig().milestones()){
                anyMilestone |= level >= milestone.level();
            }
        }

        List<String> earned = new ArrayList<>();
        if (anyPoints) earned.add("root");
        if (highest >= 10) earned.add("apprentice");
        if (highest >= 25) earned.add("journeyman");
        if (highest >= 50) earned.add("expert");
        if (anyMaxed) earned.add("master");
        if (anyMilestone) earned.add("milestone");
        if (allAtTen) earned.add("jack_of_all_trades");

        for (String path : earned){
            grant(player, path);
        }
    }

    private static void grant(ServerPlayer player, String path){
        AdvancementHolder advancement = player.level().getServer().getAdvancements().get(Constants.id(path));
        if (advancement == null) return;

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (progress.isDone()) return;

        List<String> remaining = new ArrayList<>();
        progress.getRemainingCriteria().forEach(remaining::add);
        for (String criterion : remaining){
            player.getAdvancements().award(advancement, criterion);
        }
    }
}
