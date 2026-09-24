import { defineConfig } from 'vitepress'

// GitHub Pages serves a project site from /<repository>/. For a custom domain or a user site, build with DOCS_BASE=/.
const base = process.env.DOCS_BASE ?? '/Experience-Skills/'
// Edit links point at the branch being built (the version branch in CI), falling back to the current one.
const branch = process.env.GITHUB_REF_NAME ?? '26.3'

export default defineConfig({
  title: 'Experience Skills',
  description: 'Level up skills as you play: every action trains a skill, every skill buffs an attribute. For Minecraft 26.1+ on Fabric and NeoForge.',
  base,
  cleanUrls: true,
  lastUpdated: true,
  srcExclude: ['README.md', 'scripts/**'],
  head: [['link', { rel: 'icon', type: 'image/png', href: `${base}logo.png` }]],
  themeConfig: {
    logo: { src: '/logo.png', alt: '' },
    nav: [
      { text: 'Guide', link: '/gameplay/getting-started' },
      { text: 'Skills', link: '/gameplay/skills-list' },
      { text: 'Config', link: '/config/skill-config' },
      { text: 'Developers', link: '/developer/overview' },
      { component: 'VersionSwitcher' },
    ],
    sidebar: [
      {
        text: 'Guide',
        items: [
          { text: 'Getting started', link: '/gameplay/getting-started' },
          { text: 'Skills', link: '/gameplay/skills-list' },
          { text: 'Stats', link: '/gameplay/stats' },
          { text: 'Attributes', link: '/gameplay/attributes' },
          { text: 'Experience Bottle', link: '/gameplay/experience-bottle' },
          { text: 'Commands', link: '/gameplay/commands' },
        ],
      },
      {
        text: 'Configuration',
        items: [
          { text: 'Skill config', link: '/config/skill-config' },
          { text: 'Config-file skills', link: '/config/config-skills' },
        ],
      },
      {
        text: 'For mod developers',
        collapsed: false,
        items: [
          { text: 'How the API works', link: '/developer/overview' },
          { text: 'Plugin setup', link: '/developer/plugin-api' },
          { text: 'Adding a skill', link: '/developer/custom-skills' },
          { text: 'Adding a stat', link: '/developer/custom-stats' },
          { text: 'Adding an attribute', link: '/developer/custom-attributes' },
          { text: 'API reference', link: '/developer/api-reference' },
          { text: 'Events', link: '/developer/events' },
        ],
      },
    ],
    socialLinks: [
      { icon: 'github', link: 'https://github.com/CoolerProYT/Experience-Skills' },
      { icon: 'discord', link: 'https://discord.gg/hvFfqsqQm8' },
    ],
    editLink: {
      pattern: `https://github.com/CoolerProYT/Experience-Skills/edit/${branch}/docs/:path`,
      text: 'Edit this page on GitHub',
    },
    search: { provider: 'local' },
    outline: { level: [2, 3] },
    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2026 CoolerProMC',
    },
  },
})
