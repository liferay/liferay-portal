/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_2_0;

import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;

import jakarta.portlet.PortletPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marcellus Tavares
 */
public class UpgradeDynamicDataListDisplay
	extends BaseUpgradePortletPreferences {

	public UpgradeDynamicDataListDisplay() {
		_preferenceNamesMap.put("detailDDMTemplateId", "formDDMTemplateId");
		_preferenceNamesMap.put("listDDMTemplateId", "displayDDMTemplateId");
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {"169_INSTANCE_%"};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		Map<String, String[]> preferencesMap = portletPreferences.getMap();

		for (Map.Entry<String, String> entry : _preferenceNamesMap.entrySet()) {
			String name = entry.getKey();

			String[] values = preferencesMap.get(name);

			if (values == null) {
				continue;
			}

			portletPreferences.reset(name);

			String newName = entry.getValue();

			String[] newValues = preferencesMap.get(newName);

			if (newValues == null) {
				portletPreferences.setValues(newName, values);
			}
		}

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private final Map<String, String> _preferenceNamesMap = new HashMap<>();

}