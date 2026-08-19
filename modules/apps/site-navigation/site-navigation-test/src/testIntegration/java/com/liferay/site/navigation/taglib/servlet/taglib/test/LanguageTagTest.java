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
import com.liferay.petra.function.UnsafeBiConsumer;
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

		_layout = LayoutTestUtil.addTypePortletLayout(
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

	@Test
	@TestInfo("LPD-99541")
	public void testGetLanguageEntries() throws Exception {
		_testGetLanguageEntriesWithDisplayPageTemplate();
		_testGetLanguageEntriesWithFormAction();
		_testGetLanguageEntriesWithFriendlyURLMappingPath();
		_testGetLanguageEntriesWithLocalePrependFriendlyURLStyle();
		_testGetLanguageEntriesWithRedirectParameter();
		_testGetLanguageEntriesWithSignedInUser();
		_testGetLanguageEntriesWithVirtualHostname();
		_testGetLanguageEntriesWithoutLayout();
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

	private void _testGetLanguageEntriesWithDisplayPageTemplate()
		throws Exception {

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

		Assert.assertEquals(
			StringBundler.concat(
				"/fr", PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				_group.getFriendlyURL(),
				FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE,
				friendlyURLMap.get(LocaleUtil.FRANCE)),
			_getURL(
				_getLanguageEntriesForURL(
					StringBundler.concat(
						PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
						_group.getFriendlyURL(),
						FriendlyURLResolverConstants.
							URL_SEPARATOR_JOURNAL_ARTICLE,
						friendlyURLMap.get(LocaleUtil.US)),
					null,
					_getThemeDisplay(
						LayoutLocalServiceUtil.getLayout(
							layoutPageTemplateEntry.getPlid()),
						LocaleUtil.US)),
				LocaleUtil.FRANCE));
	}

	private void _testGetLanguageEntriesWithFormAction() throws Exception {
		Assert.assertEquals(
			_FORM_ACTION + "?languageId=fr_FR",
			_getURL(
				_getLanguageEntries(
					_FORM_ACTION, _getThemeDisplay(_layout, LocaleUtil.US)),
				LocaleUtil.FRANCE));
	}

	private void _testGetLanguageEntriesWithFriendlyURLMappingPath()
		throws Exception {

		ThemeDisplay themeDisplay = _getThemeDisplay(_layout, LocaleUtil.US);

		_assertLocalizedURL(
			_layout, LocaleUtil.FRANCE, _FRIENDLY_URL_MAPPING_PATH,
			_getURL(
				_getLanguageEntriesForURL(
					StringBundler.concat(
						PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
						_group.getFriendlyURL(), _FRIENDLY_URL_MAPPING_PATH),
					null, themeDisplay),
				LocaleUtil.FRANCE));
		_assertLocalizedURL(
			_layout, LocaleUtil.FRANCE, _FRIENDLY_URL_MAPPING_PATH,
			_getURL(
				_getLanguageEntriesForURL(
					StringBundler.concat(
						PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
						_group.getFriendlyURL(),
						_layout.getFriendlyURL(LocaleUtil.FRANCE),
						_FRIENDLY_URL_MAPPING_PATH),
					null, themeDisplay),
				LocaleUtil.FRANCE));
		_assertLocalizedURL(
			_layout, LocaleUtil.FRANCE, _FRIENDLY_URL_MAPPING_PATH,
			_getURL(
				_getLanguageEntries(
					_FRIENDLY_URL_MAPPING_PATH, null, themeDisplay),
				LocaleUtil.FRANCE));
		_assertLocalizedURL(
			_layout, LocaleUtil.FRANCE, _FRIENDLY_URL_MAPPING_PATH + "?foo=bar",
			_getURL(
				_getLanguageEntries(
					_FRIENDLY_URL_MAPPING_PATH + "?foo=bar", null,
					themeDisplay),
				LocaleUtil.FRANCE));
		_assertLocalizedURL(
			_layout, LocaleUtil.FRANCE, "/tags/new%20york",
			_getURL(
				_getLanguageEntries("/tags/new%20york", null, themeDisplay),
				LocaleUtil.FRANCE));
		_assertLocalizedURL(
			_layout, LocaleUtil.FRANCE, "/tags",
			_getURL(
				_getLanguageEntries("/tags", null, themeDisplay),
				LocaleUtil.FRANCE));
		_assertLocalizedURL(
			_layout, LocaleUtil.FRANCE, "/-/blogs/blog-1",
			_getURL(
				_getLanguageEntries("/-/blogs/blog-1", null, themeDisplay),
				LocaleUtil.FRANCE));
	}

	private void _testGetLanguageEntriesWithLocalePrependFriendlyURLStyle()
		throws Exception {

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			_group.getCompanyId());

		String localePrependFriendlyURLStyleValue = portletPreferences.getValue(
			PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE,
			String.valueOf(PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE));

		try {
			_testGetLanguageEntriesWithLocalePrependFriendlyURLStyle(
				"0",
				(locale, url) -> Assert.assertEquals(
					StringBundler.concat(
						_UPDATE_LANGUAGE_PATH, "?languageId=",
						LocaleUtil.toLanguageId(locale)),
					url));

			for (String localePrependFriendlyURLStyle :
					List.of("1", "2", "3")) {

				_testGetLanguageEntriesWithLocalePrependFriendlyURLStyle(
					localePrependFriendlyURLStyle,
					(locale, url) -> _assertLocalizedURL(
						_layout, locale, StringPool.BLANK, url));
			}
		}
		finally {
			_setLocalePrependFriendlyURLStyle(
				localePrependFriendlyURLStyleValue);
		}
	}

	private void _testGetLanguageEntriesWithLocalePrependFriendlyURLStyle(
			String localePrependFriendlyURLStyle,
			UnsafeBiConsumer<Locale, String, Exception> unsafeBiConsumer)
		throws Exception {

		_setLocalePrependFriendlyURLStyle(localePrependFriendlyURLStyle);

		unsafeBiConsumer.accept(
			LocaleUtil.FRANCE,
			_getURL(
				_getLanguageEntries(_getThemeDisplay(_layout, LocaleUtil.US)),
				LocaleUtil.FRANCE));
		unsafeBiConsumer.accept(
			LocaleUtil.US,
			_getURL(
				_getLanguageEntries(
					_getThemeDisplay(_layout, LocaleUtil.FRANCE)),
				LocaleUtil.US));
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

	private void _testGetLanguageEntriesWithRedirectParameter()
		throws Exception {

		Assert.assertEquals(
			_UPDATE_LANGUAGE_PATH + "?languageId=fr_FR",
			_getURL(
				_getLanguageEntries(
					"?redirect=" + RandomTestUtil.randomString(), null,
					_getThemeDisplay(_layout, LocaleUtil.US)),
				LocaleUtil.FRANCE));
	}

	private void _testGetLanguageEntriesWithSignedInUser() throws Exception {
		ThemeDisplay themeDisplay = _getThemeDisplay(_layout, LocaleUtil.US);

		themeDisplay.setSignedIn(true);

		Assert.assertEquals(
			_UPDATE_LANGUAGE_PATH + "?languageId=fr_FR",
			_getURL(_getLanguageEntries(themeDisplay), LocaleUtil.FRANCE));
	}

	private void _testGetLanguageEntriesWithVirtualHostname() throws Exception {
		LayoutSet layoutSet = _layout.getLayoutSet();

		String url = _getURL(
			_getLanguageEntries(_getThemeDisplay(_layout, LocaleUtil.US)),
			LocaleUtil.FRANCE);

		_assertLocalizedURL(_layout, LocaleUtil.FRANCE, StringPool.BLANK, url);

		try {
			String defaultVirtualHostname = RandomTestUtil.randomString();

			layoutSet.setVirtualHostnames(
				TreeMapBuilder.put(
					defaultVirtualHostname, StringPool.BLANK
				).build());

			Assert.assertEquals(
				url,
				_getURL(
					_getLanguageEntries(
						_getThemeDisplay(_layout, LocaleUtil.US)),
					LocaleUtil.FRANCE));

			String groupVirtualHostnameURL = _getURL(
				_getLanguageEntries(
					_getThemeDisplay(
						_layout, LocaleUtil.US,
						"http://" + defaultVirtualHostname)),
				LocaleUtil.FRANCE);

			Assert.assertEquals(
				"/fr" + _layout.getFriendlyURL(LocaleUtil.FRANCE),
				groupVirtualHostnameURL);

			String localizedVirtualHostname = RandomTestUtil.randomString();

			layoutSet.setVirtualHostnames(
				TreeMapBuilder.put(
					defaultVirtualHostname, StringPool.BLANK
				).put(
					localizedVirtualHostname,
					LocaleUtil.toLanguageId(LocaleUtil.FRANCE)
				).build());

			Assert.assertEquals(
				url,
				_getURL(
					_getLanguageEntries(
						_getThemeDisplay(_layout, LocaleUtil.US)),
					LocaleUtil.FRANCE));

			Assert.assertEquals(
				groupVirtualHostnameURL,
				_getURL(
					_getLanguageEntries(
						_getThemeDisplay(
							_layout, LocaleUtil.US,
							"http://" + defaultVirtualHostname)),
					LocaleUtil.FRANCE));
			Assert.assertEquals(
				groupVirtualHostnameURL,
				_getURL(
					_getLanguageEntries(
						_getThemeDisplay(
							_layout, LocaleUtil.US,
							"http://" + localizedVirtualHostname)),
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

	private Layout _layout;

}