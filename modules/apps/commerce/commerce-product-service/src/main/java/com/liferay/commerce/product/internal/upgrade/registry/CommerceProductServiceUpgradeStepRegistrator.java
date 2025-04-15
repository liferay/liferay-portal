/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.registry;

import com.liferay.account.settings.AccountEntryGroupSettings;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.internal.upgrade.v1_10_1.CommerceSiteTypeUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v1_11_0.CPAttachmentFileEntryGroupUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v1_11_1.CPDisplayLayoutUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v1_3_0.CPDefinitionLinkUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v1_3_0.CPFriendlyURLEntryUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v1_3_0.CPInstanceUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v1_3_0.CProductUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v1_5_0.CProductExternalReferenceCodeUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v1_6_0.CPDefinitionTrashEntriesUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v1_6_0.CommerceCatalogUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v1_6_0.util.CommerceCatalogTable;
import com.liferay.commerce.product.internal.upgrade.v1_7_0.CPDefinitionFiltersUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v2_0_0.CPInstanceOptionValueRelUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v2_2_0.CPDefinitionOptionValueRelUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v2_3_0.CommerceChannelUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v2_5_0.FriendlyURLEntryUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v3_9_2.MiniumSiteInitializerUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v4_0_0.util.CommerceChannelAccountEntryRelTable;
import com.liferay.commerce.product.internal.upgrade.v4_0_2.CommerceRepositoryUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v5_11_0.CPAttachmentFileEntryGalleryEnabledUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v5_11_1.ProductDefinitionConfigurationUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v5_14_0.CPDefinitionSpecificationOptionValueUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v5_21_0.util.CPConfigurationEntryTable;
import com.liferay.commerce.product.internal.upgrade.v5_21_0.util.CPConfigurationListTable;
import com.liferay.commerce.product.internal.upgrade.v5_22_0.CPSpecificationOptionUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v5_25_0.util.CPConfigurationListRelTable;
import com.liferay.commerce.product.internal.upgrade.v5_26_0.util.CPConfigurationEntrySettingTable;
import com.liferay.commerce.product.internal.upgrade.v5_4_0.CommercePermissionUpgradeProcess;
import com.liferay.commerce.product.internal.upgrade.v5_5_0.util.CPInstanceUnitOfMeasureTable;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseUuidUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portlet.display.template.upgrade.BaseUpgradePortletPreferences;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ethan Bustad
 * @author Alessio Antonio Rendina
 * @author Igor Beslic
 */
