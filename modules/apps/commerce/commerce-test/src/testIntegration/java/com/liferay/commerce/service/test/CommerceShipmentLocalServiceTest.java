/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceShipmentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Giuseppe Pelusi
 */
@RunWith(Arquillian.class)
public class CommerceShipmentLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testGetCommerceShipmentStatusesByCommerceOrderId()
		throws Exception {

		CommerceOrder commerceOrder = _addCommerceOrder();

		int[] statuses =
			_commerceShipmentLocalService.
				getCommerceShipmentStatusesByCommerceOrderId(
					commerceOrder.getCommerceOrderId());

		Assert.assertEquals(Arrays.toString(statuses), 1, statuses.length);
	}

	@Test
	public void testGetCommerceShipments() throws Exception {
		CommerceOrder commerceOrder = _addCommerceOrder();

		List<CommerceShipment> commerceShipments =
			_commerceShipmentLocalService.getCommerceShipments(
				commerceOrder.getCommerceOrderId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceShipments.toString(), 2, commerceShipments.size());
	}

	@Test
	public void testGetCommerceShipmentsCount() throws Exception {
		CommerceOrder commerceOrder = _addCommerceOrder();

		int count = _commerceShipmentLocalService.getCommerceShipmentsCount(
			commerceOrder.getCommerceOrderId());

		Assert.assertEquals(2, count);
	}

	private CommerceOrder _addCommerceOrder() throws Exception {
		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse();

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		for (int i = 0; i < 7; i++) {
			CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
				_group.getGroupId());

			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.ONE);

			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse,
				BigDecimal.valueOf(100), cpInstance.getSku(), StringPool.BLANK);
		}

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		CommerceAddress commerceAddress =
			CommerceTestUtil.addUserCommerceAddress(
				_commerceChannel.getGroupId(), _user.getUserId());

		commerceOrder.setBillingAddressId(
			commerceAddress.getCommerceAddressId());

		BigDecimal amount = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		CommerceShippingMethod commerceShippingMethod =
			CommerceTestUtil.addFixedRateCommerceShippingMethod(
				_user.getUserId(), _commerceChannel.getGroupId(), amount);

		commerceOrder.setCommerceShippingMethodId(
			commerceShippingMethod.getCommerceShippingMethodId());

		commerceOrder.setShippingAddressId(
			commerceAddress.getCommerceAddressId());

		commerceOrder.setOrderStatus(
			CommerceShipmentConstants.ALLOWED_ORDER_STATUSES
				[RandomTestUtil.randomInt(
					0,
					CommerceShipmentConstants.ALLOWED_ORDER_STATUSES.length -
						1)]);

		CommerceShippingFixedOption commerceShippingFixedOption =
			CommerceTestUtil.addCommerceShippingFixedOption(
				commerceShippingMethod, amount);

		commerceOrder.setShippingAmount(
			commerceShippingFixedOption.getAmount());
		commerceOrder.setShippingOptionName(
			commerceShippingFixedOption.getNameCurrentValue());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		_addCommerceShipment(commerceOrder, commerceOrderItems.subList(0, 4));
		_addCommerceShipment(
			commerceOrder,
			commerceOrderItems.subList(4, commerceOrderItems.size()));

		return commerceOrder;
	}

	private CommerceShipment _addCommerceShipment(
			CommerceOrder commerceOrder,
			List<CommerceOrderItem> commerceOrderItems)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				commerceOrder.getGroupId());

		CommerceShipment commerceShipment =
			_commerceShipmentLocalService.addCommerceShipment(
				commerceOrder.getCommerceOrderId(), serviceContext);

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			_commerceShipmentItemLocalService.addCommerceShipmentItem(
				null, commerceShipment.getCommerceShipmentId(),
				commerceOrderItem.getCommerceOrderItemId(),
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				commerceOrderItem.getQuantity(), null, true, serviceContext);
		}

		return commerceShipment;
	}

	private CommerceChannel _commerceChannel;
	private CommerceCurrency _commerceCurrency;
	private CommerceInventoryWarehouse _commerceInventoryWarehouse;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommerceShipmentItemLocalService _commerceShipmentItemLocalService;

	@Inject
	private CommerceShipmentLocalService _commerceShipmentLocalService;

	private Group _group;
	private User _user;

}