# Experience Skills

> Level up your skills by collecting different experiences.

A multi-loader (Fabric + NeoForge) Minecraft mod for **MC 26.1.2 / Java 25**. It adds parallel, per-skill experience tracks alongside vanilla XP. Each skill has its own coloured experience orb entity, its own XP pool stored on the player, its own HUD bar, and its own gameplay bonus that scales with the skill's level.

Skills are now added through a **plugin API**. A plugin declares a skill once, and the mod auto-derives the config entry, the data attachment, the `ExperienceType`, the entity type, the entity renderer, the HUD bar, and the death drop from that single declaration.

Two skills exist today:

| Skill | How it's registered | Bonus |
| --- | --- | --- |
| `speed` | Hard-coded (the original, pre-API path) | `MOVEMENT_SPEED` |
| `mining_speed` | Via the API, by `InternalExperienceSkillsPlugin` | `BLOCK_BREAK_SPEED` |

`mining_speed` is a live test of the API and is marked `// TODO: Temporarily code to test API` throughout. It doubles as the reference implementation for what an addon mod writes.

---

## 1. Project layout

| Module | Purpose |
| --- | --- |
| `common/` | All gameplay logic + the API. Compiled against vanilla only — no loader APIs. |
| `fabric/` | Fabric entrypoints, mixins, platform service implementations. |
| `neoforge/` | NeoForge entrypoints, mixins, platform service implementations. |
| `gui/` | Decompiled vanilla GUI classes, kept as reference for the mixins. Not compiled. |

Anything loader-specific is reached from `common` through the service interfaces in `common/.../platform/services/`, resolved via `ServiceLoader` in `Services` (common) and `ServicesClient` (client). The `META-INF/services/` files already exist in both loaders — you never touch them when adding a skill.

The API lives in `common/.../api/`:

```
api/
├── ExperienceSkillsPlugin.java        annotation — NeoForge plugin discovery marker
├── IExperienceSkillsPlugin.java       the interface a plugin implements
├── type/
│   ├── IExperienceTypeRegistry.java   what a plugin is handed: register(...)
│   ├── ExperienceTypeRegistry.java    @ApiStatus.Internal — holds REGISTERED_TYPES
│   └── ExperienceType.java            the runtime skill object
└── internal/                          the built-in mining_speed test plugin
    ├── InternalExperienceSkillsPlugin.java
    └── entity/MiningSpeedExperienceOrb.java
```

---

## 2. Registering a skill through the API

### Step 1 — write the plugin class

```java
@ExperienceSkillsPlugin                                  // needed for NeoForge discovery
public final class MyPlugin implements IExperienceSkillsPlugin {
    @Override
    public void registerExperienceType(IExperienceTypeRegistry registry) {
        SkillsConfig config = new SkillsConfig(true, 0.02f, 10, 10, 0x00FF00);
        registry.register("mining_speed", config,
                          MiningSpeedExperienceOrb::new,                        // EntityType.EntityFactory
                          new ExperienceOrbFactory.MiningSpeedExperienceOrbFactory());
    }
}
```

`IExperienceTypeRegistry.register` takes four things:

| Argument | Meaning |
| --- | --- |
| `name` | The skill id, e.g. `"mining_speed"`. **Every derived id is built from this** — see the naming table below. |
| `config` | A `SkillsConfig` used as the *default* values written into the config file. |
| `factory` | `EntityType.EntityFactory` — the `(EntityType, Level)` constructor of your orb, used to build the entity type. |
| `orbFactory` | An `ExperienceOrbFactory` — the `(Level, pos, roughDirection, amount)` constructor, used when awarding/dropping orbs. |

Everything ends up in `ExperienceTypeRegistry.REGISTERED_TYPES`, a `Map<String, RegistryHolder>` that the rest of the mod iterates.

### Step 2 — declare the plugin to the loader

- **Fabric** — add a custom entrypoint in `fabric.mod.json`:
  ```json
  "entrypoints": {
      "experience_skills_plugin": [
          "com.coolerpromc.experienceskills.api.internal.InternalExperienceSkillsPlugin"
      ]
  }
  ```
  `ExperienceSkillsFabric` collects these with `FabricLoader.getEntrypoints("experience_skills_plugin", ...)`.
