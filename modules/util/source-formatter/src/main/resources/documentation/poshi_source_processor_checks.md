# Checks for .function, .jar, .lar, .macro, .path, .testcase, .war, or .zip

Check | Category | Description
----- | -------- | -----------
PoshiAntCommandParametersOrderCheck | [Styling](styling_checks.md#styling-checks) | Sorts parameters in AntCommand call in Poshi Script files. |
PoshiDependenciesFileLocationCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that dependencies files are located in the correct directory. |
PoshiImportsCheck | [Styling](styling_checks.md#styling-checks) | Sorts and groups imports in Poshi Script files. |
PoshiIndentationCheck | [Styling](styling_checks.md#styling-checks) | Finds incorrect indentation in Poshi Script files. |
[PoshiPauseUsageCheck](check/poshi_pause_usage_check.md#poshipauseusagecheck) | [Performance](performance_checks.md#performance-checks) | Finds missing comment with JIRA project when using `Pause`. |
PoshiPropertiesOrderCheck | [Styling](styling_checks.md#styling-checks) | Sorts the values of properties in `.testcase` file. |
PoshiPropsUtilCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `PropsUtil.get` should be inlined. |
PoshiSmokeTestCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for missing and unnecessary `property ci.retries.disabled = true` in smoke test. |
PoshiStylingCheck | [Styling](styling_checks.md#styling-checks) | Applies rules to enforce consistency in code style. |
PoshiVariableNameCheck | [Styling](styling_checks.md#styling-checks) | Checks variable names for correct use of `camelCase`. |
PoshiWhitespaceCheck | [Styling](styling_checks.md#styling-checks) | Finds missing and unnecessary whitespace. |