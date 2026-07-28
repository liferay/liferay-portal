/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.security.fips.rest.client.resource.v1_0.HealthVerificationResource;

import java.net.HttpURLConnection;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lucas Miranda
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class HealthVerificationResourceTest
	extends BaseHealthVerificationResourceTestCase {

	@Ignore
	@Override
	@Test
	public void testGraphQLPostHealthVerification() throws Exception {
	}

	@Override
	@Test
	public void testPostHealthVerification() throws Exception {
		assertHttpResponseStatusCode(
			HttpURLConnection.HTTP_CONFLICT,
			healthVerificationResource.postHealthVerificationHttpResponse());

		User user = UserTestUtil.addUser(testCompany, _PASSWORD);

		HealthVerificationResource unauthorizedHealthVerificationResource =
			HealthVerificationResource.builder(
			).authentication(
				user.getEmailAddress(), _PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		assertHttpResponseStatusCode(
			HttpURLConnection.HTTP_FORBIDDEN,
			unauthorizedHealthVerificationResource.
				postHealthVerificationHttpResponse());
	}

	private static final String _PASSWORD = "test";

}