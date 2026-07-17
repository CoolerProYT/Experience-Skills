package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.entity.custom.ModExperienceOrb;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerRespawnEvent {
    public static void onPlayerRespawn(Player player){
        if (player instanceof ServerPlayer serverPlayer){
            ModExperienceOrb.updateAllSkillStatus(serverPlayer);
        }
    }
}
