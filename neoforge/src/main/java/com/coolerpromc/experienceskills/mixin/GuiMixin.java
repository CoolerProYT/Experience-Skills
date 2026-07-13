package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.client.hud.HudRenderer;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "extractExperienceLevel", at = @At("HEAD"), cancellable = true)
    public void extractExperienceLevel(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci){
        if (!HudRenderer.shouldRender(ExperienceType.VANILLA)) ci.cancel();
    }

    @Inject(method = "extractContextualInfoBarBackground", at = @At("HEAD"), cancellable = true)
    public void extractContextualInfoBarBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci){
        if (!HudRenderer.shouldRender(ExperienceType.VANILLA)) ci.cancel();
    }
}
