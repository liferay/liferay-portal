/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.commerce.order.rule.service.COREntryLocalService;
import com.liferay.commerce.order.rule.service.COREntryRelLocalService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderRuleChannel;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
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
public class OrderRuleChannelResourceTest
	extends BaseOrderRuleChannelResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_corEntry = _corEntryLocalService.addCOREntry(
			RandomTestUtil.randomString(), _user.getUserId(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(), 1, 1,
			2022, 12, 0, 0, 0, 0, 0, 0, true, RandomTestUtil.randomString(), 0,
			RandomTestUtil.randomString(), StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				testCompany.getCompanyId(), testGroup.getGroupId(),
				_user.getUserId()));
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
	public void testDeleteOrderRuleChannel() throws Exception {
		super.testDeleteOrderRuleChannel();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteOrderRuleChannelBatch() throws Exception {
		super.testDeleteOrderRuleChannelBatch();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteOrderRuleChannel() throws Exception {
		super.testGraphQLDeleteOrderRuleChannel();
	}

	@Override
	protected OrderRuleChannel randomOrderRuleChannel() throws Exception {
		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			RandomTestUtil.randomString());

		return new OrderRuleChannel() {
			{
				channelExternalReferenceCode =
					commerceChannel.getExternalReferenceCode();
				channelId = commerceChannel.getCommerceChannelId();
				orderRuleChannelId = RandomTestUtil.randomLong();
				orderRuleExternalReferenceCode =
					_corEntry.getExternalReferenceCode();
				orderRuleId = _corEntry.getCOREntryId();
			}
		};
	}

	@Override
	protected OrderRuleChannel
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_addOrderRuleChannel(
				String externalReferenceCode, OrderRuleChannel orderRuleChannel)
		throws Exception {

		return _addCOREntryRel(orderRuleChannel);
	}

	@Override
	protected String
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_getExternalReferenceCode()
		throws Exception {

		return _corEntry.getExternalReferenceCode();
	}

	@Override
	protected OrderRuleChannel
			testGetOrderRuleIdOrderRuleChannelsPage_addOrderRuleChannel(
				Long id, OrderRuleChannel orderRuleChannel)
		throws Exception {

		return _addCOREntryRel(orderRuleChannel);
	}

	@Override
	protected Long testGetOrderRuleIdOrderRuleChannelsPage_getId()
		throws Exception {

		return _corEntry.getCOREntryId();
	}

	@Override
	protected OrderRuleChannel
			testPostOrderRuleByExternalReferenceCodeOrderRuleChannel_addOrderRuleChannel(
				OrderRuleChannel orderRuleChannel)
		throws Exception {

		return _addCOREntryRel(orderRuleChannel);
	}

	@Override
	protected OrderRuleChannel
			testPostOrderRuleIdOrderRuleChannel_addOrderRuleChannel(
				OrderRuleChannel orderRuleChannel)
		throws Exception {

		return _addCOREntryRel(orderRuleChannel);
	}

	private OrderRuleChannel _addCOREntryRel(OrderRuleChannel orderRuleChannel)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(
				orderRuleChannel.getChannelId());
		COREntryRel corEntryRel = _corEntryRelLocalService.addCOREntryRel(
			_user.getUserId(), CommerceChannel.class.getName(),
			orderRuleChannel.getChannelId(), orderRuleChannel.getOrderRuleId());

		return new OrderRuleChannel() {
			{
				channelExternalReferenceCode =
					commerceChannel.getExternalReferenceCode();
				channelId = commerceChannel.getCommerceChannelId();
				orderRuleChannelId = corEntryRel.getCOREntryRelId();
				orderRuleExternalReferenceCode =
					_corEntry.getExternalReferenceCode();
				orderRuleId = _corEntry.getCOREntryId();
			}
		};
	}

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private COREntry _corEntry;

	@Inject
	private COREntryLocalService _corEntryLocalService;

	@Inject
	private COREntryRelLocalService _corEntryRelLocalService;

	private User _user;

}