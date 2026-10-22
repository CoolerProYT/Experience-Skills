package com.coolerpromc.experienceskills.data.attachment;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.type.ExperienceTypeRegistry;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.data.attachment.helper.AttachmentKey;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
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

    public static AttachmentKey<Map<String, SkillsConfig>> SERVER_SKILL_CONFIG;
    public static AttachmentKey<Map<String, Integer>> SKILL_PROGRESS;

    private static <T> AttachmentKey<T> register(String id, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Supplier<T> def, boolean copyOnDeath){
        return register(new AttachmentKey<>(id, codec, streamCodec, def, copyOnDeath));
    }

    private static <T> AttachmentKey<T> register(AttachmentKey<T> key){
        String id = key.id();
        Codec<T> codec = key.codec();
        ALL.add(key);
        if (codec == Codec.INT){
            INT_KEYS.put(id, (AttachmentKey<Integer>) key);
        }
        return key;
    }

    public static @Nullable AttachmentKey<Integer> intById(String id){
        if (!id.contains("_experience")) id = id.concat("_experience");

        return INT_KEYS.get(id);
    }

    public static @Nullable AttachmentKey<?> byId(String id){
        if (!id.contains("_experience")) id = id.concat("_experience");

        for (AttachmentKey<?> attachmentKey : ALL){
            if (attachmentKey.id().equals(id)){
                return attachmentKey;
            }
        }
        return null;
    }

    public static void init(){
        Constants.LOGGER.info("Registering data attachments.");

        ExperienceTypeRegistry.REGISTERED_TYPES.forEach((s, holder) -> register(s + "_experience", Codec.INT, ByteBufCodecs.INT, () -> 0, true));

        SERVER_SKILL_CONFIG = register(new AttachmentKey<>("server_skill_config", Codec.unboundedMap(Codec.STRING, SkillsConfig.CODEC),
            ByteBufCodecs.<ByteBuf, String, SkillsConfig, Map<String, SkillsConfig>>map(HashMap::new, ByteBufCodecs.STRING_UTF8, SkillsConfig.STREAM_CODEC), Map::of, false, false, true));
        SKILL_PROGRESS = register(new AttachmentKey<>("skill_progress", Codec.unboundedMap(Codec.STRING, Codec.INT),
            ByteBufCodecs.<ByteBuf, String, Integer, Map<String, Integer>>map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.VAR_INT), Map::of, false, false, true));

        Services.ATTACHMENT.register();
    }
}
