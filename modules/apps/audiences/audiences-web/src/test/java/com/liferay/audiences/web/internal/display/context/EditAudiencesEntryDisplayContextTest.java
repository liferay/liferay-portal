/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.display.context;

import com.liferay.audiences.criteria.AudiencesCriteriaProvider;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
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
public class EditAudiencesEntryDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		LanguageUtil languageUtil = new LanguageUtil();

		Mockito.when(
			_language.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			(Answer<String>)invocationOnMock -> invocationOnMock.getArgument(
				1, String.class)
		);

		languageUtil.setLanguage(_language);
	}

	@Test
	@TestInfo("LPD-99227")
	public void testGetData() throws Exception {
		_testGetDataRestoresSubmittedValues();
		_testGetDataIgnoresStrayValues();
	}

	private EditAudiencesEntryDisplayContext _createDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new EditAudiencesEntryDisplayContext(
			_audiencesCriteriaProvider, httpServletRequest,
			Mockito.mock(RenderResponse.class));
	}

	private HttpServletRequest _mockHttpServletRequest() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			httpServletRequest.getParameter("redirect")
		).thenReturn(
			"/redirect"
		);

		Mockito.when(
			_audiencesCriteriaProvider.getAudiencesCriteriaTypes(
				Mockito.anyLong(), Mockito.any())
		).thenReturn(
			Collections.emptyList()
		);

		return httpServletRequest;
	}

	private void _testGetDataIgnoresStrayValues() throws Exception {
		HttpServletRequest httpServletRequest = _mockHttpServletRequest();

		Mockito.when(
			httpServletRequest.getParameter("name")
		).thenReturn(
			"My audience"
		);

		EditAudiencesEntryDisplayContext editAudiencesEntryDisplayContext =
			_createDisplayContext(httpServletRequest);

		Map<String, Object> data = editAudiencesEntryDisplayContext.getData();

		Assert.assertEquals(Boolean.FALSE, data.get("expandGeneralSettings"));
		Assert.assertEquals("", data.get("name"));

		Assert.assertEquals(
			"new-audience", editAudiencesEntryDisplayContext.getTitle());
	}

	private void _testGetDataRestoresSubmittedValues() throws Exception {
		HttpServletRequest httpServletRequest = _mockHttpServletRequest();

		Mockito.when(
			httpServletRequest.getParameter("json")
		).thenReturn(
			"{\"conjunction\":\"AND\"}"
		);

		Mockito.when(
			httpServletRequest.getParameter("name")
		).thenReturn(
			"My audience"
		);

		Mockito.when(
			httpServletRequest.getParameter("redisplay")
		).thenReturn(
			"true"
		);

		EditAudiencesEntryDisplayContext editAudiencesEntryDisplayContext =
			_createDisplayContext(httpServletRequest);

		Map<String, Object> data = editAudiencesEntryDisplayContext.getData();

		Assert.assertEquals(Boolean.TRUE, data.get("expandGeneralSettings"));
		Assert.assertEquals("My audience", data.get("name"));

		JSONObject rulesGroupJSONObject = (JSONObject)data.get("rulesGroup");

		Assert.assertEquals(
			"AND", rulesGroupJSONObject.getString("conjunction"));

		Assert.assertEquals(
			"My audience", editAudiencesEntryDisplayContext.getTitle());
	}

	private final AudiencesCriteriaProvider _audiencesCriteriaProvider =
		Mockito.mock(AudiencesCriteriaProvider.class);
	private final Language _language = Mockito.mock(Language.class);

}