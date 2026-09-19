/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Iván Zaera
 */
public class ConfigurationBeanSettingsTest {

	@Before
	public void setUp() {
		_configurationBean = new ConfigurationBean();

		_locationVariableResolver = new LocationVariableResolver(
			null, (SettingsLocatorHelper)null);

		_configurationBeanSettings = new ConfigurationBeanSettings(
			_locationVariableResolver, _configurationBean, null);
	}

	@Test
	public void testGetValueWithExistingBooleanValue() {
		Assert.assertEquals(
			String.valueOf(_configurationBean.booleanValue()),
			_configurationBeanSettings.getValue(
				"booleanValue", "defaultValue"));
	}

	@Test
	public void testGetValueWithExistingLocalizedValuesMapValue() {
		LocalizedValuesMap localizedValuesMap =
			_configurationBean.localizedValuesMap();

		Assert.assertEquals(
			localizedValuesMap.getDefaultValue(),
			_configurationBeanSettings.getValue("localizedValuesMap", null));
	}

	@Test
	public void testGetValueWithExistingStringValue() {
		Assert.assertEquals(
			_configurationBean.stringValue(),
			_configurationBeanSettings.getValue("stringValue", "defaultValue"));
	}

	@Test
	public void testGetValueWithLocationVariable() {
		String expectedValue = "Once upon a time...";

		LocationVariableResolver locationVariableResolver =
			new LocationVariableResolver(null, (SettingsLocatorHelper)null) {

				@Override
				public boolean isLocationVariable(String value) {
					return value.equals(
						_configurationBean.locationVariableValue());
				}

				@Override
				public String resolve(String value) {
					if (value.equals(
							_configurationBean.locationVariableValue())) {

						return expectedValue;
					}

					return null;
				}

			};

		ConfigurationBeanSettings configurationBeanSettings =
			new ConfigurationBeanSettings(
				locationVariableResolver, _configurationBean, null);

		Assert.assertEquals(
			expectedValue,
			configurationBeanSettings.getValue(
				"locationVariableValue", "defaultValue"));
	}

	@Test
	public void testGetValueWithMissingKey() {
		Assert.assertEquals(
			"defaultValue",
			_configurationBeanSettings.getValue("missingKey", "defaultValue"));
	}

	@Test
	public void testGetValueWithNullConfigurationBean() {
		try {
			_configurationBeanSettings = new ConfigurationBeanSettings(
				_locationVariableResolver, null, null);

			Assert.fail();
		}
		catch (NullPointerException nullPointerException) {
			Assert.assertEquals(
				"Configuration bean is null",
				nullPointerException.getMessage());
		}
	}

	@Test
	public void testGetValueWithNullLocationVariableResolver() {
		try {
			_configurationBeanSettings = new ConfigurationBeanSettings(
				null, null, null);

			Assert.fail();
		}
		catch (NullPointerException nullPointerException) {
			Assert.assertEquals(
				"Location variable resolver is null",
				nullPointerException.getMessage());
		}
	}

	@Test
	public void testGetValuesWithExistingKey() {
		Assert.assertArrayEquals(
			_configurationBean.stringArrayValue(),
			_configurationBeanSettings.getValues(
				"stringArrayValue", new String[] {"defaultValue"}));
	}

	@Test
	public void testGetValuesWithMissingKey() {
		String[] defaultValue = {"defaultValue"};

		Assert.assertArrayEquals(
			defaultValue,
			_configurationBeanSettings.getValues("missingKey", defaultValue));
	}

	private ConfigurationBean _configurationBean;
	private ConfigurationBeanSettings _configurationBeanSettings;
	private LocationVariableResolver _locationVariableResolver;

	private static class ConfigurationBean {

		public boolean booleanValue() {
			return true;
		}

		public LocalizedValuesMap localizedValuesMap() {
			return new LocalizedValuesMap("default localized value");
		}

		public String locationVariableValue() {
			return "${resource:template.ftl}";
		}

		public String[] stringArrayValue() {
			return new String[] {
				"string value 0", "string value 1", "string value 2"
			};
		}

		public String stringValue() {
			return "a string value";
		}

	}

}