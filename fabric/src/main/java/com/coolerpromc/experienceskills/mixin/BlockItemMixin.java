package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.stat.ModStats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(method = "place", at = @At("RETURN"))
    private void onPlace(BlockPlaceContext placeContext, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() != InteractionResult.SUCCESS) return;
        if (placeContext.getPlayer() instanceof ServerPlayer player) {
            player.awardStat(ModStats.BLOCK_PLACED.get(), 1);
        }
    }
}
