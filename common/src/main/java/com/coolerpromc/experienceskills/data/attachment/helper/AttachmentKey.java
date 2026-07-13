package com.coolerpromc.experienceskills.data.attachment.helper;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public record AttachmentKey<T>(String id, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Supplier<T> defaultValue, boolean copyOnDeath) {
}
