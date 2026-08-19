# Clay Storybook

Storybook for the Clay components, deployed to `storybook.clayui.com`.

This is a standalone project with its own lockfile, outside the yarn monorepo,
so it is installed and run on its own:

```bash
yarn
yarn run storybook
```

To build the static site:

```bash
yarn run build-storybook
```

## How It Finds the Components

The stories live next to the components they document, in
`clay/clay-*/stories`, and every `@clayui/*` package is aliased to the sources
next door in `.storybook/main.js`. The stylesheet is compiled from the Clay CSS
sources as well, so a change to a component or to its styles shows up here
without publishing or building anything first.

Because those sources sit outside this project, `.storybook/main.js` also adds
them to the babel rule and puts this project's `node_modules` on webpack's
resolution path, as a fallback rather than a priority. Dependencies of the Clay
packages are declared here too.

Three details there are load bearing, and each one breaks the preview at
runtime rather than at build time:

- `node_modules` is excluded from the babel rule. Storybook includes the whole
  project directory, which here holds this project's own dependencies, and
  compiling those makes the preset inject core-js polyfills into core-js
  itself.
- The extra `resolve.modules` entry is appended, never prepended. An absolute
  entry that comes first outranks the usual walk up the directory tree and
  hands every package the top level copy of a dependency instead of the one
  nested beside it.
- `react` and `react-dom` are pinned to this project's copies, or the Clay
  sources resolve their own React from whatever the surrounding checkout has
  installed and the preview dies on a second copy of React.