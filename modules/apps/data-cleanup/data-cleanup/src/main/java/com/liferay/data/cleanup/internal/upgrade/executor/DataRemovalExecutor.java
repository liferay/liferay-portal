/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.upgrade.executor;

import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.store.service.CTSContentLocalService;
import com.liferay.data.cleanup.internal.configuration.DataRemovalConfiguration;
import com.liferay.data.cleanup.internal.upgrade.DLPreviewCTSContentDataUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.ExpiredJournalArticleUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.LayoutClassedModelUsageOrphanDataUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.OutdatedPublishedCTCollectionUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.PublishedCTSContentDataUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.WidgetLayoutTypeSettingsUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.util.ConfigurationUtil;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.layout.manager.ContentManager;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.upgrade.data.cleanup.AnalyticsMessageDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.CompanyDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.ConfigurationDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.CounterDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.DDMDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.DDMStorageLinkDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.DLFileEntryDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.DataCleanupPreupgradeProcessSuite;
import com.liferay.portal.upgrade.data.cleanup.GroupDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.JournalDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.NullUnicodeContentDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.QuartzJobDetailsDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.UserDataCleanupPreupgradeProcess;

import java.util.Map;
import java.util.function.Supplier;

import org.apache.felix.cm.PersistenceManager;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	configurationPid = "com.liferay.data.cleanup.internal.configuration.DataRemovalConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class DataRemovalExecutor {

	@Activate
	protected void activate(Map<String, Object> properties) throws Exception {
		DataRemovalConfiguration dataRemovalConfiguration =
			ConfigurableUtil.createConfigurable(
				DataRemovalConfiguration.class, properties);

		ConfigurationUtil.deleteConfiguration(
			_configurationAdmin, _persistenceManager,
			DataRemovalConfiguration.class.getName());

		_removeModuleData(
			dataRemovalConfiguration::removeDLPreviewCTSContentData,
			"com.liferay.change.tracking.service",
			() -> new DLPreviewCTSContentDataUpgradeProcess(
				_ctCollectionLocalService, _ctEntryLocalService, _portal));
		_removeModuleData(
			dataRemovalConfiguration::removeOutdatedPublishedCTCollections,
			"com.liferay.change.tracking.service",
			() -> new OutdatedPublishedCTCollectionUpgradeProcess(
				_ctCollectionLocalService));
		_removeModuleData(
			dataRemovalConfiguration::removePublishedCTSContentData,
			"com.liferay.change.tracking.store.service",
			() -> new PublishedCTSContentDataUpgradeProcess(
				_ctsContentLocalService, _portal));
		_removeModuleData(
			dataRemovalConfiguration::removeExpiredJournalArticles,
			"com.liferay.journal.service",
			() -> new ExpiredJournalArticleUpgradeProcess(
				_journalArticleLocalService));
		_removeModuleData(
			dataRemovalConfiguration::removeLayoutClassedModelUsageOrphanData,
			"com.liferay.layout.service",
			() -> new LayoutClassedModelUsageOrphanDataUpgradeProcess(
				_classNameLocalService, _contentManager,
				_fragmentEntryLinkLocalService,
				_layoutClassedModelUsageLocalService,
				_layoutPageTemplateStructureLocalService,
				_layoutPageTemplateStructureRelLocalService));
		_removeModuleData(
			dataRemovalConfiguration::removeWidgetLayoutTypeSettings,
			"com.liferay.layout.service",
			() -> new WidgetLayoutTypeSettingsUpgradeProcess(
				_layoutLocalService));

		_executeDataCleanupPreupgradeProcesses(dataRemovalConfiguration);
	}

	private void _executeDataCleanupPreupgradeProcesses(
			DataRemovalConfiguration dataRemovalConfiguration)
		throws Exception {

		DataCleanupPreupgradeProcessSuite dataCleanupPreupgradeProcessSuite =
			new DataCleanupPreupgradeProcessSuite();

		for (DataCleanupPreupgradeProcess dataCleanupPreupgradeProcess :
				dataCleanupPreupgradeProcessSuite.
					getSortedDataCleanupPreupgradeProcesses()) {

			if (_isDataCleanupPreupgradeProcessEnabled(
					dataCleanupPreupgradeProcess, dataRemovalConfiguration)) {

				dataCleanupPreupgradeProcess.upgrade();

				CacheRegistryUtil.clear();
			}
		}
	}

	private boolean _isDataCleanupPreupgradeProcessEnabled(
		DataCleanupPreupgradeProcess dataCleanupPreupgradeProcess,
		DataRemovalConfiguration dataRemovalConfiguration) {

		Class<?> clazz = dataCleanupPreupgradeProcess.getClass();

		if (clazz.equals(AnalyticsMessageDataCleanupPreupgradeProcess.class)) {
			return dataRemovalConfiguration.removeAnalyticsMessageData();
		}

		if (clazz.equals(CompanyDataCleanupPreupgradeProcess.class)) {
			return dataRemovalConfiguration.removeCompanyOrphanData();
		}

		if (clazz.equals(ConfigurationDataCleanupPreupgradeProcess.class)) {
			return dataRemovalConfiguration.removeConfigurationOrphanData();
		}

		if (clazz.equals(CounterDataCleanupPreupgradeProcess.class)) {
			return dataRemovalConfiguration.fixCounterValues();
		}

		if (clazz.equals(DDMDataCleanupPreupgradeProcess.class) ||
			clazz.equals(DDMStorageLinkDataCleanupPreupgradeProcess.class)) {

			return dataRemovalConfiguration.removeDDMOrphanData();
		}

		if (clazz.equals(DLFileEntryDataCleanupPreupgradeProcess.class)) {
			return dataRemovalConfiguration.removeDLFileEntryOrphanData();
		}

		if (clazz.equals(GroupDataCleanupPreupgradeProcess.class)) {
			return dataRemovalConfiguration.removeGroupOrphanData();
		}

		if (clazz.equals(JournalDataCleanupPreupgradeProcess.class)) {
			return dataRemovalConfiguration.removeJournalOrphanData();
		}

		if (clazz.equals(
				NullUnicodeContentDataCleanupPreupgradeProcess.class)) {

			return dataRemovalConfiguration.removeNullUnicodeContentData();
		}

		if (clazz.equals(QuartzJobDetailsDataCleanupPreupgradeProcess.class)) {
			return dataRemovalConfiguration.removeQuartzJobDetailsData();
		}

		if (clazz.equals(UserDataCleanupPreupgradeProcess.class)) {
			return dataRemovalConfiguration.removeUserOrphanData();
		}

		return false;
	}

	private void _removeModuleData(
			Supplier<Boolean> booleanSupplier, String servletContextName,
			Supplier<UpgradeProcess> upgradeProcessSupplier)
		throws Exception {

		if (booleanSupplier.get()) {
			Release release = _releaseLocalService.fetchRelease(
				servletContextName);

			if (release != null) {
				UpgradeProcess upgradeProcess = upgradeProcessSupplier.get();

				upgradeProcess.upgrade();

				CacheRegistryUtil.clear();
			}
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ContentManager _contentManager;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private CTSContentLocalService _ctsContentLocalService;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Reference
	private PersistenceManager _persistenceManager;

	@Reference
	private Portal _portal;

	@Reference
	private ReleaseLocalService _releaseLocalService;

}