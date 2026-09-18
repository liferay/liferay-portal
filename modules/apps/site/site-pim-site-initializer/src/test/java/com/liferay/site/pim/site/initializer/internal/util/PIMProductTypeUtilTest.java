/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.util;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
 */
public class PIMProductTypeUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetObjectDefinitions() {
		ObjectFolder objectFolder = _mockObjectFolder();

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			_objectDefinitionLocalService.getObjectFolderObjectDefinitions(
				objectFolder.getObjectFolderId())
		).thenReturn(
			Collections.singletonList(objectDefinition)
		);

		Assert.assertEquals(
			Collections.singletonList(objectDefinition),
			PIMProductTypeUtil.getObjectDefinitions(
				_COMPANY_ID, _objectDefinitionLocalService,
				_objectFolderLocalService));
	}

	@Test
	public void testGetObjectDefinitionsWithMissingObjectFolder() {
		Mockito.when(
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES,
				_COMPANY_ID)
		).thenReturn(
			null
		);

		List<ObjectDefinition> objectDefinitions =
			PIMProductTypeUtil.getObjectDefinitions(
				_COMPANY_ID, _objectDefinitionLocalService,
				_objectFolderLocalService);

		Assert.assertTrue(
			objectDefinitions.toString(), objectDefinitions.isEmpty());
	}

	@Test
	public void testGetObjectFields() {
		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getObjectDefinitionId()
		).thenReturn(
			_OBJECT_DEFINITION_ID
		);

		List<ObjectField> objectFields = Arrays.asList(
			_mockObjectField(
				"code", ObjectFieldConstants.BUSINESS_TYPE_TEXT, false),
			_mockObjectField(
				"createDate", ObjectFieldConstants.BUSINESS_TYPE_DATE, false),
			_mockObjectField(
				"modifiedDate", ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
				false),
			_mockObjectField(
				"status", ObjectFieldConstants.BUSINESS_TYPE_TEXT, true));

		Mockito.when(
			_objectFieldLocalService.getObjectFields(_OBJECT_DEFINITION_ID)
		).thenReturn(
			objectFields
		);

		Assert.assertEquals(
			Collections.singletonList("code"),
			TransformUtil.transform(
				PIMProductTypeUtil.getObjectFields(
					objectDefinition, _objectFieldLocalService),
				ObjectField::getName));
	}

	@Test
	public void testGetObjectFieldsWithoutObjectFields() {
		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId())
		).thenReturn(
			ListUtil.fromArray(new ObjectField[0])
		);

		List<ObjectField> objectFields = PIMProductTypeUtil.getObjectFields(
			objectDefinition, _objectFieldLocalService);

		Assert.assertTrue(objectFields.toString(), objectFields.isEmpty());
	}

	private ObjectField _mockObjectField(
		String name, String businessType, boolean readOnly) {

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getBusinessType()
		).thenReturn(
			businessType
		);

		Mockito.when(
			objectField.getName()
		).thenReturn(
			name
		);

		Mockito.when(
			objectField.getReadOnly()
		).thenReturn(
			String.valueOf(readOnly)
		);

		return objectField;
	}

	private ObjectFolder _mockObjectFolder() {
		ObjectFolder objectFolder = Mockito.mock(ObjectFolder.class);

		Mockito.when(
			objectFolder.getObjectFolderId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES,
				_COMPANY_ID)
		).thenReturn(
			objectFolder
		);

		return objectFolder;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectFieldLocalService _objectFieldLocalService =
		Mockito.mock(ObjectFieldLocalService.class);
	private final ObjectFolderLocalService _objectFolderLocalService =
		Mockito.mock(ObjectFolderLocalService.class);

}