# Events

Experience Skills exposes a lightweight listener hook, `StatEvents`, fired every time a player is awarded a stat — **before** the mod does its own processing. Use it to react to progress an addon cares about (or to [pool statistics into a custom stat](/developer/custom-stats#pooling-vanilla-statistics)) without hooking each loader's events yourself.

## Registering a Listener

```java
StatEvents.register((player, stat, value) -> {
    // your logic
});
```

There is no removal — register once during setup.

## The Listener Contract

```java
@FunctionalInterface
interface OnAward {
    void onAward(Player player, Stat<?> stat, int value);
}
```

| Parameter | Meaning |
|---|---|
| `player` | The player awarded the stat. **May be a client-side player** — side-check if needed. |
| `stat` | The stat that was awarded. |
| `value` | The stat's new total value. |

::: warning Fires on both sides
Vanilla syncs stats to the client, so this path runs on **both the logical client and the logical server**. A listener that only wants server logic must check `player instanceof ServerPlayer` itself, and should filter by `stat` so it only reacts to the ones it cares about.
:::

## What You'll See

Listeners are invoked for **every** stat award — including Experience Skills' own normalised stats such as `entity_killed` and `block_walked`, not only vanilla ones. Listeners run in registration order.

## Example: Reacting to Kills

```java
StatEvents.register((player, stat, value) -> {
    if (!(player instanceof ServerPlayer serverPlayer)) return;
    if (!stat.getValue().equals(ModStats.ENTITY_KILLED.get())) return;

    // value is the player's new total entity_killed count
    if (value % 100 == 0) {
        serverPlayer.sendSystemMessage(
            Component.literal("Milestone: " + value + " kills!"));
    }
});
```

## Skill Events

`SkillEvents` holds two more hooks. Both fire on the **logical server only**.

### Level changes

```java
SkillEvents.registerLevelChange((player, type, oldLevel, newLevel) -> {
    if (newLevel > oldLevel && newLevel % 10 == 0) {
        player.sendSystemMessage(Component.literal("Level " + newLevel + "!"));
    }
});
```

Fired whenever a player's level in a skill changes for any reason: collecting orbs, commands, the API, transfers, or losing experience on death. `newLevel` is lower than `oldLevel` when levels were lost. The mod's own advancements and scoreboard export are built on this hook.

### Modifying experience gains

```java
// Double all Strength experience earned at night
SkillEvents.registerModifyGain((player, type, amount) ->
    type.getSerializedName().equals("strength") && player.level().isDarkOutside() ? amount * 2 : amount);
```

Each modifier receives the amount returned by the previous one and returns the amount to award. It applies to experience earned from stats, before it is spawned as orbs; it does not affect commands or `ExperienceSkillsAPI.givePoints`. The Skill Insight potion is built on this hook.

## Related

- [Adding a Stat](/developer/custom-stats) uses `StatEvents` to pool vanilla statistics into a custom stat.
- For changing progress directly rather than reacting to it, see the [API Reference](/developer/api-reference).
