/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.internal.model.listener;

import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jose Luis Navarro
 */
public class DataMaskRelevantObjectEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testOnBeforeCreateInvalidDetectionRegex() {
		try {
			_listener.onBeforeCreate(_mockDataMaskObjectEntry("[", null));

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			String message = modelListenerException.getMessage();

			Assert.assertTrue(message, message.contains("\"detectionRegex\""));
		}
	}

	@Test
	public void testOnBeforeCreateInvalidReplacementRegex() {
		try {
			_listener.onBeforeCreate(_mockDataMaskObjectEntry("\\w+", "["));

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			String message = modelListenerException.getMessage();

			Assert.assertTrue(
				message, message.contains("\"replacementRegex\""));
		}
	}

	@Test
	public void testOnBeforeCreateValidRegexes() throws Exception {
		_listener.onBeforeCreate(
			_mockDataMaskObjectEntry("\\w+", "(\\w+)@(\\w+)"));
	}

	@Test
	public void testOnBeforeUpdateInvalidDetectionRegex() {
		try {
			_listener.onBeforeUpdate(
				_mockDataMaskObjectEntry(null, null),
				_mockDataMaskObjectEntry("[", null));

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			String message = modelListenerException.getMessage();

			Assert.assertTrue(message, message.contains("\"detectionRegex\""));
		}
	}

	private ObjectEntry _mockDataMaskObjectEntry(
		String detectionRegex, String replacementRegex) {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"detectionRegex", detectionRegex
			).put(
				"maskType", "custom"
			).put(
				"replacementRegex", replacementRegex
			).build()
		);

		return objectEntry;
	}

	private final DataMaskRelevantObjectEntryModelListener _listener =
		new DataMaskRelevantObjectEntryModelListener();

}