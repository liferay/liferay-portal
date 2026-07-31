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
		_assertTransition(
			FIPSApplicationState.ERROR, FIPSApplicationState.POWER_OFF);
		_assertTransition(
			FIPSApplicationState.INITIALIZING, FIPSApplicationState.ERROR);
		_assertTransition(
			FIPSApplicationState.INITIALIZING, FIPSApplicationState.POWER_OFF);
		_assertTransition(
			FIPSApplicationState.INITIALIZING, FIPSApplicationState.SELF_TEST);
		_assertTransition(
			FIPSApplicationState.OPERATIONAL, FIPSApplicationState.ERROR);
		_assertTransition(
			FIPSApplicationState.OPERATIONAL, FIPSApplicationState.POWER_OFF);
		_assertTransition(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.ERROR);
		_assertTransition(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.OPERATIONAL);
		_assertTransition(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.POWER_OFF);
	}

	@Test
	public void testTransitionWithIllegalState() {
		_assertIllegalTransition(
			FIPSApplicationState.INITIALIZING,
			FIPSApplicationState.INITIALIZING);
		_assertIllegalTransition(
			FIPSApplicationState.INITIALIZING,
			FIPSApplicationState.OPERATIONAL);
		_assertIllegalTransition(
			FIPSApplicationState.OPERATIONAL,
			FIPSApplicationState.INITIALIZING);
		_assertIllegalTransition(
			FIPSApplicationState.OPERATIONAL, FIPSApplicationState.OPERATIONAL);
		_assertIllegalTransition(
			FIPSApplicationState.OPERATIONAL, FIPSApplicationState.SELF_TEST);
		_assertIllegalTransition(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.INITIALIZING);
		_assertIllegalTransition(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.SELF_TEST);
	}

	@Test
	public void testTransitionWithTerminalState() {
		for (FIPSApplicationState fipsApplicationState :
				FIPSApplicationState.values()) {

			if (fipsApplicationState != FIPSApplicationState.POWER_OFF) {
				_assertIllegalTransition(
					FIPSApplicationState.ERROR, fipsApplicationState);
			}

			_assertIllegalTransition(
				FIPSApplicationState.POWER_OFF, fipsApplicationState);
		}
	}

	private void _assertIllegalTransition(
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

	private void _assertTransition(
		FIPSApplicationState fromFIPSApplicationState,
		FIPSApplicationState toFIPSApplicationState) {

		_setFIPSApplicationState(fromFIPSApplicationState);

		_transition(toFIPSApplicationState);

		Assert.assertEquals(
			toFIPSApplicationState,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());
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

	private void _transition(FIPSApplicationState fipsApplicationState) {
		ReflectionTestUtil.invoke(
			FIPSApplicationStateMachineUtil.class, "_transition",
			new Class<?>[] {FIPSApplicationState.class}, fipsApplicationState);
	}

}