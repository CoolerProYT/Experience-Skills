package com.coolerpromc.experienceskills.entity.custom;

import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.entity.ModEntities;
import com.coolerpromc.experienceskills.data.component.ModDataComponents;
import com.coolerpromc.experienceskills.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ModThrownExperienceBottle extends ThrowableItemProjectile {

    public ModThrownExperienceBottle(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public ModThrownExperienceBottle(Level level, double x, double y, double z, ItemStack itemStack, ExperienceType experienceType) {
        super(ModEntities.EXPERIENCE_BOTTLE.get(), x, y, z, level, itemStack);
    }

    public ModThrownExperienceBottle(Level level, LivingEntity mob, ItemStack itemStack, ExperienceType experienceType) {
        super(ModEntities.EXPERIENCE_BOTTLE.get(), mob, level, itemStack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.EXPERIENCE_BOTTLE.get();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.07;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (this.level() instanceof ServerLevel level) {
            level.levelEvent(2002, this.blockPosition(), -13083194);
            // The projectile item persists through chunk unloads and world reloads.
            ExperienceType experienceType = this.getItem().get(ModDataComponents.EXPERIENCE_TYPE.get());
            if (experienceType == null || experienceType == ExperienceType.VANILLA || !experienceType.isEnabled()) {
                this.discard();
                return;
            }
            Integer stored = this.getItem().get(ModDataComponents.STORED_XP.get());
            int xpCount = stored == null ? 3 + this.random.nextInt(5) + this.random.nextInt(5) : Math.clamp(stored, 1, 1000000);
            if (hitResult instanceof BlockHitResult blockHitResult) {
                Vec3 blockNormalHit = blockHitResult.getDirection().getUnitVec3();
                ModExperienceOrb.awardWithDirection(level, hitResult.getLocation(), blockNormalHit, xpCount, experienceType, false);
            } else {
                ModExperienceOrb.awardWithDirection(level, hitResult.getLocation(), this.getDeltaMovement().scale(-1.0), xpCount, experienceType, false);
            }

            this.discard();
        }
    }
}
