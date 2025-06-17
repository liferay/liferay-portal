# Checks for .action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd

Check | Category | Description
----- | -------- | -----------
XMLBuildFileCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `build.xml`. |
XMLCDATACheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `CDATA` inside `xml`. |
XMLCheckstyleFileCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `checkstyle.xml` file. |
XMLCustomSQLOrderCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of attributes in `custom-sql` file. |
XMLCustomSQLStylingCheck | [Styling](styling_checks.md#styling-checks) | Applies rules to enforce consistency in code style for `.xml` files in directory `custom-sql`. |
XMLDDLStructuresFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of attributes in `-structures.xml` file. |
XMLDTDVersionCheck | [Styling](styling_checks.md#styling-checks) | Checks the DTD version in `*.xml` file. |
[XMLEchoMessageCheck](check/xml_echo_message_check.md#xmlechomessagecheck) | [Styling](styling_checks.md#styling-checks) | Checks the echo message attributes in `*.xml` file. |
XMLEmptyLinesCheck | [Styling](styling_checks.md#styling-checks) | Finds missing and unnecessary empty lines. |
XMLFSBExcludeFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of attributes in `fsb-exclude.xml` file. |
XMLFriendlyURLRoutesFileCheck | [Styling](styling_checks.md#styling-checks) | Performs several checks on `*-routes.xml` file. |
XMLHBMFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of imports in `*-hbm.xml` file. |
XMLImportsCheck | [Styling](styling_checks.md#styling-checks) | Sorts and groups imports in `.xml` files. |
XMLIndentationCheck | [Styling](styling_checks.md#styling-checks) | Finds incorrect indentation in `.xml` files. |
XMLInstanceWrappersFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of instance-wrappers in `instance_wrappers.xml` file. |
XMLIvyFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of dependencies in `ivy.xml` file. |
XMLJakartaTransformCheck | [JakartaTransform](jakarta_transform_checks.md#jakartatransform-checks) | Performs replacements for use of Jakarta. |
XMLLog4jFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of categories in `*-log4j.xml` file. |
XMLLog4jLoggersCheck | [Miscellaneous](miscellaneous_checks.md#miscellaneous-checks) | Checks the loggers defined in `*-log4j.xml` file. |
XMLLookAndFeelCompatibilityVersionCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Finds missing attribute `version` in `compatibility` element in `*--look-and-feel.xml` file. |
XMLLookAndFeelFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of attributes in `*--look-and-feel.xml` file. |
XMLModelHintsFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of attributes in `*-model-hints.xml` file. |
XMLPomFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of dependencies in `pom.xml` file. |
XMLPortletFileCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `portlet.xml` file. |
XMLPortletPreferencesFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of elements in files in directory `resource-actions`. |
XMLPoshiFileCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on poshi files. |
XMLProjectElementCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks the project name in `.pom` file. |
XMLResourceActionsFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of elements in files in directory `resource-actions`. |
XMLServiceAutoImportDefaultReferencesCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that the `auto-import-default-references` in `service.xml` does not equal `false`. |
[XMLServiceEntityNameCheck](check/xml_service_entity_name_check.md#xmlserviceentitynamecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that the `entity name` in `service.xml` does not equal the `package name`. |
XMLServiceFileCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `service.xml` file. |
[XMLServiceFinderNameCheck](check/xml_service_finder_name_check.md#xmlservicefindernamecheck) | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks that the `finder name` in `service.xml`. |
XMLServiceReferenceCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Checks for unused references in `service.xml` file. |
XMLSolrSchemaFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of elements in `portlet-preferences.xml` file. |
XMLSourcechecksFileCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `sourcechecks.xml` file. |
XMLSpringExtenderServiceCheck | [Performance](performance_checks.md#performance-checks) | Finds cases where Spring extender service is used as a dependency injection. |
XMLSpringFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of elements in `*-spring.xml` file. |
XMLStrutsConfigFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of elements in `struts-config.xml` file. |
XMLStylingCheck | [Styling](styling_checks.md#styling-checks) | Applies rules to enforce consistency in code style. |
XMLSuppressionsFileCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `source-formatter-suppressions.xml` file. |
XMLTagAttributesCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on tag attributes. |
XMLTestIgnorableErrorLinesFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of elements in `test-ignorable-error-lines.xml` file. |
XMLTilesDefsFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of elements in `tiles-defs.xml` file. |
XMLToggleFileCheck | [Styling](styling_checks.md#styling-checks) | Checks the order of elements in `.toggle` file. |
XMLUpgradeCompatibilityVersionCheck | [Upgrade](upgrade_checks.md#upgrade-checks) | Checks and upgrades the compatibility version in `*.xml` file. |
XMLUpgradeDTDVersionCheck | [Upgrade](upgrade_checks.md#upgrade-checks) | Checks and upgrades the DTD version in `*.xml` file. |
XMLUpgradeDeclarativeServicesCheck | [Upgrade](upgrade_checks.md#upgrade-checks) | Sets dependency-injector to ds in `service.xml` file. |
XMLUpgradeRemovedDefinitionsCheck | [Upgrade](upgrade_checks.md#upgrade-checks) | Finds removed XML definitions when upgrading. |
XMLWebFileCheck | [Bug Prevention](bug_prevention_checks.md#bug-prevention-checks) | Performs several checks on `web.xml` file. |
XMLWhitespaceCheck | [Styling](styling_checks.md#styling-checks) | Finds missing and unnecessary whitespace in `.xml` files. |
XMLWorkflowDefinitionFileNameCheck | [Styling](styling_checks.md#styling-checks) | Checks the file name of workflow definition files. |
XMLWorkflowDefinitionFileStylingCheck | [Styling](styling_checks.md#styling-checks) | Applies rules to enforce consistency in code style in `*workflow-definition.xml` files. |