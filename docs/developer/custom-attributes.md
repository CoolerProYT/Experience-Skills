# Adding an Attribute

If your skill should buff something vanilla has no attribute for — like the built-in Swim Speed, Lure Speed and Fishing Luck skills — register a new attribute. Experience Skills will keep the skill's level modifier on it in sync for you, but **you must still add it to the player's `AttributeSupplier` and implement what it does**.

## 1. Register the Attribute

Register during your plugin's registration phase, before the registries freeze:

```java
RegistryHandler<Attribute, Attribute> myAttribute =
    ExperienceSkillsAPI.registerAttribute(
        "mymod",
        "harvest_yield",
        new RangedAttribute("attribute.name.harvest_yield", 0.0, 0.0, 1024)
            .setSyncable(true)     // sync to the client if the effect is client-visible
    );
```

`registerAttribute` returns a handle; use `myAttribute.holder()` when registering the skill.

::: tip Match the built-ins
Experience Skills' own custom attributes (`swim_speed`, `lure_speed`, `fishing_luck`) are `RangedAttribute`s created exactly like this. A `RangedAttribute` is almost always what you want.
:::

## 2. Buff It From a Skill

Pass the attribute's holder as the skill's attribute:

```java
registry.register(
    "harvesting",
    new SkillsConfig(true, 0.5f, 100, 5, 0xC0FF33,
        AttributeModifier.Operation.ADD_VALUE, 100,
        ModStats.BLOCK_BROKEN.id(), true),
    myAttribute.holder()          // ← the custom attribute
);
```

From here Experience Skills keeps a level-scaled modifier on the attribute in sync automatically.

## 3. Add It to the Player

Experience Skills only manages the *modifier*. For the attribute to exist on the player at all, you must add it to the player's `AttributeSupplier`, the same as any modded attribute. On each loader that means hooking the "entity attribute creation / modification" event and adding your attribute to the player type.

::: tip
If the attribute is registered using `ExperienceSkillsAPI.registerAttribute()` it will add to player and living entity automatically.
:::
## 4. Implement the Effect

Registering an attribute and putting a modifier on it does nothing on its own — an attribute is just a number. You have to read that number somewhere and apply it. For example, a "harvest yield" attribute might be read in a block-drop hook to roll for bonus drops, or a movement attribute might be read each tick to adjust the player's velocity. This is the same work any mod does for its own attributes.

## Checklist

1. **Register** the attribute during your plugin phase.
2. **Buff it** from a skill via `handle.holder()`.
3. **Add it** to the player's `AttributeSupplier`.
4. **Implement** whatever reads the attribute and applies the effect.
5. **Add a lang entry** for its display name (the translation key you passed to the attribute), e.g. `"attribute.name.harvest_yield": "Harvest Yield"`.

::: warning Modifier only
Experience Skills syncs a modifier on the attribute — it does **not** add the attribute to entities or implement its behaviour. Skip steps 3 or 4 and your skill will level up and hold a modifier on an attribute that either doesn't exist on the player or does nothing.
:::
