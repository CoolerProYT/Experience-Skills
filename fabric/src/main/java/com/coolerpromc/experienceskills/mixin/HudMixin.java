package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.client.hud.HudRenderer;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Hud.class)
public abstract class HudMixin {
    @WrapWithCondition(
        method = "extractHotbarAndDecorations(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"
        )
    )
    private boolean wrapExtractBackground(ContextualBar instance, GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        return shouldRender();
    }

    @WrapWithCondition(
        method = "extractHotbarAndDecorations(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractExperienceLevel(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;I)V"
        )
    )
    private static boolean wrapExtractExpLevel(GuiGraphicsExtractor graphics, Font font, int experienceLevel) {
        return shouldRender();
    }

    private static boolean shouldRender() {
        return HudRenderer.shouldRender(ExperienceType.VANILLA);
    }
}
