package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.client.hud.HudRenderer;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @WrapWithCondition(
        method = "extractHotbarAndDecorations(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/contextualbar/ContextualBarRenderer;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"
        )
    )
    private boolean wrapExtractBackground(ContextualBarRenderer instance, GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        return shouldRender();
    }

    @WrapWithCondition(
        method = "extractHotbarAndDecorations(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/contextualbar/ContextualBarRenderer;extractExperienceLevel(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;I)V"
        )
    )
    private static boolean wrapExtractExpLevel(GuiGraphicsExtractor graphics, Font font, int level) {
        return shouldRender();
    }

    private static boolean shouldRender() {
        return HudRenderer.shouldRender(ExperienceType.VANILLA);
    }
}
