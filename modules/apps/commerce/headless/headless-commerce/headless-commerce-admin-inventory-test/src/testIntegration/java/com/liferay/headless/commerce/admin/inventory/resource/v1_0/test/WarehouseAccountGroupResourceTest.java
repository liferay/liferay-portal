/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.resource.v1_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupLocalServiceUtil;
import com.liferay.account.service.AccountGroupRelLocalServiceUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseRelLocalServiceUtil;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.WarehouseAccountGroup;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Page;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Pagination;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Danny Situ
 */
@RunWith(Arquillian.class)
public class WarehouseAccountGroupResourceTest
	extends BaseWarehouseAccountGroupResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, _serviceContext);

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				_serviceContext);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		for (Long accountGroupId : _accountGroupIds) {
			AccountGroupLocalServiceUtil.deleteAccountGroup(accountGroupId);
		}

		for (Long warehouseAccountGroupId : _warehouseAccountGroupIds) {
			CommerceInventoryWarehouseRelLocalServiceUtil.
				deleteCommerceInventoryWarehouseRel(warehouseAccountGroupId);
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
	public void testDeleteWarehouseAccountGroup() throws Exception {
		long commerceInventoryWarehouseId =
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId();

		WarehouseAccountGroup warehouseAccountGroup =
			warehouseAccountGroupResource.postWarehouseIdWarehouseAccountGroup(
				commerceInventoryWarehouseId, randomWarehouseAccountGroup());

		warehouseAccountGroupResource.deleteWarehouseAccountGroup(
			warehouseAccountGroup.getWarehouseAccountGroupId());

		Page<WarehouseAccountGroup> page =
			warehouseAccountGroupResource.
				getWarehouseIdWarehouseAccountGroupsPage(
					commerceInventoryWarehouseId, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(0, page.getTotalCount());
	}

	@Ignore
	@Test
	public void testGraphQLDeleteWarehouseAccountGroup() throws Exception {
		super.testGraphQLDeleteWarehouseAccountGroup();
	}

	@Override
	protected WarehouseAccountGroup randomWarehouseAccountGroup()
		throws Exception {

		AccountGroup randomAccountGroup =
			_accountGroupLocalService.addAccountGroup(
				StringPool.BLANK, _user.getUserId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_serviceContext);

		_accountGroupIds.add(randomAccountGroup.getAccountGroupId());

		AccountGroupRelLocalServiceUtil.addAccountGroupRel(
			randomAccountGroup.getAccountGroupId(),
			AccountEntry.class.getName(), _accountEntry.getAccountEntryId());

		return new WarehouseAccountGroup() {
			{
				accountGroupExternalReferenceCode =
					randomAccountGroup.getExternalReferenceCode();
				accountGroupId = randomAccountGroup.getAccountGroupId();
				warehouseAccountGroupId = RandomTestUtil.randomLong();
				warehouseExternalReferenceCode =
					_commerceInventoryWarehouse.getExternalReferenceCode();
				warehouseId =
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId();
			}
		};
	}

	@Override
	protected WarehouseAccountGroup
			testDeleteWarehouseAccountGroupBatch_addWarehouseAccountGroup()
		throws Exception {

		return warehouseAccountGroupResource.
			postWarehouseIdWarehouseAccountGroup(
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				randomWarehouseAccountGroup());
	}

	@Override
	protected WarehouseAccountGroup
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				String externalReferenceCode,
				WarehouseAccountGroup warehouseAccountGroup)
		throws Exception {

		return warehouseAccountGroupResource.
			postWarehouseByExternalReferenceCodeWarehouseAccountGroup(
				externalReferenceCode, warehouseAccountGroup);
	}

	@Override
	protected String
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_getExternalReferenceCode()
		throws Exception {

		return _commerceInventoryWarehouse.getExternalReferenceCode();
	}

	@Override
	protected WarehouseAccountGroup
			testGetWarehouseIdWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				Long id, WarehouseAccountGroup warehouseAccountGroup)
		throws Exception {

		return warehouseAccountGroupResource.
			postWarehouseIdWarehouseAccountGroup(id, warehouseAccountGroup);
	}

	@Override
	protected Long testGetWarehouseIdWarehouseAccountGroupsPage_getId()
		throws Exception {

		return _commerceInventoryWarehouse.getCommerceInventoryWarehouseId();
	}

	@Override
	protected WarehouseAccountGroup
			testPostWarehouseByExternalReferenceCodeWarehouseAccountGroup_addWarehouseAccountGroup(
				WarehouseAccountGroup warehouseAccountGroup)
		throws Exception {

		WarehouseAccountGroup postWarehouseAccountGroup =
			warehouseAccountGroupResource.
				postWarehouseByExternalReferenceCodeWarehouseAccountGroup(
					_commerceInventoryWarehouse.getExternalReferenceCode(),
					warehouseAccountGroup);

		_warehouseAccountGroupIds.add(
			postWarehouseAccountGroup.getWarehouseAccountGroupId());

		return postWarehouseAccountGroup;
	}

	@Override
	protected WarehouseAccountGroup
			testPostWarehouseIdWarehouseAccountGroup_addWarehouseAccountGroup(
				WarehouseAccountGroup warehouseAccountGroup)
		throws Exception {

		WarehouseAccountGroup postWarehouseAccountGroup =
			warehouseAccountGroupResource.postWarehouseIdWarehouseAccountGroup(
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				warehouseAccountGroup);

		_warehouseAccountGroupIds.add(
			postWarehouseAccountGroup.getWarehouseAccountGroupId());

		return postWarehouseAccountGroup;
	}

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private final List<Long> _accountGroupIds = new ArrayList<>();

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@DeleteAfterTestRun
	private CommerceInventoryWarehouse _commerceInventoryWarehouse;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

	private final List<Long> _warehouseAccountGroupIds = new ArrayList<>();

}