/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.pricing.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelLocalServiceUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.discovery.CommercePriceListDiscovery;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.pricing.constants.CommercePriceModifierConstants;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.service.CommercePriceModifierLocalService;
import com.liferay.commerce.pricing.service.CommercePriceModifierRelLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.price.list.CommercePriceEntryTestUtil;
import com.liferay.commerce.test.util.price.list.CommercePriceListTestUtil;
import com.liferay.commerce.test.util.pricing.CommercePriceModifierTestUtil;
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
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

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
public class CommercePriceListLowestDiscoveryTest {

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

		_accountEntry = CommerceAccountTestUtil.getPersonAccountEntry(
			_user.getUserId());

		_accountGroup = _accountGroupLocalService.addAccountGroup(
			StringPool.BLANK, _serviceContext.getUserId(), null,
			RandomTestUtil.randomString(), _serviceContext);

		_accountGroup.setDefaultAccountGroup(false);
		_accountGroup.setType(AccountConstants.ACCOUNT_GROUP_TYPE_STATIC);
		_accountGroup.setExpandoBridgeAttributes(_serviceContext);

		_accountGroup = _accountGroupLocalService.updateAccountGroup(
			_accountGroup);

		AccountGroupRelLocalServiceUtil.addAccountGroupRel(
			_accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			_accountEntry.getAccountEntryId());

