/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.exception.CommerceOrderShippingAddressException;
import com.liferay.commerce.exception.CommerceShipmentInactiveWarehouseException;
import com.liferay.commerce.exception.CommerceShipmentItemQuantityException;
import com.liferay.commerce.exception.DuplicateCommerceShipmentExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.exception.NoSuchShipmentItemException;
import com.liferay.commerce.helper.CommerceShippingHelper;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.price.CommerceOrderPrice;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.commerce.shipment.test.util.CommerceShipmentTestUtil;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTaxTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
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
public class CommerceShipmentTest {

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

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());
	}

	@After
	public void tearDown() throws Exception {
		for (CommerceOrder commerceOrder : _commerceOrders) {
			_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
		}

		_commerceCurrencyLocalService.deleteCommerceCurrency(_commerceCurrency);
	}

	@Test
	public void testCheckoutWihoutShippingAddress() throws Exception {
		frutillaRule.scenario(
			"It should not be possible to create an order without shipment " +
				"address"
		).given(
			"An order is created without a shipping address"
		).when(
			"Checkout is executed"
		).then(
			"An exception is raised"
		);

		try {
			BigDecimal value = BigDecimal.valueOf(RandomTestUtil.nextDouble());

			CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_commerceCurrency);

			CommerceShippingMethod commerceShippingMethod =
				CommerceTestUtil.addFixedRateCommerceShippingMethod(
					_user.getUserId(), commerceOrder.getGroupId(), value);

			commerceOrder.setCommerceShippingMethodId(
				commerceShippingMethod.getCommerceShippingMethodId());

			commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
				commerceOrder);

			_commerceOrders.add(commerceOrder);

			_commerceOrderEngine.checkoutCommerceOrder(
				commerceOrder, _user.getUserId());
		}
		catch (PortalException portalException) {
			Throwable throwable = portalException.getCause();

			Assert.assertSame(
				CommerceOrderShippingAddressException.class,
				throwable.getClass());
		}
	}

	@Test
	public void testCreateMultipleShippingForSameOrder() throws Exception {
		frutillaRule.scenario(
			"An order with multiple shipments is created"
		).given(
			"An order is created with a shipment associated"
		).when(
			"I try to add a second shipment to the same order"
		).then(
			"Multiple shipments are created"
		);

		BigDecimal value = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		commerceOrder = CommerceTestUtil.addCommerceOrderShippingDetails(
			commerceOrder, value);

		_commerceOrders.add(commerceOrder);

		CommerceShipment commerceShipment1 =
			CommerceShipmentTestUtil.createEmptyOrderShipment(
				commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId());

		CommerceShipment commerceShipment2 =
			CommerceShipmentTestUtil.createEmptyOrderShipment(
				commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId());

		Assert.assertEquals(
			commerceOrder.getGroupId(), commerceShipment1.getGroupId());
		Assert.assertEquals(
			commerceOrder.getGroupId(), commerceShipment2.getGroupId());
	}

	@Test
	public void testCreateOrderShipment() throws Exception {
		frutillaRule.scenario(
			"An order with a shipment is created"
		).given(
			"An order"
		).when(
			"I add a shipment on that order"
		).then(
			"The shipment is associated to the order"
		);

		BigDecimal value = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		commerceOrder = CommerceTestUtil.addCommerceOrderShippingDetails(
			commerceOrder, value);

		_commerceOrders.add(commerceOrder);

		CommerceShipment commerceShipment =
			CommerceShipmentTestUtil.createEmptyOrderShipment(
				commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId());

		Assert.assertEquals(
			commerceOrder.getCommerceAccountId(),
			commerceShipment.getCommerceAccountId());
		Assert.assertEquals(
			commerceOrder.getGroupId(), commerceShipment.getGroupId());
		Assert.assertEquals(
			commerceOrder.getShippingAddressId(),
			commerceShipment.getCommerceAddressId());
	}

	@Test
	public void testCreateOrderWithItemsShipment() throws Exception {
		frutillaRule.scenario(
			"An order with a shipment is created"
		).given(
			"An order"
		).when(
			"I add an item to the order"
		).then(
			"The shipment is associated with the order and to the items"
		);

		CPInstance cpInstance = _createCPInstance();

		BigDecimal value = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		CommerceContext commerceContext = new TestCommerceContext(
			null, commerceOrder.getCommerceCurrency(), _commerceChannel, null,
			null, commerceOrder);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_createCommerceInventoryWarehouse(
				_commerceChannel.getCommerceChannelId(), true);

		BigDecimal quantity = BigDecimal.TEN;

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, quantity,
			cpInstance.getSku(), StringPool.BLANK);

		BigDecimal orderedQuantity = BigDecimal.ONE;

		CommerceOrderItem commerceOrderItem =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), orderedQuantity, commerceContext);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		commerceOrder = CommerceTestUtil.addCommerceOrderShippingDetails(
			commerceOrder, value);

		_commerceOrders.add(commerceOrder);

		CommerceShipment commerceShipment =
			CommerceShipmentTestUtil.createOrderShipment(
				commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId(),
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId());

		Assert.assertEquals(
			commerceOrder.getCommerceAccountId(),
			commerceShipment.getCommerceAccountId());
		Assert.assertEquals(
			commerceOrder.getGroupId(), commerceShipment.getGroupId());
		Assert.assertEquals(
			orderedQuantity.intValue(),
			_commerceShipmentItemLocalService.getCommerceShipmentItemsCount(
				commerceShipment.getCommerceShipmentId()));

		List<CommerceShipmentItem> commerceShipmentItems =
			_commerceShipmentItemLocalService.getCommerceShipmentItems(
				commerceShipment.getCommerceShipmentId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		CommerceShipmentItem commerceShipmentItem = commerceShipmentItems.get(
			0);

		Assert.assertEquals(
			commerceOrderItem.getCommerceOrderItemId(),
			commerceShipmentItem.getCommerceOrderItemId());
		Assert.assertEquals(
			commerceShipment, commerceShipmentItem.getCommerceShipment());
	}

	@Test
	public void testCreateOrderWithShippingAmount() throws Exception {
		frutillaRule.scenario(
			"When a shipping amount is associated with an order the price " +
				"object is correctly set"
		).given(
			"An order"
		).and(
			"A shipment with a shipping amount set"
		).when(
			"I attach the shipment to the order"
		).then(
			"The price object has the shipping amount correctly set"
		);

		CPInstance cpInstance = _createCPInstance(BigDecimal.valueOf(25));

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_createCommerceInventoryWarehouse(
				_commerceChannel.getCommerceChannelId(), true);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstance.getSku(), StringPool.BLANK);

		BigDecimal value = BigDecimal.valueOf(5);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		CommerceContext commerceContext = new TestCommerceContext(
			commerceOrder.getAccountEntry(),
			commerceOrder.getCommerceCurrency(), _commerceChannel, null, null,
			commerceOrder);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(), cpInstance.getCPInstanceId(),
			BigDecimal.ONE, commerceContext);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		commerceOrder = CommerceTestUtil.addCommerceOrderShippingDetails(
			commerceOrder, value);

		_commerceOrders.add(commerceOrder);

		CommerceOrderPrice commerceOrderPrice =
			_commerceOrderPriceCalculation.getCommerceOrderPrice(
				commerceOrder, false, commerceContext);

		CommerceMoney shippableValueCommerceMoney =
			commerceOrderPrice.getShippingValue();

		Assert.assertEquals(value, shippableValueCommerceMoney.getPrice());
	}

	@Test
	public void testCreateOrderWithShippingDiscount() throws Exception {
		frutillaRule.scenario(
			"When a shipping discount is associated with an order the price " +
				"object is correctly set"
		).given(
			"An order"
		).and(
			"A shipment with a shipping discount set"
		).when(
			"I attach the shipment to the order"
		).then(
			"The price object has the shipping discounts correctly set"
		);

		CPInstance cpInstance = _createCPInstance(BigDecimal.valueOf(25));

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_createCommerceInventoryWarehouse(
				_commerceChannel.getCommerceChannelId(), true);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstance.getSku(), StringPool.BLANK);

		BigDecimal value = BigDecimal.valueOf(5);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		CommerceContext commerceContext = new TestCommerceContext(
			commerceOrder.getAccountEntry(),
			commerceOrder.getCommerceCurrency(), _commerceChannel, null, null,
			commerceOrder);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(), cpInstance.getCPInstanceId(),
			BigDecimal.ONE, commerceContext);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		commerceOrder = CommerceTestUtil.addCommerceOrderShippingDetails(
			commerceOrder, value);

		BigDecimal expectedDiscountAmount = BigDecimal.valueOf(3);

		commerceOrder.setShippingDiscountAmount(expectedDiscountAmount);

		BigDecimal expectedL1Discount = BigDecimal.valueOf(1);

		commerceOrder.setShippingDiscountPercentageLevel1(expectedL1Discount);

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		CommerceOrderPrice commerceOrderPrice =
			_commerceOrderPriceCalculation.getCommerceOrderPrice(
				commerceOrder, false, commerceContext);

		CommerceMoney shippableValueCommerceMoney =
			commerceOrderPrice.getShippingValue();

		CommerceDiscountValue commerceDiscountValue =
			commerceOrderPrice.getShippingDiscountValue();

		CommerceMoney discountAmountCommerceMoney =
			commerceDiscountValue.getDiscountAmount();
		BigDecimal[] percentages = commerceDiscountValue.getPercentages();

		Assert.assertEquals(value, shippableValueCommerceMoney.getPrice());
		Assert.assertEquals(
			expectedDiscountAmount, discountAmountCommerceMoney.getPrice());
		Assert.assertEquals(expectedL1Discount, percentages[0]);
	}

	@Test
	public void testCreateSeparateItemShippings() throws Exception {
		frutillaRule.scenario(
			"An order with 2 different shipping items"
		).given(
			"An order"
		).and(
			"2 different CPInstance and 2 different shipments for the items"
		).when(
			"I attach the shipments to their respective items"
		).then(
			"The items shall be correctly associated with their shipping " +
				"details"
		);

		CPInstance cpInstance1 = _createCPInstance();

		CPInstance cpInstance2 = _createCPInstance(cpInstance1.getGroupId());

		BigDecimal value = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		CommerceShipment commerceShipment1 =
			CommerceShipmentTestUtil.createEmptyOrderShipment(
				commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId());

		CommerceShipment commerceShipment2 =
			CommerceShipmentTestUtil.createEmptyOrderShipment(
				commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId());

		CommerceContext commerceContext = new TestCommerceContext(
			null, commerceOrder.getCommerceCurrency(), _commerceChannel, null,
			null, commerceOrder);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_createCommerceInventoryWarehouse(
				_commerceChannel.getCommerceChannelId(), true);

		BigDecimal quantity = BigDecimal.TEN;

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, quantity,
			cpInstance1.getSku(), StringPool.BLANK);
		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, quantity,
			cpInstance2.getSku(), StringPool.BLANK);

		BigDecimal orderedQuantity = BigDecimal.ONE;

		CommerceOrderItem commerceOrderItem1 =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance1.getCPInstanceId(), orderedQuantity,
				commerceContext);

		CommerceOrderItem commerceOrderItem2 =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance2.getCPInstanceId(), orderedQuantity,
				commerceContext);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		commerceOrder = CommerceTestUtil.addCommerceOrderShippingDetails(
			commerceOrder, value);

		_commerceOrders.add(commerceOrder);

		BigDecimal commerceOrderItem1Quantity =
			commerceOrderItem1.getQuantity();

		CommerceShipmentItem commerceShipmentItem1 =
			_commerceShipmentItemLocalService.addCommerceShipmentItem(
				null, commerceShipment1.getCommerceShipmentId(),
				commerceOrderItem1.getCommerceOrderItemId(),
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				commerceOrderItem1Quantity, null, true,
				ServiceContextTestUtil.getServiceContext(
					commerceOrder.getGroupId()));

		BigDecimal commerceOrderItem2Quantity =
			commerceOrderItem2.getQuantity();

		CommerceShipmentItem commerceShipmentItem2 =
			_commerceShipmentItemLocalService.addCommerceShipmentItem(
				null, commerceShipment2.getCommerceShipmentId(),
				commerceOrderItem2.getCommerceOrderItemId(),
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				commerceOrderItem2Quantity, null, true,
				ServiceContextTestUtil.getServiceContext(
					commerceOrder.getGroupId()));

		Assert.assertEquals(
			commerceShipment1, commerceShipmentItem1.getCommerceShipment());
		Assert.assertEquals(
			commerceShipment2, commerceShipmentItem2.getCommerceShipment());
	}

	@Test
	public void testCreateShipmentWithNonshippableItem() throws Exception {
		frutillaRule.scenario(
			"Attach a shipment to a non-shippable item"
		).given(
			"An order with an order item created as non-shippable"
		).when(
			"I attach the shipment to the order and its items"
		).then(
			"The order shall be non-shippable"
		);

		CPInstance cpInstance = _createCPInstance();

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setShippable(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		cpInstance = _cpInstanceLocalService.getCPInstance(
			cpDefinition.getCPDefinitionId(), cpInstance.getSku());

		BigDecimal value = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		CommerceContext commerceContext = new TestCommerceContext(
			null, commerceOrder.getCommerceCurrency(), _commerceChannel, null,
			null, commerceOrder);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_createCommerceInventoryWarehouse(
				_commerceChannel.getCommerceChannelId(), true);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstance.getSku(), StringPool.BLANK);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(), cpInstance.getCPInstanceId(),
			BigDecimal.ONE, commerceContext);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		commerceOrder = CommerceTestUtil.addCommerceOrderShippingDetails(
			commerceOrder, value);

		_commerceOrders.add(commerceOrder);

		CommerceShipmentTestUtil.createOrderShipment(
			commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId(),
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId());

		Assert.assertFalse(_commerceShippingHelper.isShippable(commerceOrder));
	}

	@Test
	public void testCreateShippingForItemsOnly() throws Exception {
		frutillaRule.scenario(
			"Attach a shipment on the order items without a shipment to the " +
				"order"
		).given(
			"An order is created"
		).and(
			"2 order items are added to the order"
		).when(
			"I attach the shipment to the items"
		).then(
			"Each item shall have a shipment item associated"
		);

		CPInstance cpInstance1 = _createCPInstance();

		CPInstance cpInstance2 = _createCPInstance(cpInstance1.getGroupId());

		BigDecimal value = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		CommerceContext commerceContext = new TestCommerceContext(
			null, commerceOrder.getCommerceCurrency(), _commerceChannel, null,
			null, commerceOrder);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_createCommerceInventoryWarehouse(
				_commerceChannel.getCommerceChannelId(), true);

		BigDecimal quantity = BigDecimal.TEN;

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, quantity,
			cpInstance1.getSku(), StringPool.BLANK);
		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, quantity,
			cpInstance2.getSku(), StringPool.BLANK);

		BigDecimal orderedQuantity = BigDecimal.ONE;

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(), cpInstance1.getCPInstanceId(),
			orderedQuantity, commerceContext);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(), cpInstance2.getCPInstanceId(),
			orderedQuantity, commerceContext);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		commerceOrder = CommerceTestUtil.addCommerceOrderShippingDetails(
			commerceOrder, value);

		_commerceOrders.add(commerceOrder);

		CommerceShipmentTestUtil.createOrderItemsOnlyShipment(
			commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId(),
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId());

		List<CommerceOrderItem> commerceOrderItems =
			_commerceOrderItemLocalService.getCommerceOrderItems(
				commerceOrder.getCommerceOrderId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			List<CommerceShipmentItem> commerceShipmentItems =
				_commerceShipmentItemLocalService.
					getCommerceShipmentItemsByCommerceOrderItemId(
						commerceOrderItem.getCommerceOrderItemId());

			Assert.assertEquals(
				commerceShipmentItems.toString(), 1,
				commerceShipmentItems.size());
		}
	}

	@Test(expected = CommerceShipmentInactiveWarehouseException.class)
	public void testCreateShippingFromInactiveWarehouse() throws Exception {
		frutillaRule.scenario(
			"Attach a shipment to a item that is stocked in an inactive " +
				"warehouse"
		).given(
			"An order with an order item associated with an inactive warehouse"
		).when(
			"I attach the shipment to the order and its items"
		).then(
			"An exception shall be raised"
		);

		CPInstance cpInstance = _createCPInstance();

		BigDecimal value = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		CommerceContext commerceContext = new TestCommerceContext(
			null, commerceOrder.getCommerceCurrency(), _commerceChannel, null,
			null, commerceOrder);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_createCommerceInventoryWarehouse(
				_commerceChannel.getCommerceChannelId(), false);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstance.getSku(), StringPool.BLANK);

		BigDecimal orderedQuantity = BigDecimal.ONE;

		CommerceOrderItem commerceOrderItem =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), orderedQuantity, commerceContext);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		commerceOrder = CommerceTestUtil.addCommerceOrderShippingDetails(
			commerceOrder, value);

		_commerceOrders.add(commerceOrder);

		CommerceShipment commerceShipment =
			CommerceShipmentTestUtil.createEmptyOrderShipment(
				commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId());

		_commerceShipmentItemLocalService.addCommerceShipmentItem(
			null, commerceShipment.getCommerceShipmentId(),
			commerceOrderItem.getCommerceOrderItemId(),
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			orderedQuantity, null, true,
			ServiceContextTestUtil.getServiceContext(
				commerceOrder.getGroupId()));
	}

	@Test(expected = NoSuchOrderException.class)
	public void testCreateShippingWithNoOrder() throws Exception {
		frutillaRule.scenario(
			"Attach a shipment to a nonexistent order"
		).given(
			"A channel"
		).when(
			"I attach the shipment using the channel groupId"
		).then(
			"An exception shall be raised"
		);

		CommerceShipmentTestUtil.createEmptyOrderShipment(
			_commerceChannel.getGroupId(), 0);
	}

	@Test
	public void testDeleteOrderShipment() throws Exception {
		frutillaRule.scenario(
			"An order with a shipment is created"
		).given(
			"An order"
		).when(
			"I delete a shipment on that order"
		).then(
			"The shipment is deleted to the order"
		);

		CPInstance cpInstance = _createCPInstance();

		BigDecimal value = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		CommerceOrder commerceOrder =
			CommerceTestUtil.createCommerceOrderForShipping(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_commerceCurrency.getCommerceCurrencyId(),
				cpInstance.getCPInstanceId(), value, BigDecimal.ONE, 1);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_createCommerceInventoryWarehouse(
				_commerceChannel.getCommerceChannelId(), true);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse,
			BigDecimal.valueOf(5), cpInstance.getSku(), StringPool.BLANK);

		_commerceOrders.add(commerceOrder);

		CommerceShipment commerceShipment =
			CommerceShipmentTestUtil.createOrderShipment(
				commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId(),
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId());

		List<CommerceShipment> commerceShipments =
			_commerceShipmentLocalService.getCommerceShipments(
				commerceOrder.getCommerceOrderId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceShipments.toString(), 1, commerceShipments.size());

		_commerceShipmentLocalService.deleteCommerceShipment(commerceShipment);

		commerceShipments = _commerceShipmentLocalService.getCommerceShipments(
			commerceOrder.getCommerceOrderId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceShipments.toString(), 0, commerceShipments.size());
	}

	@Test
	public void testShippingAmountAndShippingTaxes() throws Exception {
		frutillaRule.scenario(
			"When a shipping amount with tax is associated with an order the " +
				"price object is correctly set"
		).given(
			"An order"
		).and(
			"A shipment with a shipping amount and tax on the shipment is set"
		).when(
			"I attach the shipment to the order"
		).then(
			"The price object has the shipping amount correctly set"
		);

		long commerceTaxCategoryId = CommerceTaxTestUtil.addTaxCategoryId(
			_user.getGroupId());

		CommerceTaxMethod commerceTaxMethod =
			CommerceTaxTestUtil.addCommerceByAddressTaxMethod(
				_user.getUserId(), _commerceChannel.getGroupId(), true);

		double shippingTaxRate = 10;

		CommerceTaxTestUtil.setCommerceMethodTaxRate(
			_user.getUserId(), _commerceChannel.getGroupId(),
			commerceTaxCategoryId, commerceTaxMethod.getCommerceTaxMethodId(),
			shippingTaxRate);

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				_commerceChannel.getGroupId(),
				CommerceConstants.SERVICE_NAME_COMMERCE_TAX));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		modifiableSettings.setValue(
			"taxCategoryId", String.valueOf(commerceTaxCategoryId));

		modifiableSettings.store();

		CPInstance cpInstance = _createCPInstance(BigDecimal.valueOf(25));

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_createCommerceInventoryWarehouse(
				_commerceChannel.getCommerceChannelId(), true);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstance.getSku(), StringPool.BLANK);

		BigDecimal value = BigDecimal.valueOf(5);

		CommerceOrder commerceOrder =
			CommerceTestUtil.createCommerceOrderForShipping(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_commerceCurrency.getCommerceCurrencyId(),
				cpInstance.getCPInstanceId(), value, BigDecimal.ONE, 1);

		_commerceOrders.add(commerceOrder);

		CommerceContext commerceContext = new TestCommerceContext(
			commerceOrder.getAccountEntry(),
			commerceOrder.getCommerceCurrency(), _commerceChannel, null, null,
			commerceOrder);

		CommerceOrderPrice commerceOrderPrice =
			_commerceOrderPriceCalculation.getCommerceOrderPrice(
				commerceOrder, false, commerceContext);

		CommerceMoney shippableValueCommerceMoney =
			commerceOrderPrice.getShippingValue();

		Assert.assertEquals(value, shippableValueCommerceMoney.getPrice());

		BigDecimal expectedTaxValue = value.multiply(
			BigDecimal.valueOf(shippingTaxRate));

		expectedTaxValue = expectedTaxValue.divide(BigDecimal.valueOf(100));

		CommerceMoney taxValueCommerceMoney = commerceOrderPrice.getTaxValue();

		BigDecimal taxValue = taxValueCommerceMoney.getPrice();

		Assert.assertEquals(expectedTaxValue, taxValue.stripTrailingZeros());

		modifiableSettings.setValue("taxCategoryId", String.valueOf(0));

		modifiableSettings.store();
	}

	@Test
	public void testTranslateShippingMethods() throws Exception {
		frutillaRule.scenario(
			"Flat Rate shipping method name and description are translated"
		).given(
			"A shipping method name and description"
		).when(
			"I set the language"
		).then(
			"Shipping method name and description are translated"
		);

		String translatedShippingMethodName = LanguageUtil.get(
			LocaleUtil.JAPANESE, "flat-rate");
		String translatedShippingMethodDescription = LanguageUtil.get(
			LocaleUtil.JAPANESE, "fixed-shipping-description");

		CommerceShippingMethod commerceShippingMethod =
			CommerceTestUtil.addCommerceShippingMethod(
				_user.getUserId(), _commerceChannel.getGroupId(),
				HashMapBuilder.put(
					LocaleUtil.JAPAN, translatedShippingMethodName
				).put(
					PortalUtil.getSiteDefaultLocale(_group),
					RandomTestUtil.randomString()
				).build(),
				HashMapBuilder.put(
					LocaleUtil.JAPAN, translatedShippingMethodDescription
				).put(
					PortalUtil.getSiteDefaultLocale(_group),
					RandomTestUtil.randomString()
				).build(),
				true, "fixed");

		Assert.assertEquals(
			translatedShippingMethodDescription,
			commerceShippingMethod.getDescription(LocaleUtil.JAPAN));
		Assert.assertEquals(
			translatedShippingMethodName,
			commerceShippingMethod.getName(LocaleUtil.JAPAN));
	}

	@Test(
		expected = DuplicateCommerceShipmentExternalReferenceCodeException.class
	)
	public void testUpdateOrderShipment() throws Exception {
		frutillaRule.scenario(
			"It should not be possible to update the ERC field with a value " +
				"that already exists"
		).given(
			"An order with an order item associated with an active warehouse"
		).when(
			"I update the ERC field"
		).then(
			"An exception shall be raised"
		);

		CPInstance cpInstance = _createCPInstance();

		BigDecimal value = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		CommerceOrder commerceOrder =
			CommerceTestUtil.createCommerceOrderForShipping(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_commerceCurrency.getCommerceCurrencyId(),
				cpInstance.getCPInstanceId(), value, BigDecimal.ONE, 1);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_createCommerceInventoryWarehouse(
				_commerceChannel.getCommerceChannelId(), true);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse,
			BigDecimal.valueOf(5), cpInstance.getSku(), StringPool.BLANK);

		_commerceOrders.add(commerceOrder);

		CommerceShipment commerceShipment =
			CommerceShipmentTestUtil.createOrderShipment(
				commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId(),
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId());

		String externalReferenceCode = "externalReferenceCode";

		_commerceShipmentLocalService.updateExternalReferenceCode(
			commerceShipment.getCommerceShipmentId(), externalReferenceCode);

		CommerceShipment newCommerceShipment =
			CommerceShipmentTestUtil.createOrderShipment(
				commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId(),
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId());

		_commerceShipmentLocalService.updateExternalReferenceCode(
			newCommerceShipment.getCommerceShipmentId(), externalReferenceCode);

		_commerceShipmentLocalService.deleteCommerceShipment(commerceShipment);
		_commerceShipmentLocalService.deleteCommerceShipment(
			newCommerceShipment);
	}

	@Test(expected = CommerceShipmentItemQuantityException.class)
	public void testUpdateShippingItemQuantity() throws Exception {
		frutillaRule.scenario(
			"Update ordered quantity for shipping item to be greater than " +
				"inventory availability"
		).given(
			"An order with an order item associated with an active warehouse"
		).when(
			"I update the ordered quantity to a value greater than inventory " +
				"availability"
		).then(
			"An exception shall be raised"
		);

		CPInstance cpInstance = _createCPInstance();

		BigDecimal value = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		CommerceContext commerceContext = new TestCommerceContext(
			null, commerceOrder.getCommerceCurrency(), _commerceChannel, null,
			null, commerceOrder);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_createCommerceInventoryWarehouse(
				_commerceChannel.getCommerceChannelId(), true);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse,
			BigDecimal.valueOf(5), cpInstance.getSku(), StringPool.BLANK);

		CommerceOrderItem commerceOrderItem =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.ONE, commerceContext);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		commerceOrder = CommerceTestUtil.addCommerceOrderShippingDetails(
			commerceOrder, value);

		_commerceOrders.add(commerceOrder);

		CommerceShipmentTestUtil.createOrderShipment(
			commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId(),
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId());

		List<CommerceShipmentItem> commerceShipmentItems =
			_commerceShipmentItemLocalService.
				getCommerceShipmentItemsByCommerceOrderItemId(
					commerceOrderItem.getCommerceOrderItemId());

		CommerceShipmentItem commerceShipmentItem = commerceShipmentItems.get(
			0);

		BigDecimal newOrderedQuantity = new BigDecimal(10);

		_commerceShipmentItemLocalService.updateCommerceShipmentItem(
			commerceShipmentItem.getCommerceShipmentItemId(),
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			newOrderedQuantity, true);
	}

	@Test(expected = NoSuchShipmentItemException.class)
	public void testUpdateShippingItemQuantityWithNoShipping()
		throws Exception {

		frutillaRule.scenario(
			"It should not be possible to update the shipping quantity of an " +
				"item not associated with a shipment"
		).given(
			"An order with an order item associated with an active warehouse"
		).when(
			"I update the ordered quantity"
		).then(
			"An exception shall be raised"
		);

		CPInstance cpInstance = _createCPInstance();

		BigDecimal value = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		BigDecimal orderedQuantity = BigDecimal.ONE;

		CommerceOrder commerceOrder =
			CommerceTestUtil.createCommerceOrderForShipping(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_commerceCurrency.getCommerceCurrencyId(),
				cpInstance.getCPInstanceId(), value, orderedQuantity, 1);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_createCommerceInventoryWarehouse(
				_commerceChannel.getCommerceChannelId(), true);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse,
			BigDecimal.valueOf(5), cpInstance.getSku(), StringPool.BLANK);

		_commerceOrders.add(commerceOrder);

		_commerceShipmentItemLocalService.updateCommerceShipmentItem(
			0, commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			orderedQuantity, true);
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private CommerceInventoryWarehouse _createCommerceInventoryWarehouse(
			long commerceChannelId, boolean active)
		throws Exception {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				active,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CommerceInventoryWarehouse.class.getName(),
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			commerceChannelId,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		return commerceInventoryWarehouse;
	}

	private CPInstance _createCPInstance() throws Exception {
		return CPTestUtil.addCPInstanceWithRandomSku(_group.getGroupId());
	}

	private CPInstance _createCPInstance(BigDecimal price) throws Exception {
		return CPTestUtil.addCPInstanceWithRandomSku(
			_group.getGroupId(), price);
	}

	private CPInstance _createCPInstance(long groupId) throws Exception {
		return CPTestUtil.addCPInstanceWithRandomSku(groupId);
	}

	private static User _user;

	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject
	private CommerceOrderEngine _commerceOrderEngine;

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	private final List<CommerceOrder> _commerceOrders = new ArrayList<>();

	@Inject
	private CommerceShipmentItemLocalService _commerceShipmentItemLocalService;

	@Inject
	private CommerceShipmentLocalService _commerceShipmentLocalService;

	@Inject
	private CommerceShippingHelper _commerceShippingHelper;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	private Group _group;

	@Inject
	private UserLocalService _userLocalService;

}