import DefaultTheme from 'vitepress/theme'
import type { Theme } from 'vitepress'
import AttributeTable from './components/AttributeTable.vue'
import BottleGallery from './components/BottleGallery.vue'
import ItemSlot from './components/ItemSlot.vue'
import SkillExplorer from './components/SkillExplorer.vue'
import SkillTable from './components/SkillTable.vue'
import StatTable from './components/StatTable.vue'
import VersionSwitcher from './components/VersionSwitcher.vue'
import './style.css'

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.component('AttributeTable', AttributeTable)
    app.component('BottleGallery', BottleGallery)
    app.component('ItemSlot', ItemSlot)
    app.component('SkillExplorer', SkillExplorer)
    app.component('SkillTable', SkillTable)
    app.component('StatTable', StatTable)
    app.component('VersionSwitcher', VersionSwitcher)
  },
} satisfies Theme
