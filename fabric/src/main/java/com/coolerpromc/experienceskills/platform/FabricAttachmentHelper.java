package com.coolerpromc.experienceskills.platform;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.data.attachment.ModDataAttachments;
import com.coolerpromc.experienceskills.platform.services.IAttachmentHelper;
import com.coolerpromc.experienceskills.data.attachment.helper.AttachmentKey;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class FabricAttachmentHelper implements IAttachmentHelper {
    private static final Map<AttachmentKey<?>, AttachmentType<?>> REGISTERED = new HashMap<>();

    @Override
    public void register() {
        for (AttachmentKey<?> key : ModDataAttachments.ALL) {
            REGISTERED.put(key, register(key));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Entity entity, AttachmentKey<T> key) {
        return entity.getAttachedOrCreate((AttachmentType<T>) REGISTERED.get(key));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void set(Entity entity, AttachmentKey<T> key, T value) {
        entity.setAttached((AttachmentType<T>) REGISTERED.get(key), value);
    }

    private static <T> AttachmentType<T> register(AttachmentKey<T> key){
        return AttachmentRegistry.create(Constants.id(key.id()), builder -> {
            builder.persistent(key.codec());
            builder.initializer(key.defaultValue());
            builder.syncWith(key.streamCodec(), AttachmentSyncPredicate.all());
            if (key.copyOnDeath()){
                builder.copyOnDeath();
            }
        });
    }
}
