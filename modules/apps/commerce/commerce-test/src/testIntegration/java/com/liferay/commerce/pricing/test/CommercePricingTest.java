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
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.price.CommerceOrderItemPrice;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListAccountRelLocalService;
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
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
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
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Calendar;

import org.frutilla.FrutillaRule;

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
public class CommercePricingTest {

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

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());
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

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		BigDecimal price1 = BigDecimal.valueOf(20);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), price1, false, null,
				null, null, null, true, true);

		BigDecimal price5 = BigDecimal.valueOf(15);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), "", price5,
			BigDecimal.valueOf(5), true, false, null, null, null, null, true,
			true);

		BigDecimal price10 = BigDecimal.valueOf(10);

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

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		Assert.assertEquals(
			price1.stripTrailingZeros(), finalPrice.stripTrailingZeros());

		quantity = BigDecimal.valueOf(100);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), quantity, StringPool.BLANK,
				commerceContext);

		finalPriceCommerceMoney = commerceProductPrice.getFinalPrice();

		finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = price10.multiply(quantity);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
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
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext());

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePriceList1.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext());

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		BigDecimal price1 = BigDecimal.valueOf(20);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commercePriceList1.getCommercePriceListId(), price1, false, null,
			null, null, null, true, true);

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

		BigDecimal expectedPrice = BigDecimal.valueOf(200);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	@Test
	public void testOrderItemPriceWithPromoHigherThanTier() throws Exception {
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

		CommercePriceList promotionalCommercePriceList =
			CommercePriceListTestUtil.addPromotion(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		BigDecimal price = BigDecimal.valueOf(100);
		BigDecimal promoPrice = BigDecimal.valueOf(80);
		BigDecimal tierPrice = BigDecimal.valueOf(50);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), price, false, null,
				null, null, null, true, true);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), StringPool.BLANK,
			tierPrice, BigDecimal.valueOf(5), false, false, null, null, null,
			null, true, true);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			promotionalCommercePriceList.getCommercePriceListId(), promoPrice,
			false, null, null, null, null, true, true);

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			commerceChannel.getCommerceChannelId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstance.getSku(), StringPool.BLANK);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), commerceChannel.getGroupId(), _commerceCurrency);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			commerceOrder);

		CommerceOrderItem commerceOrderItem =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.TEN, commerceContext);

		CommerceOrderItemPrice commerceOrderItemPrice =
			_commerceOrderPriceCalculation.getCommerceOrderItemPrice(
				_commerceCurrency, commerceOrderItem);

		Assert.assertNull(commerceOrderItemPrice.getPromoPrice());

		_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
	}

	@Test
	public void testOrderItemPriceWithPromoHigherThanUnit() throws Exception {
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

		CommercePriceList promotionalCommercePriceList =
			CommercePriceListTestUtil.addPromotion(
				commerceCatalog.getGroupId(), 0.0);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		BigDecimal price = BigDecimal.valueOf(100);
		BigDecimal promoPrice = BigDecimal.valueOf(500);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), price, false, null,
			null, null, null, true, true);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			promotionalCommercePriceList.getCommercePriceListId(), promoPrice,
			false, null, null, null, null, true, true);

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			commerceChannel.getCommerceChannelId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstance.getSku(), StringPool.BLANK);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), commerceChannel.getGroupId(), _commerceCurrency);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			commerceOrder);

		CommerceOrderItem commerceOrderItem =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.ONE, commerceContext);

		CommerceOrderItemPrice commerceOrderItemPrice =
			_commerceOrderPriceCalculation.getCommerceOrderItemPrice(
				_commerceCurrency, commerceOrderItem);

		Assert.assertNull(commerceOrderItemPrice.getPromoPrice());

		_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
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

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		BigDecimal price = BigDecimal.valueOf(RandomTestUtil.randomDouble());

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

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		Assert.assertEquals(
			price.stripTrailingZeros(), finalPrice.stripTrailingZeros());
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

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		BigDecimal price = BigDecimal.valueOf(100);
		BigDecimal promoPrice = BigDecimal.valueOf(20);

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

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		Assert.assertEquals(
			promoPrice.stripTrailingZeros(), finalPrice.stripTrailingZeros());
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
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext());

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePriceList1.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext());

		CommercePriceList basePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), true, 0.0);

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

		_commercePricingClassCPDefinitionRelLocalService.
			addCommercePricingClassCPDefinitionRel(
				commercePricingClass.getCommercePricingClassId(),
				cpDefinition.getCPDefinitionId(),
				ServiceContextTestUtil.getServiceContext());

		BigDecimal price1 = BigDecimal.valueOf(20.0);

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

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = BigDecimal.valueOf(180);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
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
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext());

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePriceList1.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext());

		CommercePriceList basePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), true, 0.0);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		BigDecimal price1 = BigDecimal.valueOf(20.0);

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			basePriceList.getCommercePriceListId(), price1);

		CommercePriceModifier commercePriceModifier = _addCommercePriceModifier(
			commercePriceList1.getGroupId(),
			CommercePriceModifierConstants.TARGET_PRODUCTS,
			commercePriceList1.getCommercePriceListId(),
			CommercePriceModifierConstants.MODIFIER_TYPE_FIXED_AMOUNT,
			BigDecimal.valueOf(-10), true);

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

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = BigDecimal.valueOf(100);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
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

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		BigDecimal price1 = BigDecimal.valueOf(50);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), price1, false, null,
				null, null, null, true, true);

		BigDecimal price5 = BigDecimal.valueOf(40);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), "", price5,
			BigDecimal.valueOf(5), false, false, null, null, null, null, true,
			true);

		BigDecimal price10 = BigDecimal.valueOf(30);

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

		Assert.assertEquals(
			price1.setScale(_SCALE, RoundingMode.FLOOR),
			finalPrice.setScale(_SCALE, RoundingMode.FLOOR));

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.valueOf(100),
				StringPool.BLANK, commerceContext);

		finalPriceCommerceMoney = commerceProductPrice.getFinalPrice();

		finalPrice = finalPriceCommerceMoney.getPrice();

		BigDecimal expectedPrice = BigDecimal.valueOf(3130);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
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

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		BigDecimal price1 = BigDecimal.valueOf(20);
		BigDecimal promoPrice = BigDecimal.valueOf(15);

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

		BigDecimal price5 = BigDecimal.valueOf(8);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePromoEntry.getCommercePriceEntryId(), "", price5,
			BigDecimal.valueOf(5), false, true,
			BigDecimal.valueOf(RandomTestUtil.randomInt()),
			BigDecimal.valueOf(RandomTestUtil.randomInt()),
			BigDecimal.valueOf(RandomTestUtil.randomInt()),
			BigDecimal.valueOf(RandomTestUtil.randomInt()), true, true);

		BigDecimal price10 = BigDecimal.valueOf(5);

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

		Assert.assertEquals(
			promoPrice.stripTrailingZeros(), finalPrice.stripTrailingZeros());
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
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext());

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), true, 0.0);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		BigDecimal price1 = BigDecimal.valueOf(20);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList1.getCommercePriceListId(), price1);

		BigDecimal price5 = BigDecimal.valueOf(18);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price5,
			BigDecimal.valueOf(5), true);

		BigDecimal price10 = BigDecimal.valueOf(15);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price10,
			BigDecimal.TEN, true);

		BigDecimal price15 = BigDecimal.valueOf(10);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price15,
			BigDecimal.valueOf(15), true);

		BigDecimal price20 = BigDecimal.valueOf(5);

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

		Assert.assertEquals(0, price1.compareTo(commerceMoney1.getPrice()));

		BigDecimal quantity = BigDecimal.TEN;

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), quantity, StringPool.BLANK,
				commerceContext);

		CommerceMoney commerceMoney10 = commerceProductPrice.getFinalPrice();

		BigDecimal commercePrice10 = commerceMoney10.getPrice();

		BigDecimal expectedPrice10 = price10.multiply(quantity);

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

		BigDecimal expectedPrice15 = price15.multiply(quantity);

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

		BigDecimal expectedPrice20 = price20.multiply(quantity);

		Assert.assertEquals(
			expectedPrice20.stripTrailingZeros(),
			commercePrice20.stripTrailingZeros());
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
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext());

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), true, 0.0);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePriceList1.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext());

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		BigDecimal price1 = BigDecimal.valueOf(20);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList1.getCommercePriceListId(), price1);

		BigDecimal price5 = BigDecimal.valueOf(18);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price5,
			BigDecimal.valueOf(5), false);

		BigDecimal price10 = BigDecimal.valueOf(15);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price10,
			BigDecimal.TEN, false);

		BigDecimal price15 = BigDecimal.valueOf(10);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price15,
			BigDecimal.valueOf(15), false);

		BigDecimal price20 = BigDecimal.valueOf(5);

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

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		CommercePriceList commercePromotion =
			CommercePriceListTestUtil.addPromotion(
				commerceCatalog.getGroupId(), 0.0);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePromotion.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext());

		BigDecimal promoPrice = BigDecimal.valueOf(10);

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

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPromoPrice.stripTrailingZeros());
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
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext());

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), true, 0.0);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePriceList1.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext());

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		BigDecimal price1 = BigDecimal.valueOf(20);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList1.getCommercePriceListId(), price1);

		BigDecimal price5 = BigDecimal.valueOf(18);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price5,
			BigDecimal.valueOf(5), false);

		BigDecimal price10 = BigDecimal.valueOf(15);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price10,
			BigDecimal.valueOf(10), false);

		BigDecimal price15 = BigDecimal.valueOf(10);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), price15,
			BigDecimal.valueOf(15), false);

		BigDecimal price20 = BigDecimal.valueOf(5);

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

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());

		CommercePriceList commercePromotion =
			CommercePriceListTestUtil.addPromotion(
				commerceCatalog.getGroupId(), 0.0);

		_commercePriceListAccountRelLocalService.addCommercePriceListAccountRel(
			_user.getUserId(), commercePromotion.getCommercePriceListId(),
			_accountEntry.getAccountEntryId(), 0,
			ServiceContextTestUtil.getServiceContext());

		BigDecimal promoPrice = BigDecimal.valueOf(10);

		CommercePriceEntry commercePromotionEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				"", cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePromotion.getCommercePriceListId(), promoPrice);

		BigDecimal price3 = BigDecimal.valueOf(8);

		CommercePriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePromotionEntry.getCommercePriceEntryId(), price3,
			BigDecimal.valueOf(3), false);

		BigDecimal price7 = BigDecimal.valueOf(5);

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

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPromoPrice.stripTrailingZeros());
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

	private static final int _SCALE = 10;

	private static User _user;

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

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

	private Group _group;
	private ServiceContext _serviceContext;

}