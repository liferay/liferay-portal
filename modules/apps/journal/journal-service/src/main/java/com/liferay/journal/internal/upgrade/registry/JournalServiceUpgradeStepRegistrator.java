/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.registry;

import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.comment.upgrade.DiscussionSubscriptionClassNameUpgradeProcess;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalService;
import com.liferay.dynamic.data.mapping.util.DefaultDDMStructureHelper;
import com.liferay.dynamic.data.mapping.util.FieldsToDDMFormValuesConverter;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.journal.content.compatibility.converter.JournalContentCompatibilityConverter;
import com.liferay.journal.internal.upgrade.helper.JournalArticleImageUpgradeHelper;
import com.liferay.journal.internal.upgrade.v0_0_3.JournalArticleTypeUpgradeProcess;
import com.liferay.journal.internal.upgrade.v0_0_4.SchemaUpgradeProcess;
import com.liferay.journal.internal.upgrade.v0_0_5.JournalUpgradeProcess;
import com.liferay.journal.internal.upgrade.v0_0_5.UpgradeCompanyId;
import com.liferay.journal.internal.upgrade.v0_0_5.UpgradeJournalArticles;
import com.liferay.journal.internal.upgrade.v0_0_5.UpgradeJournalDisplayPreferences;
import com.liferay.journal.internal.upgrade.v0_0_5.UpgradeLastPublishDate;
import com.liferay.journal.internal.upgrade.v0_0_5.UpgradePortletSettings;
import com.liferay.journal.internal.upgrade.v0_0_6.ImageTypeContentAttributesUpgradeProcess;
import com.liferay.journal.internal.upgrade.v0_0_8.ArticleAssetsUpgradeProcess;
import com.liferay.journal.internal.upgrade.v0_0_8.ArticleExpirationDateUpgradeProcess;
import com.liferay.journal.internal.upgrade.v0_0_8.ArticleSystemEventsUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_0_0.JournalArticleImageUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_0_1.JournalContentSearchUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_0.DocumentLibraryTypeContentUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_0.ImageTypeContentUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_0.JournalArticleLocalizedValuesUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_1.FileUploadsConfigurationUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_2.CheckIntervalConfigurationUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_3.ResourcePermissionsUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_5.ContentImagesUpgradeProcess;
import com.liferay.journal.internal.upgrade.v1_1_6.AssetDisplayPageEntryUpgradeProcess;
import com.liferay.journal.internal.upgrade.v2_0_0.util.JournalArticleTable;
import com.liferay.journal.internal.upgrade.v2_0_0.util.JournalFeedTable;
import com.liferay.journal.internal.upgrade.v2_0_0.util.JournalFolderTable;
import com.liferay.journal.internal.upgrade.v3_3_0.StorageLinksUpgradeProcess;
import com.liferay.journal.internal.upgrade.v3_5_0.JournalArticleContentUpgradeProcess;
import com.liferay.journal.internal.upgrade.v3_5_1.JournalArticleDataFileEntryIdUpgradeProcess;
import com.liferay.journal.internal.upgrade.v4_0_0.JournalArticleDDMFieldsUpgradeProcess;
import com.liferay.journal.internal.upgrade.v4_1_0.JournalArticleExternalReferenceCodeUpgradeProcess;
import com.liferay.journal.internal.upgrade.v4_3_1.BasicWebContentAssetEntryClassTypeIdUpgradeProcess;
import com.liferay.journal.internal.upgrade.v4_4_0.GlobalJournalArticleUrlTitleUpgradeProcess;
import com.liferay.journal.internal.upgrade.v4_4_4.JournalFeedTypeUpgradeProcess;
import com.liferay.journal.internal.upgrade.v5_1_0.JournalArticleDDMStructureIdUpgradeProcess;
import com.liferay.journal.internal.upgrade.v5_1_1.JournalArticleAssetEntryClassTypeIdUpgradeProcess;
import com.liferay.journal.internal.upgrade.v5_2_0.JournalFeedDDMStructureIdUpgradeProcess;
import com.liferay.journal.internal.upgrade.v6_1_0.JournalArticleSmallImageSourceUpgradeProcess;
import com.liferay.journal.internal.upgrade.v6_1_1.JournalArticleLayoutClassedModelUsageUpgradeProcess;
import com.liferay.journal.internal.upgrade.v6_1_3.JournalArticleFriendlyURLFormatUpgradeProcess;
import com.liferay.journal.internal.upgrade.v6_1_4.JournalArticleAutogenerateDDMKeyConfigurationUpgradeProcess;
import com.liferay.journal.internal.upgrade.v6_1_5.JournalFolderAdvancedUpdateResourcePermissionUpgradeProcess;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.util.JournalConverter;
import com.liferay.portal.change.tracking.store.CTStoreFactory;
import com.liferay.portal.configuration.upgrade.PrefsPropsToConfigurationUpgradeHelper;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.capabilities.PortalCapabilityLocator;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.subscription.service.SubscriptionLocalService;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(service = UpgradeStepRegistrator.class)
public class JournalServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register("0.0.1", "0.0.2", new DummyUpgradeStep());

		registry.register(
			"0.0.2", "0.0.3",
			new JournalArticleTypeUpgradeProcess(
				_assetCategoryLocalService,
				_assetEntryAssetCategoryRelLocalService,
				_assetEntryLocalService, _assetVocabularyLocalService,
				_companyLocalService, _userLocalService));

		registry.register("0.0.3", "0.0.4", new SchemaUpgradeProcess());

		registry.register(
			"0.0.4", "0.0.5", new UpgradeCompanyId(),
			new JournalUpgradeProcess(
				_companyLocalService, _ddmStorageLinkLocalService,
				_ddmStructureLocalService, _ddmTemplateLinkLocalService,
				_defaultDDMStructureHelper, _groupLocalService,
				_resourceActionLocalService, _resourceActions,
				_resourceLocalService, _userLocalService),
			new UpgradeJournalArticles(
				_assetCategoryLocalService, _ddmStructureLocalService,
				_groupLocalService, _layoutLocalService,
				_portletPreferenceValueLocalService,
				_portletPreferencesLocalService),
			new UpgradeJournalDisplayPreferences(),
			new UpgradeLastPublishDate(),
			new UpgradePortletSettings(_settingsLocatorHelper),
			() -> {
				try {
					_deleteTempImages();
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			});

		registry.register(
			"0.0.5", "0.0.6", new JournalArticleImageUpgradeProcess());

		registry.register(
			"0.0.6", "0.0.7", new ImageTypeContentAttributesUpgradeProcess());

		registry.register(
			"0.0.7", "0.0.8",
			UpgradeProcessFactory.runSQL(
				"update JournalArticle set treePath = '/' where folderId = 0 " +
					"and treePath = '/0/'"));

		registry.register(
			"0.0.8", "0.0.9",
			new ArticleAssetsUpgradeProcess(
				_assetEntryLocalService, _companyLocalService));

		registry.register(
			"0.0.9", "0.0.10", new ArticleExpirationDateUpgradeProcess());

		registry.register(
			"0.0.10", "1.0.0",
			new ArticleSystemEventsUpgradeProcess(_systemEventLocalService));

		registry.register(
			"1.0.0", "1.0.1", new JournalContentSearchUpgradeProcess());

		registry.register("1.0.1", "1.0.2", new DummyUpgradeStep());

		registry.register(
			"1.0.2", "1.0.3",
			new DocumentLibraryTypeContentUpgradeProcess(
				_journalArticleImageUpgradeHelper));

		registry.register(
			"1.0.3", "1.0.4",
			new ImageTypeContentUpgradeProcess(
				_imageLocalService, _journalArticleImageUpgradeHelper,
				_portletFileRepository));

		registry.register(
			"1.0.4", "1.1.0",
			new JournalArticleLocalizedValuesUpgradeProcess(
				_counterLocalService));

		registry.register(
			"1.1.0", "1.1.1",
			new FileUploadsConfigurationUpgradeProcess(
				_prefsPropsToConfigurationUpgradeHelper));

		registry.register(
			"1.1.1", "1.1.2",
			new CheckIntervalConfigurationUpgradeProcess(_configurationAdmin));

		registry.register(
			"1.1.2", "1.1.3",
			new ResourcePermissionsUpgradeProcess(_resourceActions));

		registry.register(
			"1.1.3", "1.1.4",
			UpgradeProcessFactory.alterColumnType(
				"JournalArticle", "urlTitle", "VARCHAR(255) null"));

		registry.register(
			"1.1.4", "1.1.5",
			new ContentImagesUpgradeProcess(
				_journalArticleImageUpgradeHelper, _portletFileRepository));

		registry.register(
			"1.1.5", "1.1.6",
			new AssetDisplayPageEntryUpgradeProcess(
				_assetDisplayPageEntryLocalService, _companyLocalService));

		registry.register(
			"1.1.6", "1.1.7",
			new DiscussionSubscriptionClassNameUpgradeProcess(
				_classNameLocalService, _subscriptionLocalService,
				JournalArticle.class.getName(),
				DiscussionSubscriptionClassNameUpgradeProcess.DeletionMode.
					UPDATE));

		registry.register(
			"1.1.7", "1.1.8",
			new com.liferay.journal.internal.upgrade.v1_1_8.
				JournalArticleUpgradeProcess());

		registry.register(
			"1.1.8", "2.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {
					JournalArticleTable.class, JournalFeedTable.class,
					JournalFolderTable.class
				}));

		registry.register(
			"2.0.0", "3.0.0",
			new com.liferay.journal.internal.upgrade.v3_0_0.
				JournalArticleImageUpgradeProcess(_imageLocalService));

		registry.register("3.0.0", "3.0.1", new DummyUpgradeStep());

		registry.register("3.0.1", "3.0.2", new DummyUpgradeStep());

		registry.register(
			"3.0.2", "3.1.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"JournalArticle", "JournalArticleLocalization",
						"JournalArticleResource", "JournalContentSearch",
						"JournalFeed", "JournalFolder"
					};
				}

			});

		registry.register("3.1.0", "3.1.1", new DummyUpgradeStep());

		registry.register(
			"3.1.1", "3.2.0",
			new CTModelUpgradeProcess(
				"JournalArticleLocalization", "JournalArticleResource",
				"JournalArticle", "JournalFolder"));

		registry.register("3.2.0", "3.2.1", new DummyUpgradeStep());

		registry.register("3.2.1", "3.2.2", new DummyUpgradeStep());

		registry.register("3.2.2", "3.2.3", new DummyUpgradeStep());

		registry.register("3.2.3", "3.2.4", new DummyUpgradeStep());

		registry.register(
			"3.2.4", "3.3.0",
			new CTModelUpgradeProcess("JournalContentSearch", "JournalFeed"));

		registry.register(
			"3.3.0", "3.4.0",
			new StorageLinksUpgradeProcess(_classNameLocalService));

		registry.register("3.4.0", "3.4.1", new DummyUpgradeStep());

		registry.register("3.4.1", "3.4.2", new DummyUpgradeStep());

		registry.register("3.4.2", "3.4.3", new DummyUpgradeStep());

		registry.register("3.4.3", "3.4.4", new DummyUpgradeStep());

		registry.register(
			"3.4.4", "3.5.0",
			new JournalArticleContentUpgradeProcess(
				_journalContentCompatibilityConverter));

		registry.register(
			"3.5.0", "3.5.1",
			new JournalArticleDataFileEntryIdUpgradeProcess());

		registry.register(
			"3.5.1", "4.0.0",
			new JournalArticleDDMFieldsUpgradeProcess(
				_classNameLocalService, _companyLocalService,
				_ddmFieldLocalService, _ddmStructureLocalService,
				_fieldsToDDMFormValuesConverter, _journalConverter, _portal));

		registry.register(
			"4.0.0", "4.1.0",
			new JournalArticleExternalReferenceCodeUpgradeProcess());

		registry.register(
			"4.1.0", "4.2.0",
			UpgradeProcessFactory.alterColumnType(
				"JournalFeed", "DDMTemplateKey", "VARCHAR(75) null"),
			UpgradeProcessFactory.alterColumnType(
				"JournalFeed", "DDMRendererTemplateKey", "VARCHAR(75) null"),
			UpgradeProcessFactory.alterColumnType(
				"JournalFeed", "DDMStructureKey", "VARCHAR(75) null"));

		registry.register(
			"4.2.0", "4.3.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"JournalFolder"};
				}

			});

		registry.register(
			"4.3.0", "4.3.1",
			new BasicWebContentAssetEntryClassTypeIdUpgradeProcess(
				_companyLocalService, _ddmStructureLocalService,
				_groupLocalService));

		registry.register(
			"4.3.1", "4.4.0",
			new GlobalJournalArticleUrlTitleUpgradeProcess(
				_classNameLocalService, _companyLocalService,
				_groupLocalService));

		registry.register(
			"4.4.0", "4.4.1",
			UpgradeProcessFactory.alterColumnType(
				"JournalArticleLocalization", "title", "VARCHAR(800) null"));

		registry.register("4.4.1", "4.4.2", new DummyUpgradeStep());

		registry.register("4.4.2", "4.4.3", new DummyUpgradeStep());

		registry.register(
			"4.4.3", "4.4.4",
			new JournalFeedTypeUpgradeProcess(
				_assetCategoryLocalService,
				_assetEntryAssetCategoryRelLocalService,
				_assetEntryLocalService, _assetVocabularyLocalService,
				_companyLocalService, _language, _localization, _portal,
				_userLocalService));

		registry.register(
			"4.4.4", "5.0.0",
			UpgradeProcessFactory.dropColumns("JournalFeed", "type_"));

		registry.register(
			"5.0.0", "5.1.0",
			new JournalArticleDDMStructureIdUpgradeProcess(
				_classNameLocalService, _siteConnectedGroupGroupProvider));

		registry.register(
			"5.1.0", "5.1.1",
			new JournalArticleAssetEntryClassTypeIdUpgradeProcess(
				_classNameLocalService));

		registry.register(
			"5.1.1", "5.2.0",
			new JournalFeedDDMStructureIdUpgradeProcess(
				_classNameLocalService, _siteConnectedGroupGroupProvider));

		registry.register(
			"5.2.0", "5.2.1",
			new com.liferay.journal.internal.upgrade.v5_2_1.
				JournalArticleLayoutClassedModelUsageUpgradeProcess());

		registry.register(
			"5.2.1", "6.0.0",
			UpgradeProcessFactory.dropColumns(
				"JournalArticle", "DDMStructureKey"),
			UpgradeProcessFactory.dropColumns(
				"JournalFeed", "DDMStructureKey"));

		registry.register(
			"6.0.0", "6.1.0",
			new JournalArticleSmallImageSourceUpgradeProcess());

		registry.register(
			"6.1.0", "6.1.1",
			new JournalArticleLayoutClassedModelUsageUpgradeProcess(
				_classNameLocalService));

		registry.register("6.1.1", "6.1.2", new DummyUpgradeProcess());

		registry.register(
			"6.1.2", "6.1.3",
			new JournalArticleFriendlyURLFormatUpgradeProcess(
				_classNameLocalService, _friendlyURLEntryLocalService));

		registry.register(
			"6.1.3", "6.1.4",
			new JournalArticleAutogenerateDDMKeyConfigurationUpgradeProcess(
				_companyLocalService, _configurationAdmin));

		registry.register(
			"6.1.4", "6.1.5",
			new JournalFolderAdvancedUpdateResourcePermissionUpgradeProcess(
				_resourceActionLocalService));
	}

	private void _deleteTempImages() throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("Delete temporary images");
		}

		DB db = DBManagerUtil.getDB();

		db.runSQL(
			"delete from Image where imageId IN (SELECT articleImageId FROM " +
				"JournalArticleImage where tempImage = [$TRUE$])");

		db.runSQL("delete from JournalArticleImage where tempImage = [$TRUE$]");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalServiceUpgradeStepRegistrator.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Reference
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

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
	private CounterLocalService _counterLocalService;

	@Reference
	private CTStoreFactory _ctStoreFactory;

	@Reference
	private DDMFieldLocalService _ddmFieldLocalService;

	@Reference
	private DDMStorageLinkLocalService _ddmStorageLinkLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DDMTemplateLinkLocalService _ddmTemplateLinkLocalService;

	@Reference
	private DefaultDDMStructureHelper _defaultDDMStructureHelper;

	@Reference
	private FieldsToDDMFormValuesConverter _fieldsToDDMFormValuesConverter;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private JournalArticleImageUpgradeHelper _journalArticleImageUpgradeHelper;

	@Reference
	private JournalContentCompatibilityConverter
		_journalContentCompatibilityConverter;

	@Reference
	private JournalConverter _journalConverter;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Localization _localization;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private Portal _portal;

	// See LPS-82746

	@Reference
	private PortalCapabilityLocator _portalCapabilityLocator;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	@Reference
	private PrefsPropsToConfigurationUpgradeHelper
		_prefsPropsToConfigurationUpgradeHelper;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.layout.service)(&(release.schema.version>=1.0.0)))"
	)
	private Release _release;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourceActions _resourceActions;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SettingsLocatorHelper _settingsLocatorHelper;

	@Reference
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

	@Reference(target = "(default=true)")
	private Store _store;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private SystemEventLocalService _systemEventLocalService;

	@Reference
	private UserLocalService _userLocalService;

}