- **NeoForge** — nothing to declare. `ExperienceSkillsNeoForge` scans all mod files' `ModFileScanData` for the `@ExperienceSkillsPlugin` annotation and reflectively instantiates each class that implements `IExperienceSkillsPlugin` (so the class needs a public no-arg constructor).

Both loaders fill their `PLUGINS` list **before** calling `ExperienceSkills.init()`, and `Services.PLATFORM.getPlugins()` exposes it. `ExperienceTypeRegistry.init()` is the first thing `ExperienceSkills.init()` runs, so `REGISTERED_TYPES` is fully populated before anything else initialises.

### Step 3 — write the orb entity

This is the one class you must still write yourself, because the *bonus* is inherently skill-specific.

```java
public class MiningSpeedExperienceOrb extends AbstractExperienceOrb {
    public MiningSpeedExperienceOrb(Level level, Vec3 pos, Vec3 roughly, int value) {
        super(ModEntities.byName("mining_speed").get(), level, pos, roughly, value);
    }

    public MiningSpeedExperienceOrb(EntityType<? extends AbstractExperienceOrb> type, Level level) {
        super(type, level);
    }

    @Override
    public ExperienceType getExperienceType() {
        return ExperienceType.byName("mining_speed").orElse(null);
    }

    @Override
    public void updateSkillStatus(LivingEntity entity, AttachmentKey<Integer> key) {
        int level = getLevel(entity, key);
        float bonus = level * ModCommonConfig.getConfigByPath("mining_speed").get().incrementPerLevel();

        AttributeInstance attr = entity.getAttribute(Attributes.BLOCK_BREAK_SPEED);
        if (attr != null) {
            Identifier id = Constants.id("mining_speed_skill_bonus");
            attr.removeModifier(id);
            attr.addPermanentModifier(new AttributeModifier(id, bonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            if (!canAward()) attr.removeModifier(id);   // skill disabled in config → undo the bonus
        }
    }
}
```

Note how it looks itself up by name (`ModEntities.byName`, `ExperienceType.byName`, `ModCommonConfig.getConfigByPath`) rather than holding static references — that's what lets a skill be fully data-driven off its registered name.

`AbstractExperienceOrb` gives you movement, gravity, water/lava behaviour, orb merging, the 6000-tick despawn, damage, save/load, pickup, the icon-tier lookup, and all the XP math. You only supply `getExperienceType()` and `updateSkillStatus()`.

**`updateSkillStatus` must be idempotent.** It runs on every orb pickup, on every login, and after every `/experienceskills` command — hence the remove-then-add-modifier pattern, and the removal when `!canAward()`.

### Step 4 — write the orb factory

An implementation of `ExperienceOrbFactory` (a one-line record; the built-in ones live in `entity/helper/ExperienceOrbFactory.java`, an addon would put its own anywhere):

```java
record MiningSpeedExperienceOrbFactory() implements ExperienceOrbFactory {
    @Override
    public AbstractExperienceOrb createExperienceOrb(ServerLevel level, Vec3 pos, Vec3 roughDirection, int amount) {
        return new MiningSpeedExperienceOrb(level, pos, roughDirection, amount);
    }
}
```

It exists because `AbstractExperienceOrb.award(...)` may split one award into several orbs, and because `LivingDeathEvent` needs to spawn your orb type generically.

### Step 5 — decide where the XP is generated

