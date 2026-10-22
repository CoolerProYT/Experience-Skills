package com.coolerpromc.experienceskills.client.event;

import com.coolerpromc.experienceskills.client.ClientSkillState;
import com.coolerpromc.experienceskills.client.screen.SkillsScreen;
import com.coolerpromc.experienceskills.key.ModKeyMappings;
import net.minecraft.client.Minecraft;

public class ClientTickEvent {
    public static void onClientTickEnd(Minecraft minecraft) {
        ClientSkillState.tick(minecraft);

        while (ModKeyMappings.OPEN_SKILLS_SCREEN.consumeClick()){
            if (minecraft.player != null && minecraft.screen == null){
                minecraft.setScreen(new SkillsScreen());
            }
        }
    }
}
