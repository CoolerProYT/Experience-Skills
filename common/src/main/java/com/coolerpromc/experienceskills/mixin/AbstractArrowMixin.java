package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.attribute.ModAttributes;
import com.coolerpromc.experienceskills.stat.ModStats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {
    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void awardArrowHit(EntityHitResult hitResult, CallbackInfo ci){
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (self.getOwner() instanceof ServerPlayer player && hitResult.getEntity() instanceof LivingEntity target && target != player){
            player.awardStat(ModStats.ARROWS_HIT.get());
        }
    }

    @ModifyArg(
        method = "onHitEntity",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(DDD)D", ordinal = 0),
        index = 0
    )
    private double scaleArrowDamage(double damage){
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (self.getOwner() instanceof LivingEntity owner && owner.getAttributes().hasAttribute(ModAttributes.PROJECTILE_DAMAGE.holder())){
            return damage * owner.getAttributeValue(ModAttributes.PROJECTILE_DAMAGE.holder());
        }
        return damage;
    }
}
