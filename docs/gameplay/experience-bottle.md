# Experience Bottle

<div class="es-flow">
  <ItemSlot skill="strength" large />
  <p class="es-item-info">
    <strong>Experience Bottle</strong><br />
    <code>experienceskills:experience_bottle</code><br />
    <span class="es-muted">Uncommon · stacks to 64 · enchantment glint</span>
  </p>
</div>

A Bottle o' Enchanting for a single skill. Every skill has its own bottle, tinted in that skill's colour. Throw it and it shatters into experience orbs that only count toward that skill.

## All bottles

Every enabled skill gets a bottle in the **Experience Skills: Bottle** creative tab, in the same order as the HUD cycle. Hover a bottle to see its name.

<BottleGallery />

Skills added from [config files](/config/config-skills) or by other mods get a bottle too, coloured with their own `rgbColor`. Disabled skills are left out of the tab.

## Using it

- **Throw it** with right click. On impact it releases **3 to 11 XP** for its skill, the same amount a vanilla Bottle o' Enchanting gives.
- **Dispensers** fire it like any other bottle, so it works in automatic XP farms.

## Getting one

The bottle has no crafting recipe. Take it from the creative tab, or give one with a command. The skill is stored in the `experienceskills:experience_type` component:

```
/give @s experienceskills:experience_bottle[experienceskills:experience_type="strength"] 16
```

Swap `strength` for any skill id from the [Skills](/gameplay/skills-list) page. Always include the component: a bottle without one has no skill to award XP to.

::: tip Modpacks and servers
Because the skill is a normal item component, bottles work anywhere an item stack can be written: loot tables, villager trades, quest rewards and shop plugins.
:::
