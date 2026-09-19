/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.InOrder;
import org.mockito.Mockito;

/**
 * @author Iván Zaera
 */
public class FallbackSettingsTest {

	public FallbackSettingsTest() {
		_settings = Mockito.mock(Settings.class);

		FallbackKeys fallbackKeys = new FallbackKeys();

		fallbackKeys.add("key1", "key2", "key3");
		fallbackKeys.add("key2", "key7");
		fallbackKeys.add("key3", "key5");

		_fallbackSettings = new FallbackSettings(_settings, fallbackKeys);
	}

	@Test
	public void testGetValueWhenConfigured() {
		Mockito.when(
			_settings.getValue("key2", null)
		).thenReturn(
			"value"
		);

		Assert.assertEquals(
			"value", _fallbackSettings.getValue("key1", "default"));

		verifyGetValue("key1", "key2");
	}

	@Test
	public void testGetValueWhenUnconfigured() {
		Assert.assertEquals(
			"default", _fallbackSettings.getValue("key1", "default"));

		verifyGetValue("key1", "key2", "key3");
	}

	@Test
	public void testGetValuesWhenConfigured() {
		String[] defaultValues = {"default"};

		String[] mockValues = {"value"};

		Mockito.when(
			_settings.getValues("key2", null)
		).thenReturn(
			mockValues
		);

		Assert.assertArrayEquals(
			mockValues, _fallbackSettings.getValues("key1", defaultValues));

		verifyGetValues("key1", "key2");
	}

	@Test
	public void testGetValuesWhenUnconfigured() {
		String[] defaultValues = {"default"};

		Assert.assertArrayEquals(
			defaultValues, _fallbackSettings.getValues("key1", defaultValues));

		verifyGetValues("key1", "key2", "key3");
	}

	protected void verifyGetValue(String... keys) {
		InOrder inOrder = Mockito.inOrder(_settings);

		for (String key : keys) {
			inOrder.verify(
				_settings
			).getValue(
				key, null
			);
		}
	}

	protected void verifyGetValues(String... keys) {
		InOrder inOrder = Mockito.inOrder(_settings);

		for (String key : keys) {
			inOrder.verify(
				_settings
			).getValues(
				key, null
			);
		}
	}

	private final FallbackSettings _fallbackSettings;
	private final Settings _settings;

}