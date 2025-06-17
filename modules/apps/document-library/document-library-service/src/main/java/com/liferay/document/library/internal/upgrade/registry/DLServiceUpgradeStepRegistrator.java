/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.upgrade.registry;

import com.liferay.comment.upgrade.DiscussionSubscriptionClassNameUpgradeProcess;
import com.liferay.document.library.internal.upgrade.helper.DLConfigurationUpgradeHelper;
import com.liferay.document.library.internal.upgrade.v1_0_0.DocumentLibraryUpgradeProcess;
import com.liferay.document.library.internal.upgrade.v1_0_1.DLConfigurationUpgradeProcess;
import com.liferay.document.library.internal.upgrade.v1_0_1.DLFileEntryConfigurationUpgradeProcess;
import com.liferay.document.library.internal.upgrade.v1_1_0.SchemaUpgradeProcess;
import com.liferay.document.library.internal.upgrade.v1_1_2.DLFileEntryTypeUpgradeProcess;
import com.liferay.document.library.internal.upgrade.v2_0_0.UpgradeCompanyId;
import com.liferay.document.library.internal.upgrade.v3_2_1.DDMStructureLinkUpgradeProcess;
import com.liferay.document.library.internal.upgrade.v3_2_10.DLFolderAdvancedUpdateResourcePermissionUpgradeProcess;
import com.liferay.document.library.internal.upgrade.v3_2_2.DLFileEntryUpgradeProcess;
import com.liferay.document.library.internal.upgrade.v3_2_4.DLSizeLimitConfigurationUpgradeProcess;
import com.liferay.document.library.internal.upgrade.v3_2_5.DLFileEntryTypesDDMStructureUpgradeProcess;
import com.liferay.document.library.internal.upgrade.v3_2_6.DeleteStalePWCVersionsUpgradeProcess;
import com.liferay.document.library.internal.upgrade.v3_2_7.DownloadViewActionResourcePermissionUpgradeProcess;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.dynamic.data.mapping.security.permission.DDMPermissionSupport;
import com.liferay.portal.configuration.upgrade.PrefsPropsToConfigurationUpgradeHelper;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.ViewCountUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.subscription.service.SubscriptionLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Miguel Pastor
 */
@Component(service = UpgradeStepRegistrator.class)
public class DLServiceUpgradeStepRegistrator implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"0.0.1", "1.0.0", new DocumentLibraryUpgradeProcess(_store));

		registry.register(
			"1.0.0", "1.0.1",
			UpgradeProcessFactory.runSQL(
				"update DLFileShortcut set repositoryId = groupId where " +
					"repositoryId = 0"));

		registry.register(
			"1.0.1", "1.0.1.step-1",
			new DLConfigurationUpgradeProcess(
				_prefsPropsToConfigurationUpgradeHelper));

		registry.register(
			"1.0.1.step-1", "1.0.2",
			new DLFileEntryConfigurationUpgradeProcess(
				_prefsPropsToConfigurationUpgradeHelper));

		registry.register("1.0.2", "1.1.0", new SchemaUpgradeProcess());

		registry.register("1.1.0", "1.1.1", new DummyUpgradeStep());

		registry.register(
			"1.1.1", "1.1.2",
			new DLFileEntryTypeUpgradeProcess(_resourceLocalService));

		registry.register("1.1.2", "2.0.0", new UpgradeCompanyId());

		registry.register(
			"2.0.0", "3.0.0",
			new ViewCountUpgradeProcess(
				"DLFileEntry", DLFileEntry.class, "fileEntryId", "readCount"));

		registry.register(
			"3.0.0", "3.0.1",
			new DiscussionSubscriptionClassNameUpgradeProcess(
				_classNameLocalService, _subscriptionLocalService,
				DLFileEntry.class.getName(),
				DiscussionSubscriptionClassNameUpgradeProcess.DeletionMode.
					UPDATE));

		registry.register(
			"3.0.1", "3.0.2",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"DLFileVersionPreview"};
				}

			});

		registry.register(
			"3.0.2", "3.1.0",
			new CTModelUpgradeProcess("DLFileVersionPreview"));

		registry.register("3.1.0", "3.1.1", new DummyUpgradeStep());

		registry.register(
			"3.1.1", "3.1.2",
			new com.liferay.document.library.internal.upgrade.v3_2_0.
				SchemaUpgradeProcess());

		registry.register(
			"3.1.2", "3.2.0",
			new com.liferay.document.library.internal.upgrade.v3_2_0.
				StorageQuotaUpgradeProcess());

		registry.register(
			"3.2.0", "3.2.0.step-1", new DDMStructureLinkUpgradeProcess());

		registry.register(
			"3.2.0.step-1", "3.2.1",
			new com.liferay.document.library.internal.upgrade.v3_2_1.
				UpgradeDLFileEntryType());

		registry.register("3.2.1", "3.2.2", new DummyUpgradeStep());

		registry.register(
			"3.2.2", "3.2.3",
			new DLFileEntryUpgradeProcess(_classNameLocalService));

		registry.register(
			"3.2.3", "3.2.4",
			new DLSizeLimitConfigurationUpgradeProcess(
				_dlConfigurationUpgradeHelper));

		registry.register(
			"3.2.4", "3.2.5",
			new DLFileEntryTypesDDMStructureUpgradeProcess(
				_ddmPermissionSupport, _resourceActionLocalService,
				_resourcePermissionLocalService));

		registry.register(
			"3.2.5", "3.2.6", new DeleteStalePWCVersionsUpgradeProcess(_store));

		registry.register(
			"3.2.6", "3.2.7",
			new DownloadViewActionResourcePermissionUpgradeProcess());

		registry.register(
			"3.2.7", "3.2.8",
			new com.liferay.document.library.internal.upgrade.v3_2_8.
				DLFileEntryConfigurationUpgradeProcess(
					_dlConfigurationUpgradeHelper));

		registry.register(
			"3.2.8", "3.2.8.step-1",
			new com.liferay.document.library.internal.upgrade.v3_2_9.
				DLConfigurationUpgradeProcess(
					_dlConfigurationUpgradeHelper,
					_prefsPropsToConfigurationUpgradeHelper));

		registry.register(
			"3.2.8.step-1", "3.2.8.step-2",
			new com.liferay.document.library.internal.upgrade.v3_2_9.
				DLFileEntryConfigurationUpgradeProcess(
					_dlConfigurationUpgradeHelper,
					_prefsPropsToConfigurationUpgradeHelper));

		registry.register(
			"3.2.8.step-2", "3.2.9",
			new com.liferay.document.library.internal.upgrade.v3_2_9.
				DLSizeLimitConfigurationUpgradeProcess(
					_dlConfigurationUpgradeHelper));

		registry.register(
			"3.2.9", "3.2.10",
			new DLFolderAdvancedUpdateResourcePermissionUpgradeProcess(
				_resourceActionLocalService));
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DDMPermissionSupport _ddmPermissionSupport;

	@Reference
	private DLConfigurationUpgradeHelper _dlConfigurationUpgradeHelper;

	@Reference
	private PrefsPropsToConfigurationUpgradeHelper
		_prefsPropsToConfigurationUpgradeHelper;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.view.count.service)(&(release.schema.version>=1.0.0)))"
	)
	private Release _release;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference(target = "(default=true)")
	private Store _store;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

}