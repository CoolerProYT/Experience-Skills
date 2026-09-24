# Stats

A **stat** is the counter that drives a skill. Every skill names exactly one stat in its config; each time that stat advances by `xpAwardActionCount`, the skill awards `xpPointToAward` experience points. Stats are the same statistics Minecraft already tracks, so progress is saved with your world, and you can see them on the in-game Statistics screen.

## Built-in stats

Experience Skills registers these custom stats, all under the `experienceskills` namespace. Several of them **pool multiple vanilla statistics** into one counter, so a skill can react to a whole category of actions at once.

<StatTable />

::: info Why pool at all?
Vanilla splits some actions across several statistics. Distance walked, sprinted and crouched are three separate counters, for example. Pooling them into a single `block_walked` stat means a skill can be driven by "moving on land" as a whole, rather than by only one gait.
:::

## Using vanilla stats directly

A skill's `stat` doesn't have to be one of the built-ins. **Any vanilla custom statistic** from the in-game Statistics screen works too. The built-in skills already use two:

- <ItemSlot id="minecraft:rabbit_foot" name="Jumps" /> `minecraft:jump` drives **Jump**
- <ItemSlot id="minecraft:shield" name="Damage Taken" /> `minecraft:damage_taken` drives **Vitality**, **Toughness** and **Block Reach**

::: warning Mind the units
Vanilla stores some statistics in awkward units, and `xpAwardActionCount` is measured in those raw units:

- **Distances** are counted in **centimetres** (100 = one block). The pooled `block_walked` and `water_walked` stats are already in whole blocks.
- **Damage** is counted in **tenths of a heart** (10 = one full heart).
- **Time** is counted in **ticks** (20 = one second).

So a skill driven by `minecraft:damage_dealt` with `xpAwardActionCount: 200` awards every 20 hearts of damage dealt.
:::

## What counts as a stat

Only **custom statistics** can drive a skill: the `minecraft:custom` category, shown on the *General* tab of the Statistics screen. Per-block and per-item statistics such as *times mined diamond ore* or *times used a bucket* cannot be referenced directly. To build a skill around those, pool the ones you care about into a **custom stat**. See [Adding a stat](/developer/custom-stats).

## How awards fire

Whenever a player is awarded a stat, Experience Skills checks every skill whose `stat` matches. If the stat crossed one or more `xpAwardActionCount` boundaries since the last award, it grants one award per boundary crossed, stopping early if the skill hits its level cap. Even a large single jump in a stat, such as one big fall that deals a lot of damage at once, grants all the experience it should.
