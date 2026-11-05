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
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
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
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.manager.RecentGroupManager;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
			JavaConstants.JAKARTA_PORTLET_REQUEST, _mockPortletRequest);

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
	public void testGetPanelCategoriesJSONArray() throws Exception {
		_setCompanyAdmin();

		JSONArray panelCategoriesJSONArray = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getPanelCategoriesJSONArray",
			new Class<?>[] {
				HttpServletRequest.class, ResourceRequest.class,
				ThemeDisplay.class
			},
			_mockHttpServletRequest, _mockPortletRequest, _themeDisplay);

		Assert.assertNotNull(panelCategoriesJSONArray);
		Assert.assertTrue(panelCategoriesJSONArray.length() > 0);

		for (int i = 0; i < panelCategoriesJSONArray.length(); i++) {
			JSONObject panelCategoryJSONObject =
				panelCategoriesJSONArray.getJSONObject(i);

			Assert.assertTrue(panelCategoryJSONObject.has("active"));
			Assert.assertTrue(panelCategoryJSONObject.has("homeURL"));
			Assert.assertTrue(panelCategoryJSONObject.has("key"));
			Assert.assertTrue(panelCategoryJSONObject.has("label"));
		}
	}

	@Test
	public void testGetSitesJSONObject() {
		JSONObject jsonObject = _getSitesJSONObject();

		Assert.assertFalse(jsonObject.has("mySites"));
		Assert.assertFalse(jsonObject.has("recentSites"));
		Assert.assertFalse(jsonObject.has("viewAllURL"));
	}

	@Test
	public void testGetSitesJSONObjectWithCompanyAdmin() throws Exception {
		_setCompanyAdmin();

		Group group = _addGroup();

		_userLocalService.unsetGroupUsers(
			group.getGroupId(), new long[] {TestPropsValues.getUserId()},
			ServiceContextTestUtil.getServiceContext());

		Company company = _themeDisplay.getCompany();

		Group companyGroup = company.getGroup();

		_setRecentGroupsValue(
			_mockHttpServletRequest,
			StringUtil.merge(
				new long[] {group.getGroupId(), companyGroup.getGroupId()}));

		JSONObject jsonObject = _getSitesJSONObject();

		JSONArray mySitesJSONArray = jsonObject.getJSONArray("mySites");

		Assert.assertEquals(1, mySitesJSONArray.length());

		Assert.assertTrue(jsonObject.has("viewAllURL"));

		_assertGroupKeys(
			Arrays.asList(group, companyGroup),
			jsonObject.getJSONArray("recentSites"));
	}

	@Test
	public void testGetSitesJSONObjectWithMySites() throws Exception {
		_addMySiteGroups(1);

		_testGetSitesJSONObjectWithMySitesAnd1Site();

		_addMySiteGroups(2);

		_testGetSitesJSONObjectWithMySitesAndMaxSites();

		_addMySiteGroups(1);

		_testGetSitesJSONObjectWithMySitesAndMaxPlus1Sites();
	}

	@Test
	public void testGetSitesJSONObjectWithMySitesAndRecentSites()
		throws Exception {

		_addMySiteGroups(2);
		_addRecentGroups(1);

		_testGetSitesJSONObjectWithMySitesAndRecentSitesAndMaxSites();

		_addRecentGroups(1);

		_testGetSitesJSONObjectWithMySitesAndRecentSitesAndMaxPlus1Sites();
	}

	@Test
	public void testGetSitesJSONObjectWithNonmemberRecentSites()
		throws Exception {

		_addMySiteGroups(2);

		Group group = _addGroup();

		_setRecentGroupsValue(
			_mockHttpServletRequest, String.valueOf(group.getGroupId()));

		JSONObject jsonObject = _getSitesJSONObject();

		Assert.assertFalse(jsonObject.has("recentSites"));
		Assert.assertFalse(jsonObject.has("viewAllURL"));

		_assertGroupKeys(
			_groups.subList(0, 2), jsonObject.getJSONArray("mySites"));
	}

	@Test
	public void testGetSitesJSONObjectWithRecentSites() throws Exception {
		_addRecentGroups(3);

		_testGetSitesJSONObjectWithRecentSitesAndMaxSites();

		_addRecentGroups(1);

		_testGetSitesJSONObjectWithRecentSitesAndMaxPlus1Sites();
	}

	private Group _addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		LayoutTestUtil.addTypePortletLayout(group);

		_groups.add(group);

		return group;
	}

	private void _addMySiteGroups(int max) throws Exception {
		for (int i = 0; i < max; i++) {
			Group group = _addGroup();

			_userLocalService.setGroupUsers(
				group.getGroupId(), new long[] {_user.getUserId()});
		}
	}

	private void _addRecentGroups(int max) throws Exception {
		for (int i = 0; i < max; i++) {
			Group group = _addGroup();

			_recentGroups.add(group);

			_userLocalService.setGroupUsers(
				group.getGroupId(), new long[] {_user.getUserId()});
		}

		_setRecentGroupsValue(
			_mockHttpServletRequest,
			StringUtil.merge(
				ListUtil.toList(_recentGroups, Group::getGroupId)));
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

	private void _assertGroupKeys(
		List<Group> expectedGroups, JSONArray jsonArray) {

		Assert.assertEquals(
			ListUtil.toList(expectedGroups, Group::getGroupKey),
			JSONUtil.toStringList(jsonArray, "key"));
	}

	private JSONObject _getSitesJSONObject() {
		return ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getSitesJSONObject",
			new Class<?>[] {
				HttpServletRequest.class, ResourceRequest.class,
				ResourceResponse.class, ThemeDisplay.class
			},
			_mockHttpServletRequest, _mockPortletRequest,
			new MockLiferayResourceResponse(), _themeDisplay);
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

	private void _setCompanyAdmin() throws Exception {
		User user = TestPropsValues.getUser();

		_themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		_themeDisplay.setUser(user);
	}

	private void _setRecentGroupsValue(
		HttpServletRequest httpServletRequest, String value) {

		SessionClicks.put(
			httpServletRequest.getSession(), _KEY_RECENT_GROUPS, value);
	}

	private void _setUser() throws Exception {
		_user = _addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_user);

		_themeDisplay.setPermissionChecker(permissionChecker);

		_themeDisplay.setUser(_user);

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(_user.getUserId());

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		_setRecentGroupsValue(_mockHttpServletRequest, StringPool.BLANK);
	}

	private void _testGetSitesJSONObjectWithMySitesAnd1Site() {
		JSONObject jsonObject = _getSitesJSONObject();

		Assert.assertFalse(jsonObject.has("mySites"));
		Assert.assertFalse(jsonObject.has("recentSites"));
		Assert.assertFalse(jsonObject.has("viewAllURL"));
	}

	private void _testGetSitesJSONObjectWithMySitesAndMaxPlus1Sites() {
		JSONObject jsonObject = _getSitesJSONObject();

		JSONArray mySitesJSONArray = jsonObject.getJSONArray("mySites");

		Assert.assertEquals(_MAX_SITES, mySitesJSONArray.length());

		Assert.assertFalse(jsonObject.has("recentSites"));
		Assert.assertTrue(jsonObject.has("viewAllURL"));
	}

	private void _testGetSitesJSONObjectWithMySitesAndMaxSites() {
		JSONObject jsonObject = _getSitesJSONObject();

		JSONArray mySitesJSONArray = jsonObject.getJSONArray("mySites");

		Assert.assertEquals(_MAX_SITES, mySitesJSONArray.length());

		Assert.assertFalse(jsonObject.has("recentSites"));
		Assert.assertFalse(jsonObject.has("viewAllURL"));
	}

	private void _testGetSitesJSONObjectWithMySitesAndRecentSitesAndMaxPlus1Sites() {
		JSONObject jsonObject = _getSitesJSONObject();

		JSONArray mySitesJSONArray = jsonObject.getJSONArray("mySites");

		Assert.assertEquals(1, mySitesJSONArray.length());

		Assert.assertTrue(jsonObject.has("viewAllURL"));

		_assertGroupKeys(_recentGroups, jsonObject.getJSONArray("recentSites"));
	}

	private void _testGetSitesJSONObjectWithMySitesAndRecentSitesAndMaxSites() {
		JSONObject jsonObject = _getSitesJSONObject();

		JSONArray mySitesJSONArray = jsonObject.getJSONArray("mySites");

		Assert.assertEquals(2, mySitesJSONArray.length());

		Assert.assertFalse(jsonObject.has("viewAllURL"));

		_assertGroupKeys(_recentGroups, jsonObject.getJSONArray("recentSites"));
	}

	private void _testGetSitesJSONObjectWithRecentSitesAndMaxPlus1Sites() {
		JSONObject jsonObject = _getSitesJSONObject();

		Assert.assertFalse(jsonObject.has("mySites"));
		Assert.assertTrue(jsonObject.has("viewAllURL"));

		_assertGroupKeys(
			_recentGroups.subList(0, _MAX_SITES),
			jsonObject.getJSONArray("recentSites"));
	}

	private void _testGetSitesJSONObjectWithRecentSitesAndMaxSites() {
		JSONObject jsonObject = _getSitesJSONObject();

		Assert.assertFalse(jsonObject.has("mySites"));
		Assert.assertFalse(jsonObject.has("viewAllURL"));

		_assertGroupKeys(_recentGroups, jsonObject.getJSONArray("recentSites"));
	}

	private static final String _KEY_RECENT_GROUPS =
		"com.liferay.site.util_recentGroups";

	private static final int _MAX_SITES = 3;

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

	private final List<Group> _recentGroups = new ArrayList<>();
	private ThemeDisplay _themeDisplay;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}