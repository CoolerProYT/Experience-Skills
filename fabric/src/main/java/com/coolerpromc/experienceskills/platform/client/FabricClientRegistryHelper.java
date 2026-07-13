package com.coolerpromc.experienceskills.platform.client;

import com.coolerpromc.experienceskills.platform.services.client.IClientRegistryHelper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class FabricClientRegistryHelper implements IClientRegistryHelper {
    private final List<EntityRendererEntry<?>> entityRenderers = new ArrayList<>();
    private final List<GuiLayerEntry> guiLayers = new ArrayList<>();

    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, EntityRendererProvider<T> provider) {
        this.entityRenderers.add(new EntityRendererEntry<>(entityType, provider));
    }

    @Override
    public void registerGuiLayer(Identifier id, ModGuiLayer layer) {
        this.guiLayers.add(new GuiLayerEntry(id, layer));
    }

    @Override
    public void applyEntityRendererRegistrations(EntityRendererRegistrar registrar) {
        for (EntityRendererEntry<?> entry : this.entityRenderers) {
            entry.register(registrar);
        }
    }

    @Override
    public void applyGuiLayerRegistrations(GuiLayerRegistrar registrar) {
        for (GuiLayerEntry entry : this.guiLayers) {
            entry.register(registrar);
        }
    }

    private record EntityRendererEntry<T extends Entity>(EntityType<T> entityType, EntityRendererProvider<T> provider) {
        private void register(EntityRendererRegistrar registrar) {
            registrar.register(this.entityType, this.provider);
        }
    }

    private record GuiLayerEntry(Identifier id, ModGuiLayer layer) {
        private void register(GuiLayerRegistrar registrar) {
            registrar.register(this.id, this.layer);
        }
    }
}
