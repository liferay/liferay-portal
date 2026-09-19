/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Iván Zaera
 */
public class ParameterMapSettingsTest {

	@Before
	public void setUp() {
		_parameterMapSettings = new ParameterMapSettings(
			_parameterMap, _parentSettings);
	}

	@Test
	public void testGetValueWhenFoundInParameterMap() {
		_parameterMap.put("preferences--key--", new String[] {"requestValue"});

		_parentSettings.setValue("key", "settingsValue");

		Assert.assertEquals(
			"requestValue",
			_parameterMapSettings.getValue("key", "defaultValue"));
	}

	@Test
	public void testGetValueWhenFoundInParameterMapWithParameterNamePrefix() {
		_parameterMap.put("prefix--key", new String[] {"requestValue"});

		_parameterMapSettings.setParameterNamePrefix("prefix--");

		Assert.assertEquals(
			"requestValue",
			_parameterMapSettings.getValue("key", "defaultValue"));
	}

	@Test
	public void testGetValueWhenFoundInSettings() {
		_parentSettings.setValue("key", "settingsValue");

		Assert.assertEquals(
			"settingsValue",
			_parameterMapSettings.getValue("key", "defaultValue"));
	}

	@Test
	public void testGetValuesWhenFoundInParameterMap() {
		String[] values = {"requestValue1", "requestValue2"};

		_parameterMap.put("preferences--key--", values);

		Assert.assertArrayEquals(
			values,
			_parameterMapSettings.getValues(
				"key", new String[] {"defaultValue"}));
	}

	@Test
	public void testGetValuesWhenFoundInParameterMapWithParameterNamePrefix() {
		String[] values = {"requestValue1", "requestValue2"};

		_parameterMap.put("prefix--key", values);

		_parameterMapSettings.setParameterNamePrefix("prefix--");

		Assert.assertArrayEquals(
			values,
			_parameterMapSettings.getValues(
				"key", new String[] {"defaultValue"}));
	}

	@Test
	public void testGetValuesWhenFoundInSettings() {
		String[] values = {"settingsValue1", "settingsValue2"};

		_parentSettings.setValues("key", values);

		Assert.assertArrayEquals(
			values,
			_parameterMapSettings.getValues(
				"key", new String[] {"defaultValue"}));
	}

	private final Map<String, String[]> _parameterMap = new HashMap<>();
	private ParameterMapSettings _parameterMapSettings;
	private final MemorySettings _parentSettings = new MemorySettings();

}