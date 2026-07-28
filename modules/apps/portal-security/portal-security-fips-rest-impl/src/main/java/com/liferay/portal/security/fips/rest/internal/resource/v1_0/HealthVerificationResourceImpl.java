/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.fips.FIPSHealthCheckResult;
import com.liferay.portal.kernel.security.fips.FIPSHealthCheckStatus;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.security.fips.rest.dto.v1_0.HealthVerification;
import com.liferay.portal.security.fips.rest.resource.v1_0.HealthVerificationResource;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Lucas Miranda
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/health-verification.properties",
	scope = ServiceScope.PROTOTYPE, service = HealthVerificationResource.class
)
public class HealthVerificationResourceImpl
	extends BaseHealthVerificationResourceImpl {

	@Override
	public HealthVerification postHealthVerification() throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isOmniadmin() &&
			!permissionChecker.isCompanyAdmin()) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, "TRIGGER_HEALTH_VERIFICATION");
		}

		FIPSHealthCheckResult fipsHealthCheckResult =
			FIPSModeValidator.runSelfTests();

		if (fipsHealthCheckResult.getStatus() ==
				FIPSHealthCheckStatus.IN_PROGRESS) {

			throw new WebApplicationException(
				Response.status(
					Response.Status.TOO_MANY_REQUESTS
				).build());
		}

		HealthVerification healthVerification = _toHealthVerification(
			fipsHealthCheckResult);

		if (fipsHealthCheckResult.getStatus() ==
				FIPSHealthCheckStatus.NOT_APPLICABLE) {

			throw new WebApplicationException(
				Response.status(
					Response.Status.CONFLICT
				).entity(
					healthVerification
				).build());
		}

		if (fipsHealthCheckResult.getStatus() == FIPSHealthCheckStatus.FAILED) {
			throw new WebApplicationException(
				Response.status(
					Response.Status.SERVICE_UNAVAILABLE
				).entity(
					healthVerification
				).build());
		}

		return healthVerification;
	}

	@Override
	public Response postHealthVerificationBatch(
		String callbackURL, Object object) {

		return Response.status(
			Response.Status.METHOD_NOT_ALLOWED
		).build();
	}

	private HealthVerification _toHealthVerification(
		FIPSHealthCheckResult fipsHealthCheckResult) {

		HealthVerification healthVerification = new HealthVerification();

		healthVerification.setDate(() -> new Date());
		healthVerification.setFailedTest(fipsHealthCheckResult::getFailedTest);
		healthVerification.setFipsState(fipsHealthCheckResult::getFipsState);
		healthVerification.setProviderMessage(
			fipsHealthCheckResult::getProviderMessage);
		healthVerification.setProviderName(
			fipsHealthCheckResult::getProviderName);
		healthVerification.setStatus(
			() -> HealthVerification.Status.create(
				fipsHealthCheckResult.getStatus(
				).name()));

		return healthVerification;
	}

}