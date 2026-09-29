# cb2501a618b8b2cfc7e045cc610f8bc872231fb0

The commit message is missing a breaking change. The correct message is:

```
LPD-16086: Prevent to compute values for item selector and URL fields. This commit resolves a bug where pages containing item selector or URL fields could not be imported after being exported. Previously, the full value of these fields was computed and stored, leading to data inconsistencies during import/export. For a complete and correct response format, this change should be considered in conjunction with regressions LPD-33951 and LPD-57833.

# breaking

## What modules/apps/headless/headless-delivery/headless-delivery-impl/src/main/java/com/liferay/headless/delivery/internal/dto/v1_0/mapper/PageFragmentInstanceDefinitionMapper.java

PageFragmentInstanceDefinitionMapper no longer returns fully computed values in the response directly (for item selector and URL fields). Instead, use the class PK (or external reference code, if applicable) from the response to retrieve all necessary information for the referenced resource, via the appropriate Liferay service.

## Why

This change fixes an issue with page imports.
```

----

# 3aa30f7e03264d3798731f301853ec4f952c3637

The commit message does not have the complete file path. The correct message is:

```
COMMERCE-12579 Use new find method. Also rename hasDirectReplacement.

# breaking

## What modules/apps/commerce/commerce-product-content-api/src/main/java/com/liferay/commerce/product/content/helper/CPContentHelper.java

modules/apps/commerce/commerce-product-content-api/src/main/java/com/liferay/commerce/product/content/helper/CPContentHelper.java The hasDirectReplacement method is renamed to isDirectReplacement

## Why

The method now checks whether the SKU is a replacement of another product instead of checking whether it has replacements.
```

----

# 1063732432e7a5e5d3cf782ec1652728ef053eb9

The commit message does not have the complete file path. The correct message is:

```
LPS-197315 add new param to the addExtRepositoryFileEntry method with the fileName of the file Entry

# breaking

## What modules/apps/document-library/document-library-repository-external-api/src/main/java/com/liferay/document/library/repository/external/ExtRepository.java

modules/apps/document-library/document-library-repository-external-api/src/main/java/com/liferay/document/library/repository/external/ExtRepository.java The addExtRepositoryFileEntry(String extRepositoryParentFolderKey, String mimeType, String title, String description, String changeLog, InputStream inputStream) method has a new fileName parameter.

## Why

This change is required to create the files in an sharepoint external repository with the correct source file's name.
```

----

# 2681b881ad469095e572928f265cefe2f51cdb16

The commit message does not have the complete file path. The correct message is:

```
LPS-198114 Removes unused taglib and sample since those are not used in portal and have dependencies with soy

# breaking

## What modules/apps/frontend-taglib/frontend-taglib-chart/src/main/resources/META-INF/liferay-chart.tld

All taglibs in the Liferay chart module are deleted.

## Why

DXP support for Soy was removed as part of https://liferay.atlassian.net/browse/LPS-122954 and the TemplateRendererTag and ComponentRendererTag classes were deprecated as part of https://liferay.atlassian.net/browse/LPS-122966
```

----

# 87a3c8bf38374f1987debdcedaed7f9e7a0dfdbc

The commit message does not have the complete path. The correct message is:

```
LPS-198462 Removes unused BaseClayTag

# breaking

## What modules/apps/frontend-taglib/frontend-taglib-clay/src/main/java/com/liferay/frontend/taglib/clay/servlet/taglib/base/BaseClayTag.java

The BaseClayTag class is deleted.

## Why

The class no longer has any useage.
```

----

# 678e4379fb055804a2100169b6310319d8f0d07e

The commit message has the wrong format for multiple files. The correct message is:

