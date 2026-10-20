/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.internal.upgrade.registry;

import com.liferay.bookmarks.internal.upgrade.v1_0_0.UpgradeKernelPackage;
import com.liferay.bookmarks.internal.upgrade.v1_0_0.UpgradeLastPublishDate;
import com.liferay.bookmarks.internal.upgrade.v1_0_0.UpgradePortletSettings;
import com.liferay.bookmarks.internal.upgrade.v2_0_0.UpgradeBookmarksEntryResourceBlock;
import com.liferay.bookmarks.internal.upgrade.v2_0_0.UpgradeBookmarksFolderResourceBlock;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.ViewCountUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Miguel Pastor
 */
@Component(service = UpgradeStepRegistrator.class)
public class BookmarksServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register("0.0.1", "0.0.2", new UpgradeKernelPackage());

		registry.register("0.0.2", "0.0.3", new UpgradeLastPublishDate());

		registry.register("0.0.3", "1.0.0", new UpgradePortletSettings());

		registry.register(
			"1.0.0", "1.0.1", new UpgradeBookmarksEntryResourceBlock());

		registry.register(
			"1.0.1", "2.0.0", new UpgradeBookmarksFolderResourceBlock());

		registry.register(
			"2.0.0", "2.1.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"BookmarksEntry", "BookmarksFolder"};
				}

			});

		registry.register(
			"2.1.0", "3.0.0",
			new ViewCountUpgradeProcess(
				"BookmarksEntry", BookmarksEntry.class, "entryId", "visits"));

		registry.register(
			"3.0.0", "3.1.0",
			new CTModelUpgradeProcess("BookmarksEntry", "BookmarksFolder"));
	}

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.view.count.service)(&(release.schema.version>=1.0.0)))"
	)
	private Release _release;

}