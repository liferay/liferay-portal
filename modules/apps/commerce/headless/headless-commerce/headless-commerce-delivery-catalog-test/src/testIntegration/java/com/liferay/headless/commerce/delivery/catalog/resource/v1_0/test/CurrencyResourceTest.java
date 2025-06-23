/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.Currency;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Page;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Pagination;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class CurrencyResourceTest extends BaseCurrencyResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), RandomTestUtil.randomString());

		User user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			user.getUserId());

		_addCurrency(_commerceChannel, randomCurrency());
	}

	@Override
	@Test
	public void testGetChannelByExternalReferenceCodeCurrenciesPage()
		throws Exception {

		super.testGetChannelByExternalReferenceCodeCurrenciesPage();

		_testGetChannelByExternalReferenceCodeCurrenciesPage();
	}

	@Override
	protected Currency randomCurrency() throws Exception {
		return new Currency() {
			{
				active = Boolean.TRUE;
				code = StringUtil.toLowerCase(RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				formatPattern = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				id = RandomTestUtil.randomLong();
				maxFractionDigits = RandomTestUtil.randomInt();
				minFractionDigits = RandomTestUtil.randomInt();
				name = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				primary = RandomTestUtil.randomBoolean();
				priority = RandomTestUtil.randomDouble();
				symbol = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	@Override
	protected Currency
			testGetChannelByExternalReferenceCodeCurrenciesPage_addCurrency(
				String externalReferenceCode, Currency currency)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.
				getCommerceChannelByExternalReferenceCode(
					externalReferenceCode, testCompany.getCompanyId());

		return _addCurrency(commerceChannel, currency);
	}

	@Override
	protected String
			testGetChannelByExternalReferenceCodeCurrenciesPage_getExternalReferenceCode()
		throws Exception {

		return _commerceChannel.getExternalReferenceCode();
	}

	@Override
	protected Currency testGetChannelCurrenciesPage_addCurrency(
			Long channelId, Currency currency)
		throws Exception {

		return _addCurrency(
			_commerceChannelLocalService.getCommerceChannel(channelId),
			currency);
	}

	@Override
	protected Long testGetChannelCurrenciesPage_getChannelId()
		throws Exception {

		return _commerceChannel.getCommerceChannelId();
	}

	private Currency _addCurrency(
			CommerceChannel commerceChannel, Currency currency)
		throws Exception {

		_commerceCurrency = _commerceCurrencyLocalService.addCommerceCurrency(
			null, TestPropsValues.getUserId(), currency.getCode(),
			LanguageUtils.getLocalizedMap(currency.getName()),
			currency.getSymbol(), currency.getRate(),
			LanguageUtils.getLocalizedMap(currency.getFormatPattern()),
			currency.getMaxFractionDigits(), currency.getMinFractionDigits(),
			currency.getRoundingModeAsString(), currency.getPrimary(),
			currency.getPriority(), currency.getActive());

		_commerceChannelRel =
			_commerceChannelRelLocalService.addCommerceChannelRel(
				CommerceCurrency.class.getName(),
				_commerceCurrency.getCommerceCurrencyId(),
				commerceChannel.getCommerceChannelId(), _serviceContext);

		return new Currency() {
			{
				active = _commerceCurrency.isActive();
				code = _commerceCurrency.getCode();
				externalReferenceCode =
					_commerceCurrency.getExternalReferenceCode();
				formatPattern = LanguageUtils.getLanguageIdMap(
					_commerceCurrency.getFormatPatternMap());
				id = _commerceCurrency.getCommerceCurrencyId();
				maxFractionDigits = _commerceCurrency.getMaxFractionDigits();
				minFractionDigits = _commerceCurrency.getMinFractionDigits();
				name = LanguageUtils.getLanguageIdMap(
					_commerceCurrency.getNameMap());
				primary = _commerceCurrency.isPrimary();
				priority = _commerceCurrency.getPriority();
				rate = _commerceCurrency.getRate();
				roundingMode = RoundingMode.valueOf(
					_commerceCurrency.getRoundingMode());
				symbol = _commerceCurrency.getSymbol();
			}
		};
	}

	private void _testGetChannelByExternalReferenceCodeCurrenciesPage()
		throws Exception {

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), RandomTestUtil.randomString());

		Currency currency1 = _addCurrency(commerceChannel, randomCurrency());

		_addCurrency(commerceChannel, randomCurrency());

		_commerceChannelRelLocalService.deleteCommerceChannelRel(
			_commerceChannelRel.getCommerceChannelRelId());

		int currencyCount =
			_commerceChannelRelLocalService.
				getCommerceCurrencyCommerceChannelRelsCount(
					commerceChannel.getCommerceChannelId(), null);

		Page<Currency> currenciesPage =
			currencyResource.getChannelByExternalReferenceCodeCurrenciesPage(
				commerceChannel.getExternalReferenceCode(), null, null,
				Pagination.of(1, 10), null);

		List<Currency> currencyItems =
			(List<Currency>)currenciesPage.getItems();

		Currency actualCurrency = currencyItems.get(0);

		Assert.assertEquals(
			currencyItems.toString(), currencyCount, currencyItems.size());

		Assert.assertEquals(currency1.getId(), actualCurrency.getId());
	}

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@DeleteAfterTestRun
	private CommerceChannelRel _commerceChannelRel;

	@Inject
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	private ServiceContext _serviceContext;

}