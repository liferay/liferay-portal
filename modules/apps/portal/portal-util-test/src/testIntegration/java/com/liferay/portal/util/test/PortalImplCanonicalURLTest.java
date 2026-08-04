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
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.model.VirtualLayoutConstants;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutFriendlyURLLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.ClassTestRule;
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
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.TreeMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class PortalImplCanonicalURLTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			false,
			new ClassTestRule<TreeMap<String, String>>() {

				@Override
				public void afterClass(
						Description description,
						TreeMap<String, String> virtualHostnames)
					throws PortalException {

					VirtualHostLocalServiceUtil.updateVirtualHosts(
						TestPropsValues.getCompanyId(), 0, virtualHostnames);
				}

				@Override
				public TreeMap<String, String> beforeClass(
						Description description)
					throws PortalException {

					TreeMap<String, String> virtualHostnames = new TreeMap<>();

					for (VirtualHost virtualHost :
							VirtualHostLocalServiceUtil.getVirtualHosts(
								TestPropsValues.getCompanyId(), 0)) {

						virtualHostnames.put(
							virtualHost.getHostname(),
							GetterUtil.getString(virtualHost.getLanguageId()));
					}

					return virtualHostnames;
				}

			},
			new LiferayIntegrationTestRule());

	@BeforeClass
	public static void setUpClass() throws Exception {
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

		_group = GroupTestUtil.addGroup();

		_originalGroupFriendlyURL = _group.getFriendlyURL();
		_originalGroupTypeSettings = _group.getTypeSettings();

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
		_layout5 = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			HashMapBuilder.put(
				LocaleUtil.US, "Test Page"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, "/test-page"
			).build());
		_layout6 = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			HashMapBuilder.put(
				LocaleUtil.US, "Pöge"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, "/pöge"
			).build());

		String groupKey = PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME;

		if (Validator.isNull(groupKey)) {
			groupKey = GroupConstants.GUEST;
		}

		_defaultGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), groupKey);

		_defaultGroupLayout1 = _layoutLocalService.fetchFirstLayout(
			_defaultGroup.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		if (_defaultGroupLayout1 == null) {
			_defaultGroupLayout1 = LayoutTestUtil.addTypePortletLayout(
				_defaultGroup);
		}

		_defaultGroupLayout2 = LayoutTestUtil.addTypePortletLayout(
			_defaultGroup.getGroupId());

		_targetGroup = GroupTestUtil.addGroup();
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
		_group = _groupLocalService.updateGroup(
			_group.getGroupId(), _originalGroupTypeSettings);

		_group.setFriendlyURL(_originalGroupFriendlyURL);

		_group = _groupLocalService.updateGroup(_group);
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
	public void testCanonicalURLLayoutFriendlyURLWithHyphen() throws Exception {
		String portalDomain = "localhost";

		Assert.assertEquals(
			_generateURL(
				portalDomain, "8080", StringPool.BLANK, _group.getFriendlyURL(),
				_layout5.getFriendlyURL(), false),
			_portal.getCanonicalURL(
				_generateURL(
					portalDomain, "8080", StringPool.BLANK,
					_group.getFriendlyURL(), "/test%20page", false),
				_createThemeDisplay(portalDomain, _defaultGroup, 8080, false),
				_layout5, false, false));
	}

	@Test
	@TestInfo("LPD-98055")
	public void testCanonicalURLLayoutFriendlyURLWithNonasciiCharacter()
		throws Exception {

		Assert.assertEquals("/p%C3%B6ge", _layout6.getFriendlyURL());

		_testCanonicalURL(
			"localhost", "localhost", _group, _layout6, null, null, "/en",
			"/p%C3%B6ge", false, false);
		_testCanonicalURL(
			"localhost", "localhost", _group, _addLegacyLayout("/legacy-pöge"),
			null, null, "/en", "/legacy-pöge", false, false);
	}

	@Test
	@TestInfo("LPD-98055")
	public void testCanonicalURLLegacyLayoutFriendlyURLWithMixedCase()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _group, _addLegacyLayout("/LegacyHome1"),
			null, null, "/en", "/LegacyHome1", false, false);
	}

	@Test
	public void testCanonicalURLPartialCollisionWIthPublicGroupServletMapping()
		throws Exception {

		ThemeDisplay themeDisplay = _createThemeDisplay(
			"localhost", _group, _portal.getPortalServerPort(false), false);

		LayoutSet layoutSet = _layout4.getLayoutSet();

		layoutSet.setVirtualHostnames(
			TreeMapBuilder.put(
				"test.com", StringPool.BLANK
			).build());

		themeDisplay.setLayoutSet(layoutSet);

		String completeURL = StringBundler.concat(
			Http.HTTP_WITH_SLASH, "test.com:",
			PortalUtil.getPortalServerPort(false), _layout4.getFriendlyURL());

		Assert.assertEquals(
			completeURL,
			_portal.getCanonicalURL(
				HttpComponentsUtil.addParameter(
					completeURL, "_ga",
					"2.237928582.786466685.1515402734-1365236376"),
				themeDisplay, _layout4, false, false));
	}

	@Test
	@TestInfo("LPD-98055")
	public void testCanonicalURLVirtualLayout() throws Exception {
		Layout virtualLayout = new VirtualLayout(_layout6, _targetGroup);

		_testCanonicalURL(
			"localhost", "localhost", _targetGroup, virtualLayout, null, null,
			"/en", StringPool.BLANK, false, false);
		_testCanonicalURL(
			"localhost", "localhost", _targetGroup, virtualLayout, null, null,
			"/en",
			VirtualLayoutConstants.CANONICAL_URL_SEPARATOR +
				_group.getFriendlyURL() + "/p%C3%B6ge",
			true, false);
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
			"localhost", "localhost", _defaultGroup, _defaultGroupLayout1, null,
			null, "/en", StringPool.BLANK, false, false);
	}

	@Test
	public void testDefaultSiteFirstPageWithCustomPortalLocale()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _defaultGroup, _defaultGroupLayout1, null,
			null, "/es", StringPool.BLANK, false, false);
	}

	@Test
	public void testDefaultSiteSecondPage() throws Exception {
		_testCanonicalURL(
			"localhost", "localhost", _defaultGroup, _defaultGroupLayout2, null,
			null, "/en", _defaultGroupLayout2.getFriendlyURL(), false, false);
	}

	@Test
	public void testDefaultSiteSecondPageWithCustomPortalLocale()
		throws Exception {

		_testCanonicalURL(
			"localhost", "localhost", _defaultGroup, _defaultGroupLayout2, null,
			null, "/es", _defaultGroupLayout2.getFriendlyURL(), false, false);
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
			"liferay.com", "localhost", _defaultGroup, _defaultGroupLayout1,
			null, null, "/en", StringPool.BLANK, false, false);
	}

	@Test
	public void testDomainDefaultSiteFirstPageFromLocalhostWithPort()
		throws Exception {

		_testCanonicalURL(
			"liferay.com", "localhost:" + PortalUtil.getPortalServerPort(false),
			_defaultGroup, _defaultGroupLayout1, null, null, "/en",
			StringPool.BLANK, false, false);
	}

	@Test
	public void testDomainDefaultSiteFirstPageFromLocalhostWithPortSecure()
		throws Exception {

		_testCanonicalURL(
			"liferay.com", "localhost:" + PortalUtil.getPortalServerPort(false),
			_defaultGroup, _defaultGroupLayout1, null, null, "/en",
			StringPool.BLANK, false, true);
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
			"localhost", "liferay.com", _defaultGroup, _defaultGroupLayout1,
			null, null, "/en", StringPool.BLANK, false, false);
	}

	@Test
	public void testNonlocalhostDefaultSiteSecondPage() throws Exception {
		_testCanonicalURL(
			"localhost", "liferay.com", _defaultGroup, _defaultGroupLayout2,
			null, null, "/en", _defaultGroupLayout2.getFriendlyURL(), false,
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

	private Layout _addLegacyLayout(String friendlyURL) throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId());

		_layoutFriendlyURLLocalService.updateLayoutFriendlyURL(
			TestPropsValues.getUserId(), layout.getCompanyId(),
			layout.getGroupId(), layout.getPlid(), layout.isPrivateLayout(),
			friendlyURL, LocaleUtil.toLanguageId(LocaleUtil.US),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		layout.setFriendlyURL(friendlyURL);

		return _layoutLocalService.updateLayout(layout);
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

	private static Group _defaultGroup;
	private static Layout _defaultGroupLayout1;
	private static Layout _defaultGroupLayout2;
	private static Locale _defaultLocale;
	private static int _defaultPrependStyle;
	private static Group _group;

	@Inject
	private static GroupLocalService _groupLocalService;

	private static Layout _layout1;
	private static Layout _layout2;
	private static Layout _layout3;
	private static Layout _layout4;
	private static Layout _layout5;
	private static Layout _layout6;

	@Inject
	private static LayoutLocalService _layoutLocalService;

	private static String _originalGroupFriendlyURL;
	private static String _originalGroupTypeSettings;
	private static String _originalVirtualHostsDefaultSiteName;
	private static int _originalWebServerHTTPPort;
	private static int _originalWebServerHTTPSPort;
	private static Group _targetGroup;

	@Inject
	private static VirtualHostLocalService _virtualHostLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private LayoutFriendlyURLLocalService _layoutFriendlyURLLocalService;

	@Inject
	private Portal _portal;

}