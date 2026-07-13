package com.coolerpromc.experienceskills.platform;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.platform.services.IAttachmentHelper;
import com.coolerpromc.experienceskills.platform.services.IPlatformHelper;
import com.coolerpromc.experienceskills.platform.services.IRegistryHelper;

import java.util.ServiceLoader;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IRegistryHelper REGISTRY = load(IRegistryHelper.class);
    public static final IAttachmentHelper ATTACHMENT = load(IAttachmentHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}