@Component(service = UpgradeStepRegistrator.class)
public class CommerceProductServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		if (_log.isInfoEnabled()) {
			_log.info("Commerce product upgrade step registrator started");
		}

		registry.register("1.0.0", "1.1.0", new DummyUpgradeProcess());

		registry.register(
			"1.1.0", "1.2.0",
			UpgradeProcessFactory.addColumns(
				"CPDefinition", "subscriptionEnabled BOOLEAN",
				"subscriptionLength INTEGER", "subscriptionType VARCHAR(75)",
				"subscriptionTypeSettings TEXT", "maxSubscriptionCycles LONG"),
			UpgradeProcessFactory.addColumns(
				"CPInstance", "overrideSubscriptionInfo BOOLEAN",
				"subscriptionEnabled BOOLEAN", "subscriptionLength INTEGER",
				"subscriptionType VARCHAR(75)", "subscriptionTypeSettings TEXT",
				"maxSubscriptionCycles LONG"));

		registry.register(
			"1.2.0", "1.2.1", new CPDefinitionLinkUpgradeProcess());

		registry.register(
			"1.2.1", "1.2.2",
			UpgradeProcessFactory.addColumns(
				"CPDefinition", "CProductId LONG", "version INTEGER"));

		registry.register("1.2.2", "1.2.3", new CProductUpgradeProcess());

		registry.register(
			"1.2.3", "1.2.4",
			new CPFriendlyURLEntryUpgradeProcess(_classNameLocalService));

		registry.register("1.2.4", "1.3.0", new CPInstanceUpgradeProcess());

		registry.register("1.3.0", "1.4.0", new DummyUpgradeProcess());

		registry.register(
			"1.4.0", "1.5.0",
			new CProductExternalReferenceCodeUpgradeProcess());

		registry.register(
			"1.5.0", "1.5.1",
			new CommerceCatalogUpgradeProcess(
				_classNameLocalService, _groupLocalService));

		registry.register(
			"1.5.1", "1.6.0", CommerceCatalogTable.create(),
			new CPDefinitionTrashEntriesUpgradeProcess(_classNameLocalService));

		registry.register(
			"1.6.0", "1.7.0", new CPDefinitionFiltersUpgradeProcess());

		registry.register(
			"1.7.0", "1.8.0",
			new com.liferay.commerce.product.internal.upgrade.v1_8_0.
				CPAttachmentFileEntryUpgradeProcess(_classNameLocalService));

		registry.register(
			"1.8.0", "1.9.0",
			new com.liferay.commerce.product.internal.upgrade.v1_9_0.
				CPDefinitionOptionRelUpgradeProcess());

		registry.register(
			"1.9.0", "1.9.1",
			new com.liferay.commerce.product.internal.upgrade.v1_10_0.
				CPAttachmentFileEntryUpgradeProcess(_jsonFactory));

		registry.register(
			"1.9.1", "1.10.0",
			new com.liferay.commerce.product.internal.upgrade.v1_10_0.
				CPInstanceUpgradeProcess(_jsonFactory));

		registry.register(
			"1.10.0", "1.10.1",
			new CommerceSiteTypeUpgradeProcess(
				_classNameLocalService, _groupLocalService,
				_configurationProvider));

		registry.register(
			"1.10.1", "1.11.0",
			new CPAttachmentFileEntryGroupUpgradeProcess(
				_assetCategoryLocalService, _classNameLocalService));

		registry.register(
			"1.11.0", "1.11.1",
			new CPDisplayLayoutUpgradeProcess(_layoutLocalService));

		registry.register(
			"1.11.1", "1.11.2",
			new com.liferay.commerce.product.internal.upgrade.v1_11_2.
				CPDefinitionLinkUpgradeProcess());

		registry.register(
			"1.11.2", "2.0.0",
			new CPInstanceOptionValueRelUpgradeProcess(_jsonFactory));

		registry.register(
			"2.0.0", "2.1.0",
			UpgradeProcessFactory.alterColumnName(
				"CommerceCatalog", "system", "system_ BOOLEAN"),
			UpgradeProcessFactory.addColumns(
				"CPDefinition", "deliverySubscriptionEnabled BOOLEAN",
				"deliverySubscriptionLength INTEGER",
				"deliverySubscriptionType VARCHAR(75)",
				"deliverySubTypeSettings TEXT",
				"deliveryMaxSubscriptionCycles LONG"),
			UpgradeProcessFactory.addColumns(
				"CPInstance", "deliverySubscriptionEnabled BOOLEAN",
				"deliverySubscriptionLength INTEGER",
				"deliverySubscriptionType VARCHAR(75)",
				"deliverySubTypeSettings TEXT",
				"deliveryMaxSubscriptionCycles LONG"));

		registry.register(
			"2.1.0", "2.2.0", new CPDefinitionOptionValueRelUpgradeProcess());

		registry.register(
			"2.2.0", "2.2.1",
			new com.liferay.commerce.product.internal.upgrade.v2_2_1.
				CPDefinitionOptionValueRelUpgradeProcess());

		registry.register("2.2.1", "2.2.2", new DummyUpgradeProcess());

		registry.register(
			"2.2.2", "2.3.0", new CommerceChannelUpgradeProcess());

		registry.register(
			"2.3.0", "2.4.0",
			UpgradeProcessFactory.addColumns(
				"CPDefinitionOptionValueRel", "preselected BOOLEAN"));

		registry.register(
			"2.4.0", "2.5.0",
			new FriendlyURLEntryUpgradeProcess(_groupLocalService));

		registry.register(
			"2.5.0", "2.6.0",
			UpgradeProcessFactory.addColumns(
				"CPInstance", "unspsc VARCHAR(75)"));

		registry.register("2.6.0", "2.6.1", new DummyUpgradeProcess());

		registry.register(
			"2.6.1", "3.0.0",
			UpgradeProcessFactory.dropTables("CPFriendlyURLEntry"));

		registry.register(
			"3.0.0", "3.1.0",
			UpgradeProcessFactory.addColumns(
				"CPTaxCategory", "externalReferenceCode VARCHAR(75)"));

		registry.register(
			"3.1.0", "3.2.0",
			new com.liferay.commerce.product.internal.upgrade.v3_2_0.
				FriendlyURLEntryUpgradeProcess(
					_classNameLocalService, _groupLocalService));

		registry.register("3.2.0", "3.2.1", new DummyUpgradeProcess());

		registry.register(
			"3.2.1", "3.3.0",
			UpgradeProcessFactory.addColumns(
				"CPAttachmentFileEntry", "cdnEnabled BOOLEAN",
				"cdnURL VARCHAR(255)"));

		registry.register(
			"3.3.0", "3.4.0",
			new com.liferay.commerce.product.internal.upgrade.v3_4_0.
				CommerceChannelUpgradeProcess(
					_accountEntryGroupSettings, _configurationProvider));

		registry.register(
			"3.4.0", "3.5.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"CPAttachmentFileEntry", "CPDSpecificationOptionValue",
						"CPDefinition", "CPDefinitionLink",
						"CPDefinitionLocalization", "CPDefinitionOptionRel",
						"CPDefinitionOptionValueRel", "CPDisplayLayout",
						"CPInstance", "CPInstanceOptionValueRel",
						"CPMeasurementUnit", "CPOption", "CPOptionCategory",
						"CPOptionValue", "CPSpecificationOption",
						"CPTaxCategory", "CProduct", "CommerceCatalog",
						"CommerceChannel", "CommerceChannelRel"
					};
				}

			});

		registry.register("3.5.0", "3.5.1", new DummyUpgradeStep());

		registry.register(
			"3.5.1", "3.6.0",
			UpgradeProcessFactory.addColumns(
				"CPInstance", "discontinued BOOLEAN",
				"replacementCPInstanceUuid VARCHAR(75)",
				"replacementCProductId LONG", "discontinuedDate DATE"));

		registry.register(
			"3.6.0", "3.7.0",
			new CTModelUpgradeProcess(
				"CommerceCatalog", "CommerceChannel", "CommerceChannelRel",
				"CPAttachmentFileEntry", "CPDefinition", "CPDefinitionLink",
				"CPDefinitionLocalization", "CPDefinitionOptionRel",
				"CPDefinitionOptionValueRel", "CPDSpecificationOptionValue",
				"CPDisplayLayout", "CPInstance", "CPInstanceOptionValueRel",
				"CPMeasurementUnit", "CPOption", "CPOptionCategory",
				"CPOptionValue", "CProduct", "CPSpecificationOption",
				"CPTaxCategory"));

		registry.register(
			"3.7.0", "3.8.0",
			UpgradeProcessFactory.addColumns(
				"CPMeasurementUnit", "externalReferenceCode VARCHAR(75)"));

		registry.register(
			"3.8.0", "3.8.1",
			UpgradeProcessFactory.alterColumnType(
				"CPAttachmentFileEntry", "cdnURL", "STRING null"));

		registry.register(
			"3.8.1", "3.9.0",
			new BaseUuidUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"CommerceCatalog", "commerceCatalogId"},
						{"CommerceChannel", "commerceChannelId"},
						{"CPTaxCategory", "CPTaxCategoryId"}
					};
				}

			});

		registry.register(
			"3.9.0", "3.9.1",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"CommerceCatalog", "commerceCatalogId"},
						{"CommerceChannel", "commerceChannelId"},
						{"CPAttachmentFileEntry", "CPAttachmentFileEntryId"},
						{"CPInstance", "CPInstanceId"},
						{"CPOption", "CPOptionId"},
						{"CPOptionValue", "CPOptionValueId"},
						{"CProduct", "CProductId"},
						{"CPTaxCategory", "CPTaxCategoryId"}
					};
				}

			});

		registry.register(
			"3.9.1", "3.9.2",
			new MiniumSiteInitializerUpgradeProcess(_counterLocalService));

		registry.register(
			"3.9.2", "4.0.0", CommerceChannelAccountEntryRelTable.create());

		registry.register(
			"4.0.0", "4.0.1",
			new com.liferay.commerce.product.internal.upgrade.v4_0_1.
				CommerceChannelUpgradeProcess(_groupLocalService));

		registry.register(
			"4.0.1", "4.0.2",
			new CommerceRepositoryUpgradeProcess(_companyLocalService));

		registry.register(
			"4.0.2", "4.0.3",
			new com.liferay.commerce.product.internal.upgrade.v4_0_3.
				CommerceRepositoryUpgradeProcess(
					_companyLocalService, _portal, _repositoryLocalService));

		registry.register(
			"4.0.3", "4.1.0",
			UpgradeProcessFactory.addColumns(
				"CPDisplayLayout",
				"layoutPageTemplateEntryUuid VARCHAR(75) null"));

		registry.register(
			"4.1.0", "5.0.0",
			new com.liferay.commerce.product.internal.upgrade.v5_0_0.
				CPTaxCategoryUpgradeProcess());

		registry.register(
			"5.0.0", "5.1.0",
			UpgradeProcessFactory.addColumns(
				"CommerceCatalog", "accountEntryId LONG"));

		registry.register(
			"5.1.0", "5.2.0",
			UpgradeProcessFactory.addColumns(
				"CPDefinitionLink", "displayDate DATE null",
				"expirationDate DATE null", "lastPublishDate DATE null",
				"status INTEGER", "statusByUserId LONG",
				"statusByUserName VARCHAR(75) null", "statusDate DATE null"));

		registry.register(
			"5.2.0", "5.3.0",
			UpgradeProcessFactory.addColumns(
				"CommerceChannel", "accountEntryId LONG"));

		registry.register(
			"5.3.0", "5.4.0",
			new CommercePermissionUpgradeProcess(
				_resourceActionLocalService, _resourcePermissionLocalService));

		registry.register(
			"5.4.0", "5.5.0", CPInstanceUnitOfMeasureTable.create());

		registry.register(
			"5.5.0", "5.6.0",
			UpgradeProcessFactory.addColumns(
				"CPDefinitionOptionValueRel",
				"unitOfMeasureKey VARCHAR(75) null"),
			UpgradeProcessFactory.alterColumnType(
				"CPDefinitionOptionValueRel", "quantity", "BIGDECIMAL null"));

		registry.register(
			"5.6.0", "5.7.0",
			new com.liferay.commerce.product.internal.upgrade.v5_7_0.
				CPDefinitionLinkUpgradeProcess(_assetEntryLocalService));

		registry.register(
			"5.7.0", "5.8.0",
			UpgradeProcessFactory.addColumns(
				"CPDefinitionOptionRel", "infoItemServiceKey VARCHAR(255)",
				"definedExternally BOOLEAN", "typeSettings TEXT null"));

		registry.register(
			"5.8.0", "5.9.0",
			UpgradeProcessFactory.alterColumnType(
				"CPDefinitionOptionRel", "typeSettings", "TEXT null"));

		registry.register(
			"5.9.0", "5.10.0",
			UpgradeProcessFactory.alterColumnName(
				"CPDefinitionOptionRel", "DDMFormFieldTypeName",
				"commerceOptionTypeKey VARCHAR(75) null"),
			UpgradeProcessFactory.alterColumnName(
				"CPOption", "DDMFormFieldTypeName",
				"commerceOptionTypeKey VARCHAR(75) null"));

		registry.register(
			"5.10.0", "5.11.0",
			new CPAttachmentFileEntryGalleryEnabledUpgradeProcess());

		registry.register(
			"5.11.0", "5.11.1",
			new ProductDefinitionConfigurationUpgradeProcess(
				_configurationAdmin));

		registry.register(
			"5.11.1", "5.12.0",
			new com.liferay.commerce.product.internal.upgrade.v5_12_0.
				CPAttachmentFileEntryUpgradeProcess(_assetEntryLocalService));

		registry.register(
			"5.12.0", "5.12.1",
			new com.liferay.commerce.product.internal.upgrade.v5_12_1.
				CommerceChannelUpgradeProcess(
					_accountEntryGroupSettings, _configurationProvider));

		registry.register(
			"5.12.1", "5.13.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"CPMeasurementUnit", "CPMeasurementUnitId"}
					};
				}

			});

		registry.register(
			"5.13.0", "5.14.0",
			new CPDefinitionSpecificationOptionValueUpgradeProcess());

		registry.register(
			"5.14.0", "5.15.0",
			UpgradeProcessFactory.addColumns(
				"CPSpecificationOption", "priority DOUBLE"));

		registry.register(
			"5.15.0", "5.16.0",
			UpgradeProcessFactory.addColumns(
				"CPSpecificationOption", "listTypeDefinitionId LONG"));

		registry.register(
			"5.16.0", "5.17.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"CPOptionCategory", "CPOptionCategoryId"}
					};
				}

			});

		registry.register(
			"5.17.0", "5.18.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"CPSpecificationOption", "CPSpecificationOptionId"}
					};
				}

			});

		registry.register(
			"5.18.0", "5.19.0",
			UpgradeProcessFactory.addColumns(
				"CPInstanceUOM", "pricingQuantity BIGDECIMAL"));

		registry.register(
			"5.19.0", "5.20.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{
							"CPDSpecificationOptionValue",
							"CPDSpecificationOptionValueId"
						}
					};
				}

			});

		registry.register(
			"5.20.0", "5.21.0", CPConfigurationEntryTable.create(),
			CPConfigurationListTable.create());

		registry.register(
			"5.21.0", "5.21.1",
			new BaseUpgradePortletPreferences() {

				@Override
				protected String[] getPortletIds() {
					return new String[] {
						CPPortletKeys.CP_ASSET_CATEGORIES_NAVIGATION +
							"_INSTANCE_%"
					};
				}

				@Override
				protected void upgradePreferences(
						long companyId, long ownerId, int ownerType, long plid,
						String portletId, PortletPreferences portletPreferences)
					throws Exception {

					long assetVocabularyId = GetterUtil.getLong(
						portletPreferences.getValue("assetVocabularyId", null));

					AssetVocabulary assetVocabulary =
						_assetVocabularyLocalService.fetchAssetVocabulary(
							assetVocabularyId);

					if (assetVocabulary != null) {
						portletPreferences.setValue(
							"assetVocabularyExternalReferenceCode",
							assetVocabulary.getExternalReferenceCode());
					}

					long rootAssetCategoryId = GetterUtil.getLong(
						portletPreferences.getValue(
							"rootAssetCategoryId", null));

					AssetCategory assetCategory =
						_assetCategoryLocalService.fetchAssetCategory(
							rootAssetCategoryId);

					if (assetCategory != null) {
						portletPreferences.setValue(
							"rootAssetCategoryExternalReferenceCode",
							assetCategory.getExternalReferenceCode());
					}
				}

			});

		registry.register(
			"5.21.1", "5.22.0", new CPSpecificationOptionUpgradeProcess());

		registry.register(
			"5.22.0", "5.23.0",
			UpgradeProcessFactory.addColumns(
				"CPConfigurationEntry", "commerceAvailabilityEstimateId LONG"));

		registry.register(
			"5.23.0", "5.23.1",
			new BaseUpgradePortletPreferences() {

				@Override
				protected String[] getPortletIds() {
					return new String[] {CPPortletKeys.CP_CATEGORY_CONTENT_WEB};
				}

				@Override
				protected void upgradePreferences(
						long companyId, long ownerId, int ownerType, long plid,
						String portletId, PortletPreferences portletPreferences)
					throws Exception {

					long assetCategoryId = GetterUtil.getLong(
						portletPreferences.getValue("assetCategoryId", null));

					AssetCategory assetCategory =
						_assetCategoryLocalService.fetchAssetCategory(
							assetCategoryId);

					if (assetCategory != null) {
						portletPreferences.setValue(
							"assetCategoryExternalReferenceCode",
							assetCategory.getExternalReferenceCode());
					}
				}

			});

		registry.register(
			"5.23.1", "5.24.0",
			UpgradeProcessFactory.addColumns(
				"CPConfigurationEntry", "groupId LONG"));

		registry.register(
			"5.24.0", "5.25.0", CPConfigurationListRelTable.create());

		registry.register(
			"5.25.0", "5.25.1",
			new BaseUpgradePortletPreferences() {

				@Override
				protected String[] getPortletIds() {
					return new String[] {
						CPPortletKeys.CP_COMPARE_CONTENT_MINI_WEB,
						CPPortletKeys.CP_COMPARE_CONTENT_WEB,
						CPPortletKeys.CP_CONTENT_WEB,
						CPPortletKeys.CP_OPTION_FACETS + "_INSTANCE_%",
						CPPortletKeys.CP_PUBLISHER_WEB + "_INSTANCE_%",
						CPPortletKeys.CP_SEARCH_RESULTS + "_INSTANCE_%",
						CPPortletKeys.CP_SPECIFICATION_OPTION_FACETS +
							"_INSTANCE_%"
					};
				}

				@Override
				protected void upgradePreferences(
						long companyId, long ownerId, int ownerType, long plid,
						String portletId, PortletPreferences portletPreferences)
					throws Exception {
				}

			});

		registry.register(
			"5.25.1", "5.26.0", CPConfigurationEntrySettingTable.create());

		registry.register(
			"5.26.0", "5.27.0",
			UpgradeProcessFactory.alterColumnName(
				"CPConfigurationList", "masterCPConfigurationList",
				"master BOOLEAN"));

		registry.register(
			"5.27.0", "5.28.0",
			new com.liferay.commerce.product.internal.upgrade.v5_28_0.
				CPDefinitionSpecificationOptionValueUpgradeProcess(),
			new com.liferay.commerce.product.internal.upgrade.v5_28_0.
				CPSpecificationOptionUpgradeProcess());

		if (_log.isInfoEnabled()) {
			_log.info("Commerce product upgrade step registrator finished");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceProductServiceUpgradeStepRegistrator.class);

	@Reference
	private AccountEntryGroupSettings _accountEntryGroupSettings;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}