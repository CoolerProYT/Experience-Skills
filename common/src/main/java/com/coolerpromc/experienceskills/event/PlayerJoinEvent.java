package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.entity.custom.ModExperienceOrb;
import com.coolerpromc.experienceskills.stat.ModStats;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;

public class PlayerJoinEvent {
    public static void onPlayerJoined(ServerPlayer player) {
        backfill(player, ModStats.BLOCK_WALKED.get(), Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.CROUCH_ONE_CM);
        backfill(player, ModStats.WATER_WALKED.get(), Stats.WALK_ON_WATER_ONE_CM, Stats.WALK_UNDER_WATER_ONE_CM, Stats.SWIM_ONE_CM);
        ModExperienceOrb.updateAllSkillStatus(player);
    }

    private static void backfill(ServerPlayer player, Identifier target, Identifier... sources) {
        if (player.getStats().getValue(Stats.CUSTOM.get(target)) != 0) return;
        int totalCm = 0;
        for (Identifier src : sources) {
            totalCm += player.getStats().getValue(Stats.CUSTOM.get(src));
        }
        player.getStats().setValue(player, Stats.CUSTOM.get(target), totalCm / 100);
    }
}
