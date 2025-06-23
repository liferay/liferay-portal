/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.shipment.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceShipmentItemLocalServiceUtil;
import com.liferay.commerce.service.CommerceShipmentLocalServiceUtil;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.shipment.client.dto.v1_0.ShipmentItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class ShipmentItemResourceTest extends BaseShipmentItemResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testGroup.getGroupId());

		_user = UserLocalServiceUtil.getUser(_serviceContext.getUserId());

		BigDecimal amount = BigDecimal.valueOf(RandomTestUtil.nextDouble());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testCompany.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());

		BigDecimal price = BigDecimal.valueOf(RandomTestUtil.randomDouble());

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			testGroup.getGroupId(), price);

		cpInstance = CPInstanceLocalServiceUtil.updateCPInstance(cpInstance);

		_commerceInventoryWarehouse1 = _addCommerceInventoryWarehouse(
			cpInstance.getSku());
		_commerceInventoryWarehouse2 = _addCommerceInventoryWarehouse(
			cpInstance.getSku());

		_commerceOrder = CommerceTestUtil.createCommerceOrderForShipping(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId(),
			cpInstance.getCPInstanceId(), amount, BigDecimal.valueOf(5), 3);

		_commerceOrderItems.addAll(_commerceOrder.getCommerceOrderItems());

		_commerceShipment =
			CommerceShipmentLocalServiceUtil.addCommerceShipment(
				RandomTestUtil.randomString(), _commerceOrder.getGroupId(),
				_commerceOrder.getCommerceAccountId(),
				_commerceOrder.getShippingAddressId(),
				_commerceOrder.getCommerceShippingMethodId(),
				_commerceOrder.getShippingOptionName(), _serviceContext);
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteShipmentItemBatch() throws Exception {
		super.testDeleteShipmentItemBatch();
	}

	@Override
	@Test
	public void testPatchShipmentItem() throws Exception {
		super.testPatchShipmentItem();

		_testPatchShipmentItemWithWarehouseExternalReferenceCode();
	}

	@Override
	@Test
	public void testPatchShipmentItemByExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ShipmentItem shipmentItem = _addShipmentItem(
			externalReferenceCode, randomShipmentItem(), BigDecimal.ZERO);

		BigDecimal quantity = shipmentItem.getQuantity();

		quantity = quantity.subtract(BigDecimal.ONE);

		shipmentItem.setQuantity(quantity);

		ShipmentItem newShipmentItem =
			shipmentItemResource.patchShipmentItemByExternalReferenceCode(
				externalReferenceCode, shipmentItem);

		Assert.assertEquals(
			String.valueOf(quantity),
			String.valueOf(newShipmentItem.getQuantity()));
	}

	@Override
	@Test
	public void testPostShipmentItem() throws Exception {
		super.testPostShipmentItem();

		_testPostShipmentItemWithOrderItemExternalReferenceCode();
		_testPostShipmentItemWithWarehouseExternalReferenceCode();
	}

	@Override
	@Test
	public void testPutShipmentByExternalReferenceCodeItem() throws Exception {
		super.testPutShipmentByExternalReferenceCodeItem();

		_testPutShipmentByExternalReferenceCodeItemWithWarehouseExternalReferenceCode();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"quantity", "warehouseId"};
	}

	@Override
	protected ShipmentItem randomShipmentItem() throws Exception {
		CommerceOrderItem commerceOrderItem = _getShippableCommerceOrderItem();

		return new ShipmentItem() {
			{
				createDate = RandomTestUtil.nextDate();
				externalReferenceCode = RandomTestUtil.randomString();
				modifiedDate = RandomTestUtil.nextDate();
				orderItemExternalReferenceCode =
					commerceOrderItem.getExternalReferenceCode();
				orderItemId = commerceOrderItem.getCommerceOrderItemId();
				quantity = commerceOrderItem.getQuantity();
				shipmentId = _commerceShipment.getCommerceShipmentId();
				userName = commerceOrderItem.getUserName();
				warehouseExternalReferenceCode =
					_commerceInventoryWarehouse1.getExternalReferenceCode();
				warehouseId =
					_commerceInventoryWarehouse1.
						getCommerceInventoryWarehouseId();
			}
		};
	}

	@Override
	protected ShipmentItem testDeleteShipmentItem_addShipmentItem()
		throws Exception {

		return _addShipmentItem();
	}

	@Override
	protected ShipmentItem
			testDeleteShipmentItemByExternalReferenceCode_addShipmentItem()
		throws Exception {

		return _addShipmentItem();
	}

	@Override
	protected ShipmentItem
			testGetShipmentByExternalReferenceCodeItem_addShipmentItem()
		throws Exception {

		return _addShipmentItem();
	}

	@Override
	protected ShipmentItem
			testGetShipmentByExternalReferenceCodeItemsPage_addShipmentItem(
				String externalReferenceCode, ShipmentItem shipmentItem)
		throws Exception {

		return _addShipmentItem(
			shipmentItem.getExternalReferenceCode(), shipmentItem);
	}

	@Override
	protected String
			testGetShipmentByExternalReferenceCodeItemsPage_getExternalReferenceCode()
		throws Exception {

		return _commerceShipment.getExternalReferenceCode();
	}

	@Override
	protected ShipmentItem testGetShipmentItem_addShipmentItem()
		throws Exception {

		return _addShipmentItem();
	}

	@Override
	protected ShipmentItem testGetShipmentItemsPage_addShipmentItem(
			Long shipmentId, ShipmentItem shipmentItem)
		throws Exception {

		return _addShipmentItem(
			RandomTestUtil.randomString(), shipmentId,
			shipmentItem.getOrderItemId());
	}

	@Override
	protected Long testGetShipmentItemsPage_getShipmentId() throws Exception {
		return _commerceShipment.getCommerceShipmentId();
	}

	@Override
	protected ShipmentItem testGraphQLShipmentItem_addShipmentItem()
		throws Exception {

		return _addShipmentItem();
	}

	@Override
	protected ShipmentItem testPatchShipmentItem_addShipmentItem()
		throws Exception {

		CommerceOrderItem commerceOrderItem = _getShippableCommerceOrderItem();

		CommerceShipmentItem commerceShipmentItem =
			CommerceShipmentItemLocalServiceUtil.addCommerceShipmentItem(
				RandomTestUtil.randomString(),
				_commerceShipment.getCommerceShipmentId(),
				commerceOrderItem.getCommerceOrderItemId(),
				_commerceInventoryWarehouse1.getCommerceInventoryWarehouseId(),
				BigDecimal.ZERO, null, true, _serviceContext);

		_commerceShipmentItems.add(commerceShipmentItem);

		return _toShipmentItem(commerceShipmentItem);
	}

	@Override
	protected ShipmentItem testPostShipmentItem_addShipmentItem(
			ShipmentItem shipmentItem)
		throws Exception {

		return _addShipmentItem(
			shipmentItem.getExternalReferenceCode(), shipmentItem);
	}

	@Override
	protected ShipmentItem
			testPostShipmentItemByExternalReferenceCode_addShipmentItem(
				ShipmentItem shipmentItem)
		throws Exception {

		return testPostShipmentItem_addShipmentItem(shipmentItem);
	}

	@Override
	protected ShipmentItem
			testPutShipmentByExternalReferenceCodeItem_addShipmentItem()
		throws Exception {

		ShipmentItem shipmentItem = _addShipmentItem();

		shipmentItem.setExternalReferenceCode(
			_commerceShipment.getExternalReferenceCode());

		return shipmentItem;
	}

	@Override
	protected String
			testPutShipmentByExternalReferenceCodeItem_getExternalReferenceCode(
				ShipmentItem shipmentItem)
		throws Exception {

		return shipmentItem.getExternalReferenceCode();
	}

	private CommerceInventoryWarehouse _addCommerceInventoryWarehouse(
			String sku)
		throws Exception {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				_serviceContext);

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse,
			BigDecimal.valueOf(100), sku, StringPool.BLANK);

		return commerceInventoryWarehouse;
	}

	private ShipmentItem _addShipmentItem() throws Exception {
		CommerceOrderItem commerceOrderItem = _getShippableCommerceOrderItem();

		return _addShipmentItem(
			RandomTestUtil.randomString(),
			_commerceShipment.getCommerceShipmentId(),
			commerceOrderItem.getCommerceOrderItemId());
	}

	private ShipmentItem _addShipmentItem(
			String externalReferenceCode, long commerceShipmentId,
			long commerceOrderItemId)
		throws Exception {

		_commerceShipmentItem =
			CommerceShipmentItemLocalServiceUtil.addCommerceShipmentItem(
				externalReferenceCode, commerceShipmentId, commerceOrderItemId,
				_commerceInventoryWarehouse1.getCommerceInventoryWarehouseId(),
				BigDecimal.valueOf(5), null, true, _serviceContext);

		_commerceShipmentItems.add(_commerceShipmentItem);

		return _toShipmentItem(_commerceShipmentItem);
	}

	private ShipmentItem _addShipmentItem(
			String externalReferenceCode, ShipmentItem shipmentItem)
		throws Exception {

		return _addShipmentItem(
			externalReferenceCode, shipmentItem.getShipmentId(),
			shipmentItem.getOrderItemId());
	}

	private ShipmentItem _addShipmentItem(
			String externalReferenceCode, ShipmentItem shipmentItem,
			BigDecimal quantity)
		throws Exception {

		_commerceShipmentItem =
			CommerceShipmentItemLocalServiceUtil.addCommerceShipmentItem(
				externalReferenceCode, shipmentItem.getShipmentId(),
				shipmentItem.getOrderItemId(),
				_commerceInventoryWarehouse1.getCommerceInventoryWarehouseId(),
				quantity, null, true, _serviceContext);

		_commerceShipmentItems.add(_commerceShipmentItem);

		return _toShipmentItem(_commerceShipmentItem);
	}

	private CommerceOrderItem _getShippableCommerceOrderItem() {
		for (CommerceShipmentItem commerceShipmentItem :
				_commerceShipmentItems) {

			_commerceOrderItems.removeIf(
				commerceOrderItem ->
					commerceOrderItem.getCommerceOrderItemId() ==
						commerceShipmentItem.getCommerceOrderItemId());
		}

		return _commerceOrderItems.get(
			RandomTestUtil.randomInt(0, _commerceOrderItems.size() - 1));
	}

	private void _testPatchShipmentItemWithWarehouseExternalReferenceCode()
		throws Exception {

		ShipmentItem postShipmentItem = testPatchShipmentItem_addShipmentItem();

		ShipmentItem randomPatchShipmentItem = randomPatchShipmentItem();

		randomPatchShipmentItem.setWarehouseId(0L);
		randomPatchShipmentItem.setWarehouseExternalReferenceCode(
			_commerceInventoryWarehouse2.getExternalReferenceCode());

		ShipmentItem patchShipmentItem = shipmentItemResource.patchShipmentItem(
			postShipmentItem.getId(), randomPatchShipmentItem);

		randomPatchShipmentItem.setWarehouseId(
			_commerceInventoryWarehouse2.getCommerceInventoryWarehouseId());

		ShipmentItem expectedPatchShipmentItem = postShipmentItem.clone();

		BeanTestUtil.copyProperties(
			randomPatchShipmentItem, expectedPatchShipmentItem);

		ShipmentItem getShipmentItem = shipmentItemResource.getShipmentItem(
			patchShipmentItem.getId());

		assertEquals(expectedPatchShipmentItem, getShipmentItem);
		assertValid(getShipmentItem);
	}

	private void _testPostShipmentItemWithOrderItemExternalReferenceCode()
		throws Exception {

		CommerceOrderItem commerceOrderItem = _getShippableCommerceOrderItem();

		ShipmentItem randomShipmentItem = randomShipmentItem();

		randomShipmentItem.setOrderItemId(0L);
		randomShipmentItem.setOrderItemExternalReferenceCode(
			commerceOrderItem.getExternalReferenceCode());

		ShipmentItem postShipmentItem = shipmentItemResource.postShipmentItem(
			randomShipmentItem.getShipmentId(), randomShipmentItem);

		randomShipmentItem.setOrderItemId(
			commerceOrderItem.getCommerceOrderItemId());

		assertEquals(randomShipmentItem, postShipmentItem);
		assertValid(postShipmentItem);
	}

	private void _testPostShipmentItemWithWarehouseExternalReferenceCode()
		throws Exception {

		ShipmentItem randomShipmentItem = randomShipmentItem();

		randomShipmentItem.setWarehouseId(0L);
		randomShipmentItem.setWarehouseExternalReferenceCode(
			_commerceInventoryWarehouse2.getExternalReferenceCode());

		ShipmentItem postShipmentItem = shipmentItemResource.postShipmentItem(
			randomShipmentItem.getShipmentId(), randomShipmentItem);

		randomShipmentItem.setWarehouseId(
			_commerceInventoryWarehouse2.getCommerceInventoryWarehouseId());

		assertEquals(randomShipmentItem, postShipmentItem);
		assertValid(postShipmentItem);
	}

	private void _testPutShipmentByExternalReferenceCodeItemWithWarehouseExternalReferenceCode()
		throws Exception {

		CommerceOrderItem commerceOrderItem = _getShippableCommerceOrderItem();

		CommerceShipmentItem commerceShipmentItem =
			CommerceShipmentItemLocalServiceUtil.addCommerceShipmentItem(
				RandomTestUtil.randomString(),
				_commerceShipment.getCommerceShipmentId(),
				commerceOrderItem.getCommerceOrderItemId(),
				_addCommerceInventoryWarehouse(
					commerceOrderItem.getSku()
				).getCommerceInventoryWarehouseId(),
				BigDecimal.valueOf(5), null, true, _serviceContext);

		commerceShipmentItem.setExternalReferenceCode(
			_commerceShipment.getExternalReferenceCode());

		ShipmentItem postShipmentItem = _toShipmentItem(commerceShipmentItem);

		ShipmentItem randomShipmentItem = randomShipmentItem();

		randomShipmentItem.setWarehouseId(0L);
		randomShipmentItem.setWarehouseExternalReferenceCode(
			_commerceInventoryWarehouse2.getExternalReferenceCode());

		ShipmentItem putShipmentItem =
			shipmentItemResource.putShipmentByExternalReferenceCodeItem(
				postShipmentItem.getExternalReferenceCode(),
				randomShipmentItem);

		randomShipmentItem.setWarehouseId(
			_commerceInventoryWarehouse2.getCommerceInventoryWarehouseId());

		assertEquals(randomShipmentItem, putShipmentItem);
		assertValid(putShipmentItem);

		ShipmentItem getShipmentItem =
			shipmentItemResource.getShipmentByExternalReferenceCodeItem(
				randomShipmentItem.getExternalReferenceCode());

		assertEquals(putShipmentItem, getShipmentItem);
		assertValid(getShipmentItem);
	}

	private ShipmentItem _toShipmentItem(
		CommerceShipmentItem commerceShipmentItem) {

		return new ShipmentItem() {
			{
				createDate = commerceShipmentItem.getCreateDate();
				externalReferenceCode =
					commerceShipmentItem.getExternalReferenceCode();
				id = commerceShipmentItem.getCommerceShipmentItemId();
				modifiedDate = commerceShipmentItem.getModifiedDate();
				orderItemId = commerceShipmentItem.getCommerceOrderItemId();
				quantity = commerceShipmentItem.getQuantity();
				shipmentId = commerceShipmentItem.getCommerceShipmentId();
				userName = commerceShipmentItem.getUserName();
				warehouseId =
					commerceShipmentItem.getCommerceInventoryWarehouseId();
			}
		};
	}

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private CommerceInventoryWarehouse _commerceInventoryWarehouse1;

	@DeleteAfterTestRun
	private CommerceInventoryWarehouse _commerceInventoryWarehouse2;

	@DeleteAfterTestRun
	private CommerceOrder _commerceOrder;

	@DeleteAfterTestRun
	private List<CommerceOrderItem> _commerceOrderItems = new ArrayList<>();

	@DeleteAfterTestRun
	private CommerceShipment _commerceShipment;

	@DeleteAfterTestRun
	private CommerceShipmentItem _commerceShipmentItem;

	@DeleteAfterTestRun
	private final List<CommerceShipmentItem> _commerceShipmentItems =
		new ArrayList<>();

	private ServiceContext _serviceContext;
	private User _user;

}