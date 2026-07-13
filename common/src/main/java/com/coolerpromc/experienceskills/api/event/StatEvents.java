package com.coolerpromc.experienceskills.api.event;

import net.minecraft.stats.Stat;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A lightweight listener hook fired every time a player is awarded a stat, before Experience Skills does
 * its own processing. Use it to react to progress an addon cares about without hooking the loaders yourself.
 *
 * <p><b>Contract:</b> listeners are invoked for <i>every</i> stat award, on both the logical client and
 * the logical server (vanilla syncs stats to the client, which also runs this path), and are handed a raw
 * {@link Player}. A listener that only wants server-side logic must check
 * {@code player instanceof ServerPlayer} itself, and should filter by {@code stat}. Listeners also see the
 * mod's own normalised stats (such as {@code entity_killed} or {@code block_walked}), not only vanilla ones.
 */
public final class StatEvents {
    /** The registered listeners, invoked in registration order. */
    public static final List<OnAward> SUBSCRIBERS = new ArrayList<>();

    /**
     * Registers a listener to be notified of every stat award. There is no removal; register once during
     * setup.
     *
     * @param onAward the listener to add
     */
    public static void register(OnAward onAward){
        SUBSCRIBERS.add(onAward);
    }

    /** A listener notified when a player is awarded a stat. */
    @FunctionalInterface
    public interface OnAward{
        /**
         * @param player the player awarded the stat; may be a client-side player, so side-check if needed
         * @param stat   the stat that was awarded
         * @param value  the stat's new total value
         */
        void onAward(Player player, Stat<?> stat, int value);
    }
}
