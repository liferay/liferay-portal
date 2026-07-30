/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.string.StringBundler;

import java.util.Map;
import java.util.Set;

/**
 * @author Jorge García Jiménez
 */
public class FIPSApplicationStateMachineUtil {

	public static FIPSApplicationState getFIPSApplicationState() {
		return _fipsApplicationState;
	}

	public static void selfTest(Runnable runnable) {
		transition(FIPSApplicationState.SELF_TEST);

		try {
			runnable.run();
		}
		catch (Throwable throwable) {
			transition(FIPSApplicationState.ERROR);

			throw throwable;
		}

		transition(FIPSApplicationState.OPERATIONAL);
	}

	public static synchronized void transition(
		FIPSApplicationState fipsApplicationState) {

		Set<FIPSApplicationState> nextFIPSApplicationStates =
			_allowedTransitions.get(_fipsApplicationState);

		if (!nextFIPSApplicationStates.contains(fipsApplicationState)) {
			throw new IllegalStateException(
				StringBundler.concat(
					"Unable to transition the FIPS application state from \"",
					_fipsApplicationState, "\" to \"", fipsApplicationState,
					"\""));
		}

		_fipsApplicationState = fipsApplicationState;
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
	private static volatile FIPSApplicationState _fipsApplicationState =
		FIPSApplicationState.INITIALIZING;

}