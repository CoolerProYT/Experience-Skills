# Attributes

An **attribute** is what a skill actually buffs. Each skill points at one attribute, and every level applies a modifier to it whose size is `incrementPerLevel × level`, combined using the skill's `operation`.

## Operations

The `operation` decides how a level bonus combines with the attribute's value. It matches Minecraft's own attribute modifier operations:

| Operation | Meaning | Example (`incrementPerLevel` `0.02`, level `10`) |
|---|---|---|
| `add_value` | Flat amount added to the attribute | +0.2 to the raw value |
| `add_multiplied_base` | Percentage of the attribute's **base** value | +20% of the base value |
| `add_multiplied_total` | Percentage of the **final** value, after other modifiers | +20% of everything else combined |

`incrementPerLevel` may be **negative** if you want a skill to reduce an attribute.

## Custom attributes

Vanilla Minecraft has no attribute for some effects, so Experience Skills adds its own, under the `experienceskills` namespace:

<AttributeTable />

These behave like any other attribute: the skill keeps a modifier on them in sync, and the mod implements what each one does. Other mods and commands such as `/attribute` can read and change them too.

## Vanilla attributes used

The remaining built-in skills buff standard Minecraft attributes: Movement Speed, Attack Damage, Max Health, Jump Strength, Oxygen Bonus, Armor Toughness, Submerged Mining Speed, Block Interaction Range and Entity Interaction Range.

::: tip For developers
When you register a **new** custom attribute with `ExperienceSkillsAPI.registerAttribute`, Experience Skills adds it to players and keeps the skill's modifier on it in sync. You still have to implement whatever the attribute actually does. See [Adding an attribute](/developer/custom-attributes).
:::
