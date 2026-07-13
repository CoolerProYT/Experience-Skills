package com.coolerpromc.experienceskills.api.type;

import com.coolerpromc.experienceskills.config.ModCommonConfig;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import com.coolerpromc.experienceskills.data.attachment.ModDataAttachments;
import com.coolerpromc.experienceskills.data.attachment.helper.AttachmentKey;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The runtime representation of a registered skill: its live {@link SkillsConfig}, the attachment that
 * stores a player's points, the attribute it buffs, and its update {@link IExperienceTypeRegistry.Handler}.
 * One instance exists per registered skill, built at startup from the plugin registrations (with the live
 * config preferred over the registered default).
 *
 * <p>Obtain instances via {@link #byName(String)} or {@link com.coolerpromc.experienceskills.api.ExperienceSkillsAPI#getAllTypes()}.
 */
public final class ExperienceType implements StringRepresentable {
    /** Every skill, in registration order, including {@link #VANILLA}. */
    public static final LinkedList<ExperienceType> ALL = new LinkedList<>();

    /**
     * The special entry representing the vanilla XP bar in the HUD cycle. It has no attachment, attribute
     * or orb and is not an earnable skill; most API listings exclude it.
     */
    public static final ExperienceType VANILLA = register("vanilla", SkillsConfig.VANILLA, null, null, (player, level, experienceType) -> {});

    private final String name;
    private final @NotNull SkillsConfig config;
    private final @Nullable AttachmentKey<Integer> key;
    private final @Nullable Holder<Attribute> attributeHolder;
    private final IExperienceTypeRegistry.Handler handler;

    static {
        ExperienceTypeRegistry.REGISTERED_TYPES.forEach((s, registryHolder) -> register(s, ModCommonConfig.getConfigByPath(s).orElse(registryHolder.config()), ModDataAttachments.INT_KEYS.get(s + "_experience"), registryHolder.attributeHolder(), registryHolder.handler()));
    }

    private static ExperienceType register(String name, @NotNull SkillsConfig config, @Nullable AttachmentKey<Integer> key, Holder<Attribute> attributeHolder, IExperienceTypeRegistry.Handler handler){
        ExperienceType type = new ExperienceType(name, config, key, attributeHolder, handler);
        ALL.add(type);
        return type;
    }

    private ExperienceType(String name, @NotNull SkillsConfig config, @Nullable AttachmentKey<Integer> key, @Nullable Holder<Attribute> attributeHolder, IExperienceTypeRegistry.Handler handler){
        this.name = name;
        this.config = config;
        this.key = key;
        this.attributeHolder = attributeHolder;
        this.handler = handler;
    }

    /**
     * @return the next enabled skill in the HUD cycle after this one, wrapping around
     */
    public ExperienceType next(){
        LinkedList<ExperienceType> values = ALL.stream().filter(ExperienceType::isEnabled).collect(Collectors.toCollection(LinkedList::new));
        int idx = values.indexOf(this);
        return values.get((idx + 1) % values.size());
    }

    /**
     * @return whether the skill is enabled in its config
     */
    public boolean isEnabled() {
        return config.enabled();
    }

    /**
     * @return the skill's orb and HUD-bar tint, as a packed RGB integer
     */
    public int color(){
        return config.rgbColor();
    }

    /**
     * @return the attachment holding a player's points for this skill, or {@code null} for {@link #VANILLA}
     */
    public @Nullable AttachmentKey<Integer> getKey() {
        return key;
    }

    /**
     * @return the attribute this skill's level bonus is applied to, or {@code null} for {@link #VANILLA}
     */
    public @Nullable Holder<Attribute> getAttributeHolder() {
        return attributeHolder;
    }

    /**
     * @return the skill's live configuration (the on-disk values, which may differ from the registered default)
     */
    public @NonNull SkillsConfig getConfig() {
        return config;
    }

    /**
     * @return the skill's level cap
     */
    public int getMaxLevel(){
        return config.maxLevel();
    }

    /**
     * @return how the level bonus combines with the attribute
     */
    public AttributeModifier.Operation operation(){
        return config.operation();
    }

    /**
     * Invokes this skill's {@link IExperienceTypeRegistry.Handler}. Called internally whenever the skill's
     * status is refreshed; plugins supply the behaviour at registration rather than calling this directly.
     *
     * @param player the player whose skill was refreshed
     * @param level  the skill's current level for that player
     */
    public void onExperiencePointChange(ServerPlayer player, int level){
        this.handler.onUpdateSkill(player, level, this);
    }

    /**
     * @return the skill's id
     */
    @Override
    public @NonNull String getSerializedName() {
        return this.name;
    }

    private static final Map<String, ExperienceType> BY_NAME = ALL.stream().collect(Collectors.toMap(ExperienceType::getSerializedName, e -> e));

    /**
     * Looks up a skill by its id.
     *
     * @param name the skill id
     * @return the skill, or an empty {@link Optional} if none is registered under that id
     */
    public static Optional<ExperienceType> byName(String name) {
        return Optional.ofNullable(BY_NAME.get(name));
    }
}
