/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.google.drive.web.internal.oauth;

import com.liferay.document.library.opener.google.drive.web.internal.constants.DLOpenerGoogleDriveWebConstants;
import com.liferay.document.library.opener.oauth.OAuth2State;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * @author Alicia Garcia Garcia
 */
public class OAuth2StateUtil {

	public static void cleanUp(HttpServletRequest httpServletRequest) {
		HttpSession httpSession = httpServletRequest.getSession();

		httpSession.removeAttribute(
			_SESSION_ATTRIBUTE_NAME_GOOGLE_OAUTH2_STATE);
	}

	public static OAuth2State getOAuth2State(
		HttpServletRequest httpServletRequest) {

		HttpSession httpSession = httpServletRequest.getSession();

		return (OAuth2State)httpSession.getAttribute(
			_SESSION_ATTRIBUTE_NAME_GOOGLE_OAUTH2_STATE);
	}

	public static String getRedirectURI(String portalURL) {
		return portalURL + PortalUtil.getPathContext() + Portal.PATH_MODULE +
			DLOpenerGoogleDriveWebConstants.GOOGLE_DRIVE_SERVLET_PATH;
	}

	public static boolean isValid(
		OAuth2State oAuth2State, HttpServletRequest httpServletRequest) {

		if (Validator.isNotNull(
				ParamUtil.getString(httpServletRequest, "error"))) {

			return false;
		}

		String state = ParamUtil.getString(httpServletRequest, "state");

		return oAuth2State.isValid(state);
	}

	public static void save(
		HttpServletRequest httpServletRequest, OAuth2State oAuth2State) {

		HttpSession httpSession = httpServletRequest.getSession();

		httpSession.setAttribute(
			_SESSION_ATTRIBUTE_NAME_GOOGLE_OAUTH2_STATE, oAuth2State);
	}

	private static final String _SESSION_ATTRIBUTE_NAME_GOOGLE_OAUTH2_STATE =
		"google-oauth2-state";

}