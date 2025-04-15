/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_1_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletPreferences;

/**
 * @author Bryan Engler
 */
public class UpgradeCalendarClassNameIdsPortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String getUpdatePortletPreferencesWhereClause() {
		return StringBundler.concat(
			"(preferences like '%classNameIds%",
			PortalUtil.getClassNameId(
				"com.liferay.portlet.calendar.model.CalEvent"),
			"%') or (preferences like '%anyAssetType%",
			PortalUtil.getClassNameId(
				"com.liferay.portlet.calendar.model.CalEvent"),
			"%')");
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		_replaceClassNameId(portletPreferences, "anyAssetType");
		_replaceClassNameId(portletPreferences, "classNameIds");

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private void _replaceClassNameId(
			PortletPreferences portletPreferences, String name)
		throws Exception {

		String[] values = GetterUtil.getStringValues(
			portletPreferences.getValues(name, null));

		ArrayUtil.replace(
			values, "com.liferay.portlet.calendar.model.CalEvent",
			String.valueOf(
				PortalUtil.getClassNameId(
					"com.liferay.calendar.model.CalendarBooking")));

		portletPreferences.setValues(name, values);
	}

}