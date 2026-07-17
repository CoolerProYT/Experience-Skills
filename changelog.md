## 26.1.2.4
- Added stat `block_mined_in_water`
- Added `Submerged Breaking Speed` skill - break blocks faster in water
- Added `Pickaxe Breaking Speed` skill - break blocks faster using pickaxe
- Added `Shovel Breaking Speed` skill - break blocks faster using shovel
- Added `Axe Breaking Speed` skill - break blocks faster using axe
- Added `Hoe Breaking Speed` skill - break blocks faster using hoe
- Added new field `dropOnDeath` to skill config - experience will not drop when player death if set to `false`, default to `true`
- Removed `Breaking Speed` skill - replaced with tool based skill

### Fabric
- Fixed `GuiMixin` not applying - caused vanilla contextual bar still rendering behind skill experience bar

Notes: Some of the skill might be removed and replace by separated skill in the future