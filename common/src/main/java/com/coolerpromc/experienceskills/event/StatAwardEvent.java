package com.coolerpromc.experienceskills.event;

import com.coolerpromc.experienceskills.api.event.StatEvents;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.api.type.ExperienceTypeRegistry;
import com.coolerpromc.experienceskills.entity.custom.ModExperienceOrb;
import com.coolerpromc.experienceskills.stat.ModStats;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;

public class StatAwardEvent {
    private static final List<Identifier> MOVEMENT_STATS = List.of(Stats.WALK_ONE_CM, Stats.SPRINT_ONE_CM, Stats.CROUCH_ONE_CM);
    private static final List<Identifier> WATER_MOVEMENT_STATS = List.of(Stats.WALK_ON_WATER_ONE_CM, Stats.WALK_UNDER_WATER_ONE_CM, Stats.SWIM_ONE_CM);
    private static final List<Identifier> BLOCK_INTERACTED_STATS = List.of(ModStats.BLOCK_BROKEN.get(), ModStats.BLOCK_PLACED.get());

    public static void onStatsAward(Player entity, Stat<?> stat, int value) {
        StatEvents.SUBSCRIBERS.forEach(e -> e.onAward(entity, stat, value));

        if (entity instanceof ServerPlayer player){
            if (stat.getValue().equals(Stats.MOB_KILLS) || stat.getValue().equals(Stats.PLAYER_KILLS)){
                player.awardStat(ModStats.ENTITY_KILLED.get(), 1);
            }

            if (MOVEMENT_STATS.contains(stat.getValue())) {
                combineStat(stat, value, player, ModStats.BLOCK_WALKED.get(), MOVEMENT_STATS);
            }

            if (WATER_MOVEMENT_STATS.contains(stat.getValue())) {
                combineStat(stat, value, player, ModStats.WATER_WALKED.get(), WATER_MOVEMENT_STATS);
            }

            if (BLOCK_INTERACTED_STATS.contains(stat.getValue())){
                combineStat(stat, value, player, ModStats.BLOCK_INTERACTED.get(), BLOCK_INTERACTED_STATS);
            }

            ExperienceTypeRegistry.REGISTERED_TYPES.forEach((s, _) -> {
                Optional<ExperienceType> type = ExperienceType.byName(s);

                if (type.isPresent()){
                    ExperienceType experienceType = type.get();
                    if (experienceType.getConfig().stat().equals(stat.getValue())){
                        int blockRequired = experienceType.getConfig().xpAwardActionCount();
                        int previous = player.getStats().getValue(stat);
                        if (value <= previous) return;

                        int before = previous / blockRequired;
                        int after  = value / blockRequired;
                        for (int i = before; i < after; i++) {
                            if (ModExperienceOrb.reachedMaxLevel(player, experienceType.getKey(), experienceType.getMaxLevel())) break;
                            ModExperienceOrb.award(player.level(), player.position(), experienceType.getConfig().xpPointToAward(), experienceType);
                        }
                    }
                }
            });
        }
    }

    private static void combineStat(Stat<?> stat, int value, ServerPlayer player, Identifier statId, List<Identifier> list){
        int totalCm = 0;
        for (Identifier id : list) {
            Stat<Identifier> movement = Stats.CUSTOM.get(id);
            totalCm += movement.equals(stat) ? value : player.getStats().getValue(movement);
        }

        int blockWalked = totalCm / 100;
        int storedStat = player.getStats().getValue(Stats.CUSTOM.get(statId));
        if (blockWalked > storedStat) {
            player.awardStat(statId, blockWalked - storedStat);
        }
    }
}
