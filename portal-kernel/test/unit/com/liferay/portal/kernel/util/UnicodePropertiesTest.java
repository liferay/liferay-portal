/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.io.IOException;

import java.util.List;
import java.util.function.BiConsumer;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Alexander Chow
 * @author Xiangyue Cai
 */
public class UnicodePropertiesTest {

	@BeforeClass
	public static void setUpClass() {
		_safeNewLineCharacter = ReflectionTestUtil.getFieldValue(
			UnicodeProperties.class, "_SAFE_NEWLINE_CHARACTER");
	}

	@Test
	public void testFastLoad() {
		_testLoad(
			(props, unicodeProperties) -> unicodeProperties.fastLoad(props),
			false);
		_testLoad(
			(props, unicodeProperties) -> unicodeProperties.fastLoad(props),
			true);
	}

	@Test
	public void testGetProperty() {
		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.put(
			_TEST_KEY_1, _TEST_VALUE_1
		).build();

		Assert.assertEquals(
			_TEST_VALUE_1, unicodeProperties.getProperty(_TEST_KEY_1));
		Assert.assertEquals(
			_TEST_VALUE_1,
			unicodeProperties.getProperty(_TEST_KEY_1, "testDefaultValue"));

		Assert.assertNull(unicodeProperties.getProperty(_TEST_KEY_2));
		Assert.assertEquals(
			"testDefaultValue",
			unicodeProperties.getProperty(_TEST_KEY_2, "testDefaultValue"));
	}

	@Test
	public void testIsSafe() {
		UnicodeProperties unicodeProperties1 = new UnicodeProperties();

		Assert.assertFalse(
			"isSafe() should return false if UnicodeProperties is " +
				"instantiated by \"new UnicodeProperties()\"",
			unicodeProperties1.isSafe());

		UnicodeProperties unicodeProperties2 = new UnicodeProperties(false);

		Assert.assertFalse(
			"isSafe() should return false if UnicodeProperties is " +
				"instantiated by \"new UnicodeProperties(false)\"",
			unicodeProperties2.isSafe());

		UnicodeProperties unicodeProperties3 = new UnicodeProperties(true);

		Assert.assertTrue(
			"isSafe() should return true if UnicodeProperties is " +
				"instantiated by \"new UnicodeProperties(true)\"",
			unicodeProperties3.isSafe());
	}

	@Test
	public void testLoad() {
		_testLoad(
			(props, unicodeProperties) -> {
				try {
					unicodeProperties.load(props);
				}
				catch (IOException ioException) {
					ReflectionUtil.throwException(ioException);
				}
			},
			false);

		_testLoad(
			(props, unicodeProperties) -> {
				try {
					unicodeProperties.load(props);
				}
				catch (IOException ioException) {
					ReflectionUtil.throwException(ioException);
				}
			},
			true);
	}

	@Test
	public void testPutAll() {
		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.putAll(
			HashMapBuilder.put(
				_TEST_KEY_1, _TEST_VALUE_1
			).put(
				_TEST_KEY_2, _TEST_VALUE_2
			).put(
				_TEST_KEY_3, _TEST_VALUE_3
			).build()
		).build();

		_assertUnicodeProperties(
			new String[] {_TEST_VALUE_1, _TEST_VALUE_2, _TEST_VALUE_3},
			new String[] {_TEST_KEY_1, _TEST_KEY_2, _TEST_KEY_3},
			unicodeProperties);
	}

	@Test
	public void testPutLine() {
		_testPutLine(false);
		_testPutLine(true);
	}

	@Test
	public void testRemove() {
		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.put(
			_TEST_KEY_1, _TEST_VALUE_1
		).build();

		String result = unicodeProperties.remove(null);

		Assert.assertNull(result, result);

		_assertUnicodeProperties(
			new String[] {_TEST_VALUE_1}, new String[] {_TEST_KEY_1},
			unicodeProperties);

		Assert.assertEquals(
			_TEST_VALUE_1, unicodeProperties.remove(_TEST_KEY_1));
		Assert.assertTrue(
			"unicodeProperties should be empty after removing testKey1: " +
				unicodeProperties.toString(),
			unicodeProperties.isEmpty());
	}

