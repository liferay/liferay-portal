/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.internal.resource.v1_0;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.security.fips.FIPSApplicationState;
import com.liferay.portal.kernel.security.fips.FIPSApplicationStateMachineUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.security.fips.rest.dto.v1_0.FIPSHealthVerification;
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
		Response response = Mockito.mock(Response.class);

		Mockito.when(
			response.getStatusInfo()
		).thenReturn(
			Response.Status.SERVICE_UNAVAILABLE
		);

		_responseBuilder = Mockito.mock(
			Response.ResponseBuilder.class, Mockito.RETURNS_SELF);

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
	public void testPostFIPSHealthVerification() throws Exception {
		FIPSHealthVerificationResourceImpl fipsHealthVerificationResourceImpl =
			new FIPSHealthVerificationResourceImpl();

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable("FIPS_ENABLED", true);
			MockedStatic<FIPSApplicationStateMachineUtil>
				fipsApplicationStateMachineUtilMockedStatic =
					Mockito.mockStatic(FIPSApplicationStateMachineUtil.class)) {

			_testPostFIPSHealthVerification(
				FIPSApplicationState.ERROR,
				fipsApplicationStateMachineUtilMockedStatic,
				fipsHealthVerificationResourceImpl, new SecurityException(),
				FIPSHealthVerification.Status.ERROR);
			_testPostFIPSHealthVerification(
				FIPSApplicationState.POWER_OFF,
				fipsApplicationStateMachineUtilMockedStatic,
				fipsHealthVerificationResourceImpl, new IllegalStateException(),
				FIPSHealthVerification.Status.POWER_OFF);
			_testPostFIPSHealthVerification(
				FIPSApplicationState.SELF_TEST,
				fipsApplicationStateMachineUtilMockedStatic,
				fipsHealthVerificationResourceImpl, new IllegalStateException(),
				FIPSHealthVerification.Status.SELF_TEST);

			fipsApplicationStateMachineUtilMockedStatic.when(
				FIPSApplicationStateMachineUtil::getFIPSApplicationState
			).thenReturn(
				FIPSApplicationState.OPERATIONAL
			);

			fipsApplicationStateMachineUtilMockedStatic.when(
				() -> FIPSApplicationStateMachineUtil.selfTest(Mockito.any())
			).thenAnswer(
				invocation -> null
			);

			FIPSHealthVerification fipsHealthVerification =
				fipsHealthVerificationResourceImpl.postFIPSHealthVerification();

			Assert.assertEquals(
				FIPSHealthVerification.Status.OPERATIONAL,
				fipsHealthVerification.getStatus());
		}
	}

	private void _testPostFIPSHealthVerification(
		FIPSApplicationState fipsApplicationState,
		MockedStatic<FIPSApplicationStateMachineUtil>
			fipsApplicationStateMachineUtilMockedStatic,
		FIPSHealthVerificationResourceImpl fipsHealthVerificationResourceImpl,
		RuntimeException runtimeException,
		FIPSHealthVerification.Status status) {

		Mockito.clearInvocations(_responseBuilder);

		fipsApplicationStateMachineUtilMockedStatic.when(
			FIPSApplicationStateMachineUtil::getFIPSApplicationState
		).thenReturn(
			fipsApplicationState
		);

		fipsApplicationStateMachineUtilMockedStatic.when(
			() -> FIPSApplicationStateMachineUtil.selfTest(Mockito.any())
		).thenThrow(
			runtimeException
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

		Assert.assertEquals(status, fipsHealthVerification.getStatus());
	}

	private Response.ResponseBuilder _responseBuilder;

}