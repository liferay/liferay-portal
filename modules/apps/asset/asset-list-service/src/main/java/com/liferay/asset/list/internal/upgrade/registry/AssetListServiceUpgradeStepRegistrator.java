/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.upgrade.registry;

import com.liferay.asset.list.internal.upgrade.v1_3_0.AssetListEntryUpgradeProcess;
import com.liferay.asset.list.internal.upgrade.v1_4_0.AssetListEntryUsageUpgradeProcess;
import com.liferay.asset.list.internal.upgrade.v1_5_0.AssetListEntrySegmentsEntryRelUpgradeProcess;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = UpgradeStepRegistrator.class)
public class AssetListServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.1.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"AssetListEntry", "AssetListEntryAssetEntryRel",
						"AssetListEntrySegmentsEntryRel", "AssetListEntryUsage"
					};
				}

			});

		registry.register(
			"1.1.0", "1.2.0",
			new CTModelUpgradeProcess(
				"AssetListEntry", "AssetListEntryAssetEntryRel",
				"AssetListEntrySegmentsEntryRel", "AssetListEntryUsage"));

		registry.register("1.2.0", "1.3.0", new AssetListEntryUpgradeProcess());

		registry.register(
			"1.3.0", "1.4.0", new AssetListEntryUsageUpgradeProcess());

		registry.register(
			"1.4.0", "1.5.0",
			new AssetListEntrySegmentsEntryRelUpgradeProcess());

		registry.register(
			"1.5.0", "1.6.0",
			UpgradeProcessFactory.alterColumnType(
				"AssetListEntrySegmentsEntryRel", "priority", "INTEGER"));

		registry.register(
			"1.6.0", "1.7.0",
			UpgradeProcessFactory.runSQL(
				"update AssetListEntrySegmentsEntryRel set priority = -1 " +
					"where priority is null"));

		registry.register(
			"1.7.0", "1.8.0",
			new com.liferay.asset.list.internal.upgrade.v1_8_0.
				AssetListEntrySegmentsEntryRelUpgradeProcess());

		registry.register(
			"1.8.0", "1.9.0",
			new com.liferay.asset.list.internal.upgrade.v1_9_0.
				AssetListEntryUsageUpgradeProcess(
					_layoutLocalService, _layoutPageTemplateEntryLocalService,
					_layoutPageTemplateStructureLocalService, _portal));

		registry.register(
			"1.9.0", "2.0.0",
			UpgradeProcessFactory.dropColumns(
				"AssetListEntryUsage", "assetListEntryId", "classPK",
				"portletId"));

		registry.register("2.0.0", "2.0.1", new DummyUpgradeStep());

		registry.register(
			"2.0.1", "2.1.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"AssetListEntry"};
				}

			});

		registry.register(
			"2.1.0", "2.1.1",
			new com.liferay.asset.list.internal.upgrade.v2_1_1.
				AssetListEntryAssetEntryRelUpgradeProcess());
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private Portal _portal;

}