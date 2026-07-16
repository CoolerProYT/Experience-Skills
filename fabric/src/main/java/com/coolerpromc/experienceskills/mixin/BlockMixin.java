package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.event.BlockDropEvent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Unique
    @Nullable
    private static List<ItemEntity> capturedDrops = null;

    @Inject(
        method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V",
        at = @At(
            target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)Ljava/util/List;",
            value = "INVOKE"
        )
    )
    private static void dropResource(BlockState state, Level level, BlockPos pos, CallbackInfo ci){
        beginCapturingDrops();
    }

    @Inject(
        method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V",
        at = @At(
            target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)Ljava/util/List;",
            value = "INVOKE"
        )
    )
    private static void dropResource(BlockState state, LevelAccessor level, BlockPos pos, BlockEntity blockEntity, CallbackInfo ci){
        beginCapturingDrops();
    }

    @Inject(
        method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
        at = @At(
            target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;",
            value = "INVOKE"
        )
    )
    private static void dropResource(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity, Entity breaker, ItemStack tool, CallbackInfo ci){
        beginCapturingDrops();
    }

    @WrapOperation(
        method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V",
        at = @At(
            target = "Lnet/minecraft/world/level/block/state/BlockState;spawnAfterBreak(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V",
            value = "INVOKE"
        )
    )
    private static void dropResource(BlockState instance, ServerLevel level, BlockPos pos, ItemStack stack, boolean b, Operation<Void> original, @Local BlockState state){
        List<ItemEntity> captured = stopCapturingDrops();
        handleBlockDrops(level, pos, state, null, captured, null, ItemStack.EMPTY);
    }

    @WrapOperation(
        method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V",
        at = @At(
            target = "Lnet/minecraft/world/level/block/state/BlockState;spawnAfterBreak(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V",
            value = "INVOKE"
        )
    )
    private static void dropResource(BlockState instance, ServerLevel level, BlockPos pos, ItemStack stack, boolean b, Operation<Void> original, @Local BlockState state, @Local BlockEntity blockEntity){
        List<ItemEntity> captured = stopCapturingDrops();
        handleBlockDrops(level, pos, state, blockEntity, captured, null, ItemStack.EMPTY);
    }

    @WrapOperation(
        method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
        at = @At(
            target = "Lnet/minecraft/world/level/block/state/BlockState;spawnAfterBreak(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V",
            value = "INVOKE"
        )
    )
    private static void dropResource(BlockState instance, ServerLevel level, BlockPos pos, ItemStack stack, boolean b, Operation<Void> original, @Local BlockState state, @Local BlockEntity blockEntity, @Local Entity breaker, @Local ItemStack tool){
        List<ItemEntity> captured = stopCapturingDrops();
        handleBlockDrops(level, pos, state, blockEntity, captured, breaker, tool);
    }

    @Unique
    private static void beginCapturingDrops() {
        capturedDrops = new java.util.ArrayList<>();
    }

    @Unique
    private static List<ItemEntity> stopCapturingDrops() {
        List<ItemEntity> drops = capturedDrops;
        capturedDrops = null;
        return drops;
    }

    @Unique
    private static void handleBlockDrops(ServerLevel level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, List<ItemEntity> drops, @Nullable Entity breaker, ItemStack tool) {
        boolean event = BlockDropEvent.handleBlockDrop(level, pos, state, blockEntity, drops, breaker, tool);

        if (!event) {
            for (ItemEntity entity : drops) {
                level.addFreshEntity(entity);
            }
            state.spawnAfterBreak(level, pos, tool, true);
        }
    }

    @WrapOperation(
        method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V",
        at = @At(
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",
            value = "INVOKE"
        )
    )
    private static boolean popResource(Level level, Entity entity, Operation<Boolean> original){
        if (capturedDrops != null && entity instanceof ItemEntity itemEntity) {
            return capturedDrops.add(itemEntity);
        }
        return original.call(level, entity);
    }
}
