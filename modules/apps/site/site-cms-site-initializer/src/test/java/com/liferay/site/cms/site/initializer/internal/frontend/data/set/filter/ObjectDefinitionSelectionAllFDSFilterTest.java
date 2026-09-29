/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.cms.site.initializer.contributor.CMSStructureObjectFolderContributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Adolfo Pérez
 */
public class ObjectDefinitionSelectionAllFDSFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		_objectDefinitionSelectionAllFDSFilter =
			new ObjectDefinitionSelectionAllFDSFilter();

		ReflectionTestUtil.setFieldValue(
			_objectDefinitionSelectionAllFDSFilter,
			"_cmsStructureObjectFolderContributors", new ArrayList<>());
		ReflectionTestUtil.setFieldValue(
			_objectDefinitionSelectionAllFDSFilter, "language", _language);
		ReflectionTestUtil.setFieldValue(
			_objectDefinitionSelectionAllFDSFilter, "objectDefinitionService",
			_objectDefinitionService);
	}

	@Test
	public void testGetSelectionFDSFilterItems() {
		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		String objectDefinitionExternalReferenceCode =
			RandomTestUtil.randomString();

		Mockito.when(
			objectDefinition.getExternalReferenceCode()
		).thenReturn(
			objectDefinitionExternalReferenceCode
		);

		Mockito.when(
			_objectDefinitionService.getCMSObjectDefinitions(
				Mockito.anyLong(), Mockito.any(String[].class))
		).thenReturn(
			List.of(objectDefinition)
		);

		List<SelectionFDSFilterItem> selectionFDSFilterItems =
			_objectDefinitionSelectionAllFDSFilter.getSelectionFDSFilterItems(
				_locale);

		Assert.assertEquals(
			selectionFDSFilterItems.toString(), 1,
			selectionFDSFilterItems.size());

		SelectionFDSFilterItem objectDefinitionSelectionFDSFilterItem =
			selectionFDSFilterItems.get(0);

		Assert.assertEquals(
			objectDefinitionExternalReferenceCode,
			objectDefinitionSelectionFDSFilterItem.getValue());

		CMSStructureObjectFolderContributor
			cmsStructureObjectFolderContributor = Mockito.mock(
				CMSStructureObjectFolderContributor.class);

		String objectFolderExternalReferenceCode =
			RandomTestUtil.randomString();

		Mockito.when(
			cmsStructureObjectFolderContributor.
				getObjectFolderExternalReferenceCode()
		).thenReturn(
			objectFolderExternalReferenceCode
		);

		ReflectionTestUtil.setFieldValue(
			_objectDefinitionSelectionAllFDSFilter,
			"_cmsStructureObjectFolderContributors",
			List.of(cmsStructureObjectFolderContributor));

		_objectDefinitionSelectionAllFDSFilter.getSelectionFDSFilterItems(
			_locale);

		ArgumentCaptor<String[]> argumentCaptor = ArgumentCaptor.forClass(
			String[].class);

		Mockito.verify(
			_objectDefinitionService, Mockito.times(2)
		).getCMSObjectDefinitions(
			Mockito.anyLong(), argumentCaptor.capture()
		);

		List<String[]> objectFolderExternalReferenceCodesList =
			argumentCaptor.getAllValues();

		Assert.assertTrue(
			ArrayUtil.contains(
				objectFolderExternalReferenceCodesList.get(1),
				objectFolderExternalReferenceCode));
	}

	@Mock
	private Language _language;

	private final Locale _locale = LocaleUtil.US;
	private ObjectDefinitionSelectionAllFDSFilter
		_objectDefinitionSelectionAllFDSFilter;

	@Mock
	private ObjectDefinitionService _objectDefinitionService;

}