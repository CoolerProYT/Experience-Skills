package com.coolerpromc.experienceskills.data.component;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;

public class ModDataComponents {
    public static final RegistryHandler.Components<ExperienceType> EXPERIENCE_TYPE = Services.REGISTRY.registerDataComponent("experience_type", b -> b.persistent(ExperienceType.CODEC).networkSynchronized(ExperienceType.STREAM_CODEC).cacheEncoding());

    public static final RegistryHandler.Components<Integer> STORED_XP = Services.REGISTRY.registerDataComponent("stored_xp", b -> b.persistent(com.mojang.serialization.Codec.intRange(1, 1000000)).networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.VAR_INT).cacheEncoding());

    public static void init(){
        Constants.LOGGER.info("Registering data components");
    }
}
