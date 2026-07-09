/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class ProductsSectionDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetAPIURL() {
		ProductsSectionDisplayContext productsSectionDisplayContext =
			new ProductsSectionDisplayContext(null);

		Assert.assertEquals(
			"/o/search/v1.0/search?emptySearch=true&filter=" +
				URLCodec.encodeURL("cmsSection eq 'products'") +
					"&nestedFields=embedded",
			productsSectionDisplayContext.getAPIURL());
	}

	@Test
	public void testGetEmptyState() {
		LanguageUtil languageUtil = new LanguageUtil();

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(
				httpServletRequest, "click-new-to-create-your-first-product")
		).thenReturn(
			"Click \"New\" to create your first product."
		);

		Mockito.when(
			language.get(httpServletRequest, "no-products-yet")
		).thenReturn(
			"No Products Yet"
		);

		languageUtil.setLanguage(language);

		ProductsSectionDisplayContext productsSectionDisplayContext =
			new ProductsSectionDisplayContext(httpServletRequest);

		Map<String, Object> emptyState =
			productsSectionDisplayContext.getEmptyState();

		Assert.assertEquals(
			"Click \"New\" to create your first product.",
			emptyState.get("description"));
		Assert.assertEquals(
			"/states/cms_empty_state_content.svg", emptyState.get("image"));
		Assert.assertEquals("No Products Yet", emptyState.get("title"));
	}

}