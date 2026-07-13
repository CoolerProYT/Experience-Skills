package com.coolerpromc.experienceskills.client.hud;

import com.coolerpromc.experienceskills.api.type.ExperienceTypeRegistry;
import com.coolerpromc.experienceskills.client.hud.custom.AbstractExperienceBarRenderer;
import com.coolerpromc.experienceskills.config.ModCommonConfig;
import com.coolerpromc.experienceskills.data.attachment.ModDataAttachments;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

public class HudRenderer {
    private static final List<AbstractExperienceBarRenderer> renderers = new ArrayList<>();
    public static ExperienceType currentType = ExperienceType.VANILLA;

    static {
        renderers.add(new AbstractExperienceBarRenderer(Minecraft.getInstance(), ModCommonConfig.SPEED.get().rgbColor(), ExperienceType.SPEED, ModDataAttachments.SPEED_EXPERIENCE));

        // TODO: Temporarily code to test API
        ExperienceTypeRegistry.REGISTERED_TYPES.forEach((s, registryHolder) -> {
            renderers.add(new AbstractExperienceBarRenderer(Minecraft.getInstance(), ModCommonConfig.getConfigByPath(s).orElse(registryHolder.config()).rgbColor(), ExperienceType.byName(s).orElse(null), ModDataAttachments.INT_KEYS.get(s + "_experience")));
        });
    }

    public static void extractSkillsExperienceBar(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        for (AbstractExperienceBarRenderer renderer : renderers){
            if (shouldRender(renderer.type())){
                renderer.extractBackground(graphics, deltaTracker);
            }
        }
    }

    public static boolean shouldRender(ExperienceType type){
        return currentType == type && Minecraft.getInstance().gameMode.hasExperience();
    }
}
