package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.attribute.ModAttributes;
import com.coolerpromc.experienceskills.stat.ModStats;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodProperties.class)
public abstract class FoodPropertiesMixin {
    @WrapOperation(
        method = "onConsume",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V")
    )
    private void eatWithBonus(FoodData foodData, FoodProperties food, Operation<Void> original, @Local(argsOnly = true) LivingEntity user){
        original.call(foodData, food);
        if (user instanceof ServerPlayer player){
            player.awardStat(ModStats.FOOD_EATEN.get());

            double bonus = player.getAttributeValue(ModAttributes.SATURATION_BONUS.holder());
            if (bonus > 0){
                float saturation = foodData.getSaturationLevel() + (float) (food.saturation() * bonus);
                foodData.setSaturation(Math.min(saturation, foodData.getFoodLevel()));
            }
        }
    }
}
