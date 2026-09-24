# Commands

Experience Skills adds a single command, `/experienceskills`, for adjusting a player's skills directly. It requires **game master** permission (operator level 2), so it's available to server operators and in singleplayer with cheats on.

## Syntax

```
/experienceskills add <targets> <skill> <amount> [points|levels]
/experienceskills set <targets> <skill> <amount> [points|levels]
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

## Notes

- After any change the command re-applies the skill's attribute bonus (and runs its handler), so buffs update immediately.
- Setting a skill above its configured `maxLevel` is possible via commands, but the skill will no longer award experience once it is at or past the cap.
- To hand out skill XP as an item instead, give an [Experience Bottle](/gameplay/experience-bottle):

```
/give @p experienceskills:experience_bottle[experienceskills:experience_type="jump"] 8
```
