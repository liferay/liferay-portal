/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.VirtualLayoutConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LanguageIds;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletPreferences;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

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
@LanguageIds(
	availableLanguageIds = {"en_US", "es_ES", "fr_CA"},
	defaultLanguageId = "en_US"
)
@RunWith(Arquillian.class)
public class FriendlyURLServletLocalizedFriendlyURLTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_nameMap = HashMapBuilder.put(
			LocaleUtil.CANADA_FRENCH, "Accueil"
		).put(
			LocaleUtil.SPAIN, "Inicio"
		).put(
			LocaleUtil.US, "Home"
		).build();

		_friendlyURLMap = HashMapBuilder.put(
			LocaleUtil.CANADA_FRENCH, "/accueil"
		).put(
			LocaleUtil.SPAIN, "/inicio"
		).put(
			LocaleUtil.US, "/home"
		).build();
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testIncludeI18nPathCustomLocaleAlgorithm0() throws Exception {
		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			_group.getCompanyId());

		try {
			portletPreferences.setValue(
				PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE, String.valueOf(0));

			portletPreferences.store();

			_assertLocalizedSiteLayoutFriendlyURL(
				_group.getGroupId(),
				LayoutTestUtil.addTypePortletLayout(
					_group.getGroupId(), false, _nameMap, _friendlyURLMap),
				"/inicio", LocaleUtil.SPAIN, LocaleUtil.SPAIN, "/inicio",
				false);
		}
		finally {
			portletPreferences.reset(
				PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE);
		}
	}

	@Test
	public void testIncludeI18nPathCustomLocaleAlgorithm1() throws Exception {
		int originalLocalePrependFriendlyURLStyle =
			PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE;

		try {
			PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE = 1;

			_assertLocalizedSiteLayoutFriendlyURL(
				_group.getGroupId(),
				LayoutTestUtil.addTypePortletLayout(
					_group.getGroupId(), false, _nameMap, _friendlyURLMap),
				"/inicio", LocaleUtil.SPAIN, LocaleUtil.SPAIN, "/inicio", true);
		}
		finally {
			PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE =
				originalLocalePrependFriendlyURLStyle;
		}
	}

	@Test
	public void testIncludeI18nPathCustomLocaleAlgorithm2() throws Exception {
		int originalLocalePrependFriendlyURLStyle =
			PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE;

		try {
			PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE = 2;

			_assertLocalizedSiteLayoutFriendlyURL(
				_group.getGroupId(),
				LayoutTestUtil.addTypePortletLayout(
					_group.getGroupId(), false, _nameMap, _friendlyURLMap),
				"/inicio", LocaleUtil.SPAIN, LocaleUtil.SPAIN, "/inicio", true);
		}
		finally {
			PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE =
				originalLocalePrependFriendlyURLStyle;
		}
	}

	@Test
	public void testIncludeI18nPathDefaultLocaleAlgorithm0() throws Exception {
		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			_group.getCompanyId());

		try {
			portletPreferences.setValue(
				PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE, String.valueOf(0));

			portletPreferences.store();

			_assertLocalizedSiteLayoutFriendlyURL(
				_group.getGroupId(),
				LayoutTestUtil.addTypePortletLayout(
					_group.getGroupId(), false, _nameMap, _friendlyURLMap),
				"/home", LocaleUtil.US, LocaleUtil.US, "/home", false);
		}
		finally {
			portletPreferences.reset(
				PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE);
		}
	}

	@Test
	public void testIncludeI18nPathDefaultLocaleAlgorithm1() throws Exception {
		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			_group.getCompanyId());

		try {
			portletPreferences.setValue(
				PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE, String.valueOf(1));

			portletPreferences.store();

			_assertLocalizedSiteLayoutFriendlyURL(
				_group.getGroupId(),
				LayoutTestUtil.addTypePortletLayout(
					_group.getGroupId(), false, _nameMap, _friendlyURLMap),
				"/home", LocaleUtil.US, LocaleUtil.US, "/home", false);
		}
		finally {
			portletPreferences.reset(
				PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE);
		}
	}

	@Test
	public void testIncludeI18nPathDefaultLocaleAlgorithm2() throws Exception {
		int originalLocalePrependFriendlyURLStyle =
			PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE;

		try {
			PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE = 2;

			_assertLocalizedSiteLayoutFriendlyURL(
				_group.getGroupId(),
				LayoutTestUtil.addTypePortletLayout(
					_group.getGroupId(), false, _nameMap, _friendlyURLMap),
				"/home", LocaleUtil.US, LocaleUtil.US, "/home", true);
		}
		finally {
			PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE =
				originalLocalePrependFriendlyURLStyle;
		}
	}

	@Test
	public void testLocalizedSiteLayoutFriendlyURLWithVirtualHost()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String layoutFriendlyURL = "/home";

		mockHttpServletRequest.setPathInfo(layoutFriendlyURL);

		Locale locale = LocaleUtil.SPAIN;

		String i18nPathLanguageId = _portal.getI18nPathLanguageId(
			locale, StringPool.BLANK);

		String i18nPath = StringPool.SLASH + i18nPathLanguageId;

		mockHttpServletRequest.setRequestURI(i18nPath + layoutFriendlyURL);

		mockHttpServletRequest.setServerName(_VIRTUAL_HOSTNAME);

		Assert.assertEquals(
			i18nPath.concat("/inicio"),
			ReflectionTestUtil.invoke(
				_servlet, "_getLocalizedFriendlyURL",
				new Class<?>[] {
					HttpServletRequest.class, Layout.class, Locale.class,
					Locale.class
				},
				new HttpServletRequestWrapper(mockHttpServletRequest) {

					@Override
					public String getPathInfo() {
						return _group.getFriendlyURL() + layoutFriendlyURL;
					}

					@Override
					public String getRequestURI() {
						return _PRIVATE_GROUP_SERVLET_MAPPING + getPathInfo();
					}

				},
				LayoutTestUtil.addTypePortletLayout(
					_group.getGroupId(), false, _nameMap, _friendlyURLMap),
				locale, LocaleUtil.US));
	}

	@Test
	public void testLocalizedSitePrivateLayoutFriendlyURL() throws Exception {
		_testLocalizedSiteLayoutFriendlyURL(true);
	}

	@Test
	public void testLocalizedSitePublicLayoutFriendlyURL() throws Exception {
		_testLocalizedSiteLayoutFriendlyURL(false);
	}

	@Test
	public void testLocalizedVirtualPrivateLayoutFriendlyURL()
		throws Exception {

		_testLocalizedVirtualLayoutFriendlyURL(true);
	}

	@Test
	public void testLocalizedVirtualPublicLayoutFriendlyURL() throws Exception {
		_testLocalizedVirtualLayoutFriendlyURL(false);
	}

	@Test
	public void testNonexistentLocalizedSitePrivateLayoutFriendlyURL()
		throws Exception {

		_testNonexistentLocalizedSiteLayoutFriendlyURL(true);
	}

	@Test
	public void testNonexistentLocalizedSitePublicLayoutFriendlyURL()
		throws Exception {

		_testNonexistentLocalizedSiteLayoutFriendlyURL(false);
	}

	@Test
	public void testNonexistentLocalizedVirtualPrivateLayoutFriendlyURL()
		throws Exception {

		_testNonexistentLocalizedVirtualLayoutFriendlyURL(true);
	}

	@Test
	public void testNonexistentLocalizedVirtualPublicLayoutFriendlyURL()
		throws Exception {

		_testNonexistentLocalizedVirtualLayoutFriendlyURL(false);
	}

	@Test
	public void testNonexistentWronglyLocalizedSiteLayoutPrivateFriendlyURL()
		throws Exception {

		_testNonexistentWronglyLocalizedSiteLayoutFriendlyURL(true);
	}

	@Test
	public void testNonexistentWronglyLocalizedSiteLayoutPublicFriendlyURL()
		throws Exception {

		_testNonexistentWronglyLocalizedSiteLayoutFriendlyURL(false);
	}

	@Test
	public void testNonexistentWronglyLocalizedVirtualLayoutPrivateFriendlyURL()
		throws Exception {

		_testNonexistentWronglyLocalizedVirtualLayoutFriendlyURL(true);
	}

	@Test
	public void testNonexistentWronglyLocalizedVirtualLayoutPublicFriendlyURL()
		throws Exception {

		_testNonexistentWronglyLocalizedVirtualLayoutFriendlyURL(false);
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPrivateFriendlyURL1()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			true, LocaleUtil.US, null, "/home");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPrivateFriendlyURL2()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			true, LocaleUtil.CANADA_FRENCH, null, "/accueil");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPrivateFriendlyURLWithBlogsMapping1()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			true, LocaleUtil.US, "/-/blogs/one", "/home");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPrivateFriendlyURLWithBlogsMapping2()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			true, LocaleUtil.CANADA_FRENCH, "/-/blogs/one", "/accueil");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPrivateFriendlyURLWithParams1()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			true, LocaleUtil.US, "?param=value", "/home");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPrivateFriendlyURLWithParams2()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			true, LocaleUtil.CANADA_FRENCH, "?param=value", "/accueil");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPrivateFriendlyURLWithTagsMapping1()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			true, LocaleUtil.US, "/tags/one", "/home");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPrivateFriendlyURLWithTagsMapping2()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			true, LocaleUtil.CANADA_FRENCH, "/tags/one", "/accueil");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPublicFriendlyURL1()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			false, LocaleUtil.US, null, "/home");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPublicFriendlyURL2()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			false, LocaleUtil.CANADA_FRENCH, null, "/accueil");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPublicFriendlyURLWithBlogsMapping1()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			false, LocaleUtil.US, "/-/blogs/one", "/home");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPublicFriendlyURLWithBlogsMapping2()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			false, LocaleUtil.CANADA_FRENCH, "/-/blogs/one", "/accueil");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPublicFriendlyURLWithParams1()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			false, LocaleUtil.US, "?param=value", "/home");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPublicFriendlyURLWithParams2()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			false, LocaleUtil.CANADA_FRENCH, "?param=value", "/accueil");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPublicFriendlyURLWithTagsMapping1()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			false, LocaleUtil.US, "/tags/one", "/home");
	}

	@Test
	public void testWronglyLocalizedSiteLayoutPublicFriendlyURLWithTagsMapping2()
		throws Exception {

		_testWronglyLocalizedSiteLayoutFriendlyURL(
			false, LocaleUtil.CANADA_FRENCH, "/tags/one", "/accueil");
	}

	@Test
	public void testWronglyLocalizedVirtualPrivateLayoutFriendlyURL1()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			true, LocaleUtil.US, null, "/home");
	}

	@Test
	public void testWronglyLocalizedVirtualPrivateLayoutFriendlyURL2()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			true, LocaleUtil.CANADA_FRENCH, null, "/accueil");
	}

	@Test
	public void testWronglyLocalizedVirtualPrivateLayoutFriendlyURLWithBlogsMapping1()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			true, LocaleUtil.US, "/-/blogs/one", "/home");
	}

	@Test
	public void testWronglyLocalizedVirtualPrivateLayoutFriendlyURLWithBlogsMapping2()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			true, LocaleUtil.CANADA_FRENCH, "/-/blogs/one", "/accueil");
	}

	@Test
	public void testWronglyLocalizedVirtualPrivateLayoutFriendlyURLWithParams1()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			true, LocaleUtil.US, "?param=value", "/home");
	}

	@Test
	public void testWronglyLocalizedVirtualPrivateLayoutFriendlyURLWithParams2()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			true, LocaleUtil.CANADA_FRENCH, "?param=value", "/accueil");
	}

	@Test
	public void testWronglyLocalizedVirtualPrivateLayoutFriendlyURLWithTagsMapping1()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			true, LocaleUtil.US, "/tags/one", "/home");
	}

	@Test
	public void testWronglyLocalizedVirtualPrivateLayoutFriendlyURLWithTagsMapping2()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			true, LocaleUtil.CANADA_FRENCH, "/tags/one", "/accueil");
	}

	@Test
	public void testWronglyLocalizedVirtualPublicLayoutFriendlyURL1()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			false, LocaleUtil.US, null, "/home");
	}

	@Test
	public void testWronglyLocalizedVirtualPublicLayoutFriendlyURL2()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			false, LocaleUtil.CANADA_FRENCH, null, "/accueil");
	}

	@Test
	public void testWronglyLocalizedVirtualPublicLayoutFriendlyURLWithBlogsMapping1()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			false, LocaleUtil.US, "/-/blogs/one", "/home");
	}

	@Test
	public void testWronglyLocalizedVirtualPublicLayoutFriendlyURLWithBlogsMapping2()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			false, LocaleUtil.CANADA_FRENCH, "/-/blogs/one", "/accueil");
	}

	@Test
	public void testWronglyLocalizedVirtualPublicLayoutFriendlyURLWithParams1()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			false, LocaleUtil.US, "?param=value", "/home");
	}

	@Test
	public void testWronglyLocalizedVirtualPublicLayoutFriendlyURLWithParams2()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			false, LocaleUtil.CANADA_FRENCH, "?param=value", "/accueil");
	}

	@Test
	public void testWronglyLocalizedVirtualPublicLayoutFriendlyURLWithTagsMapping1()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			false, LocaleUtil.US, "/tags/one", "/home");
	}

	@Test
	public void testWronglyLocalizedVirtualPublicLayoutFriendlyURLWithTagsMapping2()
		throws Exception {

		_testWronglyLocalizedVirtualLayoutFriendlyURL(
			false, LocaleUtil.CANADA_FRENCH, "/tags/one", "/accueil");
	}

	private void _assertLocalizedSiteLayoutFriendlyURL(
			long groupId, Layout layout, String layoutFriendlyURL,
			Locale locale, Locale originalLocale,
			String expectedLayoutFriendlyURL, boolean includeI18nPath)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Group group = _groupLocalService.getGroup(groupId);

		mockHttpServletRequest.setPathInfo(
			group.getFriendlyURL() + layoutFriendlyURL);

		String groupServletMapping = _PUBLIC_GROUP_SERVLET_MAPPING;

		if (layout.isPrivateLayout()) {
			groupServletMapping = _PRIVATE_GROUP_SERVLET_MAPPING;
		}

		mockHttpServletRequest.setRequestURI(
			groupServletMapping + group.getFriendlyURL() + layoutFriendlyURL);

		StringBundler sb = new StringBundler(includeI18nPath ? 5 : 3);

		if (includeI18nPath) {
			sb.append(StringPool.SLASH);
			sb.append(_portal.getI18nPathLanguageId(locale, StringPool.BLANK));
		}

		sb.append(groupServletMapping);
		sb.append(group.getFriendlyURL());
		sb.append(expectedLayoutFriendlyURL);

		Assert.assertEquals(
			sb.toString(),
			ReflectionTestUtil.invoke(
				_servlet, "_getLocalizedFriendlyURL",
				new Class<?>[] {
					HttpServletRequest.class, Layout.class, Locale.class,
					Locale.class
				},
				mockHttpServletRequest, layout, locale, originalLocale));
	}

	private void _assertLocalizedVirtualLayoutFriendlyURL(
			long userGroupGroupId, Layout layout, String layoutFriendlyURL,
			Locale locale, Locale originalLocale,
			String expectedLayoutFriendlyURL, boolean includeI18nPath)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		StringBundler sb = new StringBundler(4);

		User user = TestPropsValues.getUser();

		Group groupUser = user.getGroup();

		sb.append(groupUser.getFriendlyURL());

		sb.append(VirtualLayoutConstants.CANONICAL_URL_SEPARATOR);

		Group userGroupGroup = _groupLocalService.getGroup(userGroupGroupId);

		sb.append(userGroupGroup.getFriendlyURL());

		sb.append(layoutFriendlyURL);

		mockHttpServletRequest.setPathInfo(sb.toString());

		sb = new StringBundler(5);

		String groupServletMapping = _PUBLIC_GROUP_SERVLET_MAPPING;

		if (layout.isPrivateLayout()) {
			groupServletMapping = _PRIVATE_GROUP_SERVLET_MAPPING;
		}

		sb.append(groupServletMapping);

		sb.append(groupUser.getFriendlyURL());
		sb.append(VirtualLayoutConstants.CANONICAL_URL_SEPARATOR);
		sb.append(userGroupGroup.getFriendlyURL());
		sb.append(layoutFriendlyURL);

		mockHttpServletRequest.setRequestURI(sb.toString());

		sb = new StringBundler(includeI18nPath ? 7 : 5);

		if (includeI18nPath) {
			sb.append(StringPool.SLASH);
			sb.append(_portal.getI18nPathLanguageId(locale, StringPool.BLANK));
		}

		sb.append(groupServletMapping);
		sb.append(groupUser.getFriendlyURL());
		sb.append(VirtualLayoutConstants.CANONICAL_URL_SEPARATOR);
		sb.append(userGroupGroup.getFriendlyURL());
		sb.append(expectedLayoutFriendlyURL);

		Assert.assertEquals(
			sb.toString(),
			ReflectionTestUtil.invoke(
				_servlet, "_getLocalizedFriendlyURL",
				new Class<?>[] {
					HttpServletRequest.class, Layout.class, Locale.class,
					Locale.class
				},
				mockHttpServletRequest, layout, locale, originalLocale));
	}

	private void _testLocalizedSiteLayoutFriendlyURL(boolean privateLayout)
		throws Exception {

		_assertLocalizedSiteLayoutFriendlyURL(
			_group.getGroupId(),
			LayoutTestUtil.addTypePortletLayout(
				_group.getGroupId(), privateLayout, _nameMap, _friendlyURLMap),
			"/inicio", LocaleUtil.SPAIN, LocaleUtil.SPAIN, "/inicio", true);
	}

	private void _testLocalizedVirtualLayoutFriendlyURL(boolean privateLayout)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId());

		UserGroup userGroup = UserGroupTestUtil.addUserGroup(
			_group.getGroupId());

		Group userGroupGroup = userGroup.getGroup();

		_userGroupLocalService.addUserUserGroup(
			serviceContext.getUserId(), userGroup.getUserGroupId());

		_assertLocalizedVirtualLayoutFriendlyURL(
			userGroupGroup.getGroupId(),
			LayoutTestUtil.addTypePortletLayout(
				userGroupGroup.getGroupId(), privateLayout, _nameMap,
				_friendlyURLMap),
			"/inicio", LocaleUtil.SPAIN, LocaleUtil.SPAIN, "/inicio", true);
	}

	private void _testNonexistentLocalizedSiteLayoutFriendlyURL(
			boolean privateLayout)
		throws Exception {

		_assertLocalizedSiteLayoutFriendlyURL(
			_group.getGroupId(),
			LayoutTestUtil.addTypePortletLayout(
				_group.getGroupId(), privateLayout, _nameMap, _friendlyURLMap),
			"/home", LocaleUtil.GERMANY, LocaleUtil.US, "/home", true);
	}

	private void _testNonexistentLocalizedVirtualLayoutFriendlyURL(
			boolean privateLayout)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId());

		UserGroup userGroup = UserGroupTestUtil.addUserGroup(
			_group.getGroupId());

		Group userGroupGroup = userGroup.getGroup();

		_userGroupLocalService.addUserUserGroup(
			serviceContext.getUserId(), userGroup.getUserGroupId());

		_assertLocalizedVirtualLayoutFriendlyURL(
			userGroupGroup.getGroupId(),
			LayoutTestUtil.addTypePortletLayout(
				userGroupGroup.getGroupId(), privateLayout, _nameMap,
				_friendlyURLMap),
			"/home", LocaleUtil.GERMANY, LocaleUtil.US, "/home", true);
	}

	private void _testNonexistentWronglyLocalizedSiteLayoutFriendlyURL(
			boolean privateLayout)
		throws Exception {

		_assertLocalizedSiteLayoutFriendlyURL(
			_group.getGroupId(),
			LayoutTestUtil.addTypePortletLayout(
				_group.getGroupId(), privateLayout, _nameMap, _friendlyURLMap),
			"/inicio", LocaleUtil.GERMANY, LocaleUtil.SPAIN, "/home", true);
	}

	private void _testNonexistentWronglyLocalizedVirtualLayoutFriendlyURL(
			boolean privateLayout)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId());

		UserGroup userGroup = UserGroupTestUtil.addUserGroup(
			_group.getGroupId());

		Group userGroupGroup = userGroup.getGroup();

		_userGroupLocalService.addUserUserGroup(
			serviceContext.getUserId(), userGroup.getUserGroupId());

		_assertLocalizedVirtualLayoutFriendlyURL(
			userGroupGroup.getGroupId(),
			LayoutTestUtil.addTypePortletLayout(
				userGroupGroup.getGroupId(), privateLayout, _nameMap,
				_friendlyURLMap),
			"/inicio", LocaleUtil.US, LocaleUtil.SPAIN, "/home", true);
	}

	private void _testWronglyLocalizedSiteLayoutFriendlyURL(
			boolean privateLayout, Locale locale, String queryString,
			String expectedLayoutFriendlyURL)
		throws Exception {

		String requestedFriendlyURL = "/inicio";

		if (Validator.isNotNull(queryString)) {
			requestedFriendlyURL += queryString;

			expectedLayoutFriendlyURL += queryString;
		}

		_assertLocalizedSiteLayoutFriendlyURL(
			_group.getGroupId(),
			LayoutTestUtil.addTypePortletLayout(
				_group.getGroupId(), privateLayout, _nameMap, _friendlyURLMap),
			requestedFriendlyURL, locale, LocaleUtil.SPAIN,
			expectedLayoutFriendlyURL, true);
	}

	private void _testWronglyLocalizedVirtualLayoutFriendlyURL(
			boolean privateLayout, Locale locale, String queryString,
			String expectedLayoutFriendlyURL)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId());

		UserGroup userGroup = UserGroupTestUtil.addUserGroup(
			_group.getGroupId());

		Group userGroupGroup = userGroup.getGroup();

		_userGroupLocalService.addUserUserGroup(
			serviceContext.getUserId(), userGroup.getUserGroupId());

		String requestedFriendlyURL = "/inicio";

		if (Validator.isNotNull(queryString)) {
			requestedFriendlyURL += queryString;

			expectedLayoutFriendlyURL += queryString;
		}

		_assertLocalizedVirtualLayoutFriendlyURL(
			userGroupGroup.getGroupId(),
			LayoutTestUtil.addTypePortletLayout(
				userGroupGroup.getGroupId(), privateLayout, _nameMap,
				_friendlyURLMap),
			requestedFriendlyURL, locale, LocaleUtil.SPAIN,
			expectedLayoutFriendlyURL, true);
	}

	private static final String _PRIVATE_GROUP_SERVLET_MAPPING =
		PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING;

	private static final String _PUBLIC_GROUP_SERVLET_MAPPING =
		PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING;

	private static final String _VIRTUAL_HOSTNAME = "test.com";

	private static Map<Locale, String> _friendlyURLMap;
	private static Map<Locale, String> _nameMap;

	@Inject
	private static Portal _portal;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "(&(servlet.type=friendly-url)(servlet.init.private=false))"
	)
	private Servlet _servlet;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

}