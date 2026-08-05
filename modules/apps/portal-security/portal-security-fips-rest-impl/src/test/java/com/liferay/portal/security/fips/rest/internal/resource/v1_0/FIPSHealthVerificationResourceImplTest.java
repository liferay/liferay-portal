/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.security.fips.FIPSApplicationStateMachineUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
<<<<<<< HEAD:modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/HealthVerificationResourceImplTest.java
import com.liferay.portal.security.fips.rest.dto.v1_0.HealthVerification;
<<<<<<< HEAD:modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/FIPSHealthVerificationResourceImplTest.java
=======
import com.liferay.portal.security.fips.rest.dto.v1_0.FIPSHealthVerification;
import com.liferay.portal.security.fips.rest.internal.audit.FIPSHealthCheckAuditor;
>>>>>>> 1894fb9 (renaming unit test):modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/FIPSHealthVerificationResourceImplTest.java
=======
import com.liferay.portal.security.fips.rest.internal.audit.FIPSHealthCheckAuditor;
>>>>>>> 435af00 (LPD-93272 Test the periodic-health-failure audit event):modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/HealthVerificationResourceImplTest.java
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

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lucas Miranda
 */
public class FIPSHealthVerificationResourceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_fipsEnabled = PropsValues.FIPS_ENABLED;

		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "FIPS_ENABLED", true);

		_permissionChecker = Mockito.mock(PermissionChecker.class);

		Mockito.when(
			_permissionChecker.isOmniadmin()
		).thenReturn(
			true
		);

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

		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "FIPS_ENABLED", _fipsEnabled);
	}

	@Test
	public void testPostFIPSHealthVerification() throws Exception {
		FIPSHealthVerificationResourceImpl fipsHealthVerificationResourceImpl =
			new FIPSHealthVerificationResourceImpl();

<<<<<<< HEAD:modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/FIPSHealthVerificationResourceImplTest.java
<<<<<<< HEAD:modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/HealthVerificationResourceImplTest.java
=======
=======
>>>>>>> 435af00 (LPD-93272 Test the periodic-health-failure audit event):modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/HealthVerificationResourceImplTest.java
		FIPSHealthCheckAuditor fipsHealthCheckAuditor = Mockito.mock(
			FIPSHealthCheckAuditor.class);

		ReflectionTestUtil.setFieldValue(
<<<<<<< HEAD:modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/FIPSHealthVerificationResourceImplTest.java
			fipsHealthVerificationResourceImpl, "_fipsHealthCheckAuditor",
			fipsHealthCheckAuditor);

>>>>>>> 1894fb9 (renaming unit test):modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/FIPSHealthVerificationResourceImplTest.java
=======
			healthVerificationResourceImpl, "_fipsHealthCheckAuditor",
			fipsHealthCheckAuditor);

>>>>>>> 435af00 (LPD-93272 Test the periodic-health-failure audit event):modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/HealthVerificationResourceImplTest.java
		try (MockedStatic<PermissionThreadLocal>
				permissionThreadLocalMockedStatic = Mockito.mockStatic(
					PermissionThreadLocal.class);
			MockedStatic<FIPSApplicationStateMachineUtil>
				fipsApplicationStateMachineUtilMockedStatic =
					Mockito.mockStatic(FIPSApplicationStateMachineUtil.class)) {

			permissionThreadLocalMockedStatic.when(
				PermissionThreadLocal::getPermissionChecker
			).thenReturn(
				_permissionChecker
			);

			FIPSHealthVerification fipsHealthVerification =
				fipsHealthVerificationResourceImpl.postFIPSHealthVerification();

			Assert.assertEquals(
				FIPSHealthVerification.Status.OPERATIONAL,
				fipsHealthVerification.getStatus());
		}

		try (MockedStatic<PermissionThreadLocal>
				permissionThreadLocalMockedStatic = Mockito.mockStatic(
					PermissionThreadLocal.class);
			MockedStatic<FIPSApplicationStateMachineUtil>
				fipsApplicationStateMachineUtilMockedStatic =
					Mockito.mockStatic(FIPSApplicationStateMachineUtil.class)) {

			permissionThreadLocalMockedStatic.when(
				PermissionThreadLocal::getPermissionChecker
			).thenReturn(
				_permissionChecker
			);

			fipsApplicationStateMachineUtilMockedStatic.when(
				() -> FIPSApplicationStateMachineUtil.selfTest(Mockito.any())
			).thenThrow(
				new SecurityException("boom")
			);

			Assert.assertThrows(
				WebApplicationException.class,
				fipsHealthVerificationResourceImpl::postFIPSHealthVerification);

			Mockito.verify(
				_responseBuilder
			).status(
				(Response.StatusType)Response.Status.SERVICE_UNAVAILABLE
			);

			ArgumentCaptor<FIPSHealthVerification> argumentCaptor =
				ArgumentCaptor.forClass(FIPSHealthVerification.class);

			Mockito.verify(
				_responseBuilder
			).entity(
				argumentCaptor.capture()
			);

			FIPSHealthVerification fipsHealthVerification =
				argumentCaptor.getValue();

			Assert.assertEquals(
<<<<<<< HEAD:modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/HealthVerificationResourceImplTest.java
				HealthVerification.Status.FAILED,
				healthVerification.getStatus());
<<<<<<< HEAD:modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/FIPSHealthVerificationResourceImplTest.java
=======
				FIPSHealthVerification.Status.ERROR,
				fipsHealthVerification.getStatus());
=======
>>>>>>> 435af00 (LPD-93272 Test the periodic-health-failure audit event):modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/HealthVerificationResourceImplTest.java

			Mockito.verify(
				fipsHealthCheckAuditor
			).audit(
				Mockito.any(Exception.class)
			);
		}

		Mockito.reset(fipsHealthCheckAuditor);

		try (MockedStatic<PermissionThreadLocal>
				permissionThreadLocalMockedStatic = Mockito.mockStatic(
					PermissionThreadLocal.class);
			MockedStatic<FIPSApplicationStateMachineUtil>
				fipsApplicationStateMachineUtilMockedStatic =
					Mockito.mockStatic(FIPSApplicationStateMachineUtil.class)) {

			permissionThreadLocalMockedStatic.when(
				PermissionThreadLocal::getPermissionChecker
			).thenReturn(
				_permissionChecker
			);

			fipsApplicationStateMachineUtilMockedStatic.when(
				() -> FIPSApplicationStateMachineUtil.selfTest(Mockito.any())
			).thenThrow(
				new IllegalStateException(
					"Unable to transition the FIPS application state")
			);

			Assert.assertThrows(
				WebApplicationException.class,
<<<<<<< HEAD:modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/FIPSHealthVerificationResourceImplTest.java
				fipsHealthVerificationResourceImpl::postFIPSHealthVerification);

			Mockito.verifyNoInteractions(fipsHealthCheckAuditor);
>>>>>>> 1894fb9 (renaming unit test):modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/FIPSHealthVerificationResourceImplTest.java
=======
				healthVerificationResourceImpl::postHealthVerification);

			Mockito.verifyNoInteractions(fipsHealthCheckAuditor);
>>>>>>> 435af00 (LPD-93272 Test the periodic-health-failure audit event):modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/HealthVerificationResourceImplTest.java
		}
	}

	private boolean _fipsEnabled;
	private PermissionChecker _permissionChecker;
	private Response.ResponseBuilder _responseBuilder;

}