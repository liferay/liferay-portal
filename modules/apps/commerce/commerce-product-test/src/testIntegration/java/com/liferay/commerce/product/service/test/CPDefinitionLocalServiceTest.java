/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.constants.CPInstanceConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class CPDefinitionLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();

		_user = UserTestUtil.addUser(_company);
	}

	@Before
	public void setUp() throws Exception {
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_company.getGroupId(), _user.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_commerceCatalog = CommerceCatalogLocalServiceUtil.addCommerceCatalog(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(), _serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				_commerceCatalog.getGroupId(), WorkflowConstants.STATUS_ANY,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CPDefinition cpDefinition : cpDefinitions) {
			_cpDefinitionLocalService.deleteCPDefinition(cpDefinition);
		}

		_cpOptionLocalService.deleteCPOptions(_company.getCompanyId());
	}

	@Test
	public void testAddCPDefinition() throws Exception {
		frutillaRule.scenario(
			"Add product definition"
		).given(
			"I add a product definition"
		).when(
			"ignoreSKUCombinations is false"
		).and(
			"hasDefaultInstance is false"
		).then(
			"product definition should be APPROVED"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpDefinition.getStatus());
	}

	@Test
	public void testAddCPDefinitionWithDefaultInstance() throws Exception {
		frutillaRule.scenario(
			"Add product definition"
		).given(
			"I add a product definition"
		).when(
			"ignoreSKUCombinations is false"
		).and(
			"hasDefaultInstance is true"
		).then(
			"product definition should be APPROVED"
		).and(
			"default product instance should be INACTIVE"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			true);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpDefinition.getStatus());

		List<CPInstance> cpInstances =
			_cpInstanceLocalService.getCPDefinitionInstances(
				cpDefinition.getCPDefinitionId(), WorkflowConstants.STATUS_ANY,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(cpInstances.toString(), 1, cpInstances.size());

		CPInstance cpInstance = cpInstances.get(0);

		Assert.assertEquals(
			WorkflowConstants.STATUS_INACTIVE, cpInstance.getStatus());
	}

	@Test
	public void testAddCPDefinitionWithDefaultInstanceAndNoSKUs()
		throws Exception {

		frutillaRule.scenario(
			"Add product definition"
		).given(
			"I add a product definition"
		).when(
			"ignoreSKUCombinations is true"
		).and(
			"hasDefaultInstance is true"
		).and(
			"no product instances are added to the definition"
		).then(
			"product definition should be APPROVED"
		).and(
			"default product instance should be INACTIVE"
		);

		int cpOptionsCount = 2;
		int cpOptionValuesCount = 2;

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		for (int i = 0; i < cpOptionsCount; i++) {
			CPOption cpOption = CPTestUtil.addCPOption(
				_commerceCatalog.getGroupId(), true);

			for (int j = 0; j < cpOptionValuesCount; j++) {
				CPTestUtil.addCPOptionValue(cpOption);
			}

			CPTestUtil.addCPDefinitionOptionRel(
				_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
				cpOption.getCPOptionId());
		}

		Assert.assertEquals(
			cpOptionsCount,
			_cpOptionLocalService.getCPOptionsCount(_company.getCompanyId()));

		cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinition.getCPDefinitionId());

		Assert.assertEquals(
			cpOptionsCount,
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRelsCount(
				cpDefinition.getCPDefinitionId()));

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpDefinition.getStatus());

		CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
			cpDefinition.getCPDefinitionId(), CPInstanceConstants.DEFAULT_SKU);

		Assert.assertEquals(
			WorkflowConstants.STATUS_INACTIVE, cpInstance.getStatus());
	}

	@Test
	public void testAddCPDefinitionWithDefaultInstanceAndSKUs()
		throws Exception {

		frutillaRule.scenario(
			"Add product definition"
		).given(
			"I add a product definition"
		).when(
			"ignoreSKUCombinations is false"
		).and(
			"hasDefaultInstance is true"
		).and(
			"some product instances are added to the definition"
		).and(
			"the definition is re-published"
		).then(
			"product definition should be APPROVED"
		).and(
			"default product instance should be INACTIVE"
		);

		int cpOptionsCount = 2;
		int cpOptionValuesCount = 2;

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			true);

		for (int i = 0; i < cpOptionsCount; i++) {
			CPOption cpOption = CPTestUtil.addCPOption(
				_commerceCatalog.getGroupId(), true);

			for (int j = 0; j < cpOptionValuesCount; j++) {
				CPTestUtil.addCPOptionValue(cpOption);
			}

			CPTestUtil.addCPDefinitionOptionRel(
				_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
				cpOption.getCPOptionId());
		}

		Assert.assertEquals(
			cpOptionsCount,
			_cpOptionLocalService.getCPOptionsCount(_company.getCompanyId()));

		Assert.assertEquals(
			cpOptionsCount,
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRelsCount(
				cpDefinition.getCPDefinitionId()));

		CPTestUtil.buildCPInstances(cpDefinition);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpDefinition.getStatus());

		CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
			cpDefinition.getCPDefinitionId(), CPInstanceConstants.DEFAULT_SKU);

		Assert.assertEquals(
			WorkflowConstants.STATUS_INACTIVE, cpInstance.getStatus());
	}

	@Test
	public void testAddCPDefinitionWithIgnoreSKUCombinations()
		throws Exception {

		frutillaRule.scenario(
			"Add product definition"
		).given(
			"I add a product definition"
		).when(
			"ignoreSKUCombinations is true"
		).and(
			"hasDefaultInstance is false"
		).then(
			"product definition should be APPROVED"
		).and(
			"product definition should have no instances"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			false);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpDefinition.getStatus());

		int count = _cpInstanceLocalService.getCPDefinitionInstancesCount(
			cpDefinition.getCPDefinitionId(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(0, count);
	}

	@Test
	public void testAddCPDefinitionWithIgnoreSKUCombinationsAndDefaultInstance()
		throws Exception {

		frutillaRule.scenario(
			"Add product definition"
		).given(
			"I add a product definition"
		).when(
			"ignoreSKUCombinations is true"
		).and(
			"hasDefaultInstance is true"
		).then(
			"product definition should be APPROVED"
		).and(
			"default product instance should be APPROVED"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpDefinition.getStatus());

		List<CPInstance> cpInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinition.getCPDefinitionId());

		int approvedCPInstances = 0;

		for (CPInstance cpInstance : cpInstances) {
			if (cpInstance.isApproved()) {
				approvedCPInstances++;
			}
		}

		Assert.assertEquals(1, approvedCPInstances);
	}

	@Test
	public void testAddExpiredCPDefinition() throws Exception {
		frutillaRule.scenario(
			"Add product definition"
		).given(
			"I add a product definition"
		).when(
			"expirationDate is passed current date"
		).and(
			"neverExpire is false"
		).then(
			"product definition should save expirationDate and have a status " +
				"of expired"
		);

		long time = System.currentTimeMillis();

		Date displayDate = new Date(time - Time.YEAR);
		Date expirationDate = new Date(time - Time.MONTH);

		Calendar expirationCalendar = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		expirationCalendar.setTime(expirationDate);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME,
			displayDate, expirationDate, false, false,
			WorkflowConstants.STATUS_EXPIRED);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, cpDefinition.getStatus());

		Assert.assertEquals(
			_portal.getDate(
				expirationCalendar.get(Calendar.MONTH),
				expirationCalendar.get(Calendar.DATE),
				expirationCalendar.get(Calendar.YEAR),
				expirationCalendar.get(Calendar.HOUR_OF_DAY),
				expirationCalendar.get(Calendar.MINUTE), _user.getTimeZone(),
				null),
			cpDefinition.getExpirationDate());
	}

	@Test
	public void testAddFutureExpiredCPDefinition() throws Exception {
		frutillaRule.scenario(
			"Add product definition"
		).given(
			"I add a product definition"
		).when(
			"expirationDate is in a future date"
		).and(
			"neverExpire is false"
		).then(
			"product definition should save expirationDate and have a status " +
				"of approved"
		);

		long time = System.currentTimeMillis();

		Date displayDate = new Date(time);
		Date expirationDate = new Date(time + Time.YEAR);

		Calendar expirationCalendar = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		expirationCalendar.setTime(expirationDate);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME,
			displayDate, expirationDate, false, false,
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpDefinition.getStatus());

		Assert.assertEquals(
			_portal.getDate(
				expirationCalendar.get(Calendar.MONTH),
				expirationCalendar.get(Calendar.DATE),
				expirationCalendar.get(Calendar.YEAR),
				expirationCalendar.get(Calendar.HOUR_OF_DAY),
				expirationCalendar.get(Calendar.MINUTE), _user.getTimeZone(),
				null),
			cpDefinition.getExpirationDate());
	}

	@Test
	public void testClonedProductPriceChangeDoesNotAffectParent()
		throws PortalException {

		frutillaRule.scenario(
			"Change Price of a cloned product sku"
		).given(
			"A product definition and its clone"
		).when(
			"changing the price of the cloned"
		).then(
			"the product price of the parent product is different from " +
				"cloned product"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			_commerceCatalog.getGroupId(), new BigDecimal(5));

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpInstance.getStatus());

		CPDefinition duplicateCPDefinition =
			_cpDefinitionLocalService.cloneCPDefinition(
				_user.getUserId(), cpInstance.getCPDefinitionId(),
				cpInstance.getGroupId(), _serviceContext);

		CPInstance duplicateCPInstance = _cpInstanceLocalService.getCPInstance(
			duplicateCPDefinition.getCPDefinitionId(), cpInstance.getSku());

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				duplicateCPInstance.getGroupId());

		CommercePriceEntry duplicateCommercePriceEntry =
			_commercePriceEntryLocalService.fetchCommercePriceEntry(
				commercePriceList.getCommercePriceListId(),
				duplicateCPInstance.getCPInstanceUuid(), StringPool.BLANK);

		duplicateCommercePriceEntry =
			_commercePriceEntryLocalService.updatePricingInfo(
				duplicateCommercePriceEntry.getCommercePriceEntryId(),
				duplicateCommercePriceEntry.isBulkPricing(), BigDecimal.TEN,
				false, BigDecimal.ZERO, null, _serviceContext);

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.fetchCommercePriceEntry(
				commercePriceList.getCommercePriceListId(),
				cpInstance.getCPInstanceUuid(), StringPool.BLANK);

		Assert.assertEquals(
			BigDecimal.TEN, duplicateCommercePriceEntry.getPrice());

		Assert.assertNotEquals(
			commercePriceEntry.getPrice(),
			duplicateCommercePriceEntry.getPrice());
	}

	@Test
	public void testCopyCPDefinition() throws PortalException {
		frutillaRule.scenario(
			"Copy a product"
		).given(
			"A product definition"
		).when(
			"the copy method is run"
		).then(
			"the copy is created without exception"
		).and(
			"ERCs of specification values are different"
		);

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		CPSpecificationOption cpSpecificationOption =
			CPTestUtil.addCPSpecificationOption(
				_commerceCatalog.getGroupId(), false);

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue1 =
				_cpDefinitionSpecificationOptionValueLocalService.
					addCPDefinitionSpecificationOptionValue(
						RandomTestUtil.randomString(),
						cpDefinition1.getCPDefinitionId(),
						cpSpecificationOption.getCPSpecificationOptionId(),
						cpSpecificationOption.getCPOptionCategoryId(),
						RandomTestUtil.randomDouble(),
						RandomTestUtil.randomLocaleStringMap(),
						ServiceContextTestUtil.getServiceContext(
							_commerceCatalog.getGroupId()));

		CPDefinition cpDefinition2 = _cpDefinitionLocalService.copyCPDefinition(
			cpDefinition1.getCPDefinitionId());

		Assert.assertNotNull(cpDefinition2);

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue2 =
				cpDefinition2.getCPDefinitionSpecificationOptionValues(
				).get(
					0
				);

		Assert.assertNotEquals(
			cpDefinitionSpecificationOptionValue1.getExternalReferenceCode(),
			cpDefinitionSpecificationOptionValue2.getExternalReferenceCode());
	}

	@Test
	public void testDeleteCPDefinitionWithIgnoreSKUCombinationsAndDefaultInstance()
		throws Exception {

		frutillaRule.scenario(
			"Delete default product instance"
		).given(
			"A product definition"
		).when(
			"ignoreSKUCombinations set to true"
		).and(
			"hasDefaultInstance set true"
		).and(
			"delete default product instance"
		).then(
			"product definition should be APPROVED"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpDefinition.getStatus());

		List<CPInstance> cpInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinition.getCPDefinitionId());

		Assert.assertEquals(cpInstances.toString(), 1, cpInstances.size());

		_cpInstanceLocalService.deleteCPInstance(cpInstances.get(0));

		cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinition.getCPDefinitionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpDefinition.getStatus());
	}

	@Test
	public void testUpdateCPDefinitionExternalReferenceCode() throws Exception {
		frutillaRule.scenario(
			"Update product definition external reference code"
		).given(
			"I add a product definition"
		).when(
			"external reference code is set"
		).then(
			"product definition should have that external reference code"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		long cpDefinitionId = cpDefinition.getCPDefinitionId();

		_cpDefinitionLocalService.updateExternalReferenceCode(
			"ERC", cpDefinitionId);

		cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		CProduct cProduct = cpDefinition.getCProduct();

		Assert.assertEquals("ERC", cProduct.getExternalReferenceCode());
	}

	@Test
	public void testUpdateExpiredCPDefinitionWithStatusExpired()
		throws Exception {

		frutillaRule.scenario(
			"Add product definition"
		).given(
			"I add a product definition"
		).when(
			"expirationDate is in the past"
		).and(
			"neverExpire is false"
		).then(
			"product definition should not update expirationDate and have a " +
				"status of expired"
		);

		long time = System.currentTimeMillis();

		Date displayDate = new Date(time - Time.YEAR);
		Date expirationDate = new Date(time - Time.MONTH);

		Calendar expirationCalendar1 = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		expirationCalendar1.setTime(expirationDate);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME,
			displayDate, expirationDate, false, false,
			WorkflowConstants.STATUS_APPROVED);

		cpDefinition = _cpDefinitionLocalService.updateStatus(
			_user.getUserId(), cpDefinition.getCPDefinitionId(),
			WorkflowConstants.STATUS_EXPIRED, _serviceContext, null);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, cpDefinition.getStatus());

		Calendar expirationCalendar2 = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		expirationCalendar2.setTime(cpDefinition.getExpirationDate());

		Assert.assertEquals(
			_portal.getDate(
				expirationCalendar1.get(Calendar.MONTH),
				expirationCalendar1.get(Calendar.DATE),
				expirationCalendar1.get(Calendar.YEAR),
				expirationCalendar1.get(Calendar.HOUR_OF_DAY),
				expirationCalendar1.get(Calendar.MINUTE), _user.getTimeZone(),
				null),
			_portal.getDate(
				expirationCalendar2.get(Calendar.MONTH),
				expirationCalendar2.get(Calendar.DATE),
				expirationCalendar2.get(Calendar.YEAR),
				expirationCalendar2.get(Calendar.HOUR_OF_DAY),
				expirationCalendar2.get(Calendar.MINUTE), _user.getTimeZone(),
				null));
	}

	@Test
	public void testUpdateFutureExpiredCPDefinitionWithStatusExpired()
		throws Exception {

		frutillaRule.scenario(
			"Add product definition"
		).given(
			"I add a product definition"
		).when(
			"expirationDate is in a future date"
		).and(
			"neverExpire is false"
		).then(
			"product definition should update expirationDate to current date " +
				"and have a status of expired"
		);

		long time = System.currentTimeMillis();

		Date displayDate = new Date(time);
		Date expirationDate = new Date(time + Time.YEAR);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME,
			displayDate, expirationDate, false, false,
			WorkflowConstants.STATUS_APPROVED);

		cpDefinition = _cpDefinitionLocalService.updateStatus(
			_user.getUserId(), cpDefinition.getCPDefinitionId(),
			WorkflowConstants.STATUS_EXPIRED, _serviceContext, null);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, cpDefinition.getStatus());

		Calendar displayDateCalendar = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		displayDateCalendar.setTime(displayDate);

		Calendar expirationCalendar = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		expirationCalendar.setTime(cpDefinition.getExpirationDate());

		Assert.assertEquals(
			_portal.getDate(
				displayDateCalendar.get(Calendar.MONTH),
				displayDateCalendar.get(Calendar.DATE),
				displayDateCalendar.get(Calendar.YEAR),
				displayDateCalendar.get(Calendar.HOUR_OF_DAY), 0,
				_user.getTimeZone(), null),
			_portal.getDate(
				expirationCalendar.get(Calendar.MONTH),
				expirationCalendar.get(Calendar.DATE),
				expirationCalendar.get(Calendar.YEAR),
				expirationCalendar.get(Calendar.HOUR_OF_DAY), 0,
				_user.getTimeZone(), null));
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private static Company _company;
	private static User _user;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Inject
	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

}