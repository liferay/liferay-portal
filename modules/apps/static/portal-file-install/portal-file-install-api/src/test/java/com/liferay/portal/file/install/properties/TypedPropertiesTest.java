/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.file.install.properties;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Matthew Tambara
 */
public class TypedPropertiesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testKeySet() throws IOException {
		TypedProperties typedProperties = _createTypedProperties(
			"testKey1 = \"testValue1\"\ntestKey2 = \"testValue2\"");

		Set<String> keys = typedProperties.keySet();

		Assert.assertEquals(keys.toString(), 2, keys.size());

		for (String key : keys) {
			Assert.assertNotEquals(null, typedProperties.get(key));
		}
	}

	@Test
	public void testLoadandSaveArray() throws IOException {
		String line = "testKey = [\"testValue1\", \"testValue2\"]";

		TypedProperties typedProperties = _createTypedProperties(line);

		Assert.assertArrayEquals(
			new String[] {"testValue1", "testValue2"},
			(String[])typedProperties.get("testKey"));

		_assertSave(typedProperties, line);
	}

	@Test
	public void testLoadandSaveCollection() throws IOException {
		String line = "testKey = (\"testValue1\", \"testValue2\")";

		TypedProperties typedProperties = _createTypedProperties(line);

		Assert.assertEquals(
			Arrays.asList("testValue1", "testValue2"),
			typedProperties.get("testKey"));

		_assertSave(typedProperties, line);
	}

	@Test
	public void testLoadandSaveComment() throws IOException {
		String line = "#comment\ntestKey = \"testValue\"";

		TypedProperties typedProperties = _createTypedProperties(line);

		Assert.assertEquals("testValue", typedProperties.get("testKey"));

		_assertSave(typedProperties, line);
	}

	@Test
	public void testLoadandSaveEmptyLines() throws IOException {
		TypedProperties typedProperties = _createTypedProperties(
			"\ntestKey1 = \"testValue1\"\n\ntestKey2 = \"testValue2\"\n");

		Assert.assertEquals("testValue1", typedProperties.get("testKey1"));
		Assert.assertEquals("testValue2", typedProperties.get("testKey2"));

		_assertSave(
			typedProperties,
			"\\\ntestKey1 = \"testValue1\"\n\\\ntestKey2 = \"testValue2\"");
	}

	@Test
	public void testLoadandSaveEmptyString() throws IOException {
		String line = "testKey = \"\"";

		TypedProperties typedProperties = _createTypedProperties(line);

		Assert.assertEquals("", typedProperties.get("testKey"));

		_assertSave(typedProperties, line);
	}

	@Test
	public void testLoadandSaveEscapedEquals() throws IOException {
		String line = "testKey = \"testValue\\=test\"";

		TypedProperties typedProperties = _createTypedProperties(line);

		Assert.assertEquals("testValue=test", typedProperties.get("testKey"));

		_assertSave(typedProperties, line);
	}

	@Test
	public void testLoadandSaveMultiline() throws IOException {
		String line =
			"testKey = [\\\n\t\"testValue1\",\\\n\t\"testValue2\"\\\n]";

		TypedProperties typedProperties = _createTypedProperties(line);

		Assert.assertArrayEquals(
			new String[] {"testValue1", "testValue2"},
			(String[])typedProperties.get("testKey"));

		_assertSave(typedProperties, line);
	}

	@Test
	public void testLoadandSaveMultipleComments() throws IOException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				TypedProperties.class.getName(), LoggerTestUtil.WARN)) {

			TypedProperties typedProperties = _createTypedProperties(
				"#comment1\n#comment2\ntestKey = \"testValue\"");

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Multiple comment lines found: [#comment1, #comment2]",
				logEntry.getMessage());

			Assert.assertEquals("testValue", typedProperties.get("testKey"));

			_assertSave(typedProperties, "#comment1\ntestKey = \"testValue\"");
		}
	}

	@Test
	public void testLoadAndSaveSubstitution() throws IOException {
		String systemKey = "testSystemKey";

		String line = StringBundler.concat("testKey = \"${", systemKey, "}\"");

		String systemValue = "testSystemValue";

		String oldSystemValue = System.getProperty(systemKey);

		System.setProperty(systemKey, systemValue);

		TypedProperties typedProperties = _createTypedProperties(line);

		try {
			Assert.assertEquals(
				"testSystemValue", typedProperties.get("testKey"));
		}
		finally {
			if (oldSystemValue == null) {
				System.clearProperty(systemKey);
			}
			else {
				System.setProperty(systemKey, oldSystemValue);
			}
		}

		_assertSave(typedProperties, line);
	}

	@Test
	public void testLoadandSaveTrailingComment() throws IOException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				TypedProperties.class.getName(), LoggerTestUtil.WARN)) {

			TypedProperties typedProperties = _createTypedProperties(
				"testKey = \"testValue\"\n#comment");

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Comment must be at beginning of config file: #comment",
				logEntry.getMessage());

			Assert.assertEquals("testValue", typedProperties.get("testKey"));

			_assertSave(typedProperties, "testKey = \"testValue\"");
		}
	}

	@Test
	public void testLoadandStoreMultilineString() throws IOException {
		String line = "testKey = \"testValue1,\\\n\ttestValue2\"";

		TypedProperties typedProperties = _createTypedProperties(line);

		Assert.assertEquals(
			"testValue1,testValue2", typedProperties.get("testKey"));

		_assertSave(typedProperties, line);
	}

	@Test
	public void testLoadBadLine() throws IOException {
		String line = "testKey = K\"testValue\"";

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				TypedProperties.class.getName(), LoggerTestUtil.WARN)) {

			TypedProperties typedProperties = _createTypedProperties(line);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to parse config line: " + line, logEntry.getMessage());

			Assert.assertEquals(null, typedProperties.get("testKey"));

			_assertSave(typedProperties, StringPool.BLANK);
		}
	}

	@NewEnv(type = NewEnv.Type.JVM)
	@NewEnv.Environment(variables = "LIFERAY_FOO_ENV_VALUE=ENV_TEST_VALUE")
	@Test
	public void testLoadEnvVariable() throws IOException {
		TypedProperties typedProperties = new TypedProperties();

		typedProperties.load(
			new StringReader("testKey=\"${LIFERAY_FOO_ENV_VALUE}\""));

		Assert.assertEquals("ENV_TEST_VALUE", typedProperties.get("testKey"));
	}

	@Test
	public void testLoadNonexistentEnvVariable() throws IOException {
		TypedProperties typedProperties = _createTypedProperties(
			"testKey=\"${LIFERAY_FOO_ENV_VALUE}\"");

		Assert.assertNull(typedProperties.get("LIFERAY_FOO_ENV_VALUE"));
	}

	@Test
	public void testLoadNontyped() throws IOException {
		TypedProperties typedProperties = _createTypedProperties(
			"testKey = \"testValue\"");

		Assert.assertEquals("testValue", typedProperties.get("testKey"));
	}

	@Test
	public void testLoadPutandSaveKeepFormat() throws IOException {
		String line = "testKey = \\\n\"testValue\"";

		TypedProperties typedProperties = _createTypedProperties(line);

		typedProperties.put("testKey", "testValue");

		_assertSave(typedProperties, line);
	}

	@Test
	public void testLoadPutandSaveMultiline() throws IOException {
		String line =
			"testKey = [\\\n\t\"testValue1\",\\\n\t\"testValue2\"\\\n]";

		TypedProperties typedProperties = _createTypedProperties(line);

		typedProperties.put(
			"testKey", new String[] {"testValue1", "testValue2"});

		_assertSave(typedProperties, line);
	}

	@Test
	public void testLoadTyped() throws IOException {
		TypedProperties typedProperties = _createTypedProperties(
			"testKey = I\"1\"");

		Assert.assertEquals(1, typedProperties.get("testKey"));
	}

	@Test
	public void testOverwriteArray() throws IOException {
		String line = "testKey = [\"testValue1\", \"testValue2\"]";

		TypedProperties typedProperties = _createTypedProperties(line);

		typedProperties.put(
			"testKey", new String[] {"testValue3", "testValue4"});

		_assertSave(
			typedProperties,
			"testKey=[\\\r\n  \"testValue3\",\\\r\n  \"testValue4\"\\\r\n]");
	}

	@Test
	public void testOverwriteNontypedWithNontyped() throws IOException {
		TypedProperties typedProperties = _createTypedProperties(
			"testKey = \"testValue1\"");

		Assert.assertEquals("testValue1", typedProperties.get("testKey"));

		typedProperties.put("testKey", "testValue2");

		Assert.assertEquals("testValue2", typedProperties.get("testKey"));
	}

	@Test
	public void testOverwriteNontypedWithTyped() throws IOException {
		TypedProperties typedProperties = _createTypedProperties(
			"testKey = \"1\"");

		Assert.assertEquals("1", typedProperties.get("testKey"));

		typedProperties.put("testKey", 1);

		Assert.assertEquals(1, typedProperties.get("testKey"));
	}

	@Test
	public void testOverwriteTypedWithNontyped() throws IOException {
		TypedProperties typedProperties = _createTypedProperties(
			"testKey = I\"1\"");

		Assert.assertEquals(1, typedProperties.get("testKey"));

		typedProperties.put("testKey", "1");

		Assert.assertEquals("1", typedProperties.get("testKey"));
	}

	@Test
	public void testOverwriteTypedWithTyped() throws IOException {
		TypedProperties typedProperties = _createTypedProperties(
			"testKey = I\"1\"");

		Assert.assertEquals(1, typedProperties.get("testKey"));

		typedProperties.put("testKey", 2);

		Assert.assertEquals(2, typedProperties.get("testKey"));
	}

	@Test
	public void testPutandSave() throws IOException {
		TypedProperties typedProperties = new TypedProperties();

		typedProperties.put("testKey", "testValue");

		_assertSave(typedProperties, "testKey=\"testValue\"");
	}

	@Test
	public void testRemove() throws IOException {
		TypedProperties typedProperties = _createTypedProperties(
			"testKey = \"testValue\"");

		Assert.assertEquals("testValue", typedProperties.get("testKey"));

		typedProperties.remove("testKey");

		Assert.assertEquals(null, typedProperties.get("testKey"));
	}

	@Test
	public void testSaveNontyped() throws IOException {
		String line = "testKey = \"testValue\"";

		TypedProperties typedProperties = _createTypedProperties(line);

		_assertSave(typedProperties, line);
	}

	@Test
	public void testSaveTyped() throws IOException {
		String line = "testKey = I\"1\"";

		TypedProperties typedProperties = _createTypedProperties(line);

		_assertSave(typedProperties, line);
	}

	@Test
	public void testWriteNontyped() throws IOException {
		TypedProperties typedProperties = new TypedProperties();

		typedProperties.put("testKey", "testValue");

		Assert.assertEquals("testValue", typedProperties.get("testKey"));
	}

	@Test
	public void testWriteTyped() throws IOException {
		TypedProperties typedProperties = new TypedProperties();

		typedProperties.put("testKey", 1);

		Assert.assertEquals(1, typedProperties.get("testKey"));
	}

	private void _assertSave(TypedProperties typedProperties, String expected)
		throws IOException {

		try (StringWriter stringWriter = new StringWriter()) {
			typedProperties.save(stringWriter);

			Assert.assertEquals(expected, stringWriter.toString());
		}
	}

	private TypedProperties _createTypedProperties(String string)
		throws IOException {

		TypedProperties typedProperties = new TypedProperties();

		try (StringReader stringReader = new StringReader(string)) {
			typedProperties.load(stringReader);
		}

		return typedProperties;
	}

}