/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.resource.v1_0.test;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.Account;
import com.liferay.headless.commerce.delivery.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.catalog.client.problem.Problem;
import com.liferay.headless.commerce.delivery.catalog.client.resource.v1_0.AccountResource;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class AccountResourceTest extends BaseAccountResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), RandomTestUtil.randomString());

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());
	}

	@Override
	@Test
	public void testGetChannelAccountsPage() throws Exception {
		super.testGetChannelAccountsPage();

		_testGetEligibleChannelAccountsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGetChannelAccountsPageWithFilterStringContains()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetChannelAccountsPageWithFilterStringEquals()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetChannelAccountsPageWithFilterStringStartsWith()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetChannelAccountsPageWithSortString() throws Exception {
	}

	@Override
	@Test
	public void testPostChannelAccount() throws Exception {
		super.testPostChannelAccount();

		_testPostChannelAccountWithLogo();
		_testPostEligibleChannelAccount();
	}

	protected Account randomAccount() throws Exception {
		return new Account() {
			{
				description = RandomTestUtil.randomString();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = RandomTestUtil.randomString();
				type = Type.BUSINESS;
			}
		};
	}

	@Override
	protected Account testGetChannelAccountsPage_addAccount(
			Long channelId, Account account)
		throws Exception {

		return _addAccount(account);
	}

	@Override
	protected Long testGetChannelAccountsPage_getChannelId() throws Exception {
		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected Account testPostChannelAccount_addAccount(Account account)
		throws Exception {

		return _addAccount(account);
	}

	private Account _addAccount(Account account) throws Exception {
		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			account.getExternalReferenceCode(), _user.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT, account.getName(),
			account.getDescription(), null, _user.getEmailAddress(), null,
			StringPool.BLANK, AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);

		_accountEntries.add(accountEntry);

		return new Account() {
			{
				dateCreated = accountEntry.getCreateDate();
				dateModified = accountEntry.getModifiedDate();
				description = accountEntry.getDescription();
				externalReferenceCode = accountEntry.getExternalReferenceCode();
				id = accountEntry.getAccountEntryId();
				name = accountEntry.getName();
			}
		};
	}

	private void _assertProblemException(
			String status, String title,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(status, problem.getStatus());
			Assert.assertEquals(title, problem.getTitle());
		}
	}

	private void _testGetEligibleChannelAccountsPage() throws Exception {
		_assertProblemException(
			"NOT_FOUND", null,
			() -> accountResource.getChannelAccountsPage(
				0L, null, null, null, null));

		Account account = _addAccount(randomAccount());

		_commerceChannelAccountEntryRelLocalService.
			addCommerceChannelAccountEntryRel(
				_user.getUserId(), account.getId(), null, 0,
				_commerceChannel.getCommerceChannelId(), false, 0,
				CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);

		User user = UserTestUtil.addUser(testCompany, "test");

		AccountResource accountResource = AccountResource.builder(
		).authentication(
			user.getEmailAddress(), "test"
		).header(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON
		).build();

		_assertProblemException(
			"NOT_FOUND", null,
			() -> accountResource.getChannelAccountsPage(
				_commerceChannel.getCommerceChannelId(), null, null, null,
				null));
	}

	private void _testPostChannelAccountWithLogo() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(), PortletKeys.PORTAL,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(testCompany.getCompanyId()), role.getRoleId(),
			new String[] {AccountActionKeys.ADD_ACCOUNT_ENTRY});

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		_userLocalService.addRoleUsers(
			role.getRoleId(), new long[] {user.getUserId()});

		AccountResource accountResource = AccountResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).header(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON
		).build();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".png", ContentTypes.IMAGE_PNG,
			DLTestUtil.getImageBytes("png"), null, null, null, serviceContext);

		Account account = randomAccount();

		account.setLogoId(fileEntry.getFileEntryId());

		HttpInvoker.HttpResponse httpResponse =
			accountResource.postChannelAccountHttpResponse(
				_commerceChannel.getCommerceChannelId(), account);

		Assert.assertEquals(
			Response.Status.FORBIDDEN.getStatusCode(),
			httpResponse.getStatusCode());

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fileEntry.getFileEntryId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		account = accountResource.postChannelAccount(
			_commerceChannel.getCommerceChannelId(), account);

		Assert.assertTrue(account.getLogoId() > 0);
	}

	private void _testPostEligibleChannelAccount() throws Exception {
		_assertProblemException(
			"NOT_FOUND", null,
			() -> accountResource.postChannelAccount(0L, randomAccount()));

		Account account = _addAccount(randomAccount());

		_commerceChannelAccountEntryRelLocalService.
			addCommerceChannelAccountEntryRel(
				_user.getUserId(), account.getId(), null, 0,
				_commerceChannel.getCommerceChannelId(), false, 0,
				CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);

		User user = UserTestUtil.addUser(testCompany, "test");

		AccountResource accountResource = AccountResource.builder(
		).authentication(
			user.getEmailAddress(), "test"
		).header(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON
		).build();

		_assertProblemException(
			"NOT_FOUND", null,
			() -> accountResource.postChannelAccount(
				_commerceChannel.getCommerceChannelId(), randomAccount()));
	}

	@DeleteAfterTestRun
	private final List<AccountEntry> _accountEntries = new ArrayList<>();

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}