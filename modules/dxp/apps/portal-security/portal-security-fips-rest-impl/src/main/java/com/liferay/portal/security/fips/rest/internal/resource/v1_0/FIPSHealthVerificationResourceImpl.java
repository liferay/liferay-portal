/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.fips.FIPSApplicationState;
import com.liferay.portal.kernel.security.fips.FIPSApplicationStateMachineUtil;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.security.fips.rest.dto.v1_0.FIPSHealthVerification;
import com.liferay.portal.security.fips.rest.resource.v1_0.FIPSHealthVerificationResource;
import com.liferay.portal.security.fips.util.FIPSUtil;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Lucas Miranda
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/fips-health-verification.properties",
	scope = ServiceScope.PROTOTYPE,
	service = FIPSHealthVerificationResource.class
)
public class FIPSHealthVerificationResourceImpl
	extends BaseFIPSHealthVerificationResourceImpl {

	@Override
	public FIPSHealthVerification postFIPSHealthVerification()
		throws Exception {

		if (!PropsValues.FIPS_ENABLED) {
			throw new NotFoundException();
		}

		if (!FIPSUtil.hasCryptoOfficerRole(contextUser)) {
			throw new PrincipalException();
		}

		FIPSHealthVerification fipsHealthVerification =
			new FIPSHealthVerification();

		fipsHealthVerification.setDateVerified(Date::new);

		try {
			FIPSApplicationStateMachineUtil.selfTest(
				FIPSModeValidator::validate);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to complete the FIPS health verification",
					exception);
			}

			fipsHealthVerification.setErrorMessage(exception::getMessage);
		}

		FIPSApplicationState fipsApplicationState =
			FIPSApplicationStateMachineUtil.getFIPSApplicationState();

		fipsHealthVerification.setStatus(
			() -> FIPSHealthVerification.Status.create(
				fipsApplicationState.name()));

		if (fipsApplicationState != FIPSApplicationState.OPERATIONAL) {
			throw new WebApplicationException(
				Response.status(
					Response.Status.SERVICE_UNAVAILABLE
				).entity(
					fipsHealthVerification
				).build());
		}

		return fipsHealthVerification;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FIPSHealthVerificationResourceImpl.class);

}