```
LPS-199164 Move XmlRpcUtil, Success, Fault into impl

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/xmlrpc/Success.java

Success and its related classes are moved from portal-kernel to portal-impl.

## Why

Code from portal-kernel is moving to portal-impl to reduce code complexity.

## Alternatives

Add portal-impl as a build dependency and fix the import statements to continue using the refactored classes.

----

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/xmlrpc/Fault.java

Fault and its related classes are moved from portal-kernel to portal-impl.

## Why

Code from portal-kernel is moving to portal-impl to reduce code complexity.

## Alternatives

Add portal-impl as a build dependency and fix the import statements to continue using the refactored classes.

----

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/xmlrpc/XmlRpcUtil.java

XmlRpcUtil and its related classes are moved from portal-kernel to portal-impl.

## Why

Code from portal-kernel is moving to portal-impl to reduce code complexity.

## Alternatives

Add portal-impl as a build dependency and fix the import statements to continue using the refactored classes.
```

----

# ab4a450c1d7ffe215a8d56379c787fb34c1ea41b

The commit message has the wrong format for multiple files. The correct message is:

```
LPS-198859 Remove ThreadLocalDistributor, no usage

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/util/ThreadLocalDistributor.java

The ThreadLocalDistributor class is removed.

## Why

ThreadLocalDistributor has no current usage.

----

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/util/ThreadLocalDistributorRegistry.java

The ThreadLocalDistributorRegistry class is removed.

## Why

ThreadLocalDistributor has no current usage.
```

----

# 0bfc1206ac4a93ec401be491f9553ac94ecea0ed

The commit message is missing a breaking change. The correct message is:

```
LPS-200453 Make PortletToolbar not a spring bean and provide the instance through filed INSTANCE.

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/portlet/toolbar/PortletToolbar.java

The PortletToolbar constructor is now private.

## Why

PortletToolbar is being removed from util-spring, and it needs a static INSTANCE field to replace its existing usages.

## Alternatives

Directly use PortletToolbar.INSTANCE to get the instance of PortletToolbar.
```

----

# f971716348b82b1ea6747ae3c011b40616bb5884

The commit message is missing a breaking change. The correct message is:

```
LPS-198809 Remove ModelSearchRegistrarHelper, not used anymore

# breaking

## What modules/apps/portal-search/portal-search-spi/src/main/java/com/liferay/portal/search/spi/model/registrar/ModelSearchRegistrarHelper.java

The ModelSearchRegistrarHelper class is removed.

## Why

The self-bootstrapping style *SearchRegistrar has changed so this class is no longer used.

----

# breaking

## What modules/apps/portal-search/portal-search-spi/src/main/java/com/liferay/portal/search/spi/model/registrar/contributor/ModelSearchDefinitionContributor.java

The ModelSearchDefinitionContributor class is removed.

## Why

The self-bootstrapping style *SearchRegistrar has changed so this class is no longer used.

## Alternatives

Rewrite *SearchRegistrar as an OSGi service of type ModelSearchConfigurator. Return all the previous ModelSearchConfigurator setter method parameters as your corresponding ModelSearchConfigurator getter return values.
```

----

# ce0cf3a6fab17cb1ac42b17f8bce790cbf176317

The commit message has the wrong format for multiple files. The correct message is:

```
LPS-201086 Merge AuditMessageFactoryUtil/AuditMessageFactoryImpl into AuditMessageFactory

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/audit/AuditMessageFactory.java

The logic from AuditMessageFactoryUtil and AuditMessageFactoryImpl is now in the AuditMessageFactory class.

## Why

The logic being split between multiple classes did not provide any value. They are merged into a single class to simplify the code.

----

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/audit/AuditMessageFactoryUtil.java

The logic from AuditMessageFactoryUtil and AuditMessageFactoryImpl is now in the AuditMessageFactory class.

## Why

The logic being split between multiple classes did not provide any value. They are merged into a single class to simplify the code.
```

----

# 258a63398ddedbdba27e1b193c83c30031509725

The commit message has an incorrect file path format. The correct message is:

```
LPS-200073 Remove class AssetEntriesFacet

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/search/facet/AssetEntriesFacet.java

The AssetEntriesFacet class is removed.

## Why

This class has been deprecated since 7.1.x, and its only usage in rules_user_custom_attribute_content.drl is replaced by FacetImpl.
```

----

# 76c2d3b68c19a1b33f18e9221d83f34310daed45

The commit message's file path has a typo. The correct message is:

