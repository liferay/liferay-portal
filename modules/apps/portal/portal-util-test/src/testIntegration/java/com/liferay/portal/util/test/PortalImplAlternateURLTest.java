/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutFriendlyURLRandomizerBumper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletPreferences;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class PortalImplAlternateURLTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws PortalException {
		_defaultLocale = LocaleUtil.getDefault();
		_defaultPrependStyle = PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE;

		LocaleUtil.setDefault(
			LocaleUtil.US.getLanguage(), LocaleUtil.US.getCountry(),
			LocaleUtil.US.getVariant());

		_virtualHostLocalService.updateVirtualHosts(
			TestPropsValues.getCompanyId(), 0,
			TreeMapBuilder.put(
				"localhost", StringPool.BLANK
			).build());
	}

	@AfterClass
	public static void tearDownClass() {
		LocaleUtil.setDefault(
			_defaultLocale.getLanguage(), _defaultLocale.getCountry(),
			_defaultLocale.getVariant());

		TestPropsUtil.set(
			PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE,
			GetterUtil.getString(_defaultPrependStyle));
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAlternateURLDoesNotHaveLocaleInHomeLayout()
		throws Exception {

		Collection<Locale> availableLocales = Arrays.asList(
			LocaleUtil.BRAZIL, LocaleUtil.US);
		Locale defaultLocale = LocaleUtil.US;

		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), availableLocales, defaultLocale);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			HashMapBuilder.put(
				LocaleUtil.BRAZIL, RandomTestUtil.randomString()
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.BRAZIL,
				StringPool.SLASH.concat(_getRandomFriendlyURL())
			).put(
				LocaleUtil.US, StringPool.SLASH.concat(_getRandomFriendlyURL())
			).build());

		ThemeDisplay themeDisplay = _getThemeDisplay(_group, layout);

		_testAlternateURLWithHomeLayout(
			availableLocales, defaultLocale, themeDisplay, 0);
		_testAlternateURLWithHomeLayout(
			availableLocales, defaultLocale, themeDisplay, 1);
		_testAlternateURLWithHomeLayout(
			availableLocales, defaultLocale, themeDisplay, 2);
		_testAlternateURLWithHomeLayout(
			availableLocales, defaultLocale, themeDisplay, 3);
	}

	@Test
	public void testAlternateURLsMatchSiteAvailableLocalesFromSitemap()
		throws Exception {

		_testAlternateURLsForSitemapFromGuestGroup(
			"localhost",
			Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY),
			LocaleUtil.US);
	}

	@Test
	public void testAlternateURLWithAssetDisplayPageEntry() throws Exception {
		Collection<Locale> availableLocales = Arrays.asList(
			LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY);
		Locale defaultLocale = LocaleUtil.US;

		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), availableLocales, defaultLocale);

		Map<Locale, String> friendlyURLMap = HashMapBuilder.put(
			LocaleUtil.GERMANY, _getRandomFriendlyURL()
		).put(
			LocaleUtil.SPAIN, _getRandomFriendlyURL()
		).put(
			LocaleUtil.US, _getRandomFriendlyURL()
		).build();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, friendlyURLMap);

		ThemeDisplay themeDisplay = _getThemeDisplay(
			_group, _getAssetDisplayPageEntryLayout(journalArticle));

		_testAlternateURLWithAssetDisplayPageEntry(
			availableLocales, defaultLocale, friendlyURLMap, 0,
			journalArticle.getResourcePrimKey(), themeDisplay);
		_testAlternateURLWithAssetDisplayPageEntry(
			availableLocales, defaultLocale, friendlyURLMap, 1,
			journalArticle.getResourcePrimKey(), themeDisplay);
		_testAlternateURLWithAssetDisplayPageEntry(
			availableLocales, defaultLocale, friendlyURLMap, 2,
			journalArticle.getResourcePrimKey(), themeDisplay);
		_testAlternateURLWithAssetDisplayPageEntry(
			availableLocales, defaultLocale, friendlyURLMap, 3,
			journalArticle.getResourcePrimKey(), themeDisplay);
	}

	@Test
	public void testAlternateURLWithFriendlyURL() throws Exception {
		_testAlternateURLWithFriendlyURL(
			"liferay.com",
			Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY),
			LocaleUtil.US, LocaleUtil.BRAZIL, "/pt-BR");
		_testAlternateURLWithFriendlyURL(
			"liferay.com",
			Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY),
			LocaleUtil.US, LocaleUtil.SPAIN, "/es");
		_testAlternateURLWithFriendlyURL(
			"localhost",
			Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY),
			LocaleUtil.US, LocaleUtil.BRAZIL, "/pt-BR");
		_testAlternateURLWithFriendlyURL(
			"localhost",
			Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY),
			LocaleUtil.US, LocaleUtil.SPAIN, "/es");
	}

	@Test
	@TestInfo("LPD-43082")
	public void testAlternateURLWithLayout() throws Exception {
		Collection<Locale> availableLocales = Arrays.asList(
			LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY);
		Locale defaultLocale = LocaleUtil.US;

		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), availableLocales, defaultLocale);

		_testAlternateURLWithLayout(
			availableLocales, defaultLocale,
			HashMapBuilder.put(
				LocaleUtil.GERMANY,
				StringPool.SLASH.concat(_getRandomFriendlyURL())
			).put(
				LocaleUtil.SPAIN,
				StringPool.SLASH.concat(_getRandomFriendlyURL())
			).put(
				LocaleUtil.US, StringPool.SLASH.concat(_getRandomFriendlyURL())
			).build());

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		_virtualHostLocalService.updateVirtualHosts(
			_group.getCompanyId(), layoutSet.getLayoutSetId(),
			TreeMapBuilder.put(
				"test.com", StringPool.BLANK
			).build());

		_testAlternateURLWithLayout(
			availableLocales, defaultLocale,
			HashMapBuilder.put(
				LocaleUtil.GERMANY, "/de" + _getRandomFriendlyURL()
			).put(
				LocaleUtil.SPAIN, "/es" + _getRandomFriendlyURL()
			).put(
				LocaleUtil.US, "/en" + _getRandomFriendlyURL()
			).build());
	}

	@Test
	public void testAlternateURLWithUrlSeparator() throws Exception {
		_testAlternateURLWithUrlSeparator("/g/");
		_testAlternateURLWithUrlSeparator("/p/");
	}

	@Test
	public void testAlternativeVirtualHostDefaultPortalLocaleAlternateURL()
		throws Exception {

		_testAlternateURLWithVirtualHosts(
			"test.com", null, null, LocaleUtil.US, StringPool.BLANK);
	}

	@Test
	public void testAlternativeVirtualHostLocalizedSiteCustomSiteLocaleAlternateURL()
		throws Exception {

		_testAlternateURLWithVirtualHosts(
			"test.com",
			Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY),
			LocaleUtil.SPAIN, LocaleUtil.US, "/en");
	}

	@Test
	public void testCustomPortalLocaleAlternateURL() throws Exception {
		_testAlternateURL("localhost", null, null, LocaleUtil.SPAIN, "/es");
	}

	@Test
	public void testDefaultPortalLocaleAlternateURL() throws Exception {
		_testAlternateURL(
			"localhost", null, null, LocaleUtil.US, StringPool.BLANK);
	}

	@Test
	public void testLocalizedSiteCustomSiteLocaleAlternateURL()
		throws Exception {

		_testAlternateURL(
			"localhost",
			Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY),
			LocaleUtil.SPAIN, LocaleUtil.US, "/en");
	}

	@Test
	public void testLocalizedSiteDefaultSiteLocaleAlternateURL()
		throws Exception {

		_testAlternateURL(
			"localhost",
			Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY),
			LocaleUtil.SPAIN, LocaleUtil.SPAIN, StringPool.BLANK);
	}

	@Test
	public void testNonlocalhostCustomPortalLocaleAlternateURL()
		throws Exception {

		_testAlternateURL("liferay.com", null, null, LocaleUtil.SPAIN, "/es");
	}

	@Test
	public void testNonlocalhostDefaultPortalLocaleAlternateURL()
		throws Exception {

		_testAlternateURL(
			"liferay.com", null, null, LocaleUtil.US, StringPool.BLANK);
	}

	@Test
	public void testNonlocalhostLocalizedSiteCustomSiteLocaleAlternateURL()
		throws Exception {

		_testAlternateURL(
			"liferay.com",
			Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY),
			LocaleUtil.SPAIN, LocaleUtil.US, "/en");
	}

	@Test
	public void testNonlocalhostLocalizedSiteDefaultSiteLocaleAlternateURL()
		throws Exception {

		_testAlternateURL(
			"liferay.com",
			Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY),
			LocaleUtil.SPAIN, LocaleUtil.SPAIN, StringPool.BLANK);
	}

	private String _generateAssetDisplayPageEntryURL(
		Locale defaultLocale, String friendlyURL, String groupFriendlyURL,
		Locale locale, String portalURL,
		PortletPreferences portletPreferences) {

		return StringBundler.concat(
			portalURL, _getI18nPath(defaultLocale, locale, portletPreferences),
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
			groupFriendlyURL,
			FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE,
			friendlyURL);
	}

	private String _generateAssetPublisherContentURL(
		String portalDomain, String languageId, String groupFriendlyURL) {

		return StringBundler.concat(
			"http://", portalDomain, languageId,
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
			Portal.FRIENDLY_URL_SEPARATOR, "asset_publisher", groupFriendlyURL,
			"/content/content-title");
	}

	private String _generateHomeLayoutURL(
		Locale defaultLocale, Locale locale, String portalURL,
		PortletPreferences portletPreferences) {

		String i18nPath = _getI18nPath(
			defaultLocale, locale, portletPreferences);

		if (Validator.isNull(i18nPath)) {
			return portalURL;
		}

		return StringBundler.concat(portalURL, i18nPath, StringPool.SLASH);
	}

	private String _generateLayoutURL(
		Locale defaultLocale, String friendlyURL, Group group, Locale locale,
		String portalURL, PortletPreferences portletPreferences) {

		LayoutSet layoutSet = group.getPublicLayoutSet();

		TreeMap<String, String> virtualHostnames =
			layoutSet.getVirtualHostnames();

		if (virtualHostnames.isEmpty()) {
			return _generateLayoutURL(
				defaultLocale, friendlyURL, _group.getFriendlyURL(), locale,
				portalURL, portletPreferences);
		}

		return StringBundler.concat(
			"http://", virtualHostnames.firstKey(), ":8080",
			_getI18nPath(defaultLocale, locale, portletPreferences),
			friendlyURL);
	}

	private String _generateLayoutURL(
		Locale defaultLocale, String friendlyURL, String groupFriendlyURL,
		Locale locale, String portalURL,
		PortletPreferences portletPreferences) {

		return StringBundler.concat(
			portalURL, _getI18nPath(defaultLocale, locale, portletPreferences),
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
			groupFriendlyURL, friendlyURL);
	}

	private String _generateURL(
		String portalDomain, String languageId, String groupFriendlyURL,
		String layoutFriendlyURL) {

		return StringBundler.concat(
			"http://", portalDomain, languageId,
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
			groupFriendlyURL, layoutFriendlyURL);
	}

	private Layout _getAssetDisplayPageEntryLayout(
			JournalArticle journalArticle)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				journalArticle.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getDDMStructureId(), true,
				WorkflowConstants.STATUS_APPROVED);

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			TestPropsValues.getUserId(), journalArticle.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC,
			ServiceContextTestUtil.getServiceContext(
				journalArticle.getGroupId()));

		return _layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid());
	}

	private String _getI18nPath(
		Locale defaultLocale, Locale locale,
		PortletPreferences portletPreferences) {

		String i18nPath = StringPool.BLANK;

		int localePrependFriendlyURLStyle = PrefsPropsUtil.getInteger(
			portletPreferences, PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE);

		if ((localePrependFriendlyURLStyle == 2) ||
			((localePrependFriendlyURLStyle != 0) &&
			 !locale.equals(defaultLocale))) {

			i18nPath = StringPool.SLASH + locale.getLanguage();
		}

		return i18nPath;
	}

	private String _getRandomFriendlyURL() {
		return _friendlyURLNormalizer.normalize(
			RandomTestUtil.randomString(
				LayoutFriendlyURLRandomizerBumper.INSTANCE));
	}

	private ThemeDisplay _getThemeDisplay(Group group, Layout layout)
		throws Exception {

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			company, _group, layout);

		themeDisplay.setPortalDomain("localhost");
		themeDisplay.setPortalURL(company.getPortalURL(group.getGroupId()));
		themeDisplay.setServerName("localhost");
		themeDisplay.setServerPort(8080);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		themeDisplay.setRequest(mockHttpServletRequest);

		return themeDisplay;
	}

	private ThemeDisplay _getThemeDisplay(Group group, String portalURL)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayoutSet(group.getPublicLayoutSet());
		themeDisplay.setPortalDomain(HttpComponentsUtil.getDomain(portalURL));
		themeDisplay.setPortalURL(portalURL);
		themeDisplay.setSiteGroupId(group.getGroupId());

		return themeDisplay;
	}

	private ThemeDisplay _getThemeDisplayWithVirtualHosts(
			Group group, String portalURL)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		LayoutSet layoutSet = group.getPublicLayoutSet();

		layoutSet.setVirtualHostnames(
			TreeMapBuilder.put(
				"liferay.com", StringPool.BLANK
			).put(
				"test.com", LocaleUtil.US.toString()
			).build());

		themeDisplay.setLayoutSet(layoutSet);

		themeDisplay.setPortalDomain(HttpComponentsUtil.getDomain(portalURL));
		themeDisplay.setPortalURL(portalURL);
		themeDisplay.setSiteGroupId(group.getGroupId());

		return themeDisplay;
	}

	private void _testAlternateURL(
			String portalDomain, Collection<Locale> groupAvailableLocales,
			Locale groupDefaultLocale, Locale alternateLocale,
			String expectedI18nPath)
		throws Exception {

		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), groupAvailableLocales, groupDefaultLocale);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), RandomTestUtil.randomString(), false);

		String canonicalURL = _generateURL(
			portalDomain, StringPool.BLANK, _group.getFriendlyURL(),
			layout.getFriendlyURL());

		String expectedAlternateURL = _generateURL(
			portalDomain, expectedI18nPath, _group.getFriendlyURL(),
			layout.getFriendlyURL());

		Assert.assertEquals(
			expectedAlternateURL,
			_portal.getAlternateURL(
				canonicalURL, _getThemeDisplay(_group, canonicalURL),
				alternateLocale, layout));

		String canonicalAssetPublisherContentURL =
			_generateAssetPublisherContentURL(
				portalDomain, StringPool.BLANK, _group.getFriendlyURL());

		String expectedAssetPublisherContentAlternateURL =
			_generateAssetPublisherContentURL(
				portalDomain, expectedI18nPath, _group.getFriendlyURL());

		Assert.assertEquals(
			expectedAssetPublisherContentAlternateURL,
			_portal.getAlternateURL(
				canonicalAssetPublisherContentURL,
				_getThemeDisplay(_group, canonicalAssetPublisherContentURL),
				alternateLocale, layout));

		TestPropsUtil.set(PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE, "2");

		Assert.assertEquals(
			expectedAlternateURL,
			_portal.getAlternateURL(
				canonicalURL, _getThemeDisplay(_group, canonicalURL),
				alternateLocale, layout));

		Assert.assertEquals(
			expectedAssetPublisherContentAlternateURL,
			_portal.getAlternateURL(
				canonicalAssetPublisherContentURL,
				_getThemeDisplay(_group, canonicalAssetPublisherContentURL),
				alternateLocale, layout));
	}

	private void _testAlternateURLsForSitemapFromGuestGroup(
			String portalDomain, Collection<Locale> groupAvailableLocales,
			Locale groupDefaultLocale)
		throws Exception {

		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), groupAvailableLocales, groupDefaultLocale);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), RandomTestUtil.randomString(), false);

		String canonicalURL = _generateURL(
			portalDomain, StringPool.BLANK, _group.getFriendlyURL(),
			layout.getFriendlyURL());

		Map<Locale, String> alternateURLs = _portal.getAlternateURLs(
			canonicalURL,
			_getThemeDisplay(
				_groupLocalService.getGroup(
					_group.getCompanyId(), GroupConstants.GUEST),
				canonicalURL),
			layout);

		Assert.assertEquals(
			alternateURLs.toString(), groupAvailableLocales.size(),
			alternateURLs.size());

		for (Locale locale : groupAvailableLocales) {
			Assert.assertTrue(
				alternateURLs.toString(), alternateURLs.containsKey(locale));
		}
	}

	private void _testAlternateURLWithAssetDisplayPageEntry(
			Collection<Locale> availableLocales, Locale defaultLocale,
			Map<Locale, String> friendlyURLMap, int prependFriendlyURLStyle,
			long resourcePrimKey, ThemeDisplay themeDisplay)
		throws Exception {

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			themeDisplay.getCompanyId());

		try {
			portletPreferences.setValue(
				PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE,
				String.valueOf(prependFriendlyURLStyle));

			portletPreferences.store();

			for (Locale alternateLocale : availableLocales) {
				String expectedAlternateURL = _generateAssetDisplayPageEntryURL(
					defaultLocale, friendlyURLMap.get(alternateLocale),
					_group.getFriendlyURL(), alternateLocale,
					themeDisplay.getPortalURL(), portletPreferences);

				String canonicalURL =
					_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
						new InfoItemReference(
							JournalArticle.class.getName(),
							new ClassPKInfoItemIdentifier(resourcePrimKey)),
						alternateLocale, themeDisplay);

				Assert.assertEquals(
					expectedAlternateURL,
					_portal.getAlternateURL(
						canonicalURL, themeDisplay, alternateLocale,
						themeDisplay.getLayout()));
			}
		}
		finally {
			portletPreferences.reset(
				PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE);
		}
	}

	private void _testAlternateURLWithFriendlyURL(
			String portalDomain, Collection<Locale> groupAvailableLocales,
			Locale groupDefaultLocale, Locale alternateLocale,
			String expectedI18nPath)
		throws Exception {

		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), groupAvailableLocales, groupDefaultLocale);

		Map<Locale, String> nameMap = new HashMap<>();
		Map<Locale, String> friendlyURLMap = new HashMap<>();

		for (Locale availableLocale : groupAvailableLocales) {
			nameMap.put(availableLocale, RandomTestUtil.randomString());
			friendlyURLMap.put(
				availableLocale, StringPool.SLASH + _getRandomFriendlyURL());
		}

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false, nameMap, friendlyURLMap);

		String canonicalURL = _generateURL(
			portalDomain, StringPool.BLANK, _group.getFriendlyURL(),
			layout.getFriendlyURL());

		String expectedAlternateURL = _generateURL(
			portalDomain, expectedI18nPath, _group.getFriendlyURL(),
			layout.getFriendlyURL(alternateLocale));

		Assert.assertEquals(
			expectedAlternateURL,
			_portal.getAlternateURL(
				canonicalURL, _getThemeDisplay(_group, canonicalURL),
				alternateLocale, layout));

		String canonicalAssetPublisherContentURL =
			_generateAssetPublisherContentURL(
				portalDomain, StringPool.BLANK, _group.getFriendlyURL());

		String expectedAssetPublisherContentAlternateURL =
			_generateAssetPublisherContentURL(
				portalDomain, expectedI18nPath, _group.getFriendlyURL());

		Assert.assertEquals(
			expectedAssetPublisherContentAlternateURL,
			_portal.getAlternateURL(
				canonicalAssetPublisherContentURL,
				_getThemeDisplay(_group, canonicalAssetPublisherContentURL),
				alternateLocale, layout));

		TestPropsUtil.set(PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE, "2");

		Assert.assertEquals(
			expectedAlternateURL,
			_portal.getAlternateURL(
				canonicalURL, _getThemeDisplay(_group, canonicalURL),
				alternateLocale, layout));

		Assert.assertEquals(
			expectedAssetPublisherContentAlternateURL,
			_portal.getAlternateURL(
				canonicalAssetPublisherContentURL,
				_getThemeDisplay(_group, canonicalAssetPublisherContentURL),
				alternateLocale, layout));
	}

	private void _testAlternateURLWithHomeLayout(
			Collection<Locale> availableLocales, Locale defaultLocale,
			ThemeDisplay themeDisplay, int prependFriendlyURLStyle)
		throws Exception {

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			themeDisplay.getCompanyId());

		try {
			portletPreferences.setValue(
				PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE,
				String.valueOf(prependFriendlyURLStyle));

			portletPreferences.store();

			String canonicalURL = _generateHomeLayoutURL(
				defaultLocale, defaultLocale, themeDisplay.getPortalURL(),
				portletPreferences);

			for (Locale alternateLocale : availableLocales) {
				String expectedAlternateURL = _generateHomeLayoutURL(
					defaultLocale, alternateLocale, themeDisplay.getPortalURL(),
					portletPreferences);

				Assert.assertEquals(
					expectedAlternateURL,
					_portal.getAlternateURL(
						canonicalURL, themeDisplay, alternateLocale,
						themeDisplay.getLayout()));
			}
		}
		finally {
			portletPreferences.reset(
				PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE);
		}
	}

	private void _testAlternateURLWithLayout(
			Collection<Locale> availableLocales, Locale defaultLocale,
			Map<Locale, String> friendlyURLMap)
		throws Exception {

		ThemeDisplay themeDisplay = _getThemeDisplay(
			_group,
			LayoutTestUtil.addTypePortletLayout(
				_group.getGroupId(), false,
				HashMapBuilder.put(
					LocaleUtil.GERMANY, RandomTestUtil.randomString()
				).put(
					LocaleUtil.SPAIN, RandomTestUtil.randomString()
				).put(
					LocaleUtil.US, RandomTestUtil.randomString()
				).build(),
				friendlyURLMap));

		_testAlternateURLWithLayout(
			availableLocales, defaultLocale, friendlyURLMap, 0, themeDisplay);
		_testAlternateURLWithLayout(
			availableLocales, defaultLocale, friendlyURLMap, 1, themeDisplay);
		_testAlternateURLWithLayout(
			availableLocales, defaultLocale, friendlyURLMap, 2, themeDisplay);
		_testAlternateURLWithLayout(
			availableLocales, defaultLocale, friendlyURLMap, 3, themeDisplay);
	}

	private void _testAlternateURLWithLayout(
			Collection<Locale> availableLocales, Locale defaultLocale,
			Map<Locale, String> friendlyURLMap, int prependFriendlyURLStyle,
			ThemeDisplay themeDisplay)
		throws Exception {

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			themeDisplay.getCompanyId());

		try {
			portletPreferences.setValue(
				PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE,
				String.valueOf(prependFriendlyURLStyle));

			portletPreferences.store();

			String canonicalURL = _generateLayoutURL(
				defaultLocale, friendlyURLMap.get(defaultLocale), _group,
				defaultLocale, themeDisplay.getPortalURL(), portletPreferences);

			for (Locale alternateLocale : availableLocales) {
				String expectedAlternateURL = _generateLayoutURL(
					defaultLocale, friendlyURLMap.get(alternateLocale), _group,
					alternateLocale, themeDisplay.getPortalURL(),
					portletPreferences);

				Assert.assertEquals(
					expectedAlternateURL,
					_portal.getAlternateURL(
						canonicalURL, themeDisplay, alternateLocale,
						themeDisplay.getLayout()));
			}
		}
		finally {
			portletPreferences.reset(
				PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE);
		}
	}

	private void _testAlternateURLWithUrlSeparator(String urlSeparator)
		throws Exception {

		_group = GroupTestUtil.addGroup();

		Locale locale = LocaleUtil.SPAIN;

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Assert.assertEquals(
			_generateURL(
				"localhost", StringPool.SLASH + locale.getLanguage(),
				_group.getFriendlyURL(), urlSeparator),
			_portal.getAlternateURL(
				_generateURL(
					"localhost", StringPool.BLANK, _group.getFriendlyURL(),
					urlSeparator),
				_getThemeDisplay(_group, layout), locale, layout));
	}

	private void _testAlternateURLWithVirtualHosts(
			String portalDomain, Collection<Locale> groupAvailableLocales,
			Locale groupDefaultLocale, Locale alternateLocale,
			String expectedI18nPath)
		throws Exception {

		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), groupAvailableLocales, groupDefaultLocale);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), RandomTestUtil.randomString(), false);

		String canonicalURL = _generateURL(
			portalDomain, StringPool.BLANK, _group.getFriendlyURL(),
			layout.getFriendlyURL());

		String expectedAlternateURL = _generateURL(
			portalDomain, expectedI18nPath, _group.getFriendlyURL(),
			layout.getFriendlyURL());

		Assert.assertEquals(
			expectedAlternateURL,
			_portal.getAlternateURL(
				canonicalURL, _getThemeDisplay(_group, canonicalURL),
				alternateLocale, layout));

		String canonicalAssetPublisherContentURL =
			_generateAssetPublisherContentURL(
				portalDomain, StringPool.BLANK, _group.getFriendlyURL());

		String expectedAssetPublisherContentAlternateURL =
			_generateAssetPublisherContentURL(
				portalDomain, expectedI18nPath, _group.getFriendlyURL());

		Assert.assertEquals(
			expectedAssetPublisherContentAlternateURL,
			_portal.getAlternateURL(
				canonicalAssetPublisherContentURL,
				_getThemeDisplayWithVirtualHosts(
					_group, canonicalAssetPublisherContentURL),
				alternateLocale, layout));

		TestPropsUtil.set(PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE, "2");

		Assert.assertEquals(
			expectedAlternateURL,
			_portal.getAlternateURL(
				canonicalURL,
				_getThemeDisplayWithVirtualHosts(_group, canonicalURL),
				alternateLocale, layout));

		Assert.assertEquals(
			expectedAssetPublisherContentAlternateURL,
			_portal.getAlternateURL(
				canonicalAssetPublisherContentURL,
				_getThemeDisplayWithVirtualHosts(
					_group, canonicalAssetPublisherContentURL),
				alternateLocale, layout));
	}

	private static Locale _defaultLocale;
	private static int _defaultPrependStyle;

	@Inject
	private static VirtualHostLocalService _virtualHostLocalService;

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

}