package com.coolerpromc.experienceskills.client.event;

import com.coolerpromc.experienceskills.client.hud.HudRenderer;
import com.coolerpromc.experienceskills.key.ModKeyMappings;
import net.minecraft.client.Minecraft;

public class ClientTickEvent {
    public static void onClientTickEnd(Minecraft minecraft) {
        while (ModKeyMappings.CYCLE_CONTEXTUAL_BAR.consumeClick()){
            HudRenderer.currentType = HudRenderer.currentType.next();
        }
    }
}
