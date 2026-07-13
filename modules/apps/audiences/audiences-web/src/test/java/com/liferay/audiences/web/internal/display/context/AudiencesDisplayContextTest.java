/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.display.context;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author Georgel Pop
 */
public class AudiencesDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		LanguageUtil languageUtil = new LanguageUtil();

		Mockito.when(
			_language.get(Mockito.any(Locale.class), Mockito.anyString())
		).thenAnswer(
			(Answer<String>)invocationOnMock -> invocationOnMock.getArgument(
				1, String.class)
		);

		languageUtil.setLanguage(_language);
	}

	@Test
	@TestInfo("LPD-94450")
	public void testGetEmptyState() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		Mockito.when(
			renderRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		AudiencesDisplayContext audiencesDisplayContext =
			new AudiencesDisplayContext(
				renderRequest, Mockito.mock(RenderResponse.class));

		Map<String, Object> emptyState =
			audiencesDisplayContext.getEmptyState();

		Assert.assertEquals(
			"click-new-to-create-your-first-audience",
			emptyState.get("description"));
		Assert.assertEquals("/states/empty_state.svg", emptyState.get("image"));
		Assert.assertEquals("no-audiences-yet", emptyState.get("title"));
	}

	private final Language _language = Mockito.mock(Language.class);

}