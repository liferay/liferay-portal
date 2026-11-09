/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringPool;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 * @author Cleydyr de Albuquerque
 * @author Tibor Lipusz
 */
public class GetterUtilTest {

	@Test
	public void testGetBoolean() {
		Assert.assertFalse(GetterUtil.getBoolean("false"));
		Assert.assertTrue(GetterUtil.getBoolean("true"));
		Assert.assertFalse(GetterUtil.getBoolean(Boolean.FALSE));
		Assert.assertTrue(GetterUtil.getBoolean(Boolean.TRUE));
		Assert.assertFalse(GetterUtil.getBoolean(null, false));
		Assert.assertTrue(GetterUtil.getBoolean(null, true));
		Assert.assertFalse(GetterUtil.getBoolean(StringPool.BLANK));
		Assert.assertFalse(GetterUtil.getBoolean(StringPool.BLANK, false));
		Assert.assertFalse(GetterUtil.getBoolean(StringPool.BLANK, true));

		for (String s : GetterUtil.BOOLEANS) {
			Assert.assertTrue(GetterUtil.getBoolean(s));
			Assert.assertTrue(GetterUtil.getBoolean(s, true));
			Assert.assertTrue(GetterUtil.getBoolean(s, false));
		}
	}

	@Test
	public void testGetDouble() {

		// Wrong first char

		Assert.assertEquals(
			GetterUtil.DEFAULT_DOUBLE, GetterUtil.getDouble("e12.3"),
			GetterUtil.DEFAULT_DOUBLE);

		// Wrong middle char

		Assert.assertEquals(
			GetterUtil.DEFAULT_DOUBLE, GetterUtil.getDouble("12e.3"),
			GetterUtil.DEFAULT_DOUBLE);

		// Start with '+'

		Assert.assertEquals(
			12.3, GetterUtil.getDouble("+12.3"), GetterUtil.DEFAULT_DOUBLE);

		// Start with '-'

		Assert.assertEquals(
			-12.3, GetterUtil.getDouble("-12.3"), GetterUtil.DEFAULT_DOUBLE);

		// Maximum double

		Assert.assertEquals(
			Double.MAX_VALUE,
			GetterUtil.getDouble(String.valueOf(Double.MAX_VALUE)),
			GetterUtil.DEFAULT_DOUBLE);

		// Minimum double

		Assert.assertEquals(
			Double.MIN_VALUE,
			GetterUtil.getDouble(String.valueOf(Double.MIN_VALUE)),
			GetterUtil.DEFAULT_DOUBLE);

		// Locale aware

		Assert.assertEquals(
			4.7, GetterUtil.getDouble("4,7", LocaleUtil.BRAZIL),
			GetterUtil.DEFAULT_DOUBLE);

		Assert.assertEquals(
			4.7, GetterUtil.getDouble("4.7", LocaleUtil.US),
			GetterUtil.DEFAULT_DOUBLE);

		// Locale aware respecting the whole input

		Assert.assertEquals(
			GetterUtil.DEFAULT_DOUBLE,
			GetterUtil.getDouble("4.7", LocaleUtil.HUNGARY),
			GetterUtil.DEFAULT_DOUBLE);

		// Blank

		for (String value :
				new String[] {StringPool.BLANK, StringPool.DOUBLE_SPACE}) {

			Assert.assertEquals(
				GetterUtil.DEFAULT_DOUBLE, GetterUtil.getDouble(value),
				GetterUtil.DEFAULT_DOUBLE);
			Assert.assertEquals(
				1.5, GetterUtil.getDouble(value, 1.5),
				GetterUtil.DEFAULT_DOUBLE);
			Assert.assertEquals(
				GetterUtil.DEFAULT_DOUBLE,
				GetterUtil.getDouble(value, LocaleUtil.US),
				GetterUtil.DEFAULT_DOUBLE);
			Assert.assertEquals(
				1.5, GetterUtil.get(value, 1.5, LocaleUtil.US),
				GetterUtil.DEFAULT_DOUBLE);
		}
	}

	@Test
	public void testGetFloat() {
		for (String value :
				new String[] {StringPool.BLANK, StringPool.DOUBLE_SPACE}) {

			Assert.assertEquals(
				GetterUtil.DEFAULT_FLOAT, GetterUtil.getFloat(value),
				GetterUtil.DEFAULT_FLOAT);
			Assert.assertEquals(
				1.5F, GetterUtil.getFloat(value, 1.5F),
				GetterUtil.DEFAULT_FLOAT);
		}
	}

