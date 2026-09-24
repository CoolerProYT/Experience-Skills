<script setup lang="ts">
import { computed } from 'vue'
import { data } from '../experienceskills'
import ItemSlot from './ItemSlot.vue'

/** The "Experience Skills: Bottle" creative tab: one bottle per enabled skill, padded to full rows of nine. */
const slots = computed(() => {
  const skills = data.skills.filter((s) => s.enabled).map((s) => s.id)
  const rows = Math.max(3, Math.ceil(skills.length / 9))
  return Array.from({ length: rows * 9 }, (_, i) => skills[i] ?? null)
})
</script>

<template>
  <div class="es-tab">
    <div class="tab-title">Experience Skills: Bottle</div>
    <div class="grid">
      <ItemSlot v-for="(id, i) in slots" :key="i" :skill="id" />
    </div>
  </div>
</template>

<style scoped>
/* Minecraft's creative inventory panel. */
.es-tab {
  display: inline-block;
  max-width: 100%;
  margin: 12px 0;
  padding: 8px 10px 10px;
  background: #c6c6c6;
  border: 3px solid;
  border-color: #fff #555 #555 #fff;
  border-radius: 4px;
  box-shadow: 0 0 0 2px #000;
}

/* Nine slots are wider than a small phone; scroll the panel there instead of the page. */
@media (max-width: 420px) {
  .es-tab {
    overflow-x: auto;
  }
}

.tab-title {
  margin-bottom: 6px;
  font: 500 14px/1.2 var(--vp-font-family-mono);
  color: #404040;
}

.grid {
  display: grid;
  grid-template-columns: repeat(9, 36px);
}
</style>
