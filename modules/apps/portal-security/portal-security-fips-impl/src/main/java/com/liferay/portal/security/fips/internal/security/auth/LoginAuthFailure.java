/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.security.auth;

import com.liferay.portal.kernel.security.auth.AuthFailure;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.security.fips.util.FIPSUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Ranked below the login module's own <code>auth.failure</code> handler so
 * that the failed login attempt is already counted by the time the audit
 * event reads it.
 *
 * @author Manuele Castro
 */
@Component(
	property = {"key=auth.failure", "service.ranking:Integer=-100"},
	service = AuthFailure.class
)
public class LoginAuthFailure implements AuthFailure {

	@Override
	public void onFailureByEmailAddress(
		long companyId, String emailAddress, Map<String, String[]> headerMap,
		Map<String, String[]> parameterMap) {

		FIPSUtil.checkCryptoOfficerLoginFailure(
			_userLocalService.fetchUserByEmailAddress(companyId, emailAddress));
	}

	@Override
	public void onFailureByScreenName(
		long companyId, String screenName, Map<String, String[]> headerMap,
		Map<String, String[]> parameterMap) {

		FIPSUtil.checkCryptoOfficerLoginFailure(
			_userLocalService.fetchUserByScreenName(companyId, screenName));
	}

	@Override
	public void onFailureByUserId(
		long companyId, long userId, Map<String, String[]> headerMap,
		Map<String, String[]> parameterMap) {

		FIPSUtil.checkCryptoOfficerLoginFailure(
			_userLocalService.fetchUserById(userId));
	}

	@Reference
	private UserLocalService _userLocalService;

}