/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;
import com.liferay.site.cms.site.initializer.util.RoleUtil;

import java.io.Serializable;

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
 * @author Pedro Leite
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-58677")}
)
@RunWith(Arquillian.class)
public class ObjectEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CMPTestUtil.getOrAddGroup(ObjectEntryModelListenerTest.class);
	}

	@Test
	public void testOnAfterCreate() throws Exception {
		ObjectEntry projectObjectEntry = CMPTestUtil.addProjectObjectEntry();

		Group group = _groupLocalService.getGroup(
			projectObjectEntry.getGroupId());

		Assert.assertEquals(
			group.getName(LocaleUtil.getDefault()),
			MapUtil.getString(projectObjectEntry.getValues(), "title"));

		Role role = RoleUtil.getOrAddCMSAdministratorRole(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId());

		_assertResourceActions(
			projectObjectEntry, role.getName(), ActionKeys.ADD_DISCUSSION,
			ActionKeys.DELETE, ActionKeys.DELETE_DISCUSSION,
			ActionKeys.PERMISSIONS, ActionKeys.SUBSCRIBE, ActionKeys.UPDATE,
			ActionKeys.UPDATE_DISCUSSION, ActionKeys.VIEW,
			ObjectActionKeys.OBJECT_ENTRY_HISTORY);

		_assertResourceActions(
			projectObjectEntry, DepotRolesConstants.PROJECT_CONTRIBUTOR,
			ActionKeys.ADD_DISCUSSION, ActionKeys.VIEW);
		_assertResourceActions(
			projectObjectEntry, DepotRolesConstants.PROJECT_MANAGER,
			ActionKeys.ADD_DISCUSSION, ActionKeys.DELETE,
			ActionKeys.DELETE_DISCUSSION, ActionKeys.PERMISSIONS,
			ActionKeys.SUBSCRIBE, ActionKeys.UPDATE,
			ActionKeys.UPDATE_DISCUSSION, ActionKeys.VIEW,
			ObjectActionKeys.OBJECT_ENTRY_HISTORY);
		_assertResourceActions(
			projectObjectEntry, DepotRolesConstants.PROJECT_MEMBER,
			ActionKeys.ADD_DISCUSSION, ActionKeys.VIEW);

		ObjectEntry taskObjectEntry = CMPTestUtil.addTaskObjectEntry(
			projectObjectEntry);

		_assertResourceActions(
			taskObjectEntry, role.getName(), ActionKeys.ADD_DISCUSSION,
			ActionKeys.DELETE, ActionKeys.DELETE_DISCUSSION,
			ActionKeys.PERMISSIONS, ActionKeys.SUBSCRIBE, ActionKeys.UPDATE,
			ActionKeys.UPDATE_DISCUSSION, ActionKeys.VIEW,
			ObjectActionKeys.OBJECT_ENTRY_HISTORY);

		_assertResourceActions(
			taskObjectEntry, DepotRolesConstants.PROJECT_CONTRIBUTOR,
			ActionKeys.ADD_DISCUSSION, ActionKeys.UPDATE, ActionKeys.VIEW);
		_assertResourceActions(
			taskObjectEntry, DepotRolesConstants.PROJECT_MANAGER,
			ActionKeys.ADD_DISCUSSION, ActionKeys.DELETE,
			ActionKeys.DELETE_DISCUSSION, ActionKeys.PERMISSIONS,
			ActionKeys.SUBSCRIBE, ActionKeys.UPDATE,
			ActionKeys.UPDATE_DISCUSSION, ActionKeys.VIEW,
			ObjectActionKeys.OBJECT_ENTRY_HISTORY);
		_assertResourceActions(
			taskObjectEntry, DepotRolesConstants.PROJECT_MEMBER,
			ActionKeys.ADD_DISCUSSION, ActionKeys.VIEW);
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		ObjectEntry projectObjectEntry = CMPTestUtil.addProjectObjectEntry();

		User user1 = UserTestUtil.addUser(projectObjectEntry.getGroupId());
		User user2 = UserTestUtil.addUser(projectObjectEntry.getGroupId());

		Map<String, Serializable> values = projectObjectEntry.getValues();

		values.put("r_userToCMPProjectManager_userId", user1.getUserId());
		values.put("r_userToCMPProjectSponsor_userId", user2.getUserId());

		projectObjectEntry.setValues(values);

		projectObjectEntry = _objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), projectObjectEntry.getObjectEntryId(),
			projectObjectEntry.getObjectEntryFolderId(), values,
			ServiceContextTestUtil.getServiceContext());

		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MANAGER),
			projectObjectEntry.getGroupId(), user1.getUserId());
		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MEMBER),
			projectObjectEntry.getGroupId(), user2.getUserId());
	}

	private void _assertResourceActions(
			ObjectEntry objectEntry, String roleName, String... actionIds)
		throws Exception {

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), roleName);

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				objectEntry.getCompanyId(), objectEntry.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry.getObjectEntryId()),
				role.getRoleId());

		for (ResourceAction resourceAction :
				_resourceActionLocalService.getResourceActions(
					objectEntry.getModelClassName())) {

			String actionId = resourceAction.getActionId();

			Assert.assertEquals(
				ArrayUtil.contains(actionIds, actionId),
				resourcePermission.hasActionId(actionId));
		}
	}

	private void _assertUserGroupRoles(
		int expectedCount, List<String> expectedUserGroupRoleNames,
		long groupId, long userId) {

		List<Role> userGroupRoles = _roleLocalService.getUserGroupRoles(
			userId, groupId);

		Assert.assertEquals(
			userGroupRoles.toString(), expectedCount, userGroupRoles.size());

		List<String> userGroupRoleNames = TransformUtil.transform(
			userGroupRoles, Role::getName);

		Assert.assertTrue(
			userGroupRoleNames.containsAll(expectedUserGroupRoleNames));
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}