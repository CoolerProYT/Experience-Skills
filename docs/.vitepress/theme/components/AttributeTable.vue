<script setup lang="ts">
import { computed } from 'vue'
import { data, number, skillsUsingAttribute } from '../experienceskills'
import ItemSlot from './ItemSlot.vue'

/** What the mod does with each attribute's value. */
const EFFECTS: Record<string, string> = {
  'experienceskills:swim_speed': 'Multiplies how fast you move and rise while swimming.',
  'experienceskills:lure_speed': "Added to your cast's Lure level, so fish bite sooner. Only whole points count.",
  'experienceskills:fishing_luck': "Added to your cast's Luck of the Sea level for better loot. Only whole points count.",
  'experienceskills:mining_luck': 'Extra items from every ore broken, rounded down: 0.2 per level is +1 drop every 5 levels.',
  'experienceskills:pickaxe_mining_speed': 'Multiplies mining speed while holding a pickaxe.',
  'experienceskills:shovel_mining_speed': 'Multiplies mining speed while holding a shovel.',
  'experienceskills:axe_mining_speed': 'Multiplies mining speed while holding an axe.',
  'experienceskills:hoe_mining_speed': 'Multiplies mining speed while holding a hoe.',
  'experienceskills:floating_mining_speed':
    'Eases the off-ground mining penalty. Vanilla divides mining speed by 5 while you are not on the ground; each point lowers that divisor by 1, so 4 removes it.',
}

const rows = computed(() => data.attributes.map((a) => ({ ...a, skills: skillsUsingAttribute(a.id) })))
</script>

<template>
  <table class="es-attributes">
    <thead>
      <tr>
        <th>Attribute</th>
        <th>Effect</th>
        <th>Default</th>
        <th>Skill</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="a in rows" :key="a.id">
        <td>
          <div class="name">{{ a.name }}</div>
          <code>{{ a.id.split(':')[1] }}</code>
        </td>
        <td>{{ EFFECTS[a.id] ?? '' }}</td>
        <td class="num">
          {{ number(a.defaultValue) }}
          <div class="es-muted">{{ number(a.min) }}–{{ number(a.max) }}</div>
        </td>
        <td>
          <div class="skills">
            <ItemSlot v-for="s in a.skills" :key="s.id" :skill="s.id" />
          </div>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<style scoped>
.name {
  font-weight: 600;
  white-space: nowrap;
}

.num {
  white-space: nowrap;
  font-variant-numeric: tabular-nums;
}

.skills {
  display: flex;
  gap: 4px;
}

code {
  font-size: 12px;
}
</style>
