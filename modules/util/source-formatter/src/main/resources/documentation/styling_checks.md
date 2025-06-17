# Styling Checks

Check | File Extensions | Description
----- | --------------- | -----------
[AnnotationUseStyleCheck](https://checkstyle.sourceforge.io/checks/annotation/annotationusestyle.html) | .java | Checks the style of elements in annotations. |
AppendCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks instances where literal Strings are appended. |
[ArrayTypeStyleCheck](https://checkstyle.sourceforge.io/checks/misc/arraytypestyle.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks the style of array type definitions. |
ArrayUtilCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks for utilization of class `ArrayUtil`. |
[AssertEqualsCheck](check/assert_equals_check.md#assertequalscheck) | .java | Checks that additional information is provided when calling `Assert.assertEquals`. |
[AvoidNestedBlocksCheck](https://checkstyle.sourceforge.io/checks/blocks/avoidnestedblocks.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds nested blocks (blocks that are used freely in the code). |
BNDCapabilityCheck | .bnd | Sorts and applies logic to fix line breaks to property values for `Provide-Capability` and `Require-Capability`. |
[BNDImportsCheck](check/bnd_imports_check.md#bndimportscheck) | .bnd | Sorts class names and checks for use of wildcards in property values for `-conditionalpackage`, `-exportcontents` and `Export-Package`. |
BNDLineBreaksCheck | .bnd | Finds missing and unnecessary line breaks in `.bnd` files. |
BNDRunInstructionsOrderCheck | .bndrun | Sorts definition keys alphabetically. |
BNDStylingCheck | .bnd | Applies rules to enforce consistency in code style. |
BNDWhitespaceCheck | .bnd | Finds missing and unnecessary whitespace in `.bnd` files. |
CSSCommentsCheck | .css or .scss | Validates comments in `.css` files. |
CSSImportsCheck | .css or .scss | Sorts and groups imports in `.css` files. |
CSSPropertiesOrderCheck | .css or .scss | Sorts properties in `.css` files. |
ChainingCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that method chaining can be used when possible. |
CodeownersWhitespaceCheck | CODEOWNERS | Finds missing and unnecessary whitespace in `CODEOWNERS` files. |
ConfigDefinitionKeysCheck | .cfg or .config | Sorts definition keys in `.config` files. |
ConfigWhitespaceCheck | .cfg or .config | Finds missing and unnecessary whitespace. |
[ConstructorMissingEmptyLineCheck](check/constructor_missing_empty_line_check.md#constructormissingemptylinecheck) | .java | Checks for line breaks when assigning variables in constructor. |
ContractionsCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds contractions in Strings (such as `can't` or `you're`). |
[CopyrightCheck](check/copyright_check.md#copyrightcheck) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Validates `copyright` header. |
[DefaultComesLastCheck](https://checkstyle.sourceforge.io/checks/coding/defaultcomeslast.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that the `default` is after all the cases in a `switch` statement. |
DockerfileEmptyLinesCheck | Dockerfile | Finds missing and unnecessary empty lines. |
DockerfileInstructionCheck | Dockerfile | Performs styling rules on instructions in `Dockerfile` files. |
EmptyCollectionCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that there are no calls to `Collections.EMPTY_LIST`, `Collections.EMPTY_MAP` or `Collections.EMPTY_SET`. |
EnumConstantDividerCheck | .java | Find unnecessary empty lines between enum constants. |
EnumConstantOrderCheck | .java | Checks the order of enum constants. |
EqualClauseIfStatementsCheck | .java | Finds consecutive if-statements with identical clauses. |
[ExceptionMessageCheck](check/message_check.md#messagecheck) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Validates messages that are passed to exceptions. |
FTLEmptyLinesCheck | .ftl | Finds missing and unnecessary empty lines. |
[FTLIfStatementCheck](check/if_statement_check.md#ifstatementcheck) | .ftl | Finds incorrect use of parentheses in statement. |
FTLImportsCheck | .ftl | Sorts and groups imports in `.ftl` files. |
FTLLiferayVariableOrderCheck | .ftl | Sorts assign statement of `liferay_*` variables. |
FTLStringRelationalOperatorCheck | .ftl | Finds cases of `==` or `!=` where `stringUtil.equals`, `validator.isNotNull` or `validator.isNull` can be used instead. |
FTLStylingCheck | .ftl | Applies rules to enforce consistency in code style. |
FTLTagAttributesCheck | .ftl | Sorts and formats attributes values in tags. |
FTLTagCheck | .ftl | Finds cases where consecutive `#assign` can be combined. |
FTLWhitespaceCheck | .ftl | Finds missing and unnecessary whitespace in `.ftl` files. |
[GetterUtilCheck](check/getter_util_check.md#getterutilcheck) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds cases where the default value is passed to `GetterUtil.get*` or `ParamUtil.get*`. |
GradleBlockOrderCheck | .gradle | Sorts logic in gradle build files. |
GradleBodyCheck | .gradle | Applies rules to enforce consistency in the body of gradle build files. |
GradleImportsCheck | .gradle | Sorts and groups imports in `.gradle` files. |
GradleIndentationCheck | .gradle | Finds incorrect indentation in gradle build files. |
GradleStylingCheck | .gradle | Applies rules to enforce consistency in code style. |
[GradleTaskCreationCheck](check/gradle_task_creation_check.md#gradletaskcreationcheck) | .gradle | Checks that a task is declared on a separate line before the closure. |
GroovyImportsCheck | .groovy | Sorts and groups imports in `.groovy` files. |
HTMLEmptyLinesCheck | .html or .path | Finds missing and unnecessary empty lines. |
HTMLWhitespaceCheck | .html or .path | Finds missing and unnecessary whitespace in `.html` files. |
[IfStatementCheck](check/if_statement_check.md#ifstatementcheck) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds empty if-statements and consecutive if-statements with identical bodies. |
[InstanceInitializerCheck](check/instance_initializer_check.md#instanceinitializercheck) | .java | Performs several checks on class instance initializer. |
InstanceofOrderCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Check the order of `instanceof` calls. |
JSONBatchEngineDataFileCheck | .ipynb, .json, or .npmbridgerc | Remove elements in `*.batch-engine-data.json` files. |
JSONPageFileCheck | .ipynb, .json, or .npmbridgerc | Sorts by `roleName` in `page.json` files. |
JSONPortletResponseUtilCheck | .java | Checks if `JSONPortletResponseUtil.writeJSON ` should come before method calling `hideDefaultSuccessMessage`. |
JSONReplacementsFileCheck | .ipynb, .json, or .npmbridgerc | Sorts by `issueKey`, `from` and `to` in `replacements.json` file. |
JSONResourcePermissionsFileCheck | .ipynb, .json, or .npmbridgerc | Sorts by `resourceName` and `roleName` in `resource-permissions.json` files. |
JSONStylingCheck | .ipynb, .json, or .npmbridgerc | Applies rules to enforce consistency in code style. |
[JSONUtilCheck](check/json_util_check.md#jsonutilcheck) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks for utilization of class `JSONUtil`. |
JSPCoreTaglibCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds cases where a `c:choose` or `c:if` tag can be used instead of an if-statement. |
JSPEmptyLinesCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds missing and unnecessary empty lines. |
JSPExceptionOrderCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks the order of exceptions in `.jsp` files. |
JSPExpressionTagCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Applies rules to enforce consistency in code style. |
[JSPImportsCheck](check/jsp_imports_check.md#jspimportscheck) | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Sorts and groups imports in `.jsp` files. |
JSPIndentationCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds incorrect indentation in `.jsp` files. |
JSPInlineVariableCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds cases where variables can be inlined. |
JSPJavaParserCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Performs JavaParser on `.java` files. |
JSPLineBreakCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds missing and unnecessary line breaks in `.jsp` lines. |
JSPOutputTaglibsCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that value of `outputKey` follows naming conventions. |
JSPStylingCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Applies rules to enforce consistency in code style. |
JSPVarNameCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that values of attribute `var` follow naming conventions. |
JSPVariableOrderCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks if variable names are in alphabetical order. |
JSPWhitespaceCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds missing and unnecessary whitespace in `.jsp` files. |
JSStylingCheck | .js or .jsx | Applies rules to enforce consistency in code style. |
JSWhitespaceCheck | .js or .jsx | Finds missing and unnecessary whitespace in `.js` files. |
JavaAggregateTestRuleParameterOrderCheck | .java | Checks the order of parameters in `new AggregateTestRule` calls. |
JavaAnnotationDefaultAttributeCheck | .java | Finds cases where the default value is passed to annotations in package `*.bnd.annotations` or `*.bind.annotations`. |
JavaAssertEqualsCheck | .java | Validates `Assert.assertEquals` calls. |
[JavaConstructorParametersCheck](check/java_constructor_parameters_check.md#javaconstructorparameterscheck) | .java | Checks that the order of variable assignments matches the order of the parameters in the constructor signature. |
JavaConstructorSuperCallCheck | .java | Finds unnecessary call to no-argument constructor of the superclass. |
JavaEmptyLinesCheck | .java | Finds missing and unnecessary empty lines. |
JavaEntityFieldsMapOrderCheck | .java | Sorts entity fields when calling `EntityFieldsMapFactory.create` and `EntityModel.toEntityFieldsMap`. |
JavaFeatureFlagsAndTestInfoAnnotationCheck | .java | Sorts the values in `@FeatureFlags` and `@TestInfo` annotation. |
JavaFinalVariableCheck | .java | Finds cases of unneeded `final` modifiers for variables and parameters. |
[JavaForLoopCheck](check/java_for_loop_check.md#javaforloopcheck) | .java | Checks if a Enhanced For Loop can be used instead of a Simple For Loop. |
JavaIOExceptionCheck | .java | Validates use of `IOException`. |
JavaImportsCheck | .java | Sorts and groups imports in `.java` files. |
JavaInnerClassImportsCheck | .java | Finds cases where inner classes are imported. |
JavaLongLinesCheck | .java | Finds lines that are longer than the specified maximum line length. |
JavaReturnStatementCheck | .java | Finds unnecessary `else` statement (when `if` and `else` statement both end with `return` statement). |
JavaRunSqlStylingCheck | .java | Applies rules to enforce consistency in code style. |
JavaServiceObjectCheck | .java | Checks for correct use of `*.is*` instead of `*.get*` when calling methods generated by ServiceBuilder. |
[JavaSignatureParametersCheck](check/java_signature_parameters_check.md#javasignatureparameterscheck) | .java | Checks the order of parameters. |
[JavaStaticImportsCheck](check/java_static_imports_check.md#javastaticimportscheck) | .java | Checks that there are no static imports. |
JavaStylingCheck | .java | Applies rules to enforce consistency in code style. |
[JavaSwitchCheck](check/java_switch_check.md#javaswitchcheck) | .java | Checks that `if/else` statement is used instead of `switch` statement. |
JavaTermDividersCheck | .java | Finds missing or unnecessary empty lines between javaterms. |
JavaTermOrderCheck | .java | Checks the order of javaterms. |
JavaTermStylingCheck | .java | Applies rules to enforce consistency in code style. |
JavaToJSONArrayCheck | .java | Finds missing and unnecessary whitespace on `toJSONArray` calls. |
JavaUpgradeAlterColumnCallsOrderCheck | .java | Sorts method calls for altering table columns. |
JavaUpgradeEmptyLinesCheck | .java | Finds missing and unnecessary empty lines in upgrade classes. |
LDIFEntryOrderCheck | .ldif | Checks the order of entries and attributes in `.ldif` file. |
LDIFWhitespaceCheck | .ldif | Finds missing and unnecessary whitespace. |
[LambdaCheck](check/lambda_check.md#lambdacheck) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that `lambda` statements are as simple as possible. |
ListStylingCheck | .list | Applies rules to enforce consistency in code style. |
[ListUtilCheck](check/list_util_check.md#listutilcheck) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks for utilization of class `ListUtil`. |
LiteralStringEqualsCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds cases where `Objects.equals` should be used. |
[LogMessageCheck](check/message_check.md#messagecheck) | .java | Validates messages that are passed to `log.*` calls. |
MarkdownBreakingChangesAmendmentsFileCheck | .markdown or .md | Checks that breaking changes amendments should contain the schematized breaking changes in `BREAKING_CHANGES_AMENDMENTS.md` file. |
MarkdownEmptyLinesCheck | .markdown or .md | Finds missing and unnecessary empty lines. |
MarkdownFileExtensionCheck | .markdown or .md | Finds `markdown` files with `.markdown` extension (use `.md`). |
MarkdownStylingCheck | .markdown or .md | Applies rules to enforce consistency in code style. |
MarkdownWhitespaceCheck | .markdown or .md | Finds missing and unnecessary whitespace. |
MethodCallsOrderCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Sorts method calls for certain object (for example, `put` calls in `java.util.HashMap`). |
MethodEqualsCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds cases where `Objects.equals` should be used. |
[MethodParamPadCheck](https://checkstyle.sourceforge.io/checks/whitespace/methodparampad.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks the padding between the identifier of a method definition, constructor definition, method call, or constructor invocation; and the left parenthesis of the parameter list. |
[MissingEmptyLineCheck](check/missing_empty_line_check.md#missingemptylinecheck) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks for missing line breaks around variable declarations. |
MissingParenthesesCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds missing parentheses in conditional statement. |
[ModifierOrderCheck](https://checkstyle.sourceforge.io/checks/modifier/modifierorder.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that the order of modifiers conforms to the suggestions in the Java Language specification, § 8.1.1, 8.3.1, 8.4.3 and 9.4. |
[MultipleVariableDeclarationsCheck](https://checkstyle.sourceforge.io/checks/coding/multiplevariabledeclarations.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that each variable declaration is in its own statement and on its own line. |
[NeedBracesCheck](https://checkstyle.sourceforge.io/checks/blocks/needbraces.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks for braces around code blocks. |
NestedIfStatementCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds nested if statements that can be combined. |
[NoLineWrapCheck](https://checkstyle.sourceforge.io/checks/whitespace/nolinewrap.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that chosen statements are not line-wrapped. |
[NoWhitespaceAfterCheck](https://checkstyle.sourceforge.io/checks/whitespace/nowhitespaceafter.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that there is no whitespace after a token. |
[NoWhitespaceBeforeCheck](https://checkstyle.sourceforge.io/checks/whitespace/nowhitespacebefore.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that there is no whitespace before a token. |
NonbreakingSpaceCheck | | Finds `no break space` (`\u00a0`) characters. |
NotRequireThisCheck | .java | Finds cases of unnecessary use of `this.`. |
NumberSuffixCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Verifies that uppercase `D`, `F`, or `L` is used when denoting Double/Float/Long. |
[OSGiResourceBuilderCheck](check/osgi_resource_builder_check.md#osgiresourcebuildercheck) | .java | Avoid using *Resource.builder. |
[OneStatementPerLineCheck](https://checkstyle.sourceforge.io/checks/coding/onestatementperline.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that there is only one statement per line. |
OperatorOperandCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Verifies that operand do not go over too many lines and make the operator hard to read. |
OperatorOrderCheck | .java | Verifies that when an operator has a literal string or a number as one of the operands, it is always on the right hand side. |
[OperatorWrapCheck](https://checkstyle.sourceforge.io/checks/whitespace/operatorwrap.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks the policy on how to wrap lines on operators. |
PlusStatementCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Performs several checks to statements where `+` is used for concatenation. |
PoshiAntCommandParametersOrderCheck | .function, .jar, .lar, .macro, .path, .testcase, .war, or .zip | Sorts parameters in AntCommand call in Poshi Script files. |
PoshiImportsCheck | .function, .jar, .lar, .macro, .path, .testcase, .war, or .zip | Sorts and groups imports in Poshi Script files. |
PoshiIndentationCheck | .function, .jar, .lar, .macro, .path, .testcase, .war, or .zip | Finds incorrect indentation in Poshi Script files. |
PoshiPropertiesOrderCheck | .function, .jar, .lar, .macro, .path, .testcase, .war, or .zip | Sorts the values of properties in `.testcase` file. |
PoshiStylingCheck | .function, .jar, .lar, .macro, .path, .testcase, .war, or .zip | Applies rules to enforce consistency in code style. |
PoshiVariableNameCheck | .function, .jar, .lar, .macro, .path, .testcase, .war, or .zip | Checks variable names for correct use of `camelCase`. |
PoshiWhitespaceCheck | .function, .jar, .lar, .macro, .path, .testcase, .war, or .zip | Finds missing and unnecessary whitespace. |
PropertiesCommentsCheck | .eslintignore, .prettierignore, or .properties | Validates comments in `.properties` files. |
PropertiesDefinitionKeysCheck | .eslintignore, .prettierignore, or .properties | Sorts definition keys in `liferay-plugin-package.properties` file. |
PropertiesDependenciesFileCheck | .eslintignore, .prettierignore, or .properties | Sorts the properties in `dependencies.properties` file. |
PropertiesEmptyLinesCheck | .eslintignore, .prettierignore, or .properties | Finds missing and unnecessary empty lines. |
PropertiesLanguageContractionsCheck | .eslintignore, .prettierignore, or .properties | Finds contractions (such as `can't` or `you're`). |
PropertiesLanguageKeysOrderCheck | .eslintignore, .prettierignore, or .properties | Sort language keys in `Language.properties` file. |
PropertiesLanguageStylingCheck | .eslintignore, .prettierignore, or .properties | Applies rules to enforce consistency in code style. |
PropertiesLongLinesCheck | .eslintignore, .prettierignore, or .properties | Finds lines that are longer than the specified maximum line length. |
PropertiesMultiLineValuesOrderCheck | .eslintignore, .prettierignore, or .properties | Verifies that property with multiple values is not on a single line. |
PropertiesStylingCheck | .eslintignore, .prettierignore, or .properties | Applies rules to enforce consistency in code style. |
PropertiesTestFileCheck | .eslintignore, .prettierignore, or .properties | Checks the order of properties in `test.properties` file. |
PropertiesWhitespaceCheck | .eslintignore, .prettierignore, or .properties | Finds missing and unnecessary whitespace in `.properties` files. |
PythonClassesAndMethodsOrderCheck | .py | Checks the order of classes and methods. |
PythonImportsCheck | .py | Sorts and groups imports in `.py` files. |
PythonStylingCheck | .py | Applies rules to enforce consistency in code style. |
PythonWhitespaceCheck | .py | Finds missing and unnecessary whitespace. |
SQLEmptyLinesCheck | .sql | Finds missing and unnecessary empty lines. |
SQLStylingCheck | .sql | Applies rules to enforce consistency in code style. |
SemiColonCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds cases of unnecessary semicolon. |
SizeIsZeroCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds cases of calls like `list.size() == 0` (use `list.isEmpty()` instead). |
SlantedQuotesCheck | | Finds `slanted quote` (`\u201a`, `\u201b`, `\u201c`, `\u201d`, `\u201e`, `\u201f`, `\u2018` or `\u2019`) characters. |
SoyEmptyLinesCheck | .soy | Finds missing and unnecessary empty lines. |
[StringLiteralEqualityCheck](https://checkstyle.sourceforge.io/checks/coding/stringliteralequality.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that string literals are not used with == or !=. |
TFBlockOrderCheck | .tf | Checks the order of blocks in `.tf` file. |
TLDElementOrderCheck | .tld | Checks the order of attributes in `.tld` file. |
TLDStylingCheck | .tld | Applies rules to enforce consistency in code style. |
TSSpecFileStylingCheck | .ts or .tsx | Applies rules to enforce consistency in code style. |
TXTEmptyLinesCheck | .txt | Finds missing and unnecessary empty lines. |
TXTStylingCheck | .txt | Applies rules to enforce consistency in code style. |
TernaryOperatorCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds use of ternary operator in `java` files (use if statement instead). |
TextBlockCheck | .java | Finds usage of text block. |
UnnecessaryMethodCallCheck | .java | Finds unnecessary method calls. |
[WhitespaceAfterCheck](https://checkstyle.sourceforge.io/checks/whitespace/whitespaceafter.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that a token is followed by whitespace, with the exception that it does not check for whitespace after the semicolon of an empty for iterator. |
[WhitespaceAroundCheck](https://checkstyle.sourceforge.io/checks/whitespace/whitespacearound.html) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that a token is surrounded by whitespace. |
WhitespaceCheck | .cql, .css, .dtd, .expect, .gradle, .groovy, .scss, .sh, .soy, .sql, .tld, .ts, .tsx, Dockerfile, or packageinfo | Finds missing and unnecessary whitespace. |
XMLCustomSQLOrderCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of attributes in `custom-sql` file. |
XMLCustomSQLStylingCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Applies rules to enforce consistency in code style for `.xml` files in directory `custom-sql`. |
XMLDDLStructuresFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of attributes in `-structures.xml` file. |
XMLDTDVersionCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the DTD version in `*.xml` file. |
[XMLEchoMessageCheck](check/xml_echo_message_check.md#xmlechomessagecheck) | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the echo message attributes in `*.xml` file. |
XMLEmptyLinesCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Finds missing and unnecessary empty lines. |
XMLFSBExcludeFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of attributes in `fsb-exclude.xml` file. |
XMLFriendlyURLRoutesFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Performs several checks on `*-routes.xml` file. |
XMLHBMFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of imports in `*-hbm.xml` file. |
XMLImportsCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Sorts and groups imports in `.xml` files. |
XMLIndentationCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .tld, .toggle, .tpl, .wsdl, .xml, or .xsd | Finds incorrect indentation in `.xml` files. |
XMLInstanceWrappersFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of instance-wrappers in `instance_wrappers.xml` file. |
XMLIvyFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of dependencies in `ivy.xml` file. |
XMLLog4jFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of categories in `*-log4j.xml` file. |
XMLLookAndFeelFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of attributes in `*--look-and-feel.xml` file. |
XMLModelHintsFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of attributes in `*-model-hints.xml` file. |
XMLPomFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of dependencies in `pom.xml` file. |
XMLPortletPreferencesFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of elements in files in directory `resource-actions`. |
XMLResourceActionsFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of elements in files in directory `resource-actions`. |
XMLSolrSchemaFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of elements in `portlet-preferences.xml` file. |
XMLSpringFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of elements in `*-spring.xml` file. |
XMLStrutsConfigFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of elements in `struts-config.xml` file. |
XMLStylingCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Applies rules to enforce consistency in code style. |
XMLTestIgnorableErrorLinesFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of elements in `test-ignorable-error-lines.xml` file. |
XMLTilesDefsFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of elements in `tiles-defs.xml` file. |
XMLToggleFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the order of elements in `.toggle` file. |
XMLWhitespaceCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Finds missing and unnecessary whitespace in `.xml` files. |
XMLWorkflowDefinitionFileNameCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the file name of workflow definition files. |
XMLWorkflowDefinitionFileStylingCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Applies rules to enforce consistency in code style in `*workflow-definition.xml` files. |
YMLDefinitionOrderCheck | .tpl, .yaml, or .yml | Sorts definitions alphabetically in `.yml` files. |
YMLEmptyLinesCheck | .tpl, .yaml, or .yml | Finds missing and unnecessary empty lines. |
YMLIndentationCheck | .tpl, .yaml, or .yml | Finds incorrect indentation in YAML files. |
YMLLongLinesCheck | .tpl, .yaml, or .yml | Finds lines that are longer than the specified maximum line length. |
YMLStylingCheck | .tpl, .yaml, or .yml | Applies rules to enforce consistency in code style. |
YMLWhitespaceCheck | .tpl, .yaml, or .yml | Finds missing and unnecessary whitespace in `.yml` files. |