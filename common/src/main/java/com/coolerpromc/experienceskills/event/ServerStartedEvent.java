package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.api.type.ExperienceTypeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;

public class ServerStartedEvent {
    // Ensure is a valid stat
    public static void onServerStarted(MinecraftServer server) {
        ExperienceTypeRegistry.REGISTERED_TYPES.forEach((s, registryHolder) -> server.registryAccess().lookupOrThrow(BuiltInRegistries.CUSTOM_STAT.key()).getOrThrow(ResourceKey.create(Registries.CUSTOM_STAT, registryHolder.config().stat())));
    }
}
