/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.staging.constants.StagingProcessesPortletKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Evan Thibodeau
 */
public class StagingIndicatorUtil {

	public static boolean isShowStagingIndicator(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group scopeGroup = themeDisplay.getScopeGroup();

		if (scopeGroup.isDepot() && scopeGroup.isStaged() &&
			PortletPermissionUtil.contains(
				themeDisplay.getPermissionChecker(),
				StagingProcessesPortletKeys.STAGING_PROCESSES,
				ActionKeys.VIEW)) {

			return true;
		}

		return false;
	}

}