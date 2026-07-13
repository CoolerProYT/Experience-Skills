package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.attribute.ModAttributes;
import com.coolerpromc.experienceskills.stat.ModStats;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
    @Unique
    private List<ItemStack> experienceSkills$retrievedItems;

    @ModifyArg(
        method = "<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/FishingHook;<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;II)V"
        ),
        index = 3
    )
    private static int addLureSpeed(int lureSpeed, @Local(argsOnly = true) Player player){
        double bonus = player.getAttributeValue(ModAttributes.LURE_SPEED.holder());
        return lureSpeed + (int) bonus;
    }

    @ModifyArg(
        method = "<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/FishingHook;<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;II)V"
        ),
        index = 2
    )
    private static int addLuck(int luck, @Local(argsOnly = true) Player player){
        double bonus = player.getAttributeValue(ModAttributes.FISHING_LUCK.holder());
        return luck + (int) bonus;
    }

    @ModifyVariable(method = "retrieve", at = @At("STORE"), name = "items")
    private List<ItemStack> captureItems(List<ItemStack> items) {
        this.experienceSkills$retrievedItems = items;
        return items;
    }

    @Inject(method = "retrieve", at = @At("RETURN"))
    private void onRetrieve(ItemStack rod, CallbackInfoReturnable<Integer> cir) {
        Player owner = ((FishingHook)(Object) this).getPlayerOwner();
        if (owner != null && experienceSkills$retrievedItems != null && !experienceSkills$retrievedItems.isEmpty()) {
            owner.awardStat(ModStats.ITEMS_FISHED.get(), 1);
        }
        experienceSkills$retrievedItems = null;
    }
}
