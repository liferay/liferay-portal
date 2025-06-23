/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountMember;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

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

		_accountEntry = AccountEntryLocalServiceUtil.addAccountEntry(
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

	@Override
	@Test
	public void testPatchAccountIdAccountMember() throws Exception {
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

	private AccountMember _randomAccountMember() throws Exception {
		User user = UserTestUtil.addUser(testCompany);

		return new AccountMember() {
			{
				email = user.getEmailAddress();
				name = user.getFullName();
			}
		};
	}

	private AccountEntry _accountEntry;
	private ServiceContext _serviceContext;
	private User _user;

}