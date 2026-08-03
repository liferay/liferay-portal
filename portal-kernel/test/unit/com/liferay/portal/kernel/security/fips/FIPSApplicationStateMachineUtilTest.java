/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.portal.kernel.test.ReflectionTestUtil;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jorge García Jiménez
 */
public class FIPSApplicationStateMachineUtilTest {

	@Before
	public void setUp() {
		_setFIPSApplicationState(FIPSApplicationState.INITIALIZING);
	}

	@Test
	public void testSelfTest() {
		FIPSApplicationStateMachineUtil.selfTest(
			() -> {
			});

		Assert.assertEquals(
			FIPSApplicationState.OPERATIONAL,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		_testSelfTest(new RuntimeException());
		_testSelfTest(new SecurityException());
	}

	@Test
	public void testTransition() {
		_testTransition(
			FIPSApplicationState.ERROR, FIPSApplicationState.POWER_OFF);
		_testTransition(
			FIPSApplicationState.ERROR, FIPSApplicationState.SELF_TEST);
		_testTransition(
			FIPSApplicationState.INITIALIZING, FIPSApplicationState.ERROR);
		_testTransition(
			FIPSApplicationState.INITIALIZING, FIPSApplicationState.POWER_OFF);
		_testTransition(
			FIPSApplicationState.INITIALIZING, FIPSApplicationState.SELF_TEST);
		_testTransition(
			FIPSApplicationState.KEY_CSP_ENTRY, FIPSApplicationState.ERROR);
		_testTransition(
			FIPSApplicationState.KEY_CSP_ENTRY,
			FIPSApplicationState.OPERATIONAL);
		_testTransition(
			FIPSApplicationState.KEY_CSP_ENTRY, FIPSApplicationState.POWER_OFF);
		_testTransition(
			FIPSApplicationState.OPERATIONAL, FIPSApplicationState.ERROR);
		_testTransition(
			FIPSApplicationState.OPERATIONAL,
			FIPSApplicationState.KEY_CSP_ENTRY);
		_testTransition(
			FIPSApplicationState.OPERATIONAL, FIPSApplicationState.POWER_OFF);
		_testTransition(
			FIPSApplicationState.OPERATIONAL, FIPSApplicationState.QUIESCENT);
		_testTransition(
			FIPSApplicationState.QUIESCENT, FIPSApplicationState.ERROR);
		_testTransition(
			FIPSApplicationState.QUIESCENT, FIPSApplicationState.KEY_CSP_ENTRY);
		_testTransition(
			FIPSApplicationState.QUIESCENT, FIPSApplicationState.OPERATIONAL);
		_testTransition(
			FIPSApplicationState.QUIESCENT, FIPSApplicationState.POWER_OFF);
		_testTransition(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.ERROR);
		_testTransition(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.OPERATIONAL);
		_testTransition(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.POWER_OFF);
	}

	@Test
	public void testTransitionWithIllegalState() {
		_testTransitionWithIllegalState(
			FIPSApplicationState.ERROR, FIPSApplicationState.ERROR);
		_testTransitionWithIllegalState(
			FIPSApplicationState.ERROR, FIPSApplicationState.INITIALIZING);
		_testTransitionWithIllegalState(
			FIPSApplicationState.ERROR, FIPSApplicationState.KEY_CSP_ENTRY);
		_testTransitionWithIllegalState(
			FIPSApplicationState.ERROR, FIPSApplicationState.OPERATIONAL);
		_testTransitionWithIllegalState(
			FIPSApplicationState.ERROR, FIPSApplicationState.QUIESCENT);
		_testTransitionWithIllegalState(
			FIPSApplicationState.INITIALIZING,
			FIPSApplicationState.INITIALIZING);
		_testTransitionWithIllegalState(
			FIPSApplicationState.INITIALIZING,
			FIPSApplicationState.KEY_CSP_ENTRY);
		_testTransitionWithIllegalState(
			FIPSApplicationState.INITIALIZING,
			FIPSApplicationState.OPERATIONAL);
		_testTransitionWithIllegalState(
			FIPSApplicationState.INITIALIZING, FIPSApplicationState.QUIESCENT);
		_testTransitionWithIllegalState(
			FIPSApplicationState.KEY_CSP_ENTRY,
			FIPSApplicationState.INITIALIZING);
		_testTransitionWithIllegalState(
			FIPSApplicationState.KEY_CSP_ENTRY,
			FIPSApplicationState.KEY_CSP_ENTRY);
		_testTransitionWithIllegalState(
			FIPSApplicationState.KEY_CSP_ENTRY, FIPSApplicationState.QUIESCENT);
		_testTransitionWithIllegalState(
			FIPSApplicationState.KEY_CSP_ENTRY, FIPSApplicationState.SELF_TEST);
		_testTransitionWithIllegalState(
			FIPSApplicationState.OPERATIONAL,
			FIPSApplicationState.INITIALIZING);
		_testTransitionWithIllegalState(
			FIPSApplicationState.OPERATIONAL, FIPSApplicationState.OPERATIONAL);
		_testTransitionWithIllegalState(
			FIPSApplicationState.OPERATIONAL, FIPSApplicationState.SELF_TEST);
		_testTransitionWithIllegalState(
			FIPSApplicationState.POWER_OFF, FIPSApplicationState.ERROR);
		_testTransitionWithIllegalState(
			FIPSApplicationState.POWER_OFF, FIPSApplicationState.INITIALIZING);
		_testTransitionWithIllegalState(
			FIPSApplicationState.POWER_OFF, FIPSApplicationState.KEY_CSP_ENTRY);
		_testTransitionWithIllegalState(
			FIPSApplicationState.POWER_OFF, FIPSApplicationState.OPERATIONAL);
		_testTransitionWithIllegalState(
			FIPSApplicationState.POWER_OFF, FIPSApplicationState.POWER_OFF);
		_testTransitionWithIllegalState(
			FIPSApplicationState.POWER_OFF, FIPSApplicationState.QUIESCENT);
		_testTransitionWithIllegalState(
			FIPSApplicationState.POWER_OFF, FIPSApplicationState.SELF_TEST);
		_testTransitionWithIllegalState(
			FIPSApplicationState.QUIESCENT, FIPSApplicationState.INITIALIZING);
		_testTransitionWithIllegalState(
			FIPSApplicationState.QUIESCENT, FIPSApplicationState.QUIESCENT);
		_testTransitionWithIllegalState(
			FIPSApplicationState.QUIESCENT, FIPSApplicationState.SELF_TEST);
		_testTransitionWithIllegalState(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.INITIALIZING);
		_testTransitionWithIllegalState(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.KEY_CSP_ENTRY);
		_testTransitionWithIllegalState(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.QUIESCENT);
		_testTransitionWithIllegalState(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.SELF_TEST);
	}

	private void _setFIPSApplicationState(
		FIPSApplicationState fipsApplicationState) {

		AtomicReference<FIPSApplicationState>
			fipsApplicationStateAtomicReference =
				ReflectionTestUtil.getFieldValue(
					FIPSApplicationStateMachineUtil.class,
					"_fipsApplicationStateAtomicReference");

		fipsApplicationStateAtomicReference.set(fipsApplicationState);
	}

	private void _testSelfTest(RuntimeException runtimeException) {
		_setFIPSApplicationState(FIPSApplicationState.INITIALIZING);

		Assert.assertThrows(
			runtimeException.getClass(),
			() -> FIPSApplicationStateMachineUtil.selfTest(
				() -> {
					throw runtimeException;
				}));

		Assert.assertEquals(
			FIPSApplicationState.ERROR,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());
	}

	private void _testTransition(
		FIPSApplicationState fromFIPSApplicationState,
		FIPSApplicationState toFIPSApplicationState) {

		_setFIPSApplicationState(fromFIPSApplicationState);

		_transition(toFIPSApplicationState);

		Assert.assertEquals(
			toFIPSApplicationState,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());
	}

	private void _testTransitionWithIllegalState(
		FIPSApplicationState fromFIPSApplicationState,
		FIPSApplicationState toFIPSApplicationState) {

		_setFIPSApplicationState(fromFIPSApplicationState);

		Assert.assertThrows(
			IllegalStateException.class,
			() -> _transition(toFIPSApplicationState));

		Assert.assertEquals(
			fromFIPSApplicationState,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());
	}

	private void _transition(FIPSApplicationState fipsApplicationState) {
		ReflectionTestUtil.invoke(
			FIPSApplicationStateMachineUtil.class, "_transition",
			new Class<?>[] {FIPSApplicationState.class}, fipsApplicationState);
	}

}