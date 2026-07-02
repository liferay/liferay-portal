# Upgrade Checks

Check | File Extensions | Description
----- | --------------- | -----------
[GradleUpgradeReleaseDXPCheck](check/gradle_upgrade_release_dxp_check.md#gradleupgradereleasedxpcheck) | .gradle | Remove and replaced dependencies in `build.gradle` that are already in `release.dxp.api` with `released.dxp.api` dependency. |
JSONUpgradeLiferayThemePackageJSONCheck | .ipynb, .json, or .npmbridgerc | Upgrade the `package.json` of a Liferay Theme to make it compatible with Liferay 7.4. |
JSPUpgradeRemovedTagsCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds removed tags when upgrading. |
JavaUpgradeModelPermissionsCheck | .java | Replace setGroupPermissions and setGuestPermissions by new implementation. |
[PropertiesUpgradeLiferayPluginPackageFileCheck](check/properties_upgrade_liferay_plugin_package_file_check.md#propertiesupgradeliferaypluginpackagefilecheck) | .eslintignore, .prettierignore, or .properties | Performs several upgrade checks in `liferay-plugin-package.properties` file. |
PropertiesUpgradeLiferayPluginPackageLiferayVersionsCheck | .eslintignore, .prettierignore, or .properties | Validates and upgrades the version in `liferay-plugin-package.properties` file. |
UpgradeBNDDeclarativeServicesCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Adds `-dsannotations-options: inherit` to `bnd.bnd` if it does not yet exist. |
UpgradeBNDIncludeResourceCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Checks if the property value `-includeresource` or `Include-Resource` exists and removes it. |
UpgradeCatchAllCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Performs replacements on Liferay's outdated code. |
[UpgradeCatchAllJSPImportsCheck](check/jsp_imports_check.md#jspimportscheck) | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Sorts and groups imports in `LPD_XXXXX.jsp` and `LPS_XXXXX.jsp` files. |
UpgradeCatchAllJavaImportsCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Sorts and groups imports in `LPD_XXXXX.java` and `LPS_XXXXX.java` files. |
UpgradeCatchAllJavaLongLinesCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Finds lines that are longer than the specified maximum line length in `LPD_XXXXX.java` and `LPS_XXXXX.java` files. |
UpgradeCatchAllJavaTermOrderCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Sorts javaterms in `LPD_XXXXX.java` and `LPS_XXXXX.java` files. |
UpgradeDeprecatedAPICheck | .java | Finds calls to deprecated classes, constructors, fields or methods after an upgrade. |
UpgradeGradleIncludeResourceCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replaces with `compileInclude` the configuration attribute for dependencies in `build.gradle` that are listed at `Include-Resource` property at `bnd.bnd` associated file. |
UpgradeImportsCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replaces deprecated package references with updated values from `imports.txt` and handles class renaming by updating variable declarations and references when a package change involves a class name change. |
UpgradeJSPFieldSetGroupCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Run code to remove 'fieldset-group' tag. |
UpgradeJavaAssetEntryAssetCategoriesCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replaces methods referring to class `AssetEntryAssetCategory` in class `AssetCategoryLocalService` with equivalent methods in class `AssetEntryAssetCategoryRelLocalService`. |
UpgradeJavaBaseFragmentCollectionContributorExtendedClassesCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Adds FragmentCollectionKey to Component annotation in classes that extend `BaseFragmentCollectionContributor` |
UpgradeJavaDDMFormValuesSerializerTrackerCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replaces the references of `DDMFormValuesSerializerTracker` class and also its methods usages. |
UpgradeJavaDisplayPageInfoItemCapabilityCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replace all references of DisplayPageInfoItemCapability to InfoItemCapability |
UpgradeJavaFDSDataProviderCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Upgrade implementations of ClayDataSetDataProvider and CommerceDataSetDataProvider to FDSDataSetDataProvider |
UpgradeJavaFacetedSearcherCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replaces the references of the `Indexer indexer = FacetedSearcher.getInstance();` declaration and `indexer.search` method call. |
UpgradeJavaFinderImplCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Add Component annotation to `*FinderImpl.java` file. |
UpgradeJavaGetFDSTableSchemaParameterCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Fill the new parameter of the method 'getFDSTableSchema' of 'FDSTableSchema'. |
UpgradeJavaGetFileMethodCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Run code migration of method from 'getFile' to 'getFileAsStream', and include a method 'FileUtil.createTempFile'. |
UpgradeJavaGetLayoutDisplayPageObjectProviderCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replace parameter type long by ItemInfoReference in the getLayoutDisplayPageObjectProvider method. |
UpgradeJavaGetLayoutDisplayPageProviderCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replace getLayoutDisplayPageProvider by getLayoutDisplayPageProviderByClassName. |
UpgradeJavaLocalServiceImplCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Add Component annotation to `*LocalServiceImpl.java` file. |
UpgradeJavaPortletIdMethodCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replace the 'document.get(Field.PORTLET_ID)' by the new interface 'PortletProviderUtil.getPortletId'. |
UpgradeJavaPortletSharedSearchSettingsCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replaces the Optional return type of the methods `getParameterValues` and `getPortletPreferences` of `PortletSharedSearchSettings` class. |
UpgradeJavaProductDTOConverterReferenceCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Updates references of `ProductDTOConverter` to `DTOConverter` |
UpgradeJavaSchedulerEntryImplConstructorCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replace constructors that use the empty constructor of the SchedulerEntryImpl class. |
UpgradeJavaScreenContributorClassCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replace class `PortalSettingsConfigurationScreenContributor` by `ConfigurationScreenWrapper` and create an inner class. |
UpgradeJavaServiceImplCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Add Component annotation to `*ServiceImpl.java` file. |
UpgradeJavaServiceReferenceAnnotationCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Run code migration to replace '@ServiceReference' by '@Reference'. |
UpgradeJavaSortFieldNameTranslatorCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Upgrade class that implements SortFieldNameTranslator. |
UpgradePortletFTLCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Include the CSS classes 'cadmin' and include for impression of 'right cadmin' in 'portlet.ftl' file. |
UpgradeRejectedExecutionHandlerCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replace Liferay's RejectedExecutionHandler with Java's RejectedExecutionHandler. |
UpgradeRemovedAPICheck | .java | Finds cases where calls are made to removed API after an upgrade. |
UpgradeSCSSMixinsCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replace outdated mixins (e.g. media-query, respond-to, etc.). |
UpgradeSCSSNodeSassPatternsCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Run code migration of Dart Sass deprecated patterns (e.g., the division operation using the '/' character, the interpolation syntax, etc.). |
UpgradeSetResultsSetTotalMethodCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Replaces methods setResults and setTotal from SearchContainer with the method setResultsAndTotal only. |
UpgradeVelocityCommentMigrationCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Run code migration of comments from a Velocity file to a Freemarker file with the syntax replacements. |
UpgradeVelocityFileImportMigrationCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Run code migration of file import from a Velocity file to a Freemarker file with the syntax replacements. |
UpgradeVelocityForeachMigrationCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Run code migration of references to Foreach statement from a Velocity file to a Freemarker file with the syntax replacements. |
UpgradeVelocityIfStatementsMigrationCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Run code migration of references to If statements from a Velocity file to a Freemarker file with the syntax replacements. |
UpgradeVelocityLiferayTaglibReferenceMigrationCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Run code migration of references to specific Liferay taglib from a Velocity file to a Freemarker file with the syntax replacements. |
UpgradeVelocityMacroDeclarationMigrationCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Run code migration of references to Macro statement from a Velocity file to a Freemarker file with the syntax replacements. |
UpgradeVelocityMacroReferenceMigrationCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Run code migration of references to a custom Macro statement from a Velocity file to a Freemarker file with the syntax replacements. |
UpgradeVelocityVariableReferenceMigrationCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Run code migration of references to variables from a Velocity file to a Freemarker file with the syntax replacements. |
UpgradeVelocityVariableSetMigrationCheck | .bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm | Run code migration of set variables from a Velocity file to a Freemarker file with the syntax replacements. |
XMLUpgradeCompatibilityVersionCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .qti, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks and upgrades the compatibility version in `*.xml` file. |
XMLUpgradeDTDVersionCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .qti, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Checks and upgrades the DTD version in `*.xml` file. |
XMLUpgradeDeclarativeServicesCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .qti, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Sets dependency-injector to ds in `service.xml` file. |
XMLUpgradeRemovedDefinitionsCheck | .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .qti, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd | Finds removed XML definitions when upgrading. |