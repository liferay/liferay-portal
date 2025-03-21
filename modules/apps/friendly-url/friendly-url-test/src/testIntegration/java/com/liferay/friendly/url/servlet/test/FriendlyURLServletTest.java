/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.VirtualLayoutConstants;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.I18nServlet;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LanguageIds;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.service.RedirectEntryLocalService;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

/**
 * @author László Csontos
 */
@LanguageIds(
	availableLanguageIds = {"en_GB", "en_US", "hu_HU", "pt_BR"},
	defaultLanguageId = "en_US"
)
@RunWith(Arquillian.class)
public class FriendlyURLServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		PropsValues.LOCALE_USE_DEFAULT_IF_NOT_AVAILABLE = true;

		LanguageUtil.init();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		List<Locale> availableLocales = Arrays.asList(
			LocaleUtil.US, LocaleUtil.UK, LocaleUtil.HUNGARY,
			LocaleUtil.BRAZIL);

		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), availableLocales, LocaleUtil.US);

		Class<?> clazz = _servlet.getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		clazz = classLoader.loadClass(
			"com.liferay.friendly.url.internal.servlet.FriendlyURLServlet");

		_getRedirectMethod = clazz.getDeclaredMethod(
			"getRedirect", HttpServletRequest.class, HttpServletResponse.class,
			String.class);

		clazz = classLoader.loadClass(
			"com.liferay.friendly.url.internal.servlet.FriendlyURLServlet" +
				"$Redirect");

		_redirectConstructor1 = clazz.getConstructor(String.class);

		_redirectConstructor2 = clazz.getConstructor(
			String.class, Boolean.TYPE, Boolean.TYPE);
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();

		PropsValues.LOCALE_USE_DEFAULT_IF_NOT_AVAILABLE = GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.LOCALE_USE_DEFAULT_IF_NOT_AVAILABLE));
	}

	@Test
	public void testCanAccessOldFriendlyURLAfterPublishLayouts()
		throws Exception {

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), _group, false, false,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Group stagingGroup = _group.getStagingGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			stagingGroup.getGroupId(), false,
			HashMapBuilder.put(
				_portal.getSiteDefaultLocale(stagingGroup), "test"
			).build(),
			HashMapBuilder.put(
				_portal.getSiteDefaultLocale(stagingGroup), "/test"
			).build());

		String oldPath = getPath(_group, layout);

		Map<String, String[]> parameters =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildFullPublishParameterMap();

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			_group.getGroupId(), false, parameters);

		layout = LayoutTestUtil.updateFriendlyURL(
			layout,
			HashMapBuilder.put(
				_portal.getSiteDefaultLocale(stagingGroup), "/new-test"
			).build());

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			_group.getGroupId(), false, parameters);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setPathInfo(oldPath);
		mockHttpServletRequest.setRequestURI(
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING + oldPath);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
				getPath(_group, layout),
			mockHttpServletResponse.getRedirectedUrl());
	}

	@Test
	public void testCanAccessOldLocalizedFriendlyURLAfterPublishLayouts()
		throws Exception {

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), _group, false, false,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Group stagingGroup = _group.getStagingGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			stagingGroup.getGroupId(), false,
			HashMapBuilder.put(
				_portal.getSiteDefaultLocale(stagingGroup), "test"
			).build(),
			HashMapBuilder.put(
				_portal.getSiteDefaultLocale(stagingGroup), "/test"
			).put(
				LocaleUtil.BRAZIL, "/teste"
			).build());

		String oldPath = _getLocalizedPath(_group, layout, LocaleUtil.BRAZIL);

		Map<String, String[]> parameters =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildFullPublishParameterMap();

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			_group.getGroupId(), false, parameters);

		layout = LayoutTestUtil.updateFriendlyURL(
			layout,
			HashMapBuilder.put(
				_portal.getSiteDefaultLocale(stagingGroup), "/new-test"
			).put(
				LocaleUtil.BRAZIL, "/novo-teste"
			).build());

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			_group.getGroupId(), false, parameters);

		String i18nLanguageId = "pt_BR";

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.I18N_LANGUAGE_ID, i18nLanguageId);
		mockHttpServletRequest.setPathInfo(oldPath);
		mockHttpServletRequest.setRequestURI(
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING + oldPath);
		mockHttpServletRequest.setServletPath(i18nLanguageId);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			"/pt" + PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
				_getLocalizedPath(_group, layout, LocaleUtil.BRAZIL),
			mockHttpServletResponse.getRedirectedUrl());
	}

	@Test
	public void testGetRedirectForAlternativeSite() throws Throwable {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			HashMapBuilder.put(
				LocaleUtil.US, "home"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.HUNGARY, "/home-hu"
			).put(
				LocaleUtil.UK, "/home-gb"
			).put(
				LocaleUtil.US, "/home"
			).build());

		_testGetRedirectForAlternativeSite(
			_redirectConstructor2.newInstance("/home", true, false),
			"/home-gb");
		_testGetRedirectForAlternativeSite(
			_redirectConstructor2.newInstance("/home", true, true),
			"/en/home-gb");
		_testGetRedirectForAlternativeSite(
			_redirectConstructor2.newInstance("/home", true, true),
			"/en-US/home-gb");
		_testGetRedirectForAlternativeSite(
			_redirectConstructor2.newInstance("/hu/home-hu", true, true),
			"/hu/home-gb");
		_testGetRedirectForAlternativeSite(
			_redirectConstructor2.newInstance("/en-GB/home-gb", true, true),
			"/en-GB/home");

		String publicGroupFriendlyURL =
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
				_group.getFriendlyURL();

		_testGetRedirectForAlternativeSite(
			_redirectConstructor2.newInstance(
				publicGroupFriendlyURL + "/home", true, false),
			publicGroupFriendlyURL + "/home-gb");
		_testGetRedirectForAlternativeSite(
			_redirectConstructor2.newInstance(
				publicGroupFriendlyURL + "/home", true, true),
			StringBundler.concat("/en", publicGroupFriendlyURL, "/home-gb"));
		_testGetRedirectForAlternativeSite(
			_redirectConstructor2.newInstance(
				publicGroupFriendlyURL + "/home", true, true),
			StringBundler.concat("/en-US", publicGroupFriendlyURL, "/home-gb"));
		_testGetRedirectForAlternativeSite(
			_redirectConstructor2.newInstance(
				StringBundler.concat("/hu", publicGroupFriendlyURL, "/home-hu"),
				true, true),
			StringBundler.concat("/hu", publicGroupFriendlyURL, "/home-gb"));
		_testGetRedirectForAlternativeSite(
			_redirectConstructor2.newInstance(
				StringBundler.concat(
					"/en-GB", publicGroupFriendlyURL, "/home-gb"),
				true, true),
			StringBundler.concat("/en-GB", publicGroupFriendlyURL, "/home"));

		_testGetRedirectForAlternativeSite(
			_redirectConstructor2.newInstance(getURL(layout), false, false),
			"/fr/home");
		_testGetRedirectForAlternativeSite(
			_redirectConstructor2.newInstance("/home", true, true),
			"/fr/home-gb");

		PropsValues.LOCALE_USE_DEFAULT_IF_NOT_AVAILABLE = false;

		_testGetRedirectForAlternativeSite(null, "/fr/home");
		_testGetRedirectForAlternativeSite(null, "/fr/home-gb");
	}

	@Test
	public void testGetRedirectForUserGroup() throws Throwable {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setPathInfo(StringPool.SLASH);

		User user = UserTestUtil.addUser(_group.getGroupId());

		UserGroup userGroup = UserGroupTestUtil.addUserGroup(
			_group.getGroupId());

		_userGroupLocalService.addUserUserGroup(
			user.getUserId(), userGroup.getUserGroupId());

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			userGroup.getGroupId());

		testGetRedirect(
			mockHttpServletRequest, getPath(user.getGroup(), layout),
			_redirectConstructor1.newInstance(
				_portal.getLayoutActualURL(
					new VirtualLayout(layout, user.getGroup()),
					Portal.PATH_MAIN)));
	}

	@Test
	public void testGetRedirectOnHiddenLayout() throws Throwable {
		Group group = GroupTestUtil.addGroup();

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(group);

		layout1.setHidden(true);

		layout1 = _layoutLocalService.updateLayout(layout1);

		Role guestRole = RoleLocalServiceUtil.getRole(
			group.getCompanyId(), RoleConstants.GUEST);

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				group.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout1.getPrimaryKey()), guestRole.getRoleId());

		resourcePermission.setActionIds(0);
		resourcePermission.setViewActionId(false);

		_resourcePermissionLocalService.updateResourcePermission(
			resourcePermission);

		_user = UserTestUtil.addUser(group.getGroupId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setPathInfo(StringPool.SLASH);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		testGetRedirect(
			mockHttpServletRequest, mockHttpServletResponse,
			group.getFriendlyURL(),
			_redirectConstructor1.newInstance(getURL(layout1)));
		Assert.assertEquals(404, mockHttpServletResponse.getStatus());
	}

	@Test
	public void testGetRedirectOnLinkToURLLayoutWithDoAsUserId()
		throws Throwable {

		_doAsUser = UserTestUtil.addUser();
		_user = UserTestUtil.addUser();

		Layout linkToURLLayout = LayoutTestUtil.addTypeLinkToURLLayout(
			_group.getGroupId(), _layout.getFriendlyURL());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.USER_ID, _doAsUser.getUserId());

		HttpSession httpSession = mockHttpServletRequest.getSession();

		httpSession.setAttribute(WebKeys.USER_ID, _user.getUserId());

		String path = getPath(_group, linkToURLLayout);

		Company company = _companyLocalService.getCompany(
			_doAsUser.getCompanyId());

		String encryptedDoAsUserId = _encryptor.encrypt(
			company.getKeyObj(), String.valueOf(_doAsUser.getUserId()));

		Object expectedRedirect = _redirectConstructor1.newInstance(
			HttpComponentsUtil.setParameter(
				_layout.getFriendlyURL(), "doAsUserId", encryptedDoAsUserId));

		testGetRedirect(mockHttpServletRequest, path, expectedRedirect);

		mockHttpServletRequest.setParameter("doAsUserId", encryptedDoAsUserId);

		testGetRedirect(mockHttpServletRequest, path, expectedRedirect);
	}

	@Test
	public void testGetRedirectWithExistentSite() throws Throwable {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setPathInfo(StringPool.SLASH);

		testGetRedirect(
			mockHttpServletRequest, getPath(_group, _layout),
			_redirectConstructor1.newInstance(getURL(_layout)));
	}

	@Test(expected = NoSuchGroupException.class)
	public void testGetRedirectWithGroupId() throws Throwable {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setPathInfo(StringPool.SLASH);

		String path = "/" + _group.getGroupId() + _layout.getFriendlyURL();

		testGetRedirect(
			mockHttpServletRequest, path,
			_redirectConstructor1.newInstance(getURL(_layout)));
	}

	@Test
	public void testGetRedirectWithI18nPath() throws Throwable {
		testGetI18nRedirect("/fr");
		testGetI18nRedirect("/hu");
		testGetI18nRedirect("/en");
		testGetI18nRedirect("/en_GB");
		testGetI18nRedirect("/en_US");
	}

	@Test(expected = NoSuchGroupException.class)
	public void testGetRedirectWithNonexistentSite() throws Throwable {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setPathInfo(StringPool.SLASH);

		testGetRedirect(mockHttpServletRequest, "/nonexistent-site/home", null);
	}

	@Test
	public void testGetRedirectWithNumericUserScreenName() throws Throwable {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setPathInfo(StringPool.SLASH);

		long numericScreenName = RandomTestUtil.nextLong();

		_user = UserTestUtil.addUser(String.valueOf(numericScreenName));

		Group userGroup = _user.getGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(userGroup);

		testGetRedirect(
			mockHttpServletRequest, getPath(userGroup, _layout),
			_redirectConstructor1.newInstance(getURL(_layout)));
	}

	@Test
	public void testGetRedirectWithRedirectEntryLayoutViewAction()
		throws Throwable {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), layout.getName(LocaleUtil.US), null, false,
			_layout.getName(LocaleUtil.US),
			ServiceContextTestUtil.getServiceContext());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(
			HttpHeaders.REFERER,
			StringBundler.concat(
				_group.getFriendlyURL(),
				PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING,
				VirtualLayoutConstants.CANONICAL_URL_SEPARATOR,
				GroupConstants.CONTROL_PANEL_FRIENDLY_URL));

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			_role, Layout.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_group.getCompanyId()), ActionKeys.UPDATE);

		_user = UserTestUtil.addUser();

		RoleLocalServiceUtil.addUserRole(_user.getUserId(), _role);

		mockHttpServletRequest.setAttribute(WebKeys.USER, _user);

		mockHttpServletRequest.setPathInfo(_layout.getFriendlyURL());

		testGetRedirect(
			mockHttpServletRequest, getPath(_group, _layout),
			_redirectConstructor1.newInstance(getURL(_layout)));
	}

	@Test
	public void testGetRedirectWithUpperCaseAccentedSourceURLRedirectEntry()
		throws Throwable {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), layout.getName(LocaleUtil.US), null, false,
			"TÉSTREDIRECT", ServiceContextTestUtil.getServiceContext());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setPathInfo("/TÉSTREDIRECT");

		testGetRedirect(
			mockHttpServletRequest, _group.getFriendlyURL() + "/TÉSTREDIRECT",
			_redirectConstructor2.newInstance(
				layout.getName(LocaleUtil.US), true, false));
	}

	@Test
	public void testGetRedirectWithUpperCaseSourceURLRedirectEntry()
		throws Throwable {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), layout.getName(LocaleUtil.US), null, false,
			"TESTREDIRECT", ServiceContextTestUtil.getServiceContext());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setPathInfo("/TESTREDIRECT");

		testGetRedirect(
			mockHttpServletRequest, _group.getFriendlyURL() + "/TESTREDIRECT",
			_redirectConstructor2.newInstance(
				layout.getName(LocaleUtil.US), true, false));
	}

	@Test
	public void testServiceForward() throws Throwable {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		long groupId = _group.getGroupId();

		Locale locale = LocaleUtil.getSiteDefault();

		Map<Locale, String> nameMap = HashMapBuilder.put(
			locale, "careers"
		).build();

		Map<Locale, String> friendlyURLMap = HashMapBuilder.put(
			locale, "/careers"
		).build();

		Layout careerLayout = LayoutTestUtil.addTypePortletLayout(
			groupId, false, nameMap, friendlyURLMap);

		nameMap.put(locale, "friendly");
		friendlyURLMap.put(locale, "/friendly");

		UnicodeProperties typeSettingsUnicodeProperties =
			_group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put("url", careerLayout.getFriendlyURL());

		String typeSettings = typeSettingsUnicodeProperties.toString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		Layout redirectLayout = _layoutLocalService.addLayout(
			null, serviceContext.getUserId(), groupId, false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, nameMap,
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), LayoutConstants.TYPE_URL,
			typeSettings, false, friendlyURLMap, serviceContext);

		mockHttpServletRequest.setPathInfo(StringPool.SLASH);

		String requestURI =
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
				getPath(_group, redirectLayout);

		mockHttpServletRequest.setRequestURI(requestURI);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			"/careers", mockHttpServletResponse.getHeader("Location"));
		Assert.assertEquals(302, mockHttpServletResponse.getStatus());
		Assert.assertTrue(mockHttpServletResponse.isCommitted());
	}

	@Test
	public void testServiceForwardToDefaultLayoutWith404OnDisabledLocale()
		throws Throwable {

		Group group = GroupTestUtil.addGroup();

		Locale locale = LocaleUtil.getSiteDefault();

		Layout homeLayout = LayoutTestUtil.addTypePortletLayout(
			group.getGroupId(), false,
			HashMapBuilder.put(
				locale, "home"
			).build(),
			HashMapBuilder.put(
				locale, "/home"
			).put(
				LocaleUtil.UK, "/home1"
			).build());

		GroupTestUtil.updateDisplaySettings(
			group.getGroupId(), Arrays.asList(LocaleUtil.UK), LocaleUtil.UK);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		testGetRedirect(
			new MockHttpServletRequest(
				"GET",
				StringBundler.concat(
					PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
					group.getFriendlyURL(), "/home")),
			mockHttpServletResponse, getPath(group, homeLayout) + "/home",
			_redirectConstructor1.newInstance(getURL(homeLayout)));

		Assert.assertEquals(404, mockHttpServletResponse.getStatus());
	}

	@Test
	public void testServiceForwardToDefaultLayoutWith404OnMissingLayout()
		throws Throwable {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		testGetRedirect(
			new MockHttpServletRequest(
				"GET",
				StringBundler.concat(
					PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
					_group.getFriendlyURL(), "/path")),
			mockHttpServletResponse, getPath(_group, _layout) + "/path",
			_redirectConstructor1.newInstance(getURL(_layout)));

		Assert.assertEquals(404, mockHttpServletResponse.getStatus());
	}

	@Test
	public void testServiceLinkToURLRedirectWithQueryParams() throws Throwable {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Layout redirectLayout = LayoutTestUtil.addTypePortletLayout(_group);

		redirectLayout.setType(LayoutConstants.TYPE_URL);

		UnicodeProperties typeSettingsUnicodeProperties =
			_group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put("url", _layout.getFriendlyURL());

		redirectLayout.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		redirectLayout = _layoutLocalService.updateLayout(redirectLayout);

		mockHttpServletRequest.setParameter("param", "true");
		mockHttpServletRequest.setPathInfo(StringPool.SLASH);

		String requestURI =
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
				getPath(_group, redirectLayout);

		mockHttpServletRequest.setRequestURI(requestURI);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertTrue(redirectedURL.contains("?param=true"));
	}

	@Test
	public void testServiceRedirect() throws Throwable {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Layout redirectLayout = LayoutTestUtil.addTypePortletLayout(_group);

		redirectLayout.setType(LayoutConstants.TYPE_URL);

		UnicodeProperties typeSettingsUnicodeProperties =
			_group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put("url", _layout.getFriendlyURL());

		redirectLayout.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		redirectLayout = _layoutLocalService.updateLayout(redirectLayout);

		mockHttpServletRequest.setPathInfo(StringPool.SLASH);

		String requestURI =
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
				getPath(_group, redirectLayout);

		mockHttpServletRequest.setRequestURI(requestURI);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			_layout.getFriendlyURL(),
			mockHttpServletResponse.getRedirectedUrl());

		Assert.assertEquals(302, mockHttpServletResponse.getStatus());
	}

	@Test
	public void testServiceRedirectWithForwardedRequest() throws Throwable {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		String oldFriendlyURL = layout.getFriendlyURL();

		String oldPath = getPath(_group, layout);

		layout = _layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getParentLayoutId(), layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(), layout.getRobotsMap(), layout.getType(),
			layout.isHidden(),
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(),
				StringPool.SLASH + RandomTestUtil.randomString()
			).build(),
			layout.isIconImage(), null, layout.getStyleBookEntryId(),
			layout.getFaviconFileEntryId(), layout.getMasterLayoutPlid(),
			ServiceContextTestUtil.getServiceContext());

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAVAX_SERVLET_FORWARD_REQUEST_URI, oldFriendlyURL);
		mockHttpServletRequest.setPathInfo(oldPath);
		mockHttpServletRequest.setRequestURI(
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING + oldPath);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			layout.getFriendlyURL(),
			mockHttpServletResponse.getRedirectedUrl());

		Assert.assertEquals(302, mockHttpServletResponse.getStatus());
	}

	@Test
	public void testServiceRedirectWithRedirectEntry() throws Exception {
		_testServiceRedirectWithRedirectEntry("path", true, 301);
		_testServiceRedirectWithRedirectEntry("path", false, 302);
	}

	protected String getI18nLanguageId(HttpServletRequest httpServletRequest) {
		String path = GetterUtil.getString(httpServletRequest.getPathInfo());

		if (Validator.isNull(path)) {
			return null;
		}

		String i18nLanguageId = httpServletRequest.getServletPath();

		int pos = i18nLanguageId.lastIndexOf(CharPool.SLASH);

		i18nLanguageId = i18nLanguageId.substring(pos + 1);

		if (Validator.isNull(i18nLanguageId)) {
			return null;
		}

		Locale locale = LocaleUtil.fromLanguageId(i18nLanguageId, true, false);

		if ((locale == null) || Validator.isNull(locale.getCountry())) {
			locale = LanguageUtil.getLocale(i18nLanguageId);
		}

		if (locale != null) {
			i18nLanguageId = LocaleUtil.toLanguageId(locale);
		}

		if (!PropsValues.LOCALE_USE_DEFAULT_IF_NOT_AVAILABLE &&
			!LanguageUtil.isAvailableLocale(i18nLanguageId)) {

			return null;
		}

		return i18nLanguageId;
	}

	protected String getPath(Group group, Layout layout) {
		return group.getFriendlyURL() + layout.getFriendlyURL();
	}

	protected String getURL(Layout layout) {
		return "/c/portal/layout?p_l_id=" + layout.getPlid() +
			"&p_v_l_s_g_id=0";
	}

	protected void testGetI18nRedirect(String i18nPath) throws Throwable {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setPathInfo(StringPool.SLASH);
		mockHttpServletRequest.setServletPath(i18nPath);

		String i18nLanguageId = getI18nLanguageId(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.I18N_LANGUAGE_ID, i18nLanguageId);

		String requestURI =
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
				getPath(_group, _layout);

		mockHttpServletRequest.setRequestURI(requestURI);

		Object expectedRedirect = _redirectConstructor1.newInstance(
			getURL(_layout));

		testGetRedirect(
			mockHttpServletRequest, _group.getFriendlyURL(), expectedRedirect);
		testGetRedirect(
			mockHttpServletRequest, getPath(_group, _layout), expectedRedirect);
	}

	protected void testGetRedirect(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String path,
			Object expectedRedirect)
		throws Throwable {

		try {
			Assert.assertEquals(
				expectedRedirect,
				_getRedirectMethod.invoke(
					_servlet, httpServletRequest, httpServletResponse, path));
		}
		catch (InvocationTargetException invocationTargetException) {
			throw invocationTargetException.getCause();
		}
	}

	protected void testGetRedirect(
			HttpServletRequest httpServletRequest, String path,
			Object expectedRedirect)
		throws Throwable {

		testGetRedirect(
			httpServletRequest, new MockHttpServletResponse(), path,
			expectedRedirect);
	}

	private String _getLocalizedPath(
		Group group, Layout layout, Locale locale) {

		return group.getFriendlyURL() + layout.getFriendlyURL(locale);
	}

	private void _testGetRedirectForAlternativeSite(
			Object expectedRedirect, String requestURI)
		throws Throwable {

		MockHttpServletRequest originalMockHttpServletRequest =
			new MockHttpServletRequest("GET", requestURI);

		int pos = requestURI.indexOf(StringPool.SLASH, 1);

		if (pos > 0) {
			originalMockHttpServletRequest.setPathInfo(
				requestURI.substring(pos));

			Map<String, String> languageIds = I18nServlet.getLanguageIdsMap();

			String servletPath = languageIds.get(
				StringUtil.toLowerCase(
					StringUtil.replace(
						requestURI.substring(0, pos), CharPool.DASH,
						CharPool.UNDERLINE)));

			if (servletPath != null) {
				originalMockHttpServletRequest.setServletPath(servletPath);
			}
		}
		else {
			originalMockHttpServletRequest.setPathInfo(StringPool.SLASH);
		}

		String layoutFriendlyURL = requestURI.substring(
			requestURI.lastIndexOf(StringPool.SLASH));

		String publicFriendlyURL = StringBundler.concat(
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
			_group.getFriendlyURL(), layoutFriendlyURL);

		HttpServletRequest virtualHostFilterProcessedHttpServletRequest =
			new HttpServletRequestWrapper(originalMockHttpServletRequest) {

				@Override
				public String getPathInfo() {
					String requestURI = getRequestURI();

					int pos = requestURI.indexOf(StringPool.SLASH, 1);

					if (pos != -1) {
						return requestURI.substring(pos);
					}

					return StringPool.SLASH;
				}

				@Override
				public String getRequestURI() {
					return super.getServletPath() + publicFriendlyURL;
				}

			};

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		if (!Validator.isBlank(
				originalMockHttpServletRequest.getServletPath())) {

			ReflectionTestUtil.invoke(
				_i18nServlet, "service",
				new Class<?>[] {
					HttpServletRequest.class, HttpServletResponse.class
				},
				virtualHostFilterProcessedHttpServletRequest,
				mockHttpServletResponse);
		}

		if (mockHttpServletResponse.getStatus() == 404) {
			Assert.assertNull(expectedRedirect);
		}
		else {
			HttpServletRequest
				virtualHostFilterAndI18nServletProcessedHttpServletRequest =
					new HttpServletRequestWrapper(
						virtualHostFilterProcessedHttpServletRequest) {

						@Override
						public String getPathInfo() {
							String requestURI = getRequestURI();

							int pos = requestURI.indexOf(StringPool.SLASH, 1);

							if (pos != -1) {
								return requestURI.substring(pos);
							}

							return StringPool.SLASH;
						}

						@Override
						public String getRequestURI() {
							return publicFriendlyURL;
						}

					};

			testGetRedirect(
				virtualHostFilterAndI18nServletProcessedHttpServletRequest,
				virtualHostFilterAndI18nServletProcessedHttpServletRequest.
					getPathInfo(),
				expectedRedirect);
		}
	}

	private void _testServiceRedirectWithRedirectEntry(
			String sourceURL, boolean permanent, int expectedStatus)
		throws Exception {

		RedirectEntry redirectEntry =
			_redirectEntryLocalService.addRedirectEntry(
				_group.getGroupId(), "http://www.liferay.com", null, permanent,
				sourceURL, ServiceContextTestUtil.getServiceContext());

		try {
			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			_servlet.service(
				new MockHttpServletRequest(
					"GET",
					StringBundler.concat(
						PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
						_group.getFriendlyURL(), CharPool.SLASH, sourceURL)),
				mockHttpServletResponse);

			Assert.assertEquals(
				expectedStatus, mockHttpServletResponse.getStatus());
			Assert.assertEquals(
				"http://www.liferay.com",
				mockHttpServletResponse.getHeader("Location"));

			redirectEntry = _redirectEntryLocalService.fetchRedirectEntry(
				_group.getGroupId(), sourceURL);

			Assert.assertNotNull(redirectEntry.getLastOccurrenceDate());
		}
		finally {
			_redirectEntryLocalService.deleteRedirectEntry(redirectEntry);
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private User _doAsUser;

	@Inject
	private Encryptor _encryptor;

	private Method _getRedirectMethod;

	@DeleteAfterTestRun
	private Group _group;

	private final I18nServlet _i18nServlet = new I18nServlet() {

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		private final ServletContext _servletContext = new MockServletContext();

	};

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

	private Constructor<?> _redirectConstructor1;
	private Constructor<?> _redirectConstructor2;

	@Inject
	private RedirectEntryLocalService _redirectEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@Inject(
		filter = "(&(servlet.type=friendly-url)(servlet.init.private=false))"
	)
	private Servlet _servlet;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

}