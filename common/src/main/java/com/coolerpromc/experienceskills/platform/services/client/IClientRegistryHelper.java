package com.coolerpromc.experienceskills.platform.services.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public interface IClientRegistryHelper {
    <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, EntityRendererProvider<T> provider);
    void registerGuiLayer(Identifier id, ModGuiLayer layer);

    void applyEntityRendererRegistrations(EntityRendererRegistrar registrar);
    void applyGuiLayerRegistrations(GuiLayerRegistrar registrar);

    interface EntityRendererRegistrar {
        <T extends Entity> void register(EntityType<T> entityType, EntityRendererProvider<T> provider);
    }
    interface GuiLayerRegistrar {
        void register(Identifier id, ModGuiLayer layer);
    }

    @FunctionalInterface
    interface ModGuiLayer {
        void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);
    }
}
