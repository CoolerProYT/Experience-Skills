## 26.2.0.3
### New
- Skills screen (default key **R**): every skill's level, progress, current bonus, progress to the next award and next milestone, with details on hover. Click a skill to show it on the HUD bar, or click it again for the vanilla bar.
- Level-up toasts (can be turned off with `levelUpToasts` in `experienceskills-client.json5`).
- 11 new skills: Knockback Resistance, Fall Resistance, Sneaking, Luck, Fire Resistance, Archery, Farming, Enchanting, Trading, Breeding and Cooking.
- Milestones: perks unlocked at a skill level, either a permanent effect or an extra attribute bonus. Several built-in skills have defaults, e.g. Swim Speed 50 grants Dolphin's Grace.
- 7 advancements for reaching skill levels and unlocking milestones.
- Potion of Skill Insight (Awkward Potion + Bottle o' Enchanting): doubles skill experience earned from actions.
- `/experienceskills transfer <from> <to> <points>` and a transfer panel on the skills screen, for moving experience between your own skills at a loss.
- New per-skill config options: `additionalStats` (train a skill from several weighted stats), `curve` (vanilla, linear, flat or exponential level costs), `scaling` (linear, sqrt or logarithmic bonus growth), `deathPenalty`, `deathDropCap` and `milestones`. Existing config files keep working and gain the new fields with their defaults, except that existing skills keep an empty `milestones` list (delete a skill's block to get its default milestones).
- New `experienceskills-general.json5`: optional XP sharing with nearby players, transfer settings, and mirroring skill levels into `es_<skill>` scoreboard objectives.
- JEI information pages for every skill's Experience Bottle, and a Jade tooltip for skill experience orbs (both optional).
- API: `SkillEvents` with level-change and experience-gain hooks; `SkillsConfig.with...` builders; `ExperienceType.levelFor`, `bonusForLevel` and related helpers.

### Changed
- **R** now opens the skills screen. The *Cycle Contextual Bar* key is gone: pick the HUD bar by clicking a skill on the screen instead.
- Clients now use the server's skill config, so levels and bonuses display correctly on servers with a customised config.
- Skill experience is no longer lost when the `keepInventory` gamerule is on.
- Changing `dropOnDeath` no longer needs a restart.
- Orb pickups play the experience orb sound; the level-up sound now plays only when a skill levels up.
- Block Reach now trains from blocks placed and broken by default, instead of damage taken. Existing configs keep their current stat.

### Fixed
- Experience orbs of different skills with the same value could merge, sending experience to the wrong skill.