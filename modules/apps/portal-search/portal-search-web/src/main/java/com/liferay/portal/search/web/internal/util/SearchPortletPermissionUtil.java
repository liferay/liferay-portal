/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;

import jakarta.portlet.RenderRequest;

/**
 * @author André de Oliveira
 */
public class SearchPortletPermissionUtil {

	public static boolean containsConfiguration(
		RenderRequest renderRequest, Portal portal) {

		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(renderRequest);

		ThemeDisplay themeDisplay = themeDisplaySupplier.getThemeDisplay();

		try {
			return PortletPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), themeDisplay.getPlid(),
				portal.getPortletId(renderRequest), ActionKeys.CONFIGURATION);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SearchPortletPermissionUtil.class);

}