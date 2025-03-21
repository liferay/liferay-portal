/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Iván Zaera
 */
public class PortletPreferencesSettingsTest {

	@Before
	public void setUp() {
		_portletPreferences = Mockito.mock(PortletPreferences.class);

		Mockito.when(
			_portletPreferences.getValue(
				Mockito.eq(_PORTLET_PREFERENCES_SINGLE_KEY),
				Mockito.nullable(String.class))
		).thenReturn(
			_PORTLET_PREFERENCES_SINGLE_VALUE
		);

		Mockito.when(
			_portletPreferences.getValues(
				Mockito.eq(_PORTLET_PREFERENCES_MULTIPLE_KEY), Mockito.any())
		).thenReturn(
			_PORTLET_PREFERENCES_MULTIPLE_VALUES
		);

		ModifiableSettings modifiableSettings = new MemorySettings();

		modifiableSettings.setValue(
			_DEFAULT_SETTINGS_SINGLE_KEY, _DEFAULT_SETTINGS_SINGLE_VALUE);
		modifiableSettings.setValues(
			_DEFAULT_SETTINGS_MULTIPLE_KEY, _DEFAULT_SETTINGS_MULTIPLE_VALUES);

		_portletPreferencesSettings = new PortletPreferencesSettings(
			_portletPreferences, modifiableSettings);
	}

	@Test
	public void testGetValuesWithExistingDefaultSettingsKey() {
		Assert.assertArrayEquals(
			_DEFAULT_SETTINGS_MULTIPLE_VALUES,
			_portletPreferencesSettings.getValues(
				_DEFAULT_SETTINGS_MULTIPLE_KEY, null));
	}

	@Test
	public void testGetValuesWithExistingPortletPreferencesKey() {
		Assert.assertArrayEquals(
			_PORTLET_PREFERENCES_MULTIPLE_VALUES,
			_portletPreferencesSettings.getValues(
				_PORTLET_PREFERENCES_MULTIPLE_KEY, null));
	}

	@Test
	public void testGetValuesWithMissingKey() {
		String[] defaultValue = {"a", "b"};

		Assert.assertArrayEquals(
			defaultValue,
			_portletPreferencesSettings.getValues("missingKeys", defaultValue));
	}

	@Test
	public void testGetValueWithExistingDefaultSettingsKey() {
		Assert.assertEquals(
			_DEFAULT_SETTINGS_SINGLE_VALUE,
			_portletPreferencesSettings.getValue(
				_DEFAULT_SETTINGS_SINGLE_KEY, null));
	}

	@Test
	public void testGetValueWithExistingPortletPreferencesKey() {
		Assert.assertEquals(
			_PORTLET_PREFERENCES_SINGLE_VALUE,
			_portletPreferencesSettings.getValue(
				_PORTLET_PREFERENCES_SINGLE_KEY, null));
	}

	@Test
	public void testGetValueWithMissingKey() {
		Assert.assertEquals(
			"default",
			_portletPreferencesSettings.getValue("missingKey", "default"));
	}

	@Test
	public void testSetValueSetsPropertyInPortletPreferences()
		throws Exception {

		_portletPreferencesSettings.setValue("key", "value");

		Mockito.verify(
			_portletPreferences
		).setValue(
			"key", "value"
		);
	}

	@Test
	public void testSetValuesSetsPropertyInPortletPreferences()
		throws Exception {

		String[] values = {"a", "b"};

		_portletPreferencesSettings.setValues("key", values);

		Mockito.verify(
			_portletPreferences
		).setValues(
			"key", values
		);
	}

	@Test
	public void testStoreIsPerformedOnPortletPreferences() throws Exception {
		_portletPreferencesSettings.store();

		Mockito.verify(
			_portletPreferences
		).store();
	}

	private static final String _DEFAULT_SETTINGS_MULTIPLE_KEY = "defaultKeys";

	private static final String[] _DEFAULT_SETTINGS_MULTIPLE_VALUES = {
		"defaultValue0", "defaultValue1"
	};

	private static final String _DEFAULT_SETTINGS_SINGLE_KEY = "defaultKey";

	private static final String _DEFAULT_SETTINGS_SINGLE_VALUE = "defaultValue";

	private static final String _PORTLET_PREFERENCES_MULTIPLE_KEY =
		"portletKeys";

	private static final String[] _PORTLET_PREFERENCES_MULTIPLE_VALUES = {
		"portletValue0", "portletValue1"
	};

	private static final String _PORTLET_PREFERENCES_SINGLE_KEY = "portletKey";

	private static final String _PORTLET_PREFERENCES_SINGLE_VALUE =
		"portletValue";

	private PortletPreferences _portletPreferences;
	private PortletPreferencesSettings _portletPreferencesSettings;

}