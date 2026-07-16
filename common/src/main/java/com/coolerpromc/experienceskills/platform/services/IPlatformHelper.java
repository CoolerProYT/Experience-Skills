package com.coolerpromc.experienceskills.platform.services;

import com.coolerpromc.experienceskills.api.IExperienceSkillsPlugin;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.List;

public interface IPlatformHelper {
    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    Path getConfigDir();

    List<IExperienceSkillsPlugin> getPlugins();

    TagKey<Block> oreTag();
}