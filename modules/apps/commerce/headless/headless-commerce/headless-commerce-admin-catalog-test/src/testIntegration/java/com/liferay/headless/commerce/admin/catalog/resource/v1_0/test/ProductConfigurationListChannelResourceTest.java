/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfigurationListChannel;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.core.util.DateConfig;
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
public class ProductConfigurationListChannelResourceTest
	extends BaseProductConfigurationListChannelResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), "USD", "en_US", false,
			_serviceContext);

		DateConfig dateConfig = DateConfig.toDisplayDateConfig(
			RandomTestUtil.nextDate(), _user.getTimeZone());

		_cpConfigurationList =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 0D, dateConfig.getMonth(),
				dateConfig.getDay(), dateConfig.getYear(), dateConfig.getHour(),
				dateConfig.getMinute(), 0, 0, 0, 0, 0, true,
				new ServiceContext());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		for (Long commerceChannelId : _commerceChannelIds) {
			_commerceChannelLocalService.deleteCommerceChannel(
				commerceChannelId);
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
	public void testDeleteProductConfigurationListChannel() throws Exception {
		ProductConfigurationListChannel productConfigurationListChannel =
			productConfigurationListChannelResource.
				postProductConfigurationListIdProductConfigurationListChannel(
					_cpConfigurationList.getCPConfigurationListId(),
					randomProductConfigurationListChannel());

		productConfigurationListChannelResource.
			deleteProductConfigurationListChannel(
				productConfigurationListChannel.
					getProductConfigurationListChannelId());

		Page<ProductConfigurationListChannel> page =
			productConfigurationListChannelResource.
				getProductConfigurationListIdProductConfigurationListChannelsPage(
					_cpConfigurationList.getCPConfigurationListId(), null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(0, page.getTotalCount());
	}

	@Ignore
	@Test
	public void testGraphQLDeleteProductConfigurationListChannel()
		throws Exception {

		super.testGraphQLDeleteProductConfigurationListChannel();
	}

	@Override
	protected ProductConfigurationListChannel
			randomProductConfigurationListChannel()
		throws Exception {

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), "USD");

		_commerceChannelIds.add(commerceChannel.getCommerceChannelId());

		return new ProductConfigurationListChannel() {
			{
				channelExternalReferenceCode =
					commerceChannel.getExternalReferenceCode();
				channelId = commerceChannel.getCommerceChannelId();
				productConfigurationListChannelId = RandomTestUtil.randomLong();
				productConfigurationListExternalReferenceCode =
					_cpConfigurationList.getExternalReferenceCode();
				productConfigurationListId =
					_cpConfigurationList.getCPConfigurationListId();
			}
		};
	}

	@Override
	protected ProductConfigurationListChannel
			testDeleteProductConfigurationListChannelBatch_addProductConfigurationListChannel()
		throws Exception {

		return productConfigurationListChannelResource.
			postProductConfigurationListIdProductConfigurationListChannel(
				_cpConfigurationList.getCPConfigurationListId(),
				randomProductConfigurationListChannel());
	}

	@Override
	protected ProductConfigurationListChannel
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListChannelsPage_addProductConfigurationListChannel(
				String externalReferenceCode,
				ProductConfigurationListChannel productConfigurationListChannel)
		throws Exception {

		return productConfigurationListChannelResource.
			postProductConfigurationListByExternalReferenceCodeProductConfigurationListChannel(
				externalReferenceCode, productConfigurationListChannel);
	}

	@Override
	protected String
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListChannelsPage_getExternalReferenceCode()
		throws Exception {

		return _cpConfigurationList.getExternalReferenceCode();
	}

	@Override
	protected ProductConfigurationListChannel
			testGetProductConfigurationListIdProductConfigurationListChannelsPage_addProductConfigurationListChannel(
				Long id,
				ProductConfigurationListChannel productConfigurationListChannel)
		throws Exception {

		return productConfigurationListChannelResource.
			postProductConfigurationListIdProductConfigurationListChannel(
				id, productConfigurationListChannel);
	}

	@Override
	protected Long
			testGetProductConfigurationListIdProductConfigurationListChannelsPage_getId()
		throws Exception {

		return _cpConfigurationList.getCPConfigurationListId();
	}

	@Override
	protected ProductConfigurationListChannel
			testPostProductConfigurationListByExternalReferenceCodeProductConfigurationListChannel_addProductConfigurationListChannel(
				ProductConfigurationListChannel productConfigurationListChannel)
		throws Exception {

		return productConfigurationListChannelResource.
			postProductConfigurationListIdProductConfigurationListChannel(
				productConfigurationListChannel.getProductConfigurationListId(),
				productConfigurationListChannel);
	}

	@Override
	protected ProductConfigurationListChannel
			testPostProductConfigurationListIdProductConfigurationListChannel_addProductConfigurationListChannel(
				ProductConfigurationListChannel productConfigurationListChannel)
		throws Exception {

		return productConfigurationListChannelResource.
			postProductConfigurationListIdProductConfigurationListChannel(
				_cpConfigurationList.getCPConfigurationListId(),
				productConfigurationListChannel);
	}

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private final List<Long> _commerceChannelIds = new ArrayList<>();

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}