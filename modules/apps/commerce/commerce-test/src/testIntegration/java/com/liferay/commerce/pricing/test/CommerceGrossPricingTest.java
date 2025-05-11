/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalServiceUtil;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListAccountRelLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalServiceUtil;
import com.liferay.commerce.price.list.test.util.CommercePriceEntryTestUtil;
import com.liferay.commerce.price.list.test.util.CommercePriceListTestUtil;
import com.liferay.commerce.pricing.constants.CommercePriceModifierConstants;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.service.CommercePriceModifierLocalService;
import com.liferay.commerce.pricing.service.CommercePriceModifierRelLocalService;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelLocalService;
import com.liferay.commerce.pricing.service.CommercePricingClassLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalServiceUtil;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodLocalService;
import com.liferay.commerce.test.util.CommerceTaxTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Calendar;

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
public class CommerceGrossPricingTest {

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

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		AccountGroup accountGroup = _accountGroupLocalService.addAccountGroup(
			StringPool.BLANK, serviceContext.getUserId(), null,
			RandomTestUtil.randomString(), serviceContext);

		accountGroup.setDefaultAccountGroup(false);
		accountGroup.setType(AccountConstants.ACCOUNT_GROUP_TYPE_DYNAMIC);
		accountGroup.setExpandoBridgeAttributes(serviceContext);

		_accountGroupLocalService.updateAccountGroup(accountGroup);

		_commerceCurrency =
			CommerceCurrencyLocalServiceUtil.fetchPrimaryCommerceCurrency(
				_group.getCompanyId());

