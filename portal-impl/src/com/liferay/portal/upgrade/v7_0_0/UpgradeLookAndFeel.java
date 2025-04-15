/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_0;

import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portlet.PortletPreferencesFactoryImpl;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.Preference;

import jakarta.portlet.PortletPreferences;

import java.util.Map;

/**
 * @author Eduardo García
 */
public class UpgradeLookAndFeel extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String getUpdatePortletPreferencesWhereClause() {
		return "preferences like '%portletSetupShowBorders%'";
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		Map<String, Preference> preferencesMap =
			PortletPreferencesFactoryImpl.createPreferencesMap(xml);

		for (Map.Entry<String, Preference> entry : preferencesMap.entrySet()) {
			String key = entry.getKey();
			Preference preference = entry.getValue();

			if (key.equals("portletSetupShowBorders")) {
				boolean showBorders = GetterUtil.getBoolean(
					preference.getValues()[0], true);

				if (!showBorders) {
					portletPreferences.setValue(
						"portletSetupPortletDecoratorId", "borderless");
				}
			}
			else {
				portletPreferences.setValues(key, preference.getValues());
			}
		}

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

}