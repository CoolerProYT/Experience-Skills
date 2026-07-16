package com.coolerpromc.experienceskills.item;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.item.custom.ModExperienceBottle;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public class ModItems {
    public static RegistryHandler.Items<ModExperienceBottle> EXPERIENCE_BOTTLE = register("experience_bottle", ModExperienceBottle::new, new Item.Properties().rarity(Rarity.UNCOMMON).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));

    public static <T extends Item> RegistryHandler.Items<T> register(String name, Function<Item.Properties, T> func, Item.Properties properties){
        return Services.REGISTRY.registerItem(name, func, properties);
    }

    public static void init(){
        Constants.LOGGER.info("Registering items.");
    }
}
