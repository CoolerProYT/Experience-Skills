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

## Related

- [Adding a Stat](/developer/custom-stats) uses `StatEvents` to pool vanilla statistics into a custom stat.
- For changing progress directly rather than reacting to it, see the [API Reference](/developer/api-reference).
