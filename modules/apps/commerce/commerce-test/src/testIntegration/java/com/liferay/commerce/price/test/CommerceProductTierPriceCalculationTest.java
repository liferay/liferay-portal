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
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
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
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

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
public class CommerceProductTierPriceCalculationTest {

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
	public void testBulkPriceExpirationDateWithCPInstanceUnitOfMeasure()
		throws Exception {

		frutillaRule.scenario(
			"The price of a product with a unit of measure is calculated " +
				"correctly"
		).given(
			"A product with a unit of measure"
		).and(
			"A bulk price applied"
		).when(
			"The bulk price expired"
		).then(
			"The correct price for the unit of measure is returned"
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

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), cpInstance.getCPInstanceId(), true,
				BigDecimal.ONE, RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				2, BigDecimal.ZERO, false, 0.0, BigDecimal.ONE,
				cpInstance.getSku());

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.addCommercePriceEntry(
				RandomTestUtil.randomString(), cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				_commercePriceList.getCommercePriceListId(), new BigDecimal(50),
				false, BigDecimal.ZERO, cpInstanceUnitOfMeasure.getKey(),
				_serviceContext);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(),
				cpInstanceUnitOfMeasure.getIncrementalOrderQuantity(), true,
				cpInstanceUnitOfMeasure.getKey(), commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(50), commerceMoney.getPrice()));

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(200), true,
				cpInstanceUnitOfMeasure.getKey(), commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(10000), commerceMoney.getPrice()));

		CommerceTierPriceEntry commerceTierPriceEntry =
			_commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
				commercePriceEntry.getCommercePriceEntryId(), BigDecimal.TEN,
				BigDecimal.ZERO, true, new BigDecimal(100), _serviceContext);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(200), true,
				cpInstanceUnitOfMeasure.getKey(), commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(2000), commerceMoney.getPrice()));

		commerceTierPriceEntry.setExpirationDate(RandomTestUtil.nextDate());

		_commerceTierPriceEntryLocalService.updateCommerceTierPriceEntry(
			commerceTierPriceEntry);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(200), true,
				cpInstanceUnitOfMeasure.getKey(), commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(2000), commerceMoney.getPrice()));

		UnsafeRunnable<Exception> unsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(200), true,
				cpInstanceUnitOfMeasure.getKey(), commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(10000), commerceMoney.getPrice()));
	}

	@Test
	public void testCanAssertExpirationDateForBulkPricing() throws Exception {
		frutillaRule.scenario(
			"The price of a product is calculated correctly"
		).given(
			"A product"
		).and(
			"A bulk price applied"
		).when(
			"The bulk price expired"
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

		CommerceTierPriceEntry commerceTierPriceEntry =
			_commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
				commercePriceEntry.getCommercePriceEntryId(), BigDecimal.TEN,
				BigDecimal.ZERO, true, new BigDecimal(10), _serviceContext);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(10), true,
				StringPool.BLANK, commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(100), commerceMoney.getPrice()));

		commerceTierPriceEntry.setExpirationDate(RandomTestUtil.nextDate());

		_commerceTierPriceEntryLocalService.updateCommerceTierPriceEntry(
			commerceTierPriceEntry);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(10), true,
				StringPool.BLANK, commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(100), commerceMoney.getPrice()));

		UnsafeRunnable<Exception> unsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(10), true,
				StringPool.BLANK, commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(200), commerceMoney.getPrice()));
	}

	@Test
	public void testCanAssertExpirationDateForTieredPricing() throws Exception {
		frutillaRule.scenario(
			"The price of a product is calculated correctly"
		).given(
			"A product"
		).and(
			"A tier price applied"
		).when(
			"The tier price expired"
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

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(20), true,
				StringPool.BLANK, commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(400), commerceMoney.getPrice()));

		CommerceTierPriceEntry commerceTierPriceEntry =
			_commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
				commercePriceEntry.getCommercePriceEntryId(),
				new BigDecimal(15), BigDecimal.ZERO, false, new BigDecimal(5),
				_serviceContext);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(5), true,
				StringPool.BLANK, commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(95), commerceMoney.getPrice()));

		commerceTierPriceEntry.setExpirationDate(RandomTestUtil.nextDate());

		_commerceTierPriceEntryLocalService.updateCommerceTierPriceEntry(
			commerceTierPriceEntry);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(10), true,
				StringPool.BLANK, commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(170), commerceMoney.getPrice()));

		UnsafeRunnable<Exception> unsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(20), true,
				StringPool.BLANK, commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(400), commerceMoney.getPrice()));
	}

	@Test
	public void testTierPriceExpirationDateWithCPInstanceUnitOfMeasure()
		throws Exception {

		frutillaRule.scenario(
			"The price of a product with a unit of measure is calculated " +
				"correctly"
		).given(
			"A product with a unit of measure"
		).and(
			"A tier price applied"
		).when(
			"The tier price expired"
		).then(
			"The correct price for the unit of measure is returned"
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

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), cpInstance.getCPInstanceId(), true,
				BigDecimal.ONE, RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				2, BigDecimal.ZERO, false, 0.0, BigDecimal.ONE,
				cpInstance.getSku());

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.addCommercePriceEntry(
				RandomTestUtil.randomString(), cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				_commercePriceList.getCommercePriceListId(), new BigDecimal(5),
				false, BigDecimal.ZERO, cpInstanceUnitOfMeasure.getKey(),
				_serviceContext);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(),
				cpInstanceUnitOfMeasure.getIncrementalOrderQuantity(), true,
				cpInstanceUnitOfMeasure.getKey(), commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(5), commerceMoney.getPrice()));

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(20), true,
				cpInstanceUnitOfMeasure.getKey(), commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(100), commerceMoney.getPrice()));

		CommerceTierPriceEntry commerceTierPriceEntry =
			_commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
				commercePriceEntry.getCommercePriceEntryId(), new BigDecimal(4),
				BigDecimal.ZERO, false, new BigDecimal(5), _serviceContext);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(5), true,
				cpInstanceUnitOfMeasure.getKey(), commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(24), commerceMoney.getPrice()));

		commerceTierPriceEntry.setExpirationDate(RandomTestUtil.nextDate());

		_commerceTierPriceEntryLocalService.updateCommerceTierPriceEntry(
			commerceTierPriceEntry);

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(10), true,
				cpInstanceUnitOfMeasure.getKey(), commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(44), commerceMoney.getPrice()));

		UnsafeRunnable<Exception> unsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), new BigDecimal(20), true,
				cpInstanceUnitOfMeasure.getKey(), commerceContext);

		commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertTrue(
			BigDecimalUtil.eq(new BigDecimal(100), commerceMoney.getPrice()));
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private CommerceCatalog _commerceCatalog;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	private CommercePriceList _commercePriceList;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Inject
	private CommerceTierPriceEntryLocalService
		_commerceTierPriceEntryLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.commerce.price.list.internal.scheduler.CheckCommerceTierPriceEntrySchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _schedulerJobConfiguration;

	private ServiceContext _serviceContext;
	private User _user;

}