```
LPS-196035 Avoid needing to regenerate after every screenName or emailAddress change. Use the immutable userId field for WebDAV access.

# breaking

## What portal-impl/src/com/liferay/portal/model/impl/UserImpl.java

WebDAV clients can no longer use the user's screen name, email address, or regular password when authenticating via Digest Auth.

## Why

WebDAV (or Digest Auth more generally) now requires each user to generate a separate password for this access, and it requires the user to take specific Account Settings UI actions to do so. Previously a simple web login would suffice. To avoid unexpected WebDav access rejections and simplify the user experience, now a userId is required.
```

----

# 51895916ce756437c2ae1c11a734c9e640abbb05

The commit message has an incomplete file path. The correct message is:

```
LPS-200359 Allow configure No Cache for documents and make it the default option

# breaking

## What modules/apps/document-library/document-library-web/src/main/java/com/liferay/document/library/web/internal/configuration/CacheControlConfiguration.java

The default cacheControl configuration values are changed.

## Why

The new configuration to avoid caching documents is now the default configuration to improve security.
```

----

# a35946f28515783df6d3de0a45ff8c9631dc416a

The commit message is missing a breaking change. The correct message is:

```
LPS-188270 Add new method getPortletInstanceConfiguration in class ConfigurationProviderImpl to replace method getPortletInstanceConfiguration in class PortletDisplay

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/theme/PortletDisplay.java

The getPortletInstanceConfiguration(Class<T> clazz) method is removed.

## Why

The getPortletInstanceConfiguration method should be implemented in ConfigurationProvider.

## Alternatives

Directly use ConfigurationProviderUtil.getPortletInstanceConfiguration(Class<T> clazz, ThemeDisplay themeDisplay) or reference the ConfigurationProvider service and use the same method.
```

----

# 29f42c44bfcc71b02e16edb99081a7a89fc3ceed

The commit message is missing a breaking change. The correct message is:

```
LPS-188270 Move class ConfigurationProviderUtil to module, prepare for next

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/module/configuration/ConfigurationProviderUtil.java

The ConfigurationProviderUtil class is moved from portal-kernel to the portal-configuration-module-configuration-api module.

## Why

This change ensures ConfigurationProvider is always available to the module. This allows the Snapshot class to replace ServiceProxyFactory's usage.

## Alternatives

Add portal-configuration-module-configuration-api as a build dependency to continue using the same class.
```

----

# fe131c06d9596e3eb7954a1d73876db8ad16ae7f

The commit message is missing a breaking change. The correct message is:

```
LPS-188270 Move interface ConfigurationProvider to module

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/module/configuration/ConfigurationProvider.java

The ConfigurationProvider class is moved from portal-kernel to the portal-configuration-module-configuration-api module.

## Why

This class is only used in modules.

## Alternatives

Add portal-configuration-module-configuration-api as a build dependency to continue using the same class.
```

----

# 5eeb81045e4c30d7f0b253fc3e282ec67a12c306

The commit message is missing a breaking change. The correct message is:

```
LPS-196524 Move Snapshot and its test to module portal-kernel

# breaking

## What osgi-util/src/main/java/com/liferay/osgi/util/service/Snapshot.java

The Snapshot class is moved into portal-kernel.

## Why

Snapshot is replacing all usages of ServiceProxyFactory.

## Alternatives

Use com.liferay.portal.kernel.module.service.Snapshot instead.
```

----

# 169322529677c73dbd060ea11b64a9eee56415c9

The commit message is missing a breaking change. The correct message is:

```
LPD-16492 Object fields with aggregation and formula business type should not be indexable

# breaking

## What modules/apps/object/object-service/src/main/java/com/liferay/object/service/impl/ObjectFieldLocalServiceImpl.java

The aggregation and formula object field types have new validation to avoid indexing them.

## Why

Elasticsearch does not fully support indexing these object field types because they're created at runtime.
```

----

# 64fbb2481d5a6af40fb4882fc53bacb78384069e

The breaking change message has a tab. The correct message is:

