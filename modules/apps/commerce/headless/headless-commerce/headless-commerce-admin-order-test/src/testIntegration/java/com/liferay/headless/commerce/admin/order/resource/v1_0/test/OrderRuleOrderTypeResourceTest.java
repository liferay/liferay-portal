/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.commerce.order.rule.service.COREntryLocalService;
import com.liferay.commerce.order.rule.service.COREntryRelLocalService;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderRuleOrderType;
import com.liferay.petra.string.StringPool;
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
public class OrderRuleOrderTypeResourceTest
	extends BaseOrderRuleOrderTypeResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_corEntry = _corEntryLocalService.addCOREntry(
			RandomTestUtil.randomString(), _user.getUserId(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(), 1, 1,
			2022, 12, 0, 0, 0, 0, 0, 0, true, RandomTestUtil.randomString(), 0,
			RandomTestUtil.randomString(), StringPool.BLANK, _serviceContext);
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
	public void testDeleteOrderRuleOrderType() throws Exception {
		super.testDeleteOrderRuleOrderType();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteOrderRuleOrderTypeBatch() throws Exception {
		super.testDeleteOrderRuleOrderTypeBatch();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteOrderRuleOrderType() throws Exception {
		super.testGraphQLDeleteOrderRuleOrderType();
	}

	@Override
	protected OrderRuleOrderType randomOrderRuleOrderType() throws Exception {
		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.addCommerceOrderType(
				RandomTestUtil.randomString(), _user.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean(), 1, 1, 2022, 12, 0, 0, 0, 0, 0,
				0, 0, true, _serviceContext);

		return new OrderRuleOrderType() {
			{
				orderRuleExternalReferenceCode =
					_corEntry.getExternalReferenceCode();
				orderRuleId = _corEntry.getCOREntryId();
				orderRuleOrderTypeId = RandomTestUtil.randomLong();
				orderTypeExternalReferenceCode =
					commerceOrderType.getExternalReferenceCode();
				orderTypeId = commerceOrderType.getCommerceOrderTypeId();
			}
		};
	}

	@Override
	protected OrderRuleOrderType
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_addOrderRuleOrderType(
				String externalReferenceCode,
				OrderRuleOrderType orderRuleOrderType)
		throws Exception {

		return _addCOREntryRel(orderRuleOrderType);
	}

	@Override
	protected String
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_getExternalReferenceCode()
		throws Exception {

		return _corEntry.getExternalReferenceCode();
	}

	@Override
	protected OrderRuleOrderType
			testGetOrderRuleIdOrderRuleOrderTypesPage_addOrderRuleOrderType(
				Long id, OrderRuleOrderType orderRuleOrderType)
		throws Exception {

		return _addCOREntryRel(orderRuleOrderType);
	}

	@Override
	protected Long testGetOrderRuleIdOrderRuleOrderTypesPage_getId()
		throws Exception {

		return _corEntry.getCOREntryId();
	}

	@Override
	protected OrderRuleOrderType
			testPostOrderRuleByExternalReferenceCodeOrderRuleOrderType_addOrderRuleOrderType(
				OrderRuleOrderType orderRuleOrderType)
		throws Exception {

		return _addCOREntryRel(orderRuleOrderType);
	}

	@Override
	protected OrderRuleOrderType
			testPostOrderRuleIdOrderRuleOrderType_addOrderRuleOrderType(
				OrderRuleOrderType orderRuleOrderType)
		throws Exception {

		return _addCOREntryRel(orderRuleOrderType);
	}

	private OrderRuleOrderType _addCOREntryRel(
			OrderRuleOrderType orderRuleOrderType)
		throws Exception {

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.getCommerceOrderType(
				orderRuleOrderType.getOrderTypeId());
		COREntryRel corEntryRel = _corEntryRelLocalService.addCOREntryRel(
			_user.getUserId(), CommerceOrderType.class.getName(),
			orderRuleOrderType.getOrderTypeId(),
			orderRuleOrderType.getOrderRuleId());

		return new OrderRuleOrderType() {
			{
				orderRuleExternalReferenceCode =
					_corEntry.getExternalReferenceCode();
				orderRuleId = _corEntry.getCOREntryId();
				orderRuleOrderTypeId = corEntryRel.getCOREntryRelId();
				orderTypeExternalReferenceCode =
					commerceOrderType.getExternalReferenceCode();
				orderTypeId = commerceOrderType.getCommerceOrderTypeId();
			}
		};
	}

	@Inject
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	private COREntry _corEntry;

	@Inject
	private COREntryLocalService _corEntryLocalService;

	@Inject
	private COREntryRelLocalService _corEntryRelLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}