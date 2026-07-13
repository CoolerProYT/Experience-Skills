package com.coolerpromc.experienceskills.api.type;

import com.coolerpromc.experienceskills.api.IExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import com.coolerpromc.experienceskills.entity.custom.AbstractExperienceOrb;
import com.coolerpromc.experienceskills.entity.helper.ExperienceOrbFactory;
import com.coolerpromc.experienceskills.platform.Services;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class ExperienceTypeRegistry implements IExperienceTypeRegistry{
    public static final ExperienceTypeRegistry INSTANCE = new ExperienceTypeRegistry();
    public static final Map<String, RegistryHolder> REGISTERED_TYPES = new HashMap<>();

    @Override
    public void register(String name, SkillsConfig config, EntityType.EntityFactory<? extends AbstractExperienceOrb> factory, ExperienceOrbFactory orbFactory) {
        REGISTERED_TYPES.put(name, new RegistryHolder(name, config, factory, orbFactory));
    }

    public static void init(){
        for (IExperienceSkillsPlugin plugin : Services.PLATFORM.getPlugins()) {
            plugin.registerExperienceType(INSTANCE);
        }
    }

    public record RegistryHolder(String name, SkillsConfig config, EntityType.EntityFactory<? extends AbstractExperienceOrb> factory, ExperienceOrbFactory orbFactory){
    }
}
