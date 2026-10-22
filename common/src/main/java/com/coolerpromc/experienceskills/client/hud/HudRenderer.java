package com.coolerpromc.experienceskills.client.hud;

import com.coolerpromc.experienceskills.api.ExperienceSkillsAPI;
import com.coolerpromc.experienceskills.client.hud.custom.ModExperienceBarRenderer;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

public class HudRenderer {
    private static final List<ModExperienceBarRenderer> renderers = new ArrayList<>();
    public static ExperienceType currentType = ExperienceType.VANILLA;

    static {
        ExperienceSkillsAPI.getAllTypes().forEach(type -> renderers.add(new ModExperienceBarRenderer(Minecraft.getInstance(), type)));
    }

    public static void extractSkillsExperienceBar(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        for (ModExperienceBarRenderer renderer : renderers){
            if (shouldRender(renderer.type())){
                renderer.extractBackground(graphics, deltaTracker);
            }
        }
    }

    public static boolean shouldRender(ExperienceType type){
        return currentType == type && Minecraft.getInstance().gameMode.hasExperience();
    }
}
