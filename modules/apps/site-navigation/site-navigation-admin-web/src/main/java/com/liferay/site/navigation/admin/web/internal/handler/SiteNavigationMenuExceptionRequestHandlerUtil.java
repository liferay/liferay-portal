/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.admin.web.internal.handler;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.exception.DuplicateSiteNavigationMenuException;
import com.liferay.site.navigation.exception.SiteNavigationMenuNameException;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

/**
 * @author Jürgen Kappler
 */
public class SiteNavigationMenuExceptionRequestHandlerUtil {

	public static void handlePortalException(
			ActionRequest actionRequest, ActionResponse actionResponse,
			PortalException portalException)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(portalException);
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String errorMessage = "an-unexpected-error-occurred";

		if (portalException instanceof DuplicateSiteNavigationMenuException) {
			errorMessage = "please-enter-a-unique-name";
		}
		else if (portalException instanceof SiteNavigationMenuNameException) {
			errorMessage = "please-enter-a-valid-name";
		}
		else {
			_log.error(portalException);
		}

		JSONObject jsonObject = JSONUtil.put(
			"error", LanguageUtil.get(themeDisplay.getLocale(), errorMessage));

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SiteNavigationMenuExceptionRequestHandlerUtil.class);

}