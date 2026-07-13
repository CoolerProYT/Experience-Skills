package com.coolerpromc.experienceskills.key;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.platform.ServicesClient;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings {
    public static final KeyMapping CYCLE_CONTEXTUAL_BAR = ServicesClient.KEY.registerMapping("key.experienceskills.cycle_contextual_bar", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KeyMapping.Category.MISC);

    public static void init() {
        Constants.LOGGER.info("Registering key mappings.");
    }
}
