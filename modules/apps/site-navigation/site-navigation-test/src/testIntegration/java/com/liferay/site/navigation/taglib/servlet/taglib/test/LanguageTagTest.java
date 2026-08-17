/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.taglib.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutFriendlyURLRandomizerBumper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.LanguageEntry;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LanguageIds;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.navigation.taglib.servlet.taglib.LanguageTag;

import jakarta.portlet.PortletPreferences;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Georgel Pop
 */
@FeatureFlag("LPD-76594")
@LanguageIds(
	availableLanguageIds = {"en_US", "fr_FR"}, defaultLanguageId = "en_US"
)
@RunWith(Arquillian.class)
public class LanguageTagTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-99541")
	public void testGetLanguageEntries() throws Exception {
		Layout layout = _addLayout();

		_testGetLanguageEntriesWithAnotherLocaleFriendlyURL(layout);
		_testGetLanguageEntriesWithDefaultPageFriendlyURLMappingPath(layout);

		_testGetLanguageEntriesWithDisplayPage();
		_testGetLanguageEntriesWithFormAction(layout);
		_testGetLanguageEntriesWithFriendlyURLMappingPath(layout);
		_testGetLanguageEntriesWithLocalePrependFriendlyURLStyle(layout, 1);
		_testGetLanguageEntriesWithLocalePrependFriendlyURLStyle(layout, 2);
		_testGetLanguageEntriesWithLocalePrependFriendlyURLStyle(layout, 3);
		_testGetLanguageEntriesWithLocalizedVirtualHostname(layout);
		_testGetLanguageEntriesWithRedirectParameter(layout);
		_testGetLanguageEntriesWithSignedInUser(layout);
		_testGetLanguageEntriesWithVirtualHostname(layout);
		_testGetLanguageEntriesWithoutLayout();
		_testGetLanguageEntriesWithoutLocalePrependFriendlyURLStyle(layout);
	}

	private Layout _addLayout() throws Exception {
		return LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			HashMapBuilder.put(
				LocaleUtil.FRANCE, RandomTestUtil.randomString()
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.FRANCE, StringPool.SLASH + _getRandomFriendlyURL()
			).put(
				LocaleUtil.US, StringPool.SLASH + _getRandomFriendlyURL()
			).build());
	}

	private void _assertLocalizedURL(
		Layout layout, Locale locale, String suffix, String url) {

		Assert.assertEquals(
			StringBundler.concat(
				StringPool.SLASH, locale.getLanguage(),
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				_group.getFriendlyURL(), layout.getFriendlyURL(locale), suffix),
			url);
	}

	private List<LanguageEntry> _getLanguageEntries(
		String currentURLSuffix, String formAction, ThemeDisplay themeDisplay) {

		Layout layout = themeDisplay.getLayout();

		String currentURL = null;

		if (layout != null) {
			currentURL = StringBundler.concat(
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				_group.getFriendlyURL(),
				layout.getFriendlyURL(themeDisplay.getLocale()),
				currentURLSuffix);
		}

		return _getLanguageEntriesForURL(currentURL, formAction, themeDisplay);
	}

	private List<LanguageEntry> _getLanguageEntries(
		String formAction, ThemeDisplay themeDisplay) {

		return _getLanguageEntries(StringPool.BLANK, formAction, themeDisplay);
	}

	private List<LanguageEntry> _getLanguageEntries(ThemeDisplay themeDisplay) {
		return _getLanguageEntries(null, themeDisplay);
	}

	private List<LanguageEntry> _getLanguageEntriesForURL(
		String currentURL, String formAction, ThemeDisplay themeDisplay) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Layout layout = themeDisplay.getLayout();

		if (layout != null) {
			mockHttpServletRequest.setAttribute(
				WebKeys.CURRENT_URL, currentURL);
			mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);
		}

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		themeDisplay.setRequest(mockHttpServletRequest);

		LanguageTag languageTag = new LanguageTag();

		languageTag.setPageContext(
			new MockPageContext(
				new MockServletContext(), mockHttpServletRequest));

		if (formAction != null) {
			languageTag.setFormAction(formAction);
		}

		return ReflectionTestUtil.invoke(
			languageTag, "_getLanguageEntries",
			new Class<?>[] {
				Collection.class, boolean.class, String.class, String.class
			},
			Arrays.asList(LocaleUtil.FRANCE, LocaleUtil.US), true,
			(formAction == null) ? _UPDATE_LANGUAGE_PATH : formAction,
			"languageId");
	}

	private String _getLocalePrependFriendlyURLStyle() throws Exception {
		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			_group.getCompanyId());

		return portletPreferences.getValue(
			PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE,
			String.valueOf(PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE));
	}

	private String _getRandomFriendlyURL() {
		return _friendlyURLNormalizer.normalize(
			RandomTestUtil.randomString(
				LayoutFriendlyURLRandomizerBumper.INSTANCE));
	}

	private ThemeDisplay _getThemeDisplay(Layout layout, Locale locale)
		throws Exception {

		Company company = CompanyLocalServiceUtil.getCompany(
			_group.getCompanyId());

		return _getThemeDisplay(
			layout, locale, company.getPortalURL(_group.getGroupId()));
	}

	private ThemeDisplay _getThemeDisplay(
			Layout layout, Locale locale, String portalURL)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = CompanyLocalServiceUtil.getCompany(
			_group.getCompanyId());

		LayoutSet layoutSet = layout.getLayoutSet();

		themeDisplay.setCompany(company);
		themeDisplay.setLanguageId(LocaleUtil.toLanguageId(locale));
		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layoutSet);
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());
		themeDisplay.setLocale(locale);
		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());
		themeDisplay.setPlid(layout.getPlid());
		themeDisplay.setPortalDomain(HttpComponentsUtil.getDomain(portalURL));
		themeDisplay.setPortalURL(portalURL);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setServerPort(PortalUtil.getPortalServerPort(false));
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private String _getURL(List<LanguageEntry> languageEntries, Locale locale) {
		for (LanguageEntry languageEntry : languageEntries) {
			if (LocaleUtil.equals(languageEntry.getLocale(), locale)) {
				return languageEntry.getURL();
			}
		}

		return null;
	}

	private void _setLocalePrependFriendlyURLStyle(
			String localePrependFriendlyURLStyle)
		throws Exception {

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			_group.getCompanyId());

		portletPreferences.setValue(
			PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE,
			localePrependFriendlyURLStyle);

		portletPreferences.store();
	}

	private void _testGetLanguageEntriesWithAnotherLocaleFriendlyURL(
			Layout layout)
		throws Exception {

		ThemeDisplay themeDisplay = _getThemeDisplay(layout, LocaleUtil.US);

		_assertLocalizedURL(
			layout, LocaleUtil.FRANCE, StringPool.BLANK,
			_getURL(
				_getLanguageEntriesForURL(
					StringBundler.concat(
						PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
						_group.getFriendlyURL(),
						layout.getFriendlyURL(LocaleUtil.FRANCE)),
					null, themeDisplay),
				LocaleUtil.FRANCE));

		_assertLocalizedURL(
			layout, LocaleUtil.FRANCE, _FRIENDLY_URL_MAPPING_PATH,
			_getURL(
				_getLanguageEntriesForURL(
					StringBundler.concat(
						PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
						_group.getFriendlyURL(),
						layout.getFriendlyURL(LocaleUtil.FRANCE),
						_FRIENDLY_URL_MAPPING_PATH),
					null, themeDisplay),
				LocaleUtil.FRANCE));
	}

	private void _testGetLanguageEntriesWithDefaultPageFriendlyURLMappingPath(
			Layout layout)
		throws Exception {

		_assertLocalizedURL(
			layout, LocaleUtil.FRANCE, _FRIENDLY_URL_MAPPING_PATH,
			_getURL(
				_getLanguageEntriesForURL(
					StringBundler.concat(
						PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
						_group.getFriendlyURL(), _FRIENDLY_URL_MAPPING_PATH),
					null, _getThemeDisplay(layout, LocaleUtil.US)),
				LocaleUtil.FRANCE));
	}

	private void _testGetLanguageEntriesWithDisplayPage() throws Exception {
		Map<Locale, String> friendlyURLMap = HashMapBuilder.put(
			LocaleUtil.FRANCE, _getRandomFriendlyURL()
		).put(
			LocaleUtil.US, _getRandomFriendlyURL()
		).build();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			RandomTestUtil.randomString(), _group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class), null, true,
			HashMapBuilder.put(
				LocaleUtil.FRANCE, RandomTestUtil.randomString()
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, friendlyURLMap,
			HashMapBuilder.put(
				LocaleUtil.FRANCE, RandomTestUtil.randomString()
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, LocaleUtil.US, null, null, false, true,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				PortalUtil.getClassNameId(JournalArticle.class),
				journalArticle.getDDMStructureKey(), true,
				WorkflowConstants.STATUS_APPROVED);

		String frenchURL = _getURL(
			_getLanguageEntriesForURL(
				StringBundler.concat(
					PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
					_group.getFriendlyURL(),
					FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE,
					friendlyURLMap.get(LocaleUtil.US)),
				null,
				_getThemeDisplay(
					LayoutLocalServiceUtil.getLayout(
						layoutPageTemplateEntry.getPlid()),
					LocaleUtil.US)),
			LocaleUtil.FRANCE);

		Assert.assertEquals(
			StringBundler.concat(
				"/fr", PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				_group.getFriendlyURL(),
				FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE,
				friendlyURLMap.get(LocaleUtil.FRANCE)),
			frenchURL);
	}

	private void _testGetLanguageEntriesWithFormAction(Layout layout)
		throws Exception {

		Assert.assertEquals(
			_FORM_ACTION + "?languageId=fr_FR",
			_getURL(
				_getLanguageEntries(
					_FORM_ACTION, _getThemeDisplay(layout, LocaleUtil.US)),
				LocaleUtil.FRANCE));
	}

	private void _testGetLanguageEntriesWithFriendlyURLMappingPath(
			Layout layout)
		throws Exception {

		ThemeDisplay themeDisplay = _getThemeDisplay(layout, LocaleUtil.US);

		_assertLocalizedURL(
			layout, LocaleUtil.FRANCE, _FRIENDLY_URL_MAPPING_PATH,
			_getURL(
				_getLanguageEntries(
					_FRIENDLY_URL_MAPPING_PATH, null, themeDisplay),
				LocaleUtil.FRANCE));

		_assertLocalizedURL(
			layout, LocaleUtil.FRANCE, _FRIENDLY_URL_MAPPING_PATH + "?foo=bar",
			_getURL(
				_getLanguageEntries(
					_FRIENDLY_URL_MAPPING_PATH + "?foo=bar", null,
					themeDisplay),
				LocaleUtil.FRANCE));

		_assertLocalizedURL(
			layout, LocaleUtil.FRANCE, "/tags/new%20york",
			_getURL(
				_getLanguageEntries("/tags/new%20york", null, themeDisplay),
				LocaleUtil.FRANCE));

		_assertLocalizedURL(
			layout, LocaleUtil.FRANCE, "/tags",
			_getURL(
				_getLanguageEntries("/tags", null, themeDisplay),
				LocaleUtil.FRANCE));

		_assertLocalizedURL(
			layout, LocaleUtil.FRANCE, "/-/blogs/blog-1",
			_getURL(
				_getLanguageEntries("/-/blogs/blog-1", null, themeDisplay),
				LocaleUtil.FRANCE));
	}

	private void _testGetLanguageEntriesWithLocalePrependFriendlyURLStyle(
			Layout layout, int localePrependFriendlyURLStyle)
		throws Exception {

		String localePrependFriendlyURLStyleValue =
			_getLocalePrependFriendlyURLStyle();

		try {
			_setLocalePrependFriendlyURLStyle(
				String.valueOf(localePrependFriendlyURLStyle));

			_assertLocalizedURL(
				layout, LocaleUtil.FRANCE, StringPool.BLANK,
				_getURL(
					_getLanguageEntries(
						_getThemeDisplay(layout, LocaleUtil.US)),
					LocaleUtil.FRANCE));

			_assertLocalizedURL(
				layout, LocaleUtil.US, StringPool.BLANK,
				_getURL(
					_getLanguageEntries(
						_getThemeDisplay(layout, LocaleUtil.FRANCE)),
					LocaleUtil.US));
		}
		finally {
			_setLocalePrependFriendlyURLStyle(
				localePrependFriendlyURLStyleValue);
		}
	}

	private void _testGetLanguageEntriesWithLocalizedVirtualHostname(
			Layout layout)
		throws Exception {

		LayoutSet layoutSet = layout.getLayoutSet();

		layoutSet.setVirtualHostnames(
			TreeMapBuilder.put(
				RandomTestUtil.randomString(),
				LocaleUtil.toLanguageId(LocaleUtil.FRANCE)
			).put(
				RandomTestUtil.randomString(), StringPool.BLANK
			).build());

		try {
			_assertLocalizedURL(
				layout, LocaleUtil.FRANCE, StringPool.BLANK,
				_getURL(
					_getLanguageEntries(
						_getThemeDisplay(layout, LocaleUtil.US)),
					LocaleUtil.FRANCE));
		}
		finally {
			layoutSet.setVirtualHostnames(new TreeMap<>());
		}
	}

	private void _testGetLanguageEntriesWithoutLayout() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.getCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);

		Assert.assertEquals(
			_UPDATE_LANGUAGE_PATH + "?languageId=fr_FR",
			_getURL(_getLanguageEntries(themeDisplay), LocaleUtil.FRANCE));
	}

	private void _testGetLanguageEntriesWithoutLocalePrependFriendlyURLStyle(
			Layout layout)
		throws Exception {

		String localePrependFriendlyURLStyleValue =
			_getLocalePrependFriendlyURLStyle();

		try {
			_setLocalePrependFriendlyURLStyle("0");

			Assert.assertEquals(
				_UPDATE_LANGUAGE_PATH + "?languageId=fr_FR",
				_getURL(
					_getLanguageEntries(
						_getThemeDisplay(layout, LocaleUtil.US)),
					LocaleUtil.FRANCE));
			Assert.assertEquals(
				_UPDATE_LANGUAGE_PATH + "?languageId=en_US",
				_getURL(
					_getLanguageEntries(
						_getThemeDisplay(layout, LocaleUtil.FRANCE)),
					LocaleUtil.US));
		}
		finally {
			_setLocalePrependFriendlyURLStyle(
				localePrependFriendlyURLStyleValue);
		}
	}

	private void _testGetLanguageEntriesWithRedirectParameter(Layout layout)
		throws Exception {

		Assert.assertEquals(
			_UPDATE_LANGUAGE_PATH + "?languageId=fr_FR",
			_getURL(
				_getLanguageEntries(
					"?redirect=" + RandomTestUtil.randomString(), null,
					_getThemeDisplay(layout, LocaleUtil.US)),
				LocaleUtil.FRANCE));
	}

	private void _testGetLanguageEntriesWithSignedInUser(Layout layout)
		throws Exception {

		ThemeDisplay themeDisplay = _getThemeDisplay(layout, LocaleUtil.US);

		themeDisplay.setSignedIn(true);

		Assert.assertEquals(
			_UPDATE_LANGUAGE_PATH + "?languageId=fr_FR",
			_getURL(_getLanguageEntries(themeDisplay), LocaleUtil.FRANCE));
	}

	private void _testGetLanguageEntriesWithVirtualHostname(Layout layout)
		throws Exception {

		LayoutSet layoutSet = layout.getLayoutSet();

		layoutSet.setVirtualHostnames(
			TreeMapBuilder.put(
				RandomTestUtil.randomString(), StringPool.BLANK
			).build());

		try {
			_assertLocalizedURL(
				layout, LocaleUtil.FRANCE, StringPool.BLANK,
				_getURL(
					_getLanguageEntries(
						_getThemeDisplay(layout, LocaleUtil.US)),
					LocaleUtil.FRANCE));
		}
		finally {
			layoutSet.setVirtualHostnames(new TreeMap<>());
		}
	}

	private static final String _FORM_ACTION = "/custom/view";

	private static final String _FRIENDLY_URL_MAPPING_PATH = "/tags/mytag";

	private static final String _UPDATE_LANGUAGE_PATH =
		"/c/portal/update_language";

	@Inject
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@DeleteAfterTestRun
	private Group _group;

}