package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.config.ModCommonConfig;
import com.coolerpromc.experienceskills.data.attachment.ModDataAttachments;
import com.coolerpromc.experienceskills.entity.custom.AbstractExperienceOrb;
import com.coolerpromc.experienceskills.entity.helper.ExperienceOrbFactory;
import com.coolerpromc.experienceskills.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;

public class StatAwardEvent {
    public static void onStatsAward(Player entity, Stat<?> stat, int value) {
        if (entity instanceof ServerPlayer player){
            if (stat.getValue() == Stats.WALK_ONE_CM){
                int lastBlockCount = Services.ATTACHMENT.get(player, ModDataAttachments.LAST_BLOCK_COUNT);
                int blockRequired = ModCommonConfig.SPEED.get().xpAwardActionCount();
                int blockWalked = value / blockRequired;

                while (blockWalked > lastBlockCount + blockRequired && blockWalked % blockRequired == 0){
                    lastBlockCount += blockRequired;
                    Services.ATTACHMENT.set(player, ModDataAttachments.LAST_BLOCK_COUNT, lastBlockCount);
                    AbstractExperienceOrb.award(player.level(), player.position(), ModCommonConfig.SPEED.get().xpPointToAward(), new ExperienceOrbFactory.SpeedExperienceOrbFactory());
                }
            }
        }
    }
}