		if (_commerceCurrency == null) {
			_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
				_group.getCompanyId());
		}

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_commerceChannel.setPriceDisplayType("tax-included");

		_commerceChannel = _commerceChannelLocalService.updateCommerceChannel(
			_commerceChannel);

		_rate = 22;

		_factor = _rate / 100;
		_factor = 1 + _factor;

		_commerceTaxCategoryId =
			CommerceTaxTestUtil.getDefaultCompanyTaxCategory(
				_user.getGroupId());

		_commerceTaxMethod = CommerceTaxTestUtil.addCommerceByAddressTaxMethod(
			_user.getUserId(), _commerceChannel.getGroupId(), true);

		CommerceTaxTestUtil.setCommerceMethodTaxRate(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceTaxCategoryId, _commerceTaxMethod.getCommerceTaxMethodId(),
			_rate);
	}

	@After
	public void tearDown() throws Exception {
		_commerceTaxMethodLocalService.deleteCommerceTaxMethod(
			_commerceTaxMethod.getCommerceTaxMethodId());
	}

	@Test
	public void testBulkTierPriceEntryNoPromoNoDiscounts() throws Exception {
		frutillaRule.scenario(
			"The unit price of a product is retrieved when no promotion nor " +
				"discounts are defined"
		).given(
			"A catalog with a product and a price list with a bulk tier " +
				"price entry with the product"
		).when(
			"The price of the product is discovered"
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

		commercePriceList.setNetPrice(false);

		commercePriceList =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePriceList);

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(_commerceTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		double netPrice1 = 20;

		double grossPrice1 = netPrice1 * _factor;

		BigDecimal price1 = BigDecimal.valueOf(grossPrice1);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), price1, false, null,
				null, null, null, true, true);

		double netPrice5 = 15;

		double grossPrice5 = netPrice5 * _factor;

		BigDecimal price5 = BigDecimal.valueOf(grossPrice5);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), "", price5,
			BigDecimal.valueOf(5), true, false, null, null, null, null, true,
			true);

		double netPrice10 = 10;

		double grossPrice10 = netPrice10 * _factor;

		BigDecimal price10 = BigDecimal.valueOf(grossPrice10);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), "", price10,
			BigDecimal.TEN, true, false, null, null, null, null, true, true);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		BigDecimal quantity = BigDecimal.ONE;

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), quantity, StringPool.BLANK,
				commerceContext);

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();
		CommerceMoney finalGrossPriceCommerceMoney =
			commerceProductPrice.getFinalPriceWithTaxAmount();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();
		BigDecimal finalGrossPrice = finalGrossPriceCommerceMoney.getPrice();

		BigDecimal expectedNetPrice1 = BigDecimal.valueOf(netPrice1);

		BigDecimal expectedNet = expectedNetPrice1.multiply(quantity);

		BigDecimal expectedGross = price1.multiply(quantity);

		finalGrossPrice = finalGrossPrice.setScale(
			expectedGross.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedGross.stripTrailingZeros(),
			finalGrossPrice.stripTrailingZeros());

		finalPrice = finalPrice.setScale(
			expectedNet.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedNet.stripTrailingZeros(), finalPrice.stripTrailingZeros());

		quantity = BigDecimal.valueOf(100);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), quantity, StringPool.BLANK,
				commerceContext);

		finalPriceCommerceMoney = commerceProductPrice.getFinalPrice();
		finalGrossPriceCommerceMoney =
			commerceProductPrice.getFinalPriceWithTaxAmount();

		finalPrice = finalPriceCommerceMoney.getPrice();
		finalGrossPrice = finalGrossPriceCommerceMoney.getPrice();

		BigDecimal expectedNetPrice10 = BigDecimal.valueOf(netPrice10);

		expectedNet = expectedNetPrice10.multiply(quantity);

		expectedGross = price10.multiply(quantity);

		finalGrossPrice = finalGrossPrice.setScale(
			expectedGross.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedGross.stripTrailingZeros(),
			finalGrossPrice.stripTrailingZeros());

		finalPrice = finalPrice.setScale(
			expectedNet.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedNet.stripTrailingZeros(), finalPrice.stripTrailingZeros());

		CPDefinitionLocalServiceUtil.deleteCPDefinition(cpDefinition);
	}

	@Test
	public void testEmptyDiscountInPriceEntryDiscoveryFalse() throws Exception {
		frutillaRule.scenario(
			"In a price entry when discovery flag is false each tier " +
				"discounts are searched in the system"
		).given(
			"A tier price entry with discovery flag equals false"
		).when(
			"The price of a product is calculated"
		).then(
			"The discounts are applied correctly"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), null,
				ServiceContextTestUtil.getServiceContext());

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		commercePriceList1.setNetPrice(false);

		commercePriceList1 =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePriceList1);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePriceList1.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext());

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(_commerceTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		double netPrice1 = 20;

		double grossPrice1 = netPrice1 * _factor;

		BigDecimal price1 = BigDecimal.valueOf(grossPrice1);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commercePriceList1.getCommercePriceListId(), price1, false, null,
			null, null, null, true, true);

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
		CommerceMoney finalGrossPriceCommerceMoney =
			commerceProductPrice.getFinalPriceWithTaxAmount();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();
		BigDecimal finalGrossPrice = finalGrossPriceCommerceMoney.getPrice();

		BigDecimal expectedGross = price1.multiply(quantity);

		BigDecimal expectedNetPrice1 = BigDecimal.valueOf(netPrice1);

		expectedNetPrice1 = expectedNetPrice1.multiply(quantity);

		finalGrossPrice = finalGrossPrice.setScale(
			expectedGross.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedGross.stripTrailingZeros(),
			finalGrossPrice.stripTrailingZeros());

		finalPrice = finalPrice.setScale(
			expectedNetPrice1.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedNetPrice1.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		CPDefinitionLocalServiceUtil.deleteCPDefinition(cpDefinition);
	}

	@Test
	public void testPriceEntryNoPromoNoDiscounts() throws Exception {
		frutillaRule.scenario(
			"The unit price of a product is retrieved when no promotion nor " +
				"discounts are defined"
		).given(
			"A catalog with a product and a price list with a price entry " +
				"with the product"
		).when(
			"The price of the product is discovered"
		).then(
			"The correct price is returned"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		commercePriceList.setNetPrice(false);

		commercePriceList =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePriceList);

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(_commerceTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		double netPrice = 35;

		double grossPrice = netPrice * _factor;

		BigDecimal price = BigDecimal.valueOf(grossPrice);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), price, false, null,
			null, null, null, true, true);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();
		CommerceMoney finalGrossPriceCommerceMoney =
			commerceProductPrice.getFinalPriceWithTaxAmount();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();
		BigDecimal finalGrossPrice = finalGrossPriceCommerceMoney.getPrice();

		BigDecimal expectedNetPrice = BigDecimal.valueOf(netPrice);

		finalGrossPrice = finalGrossPrice.setScale(
			price.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			price.stripTrailingZeros(), finalGrossPrice.stripTrailingZeros());

		finalPrice = finalPrice.setScale(
			expectedNetPrice.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedNetPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		CPDefinitionLocalServiceUtil.deleteCPDefinition(cpDefinition);
	}

	@Test
	public void testPriceEntryWithPromoNoDiscounts() throws Exception {
		frutillaRule.scenario(
			"The unit price and the promo price of a product is retrieved " +
				"when no discounts are defined"
		).given(
			"A catalog with a product a price list with a price entry with " +
				"the product and a promo on the product"
		).when(
			"The price of the product is discovered"
		).then(
			"The correct price and the promo is returned "
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CommercePriceList commercePromotion =
			CommercePriceListTestUtil.addPromotion(
				commerceCatalog.getGroupId(), 0.0);

		commercePromotion.setNetPrice(false);

		commercePromotion =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePromotion);

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(_commerceTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		double netPrice = 20;

		double grossPrice = netPrice * _factor;

		BigDecimal price = BigDecimal.valueOf(100);
		BigDecimal promoPrice = BigDecimal.valueOf(grossPrice);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), price, false, null,
			null, null, null, true, true);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commercePromotion.getCommercePriceListId(), promoPrice, false, null,
			null, null, null, true, true);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();
		CommerceMoney finalGrossPriceCommerceMoney =
			commerceProductPrice.getFinalPriceWithTaxAmount();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal finalGrossPrice = finalGrossPriceCommerceMoney.getPrice();

		BigDecimal expectedNetPrice = BigDecimal.valueOf(netPrice);

		finalPrice = finalPrice.setScale(
			expectedNetPrice.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			promoPrice.stripTrailingZeros(),
			finalGrossPrice.stripTrailingZeros());

		Assert.assertEquals(
			expectedNetPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		CPDefinitionLocalServiceUtil.deleteCPDefinition(cpDefinition);
	}

	@Test
	public void testPriceModifiersOnDifferentPriceTypePriceLists()
		throws Exception {

		frutillaRule.scenario(
			StringBundler.concat(
				"If a price list does not contain the current price entry ",
				"then the price modifiers configured on the price list are ",
				"applied on top of the base price if the priceType of the and ",
				"active pricelist is the same")
		).given(
			"A price list not containing the current product price entry and " +
				"price modifiers targeting the product defined for the price " +
					"list"
		).when(
			"The price of a product is calculated"
		).then(
			"The price modifier that results in the lowest price is applied " +
				"to the base price"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), null,
				ServiceContextTestUtil.getServiceContext());

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		commercePriceList1.setNetPrice(false);

		commercePriceList1 =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePriceList1);

		CommercePriceList basePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), true, 0.0);

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(_commerceTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		BigDecimal price1 = new BigDecimal("20");

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			basePriceList.getCommercePriceListId(), price1);

		double modifierAmount = -10;

		CommercePriceModifier commercePriceModifier = _addCommercePriceModifier(
			commercePriceList1.getGroupId(),
			CommercePriceModifierConstants.TARGET_PRODUCTS,
			commercePriceList1.getCommercePriceListId(),
			CommercePriceModifierConstants.MODIFIER_TYPE_FIXED_AMOUNT,
			BigDecimal.valueOf(modifierAmount), true);

		_commercePriceModifierRelLocalService.addCommercePriceModifierRel(
			commercePriceModifier.getCommercePriceModifierId(),
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			ServiceContextTestUtil.getServiceContext());

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.TEN, false,
				StringPool.BLANK, commerceContext);

		CommerceMoney finalPriceWithTaxAmountCommerceMoney =
			commerceProductPrice.getFinalPriceWithTaxAmount();

		BigDecimal finalPrice = finalPriceWithTaxAmountCommerceMoney.getPrice();

		BigDecimal expectedPrice = BigDecimal.valueOf(200);

		finalPrice = finalPrice.setScale(
			expectedPrice.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		CPDefinitionLocalServiceUtil.deleteCPDefinition(cpDefinition);
	}

	@Test
	public void testPriceModifiersOnPricingClass() throws Exception {
		frutillaRule.scenario(
			"If a price list does not contain the current price entry then " +
				"the price modifiers configured on the price list are " +
					"applied on top of the base price"
		).given(
			"A price list not containing the current product price entry and " +
				"price modifiers targeting pricing class and category " +
					"defined for the price list"
		).when(
			"The price of a product is calculated"
		).then(
			"The price modifier that results in the lowest price is applied " +
				"to the base price"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), null,
				ServiceContextTestUtil.getServiceContext());

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		commercePriceList1.setNetPrice(false);

		commercePriceList1 =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePriceList1);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePriceList1.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext());

		CommercePriceList basePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), true, 0.0);

		basePriceList.setNetPrice(false);

		basePriceList =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				basePriceList);

		CommercePricingClass commercePricingClass =
			_commercePricingClassLocalService.addCommercePricingClass(
				_user.getUserId(), RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				ServiceContextTestUtil.getServiceContext());

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_user.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			_user.getGroupId(), assetVocabulary.getVocabularyId());

		long[] assetCategoryIds = {assetCategory.getCategoryId()};

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId(), assetCategoryIds);

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(_commerceTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		_commercePricingClassCPDefinitionRelLocalService.
			addCommercePricingClassCPDefinitionRel(
				commercePricingClass.getCommercePricingClassId(),
				cpDefinition.getCPDefinitionId(),
				ServiceContextTestUtil.getServiceContext());

		BigDecimal price1 = new BigDecimal("20");

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			basePriceList.getCommercePriceListId(), price1);

		CommercePriceModifier commercePriceModifier1 =
			_addCommercePriceModifier(
				commercePriceList1.getGroupId(),
				CommercePriceModifierConstants.TARGET_PRODUCT_GROUPS,
				commercePriceList1.getCommercePriceListId(),
				CommercePriceModifierConstants.MODIFIER_TYPE_PERCENTAGE,
				BigDecimal.valueOf(-10), true);

		_commercePriceModifierRelLocalService.addCommercePriceModifierRel(
			commercePriceModifier1.getCommercePriceModifierId(),
			CommercePricingClass.class.getName(),
			commercePricingClass.getCommercePricingClassId(),
			ServiceContextTestUtil.getServiceContext());

		CommercePriceModifier commercePriceModifier2 =
			_addCommercePriceModifier(
				commercePriceList1.getGroupId(),
				CommercePriceModifierConstants.TARGET_CATEGORIES,
				commercePriceList1.getCommercePriceListId(),
				CommercePriceModifierConstants.MODIFIER_TYPE_REPLACE,
				BigDecimal.valueOf(19), true);

		_commercePriceModifierRelLocalService.addCommercePriceModifierRel(
			commercePriceModifier2.getCommercePriceModifierId(),
			AssetCategory.class.getName(), assetCategory.getCategoryId(),
			ServiceContextTestUtil.getServiceContext());

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.TEN, false,
				StringPool.BLANK, commerceContext);

		CommerceMoney finalPriceWithTaxAmountCommerceMoney =
			commerceProductPrice.getFinalPriceWithTaxAmount();

		BigDecimal finalPrice = finalPriceWithTaxAmountCommerceMoney.getPrice();

		BigDecimal expectedPrice = BigDecimal.valueOf(180);

		finalPrice = finalPrice.setScale(
			expectedPrice.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		CPDefinitionLocalServiceUtil.deleteCPDefinition(cpDefinition);
	}

	@Test
	public void testPriceModifiersOnProduct() throws Exception {
		frutillaRule.scenario(
			"If a price list does not contain the current price entry then " +
				"the price modifiers configured on the price list are " +
					"applied on top of the base price"
		).given(
			"A price list not containing the current product price entry and " +
				"price modifiers targeting the product defined for the price " +
					"list"
		).when(
			"The price of a product is calculated"
		).then(
			"The price modifier that results in the lowest price is applied " +
				"to the base price"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), null,
				ServiceContextTestUtil.getServiceContext());

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		commercePriceList1.setNetPrice(false);

		commercePriceList1 =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePriceList1);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePriceList1.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext());

		CommercePriceList basePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), true, 0.0);

		basePriceList.setNetPrice(false);

		basePriceList =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				basePriceList);

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(_commerceTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		BigDecimal price1 = new BigDecimal("20");

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			basePriceList.getCommercePriceListId(), price1);

		double modifierAmount = -10;

		CommercePriceModifier commercePriceModifier = _addCommercePriceModifier(
			commercePriceList1.getGroupId(),
			CommercePriceModifierConstants.TARGET_PRODUCTS,
			commercePriceList1.getCommercePriceListId(),
			CommercePriceModifierConstants.MODIFIER_TYPE_FIXED_AMOUNT,
			BigDecimal.valueOf(modifierAmount), true);

		_commercePriceModifierRelLocalService.addCommercePriceModifierRel(
			commercePriceModifier.getCommercePriceModifierId(),
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			ServiceContextTestUtil.getServiceContext());

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.TEN, false,
				StringPool.BLANK, commerceContext);

		CommerceMoney finalPriceWithTaxAmountCommerceMoney =
			commerceProductPrice.getFinalPriceWithTaxAmount();

		BigDecimal finalPrice = finalPriceWithTaxAmountCommerceMoney.getPrice();

		BigDecimal expectedPrice = BigDecimal.valueOf(100);

		finalPrice = finalPrice.setScale(
			expectedPrice.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		CPDefinitionLocalServiceUtil.deleteCPDefinition(cpDefinition);
	}

	@Test
	public void testReplacePriceModifierOnDifferentCurrenciesPriceLists()
		throws Exception {

		frutillaRule.scenario(
			StringBundler.concat(
				"If a price modifier of type replace is applied to a base ",
				"price entry of different currency then the modifier value is ",
				"applied to the final converted value")
		).given(
			"A price list not containing the current product price entry and " +
				"a price modifier of type replace targeting the product " +
					"defined for the price list using a different currency"
		).when(
			"The price of a product is calculated"
		).then(
			"The price modifier is applied to the final converted base price"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), null,
				ServiceContextTestUtil.getServiceContext());

		CommercePriceList baseCommercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), true, 0.0);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CommerceCurrency commerceCurrency =
			commercePriceList.getCommerceCurrency();

		commerceCurrency.setRate(BigDecimal.valueOf(0.92));

		commerceCurrency =
			CommerceCurrencyLocalServiceUtil.updateCommerceCurrency(
				commerceCurrency);

		_commerceCurrency = commerceCurrency;

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		BigDecimal price = BigDecimal.valueOf(50);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			baseCommercePriceList.getCommercePriceListId(), price);

		double modifierAmount = 40;

		CommercePriceModifier commercePriceModifier = _addCommercePriceModifier(
			commercePriceList.getGroupId(),
			CommercePriceModifierConstants.TARGET_PRODUCTS,
			commercePriceList.getCommercePriceListId(),
			CommercePriceModifierConstants.MODIFIER_TYPE_REPLACE,
			BigDecimal.valueOf(modifierAmount), true);

		_commercePriceModifierRelLocalService.addCommercePriceModifierRel(
			commercePriceModifier.getCommercePriceModifierId(),
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			ServiceContextTestUtil.getServiceContext());

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, false,
				StringPool.BLANK, commerceContext);

		CommerceMoney unitPrice = commerceProductPrice.getUnitPrice();

		BigDecimal finalPrice = unitPrice.getPrice();

		BigDecimal expectedPrice = BigDecimal.valueOf(40);

		finalPrice = finalPrice.setScale(
			expectedPrice.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		CPDefinitionLocalServiceUtil.deleteCPDefinition(cpDefinition);
	}

	@Test
	public void testTierPriceEntryNoPromoNoDiscounts() throws Exception {
		frutillaRule.scenario(
			"The unit price of a product is retrieved when no promotion nor " +
				"discounts are defined"
		).given(
			"A catalog with a product and a price list with a tier price " +
				"entry with the product"
		).when(
			"The price of the product is discovered"
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

		commercePriceList.setNetPrice(false);

		commercePriceList =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePriceList);

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(_commerceTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		double netPrice1 = 50;

		double grossPrice1 = netPrice1 * _factor;

		BigDecimal price1 = BigDecimal.valueOf(grossPrice1);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), price1, false, null,
				null, null, null, true, true);

		double netPrice5 = 40;

		double grossPrice5 = netPrice5 * _factor;

		BigDecimal price5 = BigDecimal.valueOf(grossPrice5);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), "", price5,
			BigDecimal.valueOf(5), false, false, null, null, null, null, true,
			true);

		double netPrice10 = 30;

		double grossPrice10 = netPrice10 * _factor;

		BigDecimal price10 = BigDecimal.valueOf(grossPrice10);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), "", price10,
			BigDecimal.TEN, false, false, null, null, null, null, true, true);

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

		BigDecimal expectedNetPrice = BigDecimal.valueOf(netPrice1);

		finalPrice = finalPrice.setScale(
			expectedNetPrice.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedNetPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.valueOf(100),
				StringPool.BLANK, commerceContext);

		finalPriceCommerceMoney = commerceProductPrice.getFinalPrice();

		finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = BigDecimal.valueOf(3130);

		finalPrice = finalPrice.setScale(
			expectedPrice.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		CPDefinitionLocalServiceUtil.deleteCPDefinition(cpDefinition);
	}

	@Test
	public void testTierPriceEntryWithPromo() throws Exception {
		frutillaRule.scenario(
			"The unit price and the promo price of a product is retrieved " +
				"when no discounts are defined"
		).given(
			"A catalog with a product a price list with a price entry with " +
				"the product and a promo on the product"
		).when(
			"The price of the product is discovered"
		).then(
			"The correct price and the promo is returned "
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		CommercePriceList commercePromotion =
			CommercePriceListTestUtil.addPromotion(
				commerceCatalog.getGroupId(), 0.0);

		commercePromotion.setNetPrice(false);

		commercePromotion =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePromotion);

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(_commerceTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		BigDecimal price1 = BigDecimal.valueOf(20);

		double netPrice1 = 15;

		double grossPrice1 = netPrice1 * _factor;

		BigDecimal promoPrice = BigDecimal.valueOf(grossPrice1);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), price1, false, null,
			null, null, null, true, true);

		CommercePriceEntry commercePromoEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePromotion.getCommercePriceListId(), promoPrice, false,
				null, null, null, null, true, true);

		double netPrice5 = 8;

		double grossPrice5 = netPrice5 * _factor;

		BigDecimal price5 = BigDecimal.valueOf(grossPrice5);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePromoEntry.getCommercePriceEntryId(), "", price5,
			BigDecimal.valueOf(5), false, true,
			BigDecimal.valueOf(RandomTestUtil.randomInt()),
			BigDecimal.valueOf(RandomTestUtil.randomInt()),
			BigDecimal.valueOf(RandomTestUtil.randomInt()),
			BigDecimal.valueOf(RandomTestUtil.randomInt()), true, true);

		double netPrice10 = 5;

		double grossPrice10 = netPrice10 * _factor;

		BigDecimal price10 = BigDecimal.valueOf(grossPrice10);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePromoEntry.getCommercePriceEntryId(), "", price10,
			BigDecimal.TEN, false, false,
			BigDecimal.valueOf(RandomTestUtil.randomInt()),
			BigDecimal.valueOf(RandomTestUtil.randomInt()),
			BigDecimal.valueOf(RandomTestUtil.randomInt()),
			BigDecimal.valueOf(RandomTestUtil.randomInt()), true, true);

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

		BigDecimal expectedPromoPrice = BigDecimal.valueOf(netPrice1);

		finalPrice = finalPrice.setScale(
			expectedPromoPrice.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedPromoPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		CPDefinitionLocalServiceUtil.deleteCPDefinition(cpDefinition);
	}

	@Test
	public void testUseBulkTierPriceEntry() throws Exception {
		frutillaRule.scenario(
			"When a tier price entry is configured then the price changes " +
				"depending on the product purchased quantity"
		).given(
			"A bulk tier price entry is created"
		).when(
			"I search for the final price of a product given its purchased " +
				"quantity"
		).then(
			"The product price is correctly calculated"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), null,
				ServiceContextTestUtil.getServiceContext());

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), true, 0.0);

		commercePriceList1.setNetPrice(false);

		commercePriceList1 =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePriceList1);

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(_commerceTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		double netPrice1 = 20;

		double grossPrice1 = netPrice1 * _factor;

		BigDecimal price1 = BigDecimal.valueOf(grossPrice1);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList1.getCommercePriceListId(), price1);

		double netPrice5 = 18;

		double grossPrice5 = netPrice5 * _factor;

		BigDecimal price5 = BigDecimal.valueOf(grossPrice5);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price5,
			BigDecimal.valueOf(5), true);

		double netPrice10 = 15;

		double grossPrice10 = netPrice10 * _factor;

		BigDecimal price10 = BigDecimal.valueOf(grossPrice10);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price10,
			BigDecimal.TEN, true);

		double netPrice15 = 10;

		double grossPrice15 = netPrice15 * _factor;

		BigDecimal price15 = BigDecimal.valueOf(grossPrice15);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price15,
			BigDecimal.valueOf(15), true);

		double netPrice20 = 5;

		double grossPrice20 = netPrice20 * _factor;

		BigDecimal price20 = BigDecimal.valueOf(grossPrice20);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price20,
			BigDecimal.valueOf(20), true);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceMoney commerceMoney1 = commerceProductPrice.getFinalPrice();

		BigDecimal commercePrice1 = commerceMoney1.getPrice();

		BigDecimal expectedNetPrice1 = BigDecimal.valueOf(netPrice1);

		commercePrice1 = commercePrice1.setScale(
			expectedNetPrice1.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedNetPrice1.stripTrailingZeros(),
			commercePrice1.stripTrailingZeros());

		BigDecimal quantity = BigDecimal.TEN;

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), quantity, StringPool.BLANK,
				commerceContext);

		CommerceMoney commerceMoney10 = commerceProductPrice.getFinalPrice();

		BigDecimal commercePrice10 = commerceMoney10.getPrice();

		BigDecimal expectedNetPrice10 = BigDecimal.valueOf(netPrice10);

		BigDecimal expectedPrice10 = expectedNetPrice10.multiply(quantity);

		commercePrice10 = commercePrice10.setScale(
			expectedPrice10.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedPrice10.stripTrailingZeros(),
			commercePrice10.stripTrailingZeros());

		quantity = BigDecimal.valueOf(18);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), quantity, StringPool.BLANK,
				commerceContext);

		CommerceMoney commerceMoney15 = commerceProductPrice.getFinalPrice();

		BigDecimal commercePrice15 = commerceMoney15.getPrice();

		BigDecimal expectedNetPrice15 = BigDecimal.valueOf(netPrice15);

		BigDecimal expectedPrice15 = expectedNetPrice15.multiply(quantity);

		commercePrice15 = commercePrice15.setScale(
			expectedPrice15.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedPrice15.stripTrailingZeros(),
			commercePrice15.stripTrailingZeros());

		quantity = BigDecimal.valueOf(25);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), quantity, StringPool.BLANK,
				commerceContext);

		CommerceMoney commerceMoney20 = commerceProductPrice.getFinalPrice();

		BigDecimal commercePrice20 = commerceMoney20.getPrice();

		BigDecimal expectedNetPrice20 = BigDecimal.valueOf(netPrice20);

		BigDecimal expectedPrice20 = expectedNetPrice20.multiply(quantity);

		commercePrice20 = commercePrice20.setScale(
			expectedPrice20.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedPrice20.stripTrailingZeros(),
			commercePrice20.stripTrailingZeros());

		CPDefinitionLocalServiceUtil.deleteCPDefinition(cpDefinition);
	}

	@Test
	public void testUseTierPriceEntryWithPromotion() throws Exception {
		frutillaRule.scenario(
			"When a promotion is configured for a tier price entry is " +
				"configured then the price changes depending on the product " +
					"purchased quantity"
		).given(
			"A tier price entry and a promotion is created for the current " +
				"product"
		).when(
			"I search for the final price of a product given its purchased " +
				"quantity"
		).then(
			"The product price is correctly calculated taking the lowest " +
				"value between promo and unit price"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), null,
				ServiceContextTestUtil.getServiceContext());

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), true, 0.0);

		commercePriceList1.setNetPrice(false);

		commercePriceList1 =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePriceList1);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePriceList1.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext());

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(_commerceTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		double netPrice1 = 20;

		double grossPrice1 = netPrice1 * _factor;

		BigDecimal price1 = BigDecimal.valueOf(grossPrice1);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList1.getCommercePriceListId(), price1);

		double netPrice5 = 18;

		double grossPrice5 = netPrice5 * _factor;

		BigDecimal price5 = BigDecimal.valueOf(grossPrice5);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price5,
			BigDecimal.valueOf(5), false);

		double netPrice10 = 15;

		double grossPrice10 = netPrice10 * _factor;

		BigDecimal price10 = BigDecimal.valueOf(grossPrice10);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price10,
			BigDecimal.TEN, false);

		double netPrice15 = 10;

		double grossPrice15 = netPrice15 * _factor;

		BigDecimal price15 = BigDecimal.valueOf(grossPrice15);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price15,
			BigDecimal.valueOf(15), false);

		double netPrice20 = 5;

		double grossPrice20 = netPrice20 * _factor;

		BigDecimal price20 = BigDecimal.valueOf(grossPrice20);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price20,
			BigDecimal.valueOf(20), false);

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

		BigDecimal expectedPrice = BigDecimal.valueOf(185);

		finalPrice = finalPrice.setScale(
			expectedPrice.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		CommercePriceList commercePromotion =
			CommercePriceListTestUtil.addPromotion(
				commerceCatalog.getGroupId(), 0.0);

		commercePromotion.setNetPrice(false);

		commercePromotion =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePromotion);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePromotion.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext());

		double netPromoPrice = 10;

		double grossPromoPrice = netPromoPrice * _factor;

		BigDecimal promoPrice = BigDecimal.valueOf(grossPromoPrice);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commercePromotion.getCommercePriceListId(), promoPrice);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.TEN, false,
				StringPool.BLANK, commerceContext);

		CommerceMoney finalPromoPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPromoPrice = finalPromoPriceCommerceMoney.getPrice();

		expectedPrice = BigDecimal.valueOf(100);

		finalPromoPrice = finalPromoPrice.setScale(
			expectedPrice.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPromoPrice.stripTrailingZeros());

		CPDefinitionLocalServiceUtil.deleteCPDefinition(cpDefinition);
	}

	@Test
	public void testUseTierPriceEntryWithTierPromotion() throws Exception {
		frutillaRule.scenario(
			"When a tier promotion is configured for a tier price entry is " +
				"configured then the price changes depending on the product " +
					"purchased quantity"
		).given(
			"A tier price entry and a promotion is created for the current " +
				"product"
		).when(
			"I search for the final price of a product given its purchased " +
				"quantity"
		).then(
			"The product price is correctly calculated taking the lowest " +
				"value between promo and unit price"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), null,
				ServiceContextTestUtil.getServiceContext());

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), true, 0.0);

		commercePriceList1.setNetPrice(false);

		commercePriceList1 =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePriceList1);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePriceList1.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext());

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(_commerceTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		double netPrice1 = 20;

		double grossPrice1 = netPrice1 * _factor;

		BigDecimal price1 = BigDecimal.valueOf(grossPrice1);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList1.getCommercePriceListId(), price1);

		double netPrice5 = 18;

		double grossPrice5 = netPrice5 * _factor;

		BigDecimal price5 = BigDecimal.valueOf(grossPrice5);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price5,
			BigDecimal.valueOf(5), false);

		double netPrice10 = 15;

		double grossPrice10 = netPrice10 * _factor;

		BigDecimal price10 = BigDecimal.valueOf(grossPrice10);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price10,
			BigDecimal.TEN, false);

		double netPrice15 = 10;

		double grossPrice15 = netPrice15 * _factor;

		BigDecimal price15 = BigDecimal.valueOf(grossPrice15);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price15,
			BigDecimal.valueOf(15), false);

		double netPrice20 = 5;

		double grossPrice20 = netPrice20 * _factor;

		BigDecimal price20 = BigDecimal.valueOf(grossPrice20);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price20,
			BigDecimal.valueOf(20), false);

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

		BigDecimal expectedPrice = BigDecimal.valueOf(185);

		finalPrice = finalPrice.setScale(
			expectedPrice.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		CommercePriceList commercePromotion =
			CommercePriceListTestUtil.addPromotion(
				commerceCatalog.getGroupId(), 0.0);

		commercePromotion.setNetPrice(false);

		commercePromotion =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePromotion);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePromotion.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext());

		double netPromoPrice1 = 10;

		double grossPromoPrice1 = netPromoPrice1 * _factor;

		BigDecimal promoPrice = BigDecimal.valueOf(grossPromoPrice1);

		CommercePriceEntry commercePromotionEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePromotion.getCommercePriceListId(), promoPrice);

		double netPromoPrice3 = 8;

		double grossPromoPrice3 = netPromoPrice3 * _factor;

		BigDecimal price3 = BigDecimal.valueOf(grossPromoPrice3);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePromotionEntry.getCommercePriceEntryId(), price3,
			BigDecimal.valueOf(3), false);

		double netPromoPrice7 = 5;

		double grossPromoPrice7 = netPromoPrice7 * _factor;

		BigDecimal price7 = BigDecimal.valueOf(grossPromoPrice7);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePromotionEntry.getCommercePriceEntryId(), price7,
			BigDecimal.valueOf(7), false);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.TEN, false,
				StringPool.BLANK, commerceContext);

		CommerceMoney finalPromoPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPromoPrice = finalPromoPriceCommerceMoney.getPrice();

		expectedPrice = BigDecimal.valueOf(72);

		finalPromoPrice = finalPromoPrice.setScale(
			expectedPrice.scale(),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPromoPrice.stripTrailingZeros());

		CPDefinitionLocalServiceUtil.deleteCPDefinition(cpDefinition);
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

	private static User _user;

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommercePriceListAccountRelLocalService
		_commercePriceListAccountRelLocalService;

	@Inject
	private CommercePriceModifierLocalService
		_commercePriceModifierLocalService;

	@Inject
	private CommercePriceModifierRelLocalService
		_commercePriceModifierRelLocalService;

	@Inject
	private CommercePricingClassCPDefinitionRelLocalService
		_commercePricingClassCPDefinitionRelLocalService;

	@Inject
	private CommercePricingClassLocalService _commercePricingClassLocalService;

	@Inject
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	private long _commerceTaxCategoryId;
	private CommerceTaxMethod _commerceTaxMethod;

	@Inject
	private CommerceTaxMethodLocalService _commerceTaxMethodLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	private double _factor;
	private Group _group;
	private double _rate;
	private ServiceContext _serviceContext;

}