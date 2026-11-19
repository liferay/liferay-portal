---

paths:
  - "modules/**/node-scripts.config.js"
  - "modules/**/package.json"
  - "modules/yarn.lock"

---

# JavaScript Bundling

Read this before judging what a dependency change (a `package.json` edit or a `yarn.lock` refresh) does to the JavaScript the portal ships. Which projects a package reaches at runtime depends on how each project is bundled, not on the lockfile alone.

## Who Bundles

A project is built by `node-scripts` when it has a `node-scripts.config.js` file or a `package.json` script that runs `node-scripts`. Its `build` script picks one of two commands:

- **`node-scripts build`.** The standard build. esbuild bundles the project's main entry and the submodules its `node-scripts.config.js` declares, into `build/node/packageRunBuild/resources/__liferay__`.
- **`node-scripts build:custom`.** For exotic projects. It runs the `customBuild` section of the project's `node-scripts.config.js`: plain esbuild options passed through unchanged, plus an optional `other` step. It does not apply the linker plugin described below, so the project bundles everything it imports into its own files, usually served from its own page. Its packages never interact with the rest of the portal at runtime. `headless-discovery-web` (GraphiQL, Monaco) is one.

## Shared Packages

`modules/node-scripts.config.js` is generated (`node-scripts generate:global-config`) from every project's `node-scripts.config.js`. Its `imports` section maps each project to what it exports. A project's own name stands for its default export. The listed entries are its submodules or the third-party packages it provides to everyone else, for example `'frontend-js-recharts': ['recharts']`, or the list under `@liferay/frontend-js-dependencies-web` (`react`, `react-dom`, and so on).

During a standard build, the linker plugin (`modules/frontend-sdk/node-scripts/util/esbuild/plugins/getLinkerPlugin.mjs`) turns every import of such a package into an esbuild external, or an import bridge, that the browser resolves at runtime to the providing project's `__liferay__/exports/<package>.(HASH).js` bundle. So:

- **A shared package**, one listed in `imports`, exists once at runtime: the copy its provider bundles. The linker matches imports by package name alone, both in a project's own code and inside every dependency it bundles. So every importer in a standard build gets the provider's version at runtime, whatever range it or its dependency asked for. A second version of a shared package in `yarn.lock` means some dependency asked for a range the provider's copy does not satisfy, and a standard-build project that bundles that dependency still gets the provider's version. Custom builds bundle the copy they resolve, and tooling never ships it.
- **Any other package** is copied into every bundle that imports it. Two versions in `yarn.lock` only matter at runtime when a single bundle ends up with both, and a custom build only ever affects itself.

## Checking What a Bundle Contains

Do not infer runtime effects from `yarn.lock`. Build the project and read its source maps (`*.js.map` under `build/node/packageRunBuild`). Each `sources` entry is a file path, so its `node_modules/<package>/` segment, nested ones included (`node_modules/a/node_modules/b/`), tells which installed copy went into that bundle. A package that appears through two different `node_modules` paths in the same source map is duplicated at runtime. Comparing the source maps from builds before and after a change shows which packages a bundle actually picked up. `~/.local/bin/gradle jar` builds a single project, including the Sass that a plain `yarn build` expects to be compiled already.

For how node-scripts itself is organized, see `modules/frontend-sdk/node-scripts/AGENTS.md`.