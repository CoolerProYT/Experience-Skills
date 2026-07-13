package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.api.type.ExperienceTypeRegistry;
import com.coolerpromc.experienceskills.data.attachment.ModDataAttachments;
import com.coolerpromc.experienceskills.entity.custom.AbstractExperienceOrb;
import com.coolerpromc.experienceskills.entity.custom.SpeedExperienceOrb;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class LivingDeathEvent {
    public static void onLivingDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if (livingEntity instanceof ServerPlayer player){
            dropSpeedExperience(player);

            // TODO: Temporarily code to test API
            ExperienceTypeRegistry.REGISTERED_TYPES.forEach((s, registryHolder) -> {
                int exp = SpeedExperienceOrb.getLevel(player, ModDataAttachments.INT_KEYS.get(s + "_experience"));
                int xpToDrop = Math.min(exp * 7, 100);

                player.level().addFreshEntity(registryHolder.orbFactory().createExperienceOrb(player.level(), player.position(), player.position().add(1, 0, 1), xpToDrop));
            });
        }
    }

    private static void dropSpeedExperience(ServerPlayer player){
        int exp = SpeedExperienceOrb.getLevel(player, ModDataAttachments.SPEED_EXPERIENCE);
        int xpToDrop = Math.min(exp * 7, 100);

        player.level().addFreshEntity(new SpeedExperienceOrb(player.level(), player.position(), player.position().add(1, 0, 1), xpToDrop));
    }
}
