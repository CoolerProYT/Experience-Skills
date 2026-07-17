package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.api.type.ExperienceTypeRegistry;
import com.coolerpromc.experienceskills.data.attachment.ModDataAttachments;
import com.coolerpromc.experienceskills.entity.ModEntities;
import com.coolerpromc.experienceskills.entity.custom.ModExperienceOrb;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;

public class LivingDeathEvent {
    public static void onLivingDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if (livingEntity instanceof ServerPlayer player){
            ExperienceTypeRegistry.REGISTERED_TYPES.forEach((s, registryHolder) -> {
                int exp = ModExperienceOrb.getLevel(player, ModDataAttachments.intById(s + "_experience"));
                int xpToDrop = Math.min(exp * 7, 100);

                ModExperienceOrb orb = ModEntities.byName(s + "_experience_orb").get().create(player.level(), EntitySpawnReason.TRIGGERED);
                if (orb != null && registryHolder.config().enabled() && ExperienceType.byName(s).get().getConfig().dropOnDeath() && xpToDrop > 0){
                    orb.spawn(player.level(), player.position(), player.position().add(1, 0, 1), xpToDrop);
                }
            });
        }
    }
}
