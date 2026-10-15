/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.model.listener.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.test.util.AccountEntryArgs;
import com.liferay.account.service.test.util.AccountEntryTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class OrganizationModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDeleteOrganizationWithMultipleAccountEntries()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		List<AccountEntry> accountEntries =
			AccountEntryTestUtil.addAccountEntries(
				2, AccountEntryArgs.withOrganizations(organization));

		List<AccountEntryOrganizationRel> accountEntryOrganizationRels =
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsByOrganizationId(
					organization.getOrganizationId());

		Assert.assertEquals(
			accountEntryOrganizationRels.toString(), accountEntries.size(),
			accountEntryOrganizationRels.size());

		for (AccountEntry accountEntry : accountEntries) {
			_assertSearchAccountEntry(accountEntry, true, organization);
		}

		_organizationLocalService.deleteOrganization(organization);

		accountEntryOrganizationRels =
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsByOrganizationId(
					organization.getOrganizationId());

		Assert.assertTrue(ListUtil.isEmpty(accountEntryOrganizationRels));

		for (AccountEntry accountEntry : accountEntries) {
			_assertSearchAccountEntry(accountEntry, false, organization);
		}
	}

	@Test
	public void testDeleteOrganizationWithSingleAccountEntry()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		AccountEntry accountEntry = AccountEntryTestUtil.addAccountEntry(
			AccountEntryArgs.withOrganizations(organization));

		_assertSearchAccountEntry(accountEntry, true, organization);

		_organizationLocalService.deleteOrganization(organization);

		Assert.assertFalse(
			_accountEntryOrganizationRelLocalService.
				hasAccountEntryOrganizationRel(
					accountEntry.getAccountEntryId(),
					organization.getOrganizationId()));

		_assertSearchAccountEntry(accountEntry, false, organization);
	}

	private void _assertSearchAccountEntry(
		AccountEntry accountEntry, boolean expected,
		Organization organization) {

		BaseModelSearchResult<AccountEntry> baseModelSearchResult =
			_accountEntryLocalService.searchAccountEntries(
				accountEntry.getCompanyId(), null,
				LinkedHashMapBuilder.<String, Object>put(
					"organizationIds",
					new long[] {organization.getOrganizationId()}
				).build(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null, false);

		List<Long> accountEntryIds = ListUtil.toList(
			baseModelSearchResult.getBaseModels(),
			AccountEntry.ACCOUNT_ENTRY_ID_ACCESSOR);

		Assert.assertEquals(
			accountEntryIds.toString(), expected,
			accountEntryIds.contains(accountEntry.getAccountEntryId()));
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

}