package com.coolerpromc.experienceskills;

import com.coolerpromc.experienceskills.api.type.ExperienceTypeRegistry;
import com.coolerpromc.experienceskills.command.ModArgumentTypeInfos;
import com.coolerpromc.experienceskills.config.ModCommonConfig;
import com.coolerpromc.experienceskills.data.attachment.ModDataAttachments;
import com.coolerpromc.experienceskills.entity.ModEntities;
import com.coolerpromc.experienceskills.entity.ModEntityDataSerializers;

public class ExperienceSkills {
    public static void init() {
        ExperienceTypeRegistry.init();
        ModCommonConfig.init();

        ModEntities.init();
        ModDataAttachments.init();
        ModEntityDataSerializers.init();
        ModArgumentTypeInfos.init();
    }
}