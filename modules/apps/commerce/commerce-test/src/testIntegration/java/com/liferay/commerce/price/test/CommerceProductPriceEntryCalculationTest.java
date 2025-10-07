/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.discount.test.util.CommerceDiscountTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Calendar;
import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CommerceProductPriceEntryCalculationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_company.getCompanyId());

		_commerceCatalog = CommerceCatalogLocalServiceUtil.addCommerceCatalog(
			null, RandomTestUtil.randomString(), _commerceCurrency.getCode(),
			LocaleUtil.US.getDisplayLanguage(),
			ServiceContextTestUtil.getServiceContext(_company.getGroupId()));

		_commercePriceList =
			_commercePriceListLocalService.
				getCatalogBaseCommercePriceListByType(
					_commerceCatalog.getGroupId(),
					CommercePriceListConstants.TYPE_PRICE_LIST);

		_group = GroupTestUtil.addGroup();

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_user = UserTestUtil.addUser(_company);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_company.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, _serviceContext);
	}

	@Test
	public void testPriceEntryDefaultDiscountOverride() throws Exception {
		frutillaRule.scenario(
			"The price of a product is calculated correctly"
		).given(
			"Product discounts exist"
		).when(
			"The default discount is set to override for a account"
		).then(
			"The correct price is returned"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.addCommercePriceEntry(
				RandomTestUtil.randomString(), cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				_commercePriceList.getCommercePriceListId(), new BigDecimal(20),
				false, BigDecimal.ZERO, StringPool.BLANK, _serviceContext);

		Calendar calendar = Calendar.getInstance();

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.addCommerceOrderType(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true,
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), 0, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), true,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceOrder commerceOrder = CommerceTestUtil.addB2BCommerceOrder(
			_group.getGroupId(), _user.getUserId(),
			_accountEntry.getAccountEntryId(),
			_commerceCurrency.getCommerceCurrencyId());

		commerceOrder.setCommerceOrderTypeId(
			commerceOrderType.getCommerceOrderTypeId());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			commerceOrder);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, true,
				StringPool.BLANK, commerceContext);

		CommerceMoney commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(20), commerceMoney.getPrice()));

		CommerceDiscount commerceDiscount1 =
			CommerceDiscountTestUtil.addAccountCommerceDiscount(
				_user.getGroupId(), _accountEntry.getAccountEntryId(),
				CommerceDiscountConstants.LEVEL_L1,
				cpDefinition.getCPDefinitionId());

		commerceDiscount1.setUsePercentage(true);
		commerceDiscount1.setLevel1(BigDecimal.TEN);

		_commerceDiscountLocalService.updateCommerceDiscount(commerceDiscount1);

		CommerceDiscount commerceDiscount2 =
			CommerceDiscountTestUtil.addChannelCommerceDiscount(
				_user.getGroupId(), _commerceChannel.getCommerceChannelId(),
				CommerceDiscountConstants.LEVEL_L1,
				cpDefinition.getCPDefinitionId());

		commerceDiscount2.setUsePercentage(true);
		commerceDiscount2.setLevel1(BigDecimal.valueOf(20));

		commerceDiscount2 =
			_commerceDiscountLocalService.updateCommerceDiscount(
				commerceDiscount2);

		CommerceDiscount commerceDiscount3 =
			CommerceDiscountTestUtil.addOrderTypeCommerceDiscount(
				_user.getGroupId(), commerceOrderType.getCommerceOrderTypeId(),
				CommerceDiscountConstants.LEVEL_L1,
				cpDefinition.getCPDefinitionId(), 0);

		commerceDiscount3.setUsePercentage(true);
		commerceDiscount3.setLevel1(BigDecimal.valueOf(30));

		_commerceDiscountLocalService.updateCommerceDiscount(commerceDiscount3);

		_commerceChannelAccountEntryRelLocalService.
			addCommerceChannelAccountEntryRel(
				_serviceContext.getUserId(), _accountEntry.getAccountEntryId(),
				CommerceDiscount.class.getName(),
				commerceDiscount2.getCommerceDiscountId(),
				_commerceChannel.getCommerceChannelId(), true, 0,
				CommerceChannelAccountEntryRelConstants.TYPE_DISCOUNT);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, true,
				StringPool.BLANK, commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		BigDecimal price = commercePriceEntry.getPrice();

		double discountValue = BigDecimalUtil.multiply(
			price,
			BigDecimalUtil.divide(
				commerceDiscount2.getLevel1(), 100, 2, RoundingMode.HALF_UP));

		Assert.assertEquals(
			BigDecimalUtil.subtract(price, discountValue),
			commerceMoney.getPrice(
			).doubleValue(),
			0);
	}

	@Test
	public void testPriceEntryExpirationDate() throws Exception {
		frutillaRule.scenario(
			"The price of a product is calculated correctly"
		).given(
			"A product with a price applied"
		).when(
			"The price expired"
		).then(
			"The correct price is returned"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		_commercePriceEntryLocalService.addCommercePriceEntry(
			RandomTestUtil.randomString(), cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(),
			_commercePriceList.getCommercePriceListId(), new BigDecimal(20),
			false, BigDecimal.ZERO, StringPool.BLANK, _serviceContext);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, _commerceCurrency, _commerceChannel, _user, _group,
			null);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, true,
				StringPool.BLANK, commerceContext);

		CommerceMoney commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(20), commerceMoney.getPrice()));

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.addCommercePriceEntry(
				RandomTestUtil.randomString(), cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				_commercePriceList.getCommercePriceListId(), new BigDecimal(50),
				false, BigDecimal.ZERO, StringPool.BLANK, _serviceContext);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, true,
				StringPool.BLANK, commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(50), commerceMoney.getPrice()));

		commercePriceEntry.setExpirationDate(RandomTestUtil.nextDate());

		_commercePriceEntryLocalService.updateCommercePriceEntry(
			commercePriceEntry);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, true,
				StringPool.BLANK, commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(50), commerceMoney.getPrice()));

		UnsafeRunnable<Exception> unsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, true,
				StringPool.BLANK, commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(20), commerceMoney.getPrice()));
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private CommerceCatalog _commerceCatalog;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	private CommercePriceList _commercePriceList;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.commerce.price.list.internal.scheduler.CheckCommercePriceEntrySchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _schedulerJobConfiguration;

	private ServiceContext _serviceContext;
	private User _user;

}