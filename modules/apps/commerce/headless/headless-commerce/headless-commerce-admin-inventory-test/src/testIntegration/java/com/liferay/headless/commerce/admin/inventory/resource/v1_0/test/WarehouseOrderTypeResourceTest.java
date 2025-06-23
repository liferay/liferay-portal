/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseRelLocalServiceUtil;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.service.CommerceOrderTypeLocalServiceUtil;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.WarehouseOrderType;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Page;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Pagination;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class WarehouseOrderTypeResourceTest
	extends BaseWarehouseOrderTypeResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				_serviceContext);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		for (Long warehouseOrderTypeId : _warehouseOrderTypeIds) {
			CommerceInventoryWarehouseRelLocalServiceUtil.
				deleteCommerceInventoryWarehouseRel(warehouseOrderTypeId);
		}
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	public void testDeleteWarehouseOrderType() throws Exception {
		long commerceInventoryWarehouseId =
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId();

		WarehouseOrderType warehouseOrderType =
			warehouseOrderTypeResource.postWarehouseIdWarehouseOrderType(
				commerceInventoryWarehouseId, randomWarehouseOrderType());

		warehouseOrderTypeResource.deleteWarehouseOrderType(
			warehouseOrderType.getWarehouseOrderTypeId());

		Page<WarehouseOrderType> page =
			warehouseOrderTypeResource.getWarehouseIdWarehouseOrderTypesPage(
				commerceInventoryWarehouseId, null, null, Pagination.of(1, 10),
				null);

		Assert.assertEquals(0, page.getTotalCount());
	}

	@Ignore
	@Override
	@Test
	public void testGetWarehouseIdWarehouseOrderTypesPageWithFilterDateTimeEquals()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetWarehouseIdWarehouseOrderTypesPageWithFilterDoubleEquals()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetWarehouseIdWarehouseOrderTypesPageWithFilterStringEquals()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetWarehouseIdWarehouseOrderTypesPageWithSortDateTime()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetWarehouseIdWarehouseOrderTypesPageWithSortDouble()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetWarehouseIdWarehouseOrderTypesPageWithSortInteger()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetWarehouseIdWarehouseOrderTypesPageWithSortString()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteWarehouseOrderType() throws Exception {
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"orderTypeExternalReferenceCode", "orderTypeId",
			"warehouseExternalReferenceCode", "warehouseId"
		};
	}

	@Override
	protected WarehouseOrderType randomWarehouseOrderType() throws Exception {
		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			RandomTestUtil.nextDate(), _user.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			RandomTestUtil.nextDate(), _user.getTimeZone());

		_commerceOrderType =
			CommerceOrderTypeLocalServiceUtil.addCommerceOrderType(
				RandomTestUtil.randomString(), _user.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean(), displayDateConfig.getMonth(),
				displayDateConfig.getDay(), displayDateConfig.getYear(),
				displayDateConfig.getHour(), displayDateConfig.getMinute(), 0,
				expirationDateConfig.getMonth(), expirationDateConfig.getDay(),
				expirationDateConfig.getYear(), expirationDateConfig.getHour(),
				expirationDateConfig.getMinute(), true, _serviceContext);

		return new WarehouseOrderType() {
			{
				orderTypeExternalReferenceCode =
					_commerceOrderType.getExternalReferenceCode();
				orderTypeId = _commerceOrderType.getCommerceOrderTypeId();
				priority = RandomTestUtil.randomInt();
				warehouseExternalReferenceCode =
					_commerceInventoryWarehouse.getExternalReferenceCode();
				warehouseId =
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId();
			}
		};
	}

	@Override
	protected WarehouseOrderType
			testDeleteWarehouseOrderTypeBatch_addWarehouseOrderType()
		throws Exception {

		return warehouseOrderTypeResource.postWarehouseIdWarehouseOrderType(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			randomWarehouseOrderType());
	}

	@Override
	protected WarehouseOrderType
			testGetWarehouseByExternalReferenceCodeWarehouseOrderTypesPage_addWarehouseOrderType(
				String externalReferenceCode,
				WarehouseOrderType warehouseOrderType)
		throws Exception {

		return warehouseOrderTypeResource.
			postWarehouseByExternalReferenceCodeWarehouseOrderType(
				externalReferenceCode, warehouseOrderType);
	}

	@Override
	protected String
			testGetWarehouseByExternalReferenceCodeWarehouseOrderTypesPage_getExternalReferenceCode()
		throws Exception {

		return _commerceInventoryWarehouse.getExternalReferenceCode();
	}

	@Override
	protected WarehouseOrderType
			testGetWarehouseIdWarehouseOrderTypesPage_addWarehouseOrderType(
				Long id, WarehouseOrderType warehouseOrderType)
		throws Exception {

		return warehouseOrderTypeResource.postWarehouseIdWarehouseOrderType(
			id, warehouseOrderType);
	}

	@Override
	protected Long testGetWarehouseIdWarehouseOrderTypesPage_getId()
		throws Exception {

		return _commerceInventoryWarehouse.getCommerceInventoryWarehouseId();
	}

	@Override
	protected WarehouseOrderType
			testPostWarehouseByExternalReferenceCodeWarehouseOrderType_addWarehouseOrderType(
				WarehouseOrderType warehouseOrderType)
		throws Exception {

		WarehouseOrderType postWarehouseOrderType =
			warehouseOrderTypeResource.
				postWarehouseByExternalReferenceCodeWarehouseOrderType(
					warehouseOrderType.getWarehouseExternalReferenceCode(),
					warehouseOrderType);

		_warehouseOrderTypeIds.add(
			postWarehouseOrderType.getWarehouseOrderTypeId());

		return postWarehouseOrderType;
	}

	@Override
	protected WarehouseOrderType
			testPostWarehouseIdWarehouseOrderType_addWarehouseOrderType(
				WarehouseOrderType warehouseOrderType)
		throws Exception {

		WarehouseOrderType postWarehouseOrderType =
			warehouseOrderTypeResource.postWarehouseIdWarehouseOrderType(
				warehouseOrderType.getWarehouseId(), warehouseOrderType);

		_warehouseOrderTypeIds.add(
			postWarehouseOrderType.getWarehouseOrderTypeId());

		return postWarehouseOrderType;
	}

	@DeleteAfterTestRun
	private CommerceInventoryWarehouse _commerceInventoryWarehouse;

	@DeleteAfterTestRun
	private CommerceOrderType _commerceOrderType;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

	private final List<Long> _warehouseOrderTypeIds = new ArrayList<>();

}