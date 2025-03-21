/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.portlet.preferences;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortletKeys;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(service = PortletPreferencesLookup.class)
public class PortletPreferencesLookupImpl implements PortletPreferencesLookup {

	@Override
	public PortletPreferences fetchPreferences(
		Portlet portlet, ThemeDisplay themeDisplay) {

		if (portlet.isStatic()) {
			return portletPreferencesLocalService.fetchPreferences(
				themeDisplay.getCompanyId(), themeDisplay.getSiteGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				PortletKeys.PREFS_PLID_SHARED, portlet.getPortletId());
		}

		return portletPreferencesLocalService.fetchPreferences(
			themeDisplay.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, themeDisplay.getPlid(),
			portlet.getPortletId());
	}

	@Reference
	protected PortletPreferencesLocalService portletPreferencesLocalService;

}