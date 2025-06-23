/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountRole;
import com.liferay.headless.admin.user.client.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.Phone;
import com.liferay.headless.admin.user.client.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccountContactInformation;
import com.liferay.headless.admin.user.client.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountResource;
import com.liferay.headless.admin.user.client.resource.v1_0.UserAccountResource;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.util.PropsValues;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class AccountRoleResourceTest extends BaseAccountRoleResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_accountResource = AccountResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).build();

		_userAccountResource = UserAccountResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).build();

		_account = _accountResource.putAccountByExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			_randomAccount());

		_sharedAccountRoles = TransformUtil.transform(
			_accountRoleLocalService.getAccountRolesByAccountEntryIds(
				TestPropsValues.getCompanyId(),
				new long[] {AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT}),
			serviceBuilderAccountRole -> {
				Role role = serviceBuilderAccountRole.getRole();

				return new AccountRole() {
					{
						accountId =
							serviceBuilderAccountRole.getAccountEntryId();
						description = role.getDescription();
						displayName = role.getTitle();
						id = serviceBuilderAccountRole.getAccountRoleId();
						name = serviceBuilderAccountRole.getRoleName();
						roleId = serviceBuilderAccountRole.getRoleId();
					}
				};
			});
	}

	@After
	@Override
	public void tearDown() throws Exception {
	}

	@Override
	@Test
	public void testDeleteAccountAccountRoleUserAccountAssociation()
		throws Exception {

		AccountRole accountRole = _addAccountAccountRole(_account);
		UserAccount userAccount = _addAccountUserAccount(_account);

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, false);

		_accountRoleLocalService.associateUser(
			_account.getId(), accountRole.getId(), userAccount.getId());

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, true);

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				deleteAccountAccountRoleUserAccountAssociationHttpResponse(
					_account.getId(), accountRole.getId(),
					userAccount.getId()));

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, false);
	}

	@Override
	@Test
	public void testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress()
		throws Exception {

		AccountRole accountRole =
			testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_addAccountRole();

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				deleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddressHttpResponse(
					testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_getExternalReferenceCode(
						accountRole),
					accountRole.getExternalReferenceCode(),
					testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_getEmailAddress()));
	}

	@Override
	@Test
	public void testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode()
		throws Exception {

		AccountRole accountRole = _addAccountAccountRole(_account);
		UserAccount userAccount = _addAccountUserAccount(_account);

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, false);

		_accountRoleLocalService.associateUser(
			_account.getId(), accountRole.getId(), userAccount.getId());

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, true);

		accountRoleResource.
			deleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCodeHttpResponse(
				_account.getExternalReferenceCode(),
				accountRole.getExternalReferenceCode(),
				userAccount.getExternalReferenceCode());

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, false);
	}

	@Override
	@Test
	public void testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode()
		throws Exception {

		AccountRole accountRole = _addAccountAccountRole(_account);
		UserAccount userAccount = _addAccountUserAccount(_account);

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, false);

		_accountRoleLocalService.associateUser(
			_account.getId(), accountRole.getId(), userAccount.getId());

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, true);

		accountRoleResource.
			deleteAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCodeHttpResponse(
				_account.getExternalReferenceCode(), accountRole.getId(),
				userAccount.getExternalReferenceCode());

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, false);
	}

	@Override
	@Test
	public void testGetAccountAccountRolesByExternalReferenceCodePage()
		throws Exception {

		Page<AccountRole> page =
			accountRoleResource.
				getAccountAccountRolesByExternalReferenceCodePage(
					testGetAccountAccountRolesByExternalReferenceCodePage_getExternalReferenceCode(),
					RandomTestUtil.randomString(), null, Pagination.of(1, 2),
					null);

		Assert.assertEquals(0, page.getTotalCount());

		String externalReferenceCode =
			testGetAccountAccountRolesByExternalReferenceCodePage_getExternalReferenceCode();

		String irrelevantExternalReferenceCode =
			testGetAccountAccountRolesByExternalReferenceCodePage_getIrrelevantExternalReferenceCode();

		if (irrelevantExternalReferenceCode != null) {
			AccountRole irrelevantAccountRole =
				testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
					irrelevantExternalReferenceCode,
					randomIrrelevantAccountRole());

			page =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, 2), null);

			Assert.assertEquals(1, page.getTotalCount());

			assertEquals(
				Arrays.asList(irrelevantAccountRole),
				(List<AccountRole>)page.getItems());
			assertValid(page);
		}

		List<AccountRole> expectedAccountRoles = ListUtil.concat(
			Arrays.asList(
				testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
					externalReferenceCode, randomAccountRole()),
				testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
					externalReferenceCode, randomAccountRole())),
			_sharedAccountRoles);

		page =
			accountRoleResource.
				getAccountAccountRolesByExternalReferenceCodePage(
					externalReferenceCode, null, null,
					Pagination.of(1, expectedAccountRoles.size()), null);

		Assert.assertEquals(expectedAccountRoles.size(), page.getTotalCount());

		assertEqualsIgnoringOrder(
			expectedAccountRoles, (List<AccountRole>)page.getItems());
		assertValid(page);

		AccountRole accountRole =
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				externalReferenceCode, randomAccountRole());

		page =
			accountRoleResource.
				getAccountAccountRolesByExternalReferenceCodePage(
					externalReferenceCode, null,
					String.format("name eq '%s'", accountRole.getName()),
					Pagination.of(1, 100), null);

		Assert.assertEquals(1, page.getTotalCount());
		Assert.assertEquals(accountRole, page.fetchFirstItem());
	}

	@Override
	@Test
	public void testGetAccountAccountRolesByExternalReferenceCodePageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetAccountAccountRolesByExternalReferenceCodePage_getExternalReferenceCode();

		List<AccountRole> expectedAccountRoles = ListUtil.concat(
			Arrays.asList(
				testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
					externalReferenceCode, randomAccountRole()),
				testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
					externalReferenceCode, randomAccountRole()),
				testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
					externalReferenceCode, randomAccountRole())),
			_sharedAccountRoles);

		Page<AccountRole> page1 =
			accountRoleResource.
				getAccountAccountRolesByExternalReferenceCodePage(
					externalReferenceCode, null, null, Pagination.of(1, 2),
					null);

		List<AccountRole> accountRoles1 = (List<AccountRole>)page1.getItems();

		Assert.assertEquals(accountRoles1.toString(), 2, accountRoles1.size());

		Page<AccountRole> page2 =
			accountRoleResource.
				getAccountAccountRolesByExternalReferenceCodePage(
					externalReferenceCode, null, null,
					Pagination.of(2, expectedAccountRoles.size() - 1), null);

		Assert.assertEquals(expectedAccountRoles.size(), page2.getTotalCount());

		List<AccountRole> accountRoles2 = (List<AccountRole>)page2.getItems();

		Assert.assertEquals(accountRoles2.toString(), 1, accountRoles2.size());

		Page<AccountRole> page3 =
			accountRoleResource.
				getAccountAccountRolesByExternalReferenceCodePage(
					externalReferenceCode, null, null,
					Pagination.of(1, expectedAccountRoles.size()), null);

		assertEqualsIgnoringOrder(
			expectedAccountRoles, (List<AccountRole>)page3.getItems());
	}

	@Override
	@Test
	public void testGetAccountAccountRolesPage() throws Exception {
		Page<AccountRole> page = accountRoleResource.getAccountAccountRolesPage(
			testGetAccountAccountRolesPage_getAccountId(),
			RandomTestUtil.randomString(), null, Pagination.of(1, 2), null);

		Assert.assertEquals(0, page.getTotalCount());

		Long accountId = testGetAccountAccountRolesPage_getAccountId();

		Long irrelevantAccountId =
			testGetAccountAccountRolesPage_getIrrelevantAccountId();

		if (irrelevantAccountId != null) {
			AccountRole irrelevantAccountRole =
				testGetAccountAccountRolesPage_addAccountRole(
					irrelevantAccountId, randomIrrelevantAccountRole());

			page = accountRoleResource.getAccountAccountRolesPage(
				irrelevantAccountId, null, null, Pagination.of(1, 2), null);

			Assert.assertEquals(1, page.getTotalCount());

			assertEquals(
				Collections.singletonList(irrelevantAccountRole),
				(List<AccountRole>)page.getItems());
			assertValid(page);
		}

		AccountRole accountRole1 =
			testGetAccountAccountRolesPage_addAccountRole(
				accountId, randomAccountRole());

		AccountRole accountRole2 =
			testGetAccountAccountRolesPage_addAccountRole(
				accountId, randomAccountRole());

		List<AccountRole> expectedAccountRoles = ListUtil.concat(
			Arrays.asList(accountRole1, accountRole2), _sharedAccountRoles);

		page = accountRoleResource.getAccountAccountRolesPage(
			accountId, null, null,
			Pagination.of(1, expectedAccountRoles.size()), null);

		Assert.assertEquals(_addSharedAccountRoles(2), page.getTotalCount());

		assertEqualsIgnoringOrder(
			expectedAccountRoles, (List<AccountRole>)page.getItems());
		assertValid(page);

		AccountRole accountRole3 =
			testGetAccountAccountRolesPage_addAccountRole(
				accountId, randomAccountRole());

		page = accountRoleResource.getAccountAccountRolesPage(
			accountId, null,
			String.format("name eq '%s'", accountRole3.getName()),
			Pagination.of(1, expectedAccountRoles.size()), null);

		Assert.assertEquals(1, page.getTotalCount());
		Assert.assertEquals(accountRole3, page.fetchFirstItem());

		page = accountRoleResource.getAccountAccountRolesPage(
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, null, null,
			Pagination.of(1, _sharedAccountRoles.size()), null);

		assertEqualsIgnoringOrder(
			_sharedAccountRoles, (List<AccountRole>)page.getItems());
	}

	@Override
	@Test
	public void testGetAccountAccountRolesPageWithPagination()
		throws Exception {

		Long accountId = testGetAccountAccountRolesPage_getAccountId();

		List<AccountRole> expectedAccountRoles = ListUtil.concat(
			Arrays.asList(
				testGetAccountAccountRolesPage_addAccountRole(
					accountId, randomAccountRole()),
				testGetAccountAccountRolesPage_addAccountRole(
					accountId, randomAccountRole()),
				testGetAccountAccountRolesPage_addAccountRole(
					accountId, randomAccountRole())),
			_sharedAccountRoles);

		Page<AccountRole> page1 =
			accountRoleResource.getAccountAccountRolesPage(
				accountId, null, null, Pagination.of(1, 2), null);

		List<AccountRole> accountRoles1 = (List<AccountRole>)page1.getItems();

		Assert.assertEquals(accountRoles1.toString(), 2, accountRoles1.size());

		Page<AccountRole> page2 =
			accountRoleResource.getAccountAccountRolesPage(
				accountId, null, null,
				Pagination.of(2, expectedAccountRoles.size() - 1), null);

		Assert.assertEquals(expectedAccountRoles.size(), page2.getTotalCount());

		List<AccountRole> accountRoles2 = (List<AccountRole>)page2.getItems();

		Assert.assertEquals(accountRoles2.toString(), 1, accountRoles2.size());

		Page<AccountRole> page3 =
			accountRoleResource.getAccountAccountRolesPage(
				accountId, null, null,
				Pagination.of(1, expectedAccountRoles.size()), null);

		assertEqualsIgnoringOrder(
			expectedAccountRoles, (List<AccountRole>)page3.getItems());
	}

	@Override
	@Test
	public void testPostAccountAccountRole() throws Exception {
		super.testPostAccountAccountRole();

		_testPostAccountAccountRoleWithExternalReferenceCode();
	}

	@Override
	@Test
	public void testPostAccountAccountRoleUserAccountAssociation()
		throws Exception {

		AccountRole accountRole = _addAccountAccountRole(_account);
		UserAccount userAccount = _addAccountUserAccount(_account);

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, false);

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				postAccountAccountRoleUserAccountAssociationHttpResponse(
					_account.getId(), accountRole.getId(),
					userAccount.getId()));

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, true);

		assertHttpResponseStatusCode(
			404,
			accountRoleResource.
				postAccountAccountRoleUserAccountAssociationHttpResponse(
					_account.getId(), 0L, userAccount.getId()));
	}

	@Override
	@Test
	public void testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress()
		throws Exception {

		AccountRole accountRole = _addAccountAccountRole(_account);
		UserAccount userAccount = _addAccountUserAccount(_account);

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, false);

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddressHttpResponse(
					_account.getExternalReferenceCode(),
					accountRole.getExternalReferenceCode(),
					userAccount.getEmailAddress()));

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, true);

		assertHttpResponseStatusCode(
			404,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddressHttpResponse(
					_account.getExternalReferenceCode(), 0L,
					userAccount.getEmailAddress()));
	}

	@Override
	@Test
	public void testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode()
		throws Exception {

		AccountRole accountRole = _addAccountAccountRole(_account);
		UserAccount userAccount = _addAccountUserAccount(_account);

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, false);

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCodeHttpResponse(
					_account.getExternalReferenceCode(),
					accountRole.getExternalReferenceCode(),
					userAccount.getExternalReferenceCode()));

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, true);

		assertHttpResponseStatusCode(
			404,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCodeHttpResponse(
					_account.getExternalReferenceCode(), 0L,
					userAccount.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testPostAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress()
		throws Exception {

		AccountRole accountRole = _addAccountAccountRole(_account);
		UserAccount userAccount = _addAccountUserAccount(_account);

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, false);

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddressHttpResponse(
					_account.getExternalReferenceCode(), accountRole.getId(),
					userAccount.getEmailAddress()));

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, true);

		assertHttpResponseStatusCode(
			404,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddressHttpResponse(
					_account.getExternalReferenceCode(), 0L,
					userAccount.getEmailAddress()));
	}

	@Override
	@Test
	public void testPostAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode()
		throws Exception {

		AccountRole accountRole = _addAccountAccountRole(_account);
		UserAccount userAccount = _addAccountUserAccount(_account);

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, false);

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCodeHttpResponse(
					_account.getExternalReferenceCode(), accountRole.getId(),
					userAccount.getExternalReferenceCode()));

		_assertAccountRoleUserAccountAssociation(
			_account, accountRole, userAccount, true);

		assertHttpResponseStatusCode(
			404,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCodeHttpResponse(
					_account.getExternalReferenceCode(), 0L,
					userAccount.getExternalReferenceCode()));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name"};
	}

	@Override
	protected AccountRole
			testDeleteAccountAccountRoleUserAccountAssociation_addAccountRole()
		throws Exception {

		return _addAccountAccountRole(_account);
	}

	@Override
	protected AccountRole
			testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_addAccountRole()
		throws Exception {

		_userAccount = _addAccountUserAccount(_account);

		return _addAccountAccountRole(_account);
	}

	@Override
	protected String
			testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_getEmailAddress()
		throws Exception {

		return _userAccount.getEmailAddress();
	}

	@Override
	protected String
			testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_getExternalReferenceCode(
				AccountRole accountRole)
		throws Exception {

		return _account.getExternalReferenceCode();
	}

	@Override
	protected AccountRole
			testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_addAccountRole()
		throws Exception {

		_userAccount = _addAccountUserAccount(_account);

		return _addAccountAccountRole(_account);
	}

	@Override
	protected String
			testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_getEmailAddress()
		throws Exception {

		return _userAccount.getEmailAddress();
	}

	@Override
	protected String
			testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_getExternalReferenceCode(
				AccountRole accountRole)
		throws Exception {

		return _account.getExternalReferenceCode();
	}

	@Override
	protected AccountRole
			testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode_addAccountRole()
		throws Exception {

		return _addAccountAccountRole(_account);
	}

	@Override
	protected AccountRole
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				String externalReferenceCode, AccountRole accountRole)
		throws Exception {

		return accountRoleResource.
			postAccountAccountRoleByExternalReferenceCode(
				externalReferenceCode, accountRole);
	}

	@Override
	protected String
			testGetAccountAccountRolesByExternalReferenceCodePage_getExternalReferenceCode()
		throws Exception {

		return _account.getExternalReferenceCode();
	}

	@Override
	protected void
			testGetAccountAccountRolesByExternalReferenceCodePageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, AccountRole, AccountRole, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetAccountAccountRolesByExternalReferenceCodePage_getExternalReferenceCode();

		AccountRole accountRole1 = randomAccountRole();
		AccountRole accountRole2 = randomAccountRole();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, accountRole1, accountRole2);
		}

		accountRole1 =
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				externalReferenceCode, accountRole1);

		accountRole2 =
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				externalReferenceCode, accountRole2);

		String filterString = String.format(
			"name in ('%s', '%s')", accountRole1.getName(),
			accountRole2.getName());

		for (EntityField entityField : entityFields) {
			Page<AccountRole> ascPage =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						externalReferenceCode, null, filterString,
						Pagination.of(1, 2), entityField.getName() + ":asc");

			assertEquals(
				Arrays.asList(accountRole1, accountRole2),
				(List<AccountRole>)ascPage.getItems());

			Page<AccountRole> descPage =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						externalReferenceCode, null, filterString,
						Pagination.of(1, 2), entityField.getName() + ":desc");

			assertEquals(
				Arrays.asList(accountRole2, accountRole1),
				(List<AccountRole>)descPage.getItems());
		}
	}

	@Override
	protected AccountRole testGetAccountAccountRolesPage_addAccountRole(
			Long accountId, AccountRole accountRole)
		throws Exception {

		return accountRoleResource.postAccountAccountRole(
			accountId, accountRole);
	}

	@Override
	protected Long testGetAccountAccountRolesPage_getAccountId() {
		return _account.getId();
	}

	@Override
	protected void testGetAccountAccountRolesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, AccountRole, AccountRole, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountId = testGetAccountAccountRolesPage_getAccountId();

		AccountRole accountRole1 = randomAccountRole();
		AccountRole accountRole2 = randomAccountRole();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, accountRole1, accountRole2);
		}

		accountRole1 = testGetAccountAccountRolesPage_addAccountRole(
			accountId, accountRole1);

		accountRole2 = testGetAccountAccountRolesPage_addAccountRole(
			accountId, accountRole2);

		String filterString = String.format(
			"name in ('%s', '%s')", accountRole1.getName(),
			accountRole2.getName());

		for (EntityField entityField : entityFields) {
			Page<AccountRole> ascPage =
				accountRoleResource.getAccountAccountRolesPage(
					accountId, null, filterString, Pagination.of(1, 2),
					entityField.getName() + ":asc");

			assertEquals(
				Arrays.asList(accountRole1, accountRole2),
				(List<AccountRole>)ascPage.getItems());

			Page<AccountRole> descPage =
				accountRoleResource.getAccountAccountRolesPage(
					accountId, null, filterString, Pagination.of(1, 2),
					entityField.getName() + ":desc");

			assertEquals(
				Arrays.asList(accountRole2, accountRole1),
				(List<AccountRole>)descPage.getItems());
		}
	}

	@Override
	protected AccountRole
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_addAccountRole(
				String externalReferenceCode, String emailAddress,
				AccountRole accountRole)
		throws Exception {

		accountRole = accountRoleResource.postAccountAccountRole(
			_account.getId(), accountRole);

		accountRoleResource.
			postAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddressHttpResponse(
				externalReferenceCode, accountRole.getId(), emailAddress);

		return accountRole;
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_getEmailAddress()
		throws Exception {

		UserAccount userAccount = _userAccountResource.postUserAccount(
			_randomUserAccount());

		return userAccount.getEmailAddress();
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_getExternalReferenceCode()
		throws Exception {

		return _account.getExternalReferenceCode();
	}

	@Override
	protected AccountRole
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_addAccountRole(
				String accountExternalReferenceCode,
				String userAccountExternalReferenceCode,
				AccountRole accountRole)
		throws Exception {

		accountRole = accountRoleResource.postAccountAccountRole(
			_account.getId(), accountRole);

		accountRoleResource.
			postAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode(
				accountExternalReferenceCode, accountRole.getId(),
				userAccountExternalReferenceCode);

		return accountRole;
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_getAccountExternalReferenceCode()
		throws Exception {

		return _account.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_getExternalReferenceCode()
		throws Exception {

		UserAccount userAccount =
			_userAccountResource.putUserAccountByExternalReferenceCode(
				RandomTestUtil.randomString(), _randomUserAccount());

		return userAccount.getExternalReferenceCode();
	}

	@Override
	protected AccountRole testPostAccountAccountRole_addAccountRole(
			AccountRole accountRole)
		throws Exception {

		return accountRoleResource.postAccountAccountRole(
			_account.getId(), accountRole);
	}

	@Override
	protected AccountRole
			testPostAccountAccountRoleByExternalReferenceCode_addAccountRole(
				AccountRole accountRole)
		throws Exception {

		return accountRoleResource.
			postAccountAccountRoleByExternalReferenceCode(
				_account.getExternalReferenceCode(), accountRole);
	}

	@Override
	protected AccountRole
			testPostAccountAccountRoleUserAccountAssociation_addAccountRole()
		throws Exception {

		return _addAccountAccountRole(_account);
	}

	@Override
	protected AccountRole
			testPostAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_addAccountRole()
		throws Exception {

		return _addAccountAccountRole(_account);
	}

	@Override
	protected AccountRole
			testPostAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode_addAccountRole()
		throws Exception {

		return _addAccountAccountRole(_account);
	}

	private AccountRole _addAccountAccountRole(Account account)
		throws Exception {

		return accountRoleResource.postAccountAccountRole(
			account.getId(), randomAccountRole());
	}

	private UserAccount _addAccountUserAccount(Account account)
		throws Exception {

		UserAccount userAccount =
			_userAccountResource.putUserAccountByExternalReferenceCode(
				RandomTestUtil.randomString(), _randomUserAccount());

		_userAccountResource.postAccountUserAccountByEmailAddress(
			account.getId(), userAccount.getEmailAddress());

		return userAccount;
	}

	private int _addSharedAccountRoles(int count) {
		return count + _sharedAccountRoles.size();
	}

	private void _assertAccountRoleUserAccountAssociation(
			Account account, AccountRole accountRole, UserAccount userAccount,
			boolean hasAssociation)
		throws Exception {

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			account.getId());

		UserGroupRole userGroupRole =
			_userGroupRoleLocalService.fetchUserGroupRole(
				userAccount.getId(), accountEntry.getAccountEntryGroupId(),
				accountRole.getRoleId());

		if (hasAssociation) {
			Assert.assertNotNull(userGroupRole);
		}
		else {
			Assert.assertNull(userGroupRole);
		}
	}

	private Account _randomAccount() {
		return new Account() {
			{
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				parentAccountId = RandomTestUtil.randomLong();
				status = RandomTestUtil.randomInt();
				type = Account.Type.create(
					AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);
			}
		};
	}

	private EmailAddress _randomEmailAddress() throws Exception {
		return new EmailAddress() {
			{
				emailAddress = RandomTestUtil.randomString() + "@liferay.com";
				primary = true;
				type = "email-address";
			}
		};
	}

	private Phone _randomPhone() throws Exception {
		return new Phone() {
			{
				extension = String.valueOf(RandomTestUtil.randomInt());
				phoneNumber = String.valueOf(RandomTestUtil.randomInt());
				phoneType = "personal";
				primary = true;
			}
		};
	}

	private PostalAddress _randomPostalAddress() throws Exception {
		return new PostalAddress() {
			{
				addressCountry = "united-states";
				addressLocality = "Diamond Bar";
				addressRegion = "California";
				addressType = "personal";
				postalCode = "91765";
				primary = true;
				streetAddressLine1 = RandomTestUtil.randomString();
				streetAddressLine2 = RandomTestUtil.randomString();
				streetAddressLine3 = RandomTestUtil.randomString();
			}
		};
	}

	private UserAccount _randomUserAccount() throws Exception {
		return new UserAccount() {
			{
				additionalName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				alternateName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				birthDate = RandomTestUtil.nextDate();
				dashboardURL = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				emailAddress =
					StringUtil.toLowerCase(RandomTestUtil.randomString()) +
						"@liferay.com";
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				familyName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				givenName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				honorificPrefix = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				honorificSuffix = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				image = StringUtil.toLowerCase(RandomTestUtil.randomString());
				jobTitle = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				profileURL = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				userAccountContactInformation =
					_randomUserAccountContactInformation();

				setBirthDate(
					() -> {
						Calendar calendar = CalendarFactoryUtil.getCalendar();

						calendar.setTime(RandomTestUtil.nextDate());
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(Calendar.MINUTE, 0);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MILLISECOND, 0);

						return calendar.getTime();
					});
			}
		};
	}

	private UserAccountContactInformation _randomUserAccountContactInformation()
		throws Exception {

		return new UserAccountContactInformation() {
			{
				emailAddresses = new EmailAddress[] {_randomEmailAddress()};
				facebook = RandomTestUtil.randomString();
				jabber = RandomTestUtil.randomString();
				postalAddresses = new PostalAddress[] {_randomPostalAddress()};
				skype = RandomTestUtil.randomString();
				sms = RandomTestUtil.randomString() + "@liferay.com";
				telephones = new Phone[] {_randomPhone()};
				twitter = RandomTestUtil.randomString();
				webUrls = new WebUrl[] {_randomWebUrl()};
			}
		};
	}

	private WebUrl _randomWebUrl() throws Exception {
		return new WebUrl() {
			{
				primary = true;
				url = "https://" + RandomTestUtil.randomString() + ".com";
				urlType = "personal";
			}
		};
	}

	private void _testPostAccountAccountRoleWithExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		AccountRole randomAccountRole = randomAccountRole();

		randomAccountRole.setExternalReferenceCode(externalReferenceCode);

		AccountRole postAccountRole = testPostAccountAccountRole_addAccountRole(
			randomAccountRole);

		assertEquals(randomAccountRole, postAccountRole);
		assertValid(postAccountRole);
		Assert.assertEquals(
			externalReferenceCode, postAccountRole.getExternalReferenceCode());
	}

	private Account _account;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private AccountResource _accountResource;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	private List<AccountRole> _sharedAccountRoles;
	private UserAccount _userAccount;
	private UserAccountResource _userAccountResource;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}