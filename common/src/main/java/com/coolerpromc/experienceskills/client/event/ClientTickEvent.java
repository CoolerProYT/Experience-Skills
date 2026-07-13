package com.coolerpromc.experienceskills.client.event;

import com.coolerpromc.experienceskills.client.hud.HudRenderer;
import com.coolerpromc.experienceskills.key.ModKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ClientTickEvent {
    public static void onClientTickEnd(Minecraft minecraft) {
        while (ModKeyMappings.CYCLE_CONTEXTUAL_BAR.consumeClick()){
            if (minecraft.player != null && minecraft.gameMode.hasExperience()){
                HudRenderer.currentType = HudRenderer.currentType.next();
                minecraft.player.sendOverlayMessage(Component.translatable("message.experienceskills.toggle_hud", HudRenderer.currentType.getSerializedName().replace('_', ' ')));
            }
        }
    }
}
