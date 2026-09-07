/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.resource.v1_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.Account;
import com.liferay.headless.commerce.admin.account.client.pagination.Page;
import com.liferay.headless.commerce.admin.account.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.account.client.problem.Problem;
import com.liferay.headless.commerce.admin.account.client.resource.v1_0.AccountResource;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class AccountResourceTest extends BaseAccountResourceTestCase {

	@Override
	@Test
	public void testDeleteAccount() throws Exception {
		Account account = testDeleteAccount_addAccount();

		assertHttpResponseStatusCode(
			204, accountResource.deleteAccountHttpResponse(account.getId()));
	}

	@Override
	@Test
	public void testDeleteAccountByExternalReferenceCode() throws Exception {
		Account account = testDeleteAccountByExternalReferenceCode_addAccount();

		assertHttpResponseStatusCode(
			204,
			accountResource.deleteAccountByExternalReferenceCodeHttpResponse(
				account.getExternalReferenceCode()));
	}

	@Ignore
	@Override
	@Test
	public void testDeleteAccountGroupByExternalReferenceCodeAccount()
		throws Exception {
	}

	@Override
	@Test
	public void testGetAccount() throws Exception {
		super.testGetAccount();

		_testGetAccountWithPermission();
	}

	@Override
	@Test
	public void testGetAccountByExternalReferenceCode() throws Exception {
		super.testGetAccountByExternalReferenceCode();

		_testGetAccountByExternalReferenceCodeWithPermission();
	}

	@Override
	@Test
	public void testGetAccountsPage() throws Exception {
		super.testGetAccountsPage();

		Account account1 = testGetAccountsPage_addAccount(randomAccount());
		Account account2 = testGetAccountsPage_addAccount(randomAccount());

		_testGetAccountsPage(Arrays.asList(account1, account2));

		_testGetAccountsPage(Collections.emptyList());

		Organization organization = OrganizationTestUtil.addOrganization();

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			account1.getId(), organization.getOrganizationId());

		_testGetAccountsPage(Collections.singletonList(account1));
		_testGetAccountsPageWithPermission();
	}

	@Ignore
	@Override
	@Test
	public void testGetAccountsPageWithFilterDateTimeEquals() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetAccountsPageWithFilterStringEquals() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetAccountsPageWithPagination() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetAccountsPageWithSortDateTime() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetAccountsPageWithSortString() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccount() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountByExternalReferenceCode()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountGroupByExternalReferenceCodeAccount()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetAccount() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetAccountByExternalReferenceCode()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetAccountByExternalReferenceCodeNotFound()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetAccountNotFound() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetAccountsPage() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testPatchAccount() throws Exception {
		Assert.assertTrue(false);
	}

	@Ignore
	@Override
	@Test
	public void testPatchAccountByExternalReferenceCode() throws Exception {
		Assert.assertTrue(false);
	}

	@Ignore
	@Override
	@Test
	public void testPostAccountByExternalReferenceCodeLogo() throws Exception {
		Assert.assertTrue(false);
	}

	@Ignore
	@Override
	@Test
	public void testPostAccountGroupByExternalReferenceCodeAccount()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Ignore
	@Override
	@Test
	public void testPostAccountLogo() throws Exception {
		Assert.assertTrue(false);
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"type"};
	}

	@Override
	protected Account randomAccount() throws Exception {
		return new Account() {
			{
				active = true;
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				defaultBillingAccountAddressId = RandomTestUtil.randomLong();
				defaultShippingAccountAddressId = RandomTestUtil.randomLong();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				logoId = RandomTestUtil.randomLong();
				logoURL = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				root = true;
				taxId = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = 2;
			}
		};
	}

	@Override
	protected Account testDeleteAccount_addAccount() throws Exception {
		return _postAccount(randomAccount());
	}

	@Override
	protected Account testDeleteAccountByExternalReferenceCode_addAccount()
		throws Exception {

		Account account = randomAccount();

		account.setExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		return _postAccount(account);
	}

	@Override
	protected Account testGetAccount_addAccount() throws Exception {
		return _postAccount(randomAccount());
	}

	@Override
	protected Account testGetAccountByExternalReferenceCode_addAccount()
		throws Exception {

		Account account = randomAccount();

		account.setExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		return _postAccount(account);
	}

	@Override
	protected Account testGetAccountsPage_addAccount(Account account)
		throws Exception {

		return _postAccount(account);
	}

	@Override
	protected Account testGraphQLAccount_addAccount() throws Exception {
		return _postAccount(randomAccount());
	}

	@Override
	protected Account testPostAccount_addAccount(Account account)
		throws Exception {

		return _postAccount(account);
	}

	private void _addRoleUsers(
			long accountEntryId, User user, String... actionIds)
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(accountEntryId),
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

	private void _assertProblemException(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private AccountResource _getAccountResource(String password, User user) {
		return AccountResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private Account _postAccount(Account account) throws Exception {
		return accountResource.postAccount(account);
	}

	private void _testGetAccountByExternalReferenceCodeWithPermission()
		throws Exception {

		User user1 = _addUser();

		AccountResource accountResource1 = _getAccountResource(
			_PASSWORD, user1);

		Account account1 = _postAccount(randomAccount());

		_assertProblemException(
			() -> accountResource1.getAccountByExternalReferenceCode(
				account1.getExternalReferenceCode()));

		_addRoleUsers(account1.getId(), user1, ActionKeys.VIEW);

		Account account2 = accountResource1.getAccountByExternalReferenceCode(
			account1.getExternalReferenceCode());

		Assert.assertEquals(account1.getId(), account2.getId());

		User user2 = _addUser();

		AccountResource accountResource2 = _getAccountResource(
			_PASSWORD, user2);

		Account account3 = _postAccount(randomAccount());

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			account3.getId(), user2.getUserId());

		_assertProblemException(
			() -> accountResource2.getAccountByExternalReferenceCode(
				account1.getExternalReferenceCode()));

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			account1.getId(), user2.getUserId());

		Account account4 = accountResource2.getAccountByExternalReferenceCode(
			account1.getExternalReferenceCode());

		Assert.assertEquals(account1.getId(), account4.getId());
	}

	private void _testGetAccountsPage(List<Account> expectedAccountEntries)
		throws Exception {

		Page<Account> accountsPage = accountResource.getAccountsPage(
			null, null, Pagination.of(1, 10), null);

		for (Account account : accountsPage.getItems()) {
			expectedAccountEntries.contains(account);
		}
	}

	private void _testGetAccountsPageWithPermission() throws Exception {
		User user = _addUser();

		AccountResource accountResource = _getAccountResource(_PASSWORD, user);

		Account account1 = _postAccount(randomAccount());

		Page<Account> accountsPage1 = accountResource.getAccountsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(0, accountsPage1.getTotalCount());

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			account1.getId(), user.getUserId());

		Page<Account> accountsPage2 = accountResource.getAccountsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(1, accountsPage2.getTotalCount());

		Account account2 = accountsPage2.fetchFirstItem();

		Assert.assertEquals(account1.getId(), account2.getId());
	}

	private void _testGetAccountWithPermission() throws Exception {
		User user1 = _addUser();

		AccountResource accountResource1 = _getAccountResource(
			_PASSWORD, user1);

		Account account1 = _postAccount(randomAccount());

		_assertProblemException(
			() -> accountResource1.getAccount(account1.getId()));

		_addRoleUsers(account1.getId(), user1, ActionKeys.VIEW);

		Account account2 = accountResource1.getAccount(account1.getId());

		Assert.assertEquals(account1.getId(), account2.getId());

		User user2 = _addUser();

		AccountResource accountResource2 = _getAccountResource(
			_PASSWORD, user2);

		Account account3 = _postAccount(randomAccount());

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			account3.getId(), user2.getUserId());

		_assertProblemException(
			() -> accountResource2.getAccount(account1.getId()));

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			account1.getId(), user2.getUserId());

		Account account4 = accountResource2.getAccount(account1.getId());

		Assert.assertEquals(account1.getId(), account4.getId());
	}

	private static final String _PASSWORD = RandomTestUtil.randomString();

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private UserLocalService _userLocalService;

}