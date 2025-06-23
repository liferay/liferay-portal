/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseLocalService;
import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.Warehouse;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Page;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Pagination;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class WarehouseResourceTest extends BaseWarehouseResourceTestCase {

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		for (Long warehouseId : _warehouseIds) {
			CommerceInventoryWarehouse commerceInventoryWarehouse =
				_commerceInventoryWarehouseLocalService.
					fetchCommerceInventoryWarehouse(warehouseId);

			if (commerceInventoryWarehouse == null) {
				continue;
			}

			_commerceInventoryWarehouseLocalService.
				deleteCommerceInventoryWarehouse(commerceInventoryWarehouse);
		}
	}

	@Override
	@Test
	public void testGetWarehousesPage() throws Exception {
		super.testGetWarehousesPage();

		Warehouse warehouse1 = _addWarehouse(randomWarehouse());
		Warehouse warehouse2 = _addWarehouse(randomWarehouse());

		Page<Warehouse> page = warehouseResource.getWarehousesPage(
			warehouse1.getName(
			).get(
				"en_US"
			),
			null, Pagination.of(1, 10), null);

		Assert.assertNotEquals(warehouse2, page.fetchFirstItem());

		assertContains(warehouse1, (List<Warehouse>)page.getItems());

		Assert.assertEquals(1, page.getTotalCount());
	}

	@Override
	@Test
	public void testPatchWarehouseByExternalReferenceCode() throws Exception {
		Warehouse postWarehouse = _addWarehouse(randomWarehouse());

		postWarehouse.setName(
			Collections.singletonMap(
				"en_US", "testPatchWarehouseByExternalReferenceCode"));

		warehouseResource.patchWarehouseByExternalReferenceCode(
			postWarehouse.getExternalReferenceCode(), postWarehouse);

		Warehouse patchWarehouse =
			warehouseResource.getWarehouseByExternalReferenceCode(
				postWarehouse.getExternalReferenceCode());

		assertEquals(postWarehouse, patchWarehouse);
	}

	@Override
	@Test
	public void testPatchWarehouseId() throws Exception {
		Warehouse postWarehouse = _addWarehouse(randomWarehouse());

		postWarehouse.setName(
			Collections.singletonMap("en_US", "testPatchWarehouseId"));

		warehouseResource.patchWarehouseId(
			postWarehouse.getId(), postWarehouse);

		Warehouse patchWarehouse =
			warehouseResource.getWarehouseByExternalReferenceCode(
				postWarehouse.getExternalReferenceCode());

		assertEquals(postWarehouse, patchWarehouse);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"city", "name"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"city", "countryISOCode", "name", "regionISOCode"};
	}

	@Override
	protected Warehouse randomWarehouse() throws Exception {
		return new Warehouse() {
			{
				active = RandomTestUtil.randomBoolean();
				city = "Milano";
				countryISOCode = "IT";
				externalReferenceCode = RandomTestUtil.randomString();
				latitude = RandomTestUtil.randomDouble();
				longitude = RandomTestUtil.randomDouble();
				name = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				regionISOCode = "25";
				street1 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				street2 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				street3 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
				zip = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	@Override
	protected Warehouse testBatchEngineDeleteImportTask_addWarehouse()
		throws Exception {

		return _addWarehouse(randomWarehouse());
	}

	@Override
	protected Warehouse
			testDeleteWarehouseByExternalReferenceCode_addWarehouse()
		throws Exception {

		return _addWarehouse(randomWarehouse());
	}

	@Override
	protected Warehouse testDeleteWarehouseId_addWarehouse() throws Exception {
		return _addWarehouse(randomWarehouse());
	}

	@Override
	protected Warehouse testGetWarehouseByExternalReferenceCode_addWarehouse()
		throws Exception {

		return _addWarehouse(randomWarehouse());
	}

	@Override
	protected Warehouse testGetWarehouseId_addWarehouse() throws Exception {
		return _addWarehouse(randomWarehouse());
	}

	@Override
	protected Warehouse testGetWarehousesPage_addWarehouse(Warehouse warehouse)
		throws Exception {

		return _addWarehouse(warehouse);
	}

	@Override
	protected Warehouse testGraphQLWarehouse_addWarehouse() throws Exception {
		return _addWarehouse(randomWarehouse());
	}

	@Override
	protected Warehouse testPostWarehouse_addWarehouse(Warehouse warehouse)
		throws Exception {

		return _addWarehouse(warehouse);
	}

	@Override
	protected Warehouse testPutWarehouseByExternalReferenceCode_addWarehouse()
		throws Exception {

		return _addWarehouse(randomWarehouse());
	}

	private Warehouse _addWarehouse(Warehouse warehouse) throws Exception {
		Warehouse postWarehouse = warehouseResource.postWarehouse(warehouse);

		_warehouseIds.add(postWarehouse.getId());

		return postWarehouse;
	}

	@Inject
	private CommerceInventoryWarehouseLocalService
		_commerceInventoryWarehouseLocalService;

	private final List<Long> _warehouseIds = new ArrayList<>();

}