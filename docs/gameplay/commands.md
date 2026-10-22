# Commands

Experience Skills adds a single command, `/experienceskills`. Its `add`, `set` and `query` subcommands adjust a player's skills directly and require **game master** permission (operator level 2), so they're available to server operators and in singleplayer with cheats on. The `transfer` subcommand is open to every player.

## Syntax

```
/experienceskills add <targets> <skill> <amount> [points|levels]
/experienceskills set <targets> <skill> <amount> [points|levels]
/experienceskills query <target> <skill> points|levels
/experienceskills transfer <from> <to> <points>
```

- `<targets>` — one or more players (supports selectors like `@a`, `@p`).
- `<skill>` — a registered skill id, e.g. `walking_speed`, `strength`, `fishing_luck`.
- `<amount>` — an integer. May be negative for `add`.
- `points` / `levels` — what the amount refers to. If omitted, `add` defaults to **points**.

## add

Adds to a player's current total.

```
# Give everyone 50 Strength points
/experienceskills add @a strength 50 points

# Give the nearest player 3 Breaking Speed levels
/experienceskills add @p breaking_speed 3 levels
```

`add ... points` grants the points directly (no orbs) and re-applies the skill's bonus. `add ... levels` raises the skill by that many levels.

## set

Sets a value outright.

```
# Set your Jump skill to level 10
/experienceskills set @s jump 10 levels

# Set your Vitality points within the current level to 0
/experienceskills set @s vitality 0 points
```

::: warning set ... points bounds
`set ... points` sets the points *within the current level*. The amount must be less than the points needed for the next level, otherwise the command fails with an error. To move whole levels, use `set ... levels`.
:::

## transfer

Moves experience from one of **your own** skills into another. Only part of it arrives: by default half, set by `transfer.efficiency` in the [general config](/config/skill-config#general-settings). Server owners can turn transfers off with `transfer.enabled`.

```
# Move 200 points out of Fishing Speed; 100 arrive in Strength
/experienceskills transfer fishing_speed strength 200
```

If you have fewer points than you asked to move, everything you have is moved. The [skills screen](/gameplay/progression#the-skills-screen) has a panel that runs this command for you.

## Notes

- After any change the command re-applies the skill's attribute bonus (and runs its handler), so buffs update immediately.
- Setting a skill above its configured `maxLevel` is possible via commands, but the skill will no longer award experience once it is at or past the cap.
- To hand out skill XP as an item instead, give an [Experience Bottle](/gameplay/experience-bottle):

```
/give @p experienceskills:experience_bottle[experienceskills:experience_type="jump"] 8
```
