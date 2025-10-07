/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Currency;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.CurrencySerDes;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 */
@RunWith(Arquillian.class)
public class CurrencyResourceTest extends BaseCurrencyResourceTestCase {

	@Override
	@Test
	public void testGetCurrenciesPage() throws Exception {
		Page<Currency> page = currencyResource.getCurrenciesPage(
			null, null, Pagination.of(1, -1), null);

		long totalCount = page.getTotalCount();

		Currency currency1 = testGetCurrenciesPage_addCurrency(
			randomCurrency());

		Currency currency2 = testGetCurrenciesPage_addCurrency(
			randomCurrency());

		page = currencyResource.getCurrenciesPage(
			null, null, Pagination.of(1, -1), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(currency1, (List<Currency>)page.getItems());
		assertContains(currency2, (List<Currency>)page.getItems());
		assertValid(page, testGetCurrenciesPage_getExpectedActions());

		currencyResource.deleteCurrency(currency1.getId());
		currencyResource.deleteCurrency(currency2.getId());
	}

	@Override
	@Test
	public void testGraphQLGetCurrenciesPage() throws Exception {

		// Namespace headlessCommerceAdminCatalog_v1_0

		GraphQLField graphQLField = new GraphQLField(
			"currencies",
			HashMapBuilder.<String, Object>put(
				"page", 1
			).put(
				"pageSize",
				() -> {
					int commerceCurrenciesCount =
						_commerceCurrencyLocalService.
							getCommerceCurrenciesCount(
								TestPropsValues.getCompanyId());

					return commerceCurrenciesCount + 10;
				}
			).build(),
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject currenciesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminCatalog_v1_0",
			"JSONObject/currencies");

		long totalCount = currenciesJSONObject.getLong("totalCount");

		Currency currency1 = testGraphQLCurrency_addCurrency(randomCurrency());
		Currency currency2 = testGraphQLCurrency_addCurrency(randomCurrency());

		currenciesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminCatalog_v1_0",
			"JSONObject/currencies");

		Assert.assertEquals(
			totalCount + 2, currenciesJSONObject.getLong("totalCount"));

		assertContains(
			currency1,
			Arrays.asList(
				CurrencySerDes.toDTOs(
					currenciesJSONObject.getString("items"))));
		assertContains(
			currency2,
			Arrays.asList(
				CurrencySerDes.toDTOs(
					currenciesJSONObject.getString("items"))));

		// No namespace

		currenciesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/currencies");

		Assert.assertEquals(
			totalCount + 2, currenciesJSONObject.getLong("totalCount"));

		assertContains(
			currency1,
			Arrays.asList(
				CurrencySerDes.toDTOs(
					currenciesJSONObject.getString("items"))));
		assertContains(
			currency2,
			Arrays.asList(
				CurrencySerDes.toDTOs(
					currenciesJSONObject.getString("items"))));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"active", "name", "symbol"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"name", "formatPattern"};
	}

	@Override
	protected Currency randomCurrency() throws Exception {
		return new Currency() {
			{
				active = Boolean.TRUE;
				code = StringUtil.toLowerCase(RandomTestUtil.randomString());
				formatPattern = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				id = RandomTestUtil.randomLong();
				maxFractionDigits = RandomTestUtil.randomInt();
				minFractionDigits = RandomTestUtil.randomInt();
				name = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				primary = RandomTestUtil.randomBoolean();
				priority = RandomTestUtil.randomDouble();
				rate = BigDecimal.valueOf(RandomTestUtil.randomDouble());
				roundingMode = RandomTestUtil.randomEnum(RoundingMode.class);
				symbol = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	@Override
	protected Currency testDeleteCurrency_addCurrency() throws Exception {
		return _addCommerceCurrency(randomCurrency());
	}

	@Override
	protected Currency testDeleteCurrencyByExternalReferenceCode_addCurrency()
		throws Exception {

		return _addCommerceCurrency(randomCurrency());
	}

	@Override
	protected Currency testGetCurrenciesPage_addCurrency(Currency currency)
		throws Exception {

		return _addCommerceCurrency(currency);
	}

	@Override
	protected Currency testGetCurrency_addCurrency() throws Exception {
		return _addCommerceCurrency(randomCurrency());
	}

	@Override
	protected Currency testGetCurrencyByExternalReferenceCode_addCurrency()
		throws Exception {

		return _addCommerceCurrency(randomCurrency());
	}

	@Override
	protected Currency testGraphQLCurrency_addCurrency() throws Exception {
		return _addCommerceCurrency(randomCurrency());
	}

	@Override
	protected Currency testPatchCurrency_addCurrency() throws Exception {
		return _addCommerceCurrency(randomCurrency());
	}

	@Override
	protected Currency testPatchCurrencyByExternalReferenceCode_addCurrency()
		throws Exception {

		return _addCommerceCurrency(randomCurrency());
	}

	@Override
	protected Currency testPostCurrency_addCurrency(Currency currency)
		throws Exception {

		return _addCommerceCurrency(currency);
	}

	private Currency _addCommerceCurrency(Currency currency) throws Exception {
		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.addCommerceCurrency(
				null, TestPropsValues.getUserId(), currency.getCode(),
				LanguageUtils.getLocalizedMap(currency.getName()),
				currency.getSymbol(), currency.getRate(),
				LanguageUtils.getLocalizedMap(currency.getFormatPattern()),
				currency.getMaxFractionDigits(),
				currency.getMinFractionDigits(),
				currency.getRoundingModeAsString(), currency.getPrimary(),
				currency.getPriority(), currency.getActive());

		return new Currency() {
			{
				active = commerceCurrency.isActive();
				code = commerceCurrency.getCode();
				externalReferenceCode =
					commerceCurrency.getExternalReferenceCode();
				formatPattern = LanguageUtils.getLanguageIdMap(
					commerceCurrency.getFormatPatternMap());
				id = commerceCurrency.getCommerceCurrencyId();
				maxFractionDigits = commerceCurrency.getMaxFractionDigits();
				minFractionDigits = commerceCurrency.getMinFractionDigits();
				name = LanguageUtils.getLanguageIdMap(
					commerceCurrency.getNameMap());
				primary = commerceCurrency.isPrimary();
				priority = commerceCurrency.getPriority();
				rate = commerceCurrency.getRate();
				roundingMode = RoundingMode.valueOf(
					commerceCurrency.getRoundingMode());
				symbol = commerceCurrency.getSymbol();
			}
		};
	}

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

}