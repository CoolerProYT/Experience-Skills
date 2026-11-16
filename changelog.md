## 26.1.2.12
### Added
- New `experienceskills:detection_range` attribute (#4). It multiplies how far away mobs notice the player (default 1.0, minimum 0.1) and stacks with sneaking, invisibility and mob heads. Use it in a skill milestone, e.g. `{ "level": 50, "attribute": "experienceskills:detection_range", "amount": -0.6, "operation": "add_multiplied_base" }`.

### Changed
- Milestones that target an attribute players do not have (such as the mob-only `minecraft:follow_range`) now log a warning instead of being ignored silently.
