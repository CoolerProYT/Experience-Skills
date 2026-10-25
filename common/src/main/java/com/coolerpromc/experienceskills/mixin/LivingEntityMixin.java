package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.attribute.ModAttributes;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;
import com.coolerpromc.experienceskills.util.ISavedHealthHolder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ISavedHealthHolder {
    @Unique
    private float savedHealth = -1;

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void rememberSavedHealth(ValueInput input, CallbackInfo ci){
        this.savedHealth = input.getFloatOr("Health", -1);
    }

    @Override
    public float getSavedHealth() {
        return this.savedHealth;
    }

    @Override
    public void clearSavedHealth() {
        this.savedHealth = -1;
    }

    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder addAttribute(AttributeSupplier.Builder builder) {
        ModAttributes.init();
        Services.REGISTRY.getRegisteredAttributes().stream().map(RegistryHandler::holder).forEach(builder::add);
        return builder;
    }

    @ModifyArg(
        method = "travelInWater",
        at = @At(
            target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V",
            value = "INVOKE"
        ),
        index = 0
    )
    private float modifySpeed(float speed) {
        LivingEntity self = (LivingEntity)(Object) this;
        return speed * (float) self.getAttributeValue(ModAttributes.SWIM_SPEED.holder());
    }

    @Inject(method = "jumpInLiquid", at = @At("RETURN"))
    private void modifyDelta(TagKey<Fluid> type, CallbackInfo ci){
        LivingEntity self = (LivingEntity)(Object) this;
        self.setDeltaMovement(self.getDeltaMovement().add(0.0D, (double)0.04F * self.getAttributeValue(ModAttributes.SWIM_SPEED.holder()), 0.0D));
    }
}
