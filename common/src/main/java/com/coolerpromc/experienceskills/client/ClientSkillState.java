package com.coolerpromc.experienceskills.client;

import com.coolerpromc.experienceskills.api.ExperienceSkillsAPI;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.client.toast.SkillLevelUpToast;
import com.coolerpromc.experienceskills.config.ModClientConfig;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import com.coolerpromc.experienceskills.data.attachment.ModDataAttachments;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.skill.SkillManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.HashMap;
import java.util.Map;

public class ClientSkillState {
    private static final int SETTLE_TICKS = 40;

    private static final Map<ExperienceType, Integer> LAST_LEVELS = new HashMap<>();
    private static LocalPlayer lastPlayer;
    private static Map<String, SkillsConfig> lastConfig;
    private static boolean usingServerConfig;
    private static int settleTicks;


    public static void tick(Minecraft minecraft){
        LocalPlayer player = minecraft.player;
        if (player == null){
            if (usingServerConfig){
                ExperienceType.ALL.forEach(ExperienceType::resetSyncedConfig);
                usingServerConfig = false;
            }
            lastPlayer = null;
            lastConfig = null;
            LAST_LEVELS.clear();
            return;
        }

        Map<String, SkillsConfig> config = Services.ATTACHMENT.get(player, ModDataAttachments.SERVER_SKILL_CONFIG);
        if (config != lastConfig && config != null && !config.isEmpty()){
            config.forEach((name, skillConfig) -> ExperienceType.byName(name).ifPresent(type -> type.applySyncedConfig(skillConfig)));
            lastConfig = config;
            usingServerConfig = true;
            settleTicks = SETTLE_TICKS;
        }

        if (player != lastPlayer){
            lastPlayer = player;
            settleTicks = SETTLE_TICKS;
        }

        boolean announce = settleTicks <= 0 && ModClientConfig.LEVEL_UP_TOASTS.get();
        if (settleTicks > 0) settleTicks--;

        for (ExperienceType type : ExperienceSkillsAPI.getEnabledTypes()){
            int level = SkillManager.getLevel(player, type);
            Integer previous = LAST_LEVELS.put(type, level);
            if (announce && previous != null && level > previous){
                SkillLevelUpToast.show(minecraft.getToastManager(), type, level);
            }
        }
    }
}
