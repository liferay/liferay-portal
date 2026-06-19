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
	public void testOnBeforeCreateWhenDetectionRegexIsInvalid()
		throws Exception {

		try {
			_listener.onBeforeCreate(_mockCustomObjectEntry("[", null));

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			Assert.assertTrue(
				modelListenerException.getMessage(),
				modelListenerException.getMessage(
				).contains(
					"\"detectionRegex\""
				));
		}
	}

	@Test
	public void testOnBeforeCreateWhenRegexesAreValid() throws Exception {
		_listener.onBeforeCreate(
			_mockCustomObjectEntry("\\w+", "(\\w+)@(\\w+)"));
	}

	@Test
	public void testOnBeforeCreateWhenReplacementRegexIsInvalid()
		throws Exception {

		try {
			_listener.onBeforeCreate(_mockCustomObjectEntry("\\w+", "["));

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			Assert.assertTrue(
				modelListenerException.getMessage(),
				modelListenerException.getMessage(
				).contains(
					"\"replacementRegex\""
				));
		}
	}

	@Test
	public void testOnBeforeUpdateWhenDetectionRegexIsInvalid()
		throws Exception {

		try {
			_listener.onBeforeUpdate(
				_mockCustomObjectEntry(null, null),
				_mockCustomObjectEntry("[", null));

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			Assert.assertTrue(
				modelListenerException.getMessage(),
				modelListenerException.getMessage(
				).contains(
					"\"detectionRegex\""
				));
		}
	}

	private ObjectEntry _mockCustomObjectEntry(
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