/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.util;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Álvaro Saugar
 */
public class AuditConfigurationScopeUtil {

	public static boolean isSystemScope(HttpServletRequest httpServletRequest) {
		return ConfigurationAdminPortletKeys.SYSTEM_SETTINGS.equals(
			PortalUtil.getPortletId(
				(PortletRequest)httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_REQUEST)));
	}

}