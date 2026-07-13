package com.coolerpromc.experienceskills.api.type;

import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;

/**
 * Handed to a plugin during {@link com.coolerpromc.experienceskills.api.IExperienceSkillsPlugin#registerExperienceType}
 * so it can declare skills. Registering a skill wires up everything derived from it — config entry, XP
 * attachment, experience-orb entity, renderer, HUD bar, the level-scaled attribute bonus, the death drop
 * and command support — all keyed off the skill's {@code name}.
 */
public interface IExperienceTypeRegistry {
    /**
     * Registers a skill whose only effect is a modifier on {@code attributeHolder} that scales with the
     * skill's level. Equivalent to {@link #register(String, SkillsConfig, Holder, Handler)} with a
     * no-op handler.
     *
     * @param name             the skill id; every derived id is built from it (for example
     *                         {@code name + "_experience"} for the XP attachment), so it must be unique
     * @param config           the default configuration, written to the config file on first run and
     *                         overridable by players afterwards
     * @param attributeHolder  the attribute the level bonus is applied to, e.g. {@code Attributes.MOVEMENT_SPEED}
     */
    default void register(String name, SkillsConfig config, Holder<Attribute> attributeHolder){
        register(name, config, attributeHolder, (p, l, e) -> {});
    }

    /**
     * Registers a skill that, in addition to the automatic attribute bonus, runs a {@link Handler} each
     * time the skill is refreshed — for effects a single attribute modifier can't express (adjusting a
     * second attribute, custom logic).
     *
     * @param name             the skill id; see {@link #register(String, SkillsConfig, Holder)}
     * @param config           the default configuration
     * @param attributeHolder  the attribute the level bonus is applied to
     * @param handler          extra work to run whenever the skill's status is updated
     * @see Handler
     */
    void register(String name, SkillsConfig config, Holder<Attribute> attributeHolder, Handler handler);

    /**
     * Extra per-skill logic that runs <i>in addition to</i> the automatic attribute bonus, not instead of
     * it. It fires whenever the skill's status is refreshed — on login, on {@code /experienceskills}
     * command changes, and each time the player levels the skill up by collecting an orb — and only when
     * the skill's attribute is present on the player.
     *
     * <p>Because it can fire at any level (not just on a change), a handler whose output depends on the
     * level should recompute it from the supplied {@code level} each call rather than assuming a delta.
     * The built-in {@code jump} skill uses one to keep {@code SAFE_FALL_DISTANCE} in step with the
     * jump-height bonus.
     */
    @FunctionalInterface
    interface Handler{
        /**
         * @param player          the player whose skill was refreshed
         * @param level           the skill's current level for this player
         * @param experienceType  the skill being refreshed
         */
        void onUpdateSkill(ServerPlayer player, int level, ExperienceType experienceType);
    }
}
