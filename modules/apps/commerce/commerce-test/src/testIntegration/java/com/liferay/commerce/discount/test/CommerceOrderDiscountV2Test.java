/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalServiceUtil;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.discount.CommerceDiscountCalculation;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.test.util.CommerceDiscountTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.price.CommerceOrderPrice;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.test.util.CommercePriceEntryTestUtil;
import com.liferay.commerce.price.list.test.util.CommercePriceListTestUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTaxTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

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
 * @author Luca Pellizzon
 */
@RunWith(Arquillian.class)
public class CommerceOrderDiscountV2Test {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_accountEntry = CommerceAccountTestUtil.getPersonAccountEntry(
			_user.getUserId());

		_commerceCurrency =
			CommerceCurrencyLocalServiceUtil.fetchPrimaryCommerceCurrency(
				_group.getCompanyId());

		if (_commerceCurrency == null) {
			_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
				_group.getCompanyId());
		}

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());
	}

	@After
	public void tearDown() throws Exception {
		for (CommerceOrder commerceOrder : _commerceOrders) {
			_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
		}
	}

	@Test
	public void testDiscountSkuWithUnitOfMeasure() throws Exception {
		frutillaRule.scenario(
			"Discount on sku is applied to the order only if consistent"
		).given(
			"An order with some order items"
		).and(
			"A discount on one sku"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned given the quantity"
		);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		_commerceOrders.add(commerceOrder);

		commerceOrder.setCommerceCurrencyCode(_commerceCurrency.getCode());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), cpInstance.getCPInstanceId(), true,
				BigDecimal.ONE, "KEY",
				HashMapBuilder.put(
					LocaleUtil.getDefault(), "NOME"
				).build(),
				0, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
				cpInstance.getSku());

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(35), cpInstanceUnitOfMeasure.getKey());

		CommerceDiscount commerceDiscount =
			CommerceDiscountTestUtil.addFixedCommerceDiscount(
				_user.getGroupId(), BigDecimal.TEN,
				CommerceDiscountConstants.TARGET_SKUS,
				UnicodePropertiesBuilder.create(
					HashMapBuilder.put(
						"unitOfMeasureKey", cpInstanceUnitOfMeasure.getKey()
					).build(),
					true
				).build(),
				cpInstance.getCPInstanceId());

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		BigDecimal quantity = BigDecimal.ONE;
		BigDecimal orderedQuantity = BigDecimal.ONE;

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, quantity,
			cpInstance.getSku(), StringPool.BLANK);

		CommerceOrderItem commerceOrderItem =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), orderedQuantity,
				cpInstanceUnitOfMeasure.getKey());

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, null, _user, _group,
			commerceOrder);

		CommerceMoney totalCommerceMoney =
			_commerceOrderPriceCalculation.getTotal(
				commerceOrder, commerceContext);
		CommerceMoney subtotalCommerceMoney =
			_commerceOrderPriceCalculation.getSubtotal(
				commerceOrder, commerceContext);

		BigDecimal commercePriceEntryPrice = commercePriceEntry.getPrice();
		BigDecimal commerceDiscountLevel1 = commerceDiscount.getLevel1();

		BigDecimal expectedValue = commercePriceEntryPrice.subtract(
			commerceDiscountLevel1);

		BigDecimal finalPrice = commerceOrderItem.getFinalPrice();

		Assert.assertEquals(expectedValue, finalPrice.stripTrailingZeros());

		BigDecimal price = subtotalCommerceMoney.getPrice();

		Assert.assertEquals(expectedValue, price.stripTrailingZeros());

		price = totalCommerceMoney.getPrice();

		Assert.assertEquals(expectedValue, price.stripTrailingZeros());
	}

	@Test
	public void testDiscountWithCouponGreaterThanCartValue() throws Exception {
		frutillaRule.scenario(
			"Discount on total is applied to the order only if consistent"
		).given(
			"An order with some order items"
		).and(
			"A discount coupon on the total price"
		).when(
			"I try to get the final price of the order"
		).then(
			"The final price will be calculated with the discount"
		);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		_commerceOrders.add(commerceOrder);

		commerceOrder.setCommerceCurrencyCode(_commerceCurrency.getCode());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstanceDiscount =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstanceDiscount.getCPDefinition();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(),
			cpInstanceDiscount.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(),
			BigDecimal.valueOf(0.9));

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstanceDiscount.getSku(), StringPool.BLANK);

		String couponCode = StringUtil.randomString();

		CommerceDiscountTestUtil.addCouponCommerceDiscount(
			_user.getGroupId(), 1, couponCode,
			CommerceDiscountConstants.TARGET_TOTAL, null);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, null, _user, _group,
			commerceOrder);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(),
			cpInstanceDiscount.getCPInstanceId(), BigDecimal.ONE,
			commerceContext);

		commerceOrder = _commerceOrderLocalService.applyCouponCode(
			commerceOrder.getCommerceOrderId(), couponCode, commerceContext);

		CommerceMoney totalCommerceMoney =
			_commerceOrderPriceCalculation.getTotal(
				commerceOrder, commerceContext);

		BigDecimal price = BigDecimal.ZERO;
		BigDecimal totalPrice = totalCommerceMoney.getPrice();

		Assert.assertEquals(
			price.stripTrailingZeros(), totalPrice.stripTrailingZeros());
	}

	@Test
	public void testMultiTargetDiscounts() throws Exception {
		frutillaRule.scenario(
			"Discounts on multiple targets shall be applied on the order"
		).given(
			"An order with some order items"
		).and(
			"A discount on one product and a discount on the total price"
		).when(
			"I try to get the final price of the order"
		).then(
			"The final price will be calculated with the discounts"
		);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		_commerceOrders.add(commerceOrder);

		commerceOrder.setCommerceCurrencyCode(_commerceCurrency.getCode());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstanceDiscount =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());
		CPInstance cpInstancePlain =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstanceDiscount.getCPDefinition();
		CPDefinition cpDefinitionPlan = cpInstancePlain.getCPDefinition();

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		CommercePriceEntry commercePriceEntryDiscount =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstanceDiscount.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(25));

		CommercePriceEntry commercePriceEntryPlain =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinitionPlan.getCProductId(),
				cpInstancePlain.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(10));

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		BigDecimal quantity = BigDecimal.TEN;
		BigDecimal orderedQuantity = BigDecimal.ONE;

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, quantity,
			cpInstanceDiscount.getSku(), StringPool.BLANK);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, quantity,
			cpInstancePlain.getSku(), StringPool.BLANK);

		CommerceDiscount commerceDiscount1 =
			CommerceDiscountTestUtil.addFixedCommerceDiscount(
				_user.getGroupId(), 10,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				cpDefinition.getCPDefinitionId());

		CommerceDiscount commerceDiscount2 =
			CommerceDiscountTestUtil.addFixedCommerceDiscount(
				_user.getGroupId(), 10, CommerceDiscountConstants.TARGET_TOTAL,
				null);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(),
			cpInstanceDiscount.getCPInstanceId(), orderedQuantity);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(),
			cpInstancePlain.getCPInstanceId(), orderedQuantity);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, null, _user, _group,
			commerceOrder);

		CommerceMoney totalCommerceMoney =
			_commerceOrderPriceCalculation.getTotal(
				commerceOrder, commerceContext);
		CommerceMoney subtotalCommerceMoney =
			_commerceOrderPriceCalculation.getSubtotal(
				commerceOrder, commerceContext);

		BigDecimal prod1Price = commercePriceEntryDiscount.getPrice();
		BigDecimal prod2Price = commercePriceEntryPlain.getPrice();

		BigDecimal prod1TotalPrice = prod1Price.multiply(orderedQuantity);
		BigDecimal prod2TotalPrice = prod2Price.multiply(orderedQuantity);

		BigDecimal expectedCartValue = prod1TotalPrice.add(prod2TotalPrice);

		BigDecimal discount1Level1 = commerceDiscount1.getLevel1();
		BigDecimal discount2Level1 = commerceDiscount2.getLevel1();

		BigDecimal expectedSubtotal = expectedCartValue.subtract(
			discount1Level1);

		BigDecimal expectedTotal = expectedSubtotal.subtract(discount2Level1);

		BigDecimal subtotalPrice = subtotalCommerceMoney.getPrice();
		BigDecimal totalPrice = totalCommerceMoney.getPrice();

		Assert.assertEquals(
			expectedSubtotal.stripTrailingZeros(),
			subtotalPrice.stripTrailingZeros());
		Assert.assertEquals(
			expectedTotal.stripTrailingZeros(),
			totalPrice.stripTrailingZeros());
	}

	@Test
	public void testMultiTargetDiscountsWithCoupon() throws Exception {
		frutillaRule.scenario(
			"Discounts on multiple targets shall be applied on the order"
		).given(
			"An order with some order items"
		).and(
			"A discount on one product and a discount coupon on the total price"
		).when(
			"I try to get the final price of the order"
		).then(
			"The final price will be calculated with the discounts"
		);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		_commerceOrders.add(commerceOrder);

		commerceOrder.setCommerceCurrencyCode(_commerceCurrency.getCode());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstanceDiscount =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());
		CPInstance cpInstancePlain =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstanceDiscount.getCPDefinition();
		CPDefinition cpDefinitionPlan = cpInstancePlain.getCPDefinition();

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		CommercePriceEntry commercePriceEntryDiscount =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstanceDiscount.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(25));

		CommercePriceEntry commercePriceEntryPlain =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinitionPlan.getCProductId(),
				cpInstancePlain.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(10));

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		BigDecimal quantity = BigDecimal.TEN;
		BigDecimal orderedQuantity = BigDecimal.ONE;

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, quantity,
			cpInstanceDiscount.getSku(), StringPool.BLANK);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, quantity,
			cpInstancePlain.getSku(), StringPool.BLANK);

		CommerceDiscount commerceDiscount1 =
			CommerceDiscountTestUtil.addFixedCommerceDiscount(
				_user.getGroupId(), 10,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				cpDefinition.getCPDefinitionId());

		String couponCode = StringUtil.randomString();

		CommerceDiscount commerceDiscount2 =
			CommerceDiscountTestUtil.addCouponCommerceDiscount(
				_user.getGroupId(), 10, couponCode,
				CommerceDiscountConstants.TARGET_TOTAL, null);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, null, _user, _group,
			commerceOrder);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(),
			cpInstanceDiscount.getCPInstanceId(), orderedQuantity,
			commerceContext);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(),
			cpInstancePlain.getCPInstanceId(), orderedQuantity,
			commerceContext);

		commerceOrder = _commerceOrderLocalService.applyCouponCode(
			commerceOrder.getCommerceOrderId(), couponCode, commerceContext);

		CommerceMoney totalCommerceMoney =
			_commerceOrderPriceCalculation.getTotal(
				commerceOrder, commerceContext);
		CommerceMoney subtotalCommerceMoney =
			_commerceOrderPriceCalculation.getSubtotal(
				commerceOrder, commerceContext);

		BigDecimal prod1Price = commercePriceEntryDiscount.getPrice();
		BigDecimal prod2Price = commercePriceEntryPlain.getPrice();

		BigDecimal prod1TotalPrice = prod1Price.multiply(orderedQuantity);
		BigDecimal prod2TotalPrice = prod2Price.multiply(orderedQuantity);

		BigDecimal expectedCartValue = prod1TotalPrice.add(prod2TotalPrice);

		BigDecimal discount1Level1 = commerceDiscount1.getLevel1();
		BigDecimal discount2Level1 = commerceDiscount2.getLevel1();

		BigDecimal expectedSubtotal = expectedCartValue.subtract(
			discount1Level1);

		BigDecimal expectedTotal = expectedSubtotal.subtract(discount2Level1);

		BigDecimal subtotalPrice = subtotalCommerceMoney.getPrice();
		BigDecimal totalPrice = totalCommerceMoney.getPrice();

		Assert.assertEquals(
			expectedSubtotal.stripTrailingZeros(),
			subtotalPrice.stripTrailingZeros());
		Assert.assertEquals(
			expectedTotal.stripTrailingZeros(),
			totalPrice.stripTrailingZeros());
	}

	@Test
	public void testTaxValueWithSubtotalDiscount() throws Exception {
		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		_commerceOrders.add(commerceOrder);

		commerceOrder.setCommerceCurrencyCode(_commerceCurrency.getCode());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId(), RandomTestUtil.randomLong(), false);

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(30));

		CommerceDiscount commerceDiscount =
			CommerceDiscountTestUtil.addChannelOrderCommerceDiscount(
				commerceCatalog.getGroupId(),
				_commerceChannel.getCommerceChannelId(),
				CommerceDiscountConstants.TARGET_SUBTOTAL);

		CommerceTaxFixedRate commerceTaxFixedRate =
			CommerceTestUtil.addCommerceTaxFixedRate(
				_user.getUserId(), _commerceChannel.getGroupId(),
				cpDefinition.getCPTaxCategoryId(), true, true);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, BigDecimal.ONE,
			cpInstance.getSku(), StringPool.BLANK);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user,
			commerceCatalog.getGroup(), commerceOrder);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(), cpInstance.getCPInstanceId(),
			BigDecimal.ONE, commerceContext);

		CommerceOrderPrice commerceOrderPrice =
			_commerceOrderPriceCalculation.getCommerceOrderPrice(
				commerceOrder, commerceContext);

		BigDecimal expectedSubtotalValue = commerceOrderPrice.getSubtotal(
		).getPrice();
		BigDecimal subtotalDiscountAmount =
			commerceOrderPrice.getSubtotalDiscountValue(
			).getDiscountAmount(
			).getPrice();

		expectedSubtotalValue = expectedSubtotalValue.subtract(
			subtotalDiscountAmount);

		BigDecimal subtotalPrice = commercePriceEntry.getPrice();
		BigDecimal discount = commerceDiscount.getLevel1();

		subtotalPrice = subtotalPrice.subtract(discount);
		subtotalPrice = subtotalPrice.round(
			new MathContext(
				expectedSubtotalValue.precision(), RoundingMode.HALF_EVEN));

		Assert.assertEquals(expectedSubtotalValue, subtotalPrice);

		BigDecimal expectedTotalValue =
			CommerceTaxTestUtil.getPriceWithTaxAmount(
				expectedSubtotalValue,
				BigDecimal.valueOf(commerceTaxFixedRate.getRate()),
				RoundingMode.HALF_EVEN);

		CommerceDiscountValue totalCommerceDiscountValue =
			_commerceDiscountCalculation.getOrderTotalCommerceDiscountValue(
				commerceOrder, expectedTotalValue, commerceContext);

		CommerceMoney discountAmountCommerceMoney =
			totalCommerceDiscountValue.getDiscountAmount();

		expectedTotalValue = expectedTotalValue.subtract(
			discountAmountCommerceMoney.getPrice()
		).round(
			new MathContext(
				_commerceCurrency.getMaxFractionDigits(),
				RoundingMode.HALF_EVEN)
		);

		CommerceMoney totalCommerceMoney =
			_commerceOrderPriceCalculation.getTotal(
				commerceOrder, commerceContext);

		BigDecimal totalPrice = totalCommerceMoney.getPrice(
		).round(
			new MathContext(
				_commerceCurrency.getMaxFractionDigits(),
				RoundingMode.HALF_EVEN)
		);

		Assert.assertEquals(expectedTotalValue, totalPrice);
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private static User _user;

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceDiscountCalculation _commerceDiscountCalculation;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	private final List<CommerceOrder> _commerceOrders = new ArrayList<>();

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	private Group _group;

}