# Liferay Gradle Plugins Node Change Log

## 8.1.2 - 2025-03-17

### Commits
- [LPD-51417] - Set yarn version appropriately (6683777b19)

## 8.1.1 - 2024-11-05

### Commits
- [LPD-40537] Fix symlinks (2f1f8bee7d)

## 8.0.8 - 2024-10-28

### Commits
- [LRCI-4827] TCP timeout for network requests (1039e25750)

## 8.0.7 - 2024-06-14

### Commits
- [LPD-28512] gradle-plugins-node: check for null in case this is run outside of
the liferay-portal repo (8b91734359)

## 8.0.5 - 2024-05-24

### Commits
- [LPD-24865] Input (_node-scripts version) (4a0a556cf9)

## 8.0.4 - 2024-05-10

### Commits
- [LPD-23882] Update node plugin to detect new build (87bb92c35d)

## 8.0.3 - 2024-04-26

### Commits
- [LPD-22341] Update configuration for ES Module (c5b3bb7f8a)
- [LPD-15162] Pin version (f636f23295)
- [LPD-15162] Move tests (f23b048eff)
- [LPD-15162] Update README.markdown (5e6c766cd3)
- [LPD-15162] Update plugins Gradle version (7757c65773)

### Dependencies
- [LPD-15162] Update the com.liferay.gradle.util dependency to version 1.0.49.

## 8.0.2 - 2023-08-09

### Commits
- [LPS-181508] Fix packageRunBuild (a9d1dd96c9)

## 8.0.1 - 2023-08-04

### Commits
- [LPS-181508] Node publishing (b894947b6b)
- [LPS-181508] Update README.markdown (c9f00ca654)
- [LPS-181508] Update plugins Gradle version (60571c128e)
- [LPS-181508] Implicit dependencies (node) (2c17d61979)
- [LPS-181508] Annotations (beddb5d22a)
- [LPS-181508] Hash (dc3228a8f7)
- [LPS-181508] Versions (0362751ca7)
- [LPS-181508] Fix tests (05eb9088d3)
- [LPS-181508] Copy (f6124575e5)
- [LPS-181508] Auto SF (runtime) (5e7a1385d1)
- [LPS-181508] Runtime (203d66906c)
- [LPS-181508] Runtime (7b88c6ea99)
- [LPS-181508] Apply java-library (d3c0c00f02)
- [LPS-150857] Update README.markdown (77ff7e244f)
- [LPS-150857] Update plugins Gradle version (4c389b37ce)

### Dependencies
- [LPS-181508] Update the com.liferay.gradle.util dependency to version 1.0.48.
- [LPS-181508] Update the com.liferay.gradle.util dependency to version 1.0.47.

## 8.0.0 - 2022-07-15

### Dependencies
- [LPS-51081] Update the com.liferay.gradle.util dependency to version 1.0.47.

## 7.2.20 - 2022-07-15

### Commits
- [LPS-51081] Apply (52ec5f96e8)
- [LPS-51081] Rename package (9d145e2f47)

## 7.2.19 - 2022-05-19