	@Test
	public void testSetProperty() {
		UnicodeProperties unicodeProperties = new UnicodeProperties();

		Assert.assertNull(unicodeProperties.setProperty(null, _TEST_VALUE_1));
		Assert.assertTrue(
			"Nothing should be put in if key is null: " +
				unicodeProperties.toString(),
			unicodeProperties.isEmpty());

		Assert.assertNull(unicodeProperties.setProperty(_TEST_KEY_1, null));
		Assert.assertTrue(
			"Nothing should be put in if value is null: " +
				unicodeProperties.toString(),
			unicodeProperties.isEmpty());

		Assert.assertNull(
			unicodeProperties.setProperty(_TEST_KEY_1, _TEST_VALUE_1));

		_assertUnicodeProperties(
			new String[] {_TEST_VALUE_1}, new String[] {_TEST_KEY_1},
			unicodeProperties);

		Assert.assertEquals(
			_TEST_VALUE_1, unicodeProperties.setProperty(_TEST_KEY_1, null));
		Assert.assertTrue(
			"setProperty() of null value must remove entry: " +
				unicodeProperties.toString(),
			unicodeProperties.isEmpty());
	}

	@Test
	public void testToString() {
		_testToString(false);
		_testToString(true);
	}

	private void _assertToString(
		String expectedToString, UnicodeProperties unicodeProperties) {

		Assert.assertEquals(expectedToString, unicodeProperties.toString());
	}

	private void _assertUnicodeProperties(
		String[] expectedValues, String[] keys,
		UnicodeProperties unicodeProperties) {

		Assert.assertEquals(
			MapUtil.toString(unicodeProperties), expectedValues.length,
			unicodeProperties.size());

		for (int i = 0; i < keys.length; i++) {
			Assert.assertEquals(
				expectedValues[i], unicodeProperties.get(keys[i]));
		}
	}

	private void _testLoad(
		BiConsumer<String, UnicodeProperties> biConsumer, boolean safe) {

		UnicodeProperties unicodeProperties = new UnicodeProperties(safe);

		biConsumer.accept(null, unicodeProperties);

		Assert.assertTrue(
			"Nothing should be loaded in if props is null: " +
				unicodeProperties.toString(),
			unicodeProperties.isEmpty());

		biConsumer.accept(_TEST_LINE_1, unicodeProperties);

		_assertUnicodeProperties(
			new String[] {_TEST_VALUE_1}, new String[] {_TEST_KEY_1},
			unicodeProperties);

		biConsumer.accept(_TEST_PROPS, unicodeProperties);

		_assertUnicodeProperties(
			new String[] {_TEST_VALUE_1, _TEST_VALUE_2, _TEST_VALUE_3},
			new String[] {_TEST_KEY_1, _TEST_KEY_2, _TEST_KEY_3},
			unicodeProperties);

		biConsumer.accept(
			StringBundler.concat(
				_TEST_LINE_1, _safeNewLineCharacter, StringPool.NEW_LINE,
				_TEST_LINE_2, _safeNewLineCharacter, StringPool.NEW_LINE,
				_TEST_LINE_3, _safeNewLineCharacter, StringPool.NEW_LINE),
			unicodeProperties);

		if (safe) {
			_assertUnicodeProperties(
				new String[] {
					_TEST_VALUE_1.concat(StringPool.NEW_LINE),
					_TEST_VALUE_2.concat(StringPool.NEW_LINE),
					_TEST_VALUE_3.concat(StringPool.NEW_LINE)
				},
				new String[] {_TEST_KEY_1, _TEST_KEY_2, _TEST_KEY_3},
				unicodeProperties);
		}
		else {
			_assertUnicodeProperties(
				new String[] {
					_TEST_VALUE_1.concat(_safeNewLineCharacter),
					_TEST_VALUE_2.concat(_safeNewLineCharacter),
					_TEST_VALUE_3.concat(_safeNewLineCharacter)
				},
				new String[] {_TEST_KEY_1, _TEST_KEY_2, _TEST_KEY_3},
				unicodeProperties);
		}
	}

