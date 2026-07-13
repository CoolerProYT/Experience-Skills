package com.coolerpromc.experienceskills.api.internal;

import com.coolerpromc.experienceskills.api.ExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.api.IExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.api.internal.entity.MiningSpeedExperienceOrb;
import com.coolerpromc.experienceskills.api.type.IExperienceTypeRegistry;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import com.coolerpromc.experienceskills.entity.helper.ExperienceOrbFactory;

@ExperienceSkillsPlugin
public final class InternalExperienceSkillsPlugin implements IExperienceSkillsPlugin {
    @Override
    public void registerExperienceType(IExperienceTypeRegistry registry) {
        SkillsConfig config = new SkillsConfig(true, 0.02f, 10, 10, 0x00FF00);
        registry.register("mining_speed", config, MiningSpeedExperienceOrb::new, new ExperienceOrbFactory.MiningSpeedExperienceOrbFactory());
    }
}
