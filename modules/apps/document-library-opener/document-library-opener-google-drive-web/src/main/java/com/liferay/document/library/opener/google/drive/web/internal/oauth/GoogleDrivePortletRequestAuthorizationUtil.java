/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.google.drive.web.internal.oauth;

import com.liferay.document.library.opener.google.drive.web.internal.DLOpenerGoogleDriveManager;
import com.liferay.document.library.opener.oauth.OAuth2State;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Alejandro Tardín
 */
public class GoogleDrivePortletRequestAuthorizationUtil {

	public static void performAuthorizationFlow(
			DLOpenerGoogleDriveManager dlOpenerGoogleDriveManager,
			Portal portal, PortletRequest portletRequest,
			PortletResponse portletResponse,
			PortletURLFactory portletURLFactory)
		throws IOException, PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String state = PwdGenerator.getPassword(5);

		OAuth2StateUtil.save(
			portal.getOriginalServletRequest(
				portal.getHttpServletRequest(portletRequest)),
			new OAuth2State(
				themeDisplay.getUserId(),
				_getSuccessURL(portal, portletRequest),
				_getFailureURL(portal, portletRequest, portletURLFactory),
				state));

		String authorizationURL =
			dlOpenerGoogleDriveManager.getAuthorizationURL(
				themeDisplay.getCompanyId(), state,
				OAuth2StateUtil.getRedirectURI(
					portal.getPortalURL(portletRequest)));

		if (!dlOpenerGoogleDriveManager.hasValidCredential(
				themeDisplay.getCompanyId(), themeDisplay.getUserId())) {

			authorizationURL = HttpComponentsUtil.setParameter(
				authorizationURL, "prompt", "select_account");
		}

		HttpServletResponse httpServletResponse = portal.getHttpServletResponse(
			portletResponse);

		httpServletResponse.sendRedirect(authorizationURL);
	}

	private static String _getFailureURL(
			Portal portal, PortletRequest portletRequest,
			PortletURLFactory portletURLFactory)
		throws PortalException {

		LiferayPortletURL liferayPortletURL = portletURLFactory.create(
			portletRequest, portal.getPortletId(portletRequest),
			portal.getControlPanelPlid(portletRequest),
			PortletRequest.RENDER_PHASE);

		return liferayPortletURL.toString();
	}

	private static String _getSuccessURL(
		Portal portal, PortletRequest portletRequest) {

		return portal.getCurrentURL(
			portal.getHttpServletRequest(portletRequest));
	}

}