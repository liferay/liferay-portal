/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.LocalizationImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Nathaly Gomes
 * @author Nícolas Moura
 */
public class ObjectDefinitionUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		LocalizationUtil localizationUtil = new LocalizationUtil();

		localizationUtil.setLocalization(new LocalizationImpl());

		Mockito.when(
			_objectDefinition.getDefaultLanguageId()
		).thenReturn(
			_DEFAULT_LANGUAGE_ID
		);
	}

	@Test
	public void testGetDescriptionWithObjectDefinition() {

		// No description

		Assert.assertNull(
			ObjectDefinitionUtil.getDescription(_objectDefinition));

		Mockito.when(
			_objectDefinition.getDescription(_DEFAULT_LANGUAGE_ID, false)
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			_objectDefinition.getDescription(_ENGLISH_LANGUAGE_ID, false)
		).thenReturn(
			StringPool.BLANK
		);

		Assert.assertNull(
			ObjectDefinitionUtil.getDescription(_objectDefinition));

		// With English translation

		String description = RandomTestUtil.randomString();

		Mockito.when(
			_objectDefinition.getDescription(_DEFAULT_LANGUAGE_ID, false)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_objectDefinition.getDescription(_ENGLISH_LANGUAGE_ID, false)
		).thenReturn(
			description
		);

		Assert.assertEquals(
			description,
			ObjectDefinitionUtil.getDescription(_objectDefinition));

		// Without English translation

		Mockito.when(
			_objectDefinition.getDescription(_DEFAULT_LANGUAGE_ID, false)
		).thenReturn(
			description
		);

		Mockito.when(
			_objectDefinition.getDescription(_ENGLISH_LANGUAGE_ID, false)
		).thenReturn(
			StringPool.BLANK
		);

		Assert.assertEquals(
			description,
			ObjectDefinitionUtil.getDescription(_objectDefinition));
	}

	@Test
	public void testGetDescriptionWithObjectField() {

		// No description

		Assert.assertNull(
			ObjectDefinitionUtil.getDescription(
				_objectDefinition, _objectField));

		Mockito.when(
			_objectField.getDescription(_DEFAULT_LANGUAGE_ID, false)
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			_objectField.getDescription(_ENGLISH_LANGUAGE_ID, false)
		).thenReturn(
			StringPool.BLANK
		);

		Assert.assertNull(
			ObjectDefinitionUtil.getDescription(
				_objectDefinition, _objectField));

		// With English translation

		String description = RandomTestUtil.randomString();

		Mockito.when(
			_objectField.getDescription(_DEFAULT_LANGUAGE_ID, false)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_objectField.getDescription(_ENGLISH_LANGUAGE_ID, false)
		).thenReturn(
			description
		);

		Assert.assertEquals(
			description,
			ObjectDefinitionUtil.getDescription(
				_objectDefinition, _objectField));

		// Without English translation

		Mockito.when(
			_objectField.getDescription(_DEFAULT_LANGUAGE_ID, false)
		).thenReturn(
			description
		);

		Mockito.when(
			_objectField.getDescription(_ENGLISH_LANGUAGE_ID, false)
		).thenReturn(
			StringPool.BLANK
		);

		Assert.assertEquals(
			description,
			ObjectDefinitionUtil.getDescription(
				_objectDefinition, _objectField));
	}

	private static final String _DEFAULT_LANGUAGE_ID = "pt_BR";

	private static final String _ENGLISH_LANGUAGE_ID = "en_US";

	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ObjectField _objectField = Mockito.mock(ObjectField.class);

}