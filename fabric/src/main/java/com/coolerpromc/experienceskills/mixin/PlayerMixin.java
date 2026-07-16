package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.event.PlayerTickEvent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci){
        PlayerTickEvent.onPlayerTickEnd((Player)(Object) this);
    }
}
