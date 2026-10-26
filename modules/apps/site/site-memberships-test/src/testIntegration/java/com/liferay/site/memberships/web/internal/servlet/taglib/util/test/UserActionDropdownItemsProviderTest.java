/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.servlet.taglib.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.memberships.constants.SiteMembershipsPortletKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class UserActionDropdownItemsProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			UserActionDropdownItemsProviderTest.class);

		bundle = BundleUtil.getBundle(
			bundle.getBundleContext(), "com.liferay.site.memberships.web");

		_depotRolesUtilClass = bundle.loadClass(
			"com.liferay.site.memberships.web.internal.util.DepotRolesUtil");

		Class<?> clazz = bundle.loadClass(
			"com.liferay.site.memberships.web.internal.servlet.taglib.util." +
				"UserActionDropdownItemsProvider");

		_constructor = clazz.getConstructor(
			User.class, RenderRequest.class, RenderResponse.class);
	}

	@Before
	public void setUp() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		_user = _addUser();
	}

	@Test
	@TestInfo("LPD-110527")
	public void testGetActionDropdownItemsWithAssetLibraryAdministrator()
		throws Exception {

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_addUser(
				_getRole(DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR)));

		Assert.assertFalse(
			GroupPermissionUtil.contains(
				permissionChecker, _depotEntry.getGroupId(),
				ActionKeys.ASSIGN_USER_ROLES));

		Assert.assertEquals(
			Collections.singletonList(
				_getRole(DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER)),
			_filterGroupRoles(permissionChecker));

		List<String> actions = _getActions(permissionChecker);

		Assert.assertTrue(actions.toString(), actions.contains("assignRoles"));
	}

	@Test
	@TestInfo("LPD-110527")
	public void testGetActionDropdownItemsWithAssetLibraryMember()
		throws Exception {

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_addUser());

		Assert.assertEquals(
			Collections.emptyList(), _filterGroupRoles(permissionChecker));

		List<String> actions = _getActions(permissionChecker);

		Assert.assertFalse(actions.toString(), actions.contains("assignRoles"));
	}

	private User _addUser(Role... roles) throws Exception {
		User user = UserTestUtil.addUser();

		_users.add(user);

		_userLocalService.addGroupUsers(
			_depotEntry.getGroupId(), new long[] {user.getUserId()});

		for (Role role : roles) {
			_userGroupRoleLocalService.addUserGroupRoles(
				user.getUserId(), _depotEntry.getGroupId(),
				new long[] {role.getRoleId()});
		}

		return user;
	}

	private List<Role> _filterGroupRoles(PermissionChecker permissionChecker)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_depotRolesUtilClass, "filterGroupRoles",
			new Class<?>[] {PermissionChecker.class, long.class, List.class},
			permissionChecker, _depotEntry.getGroupId(),
			Arrays.asList(
				_getRole(DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR),
				_getRole(DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER),
				_getRole(DepotRolesConstants.ASSET_LIBRARY_MEMBER),
				_getRole(DepotRolesConstants.ASSET_LIBRARY_OWNER)));
	}

	private List<String> _getActions(PermissionChecker permissionChecker)
		throws Exception {

		List<String> actions = new ArrayList<>();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(
				_portletLocalService.getPortletById(
					SiteMembershipsPortletKeys.SITE_MEMBERSHIPS_ADMIN),
				null));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(permissionChecker);
		themeDisplay.setScopeGroupId(_depotEntry.getGroupId());
		themeDisplay.setSiteGroupId(_depotEntry.getGroupId());

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		List<Map<String, Object>> dropdownItems = ReflectionTestUtil.invoke(
			_constructor.newInstance(
				_user, mockLiferayPortletRenderRequest,
				new MockLiferayPortletRenderResponse()),
			"getActionDropdownItems", new Class<?>[0]);

		for (Map<String, Object> dropdownItem : dropdownItems) {
			Map<String, Object> data = (Map<String, Object>)dropdownItem.get(
				"data");

			actions.add((String)data.get("action"));
		}

		return actions;
	}

	private Role _getRole(String name) throws Exception {
		return _roleLocalService.getRole(TestPropsValues.getCompanyId(), name);
	}

	private static Constructor<?> _constructor;
	private static Class<?> _depotRolesUtilClass;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}