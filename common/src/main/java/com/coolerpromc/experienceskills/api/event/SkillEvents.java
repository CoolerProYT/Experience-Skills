package com.coolerpromc.experienceskills.api.event;

import com.coolerpromc.experienceskills.api.type.ExperienceType;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Listener hooks for changes to a player's skills. Server-side only.
 */
public final class SkillEvents {
    public static final List<OnLevelChange> LEVEL_CHANGE = new ArrayList<>();
    public static final List<ModifyGain> MODIFY_GAIN = new ArrayList<>();

    /**
     * Fired whenever a player's level in a skill changes, including levels lost on death.
     */
    public static void registerLevelChange(OnLevelChange listener){
        LEVEL_CHANGE.add(listener);
    }

    /**
     * Adjusts experience earned from stats before it is spawned as orbs.
     */
    public static void registerModifyGain(ModifyGain modifier){
        MODIFY_GAIN.add(modifier);
    }

    @FunctionalInterface
    public interface OnLevelChange{
        void onLevelChange(ServerPlayer player, ExperienceType type, int oldLevel, int newLevel);
    }

    @FunctionalInterface
    public interface ModifyGain{
        int modify(ServerPlayer player, ExperienceType type, int amount);
    }
}
