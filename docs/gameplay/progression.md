# Progression

Beyond earning experience and levelling, Experience Skills has a few systems that make progress visible and give it more to do.

## The skills screen

Press **R** (*Open Skills Screen* in *Options → Controls*) to open a list of every enabled skill. Each row shows:

- the skill's **level** and level cap, with a progress bar toward the next level;
- the **bonus** it currently grants, e.g. `+12.0% Movement Speed`;
- how far its stat has advanced toward the **next experience award**, e.g. `47 / 100 Block Walked → +5 XP`;
- the **next milestone** it will unlock.

Hover a row to see everything that trains the skill, the bonus at max level, every milestone (ticked once unlocked) and how much of it you lose on death. Click a row to show that skill on the HUD bar; click it again to go back to the vanilla bar.

The panel at the bottom moves experience between your skills. Pick a source and a target, enter a number of points and press **Transfer**. See [transfer](#transferring-experience) below.

## Level-up toasts

Whenever one of your skills gains a level, a toast appears in the corner of the screen. Several levels gained at once update the same toast instead of stacking. Turn toasts off with `levelUpToasts` in the client config, `config/experienceskills-client.json5`.

## Milestones

A milestone is a perk a skill unlocks at a set level and keeps for as long as you stay at or above it. A milestone either grants a permanent potion effect or adds an extra attribute bonus. The built-in defaults are:

| Skill | Level | Perk |
| --- | --- | --- |
| Walking Speed | 50 | +0.5 Step Height (walk up full blocks) |
| Strength | 50 | +0.5 Attack Knockback |
| Vitality | 50 | +4 Max Health (two hearts) |
| Jump | 50 | −25% fall damage |
| Toughness | 50 | +0.1 Knockback Resistance |
| Swim Speed | 50 | Dolphin's Grace |
| Fishing Luck | 50 | +1 Luck |
| Oxygen | 100 | Water Breathing |
| Pickaxe Breaking Speed | 100 | Haste I |
| Fire Resistance | 100 | Fire Resistance |

Milestone effects are re-applied every few seconds, so drinking milk only clears them briefly. Server owners can add, change or remove milestones per skill in the [config](/config/skill-config#milestones).

## Advancements

Experience Skills adds its own advancement tab:

| Advancement | Requirement |
| --- | --- |
| Experience Skills | Earn your first skill experience |
| Apprentice | Reach level 10 in any skill |
| Journeyman | Reach level 25 in any skill |
| Expert | Reach level 50 in any skill |
| Master | Reach the maximum level in any skill |
| Perk Unlocked | Unlock a skill milestone |
| Jack of All Trades | Reach level 10 in every enabled skill |

Players who already had the levels before updating get the advancements the next time they log in.

## Dying

When you die, each skill with `dropOnDeath` enabled loses part of its experience. By default that is all of it, like vanilla. Up to `level × 7` points (capped at 100) drop as orbs you can pick back up. Server owners can soften this per skill with `deathPenalty` and `deathDropCap`. Nothing is lost when the `keepInventory` gamerule is on.

## Transferring experience

You can move experience from one skill to another, from the skills screen or with:

```
/experienceskills transfer <from> <to> <points>
```

Only part of the moved experience arrives: half by default. Server owners set the rate, or turn transfers off, in the [general settings](/config/skill-config#general-settings).

## Potion of Skill Insight

While under **Skill Insight**, every bit of skill experience you earn from your actions is doubled (×3 at level II). Brew it in a brewing stand:

| Ingredient | Into | Result |
| --- | --- | --- |
| Bottle o' Enchanting | Awkward Potion | Potion of Skill Insight (3:00) |
| Redstone | Potion of Skill Insight | Potion of Skill Insight (8:00) |
| Gunpowder | Potion of Skill Insight | Splash Potion of Skill Insight |
| Dragon's Breath | Splash Potion of Skill Insight | Lingering Potion of Skill Insight |

The boost applies to experience earned from stats. It does not multiply experience from bottles, commands or transfers.

## Shared experience

On servers that enable it, collecting skill experience also gives nearby players a share of it, while you keep the full amount. This is off by default; see `sharedXp` in the [general settings](/config/skill-config#general-settings).

## Scoreboards

When `scoreboard.enabled` is on, every player's skill levels are mirrored into dummy scoreboard objectives named `es_<skill>`, e.g. `es_strength`. Datapacks and command blocks can read them like any other score:

```
/execute if score @s es_strength matches 50.. run say I'm strong!
```

## JEI and Jade

With [JEI](https://modrinth.com/mod/jei) installed, each skill's Experience Bottle has an information page describing what trains the skill, its bonus at max level and its milestones. With [Jade](https://modrinth.com/mod/jade) installed, looking at a skill experience orb shows which skill it belongs to and how many points it's worth. Both are optional.
