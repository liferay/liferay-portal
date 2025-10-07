/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelLocalServiceUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.discount.test.util.CommerceDiscountTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.pricing.configuration.CommercePricingConfiguration;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
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

import java.util.ArrayList;
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
public class CommerceDiscountLocalServiceTest {

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

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), new long[] {_user.getUserId()}, null,
			_serviceContext);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		_accountGroup = _accountGroupLocalService.addAccountGroup(
			StringPool.BLANK, serviceContext.getUserId(), null,
			RandomTestUtil.randomString(), serviceContext);

		_accountGroup.setDefaultAccountGroup(false);
		_accountGroup.setType(AccountConstants.ACCOUNT_GROUP_TYPE_STATIC);
		_accountGroup.setExpandoBridgeAttributes(serviceContext);

		_accountGroup = _accountGroupLocalService.updateAccountGroup(
			_accountGroup);

		AccountGroupRelLocalServiceUtil.addAccountGroupRel(
			_accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			_accountEntry.getAccountEntryId());

		_commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId(),
			_commerceCurrency.getCode());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_commercePricingConfiguration =
			_configurationProvider.getSystemConfiguration(
				CommercePricingConfiguration.class);
	}

	@After
	public void tearDown() throws Exception {
		for (CommerceOrder commerceOrder : _commerceOrders) {
			_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
		}
	}

	@Test
	public void testCreateFixedDiscountWithTargetProduct() throws Exception {
		frutillaRule.scenario(
			"When a fixed amount discount is targeting a product in a " +
				"catalog it shall be possible to retrieve it with the " +
					"discount discovery"
		).given(
			"A catalog a product and a discount targeting that product"
		).when(
			"The discount is discovered"
		).then(
			"The discount is matching the created one"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CommerceDiscount commerceDiscount =
			CommerceDiscountTestUtil.addFixedCommerceDiscount(
				_user.getGroupId(), RandomTestUtil.nextDouble(),
				CommerceDiscountConstants.TARGET_PRODUCTS,
				cpDefinition.getCPDefinitionId());

		List<CommerceDiscount> commerceDiscounts =
			_commerceDiscountLocalService.getUnqualifiedCommerceDiscounts(
				_group.getCompanyId(), cpDefinition.getCPDefinitionId(),
				cpInstance.getCPInstanceId(), null);

		Assert.assertEquals(
			commerceDiscounts.toString(), 1, commerceDiscounts.size());

		Assert.assertEquals(commerceDiscount, commerceDiscounts.get(0));
	}

	@Test
	public void testCreatePercentageDiscountWithTargetProduct()
		throws Exception {

		frutillaRule.scenario(
			"When a percentage amount discount is targeting a product in a " +
				"catalog it shall be possible to retrieve it with the " +
					"discount discovery"
		).given(
			"A catalog a product and a discount targeting that product"
		).when(
			"The discount is discovered"
		).then(
			"The discount is matching the created one"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CommerceDiscount commerceDiscount =
			CommerceDiscountTestUtil.addPercentageCommerceDiscount(
				_user.getGroupId(),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				CommerceDiscountConstants.LEVEL_L1,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				cpDefinition.getCPDefinitionId());

		List<CommerceDiscount> commerceDiscounts =
			_commerceDiscountLocalService.getUnqualifiedCommerceDiscounts(
				_group.getCompanyId(), cpDefinition.getCPDefinitionId(),
				cpInstance.getCPInstanceId(), null);

		Assert.assertEquals(
			commerceDiscounts.toString(), 1, commerceDiscounts.size());

		Assert.assertEquals(commerceDiscount, commerceDiscounts.get(0));
	}

	@Test
	public void testGetOrderCommerceDiscountByHierarchy1() throws Exception {
		frutillaRule.scenario(
			"When multiple discounts are defined for the same target the " +
				"highest in the hierarchy shall be taken"
		).given(
			"A catalog with multiple discounts"
		).when(
			"The discount is discovered"
		).then(
			"The discount with highest rank is retrieved"
		);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		_commerceOrders.add(commerceOrder);

		long[] commerceAccountGroups =
			_accountGroupLocalService.getAccountGroupIds(
				_accountEntry.getAccountEntryId());

		CommerceDiscount commerceDiscountTotal1 =
			CommerceDiscountTestUtil.addChannelOrderCommerceDiscount(
				_user.getGroupId(), _commerceChannel.getCommerceChannelId(),
				CommerceDiscountConstants.TARGET_TOTAL);

		_orderAssertEquals(
			commerceDiscountTotal1, CommerceDiscountConstants.TARGET_TOTAL);

		CommerceDiscount commerceDiscountTotal2 =
			CommerceDiscountTestUtil.addAccountGroupOrderCommerceDiscount(
				_user.getGroupId(), commerceAccountGroups,
				CommerceDiscountConstants.TARGET_TOTAL);

		_orderAssertEquals(
			commerceDiscountTotal2, CommerceDiscountConstants.TARGET_TOTAL);

		CommerceDiscount commerceDiscountTotal3 =
			CommerceDiscountTestUtil.
				addAccountGroupAndChannelOrderCommerceDiscount(
					_user.getGroupId(), commerceAccountGroups,
					_commerceChannel.getCommerceChannelId(),
					CommerceDiscountConstants.TARGET_TOTAL);

		_orderAssertEquals(
			commerceDiscountTotal3, CommerceDiscountConstants.TARGET_TOTAL);

		CommerceDiscount commerceDiscountTotal4 =
			CommerceDiscountTestUtil.addAccountOrderCommerceDiscount(
				_user.getGroupId(), _accountEntry.getAccountEntryId(),
				CommerceDiscountConstants.TARGET_TOTAL);

		_orderAssertEquals(
			commerceDiscountTotal4, CommerceDiscountConstants.TARGET_TOTAL);

		CommerceDiscount commerceDiscountTotal5 =
			CommerceDiscountTestUtil.addAccountAndChannelOrderCommerceDiscount(
				_user.getGroupId(), _accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(),
				CommerceDiscountConstants.TARGET_TOTAL);

		_orderAssertEquals(
			commerceDiscountTotal5, CommerceDiscountConstants.TARGET_TOTAL);

		CommerceDiscount commerceDiscountShipping1 =
			CommerceDiscountTestUtil.addChannelOrderCommerceDiscount(
				_user.getGroupId(), _commerceChannel.getCommerceChannelId(),
				CommerceDiscountConstants.TARGET_SHIPPING);

		_orderAssertEquals(
			commerceDiscountShipping1,
			CommerceDiscountConstants.TARGET_SHIPPING);

		CommerceDiscount commerceDiscountShipping2 =
			CommerceDiscountTestUtil.addAccountGroupOrderCommerceDiscount(
				_user.getGroupId(), commerceAccountGroups,
				CommerceDiscountConstants.TARGET_SHIPPING);

		_orderAssertEquals(
			commerceDiscountShipping2,
			CommerceDiscountConstants.TARGET_SHIPPING);

		CommerceDiscount commerceDiscountShipping3 =
			CommerceDiscountTestUtil.
				addAccountGroupAndChannelOrderCommerceDiscount(
					_user.getGroupId(), commerceAccountGroups,
					_commerceChannel.getCommerceChannelId(),
					CommerceDiscountConstants.TARGET_SHIPPING);

		_orderAssertEquals(
			commerceDiscountShipping3,
			CommerceDiscountConstants.TARGET_SHIPPING);

		CommerceDiscount commerceDiscountShipping4 =
			CommerceDiscountTestUtil.addAccountOrderCommerceDiscount(
				_user.getGroupId(), _accountEntry.getAccountEntryId(),
				CommerceDiscountConstants.TARGET_SHIPPING);

		_orderAssertEquals(
			commerceDiscountShipping4,
			CommerceDiscountConstants.TARGET_SHIPPING);

		CommerceDiscount commerceDiscountShipping5 =
			CommerceDiscountTestUtil.addAccountAndChannelOrderCommerceDiscount(
				_user.getGroupId(), _accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(),
				CommerceDiscountConstants.TARGET_SHIPPING);

		_orderAssertEquals(
			commerceDiscountShipping5,
			CommerceDiscountConstants.TARGET_SHIPPING);

		CommerceDiscount commerceDiscountSubtotal1 =
			CommerceDiscountTestUtil.addChannelOrderCommerceDiscount(
				_user.getGroupId(), _commerceChannel.getCommerceChannelId(),
				CommerceDiscountConstants.TARGET_SUBTOTAL);

		_orderAssertEquals(
			commerceDiscountSubtotal1,
			CommerceDiscountConstants.TARGET_SUBTOTAL);

		CommerceDiscount commerceDiscountSubtotal2 =
			CommerceDiscountTestUtil.addAccountGroupOrderCommerceDiscount(
				_user.getGroupId(), commerceAccountGroups,
				CommerceDiscountConstants.TARGET_SUBTOTAL);

		_orderAssertEquals(
			commerceDiscountSubtotal2,
			CommerceDiscountConstants.TARGET_SUBTOTAL);

		CommerceDiscount commerceDiscountSubtotal3 =
			CommerceDiscountTestUtil.
				addAccountGroupAndChannelOrderCommerceDiscount(
					_user.getGroupId(), commerceAccountGroups,
					_commerceChannel.getCommerceChannelId(),
					CommerceDiscountConstants.TARGET_SUBTOTAL);

		_orderAssertEquals(
			commerceDiscountSubtotal3,
			CommerceDiscountConstants.TARGET_SUBTOTAL);

		CommerceDiscount commerceDiscountSubtotal4 =
			CommerceDiscountTestUtil.addAccountOrderCommerceDiscount(
				_user.getGroupId(), _accountEntry.getAccountEntryId(),
				CommerceDiscountConstants.TARGET_SUBTOTAL);

		_orderAssertEquals(
			commerceDiscountSubtotal4,
			CommerceDiscountConstants.TARGET_SUBTOTAL);

		CommerceDiscount commerceDiscountSubtotal5 =
			CommerceDiscountTestUtil.addAccountAndChannelOrderCommerceDiscount(
				_user.getGroupId(), _accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(),
				CommerceDiscountConstants.TARGET_SUBTOTAL);

		_orderAssertEquals(
			commerceDiscountSubtotal5,
			CommerceDiscountConstants.TARGET_SUBTOTAL);
	}

	@Test
	public void testGetOrderCommerceDiscountByHierarchy2() throws Exception {
		frutillaRule.scenario(
			"A discount is qualified by an account and a channel is not " +
				"applicable to the same account on another channel"
		).given(
			"A catalog with a discount on account and channel"
		).when(
			"The discount is discovered given a different channel"
		).then(
			"No discount is returned"
		);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		_commerceOrders.add(commerceOrder);

		CommerceDiscountTestUtil.addAccountAndChannelOrderCommerceDiscount(
			_user.getGroupId(), _accountEntry.getAccountEntryId(),
			_commerceChannel.getCommerceChannelId(),
			CommerceDiscountConstants.TARGET_TOTAL);

		List<CommerceDiscount> commerceDiscounts =
			_getOrderCommerceDiscountByHierarchy(
				_group.getCompanyId(), _accountEntry.getAccountEntryId(),
				RandomTestUtil.nextLong(),
				CommerceDiscountConstants.TARGET_TOTAL);

		Assert.assertEquals(
			commerceDiscounts.toString(), 0, commerceDiscounts.size());
	}

	@Test
	public void testGetOrderCommerceDiscountByHierarchy3() throws Exception {
		frutillaRule.scenario(
			"A discount is qualified by an account group and a channel is " +
				"not applicable to the same account group on another channel"
		).given(
			"A catalog with a discount on account group and channel"
		).when(
			"The discount is discovered given a different channel"
		).then(
			"No discount is returned"
		);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		_commerceOrders.add(commerceOrder);

		long[] commerceAccountGroups =
			_accountGroupLocalService.getAccountGroupIds(
				_accountEntry.getAccountEntryId());

		CommerceDiscountTestUtil.addAccountGroupAndChannelOrderCommerceDiscount(
			_user.getGroupId(), commerceAccountGroups,
			_commerceChannel.getCommerceChannelId(),
			CommerceDiscountConstants.TARGET_TOTAL);

		List<CommerceDiscount> commerceDiscounts =
			_getOrderCommerceDiscountByHierarchy(
				_group.getCompanyId(), _accountEntry.getAccountEntryId(),
				RandomTestUtil.nextLong(),
				CommerceDiscountConstants.TARGET_TOTAL);

		Assert.assertEquals(
			commerceDiscounts.toString(), 0, commerceDiscounts.size());
	}

	@Test
	public void testGetProductCommerceDiscountByHierarchy1() throws Exception {
		frutillaRule.scenario(
			"When multiple discounts are defined for the same target the " +
				"highest in the hierarchy shall be taken"
		).given(
			"A catalog with multiple discounts"
		).when(
			"The discount is discovered"
		).then(
			"The discount with highest rank is retrieved"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CommerceDiscount commerceUnqualifiedDiscount =
			CommerceDiscountTestUtil.addPercentageCommerceDiscount(
				_user.getGroupId(),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				CommerceDiscountConstants.LEVEL_L2,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				cpDefinition.getCPDefinitionId());

		_productAssertEquals(
			commerceUnqualifiedDiscount, cpDefinition.getCPDefinitionId(),
			cpInstance.getCPInstanceId());

		CommerceDiscount commerceChannelDiscount =
			CommerceDiscountTestUtil.addChannelCommerceDiscount(
				_user.getGroupId(), _commerceChannel.getCommerceChannelId(),
				CommerceDiscountConstants.LEVEL_L1,
				cpDefinition.getCPDefinitionId());

		_productAssertEquals(
			commerceChannelDiscount, cpDefinition.getCPDefinitionId(),
			cpInstance.getCPInstanceId());

		long[] commerceAccountGroups =
			_accountGroupLocalService.getAccountGroupIds(
				_accountEntry.getAccountEntryId());

		CommerceDiscount commerceAccountGroupsDiscount =
			CommerceDiscountTestUtil.addAccountGroupCommerceDiscount(
				_user.getGroupId(), commerceAccountGroups,
				CommerceDiscountConstants.LEVEL_L3,
				cpDefinition.getCPDefinitionId());

		_productAssertEquals(
			commerceAccountGroupsDiscount, cpDefinition.getCPDefinitionId(),
			cpInstance.getCPInstanceId());

		CommerceDiscount commerceAccountGroupsAndChannelDiscount =
			CommerceDiscountTestUtil.addAccountGroupAndChannelCommerceDiscount(
				_user.getGroupId(), commerceAccountGroups,
				_commerceChannel.getCommerceChannelId(),
				CommerceDiscountConstants.LEVEL_L3,
				cpDefinition.getCPDefinitionId());

		_productAssertEquals(
			commerceAccountGroupsAndChannelDiscount,
			cpDefinition.getCPDefinitionId(), cpInstance.getCPInstanceId());

		CommerceDiscount commerceAccountDiscount =
			CommerceDiscountTestUtil.addAccountCommerceDiscount(
				_user.getGroupId(), _accountEntry.getAccountEntryId(),
				CommerceDiscountConstants.LEVEL_L4,
				cpDefinition.getCPDefinitionId());

		_productAssertEquals(
			commerceAccountDiscount, cpDefinition.getCPDefinitionId(),
			cpInstance.getCPInstanceId());

		CommerceDiscount commerceAccountAndChannelDiscount =
			CommerceDiscountTestUtil.addAccountAndChannelCommerceDiscount(
				_user.getGroupId(), _accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(),
				CommerceDiscountConstants.LEVEL_L4,
				cpDefinition.getCPDefinitionId());

		_productAssertEquals(
			commerceAccountAndChannelDiscount, cpDefinition.getCPDefinitionId(),
			cpInstance.getCPInstanceId());
	}

	@Test
	public void testGetProductCommerceDiscountByHierarchy2() throws Exception {
		frutillaRule.scenario(
			"A discount is qualified by an account and a channel is not " +
				"applicable to the same account on another channel"
		).given(
			"A catalog with a discount on account and channel"
		).when(
			"The discount is discovered given a different channel"
		).then(
			"No discount is returned"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CommerceDiscountTestUtil.addAccountAndChannelCommerceDiscount(
			_user.getGroupId(), _accountEntry.getAccountEntryId(),
			_commerceChannel.getCommerceChannelId(),
			CommerceDiscountConstants.LEVEL_L4,
			cpDefinition.getCPDefinitionId());

		List<CommerceDiscount> commerceDiscounts =
			_getProductCommerceDiscountByHierarchy(
				_group.getCompanyId(), _accountEntry.getAccountEntryId(),
				RandomTestUtil.nextLong(), cpDefinition.getCPDefinitionId(),
				cpInstance.getCPInstanceId());

		Assert.assertEquals(
			commerceDiscounts.toString(), 0, commerceDiscounts.size());
	}

	@Test
	public void testGetProductCommerceDiscountByHierarchy3() throws Exception {
		frutillaRule.scenario(
			"A discount is qualified by an account group and a channel is " +
				"not applicable to the same account group on another channel"
		).given(
			"A catalog with a discount on account group and channel"
		).when(
			"The discount is discovered given a different channel"
		).then(
			"No discount is returned"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		long[] commerceAccountGroups =
			_accountGroupLocalService.getAccountGroupIds(
				_accountEntry.getAccountEntryId());

		CommerceDiscountTestUtil.addAccountGroupAndChannelCommerceDiscount(
			_user.getGroupId(), commerceAccountGroups,
			_commerceChannel.getCommerceChannelId(),
			CommerceDiscountConstants.LEVEL_L3,
			cpDefinition.getCPDefinitionId());

		List<CommerceDiscount> commerceDiscounts =
			_getProductCommerceDiscountByHierarchy(
				_group.getCompanyId(), _accountEntry.getAccountEntryId(),
				RandomTestUtil.nextLong(), cpDefinition.getCPDefinitionId(),
				cpInstance.getCPInstanceId());

		Assert.assertEquals(
			commerceDiscounts.toString(), 0, commerceDiscounts.size());
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private List<CommerceDiscount> _getOrderCommerceDiscountByHierarchy(
			long companyId, long commerceAccountId, long commerceChannelId,
			String commerceDiscountTargetType)
		throws Exception {

		List<CommerceDiscount> commerceDiscounts =
			_commerceDiscountLocalService.
				getAccountAndChannelAndOrderTypeCommerceDiscounts(
					commerceAccountId, commerceChannelId, 0,
					commerceDiscountTargetType);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.
				getAccountAndOrderTypeCommerceDiscounts(
					commerceAccountId, 0, commerceDiscountTargetType);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.getAccountAndChannelCommerceDiscounts(
				commerceAccountId, commerceChannelId,
				commerceDiscountTargetType);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.getAccountCommerceDiscounts(
				commerceAccountId, commerceDiscountTargetType);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		long[] commerceAccountGroupIds =
			_accountGroupLocalService.getAccountGroupIds(commerceAccountId);

		commerceDiscounts =
			_commerceDiscountLocalService.
				getAccountGroupAndChannelAndOrderTypeCommerceDiscount(
					commerceAccountGroupIds, commerceChannelId, 0,
					commerceDiscountTargetType);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.
				getAccountGroupAndOrderTypeCommerceDiscount(
					commerceAccountGroupIds, 0, commerceDiscountTargetType);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.
				getAccountGroupAndChannelCommerceDiscount(
					commerceAccountGroupIds, commerceChannelId,
					commerceDiscountTargetType);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.getAccountGroupCommerceDiscount(
				commerceAccountGroupIds, commerceDiscountTargetType);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.
				getChannelAndOrderTypeCommerceDiscounts(
					commerceChannelId, 0, commerceDiscountTargetType);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.getOrderTypeCommerceDiscounts(
				0, commerceDiscountTargetType);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.getChannelCommerceDiscounts(
				commerceChannelId, commerceDiscountTargetType);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		return _commerceDiscountLocalService.getUnqualifiedCommerceDiscounts(
			companyId, commerceDiscountTargetType);
	}

	private List<CommerceDiscount> _getProductCommerceDiscountByHierarchy(
			long companyId, long commerceAccountId, long commerceChannelId,
			long cpDefinitionId, long cpInstanceId)
		throws Exception {

		List<CommerceDiscount> commerceDiscounts =
			_commerceDiscountLocalService.
				getAccountAndChannelAndOrderTypeCommerceDiscounts(
					commerceAccountId, commerceChannelId, 0, cpDefinitionId,
					cpInstanceId, null);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.getAccountAndChannelCommerceDiscounts(
				commerceAccountId, commerceChannelId, cpDefinitionId,
				cpInstanceId, null);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.getAccountCommerceDiscounts(
				commerceAccountId, cpDefinitionId, cpInstanceId, null);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		long[] commerceAccountGroupIds =
			_accountGroupLocalService.getAccountGroupIds(commerceAccountId);

		commerceDiscounts =
			_commerceDiscountLocalService.
				getAccountGroupAndChannelAndOrderTypeCommerceDiscount(
					commerceAccountGroupIds, commerceChannelId, 0,
					cpDefinitionId, cpInstanceId, null);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.
				getAccountGroupAndChannelCommerceDiscount(
					commerceAccountGroupIds, commerceChannelId, cpDefinitionId,
					cpInstanceId, null);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.getAccountGroupCommerceDiscount(
				commerceAccountGroupIds, cpDefinitionId, cpInstanceId, null);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.
				getChannelAndOrderTypeCommerceDiscounts(
					commerceChannelId, 0, cpDefinitionId, cpInstanceId, null);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.getOrderTypeCommerceDiscounts(
				0, cpDefinitionId, cpInstanceId, null);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		commerceDiscounts =
			_commerceDiscountLocalService.getChannelCommerceDiscounts(
				commerceChannelId, cpDefinitionId, cpInstanceId, null);

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			return commerceDiscounts;
		}

		return _commerceDiscountLocalService.getUnqualifiedCommerceDiscounts(
			companyId, cpDefinitionId, cpInstanceId, null);
	}

	private void _orderAssertEquals(
			CommerceDiscount expectedDiscount, String type)
		throws Exception {

		List<CommerceDiscount> commerceDiscounts =
			_getOrderCommerceDiscountByHierarchy(
				_group.getCompanyId(), _accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), type);

		CommerceDiscount commerceDiscount = commerceDiscounts.get(0);

		Assert.assertEquals(
			expectedDiscount.getCommerceDiscountId(),
			commerceDiscount.getCommerceDiscountId());
	}

	private void _productAssertEquals(
			CommerceDiscount expectedDiscount, long cpDefinitionId,
			long cpInstanceId)
		throws Exception {

		List<CommerceDiscount> commerceDiscounts =
			_getProductCommerceDiscountByHierarchy(
				_group.getCompanyId(), _accountEntry.getAccountEntryId(),
				_commerceChannel.getCommerceChannelId(), cpDefinitionId,
				cpInstanceId);

		CommerceDiscount commerceDiscount = commerceDiscounts.get(0);

		Assert.assertEquals(
			expectedDiscount.getCommerceDiscountId(),
			commerceDiscount.getCommerceDiscountId());
	}

	private static User _user;

	private AccountEntry _accountEntry;
	private AccountGroup _accountGroup;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	private CommerceCatalog _commerceCatalog;
	private CommerceChannel _commerceChannel;
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private final List<CommerceOrder> _commerceOrders = new ArrayList<>();
	private CommercePricingConfiguration _commercePricingConfiguration;

	@Inject
	private ConfigurationProvider _configurationProvider;

	private Group _group;
	private ServiceContext _serviceContext;

}