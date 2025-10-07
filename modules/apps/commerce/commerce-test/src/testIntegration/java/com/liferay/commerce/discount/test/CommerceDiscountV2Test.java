/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalServiceUtil;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.constants.CommerceDiscountRuleConstants;
import com.liferay.commerce.discount.exception.DuplicateCommerceDiscountException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountRuleLocalService;
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
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceAccountGroupTestUtil;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

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
public class CommerceDiscountV2Test {

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

		BigDecimal discountAmountPrice = discountAmountCommerceMoney.getPrice();

		Assert.assertEquals(
			expectedDiscountLevel.stripTrailingZeros(),
			discountAmountPrice.stripTrailingZeros());

		BigDecimal price = commercePriceEntry.getPrice();

		BigDecimal expectedPrice = price.subtract(commerceDiscount.getLevel1());

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal actualPrice = finalPriceCommerceMoney.getPrice();

		Assert.assertEquals(
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

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

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

		BigDecimal price = commercePriceEntry.getPrice();

		BigDecimal expectedPrice = price.subtract(commerceDiscount.getLevel1());

		BigDecimal actualPrice = BigDecimal.ZERO;

		if (commerceProductPrice != null) {
			CommerceMoney finalPriceCommerceMoney =
				commerceProductPrice.getFinalPrice();

			actualPrice = finalPriceCommerceMoney.getPrice();
		}

		Assert.assertEquals(
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
		CPDefinition cpDefinition2 = cpInstance2.getCPDefinition();
		CPDefinition cpDefinition3 = cpInstance3.getCPDefinition();
		CPDefinition cpDefinition4 = cpInstance4.getCPDefinition();
		CPDefinition cpDefinition5 = cpInstance5.getCPDefinition();

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

		BigDecimal expectedPrice1 = price1.subtract(
			commerceDiscount2.getLevel1());

		BigDecimal price2 = commercePriceEntry2.getPrice();

		BigDecimal expectedPrice2 = price2.subtract(
			commerceDiscount2.getLevel1());

		BigDecimal price3 = commercePriceEntry3.getPrice();

		BigDecimal expectedPrice3 = price3.subtract(
			commerceDiscount3.getLevel1());

		BigDecimal price4 = commercePriceEntry4.getPrice();

		BigDecimal expectedPrice4 = price4.subtract(
			commerceDiscount3.getLevel1());

		BigDecimal price5 = commercePriceEntry5.getPrice();

		BigDecimal expectedPrice5 = price5.subtract(
			commerceDiscount3.getLevel1());

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

		Assert.assertEquals(
			expectedPrice1.stripTrailingZeros(),
			actualPrice1.stripTrailingZeros());
		Assert.assertEquals(
			expectedPrice2.stripTrailingZeros(),
			actualPrice2.stripTrailingZeros());
		Assert.assertEquals(
			expectedPrice3.stripTrailingZeros(),
			actualPrice3.stripTrailingZeros());
		Assert.assertEquals(
			expectedPrice4.stripTrailingZeros(),
			actualPrice4.stripTrailingZeros());
		Assert.assertEquals(
			expectedPrice5.stripTrailingZeros(),
			actualPrice5.stripTrailingZeros());
	}

	@Test
	public void testCommerceFixedDiscountWithUnitOfMeasure() throws Exception {
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

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), cpInstance.getCPInstanceId(), true,
				BigDecimal.ONE, "KEY",
				HashMapBuilder.put(
					LocaleUtil.getDefault(), "NOME"
				).build(),
				0, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
				cpInstance.getSku());

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(35), cpInstanceUnitOfMeasure.getKey());

		CommerceDiscount commerceDiscount =
			CommerceDiscountTestUtil.addFixedCommerceDiscount(
				_user.getGroupId(), BigDecimal.TEN,
				CommerceDiscountConstants.TARGET_SKUS,
				UnicodePropertiesBuilder.create(
					HashMapBuilder.put(
						"unitOfMeasureKey", cpInstanceUnitOfMeasure.getKey()
					).build(),
					true
				).build(),
				cpInstance.getCPInstanceId());

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		BigDecimal price = commercePriceEntry.getPrice();

		BigDecimal expectedPrice = price.subtract(commerceDiscount.getLevel1());

		BigDecimal actualPrice = BigDecimal.ZERO;

		if (commerceProductPrice != null) {
			CommerceMoney finalPriceCommerceMoney =
				commerceProductPrice.getFinalPrice();

			actualPrice = finalPriceCommerceMoney.getPrice();
		}

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			actualPrice.stripTrailingZeros());
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
		CPDefinition cpDefinition2 = cpInstance2.getCPDefinition();
		CPDefinition cpDefinition3 = cpInstance3.getCPDefinition();
		CPDefinition cpDefinition4 = cpInstance4.getCPDefinition();
		CPDefinition cpDefinition5 = cpInstance5.getCPDefinition();

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

		BigDecimal expectedPrice1_level1 = _subtractPercentage(
			commercePriceEntry1.getPrice(), commerceDiscount11.getLevel1());

		BigDecimal expectedPrice1_level2 = _subtractPercentage(
			expectedPrice1_level1, commerceDiscount12.getLevel2());

		BigDecimal expectedPrice1_level3 = _subtractPercentage(
			expectedPrice1_level2, commerceDiscount13.getLevel3());

		BigDecimal expectedPrice2 = _subtractPercentage(
			commercePriceEntry2.getPrice(), commerceDiscount21.getLevel1());
		BigDecimal expectedPrice3 = _subtractPercentage(
			commercePriceEntry3.getPrice(), commerceDiscount31.getLevel1());
		BigDecimal expectedPrice4 = _subtractPercentage(
			commercePriceEntry4.getPrice(), commerceDiscount31.getLevel1());
		BigDecimal expectedPrice5 = _subtractPercentage(
			commercePriceEntry5.getPrice(), commerceDiscount31.getLevel1());

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

		Assert.assertEquals(
			expectedPrice1_level3.stripTrailingZeros(),
			actualPrice1.stripTrailingZeros());
		Assert.assertEquals(
			expectedPrice2.stripTrailingZeros(),
			actualPrice2.stripTrailingZeros());
		Assert.assertEquals(
			expectedPrice3.stripTrailingZeros(),
			actualPrice3.stripTrailingZeros());
		Assert.assertEquals(
			expectedPrice4.stripTrailingZeros(),
			actualPrice4.stripTrailingZeros());
		Assert.assertEquals(
			expectedPrice5.stripTrailingZeros(),
			actualPrice5.stripTrailingZeros());
	}

	@Test
	public void testCommercePercentageDiscountWithMaximumAmount()
		throws Exception {

		frutillaRule.scenario(
			"Apply a percentage price discount with a maximum amount lower " +
				"than the discount amount"
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

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

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
			CommerceDiscountTestUtil.addPercentageCommerceDiscount(
				_group.getGroupId(), BigDecimal.valueOf(25),
				CommerceDiscountConstants.LEVEL_L1,
				CommerceDiscountConstants.TARGET_PRODUCTS,
				cpDefinition.getCPDefinitionId());

		commerceDiscount.setMaximumDiscountAmount(BigDecimal.ONE);

		_commerceDiscountLocalService.updateCommerceDiscount(commerceDiscount);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		BigDecimal price = commercePriceEntry.getPrice();

		BigDecimal expectedPrice = price.subtract(BigDecimal.ONE);

		BigDecimal actualPrice = BigDecimal.ZERO;

		if (commerceProductPrice != null) {
			CommerceMoney finalPriceCommerceMoney =
				commerceProductPrice.getFinalPrice();

			actualPrice = finalPriceCommerceMoney.getPrice();
		}

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			actualPrice.stripTrailingZeros());
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

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), commerceChannel.getGroupId(), _commerceCurrency);

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
			commerceChannel.getCommerceChannelId());

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
			_accountEntry, _commerceCurrency, commerceChannel, _user, _group,
			commerceOrder);

		CommerceTestUtil.addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(), cpInstance.getCPInstanceId(),
			BigDecimal.ONE, commerceContext);

		commerceOrder = _commerceOrderLocalService.applyCouponCode(
			commerceOrder.getCommerceOrderId(), couponCode, commerceContext);

		commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, commerceChannel, _user, _group,
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

		BigDecimal expectedPrice = commerceDiscount.getLevel1();

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			discountPrice.stripTrailingZeros());

		BigDecimal price = commercePriceEntry.getPrice();

		expectedPrice = price.subtract(commerceDiscount.getLevel1());

		Assert.assertEquals(
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

		BigDecimal expectedPrice = price10.multiply(quantity);

		BigDecimal expectedPrice_level1 = _subtractPercentage(
			expectedPrice, BigDecimal.valueOf(10));

		BigDecimal expectedPrice_level2 = _subtractPercentage(
			expectedPrice_level1, BigDecimal.valueOf(10));

		BigDecimal expectedPrice_level3 = _subtractPercentage(
			expectedPrice_level2, BigDecimal.valueOf(10));

		BigDecimal expectedPrice_level4 = _subtractPercentage(
			expectedPrice_level3, BigDecimal.valueOf(10));

		Assert.assertEquals(
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

		BigDecimal expectedPrice = price1.multiply(quantity);

		BigDecimal expectedPrice_level1 = _subtractPercentage(
			expectedPrice, BigDecimal.valueOf(10));

		BigDecimal expectedPrice_level2 = _subtractPercentage(
			expectedPrice_level1, BigDecimal.valueOf(15));

		BigDecimal expectedPrice_level3 = _subtractPercentage(
			expectedPrice_level2, BigDecimal.valueOf(5));

		BigDecimal expectedPrice_level4 = _subtractPercentage(
			expectedPrice_level3, BigDecimal.valueOf(10));

		Assert.assertEquals(
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

		BigDecimal price1 = BigDecimal.valueOf(20);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			commercePriceList1.getCommercePriceListId(), price1, true,
			BigDecimal.valueOf(10), BigDecimal.valueOf(15),
			BigDecimal.valueOf(5), BigDecimal.valueOf(10), true, true);

		CommerceDiscountTestUtil.addFixedCommerceDiscount(
			_group.getGroupId(), 10, CommerceDiscountConstants.TARGET_PRODUCTS,
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

		BigDecimal expectedPrice = BigDecimal.valueOf(30);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test(expected = DuplicateCommerceDiscountException.class)
	public void testDuplicateCouponCode() throws Exception {
		frutillaRule.scenario(
			"It is not possible to create 2 discounts with the same coupon code"
		).given(
			"A discount with a coupon code"
		).when(
			"I try to create another discount with the same coupon code"
		).then(
			"I should receive an exception"
		);

		String couponCode = StringUtil.randomString();

		CommerceDiscount commerceDiscount =
			CommerceDiscountTestUtil.addCouponCommerceDiscount(
				_user.getGroupId(), 1, couponCode,
				CommerceDiscountConstants.TARGET_TOTAL, null);

		Assert.assertNotNull(commerceDiscount);

		CommerceDiscountTestUtil.addCouponCommerceDiscount(
			_user.getGroupId(), 1, couponCode,
			CommerceDiscountConstants.TARGET_TOTAL, null);
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

		BigDecimal expectedPrice = _subtractPercentage(
			price, commercePriceEntry.getDiscountLevel4());

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testPriceEntryWithDiscountsWithDiscovery() throws Exception {
		frutillaRule.scenario(
			"The unit price of a product is retrieved when price entry level " +
				"discounts are defined and no system discounts are defined"
		).given(
			"A catalog with a product and a price list with a price entry " +
				"with the product with discovery flag = true"
		).when(
			"The price of the product is discovered"
		).then(
			"The correct price is returned and the discounts shall be empty"
		);

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

		BigDecimal price = BigDecimal.valueOf(10);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), price, true,
			BigDecimal.valueOf(5), BigDecimal.valueOf(5), BigDecimal.valueOf(5),
			BigDecimal.valueOf(5), true, true);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPriceCommerceMoneyPrice =
			finalPriceCommerceMoney.getPrice();

		Assert.assertEquals(
			price,
			BigDecimalUtil.stripTrailingZeros(finalPriceCommerceMoneyPrice));
	}

	@Test
	public void testPriceEntryWithSystemDiscountsWithDiscovery()
		throws Exception {

		frutillaRule.scenario(
			"The unit price of a product is retrieved when price entry level " +
				"discounts are defined and system discount are defined"
		).given(
			"A catalog with a product and a price list with a price entry " +
				"with the product with discovery flag = true"
		).when(
			"The price of the product is discovered"
		).then(
			"The correct price is returned and the discounts shall contain " +
				"an entry for each found system discount"
		);

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

		BigDecimal price = BigDecimal.valueOf(50);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), price, true,
			BigDecimal.valueOf(10), BigDecimal.valueOf(10),
			BigDecimal.valueOf(1), BigDecimal.valueOf(5), true, true);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceDiscountTestUtil.addFixedCommerceDiscount(
			_group.getGroupId(), 10, CommerceDiscountConstants.TARGET_PRODUCTS,
			cpDefinition.getCPDefinitionId());

		CommerceDiscountTestUtil.addPercentageCommerceDiscount(
			_group.getGroupId(), BigDecimal.valueOf(5),
			CommerceDiscountConstants.LEVEL_L2,
			CommerceDiscountConstants.TARGET_PRODUCTS,
			cpDefinition.getCPDefinitionId());

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedFinalPrice = price.subtract(BigDecimal.valueOf(10));

		BigDecimal expectedPrice = expectedFinalPrice.multiply(
			BigDecimal.valueOf(5));

		expectedPrice = expectedPrice.divide(BigDecimal.valueOf(100));

		expectedFinalPrice = expectedFinalPrice.subtract(expectedPrice);

		Assert.assertEquals(
			expectedFinalPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testTierPriceEntryAndSystemDiscounts() throws Exception {
		frutillaRule.scenario(
			"The unit price of a product is retrieved when discounts are " +
				"both on price entry level and system discounts are defined"
		).given(
			"A catalog with a product and a price list with a tier price " +
				"entry with the product with entry discounts on one tier"
		).when(
			"The price of the product is discovered"
		).then(
			"The correct price and discounts is returned given the quantity"
		);

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

		BigDecimal price1 = BigDecimal.valueOf(25);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), price1, true,
				BigDecimal.valueOf(10), BigDecimal.valueOf(5),
				BigDecimal.valueOf(0), BigDecimal.valueOf(10), true, true);

		BigDecimal price2 = BigDecimal.valueOf(20);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), StringPool.BLANK,
			price2, BigDecimal.valueOf(5), false, true, BigDecimal.valueOf(10),
			BigDecimal.valueOf(10), BigDecimal.valueOf(0),
			BigDecimal.valueOf(0), true, true);

		BigDecimal price3 = BigDecimal.valueOf(10);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), StringPool.BLANK,
			price3, BigDecimal.TEN, false, false, BigDecimal.valueOf(5),
			BigDecimal.valueOf(5), BigDecimal.valueOf(20),
			BigDecimal.valueOf(0), true, true);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceDiscountTestUtil.addFixedCommerceDiscount(
			_group.getGroupId(), 5, CommerceDiscountConstants.TARGET_PRODUCTS,
			cpDefinition.getCPDefinitionId());

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = BigDecimal.valueOf(20);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.valueOf(100),
				StringPool.BLANK, commerceContext);

		finalPriceCommerceMoney = commerceProductPrice.getFinalPrice();

		finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal tier1Price = BigDecimal.valueOf(4);

		tier1Price = price1.multiply(tier1Price);

		BigDecimal tier2Price = BigDecimal.valueOf(5);

		tier2Price = price2.multiply(tier2Price);

		BigDecimal tier3Price = BigDecimal.valueOf(91);

		tier3Price = price3.multiply(tier3Price);

		expectedPrice = tier1Price.add(tier2Price);

		expectedPrice = expectedPrice.add(tier3Price);

		BigDecimal totalQuantity = BigDecimal.valueOf(100);

		expectedPrice = expectedPrice.divide(totalQuantity);

		expectedPrice = expectedPrice.subtract(BigDecimal.valueOf(5));

		expectedPrice = expectedPrice.multiply(totalQuantity);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testVerifyDiscountRules() throws Exception {
		frutillaRule.scenario(
			"If a discount rule is not valid the discount is not applied"
		).given(
			"A discount with a rule on the cart total"
		).when(
			"The price of the product is discovered without an open order"
		).then(
			"The discount is not applied"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

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

		_commerceDiscountRuleLocalService.addCommerceDiscountRule(
			commerceDiscount.getCommerceDiscountId(),
			CommerceDiscountRuleConstants.TYPE_CART_TOTAL, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		BigDecimal expectedPrice = commercePriceEntry.getPrice();

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

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
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	@Inject
	private CommerceDiscountRuleLocalService _commerceDiscountRuleLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private final List<CommerceOrder> _commerceOrders = new ArrayList<>();

	@Inject
	private CommercePriceListAccountRelLocalService
		_commercePriceListAccountRelLocalService;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Inject
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	private Group _group;

	@Inject
	private UserLocalService _userLocalService;

}