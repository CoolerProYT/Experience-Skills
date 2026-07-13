package com.coolerpromc.experienceskills.api;

import com.coolerpromc.experienceskills.api.type.IExperienceTypeRegistry;

/**
 * The interface every Experience Skills plugin implements. A plugin's job is to declare one or more
 * skills through {@link #registerExperienceType(IExperienceTypeRegistry)}.
 *
 * <p>Register the plugin with the loader so it is discovered and invoked during startup:
 * annotate it with {@link ExperienceSkillsPlugin} on NeoForge, or declare it as an
 * {@code experience_skills_plugin} entrypoint in {@code fabric.mod.json} on Fabric. Implementations
 * need a public no-argument constructor.
 *
 * @see ExperienceSkillsPlugin
 * @see IExperienceTypeRegistry
 */
public interface IExperienceSkillsPlugin {
    /**
     * Called once during startup, before the rest of the mod initialises, so every skill is known before
     * anything derives config entries, attachments, entities or renderers from it.
     *
     * <p>Register each skill with one of the {@link IExperienceTypeRegistry#register} overloads. This is
     * also a safe point to register any custom stats or attributes a skill needs, via
     * {@link ExperienceSkillsAPI#registerCustomStat} / {@link ExperienceSkillsAPI#registerAttribute}.
     *
     * @param registry the registry to declare skills into
     */
    void registerExperienceType(IExperienceTypeRegistry registry);
}
