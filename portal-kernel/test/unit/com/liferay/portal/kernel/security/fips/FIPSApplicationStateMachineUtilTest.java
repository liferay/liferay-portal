/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jorge García Jiménez
 */
public class FIPSApplicationStateMachineUtilTest {

	@Before
	public void setUp() throws Exception {
		_liferayHomePath = Files.createTempDirectory("fips-audit-test");

		_fipsAuditLogPath = _liferayHomePath.resolve("logs/fips-audit.ndjson");

		_safeCloseable1 = PropsValuesTestUtil.swapWithSafeCloseable(
			"LIFERAY_HOME", String.valueOf(_liferayHomePath));

		_safeCloseable2 = PropsValuesTestUtil.swapWithSafeCloseable(
			"FIPS_AUDIT_DEPLOYMENT_INSTANCE_ID", RandomTestUtil.randomString());

		_setFIPSApplicationState(FIPSApplicationState.INITIALIZING);
	}

	@After
	public void tearDown() throws Exception {
		_safeCloseable2.close();
		_safeCloseable1.close();

		_delete(_liferayHomePath);
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

		String ndjson = _read();

		_assertField(ndjson, "event-type", "fips-state-transition");
		_assertField(ndjson, "failed-step", failedStep);
		_assertField(ndjson, "from-state", "Operational");
		_assertField(
			ndjson, "provider-error-message", "The provider is unhappy");
		_assertField(ndjson, "severity", "critical");
		_assertField(ndjson, "to-state", "Error");
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

		String[] ndjsons = _readLines();

		Assert.assertEquals(Arrays.toString(ndjsons), 2, ndjsons.length);

		_assertField(ndjsons[0], "crypto-officer-user-id", cryptoOfficerUserId);
		_assertField(ndjsons[0], "operation-type", operationType);
		_assertField(ndjsons[0], "severity", "info");
		_assertField(ndjsons[0], "to-state", "Key/CSP Entry");

		_assertField(ndjsons[1], "from-state", "Key/CSP Entry");
		_assertField(
			ndjsons[1], "message", "The operation was completed successfully");
		_assertField(ndjsons[1], "to-state", "Operational");
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

		String[] ndjsons = _readLines();

		Assert.assertEquals(Arrays.toString(ndjsons), 2, ndjsons.length);

		_assertField(ndjsons[1], "failed-step", "Key or CSP entry");
		_assertField(
			ndjsons[1], "provider-error-message", "The key is unusable");
		_assertField(ndjsons[1], "severity", "critical");
		_assertField(ndjsons[1], "to-state", "Error");
	}

