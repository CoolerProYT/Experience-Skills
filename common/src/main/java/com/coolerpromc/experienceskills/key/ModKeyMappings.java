package com.coolerpromc.experienceskills.key;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.platform.ServicesClient;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings {
    public static final KeyMapping OPEN_SKILLS_SCREEN = ServicesClient.KEY.registerMapping("key.experienceskills.open_skills_screen", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KeyMapping.Category.MISC);

    public static void init() {
        Constants.LOGGER.info("Registering key mappings.");
    }
}
