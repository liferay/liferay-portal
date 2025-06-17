# Checks for .gradle

Check | Category | Description
----- | -------- | -----------
GradleBlockOrderCheck | [Styling](styling_checks.md#styling-checks) | Sorts logic in gradle build files. |
GradleBodyCheck | [Styling](styling_checks.md#styling-checks) | Applies rules to enforce consistency in the body of gradle build files. |
GradleCommerceDependenciesCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks the modules that are outside of Commerce are not allowed to depend on Commerce modules. |
[GradleDependenciesCheck](check/gradle_dependencies_check.md#gradledependenciescheck) | [Performance](performance_checks.md#performance-checks) | Checks that modules are not depending on other modules. |
[GradleDependencyArtifactsCheck](check/gradle_dependency_artifacts_check.md#gradledependencyartifactscheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on dependencies artifacts. |
GradleDependencyConfigurationCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates the scope of dependencies in build gradle files. |
GradleDependencyVersionCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks the version for dependencies in gradle build files. |
GradleExportedPackageDependenciesCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates dependencies in gradle build files. |
GradleImportsCheck | [Styling](styling_checks.md#styling-checks) | Sorts and groups imports in `.gradle` files. |
GradleIndentationCheck | [Styling](styling_checks.md#styling-checks) | Finds incorrect indentation in gradle build files. |
GradleJakartaTransformCheck | [JakartaTransform](jakarta_transform_checks.md#jakartatransform-checks) | Performs replacements for use of Jakarta. |
GradleJavaVersionCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks values of properties `sourceCompatibility` and `targetCompatibility` in gradle build files. |
GradleMissingDependenciesForUpgradeJava21Check | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks missing dependencies for upgrade Java 21 in gradle build files. |
GradleMissingJarManifestTaskCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing `jarManifest` task when using `jarPatched` task in gradle build files. |
GradlePetraModuleDependenciesCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that dependencies in `petra` moudule can only contains `petra` dependencies. |
GradlePropertiesCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates property values in gradle build files. |
GradleProvidedDependenciesCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates the scope of dependencies in build gradle files. |
[GradleRequiredDependenciesCheck](check/gradle_required_dependencies_check.md#gradlerequireddependenciescheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates the dependencies in `/required-dependencies/required-dependencies/build.gradle`. |
GradleRestClientDependenciesCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates the project dependencies `.*-rest-client` can only be used for `testIntegrationImplementation`. |
GradleStylingCheck | [Styling](styling_checks.md#styling-checks) | Applies rules to enforce consistency in code style. |
[GradleTaskCreationCheck](check/gradle_task_creation_check.md#gradletaskcreationcheck) | [Styling](styling_checks.md#styling-checks) | Checks that a task is declared on a separate line before the closure. |
GradleTestDependencyVersionCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks the version for dependencies in gradle build files. |
GradleTestUtilDeployDirCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for incorrect use of `deployDir`. |
[GradleUpgradeReleaseDXPCheck](check/gradle_upgrade_release_dxp_check.md#gradleupgradereleasedxpcheck) | [Upgrade](upgrade_checks.md#upgrade-checks) | Remove and replaced dependencies in `build.gradle` that are already in `release.dxp.api` with `released.dxp.api` dependency. |
WhitespaceCheck | [Styling](styling_checks.md#styling-checks) | Finds missing and unnecessary whitespace. |