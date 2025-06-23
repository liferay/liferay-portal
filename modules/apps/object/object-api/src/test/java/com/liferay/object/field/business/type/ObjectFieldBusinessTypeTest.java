/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.field.business.type;

import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Carlos Correa
 */
public class ObjectFieldBusinessTypeTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetLocalizedValues() throws Exception {

		// Empty map value

		String i18nObjectFieldName = "x" + RandomTestUtil.randomString();

		Mockito.when(
			_objectField.getI18nObjectFieldName()
		).thenReturn(
			i18nObjectFieldName
		);

		TestObjectFieldBusinessType testObjectFieldBusinessType =
			new TestObjectFieldBusinessType();

		Assert.assertEquals(
			new HashMap<>(),
			testObjectFieldBusinessType.getLocalizedValues(
				_objectField, RandomTestUtil.randomLong(),
				Collections.singletonMap(
					i18nObjectFieldName, new HashMap<>())));

		// Invalid value

		try {
			testObjectFieldBusinessType.getLocalizedValues(
				_objectField, RandomTestUtil.randomLong(),
				Collections.singletonMap(
					i18nObjectFieldName, RandomTestUtil.randomLong()));

			Assert.fail();
		}
		catch (ObjectEntryValuesException.InvalidValue
					objectEntryValuesException) {

			Assert.assertEquals(
				i18nObjectFieldName,
				objectEntryValuesException.getObjectFieldName());
			Assert.assertEquals(
				"The value is invalid for object field \"" +
					i18nObjectFieldName + "\"",
				objectEntryValuesException.getMessage());
		}

		// Missing value

		Assert.assertNull(
			testObjectFieldBusinessType.getLocalizedValues(
				_objectField, RandomTestUtil.randomLong(), new HashMap<>()));

		// Null value

		Assert.assertNull(
			testObjectFieldBusinessType.getLocalizedValues(
				_objectField, RandomTestUtil.randomLong(),
				Collections.singletonMap(i18nObjectFieldName, null)));
	}

	@Test
	public void testGetValue() throws Exception {

		// Localized false

		String fieldName = "x" + RandomTestUtil.randomString();
		String i18nObjectFieldName = "x" + RandomTestUtil.randomString();

		Mockito.when(
			_objectField.getName()
		).thenReturn(
			fieldName
		);

		Mockito.when(
			_objectField.getI18nObjectFieldName()
		).thenReturn(
			i18nObjectFieldName
		);

		TestObjectFieldBusinessType testObjectFieldBusinessType =
			new TestObjectFieldBusinessType();

		String fieldValue1 = "x" + RandomTestUtil.randomString();
		String fieldValue2 = "x" + RandomTestUtil.randomString();
		String fieldValue3 = "x" + RandomTestUtil.randomString();

		Mockito.when(
			_objectField.isLocalized()
		).thenReturn(
			false
		);

		Assert.assertEquals(
			fieldValue1,
			testObjectFieldBusinessType.getValue(
				null, _objectField, RandomTestUtil.randomLong(),
				HashMapBuilder.<String, Object>put(
					fieldName, fieldValue1
				).put(
					i18nObjectFieldName,
					HashMapBuilder.put(
						"en_US", fieldValue2
					).put(
						"es_ES", fieldValue3
					).build()
				).build()));

		// Localized true and invalid value

		Mockito.when(
			_objectField.isLocalized()
		).thenReturn(
			true
		);

		try {
			testObjectFieldBusinessType.getValue(
				null, _objectField, RandomTestUtil.randomLong(),
				HashMapBuilder.<String, Object>put(
					fieldName, fieldValue1
				).put(
					i18nObjectFieldName, RandomTestUtil.randomLong()
				).build());

			Assert.fail();
		}
		catch (ObjectEntryValuesException.InvalidValue
					objectEntryValuesException) {

			Assert.assertEquals(
				i18nObjectFieldName,
				objectEntryValuesException.getObjectFieldName());
			Assert.assertEquals(
				"The value is invalid for object field \"" +
					i18nObjectFieldName + "\"",
				objectEntryValuesException.getMessage());
		}

		// Localized true and null value

		Mockito.when(
			_objectField.isLocalized()
		).thenReturn(
			true
		);

		Assert.assertEquals(
			fieldValue1,
			testObjectFieldBusinessType.getValue(
				null, _objectField, RandomTestUtil.randomLong(),
				HashMapBuilder.<String, Object>put(
					fieldName, fieldValue1
				).build()));

		// Localized true and valid value

		Mockito.when(
			_objectField.isLocalized()
		).thenReturn(
			true
		);

		Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(new Locale("es", "ES"));

		try {
			Assert.assertEquals(
				fieldValue3,
				testObjectFieldBusinessType.getValue(
					null, _objectField, RandomTestUtil.randomLong(),
					HashMapBuilder.<String, Object>put(
						fieldName, fieldValue1
					).put(
						i18nObjectFieldName,
						HashMapBuilder.put(
							"en_US", fieldValue2
						).put(
							"es_ES", fieldValue3
						).build()
					).build()));

			Assert.assertEquals(
				"",
				testObjectFieldBusinessType.getValue(
					null, _objectField, RandomTestUtil.randomLong(),
					HashMapBuilder.<String, Object>put(
						fieldName, fieldValue1
					).put(
						i18nObjectFieldName,
						HashMapBuilder.put(
							"en_US", fieldValue2
						).build()
					).build()));
		}
		finally {
			LocaleThreadLocal.setThemeDisplayLocale(locale);
		}
	}

	@Mock
	private ObjectField _objectField;

	private class TestObjectFieldBusinessType
		implements ObjectFieldBusinessType {

		@Override
		public String getDBType() {
			return "";
		}

		@Override
		public String getDDMFormFieldTypeName() {
			return "";
		}

		@Override
		public String getLabel(Locale locale) {
			return "";
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		public PropertyDefinition.PropertyType getPropertyType() {
			return null;
		}

	}

}