	private void _testPutLine(boolean safe) {
		UnicodeProperties unicodeProperties = new UnicodeProperties(safe);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				UnicodeProperties.class.getName(), LoggerTestUtil.ALL)) {

			unicodeProperties.put(_TEST_KEY_1);

			Assert.assertTrue(
				"Nothing should be put in if the line contains no \"=\": " +
					unicodeProperties.toString(),
				unicodeProperties.isEmpty());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Invalid property on line " + _TEST_KEY_1,
				logEntry.getMessage());
		}

		unicodeProperties.put("");

		Assert.assertTrue(
			"Nothing should be put in if called with empty string (\"\"): " +
				unicodeProperties.toString(),
			unicodeProperties.isEmpty());

		unicodeProperties.put("# " + _TEST_LINE_1);

		Assert.assertTrue(
			"Nothing should be put in if the line is started with #: " +
				unicodeProperties.toString(),
			unicodeProperties.isEmpty());

		unicodeProperties.put(_TEST_LINE_1);

		_assertUnicodeProperties(
			new String[] {_TEST_VALUE_1}, new String[] {_TEST_KEY_1},
			unicodeProperties);

		unicodeProperties.put(_TEST_LINE_1.concat(_safeNewLineCharacter));

		if (safe) {
			_assertUnicodeProperties(
				new String[] {_TEST_VALUE_1.concat(StringPool.NEW_LINE)},
				new String[] {_TEST_KEY_1}, unicodeProperties);
		}
		else {
			_assertUnicodeProperties(
				new String[] {_TEST_VALUE_1.concat(_safeNewLineCharacter)},
				new String[] {_TEST_KEY_1}, unicodeProperties);
		}
	}

	private void _testToString(boolean safe) {
		UnicodeProperties unicodeProperties = new UnicodeProperties(safe);

		_assertToString(StringPool.BLANK, unicodeProperties);

		unicodeProperties.put(_TEST_KEY_1, StringPool.BLANK);

		_assertToString(StringPool.BLANK, unicodeProperties);

		unicodeProperties.put(_TEST_LINE_2);

		_assertToString(
			_TEST_LINE_2.concat(StringPool.NEW_LINE), unicodeProperties);

		unicodeProperties.put(_TEST_LINE_3);

		_assertToString(
			StringBundler.concat(
				_TEST_LINE_2, StringPool.NEW_LINE, _TEST_LINE_3,
				StringPool.NEW_LINE),
			unicodeProperties);

		unicodeProperties.put(
			_TEST_KEY_3, _TEST_VALUE_3.concat(StringPool.NEW_LINE));

		if (safe) {
			_assertToString(
				StringBundler.concat(
					_TEST_LINE_2, StringPool.NEW_LINE, _TEST_LINE_3,
					_safeNewLineCharacter, StringPool.NEW_LINE),
				unicodeProperties);
		}
		else {
			_assertToString(
				StringBundler.concat(
					_TEST_LINE_2, StringPool.NEW_LINE, _TEST_LINE_3,
					StringPool.NEW_LINE, StringPool.NEW_LINE),
				unicodeProperties);
		}
	}

	private static final String _TEST_KEY_1 = "testKey1";

	private static final String _TEST_KEY_2 = "testKey2";

	private static final String _TEST_KEY_3 = "testKey3";

	private static final String _TEST_LINE_1;

	private static final String _TEST_LINE_2;

	private static final String _TEST_LINE_3;

	private static final String _TEST_PROPS;

	private static final String _TEST_VALUE_1 = "testValue1";

	private static final String _TEST_VALUE_2 = "testValue2";

	private static final String _TEST_VALUE_3 = "testValue3";

	private static String _safeNewLineCharacter;

	static {
		_TEST_LINE_1 = StringBundler.concat(
			_TEST_KEY_1, StringPool.EQUAL, _TEST_VALUE_1);

		_TEST_LINE_2 = StringBundler.concat(
			_TEST_KEY_2, StringPool.EQUAL, _TEST_VALUE_2);

		_TEST_LINE_3 = StringBundler.concat(
			_TEST_KEY_3, StringPool.EQUAL, _TEST_VALUE_3);

		_TEST_PROPS = StringBundler.concat(
			_TEST_LINE_1, StringPool.NEW_LINE, _TEST_LINE_2,
			StringPool.NEW_LINE, _TEST_LINE_3);
	}

}