# Experience Skills

> Level up your skills by collecting different experiences.

A multi-loader (Fabric + NeoForge) Minecraft mod for **MC 26.1.2 / Java 25**. It adds parallel, per-skill experience tracks alongside vanilla XP. Each skill has its own coloured experience orb entity, its own XP pool on the player, its own HUD bar, and an attribute bonus that scales with the skill's level.

A skill is **declared, not coded**. You name a *stat* to earn XP from and an *attribute* to buff, and everything else — config entry, data attachment, orb entity, renderer, HUD bar, XP generation, level bonus, death drop, command support — is derived for you.

The built-in skills are registered through the same API any addon uses — they're the reference for every technique in this document:

| Skill | Attribute | Driven by stat | Earns XP when | Shows off |
| --- | --- | --- | --- | --- |
| `speed` | `MOVEMENT_SPEED` | `experienceskills:block_walked` | You walk/sprint/crouch 100 blocks | combined stat |
| `breaking_speed` | `BLOCK_BREAK_SPEED` | `experienceskills:block_broken` | You break 10 blocks | event-fed custom stat |
| `strength` | `ATTACK_DAMAGE` | `experienceskills:entity_killed` | You kill 20 mobs or players | combined stat |
| `vitality` | `MAX_HEALTH` | `minecraft:damage_taken` | You take 10 hearts of damage | plain vanilla stat |
| `jump` | `JUMP_STRENGTH` | `minecraft:jump` | You jump 100 times | **a handler** (adjusts safe-fall) |
| `oxygen` | `OXYGEN_BONUS` | `experienceskills:under_water_time` | You spend 60s underwater | tick-fed custom stat |
| `swim_speed` | `WATER_MOVEMENT_EFFICIENCY` | `experienceskills:water_walked` | You move 100 blocks on/under water | combined stat |
| `fishing_speed` | `experienceskills:lure_speed` | `experienceskills:fishing_time` | You fish for 60s | **custom attribute** |
| `fishing_luck` | `experienceskills:fishing_luck` | `experienceskills:items_fished` | You reel in 10 catches | **custom attribute** |

