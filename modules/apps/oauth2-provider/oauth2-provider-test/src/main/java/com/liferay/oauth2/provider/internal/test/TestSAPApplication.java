/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.test;

import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.security.service.access.policy.ServiceAccessPolicy;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Application;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Carlos Sierra Andrés
 */
public class TestSAPApplication extends Application {

	@Override
	public Set<Object> getSingletons() {
		return Collections.<Object>singleton(this);
	}

	@GET
	@Path("/{sapName}")
	public boolean isSapActive(@PathParam("sapName") String sapName) {
		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		AuthVerifierResult authVerifierResult =
			accessControlContext.getAuthVerifierResult();

		if (authVerifierResult != null) {
			Map<String, Object> settings = authVerifierResult.getSettings();

			List<String> serviceAccessPolicyNames = (List<String>)settings.get(
				ServiceAccessPolicy.SERVICE_ACCESS_POLICY_NAMES);

			return serviceAccessPolicyNames.contains(sapName);
		}

		return false;
	}

	@POST
	public String post(String post) {
		return "post";
	}

}