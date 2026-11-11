/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.account.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class CommerceAccountLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testAddBusinessCommerceAccount() throws Exception {
		frutillaRule.scenario(
			"Adding a new business Commerce Account"
		).given(
			"A company"
		).and(
			"A user as Account Administrator"
		).when(
			"The Commerce Account is created"
		).then(
			"The list of accounts contains only one Commerce Account"
		).and(
			"That Commerce Account matches the one created before."
		);

		AccountEntry businessAccountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_user.getUserId(), "Business Account", "example@email.com",
				_getServiceContext());

		List<AccountEntry> accountEntries =
			_accountEntryLocalService.getUserAccountEntries(
				_user.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B),
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		int accountEntriesCount =
			_accountEntryLocalService.getUserAccountEntriesCount(
				_user.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B));

		Assert.assertEquals(
			_user.toString(), accountEntries.size(), accountEntriesCount);

		Assert.assertEquals(_user.toString(), 1, accountEntriesCount);

		AccountEntry accountEntry = accountEntries.get(0);

		Assert.assertEquals(
			businessAccountEntry.getAccountEntryId(),
			accountEntry.getAccountEntryId());
		Assert.assertEquals(
			businessAccountEntry.getType(), accountEntry.getType());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, accountEntry.getStatus());
	}

	@Test
	public void testAddPersonalCommerceAccount() throws Exception {
		frutillaRule.scenario(
			"Adding a new personal Commerce Account"
		).given(
			"A company"
		).when(
			"The Commerce Account is created"
		).then(
			"The list of accounts contains only one Commerce Account"
		).and(
			"That Commerce Account matches the one created before."
		);

		AccountEntry businessAccountEntry =
			CommerceAccountTestUtil.addPersonAccountEntry(
				_user.getUserId(), _getServiceContext());

		Assert.assertEquals(
			businessAccountEntry.getName(), _user.getFullName());

		List<AccountEntry> accountEntries =
			_accountEntryLocalService.getUserAccountEntries(
				_user.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2C),
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		int accountEntriesCount =
			_accountEntryLocalService.getUserAccountEntriesCount(
				_user.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2C));

		Assert.assertEquals(
			_user.toString(), accountEntries.size(), accountEntriesCount);

		Assert.assertEquals(_user.toString(), 1, accountEntriesCount);

		Assert.assertEquals(
			accountEntries.toString(), 1, accountEntries.size());

		AccountEntry accountEntry = accountEntries.get(0);

		Assert.assertEquals(
			businessAccountEntry.getAccountEntryId(),
			accountEntry.getAccountEntryId());
		Assert.assertEquals(
			businessAccountEntry.getName(), accountEntry.getName());
		Assert.assertEquals(
			businessAccountEntry.getType(), accountEntry.getType());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, accountEntry.getStatus());
	}

	@Test
	public void testBusinessOrganizationCommerceAccountsVisibility()
		throws Exception {

		frutillaRule.scenario(
			"Adding new business Commerce Accounts"
		).given(
			"A company"
		).when(
			"The Commerce Accounts are created"
		).and(
			"Organizations are added to them"
		).then(
			"Check the visibility of that accounts for all the organizations"
		);

		ServiceContext serviceContext = _getServiceContext();

		String organizationName = RandomTestUtil.randomString();

		for (int i = 1; i < 3; i++) {
			Organization organization =
				_organizationLocalService.addOrganization(
					_user.getUserId(),
					OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
					organizationName + i, false);

			User user = UserTestUtil.addUser(
				_user.getCompanyId(), _user.getUserId(), "organizationUser" + i,
				serviceContext.getLocale(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				new long[] {serviceContext.getScopeGroupId()}, serviceContext);

			_organizationLocalService.addUserOrganization(
				user.getUserId(), organization);

			CommerceAccountTestUtil.addBusinessAccountEntry(
				_user.getUserId(), "businessOrganizationAccount" + i,
				"example@example.com", StringPool.BLANK, null,
				new long[] {organization.getOrganizationId()}, serviceContext);
		}

		User organizationUser1 = _userLocalService.getUserByScreenName(
			_user.getCompanyId(), "organizationUser1");
		User organizationUser2 = _userLocalService.getUserByScreenName(
			_user.getCompanyId(), "organizationUser2");

		List<AccountEntry> organizationUserAccountEntries1 =
			_accountEntryLocalService.getUserAccountEntries(
				organizationUser1.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		int organizationUserAccountEntriesCount1 =
			_accountEntryLocalService.getUserAccountEntriesCount(
				organizationUser1.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B));

		Assert.assertEquals(
			organizationUser1.toString(),
			organizationUserAccountEntries1.size(),
			organizationUserAccountEntriesCount1);

		Assert.assertEquals(
			organizationUser2.toString(), 1,
			organizationUserAccountEntriesCount1);

		AccountEntry organizationUserAccountEntry1 =
			organizationUserAccountEntries1.get(0);

		Assert.assertEquals(
			"businessOrganizationAccount1",
			organizationUserAccountEntry1.getName());

		List<AccountEntry> organizationUserAccountEntries2 =
			_accountEntryLocalService.getUserAccountEntries(
				organizationUser2.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		int organizationUserAccountEntriesCount2 =
			_accountEntryLocalService.getUserAccountEntriesCount(
				organizationUser2.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B));

		Assert.assertEquals(
			organizationUser2.toString(),
			organizationUserAccountEntries2.size(),
			organizationUserAccountEntriesCount2);

		Assert.assertEquals(
			organizationUser2.toString(), 1,
			organizationUserAccountEntriesCount2);

		Assert.assertEquals(
			organizationUserAccountEntries2.toString(), 1,
			organizationUserAccountEntries2.size());

		AccountEntry organizationUserAccountEntry2 =
			organizationUserAccountEntries2.get(0);

		Assert.assertEquals(
			"businessOrganizationAccount2",
			organizationUserAccountEntry2.getName());
	}

	@Test
	public void testBusinessUserCommerceAccountsVisibility() throws Exception {
		frutillaRule.scenario(
			"Adding new business Commerce Accounts"
		).given(
			"A company"
		).when(
			"The Commerce Accounts are created"
		).and(
			"Users are added to them"
		).then(
			"Check the visibility of that accounts for all the users"
		);

		ServiceContext serviceContext = _getServiceContext();

		for (int i = 1; i < 3; i++) {
			User user = UserTestUtil.addUser(
				_user.getCompanyId(), _user.getUserId(), "businessUser" + i,
				serviceContext.getLocale(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				new long[] {serviceContext.getScopeGroupId()}, serviceContext);

			CommerceAccountTestUtil.addBusinessAccountEntry(
				_user.getUserId(), "businessUserAccount" + i,
				"example@example.com", StringPool.BLANK,
				new long[] {user.getUserId()}, null, serviceContext);
		}

		User businessUser1 = _userLocalService.getUserByScreenName(
			_user.getCompanyId(), "businessUser1");
		User businessUser2 = _userLocalService.getUserByScreenName(
			_user.getCompanyId(), "businessUser2");

		List<AccountEntry> businessUserAccountEntries1 =
			_accountEntryLocalService.getUserAccountEntries(
				businessUser1.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		int businessUserAccountEntriesCount1 =
			_accountEntryLocalService.getUserAccountEntriesCount(
				businessUser1.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B));

		Assert.assertEquals(
			businessUser1.toString(), businessUserAccountEntries1.size(),
			businessUserAccountEntriesCount1);

		Assert.assertEquals(
			businessUser1.toString(), 1, businessUserAccountEntriesCount1);

		AccountEntry businessUserAccountEntry1 =
			businessUserAccountEntries1.get(0);

		Assert.assertEquals(
			"businessUserAccount1", businessUserAccountEntry1.getName());

		List<AccountEntry> businessUserAccountEntries2 =
			_accountEntryLocalService.getUserAccountEntries(
				businessUser2.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		int businessUserAccountEntriesCount2 =
			_accountEntryLocalService.getUserAccountEntriesCount(
				businessUser2.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B));

		Assert.assertEquals(
			businessUser2.toString(), businessUserAccountEntries2.size(),
			businessUserAccountEntriesCount2);

		Assert.assertEquals(
			businessUser2.toString(), 1, businessUserAccountEntriesCount2);

		AccountEntry businessUserAccountEntry2 =
			businessUserAccountEntries2.get(0);

		Assert.assertEquals(
			"businessUserAccount2", businessUserAccountEntry2.getName());
	}

	@Test
	public void testUserCommerceAccountsVisibility() throws Exception {
		frutillaRule.scenario(
			"Adding new business Commerce Accounts"
		).given(
			"A company"
		).when(
			"The Commerce Accounts are created"
		).and(
			"Users are added to them with different criteria"
		).then(
			"Check the visibility of that accounts for all the users"
		);

		_addOrganizationSet("business");

		List<String> externalReferenceCodes = _getExternalReferenceCodes(5);

		ServiceContext serviceContext = _getServiceContext();

		for (int i = 1; i < 6; i++) {
			User user = UserTestUtil.addUser(
				_user.getCompanyId(), _user.getUserId(), "user" + i,
				serviceContext.getLocale(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, serviceContext);

			if (i == 1) {
				CommerceAccountTestUtil.addBusinessAccountEntry(
					user.getUserId(), "account" + i, "example@test.com",
					externalReferenceCodes.get(i - 1), serviceContext);

				continue;
			}

			CommerceAccountTestUtil.addBusinessAccountEntry(
				_user.getUserId(), "account" + i, "example@test.com",
				externalReferenceCodes.get(i - 1), serviceContext);
		}

		Organization liferayOrganization =
			_organizationLocalService.getOrganization(
				_user.getCompanyId(), "businessLiferay");
		Organization italyOrganization =
			_organizationLocalService.getOrganization(
				_user.getCompanyId(), "businessItaly");
		Organization chicagoOrganization =
			_organizationLocalService.getOrganization(
				_user.getCompanyId(), "businessChicago");
		Organization losAngelesOrganization =
			_organizationLocalService.getOrganization(
				_user.getCompanyId(), "businessLosAngeles");

		User user1 = _userLocalService.getUserByScreenName(
			_user.getCompanyId(), "user1");

		User user2 = _userLocalService.getUserByScreenName(
			_user.getCompanyId(), "user2");

		_organizationLocalService.addUserOrganization(
			user2.getUserId(), italyOrganization);
		_organizationLocalService.addUserOrganization(
			user2.getUserId(), chicagoOrganization);

		User user3 = _userLocalService.getUserByScreenName(
			_user.getCompanyId(), "user3");

		_organizationLocalService.addUserOrganization(
			user3.getUserId(), losAngelesOrganization);

		User user4 = _userLocalService.getUserByScreenName(
			_user.getCompanyId(), "user4");

		_organizationLocalService.addUserOrganization(
			user4.getUserId(), chicagoOrganization);

		User user5 = _userLocalService.getUserByScreenName(
			_user.getCompanyId(), "user5");

		_organizationLocalService.addUserOrganization(
			user5.getUserId(), liferayOrganization);

		AccountEntry accountEntry1 =
			_accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCodes.get(0), _user.getCompanyId());

		Assert.assertNotNull(accountEntry1);

		AccountEntry accountEntry2 =
			_accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCodes.get(1), _user.getCompanyId());

		Assert.assertNotNull(accountEntry2);

		AccountEntry accountEntry3 =
			_accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCodes.get(2), _user.getCompanyId());

		Assert.assertNotNull(accountEntry3);

		AccountEntry accountEntry4 =
			_accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCodes.get(3), _user.getCompanyId());

		Assert.assertNotNull(accountEntry4);

		AccountEntry accountEntry5 =
			_accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCodes.get(4), _user.getCompanyId());

		Assert.assertNotNull(accountEntry5);

		CommerceAccountTestUtil.addAccountEntryUserRels(
			accountEntry2.getAccountEntryId(), new long[] {user2.getUserId()},
			serviceContext);
		CommerceAccountTestUtil.addAccountEntryUserRels(
			accountEntry3.getAccountEntryId(), new long[] {user3.getUserId()},
			serviceContext);

		CommerceAccountTestUtil.addAccountEntryOrganizationRels(
			accountEntry2.getAccountEntryId(),
			new long[] {italyOrganization.getOrganizationId()});
		CommerceAccountTestUtil.addAccountEntryOrganizationRels(
			accountEntry3.getAccountEntryId(),
			new long[] {chicagoOrganization.getOrganizationId()});
		CommerceAccountTestUtil.addAccountEntryOrganizationRels(
			accountEntry4.getAccountEntryId(),
			new long[] {losAngelesOrganization.getOrganizationId()});
		CommerceAccountTestUtil.addAccountEntryOrganizationRels(
			accountEntry5.getAccountEntryId(),
			new long[] {losAngelesOrganization.getOrganizationId()});

		List<AccountEntry> userAccountEntries1 =
			_accountEntryLocalService.getUserAccountEntries(
				user1.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		int userAccountEntriesCount1 =
			_accountEntryLocalService.getUserAccountEntriesCount(
				user1.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B));

		Assert.assertEquals(
			userAccountEntries1.toString(), userAccountEntries1.size(),
			userAccountEntriesCount1);

		Assert.assertEquals(
			userAccountEntries1.toString(), 1, userAccountEntriesCount1);

		AccountEntry userAccountEntry1 = userAccountEntries1.get(0);

		Assert.assertEquals(
			accountEntry1.getAccountEntryId(),
			userAccountEntry1.getAccountEntryId());

		List<AccountEntry> userAccountEntries2 =
			_accountEntryLocalService.getUserAccountEntries(
				user2.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		int userAccountEntriesCount2 =
			_accountEntryLocalService.getUserAccountEntriesCount(
				user2.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B));

		Assert.assertEquals(
			userAccountEntries2.toString(), userAccountEntries2.size(),
			userAccountEntriesCount2);

		Assert.assertEquals(
			userAccountEntries2.toString(), 2, userAccountEntriesCount2);

		userAccountEntries2 = ListUtil.sort(userAccountEntries2);

		AccountEntry userAccountEntry2a = userAccountEntries2.get(0);
		AccountEntry userAccountEntry2b = userAccountEntries2.get(1);

		Assert.assertEquals(
			accountEntry2.getAccountEntryId(),
			userAccountEntry2a.getAccountEntryId());
		Assert.assertEquals(
			accountEntry3.getAccountEntryId(),
			userAccountEntry2b.getAccountEntryId());

		List<AccountEntry> userAccountEntries3 =
			_accountEntryLocalService.getUserAccountEntries(
				user3.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		int userAccountEntriesCount3 =
			_accountEntryLocalService.getUserAccountEntriesCount(
				user3.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B));

		Assert.assertEquals(
			userAccountEntries3.toString(), userAccountEntries3.size(),
			userAccountEntriesCount3);

		Assert.assertEquals(
			userAccountEntries3.toString(), 3, userAccountEntriesCount3);

		userAccountEntries3 = ListUtil.sort(userAccountEntries3);

		AccountEntry userAccountEntry3a = userAccountEntries3.get(0);
		AccountEntry userAccountEntry3b = userAccountEntries3.get(1);
		AccountEntry userAccountEntry3c = userAccountEntries3.get(2);

		Assert.assertEquals(
			accountEntry3.getAccountEntryId(),
			userAccountEntry3a.getAccountEntryId());
		Assert.assertEquals(
			accountEntry4.getAccountEntryId(),
			userAccountEntry3b.getAccountEntryId());
		Assert.assertEquals(
			accountEntry5.getAccountEntryId(),
			userAccountEntry3c.getAccountEntryId());

		List<AccountEntry> userAccountEntries4 =
			_accountEntryLocalService.getUserAccountEntries(
				user4.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		int userAccountEntriesCount4 =
			_accountEntryLocalService.getUserAccountEntriesCount(
				user4.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B));

		Assert.assertEquals(
			userAccountEntries4.toString(), userAccountEntries4.size(),
			userAccountEntriesCount4);

		Assert.assertEquals(
			userAccountEntries4.toString(), 1, userAccountEntriesCount4);

		AccountEntry userAccountEntry4 = userAccountEntries4.get(0);

		Assert.assertEquals(
			accountEntry3.getAccountEntryId(),
			userAccountEntry4.getAccountEntryId());

		List<AccountEntry> userAccountEntries5 =
			_accountEntryLocalService.getUserAccountEntries(
				user5.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		int userAccountEntriesCount5 =
			_accountEntryLocalService.getUserAccountEntriesCount(
				user5.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2B));

		Assert.assertEquals(
			userAccountEntries5.toString(), userAccountEntries5.size(),
			userAccountEntriesCount5);

		Assert.assertEquals(
			userAccountEntries5.toString(), 4, userAccountEntriesCount5);

		userAccountEntries5 = ListUtil.sort(userAccountEntries5);

		AccountEntry userAccountEntry5a = userAccountEntries5.get(0);
		AccountEntry userAccountEntry5b = userAccountEntries5.get(1);
		AccountEntry userAccountEntry5c = userAccountEntries5.get(2);
		AccountEntry userAccountEntry5d = userAccountEntries5.get(3);

		Assert.assertEquals(
			accountEntry2.getAccountEntryId(),
			userAccountEntry5a.getAccountEntryId());
		Assert.assertEquals(
			accountEntry3.getAccountEntryId(),
			userAccountEntry5b.getAccountEntryId());
		Assert.assertEquals(
			accountEntry4.getAccountEntryId(),
			userAccountEntry5c.getAccountEntryId());
		Assert.assertEquals(
			accountEntry5.getAccountEntryId(),
			userAccountEntry5d.getAccountEntryId());
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private Organization _addOrganization(
			long parentOrganizationId, String name)
		throws PortalException {

		return _organizationLocalService.addOrganization(
			_user.getUserId(), parentOrganizationId, name, false);
	}

	private void _addOrganizationSet(String baseName) throws Exception {
		try {
			Organization liferayOrganization = _addOrganization(
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				baseName + "Liferay");

			_addOrganization(
				liferayOrganization.getOrganizationId(), baseName + "Italy");

			Organization usaOrganization = _addOrganization(
				liferayOrganization.getOrganizationId(), baseName + "USA");

			_addOrganization(
				usaOrganization.getOrganizationId(), baseName + "Chicago");
			_addOrganization(
				usaOrganization.getOrganizationId(), baseName + "LosAngeles");
		}
		catch (Exception exception) {
			throw new Exception(exception);
		}
	}

	private List<String> _getExternalReferenceCodes(int count) {
		List<String> externalReferenceCodes = new ArrayList<>(count);

		for (int i = 0; i < count; i++) {
			externalReferenceCodes.add(RandomTestUtil.randomString());
		}

		return externalReferenceCodes;
	}

	private ServiceContext _getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_user.getCompanyId(), _user.getGroupId(), _user.getUserId());
	}

	private static User _user;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private CommerceAccountHelper _commerceAccountHelper;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private UserLocalService _userLocalService;

}