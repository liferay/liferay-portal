/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.internal.upgrade.registry;

import com.liferay.comment.upgrade.DiscussionSubscriptionClassNameUpgradeProcess;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.subscription.service.SubscriptionLocalService;
import com.liferay.wiki.internal.upgrade.v1_0_0.SchemaUpgradeProcess;
import com.liferay.wiki.internal.upgrade.v1_0_0.UpgradeCompanyId;
import com.liferay.wiki.internal.upgrade.v1_0_0.UpgradeKernelPackage;
import com.liferay.wiki.internal.upgrade.v1_0_0.UpgradeLastPublishDate;
import com.liferay.wiki.internal.upgrade.v1_0_0.UpgradePortletPreferences;
import com.liferay.wiki.internal.upgrade.v1_0_0.UpgradePortletSettings;
import com.liferay.wiki.internal.upgrade.v1_0_0.WikiPageResourceUpgradeProcess;
import com.liferay.wiki.internal.upgrade.v1_0_0.WikiPageUpgradeProcess;
import com.liferay.wiki.internal.upgrade.v1_1_0.WikiNodeUpgradeProcess;
import com.liferay.wiki.internal.upgrade.v2_0_0.util.WikiNodeTable;
import com.liferay.wiki.internal.upgrade.v2_0_0.util.WikiPageTable;
import com.liferay.wiki.model.WikiPage;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 * @author Manuel de la Peña
 */
@Component(service = UpgradeStepRegistrator.class)
public class WikiServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register("0.0.1", "0.0.2", new SchemaUpgradeProcess());

		registry.register("0.0.2", "0.0.3", new UpgradeKernelPackage());

		registry.register("0.0.3", "0.0.4", new UpgradeCompanyId());

		registry.register("0.0.4", "0.0.5", new UpgradeLastPublishDate());

		registry.register("0.0.5", "0.0.6", new UpgradePortletPreferences());

		registry.register(
			"0.0.6", "0.0.7",
			new UpgradePortletSettings(_settingsLocatorHelper));

		registry.register(
			"0.0.7", "0.0.8", new WikiPageResourceUpgradeProcess());

		registry.register("0.0.8", "1.0.0", new WikiPageUpgradeProcess());

		registry.register("1.0.0", "1.1.0", new WikiNodeUpgradeProcess());

		registry.register(
			"1.1.0", "1.1.1",
			new DiscussionSubscriptionClassNameUpgradeProcess(
				_classNameLocalService, _subscriptionLocalService,
				WikiPage.class.getName(),
				DiscussionSubscriptionClassNameUpgradeProcess.DeletionMode.
					ADD_NEW));

		registry.register(
			"1.1.1", "2.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {WikiNodeTable.class, WikiPageTable.class}));

		registry.register(
			"2.0.0", "2.1.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"WikiNode", "WikiPage", "WikiPageResource"
					};
				}

			});

		registry.register("2.1.0", "2.1.1", new DummyUpgradeStep());

		registry.register("2.1.1", "2.2.0", new DummyUpgradeStep());

		registry.register(
			"2.2.0", "2.3.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"WikiNode", "WikiPage"};
				}

			});

		registry.register(
			"2.3.0", "2.4.0",
			new CTModelUpgradeProcess(
				"WikiNode", "WikiPage", "WikiPageResource"));
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private SettingsLocatorHelper _settingsLocatorHelper;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

}