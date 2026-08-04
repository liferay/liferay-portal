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
import com.liferay.headless.cmp.client.dto.v1_0.UserAccount;
import com.liferay.headless.cmp.client.pagination.Page;
import com.liferay.headless.cmp.client.pagination.Pagination;
import com.liferay.headless.cmp.client.problem.Problem;
import com.liferay.headless.cmp.client.resource.v1_0.UserAccountResource;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
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
 * @author Pedro Leite
 */
@RunWith(Arquillian.class)
public class UserAccountResourceTest extends BaseUserAccountResourceTestCase {

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

		CMPTestUtil.getOrAddGroup(UserAccountResourceTest.class);

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

	@Override
	@Test
	public void testGetProjectUserAccountsPage() throws Exception {
		super.testGetProjectUserAccountsPage();

		_testGetProjectUserAccountsPageWithProjectManager();
		_testGetProjectUserAccountsPageWithProjectMember();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"emailAddress", "externalReferenceCode", "name"};
	}

	@Override
	protected UserAccount testGetProjectUserAccountsPage_addUserAccount(
			Long projectId, UserAccount userAccount)
		throws Exception {

		return _toUserAccount(_addUser());
	}

	@Override
	protected Long testGetProjectUserAccountsPage_getProjectId()
		throws Exception {

		ObjectEntry objectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		return objectEntry.getObjectEntryId();
	}

	private User _addUser() throws Exception {
		User user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(
			_depotEntry.getGroupId(), user.getUserId());

		return user;
	}

	private UserAccountResource _getUserAccountResource(
			ObjectEntry objectEntry, String roleName)
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[] {_depotEntry.getGroupId()},
			ServiceContextTestUtil.getServiceContext());

		Role role = _roleLocalService.getRole(
			testCompany.getCompanyId(), roleName);

		_userGroupRoleLocalService.addUserGroupRoles(
			user.getUserId(), objectEntry.getGroupId(),
			new long[] {role.getRoleId()});

		return UserAccountResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private void _testGetProjectUserAccountsPageWithProjectManager()
		throws Exception {

		ObjectEntry objectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		UserAccountResource userAccountResource = _getUserAccountResource(
			objectEntry, DepotRolesConstants.PROJECT_MANAGER);

		Page<UserAccount> page = userAccountResource.getProjectUserAccountsPage(
			objectEntry.getObjectEntryId(), null, Pagination.of(1, 20));

		long totalCount = page.getTotalCount();

		User user = _addUser();

		page = userAccountResource.getProjectUserAccountsPage(
			objectEntry.getObjectEntryId(), null, Pagination.of(1, 20));

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		assertContains(
			_toUserAccount(user), (List<UserAccount>)page.getItems());
	}

	private void _testGetProjectUserAccountsPageWithProjectMember()
		throws Exception {

		ObjectEntry objectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		UserAccountResource userAccountResource = _getUserAccountResource(
			objectEntry, DepotRolesConstants.PROJECT_MEMBER);

		AssertUtils.assertFailure(
			Problem.ProblemException.class, null,
			() -> userAccountResource.getProjectUserAccountsPage(
				objectEntry.getObjectEntryId(), null, Pagination.of(1, 20)));
	}

	private UserAccount _toUserAccount(User user) {
		return new UserAccount() {
			{
				emailAddress = user.getEmailAddress();
				externalReferenceCode = user.getExternalReferenceCode();
				id = user.getUserId();
				name = user.getFullName();
			}
		};
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