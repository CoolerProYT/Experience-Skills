# Skill Config

Every skill — built-in or added by an addon — writes its default settings to the common config on first run. You can then override any field. The config is powered by [CoolerConfig](https://github.com/CoolerProYT/CoolerConfig) and stored as JSON5 at:

```
config/experienceskills/experienceskills.json5
```

The file is on the **common** side, so it applies to both client and server. On a dedicated server, edit it there.

::: info Addon skills
Skills added by other authors may or may not read these config values. If a built-in skill misbehaves, report it here; if an addon skill does, report it to that addon's author.
:::

## Fields

Each skill is a block of the following fields:

| Field                | Type    | Description                                                                                                                                                                                                                                                                          |
|----------------------|---------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `enabled`            | boolean | Whether the skill is active. A disabled skill awards no experience and its attribute bonus is removed.                                                                                                                                                                               |
| `incrementPerLevel`  | float   | How much the attribute changes per level, applied via `operation`. With `add_multiplied_base`, `0.02` means +2% of the base value per level. May be negative.                                                                                                                        |
| `xpAwardActionCount` | int     | How far the driving stat must advance before an award fires — e.g. `100` for 100 `block_walked`.                                                                                                                                                                                     |
| `xpPointToAward`     | int     | Experience points granted each time `xpAwardActionCount` is reached.                                                                                                                                                                                                                 |
| `rgbColor`           | int     | Colour of the skill's experience orbs, HUD bar and [Experience Bottle](/gameplay/experience-bottle), as a packed RGB integer (`0xFF0000` / `16711680` is red).                                                                                                                       |
| `operation`          | enum    | How the bonus combines with the attribute: `add_value`, `add_multiplied_base` or `add_multiplied_total`.                                                                                                                                                                             |
| `maxLevel`           | int     | The level cap. Once reached, the skill stops awarding experience.                                                                                                                                                                                                                    |
| `stat`               | string  | The stat that drives the skill (see [Stats](/gameplay/stats)). Built-ins include `experienceskills:block_walked`, `experienceskills:block_broken`, `experienceskills:entity_killed`; [vanilla custom stats](https://minecraft.wiki/w/Statistics) such as `minecraft:jump` also work. |
| `dropOnDeath`        | boolean | Whether experiences should drop like vanilla when player death.                                                                                                                                                                                                                      |

## Example

A slice of the config for the Strength skill:

```json5
{
  // ...
  "strength": {
    "xpAwardActionCount": 20,
    "rgbColor": 16733525,
    "maxLevel": 100,
    "stat": "experienceskills:entity_killed",
    "incrementPerLevel": 0.02,
    "xpPointToAward": 5,
    "operation": "add_multiplied_base",
    "enabled": true,
    "dropOnDeath": true
  },
}
```

## Common Tweaks

- **Slow a skill down / speed it up.** Raise or lower `xpAwardActionCount` (more actions per award) or `xpPointToAward` (points granted per award).
- **Make a buff stronger.** Raise `incrementPerLevel`, or switch a flat `add_value` skill to `add_multiplied_base` for percentage scaling.
- **Disable a skill entirely.** Set `enabled` to `false` — its bonus is removed and it stops awarding experience.
- **Re-colour the bar, orbs and bottle.** Change `rgbColor`. Decimal or `0x` hex both work.

::: warning Units
`xpAwardActionCount` is in the stat's raw units. Distances are centimetres (100 = a block) and damage is tenths of a heart (10 = one heart). See [Stats → Mind the units](/gameplay/stats#using-vanilla-stats-directly).
:::
