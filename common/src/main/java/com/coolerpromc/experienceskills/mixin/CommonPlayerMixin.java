package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.attribute.ModAttributes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public abstract class CommonPlayerMixin {
    @WrapOperation(
        method = {
            "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F",
            "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F"
        },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/core/Holder;)D",
            ordinal = 1
        )
    )
    private double getDestroySpeed(Player instance, Holder<Attribute> holder, Operation<Double> original) {
        double value = original.call(instance, holder);
        ItemStack tool = instance.getMainHandItem();

        if (tool.is(ItemTags.PICKAXES)) {
            double bonus = instance.getAttributeValue(ModAttributes.PICKAXE_MINING_SPEED.holder());
            value *= bonus;
        }
        if (tool.is(ItemTags.SHOVELS)) {
            double bonus = instance.getAttributeValue(ModAttributes.SHOVEL_MINING_SPEED.holder());
            value *= bonus;
        }
        if (tool.is(ItemTags.AXES)) {
            double bonus = instance.getAttributeValue(ModAttributes.AXE_MINING_SPEED.holder());
            value *= bonus;
        }
        if (tool.is(ItemTags.HOES)) {
            double bonus = instance.getAttributeValue(ModAttributes.HOE_MINING_SPEED.holder());
            value *= bonus;
        }

        return value;
    }

    @ModifyConstant(
        method = {
            "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F",
            "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F"
        },
        constant = @Constant(floatValue = 5.0F)
    )
    private float modifyOffGroundPenalty(float original) {
        Player player = (Player) (Object) this;
        AttributeInstance instance = player.getAttribute(ModAttributes.FLOATING_MINING_SPEED.holder());
        if (instance != null){
            double buff = instance.getValue();
            return (float) (original - buff);
        }

        return original;
    }

    @ModifyVariable(method = "onEnchantmentPerformed", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int refundEnchantmentCost(int enchantmentCost) {
        Player player = (Player) (Object) this;
        if (player instanceof ServerPlayer && enchantmentCost > 0) {
            double chance = player.getAttributeValue(ModAttributes.ENCHANT_REFUND_CHANCE.holder());
            if (chance > 0 && player.getRandom().nextDouble() < chance) {
                return 0;
            }
        }
        return enchantmentCost;
    }
}
