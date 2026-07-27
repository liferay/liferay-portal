/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.connector;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
 */
public class LiferayCommercePIMConnectorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetKey() {
		LiferayCommercePIMConnector liferayCommercePIMConnector =
			new LiferayCommercePIMConnector();

		Assert.assertEquals(
			"liferay-commerce", liferayCommercePIMConnector.getKey());
	}

	@Test
	public void testGetName() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(LocaleUtil.US, "liferay-commerce")
		).thenReturn(
			"Liferay Commerce"
		);

		languageUtil.setLanguage(language);

		LiferayCommercePIMConnector liferayCommercePIMConnector =
			new LiferayCommercePIMConnector();

		Assert.assertEquals(
			"Liferay Commerce",
			liferayCommercePIMConnector.getName(LocaleUtil.US));
	}

	@Test
	public void testIsActive() {
		LiferayCommercePIMConnector liferayCommercePIMConnector =
			new LiferayCommercePIMConnector();

		Assert.assertTrue(
			liferayCommercePIMConnector.isActive(RandomTestUtil.randomLong()));
	}

}