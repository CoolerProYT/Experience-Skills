package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @ModifyExpressionValue(
        method = "createAttributes",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;createLivingAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;")
    )
    private static AttributeSupplier.Builder addAttribute(AttributeSupplier.Builder builder) {
        Services.REGISTRY.getRegisteredAttributes().stream().map(RegistryHandler::holder).forEach(builder::add);
        return builder;
    }
}
