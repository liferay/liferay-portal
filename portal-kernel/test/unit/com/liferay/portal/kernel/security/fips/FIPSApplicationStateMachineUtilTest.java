/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.internal.log4j.FIPSLog4jUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ObjectMessage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge García Jiménez
 * @author Rafael Praxedes
 */
public class FIPSApplicationStateMachineUtilTest {

	@BeforeClass
	public static void setUpClass() {
		_logger = Mockito.mock(Logger.class);

		_logManagerMockedStatic = Mockito.mockStatic(
			LogManager.class, Mockito.CALLS_REAL_METHODS);

		_logManagerMockedStatic.when(
			() -> LogManager.getLogger(FIPSLog4jUtil.class)
		).thenReturn(
			_logger
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_logManagerMockedStatic.close();
	}

	@Before
	public void setUp() {
		Mockito.reset(_logger);

		_safeCloseable = PropsValuesTestUtil.swapWithSafeCloseable(
			"FIPS_AUDIT_DEPLOYMENT_INSTANCE_ID", RandomTestUtil.randomString());

		Mockito.doAnswer(
			invocation -> {
				ObjectMessage objectMessage = invocation.getArgument(2);

				_fipsAuditLogEntries.add(
					(Map<String, Object>)objectMessage.getParameter());

				return null;
			}
		).when(
			_logger
		).log(
			Mockito.any(Level.class), Mockito.any(Marker.class),
			Mockito.any(Message.class)
		);

		_setFIPSApplicationState(FIPSApplicationState.INITIALIZING);
	}

	@After
	public void tearDown() {
		_safeCloseable.close();
	}

	@Test
	public void testError() {
		_setFIPSApplicationState(FIPSApplicationState.OPERATIONAL);

		String failedStep = RandomTestUtil.randomString();

		FIPSApplicationStateMachineUtil.error(
			failedStep, new SecurityException("The provider is unhappy"));

		Assert.assertEquals(
			FIPSApplicationState.ERROR,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Map<String, Object> fipsAuditLogEntry = _getLastFIPSAuditLogEntry();

		_assertEnvelope(
			fipsAuditLogEntry, "event-type", "fips-state-transition");
		_assertEnvelope(fipsAuditLogEntry, "severity", "CRITICAL");
		_assertField(fipsAuditLogEntry, "failed-step", failedStep);
		_assertField(fipsAuditLogEntry, "from-state", "OPERATIONAL");
		_assertField(
			fipsAuditLogEntry, "provider-error-message",
			"The provider is unhappy");
		_assertField(fipsAuditLogEntry, "to-state", "ERROR");
	}

	@Test
	public void testKeyCSPEntry() {
		_setFIPSApplicationState(FIPSApplicationState.OPERATIONAL);

		String cryptoOfficerUserId = RandomTestUtil.randomString();
		String operationType = RandomTestUtil.randomString();

		FIPSApplicationStateMachineUtil.keyCSPEntry(
			cryptoOfficerUserId, operationType,
			() -> {
			});

		Assert.assertEquals(
			FIPSApplicationState.OPERATIONAL,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertEquals(
			_fipsAuditLogEntries.toString(), 2, _fipsAuditLogEntries.size());

		_assertEnvelope(_fipsAuditLogEntries.get(0), "severity", "INFO");
		_assertField(
			_fipsAuditLogEntries.get(0), "crypto-officer-user-id",
			cryptoOfficerUserId);
		_assertField(
			_fipsAuditLogEntries.get(0), "operation-type", operationType);
		_assertField(_fipsAuditLogEntries.get(0), "to-state", "KEY_CSP_ENTRY");
		_assertField(
			_fipsAuditLogEntries.get(1), "from-state", "KEY_CSP_ENTRY");
		_assertField(
			_fipsAuditLogEntries.get(1), "message",
			"The operation was completed successfully");
		_assertField(_fipsAuditLogEntries.get(1), "to-state", "OPERATIONAL");
	}

	@Test
	public void testKeyCSPEntryWithFailedOperation() {
		_setFIPSApplicationState(FIPSApplicationState.OPERATIONAL);

		Assert.assertThrows(
			SecurityException.class,
			() -> FIPSApplicationStateMachineUtil.keyCSPEntry(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				() -> {
					throw new SecurityException("The key is unusable");
				}));

		Assert.assertEquals(
			FIPSApplicationState.ERROR,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertEquals(
			_fipsAuditLogEntries.toString(), 2, _fipsAuditLogEntries.size());

		_assertEnvelope(_fipsAuditLogEntries.get(1), "severity", "CRITICAL");
		_assertField(
			_fipsAuditLogEntries.get(1), "failed-step", "Key or CSP entry");
		_assertField(
			_fipsAuditLogEntries.get(1), "provider-error-message",
			"The key is unusable");
		_assertField(_fipsAuditLogEntries.get(1), "to-state", "ERROR");
	}

	@Test
	public void testOperational() {
		_setFIPSApplicationState(FIPSApplicationState.QUIESCENT);

		String cryptoOfficerUserId = RandomTestUtil.randomString();
		String reason = RandomTestUtil.randomString();

		FIPSApplicationStateMachineUtil.operational(
			cryptoOfficerUserId, reason);

		Assert.assertEquals(
			FIPSApplicationState.OPERATIONAL,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Map<String, Object> fipsAuditLogEntry = _getLastFIPSAuditLogEntry();

		_assertEnvelope(fipsAuditLogEntry, "severity", "INFO");
		_assertField(
			fipsAuditLogEntry, "crypto-officer-user-id", cryptoOfficerUserId);
		_assertField(fipsAuditLogEntry, "from-state", "QUIESCENT");
		_assertField(fipsAuditLogEntry, "reason", reason);
		_assertField(fipsAuditLogEntry, "to-state", "OPERATIONAL");
	}

	@Test
	public void testPowerOff() {
		_setFIPSApplicationState(FIPSApplicationState.OPERATIONAL);

		String initiatingActor = RandomTestUtil.randomString();

		FIPSApplicationStateMachineUtil.powerOff(initiatingActor);

		Assert.assertEquals(
			FIPSApplicationState.POWER_OFF,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Map<String, Object> fipsAuditLogEntry = _getLastFIPSAuditLogEntry();

		_assertEnvelope(fipsAuditLogEntry, "severity", "INFO");
		_assertField(fipsAuditLogEntry, "from-state", "OPERATIONAL");
		_assertField(fipsAuditLogEntry, "initiating-actor", initiatingActor);
		_assertField(fipsAuditLogEntry, "to-state", "POWER_OFF");
	}

	@Test
	public void testQuiescent() {
		_setFIPSApplicationState(FIPSApplicationState.OPERATIONAL);

		String cryptoOfficerUserId = RandomTestUtil.randomString();
		String reason = RandomTestUtil.randomString();

		FIPSApplicationStateMachineUtil.quiescent(cryptoOfficerUserId, reason);

		Assert.assertEquals(
			FIPSApplicationState.QUIESCENT,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Map<String, Object> fipsAuditLogEntry = _getLastFIPSAuditLogEntry();

		_assertEnvelope(fipsAuditLogEntry, "severity", "INFO");
		_assertField(
			fipsAuditLogEntry, "crypto-officer-user-id", cryptoOfficerUserId);
		_assertField(fipsAuditLogEntry, "from-state", "OPERATIONAL");
		_assertField(fipsAuditLogEntry, "reason", reason);
		_assertField(fipsAuditLogEntry, "to-state", "QUIESCENT");
	}

	@Test
	public void testRegisterShutdownHook() {
		_setFIPSApplicationState(FIPSApplicationState.OPERATIONAL);

		Thread thread = _getShutdownHook();

		thread.run();

		Assert.assertEquals(
			FIPSApplicationState.POWER_OFF,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Map<String, Object> fipsAuditLogEntry = _getLastFIPSAuditLogEntry();

		_assertEnvelope(fipsAuditLogEntry, "severity", "INFO");
		_assertField(fipsAuditLogEntry, "from-state", "OPERATIONAL");
		_assertField(fipsAuditLogEntry, "initiating-actor", "Operating system");
		_assertField(fipsAuditLogEntry, "to-state", "POWER_OFF");
	}

	@Test
	public void testRegisterShutdownHookWithPowerOffState() {
		_setFIPSApplicationState(FIPSApplicationState.POWER_OFF);

		Thread thread = _getShutdownHook();

		thread.run();

		Assert.assertEquals(
			FIPSApplicationState.POWER_OFF,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertTrue(_fipsAuditLogEntries.isEmpty());
	}

	@Test
	public void testSelfTest() {
		FIPSApplicationStateMachineUtil.selfTest(
			() -> {
			});

		Assert.assertEquals(
			FIPSApplicationState.OPERATIONAL,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertEquals(
			_fipsAuditLogEntries.toString(), 2, _fipsAuditLogEntries.size());

		_assertField(_fipsAuditLogEntries.get(0), "from-state", "INITIALIZING");
		_assertField(
			_fipsAuditLogEntries.get(0), "message",
			"The integrity checks were started");
		_assertField(_fipsAuditLogEntries.get(0), "to-state", "SELF_TEST");
		_assertField(
			_fipsAuditLogEntries.get(1), "message",
			"All checks and the validated provider self tests passed");
		_assertField(_fipsAuditLogEntries.get(1), "to-state", "OPERATIONAL");
	}

	@Test
	public void testSelfTestWithFailure() {
		_testSelfTestWithFailure(new RuntimeException());
		_testSelfTestWithFailure(new SecurityException());
	}

	@Test
	public void testSelfTestWithRecovery() {
		_setFIPSApplicationState(FIPSApplicationState.ERROR);

		String cryptoOfficerUserId = RandomTestUtil.randomString();
		String recoveryAction = RandomTestUtil.randomString();

		FIPSApplicationStateMachineUtil.selfTest(
			cryptoOfficerUserId, recoveryAction,
			() -> {
			});

		Assert.assertEquals(
			FIPSApplicationState.OPERATIONAL,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertEquals(
			_fipsAuditLogEntries.toString(), 2, _fipsAuditLogEntries.size());

		_assertField(
			_fipsAuditLogEntries.get(0), "crypto-officer-user-id",
			cryptoOfficerUserId);
		_assertField(_fipsAuditLogEntries.get(0), "from-state", "ERROR");
		_assertField(
			_fipsAuditLogEntries.get(0), "recovery-action", recoveryAction);
		_assertField(_fipsAuditLogEntries.get(0), "to-state", "SELF_TEST");
		_assertField(_fipsAuditLogEntries.get(1), "to-state", "OPERATIONAL");
	}

	@Test
	public void testTransition() {

		// ERROR

		_testTransition(
			FIPSApplicationState.ERROR, FIPSApplicationState.POWER_OFF);
		_testTransition(
			FIPSApplicationState.ERROR, FIPSApplicationState.SELF_TEST);

		// INITIALIZING

		_testTransition(
			FIPSApplicationState.INITIALIZING, FIPSApplicationState.ERROR);
		_testTransition(
			FIPSApplicationState.INITIALIZING, FIPSApplicationState.POWER_OFF);
		_testTransition(
			FIPSApplicationState.INITIALIZING, FIPSApplicationState.SELF_TEST);

		// KEY_CSP_ENTRY

		_testTransition(
			FIPSApplicationState.KEY_CSP_ENTRY, FIPSApplicationState.ERROR);
		_testTransition(
			FIPSApplicationState.KEY_CSP_ENTRY,
			FIPSApplicationState.OPERATIONAL);
		_testTransition(
			FIPSApplicationState.KEY_CSP_ENTRY, FIPSApplicationState.POWER_OFF);

		// OPERATIONAL

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
			FIPSApplicationState.OPERATIONAL, FIPSApplicationState.SELF_TEST);

		// QUIESCENT

		_testTransition(
			FIPSApplicationState.QUIESCENT, FIPSApplicationState.ERROR);
		_testTransition(
			FIPSApplicationState.QUIESCENT, FIPSApplicationState.KEY_CSP_ENTRY);
		_testTransition(
			FIPSApplicationState.QUIESCENT, FIPSApplicationState.OPERATIONAL);
		_testTransition(
			FIPSApplicationState.QUIESCENT, FIPSApplicationState.POWER_OFF);

		// SELF_TEST

		_testTransition(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.ERROR);
		_testTransition(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.OPERATIONAL);
		_testTransition(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.POWER_OFF);
	}

	@Test
	public void testTransitionWithIllegalState() {

		// ERROR

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

		// INITIALIZING

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

		// KEY_CSP_ENTRY

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

		// OPERATIONAL

		_testTransitionWithIllegalState(
			FIPSApplicationState.OPERATIONAL,
			FIPSApplicationState.INITIALIZING);
		_testTransitionWithIllegalState(
			FIPSApplicationState.OPERATIONAL, FIPSApplicationState.OPERATIONAL);

		// POWER_OFF

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

		// QUIESCENT

		_testTransitionWithIllegalState(
			FIPSApplicationState.QUIESCENT, FIPSApplicationState.INITIALIZING);
		_testTransitionWithIllegalState(
			FIPSApplicationState.QUIESCENT, FIPSApplicationState.QUIESCENT);
		_testTransitionWithIllegalState(
			FIPSApplicationState.QUIESCENT, FIPSApplicationState.SELF_TEST);

		// SELF_TEST

		_testTransitionWithIllegalState(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.INITIALIZING);
		_testTransitionWithIllegalState(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.KEY_CSP_ENTRY);
		_testTransitionWithIllegalState(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.QUIESCENT);
		_testTransitionWithIllegalState(
			FIPSApplicationState.SELF_TEST, FIPSApplicationState.SELF_TEST);
	}

	private void _assertEnvelope(
		Map<String, Object> fipsAuditLogEntry, String key, String value) {

		Assert.assertEquals(value, fipsAuditLogEntry.get(key));
	}

	private void _assertField(
		Map<String, Object> fipsAuditLogEntry, String key, String value) {

		Map<?, ?> fields = (Map<?, ?>)fipsAuditLogEntry.get("fields");

		Assert.assertEquals(value, fields.get(key));
	}

	private Map<String, Object> _getLastFIPSAuditLogEntry() {
		return _fipsAuditLogEntries.get(_fipsAuditLogEntries.size() - 1);
	}

	private Thread _getShutdownHook() {
		try (MockedStatic<Runtime> runtimeMockedStatic = Mockito.mockStatic(
				Runtime.class)) {

			Runtime runtime = Mockito.mock(Runtime.class);

			runtimeMockedStatic.when(
				Runtime::getRuntime
			).thenReturn(
				runtime
			);

			ReflectionTestUtil.invoke(
				FIPSApplicationStateMachineUtil.class, "_registerShutdownHook",
				new Class<?>[0]);

			ArgumentCaptor<Thread> argumentCaptor = ArgumentCaptor.forClass(
				Thread.class);

			Mockito.verify(
				runtime
			).addShutdownHook(
				argumentCaptor.capture()
			);

			return argumentCaptor.getValue();
		}
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

	private void _testSelfTestWithFailure(RuntimeException runtimeException) {
		_fipsAuditLogEntries.clear();

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

		Assert.assertEquals(
			_fipsAuditLogEntries.toString(), 2, _fipsAuditLogEntries.size());

		_assertEnvelope(_fipsAuditLogEntries.get(1), "severity", "CRITICAL");
		_assertField(_fipsAuditLogEntries.get(1), "failed-step", "Self test");
		_assertField(_fipsAuditLogEntries.get(1), "from-state", "SELF_TEST");
		_assertField(_fipsAuditLogEntries.get(1), "to-state", "ERROR");
	}

	private void _testTransition(
		FIPSApplicationState fromFIPSApplicationState,
		FIPSApplicationState toFIPSApplicationState) {

		_fipsAuditLogEntries.clear();

		_setFIPSApplicationState(fromFIPSApplicationState);

		_transition(toFIPSApplicationState);

		Assert.assertEquals(
			toFIPSApplicationState,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertEquals(
			_fipsAuditLogEntries.toString(), 1, _fipsAuditLogEntries.size());

		_assertEnvelope(
			_fipsAuditLogEntries.get(0), "event-type", "fips-state-transition");
		_assertField(
			_fipsAuditLogEntries.get(0), "from-state",
			fromFIPSApplicationState.name());
		_assertField(
			_fipsAuditLogEntries.get(0), "to-state",
			toFIPSApplicationState.name());
	}

	private void _testTransitionWithIllegalState(
		FIPSApplicationState fromFIPSApplicationState,
		FIPSApplicationState toFIPSApplicationState) {

		_fipsAuditLogEntries.clear();

		_setFIPSApplicationState(fromFIPSApplicationState);

		Assert.assertThrows(
			IllegalStateException.class,
			() -> _transition(toFIPSApplicationState));

		Assert.assertEquals(
			fromFIPSApplicationState,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertTrue(_fipsAuditLogEntries.isEmpty());
	}

	private void _transition(FIPSApplicationState fipsApplicationState) {
		ReflectionTestUtil.invoke(
			FIPSApplicationStateMachineUtil.class, "_transition",
			new Class<?>[] {FIPSApplicationState.class, Consumer.class},
			fipsApplicationState,
			(Consumer<FIPSAuditEvent>)fipsAuditEvent -> {
			});
	}

	private static Logger _logger;

	private static MockedStatic<LogManager> _logManagerMockedStatic;

	private final List<Map<String, Object>> _fipsAuditLogEntries =
		new ArrayList<>();
	private SafeCloseable _safeCloseable;

}