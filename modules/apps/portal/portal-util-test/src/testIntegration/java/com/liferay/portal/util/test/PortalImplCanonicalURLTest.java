/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class PortalImplCanonicalURLTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws PortalException {
		_originalVirtualHostsDefaultSiteName =
			ReflectionTestUtil.getAndSetFieldValue(
				PropsValues.class, "VIRTUAL_HOSTS_DEFAULT_SITE_NAME", "Guest");
		_originalWebServerHTTPPort = ReflectionTestUtil.getAndSetFieldValue(
			PropsValues.class, "WEB_SERVER_HTTP_PORT", -1);
		_originalWebServerHTTPSPort = ReflectionTestUtil.getAndSetFieldValue(
			PropsValues.class, "WEB_SERVER_HTTPS_PORT", -1);

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

		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "VIRTUAL_HOSTS_DEFAULT_SITE_NAME",
			_originalVirtualHostsDefaultSiteName);
		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "WEB_SERVER_HTTP_PORT",
			_originalWebServerHTTPPort);
		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "WEB_SERVER_HTTPS_PORT",
			_originalWebServerHTTPSPort);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout1 = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			HashMapBuilder.put(
				LocaleUtil.GERMANY, "Zuhause1"
			).put(
				LocaleUtil.SPAIN, "Casa1"
			).put(
				LocaleUtil.US, "Home1"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.GERMANY, "/zuhause1"
			).put(
				LocaleUtil.SPAIN, "/casa1"
			).put(
				LocaleUtil.US, "/home1"
			).build());

		_layout2 = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			HashMapBuilder.put(
				LocaleUtil.GERMANY, "Zuhause2"
			).put(
				LocaleUtil.SPAIN, "Casa2"
			).put(
				LocaleUtil.US, "Home2"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.GERMANY, "/zuhause2"
			).put(
				LocaleUtil.SPAIN, "/casa2"
			).put(
				LocaleUtil.US, "/home2"
			).build());

		_layout3 = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			HashMapBuilder.put(
				LocaleUtil.GERMANY, _group.getName(LocaleUtil.GERMANY)
			).put(
				LocaleUtil.SPAIN, _group.getName(LocaleUtil.SPAIN)
			).put(
				LocaleUtil.US, _group.getName(LocaleUtil.US)
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, _group.getFriendlyURL()
			).build());

		_layout4 = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			HashMapBuilder.put(
				LocaleUtil.US, "weben"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, "/weben"
			).build());

		String groupKey = PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME;

		if (Validator.isNull(groupKey)) {
			groupKey = GroupConstants.GUEST;
		}

		if (_defaultGroup == null) {
			_defaultGroup = _groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), groupKey);

			_defaultGrouplayout1 = _layoutLocalService.fetchFirstLayout(
				_defaultGroup.getGroupId(), false,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

			if (_defaultGrouplayout1 == null) {
				_defaultGrouplayout1 = LayoutTestUtil.addTypePortletLayout(
					_defaultGroup);
			}

			_defaultGrouplayout2 = LayoutTestUtil.addTypePortletLayout(
				_defaultGroup.getGroupId());
		}
	}

	@Test
	public void testCanonicalURLDistinctThemeDisplayGroup() throws Exception {
		String portalDomain = "localhost";

		ThemeDisplay themeDisplay = _createThemeDisplay(
			portalDomain, _defaultGroup, 8080, false);

		String completeURL = _generateURL(
			portalDomain, "8080", StringPool.BLANK, _group.getFriendlyURL(),
			_layout2.getFriendlyURL(), false);

		Assert.assertEquals(
			completeURL,
			_portal.getCanonicalURL(
				completeURL, themeDisplay, _layout2, false, false));
	}

	@Test
	public void testCanonicalURLPartialCollisionWIthPublicGroupServletMapping()
		throws Exception {

		ThemeDisplay themeDisplay = _createThemeDisplay(
			"localhost", _group, 8080, false);

		LayoutSet layoutSet = _layout4.getLayoutSet();

		layoutSet.setVirtualHostnames(
			TreeMapBuilder.put(
				"test.com", StringPool.BLANK
			).build());

		themeDisplay.setLayoutSet(layoutSet);

		String completeURL =
			Http.HTTP_WITH_SLASH + "test.com:8080" + _layout4.getFriendlyURL();

		Assert.assertEquals(
			completeURL,
			_portal.getCanonicalURL(
				HttpComponentsUtil.addParameter(
					completeURL, "_ga",
					"2.237928582.786466685.1515402734-1365236376"),
				themeDisplay, _layout4, false, false));
	}

	@Test
	public void testCanonicalURLWithFriendlyURL() throws Exception {
		String portalDomain = "localhost";

		ThemeDisplay themeDisplay = _createThemeDisplay(
			portalDomain, _group, 8080, false);

		for (String urlSeparator :
				FriendlyURLResolverRegistryUtil.getURLSeparators()) {

			String completeURL = _generateURL(
				portalDomain, "8080", StringPool.BLANK, _group.getFriendlyURL(),
				urlSeparator + "content-name", false);

			Assert.assertEquals(
				completeURL,
				_portal.getCanonicalURL(
					HttpComponentsUtil.addParameter(
						completeURL, "_ga",
						"2.237928582.786466685.1515402734-1365236376"),
					themeDisplay, _layout1, false, false));
			Assert.assertEquals(
				completeURL,
				_portal.getCanonicalURL(
					HttpComponentsUtil.addParameter(
						completeURL, "_ga",
						"2.237928582.786466685.1515402734-1365236376"),
					themeDisplay, _layout3, false, false));
		}
	}

	@Test
	public void testCanonicalURLWithFriendlyURLContainingLayoutID()
		throws Exception {

		_group.setFriendlyURL(
			StringPool.SLASH + _layout1.getLayoutId() +
				RandomTestUtil.randomString());

		_group = _groupLocalService.updateGroup(_group);

		testCanonicalURLWithFriendlyURL();
	}

	@Test
	public void testCanonicalURLWithFriendlyURLForBlogs() throws Exception {
		String portalDomain = "localhost";

		ThemeDisplay themeDisplay = _createThemeDisplay(
			portalDomain, _group, 8080, false);

		for (String urlSeparator :
				FriendlyURLResolverRegistryUtil.getURLSeparators()) {

			String completeURL = _generateURL(
				portalDomain, "8080", StringPool.BLANK, _group.getFriendlyURL(),
				_layout1.getFriendlyURL() + urlSeparator + "blogs/content-name",
				false);

			Assert.assertEquals(
				completeURL,
				_portal.getCanonicalURL(
					HttpComponentsUtil.addParameter(
						completeURL, "_ga",
						"2.237928582.786466685.1515402734-1365236376"),
					themeDisplay, _layout1, false, false));
		}
	}

	@Test
	public void testCanonicalURLWithoutQueryString() throws Exception {
		String portalDomain = "localhost";

		String completeURL = HttpComponentsUtil.addParameter(
			_generateURL(
				portalDomain, "8080", "/en", _group.getFriendlyURL(),
				_layout1.getFriendlyURL(), false),
			"_ga", "2.237928582.786466685.1515402734-1365236376");

		ThemeDisplay themeDisplay = _createThemeDisplay(
			portalDomain, _group, 8080, false);

		Assert.assertEquals(
			HttpComponentsUtil.removeParameter(
				_portal.getCanonicalURL(
					completeURL, themeDisplay, _layout1, true, true),
				"_ga"),
			_portal.getCanonicalURL(
				completeURL, themeDisplay, _layout1, true, false));
	}

	@Test
	public void testCustomPortalLocaleCanonicalURLFirstLayout()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _group, _layout1, null, null, "/es",
			StringPool.BLANK, false, false);
	}

	@Test
	public void testCustomPortalLocaleCanonicalURLForceLayoutFriendlyURL()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _group, _layout1, null, null, "/es",
			"/home1", true, false);
	}

	@Test
	public void testCustomPortalLocaleCanonicalURLSecondLayout()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _group, _layout2, null, null, "/es",
			"/home2", false, false);
	}

	@Test
	public void testDefaultPortalLocaleCanonicalURLFirstLayout()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _group, _layout1, null, null, "/en",
			StringPool.BLANK, false, false);
	}

	@Test
	public void testDefaultPortalLocaleCanonicalURLForceLayoutFriendlyURL()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _group, _layout1, null, null, "/en",
			"/home1", true, false);
	}

	@Test
	public void testDefaultPortalLocaleCanonicalURLSecondLayout()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _group, _layout2, null, null, "/en",
			"/home2", false, false);
	}

	@Test
	public void testDefaultSiteFirstPage() throws Exception {
		_testCanonicalURL(
			"localhost", "localhost", _defaultGroup, _defaultGrouplayout1, null,
			null, "/en", StringPool.BLANK, false, false);
	}

	@Test
	public void testDefaultSiteFirstPageWithCustomPortalLocale()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _defaultGroup, _defaultGrouplayout1, null,
			null, "/es", StringPool.BLANK, false, false);
	}

	@Test
	public void testDefaultSiteSecondPage() throws Exception {
		_testCanonicalURL(
			"localhost", "localhost", _defaultGroup, _defaultGrouplayout2, null,
			null, "/en", _defaultGrouplayout2.getFriendlyURL(), false, false);
	}

	@Test
	public void testDefaultSiteSecondPageWithCustomPortalLocale()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _defaultGroup, _defaultGrouplayout2, null,
			null, "/es", _defaultGrouplayout2.getFriendlyURL(), false, false);
	}

	@Test
	public void testDomainCustomPortalLocaleCanonicalURLFirstLayoutFromLocalhost()
		throws Exception {

		_testCanonicalURL(
			"liferay.com", "localhost", _group, _layout1, null, null, "/es",
			StringPool.BLANK, false, false);
	}

	@Test
	public void testDomainDefaultSiteFirstPageFromLocalhost() throws Exception {
		_testCanonicalURL(
			"liferay.com", "localhost", _defaultGroup, _defaultGrouplayout1,
			null, null, "/en", StringPool.BLANK, false, false);
	}

	@Test
	public void testDomainDefaultSiteFirstPageFromLocalhostWithPort()
		throws Exception {

		_testCanonicalURL(
			"liferay.com", "localhost:8080", _defaultGroup,
			_defaultGrouplayout1, null, null, "/en", StringPool.BLANK, false,
			false);
	}

	@Test
	public void testDomainDefaultSiteFirstPageFromLocalhostWithPortSecure()
		throws Exception {

		_testCanonicalURL(
			"liferay.com", "localhost:8080", _defaultGroup,
			_defaultGrouplayout1, null, null, "/en", StringPool.BLANK, false,
			true);
	}

	@Test
	@TestInfo("LPD-58324")
	public void testGetCanonicalURLWithQueryParameters() throws Exception {
		ThemeDisplay themeDisplay = _createThemeDisplay(
			"localhost", _group, 8080, false);

		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, LayoutConstants.TYPE_CONTENT, false, "/test",
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		String canonicalURL = _portal.getCanonicalURL(
			String.format(
				"http://liferay.com/web/%s/test/?/-/thisshoulnotappear",
				_group.getGroupKey()),
			themeDisplay, layout, false, false);

		String expectedSuffix = String.format(
			"/web/%s/test", StringUtil.toLowerCase(_group.getGroupKey()));

		Assert.assertTrue(canonicalURL.endsWith(expectedSuffix));
	}

	@Test
	public void testGetCanonicalURLWithURLSeparatorInFriendlyURL()
		throws Exception {

		ThemeDisplay themeDisplay = _createThemeDisplay(
			"localhost", _group, 8080, false);

		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, LayoutConstants.TYPE_CONTENT, false, "/abc/w/def",
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		String canonicalURL = _portal.getCanonicalURL(
			String.format(
				"http://liferay.com/web/%s/abc/w/def", _group.getGroupKey()),
			themeDisplay, layout, false, false);

		String expectedSuffix = String.format(
			"/web/%s/abc/w/def", StringUtil.toLowerCase(_group.getGroupKey()));

		Assert.assertTrue(canonicalURL.endsWith(expectedSuffix));
	}

	@Test
	public void testLocalizedSiteCustomSiteLocaleCanonicalURLFirstLayout()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _group, _layout1,
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.SPAIN, "/en", StringPool.BLANK, false, false);
	}

	@Test
	public void testLocalizedSiteCustomSiteLocaleCanonicalURLForceLayoutFriendlyURL()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _group, _layout1,
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.SPAIN, "/en", "/casa1", true, false);
	}

	@Test
	public void testLocalizedSiteCustomSiteLocaleCanonicalURLSecondLayout()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _group, _layout2,
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.SPAIN, "/en", "/casa2", false, false);
	}

	@Test
	public void testLocalizedSiteDefaultSiteLocaleCanonicalURLFirstLayout()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _group, _layout1,
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.SPAIN, "/es", StringPool.BLANK, false, false);
	}

	@Test
	public void testLocalizedSiteDefaultSiteLocaleCanonicalURLForceLayoutFriendlyURL()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _group, _layout1,
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.SPAIN, "/es", "/casa1", true, false);
	}

	@Test
	public void testLocalizedSiteDefaultSiteLocaleCanonicalURLSecondLayout()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _group, _layout2,
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.SPAIN, "/es", "/casa2", false, false);
	}

	@Test
	public void testNonlocalhostDefaultSiteFirstPage() throws Exception {
		_testCanonicalURL(
			"localhost", "liferay.com", _defaultGroup, _defaultGrouplayout1,
			null, null, "/en", StringPool.BLANK, false, false);
	}

	@Test
	public void testNonlocalhostDefaultSiteSecondPage() throws Exception {
		_testCanonicalURL(
			"localhost", "liferay.com", _defaultGroup, _defaultGrouplayout2,
			null, null, "/en", _defaultGrouplayout2.getFriendlyURL(), false,
			false);
	}

	@Test
	public void testNonlocalhostPortalDomainFirstLayout() throws Exception {
		_testCanonicalURL(
			"localhost", "liferay.com", _group, _layout1, null, null, "/en",
			StringPool.BLANK, false, false);
	}

	@Test
	public void testNonlocalhostPortalDomainForceLayoutFriendlyURL()
		throws Exception {

		_testCanonicalURL(
			"localhost", "liferay.com", _group, _layout1, null, null, "/en",
			"/home1", true, false);
	}

	@Test
	public void testNonlocalhostPortalDomainSecondLayout() throws Exception {
		_testCanonicalURL(
			"localhost", "liferay.com", _group, _layout2, null, null, "/en",
			"/home2", false, false);
	}

	private ThemeDisplay _createThemeDisplay(
			String portalDomain, Group group, int serverPort, boolean secure)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayoutSet(group.getPublicLayoutSet());
		themeDisplay.setPortalDomain(portalDomain);

		if (secure) {
			themeDisplay.setPortalURL(Http.HTTPS_WITH_SLASH + portalDomain);
		}
		else {
			themeDisplay.setPortalURL(Http.HTTP_WITH_SLASH + portalDomain);
		}

		themeDisplay.setSecure(secure);

		int index = portalDomain.indexOf(CharPool.COLON);

		if (index != -1) {
			serverPort = GetterUtil.getIntegerStrict(
				portalDomain.substring(index + 1));
		}

		themeDisplay.setServerPort(serverPort);
		themeDisplay.setSiteGroupId(group.getGroupId());

		return themeDisplay;
	}

	private String _generateURL(
		String portalDomain, String port, String i18nPath,
		String groupFriendlyURL, String layoutFriendlyURL, boolean secure) {

		StringBundler sb = new StringBundler(9);

		if (secure) {
			sb.append(Http.HTTPS_WITH_SLASH);
		}
		else {
			sb.append(Http.HTTP_WITH_SLASH);
		}

		sb.append(portalDomain);

		if (!portalDomain.contains(StringPool.COLON)) {
			if (port == null) {
				if (secure) {
					port = String.valueOf(PropsValues.WEB_SERVER_HTTPS_PORT);
				}
				else {
					port = String.valueOf(PropsValues.WEB_SERVER_HTTP_PORT);
				}
			}

			if (!port.equals("-1")) {
				sb.append(StringPool.COLON);
				sb.append(port);
			}
		}

		if (Validator.isNull(PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME) &&
			Validator.isNull(groupFriendlyURL)) {

			sb.append("/web/guest");
		}

		sb.append(i18nPath);

		if (Validator.isNotNull(groupFriendlyURL)) {
			sb.append(PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING);
			sb.append(groupFriendlyURL);
		}

		if (Validator.isNotNull(layoutFriendlyURL)) {
			sb.append(layoutFriendlyURL);
		}

		return sb.toString();
	}

	private void _testCanonicalURL(
			String virtualHostname, String portalDomain, Group group,
			Layout layout, Collection<Locale> groupAvailableLocales,
			Locale groupDefaultLocale, String i18nPath,
			String expectedLayoutFriendlyURL, boolean forceLayoutFriendlyURL,
			boolean secure)
		throws Exception {

		if (!group.isGuest()) {
			group = GroupTestUtil.updateDisplaySettings(
				group.getGroupId(), groupAvailableLocales, groupDefaultLocale);
		}

		String port = null;

		int index = portalDomain.indexOf(CharPool.COLON);

		if (index != -1) {
			port = portalDomain.substring(index + 1);
		}

		if (Validator.isNotNull(virtualHostname)) {
			Company company = _companyLocalService.getCompany(
				layout.getCompanyId());

			_companyLocalService.updateCompany(
				company.getCompanyId(), virtualHostname, company.getMx(),
				company.getMaxUsers(), company.isActive());
		}

		String expectedGroupFriendlyURL = StringPool.BLANK;

		if (!group.isGuest()) {
			expectedGroupFriendlyURL = group.getFriendlyURL();
		}

		String expectedPortalDomain = virtualHostname;

		if (virtualHostname.startsWith("localhost") ^
			portalDomain.startsWith("localhost")) {

			expectedPortalDomain = portalDomain;
		}

		TestPropsUtil.set(PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE, "2");

		Assert.assertEquals(
			_generateURL(
				expectedPortalDomain, port, StringPool.BLANK,
				expectedGroupFriendlyURL, expectedLayoutFriendlyURL, secure),
			_portal.getCanonicalURL(
				_generateURL(
					portalDomain, port, i18nPath, group.getFriendlyURL(),
					layout.getFriendlyURL(), secure),
				_createThemeDisplay(
					portalDomain, group, Http.HTTP_PORT, secure),
				layout, forceLayoutFriendlyURL));
		Assert.assertEquals(
			_generateURL(
				expectedPortalDomain, port, StringPool.BLANK,
				expectedGroupFriendlyURL, expectedLayoutFriendlyURL, secure),
			_portal.getCanonicalURL(
				_generateURL(
					portalDomain, port, i18nPath, group.getFriendlyURL(),
					StringUtil.upperCase(layout.getFriendlyURL()), secure),
				_createThemeDisplay(
					portalDomain, group, Http.HTTP_PORT, secure),
				layout, forceLayoutFriendlyURL));
	}

	private static Locale _defaultLocale;
	private static int _defaultPrependStyle;
	private static String _originalVirtualHostsDefaultSiteName;
	private static int _originalWebServerHTTPPort;
	private static int _originalWebServerHTTPSPort;

	@Inject
	private static VirtualHostLocalService _virtualHostLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _defaultGroup;
	private Layout _defaultGrouplayout1;
	private Layout _defaultGrouplayout2;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout1;
	private Layout _layout2;
	private Layout _layout3;
	private Layout _layout4;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

}