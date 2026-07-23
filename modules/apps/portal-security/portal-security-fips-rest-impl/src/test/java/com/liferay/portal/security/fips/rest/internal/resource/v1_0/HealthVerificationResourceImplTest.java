/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.fips.FIPSHealthCheckResult;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.security.fips.rest.dto.v1_0.HealthVerification;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lucas Miranda
 */
public class HealthVerificationResourceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_healthVerificationResourceImpl = new HealthVerificationResourceImpl();

		ReflectionTestUtil.setFieldValue(
			_healthVerificationResourceImpl, "_roleLocalService",
			Mockito.mock(RoleLocalService.class));

		_permissionChecker = Mockito.mock(PermissionChecker.class);

		Mockito.when(
			_permissionChecker.isOmniadmin()
		).thenReturn(
			true
		);

		// A JAX-RS RuntimeDelegate is required to build a Response outside the
		// server runtime.

		_responseBuilder = Mockito.mock(
			Response.ResponseBuilder.class, Mockito.RETURNS_SELF);

		Response response = Mockito.mock(Response.class);

		Mockito.when(
			response.getStatusInfo()
		).thenReturn(
			Response.Status.SERVICE_UNAVAILABLE
		);

		Mockito.when(
			_responseBuilder.build()
		).thenReturn(
			response
		);

		RuntimeDelegate runtimeDelegate = Mockito.mock(RuntimeDelegate.class);

		Mockito.when(
			runtimeDelegate.createResponseBuilder()
		).thenReturn(
			_responseBuilder
		);

		RuntimeDelegate.setInstance(runtimeDelegate);
	}

	@After
	public void tearDown() {
		RuntimeDelegate.setInstance(null);
	}

	@Test
	public void testPostHealthVerificationFailedReturns503() throws Exception {
		try (MockedStatic<PermissionThreadLocal>
				permissionThreadLocalMockedStatic = Mockito.mockStatic(
					PermissionThreadLocal.class);
			MockedStatic<FIPSModeValidator> fipsModeValidatorMockedStatic =
				Mockito.mockStatic(FIPSModeValidator.class)) {

			permissionThreadLocalMockedStatic.when(
				PermissionThreadLocal::getPermissionChecker
			).thenReturn(
				_permissionChecker
			);

			fipsModeValidatorMockedStatic.when(
				FIPSModeValidator::runSelfTests
			).thenReturn(
				FIPSHealthCheckResult.failed(
					"BCFIPS", "AES-KAT", "ERROR", "boom")
			);

			Assert.assertThrows(
				WebApplicationException.class,
				_healthVerificationResourceImpl::postHealthVerification);

			Mockito.verify(
				_responseBuilder
			).status(
				(Response.StatusType)Response.Status.SERVICE_UNAVAILABLE
			);
		}
	}

	@Test
	public void testPostHealthVerificationHealthyReturns200() throws Exception {
		try (MockedStatic<PermissionThreadLocal>
				permissionThreadLocalMockedStatic = Mockito.mockStatic(
					PermissionThreadLocal.class);
			MockedStatic<FIPSModeValidator> fipsModeValidatorMockedStatic =
				Mockito.mockStatic(FIPSModeValidator.class)) {

			permissionThreadLocalMockedStatic.when(
				PermissionThreadLocal::getPermissionChecker
			).thenReturn(
				_permissionChecker
			);

			fipsModeValidatorMockedStatic.when(
				FIPSModeValidator::runSelfTests
			).thenReturn(
				FIPSHealthCheckResult.healthy("BCFIPS")
			);

			HealthVerification healthVerification =
				_healthVerificationResourceImpl.postHealthVerification();

			Assert.assertEquals(
				HealthVerification.Status.HEALTHY,
				healthVerification.getStatus());
		}
	}

	@Test
	public void testPostHealthVerificationNotApplicableReturns409()
		throws Exception {

		try (MockedStatic<PermissionThreadLocal>
				permissionThreadLocalMockedStatic = Mockito.mockStatic(
					PermissionThreadLocal.class);
			MockedStatic<FIPSModeValidator> fipsModeValidatorMockedStatic =
				Mockito.mockStatic(FIPSModeValidator.class)) {

			permissionThreadLocalMockedStatic.when(
				PermissionThreadLocal::getPermissionChecker
			).thenReturn(
				_permissionChecker
			);

			fipsModeValidatorMockedStatic.when(
				FIPSModeValidator::runSelfTests
			).thenReturn(
				FIPSHealthCheckResult.notApplicable()
			);

			Assert.assertThrows(
				WebApplicationException.class,
				_healthVerificationResourceImpl::postHealthVerification);

			Mockito.verify(
				_responseBuilder
			).status(
				(Response.StatusType)Response.Status.CONFLICT
			);
		}
	}

	@Test
	public void testPostHealthVerificationUnauthorizedThrowsPrincipalException()
		throws Exception {

		try (MockedStatic<PermissionThreadLocal>
				permissionThreadLocalMockedStatic = Mockito.mockStatic(
					PermissionThreadLocal.class)) {

			Mockito.when(
				_permissionChecker.isOmniadmin()
			).thenReturn(
				false
			);

			permissionThreadLocalMockedStatic.when(
				PermissionThreadLocal::getPermissionChecker
			).thenReturn(
				_permissionChecker
			);

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				_healthVerificationResourceImpl::postHealthVerification);
		}
	}

	private HealthVerificationResourceImpl _healthVerificationResourceImpl;
	private PermissionChecker _permissionChecker;
	private Response.ResponseBuilder _responseBuilder;

}