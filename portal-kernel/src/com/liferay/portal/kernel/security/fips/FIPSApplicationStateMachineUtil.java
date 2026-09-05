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
			"The operation completed successfully", runnable,
			throwable -> error("Key or CSP entry", throwable));
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
		if (getFIPSApplicationState() == FIPSApplicationState.POWER_OFF) {
			return;
		}

		_transition(
			FIPSApplicationState.POWER_OFF,
			fipsAuditEvent -> fipsAuditEvent.put(
				"initiating-actor", initiatingActor));
	}

	public static void preOperationalSelfTest(Runnable runnable) {
		_transition(
			FIPSApplicationState.SELF_TEST,
			fipsAuditEvent -> fipsAuditEvent.put(
				"message", "The pre-operational self tests started"));

		_runAndTransitionToOperational(
			"All checks and the validated provider self tests passed", runnable,
			throwable -> _error(
				"Self test", "pre-operational-health-failure", throwable));
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
				"message", "The integrity checks started"));

		_runAndTransitionToOperational(
			"All checks and the validated provider self tests passed", runnable,
			throwable -> _error(
				"Self test", "periodic-health-failure", throwable));
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

		_runAndTransitionToOperational(
			"All checks and the validated provider self tests passed", runnable,
			throwable -> _error(
				"Self test", "periodic-health-failure", throwable));
	}

	private static void _error(
		String failedStep, String failureEventType, Throwable throwable) {

		try {
			FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
				failureEventType, FIPSAuditEvent.Severity.CRITICAL);

			fipsAuditEvent.put("failed-step", failedStep);

			FIPSApplicationState fipsApplicationState =
				getFIPSApplicationState();

			fipsAuditEvent.put("fips-state", fipsApplicationState.name());

			fipsAuditEvent.put(
				"provider-error-message", _getMessage(throwable));

			FIPSAuditUtil.write(fipsAuditEvent);
		}
		catch (Exception exception) {
			throwable.addSuppressed(exception);
		}

		error(failedStep, throwable);
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
		String message, Runnable runnable,
		Consumer<Throwable> throwableConsumer) {

		try {
			runnable.run();
		}
		catch (Throwable throwable) {
			try {
				throwableConsumer.accept(throwable);
			}
			catch (Exception exception) {
				throwable.addSuppressed(exception);
			}

			throw throwable;
		}

		_transition(
			FIPSApplicationState.OPERATIONAL,
			fipsAuditEvent -> fipsAuditEvent.put("message", message));
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