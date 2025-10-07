# Checks for .ftl

Check | Category | Description
----- | -------- | -----------
FTLEmptyLinesCheck | [Styling](styling_checks.md#styling-checks) | Finds missing and unnecessary empty lines. |
[FTLIfStatementCheck](check/if_statement_check.md#ifstatementcheck) | [Styling](styling_checks.md#styling-checks) | Finds incorrect use of parentheses in statement. |
FTLImportsCheck | [Styling](styling_checks.md#styling-checks) | Sorts and groups imports in `.ftl` files. |
FTLLiferayVariableOrderCheck | [Styling](styling_checks.md#styling-checks) | Sorts assign statement of `liferay_*` variables. |
FTLStringRelationalOperatorCheck | [Styling](styling_checks.md#styling-checks) | Finds cases of `==` or `!=` where `stringUtil.equals`, `validator.isNotNull` or `validator.isNull` can be used instead. |
FTLStylingCheck | [Styling](styling_checks.md#styling-checks) | Applies rules to enforce consistency in code style. |
FTLTagAttributesCheck | [Styling](styling_checks.md#styling-checks) | Sorts and formats attributes values in tags. |
FTLTagCheck | [Styling](styling_checks.md#styling-checks) | Finds cases where consecutive `#assign` can be combined. |
FTLWhitespaceCheck | [Styling](styling_checks.md#styling-checks) | Finds missing and unnecessary whitespace in `.ftl` files. |
IllegalTaglibsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases of incorrect use of certain deprecated taglibs in modules. |