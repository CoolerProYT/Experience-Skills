package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.skill.SkillManager;
import com.coolerpromc.experienceskills.skill.SkillSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerRespawnEvent {
    public static void onPlayerRespawn(Player player){
        if (player instanceof ServerPlayer serverPlayer){
            SkillSync.sendConfig(serverPlayer);
            SkillManager.updateAll(serverPlayer);
            SkillSync.updateProgress(serverPlayer);
        }
    }
}
