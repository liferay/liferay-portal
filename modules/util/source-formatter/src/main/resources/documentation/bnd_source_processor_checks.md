# Checks for .bnd

Check | Category | Description
----- | -------- | -----------
BNDBreakingChangeCommitMessageCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that commit message should contain the schematized breaking changes. |
[BNDBundleActivatorCheck](check/bnd_bundle_activator_check.md#bndbundleactivatorcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates property value for `Bundle-Activator`. |
[BNDBundleCheck](check/bnd_bundle_check.md#bndbundlecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates `Liferay-Releng-*` properties. |
[BNDBundleInformationCheck](check/bnd_bundle_information_check.md#bndbundleinformationcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates property values for `Bundle-Version`, `Bundle-Name` and `Bundle-SymbolicName`. |
BNDCapabilityCheck | [Styling](styling_checks.md#styling-checks) | Sorts and applies logic to fix line breaks to property values for `Provide-Capability` and `Require-Capability`. |
[BNDDefinitionKeysCheck](check/bnd_definition_keys_check.md#bnddefinitionkeyscheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates definition keys in `.bnd` files. |
[BNDDeprecatedAppBNDsCheck](check/bnd_deprecated_app_bnds_check.md#bnddeprecatedappbndscheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Checks for redundant `app.bnd` in deprecated or archived modules. |
[BNDDirectoryNameCheck](check/bnd_directory_name_check.md#bnddirectorynamecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks if the directory names of the submodules match the parent module name. |
[BNDExportsCheck](check/bnd_exports_check.md#bndexportscheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that modules not ending with `-api`, `-client`, `-spi`, `-taglib`, `-test-util` do not export packages. |
[BNDImportsCheck](check/bnd_imports_check.md#bndimportscheck) | [Styling](styling_checks.md#styling-checks) | Sorts class names and checks for use of wildcards in property values for `-conditionalpackage`, `-exportcontents` and `Export-Package`. |
[BNDIncludeResourceCheck](check/bnd_include_resource_check.md#bndincluderesourcecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for unnecessary including of `test-classes/integration`. |
[BNDLiferayEnterpriseAppCheck](check/bnd_liferay_enterprise_app_check.md#bndliferayenterpriseappcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for correct use of property `Liferay-Enterprise-App`. |
[BNDLiferayRelengBundleCheck](check/bnd_liferay_releng_bundle_check.md#bndliferayrelengbundlecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks if `.lfrbuild-release-src` file exists for DXP module with `Liferay-Releng-Bundle: true`. |
[BNDLiferayRelengCategoryCheck](check/bnd_liferay_releng_category_check.md#bndliferayrelengcategorycheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates `Liferay-Releng-Category` properties. |
BNDLineBreaksCheck | [Styling](styling_checks.md#styling-checks) | Finds missing and unnecessary line breaks in `.bnd` files. |
[BNDMultipleAppBNDsCheck](check/bnd_multiple_app_bnds_check.md#bndmultipleappbndscheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for duplicate `app.bnd` (when both `/apps/` and `/apps/dxp/` contain the same module). |
[BNDRangeCheck](check/bnd_range_check.md#bndrangecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for use or range expressions. |
[BNDSchemaVersionCheck](check/bnd_schema_version_check.md#bndschemaversioncheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for incorrect use of property `Liferay-Require-SchemaVersion`. |
BNDStylingCheck | [Styling](styling_checks.md#styling-checks) | Applies rules to enforce consistency in code style. |
[BNDSuiteCheck](check/bnd_suite_check.md#bndsuitecheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Checks that deprecated apps are moved to the `archived` folder. |
[BNDWebContextPathCheck](check/bnd_web_context_path_check.md#bndwebcontextpathcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks if the property value for `Web-ContextPath` matches the module directory. |
BNDWhitespaceCheck | [Styling](styling_checks.md#styling-checks) | Finds missing and unnecessary whitespace in `.bnd` files. |