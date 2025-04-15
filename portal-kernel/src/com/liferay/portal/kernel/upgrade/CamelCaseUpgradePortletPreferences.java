/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.util.TextFormatter;

import jakarta.portlet.PortletPreferences;

import java.util.Map;

/**
 * @author Julio Camarero
 */
public class CamelCaseUpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		Map<String, String[]> preferencesMap = portletPreferences.getMap();

		for (Map.Entry<String, String[]> entry : preferencesMap.entrySet()) {
			String oldName = entry.getKey();

			String newName = TextFormatter.format(oldName, TextFormatter.M);

			portletPreferences.reset(oldName);

			portletPreferences.setValues(newName, entry.getValue());
		}

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

}