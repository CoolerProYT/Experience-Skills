# Skills

Experience Skills ships with **18 built-in skills**. Each one is trained by a stat and applies a bonus to a single attribute as it levels. The values below are the defaults, read straight from the mod. Every one of them can be changed per skill in the [config](/config/skill-config).

<SkillTable />

All built-in skills cap at **level 100** and award **5 XP** each time their stat advances by the listed amount. Attributes marked *custom* are added by Experience Skills itself because vanilla has nothing matching; see [Attributes](/gameplay/attributes).

## Level calculator

Pick a skill and drag the slider to see the bonus at that level and how much of its stat it takes to get there. Skills level on the same curve as vanilla XP, so the early levels come quickly and each later one costs more.

<SkillExplorer />

## Skill details

### Movement

- <ItemSlot skill="walking_speed" /> **Walking Speed** trains as you move on land. Walking, sprinting and crouching distance are pooled together. Raises Movement Speed.
- <ItemSlot skill="jump" /> **Jump** trains from the vanilla *Jumps* statistic and raises Jump Strength. It also raises your Safe Fall Distance to match the extra height, so a jump from flat ground never hurts you.
- <ItemSlot skill="swim_speed" /> **Swim Speed** trains from distance moved on, under or through water. Raises the custom Swim Speed attribute.
- <ItemSlot skill="oxygen" /> **Oxygen** trains from time spent underwater and adds to Oxygen Bonus, so you can hold your breath longer.

### Combat

- <ItemSlot skill="strength" /> **Strength** trains from mobs and players killed. Raises Attack Damage.
- <ItemSlot skill="entity_reach" /> **Entity Reach** trains from mobs and players killed. Raises Entity Interaction Range, so you can hit from further away.
- <ItemSlot skill="vitality" /> **Vitality** trains from damage taken. Raises Max Health.
- <ItemSlot skill="toughness" /> **Toughness** trains from damage taken. Raises Armor Toughness.

### Mining

- <ItemSlot skill="pickaxe_breaking_speed" /> <ItemSlot skill="shovel_breaking_speed" /> <ItemSlot skill="axe_breaking_speed" /> <ItemSlot skill="hoe_breaking_speed" /> **Pickaxe, Shovel, Axe and Hoe Breaking Speed** each train from blocks broken with that tool, when it is the correct tool for the block. Each one raises mining speed with that tool only.
- <ItemSlot skill="mining_luck" /> **Mining Luck** trains from ores mined and adds extra drops to every ore you break: +1 item every 5 levels, up to +20 at level 100.
- <ItemSlot skill="submerged_breaking_speed" /> **Submerged Breaking Speed** trains from blocks broken while in water. Raises the vanilla Submerged Mining Speed attribute, which softens the slowdown for mining underwater.
- <ItemSlot skill="floating_breaking_speed" /> **Floating Breaking Speed** trains from blocks broken while you are not standing on the ground. It eases the ×5 slowdown vanilla applies to mining in mid-air, until at level 100 there is none.
- <ItemSlot skill="block_reach" /> **Block Reach** trains from damage taken. Raises Block Interaction Range, so you can reach blocks further away.

### Fishing

- <ItemSlot skill="fishing_speed" /> **Fishing Speed** trains from time spent with a line cast. Raises the custom Lure Speed attribute, which works like extra levels of Lure.
- <ItemSlot skill="fishing_luck" /> **Fishing Luck** trains from catches reeled in. Raises the custom Fishing Luck attribute, which works like extra levels of Luck of the Sea.

## The `vanilla` entry

Alongside the real skills there is a special `vanilla` entry. It is **not an earnable skill**. It exists only so the HUD can cycle back to the ordinary vanilla experience bar. It never appears in the config, awards no experience, and is left out of the API's list of skills.
