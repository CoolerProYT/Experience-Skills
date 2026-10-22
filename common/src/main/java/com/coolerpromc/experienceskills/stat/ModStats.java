package com.coolerpromc.experienceskills.stat;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;
import net.minecraft.resources.Identifier;

public class ModStats {
    public static final RegistryHandler<Identifier, Identifier> BLOCK_WALKED = Services.REGISTRY.registerStat("block_walked");
    public static final RegistryHandler<Identifier, Identifier> WATER_WALKED = Services.REGISTRY.registerStat("water_walked");
    public static final RegistryHandler<Identifier, Identifier> BLOCK_BROKEN = Services.REGISTRY.registerStat("block_broken");
    public static final RegistryHandler<Identifier, Identifier> ENTITY_KILLED = Services.REGISTRY.registerStat("entity_killed");
    public static final RegistryHandler<Identifier, Identifier> UNDER_WATER_TIME = Services.REGISTRY.registerStat("under_water_time");
    public static final RegistryHandler<Identifier, Identifier> FISHING_TIME = Services.REGISTRY.registerStat("fishing_time");
    public static final RegistryHandler<Identifier, Identifier> ITEMS_FISHED = Services.REGISTRY.registerStat("items_fished");
    public static final RegistryHandler<Identifier, Identifier> BLOCK_INTERACTED = Services.REGISTRY.registerStat("block_interacted");
    public static final RegistryHandler<Identifier, Identifier> BLOCK_PLACED = Services.REGISTRY.registerStat("block_placed");
    public static final RegistryHandler<Identifier, Identifier> ORE_MINED = Services.REGISTRY.registerStat("ore_mined");
    public static final RegistryHandler<Identifier, Identifier> BLOCK_MINED_IN_WATER = Services.REGISTRY.registerStat("block_mined_in_water");
    public static final RegistryHandler<Identifier, Identifier> BLOCK_MINED_WITH_PICKAXE = Services.REGISTRY.registerStat("block_mined_with_pickaxe");
    public static final RegistryHandler<Identifier, Identifier> BLOCK_MINED_WITH_SHOVEL = Services.REGISTRY.registerStat("block_mined_with_shovel");
    public static final RegistryHandler<Identifier, Identifier> BLOCK_MINED_WITH_AXE = Services.REGISTRY.registerStat("block_mined_with_axe");
    public static final RegistryHandler<Identifier, Identifier> BLOCK_MINED_WITH_HOE = Services.REGISTRY.registerStat("block_mined_with_hoe");
    public static final RegistryHandler<Identifier, Identifier> BLOCK_MINED_WHEN_FLOATING = Services.REGISTRY.registerStat("block_mined_when_floating");
    public static final RegistryHandler<Identifier, Identifier> TIME_ON_FIRE = Services.REGISTRY.registerStat("time_on_fire");
    public static final RegistryHandler<Identifier, Identifier> ARROWS_HIT = Services.REGISTRY.registerStat("arrows_hit");
    public static final RegistryHandler<Identifier, Identifier> CROPS_HARVESTED = Services.REGISTRY.registerStat("crops_harvested");
    public static final RegistryHandler<Identifier, Identifier> FOOD_EATEN = Services.REGISTRY.registerStat("food_eaten");

    public static void init(){
        Constants.LOGGER.info("Registering stats.");
    }
}
