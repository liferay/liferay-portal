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
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.model.CommerceTermEntryRel;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.commerce.term.service.CommerceTermEntryRelLocalService;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderType;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
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
public class OrderTypeResourceTest extends BaseOrderTypeResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_commerceTermEntry =
			_commerceTermEntryLocalService.addCommerceTermEntry(
				RandomTestUtil.randomString(), _user.getUserId(),
				RandomTestUtil.randomBoolean(),
				RandomTestUtil.randomLocaleStringMap(), 1, 1, 2022, 12, 0, 0, 0,
				0, 0, 0, true, RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomString(), RandomTestUtil.nextDouble(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				_serviceContext);
		_corEntry = _corEntryLocalService.addCOREntry(
			RandomTestUtil.randomString(), _user.getUserId(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(), 1, 1,
			2022, 12, 0, 0, 0, 0, 0, 0, true, RandomTestUtil.randomString(), 0,
			RandomTestUtil.randomString(), StringPool.BLANK, _serviceContext);
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetOrderTypeNotFound() throws Exception {
		super.testGraphQLGetOrderTypeNotFound();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"active", "name"};
	}

	@Override
	protected OrderType randomOrderType() {
		return new OrderType() {
			{
				active = true;
				description = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				displayDate = RandomTestUtil.nextDate();
				displayOrder = RandomTestUtil.nextInt();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = RandomTestUtil.randomString();
				id = RandomTestUtil.nextLong();
				name = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				neverExpire = true;
			}
		};
	}

	@Override
	protected OrderType testDeleteOrderType_addOrderType() throws Exception {
		return _addCommerceOrderType(randomOrderType());
	}

	@Override
	protected OrderType
			testDeleteOrderTypeByExternalReferenceCode_addOrderType()
		throws Exception {

		return _addCommerceOrderType(randomOrderType());
	}

	@Override
	protected OrderType testGetOrderRuleOrderTypeOrderType_addOrderType()
		throws Exception {

		return _addCommerceOrderType(randomOrderType());
	}

	@Override
	protected Long testGetOrderRuleOrderTypeOrderType_getOrderRuleOrderTypeId()
		throws Exception {

		return _corEntryRel.getCOREntryRelId();
	}

	@Override
	protected OrderType testGetOrderType_addOrderType() throws Exception {
		return _addCommerceOrderType(randomOrderType());
	}

	@Override
	protected OrderType testGetOrderTypeByExternalReferenceCode_addOrderType()
		throws Exception {

		return _addCommerceOrderType(randomOrderType());
	}

	@Override
	protected OrderType testGetOrderTypesPage_addOrderType(OrderType orderType)
		throws Exception {

		return _addCommerceOrderType(orderType);
	}

	@Override
	protected OrderType testGetTermOrderTypeOrderType_addOrderType()
		throws Exception {

		return _addCommerceOrderType(randomOrderType());
	}

	@Override
	protected Long testGetTermOrderTypeOrderType_getTermOrderTypeId()
		throws Exception {

		return _commerceTermEntryRel.getCommerceTermEntryRelId();
	}

	@Override
	protected Long
			testGraphQLGetOrderRuleOrderTypeOrderType_getOrderRuleOrderTypeId()
		throws Exception {

		return _corEntryRel.getCOREntryRelId();
	}

	@Override
	protected Long testGraphQLGetTermOrderTypeOrderType_getTermOrderTypeId()
		throws Exception {

		return _commerceTermEntryRel.getCommerceTermEntryRelId();
	}

	@Override
	protected OrderType testGraphQLOrderType_addOrderType() throws Exception {
		return _addCommerceOrderType(randomOrderType());
	}

	@Override
	protected OrderType testPatchOrderType_addOrderType() throws Exception {
		return _addCommerceOrderType(randomOrderType());
	}

	@Override
	protected OrderType testPatchOrderTypeByExternalReferenceCode_addOrderType()
		throws Exception {

		return _addCommerceOrderType(randomOrderType());
	}

	@Override
	protected OrderType testPostOrderType_addOrderType(OrderType orderType)
		throws Exception {

		return _addCommerceOrderType(orderType);
	}

	@Override
	protected OrderType testPutOrderTypeByExternalReferenceCode_addOrderType()
		throws Exception {

		return _addCommerceOrderType(randomOrderType());
	}

	private OrderType _addCommerceOrderType(OrderType orderType)
		throws Exception {

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			orderType.getDisplayDate(), _user.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			orderType.getExpirationDate(), _user.getTimeZone());

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.addCommerceOrderType(
				orderType.getExternalReferenceCode(), _user.getUserId(),
				LanguageUtils.getLocalizedMap(orderType.getName()),
				LanguageUtils.getLocalizedMap(orderType.getDescription()),
				GetterUtil.getBoolean(orderType.getActive()),
				displayDateConfig.getMonth(), displayDateConfig.getDay(),
				displayDateConfig.getYear(), displayDateConfig.getHour(),
				displayDateConfig.getMinute(),
				GetterUtil.getInteger(orderType.getDisplayOrder()),
				expirationDateConfig.getMonth(), expirationDateConfig.getDay(),
				expirationDateConfig.getYear() + 1,
				expirationDateConfig.getHour(),
				expirationDateConfig.getMinute(), false, _serviceContext);

		_commerceTermEntryRel =
			_commerceTermEntryRelLocalService.addCommerceTermEntryRel(
				_user.getUserId(), CommerceOrderType.class.getName(),
				commerceOrderType.getCommerceOrderTypeId(),
				_commerceTermEntry.getCommerceTermEntryId());
		_corEntryRel = _corEntryRelLocalService.addCOREntryRel(
			_user.getUserId(), CommerceOrderType.class.getName(),
			commerceOrderType.getCommerceOrderTypeId(),
			_corEntry.getCOREntryId());

		return new OrderType() {
			{
				active = commerceOrderType.isActive();
				description = LanguageUtils.getLanguageIdMap(
					commerceOrderType.getDescriptionMap());
				displayDate = commerceOrderType.getDisplayDate();
				displayOrder = commerceOrderType.getDisplayOrder();
				expirationDate = commerceOrderType.getExpirationDate();
				externalReferenceCode =
					commerceOrderType.getExternalReferenceCode();
				id = commerceOrderType.getCommerceOrderTypeId();
				name = LanguageUtils.getLanguageIdMap(
					commerceOrderType.getNameMap());
			}
		};
	}

	@Inject
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	private CommerceTermEntry _commerceTermEntry;

	@Inject
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	private CommerceTermEntryRel _commerceTermEntryRel;

	@Inject
	private CommerceTermEntryRelLocalService _commerceTermEntryRelLocalService;

	private COREntry _corEntry;

	@Inject
	private COREntryLocalService _corEntryLocalService;

	private COREntryRel _corEntryRel;

	@Inject
	private COREntryRelLocalService _corEntryRelLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}