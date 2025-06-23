/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountOrderTypeRelLocalService;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountOrderType;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 */
@RunWith(Arquillian.class)
public class DiscountOrderTypeResourceTest
	extends BaseDiscountOrderTypeResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		Calendar calendar = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		_commerceDiscount =
			_commerceDiscountLocalService.addOrUpdateCommerceDiscount(
				RandomTestUtil.randomString(), _user.getUserId(), 0,
				RandomTestUtil.randomString(),
				CommerceDiscountConstants.TARGET_PRODUCTS, false, null, false,
				BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO,
				BigDecimal.ZERO, BigDecimal.ZERO,
				CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED, 0, true,
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), true, _serviceContext);
	}

	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
	}

	@Override
	@Test
	public void testDeleteDiscountOrderType() throws Exception {
	}

	@Override
	@Test
	public void testDeleteDiscountOrderTypeBatch() throws Exception {
	}

	@Override
	@Test
	public void testGraphQLDeleteDiscountOrderType() throws Exception {
	}

	@Override
	protected Collection<EntityField> getEntityFields() throws Exception {
		return new ArrayList<>();
	}

	@Override
	protected DiscountOrderType randomDiscountOrderType() throws Exception {
		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			RandomTestUtil.nextDate(), _user.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			RandomTestUtil.nextDate(), _user.getTimeZone());

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.addCommerceOrderType(
				RandomTestUtil.randomString(), _user.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean(), displayDateConfig.getMonth(),
				displayDateConfig.getDay(), displayDateConfig.getYear(),
				displayDateConfig.getHour(), displayDateConfig.getMinute(), 0,
				expirationDateConfig.getMonth(), expirationDateConfig.getDay(),
				expirationDateConfig.getYear(), expirationDateConfig.getHour(),
				expirationDateConfig.getMinute(), true, _serviceContext);

		return new DiscountOrderType() {
			{
				discountExternalReferenceCode =
					_commerceDiscount.getExternalReferenceCode();
				discountId = _commerceDiscount.getCommerceDiscountId();
				discountOrderTypeId = RandomTestUtil.randomLong();
				orderTypeExternalReferenceCode =
					commerceOrderType.getExternalReferenceCode();
				orderTypeId = commerceOrderType.getCommerceOrderTypeId();
				priority = RandomTestUtil.nextInt();
			}
		};
	}

	@Override
	protected DiscountOrderType
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_addDiscountOrderType(
				String externalReferenceCode,
				DiscountOrderType discountOrderType)
		throws Exception {

		return _addDiscountOrderType(discountOrderType);
	}

	@Override
	protected String
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_getExternalReferenceCode()
		throws Exception {

		return _commerceDiscount.getExternalReferenceCode();
	}

	@Override
	protected DiscountOrderType
			testGetDiscountIdDiscountOrderTypesPage_addDiscountOrderType(
				Long id, DiscountOrderType discountOrderType)
		throws Exception {

		return _addDiscountOrderType(discountOrderType);
	}

	@Override
	protected Long testGetDiscountIdDiscountOrderTypesPage_getId()
		throws Exception {

		return _commerceDiscount.getCommerceDiscountId();
	}

	@Override
	protected DiscountOrderType
			testPostDiscountByExternalReferenceCodeDiscountOrderType_addDiscountOrderType(
				DiscountOrderType discountOrderType)
		throws Exception {

		return _addDiscountOrderType(discountOrderType);
	}

	@Override
	protected DiscountOrderType
			testPostDiscountIdDiscountOrderType_addDiscountOrderType(
				DiscountOrderType discountOrderType)
		throws Exception {

		return _addDiscountOrderType(discountOrderType);
	}

	private DiscountOrderType _addDiscountOrderType(
			DiscountOrderType discountOrderType)
		throws Exception {

		return _toDiscountOrderType(
			_commerceDiscountOrderTypeRelLocalService.
				addCommerceDiscountOrderTypeRel(
					_serviceContext.getUserId(),
					_commerceDiscount.getCommerceDiscountId(),
					discountOrderType.getOrderTypeId(),
					discountOrderType.getPriority(), _serviceContext));
	}

	private DiscountOrderType _toDiscountOrderType(
			CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel)
		throws Exception {

		CommerceDiscount commerceDiscount =
			commerceDiscountOrderTypeRel.getCommerceDiscount();

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.fetchCommerceOrderType(
				commerceDiscountOrderTypeRel.getCommerceOrderTypeId());

		return new DiscountOrderType() {
			{
				discountExternalReferenceCode =
					commerceDiscount.getExternalReferenceCode();
				discountId = commerceDiscount.getCommerceDiscountId();
				discountOrderTypeId =
					commerceDiscountOrderTypeRel.
						getCommerceDiscountOrderTypeRelId();
				orderTypeExternalReferenceCode =
					commerceOrderType.getExternalReferenceCode();
				orderTypeId = commerceOrderType.getCommerceOrderTypeId();
				priority = commerceDiscountOrderTypeRel.getPriority();
			}
		};
	}

	private CommerceDiscount _commerceDiscount;

	@Inject
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	@Inject
	private CommerceDiscountOrderTypeRelLocalService
		_commerceDiscountOrderTypeRelLocalService;

	@Inject
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}