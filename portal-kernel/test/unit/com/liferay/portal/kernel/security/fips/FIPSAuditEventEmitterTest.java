/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.io.Flushable;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jorge García Jiménez
 */
public class FIPSAuditEventEmitterTest {

	@Test
	public void testEmit() {
		RecordingAppendable recordingAppendable = new RecordingAppendable();

		String cmvpCertificateId = RandomTestUtil.randomString();
		String deploymentInstanceId = RandomTestUtil.randomString();
		String providerName = RandomTestUtil.randomString();
		String providerVersion = RandomTestUtil.randomString();

		FIPSAuditEventEmitter fipsAuditEventEmitter = new FIPSAuditEventEmitter(
			recordingAppendable, () -> cmvpCertificateId,
			() -> deploymentInstanceId, () -> providerName,
			() -> providerVersion);

		String eventType = RandomTestUtil.randomString();

		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			eventType, FIPSAuditSeverity.CRITICAL);

		String fromState = RandomTestUtil.randomString();
		String toState = RandomTestUtil.randomString();

		fipsAuditEvent.put("from-state", fromState);
		fipsAuditEvent.put("to-state", toState);

		fipsAuditEventEmitter.emit(fipsAuditEvent);

		String ndjson = recordingAppendable.toString();

		Assert.assertTrue(ndjson, ndjson.endsWith("}\n"));
		Assert.assertEquals(ndjson, 1, recordingAppendable.getFlushCount());

		Assert.assertTrue(
			ndjson, ndjson.contains("\"event-schema-version\":\"1.0\""));
		Assert.assertTrue(ndjson, ndjson.contains("\"severity\":\"critical\""));
		Assert.assertTrue(
			ndjson, ndjson.contains("\"event-type\":\"" + eventType + "\""));
		Assert.assertTrue(
			ndjson,
			ndjson.contains(
				"\"deployment-instance-id\":\"" + deploymentInstanceId + "\""));
		Assert.assertTrue(
			ndjson,
			ndjson.contains("\"provider-name\":\"" + providerName + "\""));
		Assert.assertTrue(
			ndjson,
			ndjson.contains(
				"\"provider-version\":\"" + providerVersion + "\""));
		Assert.assertTrue(
			ndjson,
			ndjson.contains(
				"\"cmvp-certificate-id\":\"" + cmvpCertificateId + "\""));
		Assert.assertTrue(
			ndjson, ndjson.contains("\"from-state\":\"" + fromState + "\""));
		Assert.assertTrue(
			ndjson, ndjson.contains("\"to-state\":\"" + toState + "\""));

		Assert.assertTrue(
			ndjson,
			ndjson.matches(
				"(?s).*\"timestamp\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:" +
					"\\d{2}\\.\\d{3}Z\".*"));
	}

	@Test
	public void testEmitEscapesFieldValues() {
		RecordingAppendable recordingAppendable = new RecordingAppendable();

		FIPSAuditEventEmitter fipsAuditEventEmitter = new FIPSAuditEventEmitter(
			recordingAppendable, () -> "", () -> "", () -> "", () -> "");

		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			RandomTestUtil.randomString(), FIPSAuditSeverity.INFO);

		fipsAuditEvent.put("reason", "a\\b\"c\nd\re\tfg");

		fipsAuditEventEmitter.emit(fipsAuditEvent);

		String ndjson = recordingAppendable.toString();

		Assert.assertTrue(
			ndjson, ndjson.contains("\"reason\":\"a\\\\b\\\"c\\nd\\re\\tfg\""));
	}

	@Test
	public void testEmitNestsFields() {
		RecordingAppendable recordingAppendable = new RecordingAppendable();

		String providerName = RandomTestUtil.randomString();

		FIPSAuditEventEmitter fipsAuditEventEmitter = new FIPSAuditEventEmitter(
			recordingAppendable, () -> "", () -> "", () -> providerName,
			() -> "");

		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			RandomTestUtil.randomString(), FIPSAuditSeverity.INFO);

		String spoofedProviderName = RandomTestUtil.randomString();

		fipsAuditEvent.put("provider-name", spoofedProviderName);

		fipsAuditEventEmitter.emit(fipsAuditEvent);

		String ndjson = recordingAppendable.toString();

		Assert.assertTrue(
			ndjson,
			ndjson.contains("\"provider-name\":\"" + providerName + "\""));
		Assert.assertTrue(
			ndjson,
			ndjson.contains(
				"\"fields\":{\"provider-name\":\"" + spoofedProviderName +
					"\"}"));
	}

	@Test
	public void testEmitRendersTypedFieldValues() {
		RecordingAppendable recordingAppendable = new RecordingAppendable();

		FIPSAuditEventEmitter fipsAuditEventEmitter = new FIPSAuditEventEmitter(
			recordingAppendable, () -> "", () -> "", () -> "", () -> "");

		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			RandomTestUtil.randomString(), FIPSAuditSeverity.INFO);

		int duration = RandomTestUtil.randomInt();

		fipsAuditEvent.put("detail", null);
		fipsAuditEvent.put("duration-ms", duration);
		fipsAuditEvent.put("passed", true);

		fipsAuditEventEmitter.emit(fipsAuditEvent);

		String ndjson = recordingAppendable.toString();

		Assert.assertTrue(ndjson, ndjson.contains("\"detail\":null"));
		Assert.assertTrue(
			ndjson, ndjson.contains("\"duration-ms\":" + duration));
		Assert.assertTrue(ndjson, ndjson.contains("\"passed\":true"));
	}

	@Test
	public void testEmitSortsRecordKeys() {
		RecordingAppendable recordingAppendable = new RecordingAppendable();

		FIPSAuditEventEmitter fipsAuditEventEmitter = new FIPSAuditEventEmitter(
			recordingAppendable, () -> "", () -> "", () -> "", () -> "");

		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			"", FIPSAuditSeverity.CRITICAL);

		fipsAuditEvent.put("from-state", "");
		fipsAuditEvent.put("to-state", "");

		fipsAuditEventEmitter.emit(fipsAuditEvent);

		String ndjson = recordingAppendable.toString();

		String[] keys = {
			"cmvp-certificate-id", "deployment-instance-id",
			"event-schema-version", "event-type", "fields", "from-state",
			"to-state", "provider-name", "provider-version", "severity",
			"timestamp"
		};

		int previousIndex = -1;

		for (String key : keys) {
			int index = ndjson.indexOf("\"" + key);

			Assert.assertTrue(key, index > previousIndex);

			previousIndex = index;
		}
	}

	private static class RecordingAppendable implements Appendable, Flushable {

		@Override
		public Appendable append(char c) {
			_stringBuilder.append(c);

			return this;
		}

		@Override
		public Appendable append(CharSequence charSequence) {
			_stringBuilder.append(charSequence);

			return this;
		}

		@Override
		public Appendable append(
			CharSequence charSequence, int start, int end) {

			_stringBuilder.append(charSequence, start, end);

			return this;
		}

		@Override
		public void flush() {
			_flushCount++;
		}

		public int getFlushCount() {
			return _flushCount;
		}

		@Override
		public String toString() {
			return _stringBuilder.toString();
		}

		private int _flushCount;
		private final StringBuilder _stringBuilder = new StringBuilder();

	}

}