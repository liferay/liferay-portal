/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.LocalizationImpl;

import java.util.function.Function;
import java.util.function.Supplier;

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
		_testGetDescription(
			languageId -> _objectDefinition.getDescription(languageId, false),
			() -> ObjectDefinitionUtil.getDescription(_objectDefinition));
	}

	@Test
	public void testGetDescriptionWithObjectField() {
		_testGetDescription(
			languageId -> _objectField.getDescription(languageId, false),
			() -> ObjectDefinitionUtil.getDescription(
				_objectDefinition, _objectField));
	}

	@Test
	public void testGetDescriptionWithObjectRelationship() {
		_testGetDescription(
			languageId -> _objectRelationship.getDescription(languageId, false),
			() -> ObjectDefinitionUtil.getDescription(
				_objectDefinition, _objectRelationship));
	}

	private void _testGetDescription(
		Function<String, String> descriptionFunction,
		Supplier<String> descriptionSupplier) {

		// No description

		Assert.assertNull(descriptionSupplier.get());

		Mockito.when(
			descriptionFunction.apply(_DEFAULT_LANGUAGE_ID)
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			descriptionFunction.apply(_ENGLISH_LANGUAGE_ID)
		).thenReturn(
			StringPool.BLANK
		);

		Assert.assertNull(descriptionSupplier.get());

		// With English translation

		String description = RandomTestUtil.randomString();

		Mockito.when(
			descriptionFunction.apply(_DEFAULT_LANGUAGE_ID)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			descriptionFunction.apply(_ENGLISH_LANGUAGE_ID)
		).thenReturn(
			description
		);

		Assert.assertEquals(description, descriptionSupplier.get());

		// Without English translation

		Mockito.when(
			descriptionFunction.apply(_DEFAULT_LANGUAGE_ID)
		).thenReturn(
			description
		);

		Mockito.when(
			descriptionFunction.apply(_ENGLISH_LANGUAGE_ID)
		).thenReturn(
			StringPool.BLANK
		);

		Assert.assertEquals(description, descriptionSupplier.get());
	}

	private static final String _DEFAULT_LANGUAGE_ID = "pt_BR";

	private static final String _ENGLISH_LANGUAGE_ID = "en_US";

	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ObjectField _objectField = Mockito.mock(ObjectField.class);
	private final ObjectRelationship _objectRelationship = Mockito.mock(
		ObjectRelationship.class);

}