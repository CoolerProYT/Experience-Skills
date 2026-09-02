package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.stat.ModStats;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class PlayerBlockBreakEvent {
    public static void afterBreak(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        if (player instanceof ServerPlayer serverPlayer){
            serverPlayer.awardStat(ModStats.BLOCK_BROKEN.get());
            if (serverPlayer.isInWater()){
                serverPlayer.awardStat(ModStats.BLOCK_MINED_IN_WATER.get());
            }
            if (!serverPlayer.onGround()){
                serverPlayer.awardStat(ModStats.BLOCK_MINED_WHEN_FLOATING.get());
            }

            ItemStack tool = player.getMainHandItem();

            if (tool.isCorrectToolForDrops(state)){
                if (tool.is(ItemTags.PICKAXES)){
                    player.awardStat(ModStats.BLOCK_MINED_WITH_PICKAXE.get());
                }
                if (tool.is(ItemTags.SHOVELS)){
                    player.awardStat(ModStats.BLOCK_MINED_WITH_SHOVEL.get());
                }
                if (tool.is(ItemTags.AXES)){
                    player.awardStat(ModStats.BLOCK_MINED_WITH_AXE.get());
                }
                if (tool.is(ItemTags.HOES)){
                    player.awardStat(ModStats.BLOCK_MINED_WITH_HOE.get());
                }
            }
        }
    }
}
