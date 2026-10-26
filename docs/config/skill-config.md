# Skill Config

Every skill, built-in or added by an addon, writes its default settings to the common config on first run. You can then override any field. The config is powered by [CoolerConfig](https://github.com/CoolerProYT/CoolerConfig) and stored as JSON5 at:

```
config/experienceskills-common.json5
```

The file is on the **common** side, so it applies to both client and server. On a dedicated server, edit it there; players' clients receive the server's values when they join, so levels and bonuses display the way the server computes them.

::: info Addon skills
Skills added by other authors may or may not read these config values. If a built-in skill misbehaves, report it here; if an addon skill does, report it to that addon's author.
:::

## Fields

Each skill is a block of the following fields. The ones from `additionalStats` down are optional: a config written by an older version gets their defaults, and they are added to the file the next time it is saved.

| Field                | Type    | Description |
|----------------------|---------|-------------|
| `enabled`            | boolean | Whether the skill is active. A disabled skill awards no experience and its attribute bonus and milestones are removed. |
| `incrementPerLevel`  | float   | How much the attribute changes per level, applied via `operation`. With `add_multiplied_base`, `0.02` means +2% of the base value per level. May be negative. |
| `xpAwardActionCount` | int     | How far the driving stat must advance before an award fires, e.g. `100` for 100 `block_walked`. |
| `xpPointToAward`     | int     | Experience points granted each time `xpAwardActionCount` is reached. |
| `rgbColor`           | int     | Colour of the skill's experience orbs, HUD bar and [Experience Bottle](/gameplay/experience-bottle), as a packed RGB integer (`0xFF0000` / `16711680` is red). |
| `operation`          | enum    | How the bonus combines with the attribute: `add_value`, `add_multiplied_base` or `add_multiplied_total`. |
| `maxLevel`           | int     | The level cap. Once reached, the skill stops awarding experience. |
| `stat`               | string  | The stat that drives the skill (see [Stats](/gameplay/stats)). Built-ins include `experienceskills:block_walked`, `experienceskills:block_broken`, `experienceskills:entity_killed`; [vanilla custom stats](https://minecraft.wiki/w/Statistics) such as `minecraft:jump` also work. |
| `dropOnDeath`        | boolean | Whether dying costs experience in this skill. Ignored while the `keepInventory` gamerule is on. |
| `additionalStats`    | list    | Extra stats that also train the skill, each with a `weight`. Default: none. See [below](#training-from-several-stats). |
| `curve`              | object  | How many points each level costs: `{ type, multiplier }`. Default: `{ type: "vanilla", multiplier: 1.0 }`. See [below](#level-curves). |
| `scaling`            | enum    | How the bonus grows with level: `linear`, `sqrt` or `logarithmic`. Default: `linear`. See [below](#bonus-scaling). |
| `deathPenalty`       | float   | With `dropOnDeath`, the fraction of points lost on death, from `0.0` to `1.0`. Default: `1.0` (lose everything, like vanilla). |
| `deathDropCap`       | int     | With `dropOnDeath`, the most points dropped as orbs you can pick back up. Default: `100`. |
| `milestones`         | list    | Perks unlocked at specific levels. See [below](#milestones). |

## Example

A slice of the config for the Swim Speed skill:

```json5
{
  // ...
  "swim_speed": {
    "enabled": true,
    "incrementPerLevel": 0.05,
    "xpAwardActionCount": 100,
    "xpPointToAward": 5,
    "rgbColor": 5800104,
    "operation": "add_value",
    "maxLevel": 100,
    "stat": "experienceskills:water_walked",
    "dropOnDeath": true,
    "additionalStats": [],
    "curve": { "type": "vanilla", "multiplier": 1.0 },
    "scaling": "linear",
    "deathPenalty": 1.0,
    "deathDropCap": 100,
    "milestones": [
      { "level": 50, "effect": "minecraft:dolphins_grace" }
    ]
  },
}
```

## Training from several stats

`additionalStats` lets a skill be trained by more than one stat. Each entry counts its stat `weight` times, and the skill's progress is its main `stat` plus the weighted sum of the others. An award fires each time that total crosses a multiple of `xpAwardActionCount`.

```json5
// Strength: kills count fully, and every heart of damage dealt counts as half a kill.
"stat": "experienceskills:entity_killed",
"xpAwardActionCount": 20,
"additionalStats": [
  { "stat": "minecraft:damage_dealt", "weight": 0.05 }
]
```

Weights are applied to raw stat units, so mind them: damage is in tenths of a heart and distances are in centimetres.

::: warning Adding a stat to an existing world
Progress is computed from the player's lifetime statistics. Adding a stat that a player already has a large total in awards all of that experience at once the next time the stat changes.
:::

## Level curves

| `type` | Points for each level | Feel |
| --- | --- | --- |
| `vanilla` | Same as vanilla XP | Cheap early levels, steeper after 15 and again after 30. |
| `linear` | 7 + 2 × level | Steady growth with no jumps. |
| `flat` | 10 | Every level costs the same. |
| `exponential` | 7 × 1.08^level | Each level costs 8% more than the last; very slow at high levels. |

`multiplier` scales every level's cost, so `2.0` makes the skill twice as slow to level. Points are stored, not levels, so changing the curve on an existing world re-computes everyone's levels from their points.

## Bonus scaling

`scaling` decides how the bonus is spread across the levels. All three reach the same bonus at `maxLevel` (`incrementPerLevel × maxLevel`); they differ in how quickly they get there.

| `scaling` | Bonus at level L | Effect |
| --- | --- | --- |
| `linear` | `increment × L` | Every level adds the same amount. |
| `sqrt` | `increment × √(L × maxLevel)` | Early levels add more; a quarter of the way up gives half the bonus. |
| `logarithmic` | `increment × maxLevel × ln(1+L) / ln(1+maxLevel)` | Most of the bonus comes in the first levels. |

## Milestones

A milestone grants a perk once the skill reaches `level`, and keeps it while the player stays at or above it. Each milestone sets **either** an effect **or** an attribute:

```json5
"milestones": [
  // A permanent potion effect; amplifier 0 is level I.
  { "level": 50, "effect": "minecraft:dolphins_grace", "amplifier": 0 },
  // An extra attribute modifier, with the same operations as the skill's own bonus.
  { "level": 75, "attribute": "minecraft:step_height", "amount": 0.5, "operation": "add_value" }
]
```

Effects and attributes from other mods work too; an id that doesn't exist is logged once and ignored. The [Progression](/gameplay/progression#milestones) page lists the built-in defaults.

::: tip Existing configs
Milestone defaults are only written for skills whose config entry doesn't exist yet. To get the defaults on an existing world, delete that skill's block from the config and restart.
:::

## General settings

Settings that apply to every skill live in `config/experienceskills-general.json5`:

| Setting | Default | Description |
| --- | --- | --- |
| `sharedXp.fraction` | `0.0` | When a player collects skill experience, nearby players also get this fraction of it, on average (a share worth part of a point is sometimes rounded up, sometimes down). `0` turns sharing off. |
| `sharedXp.radius` | `16.0` | How close, in blocks, another player must be to get a share. |
| `transfer.enabled` | `true` | Whether players can move experience between their skills. |
| `transfer.efficiency` | `0.5` | The fraction of transferred experience that arrives; the rest is lost. |
| `milestones.stepUp` | `true` | Allow step-height milestones such as Walking Speed level 50. Set to `false` and restart the server/world to disable automatic one-block step-up. Sneaking always ignores skill milestone step-height bonuses. |
| `scoreboard.enabled` | `false` | Mirror skill levels into `es_<skill>` scoreboard objectives. |

Client-only settings are in `config/experienceskills-client.json5`:

| Setting | Default | Description |
| --- | --- | --- |
| `levelUpToasts` | `true` | Show a toast when a skill levels up. |
| `orbPickupSounds` | `true` | Play skill experience orb collection sounds. Set to `false` to mute them for your client, including nearby players' skill orb pickups. Vanilla XP pickup and level-up sounds are unaffected. |

## Common Tweaks

- **Slow a skill down / speed it up.** Raise or lower `xpAwardActionCount` (more actions per award) or `xpPointToAward` (points granted per award), or change `curve.multiplier`.
- **Make a buff stronger.** Raise `incrementPerLevel`, or switch a flat `add_value` skill to `add_multiplied_base` for percentage scaling.
- **Front-load a skill.** Set `scaling` to `sqrt` or `logarithmic` so early levels feel rewarding without raising the cap.
- **Make death gentler.** Lower `deathPenalty`, e.g. `0.25` to lose a quarter of a skill's experience.
- **Disable a skill entirely.** Set `enabled` to `false`; its bonus is removed and it stops awarding experience.
- **Re-colour the bar, orbs and bottle.** Change `rgbColor`. Decimal or `0x` hex both work.

::: warning Units
`xpAwardActionCount` is in the stat's raw units. Distances are centimetres (100 = a block) and damage is tenths of a heart (10 = one heart). See [Stats → Mind the units](/gameplay/stats#using-vanilla-stats-directly).
:::
