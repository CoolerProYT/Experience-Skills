# Plugin Setup

A plugin is any class that implements `IExperienceSkillsPlugin`. Its job is to declare one or more skills in `registerExperienceType`. This page covers wiring the plugin up so the loader discovers it; the next pages cover what to put inside.

## 1. Implement the Interface

```java
import com.coolerpromc.experienceskills.api.IExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.api.type.IExperienceTypeRegistry;

public class MyPlugin implements IExperienceSkillsPlugin {
    @Override
    public void registerExperienceType(IExperienceTypeRegistry registry) {
        // register skills here (see "Adding a Skill")
    }
}
```

Your plugin class **must have a public no-argument constructor**.

`registerExperienceType` is called once during startup, before the rest of the mod initialises, so every skill is known before config entries, attachments, entities or renderers are derived from it. It is also the safe place to register any custom stats or attributes your skills need.

## 2. Register It With the Loader

How the plugin is discovered depends on the loader.

### NeoForge

Annotate the class with `@ExperienceSkillsPlugin`. Every mod file is scanned for the annotation and each annotated plugin is instantiated automatically — nothing else is required.

```java
import com.coolerpromc.experienceskills.api.ExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.api.IExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.api.type.IExperienceTypeRegistry;

@ExperienceSkillsPlugin
public class MyPlugin implements IExperienceSkillsPlugin {
    @Override
    public void registerExperienceType(IExperienceTypeRegistry registry) {
        // ...
    }
}
```

### Fabric

The annotation is **not** used on Fabric. Instead declare the plugin as an `experience_skills_plugin` custom entrypoint in `fabric.mod.json`:

```json
{
  "entrypoints": {
    "experience_skills_plugin": [
      "com.example.mymod.MyPlugin"
    ]
  }
}
```

::: tip Multiloader projects
Put the plugin class in your common source set and register it per loader — the `@ExperienceSkillsPlugin` annotation in the NeoForge module (or on the common class, which NeoForge still scans) and the entrypoint in the Fabric module's `fabric.mod.json`.
:::

## What's Next

- [Adding a Skill](/developer/custom-skills) — the `register(...)` overloads and handlers.
- [Adding a Stat](/developer/custom-stats) — register and drive a custom stat.
- [Adding an Attribute](/developer/custom-attributes) — register a new attribute a skill can buff.
