# Checks for .java

Check | Category | Description
----- | -------- | -----------
AccessModifierCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for cases where visibility of methods can be decreased. |
[AnnotationUseStyleCheck](https://checkstyle.sourceforge.io/checks/annotation/annotationusestyle.html) | [Styling](styling_checks.md#styling-checks) | Checks the style of elements in annotations. |
[AnonymousClassCheck](check/anonymous_class_check.md#anonymousclasscheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for serialization issue when using anonymous class. |
AppendCheck | [Styling](styling_checks.md#styling-checks) | Checks instances where literal Strings are appended. |
ArquillianCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for correct use of `com.liferay.arquillian.extension.junit.bridge.junit.Arquillian`. |
[ArrayCheck](check/array_check.md#arraycheck) | [Performance](performance_checks.md#performance-checks) | Checks if performance can be improved by using different methods that can be used by collections. |
[ArrayTypeStyleCheck](https://checkstyle.sourceforge.io/checks/misc/arraytypestyle.html) | [Styling](styling_checks.md#styling-checks) | Checks the style of array type definitions. |
ArrayUtilCheck | [Styling](styling_checks.md#styling-checks) | Checks for utilization of class `ArrayUtil`. |
[AssertEqualsCheck](check/assert_equals_check.md#assertequalscheck) | [Styling](styling_checks.md#styling-checks) | Checks that additional information is provided when calling `Assert.assertEquals`. |
AssertFailCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that calls to `Assert.fail` can be only used inside a try block as the last statement. |
AssignAsUsedCheck | [Performance](performance_checks.md#performance-checks) | Finds cases where an assign statement can be inlined or moved closer to where it is used. |
[AvoidNestedBlocksCheck](https://checkstyle.sourceforge.io/checks/blocks/avoidnestedblocks.html) | [Styling](styling_checks.md#styling-checks) | Finds nested blocks (blocks that are used freely in the code). |
[AvoidStarImportCheck](https://checkstyle.sourceforge.io/checks/imports/avoidstarimport.html) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that there are no import statements that use the * notation. |
BrandNameCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks the correct brand name. |
[CamelCaseNameCheck](check/camel_case_name_check.md#camelcasenamecheck) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks variable names for correct use of `CamelCase`. |
CapsNameCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks the correct caps name. |
ChainingCheck | [Styling](styling_checks.md#styling-checks) | Checks that method chaining can be used when possible. |
ClassNameIdCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Avoid caching noncompany scoped class name IDs. |
[CompanyIterationCheck](check/company_iteration_check.md#companyiterationcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that `CompanyLocalService.forEachCompany` or `CompanyLocalService.forEachCompanyId` is used when iterating over companies. |
[CompanyThreadLocalCheck](check/company_thread_local_check.md#companythreadlocalcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks usage of `CompanyThreadLocal`. |
CompatClassImportsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that classes are imported from `compat` modules, when possible. |
ComponentAnnotationCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on classes with @Component annotation. |
[ComponentExposureCheck](check/component_exposure_check.md#componentexposurecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Avoid exposing static component. |
ConcatCheck | [Performance](performance_checks.md#performance-checks) | Checks for correct use of `StringBundler.concat`. |
ConstantNameCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that variable names of constants follow correct naming rules. |
ConstructorGlobalVariableDeclarationCheck | [Performance](performance_checks.md#performance-checks) | Checks that initial values of global variables are not set in the constructor. |
[ConstructorMissingEmptyLineCheck](check/constructor_missing_empty_line_check.md#constructormissingemptylinecheck) | [Styling](styling_checks.md#styling-checks) | Checks for line breaks when assigning variables in constructor. |
ConsumerTypeAnnotationCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on classes with @ConsumerType annotation. |
ContractionsCheck | [Styling](styling_checks.md#styling-checks) | Finds contractions in Strings (such as `can't` or `you're`). |
[CopyrightCheck](check/copyright_check.md#copyrightcheck) | [Styling](styling_checks.md#styling-checks) | Validates `copyright` header. |
[CreatingThreadsForDBAccessCheck](check/creating_threads_for_db_access_check.md#creatingthreadsfordbaccesscheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `CompanyInheritableThreadLocalCallable` should be used when creating threads for DB access. |
[CreationMenuBuilderCheck](check/builder_check.md#buildercheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Checks that `CreationMenuBuilder` is used when possible. |
DTOEnumCreationCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks the creation of DTO enum. |
DatabaseMetaDataCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks usages of `java.sql.DatabaseMetaData`. |
[DefaultComesLastCheck](https://checkstyle.sourceforge.io/checks/coding/defaultcomeslast.html) | [Styling](styling_checks.md#styling-checks) | Checks that the `default` is after all the cases in a `switch` statement. |
DeprecatedAPICheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds calls to deprecated classes, constructors, fields or methods. |
DeprecatedClassesCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Replaces deprecated classes. |
EmptyCollectionCheck | [Styling](styling_checks.md#styling-checks) | Checks that there are no calls to `Collections.EMPTY_LIST`, `Collections.EMPTY_MAP` or `Collections.EMPTY_SET`. |
EmptyConstructorCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds unnecessary empty constructors. |
EnumConstantDividerCheck | [Styling](styling_checks.md#styling-checks) | Find unnecessary empty lines between enum constants. |
EnumConstantOrderCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of enum constants. |
EqualClauseIfStatementsCheck | [Styling](styling_checks.md#styling-checks) | Finds consecutive if-statements with identical clauses. |
[EqualsHashCodeCheck](https://checkstyle.sourceforge.io/checks/coding/equalshashcode.html) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that classes that either override `equals()` or `hashCode()` also overrides the other. |
[ExceptionCheck](check/exception_check.md#exceptioncheck) | [Performance](performance_checks.md#performance-checks) | Finds private methods that throw unnecessary exception. |
[ExceptionMessageCheck](check/message_check.md#messagecheck) | [Styling](styling_checks.md#styling-checks) | Validates messages that are passed to exceptions. |
ExceptionPrintStackTraceCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Avoid using printStackTrace. |
ExceptionVariableNameCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Validates variable names that have type `*Exception`. |
FactoryCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `*Factory` should be used when creating new instances of an object. |
FilterStringWhitespaceCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing and unnecessary whitespace in the value of the filter string in `ServiceTrackerFactory.open` or `WaiterUtil.waitForFilter`. |
[FrameworkBundleCheck](check/framework_bundle_check.md#frameworkbundlecheck) | [Performance](performance_checks.md#performance-checks) | Checks that `org.osgi.framework.Bundle.getHeaders()` is not used. |
FullyQualifiedNameCheck | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Finds cases where a Fully Qualified Name is used instead of importing a class. |
[GenericTypeCheck](check/generic_type_check.md#generictypecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that generics are always specified to provide compile-time checking and removing the risk of `ClassCastException` during runtime. |
[GetterUtilCheck](check/getter_util_check.md#getterutilcheck) | [Styling](styling_checks.md#styling-checks) | Finds cases where the default value is passed to `GetterUtil.get*` or `ParamUtil.get*`. |
[IfStatementCheck](check/if_statement_check.md#ifstatementcheck) | [Styling](styling_checks.md#styling-checks) | Finds empty if-statements and consecutive if-statements with identical bodies. |
IllegalImportsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases of incorrect use of certain classes. |
InnerExceptionClassCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that classes that should have either public constructors or inner classes. |
[InstanceInitializerCheck](check/instance_initializer_check.md#instanceinitializercheck) | [Styling](styling_checks.md#styling-checks) | Performs several checks on class instance initializer. |
InstanceofOrderCheck | [Styling](styling_checks.md#styling-checks) | Check the order of `instanceof` calls. |
[ItemBuilderCheck](check/builder_check.md#buildercheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Checks that `DropdownItemBuilder`, `LabelItemBuilder` or `NavigationItemBuilder` is used when possible. |
[ItemListBuilderCheck](check/builder_check.md#buildercheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Checks that `DropdownItemListBuilder`, `LabelItemListBuilder` or `NavigationItemListBuilder` is used when possible. |
JSONNamingCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks if variable names follow naming conventions. |
JSONPortletResponseUtilCheck | [Styling](styling_checks.md#styling-checks) | Checks if `JSONPortletResponseUtil.writeJSON ` should come before method calling `hideDefaultSuccessMessage`. |
[JSONUtilCheck](check/json_util_check.md#jsonutilcheck) | [Styling](styling_checks.md#styling-checks) | Checks for utilization of class `JSONUtil`. |
Java2HTMLCheck | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Finds incorrect use of `.java.html` in `.jsp` files. |
JavaAbstractMethodCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds incorrect `abstract` methods in `interface`. |
JavaAggregateTestRuleParameterOrderCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of parameters in `new AggregateTestRule` calls. |
JavaAnnotationDefaultAttributeCheck | [Styling](styling_checks.md#styling-checks) | Finds cases where the default value is passed to annotations in package `*.bnd.annotations` or `*.bind.annotations`. |
JavaAnnotationsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on annotations. |
[JavaAnonymousInnerClassCheck](check/java_anonymous_inner_class_check.md#javaanonymousinnerclasscheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on anonymous classes. |
JavaAssertEqualsCheck | [Styling](styling_checks.md#styling-checks) | Validates `Assert.assertEquals` calls. |
JavaBooleanStatementCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on variable declaration of type `Boolean`. |
JavaBooleanUsageCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds incorrect use of passing boolean values in `setAttribute` calls. |
JavaClassNameCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks if class names follow naming conventions. |
JavaCleanUpMethodSuperCleanUpCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that `cleanUp` method in `*Tag` class with `@Override` annotation calls the `cleanUp` method of the superclass. |
[JavaCleanUpMethodVariablesCheck](check/java_clean_up_method_variables_check.md#javacleanupmethodvariablescheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that variables in `Tag` classes get cleaned up properly. |
JavaCollapseImportsCheck | [Performance](performance_checks.md#performance-checks) | Collapses imports that use wildcard. |
[JavaCollatorUtilCheck](check/java_collator_util_check.md#javacollatorutilcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for correct use of `Collator`. |
JavaCompanyScopedIdsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where company scoped ids are used, see LPD-45118. |
[JavaComponentActivateCheck](check/java_component_activate_check.md#javacomponentactivatecheck) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks if methods with annotation `@Activate` or `@Deactivate` follow naming conventions. |
JavaComponentAnnotationsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on classes with `@Component` annotation. |
[JavaConfigurationAdminCheck](check/java_configuration_admin_check.md#javaconfigurationadmincheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for correct use of `location == ?` when calling `org.osgi.service.cm.ConfigurationAdmin#createFactoryConfiguration`. |
[JavaConfigurationCategoryCheck](check/java_configuration_category_check.md#javaconfigurationcategorycheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that the value of `category` in `@ExtendedObjectClassDefinition` matches the `categoryKey` of the corresponding class that implements `ConfigurationCategory`. |
JavaConstantsFileCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks if constants names follow naming conventions. |
[JavaConstructorParametersCheck](check/java_constructor_parameters_check.md#javaconstructorparameterscheck) | [Styling](styling_checks.md#styling-checks) | Checks that the order of variable assignments matches the order of the parameters in the constructor signature. |
JavaConstructorSuperCallCheck | [Styling](styling_checks.md#styling-checks) | Finds unnecessary call to no-argument constructor of the superclass. |
JavaDefaultAdminScreenNameCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that we do not use `PropsKeys.DEFAULT_ADMIN_SCREEN_NAME` or `PropsValues.DEFAULT_ADMIN_SCREEN_NAME`. |
JavaDeprecatedJavadocCheck | [Javadoc](javadoc_checks.md#javadoc-checks) | Checks if the `@deprecated` javadoc is pointing to the correct version. |
JavaDeserializationSecurityCheck | [Security](security_checks.md#security-checks) | Finds Java serialization vulnerabilities. |
JavaDiamondOperatorCheck | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Finds cases where Diamond Operator is not used. |
JavaDuplicateVariableCheck | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Finds variables where a variable with the same name already exists in an extended class. |
[JavaElseStatementCheck](check/java_else_statement_check.md#javaelsestatementcheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Finds unnecessary `else` statements (when the `if` statement ends with a `return` statement). |
JavaEmptyLineAfterSuperCallCheck | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Finds missing empty line after a `super` call. |
JavaEmptyLinesCheck | [Styling](styling_checks.md#styling-checks) | Finds missing and unnecessary empty lines. |
JavaEntityFieldsMapOrderCheck | [Styling](styling_checks.md#styling-checks) | Sorts entity fields when calling `EntityFieldsMapFactory.create` and `EntityModel.toEntityFieldsMap`. |
JavaExceptionCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that variable names of exceptions in `catch` statements follow naming conventions. |
JavaFeatureFlagManagerUtilCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `FeatureFlagManagerUtil.isEnabled` should be used and incorrect use of it. |
JavaFeatureFlagsAndTestInfoAnnotationCheck | [Styling](styling_checks.md#styling-checks) | Sorts the values in `@FeatureFlags` and `@TestInfo` annotation. |
JavaFinalVariableCheck | [Styling](styling_checks.md#styling-checks) | Finds cases of unneeded `final` modifiers for variables and parameters. |
[JavaFinderCacheCheck](check/java_finder_cache_check.md#javafindercachecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that the method `BasePersistenceImpl.fetchByPrimaryKey` is overridden, when using `FinderPath`. |
JavaFinderImplCustomSQLCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that hardcoded SQL values in `*FinderImpl` classes match the SQL in the `.xml` file in the `custom-sql` directory. |
[JavaForLoopCheck](check/java_for_loop_check.md#javaforloopcheck) | [Styling](styling_checks.md#styling-checks) | Checks if a Enhanced For Loop can be used instead of a Simple For Loop. |
[JavaHelperUtilCheck](check/java_helper_util_check.md#javahelperutilcheck) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Finds incorrect use of `*Helper` or `*Util` classes. |
JavaHibernateSQLCheck | [Performance](performance_checks.md#performance-checks) | Finds calls to `com.liferay.portal.kernel.dao.orm.Session.createSQLQuery` (use `Session.createSynchronizedSQLQuery` instead). |
JavaIOExceptionCheck | [Styling](styling_checks.md#styling-checks) | Validates use of `IOException`. |
JavaIgnoreAnnotationCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds methods with `@Ignore` annotation in test classes. |
JavaImportsCheck | [Styling](styling_checks.md#styling-checks) | Sorts and groups imports in `.java` files. |
[JavaIndexableCheck](check/java_indexable_check.md#javaindexablecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that the type gets returned when using annotation `@Indexable`. |
[JavaInitialRequestPortalInstanceLifecycleListenerCheck](check/java_initial_request_portal_instance_lifecycle_listener_check.md#javainitialrequestportalinstancelifecyclelistenercheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `InitialRequestPortalInstanceLifecycleListener` subclasses. |
JavaInjectAnnotationsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on classes with `@Inject` annotations. |
JavaInnerClassImportsCheck | [Styling](styling_checks.md#styling-checks) | Finds cases where inner classes are imported. |
JavaInterfaceCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that `interface` is not `static`. |
JavaInternalPackageCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on class in `internal` package. |
JavaJSImportMapsContributorCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `*JSImportMapsContributor` class. |
JavaJSPDynamicIncludeCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `*JSPDynamicInclude` class. |
[JavaLocalSensitiveComparisonCheck](check/java_local_sensitive_comparison_check.md#javalocalsensitivecomparisoncheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that `java.text.Collator` is used when comparing localized values. |
JavaLocalServiceImplErcUsageCheck | [Productivity](productivity_checks.md#productivity-checks) | Automatically generates and ensures that `externalReferenceCode` is properly used in local `*ServiceImpl` add method. |
[JavaLocalServiceImplUserIdUsageCheck](check/java_local_service_impl_user_id_usage_check.md#javalocalserviceimpluseridusagecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds incorrect use of `GuestOrUserUtil.getUserId()` or `PrincipalThreadLocal.getUserId()`. |
JavaLogClassNameCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks the name of the class that is passed in `LogFactoryUtil.getLog`. |
[JavaLogLevelCheck](check/java_log_level_check.md#javaloglevelcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that the correct log messages are printed. |
JavaLongLinesCheck | [Styling](styling_checks.md#styling-checks) | Finds lines that are longer than the specified maximum line length. |
JavaMapBuilderGenericsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing or unnecessary generics on `*MapBuilder.put` calls. |
[JavaMetaAnnotationsCheck](check/java_meta_annotations_check.md#javametaannotationscheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for correct use of attributes `description` and `name` in annotation `@aQute.bnd.annotation.metatype.Meta`. |
JavaMissingOverrideCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing @Override annotations. |
JavaMissingXMLPublicIdsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing public IDs for check XML files. |
JavaModifiedServiceMethodCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing empty lines before `removedService` or `addingService` calls. |
JavaModuleClassGetResourceCallCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that dependencies files are located in the correct directory. |
[JavaModuleComponentCheck](check/java_module_component_check.md#javamodulecomponentcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on classes with or without `@Component` annotation. |
[JavaModuleExposureCheck](check/java_module_exposure_check.md#javamoduleexposurecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for exposure of `SPI` types in `API`. |
JavaModuleIllegalImportsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases of incorrect use of certain classes in modules. |
JavaModuleInheritableVariableAccessModifierCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for cases where visibility of variable can be increased. |
JavaModuleInternalImportsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where a module imports an `internal` class from another class. |
JavaModuleJavaxPortletInitParamTemplatePathCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates the value of `javax.portlet.init-param.template-path`. |
JavaModuleServiceReferenceCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `@BeanReference` annotation should be used instead of `@ServiceReference` annotation. |
[JavaModuleTestCheck](check/java_module_test_check.md#javamoduletestcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks package names in tests. |
JavaModuleTestUtilCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks package name for `*TestUtil.java`. |
JavaModuleUniqueUpgradeStepRegistratorCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that a module can not have more than 1 upgrade step registrator class (class implements UpgradeStepRegistrator). |
JavaModuleUniqueVerifyProcessCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that a module can not have more than 1 verify process class (class extends VerifyProcess). |
[JavaMultiPlusConcatCheck](check/java_multi_plus_concat_check.md#javamultiplusconcatcheck) | [Performance](performance_checks.md#performance-checks) | Checks that we do not concatenate more than 3 String objects. |
JavaNewProblemInstantiationParametersCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `new Problem` can be simplified. |
[JavaOSGiReferenceCheck](check/java_osgi_reference_check.md#javaosgireferencecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on classes with `@Component` annotation. |
[JavaPackagePathCheck](check/java_package_path_check.md#javapackagepathcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that the package name matches the file location. |
[JavaProcessCallableCheck](check/java_process_callable_check.md#javaprocesscallablecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that a class implementing `ProcessCallable` assigns a `serialVersionUID`. |
JavaProviderTypeAnnotationCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on classes with `@ProviderType` annotation. |
JavaRedundantConstructorCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds unnecessary empty constructor. |
JavaReferenceAnnotationsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on classes with `@Reference` annotations. |
JavaReleaseInfoCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates information in `ReleaseInfo.java`. |
[JavaResultSetCheck](check/java_result_set_check.md#javaresultsetcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for correct use `java.sql.ResultSet.getInt(int)`. |
JavaReturnStatementCheck | [Styling](styling_checks.md#styling-checks) | Finds unnecessary `else` statement (when `if` and `else` statement both end with `return` statement). |
JavaRunSqlStylingCheck | [Styling](styling_checks.md#styling-checks) | Applies rules to enforce consistency in code style. |
[JavaSeeAnnotationCheck](check/java_see_annotation_check.md#javaseeannotationcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for nested annotations inside `@see`. |
JavaServiceImplCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Ensures that `afterPropertiesSet` and `destroy` methods in `*ServiceImpl` always call the method with the same name in the superclass. |
JavaServiceImplErcUsageCheck | [Productivity](productivity_checks.md#productivity-checks) | Automatically generates and ensures that `externalReferenceCode` is properly used in remote `*ServiceImpl` add method. |
JavaServiceObjectCheck | [Styling](styling_checks.md#styling-checks) | Checks for correct use of `*.is*` instead of `*.get*` when calling methods generated by ServiceBuilder. |
[JavaServiceTrackerFactoryCheck](check/java_service_tracker_factory_check.md#javaservicetrackerfactorycheck) | [Performance](performance_checks.md#performance-checks) | Checks that there are no calls to deprecated method `ServiceTrackerFactory.open(java.lang.Class)`. |
[JavaServiceUtilCheck](check/java_service_util_check.md#javaserviceutilcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that there are no calls to `*ServiceImpl` from a `*ServiceUtil` class. |
JavaSessionCheck | [Performance](performance_checks.md#performance-checks) | Finds unnecessary calls to `Session.flush()` (calls that are followed by `Session.clear()`). |
[JavaSignatureParametersCheck](check/java_signature_parameters_check.md#javasignatureparameterscheck) | [Styling](styling_checks.md#styling-checks) | Checks the order of parameters. |
JavaSnapshotClassNameCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks the name of the class that is passed to `Snapshot` constructor. |
JavaSourceFormatterDocumentationCheck | [Documentation](documentation_checks.md#documentation-checks) | Finds SourceFormatter checks that have no documentation. |
JavaStagedModelDataHandlerCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing method `setMvccVersion` in class extending `BaseStagedModelDataHandler` in module that has `mvcc-enabled=true` in `service.xml`. |
JavaStaticBlockCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `static` blocks. |
[JavaStaticImportsCheck](check/java_static_imports_check.md#javastaticimportscheck) | [Styling](styling_checks.md#styling-checks) | Checks that there are no static imports. |
JavaStaticMethodCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where methods are unnecessarily declared static. |
JavaStaticVariableDependencyCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that static variables in the same class that depend on each other are correctly defined. |
[JavaStopWatchCheck](check/java_stop_watch_check.md#javastopwatchcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for potential NullPointerException when using `StopWatch`. |
[JavaStringBundlerConcatCheck](check/java_string_bundler_concat_check.md#javastringbundlerconcatcheck) | [Performance](performance_checks.md#performance-checks) | Finds calls to `StringBundler.concat` with less than 3 parameters. |
JavaStringBundlerInitialCapacityCheck | [Performance](performance_checks.md#performance-checks) | Checks the initial capacity of new instances of `StringBundler`. |
JavaStringStartsWithSubstringCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for uses of `contains` followed by `substring`, which should be `startsWith` instead. |
JavaStylingCheck | [Styling](styling_checks.md#styling-checks) | Applies rules to enforce consistency in code style. |
[JavaSwitchCheck](check/java_switch_check.md#javaswitchcheck) | [Styling](styling_checks.md#styling-checks) | Checks that `if/else` statement is used instead of `switch` statement. |
JavaSystemEventAnnotationCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing method `setDeletionSystemEventStagedModelTypes` in class with annotation @SystemEvent. |
JavaSystemExceptionCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds unnecessary SystemExceptions. |
JavaTaglibMethodCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that a `*Tag` class has a `set*` and `get*` or `is*` method for each attribute. |
JavaTermDividersCheck | [Styling](styling_checks.md#styling-checks) | Finds missing or unnecessary empty lines between javaterms. |
JavaTermOrderCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of javaterms. |
JavaTermStylingCheck | [Styling](styling_checks.md#styling-checks) | Applies rules to enforce consistency in code style. |
[JavaTestMethodAnnotationsCheck](check/java_test_method_annotations_check.md#javatestmethodannotationscheck) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks if methods with test annotations follow the naming conventions. |
JavaToJSONArrayCheck | [Styling](styling_checks.md#styling-checks) | Finds missing and unnecessary whitespace on `toJSONArray` calls. |
JavaTransactionBoundaryCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds direct `add*` or `get*` calls in `*ServiceImpl` (those should use the `*service` global variable instead). |
[JavaUnsafeCastingCheck](check/java_unsafe_casting_check.md#javaunsafecastingcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for potential ClassCastException. |
[JavaUnusedSourceFormatterChecksCheck](check/java_unused_source_formatter_checks_check.md#javaunusedsourceformattercheckscheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Finds `*Check` classes that are not configured. |
[JavaUpgradeAlterCheck](check/java_upgrade_alter_check.md#javaupgradealtercheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `alter` calls in Upgrade classes. |
JavaUpgradeAlterColumnCallsOrderCheck | [Styling](styling_checks.md#styling-checks) | Sorts method calls for altering table columns. |
[JavaUpgradeClassCheck](check/java_upgrade_class_check.md#javaupgradeclasscheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on Upgrade classes. |
JavaUpgradeConnectionCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `DataAccess.getConnection` is used (instead of using the available global variable `connection`). |
[JavaUpgradeDropTableCheck](check/java_upgrade_drop_table_check.md#javaupgradedroptablecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `DROP_TABLE_IF_EXISTS` should be used (instead of `drop table if exists`). |
JavaUpgradeEmptyLinesCheck | [Styling](styling_checks.md#styling-checks) | Finds missing and unnecessary empty lines in upgrade classes. |
[JavaUpgradeIndexCheck](check/java_upgrade_index_check.md#javaupgradeindexcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where the service builder indexes are updated manually in Upgrade classes. This is not needed because Liferay takes care of it. |
JavaUpgradeMissingCTCollectionIdDuringUpdateCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing `ctCollectionId` in where clause during update. |
JavaUpgradeMissingTestCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing integration tests for upgrade classes. |
JavaUpgradeModelPermissionsCheck | [Upgrade](upgrade_checks.md#upgrade-checks) | Replace setGroupPermissions and setGuestPermissions by new implementation. |
JavaUpgradeVersionCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Verifies that the correct upgrade versions are used in classes that implement `UpgradeStepRegistrator`. |
JavaVariableTypeCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on the modifiers on variables. |
JavaVerifyUpgradeConnectionCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `DataAccess.getConnection` is used (instead of using the available global variable `connection`). |
JavaXMLSecurityCheck | [Security](security_checks.md#security-checks) | Finds possible XXE or Quadratic Blowup security vulnerabilities. |
JavadocCheck | [Javadoc](javadoc_checks.md#javadoc-checks) | Performs several checks on javadoc. |
[JavadocStyleCheck](https://checkstyle.sourceforge.io/checks/javadoc/javadocstyle.html) | [Javadoc](javadoc_checks.md#javadoc-checks) | Validates Javadoc comments to help ensure they are well formed. |
LPS42924Check | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `PortalUtil.getClassName*` (instead of calling `classNameLocalService` directly). |
[LambdaCheck](check/lambda_check.md#lambdacheck) | [Styling](styling_checks.md#styling-checks) | Checks that `lambda` statements are as simple as possible. |
LanguageKeysCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing language keys in `Language.properties`. |
[ListUtilCheck](check/list_util_check.md#listutilcheck) | [Styling](styling_checks.md#styling-checks) | Checks for utilization of class `ListUtil`. |
LiteralStringEqualsCheck | [Styling](styling_checks.md#styling-checks) | Finds cases where `Objects.equals` should be used. |
[LocalFinalVariableNameCheck](https://checkstyle.sourceforge.io/checks/naming/localfinalvariablename.html) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that local final variable names conform to a specified pattern. |
LocalPatternCheck | [Performance](performance_checks.md#performance-checks) | Checks that a `java.util.Pattern` variable is declared globally, so that it is initiated only once. |
[LocalVariableNameCheck](https://checkstyle.sourceforge.io/checks/naming/localvariablename.html) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that local, non-final variable names conform to a specified pattern. |
LocalVariableTypeInferenceCheck | [Performance](performance_checks.md#performance-checks) | Finds usage of `var` in local variable declaration. |
LocaleUtilCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `com.liferay.portal.kernel.util.LocaleUtil` should be used (instead of `java.util.Locale`). |
[LogMessageCheck](check/message_check.md#messagecheck) | [Styling](styling_checks.md#styling-checks) | Validates messages that are passed to `log.*` calls. |
LogParametersCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Validates the values of parameters passed to `_log.*` calls. |
[MVCCommandNameCheck](check/mvc_command_name_check.md#mvccommandnamecheck) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks for consistent naming for values of `mvc.command.name`. |
[MapBuilderCheck](check/builder_check.md#buildercheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Checks that `ConcurrentHashMapBuilder`, `HashMapBuilder`, `LinkedHashMapBuilder` or `TreeMapBuilder` is used when possible. |
[MapIterationCheck](check/map_iteration_check.md#mapiterationcheck) | [Performance](performance_checks.md#performance-checks) | Checks that there are no unnecessary map iterations. |
[MemberNameCheck](https://checkstyle.sourceforge.io/checks/naming/membername.html) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that instance variable names conform to a specified pattern. |
MethodCallsOrderCheck | [Styling](styling_checks.md#styling-checks) | Sorts method calls for certain object (for example, `put` calls in `java.util.HashMap`). |
MethodEqualsCheck | [Styling](styling_checks.md#styling-checks) | Finds cases where `Objects.equals` should be used. |
[MethodNameCheck](https://checkstyle.sourceforge.io/checks/naming/methodname.html) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that method names conform to a specified pattern. |
MethodNamingCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that method names follow naming conventions. |
[MethodParamPadCheck](https://checkstyle.sourceforge.io/checks/whitespace/methodparampad.html) | [Styling](styling_checks.md#styling-checks) | Checks the padding between the identifier of a method definition, constructor definition, method call, or constructor invocation; and the left parenthesis of the parameter list. |
MissingAuthorCheck | [Javadoc](javadoc_checks.md#javadoc-checks) | Finds classes that have no `@author` specified. |
[MissingDeprecatedCheck](https://checkstyle.sourceforge.io/checks/annotation/missingdeprecated.html) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Verifies that the annotation @Deprecated and the Javadoc tag @deprecated are both present when either of them is present. |
MissingDeprecatedJavadocCheck | [Javadoc](javadoc_checks.md#javadoc-checks) | Verifies that the annotation @Deprecated and the Javadoc tag @deprecated are both present when either of them is present. |
MissingDiamondOperatorCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for missing diamond operator for types that require diamond operator. |
[MissingEmptyLineCheck](check/missing_empty_line_check.md#missingemptylinecheck) | [Styling](styling_checks.md#styling-checks) | Checks for missing line breaks around variable declarations. |
MissingModifierCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Verifies that a method or global variable has a modifier specified. |
MissingParenthesesCheck | [Styling](styling_checks.md#styling-checks) | Finds missing parentheses in conditional statement. |
[ModelSetCallWithCompanyIdCheck](check/model_set_call_with_company_id_check.md#modelsetcallwithcompanyidcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for inserting the companyId as part of a varchar field in the database. |
ModifiedMethodCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for incorrect `modified` method with `@Modified` annotation. |
[ModifierOrderCheck](https://checkstyle.sourceforge.io/checks/modifier/modifierorder.html) | [Styling](styling_checks.md#styling-checks) | Checks that the order of modifiers conforms to the suggestions in the Java Language specification, § 8.1.1, 8.3.1, 8.4.3 and 9.4. |
[MultipleVariableDeclarationsCheck](https://checkstyle.sourceforge.io/checks/coding/multiplevariabledeclarations.html) | [Styling](styling_checks.md#styling-checks) | Checks that each variable declaration is in its own statement and on its own line. |
[NeedBracesCheck](https://checkstyle.sourceforge.io/checks/blocks/needbraces.html) | [Styling](styling_checks.md#styling-checks) | Checks for braces around code blocks. |
NestedFieldAnnotationCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for `nested.field.support` in the `property` attribute of the `Component` annotation. |
NestedIfStatementCheck | [Styling](styling_checks.md#styling-checks) | Finds nested if statements that can be combined. |
[NoLineWrapCheck](https://checkstyle.sourceforge.io/checks/whitespace/nolinewrap.html) | [Styling](styling_checks.md#styling-checks) | Checks that chosen statements are not line-wrapped. |
[NoWhitespaceAfterCheck](https://checkstyle.sourceforge.io/checks/whitespace/nowhitespaceafter.html) | [Styling](styling_checks.md#styling-checks) | Checks that there is no whitespace after a token. |
[NoWhitespaceBeforeCheck](https://checkstyle.sourceforge.io/checks/whitespace/nowhitespacebefore.html) | [Styling](styling_checks.md#styling-checks) | Checks that there is no whitespace before a token. |
NotRequireThisCheck | [Styling](styling_checks.md#styling-checks) | Finds cases of unnecessary use of `this.`. |
[NullAssertionInIfStatementCheck](check/null_assertion_in_if_statement_check.md#nullassertioninifstatementcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Verifies that null check should always be first in if-statement. |
NumberSuffixCheck | [Styling](styling_checks.md#styling-checks) | Verifies that uppercase `D`, `F`, or `L` is used when denoting Double/Float/Long. |
OSGiCommandsCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Perform several checks on `*OSGiCommands` classes. |
[OSGiResourceBuilderCheck](check/osgi_resource_builder_check.md#osgiresourcebuildercheck) | [Styling](styling_checks.md#styling-checks) | Avoid using *Resource.builder. |
[OneStatementPerLineCheck](https://checkstyle.sourceforge.io/checks/coding/onestatementperline.html) | [Styling](styling_checks.md#styling-checks) | Checks that there is only one statement per line. |
OperatorOperandCheck | [Styling](styling_checks.md#styling-checks) | Verifies that operand do not go over too many lines and make the operator hard to read. |
OperatorOrderCheck | [Styling](styling_checks.md#styling-checks) | Verifies that when an operator has a literal string or a number as one of the operands, it is always on the right hand side. |
[OperatorWrapCheck](https://checkstyle.sourceforge.io/checks/whitespace/operatorwrap.html) | [Styling](styling_checks.md#styling-checks) | Checks the policy on how to wrap lines on operators. |
[PackageNameCheck](https://checkstyle.sourceforge.io/checks/naming/packagename.html) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that package names conform to a specified pattern. |
[ParameterNameCheck](https://checkstyle.sourceforge.io/checks/naming/parametername.html) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that method parameter names conform to a specified pattern. |
ParsePrimitiveTypeCheck | [Performance](performance_checks.md#performance-checks) | Verifies that `GetterUtil.parse*` is used to parse primitive types, when possible. |
PersistenceCallCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds illegal persistence calls across component boundaries. |
[PersistenceUpdateCheck](check/persistence_update_check.md#persistenceupdatecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that there are no stale references in service code from persistence updates. |
PlusStatementCheck | [Styling](styling_checks.md#styling-checks) | Performs several checks to statements where `+` is used for concatenation. |
[PortletURLBuilderCheck](check/builder_check.md#buildercheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Checks that `PortletURLBuilder` is used when possible. |
PrimitiveWrapperInstantiationCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where `new Type` is used for primitive types (use `Type.valueOf` instead). |
PrincipalExceptionCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds calls to `PrincipalException.class.getName()` (use `PrincipalException.getNestedClasses()` instead). |
RESTDTOSetCallCheck | [Performance](performance_checks.md#performance-checks) | Ensures using set calls with `UnsafeSupplier` parameter for REST DTO. |
RecordClassCheck | [Performance](performance_checks.md#performance-checks) | Finds usage of `record`. |
RedundantBranchingStatementCheck | [Performance](performance_checks.md#performance-checks) | Finds unnecessary branching (`break`, `continue` or `return`) statements. |
[RedundantLogCheck](check/redundant_log_check.md#redundantlogcheck) | [Performance](performance_checks.md#performance-checks) | Finds unnecessary logs. |
ReferenceAnnotationCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on classes with @Reference annotation. |
[RequireThisCheck](https://checkstyle.sourceforge.io/checks/coding/requirethis.html) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that references to instance variables and methods of the present object are explicitly of the form 'this.varName' or 'this.methodName(args)' and that those references don't rely on the default behavior when 'this.' is absent. |
[ResourceBundleCheck](check/resource_bundle_check.md#resourcebundlecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that there are no calls to `java.util.ResourceBundle.getBundle`. |
ResourceImplCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `*ResourceImpl` classes (except `Base*ResourceImpl` classes). |
ResourcePermissionCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `*ResourcePermission` classes. |
[ResourcePermissionFactoryCheck](check/resource_permission_factory_check.md#resourcepermissionfactorycheck) | [Performance](performance_checks.md#performance-checks) | Checks usage of `*ResourcePermissionFactory` classes. |
ReturnVariableDeclarationAsUsedCheck | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Finds cases where a variable declaration should be moved. |
SealedAndNonsealedModifierCheck | [Performance](performance_checks.md#performance-checks) | Finds usage of `sealed` and `non-sealed`. |
SelfReferenceCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases of unnecessary reference to its own class. |
SemiColonCheck | [Styling](styling_checks.md#styling-checks) | Finds cases of unnecessary semicolon. |
[ServiceComponentRuntimeCheck](check/service_component_runtime_check.md#servicecomponentruntimecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks `ServiceComponentRuntime` usage in test classes. |
[ServiceProxyFactoryCheck](check/service_proxy_factory_check.md#serviceproxyfactorycheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds incorrect parameter in method call. |
ServiceUpdateCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that there are no stale references in service code from service updates. |
SessionKeysCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that messages send to `SessionsErrors` or `SessionMessages` follow naming conventions. |
SetUtilMethodsCheck | [Performance](performance_checks.md#performance-checks) | Finds cases of inefficient SetUtil operations. |
SizeIsZeroCheck | [Styling](styling_checks.md#styling-checks) | Finds cases of calls like `list.size() == 0` (use `list.isEmpty()` instead). |
[StaticBlockCheck](check/static_block_check.md#staticblockcheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on static blocks. |
[StaticVariableNameCheck](https://checkstyle.sourceforge.io/checks/naming/staticvariablename.html) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that static, non-final variable names conform to a specified pattern. |
StringBundlerNamingCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks for consistent naming on variables of type 'StringBundler'. |
StringCastCheck | [Performance](performance_checks.md#performance-checks) | Finds cases where a redundant `toString()` is called on variable type `String`. |
StringIndexOfCallCheck | [Styling](styling_checks.md#styling-checks) | Finds calls to `indexOf` (use `contains` instead). |
[StringLiteralEqualityCheck](https://checkstyle.sourceforge.io/checks/coding/stringliteralequality.html) | [Styling](styling_checks.md#styling-checks) | Checks that string literals are not used with == or !=. |
[StringMethodsCheck](check/string_methods_check.md#stringmethodscheck) | [Performance](performance_checks.md#performance-checks) | Checks if performance can be improved by using different String operation methods. |
SubstringCheck | [Performance](performance_checks.md#performance-checks) | Finds cases like `s.substring(1, s.length())` (use `s.substring(1)` instead). |
SystemEventCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing or redundant usage of @SystemEvent for delete events. |
TernaryOperatorCheck | [Styling](styling_checks.md#styling-checks) | Finds use of ternary operator in `java` files (use if statement instead). |
TestClassCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that names of test classes follow naming conventions. |
TestClassMissingLiferayUnitTestRuleCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing LiferayUnitTestRule. |
TextBlockCheck | [Styling](styling_checks.md#styling-checks) | Finds usage of text block. |
[ThreadContextClassLoaderCheck](check/thread_context_class_loader_check.md#threadcontextclassloadercheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks usage of `Thread.setContextClassLoader`. |
ThreadLocalUtilCheck | [Performance](performance_checks.md#performance-checks) | Finds new instances of `java.lang.Thread` (use `ThreadLocalUtil.create` instead). |
ThreadLocalVariableNameCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks for consistent naming on variables of type '*ThreadLocal'. |
ThreadNameCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that names of threads follow naming conventions. |
TransactionalTestRuleCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds usage of `TransactionalTestRule` in `*StagedModelDataHandlerTest`. |
TryWithResourcesCheck | [Performance](performance_checks.md#performance-checks) | Ensures using Try-With-Resources statement to properly close the resource. |
[TypeNameCheck](https://checkstyle.sourceforge.io/checks/naming/typename.html) | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that type names conform to a specified pattern. |
URLInputStreamCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks usages of `URL.openStream()`. |
[UnicodePropertiesBuilderCheck](check/builder_check.md#buildercheck) | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Checks that `UnicodePropertiesBuilder` is used when possible. |
[UnnecessaryAssignCheck](check/unnecessary_assign_check.md#unnecessaryassigncheck) | [Performance](performance_checks.md#performance-checks) | Finds unnecessary assign statements (when it is either reassigned or returned right after). |
UnnecessaryMethodCallCheck | [Styling](styling_checks.md#styling-checks) | Finds unnecessary method calls. |
UnnecessaryTypeCastCheck | [Performance](performance_checks.md#performance-checks) | Finds unnecessary Type Casting. |
[UnnecessaryVariableDeclarationCheck](check/unnecessary_variable_declaration_check.md#unnecessaryvariabledeclarationcheck) | [Performance](performance_checks.md#performance-checks) | Finds unnecessary variable declarations (when it is either reassigned or returned right after). |
UnparameterizedClassCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds `Class` instantiation without generic type. |
UnprocessedExceptionCheck | [Performance](performance_checks.md#performance-checks) | Finds cases where an `Exception` is swallowed without being processed. |
UnusedMethodCheck | [Performance](performance_checks.md#performance-checks) | Finds private methods that are not used. |
UnusedParameterCheck | [Performance](performance_checks.md#performance-checks) | Finds parameters in private methods that are not used. |
UnusedVariableCheck | [Performance](performance_checks.md#performance-checks) | Finds variables that are declared, but not used. |
UnwrappedVariableInfoCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds cases where the variable should be wrapped into an inner class in order to defer array elements initialization. |
UpgradeDeprecatedAPICheck | [Upgrade](upgrade_checks.md#upgrade-checks) | Finds calls to deprecated classes, constructors, fields or methods after an upgrade. |
[UpgradeProcessCheck](check/upgrade_process_check.md#upgradeprocesscheck) | [Performance](performance_checks.md#performance-checks) | Performs several checks on `*UpgradeProcess` classes. |
UpgradeRemovedAPICheck | [Upgrade](upgrade_checks.md#upgrade-checks) | Finds cases where calls are made to removed API after an upgrade. |
[ValidatorEqualsCheck](check/validator_equals_check.md#validatorequalscheck) | [Performance](performance_checks.md#performance-checks) | Checks that there are no calls to `Validator.equals(Object, Object)`. |
ValidatorIsNullCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Ensures that only variable of type `Long`, `Serializable` or `String` is passed to method `com.liferay.portal.kernel.util.Validator.isNull`. |
VariableDeclarationAsUsedCheck | [Performance](performance_checks.md#performance-checks) | Finds cases where a variable declaration can be inlined or moved closer to where it is used. |
VariableNameCheck | [Naming Conventions](naming_conventions_checks.md#naming-conventions-checks) | Checks that variable names follow naming conventions. |
[WhitespaceAfterCheck](https://checkstyle.sourceforge.io/checks/whitespace/whitespaceafter.html) | [Styling](styling_checks.md#styling-checks) | Checks that a token is followed by whitespace, with the exception that it does not check for whitespace after the semicolon of an empty for iterator. |
[WhitespaceAroundCheck](https://checkstyle.sourceforge.io/checks/whitespace/whitespacearound.html) | [Styling](styling_checks.md#styling-checks) | Checks that a token is surrounded by whitespace. |