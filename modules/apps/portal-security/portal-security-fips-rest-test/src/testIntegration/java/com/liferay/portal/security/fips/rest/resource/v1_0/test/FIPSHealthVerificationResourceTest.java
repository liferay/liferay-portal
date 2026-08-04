/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.security.fips.rest.client.http.HttpInvoker;
import com.liferay.portal.security.fips.rest.client.resource.v1_0.FIPSHealthVerificationResource;

import java.net.HttpURLConnection;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lucas Miranda
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class FIPSHealthVerificationResourceTest
	extends BaseFIPSHealthVerificationResourceTestCase {

	@Override
	@Test
	public void testPostFIPSHealthVerification() throws Exception {
		HttpInvoker.HttpResponse httpResponse =
			fipsHealthVerificationResource.
				postFIPSHealthVerificationHttpResponse();

		assertHttpResponseStatusCode(
			HttpURLConnection.HTTP_NOT_FOUND, httpResponse);

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		FIPSHealthVerificationResource
			unauthorizedFIPSHealthVerificationResource =
				FIPSHealthVerificationResource.builder(
				).authentication(
					user.getEmailAddress(), password
				).endpoint(
					testCompany.getVirtualHostname(),
					PortalUtil.getPortalServerPort(false), "http"
				).locale(
					LocaleUtil.getDefault()
				).build();

		assertHttpResponseStatusCode(
			HttpURLConnection.HTTP_FORBIDDEN,
			unauthorizedFIPSHealthVerificationResource.
				postFIPSHealthVerificationHttpResponse());
	}

}