/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceObjectActionExecutorConstants;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.test.util.CommerceDiscountTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.payment.test.util.TestCommercePaymentMethod;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.test.util.CommercePriceEntryTestUtil;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.commerce.shipment.test.util.CommerceShipmentTestUtil;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.object.action.trigger.ObjectActionTriggerRegistry;
import com.liferay.object.constants.ObjectActionConstants;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
@Sync
public class CommerceOrderSplitTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
			_serviceContext.getUserId(), "Test Business Account", null, null,
			new long[] {_user.getUserId()}, null, _serviceContext);

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				_serviceContext);

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_commerceChannels.add(_commerceChannel);

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		_commerceShippingMethod =
			CommerceTestUtil.addFixedRateCommerceShippingMethod(
				_user.getUserId(), _commerceChannel.getGroupId(),
				BigDecimal.ONE);

		_commerceCatalog1 = CommerceTestUtil.addCommerceCatalog(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId(),
			_commerceCurrency.getCode());

		_cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog1.getGroupId(), BigDecimal.valueOf(75),
			RandomTestUtil.randomString());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), _commerceInventoryWarehouse, BigDecimal.ZERO,
			_cpInstance1.getSku(), StringPool.BLANK);

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(
			_cpInstance1.getCPDefinition());

		_commerceCatalog2 = CommerceTestUtil.addCommerceCatalog(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId(),
			_commerceCurrency.getCode());

		_cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog2.getGroupId(), BigDecimal.valueOf(25),
			RandomTestUtil.randomString());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), _commerceInventoryWarehouse, BigDecimal.ZERO,
			RandomTestUtil.randomString(), StringPool.BLANK);

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(
			_cpInstance2.getCPDefinition());

		_objectAction = _addObjectAction(
			"orderStatus = 10", RandomTestUtil.randomString());
	}

	@After
	public void tearDown() throws Exception {
		for (CommerceChannel commerceChannel : _commerceChannels) {
			for (CommerceShipment commerceShipment :
					_commerceShipmentLocalService.getCommerceShipments(
						new long[] {commerceChannel.getGroupId()},
						QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

				_commerceShipmentLocalService.deleteCommerceShipment(
					commerceShipment, false);
			}

			_commerceOrderLocalService.deleteCommerceOrders(
				commerceChannel.getGroupId());
		}

		for (ObjectAction objectAction : _objectActions) {
			_objectActionLocalService.deleteObjectAction(objectAction);
		}
	}

	@Test
	public void testCommerceOrderSplit1() throws Exception {
		AccountEntry accountEntry = _addAccountEntry(_user);

		_createAndCheckoutCommerceOrder(
			accountEntry, ListUtil.fromArray(_cpInstance1), _user);

		List<CommerceOrder> commerceOrders =
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceOrders.toString(), 2, commerceOrders.size());
	}

	@Test
	public void testCommerceOrderSplit2() throws Exception {
		CommerceDiscountTestUtil.addPercentageCommerceDiscount(
			_group.getGroupId(), BigDecimal.valueOf(10),
			CommerceDiscountConstants.LEVEL_L1,
			CommerceDiscountConstants.TARGET_TOTAL, null);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog2.getGroupId(), SimpleCPTypeConstants.NAME, true,
			false);

		CPOption cpOption1 = CPTestUtil.addCPOption(
			_commerceCatalog2.getGroupId(),
			CPTestUtil.getDefaultCommerceOptionTypeKey(true), true);

		CPTestUtil.addCPDefinitionOptionValueRelWithPrice(
			_commerceCatalog1.getGroupId(), cpDefinition.getCPDefinitionId(),
			_cpInstance1.getCPInstanceId(), cpOption1.getCPOptionId(),
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC,
			BigDecimal.valueOf(50), BigDecimal.ONE, true, true,
			_serviceContext);

		CPOption cpOption2 = CPTestUtil.addCPOption(
			_commerceCatalog2.getGroupId(),
			CPTestUtil.getDefaultCommerceOptionTypeKey(true), true);

		CPTestUtil.addCPDefinitionOptionValueRelWithPrice(
			_commerceCatalog2.getGroupId(), cpDefinition.getCPDefinitionId(),
			_cpInstance2.getCPInstanceId(), cpOption2.getCPOptionId(),
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC,
			BigDecimal.valueOf(100), BigDecimal.ONE, true, true,
			_serviceContext);

		_cpInstanceLocalService.buildCPInstances(
			cpDefinition.getCPDefinitionId(), _serviceContext);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		Assert.assertEquals(cpInstances.toString(), 1, cpInstances.size());

		CPInstance cpInstance3 = cpInstances.get(0);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				cpInstance3.getGroupId());

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition.getCProductId(),
			cpInstance3.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), BigDecimal.TEN);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), _commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstance3.getSku(), StringPool.BLANK);

		_createAndCheckoutCommerceOrder(
			_accountEntry, ListUtil.fromArray(cpInstance3), _user);

		List<CommerceOrder> commerceOrders =
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceOrders.toString(), 3, commerceOrders.size());

		_assertCommerceOrder(
			commerceOrders, 1, BigDecimal.valueOf(7.5), _cpInstance1);
		_assertCommerceOrder(
			commerceOrders, 2, BigDecimal.valueOf(3.5), _cpInstance2,
			cpInstance3);
		_assertCommerceOrder(
			commerceOrders, 3, BigDecimal.valueOf(11), _cpInstance1,
			_cpInstance2, cpInstance3);

		CommerceOrderItem commerceOrderItem = _getCommerceOrderItem(
			commerceOrders, _cpInstance1, 1);

		commerceOrderItem.setFinalPrice(BigDecimal.ONE);
		commerceOrderItem.setUnitPrice(BigDecimal.ONE);

		_commerceOrderItemLocalService.updateCommerceOrderItem(
			commerceOrderItem);

		commerceOrderItem = _getCommerceOrderItem(
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			_cpInstance1, 3);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				commerceOrderItem.getFinalPrice(), BigDecimal.ONE));
		Assert.assertTrue(
			BigDecimalUtil.eq(
				commerceOrderItem.getUnitPrice(), BigDecimal.ONE));
	}

	@Test
	public void testCommerceOrderSplit3() throws Exception {
		_objectAction = _addObjectAction(
			"orderStatus = 10", RandomTestUtil.randomString());

		CommerceDiscountTestUtil.addPercentageCommerceDiscount(
			_group.getGroupId(), BigDecimal.valueOf(10),
			CommerceDiscountConstants.LEVEL_L1,
			CommerceDiscountConstants.TARGET_TOTAL,
			_cpInstance1.getCPDefinitionId());

		_createAndCheckoutCommerceOrder(
			_accountEntry, ListUtil.fromArray(_cpInstance1, _cpInstance2),
			_user);

		List<CommerceOrder> commerceOrders =
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceOrders.toString(), 3, commerceOrders.size());

		_assertCommerceOrder(
			commerceOrders, BigDecimal.valueOf(22.5), BigDecimal.valueOf(2.5));
		_assertCommerceOrder(
			commerceOrders, BigDecimal.valueOf(67.5), BigDecimal.valueOf(7.5));
		_assertCommerceOrder(
			commerceOrders, BigDecimal.valueOf(90), BigDecimal.valueOf(10));
	}

	@Test
	public void testCommerceOrderSplit4() throws Exception {
		User user = UserTestUtil.addUser();

		AccountEntry accountEntry = _addAccountEntry(user);

		_createAndCheckoutCommerceOrder(
			accountEntry, ListUtil.fromArray(_cpInstance1, _cpInstance2), user);

		CommerceOrderItem commerceOrderItem = _getCommerceOrderItem(
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			_cpInstance1, 1);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			commerceOrderItem.setQuantity(BigDecimal.valueOf(3));

			commerceOrderItem =
				_commerceOrderItemLocalService.updateCommerceOrderItem(
					commerceOrderItem);
		}

		commerceOrderItem = _getCommerceOrderItem(
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			_cpInstance1, 2);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				commerceOrderItem.getQuantity(), BigDecimal.valueOf(3)));

		commerceOrderItem.setQuantity(BigDecimal.valueOf(6));

		_commerceOrderItemLocalService.updateCommerceOrderItem(
			commerceOrderItem);

		commerceOrderItem = _getCommerceOrderItem(
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			_cpInstance1, 1);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				commerceOrderItem.getQuantity(), BigDecimal.valueOf(3)));
	}

	@Test
	public void testCommerceOrderSplit5() throws Exception {
		AccountEntry accountEntry = _addAccountEntry(_user);

		CommerceOrder commerceOrder = _createAndCheckoutCommerceOrder(
			accountEntry, ListUtil.fromArray(_cpInstance1, _cpInstance2),
			_user);

		for (CommerceOrderItem commerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			Assert.assertEquals(
				0L, commerceOrderItem.getCommerceInventoryBookedQuantityId());
		}

		List<CommerceOrder> commerceOrders =
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceOrders.toString(), 3, commerceOrders.size());

		commerceOrder = _getCommerceOrderByCPInstance(
			commerceOrders, _cpInstance1);

		for (CommerceOrderItem commerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			Assert.assertTrue(
				commerceOrderItem.getCommerceInventoryBookedQuantityId() > 0);
		}
	}

	@Test
	public void testCommerceOrderSplit6() throws Exception {
		AccountEntry accountEntry = _addAccountEntry(_user);

		CommerceOrder commerceOrder = _createAndCheckoutCommerceOrder(
			accountEntry, ListUtil.fromArray(_cpInstance1, _cpInstance2),
			_user);

		List<CommerceOrder> commerceOrders =
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		_completeCommerceOrder(
			_getCommerceOrderByCPInstance(commerceOrders, _cpInstance1));

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		Assert.assertNotEquals(
			CommerceOrderConstants.ORDER_STATUS_COMPLETED,
			commerceOrder.getOrderStatus());

		_completeCommerceOrder(
			_getCommerceOrderByCPInstance(commerceOrders, _cpInstance2));

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		Assert.assertEquals(
			CommerceOrderConstants.ORDER_STATUS_COMPLETED,
			commerceOrder.getOrderStatus());
	}

	@Test
	public void testCommerceOrderSplit7() throws Exception {
		AccountEntry accountEntry = _addAccountEntry(_user);

		CommerceOrder commerceOrder = _createAndCheckoutCommerceOrder(
			accountEntry, ListUtil.fromArray(_cpInstance1, _cpInstance2),
			_user);

		Assert.assertEquals(
			CommerceOrderConstants.ORDER_STATUS_PROCESSING,
			commerceOrder.getOrderStatus());

		List<CommerceOrder> commerceOrders =
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceOrders.toString(), 3, commerceOrders.size());

		commerceOrder = _getCommerceOrderByCPInstance(
			commerceOrders, _cpInstance1);

		CommerceShipment commerceShipment =
			CommerceShipmentTestUtil.createEmptyOrderShipment(
				commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId());

		for (CommerceOrderItem commerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			_commerceShipmentItemLocalService.addCommerceShipmentItem(
				null, commerceShipment.getCommerceShipmentId(),
				commerceOrderItem.getCommerceOrderItemId(), 0, BigDecimal.ZERO,
				null, true,
				ServiceContextTestUtil.getServiceContext(
					commerceOrder.getGroupId()));
		}

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		Assert.assertEquals(
			CommerceOrderConstants.ORDER_STATUS_PROCESSING,
			commerceOrder.getOrderStatus());
	}

	@Test
	public void testCommerceOrderSplit8() throws Exception {
		AccountEntry accountEntry = _addAccountEntry(_user);

		CommerceOrder commerceOrder = _createAndCheckoutCommerceOrder(
			accountEntry, ListUtil.fromArray(_cpInstance1, _cpInstance2),
			_user);

		List<CommerceOrder> commerceOrders =
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceOrders.toString(), 3, commerceOrders.size());

		commerceOrder = _commerceOrderEngine.transitionCommerceOrder(
			commerceOrder, CommerceOrderConstants.ORDER_STATUS_ON_HOLD,
			_user.getUserId(), true);

		_commerceOrderEngine.transitionCommerceOrder(
			commerceOrder, CommerceOrderConstants.ORDER_STATUS_PROCESSING,
			_user.getUserId(), true);

		commerceOrders = _commerceOrderLocalService.getCommerceOrders(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceOrders.toString(), 3, commerceOrders.size());
	}

	@Test
	public void testCommerceOrderSplit9() throws Exception {
		_addObjectAction("orderStatus = 1", RandomTestUtil.randomString());

		AccountEntry accountEntry = _addAccountEntry(_user);

		_createAndCheckoutCommerceOrder(
			accountEntry, ListUtil.fromArray(_cpInstance1, _cpInstance2),
			_user);

		List<CommerceOrder> commerceOrders =
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceOrders.toString(), 3, commerceOrders.size());
	}

	@Test
	public void testCommerceOrderSplit10() throws Exception {
		AccountEntry accountEntry = _addAccountEntry(_user);

		CommerceOrder commerceOrder = _createAndCheckoutCommerceOrder(
			accountEntry, ListUtil.fromArray(_cpInstance1, _cpInstance2),
			_user);

		CommerceOrderItem commerceOrderItem = _getCommerceOrderItem(
			ListUtil.fromArray(commerceOrder), _cpInstance1, 2);

		long commerceOrderItemId = commerceOrderItem.getCommerceOrderItemId();

		Assert.assertTrue(
			BigDecimalUtil.eq(commerceOrderItem.getQuantity(), BigDecimal.ONE));
		Assert.assertTrue(
			BigDecimalUtil.eq(
				commerceOrderItem.getShippedQuantity(), BigDecimal.ZERO));

		commerceOrder = _getCommerceOrderByCPInstance(
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			_cpInstance1);

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		commerceOrderItem = commerceOrderItems.get(0);

		commerceOrderItem.setQuantity(BigDecimal.valueOf(2));

		commerceOrderItem =
			_commerceOrderItemLocalService.updateCommerceOrderItem(
				commerceOrderItem);

		_addCommerceShipment(commerceOrder, commerceOrderItem, BigDecimal.ONE);

		commerceOrderItem = _commerceOrderItemLocalService.getCommerceOrderItem(
			commerceOrderItemId);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				commerceOrderItem.getQuantity(), BigDecimal.valueOf(2)));
		Assert.assertTrue(
			BigDecimalUtil.eq(
				commerceOrderItem.getShippedQuantity(), BigDecimal.ONE));
	}

	@Test
	public void testCommerceOrderSplit11() throws Exception {
		AccountEntry accountEntry = _addAccountEntry();

		_updateCommerceCatalog(accountEntry.getAccountEntryId());

		CommerceChannel commerceChannel = _addCommerceChannel(
			accountEntry.getAccountEntryId());

		_createAndCheckoutCommerceOrder(
			accountEntry, ListUtil.fromArray(_cpInstance1, _cpInstance2),
			_user);

		List<CommerceOrder> commerceOrders =
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceOrders.toString(), 3, commerceOrders.size());

		CommerceOrder commerceOrder = _getCommerceOrderByCPInstance(
			commerceOrders, _cpInstance1);

		Assert.assertEquals(
			commerceChannel.getGroupId(), commerceOrder.getGroupId());
	}

	@Test
	public void testCommerceOrderSplit12() throws Exception {
		AccountEntry accountEntry = _addAccountEntry();

		_updateCommerceCatalog(accountEntry.getAccountEntryId());

		CommerceOrder commerceOrder1 = _createAndCheckoutCommerceOrder(
			accountEntry, ListUtil.fromArray(_cpInstance1, _cpInstance2),
			_user);

		List<CommerceOrder> commerceOrders =
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceOrders.toString(), 3, commerceOrders.size());

		CommerceOrder commerceOrder2 = _getCommerceOrderByCPInstance(
			commerceOrders, _cpInstance1);

		Assert.assertEquals(
			commerceOrder1.getGroupId(), commerceOrder2.getGroupId());
	}

	@Test
	public void testCommerceOrderSplit13() throws Exception {
		AccountEntry accountEntry = _addAccountEntry();

		_addCommerceChannel(accountEntry.getAccountEntryId());

		CommerceOrder commerceOrder1 = _createAndCheckoutCommerceOrder(
			accountEntry, ListUtil.fromArray(_cpInstance1, _cpInstance2),
			_user);

		List<CommerceOrder> commerceOrders =
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceOrders.toString(), 3, commerceOrders.size());

		CommerceOrder commerceOrder2 = _getCommerceOrderByCPInstance(
			commerceOrders, _cpInstance1);

		Assert.assertEquals(
			commerceOrder1.getGroupId(), commerceOrder2.getGroupId());
	}

	@Test
	public void testCommerceOrderSplit14() throws Exception {
		AccountEntry accountEntry = _addAccountEntry();

		CommerceOrder commerceOrder1 = _createAndCheckoutCommerceOrder(
			accountEntry, ListUtil.fromArray(_cpInstance1, _cpInstance2),
			_user);

		List<CommerceOrder> commerceOrders =
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceOrders.toString(), 3, commerceOrders.size());

		CommerceOrder commerceOrder2 = _getCommerceOrderByCPInstance(
			commerceOrders, _cpInstance1);

		Assert.assertEquals(
			commerceOrder1.getGroupId(), commerceOrder2.getGroupId());
	}

	@Test
	public void testCommerceOrderSplit15() throws Exception {
		AccountEntry accountEntry = _addAccountEntry(_user);

		CommerceOrder commerceOrder1 = _createAndCheckoutCommerceOrder(
			accountEntry, ListUtil.fromArray(_cpInstance1, _cpInstance2),
			_user);

		CommerceOrder commerceOrder2 = _getCommerceOrderByCPInstance(
			_commerceOrderLocalService.getCommerceOrders(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			_cpInstance1);

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder2.getCommerceOrderItems();

		CommerceOrderItem commerceOrderItem = commerceOrderItems.get(0);

		CommerceShipment commerceShipment = _addCommerceShipment(
			commerceOrder2, commerceOrderItem, commerceOrderItem.getQuantity());

		String trackingURL = RandomTestUtil.randomString();

		commerceShipment = _commerceShipmentLocalService.updateCarrierDetails(
			commerceShipment.getCommerceShipmentId(), 0, "Test Carrier", null,
			trackingURL);

		Assert.assertEquals(trackingURL, commerceShipment.getTrackingURL());

		commerceOrderItem = _getCommerceOrderItem(
			ListUtil.fromArray(commerceOrder1), _cpInstance1, 2);

		commerceOrderItems =
			_commerceOrderItemLocalService.getSupplierCommerceOrderItems(
				commerceOrderItem.getCommerceOrderItemId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceOrderItems.toString(), 1, commerceOrderItems.size());

		commerceOrderItem = commerceOrderItems.get(0);

		List<CommerceShipmentItem> commerceShipmentItems =
			_commerceShipmentItemLocalService.
				getCommerceShipmentItemsByCommerceOrderItemId(
					commerceOrderItem.getCommerceOrderItemId());

		CommerceShipmentItem commerceShipmentItem = commerceShipmentItems.get(
			0);

		commerceShipment = _commerceShipmentLocalService.getCommerceShipment(
			commerceShipmentItem.getCommerceShipmentId());

		Assert.assertEquals(trackingURL, commerceShipment.getTrackingURL());
	}

	private AccountEntry _addAccountEntry() throws Exception {
		return _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	private AccountEntry _addAccountEntry(User user) throws Exception {
		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);

		_updateCommerceCatalog(accountEntry.getAccountEntryId());

		_commerceChannel.setAccountEntryId(accountEntry.getAccountEntryId());

		_commerceChannel = _commerceChannelLocalService.updateCommerceChannel(
			_commerceChannel);

		return accountEntry;
	}

	private CommerceChannel _addCommerceChannel(long accountEntryId)
		throws Exception {

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		commerceChannel.setAccountEntryId(accountEntryId);

		commerceChannel = _commerceChannelLocalService.updateCommerceChannel(
			commerceChannel);

		_commerceChannels.add(commerceChannel);

		return commerceChannel;
	}

	private CommerceShipment _addCommerceShipment(
			CommerceOrder commerceOrder, CommerceOrderItem commerceOrderItem,
			BigDecimal quantity)
		throws Exception {

		CommerceShipment commerceShipment =
			CommerceShipmentTestUtil.createEmptyOrderShipment(
				commerceOrder.getGroupId(), commerceOrder.getCommerceOrderId());

		_commerceShipmentItemLocalService.addCommerceShipmentItem(
			null, commerceShipment.getCommerceShipmentId(),
			commerceOrderItem.getCommerceOrderItemId(), 0, quantity, null,
			false,
			ServiceContextTestUtil.getServiceContext(
				commerceOrder.getGroupId()));

		return _commerceShipmentLocalService.getCommerceShipment(
			commerceShipment.getCommerceShipmentId());
	}

	private ObjectAction _addObjectAction(
			String conditionExpression, String externalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_COMMERCE_ORDER", _user.getCompanyId());

		ObjectAction objectAction = _objectActionLocalService.addObjectAction(
			null, _serviceContext.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, conditionExpression,
			RandomTestUtil.randomString(), null,
			HashMapBuilder.put(
				_serviceContext.getLocale(), RandomTestUtil.randomString()
			).build(),
			externalReferenceCode,
			CommerceObjectActionExecutorConstants.
				KEY_SPLIT_COMMERCE_ORDER_BY_CATALOG,
			"liferay/commerce_order_status",
			UnicodePropertiesBuilder.put(
				"objectDefinitionId", objectDefinition.getObjectDefinitionId()
			).build(),
			false);

		_objectActions.add(objectAction);

		return objectAction;
	}

	private void _assertCommerceOrder(
		List<CommerceOrder> commerceOrders, BigDecimal total,
		BigDecimal totalDiscountAmount) {

		commerceOrders = ListUtil.filter(
			commerceOrders,
			commerceOrder -> BigDecimalUtil.eq(
				commerceOrder.getTotal(), total));

		CommerceOrder commerceOrder = commerceOrders.get(0);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				commerceOrder.getTotalDiscountAmount(), totalDiscountAmount));
	}

	private void _assertCommerceOrder(
		List<CommerceOrder> commerceOrders, int size,
		BigDecimal totalDiscountAmount, CPInstance... cpInstances) {

		commerceOrders = ListUtil.filter(
			commerceOrders,
			commerceOrder -> {
				List<CommerceOrderItem> commerceOrderItems =
					commerceOrder.getCommerceOrderItems();

				return commerceOrderItems.size() == size;
			});

		CommerceOrder commerceOrder = commerceOrders.get(0);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				commerceOrder.getTotalDiscountAmount(), totalDiscountAmount));

		for (CPInstance cpInstance : cpInstances) {
			List<CommerceOrderItem> commerceOrderItems = ListUtil.filter(
				commerceOrder.getCommerceOrderItems(),
				commerceOrderItem -> StringUtil.equals(
					commerceOrderItem.getSku(), cpInstance.getSku()));

			CommerceOrderItem commerceOrderItem = commerceOrderItems.get(0);

			Assert.assertEquals(
				cpInstance.getCPDefinitionId(),
				commerceOrderItem.getCPDefinitionId());
			Assert.assertEquals(
				cpInstance.getCPInstanceId(),
				commerceOrderItem.getCPInstanceId());
			Assert.assertTrue(
				BigDecimalUtil.eq(
					BigDecimal.ONE, commerceOrderItem.getQuantity()));

			CommercePriceList commercePriceList =
				_commercePriceListLocalService.
					fetchCatalogBaseCommercePriceList(cpInstance.getGroupId());

			CommercePriceEntry commercePriceEntry =
				_commercePriceEntryLocalService.fetchCommercePriceEntry(
					commercePriceList.getCommercePriceListId(),
					cpInstance.getCPInstanceUuid(), null);

			Assert.assertTrue(
				BigDecimalUtil.eq(
					commercePriceEntry.getPrice(),
					commerceOrderItem.getUnitPrice()));
		}
	}

	private void _completeCommerceOrder(CommerceOrder commerceOrder)
		throws Exception {

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		CommerceOrderItem commerceOrderItem = commerceOrderItems.get(0);

		_addCommerceShipment(
			commerceOrder, commerceOrderItem, commerceOrderItem.getQuantity());

		commerceOrder = _commerceOrderEngine.transitionCommerceOrder(
			commerceOrder, CommerceOrderConstants.ORDER_STATUS_SHIPPED,
			_user.getUserId(), true);

		_commerceOrderEngine.transitionCommerceOrder(
			commerceOrder, CommerceOrderConstants.ORDER_STATUS_COMPLETED,
			_user.getUserId(), true);
	}

	private CommerceOrder _createAndCheckoutCommerceOrder(
			AccountEntry accountEntry, List<CPInstance> cpInstances, User user)
		throws Exception {

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user.getUserId());

		Role role = _roleLocalService.getRole(
			_group.getCompanyId(),
			AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER);

		_userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), accountEntry.getAccountEntryGroupId(),
			role.getRoleId());

		CommerceOrder commerceOrder = null;

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			commerceOrder = CommerceTestUtil.addB2BCommerceOrder(
				_commerceChannel.getSiteGroupId(), user.getUserId(),
				accountEntry.getAccountEntryId(),
				_commerceCurrency.getCommerceCurrencyId());

			for (CPInstance cpInstance : cpInstances) {
				CommerceTestUtil.addCommerceOrderItem(
					commerceOrder.getCommerceOrderId(),
					cpInstance.getCPInstanceId(), BigDecimal.ONE);
			}

			commerceOrder = _commerceOrderLocalService.fetchCommerceOrder(
				commerceOrder.getCommerceOrderId());

			Country country = CommerceInventoryTestUtil.addCountry(
				_serviceContext);

			Region region = CommerceInventoryTestUtil.addRegion(
				country.getCountryId(), _serviceContext);

			Address address = _addressLocalService.addAddress(
				RandomTestUtil.randomString(), user.getUserId(),
				AccountEntry.class.getName(), accountEntry.getAccountEntryId(),
				country.getCountryId(), 0, region.getRegionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				false, RandomTestUtil.randomString(), true,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_serviceContext);

			commerceOrder.setBillingAddressId(address.getAddressId());

			commerceOrder.setCommerceShippingMethodId(
				_commerceShippingMethod.getCommerceShippingMethodId());
			commerceOrder.setShippingAddressId(address.getAddressId());
			commerceOrder.setCommercePaymentMethodKey(
				TestCommercePaymentMethod.KEY);

			commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
				commerceOrder);

			commerceOrder = _commerceOrderEngine.checkoutCommerceOrder(
				commerceOrder, user.getUserId());
		}

		commerceOrder = _commerceOrderEngine.transitionCommerceOrder(
			commerceOrder, CommerceOrderConstants.ORDER_STATUS_PROCESSING,
			_user.getUserId(), true);

		for (int i = 0; i < 3; i++) {
			ObjectAction objectAction =
				_objectActionLocalService.getObjectAction(
					_objectAction.getObjectActionId());

			int status = objectAction.getStatus();

			if ((status == ObjectActionConstants.STATUS_FAILED) ||
				(status == ObjectActionConstants.STATUS_SUCCESS)) {

				Assert.assertEquals(
					ObjectActionConstants.STATUS_SUCCESS, status);

				return commerceOrder;
			}

			Thread.sleep(500);
		}

		return commerceOrder;
	}

	private CommerceOrder _getCommerceOrderByCPInstance(
		List<CommerceOrder> commerceOrders, CPInstance cpInstance) {

		List<CommerceOrder> filteredCommerceOrders = ListUtil.filter(
			commerceOrders,
			commerceOrder -> {
				List<CommerceOrderItem> commerceOrderItems =
					commerceOrder.getCommerceOrderItems();

				if (commerceOrderItems.size() != 1) {
					return false;
				}

				CommerceOrderItem commerceOrderItem = commerceOrderItems.get(0);

				return StringUtil.equals(
					commerceOrderItem.getSku(), cpInstance.getSku());
			});

		Assert.assertFalse(
			"No order found for SKU " + cpInstance.getSku(),
			filteredCommerceOrders.isEmpty());

		return filteredCommerceOrders.get(0);
	}

	private CommerceOrderItem _getCommerceOrderItem(
		List<CommerceOrder> commerceOrders, CPInstance cpInstance, int size) {

		List<CommerceOrder> filteredCommerceOrders = ListUtil.filter(
			commerceOrders,
			commerceOrder -> {
				List<CommerceOrderItem> commerceOrderItems =
					commerceOrder.getCommerceOrderItems();

				return commerceOrderItems.size() == size;
			});

		CommerceOrderItem commerceOrderItem = null;

		for (CommerceOrder commerceOrder : filteredCommerceOrders) {
			List<CommerceOrderItem> commerceOrderItems = ListUtil.filter(
				commerceOrder.getCommerceOrderItems(),
				curCommerceOrderItem -> StringUtil.equals(
					curCommerceOrderItem.getSku(), cpInstance.getSku()));

			if (!commerceOrderItems.isEmpty()) {
				commerceOrderItem = commerceOrderItems.get(0);
			}
		}

		Assert.assertNotNull(commerceOrderItem);

		return commerceOrderItem;
	}

	private void _updateCommerceCatalog(long accountEntryId) {
		_commerceCatalog1.setAccountEntryId(accountEntryId);

		_commerceCatalog1 = _commerceCatalogLocalService.updateCommerceCatalog(
			_commerceCatalog1);
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private AddressLocalService _addressLocalService;

	private CommerceCatalog _commerceCatalog1;
	private CommerceCatalog _commerceCatalog2;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private final List<CommerceChannel> _commerceChannels = new ArrayList<>();
	private CommerceCurrency _commerceCurrency;
	private CommerceInventoryWarehouse _commerceInventoryWarehouse;

	@Inject
	private CommerceOrderEngine _commerceOrderEngine;

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommerceShipmentItemLocalService _commerceShipmentItemLocalService;

	@Inject
	private CommerceShipmentLocalService _commerceShipmentLocalService;

	private CommerceShippingMethod _commerceShippingMethod;
	private CPInstance _cpInstance1;
	private CPInstance _cpInstance2;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	private Group _group;
	private ObjectAction _objectAction;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectActionTriggerRegistry _objectActionTriggerRegistry;

	private final List<ObjectAction> _objectActions = new ArrayList<>();

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}