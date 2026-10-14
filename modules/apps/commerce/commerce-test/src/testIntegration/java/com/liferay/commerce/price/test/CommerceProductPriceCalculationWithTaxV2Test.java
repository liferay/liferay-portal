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
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.CommerceProductPriceRequest;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalServiceUtil;
import com.liferay.commerce.price.list.test.util.CommercePriceEntryTestUtil;
import com.liferay.commerce.price.list.test.util.CommercePriceListTestUtil;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.test.util.CommerceTaxTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
public class CommerceProductPriceCalculationWithTaxV2Test {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

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

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_commerceTaxMethod = CommerceTaxTestUtil.addByAddressCommerceTaxMethod(
			_user.getUserId(), _commerceChannel.getGroupId(), true);
	}

	@After
	public void tearDown() throws Exception {
		List<CommercePriceList> commercePriceLists =
			_commercePriceListLocalService.getCommercePriceLists(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CommercePriceList commercePriceList : commercePriceLists) {
			if (commercePriceList.getCompanyId() != _group.getCompanyId()) {
				continue;
			}

			_commercePriceListLocalService.forceDeleteCommercePriceList(
				commercePriceList);
		}
	}

	@Test
	public void testGetGrossProductPriceFromGrossPriceList() throws Exception {
		long cpTaxCategoryId = CommerceTaxTestUtil.addTaxCategoryId(
			_user.getGroupId());

		double rate = Double.valueOf(RandomTestUtil.randomInt(0, 40));

		CommerceTaxTestUtil.setCommerceMethodTaxRate(
			_user.getUserId(), _commerceChannel.getGroupId(), cpTaxCategoryId,
			_commerceTaxMethod.getCommerceTaxMethodId(), rate);

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

		cpDefinition.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		BigDecimal price = new BigDecimal("111");

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), price);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		BigDecimal quantity = BigDecimal.ONE;

		_assertCommerceProductPrice(
			quantity, rate, price, true,
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					true, commerceContext, cpInstance.getCPInstanceId(),
					quantity, false, StringPool.BLANK)),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));
	}

	@Test
	public void testGetGrossProductPriceFromNetPriceList() throws Exception {
		CommerceChannel commerceTaxIncludedChannel =
			CommerceTestUtil.addCommerceChannel(
				_group.getGroupId(), _commerceCurrency.getCode());

		commerceTaxIncludedChannel =
			CommerceChannelLocalServiceUtil.updateCommerceChannel(
				commerceTaxIncludedChannel.getCommerceChannelId(),
				commerceTaxIncludedChannel.getAccountEntryId(),
				commerceTaxIncludedChannel.getSiteGroupId(),
				commerceTaxIncludedChannel.getName(),
				commerceTaxIncludedChannel.getType(),
				commerceTaxIncludedChannel.getTypeSettingsUnicodeProperties(),
				commerceTaxIncludedChannel.getCommerceCurrencyCode(),
				CommercePricingConstants.TAX_INCLUDED_IN_PRICE,
				commerceTaxIncludedChannel.isDiscountsTargetNetPrice());

		long cpTaxCategoryId = CommerceTaxTestUtil.addTaxCategoryId(
			_user.getGroupId());

		CommerceTaxMethod commerceTaxMethod =
			CommerceTaxTestUtil.addByAddressCommerceTaxMethod(
				_user.getUserId(), commerceTaxIncludedChannel.getGroupId(),
				true);

		double rate = Double.valueOf(RandomTestUtil.randomInt(0, 40));

		CommerceTaxTestUtil.setCommerceMethodTaxRate(
			_user.getUserId(), commerceTaxIncludedChannel.getGroupId(),
			cpTaxCategoryId, commerceTaxMethod.getCommerceTaxMethodId(), rate);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), 0.0);

		commercePriceList.setNetPrice(true);

		commercePriceList =
			CommercePriceListLocalServiceUtil.updateCommercePriceList(
				commercePriceList);

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition.setTaxExempt(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		BigDecimal price = new BigDecimal("111");

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			"", cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), price);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, commerceTaxIncludedChannel, _user,
			_group, null);

		BigDecimal quantity = BigDecimal.ONE;

		_assertCommerceProductPrice(
			quantity, rate, price, false,
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_createCommerceProductPriceRequest(
					true, commerceContext, cpInstance.getCPInstanceId(),
					quantity, false, StringPool.BLANK)),
			RoundingMode.valueOf(_commerceCurrency.getRoundingMode()));
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private void _assertCommerceProductPrice(
		BigDecimal quantity, double rate, BigDecimal price, boolean includeTax,
		CommerceProductPrice commerceProductPrice, RoundingMode roundingMode) {

		BigDecimal taxRate = BigDecimal.valueOf(rate);

		CommerceMoney unitPriceCommerceMoney =
			commerceProductPrice.getUnitPrice();

		BigDecimal unitPrice = unitPriceCommerceMoney.getPrice();

		CommerceMoney unitPriceWithTaxAmountCommerceMoney =
			commerceProductPrice.getUnitPriceWithTaxAmount();

		Assert.assertEquals(
			unitPriceCommerceMoney.isEmpty(),
			unitPriceWithTaxAmountCommerceMoney.isEmpty());

		BigDecimal unitPriceWithTaxAmount =
			unitPriceWithTaxAmountCommerceMoney.getPrice();

		BigDecimal expectedUnitPrice =
			CommerceTaxTestUtil.getPriceWithTaxAmount(
				unitPrice, taxRate, roundingMode);

		Assert.assertEquals(
			expectedUnitPrice.stripTrailingZeros(),
			unitPriceWithTaxAmount.stripTrailingZeros());

		CommerceMoney promoPriceCommerceMoney =
			commerceProductPrice.getUnitPromoPrice();

		CommerceMoney promoPriceWithTaxAmountCommerceMoney =
			commerceProductPrice.getUnitPromoPriceWithTaxAmount();

		Assert.assertEquals(
			promoPriceCommerceMoney.isEmpty(),
			promoPriceWithTaxAmountCommerceMoney.isEmpty());

		if (!promoPriceCommerceMoney.isEmpty()) {
			BigDecimal promoPriceWithTaxAmount =
				promoPriceWithTaxAmountCommerceMoney.getPrice();

			BigDecimal promoPrice = promoPriceCommerceMoney.getPrice();

			BigDecimal expectedPromoPrice =
				CommerceTaxTestUtil.getPriceWithTaxAmount(
					promoPrice, taxRate, roundingMode);

			Assert.assertEquals(
				expectedPromoPrice.stripTrailingZeros(),
				promoPriceWithTaxAmount.stripTrailingZeros());
		}

		CommerceMoney finaPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finaPriceCommerceMoney.getPrice();

		CommerceMoney finaPriceWithTaxAmountCommerceMoney =
			commerceProductPrice.getFinalPriceWithTaxAmount();

		BigDecimal finalPriceWithTaxAmount =
			finaPriceWithTaxAmountCommerceMoney.getPrice();

		BigDecimal expectedFinalPrice =
			CommerceTaxTestUtil.getPriceWithTaxAmount(
				finalPrice, taxRate, roundingMode);

		Assert.assertEquals(
			expectedFinalPrice.stripTrailingZeros(),
			finalPriceWithTaxAmount.stripTrailingZeros());

		BigDecimal expectedPrice = price;

		if (includeTax) {
			expectedPrice = CommerceTaxTestUtil.getPriceWithoutTaxAmount(
				price, taxRate, roundingMode);
		}

		expectedPrice = expectedPrice.multiply(quantity);

		Assert.assertEquals(
			expectedPrice.stripTrailingZeros(),
			finalPrice.stripTrailingZeros());
	}

	private CommerceProductPriceRequest _createCommerceProductPriceRequest(
		boolean calculateTax, CommerceContext commerceContext,
		long cpInstanceId, BigDecimal quantity, boolean secure,
		String unitOfMeasureKey) {

		CommerceProductPriceRequest commerceProductPriceRequest =
			new CommerceProductPriceRequest();

		commerceProductPriceRequest.setCalculateTax(calculateTax);
		commerceProductPriceRequest.setCommerceContext(commerceContext);
		commerceProductPriceRequest.setCpInstanceId(cpInstanceId);
		commerceProductPriceRequest.setQuantity(quantity);
		commerceProductPriceRequest.setSecure(secure);
		commerceProductPriceRequest.setUnitOfMeasureKey(unitOfMeasureKey);

		return commerceProductPriceRequest;
	}

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
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@DeleteAfterTestRun
	private CommerceTaxMethod _commerceTaxMethod;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	private Group _group;
	private ServiceContext _serviceContext;
	private User _user;

}