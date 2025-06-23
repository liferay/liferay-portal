/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.resource.v1_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseRelLocalServiceUtil;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.WarehouseAccount;
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
public class WarehouseAccountResourceTest
	extends BaseWarehouseAccountResourceTestCase {

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

		for (Long accountEntryId : _accountEntryIds) {
			AccountEntryLocalServiceUtil.deleteAccountEntry(accountEntryId);
		}

		for (Long warehouseAccountId : _warehouseAccountIds) {
			CommerceInventoryWarehouseRelLocalServiceUtil.
				deleteCommerceInventoryWarehouseRel(warehouseAccountId);
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
	public void testDeleteWarehouseAccount() throws Exception {
		long commerceInventoryWarehouseId =
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId();

		WarehouseAccount warehouseAccount =
			warehouseAccountResource.postWarehouseIdWarehouseAccount(
				commerceInventoryWarehouseId, randomWarehouseAccount());

		warehouseAccountResource.deleteWarehouseAccount(
			warehouseAccount.getWarehouseAccountId());

		Page<WarehouseAccount> page =
			warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
				commerceInventoryWarehouseId, null, null, Pagination.of(1, 10),
				null);

		Assert.assertEquals(0, page.getTotalCount());
	}

	@Ignore
	@Test
	public void testGraphQLDeleteWarehouseAccount() throws Exception {
		super.testGraphQLDeleteWarehouseAccount();
	}

	@Override
	protected WarehouseAccount randomWarehouseAccount() throws Exception {
		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, _serviceContext);

		_accountEntryIds.add(accountEntry.getAccountEntryId());

		return new WarehouseAccount() {
			{
				accountExternalReferenceCode =
					accountEntry.getExternalReferenceCode();
				accountId = accountEntry.getAccountEntryId();
				warehouseAccountId = RandomTestUtil.randomLong();
				warehouseExternalReferenceCode =
					_commerceInventoryWarehouse.getExternalReferenceCode();
				warehouseId =
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId();
			}
		};
	}

	@Override
	protected WarehouseAccount
			testDeleteWarehouseAccountBatch_addWarehouseAccount()
		throws Exception {

		return warehouseAccountResource.postWarehouseIdWarehouseAccount(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			randomWarehouseAccount());
	}

	@Override
	protected WarehouseAccount
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_addWarehouseAccount(
				String externalReferenceCode, WarehouseAccount warehouseAccount)
		throws Exception {

		return warehouseAccountResource.
			postWarehouseByExternalReferenceCodeWarehouseAccount(
				externalReferenceCode, warehouseAccount);
	}

	@Override
	protected String
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_getExternalReferenceCode()
		throws Exception {

		return _commerceInventoryWarehouse.getExternalReferenceCode();
	}

	@Override
	protected WarehouseAccount
			testGetWarehouseIdWarehouseAccountsPage_addWarehouseAccount(
				Long id, WarehouseAccount warehouseAccount)
		throws Exception {

		return warehouseAccountResource.postWarehouseIdWarehouseAccount(
			id, warehouseAccount);
	}

	@Override
	protected Long testGetWarehouseIdWarehouseAccountsPage_getId()
		throws Exception {

		return _commerceInventoryWarehouse.getCommerceInventoryWarehouseId();
	}

	@Override
	protected WarehouseAccount
			testPostWarehouseByExternalReferenceCodeWarehouseAccount_addWarehouseAccount(
				WarehouseAccount warehouseAccount)
		throws Exception {

		WarehouseAccount postWarehouseAccount =
			warehouseAccountResource.
				postWarehouseByExternalReferenceCodeWarehouseAccount(
					_commerceInventoryWarehouse.getExternalReferenceCode(),
					warehouseAccount);

		_warehouseAccountIds.add(postWarehouseAccount.getWarehouseAccountId());

		return postWarehouseAccount;
	}

	@Override
	protected WarehouseAccount
			testPostWarehouseIdWarehouseAccount_addWarehouseAccount(
				WarehouseAccount warehouseAccount)
		throws Exception {

		WarehouseAccount postWarehouseAccount =
			warehouseAccountResource.postWarehouseIdWarehouseAccount(
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				warehouseAccount);

		_warehouseAccountIds.add(postWarehouseAccount.getWarehouseAccountId());

		return postWarehouseAccount;
	}

	private final List<Long> _accountEntryIds = new ArrayList<>();

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@DeleteAfterTestRun
	private CommerceInventoryWarehouse _commerceInventoryWarehouse;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

	private final List<Long> _warehouseAccountIds = new ArrayList<>();

}