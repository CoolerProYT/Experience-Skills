package com.coolerpromc.experienceskills.item;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.ExperienceSkillsAPI;
import com.coolerpromc.experienceskills.data.component.ModDataComponents;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {
    public static final RegistryHandler<CreativeModeTab, CreativeModeTab> BOTTLE = Services.REGISTRY.registerCreativeTab("bottle", () -> ModItems.EXPERIENCE_BOTTLE.toStack(), Component.translatable("creativeTabs.experieceskills.bottle"), (output, params) -> {
        ExperienceSkillsAPI.getEnabledTypes().forEach(type -> {
            ItemStack stack = ModItems.EXPERIENCE_BOTTLE.toStack();
            stack.set(ModDataComponents.EXPERIENCE_TYPE.get(), type);
            output.accept(stack);
        });
    });

    public static void init(){
        Constants.LOGGER.info("Registering creative tabs.");
    }
}
