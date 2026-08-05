/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.internal.audit;

import com.liferay.portal.kernel.audit.AuditException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.fips.FIPSApplicationStateMachineUtil;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lucas Miranda
 */
@Component(service = FIPSHealthCheckAuditor.class)
public class FIPSHealthCheckAuditor {

	public void audit(Exception exception) {
		AuditMessage auditMessage = new AuditMessage(
			CompanyThreadLocal.getCompanyId(), PrincipalThreadLocal.getUserId(),
			PrincipalThreadLocal.getName(), FIPSModeValidator.class.getName(),
			"0", "periodic-health-failure", exception.getMessage());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		additionalInfoJSONObject.put(
			"failedTest", "self-test"
		).put(
			"fipsState",
			FIPSApplicationStateMachineUtil.getFIPSApplicationState(
			).getValue()
		).put(
			"providerMessage", exception.getMessage()
		).put(
			"severity", "critical"
		);

		try {
			_auditRouter.route(auditMessage);
		}
		catch (AuditException auditException) {
			_log.error(
				"Unable to route the periodic-health-failure audit event",
				auditException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FIPSHealthCheckAuditor.class);

	@Reference
	private AuditRouter _auditRouter;

}