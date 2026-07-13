package com.coolerpromc.experienceskills.api.type;

import com.coolerpromc.experienceskills.config.ModCommonConfig;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import com.coolerpromc.experienceskills.data.attachment.ModDataAttachments;
import com.coolerpromc.experienceskills.data.attachment.helper.AttachmentKey;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ExperienceType implements StringRepresentable {
    public static final LinkedList<ExperienceType> ALL = new LinkedList<>();

    public static final ExperienceType VANILLA = register("vanilla", null, null);
    public static final ExperienceType SPEED = register("speed", ModCommonConfig.SPEED.get(), ModDataAttachments.SPEED_EXPERIENCE);

    private final String name;
    private final @Nullable SkillsConfig config;
    private final @Nullable AttachmentKey<Integer> key;

    // TODO: Temporarily code to test API
    static {
        ExperienceTypeRegistry.REGISTERED_TYPES.forEach((s, registryHolder) -> {
            register(s, ModCommonConfig.getConfigByPath(s).orElse(registryHolder.config()), ModDataAttachments.INT_KEYS.get(s + "_experience"));
        });
    }

    private static ExperienceType register(String name, @Nullable SkillsConfig config, @Nullable AttachmentKey<Integer> key){
        ExperienceType type = new ExperienceType(name, config, key);
        ALL.add(type);
        return type;
    }

    private ExperienceType(String name, @Nullable SkillsConfig config, @Nullable AttachmentKey<Integer> key){
        this.name = name;
        this.config = config;
        this.key = key;
    }

    public ExperienceType next(){
        LinkedList<ExperienceType> values = ALL.stream().filter(ExperienceType::isEnabled).collect(Collectors.toCollection(LinkedList::new));
        int idx = values.indexOf(this);
        return values.get((idx + 1) % values.size());
    }

    public boolean isEnabled() {
        return config == null || config.enabled();
    }

    public int color(){
        return config == null ? -1 : config.rgbColor();
    }

    public @Nullable AttachmentKey<Integer> getKey() {
        return key;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name;
    }

    private static final Map<String, ExperienceType> BY_NAME = ALL.stream().collect(Collectors.toMap(ExperienceType::getSerializedName, e -> e));

    public static Optional<ExperienceType> byName(String name) {
        return Optional.ofNullable(BY_NAME.get(name));
    }
}
