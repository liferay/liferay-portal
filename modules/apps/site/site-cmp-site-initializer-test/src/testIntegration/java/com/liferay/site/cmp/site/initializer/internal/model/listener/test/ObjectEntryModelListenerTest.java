/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.exception.DepotEntryStagedException;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;
import com.liferay.site.cmp.site.initializer.test.util.ObjectEntryTestUtil;
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

		_assertCompletionRate(cmpProjectObjectEntry, 0);

		ObjectEntryTestUtil.partialUpdateObjectEntry(
			cmpTaskObjectEntry,
			HashMapBuilder.<String, Serializable>put(
				"state", "inProgress"
			).build());
		ObjectEntryTestUtil.partialUpdateObjectEntry(
			cmpTaskObjectEntry,
			HashMapBuilder.<String, Serializable>put(
				"state", "done"
			).build());

		_assertCompletionRate(cmpProjectObjectEntry, 100);

		CMPTestUtil.addCMPTaskObjectEntry(cmpProjectObjectEntry);

		_assertCompletionRate(cmpProjectObjectEntry, 100);

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

		_testOnAfterCreateWithCMSAdministratorUser();
	}

	@Test
	public void testOnAfterRemove() throws Exception {

		// Deleting a CMP project with a staged asset library rolls back

		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry(
				WorkflowConstants.ACTION_PUBLISH);

		long groupId = cmpProjectObjectEntry.getGroupId();

		GroupTestUtil.enableLocalStaging(_groupLocalService.getGroup(groupId));

		try {
			_objectEntryLocalService.deleteObjectEntry(
				cmpProjectObjectEntry.getObjectEntryId());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			Assert.assertTrue(
				modelListenerException.getCause() instanceof
					DepotEntryStagedException);
		}

		Assert.assertNotNull(
			_depotEntryLocalService.fetchGroupDepotEntry(groupId));
		Assert.assertNotNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpProjectObjectEntry.getObjectEntryId()));

		_stagingLocalService.disableStaging(
			_groupLocalService.getGroup(groupId),
			ServiceContextTestUtil.getServiceContext());

		_objectEntryLocalService.deleteObjectEntry(
			cmpProjectObjectEntry.getObjectEntryId());

		// Deleting a draft CMP project deletes its asset library

		cmpProjectObjectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, cmpProjectObjectEntry.getStatus());

		groupId = cmpProjectObjectEntry.getGroupId();

		Assert.assertNotNull(
			_depotEntryLocalService.fetchGroupDepotEntry(groupId));

		_objectEntryLocalService.deleteObjectEntry(
			cmpProjectObjectEntry.getObjectEntryId());

		Assert.assertNull(
			_depotEntryLocalService.fetchGroupDepotEntry(groupId));
		Assert.assertNull(_groupLocalService.fetchGroup(groupId));

		// Deleting an approved CMP project deletes its asset library and tasks

		cmpProjectObjectEntry = CMPTestUtil.addCMPProjectObjectEntry(
			WorkflowConstants.ACTION_PUBLISH);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			cmpProjectObjectEntry.getStatus());

		groupId = cmpProjectObjectEntry.getGroupId();

		Assert.assertNotNull(
			_depotEntryLocalService.fetchGroupDepotEntry(groupId));

		ObjectEntry cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry);

		_objectEntryLocalService.deleteObjectEntry(
			cmpProjectObjectEntry.getObjectEntryId());

		Assert.assertNull(
			_depotEntryLocalService.fetchGroupDepotEntry(groupId));
		Assert.assertNull(_groupLocalService.fetchGroup(groupId));
		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpTaskObjectEntry.getObjectEntryId()));
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

		cmpProjectObjectEntry = ObjectEntryTestUtil.partialUpdateObjectEntry(
			cmpProjectObjectEntry, values);

		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MANAGER),
			cmpProjectObjectEntry.getGroupId(), user1.getUserId());
		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MEMBER),
			cmpProjectObjectEntry.getGroupId(), user2.getUserId());

		_testOnAfterUpdateWhenProjectManagerAndProjectSponsorAreUnchanged(
			cmpProjectObjectEntry);
		_testOnAfterUpdateWhenProjectManagerAndProjectSponsorChange(
			cmpProjectObjectEntry);
		_testOnAfterUpdateWhenProjectManagerAssignsAndRevokesUser(
			cmpProjectObjectEntry);
		_testOnAfterUpdateWhenProjectManagerIsClearedWithoutAssignMembersPermission(
			cmpProjectObjectEntry);
	}

	@Test
	public void testOnBeforeRemove() throws Exception {

		// Deleting a CMS object entry deletes its links

		ObjectEntry cmsBasicWebContentObjectEntry =
			CMPTestUtil.addCMSBasicWebContentObjectEntry(
				_depotEntry, RandomTestUtil.randomString());

		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry();

		ObjectEntry cmpProjectLinkObjectEntry =
			CMPTestUtil.addCMPProjectLinkObjectEntry(
				cmpProjectObjectEntry, cmsBasicWebContentObjectEntry);

		ObjectEntry cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry);

		ObjectEntry cmpTaskLinkObjectEntry =
			CMPTestUtil.addCMPTaskLinkObjectEntry(
				cmpTaskObjectEntry, cmsBasicWebContentObjectEntry);

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

		// Deleting a non-CMS object entry keeps its links

		cmpProjectObjectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry();

		cmpTaskLinkObjectEntry = CMPTestUtil.addCMPTaskLinkObjectEntry(
			cmpTaskObjectEntry, cmpProjectObjectEntry);

		_objectEntryLocalService.deleteObjectEntry(
			cmpProjectObjectEntry.getObjectEntryId());

		Assert.assertNotNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpTaskLinkObjectEntry.getObjectEntryId()));
	}

	private void _assertCompletionRate(
			ObjectEntry cmpProjectObjectEntry, int expectedCompletionRate)
		throws Exception {

		Assert.assertEquals(
			expectedCompletionRate,
			MapUtil.getInteger(
				_objectEntryLocalService.getValues(
					cmpProjectObjectEntry.getObjectEntryId()),
				"completionRate"));
	}

	private void _assertGroupMembershipWithoutUserGroupRoles(
		ObjectEntry cmpProjectObjectEntry, User user) {

		_assertUserGroupRoles(
			0, Collections.emptyList(), cmpProjectObjectEntry.getGroupId(),
			user.getUserId());

		Assert.assertTrue(
			_groupLocalService.hasUserGroup(
				user.getUserId(), cmpProjectObjectEntry.getGroupId()));
	}

	private void _assertReassignment(
			ObjectEntry cmpProjectObjectEntry, User originalProjectManagerUser,
			User originalProjectSponsorUser, User user)
		throws Exception {

		User newProjectManagerUser = UserTestUtil.addUser();
		User newProjectSponsorUser = UserTestUtil.addUser();

		UserTestUtil.setUser(user);

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, newProjectManagerUser.getUserId(),
			newProjectSponsorUser.getUserId(), user.getUserId());

		UserTestUtil.setUser(TestPropsValues.getUser());

		_assertGroupMembershipWithoutUserGroupRoles(
			cmpProjectObjectEntry, originalProjectManagerUser);
		_assertGroupMembershipWithoutUserGroupRoles(
			cmpProjectObjectEntry, originalProjectSponsorUser);
		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MANAGER),
			cmpProjectObjectEntry.getGroupId(),
			newProjectManagerUser.getUserId());
		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MEMBER),
			cmpProjectObjectEntry.getGroupId(),
			newProjectSponsorUser.getUserId());
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

	private void _testOnAfterCreateWithCMSAdministratorUser() throws Exception {
		User cmsAdministratorUser = UserTestUtil.addCompanyUser(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
			RoleConstants.CMS_ADMINISTRATOR);

		User projectManagerUser = UserTestUtil.addUser();
		User projectSponsorUser = UserTestUtil.addUser();

		UserTestUtil.setUser(cmsAdministratorUser);

		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"r_userToCMPProjectManager_userId",
					projectManagerUser.getUserId()
				).put(
					"r_userToCMPProjectSponsor_userId",
					projectSponsorUser.getUserId()
				).build());

		UserTestUtil.setUser(TestPropsValues.getUser());

		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MANAGER),
			cmpProjectObjectEntry.getGroupId(), projectManagerUser.getUserId());
		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MEMBER),
			cmpProjectObjectEntry.getGroupId(), projectSponsorUser.getUserId());
	}

	private void
			_testOnAfterUpdateWhenProjectManagerAndProjectSponsorAreUnchanged(
				ObjectEntry cmpProjectObjectEntry)
		throws Exception {

		// Updating the description as an administrator keeps both roles

		User projectManagerUser = UserTestUtil.addUser(
			cmpProjectObjectEntry.getGroupId());
		User projectSponsorUser = UserTestUtil.addUser(
			cmpProjectObjectEntry.getGroupId());

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, projectManagerUser.getUserId(),
			projectSponsorUser.getUserId(), TestPropsValues.getUserId());

		String description = RandomTestUtil.randomString();

		cmpProjectObjectEntry = _updateDescription(
			cmpProjectObjectEntry, description, TestPropsValues.getUserId());

		Assert.assertEquals(
			description,
			MapUtil.getString(
				cmpProjectObjectEntry.getValues(), "description"));

		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MANAGER),
			cmpProjectObjectEntry.getGroupId(), projectManagerUser.getUserId());
		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MEMBER),
			cmpProjectObjectEntry.getGroupId(), projectSponsorUser.getUserId());

		// Updating the description without assign members keeps both roles

		User user = UserTestUtil.addUser();

		UserTestUtil.setUser(user);

		description = RandomTestUtil.randomString();

		cmpProjectObjectEntry = _updateDescription(
			cmpProjectObjectEntry, description, user.getUserId());

		Assert.assertEquals(
			description,
			MapUtil.getString(
				cmpProjectObjectEntry.getValues(), "description"));

		UserTestUtil.setUser(TestPropsValues.getUser());

		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MANAGER),
			cmpProjectObjectEntry.getGroupId(), projectManagerUser.getUserId());
		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MEMBER),
			cmpProjectObjectEntry.getGroupId(), projectSponsorUser.getUserId());
	}

	private void _testOnAfterUpdateWhenProjectManagerAndProjectSponsorChange(
			ObjectEntry cmpProjectObjectEntry)
		throws Exception {

		// A project manager may clear their own name

		User projectManagerUser = UserTestUtil.addUser(
			cmpProjectObjectEntry.getGroupId());

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, projectManagerUser.getUserId(), 0,
			TestPropsValues.getUserId());

		UserTestUtil.setUser(projectManagerUser);

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, 0, 0, projectManagerUser.getUserId());

		UserTestUtil.setUser(TestPropsValues.getUser());

		_assertGroupMembershipWithoutUserGroupRoles(
			cmpProjectObjectEntry, projectManagerUser);

		// Clearing a holder with another project role revokes only that role

		User projectContributorUser = UserTestUtil.addGroupUser(
			_groupLocalService.getGroup(cmpProjectObjectEntry.getGroupId()),
			DepotRolesConstants.PROJECT_CONTRIBUTOR);

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, projectContributorUser.getUserId(), 0,
			TestPropsValues.getUserId());

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, 0, 0, TestPropsValues.getUserId());

		_assertUserGroupRoles(
			1,
			Collections.singletonList(DepotRolesConstants.PROJECT_CONTRIBUTOR),
			cmpProjectObjectEntry.getGroupId(),
			projectContributorUser.getUserId());

		Assert.assertTrue(
			_groupLocalService.hasUserGroup(
				projectContributorUser.getUserId(),
				cmpProjectObjectEntry.getGroupId()));

		// Clearing the project manager and project sponsor revokes both holders

		User projectSponsorUser = UserTestUtil.addUser(
			cmpProjectObjectEntry.getGroupId());

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, projectManagerUser.getUserId(),
			projectSponsorUser.getUserId(), TestPropsValues.getUserId());

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, 0, 0, TestPropsValues.getUserId());

		_assertGroupMembershipWithoutUserGroupRoles(
			cmpProjectObjectEntry, projectManagerUser);
		_assertGroupMembershipWithoutUserGroupRoles(
			cmpProjectObjectEntry, projectSponsorUser);

		// Reassigning as the outgoing project manager keeps authority on save

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, projectManagerUser.getUserId(),
			projectSponsorUser.getUserId(), TestPropsValues.getUserId());

		_assertReassignment(
			cmpProjectObjectEntry, projectManagerUser, projectSponsorUser,
			projectManagerUser);

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, 0, 0, TestPropsValues.getUserId());

		// Reassigning both roles revokes the previous holders

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, projectManagerUser.getUserId(),
			projectSponsorUser.getUserId(), TestPropsValues.getUserId());

		_assertReassignment(
			cmpProjectObjectEntry, projectManagerUser, projectSponsorUser,
			TestPropsValues.getUser());
	}

	private void _testOnAfterUpdateWhenProjectManagerAssignsAndRevokesUser(
			ObjectEntry cmpProjectObjectEntry)
		throws Exception {

		// A project manager assigning an administrator is refused

		User projectManagerUser = UserTestUtil.addUser(
			cmpProjectObjectEntry.getGroupId());

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, projectManagerUser.getUserId(), 0,
			TestPropsValues.getUserId());

		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MANAGER),
			cmpProjectObjectEntry.getGroupId(), projectManagerUser.getUserId());

		User companyAdminUser = UserTestUtil.addCompanyAdminUser(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		UserTestUtil.setUser(projectManagerUser);

		try {
			_updateProjectManagerProjectSponsor(
				cmpProjectObjectEntry, projectManagerUser.getUserId(),
				companyAdminUser.getUserId(), projectManagerUser.getUserId());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			Assert.assertTrue(
				modelListenerException.getCause() instanceof
					PrincipalException.MustHavePermission);
		}

		// A project manager may assign and revoke an ordinary user

		User user = UserTestUtil.addUser();

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, projectManagerUser.getUserId(),
			user.getUserId(), projectManagerUser.getUserId());

		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MEMBER),
			cmpProjectObjectEntry.getGroupId(), user.getUserId());

		Assert.assertTrue(
			_groupLocalService.hasUserGroup(
				user.getUserId(), cmpProjectObjectEntry.getGroupId()));

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, projectManagerUser.getUserId(), 0,
			projectManagerUser.getUserId());

		UserTestUtil.setUser(TestPropsValues.getUser());

		_assertGroupMembershipWithoutUserGroupRoles(
			cmpProjectObjectEntry, user);

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, projectManagerUser.getUserId(),
			companyAdminUser.getUserId(), TestPropsValues.getUserId());

		// Revoking an administrator as a project manager is refused

		UserTestUtil.setUser(projectManagerUser);

		try {
			_updateProjectManagerProjectSponsor(
				cmpProjectObjectEntry, projectManagerUser.getUserId(), 0,
				projectManagerUser.getUserId());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			Assert.assertTrue(
				modelListenerException.getCause() instanceof
					PrincipalException.MustHavePermission);
		}

		UserTestUtil.setUser(TestPropsValues.getUser());

		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MEMBER),
			cmpProjectObjectEntry.getGroupId(), companyAdminUser.getUserId());

		_updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, 0, 0, TestPropsValues.getUserId());
	}

	private void
			_testOnAfterUpdateWhenProjectManagerIsClearedWithoutAssignMembersPermission(
				ObjectEntry cmpProjectObjectEntry)
		throws Exception {

		User projectManagerUser = UserTestUtil.addUser(
			cmpProjectObjectEntry.getGroupId());

		cmpProjectObjectEntry = _updateProjectManagerProjectSponsor(
			cmpProjectObjectEntry, projectManagerUser.getUserId(), 0,
			TestPropsValues.getUserId());

		User user = UserTestUtil.addUser();

		UserTestUtil.setUser(user);

		try {
			_updateProjectManagerProjectSponsor(
				cmpProjectObjectEntry, 0, 0, user.getUserId());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			Assert.assertTrue(
				modelListenerException.getCause() instanceof
					PrincipalException.MustHavePermission);
		}

		UserTestUtil.setUser(TestPropsValues.getUser());

		_assertUserGroupRoles(
			1, Collections.singletonList(DepotRolesConstants.PROJECT_MANAGER),
			cmpProjectObjectEntry.getGroupId(), projectManagerUser.getUserId());

		Assert.assertTrue(
			_groupLocalService.hasUserGroup(
				projectManagerUser.getUserId(),
				cmpProjectObjectEntry.getGroupId()));
	}

	private ObjectEntry _updateDescription(
			ObjectEntry cmpProjectObjectEntry, String description, long userId)
		throws Exception {

		return _objectEntryLocalService.partialUpdateObjectEntry(
			userId, cmpProjectObjectEntry.getObjectEntryId(),
			cmpProjectObjectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"description", description
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _updateProjectManagerProjectSponsor(
			ObjectEntry cmpProjectObjectEntry, long projectManagerUserId,
			long projectSponsorUserId, long userId)
		throws Exception {

		return _objectEntryLocalService.partialUpdateObjectEntry(
			userId, cmpProjectObjectEntry.getObjectEntryId(),
			cmpProjectObjectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"r_userToCMPProjectManager_userId", projectManagerUserId
			).put(
				"r_userToCMPProjectSponsor_userId", projectSponsorUserId
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

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

	@Inject
	private StagingLocalService _stagingLocalService;

}