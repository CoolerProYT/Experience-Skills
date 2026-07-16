package com.coolerpromc.experienceskills;

import com.coolerpromc.experienceskills.client.event.ClientTickEvent;
import com.coolerpromc.experienceskills.platform.ServicesClient;
import com.coolerpromc.experienceskills.platform.client.NeoForgeKeyHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.apache.logging.log4j.util.Lazy;

@Mod(value = Constants.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Constants.MODID, value = Dist.CLIENT)
public class ExperienceSkillsClientNeoForge {
    public ExperienceSkillsClientNeoForge(IEventBus eventBus){
        ExperienceSkillsClient.init();
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        ExperienceSkillsClient.initRenderer();
        ServicesClient.REGISTRY.applyEntityRendererRegistrations(event::registerEntityRenderer);
    }

    @SubscribeEvent
    public static void onRegisterGuiLayer(RegisterGuiLayersEvent event) {
        ExperienceSkillsClient.initGuiLayer();
        ServicesClient.REGISTRY.applyGuiLayerRegistrations((id, layer) -> event.registerAboveAll(id, layer::render));
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        for (Lazy<KeyMapping> mapping : NeoForgeKeyHelper.mappings){
            event.register(mapping.get());
        }
    }

    @SubscribeEvent
    public static void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        ClientTickEvent.onClientTickEnd(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onRegisterColorHandlersItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        ExperienceSkillsClient.initItemTintSource();
        ServicesClient.REGISTRY.applyItemTintSourceRegistrations(event::register);
    }
}
