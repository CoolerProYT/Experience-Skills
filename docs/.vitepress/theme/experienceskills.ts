// @ts-ignore
import raw from '../data/data.json'

export type Operation = 'add_value' | 'add_multiplied_base' | 'add_multiplied_total'

export interface Skill {
  id: string
  name: string
  attribute: string
  stat: string
  enabled: boolean
  incrementPerLevel: number
  xpAwardActionCount: number
  xpPointToAward: number
  rgbColor: number
  operation: Operation
  maxLevel: number
  dropOnDeath: boolean
  /** Whether the skill runs extra logic on update besides its attribute modifier. */
  handler: boolean
}

export interface Stat {
  id: string
  name: string
}

export interface Attribute {
  id: string
  name: string
  defaultValue: number
  min: number
  max: number
}

export const data = raw as unknown as { skills: Skill[]; stats: Stat[]; attributes: Attribute[] }

export function skill(id: string): Skill | undefined {
  return data.skills.find((s) => s.id === id || `experienceskills:${s.id}` === id)
}

/** Hosted renders of vanilla items, one PNG per item id. Mojang's textures are not bundled here. */
const VANILLA_ICONS = 'https://storage.googleapis.com/coolerpromc/textures'

export function itemIcon(id: string): string | null {
  // @ts-ignore
  const [namespace, path] = id.includes(':') ? id.split(':') : ['minecraft', id]
  return namespace === 'minecraft' ? `${VANILLA_ICONS}/${namespace}/${path}.png` : null
}

