/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.configuration.CProductVersionConfiguration;
import com.liferay.commerce.product.constants.CPInstanceConstants;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLocalization;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceOptionValueRel;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPDefinitionLinkLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalService;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceOptionValueRelLocalService;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.util.comparator.CPDefinitionModifiedDateComparator;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
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
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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

			_cpOptions.add(cpOption);

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

			_cpOptions.add(cpOption);

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
				cpDefinition, cpDefinition.getDefaultLanguageId(), null, null,
				null, null, testString, null);

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
	public void testCopyCPDefinition() throws Exception {
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

		try {
			_cpDefinitionLocalService.copyCPDefinition(
				cpDefinition1.getCPDefinitionId());

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			Assert.assertNotNull(unsupportedOperationException);
		}

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

			User user = UserTestUtil.addUser();

			CPConfigurationList cpConfigurationList =
				_cpConfigurationListLocalService.addCPConfigurationList(
					RandomTestUtil.randomString(), user.getUserId(),
					_commerceCatalog.getGroupId(), 0, false,
					RandomTestUtil.randomString(), 2, 1, 1, 2024, 0, 0, 0, 0, 0,
					0, 0, true, new ServiceContext());

			_cpConfigurationEntryLocalService.addCPConfigurationEntry(
				RandomTestUtil.randomString(), user.getUserId(),
				cpConfigurationList.getGroupId(),
				_portal.getClassNameId(CPDefinition.class),
				cpDefinition1.getCPDefinitionId(),
				cpConfigurationList.getCPConfigurationListId(), 0, "123.00",
				true, 0, "cpde", 1.0, true, true, true, 1.0, "lowstoc",
				BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
				true, true, 1.0, true, true, 1.0, 1.0);

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

			CPDefinition cpDefinition2 =
				_cpDefinitionLocalService.copyCPDefinition(
					cpDefinitionSpecificationOptionValue1.getCPDefinitionId());

			List<CPDefinitionSpecificationOptionValue>
				cpDefinitionSpecificationOptionValues =
					_cpDefinitionSpecificationOptionValueLocalService.
						getCPDefinitionSpecificationOptionValues(
							cpDefinition2.getCPDefinitionId(), null,
							QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue2 =
					cpDefinitionSpecificationOptionValues.get(0);

			Assert.assertNotEquals(
				cpDefinitionSpecificationOptionValue1.
					getExternalReferenceCode(),
				cpDefinitionSpecificationOptionValue2.
					getExternalReferenceCode());

			Assert.assertNotNull(
				_cpDefinitionInventoryLocalService.
					fetchCPDefinitionInventoryByCPDefinitionId(
						cpDefinition2.getCPDefinitionId()));
		}
	}

	@Test
	public void testCopyCPDefinitionWithSKUCombinations() throws Exception {
		frutillaRule.scenario(
			"Copy a product definition with SKU combinations"
		).given(
			"A product definition with SKU combinations"
		).when(
			"the copy method is run"
		).then(
			"the copied SKUs link to the copied option value rels"
		);

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		CPOption cpOption = CPTestUtil.addCPOption(
			_commerceCatalog.getGroupId(), true);

		_cpOptions.add(cpOption);

		for (int i = 0; i < 3; i++) {
			CPTestUtil.addCPOptionValue(cpOption);
		}

		CPTestUtil.addCPDefinitionOptionRel(
			_commerceCatalog.getGroupId(), cpDefinition1.getCPDefinitionId(),
			cpOption.getCPOptionId());

		CPTestUtil.buildCPInstances(cpDefinition1);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CProductVersionConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).put(
							"versionThreshold", 5
						).build())) {

			CPDefinition cpDefinition2 =
				_cpDefinitionLocalService.copyCPDefinition(
					cpDefinition1.getCPDefinitionId(),
					cpDefinition1.getGroupId(), WorkflowConstants.STATUS_DRAFT);

			List<CPInstance> cpInstances =
				_cpInstanceLocalService.getCPDefinitionInstances(
					cpDefinition2.getCPDefinitionId(),
					WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

			Assert.assertFalse(cpInstances.isEmpty());

			for (CPInstance cpInstance : cpInstances) {
				List<CPInstanceOptionValueRel> cpInstanceOptionValueRels =
					_cpInstanceOptionValueRelLocalService.
						getCPInstanceCPInstanceOptionValueRels(
							cpInstance.getCPInstanceId());

				for (CPInstanceOptionValueRel cpInstanceOptionValueRel :
						cpInstanceOptionValueRels) {

					CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
						_cpDefinitionOptionValueRelLocalService.
							getCPDefinitionOptionValueRel(
								cpInstanceOptionValueRel.
									getCPDefinitionOptionValueRelId());

					CPDefinitionOptionRel cpDefinitionOptionRel =
						cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

					Assert.assertEquals(
						cpDefinition2.getCPDefinitionId(),
						cpDefinitionOptionRel.getCPDefinitionId());
				}
			}
		}
	}

	@Test
	public void testDeleteCPDefinitionRemovesIncomingDefinitionLinks()
		throws Exception {

		frutillaRule.scenario(
			"Delete product definition with incoming definition links"
		).given(
			"A product definition with a definition link to another product " +
				"definition"
		).when(
			"the linked product definition is deleted"
		).then(
			"the definition link should be removed from the source product " +
				"definition"
		);

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);
		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		Calendar displayCalendar = CalendarFactoryUtil.getCalendar();

		displayCalendar.setTime(cpDefinition1.getDisplayDate());

		_cpDefinitionLinkLocalService.addCPDefinitionLinkByCProductId(
			cpDefinition1.getCPDefinitionId(), cpDefinition2.getCProductId(),
			displayCalendar.get(Calendar.MONTH),
			displayCalendar.get(Calendar.DAY_OF_MONTH),
			displayCalendar.get(Calendar.YEAR),
			displayCalendar.get(Calendar.HOUR_OF_DAY),
			displayCalendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, 0D,
			"related", _serviceContext);

		Assert.assertEquals(
			1,
			_cpDefinitionLinkLocalService.getCPDefinitionLinksCount(
				cpDefinition1.getCPDefinitionId()));

		_cpDefinitionLocalService.deleteCPDefinition(
			cpDefinition2.getCPDefinitionId());

		Assert.assertEquals(
			0,
			_cpDefinitionLinkLocalService.getCPDefinitionLinksCount(
				cpDefinition1.getCPDefinitionId()));
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
	public void testFetchApprovedOnlyCPDefinitionByCProductId()
		throws Exception {

		frutillaRule.scenario(
			"Fetch only approved CPDefinition"
		).given(
			"A newly created CPDefinition"
		).when(
			"the CPDefinition is converted to draft"
		).and(
			"the fetch of this CPDefinition is attempted"
		).then(
			"the CPDefinition is not found"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		CPDefinition fetchedCPDefinition =
			_cpDefinitionLocalService.fetchCPDefinitionByCProductId(
				cpDefinition.getCProductId(), true);

		Assert.assertNotNull(fetchedCPDefinition);
		Assert.assertEquals(
			cpDefinition.getCPDefinitionId(),
			fetchedCPDefinition.getCPDefinitionId());
		Assert.assertEquals(
			cpDefinition.getStatus(), fetchedCPDefinition.getStatus());

		cpDefinition.setStatus(WorkflowConstants.STATUS_DRAFT);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, cpDefinition.getStatus());

		fetchedCPDefinition =
			_cpDefinitionLocalService.fetchCPDefinitionByCProductId(
				cpDefinition.getCProductId(), true);

		Assert.assertNull(fetchedCPDefinition);
	}

	@Test
	public void testFindByExpirationDate() throws Exception {
		long time = System.currentTimeMillis();

		Date date = new Date(time + Time.DAY);

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME,
			new Date(time - Time.MONTH), date, false, false,
			WorkflowConstants.STATUS_APPROVED);

		cpDefinition1.setExpirationDate(new Date(time - Time.DAY));

		cpDefinition1 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition1);

		CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME,
			new Date(time - Time.MONTH), date, false, false,
			WorkflowConstants.STATUS_APPROVED);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.findByExpirationDate(
				new Date(time),
				new QueryDefinition(WorkflowConstants.STATUS_APPROVED));

		Assert.assertEquals(cpDefinitions.toString(), 1, cpDefinitions.size());

		CPDefinition cpDefinition2 = cpDefinitions.get(0);

		Assert.assertEquals(
			cpDefinition1.getCPDefinitionId(),
			cpDefinition2.getCPDefinitionId());
	}

	@Test
	public void testGetCPDefinitions() throws Exception {
		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null,
				_serviceContext);

		AccountGroup accountGroup = _accountGroupLocalService.addAccountGroup(
			StringPool.BLANK, _serviceContext.getUserId(), null,
			RandomTestUtil.randomString(), _serviceContext);

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			accountEntry.getAccountEntryId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_commerceCatalog.getGroupId(), null);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		long companyId = TestPropsValues.getCompanyId();
		long accountEntryId = accountEntry.getAccountEntryId();
		long[] accountGroupIds = {accountGroup.getAccountGroupId()};
		long[] commerceChannelGroupIds = {commerceChannel.getGroupId()};
		int[] statuses = {WorkflowConstants.STATUS_APPROVED};

		// Both filters disabled

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				companyId, accountEntryId, accountGroupIds,
				commerceChannelGroupIds, true, statuses,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(cpDefinitions.toString(), 1, cpDefinitions.size());

		// Both filters enabled and relationships exist

		cpDefinition.setAccountGroupFilterEnabled(true);
		cpDefinition.setChannelFilterEnabled(true);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		AccountGroupRel accountGroupRel =
			_accountGroupRelLocalService.addAccountGroupRel(
				accountGroup.getAccountGroupId(), CPDefinition.class.getName(),
				cpDefinition.getCPDefinitionId());

		CommerceChannelRel commerceChannelRel =
			_commerceChannelRelLocalService.addCommerceChannelRel(
				CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
				commerceChannel.getCommerceChannelId(), _serviceContext);

		cpDefinitions = _cpDefinitionLocalService.getCPDefinitions(
			companyId, accountEntryId, accountGroupIds, commerceChannelGroupIds,
			true, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);

		Assert.assertEquals(cpDefinitions.toString(), 1, cpDefinitions.size());

		// AccountGroupFilter enabled, ChannelFilter disabled
		// accountGroupRel exists

		cpDefinition.setChannelFilterEnabled(false);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		cpDefinitions = _cpDefinitionLocalService.getCPDefinitions(
			companyId, accountEntryId, accountGroupIds, commerceChannelGroupIds,
			true, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);

		Assert.assertEquals(cpDefinitions.toString(), 1, cpDefinitions.size());

		// AccountGroupFilter enabled, ChannelFilter disabled
		// accountGroupRel does not exist

		_accountGroupRelLocalService.deleteAccountGroupRel(
			accountGroupRel.getAccountGroupRelId());

		cpDefinitions = _cpDefinitionLocalService.getCPDefinitions(
			companyId, accountEntryId, accountGroupIds, commerceChannelGroupIds,
			true, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);

		Assert.assertEquals(cpDefinitions.toString(), 0, cpDefinitions.size());

		// AccountGroupFilter disabled, ChannelFilter enabled
		// commerceChannelRel exists

		cpDefinition.setAccountGroupFilterEnabled(false);
		cpDefinition.setChannelFilterEnabled(true);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		cpDefinitions = _cpDefinitionLocalService.getCPDefinitions(
			companyId, accountEntryId, accountGroupIds, commerceChannelGroupIds,
			true, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);

		Assert.assertEquals(cpDefinitions.toString(), 1, cpDefinitions.size());

		// AccountGroupFilter disabled, ChannelFilter enabled
		// commerceChannelRel does not exist

		_commerceChannelRelLocalService.deleteCommerceChannelRel(
			commerceChannelRel);

		cpDefinitions = _cpDefinitionLocalService.getCPDefinitions(
			companyId, accountEntryId, accountGroupIds, commerceChannelGroupIds,
			true, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);

		Assert.assertEquals(cpDefinitions.toString(), 0, cpDefinitions.size());

		// Both filters enabled, accountGroupRel exists

		cpDefinition.setAccountGroupFilterEnabled(true);
		cpDefinition.setChannelFilterEnabled(true);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		accountGroupRel = _accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), CPDefinition.class.getName(),
			cpDefinition.getCPDefinitionId());

		cpDefinitions = _cpDefinitionLocalService.getCPDefinitions(
			companyId, accountEntryId, accountGroupIds, commerceChannelGroupIds,
			true, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);

		Assert.assertEquals(cpDefinitions.toString(), 0, cpDefinitions.size());

		// Both filters enabled, commerceChannelRel exists

		_accountGroupRelLocalService.deleteAccountGroupRel(
			accountGroupRel.getAccountGroupRelId());

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			commerceChannel.getCommerceChannelId(), _serviceContext);

		cpDefinitions = _cpDefinitionLocalService.getCPDefinitions(
			companyId, accountEntryId, accountGroupIds, commerceChannelGroupIds,
			true, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);

		Assert.assertEquals(cpDefinitions.toString(), 0, cpDefinitions.size());
	}

	@Test
	public void testGetCPDefinitionsOrderByComparator() throws Exception {
		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);
		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null, null, true,
				new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				CPDefinitionModifiedDateComparator.getInstance(true));

		int index1 = -1;
		int index2 = -1;

		for (int i = 0; i < cpDefinitions.size(); i++) {
			long cpDefinitionId = cpDefinitions.get(i).getCPDefinitionId();

			if (cpDefinitionId == cpDefinition1.getCPDefinitionId()) {
				index1 = i;
			}
			else if (cpDefinitionId == cpDefinition2.getCPDefinitionId()) {
				index2 = i;
			}
		}

		Assert.assertTrue(
			"cpDefinition1 not found in results", index1 >= 0);
		Assert.assertTrue(
			"cpDefinition2 not found in results", index2 >= 0);
		Assert.assertTrue(
			"Results not ordered by modifiedDate ASC", index1 < index2);
	}

	@Test
	public void testGetCPDefinitionsOrderByLocalizedName() throws Exception {
		CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);
		CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);
		CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null, null, true,
				new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (int i = 1; i < cpDefinitions.size(); i++) {
			CPDefinition previous = cpDefinitions.get(i - 1);
			CPDefinition current = cpDefinitions.get(i);

			String previousName = previous.getName(
				previous.getDefaultLanguageId());
			String currentName = current.getName(
				current.getDefaultLanguageId());

			Assert.assertTrue(
				"Results not ordered ascending by localized name: " +
					previousName + " > " + currentName,
				previousName.compareToIgnoreCase(currentName) <= 0);
		}
	}

	@Test
	public void testGetCPDefinitionsWithAccountGroupFilterEnabledAndNoRel()
		throws Exception {

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null, _serviceContext);

		AccountGroup accountGroup = _accountGroupLocalService.addAccountGroup(
			StringPool.BLANK, _serviceContext.getUserId(), null,
			RandomTestUtil.randomString(), _serviceContext);

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			accountEntry.getAccountEntryId());

		CPDefinition cpDefinitionUnconfigured =
			CPTestUtil.addCPDefinitionFromCatalog(
				_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
				false);

		cpDefinitionUnconfigured.setAccountGroupFilterEnabled(true);

		cpDefinitionUnconfigured = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinitionUnconfigured);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(),
				accountEntry.getAccountEntryId(),
				_accountGroupLocalService.getAccountGroupIds(
					accountEntry.getAccountEntryId()),
				null, true,
				new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertFalse(
			"Product with accountGroupFilterEnabled=true but no " +
				"AccountGroupRel must be excluded",
			cpDefinitions.contains(cpDefinitionUnconfigured));
	}

	@Test
	public void testGetCPDefinitionsWithChannelFilterEnabledAndNoRel()
		throws Exception {

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_commerceCatalog.getGroupId(), null);

		CPDefinition cpDefinitionUnconfigured =
			CPTestUtil.addCPDefinitionFromCatalog(
				_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
				false);

		cpDefinitionUnconfigured.setChannelFilterEnabled(true);

		cpDefinitionUnconfigured = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinitionUnconfigured);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null,
				new long[] {commerceChannel.getGroupId()}, true,
				new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertFalse(
			"Product with channelFilterEnabled=true but no " +
				"CommerceChannelRel must be excluded",
			cpDefinitions.contains(cpDefinitionUnconfigured));
	}

	@Test
	public void testGetCPDefinitionsWithDraftStatus() throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		cpDefinition.setStatus(WorkflowConstants.STATUS_DRAFT);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null, null, true,
				new int[] {WorkflowConstants.STATUS_DRAFT},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(
			"Draft product not returned when filtering by STATUS_DRAFT",
			cpDefinitions.contains(cpDefinition));

		cpDefinitions = _cpDefinitionLocalService.getCPDefinitions(
			TestPropsValues.getCompanyId(), 0L, null, null, true,
			new int[] {WorkflowConstants.STATUS_APPROVED}, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertFalse(
			"Draft product returned when filtering by STATUS_APPROVED",
			cpDefinitions.contains(cpDefinition));
	}

	@Test
	public void testGetCPDefinitionsWithEmptyStatuses() throws Exception {
		CPDefinition cpDefinitionApproved =
			CPTestUtil.addCPDefinitionFromCatalog(
				_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
				false);

		CPDefinition cpDefinitionDraft = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		cpDefinitionDraft.setStatus(WorkflowConstants.STATUS_DRAFT);

		cpDefinitionDraft = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinitionDraft);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null, null, true,
				new int[0], QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(
			"Approved product must be returned with empty statuses",
			cpDefinitions.contains(cpDefinitionApproved));
		Assert.assertTrue(
			"Draft product must be returned with empty statuses",
			cpDefinitions.contains(cpDefinitionDraft));
	}

	@Test
	public void testGetCPDefinitionsWithIneligibleAccountEntry()
		throws Exception {

		AccountEntry eligibleAccountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null, _serviceContext);

		AccountEntry ineligibleAccountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null, _serviceContext);

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_commerceCatalog.getGroupId(), null);

		_commerceChannelAccountEntryRelLocalService.
			addCommerceChannelAccountEntryRel(
				TestPropsValues.getUserId(),
				eligibleAccountEntry.getAccountEntryId(),
				AccountEntry.class.getName(),
				eligibleAccountEntry.getAccountEntryId(),
				commerceChannel.getCommerceChannelId(), true, 0,
				CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		cpDefinition.setChannelFilterEnabled(true);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			commerceChannel.getCommerceChannelId(), _serviceContext);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(),
				ineligibleAccountEntry.getAccountEntryId(), null,
				new long[] {commerceChannel.getGroupId()}, true,
				new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertFalse(
			"Product must be excluded for an ineligible account entry",
			cpDefinitions.contains(cpDefinition));
	}

	@Test
	public void testGetCPDefinitionsWithInvalidCommerceChannelGroupId()
		throws Exception {

		CPDefinition cpDefinitionUnrestricted =
			CPTestUtil.addCPDefinitionFromCatalog(
				_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
				false);

		CPDefinition cpDefinitionRestricted =
			CPTestUtil.addCPDefinitionFromCatalog(
				_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
				false);

		cpDefinitionRestricted.setChannelFilterEnabled(true);

		cpDefinitionRestricted = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinitionRestricted);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null,
				new long[] {RandomTestUtil.randomLong()}, true,
				new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(
			"Product with disabled filter must be returned",
			cpDefinitions.contains(cpDefinitionUnrestricted));
		Assert.assertFalse(
			"Product with enabled filter must be excluded for an invalid " +
				"channel group id",
			cpDefinitions.contains(cpDefinitionRestricted));
	}

	@Test
	public void testGetCPDefinitionsWithMultipleCommerceChannels()
		throws Exception {

		CommerceChannel commerceChannel1 = CommerceTestUtil.addCommerceChannel(
			_commerceCatalog.getGroupId(), null);
		CommerceChannel commerceChannel2 = CommerceTestUtil.addCommerceChannel(
			_commerceCatalog.getGroupId(), null);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		cpDefinition.setChannelFilterEnabled(true);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			commerceChannel1.getCommerceChannelId(), _serviceContext);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null,
				new long[] {
					commerceChannel1.getGroupId(), commerceChannel2.getGroupId()
				},
				true, new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(
			"Product with rel on one of the channels must be returned (OR " +
				"semantics)",
			cpDefinitions.contains(cpDefinition));
	}

	@Test
	public void testGetCPDefinitionsWithPublishedFalse() throws Exception {
		CPDefinition cpDefinitionPublished =
			CPTestUtil.addCPDefinitionFromCatalog(
				_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
				false);

		CPDefinition cpDefinitionUnpublished =
			CPTestUtil.addCPDefinitionFromCatalog(
				_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
				false);

		cpDefinitionUnpublished.setPublished(false);

		cpDefinitionUnpublished = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinitionUnpublished);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null, null, false,
				new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(
			"Published product must be returned when published=false",
			cpDefinitions.contains(cpDefinitionPublished));
		Assert.assertTrue(
			"Unpublished product must be returned when published=false",
			cpDefinitions.contains(cpDefinitionUnpublished));

		cpDefinitions = _cpDefinitionLocalService.getCPDefinitions(
			TestPropsValues.getCompanyId(), 0L, null, null, true,
			new int[] {WorkflowConstants.STATUS_APPROVED}, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertFalse(
			"Unpublished product must be excluded when published=true",
			cpDefinitions.contains(cpDefinitionUnpublished));
	}

	@Test
	public void testGetCPDefinitionsWithStatusAny() throws Exception {
		CPDefinition cpDefinitionApproved =
			CPTestUtil.addCPDefinitionFromCatalog(
				_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
				false);

		CPDefinition cpDefinitionDraft = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		cpDefinitionDraft.setStatus(WorkflowConstants.STATUS_DRAFT);

		cpDefinitionDraft = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinitionDraft);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null, null, true,
				new int[] {WorkflowConstants.STATUS_ANY}, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertTrue(
			"Approved product must be returned with STATUS_ANY",
			cpDefinitions.contains(cpDefinitionApproved));
		Assert.assertTrue(
			"Draft product must be returned with STATUS_ANY",
			cpDefinitions.contains(cpDefinitionDraft));
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
			cpDefinition1.getCPDefinitionId(),
			cpDefinition1.getCPTaxCategoryId(),
			cpDefinition1.isAccountGroupFilterEnabled(),
			cpDefinition1.isChannelFilterEnabled(),
			cpDefinition1.getDDMStructureKey(), cpDefinition1.getDepth(),
			cpDefinition1.getDescriptionMap(), displayDate.getDate(),
			displayDate.getHours(), displayDate.getMinutes(),
			displayDate.getMonth(), displayDate.getYear(),
			expirationDate.getDate(), expirationDate.getHours(),
			expirationDate.getMinutes(), expirationDate.getMonth(),
			expirationDate.getYear(), true, cpDefinition1.getHeight(),
			cpDefinition1.isIgnoreSKUCombinations(),
			cpDefinition1.getMetaDescriptionMap(),
			cpDefinition1.getMetaKeywordsMap(), cpDefinition1.getMetaTitleMap(),
			cpDefinition1.getNameMap(), true, cpDefinition1.isPublished(), true,
			true, cpDefinition1.getShippingExtraPrice(),
			cpDefinition1.getShortDescriptionMap(), cpDefinition1.isTaxExempt(),
			cpDefinition1.isTelcoOrElectronics(),
			cpDefinition1.getUrlTitleMap(), cpDefinition1.getWeight(),
			cpDefinition1.getWidth(),
			ServiceContextTestUtil.getServiceContext());

		cpDefinition1 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition1.getCPDefinitionId(),
			cpDefinition1.getCPTaxCategoryId(),
			cpDefinition1.isAccountGroupFilterEnabled(),
			cpDefinition1.isChannelFilterEnabled(),
			cpDefinition1.getDDMStructureKey(), cpDefinition1.getDepth(),
			cpDefinition1.getDescriptionMap(), displayDate.getDate(),
			displayDate.getHours(), displayDate.getMinutes(),
			displayDate.getMonth(), displayDate.getYear(),
			expirationDate.getDate(), expirationDate.getHours(),
			expirationDate.getMinutes(), expirationDate.getMonth(),
			expirationDate.getYear(), true, cpDefinition1.getHeight(),
			cpDefinition1.isIgnoreSKUCombinations(),
			cpDefinition1.getMetaDescriptionMap(),
			cpDefinition1.getMetaKeywordsMap(), cpDefinition1.getMetaTitleMap(),
			cpDefinition1.getNameMap(), true, cpDefinition1.isPublished(), true,
			true, cpDefinition1.getShippingExtraPrice(),
			cpDefinition1.getShortDescriptionMap(), cpDefinition1.isTaxExempt(),
			cpDefinition1.isTelcoOrElectronics(),
			cpDefinition1.getUrlTitleMap(), cpDefinition1.getWeight(),
			cpDefinition1.getWidth(),
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
					cpDefinition1.getCPTaxCategoryId(),
					cpDefinition1.isAccountGroupFilterEnabled(),
					cpDefinition1.isChannelFilterEnabled(),
					cpDefinition1.getDDMStructureKey(),
					cpDefinition1.getDepth(), cpDefinition1.getDescriptionMap(),
					displayDate.getDate(), displayDate.getHours(),
					displayDate.getMinutes(), displayDate.getMonth(),
					displayDate.getYear(), expirationDate.getDate(),
					expirationDate.getHours(), expirationDate.getMinutes(),
					expirationDate.getMonth(), expirationDate.getYear(), true,
					cpDefinition1.getHeight(),
					cpDefinition1.isIgnoreSKUCombinations(),
					cpDefinition1.getMetaDescriptionMap(),
					cpDefinition1.getMetaKeywordsMap(),
					cpDefinition1.getMetaTitleMap(), cpDefinition1.getNameMap(),
					true, cpDefinition1.isPublished(), true, true,
					cpDefinition1.getShippingExtraPrice(),
					cpDefinition1.getShortDescriptionMap(),
					cpDefinition1.isTaxExempt(),
					cpDefinition1.isTelcoOrElectronics(),
					cpDefinition1.getUrlTitleMap(), cpDefinition1.getWeight(),
					cpDefinition1.getWidth(),
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
					cpDefinition2.getCPTaxCategoryId(),
					cpDefinition2.isAccountGroupFilterEnabled(),
					cpDefinition2.isChannelFilterEnabled(),
					cpDefinition2.getDDMStructureKey(),
					cpDefinition2.getDepth(), cpDefinition2.getDescriptionMap(),
					displayDate.getDate(), displayDate.getHours(),
					displayDate.getMinutes(), displayDate.getMonth(),
					displayDate.getYear(), expirationDate.getDate(),
					expirationDate.getHours(), expirationDate.getMinutes(),
					expirationDate.getMonth(), expirationDate.getYear(), true,
					cpDefinition2.getHeight(),
					cpDefinition2.isIgnoreSKUCombinations(),
					cpDefinition2.getMetaDescriptionMap(),
					cpDefinition2.getMetaKeywordsMap(),
					cpDefinition2.getMetaTitleMap(), cpDefinition2.getNameMap(),
					true, cpDefinition2.isPublished(), true, true,
					cpDefinition2.getShippingExtraPrice(),
					cpDefinition2.getShortDescriptionMap(),
					cpDefinition2.isTaxExempt(),
					cpDefinition2.isTelcoOrElectronics(),
					cpDefinition2.getUrlTitleMap(), cpDefinition2.getWeight(),
					cpDefinition2.getWidth(),
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
	public void testUpdateCProductLatestVersion() throws Exception {
		frutillaRule.scenario(
			"Update CProduct latest version when latest published " +
				"CPDefinition is deleted"
		).given(
			"A newly created CPDefinition"
		).when(
			"publish a copy of the current CPDefinition"
		).and(
			"delete the copy of the CPDefinition"
		).then(
			"the version of the CProduct is updated to the previous one"
		);

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		Assert.assertTrue(cpDefinition1.isPublished());

		CProduct cProduct = cpDefinition1.getCProduct();

		Assert.assertEquals(1, cProduct.getLatestVersion());
		Assert.assertEquals(
			cpDefinition1.getCPDefinitionId(),
			cProduct.getPublishedCPDefinitionId());

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
				_cpDefinitionLocalService.copyCPDefinition(
					cpDefinition1.getCPDefinitionId());

			Assert.assertNotEquals(
				cpDefinition1.getCPDefinitionId(),
				cpDefinition2.getCPDefinitionId());

			cProduct = cpDefinition2.getCProduct();

			Assert.assertEquals(2, cProduct.getLatestVersion());
			Assert.assertNotEquals(
				cProduct.getPublishedCPDefinitionId(),
				cpDefinition2.getCPDefinitionId());

			cpDefinition2 = _cpDefinitionLocalService.updateStatus(
				_serviceContext.getUserId(), cpDefinition2.getCPDefinitionId(),
				WorkflowConstants.STATUS_APPROVED, _serviceContext,
				Collections.emptyMap());

			cProduct = cpDefinition2.getCProduct();

			Assert.assertEquals(2, cProduct.getLatestVersion());
			Assert.assertEquals(
				cProduct.getPublishedCPDefinitionId(),
				cpDefinition2.getCPDefinitionId());

			_cpDefinitionLocalService.deleteCPDefinition(
				cpDefinition2.getCPDefinitionId());

			cProduct = cpDefinition1.getCProduct();

			Assert.assertEquals(1, cProduct.getLatestVersion());
			Assert.assertEquals(
				cProduct.getPublishedCPDefinitionId(),
				cpDefinition1.getCPDefinitionId());
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
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Inject
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CPConfigurationEntryLocalService _cpConfigurationEntryLocalService;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Inject
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

	@Inject
	private CPDefinitionLinkLocalService _cpDefinitionLinkLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Inject
	private CPDefinitionOptionValueRelLocalService
		_cpDefinitionOptionValueRelLocalService;

	@Inject
	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	@Inject
	private CPInstanceOptionValueRelLocalService
		_cpInstanceOptionValueRelLocalService;

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

	@DeleteAfterTestRun
	private final List<CPOption> _cpOptions = new ArrayList<>();

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