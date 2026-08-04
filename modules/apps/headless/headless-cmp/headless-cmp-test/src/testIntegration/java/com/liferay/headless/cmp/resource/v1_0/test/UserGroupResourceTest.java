/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.cmp.client.dto.v1_0.UserGroup;
import com.liferay.headless.cmp.client.pagination.Page;
import com.liferay.headless.cmp.client.pagination.Pagination;
import com.liferay.headless.cmp.client.problem.Problem;
import com.liferay.headless.cmp.client.resource.v1_0.UserGroupResource;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Larissa Ribeiro
 */
@RunWith(Arquillian.class)
public class UserGroupResourceTest extends BaseUserGroupResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		CMPTestUtil.getOrAddGroup(UserGroupResourceTest.class);

		_depotEntry1 = _addDepotEntry();
		_depotEntry2 = _addDepotEntry();
	}

	@Override
	@Test
	public void testGetProjectUserGroupsPage() throws Exception {
		super.testGetProjectUserGroupsPage();

		_testGetProjectUserGroupsPageWithMultipleDepotEntries();
		_testGetProjectUserGroupsPageWithProjectManager();
		_testGetProjectUserGroupsPageWithProjectMember();
		_testGetProjectUserGroupsPageWithoutDepotEntry();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"externalReferenceCode", "name"};
	}

	@Override
	protected UserGroup testGetProjectUserGroupsPage_addUserGroup(
			Long projectId, UserGroup userGroup)
		throws Exception {

		return _toUserGroup(_addUserGroup(_depotEntry1));
	}

	@Override
	protected Long testGetProjectUserGroupsPage_getProjectId()
		throws Exception {

		ObjectEntry objectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		return objectEntry.getObjectEntryId();
	}

	private DepotEntry _addDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
	}

	private com.liferay.portal.kernel.model.UserGroup _addUserGroup(
			DepotEntry depotEntry)
		throws Exception {

		com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup =
			UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addGroupUserGroups(
			depotEntry.getGroupId(),
			new long[] {serviceBuilderUserGroup.getUserGroupId()});

		return serviceBuilderUserGroup;
	}

	private UserGroupResource _getUserGroupResource(
			ObjectEntry objectEntry, String roleName)
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[] {_depotEntry1.getGroupId()},
			ServiceContextTestUtil.getServiceContext());

		Role role = _roleLocalService.getRole(
			testCompany.getCompanyId(), roleName);

		_userGroupRoleLocalService.addUserGroupRoles(
			user.getUserId(), objectEntry.getGroupId(),
			new long[] {role.getRoleId()});

		return UserGroupResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private void _testGetProjectUserGroupsPageWithMultipleDepotEntries()
		throws Exception {

		ObjectEntry objectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup1 =
			_addUserGroup(_depotEntry1);
		com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup2 =
			_addUserGroup(_depotEntry2);

		Page<UserGroup> page = userGroupResource.getProjectUserGroupsPage(
			objectEntry.getObjectEntryId(), null, Pagination.of(1, 100));

		assertContains(
			_toUserGroup(serviceBuilderUserGroup1),
			(List<UserGroup>)page.getItems());
		assertContains(
			_toUserGroup(serviceBuilderUserGroup2),
			(List<UserGroup>)page.getItems());
	}

	private void _testGetProjectUserGroupsPageWithoutDepotEntry()
		throws Exception {

		ObjectEntry objectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup =
			UserGroupTestUtil.addUserGroup();

		Page<UserGroup> page = userGroupResource.getProjectUserGroupsPage(
			objectEntry.getObjectEntryId(), null, Pagination.of(1, 100));

		for (UserGroup userGroup : page.getItems()) {
			Assert.assertNotEquals(
				serviceBuilderUserGroup.getName(), userGroup.getName());
		}
	}

	private void _testGetProjectUserGroupsPageWithProjectManager()
		throws Exception {

		ObjectEntry objectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		UserGroupResource userGroupResource = _getUserGroupResource(
			objectEntry, DepotRolesConstants.PROJECT_MANAGER);

		Page<UserGroup> page = userGroupResource.getProjectUserGroupsPage(
			objectEntry.getObjectEntryId(), null, Pagination.of(1, 100));

		long totalCount = page.getTotalCount();

		com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup =
			_addUserGroup(_depotEntry1);

		page = userGroupResource.getProjectUserGroupsPage(
			objectEntry.getObjectEntryId(), null, Pagination.of(1, 100));

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		assertContains(
			_toUserGroup(serviceBuilderUserGroup),
			(List<UserGroup>)page.getItems());
	}

	private void _testGetProjectUserGroupsPageWithProjectMember()
		throws Exception {

		ObjectEntry objectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		UserGroupResource userGroupResource = _getUserGroupResource(
			objectEntry, DepotRolesConstants.PROJECT_MEMBER);

		AssertUtils.assertFailure(
			Problem.ProblemException.class, null,
			() -> userGroupResource.getProjectUserGroupsPage(
				objectEntry.getObjectEntryId(), null, Pagination.of(1, 100)));
	}

	private UserGroup _toUserGroup(
		com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup) {

		return new UserGroup() {
			{
				externalReferenceCode =
					serviceBuilderUserGroup.getExternalReferenceCode();
				id = serviceBuilderUserGroup.getUserGroupId();
				name = serviceBuilderUserGroup.getName();
			}
		};
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry1;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry2;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}