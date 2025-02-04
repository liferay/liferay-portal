/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.upgrade.registry;

import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.style.book.internal.upgrade.v1_1_0.StyleBookEntryUpgradeProcess;
import com.liferay.style.book.internal.upgrade.v1_2_0.StyleBookEntryVersionUpgradeProcess;
import com.liferay.style.book.internal.upgrade.v1_7_0.StyleBookEntryThemeIdUpgradeProcess;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = UpgradeStepRegistrator.class)
public class StyleBookServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register("0.0.1", "1.0.0", new DummyUpgradeStep());

		registry.register("1.0.0", "1.1.0", new StyleBookEntryUpgradeProcess());

		registry.register(
			"1.1.0", "1.2.0", new StyleBookEntryVersionUpgradeProcess());

		registry.register(
			"1.2.0", "1.2.1",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"StyleBookEntryVersion"};
				}

			});

		registry.register(
			"1.2.1", "1.3.0", new CTModelUpgradeProcess("StyleBookEntry"));

		registry.register(
			"1.3.0", "1.4.0",
			new CTModelUpgradeProcess("StyleBookEntryVersion"));

		registry.register(
			"1.4.0", "1.4.1",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"StyleBookEntryVersion"};
				}

			});

		registry.register(
			"1.4.1", "1.5.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"StyleBookEntry", "styleBookEntryId"},
						{"StyleBookEntryVersion", "styleBookEntryId"}
					};
				}

			});

		registry.register(
			"1.5.0", "1.6.0",
			UpgradeProcessFactory.addColumns(
				"StyleBookEntry", "themeId VARCHAR(255) null"),
			UpgradeProcessFactory.addColumns(
				"StyleBookEntryVersion", "themeId VARCHAR(255) null"));

		registry.register(
			"1.6.0", "1.7.0",
			new StyleBookEntryThemeIdUpgradeProcess(_groupLocalService));
	}

	@Reference
	private GroupLocalService _groupLocalService;

}