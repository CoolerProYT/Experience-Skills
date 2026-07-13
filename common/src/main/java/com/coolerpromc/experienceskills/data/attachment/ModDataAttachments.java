package com.coolerpromc.experienceskills.data.attachment;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.type.ExperienceTypeRegistry;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.data.attachment.helper.AttachmentKey;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModDataAttachments {
    public static final List<AttachmentKey<?>> ALL = new ArrayList<>();
    public static final Map<String, AttachmentKey<Integer>> INT_KEYS = new HashMap<>();

    private static <T> AttachmentKey<T> register(String id, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Supplier<T> def, boolean copyOnDeath) {
        AttachmentKey<T> key = new AttachmentKey<>(id, codec, streamCodec, def, copyOnDeath);
        ALL.add(key);
        if (codec == Codec.INT){
            INT_KEYS.put(id, (AttachmentKey<Integer>) key);
        }
        return key;
    }

    public static @Nullable AttachmentKey<?> byId(String id){
        if (!id.contains("_experience")) id = id.concat("_experience");

        for (AttachmentKey<?> attachmentKey : ALL) {
            if (attachmentKey.id().equals(id)){
                return attachmentKey;
            }
        }
        return null;
    }

    public static void init(){
        Constants.LOGGER.info("Registering data attachments.");

        ExperienceTypeRegistry.REGISTERED_TYPES.forEach((s, _) -> register(s + "_experience", Codec.INT, ByteBufCodecs.INT, () -> 0, false));

        Services.ATTACHMENT.register();
    }
}
