/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.log4j;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayOutputStream;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.ByteBufferDestination;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.message.SimpleMessage;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Rafael Praxedes
 */
public class FIPSAuditNDJSONLayoutTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testEncode() {
		Map<String, Object> fipsAuditLogEntry =
			HashMapBuilder.<String, Object>put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			).put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			).build();

		FIPSAuditNDJSONLayout fipsAuditNDJSONLayout =
			_createFIPSAuditNDJSONLayout();

		TestByteBufferDestination testByteBufferDestination =
			new TestByteBufferDestination();

		fipsAuditNDJSONLayout.encode(
			_createLogEvent(new ObjectMessage(fipsAuditLogEntry)),
			testByteBufferDestination);

		Assert.assertEquals(
			_toSerializable(fipsAuditLogEntry),
			testByteBufferDestination._getString());
	}

	@Test
	public void testGetContentType() {
		FIPSAuditNDJSONLayout fipsAuditNDJSONLayout =
			_createFIPSAuditNDJSONLayout();

		Assert.assertEquals(
			"application/x-ndjson; charset=UTF-8",
			fipsAuditNDJSONLayout.getContentType());
	}

	@Test
	public void testToSerializableEndsWithASingleNewLine() {
		String json = _toSerializable(
			Collections.singletonMap("event-type", "fips-state-transition"));

		Assert.assertEquals(json.length() - 1, json.indexOf(CharPool.NEW_LINE));
		JSONAssert.assertEquals(
			JSONUtil.put(
				"event-type", "fips-state-transition"
			).toString(),
			json, true);
	}

	@Test
	public void testToSerializableEscapesReservedCharacters() {
		String providerErrorMessage = new String(
			new char[] {
				'a', '\\', 'b', '"', 'c', '\n', 'd', '\r', 'e', '\t', 'f', 0x01,
				'g', 0x2028, 'h', 0x2029, 'i'
			});

		_testToSerializable(
			JSONUtil.put("provider-error-message", providerErrorMessage),
			"provider-error-message", providerErrorMessage);
	}

	@Test
	public void testToSerializableRejectsAForeignEvent() {
		_testToSerializableThrows(
			new ObjectMessage(RandomTestUtil.randomString()));
		_testToSerializableThrows(
			new SimpleMessage(RandomTestUtil.randomString()));
	}

	@Test
	public void testToSerializableWritesEveryEntry() throws Exception {
		Map<String, Object> fipsAuditLogEntry =
			HashMapBuilder.<String, Object>put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			).put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			).put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			).build();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_toSerializable(fipsAuditLogEntry));

		Assert.assertEquals(fipsAuditLogEntry.size(), jsonObject.length());

		for (Map.Entry<String, Object> entry : fipsAuditLogEntry.entrySet()) {
			Assert.assertEquals(
				entry.getValue(), jsonObject.getString(entry.getKey()));
		}
	}

	@Test
	public void testToSerializableWritesValueTypes() {
		_testToSerializable(
			JSONUtil.put("array", JSONUtil.putAll("first", "second")), "array",
			new String[] {"first", "second"});
		_testToSerializable(
			JSONUtil.put("boolean", Boolean.TRUE), "boolean", Boolean.TRUE);
		_testToSerializable(JSONUtil.put("decimal", 1.5D), "decimal", 1.5D);
		_testToSerializable(
			JSONUtil.put("iterable", JSONUtil.putAll("one", 2)), "iterable",
			Arrays.asList("one", 2));
		_testToSerializable(
			JSONUtil.put("map", JSONUtil.put("first", "second")), "map",
			Collections.singletonMap("first", "second"));
		_testToSerializable(JSONUtil.put("number", 42), "number", 42);
		_testToSerializable(JSONUtil.put("string", "text"), "string", "text");
	}

	private FIPSAuditNDJSONLayout _createFIPSAuditNDJSONLayout() {
		FIPSAuditNDJSONLayout.Builder builder =
			FIPSAuditNDJSONLayout.newBuilder();

		return builder.build();
	}

	private LogEvent _createLogEvent(Message message) {
		Log4jLogEvent.Builder builder = Log4jLogEvent.newBuilder();

		builder.setLevel(Level.INFO);
		builder.setLoggerName(RandomTestUtil.randomString());
		builder.setMessage(message);

		return builder.build();
	}

	private void _testToSerializable(
		JSONObject expectedJSONObject, String key, Object value) {

		JSONAssert.assertEquals(
			expectedJSONObject.toString(),
			_toSerializable(Collections.singletonMap(key, value)), true);
	}

	private void _testToSerializableThrows(Message message) {
		FIPSAuditNDJSONLayout fipsAuditNDJSONLayout =
			_createFIPSAuditNDJSONLayout();

		IllegalStateException illegalStateException = Assert.assertThrows(
			IllegalStateException.class,
			() -> fipsAuditNDJSONLayout.toSerializable(
				_createLogEvent(message)));

		String errorMessage = illegalStateException.getMessage();

		Assert.assertTrue(errorMessage.contains("carries no FIPS audit event"));
	}

	private String _toSerializable(Map<String, Object> fipsAuditLogEntry) {
		FIPSAuditNDJSONLayout fipsAuditNDJSONLayout =
			_createFIPSAuditNDJSONLayout();

		return fipsAuditNDJSONLayout.toSerializable(
			_createLogEvent(new ObjectMessage(fipsAuditLogEntry)));
	}

	private static class TestByteBufferDestination
		implements ByteBufferDestination {

		@Override
		public ByteBuffer drain(ByteBuffer byteBuffer) {
			byteBuffer.flip();

			writeBytes(byteBuffer);

			byteBuffer.clear();

			return byteBuffer;
		}

		@Override
		public ByteBuffer getByteBuffer() {
			return _byteBuffer;
		}

		@Override
		public void writeBytes(byte[] bytes, int offset, int length) {
			_byteArrayOutputStream.write(bytes, offset, length);
		}

		@Override
		public void writeBytes(ByteBuffer byteBuffer) {
			byte[] bytes = new byte[byteBuffer.remaining()];

			byteBuffer.get(bytes);

			writeBytes(bytes, 0, bytes.length);
		}

		private String _getString() {
			drain(_byteBuffer);

			byte[] bytes = _byteArrayOutputStream.toByteArray();

			return new String(bytes, StandardCharsets.UTF_8);
		}

		private final ByteArrayOutputStream _byteArrayOutputStream =
			new ByteArrayOutputStream();
		private final ByteBuffer _byteBuffer = ByteBuffer.allocate(4096);

	}

}