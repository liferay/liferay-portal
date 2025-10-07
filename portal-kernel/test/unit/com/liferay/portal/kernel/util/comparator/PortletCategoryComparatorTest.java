/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util.comparator;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.util.LocaleUtil;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Eduardo García
 */
public class PortletCategoryComparatorTest {

	@Test
	public void testCompareLocalized() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		languageUtil.setLanguage(language);

		Mockito.when(
			language.get(Mockito.eq(LocaleUtil.SPAIN), Mockito.eq("area"))
		).thenReturn(
			"Área"
		);

		Mockito.when(
			language.get(Mockito.eq(LocaleUtil.SPAIN), Mockito.eq("zone"))
		).thenReturn(
			"Zona"
		);

		PortletCategory portletCategory1 = new PortletCategory("area");
		PortletCategory portletCategory2 = new PortletCategory("zone");

		PortletCategoryComparator portletCategoryComparator =
			new PortletCategoryComparator(LocaleUtil.SPAIN);

		int value = portletCategoryComparator.compare(
			portletCategory1, portletCategory2);

		Assert.assertTrue(value < 0);
	}

}