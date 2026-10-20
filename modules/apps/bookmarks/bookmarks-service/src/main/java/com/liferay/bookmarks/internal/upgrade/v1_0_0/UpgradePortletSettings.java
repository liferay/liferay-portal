/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.internal.upgrade.v1_0_0;

import com.liferay.bookmarks.configuration.BookmarksGroupServiceConfiguration;
import com.liferay.bookmarks.constants.BookmarksConstants;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.portal.kernel.util.PortletKeys;

/**
 * @author Miguel Pastor
 */
public class UpgradePortletSettings
	extends com.liferay.portal.upgrade.v7_0_0.UpgradePortletSettings {

	@Override
	protected void doUpgrade() throws Exception {
		upgradeMainPortlet(
			BookmarksPortletKeys.BOOKMARKS, BookmarksConstants.SERVICE_NAME,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, new String[0],
			getKeys(BookmarksGroupServiceConfiguration.class));
	}

}