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

Every enabled skill gets a bottle in the **Experience Skills: Bottle** creative tab, in the same order as the skills screen. Hover a bottle to see its name.

<BottleGallery />

Skills added from [config files](/config/config-skills) or by other mods get a bottle too, coloured with their own `rgbColor`. Disabled skills are left out of the tab.

## Using it

- **Throw it** with right click. On impact it releases the **stored XP amount** for its skill (or **3 to 11 XP** for creative bottles without a stored amount), the same amount a vanilla Bottle o' Enchanting gives.
- **Dispensers** fire it like any other bottle, so it works in automatic XP farms.

## Getting one

### Survival: store your own XP

Open the skills screen, choose a skill in the bottom selector, and click **Bottle XP**. Each click consumes **one glass bottle from your inventory** and withdraws **100 XP** from that skill by default. You must have the full amount; no XP or bottles are consumed if you cannot fill one. The skill level, bonuses and milestones update immediately.

The resulting bottle stores the exact amount withdrawn, shown in its tooltip. Throw it to release that amount as skill orbs. Store bottles somewhere safe or give them to another player. If your inventory is full, the filled bottle drops at your feet.

You can also use `/experienceskills bottle <skill>` without operator permissions. Server owners can disable extraction or change the amount in the [general settings](/config/skill-config#general-settings).

### Structure chests

Dungeon, abandoned mineshaft, desert pyramid, jungle temple, stronghold library and ancient city chests have a **10% chance** to contain one bottle for a random enabled skill, including custom skills. It stores **100 XP** by default. The chance and amount are configurable; already generated chest contents are unchanged, and existing loot is never replaced.

### Creative and commands

Take bottles from the creative tab, or give one with a command. The skill is stored in the `experienceskills:experience_type` component:

```
/give @s experienceskills:experience_bottle[experienceskills:experience_type="strength"] 16
```

Swap `strength` for any skill id from the [Skills](/gameplay/skills-list) page. Always include the component: a bottle without one has no skill to award XP to.

Add `experienceskills:stored_xp=100` alongside the skill component to give a bottle with an exact amount. Without it, bottles retain the random 3–11 XP behavior.

Bottled XP is unaffected by Skill Insight and does not grant nearby shared XP. This prevents repeatedly storing and releasing the same XP to create extra experience. Thrown orbs still follow the usual skill level cap and can be lost if left uncollected.

::: tip Modpacks and servers
Because the skill is a normal item component, bottles work anywhere an item stack can be written: loot tables, villager trades, quest rewards and shop plugins.
:::
