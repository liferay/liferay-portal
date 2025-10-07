/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryReplenishmentItemLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseLocalService;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.ReplenishmentItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.text.DateFormat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class ReplenishmentItemResourceTest
	extends BaseReplenishmentItemResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_dateFormat = DateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd");
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());
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
	public void testDeleteReplenishmentItemBatch() throws Exception {
		super.testDeleteReplenishmentItemBatch();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetReplenishmentItemsPage() throws Exception {
		super.testGraphQLGetReplenishmentItemsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetWarehouseIdReplenishmentItemsPage()
		throws Exception {

		super.testGraphQLGetWarehouseIdReplenishmentItemsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostReplenishmentItem() throws Exception {
		super.testGraphQLPostReplenishmentItem();
	}

	@Override
	@Test
	public void testPatchReplenishmentItem() throws Exception {
		ReplenishmentItem replenishmentItem =
			testGetReplenishmentItemByExternalReferenceCode_addReplenishmentItem();

		replenishmentItem.setAvailabilityDate(_dateFormat.parse("2022-09-24"));
		replenishmentItem.setExternalReferenceCode("newExternalReferenceCode");
		replenishmentItem.setQuantity(BigDecimal.valueOf(25));

		replenishmentItemResource.patchReplenishmentItem(
			replenishmentItem.getId(), replenishmentItem);

		ReplenishmentItem patchReplenishmentItem =
			replenishmentItemResource.getReplenishmentItem(
				replenishmentItem.getId());

		Assert.assertTrue(equals(replenishmentItem, patchReplenishmentItem));
	}

	@Override
	@Test
	public void testPatchReplenishmentItemByExternalReferenceCode()
		throws Exception {

		ReplenishmentItem replenishmentItem =
			testGetReplenishmentItemByExternalReferenceCode_addReplenishmentItem();

		String externalReferenceCode =
			replenishmentItem.getExternalReferenceCode();

		replenishmentItem.setAvailabilityDate(_dateFormat.parse("2022-09-24"));
		replenishmentItem.setQuantity(BigDecimal.valueOf(25));

		replenishmentItemResource.patchReplenishmentItemByExternalReferenceCode(
			externalReferenceCode, replenishmentItem);

		ReplenishmentItem patchReplenishmentItem =
			replenishmentItemResource.
				getReplenishmentItemByExternalReferenceCode(
					replenishmentItem.getExternalReferenceCode());

		Assert.assertTrue(equals(replenishmentItem, patchReplenishmentItem));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"availabilityDate", "warehouseId", "externalReferenceCode",
			"quantity", "sku"
		};
	}

	@Override
	protected ReplenishmentItem randomReplenishmentItem() throws Exception {
		return new ReplenishmentItem() {
			{
				availabilityDate = _dateFormat.parse(
					_dateFormat.format(RandomTestUtil.nextDate()));
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				quantity = BigDecimal.valueOf(RandomTestUtil.randomInt());
				sku = testGetReplenishmentItemsPage_getSku();
				warehouseId =
					testGetWarehouseIdReplenishmentItemsPage_getWarehouseId();
			}
		};
	}

	@Override
	protected ReplenishmentItem
			testDeleteReplenishmentItem_addReplenishmentItem()
		throws Exception {

		return _toReplenishmentItem(_addCommerceInventoryReplenishmentItem());
	}

	@Override
	protected ReplenishmentItem
			testDeleteReplenishmentItemByExternalReferenceCode_addReplenishmentItem()
		throws Exception {

		return _toReplenishmentItem(_addCommerceInventoryReplenishmentItem());
	}

	@Override
	protected ReplenishmentItem testGetReplenishmentItem_addReplenishmentItem()
		throws Exception {

		return _toReplenishmentItem(_addCommerceInventoryReplenishmentItem());
	}

	@Override
	protected ReplenishmentItem
			testGetReplenishmentItemByExternalReferenceCode_addReplenishmentItem()
		throws Exception {

		return _toReplenishmentItem(_addCommerceInventoryReplenishmentItem());
	}

	@Override
	protected ReplenishmentItem
			testGetReplenishmentItemsPage_addReplenishmentItem(
				String sku, ReplenishmentItem replenishmentItem)
		throws Exception {

		_commerceInventoryReplenishmentItem =
			_commerceInventoryReplenishmentItemLocalService.
				addCommerceInventoryReplenishmentItem(
					replenishmentItem.getExternalReferenceCode(),
					_user.getUserId(), _getCommerceInventoryWarehouseId(),
					replenishmentItem.getAvailabilityDate(),
					replenishmentItem.getQuantity(), sku, StringPool.BLANK);

		return _toReplenishmentItem(_commerceInventoryReplenishmentItem);
	}

	@Override
	protected String testGetReplenishmentItemsPage_getSku() throws Exception {
		if (_cpInstance != null) {
			return _cpInstance.getSku();
		}

		_cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			_group.getGroupId());

		return _cpInstance.getSku();
	}

	@Override
	protected ReplenishmentItem
			testGetWarehouseIdReplenishmentItemsPage_addReplenishmentItem(
				Long warehouseId, ReplenishmentItem replenishmentItem)
		throws Exception {

		_commerceInventoryReplenishmentItem =
			_commerceInventoryReplenishmentItemLocalService.
				addCommerceInventoryReplenishmentItem(
					replenishmentItem.getExternalReferenceCode(),
					_user.getUserId(), warehouseId,
					replenishmentItem.getAvailabilityDate(),
					replenishmentItem.getQuantity(), replenishmentItem.getSku(),
					StringPool.BLANK);

		return _toReplenishmentItem(_commerceInventoryReplenishmentItem);
	}

	@Override
	protected Long testGetWarehouseIdReplenishmentItemsPage_getWarehouseId()
		throws Exception {

		return _getCommerceInventoryWarehouseId();
	}

	@Override
	protected ReplenishmentItem
			testGraphQLReplenishmentItem_addReplenishmentItem()
		throws Exception {

		_commerceInventoryReplenishmentItem =
			_commerceInventoryReplenishmentItemLocalService.
				addCommerceInventoryReplenishmentItem(
					RandomTestUtil.randomString(), _user.getUserId(),
					_getCommerceInventoryWarehouseId(),
					_dateFormat.parse(
						_dateFormat.format(RandomTestUtil.nextDate())),
					BigDecimal.valueOf(RandomTestUtil.nextInt()),
					testGetReplenishmentItemsPage_getSku(), StringPool.BLANK);

		return _toReplenishmentItem(_commerceInventoryReplenishmentItem);
	}

	@Override
	protected ReplenishmentItem
			testGraphQLReplenishmentItem_addReplenishmentItem(
				Long warehouseId, String sku,
				ReplenishmentItem replenishmentItem)
		throws Exception {

		_commerceInventoryReplenishmentItem =
			_commerceInventoryReplenishmentItemLocalService.
				addCommerceInventoryReplenishmentItem(
					replenishmentItem.getExternalReferenceCode(),
					_user.getUserId(), replenishmentItem.getWarehouseId(),
					replenishmentItem.getAvailabilityDate(),
					replenishmentItem.getQuantity(), replenishmentItem.getSku(),
					StringPool.BLANK);

		return _toReplenishmentItem(_commerceInventoryReplenishmentItem);
	}

	@Override
	protected ReplenishmentItem testPostReplenishmentItem_addReplenishmentItem(
			ReplenishmentItem replenishmentItem)
		throws Exception {

		_commerceInventoryReplenishmentItem =
			_commerceInventoryReplenishmentItemLocalService.
				addCommerceInventoryReplenishmentItem(
					replenishmentItem.getExternalReferenceCode(),
					_user.getUserId(), replenishmentItem.getWarehouseId(),
					replenishmentItem.getAvailabilityDate(),
					replenishmentItem.getQuantity(), replenishmentItem.getSku(),
					StringPool.BLANK);

		return _toReplenishmentItem(_commerceInventoryReplenishmentItem);
	}

	@Override
	protected ReplenishmentItem
			testPutReplenishmentItemByExternalReferenceCode_addReplenishmentItem()
		throws Exception {

		return _toReplenishmentItem(_addCommerceInventoryReplenishmentItem());
	}

	@Override
	protected ReplenishmentItem
			testPutReplenishmentItemByExternalReferenceCode_createReplenishmentItem()
		throws Exception {

		return randomReplenishmentItem();
	}

	private CommerceInventoryReplenishmentItem
			_addCommerceInventoryReplenishmentItem()
		throws Exception {

		_addCommerceInventoryWarehouseItem();

		if (_commerceInventoryReplenishmentItem != null) {
			return _commerceInventoryReplenishmentItem;
		}

		_commerceInventoryReplenishmentItem =
			_commerceInventoryReplenishmentItemLocalService.
				addCommerceInventoryReplenishmentItem(
					RandomTestUtil.randomString(), _user.getUserId(),
					_getCommerceInventoryWarehouseId(),
					_dateFormat.parse(
						_dateFormat.format(RandomTestUtil.nextDate())),
					BigDecimal.valueOf(RandomTestUtil.nextInt()),
					testGetReplenishmentItemsPage_getSku(), StringPool.BLANK);

		return _commerceInventoryReplenishmentItem;
	}

	private CommerceInventoryWarehouse _addCommerceInventoryWarehouse()
		throws Exception {

		if (_commerceInventoryWarehouse != null) {
			return _commerceInventoryWarehouse;
		}

		_commerceInventoryWarehouse =
			_commerceInventoryWarehouseLocalService.
				addCommerceInventoryWarehouse(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.randomLocaleStringMap(), true,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), RandomTestUtil.nextDouble(),
					RandomTestUtil.nextDouble(), _serviceContext);

		return _commerceInventoryWarehouse;
	}

	private CommerceInventoryWarehouseItem _addCommerceInventoryWarehouseItem()
		throws Exception {

		if (_commerceInventoryWarehouseItem != null) {
			return _commerceInventoryWarehouseItem;
		}

		_commerceInventoryWarehouseItem =
			_commerceInventoryWarehouseItemLocalService.
				addCommerceInventoryWarehouseItem(
					RandomTestUtil.randomString(), _user.getUserId(),
					_getCommerceInventoryWarehouseId(),
					BigDecimal.valueOf(RandomTestUtil.nextInt()),
					testGetReplenishmentItemsPage_getSku(), StringPool.BLANK);

		return _commerceInventoryWarehouseItem;
	}

	private long _getCommerceInventoryWarehouseId() throws Exception {
		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_addCommerceInventoryWarehouse();

		return commerceInventoryWarehouse.getCommerceInventoryWarehouseId();
	}

	private ReplenishmentItem _toReplenishmentItem(
		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem) {

		return new ReplenishmentItem() {
			{
				availabilityDate =
					commerceInventoryReplenishmentItem.getAvailabilityDate();
				externalReferenceCode =
					commerceInventoryReplenishmentItem.
						getExternalReferenceCode();
				id =
					commerceInventoryReplenishmentItem.
						getCommerceInventoryReplenishmentItemId();
				quantity = commerceInventoryReplenishmentItem.getQuantity();
				sku = commerceInventoryReplenishmentItem.getSku();
				warehouseId =
					commerceInventoryReplenishmentItem.
						getCommerceInventoryWarehouseId();
			}
		};
	}

	private static DateFormat _dateFormat;

	@DeleteAfterTestRun
	private CommerceInventoryReplenishmentItem
		_commerceInventoryReplenishmentItem;

	@Inject
	private CommerceInventoryReplenishmentItemLocalService
		_commerceInventoryReplenishmentItemLocalService;

	@DeleteAfterTestRun
	private CommerceInventoryWarehouse _commerceInventoryWarehouse;

	@DeleteAfterTestRun
	private CommerceInventoryWarehouseItem _commerceInventoryWarehouseItem;

	@Inject
	private CommerceInventoryWarehouseItemLocalService
		_commerceInventoryWarehouseItemLocalService;

	@Inject
	private CommerceInventoryWarehouseLocalService
		_commerceInventoryWarehouseLocalService;

	@DeleteAfterTestRun
	private CPInstance _cpInstance;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}