	@Test
	public void testGetInteger() {

		// Wrong first char

		int result = GetterUtil.get("e123", -1);

		Assert.assertEquals(-1, result);

		// Wrong middle char

		result = GetterUtil.get("12e3", -1);

		Assert.assertEquals(-1, result);

		// Start with '+'

		result = GetterUtil.get("+123", -1);

		Assert.assertEquals(123, result);

		// Start with '-'

		result = GetterUtil.get("-123", -1);

		Assert.assertEquals(-123, result);

		// Maximum int

		result = GetterUtil.get(Integer.toString(Integer.MAX_VALUE), -1);

		Assert.assertEquals(Integer.MAX_VALUE, result);

		// Minimum int

		result = GetterUtil.get(Integer.toString(Integer.MIN_VALUE), -1);

		Assert.assertEquals(Integer.MIN_VALUE, result);

		// Larger than maximum int

		result = GetterUtil.get(Integer.toString(Integer.MAX_VALUE) + "0", -1);

		Assert.assertEquals(-1, result);

		// Smaller than minimum int

		result = GetterUtil.get(Integer.toString(Integer.MIN_VALUE) + "0", -1);

		Assert.assertEquals(-1, result);
	}

	@Test
	public void testGetLong() {

		// Wrong first char

		long result = GetterUtil.get("e123", -1L);

		Assert.assertEquals(-1L, result);

		// Wrong middle char

		result = GetterUtil.get("12e3", -1L);

		Assert.assertEquals(-1L, result);

		// Start with '+'

		result = GetterUtil.get("+123", -1L);

		Assert.assertEquals(123L, result);

		// Start with '-'

		result = GetterUtil.get("-123", -1L);

		Assert.assertEquals(-123L, result);

		// Maximum long

		result = GetterUtil.get(Long.toString(Long.MAX_VALUE), -1L);

		Assert.assertEquals(Long.MAX_VALUE, result);

		// Minimum long

		result = GetterUtil.get(Long.toString(Long.MIN_VALUE), -1L);

		Assert.assertEquals(Long.MIN_VALUE, result);

		// Larger than maximum long

		result = GetterUtil.get(Long.toString(Long.MAX_VALUE) + "0", -1L);

		Assert.assertEquals(-1L, result);

		// Smaller than minimum long

		result = GetterUtil.get(Long.toString(Long.MIN_VALUE) + "0", -1L);

		Assert.assertEquals(-1L, result);
	}

	@Test
	public void testGetShort() {

		// Wrong first char

		short result = GetterUtil.get("e123", (short)-1);

		Assert.assertEquals((short)-1, result);

		// Wrong middle char

		result = GetterUtil.get("12e3", (short)-1);

		Assert.assertEquals((short)-1, result);

		// Start with '+'

		result = GetterUtil.get("+123", (short)-1);

		Assert.assertEquals((short)123, result);

		// Start with '-'

		result = GetterUtil.get("-123", (short)-1);

		Assert.assertEquals((short)-123, result);

		// Maximum short

		result = GetterUtil.get(Short.toString(Short.MAX_VALUE), (short)-1);

		Assert.assertEquals(Short.MAX_VALUE, result);

		// Minimum short

		result = GetterUtil.get(Short.toString(Short.MIN_VALUE), (short)-1);

		Assert.assertEquals(Short.MIN_VALUE, result);

		// Larger than maximum short

		result = GetterUtil.get(
			String.valueOf(Short.MAX_VALUE) + "0", (short)-1);

		Assert.assertEquals((short)-1, result);

		// Smaller than minimum short

		result = GetterUtil.get(
			String.valueOf(Short.MIN_VALUE) + "0", (short)-1);

		Assert.assertEquals((short)-1, result);
	}

	@Test
	public void testGetString() {
		Assert.assertEquals(
			StringPool.BLANK,
			GetterUtil.getString(StringPool.BLANK, "default"));
		Assert.assertEquals(
			GetterUtil.DEFAULT_STRING, GetterUtil.getString(null));
		Assert.assertEquals("default", GetterUtil.getString(null, "default"));
		Assert.assertEquals(
			"default", GetterUtil.getString(new Object(), "default"));
		Assert.assertEquals("test", GetterUtil.getString("test"));
	}

}