# Adding a Stat

A custom stat lets you drive a skill from an action that Minecraft doesn't already expose as a single custom statistic — or from a *combination* of statistics pooled together. Registering a stat gives you an `Identifier` you can award and reference from a skill's `SkillsConfig`.

## 1. Register the Stat

Register during your plugin's registration phase (from inside `registerExperienceType`, or your mod's startup), so it happens before the game freezes the registries.

```java
// Under your own mod id
RegistryHandler<Identifier, Identifier> myStat =
    ExperienceSkillsAPI.registerCustomStat("mymod", "ores_mined");
```

The returned handle gives you the stat's `Identifier` via `myStat.get()` (or `.id()`), which is what you pass around.

::: tip
Experience Skills' own built-in stats are registered the same way and exposed through `ModStats` — for example `ModStats.BLOCK_BROKEN`. Yours will live under your own namespace.
:::

## 2. Award the Stat

A custom stat only advances when you award it. Award it on the server whenever the action you're tracking happens:

```java
player.awardStat(myStat.get(), 1);
```

Every award runs through Experience Skills' processing, so any skill driven by this stat earns experience automatically — you don't need to touch the skill from here.

## 3. Drive a Skill From It

Reference the stat's id in a skill's `SkillsConfig`:

```java
registry.register(
    "mining_mastery",
    new SkillsConfig(true, 0.02f, 100, 5, 0x99AABB,
        AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100,
        myStat.id(), true),           // ← driven by your custom stat
    Attributes.BLOCK_BREAK_SPEED
);
```

## Pooling Vanilla Statistics

The most common reason to add a stat is to **pool several vanilla statistics into one**, the way the built-in `block_walked` combines walking, sprinting and crouching distance. To do that, listen for stat awards and mirror the combined total onto your custom stat.

Subscribe to [`StatEvents`](/developer/events) and, whenever one of the source stats changes, recompute the pooled total and award the difference:

```java
StatEvents.register((player, stat, value) -> {
    if (!(player instanceof ServerPlayer serverPlayer)) return;

    List<Identifier> sources = List.of(
        Stats.MINE_BLOCK.get(Blocks.STONE),      // example sources
        Stats.MINE_BLOCK.get(Blocks.DEEPSLATE)
    );
    if (!sources.contains(stat.getValue())) return;

    int total = 0;
    for (Identifier id : sources) {
        Stat<Identifier> s = Stats.CUSTOM.get(id); // adapt to the stat category you pool
        total += serverPlayer.getStats().getValue(s);
    }

    int stored = serverPlayer.getStats().getValue(Stats.CUSTOM.get(myStat.get()));
    if (total > stored) {
        serverPlayer.awardStat(myStat.get(), total - stored);
    }
}
```

Awarding the **difference** (not the total) keeps your pooled stat in step without double-counting.

::: warning Units carry over
If you pool distances, remember vanilla measures them in centimetres. The built-in movement stats divide by 100 so `block_walked` counts whole blocks — do the same conversion in your pool if you want your `xpAwardActionCount` to be "per block".
:::

## Rules

- Register **before registries freeze** — during your plugin/registration phase.
- Only **custom statistics** can drive a skill. Registering a stat here creates exactly that kind, so it's always valid as a skill's `stat`.
- Stat awards fire on **both logical sides** (vanilla syncs stats to the client). Guard server-only logic with `player instanceof ServerPlayer`.
