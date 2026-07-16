package com.coolerpromc.experienceskills.entity.custom;

import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.entity.ModEntities;
import com.coolerpromc.experienceskills.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ModThrownExperienceBottle extends ThrowableItemProjectile {
    private final ExperienceType experienceType;

    public ModThrownExperienceBottle(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
        this.experienceType = null;
    }

    public ModThrownExperienceBottle(Level level, double x, double y, double z, ItemStack itemStack, ExperienceType experienceType) {
        super(ModEntities.EXPERIENCE_BOTTLE.get(), x, y, z, level, itemStack);
        this.experienceType = experienceType;
    }

    public ModThrownExperienceBottle(Level level, LivingEntity mob, ItemStack itemStack, ExperienceType experienceType) {
        super(ModEntities.EXPERIENCE_BOTTLE.get(), mob, level, itemStack);
        this.experienceType = experienceType;
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
            int xpCount = 3 + this.random.nextInt(5) + this.random.nextInt(5);
            if (hitResult instanceof BlockHitResult blockHitResult) {
                Vec3 blockNormalHit = blockHitResult.getDirection().getUnitVec3();
                ModExperienceOrb.awardWithDirection(level, hitResult.getLocation(), blockNormalHit, xpCount, experienceType);
            } else {
                ModExperienceOrb.awardWithDirection(level, hitResult.getLocation(), this.getDeltaMovement().scale(-1.0), xpCount, experienceType);
            }

            this.discard();
        }
    }
}
