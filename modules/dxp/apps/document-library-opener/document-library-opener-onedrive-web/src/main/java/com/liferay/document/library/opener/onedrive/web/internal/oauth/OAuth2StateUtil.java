/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.oauth;

import com.liferay.document.library.opener.oauth.OAuth2State;
import com.liferay.document.library.opener.onedrive.web.internal.constants.DLOpenerOneDriveWebKeys;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * @author Alicia Garcia Garcia
 * @author Cristina González
 */
public class OAuth2StateUtil {

	public static void cleanUp(HttpServletRequest httpServletRequest) {
		HttpSession httpSession = httpServletRequest.getSession();

		httpSession.removeAttribute(
			DLOpenerOneDriveWebKeys.DL_OPENER_ONE_DRIVE_OAUTH2_STATE);
	}

	public static OAuth2State getOAuth2State(
		HttpServletRequest httpServletRequest) {

		HttpSession httpSession = httpServletRequest.getSession();

		return (OAuth2State)httpSession.getAttribute(
			DLOpenerOneDriveWebKeys.DL_OPENER_ONE_DRIVE_OAUTH2_STATE);
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
			DLOpenerOneDriveWebKeys.DL_OPENER_ONE_DRIVE_OAUTH2_STATE,
			oAuth2State);
	}

}