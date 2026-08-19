/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.security.fips.rest.client.dto.v1_0.FIPSHealthVerification;
import com.liferay.portal.security.fips.rest.client.http.HttpInvoker;
import com.liferay.portal.security.fips.rest.client.problem.Problem;
import com.liferay.portal.security.fips.rest.client.resource.v1_0.FIPSHealthVerificationResource;
import com.liferay.portal.security.fips.rest.client.serdes.v1_0.FIPSHealthVerificationSerDes;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lucas Miranda
 */
@RunWith(Arquillian.class)
public class FIPSHealthVerificationResourceTest
	extends BaseFIPSHealthVerificationResourceTestCase {

	@Override
	@Test
	public void testPostFIPSHealthVerification() throws Exception {
		Assume.assumeFalse(PropsValues.FIPS_ENABLED);

		User user = UserTestUtil.addUser(
			testCompany, PropsValues.DEFAULT_ADMIN_PASSWORD);

		FIPSHealthVerificationResource fipsHealthVerificationResource =
			FIPSHealthVerificationResource.builder(
			).authentication(
				user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		assertHttpResponseStatusCode(
			404,
			fipsHealthVerificationResource.
				postFIPSHealthVerificationHttpResponse());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN);
			SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			Problem.ProblemException problemException = Assert.assertThrows(
				Problem.ProblemException.class,
				fipsHealthVerificationResource::postFIPSHealthVerification);

			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());

			Role role = RoleTestUtil.addRole(
				RoleConstants.CRYPTO_OFFICER, RoleConstants.TYPE_REGULAR);

			_userLocalService.addRoleUser(role.getRoleId(), user);

			HttpInvoker.HttpResponse httpResponse =
				fipsHealthVerificationResource.
					postFIPSHealthVerificationHttpResponse();

			assertHttpResponseStatusCode(503, httpResponse);

			FIPSHealthVerification fipsHealthVerification =
				FIPSHealthVerificationSerDes.toDTO(httpResponse.getContent());

			Assert.assertNotNull(fipsHealthVerification.getErrorMessage());
			Assert.assertEquals(
				FIPSHealthVerification.Status.ERROR,
				fipsHealthVerification.getStatus());
		}
	}

	@Inject
	private UserLocalService _userLocalService;

}