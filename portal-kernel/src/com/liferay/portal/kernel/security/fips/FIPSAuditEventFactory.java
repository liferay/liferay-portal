/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Jorge García Jiménez
 */
public class FIPSAuditEventFactory {

	public static FIPSAuditEvent createAuthAttemptFailure(
		String attemptedUserId, String authenticationMethod,
		int consecutiveFailureCount, String failureReason, String ipSource) {

		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			"auth-attempt-failure", FIPSAuditEvent.Severity.WARNING);

		fipsAuditEvent.put(
			"attempted-user-id", GetterUtil.getString(attemptedUserId));
		fipsAuditEvent.put(
			"authentication-method",
			GetterUtil.getString(authenticationMethod));
		fipsAuditEvent.put(
			"consecutive-failure-count", consecutiveFailureCount);
		fipsAuditEvent.put(
			"failure-reason", GetterUtil.getString(failureReason));
		fipsAuditEvent.put("ip-source", GetterUtil.getString(ipSource));

		return fipsAuditEvent;
	}

	public static FIPSAuditEvent createFederationTokenRejected(
		String receivingEndpoint, String rejectedValue, String tokenIssuer,
		String tokenType) {

		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			"federation-token-rejected", FIPSAuditEvent.Severity.WARNING);

		fipsAuditEvent.put(
			"receiving-endpoint", GetterUtil.getString(receivingEndpoint));
		fipsAuditEvent.put(
			"rejected-value", GetterUtil.getString(rejectedValue));
		fipsAuditEvent.put("token-issuer", GetterUtil.getString(tokenIssuer));
		fipsAuditEvent.put("token-type", GetterUtil.getString(tokenType));

		return fipsAuditEvent;
	}

}