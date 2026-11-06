/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.date;

import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;

import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Bruno Basto
 */
public class DateDDMFormFieldValueRendererTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		setUpFastDateFormatFactoryUtil();

		ReflectionTestUtil.setFieldValue(
			LocaleThreadLocal.class, "_themeDisplayLocale",
			_themeDisplayLocale);
	}

	@After
	public void tearDown() {
		_themeDisplayLocale.remove();
	}

	@Test
	public void testRenderDisplayLocaleBrazil() {
		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.BRAZIL);

		_assertRenderValues(
			_getSingleValueExpectedValuesMap("25/01/2015"), "2015-01-25");
		_assertRenderValues(
			_getSingleValueExpectedValuesMap("25/01/2015 01:00"),
			"2015-01-25 1:00");
	}

	@Test
	public void testRenderDisplayLocaleNull() {
		_assertRenderValues(
			HashMapBuilder.put(
				new Locale.Builder(
				).setLanguage(
					"ar"
				).setRegion(
					"SA"
				).setExtension(
					Locale.UNICODE_LOCALE_EXTENSION, "nu-arab"
				).build(),
				"٢٥\u200F/٠١\u200F/٢٠١٥"
			).put(
				LocaleUtil.BRAZIL, "25/01/2015"
			).put(
				new Locale("ca", "ES"), "25/01/2015"
			).put(
				new Locale("fi", "FI"), "25.01.2015"
			).put(
				LocaleUtil.FRANCE, "25/01/2015"
			).put(
				LocaleUtil.GERMANY, "25.01.2015"
			).put(
				LocaleUtil.HUNGARY, "2015. 01. 25."
			).put(
				LocaleUtil.JAPAN, "2015/01/25"
			).put(
				LocaleUtil.NETHERLANDS, "25-01-2015"
			).put(
				LocaleUtil.SIMPLIFIED_CHINESE, "2015/01/25"
			).put(
				LocaleUtil.SPAIN, "25/01/2015"
			).put(
				new Locale("sv", "SE"), "2015-01-25"
			).put(
				LocaleUtil.US, "01/25/2015"
			).build(),
			"2015-01-25");
		_assertRenderValues(
			HashMapBuilder.put(
				new Locale.Builder(
				).setLanguage(
					"ar"
				).setRegion(
					"SA"
				).setExtension(
					Locale.UNICODE_LOCALE_EXTENSION, "nu-arab"
				).build(),
				"٢٥\u200F/٠١\u200F/٢٠١٥\u060C ٠١:٠٠ ص"
			).put(
				LocaleUtil.BRAZIL, "25/01/2015 01:00"
			).put(
				new Locale("ca", "ES"), "25/01/2015 01:00"
			).put(
				new Locale("fi", "FI"), "25.01.2015 01.00"
			).put(
				LocaleUtil.FRANCE, "25/01/2015 01:00"
			).put(
				LocaleUtil.GERMANY, "25.01.2015, 01:00"
			).put(
				LocaleUtil.HUNGARY, "2015. 01. 25. 01:00"
			).put(
				LocaleUtil.JAPAN, "2015/01/25 01:00"
			).put(
				LocaleUtil.NETHERLANDS, "25-01-2015 01:00"
			).put(
				LocaleUtil.SIMPLIFIED_CHINESE, "2015/01/25 01:00"
			).put(
				LocaleUtil.SPAIN, "25/01/2015, 01:00"
			).put(
				new Locale("sv", "SE"), "2015-01-25 01:00"
			).put(
				LocaleUtil.US, "01/25/2015, 01:00\u202FAM"
			).build(),
			"2015-01-25 1:00");
		_assertRenderValues(
			HashMapBuilder.put(
				LocaleUtil.US, StringPool.BLANK
			).build(),
			"");
	}

	@Test
	public void testRenderDisplayLocaleUS() {
		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.US);

		_assertRenderValues(
			_getSingleValueExpectedValuesMap("01/25/2015"), "2015-01-25");
		_assertRenderValues(
			_getSingleValueExpectedValuesMap("01/25/2015, 01:00\u202FAM"),
			"2015-01-25 1:00");
	}

	protected static void setUpFastDateFormatFactoryUtil() {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());
	}

	private void _assertRenderValues(
		Map<Locale, String> expectedValuesMap, String inputValue) {

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"birthday", new UnlocalizedValue(inputValue));

		for (Map.Entry<Locale, String> entry : expectedValuesMap.entrySet()) {
			Assert.assertEquals(
				"locale " + entry.getKey(), entry.getValue(),
				_dateDDMFormFieldValueRenderer.render(
					ddmFormFieldValue, entry.getKey()));
		}
	}

	private Map<Locale, String> _getSingleValueExpectedValuesMap(
		String expectedValue) {

		return HashMapBuilder.put(
			new Locale.Builder(
			).setLanguage(
				"ar"
			).setRegion(
				"SA"
			).setExtension(
				Locale.UNICODE_LOCALE_EXTENSION, "nu-arab"
			).build(),
			expectedValue
		).put(
			LocaleUtil.BRAZIL, expectedValue
		).put(
			new Locale("ca", "ES"), expectedValue
		).put(
			new Locale("fi", "FI"), expectedValue
		).put(
			LocaleUtil.FRANCE, expectedValue
		).put(
			LocaleUtil.GERMANY, expectedValue
		).put(
			LocaleUtil.HUNGARY, expectedValue
		).put(
			LocaleUtil.JAPAN, expectedValue
		).put(
			LocaleUtil.NETHERLANDS, expectedValue
		).put(
			LocaleUtil.SIMPLIFIED_CHINESE, expectedValue
		).put(
			LocaleUtil.SPAIN, expectedValue
		).put(
			new Locale("sv", "SE"), expectedValue
		).put(
			LocaleUtil.US, expectedValue
		).build();
	}

	private static final ThreadLocal<Locale> _themeDisplayLocale =
		new CentralizedThreadLocal<>(
			LocaleThreadLocal.class + "._themeDisplayLocale");

	private final DateDDMFormFieldValueRenderer _dateDDMFormFieldValueRenderer =
		new DateDDMFormFieldValueRenderer();

}