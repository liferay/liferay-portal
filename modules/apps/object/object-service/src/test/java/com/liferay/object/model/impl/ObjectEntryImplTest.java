/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.bag.ObjectFieldBag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mikel Lorza
 */
public class ObjectEntryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetTitleValueWithNullValue() throws Exception {
		ObjectEntryImpl objectEntryImpl = Mockito.spy(new ObjectEntryImpl());

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getTitleObjectFieldId()
		).thenReturn(
			1L
		);

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getName()
		).thenReturn(
			"title"
		);

		ObjectFieldBag objectFieldBag = Mockito.mock(ObjectFieldBag.class);

		Mockito.when(
			objectFieldBag.getObjectField(1L)
		).thenReturn(
			objectField
		);

		Mockito.when(
			objectDefinition.getObjectFieldBag()
		).thenReturn(
			objectFieldBag
		);

		Mockito.doReturn(
			objectDefinition
		).when(
			objectEntryImpl
		).getObjectDefinition();

		Map<String, Serializable> indexedValues = new HashMap<>();

		indexedValues.put("title", null);

		Mockito.doReturn(
			indexedValues
		).when(
			objectEntryImpl
		).getIndexedValues();

		Mockito.doReturn(
			"en_US"
		).when(
			objectEntryImpl
		).getDefaultLanguageId();

		Assert.assertNull(objectEntryImpl.getTitleValue("en_US", true));
	}

}