The first four columns are all a JSON file needs. The last column points to the three techniques that require a plugin — [combined/custom stats](#6-driving-a-skill-from-your-own-stat), [custom attributes](#7-using-your-own-attribute), and [a handler](#8-effects-beyond-an-attribute-modifier) for effects a plain modifier can't express.

---

## 1. Two ways to add a skill

|  | [**A JSON file**](#2-route-a-a-json-file-no-code) | [**A plugin**](#3-route-b-the-plugin-api) |
| --- | --- | --- |
| Requires code | No | Yes — a mod |
| XP source | Any **existing** stat | Any existing stat, **or one you register** |
| Attribute | Any **existing** attribute | Any existing attribute, **or one you register** |
| Good for | Packs, servers, tweaking | Mods adding genuinely new mechanics |

Both routes end up in the same place. Start with the JSON file — if the stat and the attribute you want already exist, that's the whole job, and it's the majority of skills.

---

## 2. Route A: a JSON file (no code)

Drop a `.json` file into **`config/experienceskills/skills/`** (the directory is created for you on first launch). Every file in there is loaded at startup and becomes a skill.

`config/experienceskills/skills/jump.json`
```json
{
  "name": "jump",
  "attribute": "minecraft:jump_strength",
  "defaultConfig": {
    "enabled": true,
    "incrementPerLevel": 0.01,
    "xpAwardActionCount": 100,
    "xpPointToAward": 3,
    "rgbColor": 5635925,
    "operation": "add_value",
    "maxLevel": 30,
    "stat": "minecraft:jump"
  }
}
```

That's a complete, working skill: every 100 jumps the player is awarded 3 points of `jump` XP, their `JUMP_STRENGTH` rises by `0.01` per level, and they get a green orb, a HUD bar, a death drop and `/experienceskills` support. No mod, no code.

| Field | Meaning |
| --- | --- |
| `name` | The skill id. **This is what counts** — the filename is only used in error messages, though naming the file after the skill keeps things sane. |
| `attribute` | Any registered attribute id, e.g. `minecraft:movement_speed`, `minecraft:max_health`, `minecraft:luck`. |
| `defaultConfig` | The skill's default settings — see [§4](#4-skillsconfig). Written into the main config on first run; the main config owns the live values from then on. |

Three things to know:

- **`rgbColor` must be decimal.** JSON has no hex literals, so `0x55FF55` is written `5635925`.
- **A bad `stat` fails loudly at server start.** The stat is checked against the custom-stat registry on startup, so a typo throws immediately rather than producing a skill that silently never earns XP.
- **A malformed file is skipped, not fatal.** It logs an error naming the file and the server keeps starting.

If the stat or the attribute you want doesn't exist yet, or the skill needs to *do* something a single attribute modifier can't express (adjust a second attribute, run per-tick logic), you need a mod — see [§6](#6-driving-a-skill-from-your-own-stat), [§7](#7-using-your-own-attribute) and [§8](#8-effects-beyond-an-attribute-modifier).

---

## 3. Route B: the plugin API

```java
@ExperienceSkillsPlugin                                    // NeoForge discovery marker
public final class MyPlugin implements IExperienceSkillsPlugin {
    @Override
    public void registerExperienceType(IExperienceTypeRegistry registry) {
        SkillsConfig config = new SkillsConfig(
            true,                                          // enabled
            0.01f,                                         // incrementPerLevel
            100,                                           // xpAwardActionCount
            3,                                             // xpPointToAward
            0x55FF55,                                      // rgbColor
            AttributeModifier.Operation.ADD_VALUE,         // operation
            30,                                            // maxLevel
            Stats.JUMP                                     // stat that drives it
        );

        registry.register("jump", config, Attributes.JUMP_STRENGTH);
    }
}
```

Identical result to the JSON above — the config-driven route is a thin wrapper over this exact call. Reach for a plugin when you need to **register your own stat** ([§6](#6-driving-a-skill-from-your-own-stat)), **your own attribute** ([§7](#7-using-your-own-attribute)), or a **handler** for effects a plain modifier can't express ([§8](#8-effects-beyond-an-attribute-modifier)) — none of which JSON can do.

`register` has a fourth, optional argument — a `Handler` — covered in [§8](#8-effects-beyond-an-attribute-modifier):

```java
void register(String name, SkillsConfig config, Holder<Attribute> attribute);
void register(String name, SkillsConfig config, Holder<Attribute> attribute, Handler handler);
```

### Declaring the plugin to the loader

- **Fabric** — add a custom entrypoint in `fabric.mod.json`:
  ```json
  "entrypoints": {
      "experience_skills_plugin": ["com.example.MyPlugin"]
  }
  ```
- **NeoForge** — nothing to declare. Every mod file is scanned for `@ExperienceSkillsPlugin`, and each class implementing `IExperienceSkillsPlugin` is reflectively instantiated (so it needs a public no-arg constructor).

Both loaders collect plugins **before** `ExperienceSkills.init()` runs, so the registry is complete before anything else initialises.

---

## 4. `SkillsConfig`

Whichever route you take, this is the skill. What you supply are the **defaults**: they are written into the main config file on first run, and players can override every field afterwards.

| Field | Meaning |
| --- | --- |
| `enabled` | Whether the skill is active. Disabled: no XP is awarded and the attribute bonus is removed. |
| `incrementPerLevel` | How much the attribute changes per level, applied via `operation`. With `add_multiplied_base`, `0.02` = +2% of the base value per level. May be negative. |
| `xpAwardActionCount` | How far `stat` must advance before an award fires. |
| `xpPointToAward` | Points awarded each time `xpAwardActionCount` is reached. |
| `rgbColor` | Orb and HUD-bar tint. Decimal in JSON. |
| `operation` | `add_value` (flat), `add_multiplied_base` (% of the attribute's base) or `add_multiplied_total` (% of the final value). |
| `maxLevel` | Level cap. Awards stop once reached. |
| `stat` | The `Identifier` of the **custom stat** that drives this skill. |

---

## 5. How XP generation works

Every stat award a player receives passes through `StatAwardEvent.onStatsAward` — on Fabric via a mixin on `StatsCounter#setValue`, on NeoForge via the native `StatAwardEvent`. It does two things:

1. **Normalises vanilla stats into the mod's own stats.** Vanilla splits movement across `WALK_ONE_CM` / `SPRINT_ONE_CM` / `CROUCH_ONE_CM` and kills across `MOB_KILLS` / `PLAYER_KILLS`, so the mod folds those into single counters: `block_walked` and `entity_killed`. `PlayerBlockBreakEvent` feeds `block_broken` the same way.
2. **Runs one generic award loop** over every registered skill. If the stat just awarded matches that skill's `stat`, and the running total crossed a multiple of `xpAwardActionCount`, the skill is awarded `xpPointToAward` points — once per multiple crossed, so a stat that jumps by more than one can't skip its threshold.

Progress therefore lives **in a stat**, not in a bespoke counter. It persists across sessions, survives death, and shows up in the vanilla Statistics screen for free.

### The one hard rule: skills match **custom** stats only

Matching compares your `Identifier` against `stat.getValue()`, which is an `Identifier` only for `Stats.CUSTOM`. Registry stat types — `BLOCK_MINED`, `ITEM_USED`, `ENTITY_KILLED` — hold a `Block` / `Item` / `EntityType` instead, and are counted **per object** (mining stone and mining dirt are two separate counters), so they can't be named directly.

To drive a skill from one of those, register your own custom stat and feed it from an event — exactly what `block_broken` does.

### Vanilla custom stats you can point at directly

Anything in `Stats` typed as an `Identifier` works with no extra code, from either route: `minecraft:jump`, `minecraft:damage_dealt`, `minecraft:damage_taken`, `minecraft:fish_caught`, `minecraft:animals_bred`, `minecraft:item_enchanted`, `minecraft:swim_one_cm`, and so on — the full list is the "General" tab of the Statistics screen.

**Mind the units.** Distance stats count **centimetres** (100 = one block) and damage stats count **tenths of a heart**. Scale `xpAwardActionCount` accordingly.

---

## 6. Driving a skill from your own stat

Use this when no existing stat fits — you need to count something vanilla doesn't (blocks smelted, distance flown on a custom mount), or to pool a per-object registry stat into one counter (all blocks mined, regardless of block).

A custom stat is just an `Identifier` registered into `BuiltInRegistries.CUSTOM_STAT`. **Register it in your own namespace, from your own mod** — the mod's internal `registerStat` helper hardcodes the `experienceskills` namespace, so it isn't usable by addons.

**NeoForge**
```java
public static final DeferredRegister<Identifier> STATS =
    DeferredRegister.create(BuiltInRegistries.CUSTOM_STAT, "mymod");

public static final DeferredHolder<Identifier, Identifier> BLOCKS_SMELTED =
    STATS.register("blocks_smelted", () -> Identifier.fromNamespaceAndPath("mymod", "blocks_smelted"));
```

**Fabric**
```java
public static final Identifier BLOCKS_SMELTED = Identifier.fromNamespaceAndPath("mymod", "blocks_smelted");

static {
    Registry.register(BuiltInRegistries.CUSTOM_STAT, BLOCKS_SMELTED, BLOCKS_SMELTED);
}
```

Then point a skill at it — from a plugin, or from a JSON file, since by now the stat exists — and **award it yourself** from whatever event is right for your skill:

```java
// somewhere on the server, when a furnace finishes:
serverPlayer.awardStat(ModStats.BLOCKS_SMELTED, 1);
```

That single `awardStat` call is all the mod needs. It flows through `StatAwardEvent`, the generic loop sees the stat advance, and the orb spawns on its own — you never call `award()` and never touch orbs, levels or attachments.

Three things to get right:

- **Register the stat before it is awarded.** Building a `Stat` looks the value up in the registry to construct its translation key, so an unregistered `Identifier` throws.
- **Award it on the server** (`ServerPlayer`), or nothing happens.
- **Add a lang key**, or it renders as a raw key in the Statistics screen. The format is `stat.<namespace>.<path>` — so `"stat.mymod.blocks_smelted": "Blocks Smelted"`.

If your XP source isn't a countable action at all, `AbstractExperienceOrb.award(serverLevel, pos, points, type)` is public and can be called directly. It splits the amount into vanilla-sized orbs, merges into nearby ones, skips disabled types, and spawns the result. But prefer a stat: you get persistence, death-safety and the Statistics screen for nothing.

---

## 7. Using your own attribute

Naming an **existing** attribute is the easy path — the engine already knows what `MOVEMENT_SPEED` or `ATTACK_DAMAGE` *do*, so the mod applying a modifier to it is the whole job. That's why Route A is limited to them.

A plugin can equally use a **custom** attribute — `fishing_speed` and `fishing_luck` do, registering `lure_speed` and `fishing_luck` in `ModAttributes`. The mod will keep the modifier in sync — on every level-up, login and command it rewrites `<name>_skill_bonus` on that attribute to `level × incrementPerLevel` using your `operation`. What it will **not** do is give the attribute any meaning. A custom attribute is an inert number until something reads it, so you must:

1. **Register the attribute** into `BuiltInRegistries.ATTRIBUTE` (NeoForge: a `DeferredRegister`; Fabric: `Registry.register`), typically as a `RangedAttribute` with a sane min/max — the range **clamps** the final value, so a tight max can silently eat the bonus.
2. **Add it to the player's `AttributeSupplier`**, or `player.getAttribute(holder)` returns `null` and the bonus is silently dropped — no error, no log line. NeoForge: `EntityAttributeModificationEvent`. Fabric: `FabricDefaultAttributeRegistry` / a mixin on the player's attribute builder.
3. **Implement the effect yourself.** Read `player.getAttributeValue(MY_ATTRIBUTE)` wherever it should apply — a tick handler, a damage hook, a loot modifier — and act on it. `fishing_luck`'s value, for instance, is read in `FishingHookMixin` and added to the hook's luck when it's constructed.

Two rules worth knowing before picking any attribute, vanilla or custom:

- **`add_multiplied_base` is a no-op on a base-0 attribute.** Vanilla resolves an attribute as `base + Σ add_value`, then each `add_multiplied_base` adds `(base + Σ add_value) × amount`. If the base is `0` and nothing else contributes, the bonus multiplies to nothing. Use `add_value` for those — this is why `LUCK` and `OXYGEN_BONUS` need a flat operation, while `MOVEMENT_SPEED` (base `0.1`) is happy with a percentage.
- **Gear counts as `add_value`.** So `add_multiplied_base` on `ARMOR` (base 0) reads as "+x% of your *equipped* armor" — meaningful when geared, nothing when naked. That may be exactly what you want.

---

## 8. Effects beyond an attribute modifier

Everything so far produces one thing: a modifier of `level × incrementPerLevel` on a single attribute. When a skill needs to do *more* than that — touch a second attribute, run custom logic — pass a **`Handler`** as the fourth argument to `register`:

```java
registry.register(JUMP, jumpConfig, Attributes.JUMP_STRENGTH, MyPlugin::handleJump);

private static void handleJump(ServerPlayer player) {
    // called alongside the normal JUMP_STRENGTH bonus, whenever the skill is refreshed
    ...
}
```

`Handler` is a single method, `void onUpdateSkill(ServerPlayer player)`. It runs **in addition to** the automatic attribute modifier, not instead of it — the skill still buffs its named attribute as usual, and the handler does whatever extra work the skill needs.

The built-in `jump` skill uses one: raising `JUMP_STRENGTH` alone would let a high-level player jump higher than vanilla's safe-fall distance and take fall damage on landing, so `handleJump` also writes a matching bonus onto `SAFE_FALL_DISTANCE`. That's the pattern — a handler is where "this skill also needs to adjust X" lives.

Two things to know about *when* it fires:

- **The JSON route can't attach a handler** — `register(name, config, attribute)` installs a no-op. A skill that needs one must be registered from a plugin.
- **It runs on the same path as `updateAllSkillStatus`** — login, `/experienceskills` commands, and level changes through those. If your handler's output depends on the player's *current* level (rather than a constant), make sure it's recomputed from the live level each time, since the same handler is what keeps it in sync.

To drive per-tick or event-driven effects instead, read the attribute value directly from your own hook (as [§7](#7-using-your-own-attribute) describes) — the handler is specifically for keeping derived attribute state in lockstep with the skill's level.

The read-only side of the API lives in `ExperienceSkillsAPI` (`getLevel`, `getProgress`, `setLevel`, `givePoints`, `updateAllSkillStatus`, …) — use it to query or adjust a player's skills from your own code.

---

## 9. What you get automatically

Every registered skill, from either route, gets all of this. **You write none of it.**

| Derived thing | Where | Derived id / behaviour |
| --- | --- | --- |
| Config entry | `ModCommonConfig.init()` | path = `name` |
| XP data attachment | `ModDataAttachments.init()` | `name + "_experience"`, int |
| `ExperienceType` | `ExperienceType`'s static block | binds config + attachment + attribute |
| Entity type | `ModEntities.init()` | `name + "_experience_orb"` |
| Orb entity | — | the shared `AbstractExperienceOrb`, bound to your type |
| XP generation | `StatAwardEvent` | driven by `config.stat()` |
| The level bonus | `AbstractExperienceOrb.updateSkillStatus()` | `level × incrementPerLevel` as an `AttributeModifier` (id `<name>_skill_bonus`) using your `operation` |
| Entity renderer | `ExperienceSkillsClient.initRenderer()` | shared renderer, tinted by `rgbColor` |
| HUD bar | `HudRenderer` | shared bar, tinted by `rgbColor` |
| Death drop | `LivingDeathEvent` | `min(level × 7, 100)` points, skipped if disabled |
| Command `<type>` argument | `ExperienceTypeArgument` | suggestions from `ExperienceType.ALL` |
| HUD cycle slot | `ExperienceType.next()` | enabled types only |

### Naming convention (load-bearing)

Register `"jump"` and you get:

```
config path      jump
attachment id    jump_experience
entity id        jump_experience_orb
type name        jump                       (ExperienceType.byName)
modifier id      experienceskills:jump_skill_bonus
```

Deviate from the convention and the auto-wiring silently hands back `null`.

---

## 10. Runtime flow

```
JSON file  ─┐
            ├─►  REGISTERED_TYPES  ─►  config / attachment / ExperienceType
plugin  ────┘                          / entity type / renderer / HUD bar

   player earns a stat  (vanilla, or your own awardStat call)
              │
              ▼
   StatAwardEvent  →  does config.stat() match?  →  did the total cross a multiple
                                                     of xpAwardActionCount?
              │
              ▼
   AbstractExperienceOrb.award(level, pos, points, type)
              │   splits into vanilla-sized orbs, merges nearby, skipped if disabled
              ▼
   orb spawned  ──drifts to player──►  playerTouch  (ignored at maxLevel)
                                          │  attachment += value, level-up sound
                                          ▼
                                   updateSkillStatus()  →  modifier = level × incrementPerLevel
```

- **Server start** validates every skill's `stat` against the custom-stat registry (`ServerStartedEvent`), so a typo fails fast.
- **Login and command changes** call `updateAllSkillStatus(player)`, re-applying every skill's bonus, so they survive relogs.
- **Death** drops part of each skill's XP back as orbs; the XP attachment is `copyOnDeath = false`, so points are lost either way.
- **Levels/points** use vanilla's curve, reimplemented in `XpMath`.
- **Commands**: `/experienceskills {add|set|query} <targets> <type> <amount> [points|levels]`.
- **HUD**: one bar at a time, cycled with **R** (`ModKeyMappings.CYCLE_CONTEXTUAL_BAR`) via `ExperienceType.next()`, which skips disabled types. `ExperienceType.VANILLA` is a special entry with no attachment and no attribute: it means "show the vanilla XP bar" and has no orb.

---

## 11. Project layout

| Module | Purpose |
| --- | --- |
| `common/` | All gameplay logic + the API. Compiled against vanilla only — no loader APIs. |
| `fabric/` | Fabric entrypoints, mixins, platform service implementations. |
| `neoforge/` | NeoForge entrypoints, mixins, platform service implementations. |
| `gui/` | Decompiled vanilla GUI classes, kept as reference for the mixins. Not compiled. |

```
api/
├── ExperienceSkillsPlugin.java        annotation — NeoForge plugin discovery marker
├── IExperienceSkillsPlugin.java       the interface a plugin implements
├── type/
│   ├── IExperienceTypeRegistry.java   register(name, config, attribute[, handler]) + the Handler interface
│   ├── ExperienceTypeRegistry.java    @ApiStatus.Internal — holds REGISTERED_TYPES
│   └── ExperienceType.java            the runtime skill object
├── ExperienceSkillsAPI.java          read/adjust a player's skills from your own code
└── internal/
    └── InternalExperienceSkillsPlugin.java   the built-in skills + the JSON loader

stat/ModStats.java                     the mod's own custom stats
attribute/ModAttributes.java           the mod's own custom attributes (lure_speed, fishing_luck)
event/StatAwardEvent.java              stat normalisation + the generic award loop
event/ServerStartedEvent.java          validates every skill's stat exists
```

Loader-specific code is reached from `common` through the service interfaces in `common/.../platform/services/`, resolved via `ServiceLoader`.

---

## 12. Gotchas

- **Init order is fixed** in `ExperienceSkills.init()`: stats → `ExperienceTypeRegistry` (plugins + JSON files) → config → entities → attachments → serializers → argument types. `ExperienceType`'s static block reads *both* the config and the attachment keys, so it must not be loaded before those exist.
- **The config file wins.** `ExperienceType` prefers the on-disk `SkillsConfig` over the default you registered — including `stat`, `operation` and `maxLevel`. A player can retarget or disable any skill, yours included.
- **`rgbColor` is decimal in JSON**, hex only in Java.
- **`HudRenderer`** snapshots `rgbColor` at class-load, unlike the orb renderer which reads it live. A config colour change won't reach the HUD bar until restart.
- **A missing attribute is silent.** If the attribute isn't on the player's `AttributeSupplier`, `getAttribute()` returns `null` and the bonus is dropped with no error.
- **Nothing enforces the naming convention.** A mismatch between the type name and a derived id hands back `null` at lookup rather than failing at load.

---

## 13. Build & run

Standard MultiLoader-Template Gradle setup (Java 25).

```
./gradlew build                 # build all loaders
./gradlew :fabric:runClient     # Fabric dev client
./gradlew :neoforge:runClient   # NeoForge dev client
```

Mod metadata lives in `gradle.properties` and is expanded into `fabric.mod.json` and `neoforge.mods.toml` at build time. The mod depends on the external `coolerconfig` library for its config system.
