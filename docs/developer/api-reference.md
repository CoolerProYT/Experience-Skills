# API Reference

`ExperienceSkillsAPI` is the static entry point for reading and adjusting a player's skills, awarding experience, and registering the stats and attributes a skill needs.

Read-only queries take a `LivingEntity` and work on **either side** (the underlying data is synced to the client). Mutating helpers require a `ServerPlayer` and must be called on the **logical server**.

## Looking Up a Skill

```java
@Nullable ExperienceType getExperienceType(String name)
```

Returns the registered skill with that id, or `null` if none exists. Most other methods take the resulting `ExperienceType`.

```java
List<ExperienceType> getAllTypes()       // every skill except the `vanilla` HUD entry
List<ExperienceType> getEnabledTypes()   // every enabled skill except `vanilla`
```

## Reading Progress

| Method | Returns |
|---|---|
| `getLevel(LivingEntity, ExperienceType)` | Current level in the skill |
| `getProgress(LivingEntity, ExperienceType)` | Progress to next level, `0.0`–`1.0` |
| `getXpNeededForNextLevel(LivingEntity, ExperienceType)` | Points needed to reach the next level |
| `getPointsOfCurrentLevel(ServerPlayer, ExperienceType)` | Points accumulated within the current level |
| `reachedMaxLevel(ServerPlayer, ExperienceType)` | `true` if at the configured level cap |

## Changing Progress (Server-side)

Each of these re-applies the skill's bonus after changing it.

| Method | Effect |
|---|---|
| `setLevel(ServerPlayer, ExperienceType, int level)` | Set the level outright |
| `setPoints(ServerPlayer, ExperienceType, int points)` | Set points *within the current level* |
| `givePoints(ServerPlayer, ExperienceType, int points)` | Add points directly (no orbs) |
| `giveLevels(ServerPlayer, ExperienceType, int levels)` | Raise by a number of levels |
| `updateAllSkillStatus(ServerPlayer)` | Re-apply every skill's bonus and run each handler |

`updateAllSkillStatus` is called automatically on login and on command changes. Call it yourself only if you alter skill state through a path the API doesn't cover.

## Awarding Experience

```java
void award(ServerLevel level, Vec3 pos, int amount, ExperienceType experienceType)
```

Spawns experience orbs worth `amount` points of the skill at a position, splitting the amount into vanilla-sized orbs and merging into nearby ones.

::: tip Prefer stat-driven XP
Use `award` for experience that isn't tied to a stat. Where you can, drive a skill from a stat instead — that way progress persists and survives death, which loose orbs do not.
:::

## Registering Stats & Attributes

```java
RegistryHandler<Identifier, Identifier> registerCustomStat(String namespace, String name)
RegistryHandler<Attribute, Attribute>  registerAttribute(String namespace, String name, Attribute attribute)
```

Call these during your plugin's registration phase, before registries freeze. See [Adding a Stat](/developer/custom-stats) and [Adding an Attribute](/developer/custom-attributes).

## Reading Skill Metadata

An `ExperienceType` exposes its own definition:

| Method | Returns |
|---|---|
| `getConfig()` | The live `SkillsConfig` (on-disk values, which may differ from the registered default) |
| `getMaxLevel()` | The level cap |
| `color()` | Orb/HUD tint as a packed RGB integer |
| `operation()` | How the level bonus combines with the attribute |
| `isEnabled()` | Whether the skill is enabled in config |
| `getAttributeHolder()` | The attribute the bonus applies to (`null` for `vanilla`) |
| `next()` | The next enabled skill in the HUD cycle, wrapping around |

## Minimal Example

```java
ExperienceType strength = ExperienceSkillsAPI.getExperienceType("strength");
if (strength != null && player instanceof ServerPlayer sp) {
    int level = ExperienceSkillsAPI.getLevel(sp, strength);
    if (!ExperienceSkillsAPI.reachedMaxLevel(sp, strength)) {
        ExperienceSkillsAPI.givePoints(sp, strength, 25);
    }
}
```
