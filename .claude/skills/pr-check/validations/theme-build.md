# Theme Build

## Trigger

A shared CSS input that themes consume changed: `frontend-css-common`, `frontend-js-clay-web/clay/clay-css`, or the `frontend-theme-styled` or `frontend-theme-unstyled` parent themes. A theme's own `packageRunBuild` already runs when the theme is deployed (Per-Module Compile covers that), but a change to a shared input recompiles no theme on its own.

## Match

`^modules/apps/frontend-css/frontend-css-common/|^modules/apps/frontend-js/frontend-js-clay-web/clay/clay-css/|^modules/apps/frontend-theme/frontend-theme-(styled|unstyled)/`

## Command

A shared CSS change fans out to every theme. Select each module under `modules/apps` whose `package.json` declares a `liferayTheme` block, and convert each to a Gradle project path. Scan `modules/apps` rather than `modules/apps/frontend-theme`, or the commerce themes are missed, which take the same `styled` parent and are affected by the same change:

```bash
command grep --files-with-matches --include='package.json' --recursive '"liferayTheme"' \
	"${REPO_ROOT}/modules/apps" \
	| command grep --invert-match --regexp='/node_modules/' --regexp='/gradleTest/' \
	| sed "s#/package.json##" | sed "s#${REPO_ROOT}/modules/##" | tr '/' ':'
```

Run `packageRunBuild` (not `deploy`) per theme:

```bash
("${REPO_ROOT}/gradlew" \
	--project-dir "${REPO_ROOT}/modules" \
	:<path>:packageRunBuild)
```

The build needs `node` and an `npm`/`yarn` install and builds both parent themes once; that work amortizes across the theme set.

FAIL when a theme's build reports `BUILD FAILED`, and name the theme and the error. Selecting no theme at all is a broken scan rather than a pass, since a shared CSS change reaches every theme the modules build contains; report that as a FAIL too. PASS when every selected theme reports `BUILD SUCCESSFUL`.

## Checklist

```
- [ ] (One subitem per theme:) packageRunBuild <theme path>
```

## Time Estimate

~30 sec - 2 min per theme; the first also pays node download, install, and parent theme builds.