```
LPS-194004 generalize JSOnClickConfig to be passed as the default argument in the dynamically loaded JS module

# breaking

## What modules/apps/product-navigation/product-navigation-personal-menu-api/src/main/java/com/liferay/product/navigation/personal/menu/PersonalMenuEntry.java

The getJSOnClickConfigJSONObject function in PersonalMenuEntry is changed so it requires a JavaScript function to getOnClickJSModuleURL. That function is called using getJSOnClickConfigJSONObject's return value as a parameter.

## Why

This change makes getJSOnClickConfigJSONObject generic so that it can be used for any type of on-click interaction, not just for opening a selection modal.
```

----

# 1b0f30a46f323932a2b9151fd4771910f07bb0b7

The breaking change message is missing a reason. The correct message is:

```
LPD-15236 Add companyId argument to scope every request by companyId.

This is necessary because, given the following Object Definitions and companyIds:
    - C_Test & companyId 1
    - C_test & companyId 2

If we call the method with itemClassName = com.liferay.object.rest.dto.v1_0.ObjectEntry and taskItemDelegateName = C_test from a request in the companyId = 1, the method will return an instance of BatchEngineTaskItemDelegate, but thatshould not happen, as the C_test taskItemDelegateName is only defined in companyId = 2.

For that, in the following commits we need to scope by companyId all the BatchEngineTaskItemDelegates (if there is a companyId parameter, if not, it will be returned for all the companies)

# breaking

## What modules/apps/batch-engine/batch-engine-api/src/main/java/com/liferay/batch/engine/BatchEngineTaskItemDelegateRegistry.java

The method getBatchEngineTaskItemDelegate(String itemClassName, String taskItemDelegateName) is replaced with getBatchEngineTaskItemDelegate(long companyId, String itemClassName, String taskItemDelegateName).

## Why

Whether a taskItemDelegateName exists depends on the company, so BatchEngineTaskItemDelegate objects must also be retrieved with a company's scope.
```

----

# ac48b1fe243d41f6e4691e3f6e7025037811e4ac

The breaking change message has the wrong format. The correct message is:

```
LRAC-15144 segments-service Add Experiment type support

# breaking

## What modules/apps/segments/segments-service/src/main/java/com/liferay/segments/service/impl/SegmentsExperimentServiceImpl.java

The runSegmentsExperiment method has a new Experiment parameter.

## Why

This change adds support for additional Experiment types.

----

# breaking

## What modules/apps/segments/segments-service/src/main/java/com/liferay/segments/service/impl/SegmentsExperimentLocalServiceImpl.java

The runSegmentsExperiment method has a new Experiment parameter.

## Why

This change adds support for additional Experiment types.
```

----

# 0132519ea6ba7d70f64253f85a97ef1bd8f55136

The commit message is missing a breaking change. The correct message is:

```
LPS-199540 portal-impl: Remove portal property and add obsolete portal key to VerifyProperties

# breaking

## What portal-impl/src/portal.properties

The index.permission.filter.search.amplification.factor property is removed.

## Why

The index.permission.filter.search.amplification.factor property did not effectively improve permission filtering and it was only applied on the first search.

## Alternatives

The search amplification uses a better algorithm to speed permission filtering.

If the total time spent searching is still a concern, regulate it with the new Permission Filtering Time Limit at Control Panel &rarr; System Settings &rarr; Search &rarr; Default Search Result Permission Filter.
```

----

# 6de9f9ce7bd603ca6b0dbb5035c359c2c9c2ed4f

The commit message is missing a breaking change. The correct message is:

```
LPS-196539 SF rename variable name

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/theme/PortletDisplay.java

The getPortletSetup() method is renamed to getPortletPreferences(), and setPortletSetup() is renamed to setPortletPreferences().

## Why

This change makes the method names consistent with the `PortletPreferences` variable.

## Alternatives

Use getPortletPreferences() and setPortletPreferences() instead.
```

----

# 3abd46aedba5663099d5c66abd057d1f0392582f

The commit message is missing a breaking change. The correct message is:

```
LPS-197267 Move PermissionConverter to portal-security-permission-api and Remove Util

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/security/permission/PermissionConverterUtil.java

The PermissionConverterUtil class is removed.

## Why

The PermissionConverter APIs are now in portal-security-permission-api.

## Alternative

Use an OSGi service to reference PermissionConverter.
```

