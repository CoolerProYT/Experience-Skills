package com.coolerpromc.experienceskills.api;

import com.coolerpromc.experienceskills.api.type.IExperienceTypeRegistry;

public interface IExperienceSkillsPlugin {
    void registerExperienceType(IExperienceTypeRegistry registry);
}
