/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.exception.CommerceDiscountLimitationTimesException;
import com.liferay.commerce.discount.exception.CommerceDiscountValidatorException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountCommerceAccountGroupRelLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountUsageEntryLocalService;
import com.liferay.commerce.discount.test.util.CommerceDiscountTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.test.util.CommercePriceEntryTestUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
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
public class CommerceDiscountUsageTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		_accountEntry = CommerceAccountTestUtil.getPersonAccountEntry(
			_user.getUserId());

		_serviceContext = ServiceContextTestUtil.getServiceContext();

		_accountGroup = _accountGroupLocalService.addAccountGroup(
			StringPool.BLANK, _serviceContext.getUserId(), null,
			RandomTestUtil.randomString(), _serviceContext);

		_accountGroup.setDefaultAccountGroup(false);
		_accountGroup.setType(AccountConstants.ACCOUNT_GROUP_TYPE_STATIC);
		_accountGroup.setExpandoBridgeAttributes(_serviceContext);

		_accountGroup = _accountGroupLocalService.updateAccountGroup(
			_accountGroup);

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			null, RandomTestUtil.randomString(), _commerceCurrency.getCode(),
			LocaleUtil.US.getDisplayLanguage(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		_cpDefinition = _cpInstance.getCPDefinition();

		_commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				_commerceCatalog.getGroupId());

		_commercePriceEntry = CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, _cpDefinition.getCProductId(),
			_cpInstance.getCPInstanceUuid(),
			_commercePriceList.getCommercePriceListId(),
			BigDecimal.valueOf(35));

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), _commerceInventoryWarehouse,
			BigDecimal.valueOf(1000), _cpInstance.getSku(), StringPool.BLANK);
	}

	@After
	public void tearDown() throws Exception {
		for (CommerceOrder commerceOrder : _commerceOrders) {
			_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
		}
	}

	@Test
	public void testAccountGroupCouponCodeDiscount() throws Exception {
		frutillaRule.scenario(
			"Discounts can be applied by coupon code"
		).given(
			"A discount is associated with a account group"
		).and(
			"Account is assigned to account group"
		).when(
			"I apply the coupon to my order"
		).then(
			"The discount is applied to the order"
		);

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_user.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(), new long[] {_user.getUserId()},
				null, _serviceContext);

		_accountGroupRelLocalService.addAccountGroupRel(
			_accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			accountEntry.getAccountEntryId());

		String couponCode = StringUtil.randomString();

		CommerceDiscount commerceDiscount =
			CommerceDiscountTestUtil.addCouponCommerceDiscount(
				_group.getGroupId(), 35, couponCode,
				CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED, 0, 0,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				_cpDefinition.getCPDefinitionId());

		_commerceDiscountCommerceAccountGroupRelLocalService.
			addCommerceDiscountCommerceAccountGroupRel(
				_serviceContext.getUserId(),
				commerceDiscount.getCommerceDiscountId(),
				_accountGroup.getAccountGroupId(), _serviceContext);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2BCommerceOrder(
			_group.getGroupId(), _user.getUserId(),
			accountEntry.getAccountEntryId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceOrders.add(commerceOrder);

		CommerceContext commerceContext = new TestCommerceContext(
			accountEntry, _commerceCurrency, null, _user, _group,
			commerceOrder);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(), _cpInstance.getCPInstanceId(),
			BigDecimal.ONE, commerceContext);

		commerceOrder = _commerceOrderLocalService.applyCouponCode(
			commerceOrder.getCommerceOrderId(), couponCode, commerceContext);

		Assert.assertEquals(couponCode, commerceOrder.getCouponCode());
	}

	@Test(expected = CommerceDiscountLimitationTimesException.class)
	public void testAccountLimitedCouponCodeDiscount() throws Exception {
		frutillaRule.scenario(
			"Discounts can be applied by coupon code"
		).given(
			"A product with a base price"
		).and(
			"A discount with account limitation times set to 3"
		).when(
			"I insert the correct coupon code in my context the discount is " +
				"correctly applied"
		).then(
			"When the same account uses the same coupon the 4th time I " +
				"receive an exception"
		);

		_assertDiscountLimitation(
			0, 3, 3,
			CommerceDiscountConstants.LIMITATION_TYPE_LIMITED_FOR_ACCOUNTS);
	}

	@Test
	public void testApplyLimitedCouponCodeDiscountToProducts()
		throws Exception {

		frutillaRule.scenario(
			"Discounts can be applied by coupon code"
		).given(
			"A product with a base price"
		).and(
			"A discount with total limitation time set to 3"
		).when(
			"I insert the correct coupon code in my context the discount is " +
				"correctly applied"
		).then(
			"The discount usage should not be updated when getting the price " +
				"of a product"
		);

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		BigDecimal priceEntryPrice = BigDecimal.valueOf(35);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), priceEntryPrice);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		Assert.assertEquals(
			priceEntryPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), commerceChannel.getGroupId(), _commerceCurrency);

		commerceOrder.setCommerceCurrencyCode(_commerceCurrency.getCode());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			commerceChannel.getCommerceChannelId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstance.getSku(), StringPool.BLANK);

		String couponCode = StringUtil.randomString();

		CommerceDiscount commerceDiscount =
			CommerceDiscountTestUtil.addCouponCommerceDiscount(
				_group.getGroupId(), 10, couponCode,
				CommerceDiscountConstants.LIMITATION_TYPE_LIMITED, 3, 0,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				cpDefinition.getCPDefinitionId());

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(), cpInstance.getCPInstanceId(),
			BigDecimal.ONE, commerceContext);

		commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, commerceChannel, _user, _group,
			commerceOrder);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		finalPriceCommerceMoney = commerceProductPrice.getFinalPrice();

		finalPrice = finalPriceCommerceMoney.getPrice();

		Assert.assertEquals(
			priceEntryPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		int commerceDiscountUsageCount =
			_commerceDiscountUsageEntryLocalService.
				getCommerceDiscountUsageEntriesCount(
					commerceDiscount.getCommerceDiscountId());

		Assert.assertEquals(0, commerceDiscountUsageCount);

		commerceOrder = _commerceOrderLocalService.applyCouponCode(
			commerceOrder.getCommerceOrderId(), couponCode, commerceContext);

		_commerceOrders.add(commerceOrder);

		commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, commerceChannel, _user, _group,
			commerceOrder);

		_commerceProductPriceCalculation.getCommerceProductPrice(
			cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
			commerceContext);

		_commerceOrderEngine.checkoutCommerceOrder(
			commerceOrder, _user.getUserId());

		commerceDiscountUsageCount =
			_commerceDiscountUsageEntryLocalService.
				getCommerceDiscountUsageEntriesCount(
					commerceDiscount.getCommerceDiscountId());

		Assert.assertEquals(1, commerceDiscountUsageCount);

		_commerceProductPriceCalculation.getCommerceProductPrice(
			cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
			commerceContext);

		commerceDiscountUsageCount =
			_commerceDiscountUsageEntryLocalService.
				getCommerceDiscountUsageEntriesCount(
					commerceDiscount.getCommerceDiscountId());

		Assert.assertEquals(1, commerceDiscountUsageCount);
	}

	@Test(expected = CommerceDiscountValidatorException.class)
	public void testInvalidAccountGroupCouponCodeDiscount() throws Exception {
		frutillaRule.scenario(
			"Discounts can not be applied by coupon code"
		).given(
			"A discount is associated with a account group"
		).and(
			"Account is not assigned to account group"
		).when(
			"I apply the coupon to my order"
		).then(
			"The discount is not applied to the order"
		);

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_user.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(), new long[] {_user.getUserId()},
				null, _serviceContext);

		String couponCode = StringUtil.randomString();

		CommerceDiscount commerceDiscount =
			CommerceDiscountTestUtil.addCouponCommerceDiscount(
				_group.getGroupId(), 35, couponCode,
				CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED, 0, 0,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				_cpDefinition.getCPDefinitionId());

		_commerceDiscountCommerceAccountGroupRelLocalService.
			addCommerceDiscountCommerceAccountGroupRel(
				_serviceContext.getUserId(),
				commerceDiscount.getCommerceDiscountId(),
				_accountGroup.getAccountGroupId(), _serviceContext);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2BCommerceOrder(
			_group.getGroupId(), _user.getUserId(),
			accountEntry.getAccountEntryId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceOrders.add(commerceOrder);

		CommerceContext commerceContext = new TestCommerceContext(
			accountEntry, _commerceCurrency, null, _user, _group,
			commerceOrder);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(), _cpInstance.getCPInstanceId(),
			BigDecimal.ONE, commerceContext);

		_commerceOrderLocalService.applyCouponCode(
			commerceOrder.getCommerceOrderId(), couponCode, commerceContext);
	}

	@Test
	public void testLimitedCouponCodeDiscountApplyAndRemove() throws Exception {
		frutillaRule.scenario(
			"Discounts can be applied by coupon code"
		).given(
			"A product with a base price"
		).and(
			"A discount with account limitation times set to 3"
		).when(
			"I insert the correct coupon code in my context the discount is " +
				"correctly applied"
		).then(
			"If i remove the coupon code from the order before checkout the " +
				"usage count is not changed"
		);

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), commerceChannel.getGroupId(), _commerceCurrency);

		commerceOrder.setCommerceCurrencyCode(_commerceCurrency.getCode());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		BigDecimal priceEntryPrice = BigDecimal.valueOf(35);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), priceEntryPrice);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			commerceChannel.getCommerceChannelId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstance.getSku(), StringPool.BLANK);

		String couponCode = StringUtil.randomString();

		CommerceDiscount commerceDiscount =
			CommerceDiscountTestUtil.addCouponCommerceDiscount(
				_group.getGroupId(), 10, couponCode,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				cpDefinition.getCPDefinitionId());

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, commerceChannel, _user, _group,
			commerceOrder);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(), cpInstance.getCPInstanceId(),
			BigDecimal.ONE, commerceContext);

		commerceOrder = _commerceOrderLocalService.applyCouponCode(
			commerceOrder.getCommerceOrderId(), couponCode, commerceContext);

		_commerceOrders.add(commerceOrder);

		int commerceDiscountUsageCount =
			_commerceDiscountUsageEntryLocalService.
				getCommerceDiscountUsageEntriesCount(
					commerceDiscount.getCommerceDiscountId());

		Assert.assertEquals(0, commerceDiscountUsageCount);

		_commerceOrderLocalService.applyCouponCode(
			commerceOrder.getCommerceOrderId(), null, commerceContext);

		commerceDiscountUsageCount =
			_commerceDiscountUsageEntryLocalService.
				getCommerceDiscountUsageEntriesCount(
					commerceDiscount.getCommerceDiscountId());

		Assert.assertEquals(0, commerceDiscountUsageCount);
	}

	@Test(expected = CommerceDiscountLimitationTimesException.class)
	public void testTotalAndAccountLimitedCouponCodeDiscount1()
		throws Exception {

		frutillaRule.scenario(
			"Discounts can be applied by coupon code"
		).given(
			"A product with a base price"
		).and(
			"A discount with total limitation time set to 5 and account " +
				"limitation times set to 3"
		).when(
			"I insert the correct coupon code in my context the discount is " +
				"correctly applied"
		).then(
			"When the same account uses the same coupon the 4th time I " +
				"receive an exception"
		);

		_assertDiscountLimitation(
			5, 3, 3,
			CommerceDiscountConstants.
				LIMITATION_TYPE_LIMITED_FOR_ACCOUNTS_AND_TOTAL);
	}

	@Test(expected = CommerceDiscountLimitationTimesException.class)
	public void testTotalAndAccountLimitedCouponCodeDiscount2()
		throws Exception {

		frutillaRule.scenario(
			"Discounts can be applied by coupon code"
		).given(
			"A product with a base price"
		).and(
			"A discount with total limitation time set to 3 and account " +
				"limitation times set to 5"
		).when(
			"I insert the correct coupon code in my context the discount is " +
				"correctly applied"
		).then(
			"When an account uses the same coupon the 4th time I receive an " +
				"exception"
		);

		_assertDiscountLimitation(
			3, 5, 3,
			CommerceDiscountConstants.
				LIMITATION_TYPE_LIMITED_FOR_ACCOUNTS_AND_TOTAL);
	}

	@Test(expected = CommerceDiscountLimitationTimesException.class)
	public void testTotalAndAccountLimitedCouponCodeDiscountGuestAccount()
		throws Exception {

		frutillaRule.scenario(
			"Discounts can be applied by coupon code"
		).given(
			"A product with a base price"
		).and(
			"A discount with total limitation time set to 3 and account " +
				"limitation times set to 5"
		).when(
			"I insert the correct coupon code in my context the discount is " +
				"correctly applied"
		).then(
			"When an account uses the same coupon the 4th time I receive an " +
				"exception"
		);

		_accountEntry = _accountEntryLocalService.getGuestAccountEntry(
			_group.getCompanyId());

		_assertDiscountLimitation(
			3, 5, 3,
			CommerceDiscountConstants.
				LIMITATION_TYPE_LIMITED_FOR_ACCOUNTS_AND_TOTAL);
	}

	@Test(expected = CommerceDiscountLimitationTimesException.class)
	public void testTotalLimitedCouponCodeDiscount() throws Exception {
		frutillaRule.scenario(
			"Discounts can be applied by coupon code"
		).given(
			"A product with a base price"
		).and(
			"A discount with total limitation times set to 3"
		).when(
			"I insert the correct coupon code in my context the discount is " +
				"correctly applied"
		).then(
			"When i try to use the same coupon the 4th time I receive an " +
				"exception"
		);

		_assertDiscountLimitation(
			3, 0, 3, CommerceDiscountConstants.LIMITATION_TYPE_LIMITED);
	}

	@Test
	public void testUnlimitedCouponCodeDiscount() throws Exception {
		frutillaRule.scenario(
			"Discounts can be applied by coupon code"
		).given(
			"A product with a base price"
		).and(
			"A discount with no limitation"
		).when(
			"I insert the correct coupon code in my context the discount is " +
				"correctly applied"
		).then(
			"The price of the product is correctly calculated"
		);

		_assertDiscountLimitation(
			0, 0, 3, CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED);
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private void _assertDiscountLimitation(
			int limitationTimes, int limitationTimesPerAccount,
			int numberOfOrders, String limitationType)
		throws Exception {

		String couponCode = StringUtil.randomString();

		CommerceDiscount commerceDiscount =
			CommerceDiscountTestUtil.addCouponCommerceDiscount(
				_group.getGroupId(), 10, couponCode, limitationType,
				limitationTimes, limitationTimesPerAccount,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				_cpDefinition.getCPDefinitionId());

		for (int i = 0; i < numberOfOrders; i++) {
			CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_commerceCurrency);

			commerceOrder.setCommerceCurrencyCode(_commerceCurrency.getCode());

			commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
				commerceOrder);

			CommerceContext commerceContext = new TestCommerceContext(
				_accountEntry, _commerceCurrency, _commerceChannel, _user,
				_group, commerceOrder);

			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				_cpInstance.getCPInstanceId(), BigDecimal.ONE, commerceContext);

			commerceOrder = _commerceOrderLocalService.applyCouponCode(
				commerceOrder.getCommerceOrderId(), couponCode,
				commerceContext);

			_commerceOrders.add(commerceOrder);

			BigDecimal actualPrice = BigDecimal.ZERO;
			BigDecimal discountPrice = BigDecimal.ZERO;

			commerceContext = new TestCommerceContext(
				_accountEntry, _commerceCurrency, _commerceChannel, _user,
				_group, commerceOrder);

			CommerceProductPrice commerceProductPrice =
				_commerceProductPriceCalculation.getCommerceProductPrice(
					_cpInstance.getCPInstanceId(), BigDecimal.ONE,
					StringPool.BLANK, commerceContext);

			if (commerceProductPrice != null) {
				CommerceMoney finalPriceCommerceMoney =
					commerceProductPrice.getFinalPrice();

				actualPrice = finalPriceCommerceMoney.getPrice();

				CommerceDiscountValue discountValue =
					commerceProductPrice.getDiscountValue();

				CommerceMoney discountAmountCommerceMoney =
					discountValue.getDiscountAmount();

				discountPrice = discountAmountCommerceMoney.getPrice();
			}

			BigDecimal expectedPrice = commerceDiscount.getLevel1();

			Assert.assertEquals(
				expectedPrice.stripTrailingZeros(),
				discountPrice.stripTrailingZeros());

			BigDecimal price = _commercePriceEntry.getPrice();

			expectedPrice = price.subtract(commerceDiscount.getLevel1());

			Assert.assertEquals(
				expectedPrice.stripTrailingZeros(),
				actualPrice.stripTrailingZeros());

			_commerceOrderEngine.checkoutCommerceOrder(
				commerceOrder, _user.getUserId());
		}

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		commerceOrder.setCommerceCurrencyCode(_commerceCurrency.getCode());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			commerceOrder);

		_commerceOrders.add(commerceOrder);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(), _cpInstance.getCPInstanceId(),
			BigDecimal.ONE, commerceContext);

		commerceOrder = _commerceOrderLocalService.applyCouponCode(
			commerceOrder.getCommerceOrderId(), couponCode, commerceContext);

		BigDecimal actualPrice = BigDecimal.ZERO;
		BigDecimal discountPrice = BigDecimal.ZERO;

		commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			commerceOrder);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		if (commerceProductPrice != null) {
			CommerceMoney finalPriceCommerceMoney =
				commerceProductPrice.getFinalPrice();

			actualPrice = finalPriceCommerceMoney.getPrice();

			CommerceDiscountValue discountValue =
				commerceProductPrice.getDiscountValue();

			CommerceMoney discountAmountCommerceMoney =
				discountValue.getDiscountAmount();

			discountPrice = discountAmountCommerceMoney.getPrice();
		}

		BigDecimal expectedPrice = commerceDiscount.getLevel1();

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			discountPrice.stripTrailingZeros());

		BigDecimal price = _commercePriceEntry.getPrice();

		expectedPrice = price.subtract(commerceDiscount.getLevel1());

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			actualPrice.stripTrailingZeros());

		_commerceOrderEngine.checkoutCommerceOrder(
			commerceOrder, _user.getUserId());
	}

	private static User _user;

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@DeleteAfterTestRun
	private AccountGroup _accountGroup;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceDiscountCommerceAccountGroupRelLocalService
		_commerceDiscountCommerceAccountGroupRelLocalService;

	@Inject
	private CommerceDiscountUsageEntryLocalService
		_commerceDiscountUsageEntryLocalService;

	@DeleteAfterTestRun
	private CommerceInventoryWarehouse _commerceInventoryWarehouse;

	@Inject
	private CommerceOrderEngine _commerceOrderEngine;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	private final List<CommerceOrder> _commerceOrders = new ArrayList<>();

	@DeleteAfterTestRun
	private CommercePriceEntry _commercePriceEntry;

	@DeleteAfterTestRun
	private CommercePriceList _commercePriceList;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@DeleteAfterTestRun
	private CPDefinition _cpDefinition;

	@DeleteAfterTestRun
	private CPInstance _cpInstance;

	private Group _group;
	private ServiceContext _serviceContext;

}