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
import com.liferay.commerce.product.configuration.CProductVersionConfiguration;
import com.liferay.commerce.product.constants.CPInstanceConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLocalization;
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
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
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

	@Before
	public void setUp() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			company.getGroupId(), TestPropsValues.getUserId());

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

		_cpOptionLocalService.deleteCPOptions(TestPropsValues.getCompanyId());
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
			_cpOptionLocalService.getCPOptionsCount(
				TestPropsValues.getCompanyId()));

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
			_cpOptionLocalService.getCPOptionsCount(
				TestPropsValues.getCompanyId()));

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
	public void testAddCPDefinitionWithSpecialCharactersInName()
		throws Exception {

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		String testString = "Test & String+";

		CPDefinitionLocalization cpDefinitionLocalization =
			_cpDefinitionLocalService.updateCPDefinitionLocalization(
				cpDefinition, cpDefinition.getDefaultLanguageId(), testString,
				null, null, null, null, null);

		Assert.assertEquals(testString, cpDefinitionLocalization.getName());
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

		User user = TestPropsValues.getUser();

		Calendar expirationCalendar = CalendarFactoryUtil.getCalendar(
			user.getTimeZone());

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
				expirationCalendar.get(Calendar.MINUTE), user.getTimeZone(),
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

		User user = TestPropsValues.getUser();

		Calendar expirationCalendar = CalendarFactoryUtil.getCalendar(
			user.getTimeZone());

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
				expirationCalendar.get(Calendar.MINUTE), user.getTimeZone(),
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
				TestPropsValues.getUserId(), cpInstance.getCPDefinitionId(),
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
						RandomTestUtil.randomLocaleStringMap(), true,
						ServiceContextTestUtil.getServiceContext(
							_commerceCatalog.getGroupId()));

		CPDefinition cpDefinition2 = _cpDefinitionLocalService.copyCPDefinition(
			cpDefinition1.getCPDefinitionId());

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue2 =
				_cpDefinitionSpecificationOptionValueLocalService.
					getCPDefinitionSpecificationOptionValues(
						cpDefinition2.getCPDefinitionId(), null,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS, null
					).get(
						0
					);

		Assert.assertNotEquals(
			cpDefinitionSpecificationOptionValue1.getExternalReferenceCode(),
			cpDefinitionSpecificationOptionValue2.getExternalReferenceCode());

		Assert.assertNotNull(
			_cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(
					cpDefinition2.getCPDefinitionId()));
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
	public void testUpdateCPDefinitionWithVersioningEnabled() throws Exception {
		frutillaRule.scenario(
			"Update product definition with versioning enabled"
		).given(
			"I add a product definition"
		).when(
			"the product versioning is enabled"
		).and(
			"the product is updated"
		).then(
			"the product should have a new version with the product change"
		);

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		Date displayDate = cpDefinition1.getDisplayDate();
		Date expirationDate = cpDefinition1.getExpirationDate();

		cpDefinition1 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition1.getCPDefinitionId(), cpDefinition1.getNameMap(),
			cpDefinition1.getShortDescriptionMap(),
			cpDefinition1.getDescriptionMap(), cpDefinition1.getUrlTitleMap(),
			cpDefinition1.getMetaTitleMap(),
			cpDefinition1.getMetaDescriptionMap(),
			cpDefinition1.getMetaKeywordsMap(),
			cpDefinition1.isIgnoreSKUCombinations(), true, true, true,
			cpDefinition1.getShippingExtraPrice(), cpDefinition1.getWidth(),
			cpDefinition1.getHeight(), cpDefinition1.getDepth(),
			cpDefinition1.getWeight(), cpDefinition1.getCPTaxCategoryId(),
			cpDefinition1.isTaxExempt(), cpDefinition1.isTelcoOrElectronics(),
			cpDefinition1.getDDMStructureKey(), cpDefinition1.isPublished(),
			displayDate.getMonth(), displayDate.getDate(),
			displayDate.getYear(), displayDate.getHours(),
			displayDate.getMinutes(), expirationDate.getMonth(),
			expirationDate.getDate(), expirationDate.getYear(),
			expirationDate.getHours(), expirationDate.getMinutes(), true,
			ServiceContextTestUtil.getServiceContext());

		cpDefinition1 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition1.getCPDefinitionId(), cpDefinition1.getNameMap(),
			cpDefinition1.getShortDescriptionMap(),
			cpDefinition1.getDescriptionMap(), cpDefinition1.getUrlTitleMap(),
			cpDefinition1.getMetaTitleMap(),
			cpDefinition1.getMetaDescriptionMap(),
			cpDefinition1.getMetaKeywordsMap(),
			cpDefinition1.isIgnoreSKUCombinations(), true, true, true,
			cpDefinition1.getShippingExtraPrice(), cpDefinition1.getWidth(),
			cpDefinition1.getHeight(), cpDefinition1.getDepth(),
			cpDefinition1.getWeight(), cpDefinition1.getCPTaxCategoryId(),
			cpDefinition1.isTaxExempt(), cpDefinition1.isTelcoOrElectronics(),
			cpDefinition1.getDDMStructureKey(), cpDefinition1.isPublished(),
			displayDate.getMonth(), displayDate.getDate(),
			displayDate.getYear(), displayDate.getHours(),
			displayDate.getMinutes(), expirationDate.getMonth(),
			expirationDate.getDate(), expirationDate.getYear(),
			expirationDate.getHours(), expirationDate.getMinutes(), true,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(cpDefinition1.isPublished());

		CProduct cProduct = cpDefinition1.getCProduct();

		Assert.assertEquals(1, cProduct.getLatestVersion());

		Assert.assertEquals(
			cpDefinition1.getCPDefinitionId(),
			cProduct.getPublishedCPDefinitionId());

		WorkflowDefinitionLink workflowDefinitionLink = null;

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CProductVersionConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).put(
							"versionThreshold", 2
						).build())) {

			CPDefinition cpDefinition2 =
				_cpDefinitionLocalService.updateCPDefinition(
					cpDefinition1.getCPDefinitionId(),
					cpDefinition1.getNameMap(),
					cpDefinition1.getShortDescriptionMap(),
					cpDefinition1.getDescriptionMap(),
					cpDefinition1.getUrlTitleMap(),
					cpDefinition1.getMetaTitleMap(),
					cpDefinition1.getMetaDescriptionMap(),
					cpDefinition1.getMetaKeywordsMap(),
					cpDefinition1.isIgnoreSKUCombinations(), true, true, true,
					cpDefinition1.getShippingExtraPrice(),
					cpDefinition1.getWidth(), cpDefinition1.getHeight(),
					cpDefinition1.getDepth(), cpDefinition1.getWeight(),
					cpDefinition1.getCPTaxCategoryId(),
					cpDefinition1.isTaxExempt(),
					cpDefinition1.isTelcoOrElectronics(),
					cpDefinition1.getDDMStructureKey(),
					cpDefinition1.isPublished(), displayDate.getMonth(),
					displayDate.getDate(), displayDate.getYear(),
					displayDate.getHours(), displayDate.getMinutes(),
					expirationDate.getMonth(), expirationDate.getDate(),
					expirationDate.getYear(), expirationDate.getHours(),
					expirationDate.getMinutes(), true,
					ServiceContextTestUtil.getServiceContext());

			Assert.assertNotEquals(
				cpDefinition1.getCPDefinitionId(),
				cpDefinition2.getCPDefinitionId());

			cpDefinition1 = _cpDefinitionLocalService.getCPDefinition(
				cpDefinition1.getCPDefinitionId());

			Assert.assertFalse(cpDefinition1.isPublished());

			Assert.assertTrue(cpDefinition2.isPublished());

			cProduct = cpDefinition2.getCProduct();

			Assert.assertEquals(2, cProduct.getLatestVersion());
			Assert.assertEquals(
				cpDefinition2.getCPDefinitionId(),
				cProduct.getPublishedCPDefinitionId());

			workflowDefinitionLink =
				_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
					null, TestPropsValues.getUserId(),
					TestPropsValues.getCompanyId(),
					_commerceCatalog.getGroupId(), CPDefinition.class.getName(),
					0, 0, "Single Approver", 1);

			CPDefinition cpDefinition3 =
				_cpDefinitionLocalService.updateCPDefinition(
					cpDefinition2.getCPDefinitionId(),
					cpDefinition2.getNameMap(),
					cpDefinition2.getShortDescriptionMap(),
					cpDefinition2.getDescriptionMap(),
					cpDefinition2.getUrlTitleMap(),
					cpDefinition2.getMetaTitleMap(),
					cpDefinition2.getMetaDescriptionMap(),
					cpDefinition2.getMetaKeywordsMap(),
					cpDefinition2.isIgnoreSKUCombinations(), true, true, true,
					cpDefinition2.getShippingExtraPrice(),
					cpDefinition2.getWidth(), cpDefinition2.getHeight(),
					cpDefinition2.getDepth(), cpDefinition2.getWeight(),
					cpDefinition2.getCPTaxCategoryId(),
					cpDefinition2.isTaxExempt(),
					cpDefinition2.isTelcoOrElectronics(),
					cpDefinition2.getDDMStructureKey(),
					cpDefinition2.isPublished(), displayDate.getMonth(),
					displayDate.getDate(), displayDate.getYear(),
					displayDate.getHours(), displayDate.getMinutes(),
					expirationDate.getMonth(), expirationDate.getDate(),
					expirationDate.getYear(), expirationDate.getHours(),
					expirationDate.getMinutes(), true,
					ServiceContextTestUtil.getServiceContext());

			Assert.assertNotEquals(
				cpDefinition2.getCPDefinitionId(),
				cpDefinition3.getCPDefinitionId());

			Assert.assertNotNull(
				_cpDefinitionLocalService.fetchCPDefinition(
					cpDefinition1.getCPDefinitionId()));

			cpDefinition2 = _cpDefinitionLocalService.getCPDefinition(
				cpDefinition2.getCPDefinitionId());

			Assert.assertTrue(cpDefinition2.isPublished());
			Assert.assertEquals(
				cpDefinition2.getCPDefinitionId(),
				cProduct.getPublishedCPDefinitionId());

			Assert.assertTrue(cpDefinition3.isPublished());
			Assert.assertEquals(
				WorkflowConstants.STATUS_PENDING, cpDefinition3.getStatus());

			cProduct = cpDefinition3.getCProduct();

			Assert.assertEquals(3, cProduct.getLatestVersion());

			List<WorkflowTask> workflowTasks =
				_workflowTaskManager.getWorkflowTasksByUserRoles(
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			WorkflowTask workflowTask = workflowTasks.get(0);

			_workflowTaskManager.assignWorkflowTaskToUser(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				workflowTask.getWorkflowTaskId(), TestPropsValues.getUserId(),
				StringPool.BLANK, null, null);

			_workflowTaskManager.completeWorkflowTask(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				workflowTask.getWorkflowTaskId(), Constants.APPROVE,
				StringPool.BLANK, null);

			Assert.assertNull(
				_cpDefinitionLocalService.fetchCPDefinition(
					cpDefinition1.getCPDefinitionId()));

			Assert.assertNotNull(
				_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
					_classNameLocalService.getClassNameId(CProduct.class),
					cProduct.getCProductId()));

			cpDefinition2 = _cpDefinitionLocalService.getCPDefinition(
				cpDefinition2.getCPDefinitionId());

			Assert.assertFalse(cpDefinition2.isPublished());

			cpDefinition3 = _cpDefinitionLocalService.getCPDefinition(
				cpDefinition3.getCPDefinitionId());

			Assert.assertTrue(cpDefinition3.isPublished());
			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, cpDefinition3.getStatus());

			cProduct = cpDefinition3.getCProduct();

			Assert.assertEquals(3, cProduct.getLatestVersion());
			Assert.assertEquals(
				cpDefinition3.getCPDefinitionId(),
				cProduct.getPublishedCPDefinitionId());

			_cpDefinitionLocalService.deleteCPDefinition(
				cpDefinition2.getCPDefinitionId());

			Assert.assertNotNull(
				_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
					_classNameLocalService.getClassNameId(CProduct.class),
					cProduct.getCProductId()));

			_cpDefinitionLocalService.deleteCPDefinition(
				cpDefinition3.getCPDefinitionId());

			Assert.assertNull(
				_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
					_classNameLocalService.getClassNameId(CProduct.class),
					cProduct.getCProductId()));
		}
		finally {
			if (workflowDefinitionLink != null) {
				_workflowDefinitionLinkLocalService.
					deleteWorkflowDefinitionLink(workflowDefinitionLink);
			}
		}
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

		User user = TestPropsValues.getUser();

		Calendar expirationCalendar1 = CalendarFactoryUtil.getCalendar(
			user.getTimeZone());

		expirationCalendar1.setTime(expirationDate);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME,
			displayDate, expirationDate, false, false,
			WorkflowConstants.STATUS_APPROVED);

		cpDefinition = _cpDefinitionLocalService.updateStatus(
			user.getUserId(), cpDefinition.getCPDefinitionId(),
			WorkflowConstants.STATUS_EXPIRED, _serviceContext, null);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, cpDefinition.getStatus());

		Calendar expirationCalendar2 = CalendarFactoryUtil.getCalendar(
			user.getTimeZone());

		expirationCalendar2.setTime(cpDefinition.getExpirationDate());

		Assert.assertEquals(
			_portal.getDate(
				expirationCalendar1.get(Calendar.MONTH),
				expirationCalendar1.get(Calendar.DATE),
				expirationCalendar1.get(Calendar.YEAR),
				expirationCalendar1.get(Calendar.HOUR_OF_DAY),
				expirationCalendar1.get(Calendar.MINUTE), user.getTimeZone(),
				null),
			_portal.getDate(
				expirationCalendar2.get(Calendar.MONTH),
				expirationCalendar2.get(Calendar.DATE),
				expirationCalendar2.get(Calendar.YEAR),
				expirationCalendar2.get(Calendar.HOUR_OF_DAY),
				expirationCalendar2.get(Calendar.MINUTE), user.getTimeZone(),
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

		User user = TestPropsValues.getUser();

		cpDefinition = _cpDefinitionLocalService.updateStatus(
			user.getUserId(), cpDefinition.getCPDefinitionId(),
			WorkflowConstants.STATUS_EXPIRED, _serviceContext, null);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, cpDefinition.getStatus());

		Calendar displayDateCalendar = CalendarFactoryUtil.getCalendar(
			user.getTimeZone());

		displayDateCalendar.setTime(displayDate);

		Calendar expirationCalendar = CalendarFactoryUtil.getCalendar(
			user.getTimeZone());

		expirationCalendar.setTime(cpDefinition.getExpirationDate());

		Assert.assertEquals(
			_portal.getDate(
				displayDateCalendar.get(Calendar.MONTH),
				displayDateCalendar.get(Calendar.DATE),
				displayDateCalendar.get(Calendar.YEAR),
				displayDateCalendar.get(Calendar.HOUR_OF_DAY), 0,
				user.getTimeZone(), null),
			_portal.getDate(
				expirationCalendar.get(Calendar.MONTH),
				expirationCalendar.get(Calendar.DATE),
				expirationCalendar.get(Calendar.YEAR),
				expirationCalendar.get(Calendar.HOUR_OF_DAY), 0,
				user.getTimeZone(), null));
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

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
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}