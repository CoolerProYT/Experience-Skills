package com.coolerpromc.experienceskills.platform;

import com.coolerpromc.experienceskills.ExperienceSkillsNeoForge;
import com.coolerpromc.experienceskills.api.IExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.List;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public List<IExperienceSkillsPlugin> getPlugins() {
        return ExperienceSkillsNeoForge.PLUGINS;
    }
}