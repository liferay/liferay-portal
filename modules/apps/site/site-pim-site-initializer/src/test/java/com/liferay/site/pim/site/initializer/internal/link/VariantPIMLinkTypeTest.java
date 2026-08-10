/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.link;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class VariantPIMLinkTypeTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetLabel() {
		VariantPIMLinkType variantPIMLinkType = new VariantPIMLinkType();

		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(LocaleUtil.US, "variant")
		).thenReturn(
			"Variant"
		);

		languageUtil.setLanguage(language);

		Assert.assertEquals(
			"Variant", variantPIMLinkType.getLabel(LocaleUtil.US));
	}

	@Test
	public void testGetType() {
		VariantPIMLinkType variantPIMLinkType = new VariantPIMLinkType();

		Assert.assertEquals("variant", variantPIMLinkType.getType());
	}

	@Test
	public void testIsClustered() {
		VariantPIMLinkType variantPIMLinkType = new VariantPIMLinkType();

		Assert.assertTrue(variantPIMLinkType.isClustered());
	}

}