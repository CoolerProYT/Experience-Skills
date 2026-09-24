// Pulls wiki data straight from the mod so the docs never drift from the game:
// built-in skill defaults, stats and attributes from the common sources, names from the lang file,
// and the Experience Bottle's texture layers (tinted per skill in the browser).
import { copyFileSync, existsSync, mkdirSync, readFileSync, writeFileSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

const docs = join(dirname(fileURLToPath(import.meta.url)), '..')
const root = join(docs, '..')
const java = join(root, 'common/src/main/java/com/coolerpromc/experienceskills')
const assets = join(root, 'common/src/main/resources/assets/experienceskills')

const read = (file) => {
  if (!existsSync(file)) {
    console.error(`Missing ${file}. The sync script reads the mod's sources; run it from inside the repository.`)
    process.exit(1)
  }
  return readFileSync(file, 'utf8')
}

const lang = JSON.parse(read(join(assets, 'lang/en_us.json')))

// Minecraft constant names (MOVEMENT_SPEED, DAMAGE_TAKEN) to registry paths.
const path = (constant) => constant.toLowerCase()

/** `Attributes.X` is vanilla, `ModAttributes.X.holder()` is ours. */
const attributeId = (expr) => {
  const mod = expr.match(/ModAttributes\.(\w+)/)
  if (mod) return `experienceskills:${path(mod[1])}`
  const vanilla = expr.match(/Attributes\.(\w+)/)
  return vanilla ? `minecraft:${path(vanilla[1])}` : expr
}

/** `ModStats.X.id()` is ours, `Stats.X` is vanilla. */
const statId = (expr) => {
  const mod = expr.match(/ModStats\.(\w+)/)
  if (mod) return `experienceskills:${path(mod[1])}`
  const vanilla = expr.match(/Stats\.(\w+)/)
  return vanilla ? `minecraft:${path(vanilla[1])}` : expr
}

const number = (literal) => {
  const value = literal.trim()
  // Hex digits include f and d, so only decimal literals lose their Java type suffix.
  if (/^0x/i.test(value)) return parseInt(value.replace(/L$/i, ''), 16)
  return Number(value.replace(/[fFdDL]$/, ''))
}

// Built-in skills: the constants give ids, the register(...) calls give the default SkillsConfig.
const plugin = read(join(java, 'api/internal/InternalExperienceSkillsPlugin.java'))
const skillIds = Object.fromEntries([...plugin.matchAll(/public static String (\w+) = "([a-z0-9_]+)";/g)].map((m) => [m[1], m[2]]))
const skills = [...plugin.matchAll(/registry\.register\((\w+), new SkillsConfig\(([^)]*\([^)]*\)[^)]*|[^)]*)\), ([^;]+)\);/g)]
  .filter((m) => skillIds[m[1]])
  .map((m) => {
    const [enabled, incrementPerLevel, xpAwardActionCount, xpPointToAward, rgbColor, operation, maxLevel, stat, dropOnDeath] = m[2]
      .split(',')
      .map((part) => part.trim())
    const [attribute, handler] = m[3].split(',').map((part) => part.trim())
    const id = skillIds[m[1]]
    return {
      id,
      name: lang[`skill.experienceskills.${id}`] ?? id,
      attribute: attributeId(attribute),
      stat: statId(stat),
      enabled: enabled === 'true',
      incrementPerLevel: number(incrementPerLevel),
      xpAwardActionCount: number(xpAwardActionCount),
      xpPointToAward: number(xpPointToAward),
      rgbColor: number(rgbColor),
      operation: path(operation.split('.').pop()),
      maxLevel: number(maxLevel),
      dropOnDeath: dropOnDeath === 'true',
      handler: Boolean(handler),
    }
  })

// Custom stats, named from the lang file.
const stats = [...read(join(java, 'stat/ModStats.java')).matchAll(/registerStat\("([a-z0-9_]+)"\)/g)].map((m) => ({
  id: `experienceskills:${m[1]}`,
  name: lang[`stat.experienceskills.${m[1]}`] ?? m[1],
}))

// Custom attributes with their default, min and max.
const attributes = [
  ...read(join(java, 'attribute/ModAttributes.java')).matchAll(
    /register\("([a-z0-9_]+)", new RangedAttribute\("([^"]+)", ([\d.]+), ([\d.]+), ([\d.]+)\)/g,
  ),
].map((m) => ({
  id: `experienceskills:${m[1]}`,
  name: lang[m[2]] ?? m[1],
  defaultValue: number(m[3]),
  min: number(m[4]),
  max: number(m[5]),
}))

if (skills.length === 0) {
  console.error('Found no skills in InternalExperienceSkillsPlugin.java; the sync script needs updating for the new format.')
  process.exit(1)
}

// The bottle's three layers: layer0 untinted, layer1 tinted with the skill colour, layer2 with its border colour.
mkdirSync(join(docs, 'public/items'), { recursive: true })
for (const layer of ['experience_bottle', 'experience_bottle_layer1', 'experience_bottle_layer2']) {
  copyFileSync(join(assets, `textures/item/${layer}.png`), join(docs, `public/items/${layer}.png`))
}

mkdirSync(join(docs, '.vitepress/data'), { recursive: true })
writeFileSync(join(docs, '.vitepress/data/data.json'), JSON.stringify({ skills, stats, attributes }, null, 2))
console.log(`Synced ${skills.length} skills, ${stats.length} stats, ${attributes.length} attributes.`)
