/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.contributor;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class ProductTypesCMSStructureObjectFolderContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetLabel() {
		Assert.assertEquals(
			"product",
			_productTypesCMSStructureObjectFolderContributor.getLabel());
	}

	@Test
	public void testGetObjectFolderExternalReferenceCode() {
		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				false
			);

			Assert.assertNull(
				_productTypesCMSStructureObjectFolderContributor.
					getObjectFolderExternalReferenceCode());

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				true
			);

			Assert.assertEquals(
				PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES,
				_productTypesCMSStructureObjectFolderContributor.
					getObjectFolderExternalReferenceCode());
		}
	}

	@Test
	public void testGetSystemObjectFieldNames() {
		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				false
			);

			Map<String, List<String>> systemObjectFieldNames =
				_productTypesCMSStructureObjectFolderContributor.
					getSystemObjectFieldNames();

			Assert.assertTrue(systemObjectFieldNames.isEmpty());

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				true
			);

			ObjectDefinitionLocalService objectDefinitionLocalService =
				Mockito.mock(ObjectDefinitionLocalService.class);

			ObjectDefinition objectDefinition = Mockito.mock(
				ObjectDefinition.class);

			Mockito.when(
				objectDefinition.getObjectDefinitionId()
			).thenReturn(
				100L
			);

			Mockito.when(
				objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						Mockito.eq(
							PIMObjectDefinitionConstants.
								EXTERNAL_REFERENCE_CODE_BASE_SKU),
						Mockito.anyLong())
			).thenReturn(
				objectDefinition
			);

			ReflectionTestUtil.setFieldValue(
				_productTypesCMSStructureObjectFolderContributor,
				"_objectDefinitionLocalService", objectDefinitionLocalService);

			ObjectFieldLocalService objectFieldLocalService = Mockito.mock(
				ObjectFieldLocalService.class);

			List<ObjectField> objectFields = Arrays.asList(
				_mockObjectField("Text", "code", "false", true),
				_mockObjectField("DateTime", "displayDate", "false", true),
				_mockObjectField("LongInteger", "id", "true", true),
				_mockObjectField("Text", "name", "false", true),
				_mockObjectField(
					"Text", RandomTestUtil.randomString(), "false", false));

			Mockito.when(
				objectFieldLocalService.getObjectFields(100L)
			).thenReturn(
				objectFields
			);

			ReflectionTestUtil.setFieldValue(
				_productTypesCMSStructureObjectFolderContributor,
				"_objectFieldLocalService", objectFieldLocalService);

			Assert.assertEquals(
				Collections.singletonMap(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_BASE_SKU,
					Arrays.asList("code", "name")),
				_productTypesCMSStructureObjectFolderContributor.
					getSystemObjectFieldNames());
		}
	}

	private ObjectField _mockObjectField(
		String businessType, String name, String readOnly, boolean system) {

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.lenient(
		).when(
			objectField.getBusinessType()
		).thenReturn(
			businessType
		);

		Mockito.lenient(
		).when(
			objectField.getName()
		).thenReturn(
			name
		);

		Mockito.lenient(
		).when(
			objectField.getReadOnly()
		).thenReturn(
			readOnly
		);

		Mockito.lenient(
		).when(
			objectField.isSystem()
		).thenReturn(
			system
		);

		return objectField;
	}

	private final ProductTypesCMSStructureObjectFolderContributor
		_productTypesCMSStructureObjectFolderContributor =
			new ProductTypesCMSStructureObjectFolderContributor();

}