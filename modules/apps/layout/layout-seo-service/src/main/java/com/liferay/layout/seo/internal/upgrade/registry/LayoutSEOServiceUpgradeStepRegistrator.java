/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.internal.upgrade.registry;

import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.layout.seo.internal.upgrade.v2_1_0.SchemaUpgradeProcess;
import com.liferay.layout.seo.internal.upgrade.v2_2_0.LayoutSEODynamicRenderingConfigurationUpgradeProcess;
import com.liferay.layout.seo.internal.upgrade.v3_0_0.LayoutSEOEntryCustomMetaTagUpgradeProcess;
import com.liferay.layout.seo.internal.upgrade.v4_0_0.LayoutSEOEntryUpgradeProcess;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = UpgradeStepRegistrator.class)
public class LayoutSEOServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "2.0.0",
			UpgradeProcessFactory.alterColumnName(
				"LayoutSEOEntry", "enabled", "canonicalURLEnabled"),
			UpgradeProcessFactory.addColumns(
				"LayoutSEOEntry", "openGraphTitleEnabled BOOLEAN",
				"openGraphTitle STRING null",
				"openGraphDescriptionEnabled BOOLEAN",
				"openGraphDescription STRING null",
				"openGraphImageFileEntryId LONG"));

		registry.register("2.0.0", "2.1.0", new SchemaUpgradeProcess());

		registry.register(
			"2.1.0", "2.2.0",
			new CTModelUpgradeProcess("LayoutSEOEntry", "LayoutSEOSite"));

		registry.register(
			"2.2.0", "2.3.0",
			new LayoutSEODynamicRenderingConfigurationUpgradeProcess(
				_configurationAdmin));

		registry.register(
			"2.3.0", "3.0.0",
			new LayoutSEOEntryCustomMetaTagUpgradeProcess(
				_classNameLocalService, _companyLocalService,
				_ddmStorageEngineManager, _ddmStorageLinkLocalService,
				_ddmStructureLocalService, _groupLocalService));

		registry.register("3.0.0", "4.0.0", new LayoutSEOEntryUpgradeProcess());
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Reference
	private DDMStorageLinkLocalService _ddmStorageLinkLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}