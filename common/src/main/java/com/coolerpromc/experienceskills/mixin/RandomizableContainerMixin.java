package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.loot.SkillBottleLoot;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RandomizableContainer.class)
public interface RandomizableContainerMixin {
    @WrapOperation(method = "unpackLootTable", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/level/storage/loot/LootTable;fill(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/storage/loot/LootParams;J)V"))
    private void experienceSkills$addBottle(LootTable table, Container container, LootParams params, long seed,
                                            Operation<Void> original, @Local ResourceKey<LootTable> lootTableKey) {
        original.call(table, container, params, seed);
        SkillBottleLoot.add(container, ((RandomizableContainer) this).getLevel(), lootTableKey, seed);
    }
}
