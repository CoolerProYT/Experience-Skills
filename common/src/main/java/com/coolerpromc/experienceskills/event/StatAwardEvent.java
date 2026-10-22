package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.api.event.StatEvents;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.api.ExperienceSkillsAPI;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import com.coolerpromc.experienceskills.config.value.WeightedStat;
import com.coolerpromc.experienceskills.skill.SkillManager;
import com.coolerpromc.experienceskills.entity.custom.ModExperienceOrb;
import com.coolerpromc.experienceskills.stat.ModStats;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class StatAwardEvent {
    private static final List<Identifier> MOVEMENT_STATS = List.of(Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.CROUCH_ONE_CM);
    private static final List<Identifier> WATER_MOVEMENT_STATS = List.of(Stats.WALK_ON_WATER_ONE_CM, Stats.WALK_UNDER_WATER_ONE_CM, Stats.SWIM_ONE_CM);
    private static final List<Identifier> BLOCK_INTERACTED_STATS = List.of(ModStats.BLOCK_BROKEN.get(), ModStats.BLOCK_PLACED.get());

    public static void onStatsAward(Player entity, Stat<?> stat, int value){
        StatEvents.SUBSCRIBERS.forEach(e -> e.onAward(entity, stat, value));

        if (entity instanceof ServerPlayer player){
            if (stat.getValue().equals(Stats.MOB_KILLS) || stat.getValue().equals(Stats.PLAYER_KILLS)){
                player.awardStat(ModStats.ENTITY_KILLED.get(), 1);
            }

            if (MOVEMENT_STATS.contains(stat.getValue())){
                combineStat(stat, value, player, ModStats.BLOCK_WALKED.get(), MOVEMENT_STATS);
            }

            if (WATER_MOVEMENT_STATS.contains(stat.getValue())){
                combineStat(stat, value, player, ModStats.WATER_WALKED.get(), WATER_MOVEMENT_STATS);
            }

            if (BLOCK_INTERACTED_STATS.contains(stat.getValue())){
                combineStat(stat, value, player, ModStats.BLOCK_INTERACTED.get(), BLOCK_INTERACTED_STATS);
            }

            if (!(stat.getValue() instanceof Identifier changed) || stat.getType() != Stats.CUSTOM) return;

            int previous = player.getStats().getValue(stat);
            if (value <= previous) return;

            for (ExperienceType type : ExperienceSkillsAPI.getEnabledTypes()){
                SkillsConfig config = type.getConfig();
                if (config.xpAwardActionCount() <= 0 || config.allStats().stream().noneMatch(s -> s.stat().equals(changed))) continue;

                long before = (long) Math.floor(weightedProgress(player, config, changed, previous) / config.xpAwardActionCount());
                long after = (long) Math.floor(weightedProgress(player, config, changed, value) / config.xpAwardActionCount());
                if (after <= before || SkillManager.reachedMaxLevel(player, type)) continue;

                long points = (after - before) * config.xpPointToAward();
                int amount = SkillManager.modifyGain(player, type, (int) Math.min(points, Integer.MAX_VALUE));
                if (amount > 0){
                    ModExperienceOrb.award(player.level(), player.position(), amount, type);
                }
            }
        }
    }

    public static double weightedProgress(ServerPlayer player, SkillsConfig config, Identifier changed, int changedValue){
        double total = 0;
        for (WeightedStat source : config.allStats()){
            int v = source.stat().equals(changed) ? changedValue : storedValue(player, source.stat());
            total += v * source.weight();
        }
        return total;
    }

    public static double weightedProgress(ServerPlayer player, SkillsConfig config){
        double total = 0;
        for (WeightedStat source : config.allStats()){
            total += storedValue(player, source.stat()) * source.weight();
        }
        return total;
    }

    private static int storedValue(ServerPlayer player, Identifier id){
        Identifier registered = BuiltInRegistries.CUSTOM_STAT.getValue(id);
        return registered == null ? 0 : player.getStats().getValue(Stats.CUSTOM.get(registered));
    }

    private static void combineStat(Stat<?> stat, int value, ServerPlayer player, Identifier statId, List<Identifier> list){
        int totalCm = 0;
        for (Identifier id : list){
            Stat<Identifier> movement = Stats.CUSTOM.get(id);
            totalCm += movement.equals(stat) ? value : player.getStats().getValue(movement);
        }

        int blockWalked = totalCm / 100;
        int storedStat = player.getStats().getValue(Stats.CUSTOM.get(statId));
        if (blockWalked > storedStat){
            player.awardStat(statId, blockWalked - storedStat);
        }
    }
}
