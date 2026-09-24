<script setup lang="ts">
import { computed } from 'vue'
import { data, skillsUsingStat, statIcon, statName } from '../experienceskills'
import ItemSlot from './ItemSlot.vue'

/** How each built-in stat is counted; pooled stats add several vanilla statistics together. */
const COUNTS: Record<string, string> = {
  'experienceskills:block_walked': 'Walking, sprinting and crouching distance, in whole blocks',
  'experienceskills:water_walked': 'Walking on water, walking under water and swimming distance, in whole blocks',
  'experienceskills:block_broken': 'Every block broken',
  'experienceskills:block_placed': 'Every block placed',
  'experienceskills:block_interacted': 'Blocks broken plus blocks placed',
  'experienceskills:entity_killed': 'Mob kills plus player kills',
  'experienceskills:under_water_time': 'Ticks spent with your head under water',
  'experienceskills:fishing_time': 'Ticks spent with a fishing line cast',
  'experienceskills:items_fished': 'Items reeled in',
  'experienceskills:ore_mined': 'Ore blocks mined',
  'experienceskills:block_mined_in_water': 'Blocks broken while in water',
  'experienceskills:block_mined_with_pickaxe': 'Blocks broken with a pickaxe that is the correct tool',
  'experienceskills:block_mined_with_shovel': 'Blocks broken with a shovel that is the correct tool',
  'experienceskills:block_mined_with_axe': 'Blocks broken with an axe that is the correct tool',
  'experienceskills:block_mined_with_hoe': 'Blocks broken with a hoe that is the correct tool',
  'experienceskills:block_mined_when_floating': 'Blocks broken while not standing on the ground',
}

const rows = computed(() => data.stats.map((stat) => ({ ...stat, skills: skillsUsingStat(stat.id) })))
</script>

<template>
  <table class="es-stats">
    <thead>
      <tr>
        <th>Stat</th>
        <th>Counts</th>
        <th>Trains</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="stat in rows" :key="stat.id">
        <td>
          <div class="cell">
            <ItemSlot :id="statIcon(stat.id)" :name="statName(stat.id)" />
            <div>
              <div class="name">{{ stat.name }}</div>
              <code>{{ stat.id.split(':')[1] }}</code>
            </div>
          </div>
        </td>
        <td>{{ COUNTS[stat.id] ?? '' }}</td>
        <td>
          <div v-if="stat.skills.length" class="skills">
            <ItemSlot v-for="s in stat.skills" :key="s.id" :skill="s.id" />
          </div>
          <span v-else class="es-muted">None by default</span>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<style scoped>
.cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.name {
  font-weight: 600;
  white-space: nowrap;
}

.skills {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

code {
  font-size: 12px;
}
</style>
