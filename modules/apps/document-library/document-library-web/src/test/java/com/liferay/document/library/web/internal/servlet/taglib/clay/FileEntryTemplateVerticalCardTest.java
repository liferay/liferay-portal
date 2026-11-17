/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.servlet.taglib.clay;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author David Reyes
 */
public class FileEntryTemplateVerticalCardTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-111121")
	public void testGetSubtitle() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.format(
				Mockito.eq(LocaleUtil.US), Mockito.eq("right-now-by-x"),
				Mockito.any(Object.class))
		).thenAnswer(
			(Answer<String>)invocationOnMock -> String.valueOf(
				invocationOnMock.getArgument(2, Object.class))
		);

		languageUtil.setLanguage(language);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getFullName()
		).thenReturn(
			"Test & Co"
		);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			themeDisplay.getUser()
		).thenReturn(
			user
		);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		FileEntryTemplateVerticalCard fileEntryTemplateVerticalCard =
			new FileEntryTemplateVerticalCard(null, httpServletRequest);

		Assert.assertEquals(
			"Test & Co", fileEntryTemplateVerticalCard.getSubtitle());
	}

}