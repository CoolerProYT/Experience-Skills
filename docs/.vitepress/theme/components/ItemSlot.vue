<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { bottleIcon } from '../bottle'
import { itemIcon, itemName, skill as findSkill } from '../experienceskills'

/**
 * One inventory slot. Pass `id` for a vanilla item, `skill` for that skill's Experience Bottle,
 * or `color` (packed RGB) for a bottle of any colour, such as one from a config-file skill.
 */
const props = withDefaults(
  defineProps<{ id?: string | null; skill?: string | null; color?: number | null; name?: string; count?: number; label?: boolean; large?: boolean; focusable?: boolean }>(),
  { id: null, skill: null, color: null, name: '', count: 1, label: false, large: false, focusable: true },
)

const skillData = computed(() => (props.skill ? findSkill(props.skill) : undefined))
const bottleColor = computed(() => props.color ?? skillData.value?.rgbColor ?? null)
const isBottle = computed(() => bottleColor.value !== null)

const title = computed(() => {
  if (props.name) return props.name
  if (skillData.value) return `${skillData.value.name} Experience Bottle`
  if (isBottle.value) return 'Experience Bottle'
  return props.id ? itemName(props.id) : ''
})

// Bottles are tinted on a canvas, so they only exist once the page is running in the browser.
const bottleSrc = ref<string | null>(null)
async function renderBottle() {
  bottleSrc.value = bottleColor.value === null ? null : await bottleIcon(bottleColor.value)
}
onMounted(renderBottle)
watch(bottleColor, renderBottle)

const src = computed(() => (isBottle.value ? bottleSrc.value : props.id ? itemIcon(props.id) : null))

// Falls back to initials when an item has no icon or the hosted icon fails to load.
const failed = ref(false)
watch(src, () => (failed.value = false))
const initials = computed(() =>
  title.value
    .split(' ')
    .filter((word) => /^[A-Z]/.test(word))
    .slice(0, 2)
    .map((word) => word[0])
    .join(''),
)
</script>

<template>
  <span class="es-item" :class="{ 'with-label': label, large }">
    <span class="es-slot" :aria-label="title" role="img" :tabindex="focusable ? 0 : undefined">
      <img v-if="src && !failed" class="pixelated" :src="src" alt="" loading="lazy" @error="failed = true" />
      <span v-else-if="id && !isBottle" class="es-initials">{{ initials }}</span>
      <!-- Bottles always carry the enchantment glint in game. -->
      <span v-if="isBottle && src" class="es-glint" :style="{ '--mask': `url(${src})` }" />
      <span v-if="count > 1" class="es-count">{{ count }}</span>
      <span v-if="title" class="es-tooltip" :class="{ uncommon: isBottle }">{{ title }}</span>
    </span>
    <span v-if="label && title" class="es-label">{{ title }}</span>
  </span>
</template>

<style scoped>
.es-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  vertical-align: middle;
}

.es-slot {
  --size: 36px;
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: var(--size);
  height: var(--size);
  flex: none;
  background: var(--es-slot-bg);
  border: 2px solid;
  border-color: var(--es-slot-dark) var(--es-slot-light) var(--es-slot-light) var(--es-slot-dark);
  outline: none;
}

.large .es-slot {
  --size: 56px;
}

.es-slot img,
.es-glint {
  width: calc(var(--size) - 4px);
  height: calc(var(--size) - 4px);
}

.es-slot:hover,
.es-slot:focus-visible {
  background: var(--es-slot-hover);
}

.es-glint {
  position: absolute;
  inset: 0;
  margin: auto;
  pointer-events: none;
  /* A thin sweep, so the bottle's own colour still reads through most of the cycle. */
  background: linear-gradient(115deg, transparent 42%, rgba(170, 100, 255, 0.45) 47%, rgba(255, 255, 255, 0.35) 50%, rgba(170, 100, 255, 0.45) 53%, transparent 58%);
  background-size: 400% 400%;
  mix-blend-mode: screen;
  -webkit-mask: var(--mask) center / 100% 100% no-repeat;
  mask: var(--mask) center / 100% 100% no-repeat;
  animation: es-glint 3.5s linear infinite;
}

@keyframes es-glint {
  from {
    background-position: 100% 100%;
  }
  to {
    background-position: 0% 0%;
  }
}

@media (prefers-reduced-motion: reduce) {
  .es-glint {
    animation: none;
    background-position: 50% 50%;
  }
}

.es-initials {
  font: 600 12px/1 var(--vp-font-family-mono);
  color: #fff;
  text-shadow: 1px 1px 0 #3f3f3f;
}

.es-count {
  position: absolute;
  right: 1px;
  bottom: -1px;
  font: 700 12px/1 var(--vp-font-family-mono);
  color: #fff;
  text-shadow: 1px 1px 0 #3f3f3f;
}

/* Minecraft's item tooltip: near-black panel with a purple frame. */
.es-tooltip {
  position: absolute;
  left: calc(100% + 6px);
  bottom: calc(100% - 6px);
  z-index: 20;
  padding: 3px 6px;
  white-space: nowrap;
  font: 500 13px/1.3 var(--vp-font-family-mono);
  color: #fff;
  text-shadow: 1px 1px 0 #3f3f3f;
  background: rgba(16, 0, 16, 0.94);
  border: 2px solid #28007f;
  border-radius: 2px;
  box-shadow: 0 0 0 1px rgba(16, 0, 16, 0.94);
  /* display, not visibility, so hidden tooltips never widen a scrolling table. */
  display: none;
  pointer-events: none;
}

.es-tooltip.uncommon {
  color: #ffff55;
}

.es-slot:hover .es-tooltip,
.es-slot:focus-visible .es-tooltip {
  display: block;
}

.es-label {
  font-weight: 500;
}
</style>