----

# 91ba4f2de757ad28f4129563b8a0059dad4d58ad

The commit message is missing a breaking change. The correct message is:

```
LPS-197267 Remove unused PermissionConverter overloaded methods

# breaking

## What modules/apps/portal-security/portal-security-permission-api/src/main/java/com/liferay/portal/security/permission/converter/PermissionConverter.java

The convertPermissions(long) and convertPermissions(long, PermissionConversionFilter) methods are removed from PermissionConverter.

## Why

These methods are no longer used after refactoring the PermissionConverter APIs.
```

----

# 50b57897005e337516b53e1e592b1eeee70e2950

The commit message is missing a breaking change. The correct message is:

```
LPD-47825 portal-search-web: skip deprecation for internal interface

# breaking

## What modules/apps/portal-search/portal-search-web/src/main/java/com/liferay/portal/search/web/internal/category/facet/portlet/CategoryFacetPortletPreferences.java

Vocabulary IDs are removed from CategoryFacetPortletPreferences and replaced by a paired GroupExternalReferenceCode and VocabularyExternalReferenceCode. Category Facet Widget Display Templates using portletPreferences.getValues() no longer return vocabulary IDs from the CategoryFacetPortletPreferences.

## Why

Vocabulary IDs were replaced by external reference codes in Category Facet Portlet Preferences for better data preservation during imports, exports, and data migration.

## Alternatives

Vocabulary IDs for all vocabularies related to the returned categories are available through the AssetCategoriesSearchFacetDisplayContext: use assetCategoriesSearchFacetDisplayContext.getVocabularyIds().
```

----

# 971a14cc0a58eb64a36fc88863a50141b0f09022

The commit message is missing a breaking change. The correct message is:

```
LPD-77861 Semantic versioning (gw baseline)

# breaking

## What modules/apps/frontend-taglib/frontend-taglib-clay/src/main/java/com/liferay/frontend/taglib/clay/servlet/taglib/VerticalNavTag.java

The collapse, displayType, and size attributes are added to VerticalNavTag.

## Why

These attributes were added for more flexibility when using Clay components, enabling developers to configure different aspects of the VerticalNavTag element independently. Previously, internal conditionals could produce these attributes automatically (e.g., collapse depended on the displayType), but more flexibility is necessary.

## Alternatives

The displayType attribute always works, since it defaults to "transparent". The size attribute is used as a replacement for the deprecated large flag. If the size attribute isn't provided, the large flag still works as a fallback. The collapse attribute is a new control and it's disabled by default. Provide collapse with true if the previous behavior is missing.
```

----

# 37cfb5472d42205e47807e06ff6a20fe096609bf

The commit message is missing the required breaking change format. The correct message is:

