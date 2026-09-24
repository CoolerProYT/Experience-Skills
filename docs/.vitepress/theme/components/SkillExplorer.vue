<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { amount, attributeName, bonus, data, effortForLevel, hex, number, statName, xpNeededForNextLevel } from '../experienceskills'
import ItemSlot from './ItemSlot.vue'

const props = withDefaults(defineProps<{ initial?: string }>(), { initial: 'walking_speed' })

const selected = ref(data.skills.some((s) => s.id === props.initial) ? props.initial : data.skills[0]?.id)
const s = computed(() => data.skills.find((x) => x.id === selected.value)!)
const level = ref(10)
watch(s, (next) => (level.value = Math.min(level.value, next.maxLevel)))

const effort = computed(() => effortForLevel(s.value, level.value))
const atMax = computed(() => level.value >= s.value.maxLevel)
</script>

<template>
  <div class="es-explorer" :style="{ '--skill': hex(s.rgbColor) }">
    <div class="picker" role="radiogroup" aria-label="Skill">
      <button
        v-for="skill in data.skills"
        :key="skill.id"
        type="button"
        role="radio"
        :aria-checked="skill.id === selected"
        :aria-label="skill.name"
        :class="{ active: skill.id === selected }"
        @click="selected = skill.id"
      >
        <ItemSlot :skill="skill.id" :focusable="false" />
      </button>
    </div>

    <div class="head">
      <ItemSlot :skill="s.id" large />
      <div>
        <div class="title">{{ s.name }}</div>
        <div class="es-muted">Buffs {{ attributeName(s.attribute) }} · trained by {{ statName(s.stat) }}</div>
      </div>
    </div>

    <label class="slider">
      <span>Level <strong>{{ level }}</strong> <span class="es-muted">/ {{ s.maxLevel }}</span></span>
      <input v-model.number="level" type="range" min="0" :max="s.maxLevel" step="1" />
    </label>

    <div class="stats">
      <div class="stat">
        <div class="label">{{ attributeName(s.attribute) }}</div>
        <div class="value">{{ bonus(s, level) }}</div>
      </div>
      <div class="stat">
        <div class="label">Total XP</div>
        <div class="value">{{ number(effort.points) }}</div>
        <div class="es-muted">{{ atMax ? 'level cap reached' : `${number(xpNeededForNextLevel(level))} more for level ${level + 1}` }}</div>
      </div>
      <div class="stat">
        <div class="label">{{ statName(s.stat) }}</div>
        <div class="value">{{ amount(s.stat, effort.raw) }}</div>
        <div class="es-muted">{{ number(effort.awards) }} awards of {{ s.xpPointToAward }} XP</div>
      </div>
    </div>
    <p class="es-muted note">Default values. Servers can change every number in the config.</p>
  </div>
</template>

<style scoped>
.es-explorer {
  margin: 16px 0;
  padding: 16px 20px 12px;
  border: 1px solid var(--vp-c-divider);
  border-top: 4px solid var(--skill);
  border-radius: 12px;
  background: var(--vp-c-bg-soft);
  transition: border-color 0.3s;
}

.picker {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--vp-c-divider);
}

.picker button {
  padding: 2px;
  border-radius: 4px;
  outline: 2px solid transparent;
  transition: outline-color 0.15s;
}

.picker button:hover {
  outline-color: var(--vp-c-divider);
}

.picker button.active {
  outline-color: var(--vp-c-brand-1);
}

.head {
  display: flex;
  align-items: center;
  gap: 14px;
  margin: 16px 0 8px;
}

.title {
  font-size: 20px;
  font-weight: 700;
  color: var(--vp-c-text-1);
}

.slider {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin: 12px 0 16px;
}

.slider input {
  width: 100%;
  accent-color: var(--skill);
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(170px, 1fr));
  gap: 10px;
}

.stat {
  padding: 10px 14px;
  border-radius: 8px;
  background: var(--vp-c-bg);
  border: 1px solid var(--vp-c-divider);
}

.label {
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--vp-c-text-2);
}

.value {
  font-size: 22px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  color: var(--vp-c-text-1);
}

.note {
  margin: 10px 0 0;
}
</style>
