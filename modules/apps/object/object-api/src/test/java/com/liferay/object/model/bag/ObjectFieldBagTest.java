/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.bag;

import com.liferay.object.model.ObjectField;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Joshua Cords
 */
public class ObjectFieldBagTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetNestedIndexedObjectFieldsWithCustomObjectDefinition() {
		ObjectFieldBag objectFieldBag = new ObjectFieldBag(
			false,
			Arrays.asList(
				_metadataObjectField, _nonindexedObjectField,
				_nonsystemObjectField, _systemObjectField));

		Assert.assertEquals(
			Arrays.asList(_nonsystemObjectField),
			objectFieldBag.getNestedIndexedObjectFields());
	}

	@Test
	public void testGetNestedIndexedObjectFieldsWithModifiableSystemObjectDefinition() {
		ObjectFieldBag objectFieldBag = new ObjectFieldBag(
			true,
			Arrays.asList(
				_metadataObjectField, _nonindexedObjectField,
				_nonsystemObjectField, _systemObjectField));

		Assert.assertEquals(
			Arrays.asList(_nonsystemObjectField, _systemObjectField),
			objectFieldBag.getNestedIndexedObjectFields());
	}

	private ObjectField _mockObjectField(
		boolean indexed, boolean metadata, boolean system) {

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.isIndexed()
		).thenReturn(
			indexed
		);

		Mockito.when(
			objectField.isMetadata()
		).thenReturn(
			metadata
		);

		Mockito.when(
			objectField.isSystem()
		).thenReturn(
			system
		);

		return objectField;
	}

	private final ObjectField _metadataObjectField = _mockObjectField(
		true, true, true);
	private final ObjectField _nonindexedObjectField = _mockObjectField(
		false, false, false);
	private final ObjectField _nonsystemObjectField = _mockObjectField(
		true, false, false);
	private final ObjectField _systemObjectField = _mockObjectField(
		true, false, true);

}