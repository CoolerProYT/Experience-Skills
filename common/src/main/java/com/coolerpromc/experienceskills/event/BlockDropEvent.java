package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.attribute.ModAttributes;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.stat.ModStats;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class BlockDropEvent {
    // Return true to cancel
    public static boolean handleBlockDrop(ServerLevel level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, List<ItemEntity> drops, @Nullable Entity breaker, ItemStack tool) {
        if (state.is(Services.PLATFORM.oreTag()) && breaker instanceof ServerPlayer player){
            player.awardStat(ModStats.ORE_MINED.get(), 1);
            double extra = player.getAttribute(ModAttributes.MINING_LUCK.holder()).getValue();
            for (ItemEntity drop : drops) {
                drop.getItem().setCount((int) (drop.getItem().getCount() + extra));
            }
        }

        if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state) && breaker instanceof ServerPlayer player){
            player.awardStat(ModStats.CROPS_HARVESTED.get(), 1);
            double chance = player.getAttributeValue(ModAttributes.HARVEST_LUCK.holder());
            if (chance > 0 && level.getRandom().nextDouble() < chance){
                for (ItemEntity drop : drops) {
                    ItemStack stack = drop.getItem();
                    stack.setCount(Math.min(stack.getCount() * 2, stack.getMaxStackSize()));
                }
            }
        }
        return false;
    }
}
