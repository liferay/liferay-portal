/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.resource.v1_0.test;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountMember;
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountRole;
import com.liferay.headless.commerce.admin.account.client.problem.Problem;
import com.liferay.headless.commerce.admin.account.client.resource.v1_0.AccountMemberResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class AccountMemberResourceTest
	extends BaseAccountMemberResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _serviceContext.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_GUEST,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	@Override
	@Test
	public void testDeleteAccountByExternalReferenceCodeAccountMember()
		throws Exception {

		AccountMember accountMember = randomAccountMember();

		accountMemberResource.postAccountByExternalReferenceCodeAccountMember(
			_accountEntry.getExternalReferenceCode(), accountMember);

		assertHttpResponseStatusCode(
			204,
			accountMemberResource.
				deleteAccountByExternalReferenceCodeAccountMemberHttpResponse(
					_accountEntry.getExternalReferenceCode(),
					accountMember.getUserId()));

		assertHttpResponseStatusCode(
			404,
			accountMemberResource.
				getAccountByExternalReferenceCodeAccountMemberHttpResponse(
					_accountEntry.getExternalReferenceCode(),
					accountMember.getUserId()));
	}

	@Override
	@Test
	public void testDeleteAccountIdAccountMember() throws Exception {
		AccountMember accountMember = randomAccountMember();

		accountMemberResource.postAccountIdAccountMember(
			_accountEntry.getAccountEntryId(), accountMember);

		assertHttpResponseStatusCode(
			204,
			accountMemberResource.deleteAccountIdAccountMemberHttpResponse(
				_accountEntry.getAccountEntryId(), accountMember.getUserId()));

		assertHttpResponseStatusCode(
			404,
			accountMemberResource.getAccountIdAccountMemberHttpResponse(
				_accountEntry.getAccountEntryId(), accountMember.getUserId()));
	}

	@Override
	@Test
	public void testGetAccountByExternalReferenceCodeAccountMember()
		throws Exception {

		AccountMember accountMember1 = randomAccountMember();

		accountMemberResource.postAccountByExternalReferenceCodeAccountMember(
			_accountEntry.getExternalReferenceCode(), accountMember1);

		accountMember1.setAccountId(_accountEntry.getAccountEntryId());

		AccountMember accountMember2 =
			accountMemberResource.
				getAccountByExternalReferenceCodeAccountMember(
					_accountEntry.getExternalReferenceCode(),
					accountMember1.getUserId());

		assertEquals(accountMember1, accountMember2);
	}

	@Override
	@Test
	public void testGetAccountIdAccountMember() throws Exception {
		AccountMember accountMember1 = randomAccountMember();

		accountMemberResource.postAccountIdAccountMember(
			_accountEntry.getAccountEntryId(), accountMember1);

		accountMember1.setAccountId(_accountEntry.getAccountEntryId());

		AccountMember accountMember2 =
			accountMemberResource.getAccountIdAccountMember(
				_accountEntry.getAccountEntryId(), accountMember1.getUserId());

		assertEquals(accountMember1, accountMember2);
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountByExternalReferenceCodeAccountMember()
		throws Exception {

		super.testGraphQLDeleteAccountByExternalReferenceCodeAccountMember();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountIdAccountMember() throws Exception {
		super.testGraphQLDeleteAccountIdAccountMember();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetAccountByExternalReferenceCodeAccountMember()
		throws Exception {

		super.testGraphQLGetAccountByExternalReferenceCodeAccountMember();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetAccountIdAccountMember() throws Exception {
		super.testGraphQLGetAccountIdAccountMember();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetAccountIdAccountMemberNotFound()
		throws Exception {

		super.testGraphQLGetAccountIdAccountMemberNotFound();
	}

	@Override
	@Test
	public void testPatchAccountByExternalReferenceCodeAccountMember()
		throws Exception {

		_testPatchAccountByExternalReferenceCodeAccountMember();
		_testPatchAccountByExternalReferenceCodeAccountMemberWithPermission();
	}

	@Override
	@Test
	public void testPatchAccountIdAccountMember() throws Exception {
		_testPatchAccountIdAccountMember();
		_testPatchAccountIdAccountMemberWithPermission();
	}

	@Override
	@Test
	public void testPostAccountByExternalReferenceCodeAccountMember()
		throws Exception {

		super.testPostAccountByExternalReferenceCodeAccountMember();

		AccountMember accountMember1 = _randomAccountMember();

		accountMember1 =
			accountMemberResource.
				postAccountByExternalReferenceCodeAccountMember(
					_accountEntry.getExternalReferenceCode(), accountMember1);

		accountMember1.setAccountId(_accountEntry.getAccountEntryId());

		AccountMember accountMember2 =
			accountMemberResource.
				getAccountByExternalReferenceCodeAccountMember(
					_accountEntry.getExternalReferenceCode(),
					accountMember1.getUserId());

		assertEquals(accountMember1, accountMember2);
	}

	@Override
	@Test
	public void testPostAccountIdAccountMember() throws Exception {
		super.testPostAccountIdAccountMember();

		AccountMember accountMember1 = _randomAccountMember();

		accountMember1 = accountMemberResource.postAccountIdAccountMember(
			_accountEntry.getAccountEntryId(), accountMember1);

		accountMember1.setAccountId(_accountEntry.getAccountEntryId());

		AccountMember accountMember2 =
			accountMemberResource.getAccountIdAccountMember(
				_accountEntry.getAccountEntryId(), accountMember1.getUserId());

		assertEquals(accountMember1, accountMember2);

		_assertAccountRole(
			accountMember -> accountMemberResource.postAccountIdAccountMember(
				_accountEntry.getAccountEntryId(), accountMember));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"accountId", "userId", "email", "name"};
	}

	@Override
	protected AccountMember randomAccountMember() throws Exception {
		User user = UserTestUtil.addUser(testCompany);

		return new AccountMember() {
			{
				email = user.getEmailAddress();
				name = user.getFullName();
				userId = user.getUserId();
			}
		};
	}

	@Override
	protected AccountMember
			testGetAccountByExternalReferenceCodeAccountMembersPage_addAccountMember(
				String externalReferenceCode, AccountMember accountMember)
		throws Exception {

		return accountMemberResource.
			postAccountByExternalReferenceCodeAccountMember(
				_accountEntry.getExternalReferenceCode(), accountMember);
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeAccountMembersPage_getExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected AccountMember testGetAccountIdAccountMembersPage_addAccountMember(
			Long id, AccountMember accountMember)
		throws Exception {

		return accountMemberResource.postAccountIdAccountMember(
			id, accountMember);
	}

	@Override
	protected Long testGetAccountIdAccountMembersPage_getId() throws Exception {
		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected AccountMember testGraphQLAccountMember_addAccountMember()
		throws Exception {

		return accountMemberResource.
			postAccountByExternalReferenceCodeAccountMember(
				_accountEntry.getExternalReferenceCode(),
				randomAccountMember());
	}

	@Override
	protected String
			testGraphQLDeleteAccountByExternalReferenceCodeAccountMember_getExternalReferenceCode(
				AccountMember accountMember)
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected Long testGraphQLDeleteAccountIdAccountMember_getId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected AccountMember
			testPostAccountByExternalReferenceCodeAccountMember_addAccountMember(
				AccountMember accountMember)
		throws Exception {

		accountMemberResource.postAccountByExternalReferenceCodeAccountMember(
			_accountEntry.getExternalReferenceCode(), accountMember);

		accountMember.setAccountId(_accountEntry.getAccountEntryId());

		return accountMemberResource.
			getAccountByExternalReferenceCodeAccountMember(
				_accountEntry.getExternalReferenceCode(),
				accountMember.getUserId());
	}

	@Override
	protected AccountMember testPostAccountIdAccountMember_addAccountMember(
			AccountMember accountMember)
		throws Exception {

		accountMemberResource.postAccountIdAccountMember(
			_accountEntry.getAccountEntryId(), accountMember);

		accountMember.setAccountId(_accountEntry.getAccountEntryId());

		return accountMemberResource.getAccountIdAccountMember(
			_accountEntry.getAccountEntryId(), accountMember.getUserId());
	}

	private void _addRoleUsers(
			String className, long primKey, User user, String... actionIds)
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(), className,
			ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(primKey),
			role.getRoleId(), actionIds);

		_userLocalService.addRoleUsers(
			role.getRoleId(), new long[] {user.getUserId()});
	}

	private User _addUser() throws Exception {
		User user = UserTestUtil.addUser(testCompany);

		_userLocalService.updatePassword(
			user.getUserId(), _PASSWORD, _PASSWORD, false, true);

		return user;
	}

	private void _assertAccountRole(
			UnsafeConsumer<AccountMember, Exception> unsafeConsumer)
		throws Exception {

		User user = UserTestUtil.addUser(testCompany);

		AccountMember accountMember = new AccountMember() {
			{
				email = user.getEmailAddress();
				name = user.getFullName();
				userId = user.getUserId();
			}
		};

		com.liferay.account.model.AccountRole serviceBuilderAccountRole1 =
			_accountRoleLocalService.addAccountRole(
				null, _serviceContext.getUserId(),
				_accountEntry.getAccountEntryId(),
				StringUtil.toLowerCase(RandomTestUtil.randomString()), null,
				null);

		accountMember.setAccountRoles(
			new AccountRole[] {_toAccountRole(serviceBuilderAccountRole1)});

		unsafeConsumer.accept(accountMember);

		Assert.assertNotNull(
			_userGroupRoleLocalService.fetchUserGroupRole(
				user.getUserId(), _accountEntry.getAccountEntryGroupId(),
				serviceBuilderAccountRole1.getRoleId()));

		com.liferay.account.model.AccountRole serviceBuilderAccountRole2 =
			_accountRoleLocalService.addAccountRole(
				null, _serviceContext.getUserId(),
				AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
				StringUtil.toLowerCase(RandomTestUtil.randomString()), null,
				null);

		accountMember.setAccountRoles(
			new AccountRole[] {
				_toAccountRole(serviceBuilderAccountRole1),
				_toAccountRole(serviceBuilderAccountRole2)
			});

		unsafeConsumer.accept(accountMember);

		Assert.assertNotNull(
			_userGroupRoleLocalService.fetchUserGroupRole(
				user.getUserId(), _accountEntry.getAccountEntryGroupId(),
				serviceBuilderAccountRole2.getRoleId()));

		Role role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

		accountMember.setAccountRoles(
			new AccountRole[] {
				new AccountRole() {
					{
						name = role.getName();
						roleId = role.getRoleId();
					}
				}
			});

		try {
			unsafeConsumer.accept(accountMember);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}

		Assert.assertNotNull(
			_userGroupRoleLocalService.fetchUserGroupRole(
				user.getUserId(), _accountEntry.getAccountEntryGroupId(),
				serviceBuilderAccountRole1.getRoleId()));

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _serviceContext.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_GUEST,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);

		accountMember.setAccountRoles(
			new AccountRole[] {
				_toAccountRole(
					_accountRoleLocalService.addAccountRole(
						null, _serviceContext.getUserId(),
						accountEntry.getAccountEntryId(),
						StringUtil.toLowerCase(RandomTestUtil.randomString()),
						null, null))
			});

		AssertUtils.assertFailure(
			Problem.ProblemException.class, "The account role is invalid.",
			() -> unsafeConsumer.accept(accountMember));

		Assert.assertNotNull(
			_userGroupRoleLocalService.fetchUserGroupRole(
				user.getUserId(), _accountEntry.getAccountEntryGroupId(),
				serviceBuilderAccountRole1.getRoleId()));
	}

	private void _assertProblemException(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private AccountMember _getAccountMember(long... roleIds) {
		if (roleIds.length == 0) {
			return new AccountMember();
		}

		AccountMember accountMember = new AccountMember();

		accountMember.setAccountRoles(
			TransformUtil.transform(
				roleIds,
				roleId -> {
					AccountRole accountRole = new AccountRole();

					accountRole.setRoleId(roleId);

					return accountRole;
				},
				AccountRole.class));

		return accountMember;
	}

	private AccountMemberResource _getAccountMemberResource(
		String password, User user) {

		return AccountMemberResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private AccountMember _randomAccountMember() throws Exception {
		User user = UserTestUtil.addUser(testCompany);

		return new AccountMember() {
			{
				email = user.getEmailAddress();
				name = user.getFullName();
			}
		};
	}

	private void _testPatchAccountByExternalReferenceCodeAccountMember()
		throws Exception {

		AccountMember accountMember1 = randomAccountMember();

		accountMemberResource.postAccountByExternalReferenceCodeAccountMember(
			_accountEntry.getExternalReferenceCode(), accountMember1);

		accountMember1.setAccountId(_accountEntry.getAccountEntryId());

		accountMemberResource.patchAccountByExternalReferenceCodeAccountMember(
			_accountEntry.getExternalReferenceCode(),
			accountMember1.getUserId(), accountMember1);

		AccountMember accountMember2 =
			accountMemberResource.
				getAccountByExternalReferenceCodeAccountMember(
					_accountEntry.getExternalReferenceCode(),
					accountMember1.getUserId());

		assertEquals(accountMember1, accountMember2);
	}

	private void _testPatchAccountByExternalReferenceCodeAccountMemberWithPermission()
		throws Exception {

		User user1 = _addUser();

		_addRoleUsers(
			AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
			user1, ActionKeys.VIEW);

		AccountMemberResource accountMemberResource = _getAccountMemberResource(
			_PASSWORD, user1);

		com.liferay.account.model.AccountRole serviceBuilderAccountRole =
			_accountRoleLocalService.addAccountRole(
				null, _serviceContext.getUserId(),
				_accountEntry.getAccountEntryId(),
				StringUtil.toLowerCase(RandomTestUtil.randomString()), null,
				null);
		User user2 = UserTestUtil.addUser(testCompany);

		_assertProblemException(
			() ->
				accountMemberResource.
					patchAccountByExternalReferenceCodeAccountMember(
						_accountEntry.getExternalReferenceCode(),
						user2.getUserId(),
						_getAccountMember(
							serviceBuilderAccountRole.getRoleId())));

		Assert.assertNull(
			_userGroupRoleLocalService.fetchUserGroupRole(
				user2.getUserId(), _accountEntry.getAccountEntryGroupId(),
				serviceBuilderAccountRole.getRoleId()));

		_addRoleUsers(
			AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
			user1, AccountActionKeys.ASSIGN_USERS);

		_addRoleUsers(
			Role.class.getName(), serviceBuilderAccountRole.getRoleId(), user1,
			ActionKeys.VIEW);

		accountMemberResource.patchAccountByExternalReferenceCodeAccountMember(
			_accountEntry.getExternalReferenceCode(), user2.getUserId(),
			_getAccountMember(serviceBuilderAccountRole.getRoleId()));
	}

	private void _testPatchAccountIdAccountMember() throws Exception {
		AccountMember accountMember1 = randomAccountMember();

		accountMemberResource.postAccountIdAccountMember(
			_accountEntry.getAccountEntryId(), accountMember1);

		accountMember1.setAccountId(_accountEntry.getAccountEntryId());

		accountMemberResource.patchAccountIdAccountMember(
			_accountEntry.getAccountEntryId(), accountMember1.getUserId(),
			accountMember1);

		AccountMember accountMember2 =
			accountMemberResource.getAccountIdAccountMember(
				_accountEntry.getAccountEntryId(), accountMember1.getUserId());

		assertEquals(accountMember1, accountMember2);

		_assertAccountRole(
			accountMember -> accountMemberResource.patchAccountIdAccountMember(
				_accountEntry.getAccountEntryId(), accountMember.getUserId(),
				accountMember));
	}

	private void _testPatchAccountIdAccountMemberWithPermission()
		throws Exception {

		User user1 = _addUser();

		AccountMemberResource accountMemberResource = _getAccountMemberResource(
			_PASSWORD, user1);

		com.liferay.account.model.AccountRole serviceBuilderAccountRole =
			_accountRoleLocalService.addAccountRole(
				null, _serviceContext.getUserId(),
				_accountEntry.getAccountEntryId(),
				StringUtil.toLowerCase(RandomTestUtil.randomString()), null,
				null);
		User user2 = UserTestUtil.addUser(testCompany);

		_assertProblemException(
			() -> accountMemberResource.patchAccountIdAccountMember(
				_accountEntry.getAccountEntryId(), user2.getUserId(),
				_getAccountMember(serviceBuilderAccountRole.getRoleId())));

		Assert.assertNull(
			_userGroupRoleLocalService.fetchUserGroupRole(
				user2.getUserId(), _accountEntry.getAccountEntryGroupId(),
				serviceBuilderAccountRole.getRoleId()));

		_accountRoleLocalService.associateUser(
			_accountEntry.getAccountEntryId(),
			serviceBuilderAccountRole.getAccountRoleId(), user2.getUserId());

		Assert.assertNotNull(
			_userGroupRoleLocalService.fetchUserGroupRole(
				user2.getUserId(), _accountEntry.getAccountEntryGroupId(),
				serviceBuilderAccountRole.getRoleId()));

		_assertProblemException(
			() -> accountMemberResource.patchAccountIdAccountMember(
				_accountEntry.getAccountEntryId(), user2.getUserId(),
				_getAccountMember()));

		Assert.assertNotNull(
			_userGroupRoleLocalService.fetchUserGroupRole(
				user2.getUserId(), _accountEntry.getAccountEntryGroupId(),
				serviceBuilderAccountRole.getRoleId()));

		_addRoleUsers(
			AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
			user1, AccountActionKeys.ASSIGN_USERS);

		accountMemberResource.patchAccountIdAccountMember(
			_accountEntry.getAccountEntryId(), user2.getUserId(),
			_getAccountMember());
	}

	private AccountRole _toAccountRole(
			com.liferay.account.model.AccountRole serviceBuilderAccountRole)
		throws Exception {

		return new AccountRole() {
			{
				name = serviceBuilderAccountRole.getRoleName();
				roleId = serviceBuilderAccountRole.getRoleId();
			}
		};
	}

	private static final String _PASSWORD = RandomTestUtil.randomString();

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private ServiceContext _serviceContext;
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}