/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelRelLocalServiceUtil;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.WarehouseChannel;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Page;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Pagination;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
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
public class WarehouseChannelResourceTest
	extends BaseWarehouseChannelResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testGroup.getCompanyId());

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

		for (Long warehouseChannelId : _warehouseChannelIds) {
			CommerceChannelRelLocalServiceUtil.deleteCommerceChannelRel(
				warehouseChannelId);
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
	public void testDeleteWarehouseChannel() throws Exception {
		long commerceInventoryWarehouseId =
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId();

		WarehouseChannel warehouseChannel =
			warehouseChannelResource.postWarehouseIdWarehouseChannel(
				commerceInventoryWarehouseId, randomWarehouseChannel());

		warehouseChannelResource.deleteWarehouseChannel(
			warehouseChannel.getWarehouseChannelId());

		Page<WarehouseChannel> page =
			warehouseChannelResource.getWarehouseIdWarehouseChannelsPage(
				commerceInventoryWarehouseId, null, null, Pagination.of(1, 10),
				null);

		Assert.assertEquals(0, page.getTotalCount());
	}

	@Ignore
	@Override
	@Test
	public void testGetWarehouseIdWarehouseChannelsPageWithFilterDateTimeEquals()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetWarehouseIdWarehouseChannelsPageWithFilterDoubleEquals()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetWarehouseIdWarehouseChannelsPageWithFilterStringEquals()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetWarehouseIdWarehouseChannelsPageWithSortDateTime()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetWarehouseIdWarehouseChannelsPageWithSortDouble()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetWarehouseIdWarehouseChannelsPageWithSortInteger()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetWarehouseIdWarehouseChannelsPageWithSortString()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteWarehouseChannel() throws Exception {
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"channelExternalReferenceCode", "channelId",
			"warehouseExternalReferenceCode", "warehouseId"
		};
	}

	@Override
	protected WarehouseChannel randomWarehouseChannel() throws Exception {
		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());

		_commerceChannels.add(_commerceChannel);

		return new WarehouseChannel() {
			{
				channelExternalReferenceCode =
					_commerceChannel.getExternalReferenceCode();
				channelId = _commerceChannel.getCommerceChannelId();

				warehouseExternalReferenceCode =
					_commerceInventoryWarehouse.getExternalReferenceCode();
				warehouseId =
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId();
			}
		};
	}

	@Override
	protected WarehouseChannel
			testDeleteWarehouseChannelBatch_addWarehouseChannel()
		throws Exception {

		return warehouseChannelResource.postWarehouseIdWarehouseChannel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			randomWarehouseChannel());
	}

	@Override
	protected WarehouseChannel
			testGetWarehouseByExternalReferenceCodeWarehouseChannelsPage_addWarehouseChannel(
				String externalReferenceCode, WarehouseChannel warehouseChannel)
		throws Exception {

		return warehouseChannelResource.
			postWarehouseByExternalReferenceCodeWarehouseChannel(
				externalReferenceCode, warehouseChannel);
	}

	@Override
	protected String
			testGetWarehouseByExternalReferenceCodeWarehouseChannelsPage_getExternalReferenceCode()
		throws Exception {

		return _commerceInventoryWarehouse.getExternalReferenceCode();
	}

	@Override
	protected WarehouseChannel
			testGetWarehouseIdWarehouseChannelsPage_addWarehouseChannel(
				Long id, WarehouseChannel warehouseChannel)
		throws Exception {

		return warehouseChannelResource.postWarehouseIdWarehouseChannel(
			id, warehouseChannel);
	}

	@Override
	protected Long testGetWarehouseIdWarehouseChannelsPage_getId()
		throws Exception {

		return _commerceInventoryWarehouse.getCommerceInventoryWarehouseId();
	}

	@Override
	protected WarehouseChannel
			testPostWarehouseByExternalReferenceCodeWarehouseChannel_addWarehouseChannel(
				WarehouseChannel warehouseChannel)
		throws Exception {

		WarehouseChannel postWarehouseChannel =
			warehouseChannelResource.
				postWarehouseByExternalReferenceCodeWarehouseChannel(
					_commerceInventoryWarehouse.getExternalReferenceCode(),
					warehouseChannel);

		_warehouseChannelIds.add(postWarehouseChannel.getWarehouseChannelId());

		return postWarehouseChannel;
	}

	@Override
	protected WarehouseChannel
			testPostWarehouseIdWarehouseChannel_addWarehouseChannel(
				WarehouseChannel warehouseChannel)
		throws Exception {

		WarehouseChannel postWarehouseChannel =
			warehouseChannelResource.postWarehouseIdWarehouseChannel(
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				warehouseChannel);

		_warehouseChannelIds.add(postWarehouseChannel.getWarehouseChannelId());

		return postWarehouseChannel;
	}

	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private List<CommerceChannel> _commerceChannels = new ArrayList<>();

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private CommerceInventoryWarehouse _commerceInventoryWarehouse;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

	private final List<Long> _warehouseChannelIds = new ArrayList<>();

}