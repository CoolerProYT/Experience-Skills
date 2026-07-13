package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.event.StatAwardEvent;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatsCounter.class)
public abstract class StatsCounterMixin {
    @Inject(method = "setValue", at = @At("HEAD"))
    public void setValue(Player player, Stat<?> stat, int count, CallbackInfo ci){
        StatAwardEvent.onStatsAward(player, stat, count);
    }
}
