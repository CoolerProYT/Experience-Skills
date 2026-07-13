package com.coolerpromc.experienceskills.api;

/**
 * Marks a class as an Experience Skills plugin for automatic discovery.
 *
 * <p>The annotated class must implement {@link IExperienceSkillsPlugin} and have a public no-argument
 * constructor. How the annotation is used depends on the loader:
 *
 * <ul>
 *   <li><b>NeoForge</b> — every mod file is scanned for this annotation and each annotated plugin is
 *       instantiated automatically. Nothing else is required.</li>
 *   <li><b>Fabric</b> — this annotation is <i>not</i> used for discovery. Declare the plugin instead as
 *       an {@code experience_skills_plugin} custom entrypoint in {@code fabric.mod.json}.</li>
 * </ul>
 *
 * @see IExperienceSkillsPlugin
 */
public @interface ExperienceSkillsPlugin {
}
