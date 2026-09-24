# Experience Skills wiki

VitePress site for the mod. The built-in skill defaults, stats, attributes and item names are read from the mod itself, so the skill tables never drift from the game.

```bash
cd docs
npm install
npm run dev                   # syncs data, then serves http://localhost:5173
npm run build                 # syncs data, then builds to .vitepress/dist
```

`npm run sync` (run automatically by `dev` and `build`) reads `InternalExperienceSkillsPlugin.java`, `ModStats.java`, `ModAttributes.java` and `en_us.json` from `common/`, writes `.vitepress/data/data.json`, and copies the Experience Bottle's texture layers to `public/items/`. Both are git-ignored. Bottles are tinted per skill in the browser (`.vitepress/theme/bottle.ts`), the same way the game's tint sources do it, so new skill colours need no new textures. Vanilla items load from the hosted renders at `https://storage.googleapis.com/coolerpromc/textures/`, set in `.vitepress/theme/experienceskills.ts`.

If a change to `InternalExperienceSkillsPlugin` breaks the sync script's pattern, it fails the build rather than publishing an empty skill list.

## Deploying

`.github/workflows/docs.yml` builds the site and publishes it to GitHub Pages on every push to the repository's default branch, or when run by hand from the Actions tab. One-time setup: repository **Settings → Pages → Source: GitHub Actions**.

## Adding a docs version

`VersionSwitcher.vue` lists doc versions. To snapshot an old version, copy the current pages into a subfolder (e.g. `docs/v2601/`), add a matching sidebar block in `config.mts`, and add an entry to the `versions` array in `VersionSwitcher.vue`.
