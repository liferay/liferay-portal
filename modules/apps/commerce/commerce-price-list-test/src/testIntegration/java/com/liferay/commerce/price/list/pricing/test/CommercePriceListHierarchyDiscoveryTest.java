/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.pricing.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.discovery.CommercePriceListDiscovery;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.price.list.CommercePriceListTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Riccardo Alberti
 */
@RunWith(Arquillian.class)
public class CommercePriceListHierarchyDiscoveryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_commerceCurrency1 = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());
		_commerceCurrency2 = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_accountEntry1 = CommerceAccountTestUtil.getPersonAccountEntry(
			_user.getUserId());

		CommerceAccountTestUtil.addAccountGroupAndAccountRel(
			_group.getCompanyId(), RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_GROUP_TYPE_STATIC,
			_accountEntry1.getAccountEntryId(), _serviceContext);

		_commerceChannel1 = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency1.getCode());

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			null, RandomTestUtil.randomString(), _commerceCurrency1.getCode(),
			LocaleUtil.US.getDisplayLanguage(), _serviceContext);

		_commercePriceList1 = CommercePriceListTestUtil.addCommercePriceList(
			_commerceCatalog.getGroupId(), false, _TYPE, 1.0);
		_commercePriceList2 = CommercePriceListTestUtil.addCommercePriceList(
			_commerceCatalog.getGroupId(), false, _TYPE, 1.0);
		_commercePriceList3 = CommercePriceListTestUtil.addCommercePriceList(
			_commerceCatalog.getGroupId(), false, _TYPE, 1.0);
		_commercePriceList4 = CommercePriceListTestUtil.addCommercePriceList(
			_commerceCatalog.getGroupId(), false, _TYPE, 1.0);
		_commercePriceList5 = CommercePriceListTestUtil.addCommercePriceList(
			_commerceCatalog.getGroupId(), false, _TYPE, 1.0);
		_commercePriceList6 = CommercePriceListTestUtil.addCommercePriceList(
			_commerceCatalog.getGroupId(), false, _commerceCurrency2.getCode(),
			_TYPE, 0);

		_accountEntry2 = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), "Business Account1", "example1@email.com",
			_serviceContext);
		_accountEntry3 = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), "Business Account2", "example1@email.com",
			_serviceContext);
		_accountEntry4 = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), "Business Account3", "example1@email.com",
			_serviceContext);
		_accountEntry5 = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), "Business Account4", "example1@email.com",
			_serviceContext);
		_accountEntry6 = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), "Business Account5", "example1@email.com",
			_serviceContext);
		_accountEntry7 = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), "Business Account6", "example1@email.com",
			_serviceContext);

		CommerceAccountTestUtil.addAccountGroupAndAccountRel(
			_group.getCompanyId(), RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_GROUP_TYPE_STATIC,
			_accountEntry4.getAccountEntryId(), _serviceContext);
		CommerceAccountTestUtil.addAccountGroupAndAccountRel(
			_group.getCompanyId(), RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_GROUP_TYPE_STATIC,
			_accountEntry5.getAccountEntryId(), _serviceContext);

		_commerceChannel2 = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency1.getCode());
		_commerceChannel3 = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency1.getCode());
		_commerceChannel4 = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency1.getCode());
		_commerceChannel5 = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency1.getCode());

		long[] commerceAccount3AccountGroups =
			_accountGroupLocalService.getAccountGroupIds(
				_accountEntry4.getAccountEntryId());
		long[] commerceAccount4AccountGroups =
			_accountGroupLocalService.getAccountGroupIds(
				_accountEntry5.getAccountEntryId());

		CommercePriceListTestUtil.addAccountToPriceList(
			_commerceCatalog.getGroupId(), _accountEntry2.getAccountEntryId(),
			_commercePriceList1.getCommercePriceListId());
		CommercePriceListTestUtil.addAccountToPriceList(
			_commerceCatalog.getGroupId(),
			AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
			_commercePriceList1.getCommercePriceListId());
		CommercePriceListTestUtil.addChannelToPriceList(
			_commerceCatalog.getGroupId(),
			_commerceChannel2.getCommerceChannelId(),
			_commercePriceList1.getCommercePriceListId());
		CommercePriceListTestUtil.addAccountToPriceList(
			_commerceCatalog.getGroupId(), _accountEntry3.getAccountEntryId(),
			_commercePriceList1.getCommercePriceListId());

		CommercePriceListTestUtil.addAccountGroupsToPriceList(
			_commerceCatalog.getGroupId(), commerceAccount3AccountGroups,
			_commercePriceList2.getCommercePriceListId());
		CommercePriceListTestUtil.addChannelToPriceList(
			_commerceCatalog.getGroupId(),
			_commerceChannel3.getCommerceChannelId(),
			_commercePriceList2.getCommercePriceListId());

		CommercePriceListTestUtil.addChannelToPriceList(
			_commerceCatalog.getGroupId(),
			_commerceChannel2.getCommerceChannelId(),
			_commercePriceList3.getCommercePriceListId());
		CommercePriceListTestUtil.addAccountGroupsToPriceList(
			_commerceCatalog.getGroupId(), commerceAccount4AccountGroups,
			_commercePriceList3.getCommercePriceListId());

		CommercePriceListTestUtil.addChannelToPriceList(
			_commerceCatalog.getGroupId(),
			_commerceChannel4.getCommerceChannelId(),
			_commercePriceList4.getCommercePriceListId());
	}

	@After
	public void tearDown() throws Exception {
		List<CommercePriceList> commercePriceLists =
			_commercePriceListLocalService.getCommercePriceLists(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CommercePriceList commercePriceList : commercePriceLists) {
			if (commercePriceList.getCompanyId() != _group.getCompanyId()) {
				continue;
			}

			_commercePriceListLocalService.forceDeleteCommercePriceList(
				commercePriceList);
		}
	}

	@Test
	public void testCreateCatalogBasePriceList() throws Exception {
		frutillaRule.scenario(
			"A price list is created and set as base pricelist for a catalog"
		).given(
			"A catalog"
		).when(
			"A price list is created with flag catalog base price list true"
		).then(
			"The price list is the default price list of the catalog"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency1.getCode(),
				LocaleUtil.US.getDisplayLanguage(), _serviceContext);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		CommercePriceList discoveredPriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				commerceCatalog.getGroupId(), 0, 0, 0, null, null, _TYPE,
				StringPool.BLANK);

		Assert.assertEquals(
			commercePriceList.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());
	}

	@Test
	public void testRetrieveCorrectPriceListByHierarchy() throws Exception {
		frutillaRule.scenario(
			"When multiple price list are defined for the same catalog the " +
				"highest in the hierarchy shall be taken"
		).given(
			"A catalog with multiple price lists"
		).when(
			"The price list is discovered"
		).then(
			"The price list with highest rank is retrieved"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency1.getCode(),
				LocaleUtil.US.getDisplayLanguage(), _serviceContext);

		CommercePriceList commerceUnqualifiedPriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), false, _TYPE, 1.0);

		CommercePriceList discoveredPriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				commerceCatalog.getGroupId(),
				_accountEntry1.getAccountEntryId(),
				_commerceChannel1.getCommerceChannelId(), 0, null, null, _TYPE,
				StringPool.BLANK);

		Assert.assertEquals(
			commerceUnqualifiedPriceList.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());

		CommercePriceList commerceChannelPriceList =
			CommercePriceListTestUtil.addChannelPriceList(
				commerceCatalog.getGroupId(),
				_commerceChannel1.getCommerceChannelId(), _TYPE);

		discoveredPriceList = _commercePriceListDiscovery.getCommercePriceList(
			commerceCatalog.getGroupId(), _accountEntry1.getAccountEntryId(),
			_commerceChannel1.getCommerceChannelId(), 0, null, null, _TYPE,
			StringPool.BLANK);

		Assert.assertEquals(
			commerceChannelPriceList.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());

		long[] commerceAccountGroupIds =
			_accountGroupLocalService.getAccountGroupIds(
				_accountEntry1.getAccountEntryId());

		CommercePriceList commerceAccountGroupPriceList =
			CommercePriceListTestUtil.addAccountGroupPriceList(
				commerceCatalog.getGroupId(), commerceAccountGroupIds, _TYPE);

		discoveredPriceList = _commercePriceListDiscovery.getCommercePriceList(
			commerceCatalog.getGroupId(), _accountEntry1.getAccountEntryId(),
			_commerceChannel1.getCommerceChannelId(), 0, null, null, _TYPE,
			StringPool.BLANK);

		Assert.assertEquals(
			commerceAccountGroupPriceList.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());

		CommercePriceList commerceAccountGroupAndChannelPriceList =
			CommercePriceListTestUtil.addAccountGroupAndChannelPriceList(
				commerceCatalog.getGroupId(), commerceAccountGroupIds,
				_commerceChannel1.getCommerceChannelId(), _TYPE);

		discoveredPriceList = _commercePriceListDiscovery.getCommercePriceList(
			commerceCatalog.getGroupId(), _accountEntry1.getAccountEntryId(),
			_commerceChannel1.getCommerceChannelId(), 0, null, null, _TYPE,
			StringPool.BLANK);

		Assert.assertEquals(
			commerceAccountGroupAndChannelPriceList.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());

		CommercePriceList commerceAccountPriceList =
			CommercePriceListTestUtil.addAccountPriceList(
				commerceCatalog.getGroupId(),
				_accountEntry1.getAccountEntryId(), _TYPE);

		discoveredPriceList = _commercePriceListDiscovery.getCommercePriceList(
			commerceCatalog.getGroupId(), _accountEntry1.getAccountEntryId(),
			_commerceChannel1.getCommerceChannelId(), 0, null, null, _TYPE,
			StringPool.BLANK);

		Assert.assertEquals(
			commerceAccountPriceList.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());

		CommercePriceList commerceAccountAndChannelPriceList =
			CommercePriceListTestUtil.addAccountAndChannelPriceList(
				commerceCatalog.getGroupId(),
				_accountEntry1.getAccountEntryId(),
				_commerceChannel1.getCommerceChannelId(), _TYPE);

		discoveredPriceList = _commercePriceListDiscovery.getCommercePriceList(
			commerceCatalog.getGroupId(), _accountEntry1.getAccountEntryId(),
			_commerceChannel1.getCommerceChannelId(), 0, null, null, _TYPE,
			StringPool.BLANK);

		Assert.assertEquals(
			commerceAccountAndChannelPriceList.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());
	}

	@Test
	public void testRetrieveCorrectPriceListByHierarchy1() throws Exception {
		frutillaRule.scenario(
			"A price list is qualified by an account and a channel is not " +
				"applicable to the same account on another channel"
		).given(
			"A catalog with a price list on account and channel"
		).when(
			"The price list is discovered given a different channel"
		).then(
			"Only the catalog base price list is returned"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency1.getCode(),
				LocaleUtil.US.getDisplayLanguage(), _serviceContext);

		CommercePriceListTestUtil.addAccountAndChannelPriceList(
			commerceCatalog.getGroupId(), _accountEntry1.getAccountEntryId(),
			_commerceChannel1.getCommerceChannelId(), _TYPE);

		CommercePriceList discoveredPriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				commerceCatalog.getGroupId(),
				_accountEntry1.getAccountEntryId(), RandomTestUtil.nextLong(),
				0, null, null, _TYPE, StringPool.BLANK);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		Assert.assertEquals(
			commercePriceList.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());
	}

	@Test
	public void testRetrieveCorrectPriceListByHierarchy2() throws Exception {
		frutillaRule.scenario(
			"A price list is qualified by an account group and a channel is " +
				"not applicable to the same account on another channel"
		).given(
			"A catalog with a price list on account group and channel"
		).when(
			"The price list is discovered given a different channel"
		).then(
			"Only the catalog base price list is returned"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency1.getCode(),
				LocaleUtil.US.getDisplayLanguage(), _serviceContext);

		CommercePriceListTestUtil.addAccountGroupAndChannelPriceList(
			commerceCatalog.getGroupId(),
			_accountGroupLocalService.getAccountGroupIds(
				_accountEntry1.getAccountEntryId()),
			_commerceChannel1.getCommerceChannelId(), _TYPE);

		CommercePriceList discoveredPriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				commerceCatalog.getGroupId(),
				_accountEntry1.getAccountEntryId(), RandomTestUtil.nextLong(),
				0, null, null, _TYPE, StringPool.BLANK);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		Assert.assertEquals(
			commercePriceList.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());
	}

	@Test
	public void testRetrievePriceListForAccount() throws Exception {
		frutillaRule.scenario(
			"When multiple price list are defined for the same catalog the " +
				"highest in the hierarchy shall be taken"
		).given(
			"A catalog with multiple price lists"
		).when(
			"The price list is discovered"
		).then(
			"The price list associated to account is retrieved"
		);

		CommercePriceList discoveredPriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry3.getAccountEntryId(),
				_commerceChannel2.getCommerceChannelId(), 0, null, null, _TYPE,
				StringPool.BLANK);

		Assert.assertEquals(
			_commercePriceList1.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());
	}

	@Test
	public void testRetrievePriceListForAccountGroups() throws Exception {
		frutillaRule.scenario(
			"When multiple price list are defined for the same catalog the " +
				"highest in the hierarchy shall be taken"
		).given(
			"A catalog with multiple price lists"
		).when(
			"The price list is discovered"
		).then(
			"The price list associated to account group is retrieved"
		);

		CommercePriceList discoveredPriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry5.getAccountEntryId(),
				_commerceChannel2.getCommerceChannelId(), 0, null, null, _TYPE,
				StringPool.BLANK);

		Assert.assertEquals(
			_commercePriceList3.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());
	}

	@Test
	public void testRetrievePriceListForAccountGroupsPlusChannel()
		throws Exception {

		frutillaRule.scenario(
			"When multiple price list are defined for the same catalog the " +
				"highest in the hierarchy shall be taken"
		).given(
			"A catalog with multiple price lists"
		).when(
			"The price list is discovered"
		).then(
			"The price list associated to account group and channel is " +
				"retrieved"
		);

		CommercePriceList discoveredPriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry4.getAccountEntryId(),
				_commerceChannel3.getCommerceChannelId(), 0, null, null, _TYPE,
				StringPool.BLANK);

		Assert.assertEquals(
			_commercePriceList2.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());
	}

	@Test
	public void testRetrievePriceListForAccountPlusChannel() throws Exception {
		frutillaRule.scenario(
			"When multiple price list are defined for the same catalog the " +
				"highest in the hierarchy shall be taken"
		).given(
			"A catalog with multiple price lists"
		).when(
			"The price list is discovered"
		).then(
			"The price list associated to account and channel is retrieved"
		);

		CommercePriceList discoveredPriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry2.getAccountEntryId(),
				_commerceChannel2.getCommerceChannelId(), 0, null, null, _TYPE,
				StringPool.BLANK);

		Assert.assertEquals(
			_commercePriceList1.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());
	}

	@Test
	public void testRetrievePriceListForChannel() throws Exception {
		frutillaRule.scenario(
			"When multiple price list are defined for the same catalog the " +
				"highest in the hierarchy shall be taken"
		).given(
			"A catalog with multiple price lists"
		).when(
			"The price list is discovered"
		).then(
			"The price list associated to channel is retrieved"
		);

		CommercePriceList discoveredPriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry6.getAccountEntryId(),
				_commerceChannel4.getCommerceChannelId(), 0, null, null, _TYPE,
				StringPool.BLANK);

		Assert.assertEquals(
			_commercePriceList4.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());
	}

	@Test
	public void testRetrievePriceListForGuestAccount() throws Exception {
		frutillaRule.scenario(
			"When multiple price list are defined for the same catalog the " +
				"highest in the hierarchy shall be taken"
		).given(
			"A catalog with multiple price lists"
		).when(
			"The price list is discovered"
		).then(
			"The price list associated to the guest account is retrieved"
		);

		CommercePriceList discoveredPriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
				_commerceChannel2.getCommerceChannelId(), 0, null, null, _TYPE,
				StringPool.BLANK);

		Assert.assertEquals(
			_commercePriceList1.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());
	}

	@Test
	public void testRetrievePriceListUnqualified() throws Exception {
		frutillaRule.scenario(
			"When multiple price list are defined for the same catalog the " +
				"highest in the hierarchy shall be taken"
		).given(
			"A catalog with multiple price lists"
		).when(
			"The price list is discovered"
		).then(
			"The unqualified price list is retrieved"
		);

		CommercePriceList discoveredPriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry7.getAccountEntryId(),
				_commerceChannel5.getCommerceChannelId(), 0, null, null, _TYPE,
				StringPool.BLANK);

		Assert.assertEquals(
			_commercePriceList5.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());
	}

	@Test
	public void testRetrievePriceListWithEligibleCurrency() throws Exception {
		frutillaRule.scenario(
			"When multiple price list are defined for the same catalog the " +
				"eligible currency is taken"
		).given(
			"A catalog with multiple price lists with different currency"
		).when(
			"The price list is discovered"
		).then(
			"The price list is retrieved with the eligible currency"
		);

		CommercePriceList discoveredPriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry7.getAccountEntryId(),
				_commerceChannel5.getCommerceChannelId(), 0, null, null, _TYPE,
				StringPool.BLANK);

		Assert.assertEquals(
			_commercePriceList5.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());

		discoveredPriceList = _commercePriceListDiscovery.getCommercePriceList(
			_commerceCatalog.getGroupId(), _accountEntry7.getAccountEntryId(),
			_commerceChannel5.getCommerceChannelId(), 0, null,
			_commerceCurrency2.getCode(), _TYPE, StringPool.BLANK);

		Assert.assertEquals(
			_commercePriceList6.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CommerceCurrency.class.getName(),
			_commerceCurrency1.getCommerceCurrencyId(),
			_commerceChannel5.getCommerceChannelId(), _serviceContext);

		discoveredPriceList = _commercePriceListDiscovery.getCommercePriceList(
			_commerceCatalog.getGroupId(), _accountEntry7.getAccountEntryId(),
			_commerceChannel5.getCommerceChannelId(), 0, null,
			_commerceCurrency2.getCode(), _TYPE, StringPool.BLANK);

		Assert.assertNull(discoveredPriceList);

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CommerceCurrency.class.getName(),
			_commerceCurrency2.getCommerceCurrencyId(),
			_commerceChannel5.getCommerceChannelId(), _serviceContext);

		discoveredPriceList = _commercePriceListDiscovery.getCommercePriceList(
			_commerceCatalog.getGroupId(), _accountEntry7.getAccountEntryId(),
			_commerceChannel5.getCommerceChannelId(), 0, null,
			_commerceCurrency2.getCode(), _TYPE, StringPool.BLANK);

		Assert.assertEquals(
			_commercePriceList6.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());

		_commerceChannelRelLocalService.deleteCommerceChannelRels(
			CommerceCurrency.class.getName(),
			_commerceCurrency1.getCommerceCurrencyId());
		_commerceChannelRelLocalService.deleteCommerceChannelRels(
			CommerceCurrency.class.getName(),
			_commerceCurrency2.getCommerceCurrencyId());
	}

	@Test
	public void testRetrievePriceListWithMultipleCurrency() throws Exception {
		frutillaRule.scenario(
			"When multiple price list are defined for the same catalog the " +
				"correct currency is taken"
		).given(
			"A catalog with multiple price lists with different currency"
		).when(
			"The price list is discovered"
		).then(
			"The price list is retrieved with the provided currency"
		);

		CommercePriceList discoveredPriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry7.getAccountEntryId(),
				_commerceChannel5.getCommerceChannelId(), 0, null, null, _TYPE,
				StringPool.BLANK);

		Assert.assertEquals(
			_commercePriceList5.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());

		discoveredPriceList = _commercePriceListDiscovery.getCommercePriceList(
			_commerceCatalog.getGroupId(), _accountEntry7.getAccountEntryId(),
			_commerceChannel5.getCommerceChannelId(), 0, null,
			_commerceCurrency2.getCode(), _TYPE, StringPool.BLANK);

		Assert.assertEquals(
			_commercePriceList6.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private static final String _TYPE =
		CommercePriceListConstants.TYPE_PRICE_LIST;

	private AccountEntry _accountEntry1;
	private AccountEntry _accountEntry2;
	private AccountEntry _accountEntry3;
	private AccountEntry _accountEntry4;
	private AccountEntry _accountEntry5;
	private AccountEntry _accountEntry6;
	private AccountEntry _accountEntry7;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private CommerceChannel _commerceChannel1;
	private CommerceChannel _commerceChannel2;
	private CommerceChannel _commerceChannel3;
	private CommerceChannel _commerceChannel4;
	private CommerceChannel _commerceChannel5;

	@Inject
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	private CommerceCurrency _commerceCurrency1;
	private CommerceCurrency _commerceCurrency2;
	private CommercePriceList _commercePriceList1;
	private CommercePriceList _commercePriceList2;
	private CommercePriceList _commercePriceList3;
	private CommercePriceList _commercePriceList4;
	private CommercePriceList _commercePriceList5;
	private CommercePriceList _commercePriceList6;

	@Inject(
		filter = "component.name=com.liferay.commerce.price.list.internal.discovery.CommercePriceListHierarchyDiscoveryImpl"
	)
	private CommercePriceListDiscovery _commercePriceListDiscovery;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	private Group _group;
	private ServiceContext _serviceContext;
	private User _user;

}