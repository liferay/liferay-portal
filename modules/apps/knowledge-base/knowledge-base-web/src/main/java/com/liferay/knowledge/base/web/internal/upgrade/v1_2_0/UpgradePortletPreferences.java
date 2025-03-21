/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.upgrade.v1_2_0;

import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;

import jakarta.portlet.PortletPreferences;

/**
 * @author Alejandro Tardín
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String[] getPortletIds() {
		return _PORTLET_IDS;
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		String socialBookmarksDisplayStyle = portletPreferences.getValue(
			"socialBookmarksDisplayStyle", "menu");

		if (!socialBookmarksDisplayStyle.equals("menu")) {
			portletPreferences.setValue(
				"socialBookmarksDisplayStyle", "inline");
		}

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private static final String[] _PORTLET_IDS = {
		"com_liferay_knowledge_base_web_portlet_ArticlePortlet_INSTANCE_%",
		"com_liferay_knowledge_base_web_portlet_DisplayPortlet",
		"com_liferay_knowledge_base_web_portlet_SearchPortlet",
		"com_liferay_knowledge_base_web_portlet_SectionPortlet_INSTANCE_%"
	};

}