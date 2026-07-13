package com.coolerpromc.experienceskills;

import com.coolerpromc.experienceskills.api.IExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.command.ModCommands;
import com.coolerpromc.experienceskills.event.LivingDeathEvent;
import com.coolerpromc.experienceskills.event.PlayerBlockBreakEvent;
import com.coolerpromc.experienceskills.event.PlayerJoinEvent;
import com.coolerpromc.experienceskills.event.ServerStartedEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;
import java.util.List;

public class ExperienceSkillsFabric implements ModInitializer {
    public static final List<IExperienceSkillsPlugin> PLUGINS = new ArrayList<>();

    @Override
    public void onInitialize() {
        PLUGINS.addAll(FabricLoader.getInstance().getEntrypoints("experience_skills_plugin", IExperienceSkillsPlugin.class));

        ExperienceSkills.init();

        ServerLivingEntityEvents.AFTER_DEATH.register(LivingDeathEvent::onLivingDeath);
        ServerPlayerEvents.JOIN.register(PlayerJoinEvent::onPlayerJoined);
        CommandRegistrationCallback.EVENT.register(ModCommands::register);
        PlayerBlockBreakEvents.AFTER.register(PlayerBlockBreakEvent::afterBreak);
        ServerLifecycleEvents.SERVER_STARTED.register(ServerStartedEvent::onServerStarted);
    }
}
