package com.coolerpromc.experienceskills.platform.services.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public interface IKeyHelper {
    KeyMapping registerMapping(String translationKey, InputConstants.Type type, int code, KeyMapping.Category category);
}
