/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.test.util.DepotTestUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class DepotAdminSelectRoleDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			DepotAdminSelectRoleDisplayContextTest.class);

		bundle = BundleUtil.getBundle(
			bundle.getBundleContext(), "com.liferay.depot.web");

		Class<?> clazz = bundle.loadClass(
			"com.liferay.depot.web.internal.display.context." +
				"DepotAdminSelectRoleDisplayContext$Step2");

		_constructor = clazz.getConstructor(
			Group.class, RenderRequest.class, RenderResponse.class, User.class);
	}

	@Before
	public void setUp() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(),
			FeatureFlagManagerUtil.isEnabled(
				TestPropsValues.getCompanyId(), "LPD-57283"),
			"LPD-57283");

		_depotEntry = _addDepotEntry(DepotConstants.TYPE_ASSET_LIBRARY);
	}

	@Test
	@TestInfo("LPD-110527")
	public void testFilterGroupRolesWithAssetLibraryAdministrator()
		throws Exception {

		DepotTestUtil.withAssetLibraryAdministrator(
			_depotEntry,
			user -> {
				Assert.assertFalse(
					GroupPermissionUtil.contains(
						PermissionThreadLocal.getPermissionChecker(),
						_depotEntry.getGroupId(),
						ActionKeys.ASSIGN_USER_ROLES));

				Assert.assertEquals(
					Collections.singletonList(
						_getRole(
							DepotRolesConstants.
								ASSET_LIBRARY_CONTENT_REVIEWER)),
					_filterGroupRoles(
						_depotEntry, user,
						DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
						DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
						DepotRolesConstants.ASSET_LIBRARY_MEMBER,
						DepotRolesConstants.ASSET_LIBRARY_OWNER));
			});
	}

	@Test
	@TestInfo("LPD-110527")
	public void testFilterGroupRolesWithAssetLibraryMember() throws Exception {
		DepotTestUtil.withAssetLibraryMember(
			_depotEntry,
			user -> Assert.assertEquals(
				Collections.emptyList(),
				_filterGroupRoles(
					_depotEntry, user,
					DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
					DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
					DepotRolesConstants.ASSET_LIBRARY_MEMBER,
					DepotRolesConstants.ASSET_LIBRARY_OWNER)));
	}

	@FeatureFlag("LPD-57283")
	@Test
	@TestInfo("LPD-110527")
	public void testFilterGroupRolesWithDesignLibraryAdministrator()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_DESIGN_LIBRARY);

		DepotTestUtil.withDesignLibraryAdministrator(
			depotEntry,
			user -> Assert.assertEquals(
				Collections.singletonList(
					_getRole(
						DepotRolesConstants.DESIGN_LIBRARY_CONTENT_REVIEWER)),
				_filterGroupRoles(
					depotEntry, user,
					DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR,
					DepotRolesConstants.DESIGN_LIBRARY_CONTENT_REVIEWER,
					DepotRolesConstants.DESIGN_LIBRARY_MEMBER,
					DepotRolesConstants.DESIGN_LIBRARY_OWNER)));
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), type,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private List<Role> _filterGroupRoles(
			DepotEntry depotEntry, User user, String... roleNames)
		throws Exception {

		List<Role> roles = new ArrayList<>();

		for (String roleName : roleNames) {
			roles.add(_getRole(roleName));
		}

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return ReflectionTestUtil.invoke(
			_constructor.newInstance(
				depotEntry.getGroup(), mockLiferayPortletRenderRequest,
				new MockLiferayPortletRenderResponse(), user),
			"_filterGroupRoles", new Class<?>[] {List.class}, roles);
	}

	private Role _getRole(String name) throws Exception {
		return _roleLocalService.getRole(TestPropsValues.getCompanyId(), name);
	}

	private static Constructor<?> _constructor;

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}