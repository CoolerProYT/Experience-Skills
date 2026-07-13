package com.coolerpromc.experienceskills.platform.client;

import com.coolerpromc.experienceskills.platform.services.client.IKeyHelper;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.apache.logging.log4j.util.Lazy;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeKeyHelper implements IKeyHelper {
    public static final List<Lazy<KeyMapping>> mappings = new ArrayList<>();

    @Override
    public KeyMapping registerMapping(String translationKey, InputConstants.Type type, int code, KeyMapping.Category category) {
        Lazy<KeyMapping> mapping = Lazy.lazy(() -> new KeyMapping(translationKey, type, code, category));
        mappings.add(mapping);
        return mapping.get();
    }
}
