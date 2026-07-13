package com.coolerpromc.experienceskills.client.hud.custom;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.data.attachment.helper.AttachmentKey;
import com.coolerpromc.experienceskills.entity.custom.ModExperienceOrb;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.util.XpMath;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public record ModExperienceBarRenderer(Minecraft minecraft, int color, ExperienceType type, AttachmentKey<Integer> key) implements ContextualBarRenderer {
    private static final Identifier EXPERIENCE_BAR_BACKGROUND_SPRITE = Constants.id("hud/experience_bar_background");
    private static final Identifier EXPERIENCE_BAR_PROGRESS_SPRITE = Constants.id("hud/experience_bar_progress");

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        LocalPlayer player = this.minecraft.player;
        int left = this.left(this.minecraft.getWindow());
        int top = this.top(this.minecraft.getWindow());

        int points = Services.ATTACHMENT.get(player, key);
        int level = XpMath.getLevel(points);
        int xpNeededForNextLevel = XpMath.getXpNeededForNextLevel(level);
        float progressFraction = ModExperienceOrb.getProgress(player, key);

        if (xpNeededForNextLevel > 0) {
            int progress = (int)(progressFraction * 183.0F);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_BACKGROUND_SPRITE, left, top, 182, 5, ARGB.color(0xFF, color));
            if (progress > 0) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, left, top, progress, 5, ARGB.color(0xFF, color));
            }
            if (level > 0){
                extractExperienceLevel(graphics, this.minecraft.font, level);
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
    }

    private void extractExperienceLevel(GuiGraphicsExtractor graphics, Font font, int experienceLevel) {
        Component str = Component.translatable("gui.experience.level", experienceLevel);
        int x = (graphics.guiWidth() - font.width(str)) / 2;
        int y = graphics.guiHeight() - 24 - 9 - 2;
        graphics.text(font, str, x + 1, y, -16777216, false);
        graphics.text(font, str, x - 1, y, -16777216, false);
        graphics.text(font, str, x, y + 1, -16777216, false);
        graphics.text(font, str, x, y - 1, -16777216, false);
        graphics.text(font, str, x, y, ARGB.color(0xFF, color), false);
    }
}