function prettify(id: string): string {
  const path = id.replace(/^#/, '').split(':').pop() ?? id
  return path
    .split('_') // @ts-ignore
    .map((word) => (['of', 'the', 'in', 'with', 'when'].includes(word) ? word : word.charAt(0).toUpperCase() + word.slice(1)))
    .join(' ')
}

export const itemName = prettify

const VANILLA_STAT_NAMES: Record<string, string> = {
  'minecraft:jump': 'Jumps',
  'minecraft:damage_taken': 'Damage Taken',
  'minecraft:damage_dealt': 'Damage Dealt',
}

export function statName(id: string): string {
  return data.stats.find((s) => s.id === id)?.name ?? VANILLA_STAT_NAMES[id] ?? prettify(id)
}

export function attributeName(id: string): string {
  return data.attributes.find((a) => a.id === id)?.name ?? prettify(id)
}

export function isCustomAttribute(id: string): boolean {
  return id.startsWith('experienceskills:')
}

/** Item shown next to a stat, picked to suggest the action it counts. */
const STAT_ICONS: Record<string, string> = {
  'experienceskills:block_walked': 'minecraft:leather_boots',
  'experienceskills:water_walked': 'minecraft:water_bucket',
  'experienceskills:block_broken': 'minecraft:cobblestone',
  'experienceskills:block_placed': 'minecraft:bricks',
  'experienceskills:block_interacted': 'minecraft:crafting_table',
  'experienceskills:entity_killed': 'minecraft:bone',
  'experienceskills:under_water_time': 'minecraft:turtle_helmet',
  'experienceskills:fishing_time': 'minecraft:fishing_rod',
  'experienceskills:items_fished': 'minecraft:cod',
  'experienceskills:ore_mined': 'minecraft:diamond_ore',
  'experienceskills:block_mined_in_water': 'minecraft:prismarine',
  'experienceskills:block_mined_with_pickaxe': 'minecraft:iron_pickaxe',
  'experienceskills:block_mined_with_shovel': 'minecraft:iron_shovel',
  'experienceskills:block_mined_with_axe': 'minecraft:iron_axe',
  'experienceskills:block_mined_with_hoe': 'minecraft:iron_hoe',
  'experienceskills:block_mined_when_floating': 'minecraft:phantom_membrane',
  'minecraft:jump': 'minecraft:rabbit_foot',
  'minecraft:damage_taken': 'minecraft:shield',
  'minecraft:damage_dealt': 'minecraft:diamond_sword',
}

export function statIcon(id: string): string | null {
  return STAT_ICONS[id] ?? null
}

/** What one raw unit of a stat means, so counts can be shown in readable terms. */
type Unit = { scale: number; one: string; many: string }
const UNITS: Record<string, Unit> = {
  'experienceskills:block_walked': { scale: 1, one: 'block', many: 'blocks' },
  'experienceskills:water_walked': { scale: 1, one: 'block', many: 'blocks' },
  'experienceskills:entity_killed': { scale: 1, one: 'kill', many: 'kills' },
  'experienceskills:under_water_time': { scale: 1 / 20, one: 'second', many: 'seconds' },
  'experienceskills:fishing_time': { scale: 1 / 20, one: 'second', many: 'seconds' },
  'experienceskills:items_fished': { scale: 1, one: 'catch', many: 'catches' },
  'experienceskills:ore_mined': { scale: 1, one: 'ore', many: 'ores' },
  'minecraft:jump': { scale: 1, one: 'jump', many: 'jumps' },
  'minecraft:damage_taken': { scale: 1 / 10, one: 'heart', many: 'hearts' },
  'minecraft:damage_dealt': { scale: 1 / 10, one: 'heart', many: 'hearts' },
}
const BLOCKS: Unit = { scale: 1, one: 'block', many: 'blocks' }

export function unitOf(stat: string): Unit {
  return UNITS[stat] ?? (stat.startsWith('experienceskills:block_') ? BLOCKS : { scale: 1, one: 'time', many: 'times' })
}

/** A raw stat amount in readable units: 1200 ticks of under_water_time is "60 seconds". */
export function amount(stat: string, raw: number): string {
  const unit = unitOf(stat)
  const value = raw * unit.scale
  if (unit.one === 'second' && value >= 120) {
    const minutes = value / 60
    return `${number(minutes)} ${minutes === 1 ? 'minute' : 'minutes'}`
  }
  return `${number(value)} ${value === 1 ? unit.one : unit.many}`
}

export function number(value: number): string {
  const rounded = Math.round(value * 100) / 100
  return rounded.toLocaleString('en-US', { maximumFractionDigits: 2 })
}

/** The attribute bonus at a level, in the skill's operation: "+20% base", "+0.5". */
export function bonus(s: Skill, level: number): string {
  const value = s.incrementPerLevel * level
  const sign = value < 0 ? '−' : '+'
  const abs = Math.abs(value)
  switch (s.operation) {
    case 'add_multiplied_base':
      return `${sign}${number(abs * 100)}% base`
    case 'add_multiplied_total':
      return `${sign}${number(abs * 100)}% total`
    default:
      return `${sign}${number(abs)}`
  }
}

export const OPERATION_LABELS: Record<Operation, string> = {
  add_value: 'Flat',
  add_multiplied_base: '% of base',
  add_multiplied_total: '% of total',
}

// Level curve, same as vanilla XP (XpMath in the mod).
export function xpNeededForNextLevel(level: number): number {
  if (level >= 30) return 112 + (level - 30) * 9
  if (level >= 15) return 37 + (level - 15) * 5
  return 7 + level * 2
}

export function totalXpForLevel(level: number): number {
  let total = 0
  for (let l = 0; l < level; l++) total += xpNeededForNextLevel(l)
  return total
}

/** Awards needed to go from nothing to a level, and the raw stat that takes. */
export function effortForLevel(s: Skill, level: number) {
  const points = totalXpForLevel(level)
  const awards = s.xpPointToAward > 0 ? Math.ceil(points / s.xpPointToAward) : Infinity
  return { points, awards, raw: awards * s.xpAwardActionCount }
}

export function hex(rgb: number): string {
  return `#${(rgb & 0xffffff).toString(16).padStart(6, '0')}`
}

/** The bottle's highlight layer: hue shifted ~30°, less saturated and brighter (ExperienceBottleBorderTintSource). */
export function borderColor(rgb: number): number {
  const r = (rgb >> 16) & 0xff
  const g = (rgb >> 8) & 0xff
  const b = rgb & 0xff
  const max = Math.max(r, g, b)
  const min = Math.min(r, g, b)
  const delta = max - min
  let hue = 0
  if (delta !== 0) {
    if (max === r) hue = ((g - b) / delta) % 6
    else if (max === g) hue = (b - r) / delta + 2
    else hue = (r - g) / delta + 4
    hue /= 6
    if (hue < 0) hue += 1
  }
  const saturation = max === 0 ? 0 : delta / max
  const brightness = max / 255

  let h = hue + 0.08
  if (h > 1) h -= 1
  const s = Math.max(0, saturation - 0.3)
  const v = Math.min(1, brightness + 0.3)

  const i = Math.floor(h * 6)
  const f = h * 6 - i
  const p = v * (1 - s)
  const q = v * (1 - f * s)
  const t = v * (1 - (1 - f) * s)
  const [rr, gg, bb] = [
    [v, t, p],
    [q, v, p],
    [p, v, t],
    [p, q, v],
    [t, p, v],
    [v, p, q],
  ][i % 6]
  return (Math.round(rr * 255) << 16) | (Math.round(gg * 255) << 8) | Math.round(bb * 255)
}

/** Groups for the skill list, by what the player is doing. Skills not listed land in "Other". */
const CATEGORIES: [string, string[]][] = [
  ['Movement', ['walking_speed', 'jump', 'swim_speed', 'oxygen']],
  ['Combat', ['strength', 'vitality', 'toughness', 'entity_reach']],
  ['Mining', ['mining_luck', 'pickaxe_breaking_speed', 'shovel_breaking_speed', 'axe_breaking_speed', 'hoe_breaking_speed', 'submerged_breaking_speed', 'floating_breaking_speed', 'block_reach']],
  ['Fishing', ['fishing_speed', 'fishing_luck']],
]

export function category(id: string): string {
  return CATEGORIES.find(([, ids]) => ids.includes(id))?.[0] ?? 'Other'
}

export function categories(): string[] {
  const used = new Set(data.skills.map((s) => category(s.id)))
  return [...CATEGORIES.map(([name]) => name), 'Other'].filter((name) => used.has(name))
}

export function skillsUsingStat(stat: string): Skill[] {
  return data.skills.filter((s) => s.stat === stat)
}

export function skillsUsingAttribute(attribute: string): Skill[] {
  return data.skills.filter((s) => s.attribute === attribute)
}
