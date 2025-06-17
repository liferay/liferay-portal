# Checks for .jsp, .jspf, .jspx, .tag, .tpl, or .vm

Check | Category | Description
----- | -------- | -----------
AppendCheck | [Styling](styling_checks.md#styling-checks) | Checks instances where literal Strings are appended. |
[ArrayCheck](check/array_check.md#arraycheck) | [Performance](performance_checks.md#performance-checks) | Checks if performance can be improved by using different methods that can be used by collections. |
[ArrayTypeStyleCheck](https://checkstyle.sourceforge.io/checks/misc/arraytypestyle.html) | [Styling](styling_checks.md#styling-checks) | Checks the style of array type definitions. |
ArrayUtilCheck | [Styling](styling_checks.md#styling-checks) | Checks for utilization of class `ArrayUtil`. |
[AvoidNestedBlocksCheck](https://checkstyle.sourceforge.io/checks/blocks/avoidnestedblocks.html) | [Styling](styling_checks.md#styling-checks) | Finds nested blocks (blocks that are used freely in the code). |
BrandNameCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks the correct brand name. |
[CamelCaseNameCheck](check/camel_case_name_check.md#camelcasenamecheck) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks variable names for correct use of `CamelCase`. |
CapsNameCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks the correct caps name. |
ChainingCheck | [Styling](styling_checks.md#styling-checks) | Checks that method chaining can be used when possible. |
[CompanyIterationCheck](check/company_iteration_check.md#companyiterationcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that `CompanyLocalService.forEachCompany` or `CompanyLocalService.forEachCompanyId` is used when iterating over companies. |
CompatClassImportsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that classes are imported from `compat` modules, when possible. |
ConcatCheck | [Performance](performance_checks.md#performance-checks) | Checks for correct use of `StringBundler.concat`. |
ConstantNameCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that variable names of constants follow correct naming rules. |
ContractionsCheck | [Styling](styling_checks.md#styling-checks) | Finds contractions in Strings (such as `can't` or `you're`). |
[CopyrightCheck](check/copyright_check.md#copyrightcheck) | [Styling](styling_checks.md#styling-checks) | Validates `copyright` header. |
[DefaultComesLastCheck](https://checkstyle.sourceforge.io/checks/coding/defaultcomeslast.html) | [Styling](styling_checks.md#styling-checks) | Checks that the `default` is after all the cases in a `switch` statement. |
EmptyCollectionCheck | [Styling](styling_checks.md#styling-checks) | Checks that there are no calls to `Collections.EMPTY_LIST`, `Collections.EMPTY_MAP` or `Collections.EMPTY_SET`. |
[ExceptionMessageCheck](check/message_check.md#messagecheck) | [Styling](styling_checks.md#styling-checks) | Validates messages that are passed to exceptions. |
FactoryCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `*Factory` should be used when creating new instances of an object. |
[GenericTypeCheck](check/generic_type_check.md#generictypecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that generics are always specified to provide compile-time checking and removing the risk of `ClassCastException` during runtime. |
[GetterUtilCheck](check/getter_util_check.md#getterutilcheck) | [Styling](styling_checks.md#styling-checks) | Finds cases where the default value is passed to `GetterUtil.get*` or `ParamUtil.get*`. |
[IfStatementCheck](check/if_statement_check.md#ifstatementcheck) | [Styling](styling_checks.md#styling-checks) | Finds empty if-statements and consecutive if-statements with identical bodies. |
IllegalImportsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases of incorrect use of certain classes. |
IllegalTaglibsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases of incorrect use of certain deprecated taglibs in modules. |
InstanceofOrderCheck | [Styling](styling_checks.md#styling-checks) | Check the order of `instanceof` calls. |
JSONNamingCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks if variable names follow naming conventions. |
[JSONUtilCheck](check/json_util_check.md#jsonutilcheck) | [Styling](styling_checks.md#styling-checks) | Checks for utilization of class `JSONUtil`. |
[JSPArrowFunctionCheck](check/jsp_arrow_function_check.md#jsparrowfunctioncheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that there are no array functions. |
JSPCoreTaglibCheck | [Styling](styling_checks.md#styling-checks) | Finds cases where a `c:choose` or `c:if` tag can be used instead of an if-statement. |
[JSPDefineObjectsCheck](check/jsp_define_objects_check.md#jspdefineobjectscheck) | [Performance](performance_checks.md#performance-checks) | Checks for unnecessary duplication of code that already exists in `defineObjects`. |
JSPEmptyLinesCheck | [Styling](styling_checks.md#styling-checks) | Finds missing and unnecessary empty lines. |
JSPExceptionOrderCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of exceptions in `.jsp` files. |
JSPExpressionTagCheck | [Styling](styling_checks.md#styling-checks) | Applies rules to enforce consistency in code style. |
[JSPFileNameCheck](check/jsp_file_name_check.md#jspfilenamecheck) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks if the file name of `.jsp` or `.jspf` follows the naming conventions. |
[JSPFunctionNameCheck](check/jsp_function_name_check.md#jspfunctionnamecheck) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Check if the names of functions in `.jsp` files follow naming conventions. |
JSPGetStaticResourceURLCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks calls to `PortalUtil.getStaticResourceURL` and `getContextPath` without `getPathProxy`. |
[JSPIllegalSyntaxCheck](check/jsp_illegal_syntax_check.md#jspillegalsyntaxcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds incorrect use of `System.out.print`, `console.log` or `debugger.*` in `.jsp` files. |
[JSPImportsCheck](check/jsp_imports_check.md#jspimportscheck) | [Styling](styling_checks.md#styling-checks) | Sorts and groups imports in `.jsp` files. |
[JSPIncludeCheck](check/jsp_include_check.md#jspincludecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates values of `include` in `.jsp` files. |
JSPIndentationCheck | [Styling](styling_checks.md#styling-checks) | Finds incorrect indentation in `.jsp` files. |
JSPInlineVariableCheck | [Styling](styling_checks.md#styling-checks) | Finds cases where variables can be inlined. |
JSPJakartaTransformCheck | [JakartaTransform](jakarta_transform_checks.md#jakartatransform-checks) | Performs replacements for use of Jakarta. |
JSPJavaParserCheck | [Styling](styling_checks.md#styling-checks) | Performs JavaParser on `.java` files. |
JSPLanguageKeysCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing language keys in `Language.properties`. |
JSPLanguageUtilCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds incorrect use of `LanguageUtil.get` in `.jsp` files. |
JSPLineBreakCheck | [Styling](styling_checks.md#styling-checks) | Finds missing and unnecessary line breaks in `.jsp` lines. |
JSPLogFileNameCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates the value that is passed to `LogFactoryUtil.getLog` in `.jsp`. |
[JSPMethodCallsCheck](check/jsp_method_calls_check.md#jspmethodcallscheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that type `LiferayPortletResponse` is used to call `getNamespace()`. |
[JSPMissingTaglibsCheck](check/jsp_missing_taglibs_check.md#jspmissingtaglibscheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for missing taglibs. |
[JSPModuleIllegalImportsCheck](check/jsp_module_illegal_imports_check.md#jspmoduleillegalimportscheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Finds incorrect use of `com.liferay.registry.Registry` or `com.liferay.util.ContentUtil`. |
JSPOutputTaglibsCheck | [Styling](styling_checks.md#styling-checks) | Checks that value of `outputKey` follows naming conventions. |
[JSPParenthesesCheck](check/if_statement_check.md#ifstatementcheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Finds incorrect use of parentheses in statement. |
JSPRedirectBackURLCheck | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Validates values of variable `redirect`. |
[JSPSendRedirectCheck](check/jsp_send_redirect_check.md#jspsendredirectcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that there are no calls to `HttpServletResponse.sendRedirect` from `jsp` files. |
[JSPServiceUtilCheck](check/jsp_service_util_check.md#jspserviceutilcheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Finds incorrect use of `*ServiceUtil` in `.jsp` files in modules. |
JSPSessionKeysCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that messages send to `SessionsErrors` or `SessionMessages` follow naming conventions. |
JSPStylingCheck | [Styling](styling_checks.md#styling-checks) | Applies rules to enforce consistency in code style. |
JSPTagAttributesCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on tag attributes. |
JSPTaglibMissingAttributesCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for missing taglib attributes. |
[JSPTaglibVariableCheck](check/jsp_taglib_variable_check.md#jsptaglibvariablecheck) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks if variable names follow naming conventions. |
[JSPUnusedJSPFCheck](check/jsp_unused_jspf_check.md#jspunusedjspfcheck) | [Performance](performance_checks.md#performance-checks) | Finds `.jspf` files that are not used. |
JSPUnusedTermsCheck | [Performance](performance_checks.md#performance-checks) | Finds taglibs, variables and imports that are unused. |
JSPUpgradeRemovedTagsCheck | [Upgrade](upgrade_checks.md#upgrade-checks) | Finds removed tags when upgrading. |
JSPVarNameCheck | [Styling](styling_checks.md#styling-checks) | Checks that values of attribute `var` follow naming conventions. |
JSPVariableOrderCheck | [Styling](styling_checks.md#styling-checks) | Checks if variable names are in alphabetical order. |
JSPWhitespaceCheck | [Styling](styling_checks.md#styling-checks) | Finds missing and unnecessary whitespace in `.jsp` files. |
JSPXSSVulnerabilitiesCheck | [Security](security_checks.md#security-checks) | Finds xss vulnerabilities. |
[LambdaCheck](check/lambda_check.md#lambdacheck) | [Styling](styling_checks.md#styling-checks) | Checks that `lambda` statements are as simple as possible. |
[ListUtilCheck](check/list_util_check.md#listutilcheck) | [Styling](styling_checks.md#styling-checks) | Checks for utilization of class `ListUtil`. |
LiteralStringEqualsCheck | [Styling](styling_checks.md#styling-checks) | Finds cases where `Objects.equals` should be used. |
[LocalFinalVariableNameCheck](https://checkstyle.sourceforge.io/checks/naming/localfinalvariablename.html) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that local final variable names conform to a specified pattern. |
[LocalVariableNameCheck](https://checkstyle.sourceforge.io/checks/naming/localvariablename.html) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that local, non-final variable names conform to a specified pattern. |
LocalVariableTypeInferenceCheck | [Performance](performance_checks.md#performance-checks) | Finds usage of `var` in local variable declaration. |
LocaleUtilCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `com.liferay.portal.kernel.util.LocaleUtil` should be used (instead of `java.util.Locale`). |
LogParametersCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates the values of parameters passed to `_log.*` calls. |
[MapBuilderCheck](check/builder_check.md#buildercheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Checks that `ConcurrentHashMapBuilder`, `HashMapBuilder`, `LinkedHashMapBuilder` or `TreeMapBuilder` is used when possible. |
[MapIterationCheck](check/map_iteration_check.md#mapiterationcheck) | [Performance](performance_checks.md#performance-checks) | Checks that there are no unnecessary map iterations. |
[MemberNameCheck](https://checkstyle.sourceforge.io/checks/naming/membername.html) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that instance variable names conform to a specified pattern. |
MethodCallsOrderCheck | [Styling](styling_checks.md#styling-checks) | Sorts method calls for certain object (for example, `put` calls in `java.util.HashMap`). |
MethodEqualsCheck | [Styling](styling_checks.md#styling-checks) | Finds cases where `Objects.equals` should be used. |
[MethodNameCheck](https://checkstyle.sourceforge.io/checks/naming/methodname.html) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that method names conform to a specified pattern. |
MethodNamingCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that method names follow naming conventions. |
[MethodParamPadCheck](https://checkstyle.sourceforge.io/checks/whitespace/methodparampad.html) | [Styling](styling_checks.md#styling-checks) | Checks the padding between the identifier of a method definition, constructor definition, method call, or constructor invocation; and the left parenthesis of the parameter list. |
MissingDiamondOperatorCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for missing diamond operator for types that require diamond operator. |
[MissingEmptyLineCheck](check/missing_empty_line_check.md#missingemptylinecheck) | [Styling](styling_checks.md#styling-checks) | Checks for missing line breaks around variable declarations. |
MissingParenthesesCheck | [Styling](styling_checks.md#styling-checks) | Finds missing parentheses in conditional statement. |
[ModifierOrderCheck](https://checkstyle.sourceforge.io/checks/modifier/modifierorder.html) | [Styling](styling_checks.md#styling-checks) | Checks that the order of modifiers conforms to the suggestions in the Java Language specification, § 8.1.1, 8.3.1, 8.4.3 and 9.4. |
[MultipleVariableDeclarationsCheck](https://checkstyle.sourceforge.io/checks/coding/multiplevariabledeclarations.html) | [Styling](styling_checks.md#styling-checks) | Checks that each variable declaration is in its own statement and on its own line. |
[NeedBracesCheck](https://checkstyle.sourceforge.io/checks/blocks/needbraces.html) | [Styling](styling_checks.md#styling-checks) | Checks for braces around code blocks. |
NestedIfStatementCheck | [Styling](styling_checks.md#styling-checks) | Finds nested if statements that can be combined. |
[NoLineWrapCheck](https://checkstyle.sourceforge.io/checks/whitespace/nolinewrap.html) | [Styling](styling_checks.md#styling-checks) | Checks that chosen statements are not line-wrapped. |
[NoWhitespaceAfterCheck](https://checkstyle.sourceforge.io/checks/whitespace/nowhitespaceafter.html) | [Styling](styling_checks.md#styling-checks) | Checks that there is no whitespace after a token. |
[NoWhitespaceBeforeCheck](https://checkstyle.sourceforge.io/checks/whitespace/nowhitespacebefore.html) | [Styling](styling_checks.md#styling-checks) | Checks that there is no whitespace before a token. |
NumberSuffixCheck | [Styling](styling_checks.md#styling-checks) | Verifies that uppercase `D`, `F`, or `L` is used when denoting Double/Float/Long. |
[OneStatementPerLineCheck](https://checkstyle.sourceforge.io/checks/coding/onestatementperline.html) | [Styling](styling_checks.md#styling-checks) | Checks that there is only one statement per line. |
OperatorOperandCheck | [Styling](styling_checks.md#styling-checks) | Verifies that operand do not go over too many lines and make the operator hard to read. |
[OperatorWrapCheck](https://checkstyle.sourceforge.io/checks/whitespace/operatorwrap.html) | [Styling](styling_checks.md#styling-checks) | Checks the policy on how to wrap lines on operators. |
[ParameterNameCheck](https://checkstyle.sourceforge.io/checks/naming/parametername.html) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that method parameter names conform to a specified pattern. |
ParsePrimitiveTypeCheck | [Performance](performance_checks.md#performance-checks) | Verifies that `GetterUtil.parse*` is used to parse primitive types, when possible. |
PersistenceCallCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds illegal persistence calls across component boundaries. |
PlusStatementCheck | [Styling](styling_checks.md#styling-checks) | Performs several checks to statements where `+` is used for concatenation. |
[PortletURLBuilderCheck](check/builder_check.md#buildercheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Checks that `PortletURLBuilder` is used when possible. |
PrimitiveWrapperInstantiationCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `new Type` is used for primitive types (use `Type.valueOf` instead). |
PrincipalExceptionCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds calls to `PrincipalException.class.getName()` (use `PrincipalException.getNestedClasses()` instead). |
[ResourceBundleCheck](check/resource_bundle_check.md#resourcebundlecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that there are no calls to `java.util.ResourceBundle.getBundle`. |
SemiColonCheck | [Styling](styling_checks.md#styling-checks) | Finds cases of unnecessary semicolon. |
SetUtilMethodsCheck | [Performance](performance_checks.md#performance-checks) | Finds cases of inefficient SetUtil operations. |
SizeIsZeroCheck | [Styling](styling_checks.md#styling-checks) | Finds cases of calls like `list.size() == 0` (use `list.isEmpty()` instead). |
[StaticVariableNameCheck](https://checkstyle.sourceforge.io/checks/naming/staticvariablename.html) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that static, non-final variable names conform to a specified pattern. |
StringBundlerNamingCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks for consistent naming on variables of type 'StringBundler'. |
StringCastCheck | [Performance](performance_checks.md#performance-checks) | Finds cases where a redundant `toString()` is called on variable type `String`. |
[StringLiteralEqualityCheck](https://checkstyle.sourceforge.io/checks/coding/stringliteralequality.html) | [Styling](styling_checks.md#styling-checks) | Checks that string literals are not used with == or !=. |
[StringMethodsCheck](check/string_methods_check.md#stringmethodscheck) | [Performance](performance_checks.md#performance-checks) | Checks if performance can be improved by using different String operation methods. |
SubstringCheck | [Performance](performance_checks.md#performance-checks) | Finds cases like `s.substring(1, s.length())` (use `s.substring(1)` instead). |
TernaryOperatorCheck | [Styling](styling_checks.md#styling-checks) | Finds use of ternary operator in `java` files (use if statement instead). |
UnnecessaryTypeCastCheck | [Performance](performance_checks.md#performance-checks) | Finds unnecessary Type Casting. |
UnparameterizedClassCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds `Class` instantiation without generic type. |
[ValidatorEqualsCheck](check/validator_equals_check.md#validatorequalscheck) | [Performance](performance_checks.md#performance-checks) | Checks that there are no calls to `Validator.equals(Object, Object)`. |
ValidatorIsNullCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Ensures that only variable of type `Long`, `Serializable` or `String` is passed to method `com.liferay.portal.kernel.util.Validator.isNull`. |
VariableNameCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that variable names follow naming conventions. |
[WhitespaceAfterCheck](https://checkstyle.sourceforge.io/checks/whitespace/whitespaceafter.html) | [Styling](styling_checks.md#styling-checks) | Checks that a token is followed by whitespace, with the exception that it does not check for whitespace after the semicolon of an empty for iterator. |
[WhitespaceAroundCheck](https://checkstyle.sourceforge.io/checks/whitespace/whitespacearound.html) | [Styling](styling_checks.md#styling-checks) | Checks that a token is surrounded by whitespace. |