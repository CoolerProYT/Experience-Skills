package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.entity.custom.AbstractExperienceOrb;
import net.minecraft.server.level.ServerPlayer;

public class PlayerJoinEvent {
    public static void onPlayerJoined(ServerPlayer player) {
        AbstractExperienceOrb.updateAllSkillStatus(player);
    }
}
