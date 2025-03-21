/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_2_0;

import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.portlet.PortletPreferences;

/**
 * @author Eduardo García
 */
public class UpgradeBlogsAggregator extends BaseUpgradePortletPreferences {

	@Override
	protected String[] getPortletIds() {
		return new String[] {"115"};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		boolean enableRssSubscription = GetterUtil.getBoolean(
			portletPreferences.getValue("enableRssSubscription", null), true);

		if (!enableRssSubscription) {
			portletPreferences.setValue("enableRss", Boolean.FALSE.toString());
		}

		portletPreferences.reset("enableRssSubscription");

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

}