```
LPD-59218 Semantic versioning

# breaking

## What portal-kernel/src/com/liferay/portal/kernel/util/Props.java

The interface com.liferay.portal.kernel.util.Props has been removed. Its methods (contains, get, getArray, getProperties) are now directly available as static methods on com.liferay.portal.kernel.util.PropsUtil.

## Why

Props was a redundant indirection layer. PropsUtil already provided the same static API, and PropsImpl simply delegated back to PropsUtil. Removing Props eliminates the unnecessary interface and simplifies the properties access pattern.

----

# breaking

## What portal-impl/src/com/liferay/portal/util/PropsImpl.java

The class com.liferay.portal.util.PropsImpl has been removed. It was the sole implementation of com.liferay.portal.kernel.util.Props, which has also been removed.

## Why

PropsImpl was a pass-through that delegated every call to com.liferay.portal.util.PropsUtil. With Props and PropsUtil merged into com.liferay.portal.kernel.util.PropsUtil, this class is no longer needed.

----

# breaking

## What portal-impl/src/com/liferay/portal/util/PropsUtil.java

The class com.liferay.portal.util.PropsUtil has been removed. All of its functionality has been merged into com.liferay.portal.kernel.util.PropsUtil. Additionally, PropsUtil.getProps() and PropsUtil.setProps(Props) have been removed from the kernel PropsUtil. The properties are now initialized directly and cannot be swapped at runtime.

## Why

Having two PropsUtil classes (portal-impl and portal-kernel) with an intermediate Props interface created unnecessary complexity. The portal-kernel PropsUtil now loads properties directly via ConfigurationFactoryImpl, removing the need for runtime Props injection via setProps().

## Alternatives

Update imports from com.liferay.portal.util.PropsUtil to com.liferay.portal.kernel.util.PropsUtil. Replace PropsUtil.getProps() calls with direct PropsUtil static method calls. Remove PropsUtil.setProps() calls as they are no longer needed.

----

# breaking

## What portal-impl/src/com/liferay/portal/util/PropsFiles.java

The class com.liferay.portal.util.PropsFiles has been removed. Its constants (CAPTCHA, CONTENT_TYPES, PORTAL) have been inlined into ConfigurationFactoryImpl.

## Why

PropsFiles only held three string constants and was referenced in a single place. Inlining them eliminates a trivial class.

----

# breaking

## What portal-test/src/com/liferay/portal/test/rule/InitializeKernelUtilTestRule.java

InitializeKernelUtilTestRule now extends AbstractTestRule<Map<String, String>, Map<String, String>> instead of AbstractTestRule<Void, Properties>. The beforeClass method now returns Map<String, String> instead of Void, and afterClass/afterMethod accept Map<String, String> instead of Void/Properties. The rule no longer calls PropsUtil.setProps() or uses reflection to call addProperties/removeProperties.

## Why

With the removal of the Props interface and PropsUtil.setProps(), the test rule can no longer swap in a new PropsImpl. Instead it saves and restores individual property values via PropsUtil.set(String, String) and PropsUtil.get(String).

## Alternatives

Use PropsUtil.set(String, String) to set individual properties and PropsUtil.get(String) to read them back, instead of the former Props-based swapping pattern.
```

----

# 780cf785b969d493e8bc80ab19d9855abce13f52

The commit message is missing a breaking change. The correct message is:

```
LPD-71875 Stop returning fieldReference from name field for ContentStructureField

# breaking

## What modules/apps/headless/headless-delivery/headless-delivery-api/src/main/java/com/liferay/headless/delivery/dto/v1_0/util/ContentStructureUtil.java

The name field of the ContentStructureField returned by the headless-delivery REST API now contains the field's actual name instead of its fieldReference.

## Why

The name field was populated with the fieldReference rather than the field's name. It now returns the actual name, while the fieldReference remains available through the dedicated fieldReference field.

## Alternatives

Consumers that relied on the name field to obtain the fieldReference should read the fieldReference field of ContentStructureField instead.
```

----

# 1efb89cecc2466d44328f11977ed5730753b26cc

The commit message is missing a breaking change. The correct message is:

```
LPD-71875 Stop returning fieldReference from name field for ContentField

# breaking

## What modules/apps/headless/headless-delivery/headless-delivery-api/src/main/java/com/liferay/headless/delivery/dto/v1_0/util/ContentFieldUtil.java

The name field of the ContentField returned by the headless-delivery REST API now contains the field's actual name instead of its fieldReference.

## Why

The name field was populated with the fieldReference rather than the field's name. It now returns the actual name, while the fieldReference remains available through the dedicated fieldReference field.

## Alternatives

Consumers that relied on the name field to obtain the fieldReference should read the fieldReference field of ContentField instead.
```

----

# 021cb3d141646ffc1ff0b122b810f8570aeb40ce

The commit message is missing a breaking change. The correct message is:

```
LPD-107356 Regen

# breaking

## What modules/apps/object/object-api/src/main/java/com/liferay/object/service/ObjectEntryLocalService.java

Removed updateModifiedDate(long, Date).

## Why

It existed only for ObjectEntryModelListener, which now writes the root object entry's modified date through the persistence.

----

# breaking

## What modules/apps/object/object-api/src/main/java/com/liferay/object/service/ObjectEntryVersionLocalService.java

Removed updateLatestObjectEntryVersionModifiedDate(Date, long).

## Why

Its only caller was ObjectEntryLocalService.updateModifiedDate.
```