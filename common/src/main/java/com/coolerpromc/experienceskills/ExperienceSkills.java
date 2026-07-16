package com.coolerpromc.experienceskills;

import com.coolerpromc.experienceskills.api.type.ExperienceTypeRegistry;
import com.coolerpromc.experienceskills.attribute.ModAttributes;
import com.coolerpromc.experienceskills.command.ModArgumentTypeInfos;
import com.coolerpromc.experienceskills.config.ModCommonConfig;
import com.coolerpromc.experienceskills.data.attachment.ModDataAttachments;
import com.coolerpromc.experienceskills.data.component.ModDataComponents;
import com.coolerpromc.experienceskills.entity.ModEntities;
import com.coolerpromc.experienceskills.entity.ModEntityDataSerializers;
import com.coolerpromc.experienceskills.item.ModCreativeTabs;
import com.coolerpromc.experienceskills.item.ModItems;
import com.coolerpromc.experienceskills.stat.ModStats;

public class ExperienceSkills {
    public static void init() {
        ModStats.init();

        ExperienceTypeRegistry.init();
        ModCommonConfig.init();

        ModItems.init();
        ModCreativeTabs.init();
        ModDataComponents.init();
        ModEntities.init();
        ModDataAttachments.init();
        ModEntityDataSerializers.init();
        ModArgumentTypeInfos.init();
        ModAttributes.init();
    }
}