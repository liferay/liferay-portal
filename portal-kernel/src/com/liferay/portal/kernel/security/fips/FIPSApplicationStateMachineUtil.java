/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author Jorge García Jiménez
 */
public class FIPSApplicationStateMachineUtil {

	public static void error(String failedStep, Throwable throwable) {
		_transition(
			FIPSApplicationState.ERROR,
			fipsAuditEvent -> {
				fipsAuditEvent.put("failed-step", failedStep);
				fipsAuditEvent.put(
					"provider-error-message", _getMessage(throwable));
			});
	}

	public static FIPSApplicationState getFIPSApplicationState() {
		return _fipsApplicationStateAtomicReference.get();
	}

	public static void keyCSPEntry(
		String cryptoOfficerUserId, String operationType, Runnable runnable) {

		_transition(
			FIPSApplicationState.KEY_CSP_ENTRY,
			fipsAuditEvent -> {
				fipsAuditEvent.put(
					"crypto-officer-user-id", cryptoOfficerUserId);
				fipsAuditEvent.put("operation-type", operationType);
			});

		_runAndTransitionToOperational(
			"Key or CSP entry", "The operation was completed successfully",
			runnable);
	}

	public static void operational(String cryptoOfficerUserId, String reason) {
		_transition(
			FIPSApplicationState.OPERATIONAL,
			fipsAuditEvent -> {
				fipsAuditEvent.put(
					"crypto-officer-user-id", cryptoOfficerUserId);
				fipsAuditEvent.put("reason", reason);
			});
	}

	public static void powerOff(String initiatingActor) {
		_transition(
			FIPSApplicationState.POWER_OFF,
			fipsAuditEvent -> fipsAuditEvent.put(
				"initiating-actor", initiatingActor));
	}

	public static void quiescent(String cryptoOfficerUserId, String reason) {
		_transition(
			FIPSApplicationState.QUIESCENT,
			fipsAuditEvent -> {
				fipsAuditEvent.put(
					"crypto-officer-user-id", cryptoOfficerUserId);
				fipsAuditEvent.put("reason", reason);
			});
	}

	public static void selfTest(Runnable runnable) {
		_transition(
			FIPSApplicationState.SELF_TEST,
			fipsAuditEvent -> fipsAuditEvent.put(
				"message", "The integrity checks were started"));

		_runSelfTest(runnable);
	}

	public static void selfTest(
		String cryptoOfficerUserId, String recoveryAction, Runnable runnable) {

		_transition(
			FIPSApplicationState.SELF_TEST,
			fipsAuditEvent -> {
				fipsAuditEvent.put(
					"crypto-officer-user-id", cryptoOfficerUserId);
				fipsAuditEvent.put("recovery-action", recoveryAction);
			});

		_runSelfTest(runnable);
	}

	private static String _getMessage(Throwable throwable) {
		String message = throwable.getMessage();

		if (message == null) {
			return throwable.toString();
		}

		return message;
	}

	private static FIPSAuditEvent.Severity _getSeverity(
		FIPSApplicationState fipsApplicationState) {

		if (fipsApplicationState == FIPSApplicationState.ERROR) {
			return FIPSAuditEvent.Severity.CRITICAL;
		}

		return FIPSAuditEvent.Severity.INFO;
	}

	private static void _registerShutdownHook() {
		Runtime runtime = Runtime.getRuntime();

		runtime.addShutdownHook(
			new Thread(
				() -> {
					if (getFIPSApplicationState() ==
							FIPSApplicationState.POWER_OFF) {

						return;
					}

					try {
						powerOff("Operating system");
					}
					catch (Throwable throwable) {
						if (_log.isDebugEnabled()) {
							_log.debug(throwable);
						}
					}
				}));
	}

	private static void _runAndTransitionToOperational(
		String failedStep, String message, Runnable runnable) {

		try {
			runnable.run();
		}
		catch (Throwable throwable) {
			error(failedStep, throwable);

			throw throwable;
		}

		_transition(
			FIPSApplicationState.OPERATIONAL,
			fipsAuditEvent -> fipsAuditEvent.put("message", message));
	}

	private static void _runSelfTest(Runnable runnable) {
		_runAndTransitionToOperational(
			"Self test",
			"All checks and the validated provider self tests passed",
			runnable);
	}

	private static void _transition(
		FIPSApplicationState fipsApplicationState,
		Consumer<FIPSAuditEvent> fipsAuditEventConsumer) {

		FIPSApplicationState previousFIPSApplicationState =
			_fipsApplicationStateAtomicReference.getAndUpdate(
				currentFIPSApplicationState -> {
					Set<FIPSApplicationState> nextFIPSApplicationStates =
						_allowedTransitions.getOrDefault(
							currentFIPSApplicationState, Set.of());

					if (!nextFIPSApplicationStates.contains(
							fipsApplicationState)) {

						throw new IllegalStateException(
							StringBundler.concat(
								"Unable to transition the FIPS application ",
								"state from \"", currentFIPSApplicationState,
								"\" to \"", fipsApplicationState, "\""));
					}

					return fipsApplicationState;
				});

		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			"fips-state-transition", _getSeverity(fipsApplicationState));

		fipsAuditEvent.put("from-state", previousFIPSApplicationState.name());
		fipsAuditEvent.put("to-state", fipsApplicationState.name());

		fipsAuditEventConsumer.accept(fipsAuditEvent);

		FIPSAuditUtil.write(fipsAuditEvent);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FIPSApplicationStateMachineUtil.class);

	private static final Map<FIPSApplicationState, Set<FIPSApplicationState>>
		_allowedTransitions = Map.of(
			FIPSApplicationState.ERROR,
			Set.of(
				FIPSApplicationState.POWER_OFF, FIPSApplicationState.SELF_TEST),
			FIPSApplicationState.INITIALIZING,
			Set.of(
				FIPSApplicationState.ERROR, FIPSApplicationState.POWER_OFF,
				FIPSApplicationState.SELF_TEST),
			FIPSApplicationState.KEY_CSP_ENTRY,
			Set.of(
				FIPSApplicationState.ERROR, FIPSApplicationState.OPERATIONAL,
				FIPSApplicationState.POWER_OFF),
			FIPSApplicationState.OPERATIONAL,
			Set.of(
				FIPSApplicationState.ERROR, FIPSApplicationState.KEY_CSP_ENTRY,
				FIPSApplicationState.POWER_OFF, FIPSApplicationState.QUIESCENT,
				FIPSApplicationState.SELF_TEST),
			FIPSApplicationState.POWER_OFF, Set.of(),
			FIPSApplicationState.QUIESCENT,
			Set.of(
				FIPSApplicationState.ERROR, FIPSApplicationState.KEY_CSP_ENTRY,
				FIPSApplicationState.OPERATIONAL,
				FIPSApplicationState.POWER_OFF),
			FIPSApplicationState.SELF_TEST,
			Set.of(
				FIPSApplicationState.ERROR, FIPSApplicationState.OPERATIONAL,
				FIPSApplicationState.POWER_OFF));
	private static final AtomicReference<FIPSApplicationState>
		_fipsApplicationStateAtomicReference = new AtomicReference<>(
			FIPSApplicationState.INITIALIZING);

	static {
		if (PropsValues.FIPS_ENABLED) {
			_registerShutdownHook();
		}
	}

}