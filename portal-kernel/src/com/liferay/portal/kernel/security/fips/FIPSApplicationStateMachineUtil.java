/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.string.StringBundler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Jorge García Jiménez
 */
public class FIPSApplicationStateMachineUtil {

	public static FIPSApplicationState getFIPSApplicationState() {
		return _fipsApplicationStateAtomicReference.get();
	}

	public static void selfTest(Runnable runnable) {
		_transition(FIPSApplicationState.SELF_TEST);

		try {
			runnable.run();
		}
		catch (Throwable throwable) {
			_transition(FIPSApplicationState.ERROR);

			throw throwable;
		}

		_transition(FIPSApplicationState.OPERATIONAL);
	}

	private static void _transition(FIPSApplicationState fipsApplicationState) {
		_fipsApplicationStateAtomicReference.updateAndGet(
			currentFIPSApplicationState -> {
				Set<FIPSApplicationState> nextFIPSApplicationStates =
					_allowedTransitions.getOrDefault(
						currentFIPSApplicationState, Set.of());

				if (!nextFIPSApplicationStates.contains(fipsApplicationState)) {
					throw new IllegalStateException(
						StringBundler.concat(
							"Unable to transition the FIPS application state ",
							"from \"", currentFIPSApplicationState, "\" to \"",
							fipsApplicationState, "\""));
				}

				return fipsApplicationState;
			});
	}

	private static final Map<FIPSApplicationState, Set<FIPSApplicationState>>
		_allowedTransitions = Map.of(
			FIPSApplicationState.ERROR, Set.of(FIPSApplicationState.POWER_OFF),
			FIPSApplicationState.INITIALIZING,
			Set.of(
				FIPSApplicationState.ERROR, FIPSApplicationState.POWER_OFF,
				FIPSApplicationState.SELF_TEST),
			FIPSApplicationState.OPERATIONAL,
			Set.of(FIPSApplicationState.ERROR, FIPSApplicationState.POWER_OFF),
			FIPSApplicationState.POWER_OFF, Set.of(),
			FIPSApplicationState.SELF_TEST,
			Set.of(
				FIPSApplicationState.ERROR, FIPSApplicationState.OPERATIONAL,
				FIPSApplicationState.POWER_OFF));
	private static final AtomicReference<FIPSApplicationState>
		_fipsApplicationStateAtomicReference = new AtomicReference<>(
			FIPSApplicationState.INITIALIZING);

}