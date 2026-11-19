package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.event.BlockDropEvent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

/**
 * Some mods (e.g. Right Click Harvest) harvest crops by calling {@link Block#getDrops} and popping the
 * stacks themselves, bypassing {@code dropResources} and therefore our normal drop handling.
 * Catch those calls here; calls made from inside {@code dropResources} are already handled elsewhere.
 */
@Mixin(Block.class)
public abstract class BlockDropsMixin {
    @Unique
    private static final ThreadLocal<int[]> experienceskills$dropResourcesDepth = ThreadLocal.withInitial(() -> new int[1]);

    @WrapMethod(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V")
    private static void experienceskills$trackDropResources(BlockState state, Level level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, ItemStack tool, Operation<Void> original) {
        int[] depth = experienceskills$dropResourcesDepth.get();
        depth[0]++;
        try {
            original.call(state, level, pos, blockEntity, breaker, tool);
        } finally {
            depth[0]--;
        }
    }

    @ModifyReturnValue(
        method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;",
        at = @At("RETURN")
    )
    private static List<ItemStack> experienceskills$externalHarvest(List<ItemStack> drops, BlockState state, ServerLevel level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, ItemInstance tool) {
        if (breaker instanceof ServerPlayer player && experienceskills$dropResourcesDepth.get()[0] == 0) {
            BlockDropEvent.handleCropHarvest(level, state, player, drops);
        }
        return drops;
    }
}
