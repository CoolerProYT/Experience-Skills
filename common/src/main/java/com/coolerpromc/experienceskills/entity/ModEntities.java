package com.coolerpromc.experienceskills.entity;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.type.ExperienceTypeRegistry;
import com.coolerpromc.experienceskills.entity.custom.AbstractExperienceOrb;
import com.coolerpromc.experienceskills.entity.custom.SpeedExperienceOrb;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class ModEntities {
    public static final List<RegistryHandler.Entities<? extends AbstractExperienceOrb>> ALL = new ArrayList<>();

    public static final RegistryHandler.Entities<SpeedExperienceOrb> SPEED_EXPERIENCE_ORB = register("speed_experience_orb", SpeedExperienceOrb::new, MobCategory.MISC, b -> b.noLootTable().sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(20));

    public static <T extends AbstractExperienceOrb> RegistryHandler.Entities<T> register(String name, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> builder){
        RegistryHandler.Entities<T> entities = Services.REGISTRY.registerEntity(name, factory, category, builder);
        ALL.add(entities);
        return entities;
    }

    public static RegistryHandler.Entities<? extends AbstractExperienceOrb> byName(String name){
        if (!name.contains("_experience_orb")) name = name.concat("_experience_orb");

        for (RegistryHandler.Entities<? extends AbstractExperienceOrb> entities : ALL) {
            if (entities.id().getPath().equals(name)){
                return entities;
            }
        }

        return null;
    }

    public static void init(){
        Constants.LOGGER.info("Registering entities.");

        // TODO: Temporarily code to test API
        ExperienceTypeRegistry.REGISTERED_TYPES.forEach((s, registryHolder) -> {
            register(s + "_experience_orb", registryHolder.factory(), MobCategory.MISC, b -> b.noLootTable().sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(20));
        });
    }
}
