/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.facebook;

import com.liferay.portal.kernel.facebook.FacebookConnect;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.module.service.Snapshot;

import jakarta.portlet.PortletRequest;

/**
 * @author Wilson Man
 * @author Brian Wing Shun Chan
 * @author Mika Koivisto
 */
public class FacebookConnectUtil {

	public static String getAccessToken(
		long companyId, String redirect, String code) {

		FacebookConnect facebookConnect = _facebookConnectSnapshot.get();

		return facebookConnect.getAccessToken(companyId, redirect, code);
	}

	public static String getAccessTokenURL(long companyId) {
		FacebookConnect facebookConnect = _facebookConnectSnapshot.get();

		return facebookConnect.getAccessTokenURL(companyId);
	}

	public static String getAppId(long companyId) {
		FacebookConnect facebookConnect = _facebookConnectSnapshot.get();

		return facebookConnect.getAppId(companyId);
	}

	public static String getAppSecret(long companyId) {
		FacebookConnect facebookConnect = _facebookConnectSnapshot.get();

		return facebookConnect.getAppSecret(companyId);
	}

	public static String getAuthURL(long companyId) {
		FacebookConnect facebookConnect = _facebookConnectSnapshot.get();

		return facebookConnect.getAuthURL(companyId);
	}

	public static FacebookConnect getFacebookConnect() {
		return _facebookConnectSnapshot.get();
	}

	public static JSONObject getGraphResources(
		long companyId, String path, String accessToken, String fields) {

		FacebookConnect facebookConnect = _facebookConnectSnapshot.get();

		return facebookConnect.getGraphResources(
			companyId, path, accessToken, fields);
	}

	public static String getGraphURL(long companyId) {
		FacebookConnect facebookConnect = _facebookConnectSnapshot.get();

		return facebookConnect.getGraphURL(companyId);
	}

	public static String getProfileImageURL(PortletRequest portletRequest) {
		FacebookConnect facebookConnect = _facebookConnectSnapshot.get();

		return facebookConnect.getProfileImageURL(portletRequest);
	}

	public static String getRedirectURL(long companyId) {
		FacebookConnect facebookConnect = _facebookConnectSnapshot.get();

		return facebookConnect.getRedirectURL(companyId);
	}

	public static boolean isEnabled(long companyId) {
		FacebookConnect facebookConnect = _facebookConnectSnapshot.get();

		return facebookConnect.isEnabled(companyId);
	}

	public static boolean isVerifiedAccountRequired(long companyId) {
		FacebookConnect facebookConnect = _facebookConnectSnapshot.get();

		return facebookConnect.isVerifiedAccountRequired(companyId);
	}

	private FacebookConnectUtil() {
	}

	private static final Snapshot<FacebookConnect> _facebookConnectSnapshot =
		new Snapshot<>(FacebookConnectUtil.class, FacebookConnect.class);

}