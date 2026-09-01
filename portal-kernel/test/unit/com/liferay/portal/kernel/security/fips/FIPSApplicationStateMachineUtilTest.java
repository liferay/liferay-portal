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
		_testError(FIPSApplicationState.INITIALIZING);
		_testError(FIPSApplicationState.KEY_CSP_ENTRY);
		_testError(FIPSApplicationState.OPERATIONAL);
		_testError(FIPSApplicationState.QUIESCENT);
		_testError(FIPSApplicationState.SELF_TEST);
	}

	@Test
	public void testErrorWithIllegalState() {
		_testErrorWithIllegalState(FIPSApplicationState.ERROR);
		_testErrorWithIllegalState(FIPSApplicationState.POWER_OFF);
	}

	@Test
	public void testKeyCSPEntry() {
		_testKeyCSPEntry(FIPSApplicationState.OPERATIONAL);
		_testKeyCSPEntry(FIPSApplicationState.QUIESCENT);
	}

	@Test
	public void testKeyCSPEntryWithFailedOperation() {
		_setFIPSApplicationState(FIPSApplicationState.OPERATIONAL);

		String providerErrorMessage = RandomTestUtil.randomString();

		Assert.assertThrows(
			SecurityException.class,
			() -> FIPSApplicationStateMachineUtil.keyCSPEntry(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				() -> {
					throw new SecurityException(providerErrorMessage);
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
			providerErrorMessage);
		_assertField(_fipsAuditLogEntries.get(1), "to-state", "ERROR");
	}

	@Test
	public void testKeyCSPEntryWithIllegalState() {
		_testKeyCSPEntryWithIllegalState(FIPSApplicationState.ERROR);
		_testKeyCSPEntryWithIllegalState(FIPSApplicationState.INITIALIZING);
		_testKeyCSPEntryWithIllegalState(FIPSApplicationState.KEY_CSP_ENTRY);
		_testKeyCSPEntryWithIllegalState(FIPSApplicationState.POWER_OFF);
		_testKeyCSPEntryWithIllegalState(FIPSApplicationState.SELF_TEST);
	}

	@Test
	public void testOperational() {
		_testOperational(FIPSApplicationState.KEY_CSP_ENTRY);
		_testOperational(FIPSApplicationState.QUIESCENT);
		_testOperational(FIPSApplicationState.SELF_TEST);
	}

	@Test
	public void testOperationalWithIllegalState() {
		_testOperationalWithIllegalState(FIPSApplicationState.ERROR);
		_testOperationalWithIllegalState(FIPSApplicationState.INITIALIZING);
		_testOperationalWithIllegalState(FIPSApplicationState.OPERATIONAL);
		_testOperationalWithIllegalState(FIPSApplicationState.POWER_OFF);
	}

	@Test
	public void testPowerOff() {
		_testPowerOff(FIPSApplicationState.ERROR);
		_testPowerOff(FIPSApplicationState.INITIALIZING);
		_testPowerOff(FIPSApplicationState.KEY_CSP_ENTRY);
		_testPowerOff(FIPSApplicationState.OPERATIONAL);
		_testPowerOff(FIPSApplicationState.QUIESCENT);
		_testPowerOff(FIPSApplicationState.SELF_TEST);
	}

	@Test
	public void testPowerOffWithPowerOffState() {
		_setFIPSApplicationState(FIPSApplicationState.POWER_OFF);

		FIPSApplicationStateMachineUtil.powerOff(RandomTestUtil.randomString());

		Assert.assertEquals(
			FIPSApplicationState.POWER_OFF,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertTrue(_fipsAuditLogEntries.isEmpty());
	}

	@Test
	public void testPowerOffWithShutdownHook() {
		_setFIPSApplicationState(FIPSApplicationState.OPERATIONAL);

		Thread thread = _getShutdownHookThread();

		thread.run();

		Assert.assertEquals(
			FIPSApplicationState.POWER_OFF,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertEquals(
			_fipsAuditLogEntries.toString(), 1, _fipsAuditLogEntries.size());

		_assertEnvelope(_fipsAuditLogEntries.get(0), "severity", "INFO");
		_assertField(_fipsAuditLogEntries.get(0), "from-state", "OPERATIONAL");
		_assertField(
			_fipsAuditLogEntries.get(0), "initiating-actor",
			"Operating system");
		_assertField(_fipsAuditLogEntries.get(0), "to-state", "POWER_OFF");
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

		Assert.assertEquals(
			_fipsAuditLogEntries.toString(), 1, _fipsAuditLogEntries.size());

		_assertEnvelope(_fipsAuditLogEntries.get(0), "severity", "INFO");
		_assertField(
			_fipsAuditLogEntries.get(0), "crypto-officer-user-id",
			cryptoOfficerUserId);
		_assertField(_fipsAuditLogEntries.get(0), "from-state", "OPERATIONAL");
		_assertField(_fipsAuditLogEntries.get(0), "reason", reason);
		_assertField(_fipsAuditLogEntries.get(0), "to-state", "QUIESCENT");
	}

	@Test
	public void testQuiescentWithIllegalState() {
		_testQuiescentWithIllegalState(FIPSApplicationState.ERROR);
		_testQuiescentWithIllegalState(FIPSApplicationState.INITIALIZING);
		_testQuiescentWithIllegalState(FIPSApplicationState.KEY_CSP_ENTRY);
		_testQuiescentWithIllegalState(FIPSApplicationState.POWER_OFF);
		_testQuiescentWithIllegalState(FIPSApplicationState.QUIESCENT);
		_testQuiescentWithIllegalState(FIPSApplicationState.SELF_TEST);
	}

	@Test
	public void testSelfTest() {
		_testSelfTest(FIPSApplicationState.ERROR);
		_testSelfTest(FIPSApplicationState.INITIALIZING);
		_testSelfTest(FIPSApplicationState.OPERATIONAL);
	}

	@Test
	public void testSelfTestWithFailure() {
		_testSelfTestWithFailure(new RuntimeException());
		_testSelfTestWithFailure(new SecurityException());
	}

	@Test
	public void testSelfTestWithIllegalState() {
		_testSelfTestWithIllegalState(FIPSApplicationState.KEY_CSP_ENTRY);
		_testSelfTestWithIllegalState(FIPSApplicationState.POWER_OFF);
		_testSelfTestWithIllegalState(FIPSApplicationState.QUIESCENT);
		_testSelfTestWithIllegalState(FIPSApplicationState.SELF_TEST);
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

	private void _assertEnvelope(
		Map<String, Object> fipsAuditLogEntry, String key, String value) {

		Assert.assertEquals(value, fipsAuditLogEntry.get(key));
	}

	private void _assertField(
		Map<String, Object> fipsAuditLogEntry, String key, String value) {

		Map<?, ?> fields = (Map<?, ?>)fipsAuditLogEntry.get("fields");

		Assert.assertEquals(value, fields.get(key));
	}

	private void _assertIllegalStateException(
		FIPSApplicationState fipsApplicationState, Runnable runnable) {

		_fipsAuditLogEntries.clear();

		_setFIPSApplicationState(fipsApplicationState);

		Assert.assertThrows(IllegalStateException.class, runnable::run);

		Assert.assertEquals(
			fipsApplicationState,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertTrue(_fipsAuditLogEntries.isEmpty());
	}

	private Thread _getShutdownHookThread() {
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

	private void _testError(FIPSApplicationState fipsApplicationState) {
		_fipsAuditLogEntries.clear();

		_setFIPSApplicationState(fipsApplicationState);

		String failedStep = RandomTestUtil.randomString();
		String providerErrorMessage = RandomTestUtil.randomString();

		FIPSApplicationStateMachineUtil.error(
			failedStep, new SecurityException(providerErrorMessage));

		Assert.assertEquals(
			FIPSApplicationState.ERROR,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertEquals(
			_fipsAuditLogEntries.toString(), 1, _fipsAuditLogEntries.size());

		_assertEnvelope(
			_fipsAuditLogEntries.get(0), "event-type", "fips-state-transition");
		_assertEnvelope(_fipsAuditLogEntries.get(0), "severity", "CRITICAL");
		_assertField(_fipsAuditLogEntries.get(0), "failed-step", failedStep);
		_assertField(
			_fipsAuditLogEntries.get(0), "from-state",
			fipsApplicationState.name());
		_assertField(
			_fipsAuditLogEntries.get(0), "provider-error-message",
			providerErrorMessage);
		_assertField(_fipsAuditLogEntries.get(0), "to-state", "ERROR");
	}

	private void _testErrorWithIllegalState(
		FIPSApplicationState fipsApplicationState) {

		_assertIllegalStateException(
			fipsApplicationState,
			() -> FIPSApplicationStateMachineUtil.error(
				RandomTestUtil.randomString(),
				new SecurityException(RandomTestUtil.randomString())));
	}

	private void _testKeyCSPEntry(FIPSApplicationState fipsApplicationState) {
		_fipsAuditLogEntries.clear();

		_setFIPSApplicationState(fipsApplicationState);

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
			_fipsAuditLogEntries.get(0), "from-state",
			fipsApplicationState.name());
		_assertField(
			_fipsAuditLogEntries.get(0), "operation-type", operationType);
		_assertField(_fipsAuditLogEntries.get(0), "to-state", "KEY_CSP_ENTRY");
		_assertField(
			_fipsAuditLogEntries.get(1), "from-state", "KEY_CSP_ENTRY");
		_assertField(
			_fipsAuditLogEntries.get(1), "message",
			"The operation completed successfully");
		_assertField(_fipsAuditLogEntries.get(1), "to-state", "OPERATIONAL");
	}

	private void _testKeyCSPEntryWithIllegalState(
		FIPSApplicationState fipsApplicationState) {

		_assertIllegalStateException(
			fipsApplicationState,
			() -> FIPSApplicationStateMachineUtil.keyCSPEntry(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				() -> {
				}));
	}

	private void _testOperational(FIPSApplicationState fipsApplicationState) {
		_fipsAuditLogEntries.clear();

		_setFIPSApplicationState(fipsApplicationState);

		String cryptoOfficerUserId = RandomTestUtil.randomString();
		String reason = RandomTestUtil.randomString();

		FIPSApplicationStateMachineUtil.operational(
			cryptoOfficerUserId, reason);

		Assert.assertEquals(
			FIPSApplicationState.OPERATIONAL,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertEquals(
			_fipsAuditLogEntries.toString(), 1, _fipsAuditLogEntries.size());

		_assertEnvelope(_fipsAuditLogEntries.get(0), "severity", "INFO");
		_assertField(
			_fipsAuditLogEntries.get(0), "crypto-officer-user-id",
			cryptoOfficerUserId);
		_assertField(
			_fipsAuditLogEntries.get(0), "from-state",
			fipsApplicationState.name());
		_assertField(_fipsAuditLogEntries.get(0), "reason", reason);
		_assertField(_fipsAuditLogEntries.get(0), "to-state", "OPERATIONAL");
	}

	private void _testOperationalWithIllegalState(
		FIPSApplicationState fipsApplicationState) {

		_assertIllegalStateException(
			fipsApplicationState,
			() -> FIPSApplicationStateMachineUtil.operational(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));
	}

	private void _testPowerOff(FIPSApplicationState fipsApplicationState) {
		_fipsAuditLogEntries.clear();

		_setFIPSApplicationState(fipsApplicationState);

		String initiatingActor = RandomTestUtil.randomString();

		FIPSApplicationStateMachineUtil.powerOff(initiatingActor);

		Assert.assertEquals(
			FIPSApplicationState.POWER_OFF,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertEquals(
			_fipsAuditLogEntries.toString(), 1, _fipsAuditLogEntries.size());

		_assertEnvelope(_fipsAuditLogEntries.get(0), "severity", "INFO");
		_assertField(
			_fipsAuditLogEntries.get(0), "from-state",
			fipsApplicationState.name());
		_assertField(
			_fipsAuditLogEntries.get(0), "initiating-actor", initiatingActor);
		_assertField(_fipsAuditLogEntries.get(0), "to-state", "POWER_OFF");
	}

	private void _testQuiescentWithIllegalState(
		FIPSApplicationState fipsApplicationState) {

		_assertIllegalStateException(
			fipsApplicationState,
			() -> FIPSApplicationStateMachineUtil.quiescent(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));
	}

	private void _testSelfTest(FIPSApplicationState fipsApplicationState) {
		_fipsAuditLogEntries.clear();

		_setFIPSApplicationState(fipsApplicationState);

		FIPSApplicationStateMachineUtil.selfTest(
			() -> {
			});

		Assert.assertEquals(
			FIPSApplicationState.OPERATIONAL,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertEquals(
			_fipsAuditLogEntries.toString(), 2, _fipsAuditLogEntries.size());

		_assertField(
			_fipsAuditLogEntries.get(0), "from-state",
			fipsApplicationState.name());
		_assertField(
			_fipsAuditLogEntries.get(0), "message",
			"The integrity checks started");
		_assertField(_fipsAuditLogEntries.get(0), "to-state", "SELF_TEST");
		_assertField(
			_fipsAuditLogEntries.get(1), "message",
			"All checks and the validated provider self tests passed");
		_assertField(_fipsAuditLogEntries.get(1), "to-state", "OPERATIONAL");
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

	private void _testSelfTestWithIllegalState(
		FIPSApplicationState fipsApplicationState) {

		_assertIllegalStateException(
			fipsApplicationState,
			() -> FIPSApplicationStateMachineUtil.selfTest(
				() -> {
				}));
	}

	private static final Logger _logger = Mockito.mock(Logger.class);

	private static final MockedStatic<LogManager> _logManagerMockedStatic =
		Mockito.mockStatic(LogManager.class, Mockito.CALLS_REAL_METHODS);

	private final List<Map<String, Object>> _fipsAuditLogEntries =
		new ArrayList<>();
	private SafeCloseable _safeCloseable;

}