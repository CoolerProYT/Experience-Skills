# How the API Works

Experience Skills exposes a Java API so other mods can add skills, stats and attributes, and read or change a player's progress at runtime. This page explains the moving parts; the following pages walk through each task.

::: warning Experimental
The API may change between versions. Pin the version you build against and test after updating.
:::

## The Three Building Blocks

Everything in Experience Skills is built from three concepts:

- **Stat** — a counter that measures an action (blocks walked, mobs killed, damage taken). Stats drive skills.
- **Attribute** — the thing a skill buffs (movement speed, attack damage, a custom attribute you add).
- **Skill** (`ExperienceType`) — ties a stat to an attribute. As its driving stat advances, the skill awards experience; as it levels, it applies a scaling modifier to the attribute.

A skill is defined by a `SkillsConfig` (its tunable numbers — see [Skill Config](/config/skill-config)) plus the attribute it buffs and an optional handler for extra logic.

## What Registering a Skill Wires Up

When you register a skill, Experience Skills derives everything from its `name` automatically:

- a config entry (written to disk, player-overridable),
- the per-player XP storage attachment,
- the tinted experience-orb entity and its renderer,
- the HUD bar,
- the level-scaled attribute modifier,
- the on-death drop of the skill's XP,
- and command support (`/experienceskills … <name>`).

You provide the definition; the mod builds the rest.

## The Lifecycle

1. **Startup — plugin discovery.** Every registered plugin's `registerExperienceType` is called once, *before* the rest of the mod initialises, so all skills are known before anything is derived from them. This is also where you register any custom stats or attributes your skills need.
2. **Play — earning XP.** Whenever a player is awarded a stat, the mod checks each skill whose `stat` matches and grants experience for every `xpAwardActionCount` boundary crossed.
3. **Refresh — applying bonuses.** A skill's status is refreshed on login, on `/experienceskills` command changes, and each time the player levels it up. On refresh the level-scaled attribute modifier is re-applied and the skill's `Handler` (if any) runs.

## The Entry Points

| Type | Role |
|---|---|
| `IExperienceSkillsPlugin` | The interface your plugin implements; its `registerExperienceType` declares skills. |
| `@ExperienceSkillsPlugin` | Annotation that marks a plugin for automatic discovery on NeoForge. |
| `IExperienceTypeRegistry` | Handed to your plugin so it can `register(...)` skills. |
| `ExperienceSkillsAPI` | Static helper for reading/adjusting progress, awarding XP, and registering custom stats/attributes. |
| `StatEvents` | A listener hook fired for every stat award. |

## Getting the Dependency

Experience Skills is published on a Maven repository. Add the artifact for your loader:

```groovy
repositories {
    maven {
        name = "CoolerProMC Maven"
        url = "https://maven.coolerpromc.com/releases"
    }
}

dependencies {
    // Pick the artifact matching your project:

    // Common (multiloader projects)
    compileOnly "com.coolerpromc.experienceskills:experienceskills-common-${minecraft_version}:${experienceskills_version}"

    // Fabric
    compileOnly "com.coolerpromc.experienceskills:experienceskills-fabric-${minecraft_version}:${experienceskills_version}"

    // NeoForge
    compileOnly "com.coolerpromc.experienceskills:experienceskills-neoforge-${minecraft_version}:${experienceskills_version}"
}
```

```properties
# gradle.properties
minecraft_version=26.1.2
experienceskills_version=<latest>
```

Check the mod's CurseForge or Modrinth page for the latest `experienceskills_version`.

Next: [Plugin Setup](/developer/plugin-api).
