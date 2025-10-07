# Checks for .ipynb, .json, or .npmbridgerc

Check | Category | Description
----- | -------- | -----------
JSONBatchEngineDataFileCheck | [Styling](styling_checks.md#styling-checks) | Remove elements in `*.batch-engine-data.json` files. |
JSONCommerceCatalogFileCheck | [Styling](styling_checks.md#styling-checks) | Sorts `*.options.json`, `*.products.json`, and `*.products.specifications.json` files. |
[JSONDeprecatedPackagesCheck](check/json_deprecated_packages_check.md#jsondeprecatedpackagescheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds incorrect use of deprecated packages in `package.json` files. |
JSONPackageJSONBNDVersionCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks the version for dependencies in `package.json` files. |
JSONPackageJSONCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks content of `package.json` files. |
JSONPackageJSONDependencyVersionCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks the version for dependencies in `package.json` files. |
JSONPageFileCheck | [Styling](styling_checks.md#styling-checks) | Sorts by `roleName` in `page.json` files. |
JSONReplacementsFileCheck | [Styling](styling_checks.md#styling-checks) | Sorts by `issueKey`, `from` and `to` in `replacements.json` file. |
JSONResourcePermissionsFileCheck | [Styling](styling_checks.md#styling-checks) | Sorts by `resourceName` and `roleName` in `resource-permissions.json` files. |
JSONStylingCheck | [Styling](styling_checks.md#styling-checks) | Applies rules to enforce consistency in code style. |
JSONUpgradeLiferayThemePackageJSONCheck | [Upgrade](upgrade_checks.md#upgrade-checks) | Upgrade the `package.json` of a Liferay Theme to make it compatible with Liferay 7.4. |
[JSONValidationCheck](check/json_validation_check.md#jsonvalidationcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates content of `.json` files. |