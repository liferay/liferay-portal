/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.upgrade.v1_1_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;

import jakarta.portlet.PortletPreferences;

/**
 * @author Sergio González
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String[] getPortletIds() {
		return new String[] {"com_liferay_blogs_web_portlet_BlogsPortlet"};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		String enableSocialBookmarks = portletPreferences.getValue(
			"enableSocialBookmarks", Boolean.TRUE.toString());

		if (!enableSocialBookmarks.equals(Boolean.TRUE.toString())) {
			portletPreferences.setValue(
				"socialBookmarksTypes", StringPool.BLANK);
		}

		portletPreferences.reset("enableSocialBookmarks");
		portletPreferences.reset("socialBookmarksDisplayPosition");

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

}