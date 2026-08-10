/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutFriendlyURLRandomizerBumper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.action.UpdateLanguageAction;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.VirtualLayoutConstants;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LanguageIds;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Ricardo Couso
 */
@LanguageIds(
	availableLanguageIds = {"de_DE", "en_GB", "en_US", "fr_FR"},
	defaultLanguageId = "en_US"
)
@RunWith(Arquillian.class)
public class UpdateLanguageActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			HashMapBuilder.put(
				_defaultLocale, "Page in Default Locale"
			).put(
				_sourceLocale, "Page in Source Locale"
			).put(
				_targetLocale, "Page in Target Locale"
			).build(),
			HashMapBuilder.put(
				_defaultLocale, "/page-in-default-locale"
			).put(
				_sourceLocale, "/page-in-source-locale"
			).put(
				_targetLocale, "/page-in-target-locale"
			).build());

		_journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				_defaultLocale, "asset"
			).put(
				_sourceLocale, "assetsource"
			).put(
				_sourceUKLocale, "assetuksource"
			).put(
				_targetLocale, "assettarget"
			).build(),
			null,
			HashMapBuilder.put(
				_defaultLocale, "c1"
			).build(),
			_layout.getUuid(), LocaleUtil.getSiteDefault(), null, false, true,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Test
	@TestInfo("LPD-88958")
	public void testExecute() throws Exception {
		_testExecuteWithImpersonation();
	}

	@Test
	@TestInfo({"LPD-86415", "LPD-102324"})
	public void testGetRedirect() throws Exception {
		_testGetRedirectWithControlPanelURL(false);
		_testGetRedirectWithControlPanelURL(true);
		_testGetRedirectWithFriendlyURL(false);
		_testGetRedirectWithFriendlyURL(true);
		_testGetRedirectWithGroupFriendlyURLWithPortletURLMapping();
		_testGetRedirectWithLayoutFriendlyURLWithPortletURLMapping();
		_testGetRedirectWithoutLayoutFriendlyURLWithPortletURLMapping(
			_sourceLocale);
		_testGetRedirectWithoutLayoutFriendlyURLWithPortletURLMapping(null);
		_testGetRedirectWithPortletFriendlyURL(_sourceLocale);
		_testGetRedirectWithPortletFriendlyURL(null);
		_testGetRedirectWithPortletURLMapping(_sourceLocale);
		_testGetRedirectWithPortletURLMapping(null);
	}

	@Test
	public void testGetRedirectWithFriendlyURLEndingInAvailableLanguageId()
		throws Exception {

		_updateLayoutFriendlyURL(
			StringPool.SLASH.concat(_defaultLocale.getLanguage()));

		_testGetRedirectWithLayoutFriendlyURL(false);

		LayoutSet layoutSet = _layout.getLayoutSet();

		layoutSet.setVirtualHostnames(
			TreeMapBuilder.put(
				_VIRTUAL_HOSTNAME, StringPool.BLANK
			).build());

		_testGetRedirectWithLayoutFriendlyURL(true);
	}

	@Test
	@TestInfo("LPD-51902")
	public void testGetRedirectWithFriendlyURLWithVirtualHost()
		throws Exception {

		LayoutSet layoutSet = _layout.getLayoutSet();

		layoutSet.setVirtualHostnames(
			TreeMapBuilder.put(
				_VIRTUAL_HOSTNAME, StringPool.BLANK
			).build());

		_testGetRedirect(
			_sourceUKLocale,
			StringBundler.concat(
				StringPool.SLASH, _sourceUKLocale.toLanguageTag(),
				_getFriendlyURLSeparatorPart(_sourceUKLocale), "?queryString"),
			_targetLocale,
			_getFriendlyURLSeparatorPart(_targetLocale) + "?queryString", true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetRedirectWithInvalidRedirectParameter() throws Exception {
		UpdateLanguageAction updateLanguageAction = new UpdateLanguageAction();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"redirect",
			RandomTestUtil.randomString() + " " +
				RandomTestUtil.randomString());

		updateLanguageAction.getRedirect(
			mockHttpServletRequest, new ThemeDisplay(), _targetLocale);
	}

	@Test
	public void testGetRedirectWithNoRedirectParameter() throws Exception {
		UpdateLanguageAction updateLanguageAction = new UpdateLanguageAction();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_group.getPublicLayoutSet());
		themeDisplay.setSiteGroupId(_group.getGroupId());

		Assert.assertEquals(
			StringBundler.concat(
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				_group.getFriendlyURL(), StringPool.SLASH),
			updateLanguageAction.getRedirect(
				mockHttpServletRequest, themeDisplay, _targetLocale));
	}

	@Test(expected = NoSuchLayoutException.class)
	public void testGetRedirectWithNoSuchLayoutRedirectParameter()
		throws Exception {

		UpdateLanguageAction updateLanguageAction = new UpdateLanguageAction();

		String testURLSeparator = null;

		for (String urlSeparator :
				FriendlyURLResolverRegistryUtil.getURLSeparators()) {

			if (!Portal.FRIENDLY_URL_SEPARATOR.equals(urlSeparator) &&
				!VirtualLayoutConstants.CANONICAL_URL_SEPARATOR.equals(
					urlSeparator)) {

				testURLSeparator = urlSeparator;

				break;
			}
		}

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("p_l_id", "0");
		mockHttpServletRequest.setParameter(
			"redirect", testURLSeparator + "no-such-page");

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(_layout);

		updateLanguageAction.getRedirect(
			mockHttpServletRequest, themeDisplay, _targetLocale);
	}

	private String _getFriendlyURLSeparatorPart(Locale locale)
		throws Exception {

		return _getFriendlyURLSeparatorPart(
			locale, Portal.FRIENDLY_URL_SEPARATOR);
	}

	private String _getFriendlyURLSeparatorPart(Locale locale, String separator)
		throws Exception {

		Map<Locale, String> friendlyURLMap =
			_journalArticle.getFriendlyURLMap();

		return separator + friendlyURLMap.get(locale);
	}

	private void _testExecuteWithImpersonation() throws Exception {
		User impersonatedUser = UserTestUtil.addUser(_group.getGroupId());

		impersonatedUser.setLanguageId(LocaleUtil.toLanguageId(_sourceLocale));

		impersonatedUser = _userLocalService.updateUser(impersonatedUser);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		HttpSession httpSession = mockHttpServletRequest.getSession();

		httpSession.setAttribute(WebKeys.LOCALE, _sourceLocale);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setDoAsUserId(
			String.valueOf(impersonatedUser.getUserId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_group.getPublicLayoutSet());
		themeDisplay.setSignedIn(true);
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(impersonatedUser);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		String languageId = LocaleUtil.toLanguageId(_targetLocale);

		mockHttpServletRequest.setParameter("languageId", languageId);

		mockHttpServletRequest.setParameter(
			"redirect",
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
				_group.getFriendlyURL() + StringPool.SLASH);

		User user = TestPropsValues.getUser();

		String userLanguageId = user.getLanguageId();

		UpdateLanguageAction updateLanguageAction = new UpdateLanguageAction();

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		updateLanguageAction.execute(
			null, mockHttpServletRequest, mockHttpServletResponse);

		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertTrue(
			redirectedURL.contains("doAsUserLanguageId=" + languageId));

		Assert.assertEquals(
			_sourceLocale, httpSession.getAttribute(WebKeys.LOCALE));

		impersonatedUser = _userLocalService.getUser(
			impersonatedUser.getUserId());

		Assert.assertEquals(languageId, impersonatedUser.getLanguageId());

		user = _userLocalService.getUser(user.getUserId());

		Assert.assertEquals(userLanguageId, user.getLanguageId());

		_userLocalService.deleteUser(impersonatedUser);
	}

	private void _testGetRedirect(
			Locale sourceLocale, String sourceURL, Locale targetLocale,
			String targetURL, boolean virtualHost)
		throws Exception {

		_testGetRedirect(
			StringPool.BLANK, sourceLocale, sourceURL, targetLocale, targetURL,
			virtualHost);

		String contextPath = "/" + RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					_portal, "_pathContext", contextPath)) {

			_testGetRedirect(
				contextPath, sourceLocale, contextPath + sourceURL,
				targetLocale, targetURL, virtualHost);
		}
	}

	private void _testGetRedirect(
			String contextPath, Locale sourceLocale, String sourceURL,
			Locale targetLocale, String targetURL, boolean virtualHost)
		throws Exception {

		if (Validator.isNotNull(contextPath)) {
			targetURL = contextPath + targetURL;
		}

		if (virtualHost) {
			targetURL = Http.HTTP_WITH_SLASH + _VIRTUAL_HOSTNAME + targetURL;
		}

		ThemeDisplay themeDisplay = new ThemeDisplay();

		if (sourceLocale != null) {
			themeDisplay.setI18nLanguageId(sourceLocale.getLanguage());
			themeDisplay.setI18nPath("/" + sourceLocale.getLanguage());
			themeDisplay.setLocale(sourceLocale);
		}

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_group.getPublicLayoutSet());
		themeDisplay.setPathContext(contextPath);

		if (virtualHost) {
			themeDisplay.setPortalDomain(_VIRTUAL_HOSTNAME);
			themeDisplay.setPortalURL(Http.HTTP_WITH_SLASH + _VIRTUAL_HOSTNAME);
		}

		themeDisplay.setSiteGroupId(_group.getGroupId());

		_testGetRedirect(
			contextPath, targetURL, targetLocale, themeDisplay, sourceURL);

		if (sourceLocale != null) {
			_testGetRedirect(
				contextPath, targetURL, targetLocale, themeDisplay,
				"/" + sourceLocale.getLanguage() + sourceURL);
		}
	}

	private void _testGetRedirect(
			String contextPath, String expectedRedirect, Locale targetLocale,
			ThemeDisplay themeDisplay, String url)
		throws Exception {

		UpdateLanguageAction updateLanguageAction = new UpdateLanguageAction();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setContextPath(contextPath);

		HttpSession httpSession = mockHttpServletRequest.getSession();

		httpSession.setAttribute(WebKeys.LOCALE, targetLocale);

		mockHttpServletRequest.setParameter("redirect", url);

		Assert.assertEquals(
			expectedRedirect,
			updateLanguageAction.getRedirect(
				mockHttpServletRequest, themeDisplay, targetLocale));
	}

	private void _testGetRedirectWithControlPanelURL(boolean i18n)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		if (i18n) {
			themeDisplay.setI18nLanguageId(_sourceLocale.getLanguage());
			themeDisplay.setI18nPath("/" + _sourceLocale.getLanguage());
			themeDisplay.setLocale(_sourceLocale);
		}

		Layout controlPanelLayout = LayoutLocalServiceUtil.getLayout(
			PortalUtil.getControlPanelPlid(_group.getCompanyId()));

		themeDisplay.setLayout(controlPanelLayout);

		String controlPanelURL = StringBundler.concat(
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING,
			_group.getFriendlyURL(),
			VirtualLayoutConstants.CANONICAL_URL_SEPARATOR,
			GroupConstants.CONTROL_PANEL_FRIENDLY_URL,
			controlPanelLayout.getFriendlyURL());

		controlPanelURL += "?queryString";

		_testGetRedirect(
			StringPool.BLANK, controlPanelURL, _targetLocale, themeDisplay,
			controlPanelURL);

		if (i18n) {
			_testGetRedirect(
				StringPool.BLANK, controlPanelURL, _targetLocale, themeDisplay,
				"/" + _sourceLocale.getLanguage() + controlPanelURL);
		}
		else {
			_testGetRedirect(
				StringPool.BLANK,
				"/" + _sourceLocale.getLanguage() + controlPanelURL,
				_targetLocale, themeDisplay,
				"/" + _sourceLocale.getLanguage() + controlPanelURL);
		}
	}

	private void _testGetRedirectWithFriendlyURL(boolean i18n)
		throws Exception {

		_testGetRedirectWithFriendlyURL(i18n, "", "");
		_testGetRedirectWithFriendlyURL(
			i18n, _getFriendlyURLSeparatorPart(_sourceLocale),
			_getFriendlyURLSeparatorPart(_targetLocale));
		_testGetRedirectWithFriendlyURL(
			i18n,
			_getFriendlyURLSeparatorPart(
				_sourceLocale,
				FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE),
			_getFriendlyURLSeparatorPart(
				_targetLocale,
				FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE));
	}

	private void _testGetRedirectWithFriendlyURL(
			boolean i18n, String sourceFriendlyURLSeparatorPart,
			String targetFriendlyURLSeparatorPart)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		if (i18n) {
			themeDisplay.setI18nLanguageId(_sourceLocale.getLanguage());
			themeDisplay.setI18nPath("/" + _sourceLocale.getLanguage());
			themeDisplay.setLocale(_sourceLocale);
		}

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_group.getPublicLayoutSet());
		themeDisplay.setSiteGroupId(_group.getGroupId());

		String targetURL =
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
				_group.getFriendlyURL() + _layout.getFriendlyURL(_targetLocale);

		targetURL += targetFriendlyURLSeparatorPart + "?queryString";

		String sourceURL =
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
				_group.getFriendlyURL() + _layout.getFriendlyURL(_sourceLocale);

		sourceURL += sourceFriendlyURLSeparatorPart + "?queryString";

		_testGetRedirect(
			StringPool.BLANK, targetURL, _targetLocale, themeDisplay,
			sourceURL);
		_testGetRedirect(
			StringPool.BLANK, targetURL, _targetLocale, themeDisplay,
			"/" + _sourceLocale.getLanguage() + sourceURL);
	}

	private void _testGetRedirectWithGroupFriendlyURLWithPortletURLMapping()
		throws Exception {

		_testGetRedirectWithGroupFriendlyURLWithPortletURLMapping(
			"questions", _sourceLocale);
		_testGetRedirectWithGroupFriendlyURLWithPortletURLMapping(
			"questions", null);
		_testGetRedirectWithGroupFriendlyURLWithPortletURLMapping(
			"tags", _sourceLocale);
		_testGetRedirectWithGroupFriendlyURLWithPortletURLMapping("tags", null);
	}

	private void _testGetRedirectWithGroupFriendlyURLWithPortletURLMapping(
			String mapping, Locale sourceLocale)
		throws Exception {

		String originalGroupFriendlyURL = _group.getFriendlyURL();

		try {
			_group = _groupLocalService.updateFriendlyURL(
				_group.getGroupId(), StringPool.SLASH + mapping);

			String layoutFriendlyURL = StringUtil.toLowerCase(
				RandomTestUtil.randomString(
					LayoutFriendlyURLRandomizerBumper.INSTANCE));

			Locale locale = sourceLocale;

			if (locale == null) {
				locale = _defaultLocale;
			}

			for (Locale curLocale : Arrays.asList(locale, _targetLocale)) {
				_layout = _layoutLocalService.updateFriendlyURL(
					TestPropsValues.getUserId(), _layout.getPlid(),
					StringPool.SLASH + layoutFriendlyURL,
					LocaleUtil.toLanguageId(curLocale));
			}

			String sourceURL = StringBundler.concat(
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				_group.getFriendlyURL(), _layout.getFriendlyURL(locale),
				"?queryString");

			String targetURL = StringBundler.concat(
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				_group.getFriendlyURL(), _layout.getFriendlyURL(_targetLocale),
				"?queryString");

			_testGetRedirect(
				sourceLocale, sourceURL, _targetLocale, targetURL, false);
		}
		finally {
			_group = _groupLocalService.updateFriendlyURL(
				_group.getGroupId(), originalGroupFriendlyURL);
		}
	}

	private void _testGetRedirectWithLayoutFriendlyURL(boolean virtualHost)
		throws Exception {

		for (Locale locale : _availableLocales) {
			_testGetRedirectWithLayoutFriendlyURL(
				StringPool.BLANK, null, locale, virtualHost);

			if (!Objects.equals(_defaultLocale, locale)) {
				_testGetRedirectWithLayoutFriendlyURL(
					StringPool.BLANK, _defaultLocale, locale, virtualHost);
			}
		}
	}

	private void _testGetRedirectWithLayoutFriendlyURL(
			String path, Locale sourceLocale, Locale targetLocale,
			boolean virtualHost)
		throws Exception {

		String layoutFriendlyURL = _layout.getFriendlyURL();

		if (sourceLocale != null) {
			layoutFriendlyURL = _layout.getFriendlyURL(sourceLocale);
		}

		String sourceURL = StringBundler.concat(
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
			_group.getFriendlyURL(), layoutFriendlyURL, path, "?queryString");

		String targetURL = StringBundler.concat(
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
			_group.getFriendlyURL(), _layout.getFriendlyURL(targetLocale), path,
			"?queryString");

		_testGetRedirect(
			sourceLocale, sourceURL, targetLocale, targetURL, virtualHost);
	}

	private void _testGetRedirectWithLayoutFriendlyURLWithPortletURLMapping()
		throws Exception {

		_testGetRedirectWithLayoutFriendlyURLWithPortletURLMapping(
			"questions", _sourceLocale);
		_testGetRedirectWithLayoutFriendlyURLWithPortletURLMapping(
			"questions", null);
		_testGetRedirectWithLayoutFriendlyURLWithPortletURLMapping(
			"tags", _sourceLocale);
		_testGetRedirectWithLayoutFriendlyURLWithPortletURLMapping(
			"tags", null);
	}

	private void _testGetRedirectWithLayoutFriendlyURLWithPortletURLMapping(
			String mapping, Locale sourceLocale)
		throws Exception {

		String layoutFriendlyURL = StringBundler.concat(
			StringUtil.toLowerCase(
				RandomTestUtil.randomString(
					LayoutFriendlyURLRandomizerBumper.INSTANCE)),
			StringPool.DASH, mapping, StringPool.DASH,
			StringUtil.toLowerCase(
				RandomTestUtil.randomString(
					LayoutFriendlyURLRandomizerBumper.INSTANCE)));

		String languageId = "en_US";

		if (sourceLocale != null) {
			languageId = LocaleUtil.toLanguageId(sourceLocale);
		}

		_layout = _layoutLocalService.updateFriendlyURL(
			TestPropsValues.getUserId(), _layout.getPlid(),
			StringPool.SLASH + layoutFriendlyURL, languageId);

		_testGetRedirect(
			sourceLocale,
			StringBundler.concat(
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				_group.getFriendlyURL(), layoutFriendlyURL, "?queryString"),
			_targetLocale,
			StringBundler.concat(
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				_group.getFriendlyURL(), _layout.getFriendlyURL(_targetLocale),
				"?queryString"),
			false);
	}

	private void _testGetRedirectWithoutLayoutFriendlyURLWithPortletURLMapping(
			Locale sourceLocale)
		throws Exception {

		String mappingPart = "/tags/" + RandomTestUtil.randomString();

		_testGetRedirect(
			sourceLocale,
			StringBundler.concat(
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				_group.getFriendlyURL(), mappingPart),
			_targetLocale,
			StringBundler.concat(
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				_group.getFriendlyURL(), _layout.getFriendlyURL(_targetLocale),
				mappingPart),
			false);
	}

	private void _testGetRedirectWithPortletFriendlyURL(Locale sourceLocale)
		throws Exception {

		Map<Locale, String> friendlyURLMap =
			_journalArticle.getFriendlyURLMap();

		Locale defaultLocale = LocaleUtil.fromLanguageId(
			_group.getDefaultLanguageId());

		String path =
			_PORTLET_FRIENDLY_URL_PART_ASSET_PUBLISHER +
				friendlyURLMap.get(defaultLocale);

		_testGetRedirectWithLayoutFriendlyURL(
			path, sourceLocale, _targetLocale, false);
	}

	private void _testGetRedirectWithPortletURLMapping(Locale sourceLocale)
		throws Exception {

		_testGetRedirectWithLayoutFriendlyURL(
			"/tags/" + RandomTestUtil.randomString(), sourceLocale,
			_targetLocale, false);
	}

	private void _updateLayoutFriendlyURL(String suffix) throws Exception {
		for (Locale locale : _availableLocales) {
			_layout = _layoutLocalService.updateFriendlyURL(
				TestPropsValues.getUserId(), _layout.getPlid(),
				_layout.getFriendlyURL(locale) + suffix,
				LocaleUtil.toLanguageId(locale));
		}
	}

	private static final String _PORTLET_FRIENDLY_URL_PART_ASSET_PUBLISHER =
		"/-/asset_publisher/instanceID/content/";

	private static final String _VIRTUAL_HOSTNAME = "test.com";

	private static final List<Locale> _availableLocales = Arrays.asList(
		LocaleUtil.GERMANY, LocaleUtil.FRANCE, LocaleUtil.UK, LocaleUtil.US);
	private static final Locale _defaultLocale = LocaleUtil.US;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private JournalArticle _journalArticle;
	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

	private final Locale _sourceLocale = LocaleUtil.FRANCE;
	private final Locale _sourceUKLocale = LocaleUtil.UK;
	private final Locale _targetLocale = LocaleUtil.GERMANY;

	@Inject
	private UserLocalService _userLocalService;

}