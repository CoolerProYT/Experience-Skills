<script setup>
import {useRouter, useData, withBase} from 'vitepress'
import {ref, computed} from 'vue'

const router = useRouter()
const {page} = useData()

const isOpen = ref(false)

// List all versions with their paths.
// Add new entries here (e.g. {label: 'v26.1.x', path: '/v2601/'}) when you
// snapshot an older docs version into a subfolder, exactly like ResourcesTrees.
const versions = [
    {label: 'v26.x (Latest)', path: '/'},
]

// Detect current version from URL
const currentVersion = computed(() => {
    const path = page.value.relativePath

    // Sort versions by path length (longest first) to match most specific first
    const sortedVersions = [...versions].sort((a, b) => {
        const pathA = a.path.replace(/^\//, '')
        const pathB = b.path.replace(/^\//, '')
        return pathB.length - pathA.length
    })

    const found = sortedVersions.find(v => {
        const versionPath = v.path.replace(/^\//, '')
        // Empty path (root) should only match if no other version matches
        if (versionPath === '') return true
        return path.startsWith(versionPath)
    })

    return found?.label || versions[0].label
})

// Navigate on selection
function switchVersion(v) {
    // relativePath is the markdown file without the site base, e.g. "v2601/gameplay/stats.md".
    const relativePath = page.value.relativePath.replace(/\.md$/, '').replace(/(^|\/)index$/, '')

    // Extract the page name (everything after the version directory)
    const pageMatch = relativePath.match(/^v[^/]+\/(.*)$/)
    const pageName = pageMatch ? pageMatch[1] : relativePath

    // Build new path with selected version, under the site base
    router.go(withBase(`${v.path}${pageName}`))
}

function showDropdown() {
    isOpen.value = true
}

function hideDropdown() {
    isOpen.value = false
}
</script>

<template>
    <div
        class="version-switcher VPFlyout"
        @mouseenter="showDropdown"
        @mouseleave="hideDropdown"
    >
        <button
            class="button"
            type="button"
            :aria-expanded="isOpen"
        >
            <span class="text">{{ currentVersion }}</span>
            <svg class="icon" width="14" height="14" viewBox="0 0 24 24" fill="none">
                <path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
        </button>

        <div v-if="isOpen" class="menu">
            <div class="items">
                <div
                    v-for="v in versions"
                    :key="v.label"
                    class="item"
                    :class="{ active: v.label === currentVersion }"
                    @click="switchVersion(v)"
                >
                    <span class="text">{{ v.label }}</span>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.version-switcher {
    position: relative;
    display: flex;
    align-items: center;
}

.button {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 0 12px;
    line-height: 48px;
    font-size: 14px;
    font-weight: 500;
    color: var(--vp-c-text-1);
    background: transparent;
    border: none;
    cursor: pointer;
    transition: color 0.25s;
}

.button:hover {
    color: var(--vp-c-brand-1);
}

.button .text {
    line-height: 48px;
}

.button .icon {
    width: 14px;
    height: 14px;
    fill: none;
    transition: transform 0.25s;
}

.button[aria-expanded="true"] .icon {
    transform: rotate(180deg);
}

.menu {
    position: absolute;
    top: calc(100% - 15px);
    right: 0;
    background: var(--vp-c-bg-elv);
    border: 1px solid var(--vp-c-divider);
    border-radius: 8px;
    padding: 12px;
    min-width: 128px;
    box-shadow: var(--vp-shadow-3);
    z-index: 100;
}

.items {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.item {
    padding: 6px 12px;
    font-size: 14px;
    color: var(--vp-c-text-1);
    cursor: pointer;
    transition: background-color 0.25s, color 0.25s;
    white-space: nowrap;
    border-radius: 6px;
}

.item:hover {
    background-color: var(--vp-c-default-soft);
    color: var(--vp-c-brand-1);
}

.item.active {
    color: var(--vp-c-brand-1);
}

.item .text {
    line-height: 20px;
}
</style>
