/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lianne Louie
 */
@RunWith(Arquillian.class)
public class CommerceCatalogModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testOnAfterCreate() throws Exception {
		_testOnAfterCreate(_LocaleUtil.SPAIN);
		_testOnAfterCreate(LocaleUtil.US);
	}

	private void _testOnAfterCreate(Locale locale) throws Exception {
		Group group = GroupTestUtil.addGroup();

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(group.getCompanyId());

		String name = RandomTestUtil.randomString();

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, name, commerceCurrency.getCode(),
				LocaleUtil.toLanguageId(locale),
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Assert.assertEquals(
			_language.format(locale, "x-base-price-list", name, false),
			_getCommercePriceListName(
				commerceCatalog, CommercePriceListConstants.TYPE_PRICE_LIST));
		Assert.assertEquals(
			_language.format(locale, "x-base-promotion", name, false),
			_getCommercePriceListName(
				commerceCatalog, CommercePriceListConstants.TYPE_PROMOTION));
	}

	private String _getCommercePriceListName(
		CommerceCatalog commerceCatalog, String type) {

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.
				fetchCatalogBaseCommercePriceListByType(
					commerceCatalog.getGroupId(), type);

		return commercePriceList.getName();
	}

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private Language _language;

}