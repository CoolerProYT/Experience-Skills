# Adding a Skill

Inside your plugin's `registerExperienceType`, call one of the registry's `register` overloads. Each skill needs a unique `name`, a `SkillsConfig` (its default settings) and the attribute it buffs.

## The `register` Overloads

```java
// Attribute bonus only
void register(String name, SkillsConfig config, Holder<Attribute> attributeHolder);

// Attribute bonus + custom per-refresh logic
void register(String name, SkillsConfig config, Holder<Attribute> attributeHolder, Handler handler);
```

- `name` — the skill id. Every derived id is built from it (for example `name + "_experience"` for the XP attachment), so it must be **unique**.
- `config` — the *default* `SkillsConfig`. It's written to the config file on first run and players can override every field afterward.
- `attributeHolder` — the attribute the level bonus is applied to, e.g. `Attributes.MOVEMENT_SPEED`.
- `handler` — optional; extra work run each time the skill is refreshed (see [Handlers](#handlers)).

## The `SkillsConfig` Record

```java
new SkillsConfig(
    boolean enabled,
    float   incrementPerLevel,
    int     xpAwardActionCount,
    int     xpPointToAward,
    int     rgbColor,
    AttributeModifier.Operation operation,
    int     maxLevel,
    Identifier stat,
    boolean dropOnDeath
)
```

Every field is documented on the [Skill Config](/config/skill-config) page. Note that `stat` must be a **custom statistic** — a built-in Experience Skills stat, a stat you registered yourself, or a `minecraft:custom` stat. Per-block / per-item statistics can't be used directly; pool them into a custom stat first (see [Adding a Stat](/developer/custom-stats)).

## Example: A Simple Skill

A "Prospecting" skill that raises Luck as you break blocks:

```java
@Override
public void registerExperienceType(IExperienceTypeRegistry registry) {
    registry.register(
        "prospecting",
        new SkillsConfig(
            true,                                        // enabled
            0.02f,                                       // +2% per level
            50,                                          // every 50 blocks broken
            5,                                           // award 5 points
            0x33CC66,                                    // orb/bar colour
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            100,                                         // level cap
            ModStats.BLOCK_BROKEN.id(),                  // driving stat
            true                                         // dropOnDeath
        ),
        Attributes.LUCK                                  // attribute buffed
    );
}
```

::: tip Referencing stats
Built-in stat ids are exposed through `ModStats` (e.g. `ModStats.BLOCK_BROKEN.id()`). Vanilla custom stats come from `net.minecraft.stats.Stats` (e.g. `Stats.JUMP`, `Stats.DAMAGE_DEALT`).
:::

## Handlers

A `Handler` runs *in addition to* the automatic attribute bonus — use it for effects a single modifier can't express, like adjusting a second attribute or running custom logic.

```java
@FunctionalInterface
interface Handler {
    void onUpdateSkill(ServerPlayer player, int level, ExperienceType experienceType);
}
```

It fires whenever the skill's status is refreshed — on login, on `/experienceskills` command changes, and each time the player levels the skill up — and **only when the skill's attribute is present on the player**.

::: warning Recompute from `level`, don't assume a delta
A handler can fire at any level, not just on a change. If its output depends on the level, recompute it from the supplied `level` each call rather than assuming it changed by one.
:::

### Example: The Built-in Jump Handler

The built-in `jump` skill uses a handler to keep `SAFE_FALL_DISTANCE` in step with the jump-height bonus, so higher jumps never turn into fall damage:

```java
registry.register(
    "jump",
    new SkillsConfig(true, 0.02f, 100, 5, 14392458,
        AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, Stats.JUMP, true),
    Attributes.JUMP_STRENGTH,
    (player, level, type) -> {
        AttributeInstance attr = player.getAttribute(Attributes.SAFE_FALL_DISTANCE);
        if (attr == null) return;

        float perLevel = type.getConfig().incrementPerLevel();
        float jumpStrengthAtLevel = 0.42f + (0.42f * perLevel * level);
        float safeFallBonus = safeFallDistanceForLevel(jumpStrengthAtLevel) - 3.0f;

        Identifier id = Constants.id("update_for_jump");
        attr.removeModifier(id);
        attr.addPermanentModifier(
            new AttributeModifier(id, safeFallBonus, AttributeModifier.Operation.ADD_VALUE));
    }
);
```

Note how it removes its old modifier before re-adding, and computes the bonus purely from `level` — both essential because the handler re-runs on every refresh.

## Enabling / Disabling at Runtime

If a skill is disabled in config, its attribute bonus is removed and it awards no experience. A handler should mirror this — if it manages an extra modifier, remove that modifier when `type.getConfig().enabled()` is `false`.
