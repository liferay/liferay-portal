/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.web.internal.display.context;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutContentVersionDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws PortalException {
		_setUpDraftLayout();
		_setUpGroup();
		_setUpLanguage();
		_setUpLayoutLocalService();
		_setUpPublishedLayout();
	}

	@Test
	public void testGetContext() throws PortalException {
		SegmentsExperience defaultSegmentsExperience = _getSegmentsExperience(
			null, null);

		String segmentsEntryERC = RandomTestUtil.randomString();
		String segmentsEntryScopeERC = RandomTestUtil.randomString();

		SegmentsExperience loserSegmentsExperience = _getSegmentsExperience(
			segmentsEntryERC, segmentsEntryScopeERC);
		SegmentsExperience winnerSegmentsExperience = _getSegmentsExperience(
			segmentsEntryERC, segmentsEntryScopeERC);

		Mockito.when(
			_segmentsExperienceLocalService.getSegmentsExperiences(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.eq(true))
		).thenReturn(
			Arrays.asList(
				winnerSegmentsExperience, loserSegmentsExperience,
				defaultSegmentsExperience)
		);

		LayoutContentVersionDisplayContext layoutContentVersionDisplayContext =
			new LayoutContentVersionDisplayContext(
				_getMockHttpServletRequest(), _language, _layoutLocalService,
				_segmentsExperienceLocalService);

		Map<String, Object> context =
			layoutContentVersionDisplayContext.getContext();

		Map<String, Object> config = (Map<String, Object>)context.get("config");

		Assert.assertEquals(
			LocaleUtil.toLanguageId(_siteDefaultLocale),
			config.get("defaultLanguageId"));
		Assert.assertEquals(_draftName, config.get("draftName"));
		Assert.assertEquals(Boolean.TRUE, config.get("hasDraft"));
		Assert.assertEquals(
			StringBundler.concat(
				"/o/headless-admin-site/v1.0/sites/",
				_group.getExternalReferenceCode(), "/site-pages/",
				_publishedLayout.getExternalReferenceCode(),
				"/page-specification-versions"),
			config.get("pageSpecificationVersionsURL"));

		_assertAvailableLanguages(
			(Map<String, Object>)config.get("availableLanguages"), _locale,
			_siteDefaultLocale);

		List<Map<String, Object>> availableSegmentsExperiences =
			(List<Map<String, Object>>)config.get(
				"availableSegmentsExperiences");

		_assertSegmentsExperience(
			true, winnerSegmentsExperience,
			availableSegmentsExperiences.get(0));
		_assertSegmentsExperience(
			false, loserSegmentsExperience,
			availableSegmentsExperiences.get(1));
		_assertSegmentsExperience(
			true, defaultSegmentsExperience,
			availableSegmentsExperiences.get(2));

		Assert.assertEquals(
			availableSegmentsExperiences.toString(), 3,
			availableSegmentsExperiences.size());
	}

	private void _assertAvailableLanguages(
		Map<String, Object> availableLanguages, Locale... locales) {

		Assert.assertEquals(
			availableLanguages.toString(), locales.length,
			availableLanguages.size());

		for (Locale locale : locales) {
			Map<String, Object> languageMap =
				(Map<String, Object>)availableLanguages.get(
					LocaleUtil.toLanguageId(locale));

			Assert.assertEquals(
				StringUtil.toLowerCase(LocaleUtil.toW3cLanguageId(locale)),
				languageMap.get("languageIcon"));
			Assert.assertEquals(
				LocaleUtil.toW3cLanguageId(locale),
				languageMap.get("w3cLanguageId"));
		}
	}

	private void _assertSegmentsExperience(
		boolean active, SegmentsExperience segmentsExperience,
		Map<String, Object> segmentsExperienceMap) {

		Assert.assertEquals(active, segmentsExperienceMap.get("active"));
		Assert.assertEquals(
			segmentsExperience.getExternalReferenceCode(),
			segmentsExperienceMap.get("segmentsExperienceERC"));
		Assert.assertEquals(
			segmentsExperience.getName(_locale),
			segmentsExperienceMap.get("segmentsExperienceName"));
		Assert.assertEquals(
			active ? "active" : "inactive",
			segmentsExperienceMap.get("statusLabel"));
	}

	private MockHttpServletRequest _getMockHttpServletRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockHttpServletRequest;
	}

	private SegmentsExperience _getSegmentsExperience(
		String segmentsEntryERC, String segmentsEntryScopeERC) {

		SegmentsExperience segmentsExperience = Mockito.mock(
			SegmentsExperience.class);

		Mockito.when(
			segmentsExperience.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			segmentsExperience.getName(_locale)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			segmentsExperience.getSegmentsEntryERC()
		).thenReturn(
			segmentsEntryERC
		);

		Mockito.when(
			segmentsExperience.getSegmentsEntryScopeERC()
		).thenReturn(
			segmentsEntryScopeERC
		);

		Mockito.when(
			segmentsExperience.getSegmentsExperienceId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			segmentsExperience.hasDefaultSegmentsEntry()
		).thenReturn(
			Validator.isNull(segmentsEntryERC)
		);

		return segmentsExperience;
	}

	private ThemeDisplay _getThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLayout()
		).thenReturn(
			_draftLayout
		);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			_locale
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);

		Mockito.when(
			themeDisplay.getSiteDefaultLocale()
		).thenReturn(
			_siteDefaultLocale
		);

		return themeDisplay;
	}

	private void _setUpDraftLayout() {
		_draftName = RandomTestUtil.randomString();

		Mockito.when(
			_draftLayout.getName(_siteDefaultLocale)
		).thenReturn(
			_draftName
		);

		Mockito.when(
			_draftLayout.isApproved()
		).thenReturn(
			false
		);
	}

	private void _setUpGroup() {
		Mockito.when(
			_group.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	private void _setUpLanguage() {
		Mockito.when(
			_language.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1, String.class)
		);

		Mockito.when(
			_language.getAvailableLocales(Mockito.anyLong())
		).thenReturn(
			new LinkedHashSet<>(Arrays.asList(_locale, _siteDefaultLocale))
		);
	}

	private void _setUpLayoutLocalService() throws PortalException {
		Mockito.when(
			_layoutLocalService.getLayout(Mockito.anyLong())
		).thenReturn(
			_publishedLayout
		);
	}

	private void _setUpPublishedLayout() {
		Mockito.when(
			_publishedLayout.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	private final Layout _draftLayout = Mockito.mock(Layout.class);
	private String _draftName;
	private final Group _group = Mockito.mock(Group.class);
	private final Language _language = Mockito.mock(Language.class);
	private final LayoutLocalService _layoutLocalService = Mockito.mock(
		LayoutLocalService.class);
	private final Locale _locale = LocaleUtil.US;
	private final Layout _publishedLayout = Mockito.mock(Layout.class);
	private final SegmentsExperienceLocalService
		_segmentsExperienceLocalService = Mockito.mock(
			SegmentsExperienceLocalService.class);
	private final Locale _siteDefaultLocale = LocaleUtil.GERMANY;

}