/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalServiceUtil;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.test.util.CommerceDiscountTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListAccountRelLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.test.util.CommercePriceEntryTestUtil;
import com.liferay.commerce.price.list.test.util.CommercePriceListTestUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.test.util.CommerceAccountGroupTestUtil;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTaxTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
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
public class CommerceDiscountTargetGrossV2Test {

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

		_commerceChannel.setPriceDisplayType("tax-included");
		_commerceChannel.setDiscountsTargetNetPrice(false);

		_commerceChannel = _commerceChannelLocalService.updateCommerceChannel(
			_commerceChannel);

		_commerceTaxMethod = CommerceTaxTestUtil.addCommerceByAddressTaxMethod(
			_user.getUserId(), _commerceChannel.getGroupId(), true);
	}

	@After
	public void tearDown() throws Exception {
		for (CommerceOrder commerceOrder : _commerceOrders) {
			_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
		}

		_commercePriceListLocalService.deleteCommercePriceLists(
			_group.getCompanyId());
	}

	@Test
	public void testAccountGroupDiscount() throws Exception {
		frutillaRule.scenario(
			"When a discount has an account groups associated to it, it will " +
				"be applied only if the criteria are fulfilled"
		).given(
			"A product with a base price"
		).and(
			"A discount with a account groups associated. The account groups " +
				"is associated to a specific user"
		).when(
			"I try to get the final price of the product and I am the user " +
				"associated to the account groups"
		).then(
			"The final price will be calculated taking into consideration " +
				"the discount"
		);

		long cpTaxCategoryId = CommerceTaxTestUtil.addTaxCategoryId(
			_user.getGroupId());

		double rate = Double.valueOf(RandomTestUtil.randomInt(0, 40));

		BigDecimal taxRate = BigDecimal.valueOf(rate);

		CommerceTaxTestUtil.setCommerceMethodTaxRate(
			_user.getUserId(), _commerceChannel.getGroupId(), cpTaxCategoryId,
			_commerceTaxMethod.getCommerceTaxMethodId(), rate);

		AccountGroup accountGroup =
			CommerceAccountGroupTestUtil.addAccountEntryToAccountGroup(
				_group.getGroupId(), _accountEntry);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(35));

		CommerceDiscount commerceDiscount =
			CommerceDiscountTestUtil.addFixedCommerceDiscount(
				_group.getGroupId(), 10,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				cpDefinition.getCPDefinitionId());

		CommerceDiscountTestUtil.addDiscountCommerceAccountGroupRel(
			commerceDiscount, accountGroup);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceDiscountValue discountValue =
			commerceProductPrice.getDiscountValue();

		CommerceMoney discountAmountCommerceMoney =
			discountValue.getDiscountAmount();

		BigDecimal expectedDiscountLevel = commerceDiscount.getLevel1();

		BigDecimal discountAmountWithoutTaxAmount =
			CommerceTaxTestUtil.getPriceWithoutTaxAmount(
				expectedDiscountLevel, taxRate,
				RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal discountAmountPrice = discountAmountCommerceMoney.getPrice();

		Assert.assertEquals(
			discountAmountWithoutTaxAmount.stripTrailingZeros(),
			discountAmountPrice.stripTrailingZeros());

		BigDecimal expectedPrice = CommerceTaxTestUtil.getPriceWithTaxAmount(
			commercePriceEntry.getPrice(), taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		expectedPrice = expectedPrice.subtract(commerceDiscount.getLevel1());

		expectedPrice = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal actualPrice = finalPriceCommerceMoney.getPrice();

		_assertPrices(
			expectedPrice.stripTrailingZeros(),
			actualPrice.stripTrailingZeros());
	}

	@Test
	public void testCommerceFixedDiscount() throws Exception {
		frutillaRule.scenario(
			"Apply a fixed price discount"
		).given(
			"A product with a base price"
		).and(
			"A discount with fixed value"
		).when(
			"I try to get the final price of the product"
		).then(
			"The final price will be calculated taking into consideration " +
				"the discount"
		);

		long cpTaxCategoryId = CommerceTaxTestUtil.addTaxCategoryId(
			_user.getGroupId());

		double rate = Double.valueOf(RandomTestUtil.randomInt(0, 40));

		BigDecimal taxRate = BigDecimal.valueOf(rate);

		CommerceTaxTestUtil.setCommerceMethodTaxRate(
			_user.getUserId(), _commerceChannel.getGroupId(), cpTaxCategoryId,
			_commerceTaxMethod.getCommerceTaxMethodId(), rate);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(25));

		CommerceDiscount commerceDiscount =
			CommerceDiscountTestUtil.addFixedCommerceDiscount(
				_group.getGroupId(), 10,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				cpDefinition.getCPDefinitionId());

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		BigDecimal expectedPrice = CommerceTaxTestUtil.getPriceWithTaxAmount(
			commercePriceEntry.getPrice(), taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		expectedPrice = expectedPrice.subtract(commerceDiscount.getLevel1());

		expectedPrice = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal actualPrice = BigDecimal.ZERO;

		if (commerceProductPrice != null) {
			CommerceMoney finalPriceCommerceMoney =
				commerceProductPrice.getFinalPrice();

			actualPrice = finalPriceCommerceMoney.getPrice();
		}

		_assertPrices(
			expectedPrice.stripTrailingZeros(),
			actualPrice.stripTrailingZeros());
	}

	@Test
	public void testCommerceFixedDiscounts() throws Exception {
		frutillaRule.scenario(
			"When few fixed discount are available check how they are applied"
		).given(
			"Some products with base prices"
		).and(
			"Some discounts linked to single product or to categories"
		).when(
			"I try to get the final prices of the products"
		).then(
			"The final prices are calculated based on the available " +
				"discounts. If more discount per type are available then the " +
					"best matching is applied"
		);

		long cpTaxCategoryId = CommerceTaxTestUtil.addTaxCategoryId(
			_user.getGroupId());

		double rate = Double.valueOf(RandomTestUtil.randomInt(0, 40));

		BigDecimal taxRate = BigDecimal.valueOf(rate);

		CommerceTaxTestUtil.setCommerceMethodTaxRate(
			_user.getUserId(), _commerceChannel.getGroupId(), cpTaxCategoryId,
			_commerceTaxMethod.getCommerceTaxMethodId(), rate);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());
		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());
		CPInstance cpInstance3 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());
		CPInstance cpInstance4 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());
		CPInstance cpInstance5 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition1 = cpInstance1.getCPDefinition();

		cpDefinition1.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition1.setTaxExempt(false);

		cpDefinition1 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition1);

		CPDefinition cpDefinition2 = cpInstance2.getCPDefinition();

		cpDefinition2.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition2.setTaxExempt(false);

		cpDefinition2 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition2);

		CPDefinition cpDefinition3 = cpInstance3.getCPDefinition();

		cpDefinition3.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition3.setTaxExempt(false);

		cpDefinition3 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition3);

		CPDefinition cpDefinition4 = cpInstance4.getCPDefinition();

		cpDefinition4.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition4.setTaxExempt(false);

		cpDefinition4 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition4);

		CPDefinition cpDefinition5 = cpInstance5.getCPDefinition();

		cpDefinition5.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition5.setTaxExempt(false);

		cpDefinition5 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition5);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		CommercePriceEntry commercePriceEntry1 =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition1.getCProductId(),
				cpInstance1.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(111));

		CommercePriceEntry commercePriceEntry2 =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition2.getCProductId(),
				cpInstance2.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(222));

		CommercePriceEntry commercePriceEntry3 =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition3.getCProductId(),
				cpInstance3.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(333));

		CommercePriceEntry commercePriceEntry4 =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition4.getCProductId(),
				cpInstance4.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(444));

		CommercePriceEntry commercePriceEntry5 =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition5.getCProductId(),
				cpInstance5.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(555));

		long[] category1CPDefinitionIds = {
			cpDefinition1.getCPDefinitionId(),
			cpDefinition2.getCPDefinitionId(), cpDefinition3.getCPDefinitionId()
		};

		long[] category2CPDefinitionIds = {
			cpDefinition3.getCPDefinitionId(),
			cpDefinition4.getCPDefinitionId(), cpDefinition5.getCPDefinitionId()
		};

		AssetCategory assetCategory1 = CPTestUtil.addCategoryToCPDefinitions(
			_group.getGroupId(), category1CPDefinitionIds);

		AssetCategory assetCategory2 = CPTestUtil.addCategoryToCPDefinitions(
			_group.getGroupId(), category2CPDefinitionIds);

		CommerceDiscountTestUtil.addFixedCommerceDiscount(
			_group.getGroupId(), 11, CommerceDiscountConstants.TARGET_PRODUCTS,
			cpDefinition1.getCPDefinitionId());

		CommerceDiscount commerceDiscount2 =
			CommerceDiscountTestUtil.addFixedCommerceDiscount(
				_group.getGroupId(), 22,
				CommerceDiscountConstants.TARGET_CATEGORIES,
				assetCategory1.getCategoryId());

		CommerceDiscount commerceDiscount3 =
			CommerceDiscountTestUtil.addFixedCommerceDiscount(
				_group.getGroupId(), 33,
				CommerceDiscountConstants.TARGET_CATEGORIES,
				assetCategory2.getCategoryId());

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice1 =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance1.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceProductPrice commerceProductPrice2 =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance2.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceProductPrice commerceProductPrice3 =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance3.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceProductPrice commerceProductPrice4 =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance4.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceProductPrice commerceProductPrice5 =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance5.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		BigDecimal price1 = commercePriceEntry1.getPrice();

		BigDecimal expectedPrice1 = CommerceTaxTestUtil.getPriceWithTaxAmount(
			price1, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		expectedPrice1 = expectedPrice1.subtract(commerceDiscount2.getLevel1());

		expectedPrice1 = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice1, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal price2 = commercePriceEntry2.getPrice();

		BigDecimal expectedPrice2 = CommerceTaxTestUtil.getPriceWithTaxAmount(
			price2, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		expectedPrice2 = expectedPrice2.subtract(commerceDiscount2.getLevel1());

		expectedPrice2 = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice2, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal price3 = commercePriceEntry3.getPrice();

		BigDecimal expectedPrice3 = CommerceTaxTestUtil.getPriceWithTaxAmount(
			price3, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		expectedPrice3 = expectedPrice3.subtract(commerceDiscount3.getLevel1());

		expectedPrice3 = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice3, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal price4 = commercePriceEntry4.getPrice();

		BigDecimal expectedPrice4 = CommerceTaxTestUtil.getPriceWithTaxAmount(
			price4, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		expectedPrice4 = expectedPrice4.subtract(commerceDiscount3.getLevel1());

		expectedPrice4 = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice4, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal price5 = commercePriceEntry5.getPrice();

		BigDecimal expectedPrice5 = CommerceTaxTestUtil.getPriceWithTaxAmount(
			price5, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		expectedPrice5 = expectedPrice5.subtract(commerceDiscount3.getLevel1());

		expectedPrice5 = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice5, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal actualPrice1 = BigDecimal.ZERO;
		BigDecimal actualPrice2 = BigDecimal.ZERO;
		BigDecimal actualPrice3 = BigDecimal.ZERO;
		BigDecimal actualPrice4 = BigDecimal.ZERO;
		BigDecimal actualPrice5 = BigDecimal.ZERO;

		if (commerceProductPrice1 != null) {
			CommerceMoney finalPriceCommerceMoney1 =
				commerceProductPrice1.getFinalPrice();

			actualPrice1 = finalPriceCommerceMoney1.getPrice();
		}

		if (commerceProductPrice2 != null) {
			CommerceMoney finalPriceCommerceMoney2 =
				commerceProductPrice2.getFinalPrice();

			actualPrice2 = finalPriceCommerceMoney2.getPrice();
		}

		if (commerceProductPrice3 != null) {
			CommerceMoney finalPriceCommerceMoney3 =
				commerceProductPrice3.getFinalPrice();

			actualPrice3 = finalPriceCommerceMoney3.getPrice();
		}

		if (commerceProductPrice4 != null) {
			CommerceMoney finalPriceCommerceMoney4 =
				commerceProductPrice4.getFinalPrice();

			actualPrice4 = finalPriceCommerceMoney4.getPrice();
		}

		if (commerceProductPrice5 != null) {
			CommerceMoney finalPriceCommerceMoney5 =
				commerceProductPrice5.getFinalPrice();

			actualPrice5 = finalPriceCommerceMoney5.getPrice();
		}

		_assertPrices(
			expectedPrice1.stripTrailingZeros(),
			actualPrice1.stripTrailingZeros());
		_assertPrices(
			expectedPrice2.stripTrailingZeros(),
			actualPrice2.stripTrailingZeros());
		_assertPrices(
			expectedPrice3.stripTrailingZeros(),
			actualPrice3.stripTrailingZeros());
		_assertPrices(
			expectedPrice4.stripTrailingZeros(),
			actualPrice4.stripTrailingZeros());
		_assertPrices(
			expectedPrice5.stripTrailingZeros(),
			actualPrice5.stripTrailingZeros());
	}

	@Test
	public void testCommercePercentageDiscounts() throws Exception {
		frutillaRule.scenario(
			"When few percentage discount are available check how they are " +
				"applied"
		).given(
			"Some products with base prices"
		).and(
			"Some discounts linked to single product or to categories"
		).when(
			"I try to get the final prices of the products"
		).then(
			"The final prices are calculated based on the available " +
				"discounts. If more discount per type are available then the " +
					"best matching is applied"
		);

		long cpTaxCategoryId = CommerceTaxTestUtil.addTaxCategoryId(
			_user.getGroupId());

		double rate = Double.valueOf(RandomTestUtil.randomInt(0, 40));

		BigDecimal taxRate = BigDecimal.valueOf(rate);

		CommerceTaxTestUtil.setCommerceMethodTaxRate(
			_user.getUserId(), _commerceChannel.getGroupId(), cpTaxCategoryId,
			_commerceTaxMethod.getCommerceTaxMethodId(), rate);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());
		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());
		CPInstance cpInstance3 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());
		CPInstance cpInstance4 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());
		CPInstance cpInstance5 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition1 = cpInstance1.getCPDefinition();

		cpDefinition1.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition1.setTaxExempt(false);

		cpDefinition1 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition1);

		CPDefinition cpDefinition2 = cpInstance2.getCPDefinition();

		cpDefinition2.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition2.setTaxExempt(false);

		cpDefinition2 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition2);

		CPDefinition cpDefinition3 = cpInstance3.getCPDefinition();

		cpDefinition3.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition3.setTaxExempt(false);

		cpDefinition3 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition3);

		CPDefinition cpDefinition4 = cpInstance4.getCPDefinition();

		cpDefinition4.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition4.setTaxExempt(false);

		cpDefinition4 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition4);

		CPDefinition cpDefinition5 = cpInstance5.getCPDefinition();

		cpDefinition5.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition5.setTaxExempt(false);

		cpDefinition5 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition5);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		CommercePriceEntry commercePriceEntry1 =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition1.getCProductId(),
				cpInstance1.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(125));

		CommercePriceEntry commercePriceEntry2 =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition2.getCProductId(),
				cpInstance2.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(160));

		CommercePriceEntry commercePriceEntry3 =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition3.getCProductId(),
				cpInstance3.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(300));

		CommercePriceEntry commercePriceEntry4 =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition4.getCProductId(),
				cpInstance4.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(109));

		CommercePriceEntry commercePriceEntry5 =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition5.getCProductId(),
				cpInstance5.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(405));

		long[] category1CPDefinitionIds = {
			cpDefinition2.getCPDefinitionId(), cpDefinition3.getCPDefinitionId()
		};

		long[] category2CPDefinitionIds = {
			cpDefinition3.getCPDefinitionId(),
			cpDefinition4.getCPDefinitionId(), cpDefinition5.getCPDefinitionId()
		};

		AssetCategory assetCategory1 = CPTestUtil.addCategoryToCPDefinitions(
			_group.getGroupId(), category1CPDefinitionIds);

		AssetCategory assetCategory2 = CPTestUtil.addCategoryToCPDefinitions(
			_group.getGroupId(), category2CPDefinitionIds);

		CommerceDiscount commerceDiscount11 =
			CommerceDiscountTestUtil.addPercentageCommerceDiscount(
				_group.getGroupId(), BigDecimal.valueOf(10),
				CommerceDiscountConstants.LEVEL_L1,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				cpDefinition1.getCPDefinitionId());

		CommerceDiscount commerceDiscount12 =
			CommerceDiscountTestUtil.addPercentageCommerceDiscount(
				_group.getGroupId(), BigDecimal.valueOf(15),
				CommerceDiscountConstants.LEVEL_L2,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				cpDefinition1.getCPDefinitionId());

		CommerceDiscount commerceDiscount13 =
			CommerceDiscountTestUtil.addPercentageCommerceDiscount(
				_group.getGroupId(), BigDecimal.valueOf(20),
				CommerceDiscountConstants.LEVEL_L3,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				cpDefinition1.getCPDefinitionId());

		CommerceDiscountTestUtil.addPercentageCommerceDiscount(
			_group.getGroupId(), BigDecimal.valueOf(0),
			CommerceDiscountConstants.LEVEL_L4,
			CommerceDiscountConstants.TARGET_PRODUCTS,
			cpDefinition1.getCPDefinitionId());

		CommerceDiscount commerceDiscount21 =
			CommerceDiscountTestUtil.addPercentageCommerceDiscount(
				_group.getGroupId(), BigDecimal.valueOf(30),
				CommerceDiscountConstants.LEVEL_L1,
				CommerceDiscountConstants.TARGET_CATEGORIES,
				assetCategory1.getCategoryId());

		CommerceDiscountTestUtil.addPercentageCommerceDiscount(
			_group.getGroupId(), BigDecimal.valueOf(0),
			CommerceDiscountConstants.LEVEL_L2,
			CommerceDiscountConstants.TARGET_CATEGORIES,
			assetCategory1.getCategoryId());

		CommerceDiscountTestUtil.addPercentageCommerceDiscount(
			_group.getGroupId(), BigDecimal.valueOf(0),
			CommerceDiscountConstants.LEVEL_L3,
			CommerceDiscountConstants.TARGET_CATEGORIES,
			assetCategory1.getCategoryId());

		CommerceDiscountTestUtil.addPercentageCommerceDiscount(
			_group.getGroupId(), BigDecimal.valueOf(0),
			CommerceDiscountConstants.LEVEL_L4,
			CommerceDiscountConstants.TARGET_CATEGORIES,
			assetCategory1.getCategoryId());

		CommerceDiscount commerceDiscount31 =
			CommerceDiscountTestUtil.addPercentageCommerceDiscount(
				_group.getGroupId(), BigDecimal.valueOf(50),
				CommerceDiscountConstants.LEVEL_L1,
				CommerceDiscountConstants.TARGET_CATEGORIES,
				assetCategory2.getCategoryId());

		CommerceDiscountTestUtil.addPercentageCommerceDiscount(
			_group.getGroupId(), BigDecimal.valueOf(0),
			CommerceDiscountConstants.LEVEL_L2,
			CommerceDiscountConstants.TARGET_CATEGORIES,
			assetCategory2.getCategoryId());

		CommerceDiscountTestUtil.addPercentageCommerceDiscount(
			_group.getGroupId(), BigDecimal.valueOf(0),
			CommerceDiscountConstants.LEVEL_L3,
			CommerceDiscountConstants.TARGET_CATEGORIES,
			assetCategory2.getCategoryId());

		CommerceDiscountTestUtil.addPercentageCommerceDiscount(
			_group.getGroupId(), BigDecimal.valueOf(0),
			CommerceDiscountConstants.LEVEL_L4,
			CommerceDiscountConstants.TARGET_CATEGORIES,
			assetCategory2.getCategoryId());

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice1 =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance1.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceProductPrice commerceProductPrice2 =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance2.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceProductPrice commerceProductPrice3 =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance3.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceProductPrice commerceProductPrice4 =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance4.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceProductPrice commerceProductPrice5 =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance5.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		BigDecimal price1 = CommerceTaxTestUtil.getPriceWithTaxAmount(
			commercePriceEntry1.getPrice(), taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal expectedPrice1_level1 = _subtractPercentage(
			price1, commerceDiscount11.getLevel1());

		BigDecimal expectedPrice1_level2 = _subtractPercentage(
			expectedPrice1_level1, commerceDiscount12.getLevel2());

		BigDecimal expectedPrice1_level3 = _subtractPercentage(
			expectedPrice1_level2, commerceDiscount13.getLevel3());

		expectedPrice1_level3 = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice1_level3, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal price2 = CommerceTaxTestUtil.getPriceWithTaxAmount(
			commercePriceEntry2.getPrice(), taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal expectedPrice2 = _subtractPercentage(
			price2, commerceDiscount21.getLevel1());

		expectedPrice2 = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice2, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal price3 = CommerceTaxTestUtil.getPriceWithTaxAmount(
			commercePriceEntry3.getPrice(), taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal expectedPrice3 = _subtractPercentage(
			price3, commerceDiscount31.getLevel1());

		expectedPrice3 = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice3, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal price4 = CommerceTaxTestUtil.getPriceWithTaxAmount(
			commercePriceEntry4.getPrice(), taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal expectedPrice4 = _subtractPercentage(
			price4, commerceDiscount31.getLevel1());

		expectedPrice4 = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice4, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal price5 = CommerceTaxTestUtil.getPriceWithTaxAmount(
			commercePriceEntry5.getPrice(), taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal expectedPrice5 = _subtractPercentage(
			price5, commerceDiscount31.getLevel1());

		expectedPrice5 = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice5, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal actualPrice1 = BigDecimal.ZERO;
		BigDecimal actualPrice2 = BigDecimal.ZERO;
		BigDecimal actualPrice3 = BigDecimal.ZERO;
		BigDecimal actualPrice4 = BigDecimal.ZERO;
		BigDecimal actualPrice5 = BigDecimal.ZERO;

		if (commerceProductPrice1 != null) {
			CommerceMoney finalPriceCommerceMoney1 =
				commerceProductPrice1.getFinalPrice();

			actualPrice1 = finalPriceCommerceMoney1.getPrice();
		}

		if (commerceProductPrice2 != null) {
			CommerceMoney finalPriceCommerceMoney2 =
				commerceProductPrice2.getFinalPrice();

			actualPrice2 = finalPriceCommerceMoney2.getPrice();
		}

		if (commerceProductPrice3 != null) {
			CommerceMoney finalPriceCommerceMoney3 =
				commerceProductPrice3.getFinalPrice();

			actualPrice3 = finalPriceCommerceMoney3.getPrice();
		}

		if (commerceProductPrice4 != null) {
			CommerceMoney finalPriceCommerceMoney4 =
				commerceProductPrice4.getFinalPrice();

			actualPrice4 = finalPriceCommerceMoney4.getPrice();
		}

		if (commerceProductPrice5 != null) {
			CommerceMoney finalPriceCommerceMoney5 =
				commerceProductPrice5.getFinalPrice();

			actualPrice5 = finalPriceCommerceMoney5.getPrice();
		}

		_assertPrices(
			expectedPrice1_level3.stripTrailingZeros(),
			actualPrice1.stripTrailingZeros());
		_assertPrices(
			expectedPrice2.stripTrailingZeros(),
			actualPrice2.stripTrailingZeros());
		_assertPrices(
			expectedPrice3.stripTrailingZeros(),
			actualPrice3.stripTrailingZeros());
		_assertPrices(
			expectedPrice4.stripTrailingZeros(),
			actualPrice4.stripTrailingZeros());
		_assertPrices(
			expectedPrice5.stripTrailingZeros(),
			actualPrice5.stripTrailingZeros());
	}

	@Test
	public void testCouponCodeDiscount() throws Exception {
		frutillaRule.scenario(
			"Discounts can be applied by coupon code"
		).given(
			"A product with a base price"
		).and(
			"A discount that needs a coupon code to be available to use"
		).when(
			"I insert the correct coupon code in my context"
		).then(
			"The coupon discount is available to use and is applied for " +
				"calculating the final price"
		);

		long cpTaxCategoryId = CommerceTaxTestUtil.addTaxCategoryId(
			_user.getGroupId());

		double rate = Double.valueOf(RandomTestUtil.randomInt(0, 40));

		BigDecimal taxRate = BigDecimal.valueOf(rate);

		CommerceTaxTestUtil.setCommerceMethodTaxRate(
			_user.getUserId(), _commerceChannel.getGroupId(), cpTaxCategoryId,
			_commerceTaxMethod.getCommerceTaxMethodId(), rate);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		commerceOrder.setCommerceCurrencyCode(_commerceCurrency.getCode());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(35));

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstance.getSku(), StringPool.BLANK);

		String couponCode = StringUtil.randomString();

		CommerceDiscount commerceDiscount =
			CommerceDiscountTestUtil.addCouponCommerceDiscount(
				_group.getGroupId(), 10, couponCode,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				cpDefinition.getCPDefinitionId());

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			commerceOrder);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(), cpInstance.getCPInstanceId(),
			BigDecimal.ONE, commerceContext);

		commerceOrder = _commerceOrderLocalService.applyCouponCode(
			commerceOrder.getCommerceOrderId(), couponCode, commerceContext);

		commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			commerceOrder);

		_commerceOrders.add(commerceOrder);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		BigDecimal actualPrice = BigDecimal.ZERO;
		BigDecimal discountPrice = BigDecimal.ZERO;

		if (commerceProductPrice != null) {
			CommerceMoney finalPriceCommerceMoney =
				commerceProductPrice.getFinalPrice();

			actualPrice = finalPriceCommerceMoney.getPrice();

			CommerceDiscountValue discountValue =
				commerceProductPrice.getDiscountValue();

			CommerceMoney discountAmountCommerceMoney =
				discountValue.getDiscountAmount();

			discountPrice = discountAmountCommerceMoney.getPrice();
		}

		BigDecimal discountAmountWithoutTaxAmount =
			CommerceTaxTestUtil.getPriceWithoutTaxAmount(
				commerceDiscount.getLevel1(), taxRate,
				RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			discountAmountWithoutTaxAmount.stripTrailingZeros(),
			discountPrice.stripTrailingZeros());

		BigDecimal expectedPrice = CommerceTaxTestUtil.getPriceWithTaxAmount(
			commercePriceEntry.getPrice(), taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		expectedPrice = expectedPrice.subtract(commerceDiscount.getLevel1());

		expectedPrice = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		_assertPrices(
			expectedPrice.stripTrailingZeros(),
			actualPrice.stripTrailingZeros());
	}

	@Test
	public void testDiscountInBulkTierPriceEntryDiscoveryFalse()
		throws Exception {

		frutillaRule.scenario(
			"In a tier price entry when discovery flag is false each tier " +
				"has its discount levels"
		).given(
			"A bulk tier price entry with discovery flag equals false"
		).when(
			"The price of a product is calculated"
		).then(
			"The discounts on each tier are applied correctly"
		);

		long cpTaxCategoryId = CommerceTaxTestUtil.addTaxCategoryId(
			_user.getGroupId());

		double rate = Double.valueOf(RandomTestUtil.randomInt(0, 40));

		BigDecimal taxRate = BigDecimal.valueOf(rate);

		CommerceTaxTestUtil.setCommerceMethodTaxRate(
			_user.getUserId(), _commerceChannel.getGroupId(), cpTaxCategoryId,
			_commerceTaxMethod.getCommerceTaxMethodId(), rate);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePriceList1.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		BigDecimal price1 = BigDecimal.valueOf(20);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList1.getCommercePriceListId(), price1, false,
				BigDecimal.valueOf(10), BigDecimal.valueOf(15),
				BigDecimal.valueOf(5), BigDecimal.valueOf(10), true, true);

		BigDecimal price5 = BigDecimal.valueOf(18);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), StringPool.BLANK,
			price5, BigDecimal.valueOf(5), true, false, BigDecimal.valueOf(10),
			BigDecimal.valueOf(10), BigDecimal.valueOf(10),
			BigDecimal.valueOf(10), true, true);

		BigDecimal price10 = BigDecimal.valueOf(15);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), StringPool.BLANK,
			price10, BigDecimal.TEN, true, false, BigDecimal.valueOf(10),
			BigDecimal.valueOf(10), BigDecimal.valueOf(10),
			BigDecimal.valueOf(10), true, true);

		BigDecimal price15 = BigDecimal.valueOf(10);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), StringPool.BLANK,
			price15, BigDecimal.valueOf(15), true, false,
			BigDecimal.valueOf(10), BigDecimal.valueOf(10),
			BigDecimal.valueOf(10), BigDecimal.valueOf(10), true, true);

		BigDecimal price20 = BigDecimal.valueOf(5);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), StringPool.BLANK,
			price20, BigDecimal.valueOf(20), true, false,
			BigDecimal.valueOf(10), BigDecimal.valueOf(10),
			BigDecimal.valueOf(10), BigDecimal.valueOf(10), true, true);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		BigDecimal quantity = BigDecimal.TEN;

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), quantity, false, StringPool.BLANK,
				commerceContext);

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		price10 = CommerceTaxTestUtil.getPriceWithTaxAmount(
			price10, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal expectedPrice = price10.multiply(quantity);

		BigDecimal expectedPrice_level1 = _subtractPercentage(
			expectedPrice, BigDecimal.valueOf(10));

		BigDecimal expectedPrice_level2 = _subtractPercentage(
			expectedPrice_level1, BigDecimal.valueOf(10));

		BigDecimal expectedPrice_level3 = _subtractPercentage(
			expectedPrice_level2, BigDecimal.valueOf(10));

		BigDecimal expectedPrice_level4 = _subtractPercentage(
			expectedPrice_level3, BigDecimal.valueOf(10));

		expectedPrice_level4 = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice_level4, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		_assertPrices(
			expectedPrice_level4.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testDiscountInPriceEntryDiscoveryFalse() throws Exception {
		frutillaRule.scenario(
			"In a price entry when discovery flag is false it has its " +
				"discount levels"
		).given(
			"A price entry with discovery flag equals false"
		).when(
			"The price of a product is calculated"
		).then(
			"The discounts on the entry are applied correctly"
		);

		long cpTaxCategoryId = CommerceTaxTestUtil.addTaxCategoryId(
			_user.getGroupId());

		double rate = Double.valueOf(RandomTestUtil.randomInt(0, 40));

		BigDecimal taxRate = BigDecimal.valueOf(rate);

		CommerceTaxTestUtil.setCommerceMethodTaxRate(
			_user.getUserId(), _commerceChannel.getGroupId(), cpTaxCategoryId,
			_commerceTaxMethod.getCommerceTaxMethodId(), rate);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePriceList1.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		BigDecimal price1 = BigDecimal.valueOf(20);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			commercePriceList1.getCommercePriceListId(), price1, false,
			BigDecimal.valueOf(10), BigDecimal.valueOf(15),
			BigDecimal.valueOf(5), BigDecimal.valueOf(10), true, true);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		BigDecimal quantity = BigDecimal.TEN;

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), quantity, false, StringPool.BLANK,
				commerceContext);

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		price1 = CommerceTaxTestUtil.getPriceWithTaxAmount(
			price1, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal expectedPrice = price1.multiply(quantity);

		BigDecimal expectedPrice_level1 = _subtractPercentage(
			expectedPrice, BigDecimal.valueOf(10));

		BigDecimal expectedPrice_level2 = _subtractPercentage(
			expectedPrice_level1, BigDecimal.valueOf(15));

		BigDecimal expectedPrice_level3 = _subtractPercentage(
			expectedPrice_level2, BigDecimal.valueOf(5));

		BigDecimal expectedPrice_level4 = _subtractPercentage(
			expectedPrice_level3, BigDecimal.valueOf(10));

		expectedPrice_level4 = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice_level4, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		_assertPrices(
			expectedPrice_level4.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testDiscountInPriceEntryDiscoveryTrue() throws Exception {
		frutillaRule.scenario(
			"In a price entry when discovery flag is true the discounts are " +
				"searched in the system"
		).given(
			"A price entry with discovery flag equals true and a system " +
				"discount"
		).when(
			"The price of a product is calculated"
		).then(
			"The discounts on each tier are applied correctly"
		);

		long cpTaxCategoryId = CommerceTaxTestUtil.addTaxCategoryId(
			_user.getGroupId());

		double rate = Double.valueOf(RandomTestUtil.randomInt(0, 40));

		BigDecimal taxRate = BigDecimal.valueOf(rate);

		CommerceTaxTestUtil.setCommerceMethodTaxRate(
			_user.getUserId(), _commerceChannel.getGroupId(), cpTaxCategoryId,
			_commerceTaxMethod.getCommerceTaxMethodId(), rate);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePriceList1.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		BigDecimal price1 = BigDecimal.valueOf(20);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			commercePriceList1.getCommercePriceListId(), price1, true,
			BigDecimal.valueOf(10), BigDecimal.valueOf(15),
			BigDecimal.valueOf(5), BigDecimal.valueOf(10), true, true);

		BigDecimal discount1 = new BigDecimal("10");

		CommerceDiscountTestUtil.addFixedCommerceDiscount(
			_group.getGroupId(), discount1.doubleValue(),
			CommerceDiscountConstants.TARGET_PRODUCTS,
			cpDefinition.getCPDefinitionId());

		BigDecimal percentage1 = BigDecimal.valueOf(5);

		CommerceDiscountTestUtil.addPercentageCommerceDiscount(
			_group.getGroupId(), percentage1,
			CommerceDiscountConstants.LEVEL_L1,
			CommerceDiscountConstants.TARGET_PRODUCTS,
			cpDefinition.getCPDefinitionId());

		BigDecimal percentage3 = BigDecimal.valueOf(70);

		CommerceDiscountTestUtil.addPercentageCommerceDiscount(
			_group.getGroupId(), percentage3,
			CommerceDiscountConstants.LEVEL_L3,
			CommerceDiscountConstants.TARGET_PRODUCTS,
			cpDefinition.getCPDefinitionId());

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.TEN, false,
				StringPool.BLANK, commerceContext);

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = price1;

		expectedPrice = CommerceTaxTestUtil.getPriceWithTaxAmount(
			expectedPrice, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		expectedPrice = expectedPrice.subtract(discount1);

		BigDecimal percentage4 = _ONE_HUNDRED.subtract(percentage3);

		expectedPrice = expectedPrice.multiply(percentage4);

		expectedPrice = expectedPrice.divide(_ONE_HUNDRED);

		expectedPrice = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		expectedPrice = expectedPrice.multiply(BigDecimal.valueOf(10));

		_assertPrices(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testPriceEntryWithDiscountsNoDiscovery() throws Exception {
		frutillaRule.scenario(
			"The unit price of a product is retrieved when price entry level " +
				"discounts are defined"
		).given(
			"A catalog with a product and a price list with a price entry " +
				"with the product with discovery flag = false"
		).when(
			"The price of the product is discovered"
		).then(
			"The correct price is returned and the discounts taken from the " +
				"price entry"
		);

		long cpTaxCategoryId = CommerceTaxTestUtil.addTaxCategoryId(
			_user.getGroupId());

		double rate = Double.valueOf(RandomTestUtil.randomInt(0, 40));

		BigDecimal taxRate = BigDecimal.valueOf(rate);

		CommerceTaxTestUtil.setCommerceMethodTaxRate(
			_user.getUserId(), _commerceChannel.getGroupId(), cpTaxCategoryId,
			_commerceTaxMethod.getCommerceTaxMethodId(), rate);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		BigDecimal price = BigDecimal.valueOf(20);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), price, false,
				BigDecimal.valueOf(0), BigDecimal.valueOf(0),
				BigDecimal.valueOf(0), BigDecimal.valueOf(5), true, true);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		price = CommerceTaxTestUtil.getPriceWithTaxAmount(
			price, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		BigDecimal expectedPrice = _subtractPercentage(
			price, commercePriceEntry.getDiscountLevel4());

		expectedPrice = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
			expectedPrice, taxRate,
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		_assertPrices(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private void _assertPrices(BigDecimal expectedPrice, BigDecimal actualPrice)
		throws Exception {

		_commerceCurrency.setFormatPattern("$###,##0.00", LocaleUtil.US);

		String formattedExpectedPrice = _commercePriceFormatter.format(
			_commerceCurrency, expectedPrice, LocaleUtil.US);

		String formattedActualPrice = _commercePriceFormatter.format(
			_commerceCurrency, actualPrice, LocaleUtil.US);

		Assert.assertEquals(formattedExpectedPrice, formattedActualPrice);
	}

	private BigDecimal _subtractPercentage(
		BigDecimal base, BigDecimal percentage) {

		BigDecimal percentageValue = percentage.divide(_ONE_HUNDRED);

		BigDecimal calculatedPercentage = percentageValue.multiply(base);

		return base.subtract(calculatedPercentage);
	}

	private static final BigDecimal _ONE_HUNDRED = BigDecimal.valueOf(100);

	private static User _user;

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private final List<CommerceOrder> _commerceOrders = new ArrayList<>();

	@Inject
	private CommercePriceFormatter _commercePriceFormatter;

	@Inject
	private CommercePriceListAccountRelLocalService
		_commercePriceListAccountRelLocalService;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@DeleteAfterTestRun
	private CommerceTaxMethod _commerceTaxMethod;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	private Group _group;

}