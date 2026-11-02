package com.coolerpromc.experienceskills.loot;

import com.coolerpromc.experienceskills.api.ExperienceSkillsAPI;
import com.coolerpromc.experienceskills.config.ModGeneralConfig;
import com.coolerpromc.experienceskills.data.component.ModDataComponents;
import com.coolerpromc.experienceskills.item.ModItems;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import java.util.Set;

public final class SkillBottleLoot {
    private static final Set<String> EXPERIENCE_SKILLS_CHESTS = Set.of("chests/simple_dungeon", "chests/abandoned_mineshaft",
        "chests/desert_pyramid", "chests/jungle_temple", "chests/stronghold_library", "chests/ancient_city");

    public static void add(Container container, Level level, ResourceKey<LootTable> lootTableKey, long seed) {
        var id = lootTableKey.identifier();
        if (!id.getNamespace().equals("minecraft") || !EXPERIENCE_SKILLS_CHESTS.contains(id.getPath())) return;
        double chance = ModGeneralConfig.BOTTLE_LOOT_CHANCE.get();
        if (chance <= 0) return;
        RandomSource random = seed == 0 ? level.getRandom() : RandomSource.create(seed);
        var skills = ExperienceSkillsAPI.getEnabledTypes();
        if (skills.isEmpty() || random.nextDouble() >= chance) return;
        ItemStack bottle = new ItemStack(ModItems.EXPERIENCE_BOTTLE.get());
        bottle.set(ModDataComponents.EXPERIENCE_TYPE.get(), skills.get(random.nextInt(skills.size())));
        bottle.set(ModDataComponents.STORED_XP.get(), ModGeneralConfig.BOTTLE_LOOT_XP.get());
        // Preserve generated loot and choose uniformly among the remaining empty slots.
        int chosen = -1;
        int empty = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (container.getItem(i).isEmpty() && random.nextInt(++empty) == 0) chosen = i;
        }
        if (chosen >= 0) container.setItem(chosen, bottle);
    }
}