		_commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId(),
			_commerceCurrency1.getCode());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency1.getCode());
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
	public void testRetrieveCorrectPriceListByCurrency() throws Exception {
		frutillaRule.scenario(
			"When multiple price list are defined for the same catalog the " +
				"price list that provides the correct currency should be taken"
		).given(
			"A catalog with multiple price lists and one product"
		).when(
			"The price list is discovered"
		).then(
			"The price list that gives the correct currency is retrieved"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CommercePriceList commerceUnqualifiedPriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				_commerceCatalog.getGroupId(), false, _TYPE, 1.0);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commerceUnqualifiedPriceList.getCommercePriceListId(),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()));

		CommercePriceList discoveredCommercePriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0,
				cpInstance.getCPInstanceUuid(), null, _TYPE, StringPool.BLANK);

		Assert.assertEquals(
			commerceUnqualifiedPriceList.getCommercePriceListId(),
			discoveredCommercePriceList.getCommercePriceListId());

		CommercePriceList commerceCurrencyPriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				_commerceCatalog.getGroupId(), false,
				_commerceCurrency2.getCode(), _TYPE, 0);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commerceCurrencyPriceList.getCommercePriceListId(),
			commercePriceEntry.getPrice());

		discoveredCommercePriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0,
				cpInstance.getCPInstanceUuid(), _commerceCurrency2.getCode(),
				_TYPE, StringPool.BLANK);

		Assert.assertEquals(
			commerceCurrencyPriceList.getCommercePriceListId(),
			discoveredCommercePriceList.getCommercePriceListId());
	}

	@Test
	public void testRetrieveCorrectPriceListByEligibleCurrency()
		throws Exception {

		frutillaRule.scenario(
			"When multiple price list are defined for the same catalog the " +
				"price list that provides the eligible currency should be taken"
		).given(
			"A catalog with multiple price lists and one product"
		).when(
			"The price list is discovered"
		).then(
			"The price list that gives the eligible currency is retrieved"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CommercePriceList commerceUnqualifiedPriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				_commerceCatalog.getGroupId(), false, _TYPE, 1.0);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commerceUnqualifiedPriceList.getCommercePriceListId(),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()));

		CommercePriceList discoveredCommercePriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0,
				cpInstance.getCPInstanceUuid(), null, _TYPE, StringPool.BLANK);

		Assert.assertEquals(
			commerceUnqualifiedPriceList.getCommercePriceListId(),
			discoveredCommercePriceList.getCommercePriceListId());

		CommercePriceList commerceCurrencyPriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				_commerceCatalog.getGroupId(), false,
				_commerceCurrency2.getCode(), _TYPE, 0);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commerceCurrencyPriceList.getCommercePriceListId(),
			commercePriceEntry.getPrice());

		discoveredCommercePriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0,
				cpInstance.getCPInstanceUuid(), _commerceCurrency2.getCode(),
				_TYPE, StringPool.BLANK);

		Assert.assertEquals(
			commerceCurrencyPriceList.getCommercePriceListId(),
			discoveredCommercePriceList.getCommercePriceListId());

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CommerceCurrency.class.getName(),
			_commerceCurrency1.getCommerceCurrencyId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);

		discoveredCommercePriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0,
				cpInstance.getCPInstanceUuid(), _commerceCurrency2.getCode(),
				_TYPE, StringPool.BLANK);

		Assert.assertNull(discoveredCommercePriceList);

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CommerceCurrency.class.getName(),
			_commerceCurrency2.getCommerceCurrencyId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);

		discoveredCommercePriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0,
				cpInstance.getCPInstanceUuid(), _commerceCurrency2.getCode(),
				_TYPE, StringPool.BLANK);

		Assert.assertEquals(
			commerceCurrencyPriceList.getCommercePriceListId(),
			discoveredCommercePriceList.getCommercePriceListId());

		_commerceChannelRelLocalService.deleteCommerceChannelRels(
			CommerceCurrency.class.getName(),
			_commerceCurrency1.getCommerceCurrencyId());
		_commerceChannelRelLocalService.deleteCommerceChannelRels(
			CommerceCurrency.class.getName(),
			_commerceCurrency2.getCommerceCurrencyId());
	}

	@Test
	public void testRetrieveCorrectPriceListByLowestEntry() throws Exception {
		frutillaRule.scenario(
			"When multiple price list are defined for the same catalog the " +
				"price list that provides the lowest price entry shall be taken"
		).given(
			"A catalog with multiple price lists and one product"
		).when(
			"The price list is discovered"
		).then(
			"The price list that gives the lowest price is retrieved"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CommercePriceList commerceUnqualifiedPriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				_commerceCatalog.getGroupId(), false, _TYPE, 1.0);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commerceUnqualifiedPriceList.getCommercePriceListId(),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()));

		BigDecimal lowestPrice = commercePriceEntry.getPrice();

		CommercePriceList expectedPriceList = commerceUnqualifiedPriceList;

		CommercePriceList discoveredCommercePriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0,
				cpInstance.getCPInstanceUuid(), null, _TYPE, StringPool.BLANK);

		Assert.assertEquals(
			expectedPriceList.getCommercePriceListId(),
			discoveredCommercePriceList.getCommercePriceListId());

		CommercePriceList commerceChannelPriceList =
			CommercePriceListTestUtil.addChannelPriceList(
				_commerceCatalog.getGroupId(),
				_commerceChannel.getCommerceChannelId(), _TYPE);

		commercePriceEntry = CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commerceChannelPriceList.getCommercePriceListId(),
			BigDecimal.valueOf(RandomTestUtil.randomDouble()));

		if (lowestPrice.compareTo(commercePriceEntry.getPrice()) > 0) {
			lowestPrice = commercePriceEntry.getPrice();
			expectedPriceList = commerceChannelPriceList;
		}

		discoveredCommercePriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0,
				cpInstance.getCPInstanceUuid(), null, _TYPE, StringPool.BLANK);

		Assert.assertEquals(
			expectedPriceList.getCommercePriceListId(),
			discoveredCommercePriceList.getCommercePriceListId());

		long[] commerceAccountGroupIds =
			_accountGroupLocalService.getAccountGroupIds(
				_accountEntry.getAccountEntryId());

		CommercePriceList commerceAccountGroupPriceList =
			CommercePriceListTestUtil.addAccountGroupPriceList(
				_commerceCatalog.getGroupId(), commerceAccountGroupIds, _TYPE);

		commercePriceEntry = CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commerceAccountGroupPriceList.getCommercePriceListId(),
			BigDecimal.valueOf(RandomTestUtil.randomDouble()));

		if (lowestPrice.compareTo(commercePriceEntry.getPrice()) > 0) {
			lowestPrice = commercePriceEntry.getPrice();
			expectedPriceList = commerceAccountGroupPriceList;
		}

		discoveredCommercePriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0,
				cpInstance.getCPInstanceUuid(), null, _TYPE, StringPool.BLANK);

		Assert.assertEquals(
			expectedPriceList.getCommercePriceListId(),
			discoveredCommercePriceList.getCommercePriceListId());

		CommercePriceList commerceAccountGroupAndChannelPriceList =
			CommercePriceListTestUtil.addAccountGroupAndChannelPriceList(
				_commerceCatalog.getGroupId(), commerceAccountGroupIds,
				_commerceChannel.getCommerceChannelId(), _TYPE);

		commercePriceEntry = CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commerceAccountGroupAndChannelPriceList.getCommercePriceListId(),
			BigDecimal.valueOf(RandomTestUtil.randomDouble()));

		if (lowestPrice.compareTo(commercePriceEntry.getPrice()) > 0) {
			lowestPrice = commercePriceEntry.getPrice();
			expectedPriceList = commerceAccountGroupAndChannelPriceList;
		}

		discoveredCommercePriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0,
				cpInstance.getCPInstanceUuid(), null, _TYPE, StringPool.BLANK);

		Assert.assertEquals(
			expectedPriceList.getCommercePriceListId(),
			discoveredCommercePriceList.getCommercePriceListId());

		CommercePriceList commerceAccountPriceList =
			CommercePriceListTestUtil.addAccountPriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(), _TYPE);

		commercePriceEntry = CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commerceAccountPriceList.getCommercePriceListId(),
			BigDecimal.valueOf(RandomTestUtil.randomDouble()));

		if (lowestPrice.compareTo(commercePriceEntry.getPrice()) > 0) {
			lowestPrice = commercePriceEntry.getPrice();
			expectedPriceList = commerceAccountPriceList;
		}

		discoveredCommercePriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0,
				cpInstance.getCPInstanceUuid(), null, _TYPE, StringPool.BLANK);

		Assert.assertEquals(
			expectedPriceList.getCommercePriceListId(),
			discoveredCommercePriceList.getCommercePriceListId());

		CommercePriceList commerceAccountAndChannelPriceList =
			CommercePriceListTestUtil.addAccountAndChannelPriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), _TYPE);

		commercePriceEntry = CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commerceAccountAndChannelPriceList.getCommercePriceListId(),
			BigDecimal.valueOf(RandomTestUtil.randomDouble()));

		if (lowestPrice.compareTo(commercePriceEntry.getPrice()) > 0) {
			expectedPriceList = commerceAccountAndChannelPriceList;
		}

		discoveredCommercePriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0,
				cpInstance.getCPInstanceUuid(), null, _TYPE, StringPool.BLANK);

		Assert.assertEquals(
			expectedPriceList.getCommercePriceListId(),
			discoveredCommercePriceList.getCommercePriceListId());
	}

	@Test
	public void testRetrieveCorrectPromotionListWithPriceModifications()
		throws Exception {

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				_commerceCatalog.getGroupId(), false, _TYPE, 1.0);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			RandomTestUtil.randomString(), cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			commercePriceList1.getCommercePriceListId(),
			BigDecimal.valueOf(RandomTestUtil.randomDouble()));

		CommercePriceList commercePriceList2 =
			CommercePriceListTestUtil.addCommercePriceList(
				_commerceCatalog.getGroupId(), false,
				CommercePriceListConstants.TYPE_PROMOTION, 1.0);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			RandomTestUtil.randomString(), cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			commercePriceList2.getCommercePriceListId(), BigDecimal.ZERO);

		CommercePriceList discoveredPriceList =
			_commercePriceListDiscovery.getCommercePriceList(
				_commerceCatalog.getGroupId(),
				_accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), 0,
				cpInstance.getCPInstanceUuid(), null,
				CommercePriceListConstants.TYPE_PROMOTION, StringPool.BLANK);

		Assert.assertEquals(
			commercePriceList2.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());

		CommercePriceList commercePriceList3 =
			CommercePriceListTestUtil.addCommercePriceList(
				_commerceCatalog.getGroupId(), false,
				CommercePriceListConstants.TYPE_PROMOTION, 1.0);

		discoveredPriceList = _commercePriceListDiscovery.getCommercePriceList(
			_commerceCatalog.getGroupId(), _accountEntry.getAccountEntryId(),
			_commerceChannel.getCommerceChannelId(), 0,
			cpInstance.getCPInstanceUuid(), null,
			CommercePriceListConstants.TYPE_PROMOTION, StringPool.BLANK);

		Assert.assertEquals(
			commercePriceList2.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());

		CommercePriceModifier commercePriceModifier =
			CommercePriceModifierTestUtil.addCommercePriceModifier(
				_commerceCatalog.getGroupId(), _user,
				commercePriceList3.getCommercePriceListId(),
				RandomTestUtil.randomString(),
				CommercePriceModifierConstants.TARGET_CATALOG,
				BigDecimal.valueOf(-50.0),
				CommercePriceModifierConstants.MODIFIER_TYPE_PERCENTAGE, 1.0,
				true, _serviceContext);

		_commercePriceModifierRelLocalService.addCommercePriceModifierRel(
			commercePriceModifier.getCommercePriceModifierId(),
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			_serviceContext);

		discoveredPriceList = _commercePriceListDiscovery.getCommercePriceList(
			_commerceCatalog.getGroupId(), _accountEntry.getAccountEntryId(),
			_commerceChannel.getCommerceChannelId(), 0,
			cpInstance.getCPInstanceUuid(), null,
			CommercePriceListConstants.TYPE_PROMOTION, StringPool.BLANK);

		Assert.assertEquals(
			commercePriceList3.getCommercePriceListId(),
			discoveredPriceList.getCommercePriceListId());
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private static final String _TYPE =
		CommercePriceListConstants.TYPE_PRICE_LIST;

	private AccountEntry _accountEntry;
	private AccountGroup _accountGroup;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	private CommerceCatalog _commerceCatalog;
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	private CommerceCurrency _commerceCurrency1;
	private CommerceCurrency _commerceCurrency2;

	@Inject(
		filter = "component.name=com.liferay.commerce.price.list.internal.discovery.CommercePriceListLowestDiscoveryImpl"
	)
	private CommercePriceListDiscovery _commercePriceListDiscovery;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommercePriceModifierLocalService
		_commercePriceModifierLocalService;

	@Inject
	private CommercePriceModifierRelLocalService
		_commercePriceModifierRelLocalService;

	private Group _group;
	private ServiceContext _serviceContext;
	private User _user;

}