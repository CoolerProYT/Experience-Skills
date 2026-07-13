package com.coolerpromc.experienceskills.command;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.command.argument.ExperienceTypeArgument;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;

public class ModArgumentTypeInfos {
    public static final RegistryHandler<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<ExperienceTypeArgument, ExperienceTypeArgument.Info.Template>> EXPERIENCE_TYPE = Services.REGISTRY.registerArgumentTypeInfo("experience_type", ExperienceTypeArgument.class, new ExperienceTypeArgument.Info());

    public static void init(){
        Constants.LOGGER.info("Registering argument type info.");
    }
}
