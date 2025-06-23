/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceOrderTypeRel;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.commerce.service.CommerceOrderTypeRelLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderTypeChannel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 * @author Stefano Motta
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class OrderTypeChannelResourceTest
	extends BaseOrderTypeChannelResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_commerceOrderType =
			_commerceOrderTypeLocalService.addCommerceOrderType(
				RandomTestUtil.randomString(), _user.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean(), 1, 1, 2022, 12, 0, 0, 0, 0, 0,
				0, 0, true, _serviceContext);
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
	public void testDeleteOrderTypeChannel() throws Exception {
		super.testDeleteOrderTypeChannel();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteOrderTypeChannelBatch() throws Exception {
		super.testDeleteOrderTypeChannelBatch();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteOrderTypeChannel() throws Exception {
		super.testGraphQLDeleteOrderTypeChannel();
	}

	@Override
	protected OrderTypeChannel randomOrderTypeChannel() throws Exception {
		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			RandomTestUtil.randomString());

		return new OrderTypeChannel() {
			{
				channelExternalReferenceCode =
					commerceChannel.getExternalReferenceCode();
				channelId = commerceChannel.getCommerceChannelId();
				orderTypeChannelId = RandomTestUtil.randomLong();
				orderTypeExternalReferenceCode =
					_commerceOrderType.getExternalReferenceCode();
				orderTypeId = _commerceOrderType.getCommerceOrderTypeId();
			}
		};
	}

	@Override
	protected OrderTypeChannel
			testGetOrderTypeByExternalReferenceCodeOrderTypeChannelsPage_addOrderTypeChannel(
				String externalReferenceCode, OrderTypeChannel orderTypeChannel)
		throws Exception {

		return _addCommerceOrderTypeRel(orderTypeChannel);
	}

	@Override
	protected String
			testGetOrderTypeByExternalReferenceCodeOrderTypeChannelsPage_getExternalReferenceCode()
		throws Exception {

		return _commerceOrderType.getExternalReferenceCode();
	}

	@Override
	protected OrderTypeChannel
			testGetOrderTypeIdOrderTypeChannelsPage_addOrderTypeChannel(
				Long id, OrderTypeChannel orderTypeChannel)
		throws Exception {

		return _addCommerceOrderTypeRel(orderTypeChannel);
	}

	@Override
	protected Long testGetOrderTypeIdOrderTypeChannelsPage_getId()
		throws Exception {

		return _commerceOrderType.getCommerceOrderTypeId();
	}

	@Override
	protected OrderTypeChannel
			testPostOrderTypeByExternalReferenceCodeOrderTypeChannel_addOrderTypeChannel(
				OrderTypeChannel orderTypeChannel)
		throws Exception {

		return _addCommerceOrderTypeRel(orderTypeChannel);
	}

	@Override
	protected OrderTypeChannel
			testPostOrderTypeIdOrderTypeChannel_addOrderTypeChannel(
				OrderTypeChannel orderTypeChannel)
		throws Exception {

		return _addCommerceOrderTypeRel(orderTypeChannel);
	}

	private OrderTypeChannel _addCommerceOrderTypeRel(
			OrderTypeChannel orderTypeChannel)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(
				orderTypeChannel.getChannelId());
		CommerceOrderTypeRel commerceOrderTypeRel =
			_commerceOrderTypeRelLocalService.addCommerceOrderTypeRel(
				_user.getUserId(), CommerceChannel.class.getName(),
				orderTypeChannel.getChannelId(),
				orderTypeChannel.getOrderTypeId(), _serviceContext);

		return new OrderTypeChannel() {
			{
				channelExternalReferenceCode =
					commerceChannel.getExternalReferenceCode();
				channelId = commerceChannel.getCommerceChannelId();
				orderTypeChannelId =
					commerceOrderTypeRel.getCommerceOrderTypeRelId();
				orderTypeExternalReferenceCode =
					_commerceOrderType.getExternalReferenceCode();
				orderTypeId = _commerceOrderType.getCommerceOrderTypeId();
			}
		};
	}

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private CommerceOrderType _commerceOrderType;

	@Inject
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	@Inject
	private CommerceOrderTypeRelLocalService _commerceOrderTypeRelLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}