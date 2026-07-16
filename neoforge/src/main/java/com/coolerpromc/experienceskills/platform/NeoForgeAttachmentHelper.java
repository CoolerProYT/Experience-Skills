package com.coolerpromc.experienceskills.platform;

import com.coolerpromc.experienceskills.data.attachment.ModDataAttachments;
import com.coolerpromc.experienceskills.platform.services.IAttachmentHelper;
import com.coolerpromc.experienceskills.data.attachment.helper.AttachmentKey;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NeoForgeAttachmentHelper implements IAttachmentHelper {
    private static final Map<AttachmentKey<?>, Supplier<AttachmentType<?>>> REGISTERED = new HashMap<>();

    @Override
    public void register() {
        for (AttachmentKey<?> key : ModDataAttachments.ALL) {
            REGISTERED.put(key, register(key));
        }
    }

    @Override
    public <T> T get(Entity entity, AttachmentKey<T> key, T fallback) {
        if (REGISTERED.get(key) == null){
            return fallback;
        }
        return get(entity, key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Entity entity, AttachmentKey<T> key) {
        Supplier<AttachmentType<T>> type = (Supplier<AttachmentType<T>>) (Object) REGISTERED.get(key);
        return entity.getData(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void set(Entity entity, AttachmentKey<T> key, T value) {
        Supplier<AttachmentType<T>> type = (Supplier<AttachmentType<T>>) (Object) REGISTERED.get(key);
        entity.setData(type.get(), value);
    }

    private static <T> Supplier<AttachmentType<?>> register(AttachmentKey<T> key){
        return NeoForgeRegistryHelper.ATTACHMENTS.register(key.id(), () -> {
            AttachmentType.Builder<T> builder = AttachmentType.builder(key.defaultValue()).serialize(key.codec().fieldOf(key.id())).sync(key.streamCodec());
            if (key.copyOnDeath()){
                builder.copyOnDeath();
            }
            return builder.build();
        });
    }
}
