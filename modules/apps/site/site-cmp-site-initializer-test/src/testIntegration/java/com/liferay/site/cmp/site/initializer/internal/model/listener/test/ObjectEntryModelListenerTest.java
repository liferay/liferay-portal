/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
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
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
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
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
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

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testOnAfterCreate() throws Exception {
		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry();

		Group group = _groupLocalService.getGroup(
			cmpProjectObjectEntry.getGroupId());

		Assert.assertEquals(
			group.getName(LocaleUtil.getDefault()),
			MapUtil.getString(cmpProjectObjectEntry.getValues(), "title"));

		Role role = RoleUtil.getOrAddCMSAdministratorRole(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId());

		_assertResourceActions(
			cmpProjectObjectEntry, role.getName(), ActionKeys.ADD_DISCUSSION,
			ActionKeys.DELETE, ActionKeys.DELETE_DISCUSSION,
			ActionKeys.PERMISSIONS, ActionKeys.SUBSCRIBE, ActionKeys.UPDATE,
			ActionKeys.UPDATE_DISCUSSION, ActionKeys.VIEW,
			ObjectActionKeys.OBJECT_ENTRY_HISTORY);

		_assertResourceActions(
			cmpProjectObjectEntry, DepotRolesConstants.PROJECT_CONTRIBUTOR,
			ActionKeys.ADD_DISCUSSION, ActionKeys.VIEW);
		_assertResourceActions(
			cmpProjectObjectEntry, DepotRolesConstants.PROJECT_MANAGER,
			ActionKeys.ADD_DISCUSSION, ActionKeys.DELETE,
			ActionKeys.DELETE_DISCUSSION, ActionKeys.PERMISSIONS,
			ActionKeys.SUBSCRIBE, ActionKeys.UPDATE,
			ActionKeys.UPDATE_DISCUSSION, ActionKeys.VIEW,
			ObjectActionKeys.OBJECT_ENTRY_HISTORY);
		_assertResourceActions(
			cmpProjectObjectEntry, DepotRolesConstants.PROJECT_MEMBER,
			ActionKeys.ADD_DISCUSSION, ActionKeys.VIEW,
			ObjectActionKeys.OBJECT_ENTRY_HISTORY);

		ObjectEntry cmpProjectLinkObjectEntry =
			CMPTestUtil.addCMPProjectLinkObjectEntry(cmpProjectObjectEntry);

		_assertResourceActions(
			cmpProjectLinkObjectEntry, role.getName(), ActionKeys.DELETE,
			ActionKeys.PERMISSIONS, ActionKeys.UPDATE, ActionKeys.VIEW);
		_assertResourceActions(
			cmpProjectLinkObjectEntry, DepotRolesConstants.PROJECT_CONTRIBUTOR,
			ActionKeys.DELETE, ActionKeys.VIEW);
		_assertResourceActions(
			cmpProjectLinkObjectEntry, DepotRolesConstants.PROJECT_MANAGER,
			ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
			ActionKeys.VIEW);
		_assertResourceActions(
			cmpProjectLinkObjectEntry, DepotRolesConstants.PROJECT_MEMBER,
			ActionKeys.VIEW);

		ObjectEntry cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry);

		_assertResourceActions(
			cmpTaskObjectEntry, role.getName(), ActionKeys.ADD_DISCUSSION,
			ActionKeys.DELETE, ActionKeys.DELETE_DISCUSSION,
			ActionKeys.PERMISSIONS, ActionKeys.SUBSCRIBE, ActionKeys.UPDATE,
			ActionKeys.UPDATE_DISCUSSION, ActionKeys.VIEW,
			ObjectActionKeys.OBJECT_ENTRY_HISTORY);
		_assertResourceActions(
			cmpTaskObjectEntry, DepotRolesConstants.PROJECT_CONTRIBUTOR,
			ActionKeys.ADD_DISCUSSION, ActionKeys.UPDATE, ActionKeys.VIEW,
			ObjectActionKeys.OBJECT_ENTRY_HISTORY);
		_assertResourceActions(
			cmpTaskObjectEntry, DepotRolesConstants.PROJECT_MANAGER,
			ActionKeys.ADD_DISCUSSION, ActionKeys.DELETE,
			ActionKeys.DELETE_DISCUSSION, ActionKeys.PERMISSIONS,
			ActionKeys.SUBSCRIBE, ActionKeys.UPDATE,
			ActionKeys.UPDATE_DISCUSSION, ActionKeys.VIEW,
			ObjectActionKeys.OBJECT_ENTRY_HISTORY);
		_assertResourceActions(
			cmpTaskObjectEntry, DepotRolesConstants.PROJECT_MEMBER,
			ActionKeys.ADD_DISCUSSION, ActionKeys.VIEW,
			ObjectActionKeys.OBJECT_ENTRY_HISTORY);

		ObjectEntry cmpTaskLinkObjectEntry =
			CMPTestUtil.addCMPTaskLinkObjectEntry(cmpTaskObjectEntry);

		_assertResourceActions(
			cmpTaskLinkObjectEntry, role.getName(), ActionKeys.DELETE,
			ActionKeys.PERMISSIONS, ActionKeys.UPDATE, ActionKeys.VIEW);
		_assertResourceActions(
			cmpTaskLinkObjectEntry, DepotRolesConstants.PROJECT_CONTRIBUTOR,
			ActionKeys.DELETE, ActionKeys.VIEW);
		_assertResourceActions(
			cmpTaskLinkObjectEntry, DepotRolesConstants.PROJECT_MANAGER,
			ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
			ActionKeys.VIEW);
		_assertResourceActions(
			cmpTaskLinkObjectEntry, DepotRolesConstants.PROJECT_MEMBER,
			ActionKeys.VIEW);
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry();

		User user1 = UserTestUtil.addUser(cmpProjectObjectEntry.getGroupId());
		User user2 = UserTestUtil.addUser(cmpProjectObjectEntry.getGroupId());

		Map<String, Serializable> values = cmpProjectObjectEntry.getValues();

		values.put("r_userToCMPProjectManager_userId", user1.getUserId());
		values.put("r_userToCMPProjectSponsor_userId", user2.getUserId());

		cmpProjectObjectEntry.setValues(values);

		cmpProjectObjectEntry =
			_objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(),
				cmpProjectObjectEntry.getObjectEntryId(),
				cmpProjectObjectEntry.getObjectEntryFolderId(), values,
				ServiceContextTestUtil.getServiceContext());

		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MANAGER),
			cmpProjectObjectEntry.getGroupId(), user1.getUserId());
		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MEMBER),
			cmpProjectObjectEntry.getGroupId(), user2.getUserId());
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		ObjectEntry cmsBasicWebContentObjectEntry =
			_addCMSBasicWebContentObjectEntry();

		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry();

		ObjectEntry cmpProjectLinkObjectEntry = _addCMPProjectLinkObjectEntry(
			cmsBasicWebContentObjectEntry, cmpProjectObjectEntry);

		ObjectEntry cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry);

		ObjectEntry cmpTaskLinkObjectEntry = _addCMPTaskLinkObjectEntry(
			cmsBasicWebContentObjectEntry, cmpTaskObjectEntry);

		Assert.assertNotNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpProjectLinkObjectEntry.getObjectEntryId()));
		Assert.assertNotNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpTaskLinkObjectEntry.getObjectEntryId()));

		_objectEntryLocalService.deleteObjectEntry(
			cmsBasicWebContentObjectEntry.getObjectEntryId());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpProjectLinkObjectEntry.getObjectEntryId()));
		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpTaskLinkObjectEntry.getObjectEntryId()));

		_testOnBeforeRemoveWhenObjectEntryIsNotCMSObjectEntry();
	}

	private ObjectEntry _addCMPProjectLinkObjectEntry(
			ObjectEntry linkedObjectEntry, ObjectEntry cmpProjectObjectEntry)
		throws Exception {

		ObjectDefinition cmpProjectLinkObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT_LINK", cmpProjectObjectEntry.getCompanyId());

		Group group = _groupLocalService.getGroup(
			linkedObjectEntry.getGroupId());

		return _objectEntryLocalService.addObjectEntry(
			cmpProjectObjectEntry.getGroupId(),
			cmpProjectObjectEntry.getUserId(),
			cmpProjectLinkObjectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"classExternalReferenceCode",
				linkedObjectEntry.getExternalReferenceCode()
			).put(
				"className", linkedObjectEntry.getModelClassName()
			).put(
				"groupExternalReferenceCode", group.getExternalReferenceCode()
			).put(
				"r_cmpProjectToCMPProjectLinks_c_cmpProjectId",
				cmpProjectObjectEntry.getObjectEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addCMPTaskLinkObjectEntry(
			ObjectEntry linkedObjectEntry, ObjectEntry cmpTaskObjectEntry)
		throws Exception {

		ObjectDefinition cmpTaskLinkObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK_LINK", cmpTaskObjectEntry.getCompanyId());

		Group group = _groupLocalService.getGroup(
			linkedObjectEntry.getGroupId());

		return _objectEntryLocalService.addObjectEntry(
			cmpTaskObjectEntry.getGroupId(), cmpTaskObjectEntry.getUserId(),
			cmpTaskLinkObjectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"classExternalReferenceCode",
				linkedObjectEntry.getExternalReferenceCode()
			).put(
				"className", linkedObjectEntry.getModelClassName()
			).put(
				"groupExternalReferenceCode", group.getExternalReferenceCode()
			).put(
				"r_cmpTaskToCMPTaskLinks_c_cmpTaskId",
				cmpTaskObjectEntry.getObjectEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addCMSBasicWebContentObjectEntry() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", _depotEntry.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), _depotEntry.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, "en_US",
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build(),
			ServiceContextTestUtil.getServiceContext());
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

	private void _testOnBeforeRemoveWhenObjectEntryIsNotCMSObjectEntry()
		throws Exception {

		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry();

		ObjectEntry cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry();

		ObjectEntry cmpTaskLinkObjectEntry = _addCMPTaskLinkObjectEntry(
			cmpProjectObjectEntry, cmpTaskObjectEntry);

		_objectEntryLocalService.deleteObjectEntry(
			cmpProjectObjectEntry.getObjectEntryId());

		Assert.assertNotNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpTaskLinkObjectEntry.getObjectEntryId()));
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}