/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalServiceUtil;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.test.util.CommerceDiscountTestUtil;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.CommerceProductPriceRequest;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.test.util.CommercePriceEntryTestUtil;
import com.liferay.commerce.price.list.test.util.CommercePriceListTestUtil;
import com.liferay.commerce.pricing.constants.CommercePriceModifierConstants;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.service.CommercePriceModifierLocalService;
import com.liferay.commerce.pricing.service.CommercePriceModifierRelLocalService;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.option.CommerceOptionValue;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.test.util.CommerceProductTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Riccardo Alberti
 */
@RunWith(Arquillian.class)
public class CommerceProductPriceCalculationV2Test {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_accountEntry = CommerceAccountTestUtil.getPersonAccountEntry(
			_user.getUserId());

		_commerceCurrency =
			CommerceCurrencyLocalServiceUtil.fetchPrimaryCommerceCurrency(
				_group.getCompanyId());

		if (_commerceCurrency == null) {
			_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
				_group.getCompanyId());
		}

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());
	}

	@After
	public void tearDown() throws Exception {
		_commercePriceListLocalService.deleteCommercePriceLists(
			_group.getCompanyId());
	}

	@Test
	public void testCalculatePriceDynamicOptionSKU() throws Exception {
		frutillaRule.scenario(
			"The price of a product with 3 option values selected is calculated"
		).given(
			"A product with 3 option values linked to SKUs with price type " +
				"dynamic"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned given the quantity"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice1 = BigDecimal.valueOf(35);

		CPDefinition cpDefinition1 = cpInstance1.getCPDefinition();

		CProduct cProduct1 = cpDefinition1.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct1.getCProductId(),
			cpInstance1.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice1);

		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice2 = BigDecimal.valueOf(100);

		CPDefinition cpDefinition2 = cpInstance2.getCPDefinition();

		CProduct cProduct2 = cpDefinition2.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct2.getCProductId(),
			cpInstance2.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice2);

		CPInstance cpInstance3 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice3 = BigDecimal.valueOf(150);

		CPDefinition cpDefinition3 = cpInstance3.getCPDefinition();

		CProduct cProduct3 = cpDefinition3.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct3.getCProductId(),
			cpInstance3.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice3);

		CPInstance cpInstance4 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice4 = BigDecimal.valueOf(200);

		CPDefinition cpDefinition4 = cpInstance4.getCPDefinition();

		CProduct cProduct4 = cpDefinition4.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct4.getCProductId(),
			cpInstance4.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice4);

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance2.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance3.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance4.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, BigDecimal.ONE));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance1.getCPInstanceId(), BigDecimal.ONE, true,
					StringPool.BLANK));

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = cpInstancePrice1.add(cpInstancePrice2);

		expectedPrice = expectedPrice.add(cpInstancePrice3);

		expectedPrice = expectedPrice.add(cpInstancePrice4);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testCalculatePriceDynamicOptionSKUWithPromo() throws Exception {
		frutillaRule.scenario(
			"The price of a product with 3 option values selected is calculated"
		).given(
			"A product with 3 option values linked to SKUs with price type " +
				"dynamic"
		).and(
			"Some linked SKUs have a promo price"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned given the quantity"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice1 = BigDecimal.valueOf(35);

		CPDefinition cpDefinition1 = cpInstance1.getCPDefinition();

		CProduct cProduct1 = cpDefinition1.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct1.getCProductId(),
			cpInstance1.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice1);

		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice2 = BigDecimal.valueOf(100);

		CPDefinition cpDefinition2 = cpInstance2.getCPDefinition();

		CProduct cProduct2 = cpDefinition2.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct2.getCProductId(),
			cpInstance2.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice2);

		CPInstance cpInstance3 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice3 = BigDecimal.valueOf(150);

		CPDefinition cpDefinition3 = cpInstance3.getCPDefinition();

		CProduct cProduct3 = cpDefinition3.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct3.getCProductId(),
			cpInstance3.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice3);

		BigDecimal cpInstancePromoPrice2 = BigDecimal.valueOf(100);

		CommercePriceList commercePromotion =
			CommercePriceListTestUtil.addPromotion(
				commerceCatalog.getGroupId(), 0.0);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition3.getCProductId(),
			cpInstance3.getCPInstanceUuid(),
			commercePromotion.getCommercePriceListId(), cpInstancePromoPrice2,
			false, null, null, null, null, true, true);

		CPInstance cpInstance4 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice4 = BigDecimal.valueOf(200);

		CPDefinition cpDefinition4 = cpInstance4.getCPDefinition();

		CProduct cProduct4 = cpDefinition4.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct4.getCProductId(),
			cpInstance4.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice4);

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance2.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance3.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance4.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, BigDecimal.ONE));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance1.getCPInstanceId(), BigDecimal.ONE, true,
					StringPool.BLANK));

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = cpInstancePrice1.add(cpInstancePrice2);

		expectedPrice = expectedPrice.add(cpInstancePromoPrice2);

		expectedPrice = expectedPrice.add(cpInstancePrice4);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testCalculatePriceDynamicOptionSKUWithQuantities()
		throws Exception {

		frutillaRule.scenario(
			"The price of a product with 3 option values selected is calculated"
		).given(
			"A product with 3 option values linked to SKUs with price type " +
				"dynamic"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned given the quantity"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice1 = BigDecimal.valueOf(35);

		CPDefinition cpDefinition1 = cpInstance1.getCPDefinition();

		CProduct cProduct1 = cpDefinition1.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct1.getCProductId(),
			cpInstance1.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice1);

		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice2 = BigDecimal.valueOf(100);

		CPDefinition cpDefinition2 = cpInstance2.getCPDefinition();

		CProduct cProduct2 = cpDefinition2.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct2.getCProductId(),
			cpInstance2.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice2);

		CPInstance cpInstance3 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice3 = BigDecimal.valueOf(150);

		CPDefinition cpDefinition3 = cpInstance3.getCPDefinition();

		CProduct cProduct3 = cpDefinition3.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct3.getCProductId(),
			cpInstance3.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice3);

		CPInstance cpInstance4 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice4 = BigDecimal.valueOf(200);

		CPDefinition cpDefinition4 = cpInstance4.getCPDefinition();

		CProduct cProduct4 = cpDefinition4.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct4.getCProductId(),
			cpInstance4.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice4);

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		BigDecimal quantity1 = BigDecimal.ONE;

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance2.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, quantity1));

		BigDecimal quantity2 = BigDecimal.valueOf(3);

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance3.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, quantity2));

		BigDecimal quantity3 = BigDecimal.TEN;

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance4.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, quantity3));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance1.getCPInstanceId(), BigDecimal.ONE, true,
					StringPool.BLANK));

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = cpInstancePrice1.add(
			cpInstancePrice2.multiply(quantity1));

		expectedPrice = expectedPrice.add(cpInstancePrice3.multiply(quantity2));

		expectedPrice = expectedPrice.add(cpInstancePrice4.multiply(quantity3));

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testCalculatePriceMixedOptionSKUWithDiscount()
		throws Exception {

		frutillaRule.scenario(
			"The price of a product with 3 option values selected is calculated"
		).given(
			"A product with 3 option values some linked to SKU and some with " +
				"price type dynamic"
		).and(
			"The product has a discount applied on it"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned given the quantity"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice1 = BigDecimal.valueOf(35);

		CPDefinition cpDefinition1 = cpInstance1.getCPDefinition();

		CProduct cProduct1 = cpDefinition1.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct1.getCProductId(),
			cpInstance1.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice1);

		double discountAmount = 10;

		CommerceDiscountTestUtil.addFixedCommerceDiscount(
			_group.getGroupId(), discountAmount,
			CommerceDiscountConstants.TARGET_PRODUCTS,
			cpDefinition1.getCPDefinitionId());

		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice2 = BigDecimal.valueOf(100);

		CPDefinition cpDefinition2 = cpInstance2.getCPDefinition();

		CProduct cProduct2 = cpDefinition2.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct2.getCProductId(),
			cpInstance2.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice2);

		CPInstance cpInstance3 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice3 = BigDecimal.valueOf(150);

		CPDefinition cpDefinition3 = cpInstance3.getCPDefinition();

		CProduct cProduct3 = cpDefinition3.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct3.getCProductId(),
			cpInstance3.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice3);

		BigDecimal cpInstancePromoPrice2 = BigDecimal.valueOf(100);

		CommercePriceList commercePromotion =
			CommercePriceListTestUtil.addPromotion(
				commerceCatalog.getGroupId(), 0.0);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition3.getCProductId(),
			cpInstance3.getCPInstanceUuid(),
			commercePromotion.getCommercePriceListId(), cpInstancePromoPrice2,
			false, null, null, null, null, true, true);

		CPInstance cpInstance4 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal optionValuePrice3 = BigDecimal.valueOf(20);

		BigDecimal cpInstancePrice4 = BigDecimal.valueOf(200);

		CPDefinition cpDefinition4 = cpInstance4.getCPDefinition();

		CProduct cProduct4 = cpDefinition4.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct4.getCProductId(),
			cpInstance4.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice4);

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance2.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance3.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance4.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), optionValuePrice3,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance1.getCPInstanceId(), BigDecimal.ONE, true,
					StringPool.BLANK));

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = cpInstancePrice1.subtract(
			BigDecimal.valueOf(discountAmount));

		expectedPrice = expectedPrice.add(cpInstancePromoPrice2);

		expectedPrice = expectedPrice.add(optionValuePrice3);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testCalculatePriceMixedOptionSKUWithOptionDiscount()
		throws Exception {

		frutillaRule.scenario(
			"The price of a product with 3 option values selected is calculated"
		).given(
			"A product with 3 option values some linked to SKU and some with " +
				"price type dynamic"
		).and(
			"Some linked SKUs have discount applied on"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned given the quantity"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice1 = BigDecimal.valueOf(35);

		CPDefinition cpDefinition1 = cpInstance1.getCPDefinition();

		CProduct cProduct1 = cpDefinition1.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct1.getCProductId(),
			cpInstance1.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice1);

		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice2 = BigDecimal.valueOf(100);

		CPDefinition cpDefinition2 = cpInstance2.getCPDefinition();

		CProduct cProduct2 = cpDefinition2.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct2.getCProductId(),
			cpInstance2.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice2);

		CPInstance cpInstance3 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice3 = BigDecimal.valueOf(150);

		CPDefinition cpDefinition3 = cpInstance3.getCPDefinition();

		CProduct cProduct3 = cpDefinition3.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct3.getCProductId(),
			cpInstance3.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice3);

		BigDecimal cpInstancePromoPrice2 = BigDecimal.valueOf(100);

		CommercePriceList commercePromotion =
			CommercePriceListTestUtil.addPromotion(
				commerceCatalog.getGroupId(), 0.0);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition3.getCProductId(),
			cpInstance3.getCPInstanceUuid(),
			commercePromotion.getCommercePriceListId(), cpInstancePromoPrice2,
			true, null, null, null, null, true, true);

		double discountAmount = 10;

		CommerceDiscountTestUtil.addFixedCommerceDiscount(
			_group.getGroupId(), discountAmount,
			CommerceDiscountConstants.TARGET_PRODUCTS,
			cpDefinition3.getCPDefinitionId());

		CPInstance cpInstance4 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal optionValuePrice3 = BigDecimal.valueOf(20);

		BigDecimal cpInstancePrice4 = BigDecimal.valueOf(200);

		CPDefinition cpDefinition4 = cpInstance4.getCPDefinition();

		CProduct cProduct4 = cpDefinition4.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct4.getCProductId(),
			cpInstance4.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice4);

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance2.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance3.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance4.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), optionValuePrice3,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance1.getCPInstanceId(), BigDecimal.ONE, true,
					StringPool.BLANK));

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = cpInstancePrice1.add(cpInstancePromoPrice2);

		expectedPrice = expectedPrice.subtract(
			BigDecimal.valueOf(discountAmount));

		expectedPrice = expectedPrice.add(optionValuePrice3);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testCalculatePriceMixedOptionSKUWithOptionLineDiscount()
		throws Exception {

		frutillaRule.scenario(
			"The price of a product with 3 option values selected is calculated"
		).given(
			"A product with 3 option values some linked to SKU and some with " +
				"price type dynamic"
		).and(
			"Some linked SKUs have discount defined at price entry level " +
				"applied on"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned given the quantity"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice1 = BigDecimal.valueOf(35);

		CPDefinition cpDefinition1 = cpInstance1.getCPDefinition();

		CProduct cProduct1 = cpDefinition1.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct1.getCProductId(),
			cpInstance1.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice1);

		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice2 = BigDecimal.valueOf(100);

		CPDefinition cpDefinition2 = cpInstance2.getCPDefinition();

		CProduct cProduct2 = cpDefinition2.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct2.getCProductId(),
			cpInstance2.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice2);

		CPInstance cpInstance3 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice3 = BigDecimal.valueOf(150);

		CPDefinition cpDefinition3 = cpInstance3.getCPDefinition();

		BigDecimal level1 = BigDecimal.valueOf(10);
		BigDecimal level2 = BigDecimal.valueOf(10);
		BigDecimal level3 = BigDecimal.valueOf(10);
		BigDecimal level4 = BigDecimal.valueOf(10);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition3.getCProductId(),
			cpInstance3.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice3, false,
			level1, level2, level3, level4, true, true);

		double discountAmount = 10;

		CommerceDiscountTestUtil.addFixedCommerceDiscount(
			_group.getGroupId(), discountAmount,
			CommerceDiscountConstants.TARGET_PRODUCTS,
			cpDefinition3.getCPDefinitionId());

		CPInstance cpInstance4 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal optionValuePrice3 = BigDecimal.valueOf(20);

		BigDecimal cpInstancePrice4 = BigDecimal.valueOf(200);

		CPDefinition cpDefinition4 = cpInstance4.getCPDefinition();

		CProduct cProduct3 = cpDefinition4.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct3.getCProductId(),
			cpInstance4.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice4);

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance2.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance3.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance4.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), optionValuePrice3,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance1.getCPInstanceId(), BigDecimal.ONE, true,
					StringPool.BLANK));

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal discountedPercentage1 = level1.divide(_HUNDRED);

		discountedPercentage1 = _ONE.subtract(discountedPercentage1);

		BigDecimal discountedPercentage2 = level2.divide(_HUNDRED);

		discountedPercentage2 = _ONE.subtract(discountedPercentage2);

		BigDecimal discountedPercentage3 = level3.divide(_HUNDRED);

		discountedPercentage3 = _ONE.subtract(discountedPercentage3);

		BigDecimal discountedPercentage4 = level4.divide(_HUNDRED);

		discountedPercentage4 = _ONE.subtract(discountedPercentage4);

		BigDecimal discountedCPInstancePrice2 = cpInstancePrice3.multiply(
			discountedPercentage1);

		discountedCPInstancePrice2 = discountedCPInstancePrice2.multiply(
			discountedPercentage2);

		discountedCPInstancePrice2 = discountedCPInstancePrice2.multiply(
			discountedPercentage3);

		discountedCPInstancePrice2 = discountedCPInstancePrice2.multiply(
			discountedPercentage4);

		BigDecimal expectedPrice = cpInstancePrice1.add(
			discountedCPInstancePrice2);

		expectedPrice = expectedPrice.add(optionValuePrice3);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testCalculatePriceMixedOptionSKUWithPriceModifier()
		throws Exception {

		frutillaRule.scenario(
			"The price of a product with 3 option values selected is calculated"
		).given(
			"A product with 3 option values some linked to SKU and some with " +
				"price type dynamic"
		).and(
			"Some linked SKUs have their price modifiers applied on"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned given the quantity"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList basePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice1 = BigDecimal.valueOf(35);

		CPDefinition cpDefinition1 = cpInstance1.getCPDefinition();

		CProduct cProduct1 = cpDefinition1.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct1.getCProductId(),
			cpInstance1.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice1);

		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition2 = cpInstance2.getCPDefinition();

		CProduct cProduct2 = cpDefinition2.getCProduct();

		BigDecimal cpInstanceBasePrice1 = BigDecimal.valueOf(100);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct2.getCProductId(),
			cpInstance2.getCPInstanceUuid(),
			basePriceList.getCommercePriceListId(), cpInstanceBasePrice1);

		BigDecimal modifierAmount = BigDecimal.valueOf(-10);

		CommercePriceModifier commercePriceModifier = _addCommercePriceModifier(
			commercePriceList.getGroupId(),
			CommercePriceModifierConstants.TARGET_PRODUCTS,
			commercePriceList.getCommercePriceListId(),
			CommercePriceModifierConstants.MODIFIER_TYPE_FIXED_AMOUNT,
			modifierAmount, true);

		_commercePriceModifierRelLocalService.addCommercePriceModifierRel(
			commercePriceModifier.getCommercePriceModifierId(),
			CPDefinition.class.getName(), cpDefinition2.getCPDefinitionId(),
			_serviceContext);

		CPInstance cpInstance3 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice2 = BigDecimal.valueOf(150);

		CPDefinition cpDefinition3 = cpInstance3.getCPDefinition();

		CProduct cProduct3 = cpDefinition3.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct3.getCProductId(),
			cpInstance3.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice2);

		BigDecimal cpInstancePromoPrice2 = BigDecimal.valueOf(100);

		CommercePriceList commercePromotion =
			CommercePriceListTestUtil.addPromotion(
				commerceCatalog.getGroupId(), 0.0);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition3.getCProductId(),
			cpInstance3.getCPInstanceUuid(),
			commercePromotion.getCommercePriceListId(), cpInstancePromoPrice2,
			false, null, null, null, null, true, true);

		CPInstance cpInstance4 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal optionValuePrice3 = BigDecimal.valueOf(20);

		BigDecimal cpInstancePrice3 = BigDecimal.valueOf(200);

		CPDefinition cpDefinition4 = cpInstance4.getCPDefinition();

		CProduct cProduct4 = cpDefinition4.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct4.getCProductId(),
			cpInstance4.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice3);

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance2.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance3.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance4.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), optionValuePrice3,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance1.getCPInstanceId(), BigDecimal.ONE, true,
					StringPool.BLANK));

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = cpInstanceBasePrice1.add(modifierAmount);

		expectedPrice = expectedPrice.add(cpInstancePrice1);

		expectedPrice = expectedPrice.add(optionValuePrice3);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testCalculatePriceMixedOptionSKUWithPromo() throws Exception {
		frutillaRule.scenario(
			"The price of a product with 3 option values selected is calculated"
		).given(
			"A product with 3 option values some linked to SKU and some with " +
				"price type dynamic"
		).and(
			"Some linked SKUs have a promo price"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned given the quantity"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice1 = BigDecimal.valueOf(35);

		CPDefinition cpDefinition1 = cpInstance1.getCPDefinition();

		CProduct cProduct1 = cpDefinition1.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct1.getCProductId(),
			cpInstance1.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice1);

		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice2 = BigDecimal.valueOf(100);

		CPDefinition cpDefinition2 = cpInstance2.getCPDefinition();

		CProduct cProduct2 = cpDefinition2.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct2.getCProductId(),
			cpInstance2.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice2);

		CPInstance cpInstance3 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice3 = BigDecimal.valueOf(150);

		CPDefinition cpDefinition3 = cpInstance3.getCPDefinition();

		CProduct cProduct3 = cpDefinition3.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct3.getCProductId(),
			cpInstance3.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice3);

		BigDecimal cpInstancePromoPrice2 = BigDecimal.valueOf(100);

		CommercePriceList commercePromotion =
			CommercePriceListTestUtil.addPromotion(
				commerceCatalog.getGroupId(), 0.0);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition3.getCProductId(),
			cpInstance3.getCPInstanceUuid(),
			commercePromotion.getCommercePriceListId(), cpInstancePromoPrice2,
			false, null, null, null, null, true, true);

		CPInstance cpInstance4 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice4 = BigDecimal.valueOf(200);

		BigDecimal optionValuePrice3 = BigDecimal.valueOf(20);

		CPDefinition cpDefinition4 = cpInstance4.getCPDefinition();

		CProduct cProduct4 = cpDefinition4.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct4.getCProductId(),
			cpInstance4.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice4);

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance2.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance3.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance4.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), optionValuePrice3,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance1.getCPInstanceId(), BigDecimal.ONE, true,
					StringPool.BLANK));

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = cpInstancePrice1.add(cpInstancePromoPrice2);

		expectedPrice = expectedPrice.add(optionValuePrice3);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testCalculatePriceMixedOptionSKUWithTierPrice()
		throws Exception {

		frutillaRule.scenario(
			"The price of a product with 3 option values selected is calculated"
		).given(
			"A product with 3 option values linked to SKUs with price type " +
				"static"
		).and(
			"some linked SKUs have tier price entries"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned given the quantity"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice1 = BigDecimal.valueOf(35);

		CPDefinition cpDefinition1 = cpInstance1.getCPDefinition();

		CProduct cProduct1 = cpDefinition1.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct1.getCProductId(),
			cpInstance1.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice1);

		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice2 = BigDecimal.valueOf(100);

		CPDefinition cpDefinition2 = cpInstance2.getCPDefinition();

		CProduct cProduct2 = cpDefinition2.getCProduct();

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cProduct2.getCProductId(),
				cpInstance2.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), cpInstancePrice2);

		BigDecimal price5 = BigDecimal.valueOf(40);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), StringPool.BLANK,
			price5, BigDecimal.valueOf(5), false, false, null, null, null, null,
			true, true);

		BigDecimal price10 = BigDecimal.valueOf(30);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), StringPool.BLANK,
			price10, BigDecimal.TEN, false, false, null, null, null, null, true,
			true);

		CPInstance cpInstance3 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPInstance cpInstance4 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance2.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC,
				BigDecimal.valueOf(7)));

		BigDecimal optionValuePrice2 = BigDecimal.valueOf(15);

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance3.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), optionValuePrice2,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		BigDecimal optionValuePrice3 = BigDecimal.valueOf(20);

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance4.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), optionValuePrice3,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance1.getCPInstanceId(), BigDecimal.ONE, true,
					StringPool.BLANK));

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal tier1OptionValuePrice1 = cpInstancePrice2.multiply(
			BigDecimal.valueOf(4));
		BigDecimal tier2OptionValuePrice1 = price5.multiply(
			BigDecimal.valueOf(3));

		BigDecimal expectedPrice = cpInstancePrice1.add(tier1OptionValuePrice1);

		expectedPrice = expectedPrice.add(tier2OptionValuePrice1);

		expectedPrice = expectedPrice.add(optionValuePrice2);

		expectedPrice = expectedPrice.add(optionValuePrice3);

		Assert.assertEquals(
			expectedPrice,
			finalPrice.setScale(expectedPrice.scale(), RoundingMode.HALF_UP));
	}

	@Test
	public void testCalculatePriceStaticOptionNoSKU() throws Exception {
		frutillaRule.scenario(
			"The price of a product with 3 option values selected is calculated"
		).given(
			"A product with 3 option values not linked to SKUs with price " +
				"type static"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned given the quantity"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice = BigDecimal.valueOf(35);

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CProduct cProduct = cpDefinition.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice);

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		BigDecimal optionValuePrice1 = BigDecimal.valueOf(10);

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				optionValuePrice1, CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
				BigDecimal.ONE));

		BigDecimal optionValuePrice2 = BigDecimal.valueOf(15);

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				optionValuePrice2, CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
				BigDecimal.ONE));

		BigDecimal optionValuePrice3 = BigDecimal.valueOf(20);

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				optionValuePrice3, CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
				BigDecimal.ONE));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance.getCPInstanceId(), BigDecimal.ONE, true,
					StringPool.BLANK));

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = cpInstancePrice.add(optionValuePrice1);

		expectedPrice = expectedPrice.add(optionValuePrice2);

		expectedPrice = expectedPrice.add(optionValuePrice3);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testCalculatePriceStaticOptionNoSKUWithQuantities()
		throws Exception {

		frutillaRule.scenario(
			"The price of a product with 3 option values selected is calculated"
		).given(
			"A product with 3 option values not linked to SKUs with price " +
				"type static"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned given the quantity"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice = BigDecimal.valueOf(35);

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CProduct cProduct = cpDefinition.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice);

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		BigDecimal optionValuePrice1 = BigDecimal.valueOf(10);

		BigDecimal quantity1 = BigDecimal.ONE;

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				optionValuePrice1, CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
				quantity1));

		BigDecimal optionValuePrice2 = BigDecimal.valueOf(15);

		BigDecimal quantity2 = BigDecimal.valueOf(11);

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				optionValuePrice2, CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
				quantity2));

		BigDecimal optionValuePrice3 = BigDecimal.valueOf(20);

		BigDecimal quantity3 = BigDecimal.TEN;

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				optionValuePrice3, CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
				quantity3));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance.getCPInstanceId(), BigDecimal.ONE, true,
					StringPool.BLANK));

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = cpInstancePrice.add(optionValuePrice1);

		expectedPrice = expectedPrice.add(optionValuePrice2);

		expectedPrice = expectedPrice.add(optionValuePrice3);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testCalculatePriceStaticOptionSKU() throws Exception {
		frutillaRule.scenario(
			"The price of a product with 3 option values selected is calculated"
		).given(
			"A product with 3 option values linked to SKUs with price type " +
				"static"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned given the quantity"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice = BigDecimal.valueOf(35);

		CPDefinition cpDefinition = cpInstance1.getCPDefinition();

		CProduct cProduct = cpDefinition.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct.getCProductId(),
			cpInstance1.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice);

		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPInstance cpInstance3 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPInstance cpInstance4 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		BigDecimal optionValuePrice1 = BigDecimal.valueOf(10);

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance2.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), optionValuePrice1,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		BigDecimal optionValuePrice2 = BigDecimal.valueOf(15);

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance3.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), optionValuePrice2,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		BigDecimal optionValuePrice3 = BigDecimal.valueOf(20);

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance4.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), optionValuePrice3,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance1.getCPInstanceId(), BigDecimal.ONE, true,
					StringPool.BLANK));

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = cpInstancePrice.add(optionValuePrice1);

		expectedPrice = expectedPrice.add(optionValuePrice2);

		expectedPrice = expectedPrice.add(optionValuePrice3);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testCalculatePriceStaticOptionSKUBundle() throws Exception {
		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CProduct cProduct = cpDefinition.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), BigDecimal.ZERO);

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), BigDecimal.valueOf(9),
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpInstance.getCPInstanceId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), BigDecimal.valueOf(4),
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, BigDecimal.ONE));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance.getCPInstanceId(), BigDecimal.ONE, true,
					StringPool.BLANK));

		CommerceMoney unitPrice = commerceProductPrice.getUnitPrice();
		CommerceMoney finalPrice = commerceProductPrice.getFinalPrice();

		Assert.assertEquals(
			BigDecimal.valueOf(13),
			BigDecimalUtil.stripTrailingZeros(unitPrice.getPrice()));
		Assert.assertEquals(
			BigDecimal.valueOf(13),
			BigDecimalUtil.stripTrailingZeros(finalPrice.getPrice()));

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance.getCPInstanceId(), BigDecimal.valueOf(2), true,
					StringPool.BLANK));

		unitPrice = commerceProductPrice.getUnitPrice();
		finalPrice = commerceProductPrice.getFinalPrice();

		Assert.assertEquals(
			BigDecimal.valueOf(13),
			BigDecimalUtil.stripTrailingZeros(unitPrice.getPrice()));
		Assert.assertEquals(
			BigDecimal.valueOf(26),
			BigDecimalUtil.stripTrailingZeros(finalPrice.getPrice()));
	}

	@Test
	public void testCalculatePriceStaticOptionWithSKUWithQuantities()
		throws Exception {

		frutillaRule.scenario(
			"The price of a product with 3 option values selected is calculated"
		).given(
			"A product with 3 option values with price type static"
		).and(
			"an option value linked to a cpInstance1"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned and the quantity of the linked " +
				"option is taken into account"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice1 = BigDecimal.valueOf(35);

		CPDefinition cpDefinition1 = cpInstance1.getCPDefinition();

		CProduct cProduct1 = cpDefinition1.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct1.getCProductId(),
			cpInstance1.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice1);

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		BigDecimal optionValuePrice1 = BigDecimal.valueOf(10);

		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice2 = BigDecimal.valueOf(100);

		CPDefinition cpDefinition2 = cpInstance2.getCPDefinition();

		CProduct cProduct2 = cpDefinition2.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct2.getCProductId(),
			cpInstance2.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), cpInstancePrice2);

		BigDecimal quantity1 = BigDecimal.TEN;

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				cpDefinition2.getCPDefinitionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				optionValuePrice1, CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
				quantity1));

		BigDecimal optionValuePrice2 = BigDecimal.valueOf(15);

		BigDecimal quantity2 = BigDecimal.valueOf(11);

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				optionValuePrice2, CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
				quantity2));

		BigDecimal optionValuePrice3 = BigDecimal.valueOf(20);

		BigDecimal quantity3 = BigDecimal.TEN;

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				optionValuePrice3, CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
				quantity3));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, commerceOptionValues,
					cpInstance1.getCPInstanceId(), BigDecimal.ONE, true,
					StringPool.BLANK));

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = cpInstancePrice1.add(
			optionValuePrice1.multiply(quantity1));

		expectedPrice = expectedPrice.add(optionValuePrice2);

		expectedPrice = expectedPrice.add(optionValuePrice3);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testCalculatePriceWithParentPriceEntry() throws Exception {
		frutillaRule.scenario(
			"The price of a product is calculated"
		).given(
			"A product with a price entry in a parent price list"
		).when(
			"The price of the product is calculated"
		).then(
			"The correct price is returned given the quantity"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList parentPriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		BigDecimal cpInstancePrice = BigDecimal.valueOf(35);

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CProduct cProduct = cpDefinition.getCProduct();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cProduct.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			parentPriceList.getCommercePriceListId(), cpInstancePrice);

		AccountEntry accountEntry1 =
			CommerceAccountTestUtil.getPersonAccountEntry(_user.getUserId());

		CommercePriceList childPriceList =
			CommercePriceListTestUtil.addAccountPriceList(
				commerceCatalog.getGroupId(), accountEntry1.getAccountEntryId(),
				CommercePriceListConstants.TYPE_PRICE_LIST);

		childPriceList.setParentCommercePriceListId(
			parentPriceList.getCommercePriceListId());

		_commercePriceListLocalService.updateCommercePriceList(childPriceList);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					commerceContext, null, cpInstance.getCPInstanceId(),
					BigDecimal.ONE, true, StringPool.BLANK));

		Assert.assertEquals(
			parentPriceList.getCommercePriceListId(),
			commerceProductPrice.getCommercePriceListId());

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		Assert.assertEquals(
			cpInstancePrice,
			BigDecimalUtil.stripTrailingZeros(
				finalPriceCommerceMoney.getPrice()));
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private CommercePriceModifier _addCommercePriceModifier(
			long groupId, String target, long commercePriceListId, String type,
			BigDecimal amount, boolean neverExpire)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		Calendar calendar = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		return _commercePriceModifierLocalService.addCommercePriceModifier(
			groupId, RandomTestUtil.randomString(), target, commercePriceListId,
			type, amount, 0.0, true, calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR),
			calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
			calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
			calendar.get(Calendar.MINUTE), neverExpire, serviceContext);
	}

	private CommerceProductPriceRequest _createCommerceProductPriceRequest(
		CommerceContext commerceContext,
		List<CommerceOptionValue> commerceOptionValues, long cpInstanceId,
		BigDecimal quantity, boolean secure, String unitOfMeasureKey) {

		CommerceProductPriceRequest commerceProductPriceRequest =
			new CommerceProductPriceRequest();

		commerceProductPriceRequest.setCommerceContext(commerceContext);

		if (commerceOptionValues != null) {
			commerceProductPriceRequest.setCommerceOptionValues(
				commerceOptionValues);
		}

		commerceProductPriceRequest.setCpInstanceId(cpInstanceId);
		commerceProductPriceRequest.setQuantity(quantity);
		commerceProductPriceRequest.setSecure(secure);
		commerceProductPriceRequest.setUnitOfMeasureKey(unitOfMeasureKey);

		return commerceProductPriceRequest;
	}

	private static final BigDecimal _HUNDRED = BigDecimal.valueOf(100);

	private static final BigDecimal _ONE = BigDecimal.ONE;

	private static User _user;

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommercePriceModifierLocalService
		_commercePriceModifierLocalService;

	@Inject
	private CommercePriceModifierRelLocalService
		_commercePriceModifierRelLocalService;

	@Inject
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Inject
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	private Group _group;
	private ServiceContext _serviceContext;

}