# Config-File Skills

You can add a **brand-new skill without writing any Java**. Experience Skills scans a folder of JSON files at startup and registers a skill for each one, exactly as if a plugin had declared it. This is the easiest way for modpack makers to add skills around attributes that already exist.

## Location

Drop `.json` files into:

```
config/experienceskills/skills/
```

The folder is created for you on first launch. Every `.json` file in it (searched recursively) becomes one skill.

## File Format

Each file has three top-level keys:

| Key | Description |
|---|---|
| `name` | The skill's unique id. Everything derived from the skill is keyed off this. |
| `attribute` | The registry id of the attribute the skill buffs. |
| `defaultConfig` | The skill's settings — the same fields as a [Skill Config](/config/skill-config) block. |

```json
{
  "name": "luck",
  "attribute": "minecraft:luck",
  "defaultConfig": {
    "enabled": true,
    "incrementPerLevel": 0.1,
    "xpAwardActionCount": 100,
    "xpPointToAward": 5,
    "rgbColor": 43520,
    "operation": "add_value",
    "maxLevel": 50,
    "stat": "minecraft:animals_bred",
    "dropOnDeath": true
  }
}
```

That single file adds a **Luck** skill that trains from animals bred and adds flat Luck as it levels. It gets its own orbs, HUD bar and <ItemSlot :color="43520" name="Luck Experience Bottle" /> [Experience Bottle](/gameplay/experience-bottle), all in its `rgbColor` (`43520` is `#00AA00`).

## Rules & Limits

- **`name` must be unique** across all skills, including built-ins and addon skills.
- **`attribute` must already exist.** Config-file skills can only buff an attribute that some mod (vanilla or otherwise) has registered — they can't create a new attribute. To add a new attribute you need the [Plugin API](/developer/custom-attributes).
- **`stat` must be a custom statistic.** Use a built-in Experience Skills stat or a `minecraft:custom` stat. Per-block / per-item statistics can't drive a skill directly.
- Config-file skills get the automatic attribute bonus only — they can't attach custom per-update logic (a `Handler`). For that, use a Java [plugin](/developer/custom-skills).
- After first run the skill also appears in the main config, where its `defaultConfig` becomes the editable live values.

::: tip When to use which
Use **config-file skills** to buff an existing attribute from an existing stat with no code. Reach for the **Plugin API** when you need a new attribute, a new stat, or custom logic that a single attribute modifier can't express.
:::
