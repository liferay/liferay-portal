/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.display.context;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import java.io.Serializable;

/**
 * @author Drew Brokke
 */
public class ConfigurationScopeDisplayContextFactory {

	public static ConfigurationScopeDisplayContext create(
		PortletRequest portletRequest) {

		return create(
			PortalUtil.getPortletId(portletRequest),
			(ThemeDisplay)portletRequest.getAttribute(WebKeys.THEME_DISPLAY));
	}

	public static ConfigurationScopeDisplayContext create(
		String portletId, ThemeDisplay themeDisplay) {

		ExtendedObjectClassDefinition.Scope scope =
			ExtendedObjectClassDefinition.Scope.SYSTEM;

		Serializable scopePK = null;

		if (portletId.equals(ConfigurationAdminPortletKeys.INSTANCE_SETTINGS)) {
			scope = ExtendedObjectClassDefinition.Scope.COMPANY;
			scopePK = themeDisplay.getCompanyId();
		}
		else if (portletId.equals(
					ConfigurationAdminPortletKeys.SITE_SETTINGS)) {

			scope = ExtendedObjectClassDefinition.Scope.GROUP;
			scopePK = themeDisplay.getScopeGroupId();
		}

		return new ConfigurationScopeDisplayContext(scope, scopePK);
	}

}