	@Test
	public void testPowerOff() {
		_setFIPSApplicationState(FIPSApplicationState.OPERATIONAL);

		String initiatingActor = RandomTestUtil.randomString();

		FIPSApplicationStateMachineUtil.powerOff(initiatingActor);

		Assert.assertEquals(
			FIPSApplicationState.POWER_OFF,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		String ndjson = _read();

		_assertField(ndjson, "from-state", "Operational");
		_assertField(ndjson, "initiating-actor", initiatingActor);
		_assertField(ndjson, "severity", "info");
		_assertField(ndjson, "to-state", "Power-off");
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

		FIPSApplicationStateMachineUtil.operational(
			cryptoOfficerUserId, reason);

		Assert.assertEquals(
			FIPSApplicationState.OPERATIONAL,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		String[] ndjsons = _readLines();

		Assert.assertEquals(Arrays.toString(ndjsons), 2, ndjsons.length);

		_assertField(ndjsons[0], "crypto-officer-user-id", cryptoOfficerUserId);
		_assertField(ndjsons[0], "reason", reason);
		_assertField(ndjsons[0], "to-state", "Quiescent");

		_assertField(ndjsons[1], "from-state", "Quiescent");
		_assertField(ndjsons[1], "reason", reason);
		_assertField(ndjsons[1], "to-state", "Operational");
	}

	@Test
	public void testSelfTest() {
		FIPSApplicationStateMachineUtil.selfTest(
			() -> {
			});

		Assert.assertEquals(
			FIPSApplicationState.OPERATIONAL,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		String[] ndjsons = _readLines();

		Assert.assertEquals(Arrays.toString(ndjsons), 2, ndjsons.length);

		_assertField(ndjsons[0], "from-state", "Initializing");
		_assertField(
			ndjsons[0], "message", "The integrity checks were started");
		_assertField(ndjsons[0], "to-state", "Self-Test");

		_assertField(
			ndjsons[1], "message",
			"All checks and the validated provider self tests passed");
		_assertField(ndjsons[1], "to-state", "Operational");

		_testSelfTest(new RuntimeException());
		_testSelfTest(new SecurityException());
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

		String[] ndjsons = _readLines();

		Assert.assertEquals(Arrays.toString(ndjsons), 2, ndjsons.length);

		_assertField(ndjsons[0], "crypto-officer-user-id", cryptoOfficerUserId);
		_assertField(ndjsons[0], "from-state", "Error");
		_assertField(ndjsons[0], "recovery-action", recoveryAction);
		_assertField(ndjsons[0], "to-state", "Self-Test");

		_assertField(ndjsons[1], "to-state", "Operational");
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

	private void _assertField(String ndjson, String key, String value) {
		Assert.assertTrue(
			ndjson,
			ndjson.contains(
				StringBundler.concat("\"", key, "\":\"", value, "\"")));
	}

	private void _delete(Path path) throws Exception {
		File file = path.toFile();

		File[] childFiles = file.listFiles();

		if (childFiles != null) {
			for (File childFile : childFiles) {
				_delete(childFile.toPath());
			}
		}

		Files.delete(path);
	}

	private void _deleteFIPSAuditLog() {
		File file = _fipsAuditLogPath.toFile();

		file.delete();
	}

	private String _read() {
		try {
			if (!Files.exists(_fipsAuditLogPath)) {
				return "";
			}

			return new String(
				Files.readAllBytes(_fipsAuditLogPath), StandardCharsets.UTF_8);
		}
		catch (IOException ioException) {
			throw new AssertionError(ioException);
		}
	}

	private String[] _readLines() {
		return StringUtil.split(_read(), '\n');
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
		_deleteFIPSAuditLog();

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

		String[] ndjsons = _readLines();

		Assert.assertEquals(Arrays.toString(ndjsons), 2, ndjsons.length);

		_assertField(ndjsons[1], "failed-step", "Self test");
		_assertField(ndjsons[1], "from-state", "Self-Test");
		_assertField(ndjsons[1], "severity", "critical");
		_assertField(ndjsons[1], "to-state", "Error");
	}

	private void _testTransition(
		FIPSApplicationState fromFIPSApplicationState,
		FIPSApplicationState toFIPSApplicationState) {

		_deleteFIPSAuditLog();

		_setFIPSApplicationState(fromFIPSApplicationState);

		_transition(toFIPSApplicationState);

		Assert.assertEquals(
			toFIPSApplicationState,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		String[] ndjsons = _readLines();

		Assert.assertEquals(Arrays.toString(ndjsons), 1, ndjsons.length);

		_assertField(ndjsons[0], "event-type", "fips-state-transition");
		_assertField(
			ndjsons[0], "from-state", fromFIPSApplicationState.getValue());
		_assertField(ndjsons[0], "to-state", toFIPSApplicationState.getValue());
	}

	private void _testTransitionWithIllegalState(
		FIPSApplicationState fromFIPSApplicationState,
		FIPSApplicationState toFIPSApplicationState) {

		_deleteFIPSAuditLog();

		_setFIPSApplicationState(fromFIPSApplicationState);

		Assert.assertThrows(
			IllegalStateException.class,
			() -> _transition(toFIPSApplicationState));

		Assert.assertEquals(
			fromFIPSApplicationState,
			FIPSApplicationStateMachineUtil.getFIPSApplicationState());

		Assert.assertEquals("", _read());
	}

	private void _transition(FIPSApplicationState fipsApplicationState) {
		ReflectionTestUtil.invoke(
			FIPSApplicationStateMachineUtil.class, "_transition",
			new Class<?>[] {FIPSApplicationState.class, Consumer.class},
			fipsApplicationState,
			(Consumer<FIPSAuditEvent>)fipsAuditEvent -> {
			});
	}

	private Path _fipsAuditLogPath;
	private Path _liferayHomePath;
	private SafeCloseable _safeCloseable1;
	private SafeCloseable _safeCloseable2;

}