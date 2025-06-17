# Bug Prevention Checks

Check | File Extensions | Description
----- | --------------- | -----------
AccessModifierCheck | .java | Checks for cases where visibility of methods can be decreased. |
[AnonymousClassCheck](check/anonymous_class_check.md#anonymousclasscheck) | .java | Checks for serialization issue when using anonymous class. |
ArquillianCheck | .java | Checks for correct use of `com.liferay.arquillian.extension.junit.bridge.junit.Arquillian`. |
AssertFailCheck | .java | Checks that calls to `Assert.fail` can be only used inside a try block as the last statement. |
[AvoidStarImportCheck](https://checkstyle.sourceforge.io/checks/imports/avoidstarimport.html) | .java | Checks that there are no import statements that use the * notation. |
BNDBreakingChangeCommitMessageCheck | .bnd | Checks that commit message should contain the schematized breaking changes. |
[BNDBundleActivatorCheck](check/bnd_bundle_activator_check.md#bndbundleactivatorcheck) | .bnd | Validates property value for `Bundle-Activator`. |
[BNDBundleCheck](check/bnd_bundle_check.md#bndbundlecheck) | .bnd | Validates `Liferay-Releng-*` properties. |
[BNDBundleInformationCheck](check/bnd_bundle_information_check.md#bndbundleinformationcheck) | .bnd | Validates property values for `Bundle-Version`, `Bundle-Name` and `Bundle-SymbolicName`. |
[BNDDefinitionKeysCheck](check/bnd_definition_keys_check.md#bnddefinitionkeyscheck) | .bnd | Validates definition keys in `.bnd` files. |
[BNDDirectoryNameCheck](check/bnd_directory_name_check.md#bnddirectorynamecheck) | .bnd | Checks if the directory names of the submodules match the parent module name. |
[BNDExportsCheck](check/bnd_exports_check.md#bndexportscheck) | .bnd | Checks that modules not ending with `-api`, `-client`, `-spi`, `-taglib`, `-test-util` do not export packages. |
[BNDIncludeResourceCheck](check/bnd_include_resource_check.md#bndincluderesourcecheck) | .bnd | Checks for unnecessary including of `test-classes/integration`. |
[BNDLiferayEnterpriseAppCheck](check/bnd_liferay_enterprise_app_check.md#bndliferayenterpriseappcheck) | .bnd | Checks for correct use of property `Liferay-Enterprise-App`. |
[BNDLiferayRelengBundleCheck](check/bnd_liferay_releng_bundle_check.md#bndliferayrelengbundlecheck) | .bnd | Checks if `.lfrbuild-release-src` file exists for DXP module with `Liferay-Releng-Bundle: true`. |
[BNDLiferayRelengCategoryCheck](check/bnd_liferay_releng_category_check.md#bndliferayrelengcategorycheck) | .bnd | Validates `Liferay-Releng-Category` properties. |
[BNDMultipleAppBNDsCheck](check/bnd_multiple_app_bnds_check.md#bndmultipleappbndscheck) | .bnd | Checks for duplicate `app.bnd` (when both `/apps/` and `/apps/dxp/` contain the same module). |
[BNDRangeCheck](check/bnd_range_check.md#bndrangecheck) | .bnd | Checks for use or range expressions. |
[BNDSchemaVersionCheck](check/bnd_schema_version_check.md#bndschemaversioncheck) | .bnd | Checks for incorrect use of property `Liferay-Require-SchemaVersion`. |
[BNDWebContextPathCheck](check/bnd_web_context_path_check.md#bndwebcontextpathcheck) | .bnd | Checks if the property value for `Web-ContextPath` matches the module directory. |
CDNCheck | | Checks the URL in `artifact.properties` files. |
CIMergeAndGitRepoFileCheck | .gitrepo or ci-merge | Checks that `ci-merge` and `.gitrepo` files can not be added or modified. |
CQLKeywordCheck | .cql | Checks that Cassandra keywords are upper case. |
CSPIllegalTagsCheck | .ftl, .html, .jsp, .jspf, .jspx, or .vm | Finds cases of incorrect use of certain tags. |
CSPTagIllegalAttributesCheck | .ftl, .html, .jsp, .jspf, .jspx, or .vm | Finds cases of incorrect use of tag attributes. |
CSPTagMissingAttributesCheck | .ftl, .html, .jsp, .jspf, .jspx, or .vm | Checks for missing tag attributes. |
ClassNameIdCheck | .java | Avoid caching noncompany scoped class name IDs. |
[CodeownersFileLocationCheck](check/codeowners_file_location_check.md#codeownersfilelocationcheck) | CODEOWNERS | Checks that `CODEOWNERS` files are located in `.github` directory. |
[CompanyIterationCheck](check/company_iteration_check.md#companyiterationcheck) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that `CompanyLocalService.forEachCompany` or `CompanyLocalService.forEachCompanyId` is used when iterating over companies. |
[CompanyThreadLocalCheck](check/company_thread_local_check.md#companythreadlocalcheck) | .java | Checks usage of `CompanyThreadLocal`. |
CompatClassImportsCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that classes are imported from `compat` modules, when possible. |
ComponentAnnotationCheck | .java | Performs several checks on classes with @Component annotation. |
[ComponentExposureCheck](check/component_exposure_check.md#componentexposurecheck) | .java | Avoid exposing static component. |
ConsumerTypeAnnotationCheck | .java | Performs several checks on classes with @ConsumerType annotation. |
[CreatingThreadsForDBAccessCheck](check/creating_threads_for_db_access_check.md#creatingthreadsfordbaccesscheck) | .java | Finds cases where `CompanyInheritableThreadLocalCallable` should be used when creating threads for DB access. |
DTOEnumCreationCheck | .java | Checks the creation of DTO enum. |
DatabaseMetaDataCheck | .java | Checks usages of `java.sql.DatabaseMetaData`. |
DeprecatedAPICheck | .java | Finds calls to deprecated classes, constructors, fields or methods. |
EmptyConstructorCheck | .java | Finds unnecessary empty constructors. |
[EqualsHashCodeCheck](https://checkstyle.sourceforge.io/checks/coding/equalshashcode.html) | .java | Checks that classes that either override `equals()` or `hashCode()` also overrides the other. |
ExceptionPrintStackTraceCheck | .java | Avoid using printStackTrace. |
FactoryCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds cases where `*Factory` should be used when creating new instances of an object. |
FilterStringWhitespaceCheck | .java | Finds missing and unnecessary whitespace in the value of the filter string in `ServiceTrackerFactory.open` or `WaiterUtil.waitForFilter`. |
[GenericTypeCheck](check/generic_type_check.md#generictypecheck) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that generics are always specified to provide compile-time checking and removing the risk of `ClassCastException` during runtime. |
GradleCommerceDependenciesCheck | .gradle | Checks the modules that are outside of Commerce are not allowed to depend on Commerce modules. |
[GradleDependencyArtifactsCheck](check/gradle_dependency_artifacts_check.md#gradledependencyartifactscheck) | .eslintignore, .gradle, .prettierignore, or .properties | Performs several checks on dependencies artifacts. |
GradleDependencyConfigurationCheck | .gradle | Validates the scope of dependencies in build gradle files. |
GradleDependencyVersionCheck | .gradle | Checks the version for dependencies in gradle build files. |
GradleExportedPackageDependenciesCheck | .gradle | Validates dependencies in gradle build files. |
GradleJavaVersionCheck | .gradle | Checks values of properties `sourceCompatibility` and `targetCompatibility` in gradle build files. |
GradleMissingDependenciesForUpgradeJava21Check | .gradle | Checks missing dependencies for upgrade Java 21 in gradle build files. |
GradleMissingJarManifestTaskCheck | .gradle | Finds missing `jarManifest` task when using `jarPatched` task in gradle build files. |
GradlePetraModuleDependenciesCheck | .gradle | Checks that dependencies in `petra` moudule can only contains `petra` dependencies. |
GradlePropertiesCheck | .gradle | Validates property values in gradle build files. |
GradleProvidedDependenciesCheck | .gradle | Validates the scope of dependencies in build gradle files. |
[GradleRequiredDependenciesCheck](check/gradle_required_dependencies_check.md#gradlerequireddependenciescheck) | .gradle | Validates the dependencies in `/required-dependencies/required-dependencies/build.gradle`. |
GradleRestClientDependenciesCheck | .gradle | Validates the project dependencies `.*-rest-client` can only be used for `testIntegrationImplementation`. |
GradleTestDependencyVersionCheck | .gradle | Checks the version for dependencies in gradle build files. |
GradleTestUtilDeployDirCheck | .gradle | Checks for incorrect use of `deployDir`. |
IllegalImportsCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds cases of incorrect use of certain classes. |
IllegalTaglibsCheck | .ftl, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds cases of incorrect use of certain deprecated taglibs in modules. |
[IncorrectFileLocationCheck](check/incorrect_file_location_check.md#incorrectfilelocationcheck) | | Checks that `/src/*/java/` only contains `.java` files. |
IncorrectFilePathCheck | | Checks that file path contains illegal characters. |
InnerExceptionClassCheck | .java | Checks that classes that should have either public constructors or inner classes. |
JSCompatibilityCheck | | Checks for JavaScript compatibility. |
[JSLodashDependencyCheck](check/js_lodash_dependency_check.md#jslodashdependencycheck) | .js or .jsx | Finds incorrect use of `AUI._`. |
[JSONDeprecatedPackagesCheck](check/json_deprecated_packages_check.md#jsondeprecatedpackagescheck) | .ipynb, .json, or .npmbridgerc | Finds incorrect use of deprecated packages in `package.json` files. |
JSONPackageJSONBNDVersionCheck | .ipynb, .json, or .npmbridgerc | Checks the version for dependencies in `package.json` files. |
JSONPackageJSONCheck | .ipynb, .json, or .npmbridgerc | Checks content of `package.json` files. |
JSONPackageJSONDependencyVersionCheck | .ipynb, .json, or .npmbridgerc | Checks the version for dependencies in `package.json` files. |
[JSONValidationCheck](check/json_validation_check.md#jsonvalidationcheck) | .ipynb, .json, or .npmbridgerc | Validates content of `.json` files. |
[JSPArrowFunctionCheck](check/jsp_arrow_function_check.md#jsparrowfunctioncheck) | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that there are no array functions. |
JSPGetStaticResourceURLCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks calls to `PortalUtil.getStaticResourceURL` and `getContextPath` without `getPathProxy`. |
[JSPIllegalSyntaxCheck](check/jsp_illegal_syntax_check.md#jspillegalsyntaxcheck) | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds incorrect use of `System.out.print`, `console.log` or `debugger.*` in `.jsp` files. |
[JSPIncludeCheck](check/jsp_include_check.md#jspincludecheck) | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Validates values of `include` in `.jsp` files. |
JSPLanguageKeysCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds missing language keys in `Language.properties`. |
JSPLanguageUtilCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds incorrect use of `LanguageUtil.get` in `.jsp` files. |
JSPLogFileNameCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Validates the value that is passed to `LogFactoryUtil.getLog` in `.jsp`. |
[JSPMethodCallsCheck](check/jsp_method_calls_check.md#jspmethodcallscheck) | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that type `LiferayPortletResponse` is used to call `getNamespace()`. |
[JSPMissingTaglibsCheck](check/jsp_missing_taglibs_check.md#jspmissingtaglibscheck) | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks for missing taglibs. |
[JSPSendRedirectCheck](check/jsp_send_redirect_check.md#jspsendredirectcheck) | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that there are no calls to `HttpServletResponse.sendRedirect` from `jsp` files. |
JSPSessionKeysCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that messages send to `SessionsErrors` or `SessionMessages` follow naming conventions. |
JSPTagAttributesCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Performs several checks on tag attributes. |
JSPTaglibMissingAttributesCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks for missing taglib attributes. |
JavaAbstractMethodCheck | .java | Finds incorrect `abstract` methods in `interface`. |
JavaAnnotationsCheck | .java | Performs several checks on annotations. |
[JavaAnonymousInnerClassCheck](check/java_anonymous_inner_class_check.md#javaanonymousinnerclasscheck) | .java | Performs several checks on anonymous classes. |
JavaBooleanStatementCheck | .java | Performs several checks on variable declaration of type `Boolean`. |
JavaBooleanUsageCheck | .java | Finds incorrect use of passing boolean values in `setAttribute` calls. |
JavaCleanUpMethodSuperCleanUpCheck | .java | Checks that `cleanUp` method in `*Tag` class with `@Override` annotation calls the `cleanUp` method of the superclass. |
[JavaCleanUpMethodVariablesCheck](check/java_clean_up_method_variables_check.md#javacleanupmethodvariablescheck) | .java | Checks that variables in `Tag` classes get cleaned up properly. |
[JavaCollatorUtilCheck](check/java_collator_util_check.md#javacollatorutilcheck) | .java | Checks for correct use of `Collator`. |
JavaCompanyScopedIdsCheck | .java | Finds cases where company scoped ids are used, see LPD-45118. |
JavaComponentAnnotationsCheck | .java | Performs several checks on classes with `@Component` annotation. |
[JavaConfigurationAdminCheck](check/java_configuration_admin_check.md#javaconfigurationadmincheck) | .java | Checks for correct use of `location == ?` when calling `org.osgi.service.cm.ConfigurationAdmin#createFactoryConfiguration`. |
[JavaConfigurationCategoryCheck](check/java_configuration_category_check.md#javaconfigurationcategorycheck) | .java | Checks that the value of `category` in `@ExtendedObjectClassDefinition` matches the `categoryKey` of the corresponding class in `configuration-admin-web`. |
JavaDeprecatedKernelClassesCheck | .java | Finds calls to deprecated classes `com.liferay.portal.kernel.util.CharPool` and `com.liferay.portal.kernel.util.StringPool`. |
JavaFeatureFlagManagerUtilCheck | .java | Finds cases where `FeatureFlagManagerUtil.isEnabled` should be used. |
[JavaFinderCacheCheck](check/java_finder_cache_check.md#javafindercachecheck) | .java | Checks that the method `BasePersistenceImpl.fetchByPrimaryKey` is overridden, when using `FinderPath`. |
JavaFinderImplCustomSQLCheck | .java | Checks that hardcoded SQL values in `*FinderImpl` classes match the SQL in the `.xml` file in the `custom-sql` directory. |
JavaIgnoreAnnotationCheck | .java | Finds methods with `@Ignore` annotation in test classes. |
[JavaIndexableCheck](check/java_indexable_check.md#javaindexablecheck) | .java | Checks that the type gets returned when using annotation `@Indexable`. |
[JavaInitialRequestPortalInstanceLifecycleListenerCheck](check/java_initial_request_portal_instance_lifecycle_listener_check.md#javainitialrequestportalinstancelifecyclelistenercheck) | .java | Performs several checks on `InitialRequestPortalInstanceLifecycleListener` subclasses. |
JavaInjectAnnotationsCheck | .java | Performs several checks on classes with `@Inject` annotations. |
JavaInterfaceCheck | .java | Checks that `interface` is not `static`. |
JavaInternalPackageCheck | .java | Performs several checks on class in `internal` package. |
JavaJSImportMapsContributorCheck | .java | Performs several checks on `*JSImportMapsContributor` class. |
JavaJSPDynamicIncludeCheck | .java | Performs several checks on `*JSPDynamicInclude` class. |
[JavaLocalSensitiveComparisonCheck](check/java_local_sensitive_comparison_check.md#javalocalsensitivecomparisoncheck) | .java | Checks that `java.text.Collator` is used when comparing localized values. |
JavaLogClassNameCheck | .java | Checks the name of the class that is passed in `LogFactoryUtil.getLog`. |
[JavaLogLevelCheck](check/java_log_level_check.md#javaloglevelcheck) | .java | Checks that the correct log messages are printed. |
JavaMapBuilderGenericsCheck | .java | Finds missing or unnecessary generics on `*MapBuilder.put` calls. |
[JavaMetaAnnotationsCheck](check/java_meta_annotations_check.md#javametaannotationscheck) | .java | Checks for correct use of attributes `description` and `name` in annotation `@aQute.bnd.annotation.metatype.Meta`. |
JavaMissingOverrideCheck | .java | Finds missing @Override annotations. |
JavaMissingXMLPublicIdsCheck | .java | Finds missing public IDs for check XML files. |
JavaModifiedServiceMethodCheck | .java | Finds missing empty lines before `removedService` or `addingService` calls. |
JavaModuleClassGetResourceCallCheck | .java | Checks that dependencies files are located in the correct directory. |
[JavaModuleComponentCheck](check/java_module_component_check.md#javamodulecomponentcheck) | .java | Performs several checks on classes with or without `@Component` annotation. |
[JavaModuleExposureCheck](check/java_module_exposure_check.md#javamoduleexposurecheck) | .java | Checks for exposure of `SPI` types in `API`. |
JavaModuleIllegalImportsCheck | .java | Finds cases of incorrect use of certain classes in modules. |
JavaModuleInheritableVariableAccessModifierCheck | .java | Checks for cases where visibility of variable can be increased. |
JavaModuleInternalImportsCheck | .java | Finds cases where a module imports an `internal` class from another class. |
JavaModuleJavaxPortletInitParamTemplatePathCheck | .java | Validates the value of `javax.portlet.init-param.template-path`. |
JavaModuleServiceReferenceCheck | .java | Finds cases where `@BeanReference` annotation should be used instead of `@ServiceReference` annotation. |
[JavaModuleTestCheck](check/java_module_test_check.md#javamoduletestcheck) | .java | Checks package names in tests. |
JavaModuleTestUtilCheck | .java | Checks package name for `*TestUtil.java`. |
JavaModuleUniqueUpgradeStepRegistratorCheck | .java | Checks that a module can not have more than 1 upgrade step registrator class (class implements UpgradeStepRegistrator). |
JavaModuleUniqueVerifyProcessCheck | .java | Checks that a module can not have more than 1 verify process class (class extends VerifyProcess). |
JavaNewProblemInstantiationParametersCheck | .java | Finds cases where `new Problem` can be simplified. |
[JavaOSGiReferenceCheck](check/java_osgi_reference_check.md#javaosgireferencecheck) | .java | Performs several checks on classes with `@Component` annotation. |
[JavaPackagePathCheck](check/java_package_path_check.md#javapackagepathcheck) | .java | Checks that the package name matches the file location. |
[JavaProcessCallableCheck](check/java_process_callable_check.md#javaprocesscallablecheck) | .java | Checks that a class implementing `ProcessCallable` assigns a `serialVersionUID`. |
JavaProviderTypeAnnotationCheck | .java | Performs several checks on classes with `@ProviderType` annotation. |
JavaRedundantConstructorCheck | .java | Finds unnecessary empty constructor. |
JavaReferenceAnnotationsCheck | .java | Performs several checks on classes with `@Reference` annotations. |
JavaReleaseInfoCheck | .java | Validates information in `ReleaseInfo.java`. |
[JavaResultSetCheck](check/java_result_set_check.md#javaresultsetcheck) | .java | Checks for correct use `java.sql.ResultSet.getInt(int)`. |
[JavaSeeAnnotationCheck](check/java_see_annotation_check.md#javaseeannotationcheck) | .java | Checks for nested annotations inside `@see`. |
JavaServiceImplCheck | .java | Ensures that `afterPropertiesSet` and `destroy` methods in `*ServiceImpl` always call the method with the same name in the superclass. |
[JavaServiceUtilCheck](check/java_service_util_check.md#javaserviceutilcheck) | .java | Checks that there are no calls to `*ServiceImpl` from a `*ServiceUtil` class. |
JavaSnapshotClassNameCheck | .java | Checks the name of the class that is passed to `Snapshot` constructor. |
JavaStagedModelDataHandlerCheck | .java | Finds missing method `setMvccVersion` in class extending `BaseStagedModelDataHandler` in module that has `mvcc-enabled=true` in `service.xml`. |
JavaStaticBlockCheck | .java | Performs several checks on `static` blocks. |
JavaStaticMethodCheck | .java | Finds cases where methods are unnecessarily declared static. |
JavaStaticVariableDependencyCheck | .java | Checks that static variables in the same class that depend on each other are correctly defined. |
[JavaStopWatchCheck](check/java_stop_watch_check.md#javastopwatchcheck) | .java | Checks for potential NullPointerException when using `StopWatch`. |
JavaStringStartsWithSubstringCheck | .java | Checks for uses of `contains` followed by `substring`, which should be `startsWith` instead. |
JavaSystemEventAnnotationCheck | .java | Finds missing method `setDeletionSystemEventStagedModelTypes` in class with annotation @SystemEvent. |
JavaSystemExceptionCheck | .java | Finds unnecessary SystemExceptions. |
JavaTaglibMethodCheck | .java | Checks that a `*Tag` class has a `set*` and `get*` or `is*` method for each attribute. |
JavaTransactionBoundaryCheck | .java | Finds direct `add*` or `get*` calls in `*ServiceImpl` (those should use the `*service` global variable instead). |
[JavaUnsafeCastingCheck](check/java_unsafe_casting_check.md#javaunsafecastingcheck) | .java | Checks for potential ClassCastException. |
[JavaUpgradeAlterCheck](check/java_upgrade_alter_check.md#javaupgradealtercheck) | .java | Performs several checks on `alter` calls in Upgrade classes. |
[JavaUpgradeClassCheck](check/java_upgrade_class_check.md#javaupgradeclasscheck) | .java | Performs several checks on Upgrade classes. |
JavaUpgradeConnectionCheck | .java | Finds cases where `DataAccess.getConnection` is used (instead of using the available global variable `connection`). |
[JavaUpgradeDropTableCheck](check/java_upgrade_drop_table_check.md#javaupgradedroptablecheck) | .java | Finds cases where `DROP_TABLE_IF_EXISTS` should be used (instead of `drop table if exists`). |
[JavaUpgradeIndexCheck](check/java_upgrade_index_check.md#javaupgradeindexcheck) | .java | Finds cases where the service builder indexes are updated manually in Upgrade classes. This is not needed because Liferay takes care of it. |
JavaUpgradeMissingCTCollectionIdDuringUpdateCheck | .java | Finds missing `ctCollectionId` in where clause during update. |
JavaUpgradeMissingTestCheck | .java | Finds missing integration tests for upgrade classes. |
JavaUpgradeVersionCheck | .java | Verifies that the correct upgrade versions are used in classes that implement `UpgradeStepRegistrator`. |
JavaVariableTypeCheck | .java | Performs several checks on the modifiers on variables. |
JavaVerifyUpgradeConnectionCheck | .java | Finds cases where `DataAccess.getConnection` is used (instead of using the available global variable `connection`). |
LFRBuildContentCheck | .lfrbuild-* | Finds `.lfrbuild*` files that are not empty. |
LPS42924Check | .java | Finds cases where `PortalUtil.getClassName*` (instead of calling `classNameLocalService` directly). |
LanguageKeysCheck | .java, .js, or .jsx | Finds missing language keys in `Language.properties`. |
LibraryVulnerabilitiesCheck | .gradle, .gradle, .gradle, .gradle, .json, .json, .properties, .properties, .xml, or .xml | Checks the introduction of libraries and third party components with known vulnerabilities. |
LocaleUtilCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds cases where `com.liferay.portal.kernel.util.LocaleUtil` should be used (instead of `java.util.Locale`). |
LogParametersCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Validates the values of parameters passed to `_log.*` calls. |
[MissingDeprecatedCheck](https://checkstyle.sourceforge.io/checks/annotation/missingdeprecated.html) | .java | Verifies that the annotation @Deprecated and the Javadoc tag @deprecated are both present when either of them is present. |
MissingDiamondOperatorCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks for missing diamond operator for types that require diamond operator. |
MissingModifierCheck | .java | Verifies that a method or global variable has a modifier specified. |
ModifiedMethodCheck | .java | Checks for incorrect `modified` method with `@Modified` annotation. |
NestedFieldAnnotationCheck | .java | Checks for `nested.field.support` in the `property` attribute of the `Component` annotation. |
[NullAssertionInIfStatementCheck](check/null_assertion_in_if_statement_check.md#nullassertioninifstatementcheck) | .java | Verifies that null check should always be first in if-statement. |
OSGiCommandsCheck | .java | Perform several checks on `*OSGiCommands` classes. |
PackageinfoBNDExportPackageCheck | packageinfo | Finds legacy `packageinfo` files. |
PersistenceCallCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds illegal persistence calls across component boundaries. |
[PersistenceUpdateCheck](check/persistence_update_check.md#persistenceupdatecheck) | .java | Checks that there are no stale references in service code from persistence updates. |
PoshiDependenciesFileLocationCheck | .function, .jar, .lar, .macro, .path, .testcase, .war, or .zip | Checks that dependencies files are located in the correct directory. |
PoshiPropsUtilCheck | .function, .jar, .lar, .macro, .path, .testcase, .war, or .zip | Finds cases where `PropsUtil.get` should be inlined. |
PoshiSmokeTestCheck | .function, .jar, .lar, .macro, .path, .testcase, .war, or .zip | Checks for missing and unnecessary `property ci.retries.disabled = true` in smoke test. |
PrimitiveWrapperInstantiationCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds cases where `new Type` is used for primitive types (use `Type.valueOf` instead). |
PrincipalExceptionCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds calls to `PrincipalException.class.getName()` (use `PrincipalException.getNestedClasses()` instead). |
PropertiesArchivedModulesCheck | .eslintignore, .prettierignore, or .properties | Finds `test.batch.class.names.includes` property value pointing to archived modules in `test.properties`. |
PropertiesBuildIncludeDirsCheck | .eslintignore, .prettierignore, or .properties | Verifies property value of `build.include.dirs` in `build.properties`. |
PropertiesFeatureFlagsCheck | .eslintignore, .prettierignore, or .properties | Generate feature flags in `portal.properties` file. |
PropertiesImportedFilesContentCheck | .eslintignore, .prettierignore, or .properties | Performs several checks on `imported-files.properties` file. |
[PropertiesLanguageKeysCheck](check/properties_language_keys_check.md#propertieslanguagekeyscheck) | .eslintignore, .prettierignore, or .properties | Checks that there is no HTML markup in language keys. |
PropertiesLanguageKeysContextCheck | .eslintignore, .prettierignore, or .properties | Checks if the language keys include a word of context to indicate specific meaning. |
PropertiesLiferayPluginPackageFileCheck | .eslintignore, .prettierignore, or .properties | Performs several checks on `liferay-plugin-package.properties` file. |
PropertiesLiferayPluginPackageLiferayVersionsCheck | .eslintignore, .prettierignore, or .properties | Validates the version in `liferay-plugin-package.properties` file. |
PropertiesPlaywrightTestCheck | .eslintignore, .prettierignore, or .properties | Performs several checks on `test.properties` for Playwright test. |
PropertiesPortalFileCheck | .eslintignore, .prettierignore, or .properties | Performs several checks on `portal.properties` or `portal-*.properties` file. |
PropertiesPortletFileCheck | .eslintignore, .prettierignore, or .properties | Performs several checks on `portlet.properties` file. |
PropertiesReleaseBuildCheck | .eslintignore, .prettierignore, or .properties | Verifies that the information in `release.properties` matches the information in `ReleaseInfo.java`. |
PropertiesServiceKeysCheck | .eslintignore, .prettierignore, or .properties | Finds usage of legacy properties in `service.properties`. |
PropertiesSourceFormatterContentCheck | .eslintignore, .prettierignore, or .properties | Performs several checks on `source-formatter.properties` file. |
PropertiesSourceFormatterFileCheck | .eslintignore, .prettierignore, or .properties | Performs several checks on `source-formatter.properties` file. |
PropertiesVerifyPropertiesCheck | .eslintignore, .prettierignore, or .properties | Finds usage of legacy properties in `portal.properties` or `system.properties`. |
ReferenceAnnotationCheck | .java | Performs several checks on classes with @Reference annotation. |
[RequireThisCheck](https://checkstyle.sourceforge.io/checks/coding/requirethis.html) | .java | Checks that references to instance variables and methods of the present object are explicitly of the form 'this.varName' or 'this.methodName(args)' and that those references don't rely on the default behavior when 'this.' is absent. |
[ResourceBundleCheck](check/resource_bundle_check.md#resourcebundlecheck) | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Checks that there are no calls to `java.util.ResourceBundle.getBundle`. |
ResourceImplCheck | .java | Performs several checks on `*ResourceImpl` classes (except `Base*ResourceImpl` classes). |
ResourcePermissionCheck | .java | Performs several checks on `*ResourcePermission` classes. |
[SQLLongNamesCheck](check/sql_long_names_check.md#sqllongnamescheck) | .sql | Checks for table and column names that exceed 30 characters. |
SelfReferenceCheck | .java | Finds cases of unnecessary reference to its own class. |
[ServiceComponentRuntimeCheck](check/service_component_runtime_check.md#servicecomponentruntimecheck) | .java | Checks `ServiceComponentRuntime` usage in test classes. |
[ServiceProxyFactoryCheck](check/service_proxy_factory_check.md#serviceproxyfactorycheck) | .java | Finds incorrect parameter in method call. |
ServiceUpdateCheck | .java | Checks that there are no stale references in service code from service updates. |
[StaticBlockCheck](check/static_block_check.md#staticblockcheck) | .java | Performs several checks on static blocks. |
SystemEventCheck | .java | Finds missing or redundant usage of @SystemEvent for delete events. |
TLDTypeCheck | .tld | Ensures the fully qualified name is used for types in `.tld` file. |
TSConfigFileCheck | .ts or .tsx | Performs several checks on `ts.config` file. |
TSSpecFileLocationCheck | .ts or .tsx | Checks that `*.spec.ts` file should be inside a folder that contains a `config.ts`. |
TestClassMissingLiferayUnitTestRuleCheck | .java | Finds missing LiferayUnitTestRule. |
[ThreadContextClassLoaderCheck](check/thread_context_class_loader_check.md#threadcontextclassloadercheck) | .java | Checks usage of `Thread.setContextClassLoader`. |
TransactionalTestRuleCheck | .java | Finds usage of `TransactionalTestRule` in `*StagedModelDataHandlerTest`. |
URLInputStreamCheck | .java | Checks usages of `URL.openStream()`. |
UnparameterizedClassCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds `Class` instantiation without generic type. |
UnwrappedVariableInfoCheck | .java | Finds cases where the variable should be wrapped into an inner class in order to defer array elements initialization. |
ValidatorIsNullCheck | .java, .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Ensures that only variable of type `Long`, `Serializable` or `String` is passed to method `com.liferay.portal.kernel.util.Validator.isNull`. |
XMLBuildFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Performs several checks on `build.xml`. |
XMLCDATACheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Performs several checks on `CDATA` inside `xml`. |
XMLCheckstyleFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Performs several checks on `checkstyle.xml` file. |
XMLLookAndFeelCompatibilityVersionCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Finds missing attribute `version` in `compatibility` element in `*--look-and-feel.xml` file. |
XMLPortletFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Performs several checks on `portlet.xml` file. |
XMLPoshiFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Performs several checks on poshi files. |
XMLProjectElementCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks the project name in `.pom` file. |
XMLServiceAutoImportDefaultReferencesCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks that the `auto-import-default-references` in `service.xml` does not equal `false`. |
[XMLServiceEntityNameCheck](check/xml_service_entity_name_check.md#xmlserviceentitynamecheck) | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks that the `entity name` in `service.xml` does not equal the `package name`. |
XMLServiceFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Performs several checks on `service.xml` file. |
[XMLServiceFinderNameCheck](check/xml_service_finder_name_check.md#xmlservicefindernamecheck) | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks that the `finder name` in `service.xml`. |
XMLServiceReferenceCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks for unused references in `service.xml` file. |
XMLSourcechecksFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Performs several checks on `sourcechecks.xml` file. |
XMLSuppressionsFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Performs several checks on `source-formatter-suppressions.xml` file. |
XMLTagAttributesCheck | .action, .function, .html, .jelly, .jrxml, .macro, .path, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Performs several checks on tag attributes. |
XMLWebFileCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Performs several checks on `web.xml` file. |
YMLRESTConfigFileBreakingChangeCommitMessageCheck | .tpl, .yaml, or .yml | Checks that commit message should contain the schematized breaking changes. |