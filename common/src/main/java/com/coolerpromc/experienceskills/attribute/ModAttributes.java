package com.coolerpromc.experienceskills.attribute;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ModAttributes {
    public static final RegistryHandler<Attribute, Attribute> LURE_SPEED = register("lure_speed", new RangedAttribute("attribute.name.lure_speed", 0.0, 0.0, 1024).setSyncable(true));
    public static final RegistryHandler<Attribute, Attribute> FISHING_LUCK = register("fishing_luck", new RangedAttribute("attribute.name.fishing_luck", 0.0, 0.0, 1024).setSyncable(true));
    public static final RegistryHandler<Attribute, Attribute> SWIM_SPEED = register("swim_speed", new RangedAttribute("attribute.name.swim_speed", 1, 0.0, 1024).setSyncable(true));
    public static final RegistryHandler<Attribute, Attribute> MINING_LUCK = register("mining_luck", new RangedAttribute("attribute.name.mining_luck", 0, 0.0, 1024).setSyncable(true));
    public static final RegistryHandler<Attribute, Attribute> PICKAXE_MINING_SPEED = register("pickaxe_mining_speed", new RangedAttribute("attribute.name.pickaxe_mining_speed", 1.0, 0.0, 1024).setSyncable(true));
    public static final RegistryHandler<Attribute, Attribute> SHOVEL_MINING_SPEED = register("shovel_mining_speed", new RangedAttribute("attribute.name.shovel_mining_speed", 1.0, 0.0, 1024).setSyncable(true));
    public static final RegistryHandler<Attribute, Attribute> AXE_MINING_SPEED = register("axe_mining_speed", new RangedAttribute("attribute.name.axe_mining_speed", 1.0, 0.0, 1024).setSyncable(true));
    public static final RegistryHandler<Attribute, Attribute> HOE_MINING_SPEED = register("hoe_mining_speed", new RangedAttribute("attribute.name.hoe_mining_speed", 1.0, 0.0, 1024).setSyncable(true));
    public static final RegistryHandler<Attribute, Attribute> FLOATING_MINING_SPEED = register("floating_mining_speed", new RangedAttribute("attribute.name.floating_mining_speed", 0, 0.0, 4).setSyncable(true));

    public static RegistryHandler<Attribute, Attribute> register(String name, Attribute attribute){
        return Services.REGISTRY.registerAttribute(name, attribute);
    }

    public static void init(){
        Constants.LOGGER.info("Registering attributes");
    }
}
