/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.fido2.web.internal.audit;

import com.liferay.multi.factor.authentication.fido2.web.internal.constants.MFAFIDO2EventTypes;
import com.liferay.portal.kernel.audit.AuditException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marta Medio
 */
@Component(service = MFAFIDO2AuditMessageBuilder.class)
public class MFAFIDO2AuditMessageBuilder {

	public AuditMessage buildNonexistentUserVerificationFailureAuditMessage(
		long companyId, long userId, String checkerClassName) {

		AuditMessage auditMessage = new AuditMessage(
			companyId, userId, "Nonexistent", "verify_failure",
			JSONUtil.put("reason", "Nonexistent User"), checkerClassName,
			String.valueOf(userId),
			MFAFIDO2EventTypes.MFA_FIDO2_VERIFICATION_FAILURE, null, "mfa");

		_setVerificationResult(auditMessage, "verification_failure");

		return auditMessage;
	}

	public AuditMessage buildNotVerifiedAuditMessage(
		User user, String checkerClassName, String reason) {

		AuditMessage auditMessage = new AuditMessage(
			user.getCompanyId(), user.getUserId(), user.getFullName(),
			"verify_failure", JSONUtil.put("reason", reason), checkerClassName,
			String.valueOf(user.getPrimaryKey()),
			MFAFIDO2EventTypes.MFA_FIDO2_NOT_VERIFIED, null, "mfa");

		_setVerificationResult(auditMessage, "not_verified");

		return auditMessage;
	}

	public AuditMessage buildUnconfiguredUserVerificationFailureAuditMessage(
		long companyId, User user, String checkerClassName) {

		AuditMessage auditMessage = new AuditMessage(
			companyId, user.getUserId(), "Unconfigured", "verify_failure",
			JSONUtil.put("reason", "Unconfigured for User"), checkerClassName,
			null, MFAFIDO2EventTypes.MFA_FIDO2_VERIFICATION_FAILURE, null,
			"mfa");

		_setVerificationResult(auditMessage, "verification_failure");

		return auditMessage;
	}

	public AuditMessage buildVerificationFailureAuditMessage(
		User user, String checkerClassName, String reason) {

		AuditMessage auditMessage = new AuditMessage(
			user.getCompanyId(), user.getUserId(), user.getFullName(),
			"verify_failure", JSONUtil.put("reason", reason), checkerClassName,
			String.valueOf(user.getPrimaryKey()),
			MFAFIDO2EventTypes.MFA_FIDO2_VERIFICATION_FAILURE, null, "mfa");

		_setVerificationResult(auditMessage, "verification_failure");

		return auditMessage;
	}

	public AuditMessage buildVerifiedAuditMessage(
		User user, String checkerClassName) {

		AuditMessage auditMessage = new AuditMessage(
			user.getCompanyId(), user.getUserId(), user.getFullName(), "verify",
			null, checkerClassName, String.valueOf(user.getPrimaryKey()),
			MFAFIDO2EventTypes.MFA_FIDO2_VERIFIED, null, "mfa");

		_setVerificationResult(auditMessage, "verified");

		return auditMessage;
	}

	public void routeAuditMessage(AuditMessage auditMessage) {
		try {
			_auditRouter.route(auditMessage);
		}
		catch (AuditException auditException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", auditException);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private void _setVerificationResult(
		AuditMessage auditMessage, String outcome) {

		long companyId = auditMessage.getCompanyId();

		if ((companyId == CompanyConstants.SYSTEM) ||
			!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-6417")) {

			return;
		}

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		additionalInfoJSONObject.put(
			"factor", "fido2"
		).put(
			"outcome", outcome
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MFAFIDO2AuditMessageBuilder.class);

	@Reference
	private AuditRouter _auditRouter;

}