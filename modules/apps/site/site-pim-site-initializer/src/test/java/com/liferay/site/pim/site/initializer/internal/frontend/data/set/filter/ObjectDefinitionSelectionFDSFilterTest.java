/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.internal.constants.PIMObjectFolderConstants;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Balazs Breier
 */
public class ObjectDefinitionSelectionFDSFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		ReflectionTestUtil.setFieldValue(
			_objectDefinitionSelectionFDSFilter, "_objectDefinitionService",
			_objectDefinitionService);
	}

	@Test
	public void testGetSelectionFDSFilterItems() {
		ObjectDefinition objectDefinition1 = _mockObjectDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		String externalReferenceCode = RandomTestUtil.randomString();

		ObjectDefinition objectDefinition2 = _mockObjectDefinition(
			externalReferenceCode, "PIM Base SKU");

		Mockito.when(
			_objectDefinitionService.getCMSObjectDefinitions(
				Mockito.anyLong(), Mockito.any(String[].class))
		).thenReturn(
			List.of(objectDefinition1)
		);

		Mockito.when(
			_objectDefinitionService.getCMSObjectDefinitions(
				Mockito.anyLong(),
				Mockito.eq(
					new String[] {
						PIMObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES
					}))
		).thenReturn(
			List.of(objectDefinition2)
		);

		List<SelectionFDSFilterItem> selectionFDSFilterItems =
			_objectDefinitionSelectionFDSFilter.getSelectionFDSFilterItems(
				_locale);

		Assert.assertEquals(
			selectionFDSFilterItems.toString(), 1,
			selectionFDSFilterItems.size());

		SelectionFDSFilterItem selectionFDSFilterItem =
			selectionFDSFilterItems.get(0);

		Assert.assertEquals("PIM Base SKU", selectionFDSFilterItem.getLabel());
		Assert.assertEquals(
			externalReferenceCode, selectionFDSFilterItem.getValue());
	}

	private ObjectDefinition _mockObjectDefinition(
		String externalReferenceCode, String label) {

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		Mockito.when(
			objectDefinition.getLabel(_locale)
		).thenReturn(
			label
		);

		return objectDefinition;
	}

	private final Locale _locale = LocaleUtil.US;
	private final ObjectDefinitionSelectionFDSFilter
		_objectDefinitionSelectionFDSFilter =
			new ObjectDefinitionSelectionFDSFilter();

	@Mock
	private ObjectDefinitionService _objectDefinitionService;

}