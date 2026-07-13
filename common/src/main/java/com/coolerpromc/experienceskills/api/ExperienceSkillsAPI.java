package com.coolerpromc.experienceskills.api;

import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.entity.custom.ModExperienceOrb;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * The public entry point for reading and adjusting a player's skills, awarding experience, and registering
 * the custom stats and attributes a skill needs.
 *
 * <p>Read-only queries ({@link #getLevel}, {@link #getProgress}, {@link #getXpNeededForNextLevel}) take a
 * {@link LivingEntity} and work on either side, since the underlying data is synced to the client. The
 * mutating helpers require a {@link ServerPlayer} and must be called on the logical server.
 */
public class ExperienceSkillsAPI {
    /**
     * Looks up a registered skill by its id.
     *
     * @param name the skill id
     * @return the skill, or {@code null} if no skill with that id is registered
     */
    public static @Nullable ExperienceType getExperienceType(String name){
        return ExperienceType.byName(name).orElse(null);
    }

    /**
     * @return the entity's current level in the given skill
     */
    public static int getLevel(LivingEntity entity, ExperienceType experienceType){
        return ModExperienceOrb.getLevel(entity, experienceType.getKey());
    }

    /**
     * @return progress towards the next level, from {@code 0.0} to {@code 1.0}
     */
    public static float getProgress(LivingEntity entity, ExperienceType experienceType){
        return ModExperienceOrb.getProgress(entity, experienceType.getKey());
    }

    /**
     * @return the points needed to advance from the entity's current level to the next
     */
    public static int getXpNeededForNextLevel(LivingEntity entity, ExperienceType experienceType){
        return ModExperienceOrb.getXpNeededForNextLevel(entity, experienceType.getKey());
    }

    /**
     * Sets the player's level in the skill outright, then re-applies its bonus. Server-side.
     */
    public static void setLevel(ServerPlayer player, ExperienceType experienceType, int level){
        ModExperienceOrb.setLevel(player, level, experienceType.getKey());
    }

    /**
     * Sets the player's points <i>within their current level</i> in the skill, then re-applies its bonus.
     * Server-side.
     */
    public static void setPoints(ServerPlayer player, ExperienceType experienceType, int points){
        ModExperienceOrb.setPoints(player, points, experienceType.getKey());
    }

    /**
     * Adds experience points to the player's skill total directly (no orbs), then re-applies its bonus.
     * Server-side.
     *
     * @param points the number of points to add
     */
    public static void givePoints(ServerPlayer player, ExperienceType experienceType, int points){
        ModExperienceOrb.giveExperiencePoints(player, points, experienceType);
    }

    /**
     * Raises the player's skill by a number of levels, then re-applies its bonus. Server-side.
     *
     * @param level the number of levels to add
     */
    public static void giveLevels(ServerPlayer player, ExperienceType experienceType, int level){
        ModExperienceOrb.giveExperienceLevels(player, level, experienceType);
    }

    /**
     * @return how many points the player has accumulated within their current level of the skill
     */
    public static int getPointsOfCurrentLevel(ServerPlayer player, ExperienceType experienceType){
        return ModExperienceOrb.getCurrentLevelPoints(player, experienceType);
    }

    /**
     * Re-applies every registered skill's attribute bonus (and runs each skill's handler) for the player.
     * Called automatically on login and command changes; call it manually if you alter skill state through
     * a path this API doesn't cover.
     */
    public static void updateAllSkillStatus(ServerPlayer player){
        ModExperienceOrb.updateAllSkillStatus(player);
    }

    /**
     * @return {@code true} if the player has reached the skill's configured level cap
     */
    public static boolean reachedMaxLevel(ServerPlayer player, ExperienceType experienceType){
        return ModExperienceOrb.reachedMaxLevel(player, experienceType.getKey(), experienceType.getMaxLevel());
    }

    /**
     * Spawns experience orbs worth {@code amount} points of the skill at a position, splitting the amount
     * into vanilla-sized orbs and merging into nearby ones. Use this for XP that isn't tied to a stat;
     * prefer driving a skill from a stat where you can, so progress persists and survives death.
     *
     * @param level          the server level to spawn in
     * @param pos            where to spawn
     * @param amount         total points to award
     * @param experienceType the skill to award
     */
    public static void award(ServerLevel level, Vec3 pos, int amount, ExperienceType experienceType){
        ModExperienceOrb.award(level, pos, amount, experienceType);
    }

    /**
     * @return every registered skill, excluding the special {@code vanilla} HUD entry
     */
    public static List<ExperienceType> getAllTypes(){
        return ExperienceType.ALL.stream().filter(t -> t != ExperienceType.VANILLA).toList();
    }

    /**
     * @return every registered skill that is currently enabled, excluding the special {@code vanilla} entry
     */
    public static List<ExperienceType> getEnabledTypes(){
        return ExperienceType.ALL.stream().filter(t -> t != ExperienceType.VANILLA).filter(ExperienceType::isEnabled).toList();
    }

    /**
     * Registers a custom stat that a skill can be driven from. Call during your plugin's
     * {@link IExperienceSkillsPlugin#registerExperienceType registerExperienceType} (or your mod's
     * registration phase) so it is registered before the game freezes the registries.
     *
     * @param namespace your mod id
     * @param name      the stat's path
     * @return a handle to the registered stat
     */
    public static RegistryHandler<Identifier, Identifier> registerCustomStat(String namespace, String name){
        return Services.REGISTRY.registerStat(namespace, name);
    }

    /**
     * Registers a custom attribute a skill can buff. Note that Experience Skills only keeps a modifier in
     * sync on the attribute; you must still add it to the player's {@code AttributeSupplier} and implement
     * whatever the attribute does. Call during your plugin's registration phase.
     *
     * @param namespace your mod id
     * @param name      the attribute's path
     * @param attribute the attribute to register (typically a {@code RangedAttribute})
     * @return a handle to the registered attribute
     */
    public static RegistryHandler<Attribute, Attribute> registerAttribute(String namespace, String name, Attribute attribute){
        return Services.REGISTRY.registerAttribute(namespace, name, attribute);
    }
}
