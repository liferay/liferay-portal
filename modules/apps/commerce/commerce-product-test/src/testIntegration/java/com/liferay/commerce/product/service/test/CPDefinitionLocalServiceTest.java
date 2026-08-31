/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
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
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.exception.NoSuchCProductException;
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
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.product.util.comparator.CPDefinitionModifiedDateComparator;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
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
		_testCopyCPDefinition();
		_testCopyCPDefinitionDoesNotCopyDraftCPDefinition();
		_testCopyCPDefinitionSetsExistingDraftToIncomplete();
		_testCopyCPDefinitionWithSKUCombinations();
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
		_testGetCPDefinitions();
		_testGetCPDefinitionsOrderByComparator();
		_testGetCPDefinitionsOrderByLocalizedName();
		_testGetCPDefinitionsWithAccountGroupFilterEnabledAndNoRel();
		_testGetCPDefinitionsWithBothFiltersEnabledAndAllRels();
		_testGetCPDefinitionsWithBothFiltersEnabledAndOnlyAccountGroupRel();
		_testGetCPDefinitionsWithBothFiltersEnabledAndOnlyChannelRel();
		_testGetCPDefinitionsWithChannelFilterEnabledAndNoRel();
		_testGetCPDefinitionsWithDraftStatus();
		_testGetCPDefinitionsWithEmptyStatuses();
		_testGetCPDefinitionsWithIneligibleAccountEntry();
		_testGetCPDefinitionsWithInvalidCommerceChannelGroupId();
		_testGetCPDefinitionsWithMultipleCommerceChannels();
		_testGetCPDefinitionsWithPublishedFalse();
		_testGetCPDefinitionsWithStatusAny();
	}

	@Test
	public void testGetOrAddEmptyCPDefinition() throws Exception {
		frutillaRule.scenario(
			"Get or add an empty product definition"
		).given(
			"A catalog and an external reference code"
		).when(
			"An empty product definition is requested"
		).then(
			"A NoSuchCProductException is thrown while lazy referencing is " +
				"disabled"
		).and(
			"An empty stub paired with a product carrying the given external " +
				"reference code is returned while lazy referencing is enabled"
		).and(
			"The same product definition is resolved on subsequent requests"
		).and(
			"The stub is hidden from the store front"
		).and(
			"The empty status is cleared once the stub is updated"
		);

		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			_cpDefinitionLocalService.getOrAddEmptyCPDefinition(
				externalReferenceCode, SimpleCPTypeConstants.NAME,
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				_commerceCatalog.getGroupId());

			Assert.fail();
		}
		catch (NoSuchCProductException noSuchCProductException) {
			Assert.assertNotNull(noSuchCProductException);
		}

		CPDefinition cpDefinition = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			cpDefinition = _cpDefinitionLocalService.getOrAddEmptyCPDefinition(
				externalReferenceCode, SimpleCPTypeConstants.NAME,
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				_commerceCatalog.getGroupId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, cpDefinition.getStatus());
			Assert.assertEquals(
				externalReferenceCode,
				cpDefinition.getCProductExternalReferenceCode());
			Assert.assertEquals(
				SimpleCPTypeConstants.NAME, cpDefinition.getProductTypeName());
			Assert.assertEquals(1, cpDefinition.getVersion());
			Assert.assertFalse(cpDefinition.isPublished());

			CProduct cProduct = _cProductLocalService.getCProduct(
				cpDefinition.getCProductId());

			Assert.assertEquals(
				externalReferenceCode, cProduct.getExternalReferenceCode());
			Assert.assertEquals(
				cpDefinition.getCPDefinitionId(),
				cProduct.getPublishedCPDefinitionId());

			CPDefinition resolvedCPDefinition =
				_cpDefinitionLocalService.getOrAddEmptyCPDefinition(
					externalReferenceCode, SimpleCPTypeConstants.NAME,
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					_commerceCatalog.getGroupId());

			Assert.assertEquals(
				cpDefinition.getCPDefinitionId(),
				resolvedCPDefinition.getCPDefinitionId());
		}

		CPDefinition existingCPDefinition =
			_cpDefinitionLocalService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					externalReferenceCode, TestPropsValues.getCompanyId(),
					false);

		Assert.assertEquals(
			cpDefinition.getCPDefinitionId(),
			existingCPDefinition.getCPDefinitionId());

		Assert.assertNull(
			_cpDefinitionLocalService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					externalReferenceCode, TestPropsValues.getCompanyId(),
					true));

		long cpDefinitionId = cpDefinition.getCPDefinitionId();
		long cpTaxCategoryId = cpDefinition.getCPTaxCategoryId();

		Date displayDate = cpDefinition.getDisplayDate();

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinitionId, cpTaxCategoryId, false, false, null, 0,
			Collections.emptyMap(), displayDate.getDate(),
			displayDate.getHours(), displayDate.getMinutes(),
			displayDate.getMonth(), displayDate.getYear(), 0, 0, 0, 0, 0, true,
			0, false, Collections.emptyMap(), Collections.emptyMap(),
			Collections.emptyMap(),
			RandomTestUtil.randomLocaleStringMap(LocaleUtil.US), true, true,
			false, false, 0, Collections.emptyMap(), false, false,
			Collections.emptyMap(), 0, 0,
			ServiceContextTestUtil.getServiceContext(
				_commerceCatalog.getGroupId()));

		Assert.assertEquals(cpDefinitionId, cpDefinition.getCPDefinitionId());
		Assert.assertNotEquals(
			WorkflowConstants.STATUS_EMPTY, cpDefinition.getStatus());
		Assert.assertEquals(1, cpDefinition.getVersion());
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
			cpDefinitionId, "ERC");

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

	private void _testCopyCPDefinition() throws Exception {
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

		CPDefinition cpDefinition2 = _cpDefinitionLocalService.copyCPDefinition(
			cpDefinition1.getCPDefinitionId());

		Assert.assertEquals(
			cpDefinition1.getCPDefinitionId(),
			cpDefinition2.getCPDefinitionId());

		User user = UserTestUtil.addUser();

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 2, 1, 1, 2024, 0, 0, 0, 0, 0, 0,
				0, true, new ServiceContext());

		_cpConfigurationEntryLocalService.addCPConfigurationEntry(
			RandomTestUtil.randomString(), user.getUserId(),
			cpConfigurationList.getGroupId(),
			_portal.getClassNameId(CPDefinition.class),
			cpDefinition1.getCPDefinitionId(),
			cpConfigurationList.getCPConfigurationListId(), 0, "123.00", true,
			0, "cpde", 1.0, true, true, true, 1.0, "lowstoc", BigDecimal.TEN,
			BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, true, true, 1.0,
			true, true, 1.0, 1.0);

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

			CPDefinition cpDefinition3 =
				_cpDefinitionLocalService.copyCPDefinition(
					cpDefinitionSpecificationOptionValue1.getCPDefinitionId());

			Assert.assertNotEquals(
				cpDefinition1.getCPDefinitionId(),
				cpDefinition3.getCPDefinitionId());

			List<CPDefinitionSpecificationOptionValue>
				cpDefinitionSpecificationOptionValues =
					_cpDefinitionSpecificationOptionValueLocalService.
						getCPDefinitionSpecificationOptionValues(
							cpDefinition3.getCPDefinitionId(), null,
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
						cpDefinition3.getCPDefinitionId()));
		}
	}

	private void _testCopyCPDefinitionDoesNotCopyDraftCPDefinition()
		throws Exception {

		frutillaRule.scenario(
			"Do not copy a draft product definition"
		).given(
			"A draft product definition"
		).when(
			"the copy method is run"
		).then(
			"the draft product definition is returned"
		);

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

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
					cpDefinition1.getCPDefinitionId(),
					cpDefinition1.getGroupId(), WorkflowConstants.STATUS_DRAFT);

			Assert.assertEquals(
				WorkflowConstants.STATUS_DRAFT, cpDefinition2.getStatus());

			CPDefinition cpDefinition3 =
				_cpDefinitionLocalService.copyCPDefinition(
					cpDefinition2.getCPDefinitionId(),
					cpDefinition2.getGroupId(), WorkflowConstants.STATUS_DRAFT);

			Assert.assertEquals(
				cpDefinition2.getCPDefinitionId(),
				cpDefinition3.getCPDefinitionId());
		}
	}

	private void _testCopyCPDefinitionSetsExistingDraftToIncomplete()
		throws Exception {

		frutillaRule.scenario(
			"Set existing draft to incomplete when a new draft is created"
		).given(
			"A published product definition with an existing draft"
		).when(
			"a new draft is created from the published product definition"
		).then(
			"the existing draft is set to incomplete"
		).and(
			"the new draft is created"
		);

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpDefinition1.getStatus());

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
					cpDefinition1.getCPDefinitionId(),
					cpDefinition1.getGroupId(), WorkflowConstants.STATUS_DRAFT);

			Assert.assertEquals(
				WorkflowConstants.STATUS_DRAFT, cpDefinition2.getStatus());

			CPDefinition cpDefinition3 =
				_cpDefinitionLocalService.copyCPDefinition(
					cpDefinition1.getCPDefinitionId(),
					cpDefinition1.getGroupId(), WorkflowConstants.STATUS_DRAFT);

			Assert.assertEquals(
				WorkflowConstants.STATUS_DRAFT, cpDefinition3.getStatus());

			cpDefinition2 = _cpDefinitionLocalService.getCPDefinition(
				cpDefinition2.getCPDefinitionId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_INCOMPLETE, cpDefinition2.getStatus());
		}
	}

	private void _testCopyCPDefinitionWithSKUCombinations() throws Exception {
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

	private void _testGetCPDefinitions() throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null, null, true,
				new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(cpDefinitions.contains(cpDefinition));
	}

	private void _testGetCPDefinitionsOrderByComparator() throws Exception {
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

		Assert.assertTrue(
			cpDefinitions.indexOf(cpDefinition1) < cpDefinitions.indexOf(
				cpDefinition2));
	}

	private void _testGetCPDefinitionsOrderByLocalizedName() throws Exception {
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
			CPDefinition currentCPDefinition = cpDefinitions.get(i);
			CPDefinition previousCPDefinition = cpDefinitions.get(i - 1);

			String currentName = currentCPDefinition.getName(
				currentCPDefinition.getDefaultLanguageId());
			String previousName = previousCPDefinition.getName(
				previousCPDefinition.getDefaultLanguageId());

			Assert.assertTrue(
				previousName.compareToIgnoreCase(currentName) <= 0);
		}
	}

	private void _testGetCPDefinitionsWithAccountGroupFilterEnabledAndNoRel()
		throws Exception {

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

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		cpDefinition.setAccountGroupFilterEnabled(true);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(),
				accountEntry.getAccountEntryId(),
				_accountGroupLocalService.getAccountGroupIds(
					accountEntry.getAccountEntryId()),
				null, true, new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertFalse(cpDefinitions.contains(cpDefinition));
	}

	private void _testGetCPDefinitionsWithBothFiltersEnabledAndAllRels()
		throws Exception {

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

		cpDefinition.setAccountGroupFilterEnabled(true);
		cpDefinition.setChannelFilterEnabled(true);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), CPDefinition.class.getName(),
			cpDefinition.getCPDefinitionId());

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			commerceChannel.getCommerceChannelId(), _serviceContext);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(),
				accountEntry.getAccountEntryId(),
				new long[] {accountGroup.getAccountGroupId()},
				new long[] {commerceChannel.getGroupId()}, true,
				new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(cpDefinitions.contains(cpDefinition));
	}

	private void _testGetCPDefinitionsWithBothFiltersEnabledAndOnlyAccountGroupRel()
		throws Exception {

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

		cpDefinition.setAccountGroupFilterEnabled(true);
		cpDefinition.setChannelFilterEnabled(true);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), CPDefinition.class.getName(),
			cpDefinition.getCPDefinitionId());

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(),
				accountEntry.getAccountEntryId(),
				new long[] {accountGroup.getAccountGroupId()},
				new long[] {commerceChannel.getGroupId()}, true,
				new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertFalse(cpDefinitions.contains(cpDefinition));
	}

	private void _testGetCPDefinitionsWithBothFiltersEnabledAndOnlyChannelRel()
		throws Exception {

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

		cpDefinition.setAccountGroupFilterEnabled(true);
		cpDefinition.setChannelFilterEnabled(true);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			commerceChannel.getCommerceChannelId(), _serviceContext);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(),
				accountEntry.getAccountEntryId(),
				new long[] {accountGroup.getAccountGroupId()},
				new long[] {commerceChannel.getGroupId()}, true,
				new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertFalse(cpDefinitions.contains(cpDefinition));
	}

	private void _testGetCPDefinitionsWithChannelFilterEnabledAndNoRel()
		throws Exception {

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_commerceCatalog.getGroupId(), null);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		cpDefinition.setChannelFilterEnabled(true);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null,
				new long[] {commerceChannel.getGroupId()}, true,
				new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertFalse(cpDefinitions.contains(cpDefinition));
	}

	private void _testGetCPDefinitionsWithDraftStatus() throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		cpDefinition.setStatus(WorkflowConstants.STATUS_DRAFT);

		cpDefinition = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null, null, true,
				new int[] {WorkflowConstants.STATUS_DRAFT}, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertTrue(cpDefinitions.contains(cpDefinition));

		cpDefinitions = _cpDefinitionLocalService.getCPDefinitions(
			TestPropsValues.getCompanyId(), 0L, null, null, true,
			new int[] {WorkflowConstants.STATUS_APPROVED}, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertFalse(cpDefinitions.contains(cpDefinition));
	}

	private void _testGetCPDefinitionsWithEmptyStatuses() throws Exception {
		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		cpDefinition2.setStatus(WorkflowConstants.STATUS_DRAFT);

		cpDefinition2 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition2);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null, null, true,
				new int[0], QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(cpDefinitions.contains(cpDefinition1));
		Assert.assertTrue(cpDefinitions.contains(cpDefinition2));
	}

	private void _testGetCPDefinitionsWithIneligibleAccountEntry()
		throws Exception {

		AccountEntry eligibleAccountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null,
				_serviceContext);

		AccountEntry ineligibleAccountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null,
				_serviceContext);

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

		Assert.assertFalse(cpDefinitions.contains(cpDefinition));
	}

	private void _testGetCPDefinitionsWithInvalidCommerceChannelGroupId()
		throws Exception {

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		cpDefinition2.setChannelFilterEnabled(true);

		cpDefinition2 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition2);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null,
				new long[] {RandomTestUtil.randomLong()}, true,
				new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(cpDefinitions.contains(cpDefinition1));
		Assert.assertFalse(cpDefinitions.contains(cpDefinition2));
	}

	private void _testGetCPDefinitionsWithMultipleCommerceChannels()
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

		Assert.assertTrue(cpDefinitions.contains(cpDefinition));
	}

	private void _testGetCPDefinitionsWithPublishedFalse() throws Exception {
		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		cpDefinition2.setPublished(false);

		cpDefinition2 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition2);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null, null, false,
				new int[] {WorkflowConstants.STATUS_APPROVED},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(cpDefinitions.contains(cpDefinition1));
		Assert.assertTrue(cpDefinitions.contains(cpDefinition2));

		cpDefinitions = _cpDefinitionLocalService.getCPDefinitions(
			TestPropsValues.getCompanyId(), 0L, null, null, true,
			new int[] {WorkflowConstants.STATUS_APPROVED}, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertFalse(cpDefinitions.contains(cpDefinition2));
	}

	private void _testGetCPDefinitionsWithStatusAny() throws Exception {
		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		cpDefinition2.setStatus(WorkflowConstants.STATUS_DRAFT);

		cpDefinition2 = _cpDefinitionLocalService.updateCPDefinition(
			cpDefinition2);

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				TestPropsValues.getCompanyId(), 0L, null, null, true,
				new int[] {WorkflowConstants.STATUS_ANY}, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertTrue(cpDefinitions.contains(cpDefinition1));
		Assert.assertTrue(cpDefinitions.contains(cpDefinition2));
	}

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
	private CProductLocalService _cProductLocalService;

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