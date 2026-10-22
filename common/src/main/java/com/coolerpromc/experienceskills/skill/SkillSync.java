package com.coolerpromc.experienceskills.skill;

import com.coolerpromc.experienceskills.api.ExperienceSkillsAPI;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import com.coolerpromc.experienceskills.data.attachment.ModDataAttachments;
import com.coolerpromc.experienceskills.event.StatAwardEvent;
import com.coolerpromc.experienceskills.platform.Services;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashMap;
import java.util.Map;

public class SkillSync {
    public static void sendConfig(ServerPlayer player){
        Map<String, SkillsConfig> configs = new LinkedHashMap<>();
        for (ExperienceType type : ExperienceSkillsAPI.getAllTypes()){
            configs.put(type.getSerializedName(), type.getConfig());
        }
        Services.ATTACHMENT.set(player, ModDataAttachments.SERVER_SKILL_CONFIG, configs);
    }

    public static void updateProgress(ServerPlayer player){
        Map<String, Integer> progress = new LinkedHashMap<>();
        for (ExperienceType type : ExperienceSkillsAPI.getEnabledTypes()){
            SkillsConfig config = type.getConfig();
            if (config.xpAwardActionCount() <= 0) continue;

            double total = StatAwardEvent.weightedProgress(player, config);
            progress.put(type.getSerializedName(), (int) Math.floor(total % config.xpAwardActionCount()));
        }

        if (!progress.equals(Services.ATTACHMENT.get(player, ModDataAttachments.SKILL_PROGRESS))){
            Services.ATTACHMENT.set(player, ModDataAttachments.SKILL_PROGRESS, progress);
        }
    }
}
