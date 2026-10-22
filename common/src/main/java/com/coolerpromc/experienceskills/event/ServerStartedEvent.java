package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.api.ExperienceSkillsAPI;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.config.value.WeightedStat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;

public class ServerStartedEvent {
    // Ensure is a valid stat
    public static void onServerStarted(MinecraftServer server){
        for (ExperienceType type : ExperienceSkillsAPI.getAllTypes()){
            for (WeightedStat source : type.getConfig().allStats()){
                if (!BuiltInRegistries.CUSTOM_STAT.containsKey(source.stat())){
                    throw new IllegalStateException("Skill '" + type.getSerializedName() + "' is driven by '" + source.stat() + "', which is not a registered custom stat. Check the skill's 'stat' and 'additionalStats' in the config.");
                }
            }
        }
    }
}
