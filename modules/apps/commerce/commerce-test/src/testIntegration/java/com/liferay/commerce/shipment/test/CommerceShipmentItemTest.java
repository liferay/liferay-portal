/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceShipmentConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.exception.CommerceShipmentStatusException;
import com.liferay.commerce.exception.DuplicateCommerceShipmentItemExternalReferenceCodeException;
import com.liferay.commerce.inventory.engine.CommerceInventoryEngine;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.commerce.shipment.test.util.CommerceShipmentTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class CommerceShipmentItemTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = _commerceChannelLocalService.addCommerceChannel(
			null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			_group.getGroupId(), "Test Channel",
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), serviceContext);

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceShipment = CommerceShipmentTestUtil.createEmptyOrderShipment(
			_group.getGroupId(), _commerceOrder.getCommerceOrderId());

		_commerceContext = new TestCommerceContext(
			_commerceOrder.getAccountEntry(), _commerceCurrency,
			_commerceChannel, _user, _group, _commerceOrder);
	}

	@Test
	public void testAddCommerceShipmentItem() throws Exception {
		frutillaRule.scenario(
			"Create a Shipment Item for an Order"
		).given(
			"A Group"
		).and(
			"An Order Item"
		).and(
			"A CPInstance"
		).when(
			"A Shipment Item is added to a shipment"
		).then(
			"That shipment should contain the shipment Item"
		);

		CommerceShipmentItem commerceShipmentItem =
			CommerceShipmentTestUtil.addCommerceShipmentItem(
				_commerceContext,
				CPTestUtil.addCPInstanceWithRandomSku(_group.getGroupId()),
				_group.getGroupId(), _user.getUserId(),
				_commerceOrder.getCommerceOrderId(),
				_commerceShipment.getCommerceShipmentId(), 2, 1);

		List<CommerceShipmentItem> commerceShipmentItems =
			_commerceShipmentItemLocalService.getCommerceShipmentItems(
				_commerceShipment.getCommerceShipmentId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			commerceShipmentItems.toString(), 1, commerceShipmentItems.size());

		Assert.assertEquals(
			commerceShipmentItems.toString(), 1,
			_commerceShipmentItemLocalService.
				getValidCommerceShipmentItemsCount(
					_commerceShipment.getCommerceShipmentId()));

		CommerceShipmentItem actualCommerceShipmentItem =
			commerceShipmentItems.get(0);

		Assert.assertEquals(commerceShipmentItem, actualCommerceShipmentItem);

		_resetCommerceShipment();
	}

	@Test(expected = CommerceShipmentStatusException.class)
	public void testAddCommerceShipmentItemOnShippedShipment()
		throws Exception {

		frutillaRule.scenario(
			"Try to add a new Shipment Item after a shipment is marked as " +
				"shipped"
		).given(
			"A Group"
		).and(
			"A ShipmentItem"
		).and(
			"A CPInstance"
		).when(
			"A Shipment is marked as shipped"
		).then(
			"Shipment Items should not be able to be added"
		);

		_commerceShipment.setStatus(
			CommerceShipmentConstants.SHIPMENT_STATUS_SHIPPED);

		_commerceShipment =
			_commerceShipmentLocalService.updateCommerceShipment(
				_commerceShipment);

		CommerceShipmentTestUtil.addCommerceShipmentItem(
			_commerceContext,
			CPTestUtil.addCPInstanceWithRandomSku(_group.getGroupId()),
			_group.getGroupId(), _user.getUserId(),
			_commerceOrder.getCommerceOrderId(),
			_commerceShipment.getCommerceShipmentId(), 1, 1);

		Assert.assertEquals(
			_commerceShipment.getStatus(),
			CommerceShipmentConstants.SHIPMENT_STATUS_SHIPPED);

		_resetCommerceShipment();
	}

	@Test
	public void testDeleteShipmentItemAndDoNotRestoreStockQuantity()
		throws Exception {

		frutillaRule.scenario(
			"Delete a Shipment Item after a shipment is marked as shipped " +
				"and do not restock the sku"
		).given(
			"A Group"
		).and(
			"A ShipmentItem"
		).and(
			"A CPInstance"
		).and(
			"A Shipment is marked as shipped"
		).when(
			"The Shipment Item is deleted and we do not restock the item"
		).then(
			"Our inventory should contain not that item quantity"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			_group.getGroupId());

		CommerceShipmentItem commerceShipmentItem =
			CommerceShipmentTestUtil.addCommerceShipmentItem(
				_commerceContext, cpInstance, _group.getGroupId(),
				_user.getUserId(), _commerceOrder.getCommerceOrderId(),
				_commerceShipment.getCommerceShipmentId(), 1, 1);

		_commerceShipment.setStatus(
			CommerceShipmentConstants.SHIPMENT_STATUS_SHIPPED);

		_commerceShipment =
			_commerceShipmentLocalService.updateCommerceShipment(
				_commerceShipment);

		_commerceShipmentItemLocalService.deleteCommerceShipmentItem(
			commerceShipmentItem, false);

		BigDecimal actualCPInstanceStockQuantity =
			_commerceInventoryEngine.getStockQuantity(
				_user.getCompanyId(), cpInstance.getGroupId(),
				cpInstance.getSku(), StringPool.BLANK);

		Assert.assertFalse(
			BigDecimalUtil.eq(BigDecimal.ONE, actualCPInstanceStockQuantity));

		_resetCommerceShipment();
	}

	@Test
	public void testDeleteShipmentItemAndRestoreStockQuantity()
		throws Exception {

		frutillaRule.scenario(
			"Delete a Shipment Item after a shipment is marked as shipped " +
				"and restock the sku"
		).given(
			"A Group"
		).and(
			"A ShipmentItem"
		).and(
			"A CPInstance"
		).and(
			"A Shipment is marked as shipped"
		).when(
			"The Shipment Item is deleted and we restock the item"
		).then(
			"Our inventory should contain the original item quantity"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			_group.getGroupId());

		CommerceShipmentItem commerceShipmentItem =
			CommerceShipmentTestUtil.addCommerceShipmentItem(
				_commerceContext, cpInstance, _commerceChannel.getGroupId(),
				_user.getUserId(), _commerceOrder.getCommerceOrderId(),
				_commerceShipment.getCommerceShipmentId(), 1, 1);

		_commerceShipment.setStatus(
			CommerceShipmentConstants.SHIPMENT_STATUS_SHIPPED);

		_commerceShipment =
			_commerceShipmentLocalService.updateCommerceShipment(
				_commerceShipment);

		_commerceShipmentItemLocalService.deleteCommerceShipmentItem(
			commerceShipmentItem, true);

		BigDecimal actualCPInstanceStockQuantity =
			_commerceInventoryEngine.getStockQuantity(
				_user.getCompanyId(), 0, cpInstance.getGroupId(),
				_commerceChannel.getGroupId(), cpInstance.getSku(),
				StringPool.BLANK);

		Assert.assertTrue(
			BigDecimalUtil.eq(BigDecimal.ONE, actualCPInstanceStockQuantity));

		_resetCommerceShipment();
	}

	@Test
	public void testDeleteShipmentItemSuccessfullyIfItemHasNoInventory()
		throws Exception {

		frutillaRule.scenario(
			"Delete a Shipment Item after a shipment is marked as shipped " +
				"and restock the sku"
		).given(
			"A Group"
		).and(
			"A ShipmentItem created without inventory validation"
		).and(
			"A CPInstance with allowed back orders and no inventory"
		).when(
			"The ShipmentItem is deleted"
		).then(
			"The ShipmentItem is not restocked since there was no inventory " +
				"and the ShipmentItem is deleted successfully"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			_group.getGroupId());

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(
			cpInstance.getCPDefinition());

		CommerceShipmentItem commerceShipmentItem =
			CommerceShipmentTestUtil.addCommerceShipmentItem(
				_commerceContext, cpInstance, _commerceChannel.getGroupId(),
				_user.getUserId(), _commerceOrder.getCommerceOrderId(),
				_commerceShipment.getCommerceShipmentId(), 1, 1);

		_commerceInventoryWarehouseItemLocalService.
			deleteCommerceInventoryWarehouseItems(
				_group.getCompanyId(), cpInstance.getSku(), StringPool.BLANK);

		_commerceShipmentItemLocalService.deleteCommerceShipmentItem(
			commerceShipmentItem, true);

		Assert.assertNull(
			_commerceShipmentItemLocalService.fetchCommerceShipmentItem(
				commerceShipmentItem.getCommerceShipmentItemId()));

		_resetCommerceShipment();
	}

	@Test(
		expected = DuplicateCommerceShipmentItemExternalReferenceCodeException.class
	)
	public void testUpdateCommerceShipmentItem() throws Exception {
		frutillaRule.scenario(
			"It should not be possible to update the ERC field with a value " +
				"that already exists"
		).given(
			"An commerce shipment with an commerce shipment item associated"
		).when(
			"I update the ERC field"
		).then(
			"An exception shall be raised"
		);

		CommerceShipmentItem commerceShipmentItem =
			CommerceShipmentTestUtil.addCommerceShipmentItem(
				_commerceContext,
				CPTestUtil.addCPInstanceWithRandomSku(_group.getGroupId()),
				_group.getGroupId(), _user.getUserId(),
				_commerceOrder.getCommerceOrderId(),
				_commerceShipment.getCommerceShipmentId(), 2, 1);

		CommerceShipmentItem newCommerceShipmentItem =
			CommerceShipmentTestUtil.addCommerceShipmentItem(
				_commerceContext,
				CPTestUtil.addCPInstanceWithRandomSku(_group.getGroupId()),
				_group.getGroupId(), _user.getUserId(),
				_commerceOrder.getCommerceOrderId(),
				_commerceShipment.getCommerceShipmentId(), 2, 1);

		String externalReferenceCode = "externalReferenceCode";

		_commerceShipmentItemLocalService.updateExternalReferenceCode(
			commerceShipmentItem.getCommerceShipmentItemId(),
			externalReferenceCode);

		_commerceShipmentItemLocalService.updateExternalReferenceCode(
			newCommerceShipmentItem.getCommerceShipmentItemId(),
			externalReferenceCode);

		_commerceShipmentItemLocalService.deleteCommerceShipmentItem(
			newCommerceShipmentItem.getCommerceShipmentItemId());

		_resetCommerceShipment();
	}

	@Test(expected = CommerceShipmentStatusException.class)
	public void testUpdateCommerceShipmentItemOnShippedShipment()
		throws Exception {

		frutillaRule.scenario(
			"Try to increase a Shipment Item's quantity after a shipment is " +
				"marked as shipped"
		).given(
			"A Group"
		).and(
			"A ShipmentItem"
		).when(
			"A Shipment is marked as shipped"
		).then(
			"Shipment Items should not be able to be added"
		);

		_commerceShipment.setStatus(
			CommerceShipmentConstants.SHIPMENT_STATUS_SHIPPED);

		_commerceShipment =
			_commerceShipmentLocalService.updateCommerceShipment(
				_commerceShipment);

		CommerceShipmentItem commerceShipmentItem =
			CommerceShipmentTestUtil.addCommerceShipmentItem(
				_commerceContext,
				CPTestUtil.addCPInstanceWithRandomSku(_group.getGroupId()),
				_group.getGroupId(), _user.getUserId(),
				_commerceOrder.getCommerceOrderId(),
				_commerceShipment.getCommerceShipmentId(), 2, 1);

		CommerceShipmentItem newCommerceShipmentItem =
			_commerceShipmentItemLocalService.updateCommerceShipmentItem(
				_commerceShipmentItem.getCommerceShipmentItemId(),
				_commerceShipmentItem.getCommerceInventoryWarehouseId(),
				BigDecimal.valueOf(2), true);

		Assert.assertEquals(
			_commerceShipment.getStatus(),
			CommerceShipmentConstants.SHIPMENT_STATUS_SHIPPED);
		Assert.assertEquals(
			commerceShipmentItem.getQuantity(),
			newCommerceShipmentItem.getQuantity());

		_resetCommerceShipment();
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private void _resetCommerceShipment() throws Exception {
		_commerceShipmentLocalService.deleteCommerceShipment(
			_commerceShipment, true);

		_commerceShipmentLocalService.addCommerceShipment(_commerceShipment);
	}

	private static User _user;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private CommerceContext _commerceContext;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceInventoryEngine _commerceInventoryEngine;

	@Inject
	private CommerceInventoryWarehouseItemLocalService
		_commerceInventoryWarehouseItemLocalService;

	@DeleteAfterTestRun
	private CommerceOrder _commerceOrder;

	@DeleteAfterTestRun
	private CommerceShipment _commerceShipment;

	@DeleteAfterTestRun
	private CommerceShipmentItem _commerceShipmentItem;

	@Inject
	private CommerceShipmentItemLocalService _commerceShipmentItemLocalService;

	@Inject
	private CommerceShipmentLocalService _commerceShipmentLocalService;

	private Group _group;

}