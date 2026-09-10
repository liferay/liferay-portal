/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.test.util;

import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;

import org.mockito.Mockito;

/**
 * @author Mario Leandro
 */
public abstract class BasePanelAppNavigationItemsTestCase {

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			language.get(Mockito.eq(LocaleUtil.ENGLISH), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		Mockito.when(
			language.get(Mockito.eq(LocaleUtil.SPAIN), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1) + _LOCALE_SUFFIX
		);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.SPAIN
		);
	}

	protected void assertCanonicalName(
		String expectedCanonicalName,
		PanelAppNavigationItem panelAppNavigationItem) {

		Assert.assertEquals(
			expectedCanonicalName, panelAppNavigationItem.getCanonicalName());
		Assert.assertEquals(
			expectedCanonicalName + _LOCALE_SUFFIX,
			panelAppNavigationItem.getLabel());
	}

	protected void assertCanonicalNames(
		List<PanelAppNavigationItem> panelAppNavigationItems,
		String... expectedCanonicalNames) {

		Assert.assertEquals(
			panelAppNavigationItems.toString(), expectedCanonicalNames.length,
			panelAppNavigationItems.size());

		for (int i = 0; i < expectedCanonicalNames.length; i++) {
			assertCanonicalName(
				expectedCanonicalNames[i], panelAppNavigationItems.get(i));
		}
	}

	protected void assertParameterValue(
		String expectedParameterValue,
		PanelAppNavigationItem panelAppNavigationItem, String parameterName) {

		String href = panelAppNavigationItem.getHref();

		Assert.assertEquals(
			href, expectedParameterValue,
			_getParameterValue(href, parameterName));
	}

	protected final HttpServletRequest httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	protected final Language language = Mockito.mock(Language.class);
	protected final ThemeDisplay themeDisplay = Mockito.mock(
		ThemeDisplay.class);

	private String _getParameterValue(String href, String parameterName) {
		for (String parameter : StringUtil.split(href, CharPool.SEMICOLON)) {
			int index = parameter.indexOf(CharPool.EQUAL);

			if (index == -1) {
				continue;
			}

			String namespacedParameterName = parameter.substring(0, index);

			if (namespacedParameterName.endsWith(
					StringPool.UNDERLINE + parameterName)) {

				return parameter.substring(index + 1);
			}
		}

		return null;
	}

	private static final String _LOCALE_SUFFIX = "-es";

}