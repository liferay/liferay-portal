/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.upgrade.v1_0_0;

import com.liferay.blogs.configuration.BlogsGroupServiceConfiguration;
import com.liferay.blogs.constants.BlogsConstants;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.web.internal.configuration.BlogsPortletInstanceConfiguration;
import com.liferay.portal.kernel.util.PortletKeys;

/**
 * @author Sergio González
 */
public class UpgradePortletSettings
	extends com.liferay.portal.upgrade.v7_0_0.UpgradePortletSettings {

	@Override
	protected void doUpgrade() throws Exception {
		upgradeMainPortlet(
			BlogsPortletKeys.BLOGS, BlogsConstants.SERVICE_NAME,
			PortletKeys.PREFS_OWNER_TYPE_GROUP,
			getKeys(BlogsPortletInstanceConfiguration.class),
			getKeys(BlogsGroupServiceConfiguration.class));
	}

}