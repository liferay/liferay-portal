/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.internal.upgrade.registry;

import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.site.navigation.internal.upgrade.v2_0_0.util.SiteNavigationMenuItemTable;
import com.liferay.site.navigation.internal.upgrade.v2_0_0.util.SiteNavigationMenuTable;
import com.liferay.site.navigation.internal.upgrade.v2_3_0.SiteNavigationMenuItemUpgradeProcess;
import com.liferay.site.navigation.internal.upgrade.v3_0_0.SiteNavigationMenuItemExternalReferenceCodeUpgradeProcess;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author José Ángel Jiménez
 */
@Component(service = UpgradeStepRegistrator.class)
public class SiteNavigationServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "2.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {
					SiteNavigationMenuItemTable.class,
					SiteNavigationMenuTable.class
				}));

		registry.register(
			"2.0.0", "2.1.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"SiteNavigationMenu", "SiteNavigationMenuItem"
					};
				}

			});

		registry.register(
			"2.1.0", "2.2.0",
			new CTModelUpgradeProcess(
				"SiteNavigationMenu", "SiteNavigationMenuItem"));

		registry.register(
			"2.2.0", "2.3.0", new SiteNavigationMenuItemUpgradeProcess());

		registry.register(
			"2.3.0", "2.4.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"SiteNavigationMenuItem", "siteNavigationMenuItemId"}
					};
				}

			});

		registry.register(
			"2.4.0", "2.5.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"SiteNavigationMenu", "siteNavigationMenuId"}
					};
				}

			});

		registry.register(
			"2.5.0", "3.0.0",
			new SiteNavigationMenuItemExternalReferenceCodeUpgradeProcess(
				_assetVocabularyLocalService, _journalArticleLocalService,
				_kbArticleLocalService, _layoutLocalService));
	}

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

}