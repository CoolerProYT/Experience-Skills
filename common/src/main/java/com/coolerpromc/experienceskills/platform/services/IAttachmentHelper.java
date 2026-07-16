package com.coolerpromc.experienceskills.platform.services;

import com.coolerpromc.experienceskills.data.attachment.helper.AttachmentKey;
import net.minecraft.world.entity.Entity;

public interface IAttachmentHelper {
    void register();
    <T> T get(Entity entity, AttachmentKey<T> key, T fallback);
    <T> T get(Entity entity, AttachmentKey<T> key);
    <T> void set(Entity entity, AttachmentKey<T> key, T value);
}
