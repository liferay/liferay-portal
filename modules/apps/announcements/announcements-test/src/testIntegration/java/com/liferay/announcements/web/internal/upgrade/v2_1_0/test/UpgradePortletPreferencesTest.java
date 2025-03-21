/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.upgrade.v2_1_0.test;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Accessor;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marco Galluzzi
 */
@RunWith(Arquillian.class)
public class UpgradePortletPreferencesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		_portletId = LayoutTestUtil.addPortletToLayout(
			_layout, AnnouncementsPortletKeys.ANNOUNCEMENTS);
	}

	@Test
	public void testUpgradeEmptySelectedScopeIds() throws Exception {
		Map<String, String> map = HashMapBuilder.put(
			"selectedScopeGroupIds", StringPool.BLANK
		).put(
			"selectedScopeOrganizationIds", StringPool.BLANK
		).put(
			"selectedScopeRoleIds", StringPool.BLANK
		).put(
			"selectedScopeUserGroupIds", StringPool.BLANK
		).build();

		_testUpgrade(map, map);
	}

	@Test
	public void testUpgradeExternalReferenceCodeWithSpecialCharacters()
		throws Exception {

		String specialCharacters = "One\\+-!():^[]\"{}~*?|&,; /Two";

		_testUpgrade(
			_addModels(
				1,
				() -> {
					Group group = GroupTestUtil.addGroup();

					group.setExternalReferenceCode(specialCharacters);

					return _groupLocalService.updateGroup(group);
				}),
			_addModels(
				1,
				() -> {
					Organization organization =
						OrganizationTestUtil.addOrganization();

					organization.setExternalReferenceCode(specialCharacters);

					return _organizationLocalService.updateOrganization(
						organization);
				}),
			_addModels(
				1,
				() -> {
					Role role = RoleTestUtil.addRole(
						RoleConstants.TYPE_REGULAR);

					role.setExternalReferenceCode(specialCharacters);

					return _roleLocalService.updateRole(role);
				}),
			_addModels(
				1,
				() -> {
					UserGroup userGroup = UserGroupTestUtil.addUserGroup();

					userGroup.setExternalReferenceCode(specialCharacters);

					return _userGroupLocalService.updateUserGroup(userGroup);
				}));
	}

	@Test
	public void testUpgradeInvalidSelectedScopeId() throws Exception {
		String randomSelectedScopeId = String.valueOf(
			RandomTestUtil.randomLong());

		Map<String, String> map = HashMapBuilder.put(
			"selectedScopeGroupIds", randomSelectedScopeId
		).put(
			"selectedScopeOrganizationIds", randomSelectedScopeId
		).put(
			"selectedScopeRoleIds", randomSelectedScopeId
		).put(
			"selectedScopeUserGroupIds", randomSelectedScopeId
		).build();

		_testUpgrade(map, map);
	}

	@Test
	public void testUpgradeInvalidSelectedScopeIds() throws Exception {
		String randomSelectedScopeIds =
			String.valueOf(RandomTestUtil.randomLong()) + ',' +
				String.valueOf(RandomTestUtil.randomLong()) + ',' +
					String.valueOf(RandomTestUtil.randomLong());

		Map<String, String> map = HashMapBuilder.put(
			"selectedScopeGroupIds", randomSelectedScopeIds
		).put(
			"selectedScopeOrganizationIds", randomSelectedScopeIds
		).put(
			"selectedScopeRoleIds", randomSelectedScopeIds
		).put(
			"selectedScopeUserGroupIds", randomSelectedScopeIds
		).build();

		_testUpgrade(map, map);
	}

	@Test
	public void testUpgradeSomeInvalidSelectedScopeIds() throws Exception {
		List<Group> groups = _addModels(2, GroupTestUtil::addGroup);
		List<Organization> organizations = _addModels(
			2, OrganizationTestUtil::addOrganization);
		List<Role> roles = _addModels(
			2, () -> RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR));
		List<UserGroup> userGroups = _addModels(
			2, UserGroupTestUtil::addUserGroup);

		String selectedScopeGroupIds = ListUtil.toString(
			groups, Group.GROUP_ID_ACCESSOR);
		String selectedScopeOrganizationIds = ListUtil.toString(
			organizations, Organization.ORGANIZATION_ID_ACCESSOR);
		String selectedScopeRoleIds = ListUtil.toString(
			roles, Role.ROLE_ID_ACCESSOR);
		String selectedScopeUserGroupIds = ListUtil.toString(
			userGroups, UserGroup.USER_GROUP_ID_ACCESSOR);

		String randomSelectedScopeId = String.valueOf(
			RandomTestUtil.randomLong());

		_testUpgrade(
			_toJSON(groups, _GROUP_EXTERNAL_REFERENCE_CODE_ACCESSOR),
			selectedScopeGroupIds + ',' + randomSelectedScopeId,
			_toJSON(
				organizations, _ORGANIZATION_EXTERNAL_REFERENCE_CODE_ACCESSOR),
			selectedScopeOrganizationIds + ',' + randomSelectedScopeId,
			_toJSON(roles, _ROLE_EXTERNAL_REFERENCE_CODE_ACCESSOR),
			selectedScopeRoleIds + ',' + randomSelectedScopeId,
			_toJSON(userGroups, _USERGROUP_EXTERNAL_REFERENCE_CODE_ACCESSOR),
			selectedScopeUserGroupIds + ',' + randomSelectedScopeId);
	}

	@Test
	public void testUpgradeValidSelectedScopeId() throws Exception {
		_testUpgrade(
			_addModels(1, GroupTestUtil::addGroup),
			_addModels(1, OrganizationTestUtil::addOrganization),
			_addModels(
				1, () -> RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR)),
			_addModels(1, UserGroupTestUtil::addUserGroup));
	}

	@Test
	public void testUpgradeValidSelectedScopeIds() throws Exception {
		_testUpgrade(
			_addModels(3, GroupTestUtil::addGroup),
			_addModels(3, OrganizationTestUtil::addOrganization),
			_addModels(
				3, () -> RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR)),
			_addModels(3, UserGroupTestUtil::addUserGroup));
	}

	@Test
	public void testUpgradeWithoutSelectedScopeIds() throws Exception {
		_testUpgrade(Collections.emptyMap(), Collections.emptyMap());
	}

	private static <T extends ExternalReferenceCodeModel> Accessor<T, String>
		_getExternalReferenceCodeAccessor(Class<T> typeClass) {

		return new Accessor<T, String>() {

			@Override
			public String get(T t) {
				return t.getExternalReferenceCode();
			}

			@Override
			public Class<String> getAttributeClass() {
				return String.class;
			}

			@Override
			public Class<T> getTypeClass() {
				return typeClass;
			}

		};
	}

	private <T> List<T> _addModels(
			int count, UnsafeSupplier<T, Exception> unsafeSupplier)
		throws Exception {

		List<T> models = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			models.add(unsafeSupplier.get());
		}

		return models;
	}

	private void _assertPortletPreferences(Map<String, String> expectedMap)
		throws Exception {

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_layout, _portletId);

		Map<String, String[]> map = portletPreferences.getMap();

		Assert.assertEquals(
			MapUtil.toString(map), expectedMap.size(), map.size());

		for (Map.Entry<String, String> entry : expectedMap.entrySet()) {
			Assert.assertTrue(entry.getKey(), map.containsKey(entry.getKey()));

			Assert.assertArrayEquals(
				new String[] {entry.getValue()}, map.get(entry.getKey()));
		}
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_entityCache.clearCache();
			_multiVMPool.clear();
		}
	}

	private void _testUpgrade(
			List<Group> groups, List<Organization> organizations,
			List<Role> roles, List<UserGroup> userGroups)
		throws Exception {

		_testUpgrade(
			_toJSON(groups, _GROUP_EXTERNAL_REFERENCE_CODE_ACCESSOR),
			ListUtil.toString(groups, Group.GROUP_ID_ACCESSOR),
			_toJSON(
				organizations, _ORGANIZATION_EXTERNAL_REFERENCE_CODE_ACCESSOR),
			ListUtil.toString(
				organizations, Organization.ORGANIZATION_ID_ACCESSOR),
			_toJSON(roles, _ROLE_EXTERNAL_REFERENCE_CODE_ACCESSOR),
			ListUtil.toString(roles, Role.ROLE_ID_ACCESSOR),
			_toJSON(userGroups, _USERGROUP_EXTERNAL_REFERENCE_CODE_ACCESSOR),
			ListUtil.toString(userGroups, UserGroup.USER_GROUP_ID_ACCESSOR));
	}

	private void _testUpgrade(
			Map<String, String> expectedMap, Map<String, String> map)
		throws Exception {

		LayoutTestUtil.updateLayoutPortletPreferences(_layout, _portletId, map);

		_assertPortletPreferences(map);

		_runUpgrade();

		_assertPortletPreferences(expectedMap);
	}

	private void _testUpgrade(
			String selectedScopeGroupExternalReferenceCodes,
			String selectedScopeGroupIds,
			String selectedScopeOrganizationExternalReferenceCodes,
			String selectedScopeOrganizationIds,
			String selectedScopeRoleExternalReferenceCodes,
			String selectedScopeRoleIds,
			String selectedScopeUserGroupExternalReferenceCodes,
			String selectedScopeUserGroupIds)
		throws Exception {

		_testUpgrade(
			HashMapBuilder.put(
				"selectedScopeGroupExternalReferenceCodes",
				selectedScopeGroupExternalReferenceCodes
			).put(
				"selectedScopeGroupIds", selectedScopeGroupIds
			).put(
				"selectedScopeOrganizationExternalReferenceCodes",
				selectedScopeOrganizationExternalReferenceCodes
			).put(
				"selectedScopeOrganizationIds", selectedScopeOrganizationIds
			).put(
				"selectedScopeRoleExternalReferenceCodes",
				selectedScopeRoleExternalReferenceCodes
			).put(
				"selectedScopeRoleIds", selectedScopeRoleIds
			).put(
				"selectedScopeUserGroupExternalReferenceCodes",
				selectedScopeUserGroupExternalReferenceCodes
			).put(
				"selectedScopeUserGroupIds", selectedScopeUserGroupIds
			).build(),
			HashMapBuilder.put(
				"selectedScopeGroupIds", selectedScopeGroupIds
			).put(
				"selectedScopeOrganizationIds", selectedScopeOrganizationIds
			).put(
				"selectedScopeRoleIds", selectedScopeRoleIds
			).put(
				"selectedScopeUserGroupIds", selectedScopeUserGroupIds
			).build());
	}

	private <T> String _toJSON(List<T> list, Accessor<T, String> accessor) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			TransformUtil.transform(
				ListUtil.toList(list, accessor), HtmlUtil::escape));

		return jsonArray.toString();
	}

	private static final String _CLASS_NAME =
		"com.liferay.announcements.web.internal.upgrade.v2_1_0." +
			"UpgradePortletPreferences";

	private static final Accessor<Group, String>
		_GROUP_EXTERNAL_REFERENCE_CODE_ACCESSOR =
			_getExternalReferenceCodeAccessor(Group.class);

	private static final Accessor<Organization, String>
		_ORGANIZATION_EXTERNAL_REFERENCE_CODE_ACCESSOR =
			_getExternalReferenceCodeAccessor(Organization.class);

	private static final Accessor<Role, String>
		_ROLE_EXTERNAL_REFERENCE_CODE_ACCESSOR =
			_getExternalReferenceCodeAccessor(Role.class);

	private static final Accessor<UserGroup, String>
		_USERGROUP_EXTERNAL_REFERENCE_CODE_ACCESSOR =
			_getExternalReferenceCodeAccessor(UserGroup.class);

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	private String _portletId;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.announcements.web.internal.upgrade.registry.AnnouncementsWebUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

}