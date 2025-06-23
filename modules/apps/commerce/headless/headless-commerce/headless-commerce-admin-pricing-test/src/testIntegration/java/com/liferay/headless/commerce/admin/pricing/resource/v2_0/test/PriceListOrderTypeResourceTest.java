/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRel;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListOrderTypeRelLocalService;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceListOrderType;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;

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
public class PriceListOrderTypeResourceTest
	extends BasePriceListOrderTypeResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				_serviceContext.getCompanyId());

		Calendar calendar = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		_commercePriceList =
			_commercePriceListLocalService.addCommercePriceList(
				RandomTestUtil.randomString(), _user.getUserId(),
				TestPropsValues.getGroupId(), commerceCurrency.getCode(), true,
				CommercePriceListConstants.TYPE_PRICE_LIST, 0, false,
				RandomTestUtil.randomString(), 0, calendar.get(Calendar.MONTH),
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
	public void testDeletePriceListOrderType() throws Exception {
	}

	@Override
	@Test
	public void testDeletePriceListOrderTypeBatch() throws Exception {
	}

	@Override
	@Test
	public void testGraphQLDeletePriceListOrderType() throws Exception {
	}

	@Override
	protected Collection<EntityField> getEntityFields() throws Exception {
		return new ArrayList<>();
	}

	@Override
	protected PriceListOrderType randomPriceListOrderType() throws Exception {
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

		return new PriceListOrderType() {
			{
				orderTypeExternalReferenceCode =
					commerceOrderType.getExternalReferenceCode();
				orderTypeId = commerceOrderType.getCommerceOrderTypeId();
				priceListExternalReferenceCode =
					_commercePriceList.getExternalReferenceCode();
				priceListId = _commercePriceList.getCommercePriceListId();
				priceListOrderTypeId = RandomTestUtil.randomLong();
				priority = RandomTestUtil.nextInt();
			}
		};
	}

	@Override
	protected PriceListOrderType
			testGetPriceListByExternalReferenceCodePriceListOrderTypesPage_addPriceListOrderType(
				String externalReferenceCode,
				PriceListOrderType priceListOrderType)
		throws Exception {

		return _addPriceListOrderType(priceListOrderType);
	}

	@Override
	protected String
			testGetPriceListByExternalReferenceCodePriceListOrderTypesPage_getExternalReferenceCode()
		throws Exception {

		return _commercePriceList.getExternalReferenceCode();
	}

	@Override
	protected PriceListOrderType
			testGetPriceListIdPriceListOrderTypesPage_addPriceListOrderType(
				Long id, PriceListOrderType priceListOrderType)
		throws Exception {

		return _addPriceListOrderType(priceListOrderType);
	}

	@Override
	protected Long testGetPriceListIdPriceListOrderTypesPage_getId()
		throws Exception {

		return _commercePriceList.getCommercePriceListId();
	}

	@Override
	protected PriceListOrderType
			testPostPriceListByExternalReferenceCodePriceListOrderType_addPriceListOrderType(
				PriceListOrderType priceListOrderType)
		throws Exception {

		return _addPriceListOrderType(priceListOrderType);
	}

	@Override
	protected PriceListOrderType
			testPostPriceListIdPriceListOrderType_addPriceListOrderType(
				PriceListOrderType priceListOrderType)
		throws Exception {

		return _addPriceListOrderType(priceListOrderType);
	}

	private PriceListOrderType _addPriceListOrderType(
			PriceListOrderType priceListOrderType)
		throws Exception {

		return _toPriceListOrderType(
			_commercePriceListOrderTypeRelLocalService.
				addCommercePriceListOrderTypeRel(
					_serviceContext.getUserId(),
					_commercePriceList.getCommercePriceListId(),
					priceListOrderType.getOrderTypeId(),
					priceListOrderType.getPriority(), _serviceContext));
	}

	private PriceListOrderType _toPriceListOrderType(
			CommercePriceListOrderTypeRel commercePriceListOrderTypeRel)
		throws Exception {

		CommercePriceList commercePriceList =
			commercePriceListOrderTypeRel.getCommercePriceList();

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.fetchCommerceOrderType(
				commercePriceListOrderTypeRel.getCommerceOrderTypeId());

		return new PriceListOrderType() {
			{
				orderTypeExternalReferenceCode =
					commerceOrderType.getExternalReferenceCode();
				orderTypeId = commerceOrderType.getCommerceOrderTypeId();
				priceListExternalReferenceCode =
					commercePriceList.getExternalReferenceCode();
				priceListId = commercePriceList.getCommercePriceListId();
				priceListOrderTypeId =
					commercePriceListOrderTypeRel.
						getCommercePriceListOrderTypeRelId();
				priority = commercePriceListOrderTypeRel.getPriority();
			}
		};
	}

	@Inject
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	private CommercePriceList _commercePriceList;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommercePriceListOrderTypeRelLocalService
		_commercePriceListOrderTypeRelLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}