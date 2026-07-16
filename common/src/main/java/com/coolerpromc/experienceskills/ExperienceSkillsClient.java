package com.coolerpromc.experienceskills;

import com.coolerpromc.experienceskills.api.type.ExperienceTypeRegistry;
import com.coolerpromc.experienceskills.client.hud.HudRenderer;
import com.coolerpromc.experienceskills.client.model.tint.ExperienceBottleBorderTintSource;
import com.coolerpromc.experienceskills.client.model.tint.ExperienceBottleTintSource;
import com.coolerpromc.experienceskills.client.renderer.entity.ModExperienceOrbRenderer;
import com.coolerpromc.experienceskills.entity.ModEntities;
import com.coolerpromc.experienceskills.key.ModKeyMappings;
import com.coolerpromc.experienceskills.platform.ServicesClient;
import com.coolerpromc.experienceskills.platform.services.client.IClientRegistryHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
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
        initItemTintSource();
    }

    public static void initRenderer(){
        ExperienceTypeRegistry.REGISTERED_TYPES.forEach((s, registryHolder) -> {
            registerEntityRenderer(Objects.requireNonNull(ModEntities.byName(s)).get(), ModExperienceOrbRenderer::new);
        });
        registerEntityRenderer(ModEntities.EXPERIENCE_BOTTLE.get(), ThrownItemRenderer::new);
    }

    public static void initGuiLayer(){
        registerGuiLayer(Constants.id("skills_experience_bar"), HudRenderer::extractSkillsExperienceBar);
    }

    public static void initItemTintSource(){
        registerItemTintSource(Constants.id("experience_bottle"), ExperienceBottleTintSource.MAP_CODEC);
        registerItemTintSource(Constants.id("experience_bottle_border"), ExperienceBottleBorderTintSource.MAP_CODEC);
    }

    private static <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, EntityRendererProvider<T> provider){
        ServicesClient.REGISTRY.registerEntityRenderer(entityType, provider);
    }

    private static void registerGuiLayer(Identifier id, IClientRegistryHelper.ModGuiLayer layer){
        ServicesClient.REGISTRY.registerGuiLayer(id, layer);
    }

    private static void registerItemTintSource(Identifier id, MapCodec<? extends ItemTintSource> mapCodec){
        ServicesClient.REGISTRY.registerItemTintSource(id, mapCodec);
    }
}
