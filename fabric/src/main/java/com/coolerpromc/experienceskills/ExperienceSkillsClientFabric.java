package com.coolerpromc.experienceskills;

import com.coolerpromc.experienceskills.client.event.ClientTickEvent;
import com.coolerpromc.experienceskills.platform.ServicesClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class ExperienceSkillsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ExperienceSkillsClient.initAll();
        ServicesClient.REGISTRY.applyEntityRendererRegistrations(EntityRenderers::register);
        ServicesClient.REGISTRY.applyGuiLayerRegistrations((id, layer) -> HudElementRegistry.addLast(id, layer::render));

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvent::onClientTickEnd);
    }
}
