/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalServiceUtil;
import com.liferay.commerce.price.list.service.CommercePriceListLocalServiceUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.Sku;
import com.liferay.headless.commerce.admin.pricing.client.problem.Problem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class SkuResourceTest extends BaseSkuResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				testCompany.getCompanyId());

		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			testGroup.getCompanyId(), testGroup.getGroupId(), _user.getUserId(),
			commerceCurrency.getCode());

		_cpInstance =
			CPTestUtil.addCPInstanceFromCatalogWithoutCommercePriceEntry(
				commerceCatalog.getGroupId(), BigDecimal.ONE, BigDecimal.ZERO,
				RandomTestUtil.randomString());

		String unitOfMeasureKey = RandomTestUtil.randomString();

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			CPTestUtil.addCPInstanceUnitOfMeasure(
				testGroup.getGroupId(), _cpInstance.getCPInstanceId(),
				unitOfMeasureKey, BigDecimal.ONE, _cpInstance.getSku());

		CPDefinition cpDefinition = _cpInstance.getCPDefinition();

		_commerceBasePriceListPriceEntry = _addCommercePriceEntry(
			CommercePriceListLocalServiceUtil.
				fetchCatalogBaseCommercePriceListByType(
					_cpInstance.getGroupId(), "price-list"),
			_cpInstance, cpDefinition.getCProductId(), BigDecimal.TEN,
			cpInstanceUnitOfMeasure.getKey());
		_commerceBasePromotionPriceEntry = _addCommercePriceEntry(
			CommercePriceListLocalServiceUtil.
				fetchCatalogBaseCommercePriceListByType(
					_cpInstance.getGroupId(), "promotion"),
			_cpInstance, cpDefinition.getCProductId(), BigDecimal.TEN,
			cpInstanceUnitOfMeasure.getKey());
	}

	@Ignore
	@Override
	@Test
	public void testGetDiscountSkuSku() throws Exception {
		super.testGetDiscountSkuSku();
	}

	@Test
	public void testGetPriceEntryIdSku() throws Exception {
		super.testGetPriceEntryIdSku();

		_testGetPriceEntryIdSkuWithUnitOfMeasures();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetDiscountSkuSku() throws Exception {
		super.testGraphQLGetDiscountSkuSku();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPriceEntryIdSku() throws Exception {
		super.testGraphQLGetPriceEntryIdSku();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"basePrice", "basePriceFormatted", "basePromoPrice",
			"basePromoPriceFormatted", "name"
		};
	}

	@Override
	protected Sku testGetPriceEntryIdSku_addSku() throws Exception {
		return _addSku();
	}

	@Override
	protected Long testGetPriceEntryIdSku_getPriceEntryId() throws Exception {
		return _commerceBasePriceListPriceEntry.getCommercePriceEntryId();
	}

	@Override
	protected Long testGraphQLGetPriceEntryIdSku_getPriceEntryId()
		throws Exception {

		return _commerceBasePriceListPriceEntry.getCommercePriceEntryId();
	}

	@Override
	protected Sku testGraphQLSku_addSku() throws Exception {
		return _addSku();
	}

	private CommercePriceEntry _addCommercePriceEntry(
			CommercePriceList commercePriceList, CPInstance cpInstance,
			long cProductId, BigDecimal price, String uomKey)
		throws Exception {

		return CommercePriceEntryLocalServiceUtil.addCommercePriceEntry(
			StringPool.BLANK, cProductId, cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), price, false,
			BigDecimal.ZERO, uomKey,
			ServiceContextTestUtil.getServiceContext(cpInstance.getGroupId()));
	}

	private Sku _addSku() throws Exception {
		return new Sku() {
			{
				setBasePrice(() -> _getPrice(_commerceBasePriceListPriceEntry));
				setBasePriceFormatted(
					() -> _formatPrice(
						_cpInstance.getCompanyId(),
						_commerceBasePriceListPriceEntry,
						LocaleUtil.getDefault()));
				setBasePromoPrice(
					() -> _getPrice(_commerceBasePromotionPriceEntry));
				setBasePromoPriceFormatted(
					() -> _formatPrice(
						_cpInstance.getCompanyId(),
						_commerceBasePromotionPriceEntry,
						LocaleUtil.getDefault()));
				setId(_cpInstance::getCPInstanceId);
				setName(_cpInstance::getSku);
			}
		};
	}

	private String _formatPrice(
			long companyId, CommercePriceEntry priceEntry, Locale locale)
		throws Exception {

		if (priceEntry == null) {
			CommerceCurrency commerceCurrency =
				_commerceCurrencyLocalService.fetchPrimaryCommerceCurrency(
					companyId);

			return _commercePriceFormatter.format(
				commerceCurrency, BigDecimal.ZERO, locale);
		}

		CommercePriceList commercePriceList = priceEntry.getCommercePriceList();

		return _commercePriceFormatter.format(
			commercePriceList.getCommerceCurrency(), priceEntry.getPrice(),
			locale);
	}

	private double _getPrice(CommercePriceEntry commercePriceEntry) {
		if (commercePriceEntry == null) {
			return 0D;
		}

		BigDecimal price = commercePriceEntry.getPrice();

		return price.doubleValue();
	}

	private void _testGetPriceEntryIdSkuWithUnitOfMeasures() throws Exception {
		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure1 =
			CPTestUtil.addCPInstanceUnitOfMeasure(
				testGroup.getGroupId(), _cpInstance.getCPInstanceId(),
				RandomTestUtil.randomString(), BigDecimal.ONE,
				_cpInstance.getSku());

		CommercePriceEntry commerceBasePriceListPriceEntry1 =
			_updateCommercePriceEntry(
				BigDecimal.valueOf(25),
				CommercePriceListConstants.TYPE_PRICE_LIST,
				cpInstanceUnitOfMeasure1.getKey());
		CommercePriceEntry commerceBasePromoPriceListPriceEntry1 =
			_updateCommercePriceEntry(
				BigDecimal.valueOf(15),
				CommercePriceListConstants.TYPE_PROMOTION,
				cpInstanceUnitOfMeasure1.getKey());

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure2 =
			CPTestUtil.addCPInstanceUnitOfMeasure(
				testGroup.getGroupId(), _cpInstance.getCPInstanceId(),
				RandomTestUtil.randomString(), BigDecimal.ONE,
				_cpInstance.getSku());

		CommercePriceEntry commerceBasePriceListPriceEntry2 =
			_updateCommercePriceEntry(
				BigDecimal.valueOf(20),
				CommercePriceListConstants.TYPE_PRICE_LIST,
				cpInstanceUnitOfMeasure2.getKey());
		CommercePriceEntry commerceBasePromoPriceListPriceEntry2 =
			_updateCommercePriceEntry(
				BigDecimal.valueOf(10),
				CommercePriceListConstants.TYPE_PROMOTION,
				cpInstanceUnitOfMeasure2.getKey());

		BigDecimal expectedSkuBasePrice1 =
			commerceBasePriceListPriceEntry1.getPrice();

		Sku sku1 = skuResource.getPriceEntryIdSku(
			commerceBasePriceListPriceEntry1.getCommercePriceEntryId());

		BigDecimal skuBasePrice1 = BigDecimal.valueOf(sku1.getBasePrice());

		Assert.assertEquals(
			expectedSkuBasePrice1.stripTrailingZeros(),
			skuBasePrice1.stripTrailingZeros());

		BigDecimal expectedSkuBasePrice2 =
			commerceBasePriceListPriceEntry2.getPrice();

		Sku sku2 = skuResource.getPriceEntryIdSku(
			commerceBasePriceListPriceEntry2.getCommercePriceEntryId());

		BigDecimal skuBasePrice2 = BigDecimal.valueOf(sku2.getBasePrice());

		Assert.assertEquals(
			expectedSkuBasePrice2.stripTrailingZeros(),
			skuBasePrice2.stripTrailingZeros());

		BigDecimal expectedSkuBasePromoPrice1 =
			commerceBasePromoPriceListPriceEntry1.getPrice();
		BigDecimal skuBasePromoPrice1 = BigDecimal.valueOf(
			sku1.getBasePromoPrice());

		Assert.assertEquals(
			expectedSkuBasePromoPrice1.stripTrailingZeros(),
			skuBasePromoPrice1.stripTrailingZeros());

		BigDecimal expectedSkuBasePromoPrice2 =
			commerceBasePromoPriceListPriceEntry2.getPrice();
		BigDecimal skuBasePromoPrice2 = BigDecimal.valueOf(
			sku2.getBasePromoPrice());

		Assert.assertEquals(
			expectedSkuBasePromoPrice2.stripTrailingZeros(),
			skuBasePromoPrice2.stripTrailingZeros());

		_cpInstanceUnitOfMeasureLocalService.deleteCPInstanceUnitOfMeasure(
			cpInstanceUnitOfMeasure1);

		try {
			skuResource.getPriceEntryIdSku(
				commerceBasePriceListPriceEntry1.getCommercePriceEntryId());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}

		sku2 = skuResource.getPriceEntryIdSku(
			commerceBasePriceListPriceEntry2.getCommercePriceEntryId());

		skuBasePrice2 = BigDecimal.valueOf(sku2.getBasePrice());

		Assert.assertEquals(
			expectedSkuBasePrice2.stripTrailingZeros(),
			skuBasePrice2.stripTrailingZeros());

		Assert.assertEquals(
			expectedSkuBasePromoPrice2.stripTrailingZeros(),
			skuBasePromoPrice2.stripTrailingZeros());
	}

	private CommercePriceEntry _updateCommercePriceEntry(
		BigDecimal price, String priceListType, String uomKey) {

		CommercePriceEntry commerceBasePriceListPriceEntry =
			_commercePriceEntryLocalService.getInstanceBaseCommercePriceEntry(
				_cpInstance.getCPInstanceUuid(), priceListType, uomKey);

		commerceBasePriceListPriceEntry.setPrice(price);

		return _commercePriceEntryLocalService.updateCommercePriceEntry(
			commerceBasePriceListPriceEntry);
	}

	@DeleteAfterTestRun
	private CommercePriceEntry _commerceBasePriceListPriceEntry;

	@DeleteAfterTestRun
	private CommercePriceEntry _commerceBasePromotionPriceEntry;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Inject
	private CommercePriceFormatter _commercePriceFormatter;

	@DeleteAfterTestRun
	private CPInstance _cpInstance;

	@Inject
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@DeleteAfterTestRun
	private User _user;

}