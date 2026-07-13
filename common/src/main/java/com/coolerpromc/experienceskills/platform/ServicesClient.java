package com.coolerpromc.experienceskills.platform;

import com.coolerpromc.experienceskills.platform.services.client.IClientRegistryHelper;
import com.coolerpromc.experienceskills.platform.services.client.IKeyHelper;

public final class ServicesClient {
    public static final IClientRegistryHelper REGISTRY = Services.load(IClientRegistryHelper.class);
    public static final IKeyHelper KEY = Services.load(IKeyHelper.class);

    private ServicesClient() {
    }
}
