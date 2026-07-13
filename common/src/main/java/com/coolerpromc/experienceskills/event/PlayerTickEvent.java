package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.stat.ModStats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;

public class PlayerTickEvent {
    public static void onPlayerTickEnd(Player entity) {
        if (entity.isUnderWater()){
            entity.awardStat(ModStats.UNDER_WATER_TIME.get(), 1);
        }

        if (entity.fishing != null && entity.fishing.currentState == FishingHook.FishHookState.BOBBING){
            entity.awardStat(ModStats.FISHING_TIME.get(), 1);
        }
    }
}
