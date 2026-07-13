package com.coolerpromc.experienceskills.platform.client;

import com.coolerpromc.experienceskills.platform.services.client.IKeyHelper;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class FabricKeyHelper implements IKeyHelper {
    @Override
    public KeyMapping registerMapping(String translationKey, InputConstants.Type type, int code, KeyMapping.Category category) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(translationKey, type, code, category));
    }
}