### Commits
- [LPS-148304] Optional annotation (see
https://github.com/gradle/gradle/issues/2016) (898ff2e76c)

## 7.2.18 - 2022-05-17

### Commits
- [LPS-148304] Source formatting (4fb7c59d38)
- [LPS-148304] Update ExecuteNodeTask.java (788701e0d4)
- [LPS-148304] Update ExecutePackageManagerTask.java (6129b00477)

## 7.2.17 - 2022-05-14

### Commits
- [LPS-148304] Use Yarn's internal cache check (b3d7b0c0f9)
- [LPS-148304] Update annotation (tasks that extends this class will need to add
inputs as needed, as an example see PackageRunBuildTask) (c379274f8a)
- [LPS-148304] Add missing import (5d291710a4)
- [LPS-148304] Edit annotation (d133edc943)
- [LPS-148304] Add annotation (0ecfc483d0)
- [LPS-148304] Add annotations to ExecuteNodeTask (536af04b92)
- [LPS-148304] Add annotations to ExecutePackageManagerTask (e8ce449310)
- [LPS-148304] Add annotations to ExecuteNodeScriptTask (16fd256457)
- [LPS-150378] SF, inline (15a4ebdc1f)

## 7.2.16 - 2022-03-22

### Commits
- [LPS-149634] as used (f59385be2b)
- [LPS-149634] node osx arm builds are only available for node 16+ so download
fails for node < 15 (38a2445b3d)

## 7.2.15 - 2022-02-04

### Commits
- [LPS-146791] sort (e3c717f8ef)
- [LPS-146791] - Add --ignore-engines flag to yarnInstall task (06db76a25e)

## 7.2.14 - 2022-01-03

### Commits
- [LPS-144756] yarn/npm now requires access token to publish (5f10d18aa9)

## 7.2.13 - 2022-01-03

### Commits
- [LPS-144575] Set publish registry url (bac2e5160e)

## 7.2.12 - 2022-01-03

### Commits
- [LPS-144575] Revert "LPS-144575 Set publish registry url" (1ec9c28a31)

### Dependencies
- [LRCI-2670] Update the com.liferay.gradle.util dependency to version 1.0.46.

## 7.2.11 - 2021-12-29

### Commits
- [LPS-144575] Set publish registry url (7b0a1c7c46)

## 7.2.10 - 2021-12-02

### Commits
- [LPS-143280] use arm arch (81c79fa7db)

### Dependencies
- [LPS-143280] Update the com.liferay.gradle.util dependency to version 1.0.45.

## 7.2.9 - 2021-11-17

### Commits
- [LPS-142100] [LPS-141250] revert (eee87baefd)

## 7.2.8 - 2021-11-17

### Commits
- [LPS-105380] Remove final from parameters (a4e6ca0985)
- [LPS-105380] Remove final from variables (5a4e0b26b4)
- [LPS-105380] Revert "LPS-105380 Remove final from variables" (cdda76fc8e)
- [LPS-105380] Revert "LPS-105380 Remove final from parameters" (b2f9eed2c2)
- [LPS-105380] Remove final from parameters (f615ff77ff)
- [LPS-105380] Remove final from variables (ffe6186e71)
- [LPS-105380] Merge consecutive if-statements (1ec70acda6)
- [LPS-105380] Increase performance by moving variable declaration after throw
statement (444d3bb171)

## 7.2.7 - 2021-05-24

### Commits
- [LPS-106149] Use Gradle incremental cache check (97365d9a4c)
- [LPS-130505] Revert "LPS-130505 SF, no need to call methods" (a442601c7a)
- [LPS-130505] Revert "LPS-130505 SF, no need to call methods" (5bbb659cb8)
- [LPS-130505] SF, no need to call methods (18d35dc2bb)
- [LPS-130505] SF, no need to call methods (635298fad3)

## 7.2.6 - 2021-03-29

### Commits
- [LPS-129858] Add excludes for package.json build outputs (f7d4593da4)
- [LPS-127478] Document yarnUrl extension in NodePlugin (a68a22b757)
- [LPS-105380] Inline (e47639f436)

## 7.2.5 - 2020-11-02

### Commits
- [LPS-111291] Import statements (d414bad0fa)
- [LPS-111291] Update readme (a87d2593e9)
- [LPS-111291] Gradle 5.6.4 tests (40f4f9e2f3)
- [LPS-111291] Update README.markdown (eea12b7f8f)
- [LPS-111291] Update plugins Gradle version (003c3832b0)

### Dependencies
- [LPS-111291] Update the com.liferay.gradle.util dependency to version 1.0.44.

## 7.2.4 - 2020-10-13

### Commits
- [LPS-105380] Revert "LPS-105380 Remove static" (51f026ea07)
- [LPS-105380] Revert "LPS-105380 Sort" (6b61f951ee)
- [LPS-105380] Sort (9769746a1e)
- [LPS-105380] Remove static (05f9037b31)

## 7.2.3 - 2020-10-02

### Commits
- [LPS-105380] Lock the class instead of the object (ac7dc533ff)

## 7.2.2 - 2020-10-01

### Commits
- [LPS-121567] Fix logic (daaaee90b9)
- [LPS-121567] Teach Gradle NodePlugin about new @liferay/npm-scripts
(3c026b7688)
- [LPS-105380] Methods don't need to be static (c42da838cc)

## 7.2.1 - 2020-08-17

### Commits
- [LPS-100168] Set default package manager to npm (ecfaf97f9b)

## 7.1.3 - 2020-07-21

### Commits
- [LPS-105873] Fix publishPlugins (b02ceab8c7)
- [LPS-105873] Apply (6539d8d782)
- [LPS-105873] Simplify (a6ab6c3589)
- [LPS-105873] Move plugin (1bfa9788e5)
- [LPS-105873] Fix publish task (82ceeca684)
- [LPS-105380] SF, inline (e11238b1a3)

## 7.1.2 - 2020-07-09

### Commits
- [LPS-116808] Move to Node extension (d1f6090cbc)
- [LPS-116808] Task creation (a7b3e822f1)
- [LPS-116808] Run downloadNode task (ec6840c082)
- [LPS-116808] Remove unneeded property (610b35d70c)
- [LPS-116808] Follow existing pattern (fc8925519e)
- [LPS-116808] return null for scriptFile is node download is false (21feae3275)

## 7.0.6 - 2020-07-07

### Commits
- [LPS-105873] Move to downloadNode (6e232aa56b)
- [LPS-105873] Copy npm logic (90c25c6d16)
- [LPS-105873] download yarn if it is not available (1e7347c00a)
- [LPS-105380] Change to throw Exception, since that's what the calling method
is doing, so throwing *Exception is not adding value (216100ecdd)

### Dependencies
- [LPS-115020] Update the com.liferay.gradle.util dependency to version 1.0.43.
- [LPS-88645] Update the com.liferay.gradle.util dependency to version 1.0.42.
- [LPS-88645] Update the com.liferay.gradle.util dependency to version 1.0.41.
- [LPS-113624] Update the com.liferay.gradle.util dependency to version 1.0.40.

## 7.0.5 - 2020-04-28

### Commits
- [LPS-110422] Apply (9a704b4ca9)
- [LPS-110422] Gradle plugins not compatible with gradle 6.0+ * Add OsgiHelper
in gradle-util * Replace gradle osgi plugin dependency by local OsgiHelper
(53e90cb8f3)

### Dependencies
- [LPS-110422] Update the com.liferay.gradle.util dependency to version 1.0.39.
- [LPS-111896] Update the com.liferay.gradle.util dependency to version 1.0.38.
- [LPS-88645] Update the com.liferay.gradle.util dependency to version 1.0.37.

## 7.0.4 - 2020-03-30

### Commits
- [LPS-111192] Property to add the offline flag for Yarn commands (8a9422bd06)

## 7.0.3 - 2020-03-17

### Commits
- [LPS-110486] The package-lock.json file is used to identify NPM projects
(56e0254b9a)

## 7.0.2 - 2020-03-13

### Dependencies
- [LPS-110283] Update the com.liferay.gradle.util dependency to version 1.0.36.

## 7.0.1 - 2020-03-04

### Commits
- [LPS-106149] Baseline (becb322fa3)
- [LPS-106149] Cacheable tasks (5f1911b5ba)
- [LPS-106149] Remove classes dir check for packageRunBuild (d53d5fd9a1)
- [LPS-106149] Enable build cache for packageRunBuild (ca484c23ca)
- [LPS-105380] Rename exception variables (b3173da81b)
- [LPS-100515] Update README.markdown (694b3791de)
- [LPS-100515] Update plugins Gradle version (448efac158)
- [LPS-104132] Apply it everywhere (02f1d1d169)

### Dependencies
- [LPS-106149] Update the com.liferay.gradle.util dependency to version 1.0.35.

## 6.0.2 - 2019-10-23

### Commits
- [LPS-103580] Update method name (190ba2834f)
- [LPS-103580] Enable incremental cache for only Yarn projects (42bf3330ff)
- [LPS-103580] Update input to check the Yarn project's node_modules directory
(feecab8bef)

## 6.0.1 - 2019-10-21

### Commits
- [LPS-102367] The scriptFile property must check useNpm (52c2c7f703)
- [LPS-102367] Move reusable logic to NodePluginUtil (5ba5c9babb)

## 5.1.3 - 2019-10-16

### Commits
- [LPS-102367] Just check the major version (7fb17bd806)
- [LPS-102367] Check for liferay-npm-scripts version 12 (3d847962de)
- [LPS-102367] Add node_modules directory to packageRunBuild inputs (2f472bbe0c)
- [LPS-102367] Add system property to turn off the cache (b1c4a24b6e)
- [LPS-102367] Add incremental check (f9dbe64130)
- [LPS-102367] Rerun the jar task (12beba1b06)
- [LPS-102367] Run packageRunBuild before processResources if
liferay-npm-scripts version is greater than 10 (2f12ed7710)
- [LPS-102367] Source formatting (f65072896c)
- [LPS-102367] Add scriptFile property to NodeExtension (1af32195ad)
- [LPS-102367] Remove stale digest check (8dc60dd4ae)

## 5.1.2 - 2019-09-19

### Commits
- [LPS-101470] Fix typo (58c95da818)

## 5.1.1 - 2019-09-18

### Commits
- [LPS-101470] Source formatting (4f1067a006)
- [LPS-101470] Move digest inputs into a separate method (a60750e64b)
- [LPS-101470] Rename method (1595244c98)
- [LPS-101470] Inline (a9d1f7bf97)
- [LPS-101470] Avoid swallowing exception (57a0c91c07)
- [LPS-101470] Filter out null values (npmUrl is an optional input) (e3f24d6f82)
- [LPS-101470] Use logic from DigestUtil (218458d57f)
- [LPS-101470] Consolidate (edfba9e995)
- [LPS-101470] Move existing method to DigestUtil (cae9d33aca)
- [LPS-101470] Move logic to DigestUtil (d74c04d5f6)
- [LPS-101470] After changing nodeVersion, the version of node is not correctly
changed (c0d952fb13)

## 5.0.1 - 2019-09-16

### Commits
- [LRQA-52072] Add property to ignore failures for packageRunTest (61b712b72c)
- [LRQA-52072] Add PackageRunTestTask (no logic changes) (3e5b79d009)

## 4.9.1 - 2019-08-28

### Commits
- [LPS-100163] Remove Gradle task packageLinks (e17f3ba33a)

## 4.8.1 - 2019-08-24

### Commits
- [LPS-100168] Fix gradleTest (55ca3f97c1)
- [LPS-100168] Update tasks that extend ExecutePackageManagerTask (381329db65)
- [LPS-100168] Use new property (bcefa7a3b1)
- [LPS-100168] Add useNpm to NodeExtension (0a22995cf5)

## 4.7.2 - 2019-08-21

### Commits
- [LPS-100168] Add LiferayYarnPlugin (6620b72135)
- [LPS-100168] Move Yarn logic to gradle-plugins-node (ce1ab89ae2)

## 4.7.1 - 2019-08-19

### Commits
- [LPS-99977] Remove package.json link tasks from the build group (bd900159af)
- [LPS-99977] Create digest file for just the build script in package.json
(dba8424c41)
- [LPS-99977] Add SuppressWarnings (4dcd2a1c1f)
- [LPS-99977] Rename NpmRunTask to PackageRunTask (5731f4c5ea)
- [LPS-99977] Rename NpmLinkTask to PackageLinkTask (d01483ea92)
- [LPS-99977] Rename BaseNpmCommandTask to ExecutePackageManagerDigestTask
(4a38fdb2a3)
- [LPS-99977] Rename ExecuteNpmTask to ExecutePackageManagerTask (e47fedfc9e)

## 4.6.20 - 2019-08-14

### Commits
- [LPS-99774] Expose constant (961034d5fb)

## 4.6.18 - 2019-06-21

### Commits
- [LPS-96247] Update (7385039e86)
- [LPS-96247] Source formatting (aae01ea1d4)
- [LPS-96247] Migrate away from deprecated SourceSetOutput.getClassesDir
(5b0f9860d5)

### Dependencies
- [LPS-96247] Update the com.liferay.gradle.util dependency to version 1.0.34.

## 4.6.17 - 2019-06-10

### Commits
- [LPS-93220] Logging (dbf8127026)
- [LPS-93220] Check the cli directory too (fix for babel) (eb24ea1ac7)
- [LPS-93220] Create deleted NPM symbolic links (cdd077348b)
- [LPS-93220] Abstract out reusable logic (no logic changes) (22aba734ba)
- [LPS-96376] Update to liferay-npm-scripts v2.1.0 (prettier) (7930ab3625)
- [LPS-0] SF. Space character correction for build scripts & READMEs
(9dd5d12c9a)
- [LPS-88909] Revert "LPS-88909 Disable cache (see
https://github.com/liferay/liferay-portal/commit/fd8763c#diff-082f1d3a132d6a95e4d04c1b3ee8ccedR155)"
(c0cc8d0404)

## 4.6.16 - 2019-05-24

### Commits
- [LPS-88909] Disable cache (see
https://github.com/liferay/liferay-portal/commit/fd8763c#diff-082f1d3a132d6a95e4d04c1b3ee8ccedR155)
(21e1e8f960)

## 4.6.15 - 2019-05-06

### Commits
- [LPS-94947] Use Collections#emptyList for performance (46ca71a49d)
- [LPS-94947] deleteAllActions is deprecated and removed in Gradle 5.x
(b456fce82d)

## 4.6.14 - 2019-05-06

### Commits
- [LPS-91967] Ignore nodejs.npm.args for Yarn (0ef0891f12)

## 4.6.13 - 2019-05-01

### Commits
- [LPS-91967] Update the digest path so its the same for both NPM and Yarn
(22071074bc)

## 4.6.12 - 2019-04-25

### Commits
- [LPS-77425] Update the npmrc file location for Yarn (8afaa2938b)

## 4.6.11 - 2019-04-11

### Commits
- [LPS-91967] Configure DownloadNodeModuleTask for Yarn (408ce16abc)

## 4.6.10 - 2019-04-10

### Commits
- [LPS-91967] Configure DownloadNodeModuleTask for Yarn (f59ecd4e7d)

## 4.6.9 - 2019-04-03

### Commits
- [LPS-93258] Enable the production flag for Yarn (9d1aeab7bc)
- [LPS-93258] Enable the registry flag for Yarn (91c438e2c4)
- [LPS-91967] Skip node_modules_cache directory (43a618c849)

## 4.6.8 - 2019-03-20

### Commits
- [LPS-91967] Move reusable logic into FileUtil (f10dc01f04)
- [LPS-91967] Add ability to override the default NPM command (d8f29b10ea)
- [LPS-91967] Add ability to override the default Node modules directory
(a9d4a2ef6b)
- [LPS-91967] Check script file name before applying NPM arguments (133df2f1d2)

## 4.6.7 - 2019-02-20

### Commits
- [LPS-90945] Remove org.apache.commons dependency (7dff9f303d)

## 4.6.6 - 2019-02-04

### Commits
- [LPS-89916] Add ability to filter npmLinkTasks (0f43909b73)
- [LPS-89916] Add npmLink tasks (7f2c72c770)
- [LPS-89916] Move common logic to base class (744281bde9)

## 4.6.5 - 2019-01-24

### Commits
- [LPS-89436] Add Soy files to inputs (d0862c127a)
- [LPS-89436] Sort alphabetically (no logic changes) (7fa06d88f9)
- [LPS-89369] Delete the digest to avoid skipping the task (f877c8e96b)
- [LPS-89369] Catch Soy compile errors (31311019ea)

### Description
- [LPS-89369] The `npmRun` task should fail when there are Soy compile errors.
- [LPS-89436] Add Soy files as inputs for tasks of type `NpmRunTask`.

## 4.6.4 - 2019-01-16

### Commits
- [LPS-88909] Include all files that end with the js extension (16443339ad)
- [LPS-88909] Prevent processResources from overwriting transpiled files
(294d4807e3)

### Description
- [LPS-88909] If the `npmRun.sourceDigestFile` matches the `npmRun.sourceFiles`
digest, all files ending with the `js` extension in the classes directory will
be copied before the `processResources` task runs and then copied back to
preserve changes made by the `npmRun` task.

## 4.6.3 - 2019-01-14

### Commits
- [LPS-89126] Update outputs for the npmInstall task (34504e3e82)

### Description
- [LPS-89126] Fix failures in parallel builds by updating the outputs for tasks
of type `NpmInstallTask` to check the `nodeModulesDir` instead of the
`nodeModulesDigestFile`.

## 4.6.2 - 2019-01-09

### Commits
- [LPS-87479] Check the classes directory for Java projects (6e5549beb9)

### Description
- [LPS-87479] Set the up-to-date check for `npmRun` tasks to `true` if the
classes directory does not exist for Java projects.

## 4.6.1 - 2019-01-09

### Commits
- [LPS-88909] Prevent the processResources task from overwriting transpiled
Javascript files (4f6e6bbbb1)
- [LPS-88909] Use NpmRunTask (no logic changes) (1e238ce693)

### Description
- [LPS-88909] The `processResources` task will skip overwriting files ending
with `.es.js` if the `npmRun.sourceDigestFile` matches the `npmRun.sourceFiles`
digest to preserve changes made by the `npmRun` task.

## 4.6.0 - 2019-01-07

### Description
- [LPS-87479] Update the [Liferay Gradle Util] dependency to version 1.0.33.
- [LPS-87479] Add inputs and outputs for tasks of type `NpmRunTask` to skip
rerunning the task if none of the inputs or outputs have changed.
- [LPS-87479] Update the inputs for tasks of type `NpmInstallTask` to use the
`nodeModulesDigestFile` instead of the `node_modules` directory as an input.

## 4.5.2 - 2019-01-07

### Commits
- [LPS-87479] Create the directory if the script deletes it (c679cf96b5)
- [LPS-87479] Remove redundant input checks (bb6300fcb5)
- [LPS-87479] Update default includes (add *.json and *.jsx) (02322a1475)
- [LPS-87479] Improve the up-to-date check for npmInstall (ec0fadeb00)
- [LPS-87479] Update the npmInstall digest check (no logic changes) (b515e04aa8)
- [LPS-87479] Set the up-to-date check to false if the classes directory does
not exist (8625065553)
- [LPS-87479] Sort attributes alphabetically (5bfe771d93)
- [LPS-87479] Move logic to StringUtil (aba3a15477)
- [LPS-87479] Make the task name logic more generic (fcb1764705)
- [LPS-87479] Update scriptName property for consistency with other gradle
plugins (fffd355d2a)
- [LPS-87479] Check package.json files and Node.js version to improve
performance (8c2c3f0809)
- [LPS-87479] Make the source files optional (5e7aa83c87)
- [LPS-87479] Create a digest from the sources (3494f40166)
- [LPS-87479] Update sources (e19724e6bc)
- [LPS-87479] Add option for includes and excludes (c6c84041dd)
- [LPS-87479] camelCase npmRun task names (e8cb9d43d6)
- [LPS-87479] create NpmRunTask and set inputs and outputs so Gradle can check
to see if task is uptodate (e4ad8d8111)
- [LPS-85609] Update readme (c182ff396d)
- [LPS-85609] Simplify gradleTest (a8b0feff31)
- [LPS-85609] Use Gradle 4.10.2 (9aa90f8961)

## 4.5.1 - 2018-11-19

### Dependencies
- [LPS-87466] Update the com.liferay.gradle.util dependency to version 1.0.33.

## 4.5.0 - 2018-11-16

### Description
- [LPS-87465] Add the property `production` to all tasks that extend
`ExecuteNodeTask`. If `true`, [`devDependencies`](https://docs.npmjs.com/files/package.json#devdependencies)
are not installed when running `npm install` without any arguments and sets
`NODE_ENV=production` for lifecycle scripts.

## 4.4.5 - 2018-11-16

### Commits
- [LPS-87465] Update readme (1ad0bbf3f1)
- [LPS-87465] Move production property to the executeNPM task (f0e2d2ea29)
- [LPS-87465] The executeNPM task has a registry property (b41de3e7d9)
- [LPS-87465] set production and registry flags for npmIstall (e32aa79975)

## 4.4.4 - 2018-11-16

### Commits
- [LPS-87192] Set the Eclipse task property gradleVersion (040b2abdee)
- [LPS-87192] Add variable gradleVersion (no logic changes) (2f7c0b2fe4)
- [LPS-85609] Fix for CI (test only 4.10.2) (4eed005731)
- [LPS-85609] Test plugins up to Gradle 4.10.2 (60905bc960)
- [LPS-85609] Update supported Gradle versions (d79b89682b)
- [LPS-86589] Update readme (4280a3d596)
- [LPS-86589] Test Gradle plugins from Gradle 2.14.1 to 3.5.1 (6df521a506)

### Dependencies
- [LPS-87466] Update the com.liferay.gradle.util dependency to version 1.0.32.

## 4.4.3 - 2018-10-22

### Commits
- [LPS-86576] Simplify gradle tests (use Node.js 5.5.0) (3cfaed9d50)
- [LPS-86576] Fix downloadNode task for Windows (Node.js versions 5.5.0-6.2.0)
(051ea17262)
- [LPS-84119] SF, declare when used (efa63307f1)

### Description
- [LPS-86576] Node.js provides Windows binaries bundled with NPM and Node.js
beginning from version 6.2.1. Download and install Node.js and NPM separately
for Node.js versions 5.5.0 - 6.2.0.

## 4.4.2 - 2018-10-09

### Commits
- [LPS-85959] Update readme (9ad882d597)
- [LPS-85959] Update log message (a6c882d669)
- [LPS-85959] Use 'npm cache verify' instead (e14ed0a697)

### Description
- [LPS-85959] Verify the NPM cached data before retrying `npm install`.

## 4.4.1 - 2018-10-03

### Commits
- [LPS-85959] Update readme (35e60010f4)
- [LPS-85959] Delete the npm cached data before retrying npm install
(f525119df2)
- [LPS-71117] Test plugins with Gradle up to 3.5.1 (c3e12d1cf3)
- [LPS-71117] Update supported Gradle versions in READMEs (fdcc16c0d4)
- [LPS-67658] Fix gradleTest for windows (6386e70e90)
- [LPS-82568] Fix readme (8b71557f78)
- [LPS-82568] Update readme (7af903d2a7)

### Dependencies
- [LPS-84094] Update the com.liferay.gradle.util dependency to version 1.0.31.
- [LPS-84094] Update the com.liferay.gradle.util dependency to version 1.0.30.

### Description
- [LPS-85959] Delete the NPM cached data before retrying `npm install`.

## 4.4.0 - 2018-06-22

### Description
- [LPS-82568] Add the property `environment` to all tasks that extend
`ExecuteNodeTask`. This provides a way to set environment variables.

## 4.3.6 - 2018-06-22

### Commits
- [LPS-82568] Log environment variables (7c14fb2e9c)
- [LPS-82568] Allow to pass environment variables to Node and NPM (7644469a3c)

## 4.3.5 - 2018-06-08

### Commits
- [LPS-82310] Recreate "npm" symbolic link (e4e51be325)

### Description
- [LPS-82130] Fix the broken `npm` symbolic link.

## 4.3.4 - 2018-05-07

### Commits
- [LPS-75530] outputs for npmInstall (395da5841e)
- [LPS-78741] Readme fixes (a865c2bcf4)

### Description
- [LPS-75530] Define task inputs and outputs for `NpmInstallTask`.

## 4.3.3 - 2018-04-05

### Commits
- [LPS-78741] Update readme (374aa74891)
- [LPS-78741] Use 'npm ci' only if the 'pacakge-lock.json' file exists
(df2cc0420e)

### Description
- [LPS-78741] Fix the `npmPackageLock` task execution when the `npmInstall`
task's `useNpmCI` property is set to `true`.

## 4.3.2 - 2018-03-30

### Commits
- [LPS-78741] Update readme (853eaa597d)
- [LPS-78741] Check digest before running 'npm ci' (21195cb57b)

### Description
- [LPS-78741] Do not run `npm ci` if the `nodeModulesDigestFile` matches the
`node_modules` directory's digest.

## 4.3.1 - 2018-03-22

### Commits
- [LPS-78741] Update readme (570b6170b2)
- [LPS-78741] Skip running 'npm install' if the saved digest matches with the
new digest (7d67225c0f)

### Description
- [LPS-78741] Do not run `npm install` if the `nodeModulesDigestFile` matches
the `node_modules` directory's digest.

## 4.3.0 - 2018-03-15

### Description
- [LPS-78741] Add the property `useNpmCI` to the `NpmInstallTask`. If `true`,
run `npm ci` instead of `npm install`.
- [LPS-73472] Allow single `"bin"` values in the `package.json` files.

## 4.2.1 - 2018-03-15

### Commits
- [LPS-73472] Allow single "bin" values in package.json (5104167c2f)
- [LPS-78741] Update readme (217b0808d3)
- [LPS-78741] Simplify (the default value is false) (98ee03c627)
- [LPS-78741] Add boolean argument to run `npm ci` (15e543b9b7)
- [LPS-77425] Partial revert of d25f48516a9ad080bcbd50e228979853d3f2dda5
(60d3a950d6)
- [LPS-77425] Increment all major versions (d25f48516a)

### Dependencies
- [LPS-77425] Update the com.liferay.gradle.util dependency to version 1.0.29.

## 4.2.0 - 2018-02-13

### Commits
- [LPS-77996] Reuse path instance (4b05326b96)
- [LPS-77996] Delete the node_modules dir if there's no digest (ab53f71c97)
- [LPS-77996] By forcing "reset" to true, we'll remove the node_modules dir
(eafb87484f)
- [LPS-77996] This method doesn't change any file outside the project dir
(dbbd8c5e5b)
- [LPS-77996] Let's enable this new feature only in our modules (f9806a4fbb)
- [LPS-77996] Fix readme (3bec8177c9)
- [LPS-77996] Use project.delete (b1ccdbca00)

### Description
- [LPS-77996] Add the property `nodeModulesDigestFile`. If this property is
set, the digest is compared with the `node_modules` directory's digest. If they
don't match, the `node_modules` directory is deleted before running
`npm install`.

## 4.1.1 - 2018-02-13

### Commits
- [LPS-77996] Update readme (f9bf327f34)
- [LPS-77996] Set default value for nodeModulesDigestFile (b84d26b430)
- [LPS-77996] (f8f017d085)

## 4.1.0 - 2018-02-08

### Description
- [LPS-69802] Add the task `npmPackageLock` to delete the NPM files and run
`npm install` to install the dependencies declared in the project's
`package.json` file, if present.

## 4.0.3 - 2018-02-08

### Commits
- [LPS-69802] Remove wrong task dependency (95756ed799)
- [LPS-69802] Update readme (806f766578)
- [LPS-69802] Add 'npmPackageLock' task (cfd5235b60)
- [LRDOCS-4129] Fix Gradle plugin README links (4592b9f829)

## 4.0.2 - 2018-01-17

### Commits
- [LPS-76644] Enable Gradle plugins publishing (8bfdfd53d7)

## 4.0.1 - 2018-01-02

### Commits
- [LPS-74904] Fail the build if all the retries failed (7794f42571)
- [LPS-76644] Add description to Gradle plugins (5cb7b30e6f)

### Description
- [LPS-74904] Fail the build if all retries configured in the
`npmInstallRetries` property of an `ExecuteNodeTask` instance have been
exhausted.

## 4.0.0 - 2017-11-20

### Description
- [LPS-75965] Download the Node.js Windows distribution if running on Windows.
- [LPS-75965] The `downloadNode.nodeExeUrl` and `node.nodeExeUrl` properties
are no longer available.

## 3.2.2 - 2017-11-20

### Commits
- [LPS-75965] Fix readme (24b5d5b9b0)
- [LPS-75965] Update readme (757d7f9e79)
- [LPS-75965] Download and use the Windows Node.js archive (ccbfd853dd)

## 3.2.1 - 2017-10-10

### Commits
- [LPS-75175] Fix circular task dependency (f2aed246eb)
- [LPS-75175] Add Gradle test (1111a0b28c)
- [LPS-74770] Additional wordsmithing for the Node Gradle Plugin's README
(e51c56cabf)
- [LPS-74770] In some programming languages, you need if/then. In English, you
often do not. (4f6b075e4c)

### Description
- [LPS-75175] Fix the `downloadNode` task's circular dependency when setting
the `node.global` property to `true` in the root project.

## 3.2.0 - 2017-09-28

### Commits
- [LPS-74933] Update readme (ef3c37eba4)

### Description
- [LPS-74933] Add the ability to merge the existing `package.json` of the
project with the values provided by the task properties of
`PublishNodeModuleTask` when publishing a package to the NPM registry.

## 3.1.2 - 2017-09-28

### Commits
- [LPS-74933] Merge package.json from the root dir when publishing (1deb5279b2)
- [LPS-74770] Update readme (f186fe58cc)

## 3.1.1 - 2017-09-18

### Commits
- [LPS-74770] Add Gradle test (3213d30bf3)
- [LPS-74770] Run "npm test" with the "check" task (b96373a714)

### Description
- [LPS-74770] Run the `"test"` script (if declared in the `package.json` file)
when executing the `check` task.

## 3.1.0 - 2017-08-29

### Commits
- [LPS-73472] Delete before creating the new symlink (58aeefca50)

### Description
- [LPS-73070] Add the ability to run `ExecuteNpmTask` instances concurrently
even when pointing to a shared NPM's cache directory, if supported.
- [LPS-73070] By default, use the current user's NPM cache directory
concurrently if running on NPM 5. Prior versions of NPM do not allow for
concurrent access to the cache directory; only one NPM invocation at a time.
- [LPS-73070] Delete the `package-lock.json` file when running the `cleanNPM`
task, if present.
- [LPS-73070] Use the `package-lock.json` file to calculate the `node_modules`
cache digest.
- [LPS-73472] Remove spurious files before recreating symbolic links in the
`.bin` directories of `node_modules`.

## 3.0.1 - 2017-08-29

### Commits
- [LPS-73070] Update readme (1f5f825868)
- [LPS-73070] Use the global cache concurrently with NPM >= 5 (4aee4b75f1)
- [LPS-73070] Reuse variable (aba7dbceca)
- [LPS-73070] Add option to use the NPM cache concurrently, if supported
(660ff1dcf7)
- [LPS-73070] Move logic to plugin class (375279bdef)
- [LPS-73070] Rename method to avoid conflict (5ec672eaf5)
- [LPS-73070] Use package-lock.json to calculate the cache digest (959f408370)
- [LPS-73070] Extract util method (9cbfd011ff)
- [LPS-73070] Remove package-lock.json when running the "cleanNPM" task
(72345a135e)

## 3.0.0 - 2017-07-17

### Commits
- [LPS-73472] Update readme (b13904c177)

### Description
- [LPS-73472] Recreate symbolic links in the `.bin` directories of
`node_modules` if the `NpmInstallTask`'s `nodeModulesCacheDir` property is set.
- [LPS-73472] Remove all deprecated methods.
- [LPS-73472] The `NpmInstallTask`'s `nodeModulesCacheRemoveBinDirs` property
is no longer available.

## 2.3.1 - 2017-07-17

### Commits
- [LPS-73472] Remove deprecated method (1840f69439)
- [LPS-73472] Remove unnecessary property, it makes sense to be always true
(82312e98b5)
- [LPS-73472] Recreate symbolic links in the ".bin" directory (a5fa38aa50)
- [LPS-73472] Delete only symlinks in ".bin" directories, keep real files
(9e23d1ad74)
- [LPS-73472] Edit Readme (837196de5c)

### Dependencies
- [LPS-73584] Update the com.liferay.gradle.util dependency to version 1.0.29.
- [LPS-73584] Update the com.liferay.gradle.util dependency to version 1.0.28.

## 2.3.0 - 2017-07-07

### Commits
- [LPS-73472] Add Gradle test (8fd83a9b11)
- [LPS-73472] Update readme (fce09c33f7)

### Description
- [LPS-73472] Add a `npmRum[script]` task for each
[script](https://docs.npmjs.com/misc/scripts) declared in the `package.json`
file.
- [LPS-73472] Run the `"build"` script (if declared in the `package.json` file)
when compiling a Java project.

## 2.2.2 - 2017-07-07

### Commits
- [LPS-73472] Run the "build" script during the Java lifecycle (632d05d339)
- [LPS-73472] Add tasks to run NPM scripts (69738178bf)
- [LPS-73472] Read the package.json file only once (f65172f069)
- [LRDOCS-3663] Gradle Node plugin does not support setting the global property
via CLI (af53e8530c)
- [LPS-72429] Use Collections.addAll() (6c448e59ea)

### Dependencies
- [LPS-72914] Update the com.liferay.gradle.util dependency to version 1.0.27.

## 2.2.1 - 2017-05-03

### Commits
- [LPS-72340] Update readme (7c61e05e8d)
- [LPS-72340] Skip "npmShrinkwrap" task if package.json does not exist
(833701abc8)

### Description
- [LPS-72340] Skip task `npmShrinkwrap` if project does not contain a
`package.json` file.

## 2.2.0 - 2017-04-25

### Description
- [LPS-72152] Add property `npmUrl` to all tasks that extend
`DownloadNodeTask`. If set, it downloads a specific version of NPM to override
the one that comes with the Node.js installation.
- [LPS-72152] Add properties `npmUrl` and `npmVersion` to the `node` extension
object. By default, `npmUrl` is equal to
`https://registry.npmjs.org/npm/-/npm-${node.npmVersion}.tgz`. These properties
let you set a specific version of NPM to download with the `downloadNode` task.

## 2.1.1 - 2017-04-25

### Commits
- [LPS-72152] Update readme (8e96c1ff44)
- [LPS-72152] Add option to use a different NPM version (569a4e4f04)

## 2.1.0 - 2017-04-11

### Commits
- [LPS-71826] Update readme (694a93eeed)

### Description
- [LPS-71826] Add the ability to set the NPM log level by setting the property
`logLevel` of `ExecuteNPMTask`.

## 2.0.3 - 2017-04-11

### Commits
- [LPS-71826] Make NPM log level configurable (d1da271de5)

## 2.0.2 - 2017-03-13

### Commits
- [LPS-71222] Always sort npm-shrinkwrap.json (e42a18b3f1)

### Description
- [LPS-71222] Always sort the generated `npm-shrinkwrap.json` files.

## 2.0.1 - 2017-03-09

### Commits
- [LPS-70634] Don't override package.json if it already exists (9b85118e41)
- [LPS-66709] Update supported Gradle versions in READMEs (06e315582b)
- [LPS-67573] Enable semantic versioning check on CI (63d7f4993f)
- [LPS-67352] Rename *JsonObject -> *JSONObject (55adf55a72)
- [LPS-67352] SF - Vars named *JSON is reserved for String. Vars named
*JSONObject is reserved for JSONObject. Vars named *JSONArray is reserved for
JSONArray (082700d388)

### Description
- [LPS-70634] Reuse the `package.json` file of a project, if it exists, while
executing the `PublishNodeModuleTask`.

## 2.0.0 - 2017-02-23

### Description
- [LPS-69920] Fix duplicated NPM arguments while retrying `npm install` in case
of failure.
- [LPS-70870] Fix Node.js download with authenticated proxies.

## 1.5.3 - 2017-02-23

### Commits
- [LPS-70870] Pass proxy authentication to Ant (used by FileUtil.get)
(0286be601e)
- [LPS-70870] Extract method (8d7d822776)
- [LPS-69920] Reset original args to avoid duplicating them at next runs
(162fa980d4)
- [LPS-69920] Update readme (ccd98edf8a)
- [LPS-69920] Expose real list of args (d8a3c28263)

## 1.5.2 - 2017-02-09

### Commits
- [LPS-69920] "npmInstall" does not have up-to-date check anymore (f47c9cae3b)
- [LPS-69920] Remove IO annotations until
https://github.com/gradle/gradle/issues/1365 is fixed (376df707c5)
- [LPS-70060] Test plugins with Gradle 3.3 (09bed59a42)

### Description
- [LPS-69920] Remove up-to-date check from all tasks that extend
`NpmInstallTask`.

## 1.5.1 - 2016-12-29

### Commits
- [LPS-69920] Update readme (882ad1d8df)
- [LPS-69920] Retry "npm install" 2 times (so run it 3 times max) (0ab6e3085a)
- [LPS-69920] Reuse argument to retry "npm install" a few times if it fails
(fb6002bbf5)
- [LPS-69802] Update Node Gradle plugin README (45abdf0dfd)

### Description
- [LPS-69920] Retry `npm install` automatically if it fails.

## 1.5.0 - 2016-12-21

### Commits
- [LPS-69802] Update readme (197e85d133)
- [LPS-66709] Add links to the NPM documentation (362862a5ab)
- [LPS-68564] Update readme (bdefd8d8e8)

### Description
- [LPS-69802] Add the task `cleanNPM` to delete the `node_modules` directory
and the `npm-shrinkwrap.json` file from the project, if present.
- [LPS-69802] Execute the `cleanNPM` task before generating the
`npm-shrinkwrap.json` file via the `npmShrinkwrap` task.

## 1.4.3 - 2016-12-21

### Commits
- [LPS-69802] Clean NPM files and dirs before running "npmShrinkwrap" task
(394ef8643a)

## 1.4.2 - 2016-12-14

### Commits
- [LPS-69677] Not needed (da7a0fac6b)
- [LPS-69677] Fix NPE and run NPM synchronously if using the global Node
(b94a58d4d5)

### Description
- [LPS-69677] Fix problem with `ExecuteNpmTask` when `node.download = false`.

## 1.4.1 - 2016-12-08

### Commits
- [LPS-69618] Disable up-to-date check for the "npmInstall" task (ce8c7d2854)
- [LPS-66709] README typo (283446e516)
- [LPS-66709] Add supported Gradle versions in READMEs (e0d9458520)

### Description
- [LPS-69618] Disable the up-to-date check for the `npmInstall` task.

## 1.4.0 - 2016-11-29

### Commits
- [LPS-69445] Update readme (e1e901c854)

### Description
- [LPS-69445] Add the `useGradleExec` property to all tasks that extend
`ExecuteNodeTask`. If `true`, Node.js is invoked via
[`project.exec`](https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:exec(org.gradle.api.Action)),
which can solve hanging problems with the Gradle Daemon.

## 1.3.1 - 2016-11-29

### Commits
- [LPS-69445] Use Gradle "exec" if we're running in a Gradle daemon (8306c4af68)
- [LPS-69445] Add option to run Node.js via a regular Gradle "exec" (bbb756b9e7)
- [LPS-69259] Test plugins with Gradle 3.2.1 (72873ed836)
- [LPS-69259] Test plugins with Gradle 3.2 (dec6105d3d)

## 1.3.0 - 2016-10-21

### Commits
- [LPS-66906] Update readme (860f57aff8)

### Description
- [LPS-66906] Add the ability to use callables and closures as a value for the
`removeShrinkwrappedUrls` property of `NpmInstallTask`.
- [LPS-66906] Set the `removeShrinkwrappedUrls` property of all tasks that
extend `NpmInstallTask` to `true` by default if the property `registry` has a
value.

## 1.2.1 - 2016-10-21

### Commits
- [LPS-66906] Deprecate old method (052b59dc21)
- [LPS-66906] By default, remove shrinkwrapped URLs if a registry is set
(a53533c32b)
- [LPS-66709] Consistency, we use "command line" in all the other READMEs
(b17f3daf06)
- [LPS-66709] Wordsmithing READMEs for consistency (a3cc8c4c6b)
- [LPS-66709] Edit gradle-plugins-node README (6843a73be9)
- [LPS-66709] Add README for gradle-plugins-node (1a4fb0a9cd)

## 1.2.0 - 2016-10-06

### Description
- [LPS-68564] Add task `npmShrinkwrap` to call `npm shrinkwrap` and exclude
unwanted dependencies from the generated `npm-shrinkwrap.json` file.

## 1.1.1 - 2016-10-06

### Commits
- [LPS-68564] Fix task description (0cdf5e142a)
- [LPS-68564] Bypass SF warning (fa64a53786)
- [LPS-68564] Use tabs in npm-shrinkwrap.json for consistency (82903e3b6c)
- [LPS-68564] Add task to call "npm shrinkwrap" and exclude dependencies
(1270dd4ca6)
- [LPS-68231] Test plugins with Gradle 3.1 (49ec4cdbd8)

## 1.1.0 - 2016-09-20

### Description
- [LPS-66906] Add property `inheritProxy` to all tasks that extend
`ExecuteNodeTask`. If `true`, the Java proxy system properties are passed to
Node.js via the environment variables `http_proxy`, `https_proxy`, and
`no_proxy`.
- [LPS-67573] Make most methods private in order to reduce API surface.
- [LPS-67573] Move utility classes to the
`com.liferay.gradle.plugins.node.internal` package.

## 1.0.23 - 2016-09-20

### Commits
- [LPS-67573] Export package (d80649bd18)
- [LPS-67573] Move internal classes to their own packages (9dd224cdb1)
- [LPS-67573] Remove unused parameter (d80a3d77ce)
- [LPS-67573] Make methods private to reduce API surface (0bf4bdb787)
- [LPS-66906] Remove unnecessary cast (25f3c8ffdf)
- [LPS-66906] Set proxy settings as environment variables (d3ad326f10)
- [LPS-67352] SF, enforce empty line after finishing referencing variable
(4ff2bb6038)
- [LPS-67352] Keep empty line between declaring a variable and using it
(ea421a5ab5)

## 1.0.22 - 2016-08-27

### Commits
- [LPS-67023] Do not download a module if it will be done by "npm install"
(711b415559)
- [LPS-67658] Convert gradle-plugins-node sample into a smoke test (d4d40c57be)
- [LPS-67658] Configure GradleTest in gradle-plugins-node (3bea1444af)
- [LPS-67658] Need "compileOnly" to keep dependencies out of "compile"
(4a3cd0bc9d)
- [LPS-67658] These plugins must work with Gradle 2.5+ (5b963e363d)

### Description
- [LPS-67023] A `DownloadNodeModuleTask` task is automatically disabled if the
following conditions are met:
	- The task is configured to download in the project's `node_modules`
	directory.
	- The project has a `package.json` file.
	- The `package.json` file contains the same module name in the
	`dependencies` or `devDependencies` object.

## 1.0.21 - 2016-08-15

### Commits
- [LPS-66906] Stop retrying if Node.js completed succesfully (490ff7c860)

## 1.0.20 - 2016-08-11

### Commits
- [LPS-66906] Rethrow the exception if copy fails (df634b2172)
- [LPS-66906] Make "npm install" retries configurable, defaults to disabled
(5b346ef74f)

## 1.0.19 - 2016-08-09

### Commits
- [LPS-66906] Automatically remove .bin dirs to bypass GRADLE-1843 (65a0d0ec03)

## 1.0.18 - 2016-08-05

### Commits
- [LPS-66906] Retry "npm install" a few times if the Node invocation fails
(d5a5d6cd1f)
- [LPS-66906] Extract method (fe7f58737c)
- [LPS-66906] No need to call "npm install" after restoring the cache
(7c336b46e7)
- [LPS-66906] Fail the Gradle build if Node.js fails (ac60817eb3)
- [LPS-66906] Add option to hide the NPM progress bar (4ec3547f9e)
- [LPS-66906] Block NPM parallel execution if we're writing on a shared dir
(c8ecdbb235)
- [LPS-66906] Add option to download Node.js only once in the root project
(cfeca18aa0)

## 1.0.17 - 2016-08-01

### Commits
- [LPS-66906] Add option to set NPM registry (43431b0ca1)
- [LPS-66906] Sync NPM and Gradle log levels (6231d7904b)
- [LPS-66906] Ensure the working dir exists before invoking Node (0b659a2c23)
- [LPS-66906] Sort (3b5a8b5672)
- [LPS-66906] Preconfigure only our main "downloadNode" task (473866dcd1)
- [LPS-66906] Add logging while calling Node (6dd9f144d7)
- [LPS-66906] Use task logger (435a728425)
- [LPS-66906] Copy proxy settings from Java system properties to NPM
(d37aef57fd)
- [LPS-66906] Add option to remove URLs from npm-shrinkwrap.json (e4b01ca92b)
- [LPS-66906] Add option to cache the node_modules dir (e66ec9e283)
- [LPS-66906] Move "npm install" task in its own class (18a3666308)

## 1.0.16 - 2016-06-16

### Dependencies
- [LPS-65749] Update the com.liferay.gradle.util dependency to version 1.0.26.

## 1.0.15 - 2016-06-09

### Commits
- [LPS-66410] Command line argument to set "node.download" property (d70ba4da2a)

## 1.0.14 - 2016-06-07

### Commits
- [LPS-66410] Add task description (f125b7b933)
- [LPS-66410] Use Process Builder to bypass GRADLE-3329 (bd94f9f191)
- [LPS-66410] Fix NPE when converting a closure that returns null (175b7a2046)
- [LPS-66410] Add option to use local Node.js installation (afc0b6440f)
- [LPS-66410] If nodeDir == null, use local Node.js installation (189e1f4b1e)
- [LPS-66410] Move NPM cache configuration into task argument (83f60d9940)
- [LPS-65810] Gradle plugins aren't used in OSGi, no need to export anything
(83cdd8ddcd)
- [LPS-64816] Update Gradle plugin samples (3331002e5d)

## 1.0.13 - 2016-04-21

### Commits
- [LPS-65245] Disable "npmInstall" if package.json contains no dependencies
(14974c8631)
- [LPS-65245] Let NPM download modules in "projectDir/node_modules" (6c9ad97c93)
- [LPS-61099] Delete build.xml in modules (c9a7e1d370)
- [LPS-63943] This is done automatically now (f1e42382d9)
- [LPS-62883] Update build.gradle of Node-based plugins (176b68ee4f)
- [LPS-62942] Explicitly list exported packages for correctness (f095a51e25)

### Dependencies
- [LPS-65086] Update the com.liferay.gradle.util dependency to version 1.0.25.

## 1.0.12 - 2016-01-28

### Commits
- [LPS-62671] Add main property to generated package.json files (1540d21d9a)
- [LPS-61848] An empty settings.gradle is enough (2e5eb90e23)

## 1.0.11 - 2016-01-26

### Commits
- [LPS-62504] Update Node version (36b93b345b)
- [LPS-62504] Allow custom working dir (c7a318bb3d)
- [LPS-62504] Use the version of NPM embedded in the Node distribution
(5d9446e14b)
- [LPS-62504] Base task to execute a Node script (6e6a1a6fed)
- [LPS-62504] Fix Node/NPM download for newer versions (e4d270947a)
- [LPS-61088] Remove classes and resources dir from Include-Resource
(1b0e1275bc)

## 1.0.10 - 2016-01-12

### Commits
- [LPS-61754] Use osgiHelper for a better default value (7af54a97ac)
- [LPS-61420] Incorrect tabs and linebreaks in /modules/sdk (955b0fba88)

## 1.0.9 - 2015-12-15

### Commits
- [LPS-61322] Add a task which runs "npm install" in the project dir
(582d1cbab1)

## 1.0.7 - 2015-11-03

### Commits
- [LPS-59564] Update directory layout for "sdk" modules (ea19635556)

### Dependencies
- [LPS-60153] Update the com.liferay.gradle.util dependency to version 1.0.23.

## 1.0.6 - 2015-09-17

### Commits
- [LPS-58655] Add option to set the bugs URL in the package.json file, otherwise
it will incorrectly point to https://github.com/liferay/liferay-portal/issues
(2629048392)
- [LPS-58655] Fix passing credentials to NPM (3e26375845)

## 1.0.5 - 2015-09-15

### Commits
- [LPS-58655] Update sample (7562f2d297)
- [LPS-58655] Convert to null if empty, so the task input validation will throw
an error (defdfdc104)
- [LPS-58655] Automatically generate the package.json file (b794bd415c)

## 1.0.4 - 2015-09-14

### Commits
- [LPS-58609] Move the NPM cache configuration as a command line argument
(8cdd47a961)
- [LPS-58609] New methods to add default NPM arguments (07fc3922b8)
- [LPS-58609] New methods to append NPM arguments (be2e990495)

## 1.0.3 - 2015-09-09

### Commits
- [LPS-58467] Override PATH so package.json scripts will use the locally
downloaded Node (b68b6ed3df)

## 1.0.1 - 2015-09-08

### Commits
- [LPS-58467] Not necessary (1b2291a9d3)
- [LPS-58260] Use the system "tar" to extract the tarball, otherwise the
symbolic links in it will be lost (0a1438b324)
- [LPS-51081] Remove modules' Eclipse project files (b3f19f9012)
- [LPS-51081] Replace modules' Ant files with Gradle alternatives (9e60160a85)
- [LPS-51081] Remove modules' Ivy files (076b384eef)

### Dependencies
- [LPS-58467] Update the com.liferay.gradle.util dependency to version 1.0.19.

[LPD-15162]: https://issues.liferay.com/browse/LPD-15162
[LPD-22341]: https://issues.liferay.com/browse/LPD-22341
[LPD-23882]: https://issues.liferay.com/browse/LPD-23882
[LPD-24865]: https://issues.liferay.com/browse/LPD-24865
[LPD-28512]: https://issues.liferay.com/browse/LPD-28512
[LPD-40537]: https://issues.liferay.com/browse/LPD-40537
[LPD-51417]: https://issues.liferay.com/browse/LPD-51417
[LPS-0]: https://issues.liferay.com/browse/LPS-0
[LPS-51081]: https://issues.liferay.com/browse/LPS-51081
[LPS-58260]: https://issues.liferay.com/browse/LPS-58260
[LPS-58467]: https://issues.liferay.com/browse/LPS-58467
[LPS-58609]: https://issues.liferay.com/browse/LPS-58609
[LPS-58655]: https://issues.liferay.com/browse/LPS-58655
[LPS-59564]: https://issues.liferay.com/browse/LPS-59564
[LPS-60153]: https://issues.liferay.com/browse/LPS-60153
[LPS-61088]: https://issues.liferay.com/browse/LPS-61088
[LPS-61099]: https://issues.liferay.com/browse/LPS-61099
[LPS-61322]: https://issues.liferay.com/browse/LPS-61322
[LPS-61420]: https://issues.liferay.com/browse/LPS-61420
[LPS-61754]: https://issues.liferay.com/browse/LPS-61754
[LPS-61848]: https://issues.liferay.com/browse/LPS-61848
[LPS-62504]: https://issues.liferay.com/browse/LPS-62504
[LPS-62671]: https://issues.liferay.com/browse/LPS-62671
[LPS-62883]: https://issues.liferay.com/browse/LPS-62883
[LPS-62942]: https://issues.liferay.com/browse/LPS-62942
[LPS-63943]: https://issues.liferay.com/browse/LPS-63943
[LPS-64816]: https://issues.liferay.com/browse/LPS-64816
[LPS-65086]: https://issues.liferay.com/browse/LPS-65086
[LPS-65245]: https://issues.liferay.com/browse/LPS-65245
[LPS-65749]: https://issues.liferay.com/browse/LPS-65749
[LPS-65810]: https://issues.liferay.com/browse/LPS-65810
[LPS-66410]: https://issues.liferay.com/browse/LPS-66410
[LPS-66709]: https://issues.liferay.com/browse/LPS-66709
[LPS-66906]: https://issues.liferay.com/browse/LPS-66906
[LPS-67023]: https://issues.liferay.com/browse/LPS-67023
[LPS-67352]: https://issues.liferay.com/browse/LPS-67352
[LPS-67573]: https://issues.liferay.com/browse/LPS-67573
[LPS-67658]: https://issues.liferay.com/browse/LPS-67658
[LPS-68231]: https://issues.liferay.com/browse/LPS-68231
[LPS-68564]: https://issues.liferay.com/browse/LPS-68564
[LPS-69259]: https://issues.liferay.com/browse/LPS-69259
[LPS-69445]: https://issues.liferay.com/browse/LPS-69445
[LPS-69618]: https://issues.liferay.com/browse/LPS-69618
[LPS-69677]: https://issues.liferay.com/browse/LPS-69677
[LPS-69802]: https://issues.liferay.com/browse/LPS-69802
[LPS-69920]: https://issues.liferay.com/browse/LPS-69920
[LPS-70060]: https://issues.liferay.com/browse/LPS-70060
[LPS-70634]: https://issues.liferay.com/browse/LPS-70634
[LPS-70870]: https://issues.liferay.com/browse/LPS-70870
[LPS-71117]: https://issues.liferay.com/browse/LPS-71117
[LPS-71222]: https://issues.liferay.com/browse/LPS-71222
[LPS-71826]: https://issues.liferay.com/browse/LPS-71826
[LPS-72152]: https://issues.liferay.com/browse/LPS-72152
[LPS-72340]: https://issues.liferay.com/browse/LPS-72340
[LPS-72429]: https://issues.liferay.com/browse/LPS-72429
[LPS-72914]: https://issues.liferay.com/browse/LPS-72914
[LPS-73070]: https://issues.liferay.com/browse/LPS-73070
[LPS-73472]: https://issues.liferay.com/browse/LPS-73472
[LPS-73584]: https://issues.liferay.com/browse/LPS-73584
[LPS-74770]: https://issues.liferay.com/browse/LPS-74770
[LPS-74904]: https://issues.liferay.com/browse/LPS-74904
[LPS-74933]: https://issues.liferay.com/browse/LPS-74933
[LPS-75175]: https://issues.liferay.com/browse/LPS-75175
[LPS-75530]: https://issues.liferay.com/browse/LPS-75530
[LPS-75965]: https://issues.liferay.com/browse/LPS-75965
[LPS-76644]: https://issues.liferay.com/browse/LPS-76644
[LPS-77425]: https://issues.liferay.com/browse/LPS-77425
[LPS-77996]: https://issues.liferay.com/browse/LPS-77996
[LPS-78741]: https://issues.liferay.com/browse/LPS-78741
[LPS-82310]: https://issues.liferay.com/browse/LPS-82310
[LPS-82568]: https://issues.liferay.com/browse/LPS-82568
[LPS-84094]: https://issues.liferay.com/browse/LPS-84094
[LPS-84119]: https://issues.liferay.com/browse/LPS-84119
[LPS-85609]: https://issues.liferay.com/browse/LPS-85609
[LPS-85959]: https://issues.liferay.com/browse/LPS-85959
[LPS-86576]: https://issues.liferay.com/browse/LPS-86576
[LPS-86589]: https://issues.liferay.com/browse/LPS-86589
[LPS-87192]: https://issues.liferay.com/browse/LPS-87192
[LPS-87465]: https://issues.liferay.com/browse/LPS-87465
[LPS-87466]: https://issues.liferay.com/browse/LPS-87466
[LPS-87479]: https://issues.liferay.com/browse/LPS-87479
[LPS-88645]: https://issues.liferay.com/browse/LPS-88645
[LPS-88909]: https://issues.liferay.com/browse/LPS-88909
[LPS-89126]: https://issues.liferay.com/browse/LPS-89126
[LPS-89369]: https://issues.liferay.com/browse/LPS-89369
[LPS-89436]: https://issues.liferay.com/browse/LPS-89436
[LPS-89916]: https://issues.liferay.com/browse/LPS-89916
[LPS-90945]: https://issues.liferay.com/browse/LPS-90945
[LPS-91967]: https://issues.liferay.com/browse/LPS-91967
[LPS-93220]: https://issues.liferay.com/browse/LPS-93220
[LPS-93258]: https://issues.liferay.com/browse/LPS-93258
[LPS-94947]: https://issues.liferay.com/browse/LPS-94947
[LPS-96247]: https://issues.liferay.com/browse/LPS-96247
[LPS-96376]: https://issues.liferay.com/browse/LPS-96376
[LPS-99774]: https://issues.liferay.com/browse/LPS-99774
[LPS-99977]: https://issues.liferay.com/browse/LPS-99977
[LPS-100163]: https://issues.liferay.com/browse/LPS-100163
[LPS-100168]: https://issues.liferay.com/browse/LPS-100168
[LPS-100515]: https://issues.liferay.com/browse/LPS-100515
[LPS-101470]: https://issues.liferay.com/browse/LPS-101470
[LPS-102367]: https://issues.liferay.com/browse/LPS-102367
[LPS-103580]: https://issues.liferay.com/browse/LPS-103580
[LPS-104132]: https://issues.liferay.com/browse/LPS-104132
[LPS-105380]: https://issues.liferay.com/browse/LPS-105380
[LPS-105873]: https://issues.liferay.com/browse/LPS-105873
[LPS-106149]: https://issues.liferay.com/browse/LPS-106149
[LPS-110283]: https://issues.liferay.com/browse/LPS-110283
[LPS-110422]: https://issues.liferay.com/browse/LPS-110422
[LPS-110486]: https://issues.liferay.com/browse/LPS-110486
[LPS-111192]: https://issues.liferay.com/browse/LPS-111192
[LPS-111291]: https://issues.liferay.com/browse/LPS-111291
[LPS-111896]: https://issues.liferay.com/browse/LPS-111896
[LPS-113624]: https://issues.liferay.com/browse/LPS-113624
[LPS-115020]: https://issues.liferay.com/browse/LPS-115020
[LPS-116808]: https://issues.liferay.com/browse/LPS-116808
[LPS-121567]: https://issues.liferay.com/browse/LPS-121567
[LPS-127478]: https://issues.liferay.com/browse/LPS-127478
[LPS-129858]: https://issues.liferay.com/browse/LPS-129858
[LPS-130505]: https://issues.liferay.com/browse/LPS-130505
[LPS-141250]: https://issues.liferay.com/browse/LPS-141250
[LPS-142100]: https://issues.liferay.com/browse/LPS-142100
[LPS-143280]: https://issues.liferay.com/browse/LPS-143280
[LPS-144575]: https://issues.liferay.com/browse/LPS-144575
[LPS-144756]: https://issues.liferay.com/browse/LPS-144756
[LPS-146791]: https://issues.liferay.com/browse/LPS-146791
[LPS-148304]: https://issues.liferay.com/browse/LPS-148304
[LPS-149634]: https://issues.liferay.com/browse/LPS-149634
[LPS-150378]: https://issues.liferay.com/browse/LPS-150378
[LPS-150857]: https://issues.liferay.com/browse/LPS-150857
[LPS-181508]: https://issues.liferay.com/browse/LPS-181508
[LRCI-2670]: https://issues.liferay.com/browse/LRCI-2670
[LRCI-4827]: https://issues.liferay.com/browse/LRCI-4827
[LRDOCS-3663]: https://issues.liferay.com/browse/LRDOCS-3663
[LRDOCS-4129]: https://issues.liferay.com/browse/LRDOCS-4129
[LRQA-52072]: https://issues.liferay.com/browse/LRQA-52072