/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
 */
public class PIMConnectorStatusSelectionFDSFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(Mockito.eq(LocaleUtil.US), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		languageUtil.setLanguage(language);
	}

	@Test
	public void testGetEntityFieldType() {
		Assert.assertEquals(
			FDSEntityFieldTypes.BOOLEAN,
			_pimConnectorStatusSelectionFDSFilter.getEntityFieldType());
	}

	@Test
	public void testGetId() {
		Assert.assertEquals(
			"active", _pimConnectorStatusSelectionFDSFilter.getId());
	}

	@Test
	public void testGetLabel() {
		Assert.assertEquals(
			"status", _pimConnectorStatusSelectionFDSFilter.getLabel());
	}

	@Test
	public void testGetSelectionFDSFilterItems() {
		List<SelectionFDSFilterItem> selectionFDSFilterItems =
			_pimConnectorStatusSelectionFDSFilter.getSelectionFDSFilterItems(
				LocaleUtil.US);

		Assert.assertEquals(
			selectionFDSFilterItems.toString(), 2,
			selectionFDSFilterItems.size());

		SelectionFDSFilterItem selectionFDSFilterItem =
			selectionFDSFilterItems.get(0);

		Assert.assertEquals("active", selectionFDSFilterItem.getLabel());
		Assert.assertTrue((boolean)selectionFDSFilterItem.getValue());

		selectionFDSFilterItem = selectionFDSFilterItems.get(1);

		Assert.assertEquals("inactive", selectionFDSFilterItem.getLabel());
		Assert.assertFalse((boolean)selectionFDSFilterItem.getValue());
	}

	private final PIMConnectorStatusSelectionFDSFilter
		_pimConnectorStatusSelectionFDSFilter =
			new PIMConnectorStatusSelectionFDSFilter();

}