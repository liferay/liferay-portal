/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.action.UpdateLanguageAction;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.VirtualLayoutConstants;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LanguageIds;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

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
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

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
	public void testGetRedirect() throws Exception {
		_testGetRedirectWithControlPanelURL(false);
		_testGetRedirectWithControlPanelURL(true);
		_testGetRedirectWithFriendlyURL(false);
		_testGetRedirectWithFriendlyURL(true);
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

		_assertGetRedirectWithLayoutFriendlyURL(false);

		LayoutSet layoutSet = _layout.getLayoutSet();

		layoutSet.setVirtualHostnames(
			TreeMapBuilder.put(
				_VIRTUAL_HOSTNAME, StringPool.BLANK
			).build());

		_assertGetRedirectWithLayoutFriendlyURL(true);
	}

	@Test
	public void testGetRedirectWithFriendlyURLWithVirtualHost()
		throws Exception {

		LayoutSet layoutSet = _layout.getLayoutSet();

		layoutSet.setVirtualHostnames(
			TreeMapBuilder.put(
				_VIRTUAL_HOSTNAME, StringPool.BLANK
			).build());

		_assertGetRedirect(
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

	private void _assertGetRedirect(
			Locale sourceLocale, String sourceURL, Locale targetLocale,
			String targetURL, boolean virtualHost)
		throws Exception {

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

		if (virtualHost) {
			themeDisplay.setPortalDomain(_VIRTUAL_HOSTNAME);
			themeDisplay.setPortalURL(Http.HTTP_WITH_SLASH + _VIRTUAL_HOSTNAME);
		}

		themeDisplay.setSiteGroupId(_group.getGroupId());

		_assertRedirect(targetURL, targetLocale, themeDisplay, sourceURL);

		if (sourceLocale != null) {
			_assertRedirect(
				targetURL, targetLocale, themeDisplay,
				"/" + sourceLocale.getLanguage() + sourceURL);
		}
	}

	private void _assertGetRedirectWithLayoutFriendlyURL(boolean virtualHost)
		throws Exception {

		for (Locale locale : _availableLocales) {
			_assertGetRedirectWithLayoutFriendlyURL(
				StringPool.BLANK, null, locale, virtualHost);

			if (!Objects.equals(_defaultLocale, locale)) {
				_assertGetRedirectWithLayoutFriendlyURL(
					StringPool.BLANK, _defaultLocale, locale, virtualHost);
			}
		}
	}

	private void _assertGetRedirectWithLayoutFriendlyURL(
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

		_assertGetRedirect(
			sourceLocale, sourceURL, targetLocale, targetURL, virtualHost);
	}

	private void _assertRedirect(
			String expectedRedirect, Locale targetLocale,
			ThemeDisplay themeDisplay, String url)
		throws Exception {

		UpdateLanguageAction updateLanguageAction = new UpdateLanguageAction();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		HttpSession httpSession = mockHttpServletRequest.getSession();

		httpSession.setAttribute(WebKeys.LOCALE, targetLocale);

		mockHttpServletRequest.setParameter("redirect", url);

		Assert.assertEquals(
			expectedRedirect,
			updateLanguageAction.getRedirect(
				mockHttpServletRequest, themeDisplay, targetLocale));
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

		_assertRedirect(
			controlPanelURL, _targetLocale, themeDisplay, controlPanelURL);

		if (i18n) {
			_assertRedirect(
				controlPanelURL, _targetLocale, themeDisplay,
				"/" + _sourceLocale.getLanguage() + controlPanelURL);
		}
		else {
			_assertRedirect(
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

		_assertRedirect(targetURL, _targetLocale, themeDisplay, sourceURL);
		_assertRedirect(
			targetURL, _targetLocale, themeDisplay,
			"/" + _sourceLocale.getLanguage() + sourceURL);
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

		_assertGetRedirectWithLayoutFriendlyURL(
			path, sourceLocale, _targetLocale, false);
	}

	private void _testGetRedirectWithPortletURLMapping(Locale sourceLocale)
		throws Exception {

		_assertGetRedirectWithLayoutFriendlyURL(
			"/tags/tagname", sourceLocale, _targetLocale, false);
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
	private JournalArticle _journalArticle;
	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private final Locale _sourceLocale = LocaleUtil.FRANCE;
	private final Locale _sourceUKLocale = LocaleUtil.UK;
	private final Locale _targetLocale = LocaleUtil.GERMANY;

}