/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.test;

import com.liferay.account.exception.DuplicateAccountEntryOrganizationRelException;
import com.liferay.account.exception.NoSuchEntryException;
import com.liferay.account.exception.NoSuchEntryOrganizationRelException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.model.AccountEntryOrganizationRelModel;
import com.liferay.account.retriever.AccountOrganizationRetriever;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.test.util.AccountEntryTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchOrganizationException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class AccountEntryOrganizationRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_accountEntry = AccountEntryTestUtil.addAccountEntry();

		_organization = OrganizationTestUtil.addOrganization();
	}

	@Test
	public void testAddAccountEntryOrganizationRel() throws Exception {
		AccountEntryOrganizationRel accountEntryOrganizationRel =
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					_organization.getOrganizationId());

		_accountEntryOrganizationRels.add(accountEntryOrganizationRel);

		Assert.assertNotNull(accountEntryOrganizationRel);
		Assert.assertNotNull(
			_accountEntryOrganizationRelLocalService.
				fetchAccountEntryOrganizationRel(
					accountEntryOrganizationRel.getPrimaryKey()));
	}

	@Test(expected = DuplicateAccountEntryOrganizationRelException.class)
	public void testAddAccountEntryOrganizationRelThrowsDuplicateAccountEntryOrganizationRelException()
		throws Exception {

		_accountEntryOrganizationRels.add(
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					_organization.getOrganizationId()));

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			_accountEntry.getAccountEntryId(),
			_organization.getOrganizationId());
	}

	@Test(expected = NoSuchEntryException.class)
	public void testAddAccountEntryOrganizationRelThrowsNoSuchEntryException()
		throws Exception {

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			RandomTestUtil.randomLong(), _organization.getOrganizationId());
	}

	@Test(expected = NoSuchOrganizationException.class)
	public void testAddAccountEntryOrganizationRelThrowsNoSuchOrganizationException()
		throws Exception {

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			_accountEntry.getAccountEntryId(), RandomTestUtil.randomLong());
	}

	@Test
	public void testAddAccountEntryOrganizationRels() throws Exception {
		_organizations.add(OrganizationTestUtil.addOrganization());
		_organizations.add(OrganizationTestUtil.addOrganization());

		long[] organizationIds = ListUtil.toLongArray(
			_organizations, Organization.ORGANIZATION_ID_ACCESSOR);

		_accountEntryOrganizationRelLocalService.
			addAccountEntryOrganizationRels(
				_accountEntry.getAccountEntryId(), organizationIds);

		Assert.assertEquals(
			2,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCount(
					_accountEntry.getAccountEntryId()));

		long[] accountEntryOrganizationIds = ListUtil.toLongArray(
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRels(
					_accountEntry.getAccountEntryId()),
			AccountEntryOrganizationRelModel::getOrganizationId);

		Assert.assertTrue(
			ArrayUtil.containsAll(
				organizationIds, accountEntryOrganizationIds));
		Assert.assertTrue(
			ArrayUtil.containsAll(
				accountEntryOrganizationIds, organizationIds));
	}

	@Test
	public void testDeleteAccountEntryOrganizationRel() throws Exception {
		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			_accountEntry.getAccountEntryId(),
			_organization.getOrganizationId());

		Assert.assertTrue(
			_accountEntryOrganizationRelLocalService.
				hasAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					_organization.getOrganizationId()));

		_assertSearchAccountEntries(true);
		_assertSearchAccountOrganizations(true);

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(
				_accountEntry.getAccountEntryId(),
				_organization.getOrganizationId());

		Assert.assertFalse(
			_accountEntryOrganizationRelLocalService.
				hasAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					_organization.getOrganizationId()));

		_assertSearchAccountEntries(false);
		_assertSearchAccountOrganizations(false);

		AccountEntryOrganizationRel accountEntryOrganizationRel =
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					_organization.getOrganizationId());

		_assertSearchAccountEntries(true);
		_assertSearchAccountOrganizations(true);

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(accountEntryOrganizationRel);

		Assert.assertFalse(
			_accountEntryOrganizationRelLocalService.
				hasAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					_organization.getOrganizationId()));

		_assertSearchAccountEntries(false);
		_assertSearchAccountOrganizations(false);

		accountEntryOrganizationRel =
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					_organization.getOrganizationId());

		_assertSearchAccountEntries(true);
		_assertSearchAccountOrganizations(true);

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(
				accountEntryOrganizationRel.getAccountEntryOrganizationRelId());

		Assert.assertFalse(
			_accountEntryOrganizationRelLocalService.
				hasAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					_organization.getOrganizationId()));

		_assertSearchAccountEntries(false);
		_assertSearchAccountOrganizations(false);
	}

	@Test(expected = NoSuchEntryOrganizationRelException.class)
	public void testDeleteAccountEntryOrganizationRelThrowsNoSuchEntryOrganizationRelException()
		throws Exception {

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(
				_accountEntry.getAccountEntryId(),
				_organization.getOrganizationId());
	}

	@Test
	public void testDeleteAccountEntryOrganizationRels() throws Exception {
		_organizations.add(OrganizationTestUtil.addOrganization());
		_organizations.add(OrganizationTestUtil.addOrganization());

		long[] organizationIds = ListUtil.toLongArray(
			_organizations, Organization.ORGANIZATION_ID_ACCESSOR);

		_accountEntryOrganizationRelLocalService.
			addAccountEntryOrganizationRels(
				_accountEntry.getAccountEntryId(), organizationIds);

		Assert.assertEquals(
			2,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCount(
					_accountEntry.getAccountEntryId()));

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRels(
				_accountEntry.getAccountEntryId(), organizationIds);

		Assert.assertEquals(
			0,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCount(
					_accountEntry.getAccountEntryId()));
	}

	@Test
	public void testDeleteAccountEntryOrganizationRelsByAccountEntryId()
		throws Exception {

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			_accountEntry.getAccountEntryId(),
			_organization.getOrganizationId());

		_assertSearchAccountEntries(true);
		_assertSearchAccountOrganizations(true);

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRelsByAccountEntryId(
				_accountEntry.getAccountEntryId());

		Assert.assertFalse(
			_accountEntryOrganizationRelLocalService.
				hasAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					_organization.getOrganizationId()));

		_assertSearchAccountEntries(false);
		_assertSearchAccountOrganizations(false);
	}

	@Test
	public void testDeleteAccountEntryOrganizationRelsByOrganizationId()
		throws Exception {

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			_accountEntry.getAccountEntryId(),
			_organization.getOrganizationId());

		_assertSearchAccountEntries(true);
		_assertSearchAccountOrganizations(true);

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRelsByOrganizationId(
				_organization.getOrganizationId());

		Assert.assertFalse(
			_accountEntryOrganizationRelLocalService.
				hasAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					_organization.getOrganizationId()));

		_assertSearchAccountEntries(false);
		_assertSearchAccountOrganizations(false);
	}

	@Test
	public void testGetAccountEntryOrganizationRelsByOrganizationId()
		throws Exception {

		_accountEntries.add(AccountEntryTestUtil.addAccountEntry());
		_accountEntries.add(AccountEntryTestUtil.addAccountEntry());

		long[] expectedAccountEntryIds = ListUtil.toLongArray(
			_accountEntries, AccountEntry.ACCOUNT_ENTRY_ID_ACCESSOR);

		for (long accountEntryId : expectedAccountEntryIds) {
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					accountEntryId, _organization.getOrganizationId());
		}

		List<AccountEntryOrganizationRel> accountEntryOrganizationRels =
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsByOrganizationId(
					_organization.getOrganizationId());

		long[] accountEntryIds = ListUtil.toLongArray(
			accountEntryOrganizationRels,
			AccountEntryOrganizationRelModel::getAccountEntryId);

		Assert.assertTrue(
			ArrayUtil.containsAll(expectedAccountEntryIds, accountEntryIds));
		Assert.assertTrue(
			ArrayUtil.containsAll(accountEntryIds, expectedAccountEntryIds));
	}

	@Test
	public void testSetAccountEntryOrganizationRels() throws Exception {
		for (int i = 0; i < 10; i++) {
			_organizations.add(OrganizationTestUtil.addOrganization());
		}

		_testSetAccountEntryOrganizationRels(_organizations.subList(0, 4));
		_testSetAccountEntryOrganizationRels(_organizations.subList(5, 9));
		_testSetAccountEntryOrganizationRels(_organizations.subList(3, 7));
	}

	private void _assertSearchAccountEntries(boolean expected) {
		BaseModelSearchResult<AccountEntry> baseModelSearchResult =
			_accountEntryLocalService.searchAccountEntries(
				_accountEntry.getCompanyId(), null,
				LinkedHashMapBuilder.<String, Object>put(
					"organizationIds",
					new long[] {_organization.getOrganizationId()}
				).build(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null, false);

		List<Long> accountEntryIds = ListUtil.toList(
			baseModelSearchResult.getBaseModels(),
			AccountEntry.ACCOUNT_ENTRY_ID_ACCESSOR);

		Assert.assertEquals(
			accountEntryIds.toString(), expected,
			accountEntryIds.contains(_accountEntry.getAccountEntryId()));
	}

	private void _assertSearchAccountOrganizations(boolean expected)
		throws Exception {

		BaseModelSearchResult<Organization> baseModelSearchResult =
			_accountOrganizationRetriever.searchAccountOrganizations(
				_accountEntry.getAccountEntryId(), null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, "organizationId", false);

		List<Long> organizationIds = ListUtil.toList(
			baseModelSearchResult.getBaseModels(),
			Organization.ORGANIZATION_ID_ACCESSOR);

		Assert.assertEquals(
			organizationIds.toString(), expected,
			organizationIds.contains(_organization.getOrganizationId()));
	}

	private void _testSetAccountEntryOrganizationRels(
			List<Organization> organizations)
		throws Exception {

		_accountEntryOrganizationRelLocalService.
			setAccountEntryOrganizationRels(
				_accountEntry.getAccountEntryId(),
				ListUtil.toLongArray(
					organizations, Organization.ORGANIZATION_ID_ACCESSOR));

		List<Long> expectedOrganizationIds = ListUtil.toList(
			organizations, Organization.ORGANIZATION_ID_ACCESSOR);
		List<Long> actualOrganizationIds = ListUtil.toList(
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRels(
					_accountEntry.getAccountEntryId()),
			AccountEntryOrganizationRel::getOrganizationId);

		Assert.assertEquals(
			actualOrganizationIds.toString(), expectedOrganizationIds.size(),
			actualOrganizationIds.size());

		Assert.assertTrue(
			expectedOrganizationIds.containsAll(actualOrganizationIds));
	}

	@DeleteAfterTestRun
	private final List<AccountEntry> _accountEntries = new ArrayList<>();

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@DeleteAfterTestRun
	private final List<AccountEntryOrganizationRel>
		_accountEntryOrganizationRels = new ArrayList<>();

	@Inject
	private AccountOrganizationRetriever _accountOrganizationRetriever;

	@DeleteAfterTestRun
	private Organization _organization;

	@DeleteAfterTestRun
	private final List<Organization> _organizations = new ArrayList<>();

}