**This is the only part the API does not do for you** (see [Known gaps](#5-known-gaps)). Nothing spawns `mining_speed` orbs yet. To award XP you need a server-side hook that calls:

```java
AbstractExperienceOrb.award(serverLevel, player.position(),
                            config.xpPointToAward(),
                            new MiningSpeedExperienceOrbFactory());
```

The reference pattern is `StatAwardEvent.onStatsAward` (see the built-in Speed skill): watch a vanilla stat, and each time it crosses a multiple of `xpAwardActionCount`, award `xpPointToAward` points. A companion attachment (`LAST_BLOCK_COUNT`) debounces it so the same stat value isn't awarded twice.

Existing hooks, and how each loader reaches them:

| Hook | Common class | Fabric | NeoForge |
| --- | --- | --- | --- |
| Stat awarded (distance walked, blocks mined, …) | `event/StatAwardEvent` | `mixin/StatsCounterMixin` on `StatsCounter#setValue` | native `StatAwardEvent` |
| Player death (drop XP back as orbs) | `event/LivingDeathEvent` | `ServerLivingEntityEvents.AFTER_DEATH` | native `LivingDeathEvent` |
| Player login (re-apply bonuses) | `event/PlayerJoinEvent` | `ServerPlayerEvents.JOIN` | `PlayerEvent.PlayerLoggedInEvent` |
| Command registration | `command/ModCommands` | `CommandRegistrationCallback` | `RegisterCommandsEvent` |

`StatAwardEvent` is currently hard-coded to Speed/`WALK_ONE_CM`. Adding a stat-driven skill means extending it with another branch (or, for an external addon, subscribing to the equivalent event in your own mod).

---

## 3. What gets auto-registered from `REGISTERED_TYPES`

Once `registry.register(name, …)` has been called, each of these iterates `ExperienceTypeRegistry.REGISTERED_TYPES` and wires the skill up. **You write none of this.**

| Derived thing | Where | Id it derives |
| --- | --- | --- |
| Config entry | `ModCommonConfig.init()` | `name` (comment auto-capitalised) |
| Data attachment (XP total) | `ModDataAttachments.init()` | `name + "_experience"` |
| `ExperienceType` constant | `ExperienceType`'s static block | `name` |
| Entity type | `ModEntities.init()` | `name + "_experience_orb"` (MISC, 0.5×0.5, no loot table, tracking 6/20) |
| Entity renderer | `ExperienceSkillsClient.initRenderer()` | reuses `AbstractExperienceOrbRenderer` |
| HUD bar renderer | `HudRenderer`'s static block | reuses `AbstractExperienceBarRenderer` |
| Death drop | `LivingDeathEvent` | `min(level * 7, 100)` points via your `orbFactory` |
| Command `<type>` argument | `ExperienceTypeArgument` | suggestions from `ExperienceType.ALL` |
| HUD cycle slot | `ExperienceType.next()` | enabled types only |

### Naming convention (this is load-bearing)

Register `"mining_speed"` and you get:

```
config path        mining_speed
attachment id      mining_speed_experience          (ModDataAttachments.byId appends the suffix)
entity id          mining_speed_experience_orb      (ModEntities.byName appends the suffix)
type name          mining_speed                     (ExperienceType.byName)
```

The lookups (`ModEntities.byName`, `ModDataAttachments.byId`) tolerate being passed either the bare name or the suffixed one. Deviate from the convention and the auto-wiring silently hands you `null`.

### Config

`SkillsConfig` is a record — `enabled`, `incrementPerLevel`, `xpAwardActionCount`, `xpPointToAward`, `rgbColor` — the same shape for every skill, JSON5, common-side, via the external `coolerconfig` library.

The `SkillsConfig` you pass to `register(...)` is only the **default**. At runtime, read the live value with `ModCommonConfig.getConfigByPath(name)`, which returns the user's edited config and falls back to the plugin's default if the path is missing.

`rgbColor` drives both the orb tint and the HUD bar colour. `enabled: false` hides the type from commands, blocks orb spawning (`canAward()`), removes it from the HUD cycle, and makes `updateSkillStatus` strip its bonus.

### Renderer and HUD

`AbstractExperienceOrbRenderer` reuses the vanilla `experience_orb.png` atlas, picks the icon tier from the orb's value, and tints it with `getExperienceType().color()` — straight from config. You only need your own renderer subclass for different geometry or a texture.

The HUD shows one bar at a time: `HudRenderer.currentType`, advanced by the cycle key (default **R**, `ModKeyMappings.CYCLE_CONTEXTUAL_BAR`, handled in `ClientTickEvent`) via `ExperienceType.next()`, which skips disabled types. Each loader's `GuiMixin` suppresses the *vanilla* bar and level number unless `currentType == VANILLA`; a single GUI layer (`skills_experience_bar`) draws whichever skill bar is selected, looping over every registered `AbstractExperienceBarRenderer`. Bars reuse `assets/experienceskills/textures/gui/sprites/hud/experience_bar_{background,progress}.png`, tinted — no new textures per skill.

---

## 4. Runtime flow

```
plugin.registerExperienceType()  →  REGISTERED_TYPES  →  config / attachment / ExperienceType
                                                          / entity type / renderer / HUD bar
                     ┌──────────────────────────────────────────────────────────┐
  behaviour hook ───►│ AbstractExperienceOrb.award(level, pos, points, factory)  │
  (StatAwardEvent)   └──────────────────────────────────────────────────────────┘
                                          │  splits into vanilla-sized orbs, merges nearby,
                                          │  skips entirely if !canAward()
                                          ▼
                              XxxExperienceOrb spawned  ──drifts to player──►  playerTouch
                                                                                   │
                          addExperience(): attachment += value, level-up sound      │
                                                                                   ▼
                                                                          updateSkillStatus()
                                                                          (attribute modifier)
```

- **Login / command changes** call `AbstractExperienceOrb.updateAllSkillStatus(player)` (via `PlayerJoinEvent`), which instantiates a throwaway orb of *every* registered type and re-applies its `updateSkillStatus` — so bonuses survive relogs.
- **Death** drops part of each skill's XP back as orbs (`LivingDeathEvent`), and the XP attachment is `copyOnDeath = false`, so the points are lost regardless.
- **Levels/points** use vanilla's curve, reimplemented in `XpMath` (`getLevel`, `getXpNeededForNextLevel`, `getTotalForLevel`, `getProgress`).
- **Commands**: `/experienceskills {add|set|query} <targets> <type> <amount> [points|levels]`, permission level gamemasters. `<type>` is `ExperienceTypeArgument`; its suggestions come from `ExperienceType.ALL` minus disabled types minus `vanilla`, so a new skill appears with no extra work.

`ExperienceType.VANILLA` is a special entry with a `null` config and `null` key: it means "show the vanilla XP bar" in the HUD cycle and has no orb. Every real skill must have a non-null key.

---

## 5. Known gaps

- **No API hook for XP generation.** `IExperienceTypeRegistry` registers the *type*, not the *behaviour*. `StatAwardEvent` is still hard-coded to Speed, and `mining_speed` currently has no way to earn XP. An addon must currently hook its own loader events.
- The Speed skill still uses the **pre-API hard-coded path** (`ModCommonConfig.SPEED`, `ModDataAttachments.SPEED_EXPERIENCE`, `ExperienceType.SPEED`, `ModEntities.SPEED_EXPERIENCE_ORB`, plus its own branch in `LivingDeathEvent`), in parallel with the API path.
- The API wiring is marked `// TODO: Temporarily code to test API` in `ExperienceType`, `ModEntities`, `ModDataAttachments`, `ModCommonConfig`, `ExperienceSkillsClient`, `HudRenderer`, and `LivingDeathEvent`.
- Addons can't register **extra attachments** (like Speed's `LAST_BLOCK_COUNT` debounce counter) through the API — only the one XP attachment per skill.

## 6. Gotchas

- **Init order is fixed** in `ExperienceSkills.init()`: `ExperienceTypeRegistry` → config → entities → attachments → serializers → argument types. `ExperienceType`'s static block reads *both* `ModCommonConfig.getConfigByPath` and `ModDataAttachments.INT_KEYS`, so it must not be class-loaded before config and attachments have initialised.
- **`ExperienceType.BY_NAME`** is a static field built from `ALL` at class-init. Hard-coded constants must be declared above it; API types are fine because the static block runs first.
- **`HudRenderer`'s static block** snapshots `rgbColor` at class-load, unlike the orb renderer which reads the colour live. A colour change won't reach the HUD bar until restart.
- **`updateAllSkillStatus`** dereferences `orb.getExperienceType().getKey()` — a registered orb type whose `ExperienceType` is missing or has a `null` key NPEs on login.
- **NeoForge plugin classes** are instantiated reflectively via `getDeclaredConstructor().newInstance()`, so they need an accessible no-arg constructor.

---

## 7. Build & run

Standard MultiLoader-Template Gradle setup (Java 25).

```
./gradlew build                 # build all loaders
./gradlew :fabric:runClient     # Fabric dev client
./gradlew :neoforge:runClient   # NeoForge dev client
```

Mod metadata lives in `gradle.properties` (mod id, version, MC/loader versions) and is expanded into `fabric.mod.json` and `neoforge.mods.toml` at build time. The mod depends on the external `coolerconfig` library for its config system.
