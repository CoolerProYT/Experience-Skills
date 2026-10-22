# Getting started

Experience Skills turns the things you already do in Minecraft into a set of levelled skills. Each **skill** is trained by a **stat** (an action you perform) and, as it levels up, applies a scaling bonus to a Minecraft **attribute**. Walk a lot and your Walking Speed skill raises your movement speed; mine with a pickaxe and your Pickaxe Breaking Speed skill makes it dig faster.

<div class="es-flow">
  <div><ItemSlot id="minecraft:leather_boots" name="Walk 100 blocks" /><span>Perform an action</span></div>
  <span class="es-arrow">➜</span>
  <div><ItemSlot skill="walking_speed" /><span>Collect skill XP</span></div>
  <span class="es-arrow">➜</span>
  <div><ItemSlot id="minecraft:sugar" name="+2% Movement Speed per level" /><span>Get stronger</span></div>
</div>

## How it works

1. **Perform an action.** Every action Minecraft already tracks as a statistic, like walking, mining, killing or taking damage, feeds a skill.
2. **Earn experience.** Each time the driving stat advances by a set amount, the skill drops **experience orbs** tinted in that skill's colour. Collect them to gain points.
3. **Level up.** Enough points raises the skill's level, on the same curve as vanilla XP levels by default (servers can change the curve per skill). A toast pops up each time a skill levels.
4. **Get stronger.** Every level re-applies that skill's attribute bonus, so higher levels mean bigger buffs, up to the skill's level cap. Some skills also unlock a [milestone perk](/gameplay/progression#milestones) at certain levels.

Because skills are driven by persistent statistics rather than a temporary counter, your progress is saved with the world. When you die, you lose skill experience like vanilla experience (unless `keepInventory` is on), and some of it drops as orbs you can pick back up. How much is lost is configurable per skill.

## The HUD

Experience Skills replaces the vanilla XP bar with a per-skill bar. It shows the current skill's level and its progress toward the next level, tinted in that skill's colour.

To choose which skill's bar is displayed, open the skills screen with **R** and click a skill. Click the same skill again to go back to the vanilla bar.

## The skills screen

Press **R** (rebindable as *Open Skills Screen* in *Options → Controls*) to see every skill at once: its level, progress to the next level, current bonus, how close you are to the next experience award, and the next milestone. Hover a skill for the full details, and click it to show it on the HUD bar (click it again for the vanilla bar). Press **R** again or **Esc** to close it. See [Progression](/gameplay/progression) for everything else the screen can do.

## Experience Bottles

Each skill has its own [Experience Bottle](/gameplay/experience-bottle), tinted in the skill's colour. Throw one and it bursts into orbs for that skill only. They are found in the **Experience Skills: Bottle** creative tab.

## What's next

- Browse every built-in skill and try the level calculator on the [Skills](/gameplay/skills-list) page.
- Learn what drives each skill on the [Stats](/gameplay/stats) page.
- Read about milestones, advancements, transfers and the Skill Insight potion on the [Progression](/gameplay/progression) page.
- Tune skills to taste with the [Skill config](/config/skill-config).
- Add your own skills, no code required, with [Config-file skills](/config/config-skills).
- Extend the mod from Java with the [Plugin API](/developer/overview).
