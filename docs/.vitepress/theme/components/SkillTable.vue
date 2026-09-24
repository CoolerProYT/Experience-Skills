<script setup lang="ts">
import { computed, ref } from 'vue'
import { amount, attributeName, bonus, categories, category, data, hex, isCustomAttribute, statIcon, statName } from '../experienceskills'
import ItemSlot from './ItemSlot.vue'

const filter = ref('All')
const query = ref('')

const rows = computed(() => {
  const q = query.value.trim().toLowerCase()
  return data.skills.filter((s) => {
    if (filter.value !== 'All' && category(s.id) !== filter.value) return false
    if (!q) return true
    return [s.id, s.name, attributeName(s.attribute), statName(s.stat), s.stat, s.attribute].some((text) => text.toLowerCase().includes(q))
  })
})
</script>

<template>
  <div class="es-skills">
    <div class="toolbar">
      <div class="chips" role="group" aria-label="Filter by category">
        <button v-for="name in ['All', ...categories()]" :key="name" type="button" :class="{ active: filter === name }" @click="filter = name">
          {{ name }}
        </button>
      </div>
      <input v-model="query" type="search" placeholder="Search skills, stats, attributes" aria-label="Search skills" />
    </div>

    <table>
      <thead>
        <tr>
          <th>Skill</th>
          <th>Bonus</th>
          <th>Trained by</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="s in rows" :key="s.id">
          <td>
            <div class="cell">
              <ItemSlot :skill="s.id" />
              <div>
                <div class="name">
                  <span class="swatch" :style="{ background: hex(s.rgbColor) }" />
                  {{ s.name }}
                </div>
                <code>{{ s.id }}</code>
              </div>
            </div>
          </td>
          <td>
            <div class="attr">
              {{ attributeName(s.attribute) }}
              <span v-if="isCustomAttribute(s.attribute)" class="badge">custom</span>
            </div>
            <div class="num">
              <strong>{{ bonus(s, 1) }}</strong> <span class="es-muted">per level</span>
            </div>
            <div class="num es-muted">Lv {{ s.maxLevel }}: {{ bonus(s, s.maxLevel) }}</div>
          </td>
          <td>
            <div class="cell">
              <ItemSlot :id="statIcon(s.stat)" :name="statName(s.stat)" />
              <div>
                <div>{{ statName(s.stat) }}</div>
                <div class="es-muted">{{ s.xpPointToAward }} XP every {{ amount(s.stat, s.xpAwardActionCount) }}</div>
              </div>
            </div>
          </td>
        </tr>
        <tr v-if="rows.length === 0">
          <td colspan="3" class="es-muted">No skill matches “{{ query }}”.</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.es-skills {
  margin: 16px 0;
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.chips button {
  padding: 4px 12px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid var(--vp-c-divider);
  border-radius: 999px;
  color: var(--vp-c-text-2);
  transition: all 0.2s;
}

.chips button:hover {
  color: var(--vp-c-brand-1);
  border-color: var(--vp-c-brand-1);
}

.chips button.active {
  color: var(--vp-c-white);
  background: var(--vp-c-brand-3);
  border-color: var(--vp-c-brand-3);
}

input[type='search'] {
  flex: 1 1 220px;
  max-width: 300px;
  padding: 6px 12px;
  font-size: 14px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 8px;
  background: var(--vp-c-bg-soft);
}

input[type='search']:focus {
  border-color: var(--vp-c-brand-1);
}

.cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.name {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.swatch {
  width: 10px;
  height: 10px;
  border-radius: 2px;
  box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.25);
}

.attr {
  font-weight: 500;
}

.badge {
  margin-left: 4px;
  padding: 1px 6px;
  font-size: 11px;
  font-weight: 600;
  border-radius: 999px;
  color: var(--vp-c-brand-1);
  background: var(--vp-c-brand-soft);
}

.num {
  font-variant-numeric: tabular-nums;
}

/* Tighter than VitePress's default cells so three columns fit beside the outline. */
table th,
table td {
  padding: 8px 10px;
}

code {
  font-size: 12px;
}
</style>
