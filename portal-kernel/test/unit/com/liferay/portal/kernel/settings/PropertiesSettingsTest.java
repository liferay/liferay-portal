/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import com.liferay.portal.kernel.test.ReflectionTestUtil;

import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Iván Zaera
 */
public class PropertiesSettingsTest {

	@Before
	public void setUp() {
		_propertiesSettings = new PropertiesSettings(
			new LocationVariableResolver(null, (SettingsLocatorHelper)null),
			new Properties() {
				{
					put(_MULTIPLE_KEY, _MULTIPLE_VALUES);
					put(_SINGLE_KEY, _SINGLE_VALUE);
				}
			});

		_properties = ReflectionTestUtil.getFieldValue(
			_propertiesSettings, "_properties");
	}

	@Test
	public void testGetValueWithDefaultValue() {
		Assert.assertEquals(
			"default", _propertiesSettings.getValue("missingKey", "default"));
	}

	@Test
	public void testGetValueWithExistingKey() {
		Assert.assertEquals(
			_SINGLE_VALUE, _propertiesSettings.getValue(_SINGLE_KEY, null));
	}

	@Test
	public void testGetValueWithMissingKey() {
		Assert.assertEquals(
			null, _propertiesSettings.getValue("missingKey", null));
	}

	@Test
	public void testGetValueWithResourceValue() {
		String expectedValue = "resourceValue";

		PropertiesSettings propertiesSettings = new PropertiesSettings(
			_createLocationVariableResolver(
				_RESOURCE_SINGLE_VALUE, expectedValue),
			new Properties() {
				{
					put(_SINGLE_KEY, _RESOURCE_SINGLE_VALUE);
				}
			});

		Assert.assertEquals(
			expectedValue, propertiesSettings.getValue(_SINGLE_KEY, null));
	}

	@Test
	public void testGetValuesWithDefaultValue() {
		String[] defaultValue = {"default0", "default1"};

		Assert.assertArrayEquals(
			defaultValue,
			_propertiesSettings.getValues("missingKey", defaultValue));
	}

	@Test
	public void testGetValuesWithExistingKey() {
		Assert.assertArrayEquals(
			new String[] {"value0", "value1", "value2"},
			_propertiesSettings.getValues(_MULTIPLE_KEY, null));
	}

	@Test
	public void testGetValuesWithMissingKey() {
		Assert.assertArrayEquals(
			null, _propertiesSettings.getValues("missingKey", null));
	}

	@Test
	public void testGetValuesWithResourceValue() {
		String expectedValue = "resourceValue0,resourceValue1,resourceValue2";

		PropertiesSettings propertiesSettings = new PropertiesSettings(
			_createLocationVariableResolver(
				_RESOURCE_MULTIPLE_VALUES, expectedValue),
			new Properties() {
				{
					put(_MULTIPLE_KEY, _RESOURCE_MULTIPLE_VALUES);
				}
			});

		Assert.assertArrayEquals(
			expectedValue.split(","),
			propertiesSettings.getValues(_MULTIPLE_KEY, null));
	}

	private LocationVariableResolver _createLocationVariableResolver(
		String resolveString, String expectedValue) {

		return new LocationVariableResolver(null, (SettingsLocatorHelper)null) {

			@Override
			public boolean isLocationVariable(String value) {
				return value.equals(resolveString);
			}

			@Override
			public String resolve(String value) {
				if (value.equals(resolveString)) {
					return expectedValue;
				}

				return null;
			}

		};
	}

	private static final String _MULTIPLE_KEY = "multipleKey";

	private static final String _MULTIPLE_VALUES = "value0,value1,value2";

	private static final String _RESOURCE_MULTIPLE_VALUES =
		"${resource:multiple.txt}";

	private static final String _RESOURCE_SINGLE_VALUE =
		"${resource:single.txt}";

	private static final String _SINGLE_KEY = "key";

	private static final String _SINGLE_VALUE = "value";

	private Map<String, String> _properties;
	private PropertiesSettings _propertiesSettings;

}