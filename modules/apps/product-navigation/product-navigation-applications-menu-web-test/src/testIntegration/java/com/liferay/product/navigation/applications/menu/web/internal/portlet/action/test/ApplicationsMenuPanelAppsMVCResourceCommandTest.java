/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.applications.menu.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletRequest;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.product.navigation.applications.menu.web.internal.portlet.action.test.constants.ApplicationsMenuTestPortletKeys;
import com.liferay.site.manager.RecentGroupManager;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
@Sync
public class ApplicationsMenuPanelAppsMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_themeDisplay = _getThemeDisplay();

		_mockHttpServletRequest = new MockHttpServletRequest();

		_mockPortletRequest = new MockLiferayResourceRequest();

		_mockPortletRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, _mockHttpServletRequest);
		_mockPortletRequest.setAttribute(WebKeys.THEME_DISPLAY, _themeDisplay);

		_mockHttpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST, _mockPortletRequest);

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);
		_mockHttpServletRequest.setAttribute(
			WebKeys.USER, TestPropsValues.getUser());

		_setUser();
	}

	@After
	public void tearDown() throws Exception {
		PrincipalThreadLocal.setName(_originalName);
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Test
	public void testNoSites() {
		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getSitesJSONObject",
			new Class<?>[] {
				HttpServletRequest.class, ResourceRequest.class,
				ResourceResponse.class, ThemeDisplay.class
			},
			_mockHttpServletRequest, _mockPortletRequest,
			new MockLiferayResourceResponse(), _themeDisplay);

		Assert.assertFalse(jsonObject.has("mySites"));
		Assert.assertFalse(jsonObject.has("recentSites"));
		Assert.assertFalse(jsonObject.has("viewAllURL"));
	}

	@Test
	public void testOnlyMyRecentSitesLessThan8() throws Exception {
		_addRecentGroups(7);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getSitesJSONObject",
			new Class<?>[] {
				HttpServletRequest.class, ResourceRequest.class,
				ResourceResponse.class, ThemeDisplay.class
			},
			_mockHttpServletRequest, _mockPortletRequest,
			new MockLiferayResourceResponse(), _themeDisplay);

		Assert.assertFalse(jsonObject.has("mySites"));
		Assert.assertTrue(jsonObject.has("recentSites"));
		Assert.assertFalse(jsonObject.has("viewAllURL"));
	}

	@Test
	public void testOnlyMyRecentSitesMax8() throws Exception {
		_addRecentGroups(8);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getSitesJSONObject",
			new Class<?>[] {
				HttpServletRequest.class, ResourceRequest.class,
				ResourceResponse.class, ThemeDisplay.class
			},
			_mockHttpServletRequest, _mockPortletRequest,
			new MockLiferayResourceResponse(), _themeDisplay);

		Assert.assertFalse(jsonObject.has("mySites"));
		Assert.assertTrue(jsonObject.has("recentSites"));
		Assert.assertFalse(jsonObject.has("viewAllURL"));
	}

	@Test
	public void testOnlyMyRecentSitesMoreThan8() throws Exception {
		_addRecentGroups(10);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getSitesJSONObject",
			new Class<?>[] {
				HttpServletRequest.class, ResourceRequest.class,
				ResourceResponse.class, ThemeDisplay.class
			},
			_mockHttpServletRequest, _mockPortletRequest,
			new MockLiferayResourceResponse(), _themeDisplay);

		Assert.assertFalse(jsonObject.has("mySites"));
		Assert.assertTrue(jsonObject.has("recentSites"));
		Assert.assertTrue(jsonObject.has("viewAllURL"));
	}

	@Test
	public void testOnlyMySitesLessThan8() throws Exception {
		_addMySiteGroups(7);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getSitesJSONObject",
			new Class<?>[] {
				HttpServletRequest.class, ResourceRequest.class,
				ResourceResponse.class, ThemeDisplay.class
			},
			_mockHttpServletRequest, _mockPortletRequest,
			new MockLiferayResourceResponse(), _themeDisplay);

		Assert.assertFalse(jsonObject.has("viewAllURL"));
		Assert.assertFalse(jsonObject.has("recentSites"));
		Assert.assertTrue(jsonObject.has("mySites"));
	}

	@Test
	public void testOnlyMySitesMax8() throws Exception {
		_addMySiteGroups(8);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getSitesJSONObject",
			new Class<?>[] {
				HttpServletRequest.class, ResourceRequest.class,
				ResourceResponse.class, ThemeDisplay.class
			},
			_mockHttpServletRequest, _mockPortletRequest,
			new MockLiferayResourceResponse(), _themeDisplay);

		Assert.assertFalse(jsonObject.has("viewAllURL"));
		Assert.assertFalse(jsonObject.has("recentSites"));
		Assert.assertTrue(jsonObject.has("mySites"));
	}

	@Test
	public void testOnlyMySitesMoreThan8() throws Exception {
		_addMySiteGroups(10);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getSitesJSONObject",
			new Class<?>[] {
				HttpServletRequest.class, ResourceRequest.class,
				ResourceResponse.class, ThemeDisplay.class
			},
			_mockHttpServletRequest, _mockPortletRequest,
			new MockLiferayResourceResponse(), _themeDisplay);

		Assert.assertTrue(jsonObject.has("viewAllURL"));
		Assert.assertFalse(jsonObject.has("recentSites"));
		Assert.assertTrue(jsonObject.has("mySites"));
	}

	@Test
	public void testPanelCategories() {
		JSONArray panelCategoriesJSONArray = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getPanelCategoriesJSONArray",
			new Class<?>[] {HttpServletRequest.class, ThemeDisplay.class},
			_mockHttpServletRequest, _themeDisplay);

		Assert.assertTrue(
			_containsPortletId(
				panelCategoriesJSONArray,
				ApplicationsMenuTestPortletKeys.
					APPLICATIONS_MENU_TEST_PORTLET));
	}

	@Test
	public void testRecentSitesAndMySitesLessThan7() throws Exception {
		_addMySiteGroups(3);
		_addRecentGroups(3);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getSitesJSONObject",
			new Class<?>[] {
				HttpServletRequest.class, ResourceRequest.class,
				ResourceResponse.class, ThemeDisplay.class
			},
			_mockHttpServletRequest, _mockPortletRequest,
			new MockLiferayResourceResponse(), _themeDisplay);

		Assert.assertFalse(jsonObject.has("viewAllURL"));
		Assert.assertTrue(jsonObject.has("recentSites"));
		Assert.assertTrue(jsonObject.has("mySites"));
	}

	@Test
	public void testRecentSitesAndMySitesMax7() throws Exception {
		_addMySiteGroups(3);
		_addRecentGroups(4);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getSitesJSONObject",
			new Class<?>[] {
				HttpServletRequest.class, ResourceRequest.class,
				ResourceResponse.class, ThemeDisplay.class
			},
			_mockHttpServletRequest, _mockPortletRequest,
			new MockLiferayResourceResponse(), _themeDisplay);

		Assert.assertFalse(jsonObject.has("viewAllURL"));
		Assert.assertTrue(jsonObject.has("recentSites"));
		Assert.assertTrue(jsonObject.has("mySites"));
	}

	@Test
	public void testRecentSitesAndMySitesMoreThan7() throws Exception {
		_addMySiteGroups(4);
		_addRecentGroups(4);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getSitesJSONObject",
			new Class<?>[] {
				HttpServletRequest.class, ResourceRequest.class,
				ResourceResponse.class, ThemeDisplay.class
			},
			_mockHttpServletRequest, _mockPortletRequest,
			new MockLiferayResourceResponse(), _themeDisplay);

		Assert.assertTrue(jsonObject.has("viewAllURL"));

		Assert.assertTrue(jsonObject.has("recentSites"));

		JSONArray recentSitesJSONArray = jsonObject.getJSONArray("recentSites");

		Assert.assertEquals(4, recentSitesJSONArray.length());

		Assert.assertTrue(jsonObject.has("mySites"));

		JSONArray mySitesSitesJSONArray = jsonObject.getJSONArray("mySites");

		Assert.assertEquals(3, mySitesSitesJSONArray.length());
	}

	private void _addMySiteGroups(int max) throws Exception {
		for (int i = 0; i < max; i++) {
			Group group = GroupTestUtil.addGroup();

			LayoutTestUtil.addTypePortletLayout(group);

			_groups.add(group);

			_userLocalService.setGroupUsers(
				group.getGroupId(), new long[] {_user.getUserId()});
		}
	}

	private void _addRecentGroups(int max) throws Exception {
		List<Long> groupIds = new ArrayList<>();

		for (int i = 0; i < max; i++) {
			Group group = GroupTestUtil.addGroup();

			LayoutTestUtil.addTypePortletLayout(group);

			_groups.add(group);

			groupIds.add(group.getGroupId());

			_userLocalService.setGroupUsers(
				group.getGroupId(), new long[] {_user.getUserId()});
		}

		_setRecentGroupsValue(
			_mockHttpServletRequest, StringUtil.merge(groupIds));
	}

	private User _addUser() throws Exception {
		return UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new long[0],
			ServiceContextTestUtil.getServiceContext());
	}

	private boolean _containsPortletId(
		JSONArray panelCategoriesJSONArray, String portletId) {

		for (int i = 0; i < panelCategoriesJSONArray.length(); i++) {
			JSONObject childCategoryJSONObject =
				panelCategoriesJSONArray.getJSONObject(i);

			JSONArray childCategoriesJSONArray =
				childCategoryJSONObject.getJSONArray("childCategories");

			for (int j = 0; j < childCategoriesJSONArray.length(); j++) {
				JSONObject panelAppsJSONObject =
					childCategoriesJSONArray.getJSONObject(j);

				JSONArray panelAppsJSONArray = panelAppsJSONObject.getJSONArray(
					"panelApps");

				for (int k = 0; k < panelAppsJSONArray.length(); k++) {
					JSONObject panelAppJSONObject =
						panelAppsJSONArray.getJSONObject(k);

					if (Objects.equals(
							panelAppJSONObject.get("portletId"), portletId)) {

						return true;
					}
				}
			}
		}

		return false;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLayout(layout);
		themeDisplay.setLocale(
			LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSignedIn(true);
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _setRecentGroupsValue(
		HttpServletRequest httpServletRequest, String value) {

		SessionClicks.put(httpServletRequest, _KEY_RECENT_GROUPS, value);
	}

	private void _setUser() throws Exception {
		_user = _addUser();

		_themeDisplay.setUser(_user);

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(_user.getUserId());

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		_setRecentGroupsValue(_mockHttpServletRequest, StringPool.BLANK);
	}

	private static final String _KEY_RECENT_GROUPS =
		"com.liferay.site.util_recentGroups";

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups = new ArrayList<>();

	private HttpServletRequest _mockHttpServletRequest;
	private MockPortletRequest _mockPortletRequest;

	@Inject(filter = "mvc.command.name=/applications_menu/panel_apps")
	private MVCResourceCommand _mvcResourceCommand;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

	@Inject
	private RecentGroupManager _recentGroupManager;

	private ThemeDisplay _themeDisplay;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}