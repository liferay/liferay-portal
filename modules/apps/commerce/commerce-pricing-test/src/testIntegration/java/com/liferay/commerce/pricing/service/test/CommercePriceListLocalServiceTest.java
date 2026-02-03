/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.exception.CommercePriceListExpirationDateException;
import com.liferay.commerce.price.list.exception.DuplicateCommerceBasePriceListException;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CommercePriceListLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			group.getCompanyId());
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			group.getCompanyId(), group.getGroupId(),
			TestPropsValues.getUserId());

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			null, RandomTestUtil.randomString(), _commerceCurrency.getCode(),
			LocaleUtil.US.getDisplayLanguage(), _serviceContext);
	}

	@Test
	public void testAddCommercePriceList() throws Exception {
		try {
			_commercePriceListLocalService.addCommercePriceList(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_commerceCatalog.getGroupId(), _commerceCurrency.getCode(),
				RandomTestUtil.randomBoolean(),
				CommercePriceListConstants.TYPE_PRICE_LIST, 0, true,
				RandomTestUtil.randomString(), RandomTestUtil.randomDouble(), 1,
				1, 2026, 12, 0, 1, 1, 2100, 12, 0, false, _serviceContext);

			Assert.fail();
		}
		catch (DuplicateCommerceBasePriceListException
					duplicateCommerceBasePriceListException) {

			Assert.assertNotNull(duplicateCommerceBasePriceListException);
		}

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.addCommercePriceList(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_commerceCatalog.getGroupId(), _commerceCurrency.getCode(),
				RandomTestUtil.randomBoolean(),
				CommercePriceListConstants.TYPE_PRICE_LIST, 0, false,
				RandomTestUtil.randomString(), RandomTestUtil.randomDouble(), 1,
				1, 2026, 12, 0, 1, 1, 2100, 12, 0, false, _serviceContext);

		Assert.assertNotNull(commercePriceList.getExpirationDate());

		commercePriceList = _commercePriceListLocalService.addCommercePriceList(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_commerceCatalog.getGroupId(), _commerceCurrency.getCode(),
			RandomTestUtil.randomBoolean(),
			CommercePriceListConstants.TYPE_PROMOTION, 0, false,
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(), 1, 1,
			2026, 12, 0, 1, 1, 2100, 12, 0, false, _serviceContext);

		Assert.assertNotNull(commercePriceList.getExpirationDate());
	}

	@Test
	public void testUpdateCommercePriceList() throws Exception {
		CommercePriceList commercePriceList =
			_commercePriceListLocalService.
				getCatalogBaseCommercePriceListByType(
					_commerceCatalog.getGroupId(),
					CommercePriceListConstants.TYPE_PRICE_LIST);

		try {
			commercePriceList =
				_commercePriceListLocalService.updateCommercePriceList(
					commercePriceList.getCommercePriceListId(),
					commercePriceList.getCommerceCurrencyCode(),
					commercePriceList.isNetPrice(),
					commercePriceList.getParentCommercePriceListId(),
					commercePriceList.getName(),
					commercePriceList.getPriority(), 1, 1, 2026, 12, 0, 0, 0, 0,
					0, 0, false, _serviceContext);

			Assert.fail();
		}
		catch (CommercePriceListExpirationDateException
					commercePriceListExpirationDateException) {

			Assert.assertNotNull(commercePriceListExpirationDateException);
		}

		commercePriceList =
			_commercePriceListLocalService.updateCommercePriceList(
				commercePriceList.getCommercePriceListId(),
				commercePriceList.getCommerceCurrencyCode(),
				commercePriceList.isNetPrice(),
				commercePriceList.getParentCommercePriceListId(),
				commercePriceList.getName(), 10, 1, 1, 2026, 12, 0, 0, 0, 0, 0,
				0, true, _serviceContext);

		Assert.assertNull(commercePriceList.getExpirationDate());

		commercePriceList = _commercePriceListLocalService.addCommercePriceList(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_commerceCatalog.getGroupId(), _commerceCurrency.getCode(),
			RandomTestUtil.randomBoolean(),
			CommercePriceListConstants.TYPE_PRICE_LIST, 0, false,
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(), 1, 1,
			2026, 12, 0, 1, 1, 2100, 12, 0, true, _serviceContext);

		Assert.assertNull(commercePriceList.getExpirationDate());

		commercePriceList =
			_commercePriceListLocalService.updateCommercePriceList(
				commercePriceList.getCommercePriceListId(),
				commercePriceList.getCommerceCurrencyCode(),
				commercePriceList.isNetPrice(),
				commercePriceList.getParentCommercePriceListId(),
				commercePriceList.getName(), 10, 1, 1, 2026, 12, 0, 1, 1, 2100,
				12, 0, false, _serviceContext);

		Assert.assertNotNull(commercePriceList.getExpirationDate());

		commercePriceList =
			_commercePriceListLocalService.
				getCatalogBaseCommercePriceListByType(
					_commerceCatalog.getGroupId(),
					CommercePriceListConstants.TYPE_PROMOTION);

		commercePriceList =
			_commercePriceListLocalService.updateCommercePriceList(
				commercePriceList.getCommercePriceListId(),
				commercePriceList.getCommerceCurrencyCode(),
				commercePriceList.isNetPrice(),
				commercePriceList.getParentCommercePriceListId(),
				commercePriceList.getName(), commercePriceList.getPriority(), 1,
				1, 2026, 12, 0, 1, 1, 2100, 12, 0, false, _serviceContext);

		Assert.assertNotNull(commercePriceList.getExpirationDate());

		commercePriceList = _commercePriceListLocalService.addCommercePriceList(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_commerceCatalog.getGroupId(), _commerceCurrency.getCode(),
			RandomTestUtil.randomBoolean(),
			CommercePriceListConstants.TYPE_PROMOTION, 0, false,
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(), 1, 1,
			2026, 12, 0, 1, 1, 2100, 12, 0, true, _serviceContext);

		Assert.assertNull(commercePriceList.getExpirationDate());

		commercePriceList =
			_commercePriceListLocalService.updateCommercePriceList(
				commercePriceList.getCommercePriceListId(),
				commercePriceList.getCommerceCurrencyCode(),
				commercePriceList.isNetPrice(),
				commercePriceList.getParentCommercePriceListId(),
				commercePriceList.getName(), 10, 1, 1, 2026, 12, 0, 1, 1, 2100,
				12, 0, false, _serviceContext);

		Assert.assertNotNull(commercePriceList.getExpirationDate());
	}

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	private ServiceContext _serviceContext;

}