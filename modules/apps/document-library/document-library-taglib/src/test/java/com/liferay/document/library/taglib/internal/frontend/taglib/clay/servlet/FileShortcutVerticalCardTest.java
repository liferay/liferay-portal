/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.taglib.internal.frontend.taglib.clay.servlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author David Reyes
 */
public class FileShortcutVerticalCardTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpLanguageUtil();
	}

	@Test
	@TestInfo("LPD-111121")
	public void testGetSubtitle() throws Exception {
		FileShortcut fileShortcut = Mockito.mock(FileShortcut.class);

		Mockito.when(
			fileShortcut.getModifiedDate()
		).thenReturn(
			new Date()
		);

		Mockito.when(
			fileShortcut.getUserName()
		).thenReturn(
			"Test & Co"
		);

		FileShortcutVerticalCard fileShortcutVerticalCard =
			new FileShortcutVerticalCard(
				Collections.emptySet(), fileShortcut, _httpServletRequest,
				null);

		Assert.assertEquals(
			"1 minute Test & Co", fileShortcutVerticalCard.getSubtitle());
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.format(
				Mockito.eq(_httpServletRequest), Mockito.eq("x-ago-by-x"),
				Mockito.any(Object[].class))
		).thenAnswer(
			(Answer<String>)invocationOnMock -> StringUtil.merge(
				invocationOnMock.getArgument(2, Object[].class),
				StringPool.SPACE)
		);

		Mockito.when(
			language.getTimeDescription(
				Mockito.eq(_httpServletRequest), Mockito.anyLong(),
				Mockito.eq(true))
		).thenReturn(
			"1 minute"
		);

		languageUtil.setLanguage(language);
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);

}