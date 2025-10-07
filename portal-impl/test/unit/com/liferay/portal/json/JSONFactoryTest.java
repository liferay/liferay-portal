/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.json;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.jabsorb.serializer.LiferayJSONDeserializationWhitelist;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Igor Spasic
 */
public class JSONFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		JSONInit.init();

		JSONFactoryImpl jsonFactoryImpl = new JSONFactoryImpl();

		LiferayJSONDeserializationWhitelist
			liferayJSONDeserializationWhitelist =
				jsonFactoryImpl.getLiferayJSONDeserializationWhitelist();

		liferayJSONDeserializationWhitelist.register(
			FooBean.class.getName(), FooBean1.class.getName(),
			FooBean2.class.getName(), FooBean3.class.getName(),
			FooBean4.class.getName(), FooBean5.class.getName(),
			FooBean6.class.getName(), FooBean7.class.getName());

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(jsonFactoryImpl);
	}

	@Test
	public void testAnnotations() {
		String json = removeQuotes(
			JSONFactoryUtil.looseSerialize(new FooBean()));

		Assert.assertEquals("{name:bar,value:173}", json);
	}

	@Test
	public void testCollection() {
		String json = removeQuotes(
			JSONFactoryUtil.looseSerialize(new FooBean1()));

		Assert.assertEquals("{collection:[element],value:173}", json);
	}

	@Test
	public void testDeserializeLongArrayToIntegerArray() {
		String json = JSONFactoryUtil.serialize(
			HashMapBuilder.<String, long[]>put(
				"key", new long[] {1L, 2L, 3L, 4L, 5L}
			).build());

		Object object = JSONFactoryUtil.deserialize(json);

		Assert.assertTrue(object instanceof Map);

		Map<String, long[]> deserializedMap = (Map<String, long[]>)object;

		Object values = deserializedMap.get("key");

		Assert.assertTrue(values instanceof Integer[]);
	}

	@Test
	public void testDeserializeNonwhitelistedClass() {
		String json = JSONFactoryUtil.serialize(new JSONFactoryTest());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				LiferayJSONDeserializationWhitelist.class.getName(),
				LoggerTestUtil.WARN)) {

			Object object = JSONFactoryUtil.deserialize(json);

			Assert.assertTrue(
				object.getClass() + " is not an instance of Map",
				object instanceof Map);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertTrue(
				logEntry.getMessage(),
				StringUtil.startsWith(
					logEntry.getMessage(),
					"Unable to deserialize " +
						JSONFactoryTest.class.getName()));
		}
	}

	@Test
	public void testDeserializePrimitiveArrays() {
		String json = buildPrimitiveArraysJSON();

		Object object = JSONFactoryUtil.deserialize(json);

		Assert.assertTrue(object instanceof FooBean3);

		checkPrimitiveArrays((FooBean3)object);
	}

	@Test
	public void testDeserializePrimitiveArraysSerializable() {
		String json = buildPrimitiveArraysSerializableJSON();

		Object object = JSONFactoryUtil.deserialize(json);

		Assert.assertTrue(object instanceof FooBean4);

		checkPrimitiveArrays((FooBean4)object);
	}

	@Test
	public void testDeserializePrimitives() {
		String json = buildPrimitivesJSON();

		Object object = JSONFactoryUtil.deserialize(json);

		Assert.assertTrue(object instanceof FooBean5);

		checkPrimitives((FooBean5)object);
	}

	@Test
	public void testDeserializePrimitivesSerializable() {
		String json = buildPrimitivesSerializableJSON();

		Object object = JSONFactoryUtil.deserialize(json);

		Assert.assertTrue(object instanceof FooBean6);

		checkPrimitives((FooBean6)object);
	}

	@Test
	public void testHasProperty() {
		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		jsonSerializer.exclude("class");

		String jsonString = jsonSerializer.serialize(new Three());

		Assert.assertEquals("{\"flag\":true}", jsonString);
	}

	@Test
	public void testLooseDeserialize() {
		Object object = JSONFactoryUtil.looseDeserialize(
			"{\"class\":\"" + JSONFactoryUtil.class.getName() + "\"}");

		Assert.assertTrue(object instanceof Map);

		object = JSONFactoryUtil.looseDeserialize(
			"{\"class\":\"java.lang.Thread\"}");

		Assert.assertTrue(object instanceof Map);
	}

	@Test
	public void testLooseDeserializeSafe() {
		Object object = JSONFactoryUtil.looseDeserialize(
			"{\"class\":\"java.lang.Thread\"}");

		Assert.assertEquals(LinkedHashMap.class, object.getClass());

		object = JSONFactoryUtil.looseDeserialize(
			"{\"\u0063lass\":\"java.lang.Thread\"}");

		Assert.assertEquals(LinkedHashMap.class, object.getClass());

		Map<?, ?> map = (Map<?, ?>)object;

		Assert.assertTrue(map.containsKey("class"));

		JSONFactoryUtil.looseDeserialize(
			"{\"class\":\"" + JSONFactoryUtil.class.getName() + "\"}");

		map = (Map<?, ?>)JSONFactoryUtil.looseDeserialize(
			"{\"class\":\"" + JSONFactoryUtil.class.getName() +
				"\",\"foo\": \"boo\"}");

		Assert.assertNotNull(map);
		Assert.assertEquals(map.toString(), 2, map.size());
		Assert.assertEquals(
			"com.liferay.portal.kernel.json.JSONFactoryUtil", map.get("class"));
		Assert.assertEquals("boo", map.get("foo"));

		map = (Map<?, ?>)JSONFactoryUtil.looseDeserialize(
			StringBundler.concat(
				"{\"class\":\"", JSONFactoryUtil.class.getName(),
				"\",\"foo\": \"boo\",\"jsonFactory\":{\"class\":\"",
				JSONFactoryImpl.class.getName(), "\"}}"));

		Assert.assertNotNull(map);
		Assert.assertEquals(map.toString(), 3, map.size());
		Assert.assertEquals(JSONFactoryUtil.class.getName(), map.get("class"));
		Assert.assertEquals("boo", map.get("foo"));

		map = (Map<?, ?>)map.get("jsonFactory");

		Assert.assertNotNull(map);
		Assert.assertEquals(map.toString(), 1, map.size());
		Assert.assertEquals(JSONFactoryImpl.class.getName(), map.get("class"));
	}

	@Test
	public void testSerializeDeserializeEnum() {
		String json = JSONFactoryUtil.serialize(
			HashMapBuilder.put(
				"enum", FooBean7.TEST_1
			).build());

		Object object = JSONFactoryUtil.deserialize(json);

		Assert.assertTrue(object instanceof HashMap);

		HashMap<?, ?> hashMap = (HashMap<?, ?>)object;

		Assert.assertTrue(hashMap.get("enum") instanceof FooBean7);

		Assert.assertSame(hashMap.get("enum"), FooBean7.TEST_1);
	}

	@Test
	public void testSerializeDeserializeEnumInsideMessage() {
		Message message = new Message();

		message.put("enum", FooBean7.TEST_1);

		String json = JSONFactoryUtil.serialize(message);

		Object object = JSONFactoryUtil.deserialize(json);

		Assert.assertTrue(object instanceof Message);

		message = (Message)object;

		Assert.assertTrue(message.get("enum") instanceof FooBean7);

		Assert.assertSame(message.get("enum"), FooBean7.TEST_1);
	}

	@Test
	public void testSerializePrimitiveArrays() {
		String json = buildPrimitiveArraysJSON();

		Assert.assertNotNull(json);

		checkJSONPrimitiveArrays(json);
	}

	@Test
	public void testSerializePrimitiveArraysSerializable() {
		String json = buildPrimitiveArraysSerializableJSON();

		Assert.assertNotNull(json);

		checkJSONPrimitiveArrays(json);
		checkJSONSerializableArgument(json);
	}

	@Test
	public void testSerializePrimitives() {
		String json = buildPrimitivesJSON();

		Assert.assertNotNull(json);

		checkJSONPrimitives(json);
	}

	@Test
	public void testSerializePrimitivesSerializable() {
		String json = buildPrimitivesSerializableJSON();

		Assert.assertNotNull(json);

		checkJSONPrimitives(json);
		checkJSONSerializableArgument(json);
	}

	@Test
	public void testStrictMode() {
		String json = removeQuotes(
			JSONFactoryUtil.looseSerialize(new FooBean2()));

		Assert.assertEquals("{value:173}", json);
	}

	protected String buildPrimitiveArraysJSON() {
		FooBean3 fooBean3 = new FooBean3();

		initializePrimitiveArrays(fooBean3);

		return JSONFactoryUtil.serialize(fooBean3);
	}

	protected String buildPrimitiveArraysSerializableJSON() {
		FooBean4 fooBean4 = new FooBean4();

		initializePrimitiveArrays(fooBean4);

		return JSONFactoryUtil.serialize(fooBean4);
	}

	protected String buildPrimitivesJSON() {
		FooBean5 fooBean5 = new FooBean5();

		initializePrimitives(fooBean5);

		return JSONFactoryUtil.serialize(fooBean5);
	}

	protected String buildPrimitivesSerializableJSON() {
		FooBean6 fooBean6 = new FooBean6();

		initializePrimitives(fooBean6);

		return JSONFactoryUtil.serialize(fooBean6);
	}

	protected void checkJSONPrimitiveArrays(String json) {
		Assert.assertTrue(
			json, json.contains("\"doubleArray\":" + _DOUBLE_ARRAY_STRING));
		Assert.assertTrue(
			json, json.contains("\"longArray\":" + _LONG_ARRAY_STRING));
		Assert.assertTrue(
			json, json.contains("\"integerArray\":" + _INTEGER_ARRAY_STRING));
	}

	protected void checkJSONPrimitives(String json) {
		Assert.assertTrue(json, json.contains("\"longValue\":" + _LONG_VALUE));
		Assert.assertTrue(
			json, json.contains("\"integerValue\":" + _INTEGER_VALUE));
		Assert.assertTrue(
			json, json.contains("\"doubleValue\":" + _DOUBLE_VALUE));
	}

	protected void checkJSONSerializableArgument(String json) {
		Assert.assertTrue(json, json.contains("serializable"));
	}

	protected void checkPrimitiveArrays(FooBean3 fooBean3) {
		AssertUtils.assertEquals(_DOUBLE_ARRAY, fooBean3.getDoubleArray());
		Assert.assertArrayEquals(_INTEGER_ARRAY, fooBean3.getIntegerArray());
		Assert.assertArrayEquals(_LONG_ARRAY, fooBean3.getLongArray());
	}

	protected void checkPrimitives(FooBean5 fooBean5) {
		Assert.assertEquals(_INTEGER_VALUE, fooBean5.getIntegerValue());
		Assert.assertEquals(_LONG_VALUE, fooBean5.getLongValue());
		AssertUtils.assertEquals(_DOUBLE_VALUE, fooBean5.getDoubleValue());
	}

	protected void initializePrimitiveArrays(FooBean3 fooBean3) {
		fooBean3.setDoubleArray(_DOUBLE_ARRAY);
		fooBean3.setIntegerArray(_INTEGER_ARRAY);
		fooBean3.setLongArray(_LONG_ARRAY);
	}

	protected void initializePrimitives(FooBean5 fooBean5) {
		fooBean5.setDoubleValue(_DOUBLE_VALUE);
		fooBean5.setIntegerValue(_INTEGER_VALUE);
		fooBean5.setLongValue(_LONG_VALUE);
	}

	protected String removeQuotes(String string) {
		return StringUtil.replace(string, CharPool.QUOTE, StringPool.BLANK);
	}

	private static final double[] _DOUBLE_ARRAY = {1.2345, 2.3456, 5.6789};

	private static final String _DOUBLE_ARRAY_STRING = "[1.2345,2.3456,5.6789]";

	private static final double _DOUBLE_VALUE = 3.1425927;

	private static final int[] _INTEGER_ARRAY = {1, 2, 3, 4, 5};

	private static final String _INTEGER_ARRAY_STRING = "[1,2,3,4,5]";

	private static final int _INTEGER_VALUE = 5;

	private static final long[] _LONG_ARRAY = {
		10000000000000L, 20000000000000L, 30000000000000L
	};

	private static final String _LONG_ARRAY_STRING =
		"[10000000000000,20000000000000,30000000000000]";

	private static final long _LONG_VALUE = 50000000000000L;

}