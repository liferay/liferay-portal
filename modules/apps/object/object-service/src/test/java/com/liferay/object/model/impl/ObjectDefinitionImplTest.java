/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Magdalena Jedraszak
 */
public class ObjectDefinitionImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetPortletId() {
		ObjectDefinition objectDefinition = new ObjectDefinitionImpl();

		String classNameSuffix = RandomTestUtil.randomString();

		objectDefinition.setClassName(
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION + classNameSuffix);

		Assert.assertEquals(
			ObjectPortletKeys.OBJECT_DEFINITIONS + StringPool.UNDERLINE +
				classNameSuffix,
			objectDefinition.getPortletId());
	}

	@Test
	public void testGetRESTContextPath() {

		// Modifiable custom object definition

		_testGetRESTContextPath("/c/customobjects", "CustomObject", false);

		// Modifiable system object definition

		_testGetRESTContextPath(
			"/headless-builder/endpoints", "APIEndpoint", true);

		// Unmodifiable system object definition

		try {
			ObjectDefinition objectDefinition = _createObjectDefinition(
				false, "AccountEntry", true);

			objectDefinition.getRESTContextPath();

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			Assert.assertNotNull(unsupportedOperationException);
		}
	}

	private ObjectDefinition _createObjectDefinition(
		boolean modifiable, String name, boolean system) {

		ObjectDefinition objectDefinition = new ObjectDefinitionImpl();

		objectDefinition.setModifiable(modifiable);
		objectDefinition.setName(name);
		objectDefinition.setPluralLabel(
			TextFormatter.formatPlural(StringUtil.lowerCaseFirstLetter(name)));
		objectDefinition.setSystem(system);

		return objectDefinition;
	}

	private void _testGetRESTContextPath(
		String expectedRESTContextPath, String name, boolean system) {

		ObjectDefinition objectDefinition = Mockito.spy(
			_createObjectDefinition(true, name, system));

		Assert.assertEquals(
			expectedRESTContextPath, objectDefinition.getRESTContextPath());
	}

}