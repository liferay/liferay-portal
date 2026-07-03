---
name: release-cut
description: Use when cutting or publishing a new portal security-patch release — bumping the release version, publishing artifacts to Artifactory, or when Arena (arena-parent) needs updated portal artifacts.
---

# Release Cut (Maven Artifact Handoff)

This repo does not deploy to production. Artifacts publish to Artifactory; the Arena
project (`/opt/projects/arena-parent/4.7.x/arena-parent`) consumes them and owns the
production deploy. Full artifact list and rationale: `DESIGN.md` → Production Deployment.

**The handoff spans two repos with no automated link. The release is not done until
the version check in step 5 passes.**

## Checklist

1. **Bump versions** at the top of `scripts/deploy-maven-artifacts.sh`:
   `version=` (always) and `lexicon_version=` (only if lexicon changed).
   Convention: letter suffix increments — `7.0.6e` → `7.0.6f`.

2. **Build the bundle** into `bundles.org` (`ant deploy` / full `ant clean all`;
   the script reads from `bundles_dir=.../bundles.org`).

3. **Publish**: run `scripts/deploy-maven-artifacts.sh` — deploys OSGi zip, portal
   core jars, portal-web war, theme + lexicon webjars to
   `artifactory.axiell.com/.../ext-release-local`.

4. **Bump the consumer**: in `arena-parent/pom.xml`, set every
   `com.liferay.portal.*.version`, `com.liferay.util.*.version`, and
   `com.liferay.support.tomcat.version` property to the new version string.

5. **Verify — REQUIRED, do not skip**: script and consumer pom must agree.
   ```bash
   grep '^version=' scripts/deploy-maven-artifacts.sh
   grep -o 'com\.liferay\.[a-z.]*version>[^<]*' \
     /opt/projects/arena-parent/4.7.x/arena-parent/pom.xml | sort -u
   ```
   Any property still on the previous letter suffix means Arena will silently
   build against stale portal artifacts.

6. **Hand off**: Arena's own build/deploy (its `portal/build/pom.xml` unpacks the
   OSGi zip into `${liferay.home}`) pushes the new artifacts toward production.

## Red flags

- Declaring the release done after step 3 — publishing without the pom bump is
  the historical failure mode (documented drift: pom on `e` while script on `f`).
- Editing `build.wos.properties` for release config — use
  `build.${user.name}.properties` overrides (`AGENTS.md`).
