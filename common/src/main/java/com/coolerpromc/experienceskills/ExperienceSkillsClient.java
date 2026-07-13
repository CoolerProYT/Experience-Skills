package com.coolerpromc.experienceskills;

import com.coolerpromc.experienceskills.api.type.ExperienceTypeRegistry;
import com.coolerpromc.experienceskills.client.hud.HudRenderer;
import com.coolerpromc.experienceskills.client.renderer.entity.ModExperienceOrbRenderer;
import com.coolerpromc.experienceskills.entity.ModEntities;
import com.coolerpromc.experienceskills.key.ModKeyMappings;
import com.coolerpromc.experienceskills.platform.ServicesClient;
import com.coolerpromc.experienceskills.platform.services.client.IClientRegistryHelper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;

public class ExperienceSkillsClient {
    public static void init(){
        ModKeyMappings.init();
    }

    public static void initAll(){
        init();
        initRenderer();
        initGuiLayer();
    }

    public static void initRenderer(){
        ExperienceTypeRegistry.REGISTERED_TYPES.forEach((s, registryHolder) -> {
            registerEntityRenderer(Objects.requireNonNull(ModEntities.byName(s)).get(), ModExperienceOrbRenderer::new);
        });
    }

    public static void initGuiLayer(){
        registerGuiLayer(Constants.id("skills_experience_bar"), HudRenderer::extractSkillsExperienceBar);
    }

    private static <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, EntityRendererProvider<T> provider){
        ServicesClient.REGISTRY.registerEntityRenderer(entityType, provider);
    }

    private static void registerGuiLayer(Identifier id, IClientRegistryHelper.ModGuiLayer layer){
        ServicesClient.REGISTRY.registerGuiLayer(id, layer);
    }
}
