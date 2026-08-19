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
import com.liferay.headless.cmp.client.dto.v1_0.TaskAssignee;
import com.liferay.headless.cmp.client.pagination.Page;
import com.liferay.headless.cmp.client.resource.v1_0.TaskAssigneeResource;
import com.liferay.headless.cmp.resource.v1_0.test.util.CMPLicenseTestUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class TaskAssigneeResourceTest extends BaseTaskAssigneeResourceTestCase {

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

		CMPTestUtil.getOrAddGroup(TaskAssigneeResourceTest.class);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
	}

	@Override
	@Test
	public void testGetProjectTaskAssigneesPage() throws Exception {
		ObjectEntry objectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		User user1 = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[] {objectEntry.getGroupId()},
			ServiceContextTestUtil.getServiceContext());

		User user2 = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[] {_depotEntry.getGroupId()},
			ServiceContextTestUtil.getServiceContext());

		Role role = _roleLocalService.addRole(
			null, TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null, RoleConstants.TYPE_DEPOT,
			DepotRolesConstants.SUBTYPE_PROJECT, null);

		Page<TaskAssignee> page =
			taskAssigneeResource.getProjectTaskAssigneesPage(
				objectEntry.getObjectEntryId(), role.getName(), null);

		assertEquals(
			new TaskAssignee() {
				{
					setExternalReferenceCode(role::getExternalReferenceCode);
					setName(role::getName);
					setType(() -> "Role");
				}
			},
			page.fetchFirstItem());

		page = taskAssigneeResource.getProjectTaskAssigneesPage(
			objectEntry.getObjectEntryId(), user1.getLastName(), null);

		assertEquals(
			new TaskAssignee() {
				{
					setExternalReferenceCode(user1::getExternalReferenceCode);
					setName(user1::getFullName);
					setType(() -> "User");
				}
			},
			page.fetchFirstItem());

		page = taskAssigneeResource.getProjectTaskAssigneesPage(
			objectEntry.getObjectEntryId(), user2.getLastName(), null);

		Assert.assertNull(page.fetchFirstItem());

		_assertTaskAssigneeType(
			"Role",
			taskAssigneeResource.getProjectTaskAssigneesPage(
				objectEntry.getObjectEntryId(), null, "Role"));
		_assertTaskAssigneeType(
			"User",
			taskAssigneeResource.getProjectTaskAssigneesPage(
				objectEntry.getObjectEntryId(), null, "User"));

		_testGetProjectTaskAssigneesPageWithAppDisabled(objectEntry);
	}

	@Override
	@Test
	public void testGetTaskAssigneesPage() throws Exception {
		Role role = _roleLocalService.addRole(
			null, TestPropsValues.getUserId(), null, 0, "Custom Project Role",
			null, null, RoleConstants.TYPE_DEPOT,
			DepotRolesConstants.SUBTYPE_PROJECT, null);

		User user = _addUser(_depotEntry.getGroupId(), "John", "Doe");

		Page<TaskAssignee> page = taskAssigneeResource.getTaskAssigneesPage(
			"Custom", null);

		assertEquals(
			new TaskAssignee() {
				{
					setExternalReferenceCode(role::getExternalReferenceCode);
					setName(role::getName);
					setType(() -> "Role");
				}
			},
			page.fetchFirstItem());

		page = taskAssigneeResource.getTaskAssigneesPage("Doe", null);

		assertEquals(
			new TaskAssignee() {
				{
					setExternalReferenceCode(user::getExternalReferenceCode);
					setName(user::getFullName);
					setType(() -> "User");
				}
			},
			page.fetchFirstItem());

		page = taskAssigneeResource.getTaskAssigneesPage("John", null);

		assertEquals(
			new TaskAssignee() {
				{
					setExternalReferenceCode(user::getExternalReferenceCode);
					setName(user::getFullName);
					setType(() -> "User");
				}
			},
			page.fetchFirstItem());

		_assertTaskAssigneeType(
			"Role", taskAssigneeResource.getTaskAssigneesPage(null, "Role"));
		_assertTaskAssigneeType(
			"User", taskAssigneeResource.getTaskAssigneesPage(null, "User"));

		_testGetTaskAssigneesPageWithAppDisabled();
		_testGetTaskAssigneesPageWithAppExpired();
		_testGetTaskAssigneesPageWithSpaceAdministrator();
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"externalReferenceCode", "name", "type"};
	}

	private User _addUser(long groupId, String firstName, String lastName)
		throws Exception {

		return UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), LocaleUtil.getDefault(), firstName,
			lastName, new long[] {groupId},
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertTaskAssigneeType(
		String expectedType, Page<TaskAssignee> page) {

		for (TaskAssignee taskAssignee : page.getItems()) {
			Assert.assertEquals(expectedType, taskAssignee.getType());
		}
	}

	private long[] _getTaskAssigneeIds(Page<TaskAssignee> page) {
		return TransformUtil.transformToLongArray(
			page.getItems(),
			taskAssignee -> GetterUtil.getLong(taskAssignee.getId()));
	}

	private void _testGetProjectTaskAssigneesPageWithAppDisabled(
			ObjectEntry objectEntry)
		throws Exception {

		try (AutoCloseable autoCloseable =
				CMPLicenseTestUtil.withAppDisabled()) {

			assertHttpResponseStatusCode(
				400,
				taskAssigneeResource.getProjectTaskAssigneesPageHttpResponse(
					objectEntry.getObjectEntryId(), null, null));
		}

		assertHttpResponseStatusCode(
			200,
			taskAssigneeResource.getProjectTaskAssigneesPageHttpResponse(
				objectEntry.getObjectEntryId(), null, null));
	}

	private void _testGetTaskAssigneesPageWithAppDisabled() throws Exception {
		try (AutoCloseable autoCloseable =
				CMPLicenseTestUtil.withAppDisabled()) {

			assertHttpResponseStatusCode(
				400,
				taskAssigneeResource.getTaskAssigneesPageHttpResponse(
					null, null));
		}

		assertHttpResponseStatusCode(
			200,
			taskAssigneeResource.getTaskAssigneesPageHttpResponse(null, null));
	}

	private void _testGetTaskAssigneesPageWithAppExpired() throws Exception {
		try (AutoCloseable autoCloseable =
				CMPLicenseTestUtil.withAppExpired()) {

			assertHttpResponseStatusCode(
				400,
				taskAssigneeResource.getTaskAssigneesPageHttpResponse(
					null, null));
		}

		assertHttpResponseStatusCode(
			200,
			taskAssigneeResource.getTaskAssigneesPageHttpResponse(null, null));
	}

	private void _testGetTaskAssigneesPageWithSpaceAdministrator()
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		long groupId = depotEntry.getGroupId();

		String lastName = RandomTestUtil.randomString();

		User administratorUser = _addUser(
			groupId, RandomTestUtil.randomString(), lastName);

		Role administratorRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.ADMINISTRATOR);

		_roleLocalService.addUserRoles(
			administratorUser.getUserId(),
			new long[] {administratorRole.getRoleId()});

		User assignableUser = _addUser(
			groupId, RandomTestUtil.randomString(), lastName);

		User spaceAdministratorUser = UserTestUtil.addUser(
			testCompany, PropsValues.DEFAULT_ADMIN_PASSWORD);

		_userLocalService.updateEmailAddressVerified(
			spaceAdministratorUser.getUserId(), true);

		_userLocalService.addGroupUsers(
			groupId, new long[] {spaceAdministratorUser.getUserId()});

		Role assetLibraryAdministratorRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);

		_userGroupRoleLocalService.addUserGroupRoles(
			spaceAdministratorUser.getUserId(), groupId,
			new long[] {assetLibraryAdministratorRole.getRoleId()});

		TaskAssigneeResource spaceAdministratorTaskAssigneeResource =
			TaskAssigneeResource.builder(
			).authentication(
				spaceAdministratorUser.getEmailAddress(),
				PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		long[] taskAssigneeIds = _getTaskAssigneeIds(
			spaceAdministratorTaskAssigneeResource.getTaskAssigneesPage(
				lastName, "User"));

		Assert.assertFalse(
			ArrayUtil.contains(taskAssigneeIds, administratorUser.getUserId()));
		Assert.assertTrue(
			ArrayUtil.contains(taskAssigneeIds, assignableUser.getUserId()));

		Assert.assertTrue(
			ArrayUtil.contains(
				_getTaskAssigneeIds(
					spaceAdministratorTaskAssigneeResource.getTaskAssigneesPage(
						lastName, null)),
				administratorUser.getUserId()));

		Assert.assertTrue(
			ArrayUtil.contains(
				_getTaskAssigneeIds(
					taskAssigneeResource.getTaskAssigneesPage(
						lastName, "User")),
				administratorUser.getUserId()));
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}