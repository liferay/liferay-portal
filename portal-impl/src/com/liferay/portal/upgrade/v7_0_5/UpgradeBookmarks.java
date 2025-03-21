/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_5;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

/**
 * @author Jonathan McCann
 */
public class UpgradeBookmarks extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String[] getPortletIds() {
		return new String[] {"28"};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		String entryColumns = portletPreferences.getValue(
			"entryColumns", StringPool.BLANK);

		if (Validator.isNotNull(entryColumns) &&
			entryColumns.contains(StringPool.COMMA)) {

			portletPreferences.setValues(
				"entryColumns", entryColumns.split(StringPool.COMMA));
		}

		String folderColumns = portletPreferences.getValue(
			"folderColumns", StringPool.BLANK);

		if (Validator.isNotNull(folderColumns) &&
			folderColumns.contains(StringPool.COMMA)) {

			portletPreferences.setValues(
				"folderColumns", folderColumns.split(StringPool.COMMA));
		}

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

}