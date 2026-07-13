package com.coolerpromc.experienceskills.api.type;

import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import com.coolerpromc.experienceskills.entity.custom.AbstractExperienceOrb;
import com.coolerpromc.experienceskills.entity.helper.ExperienceOrbFactory;
import net.minecraft.world.entity.EntityType;

public interface IExperienceTypeRegistry {
    void register(String name, SkillsConfig config, EntityType.EntityFactory<? extends AbstractExperienceOrb> factory, ExperienceOrbFactory orbFactory);
}
