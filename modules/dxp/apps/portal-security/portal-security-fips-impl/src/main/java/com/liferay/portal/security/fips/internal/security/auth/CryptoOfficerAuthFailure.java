/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.security.auth;

import com.liferay.portal.kernel.audit.AuditRequestThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.AuthFailure;
import com.liferay.portal.kernel.security.fips.FIPSAuditEventFactory;
import com.liferay.portal.kernel.security.fips.FIPSAuditUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.security.fips.util.FIPSUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Manuele Castro
 */
@Component(
	property = {"key=auth.failure", "service.ranking:Integer=-100"},
	service = AuthFailure.class
)
public class CryptoOfficerAuthFailure implements AuthFailure {

	@Override
	public void onFailureByEmailAddress(
		long companyId, String emailAddress, Map<String, String[]> headerMap,
		Map<String, String[]> parameterMap) {

		_auditAuthAttemptFailure(
			_userLocalService.fetchUserByEmailAddress(companyId, emailAddress));
	}

	@Override
	public void onFailureByScreenName(
		long companyId, String screenName, Map<String, String[]> headerMap,
		Map<String, String[]> parameterMap) {

		_auditAuthAttemptFailure(
			_userLocalService.fetchUserByScreenName(companyId, screenName));
	}

	@Override
	public void onFailureByUserId(
		long companyId, long userId, Map<String, String[]> headerMap,
		Map<String, String[]> parameterMap) {

		_auditAuthAttemptFailure(_userLocalService.fetchUserById(userId));
	}

	private void _auditAuthAttemptFailure(User user) {
		if ((user == null) || !FIPSUtil.hasCryptoOfficerRole(user)) {
			return;
		}

		try {
			String failureReason = "bad-credential";

			if (user.isLockout()) {
				failureReason = "locked";
			}

			AuditRequestThreadLocal auditRequestThreadLocal =
				AuditRequestThreadLocal.getAuditThreadLocal();

			FIPSAuditUtil.write(
				FIPSAuditEventFactory.createAuthAttemptFailure(
					String.valueOf(user.getUserId()), "local",
					auditRequestThreadLocal.getClientIP(),
					user.getFailedLoginAttempts(), failureReason));
		}
		catch (Throwable throwable) {
			_log.error(
				"Unable to write the auth attempt failure FIPS audit event",
				throwable);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CryptoOfficerAuthFailure.class);

	@Reference
	private UserLocalService _userLocalService;

}