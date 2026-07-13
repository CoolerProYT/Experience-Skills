package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.stat.ModStats;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class PlayerBlockBreakEvent {
    public static void afterBreak(Level level, Player player, BlockPos pos, BlockState blockState, @Nullable BlockEntity blockEntity) {
        if (player instanceof ServerPlayer serverPlayer){
            serverPlayer.awardStat(ModStats.BLOCK_BROKEN.get(), 1);
        }
    }
}
