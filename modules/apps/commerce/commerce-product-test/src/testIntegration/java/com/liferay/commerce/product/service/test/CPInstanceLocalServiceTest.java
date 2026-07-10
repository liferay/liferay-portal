/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPInstanceConstants;
import com.liferay.commerce.product.exception.NoSuchCPInstanceException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalServiceUtil;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalServiceUtil;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalServiceUtil;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @author Igor Beslic
 */
@RunWith(Arquillian.class)
public class CPInstanceLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@Before
	public void setUp() throws Exception {
		_commerceCatalog = CommerceCatalogLocalServiceUtil.addCommerceCatalog(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(),
			ServiceContextTestUtil.getServiceContext(_company.getGroupId()));
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
	public void testActiveCPInstanceNewSkuContributorOptionValueAdded()
		throws Exception {

		int cpOptionsCount = 2;
		int cpOptionValuesCount = 2;

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_assertBuildCPInstancesSuccess(
			_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
			cpOptionsCount, cpOptionValuesCount);

		CPDefinitionOptionValueRel randomCPDefinitionOptionValueRel =
			CPTestUtil.getRandomCPDefinitionOptionValueRel(
				cpDefinition.getCPDefinitionId());

		CPDefinitionOptionRel cpDefinitionOptionRel =
			CPDefinitionOptionRelLocalServiceUtil.getCPDefinitionOptionRel(
				randomCPDefinitionOptionValueRel.getCPDefinitionOptionRelId());

		CPOptionValue cpOptionValue = CPTestUtil.addCPOptionValue(
			cpDefinitionOptionRel.getCPOption());

		CPDefinitionOptionValueRel newCPDefinitionOptionValueRel =
			CPDefinitionOptionValueRelLocalServiceUtil.
				addCPDefinitionOptionValueRel(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
					cpOptionValue,
					ServiceContextTestUtil.getServiceContext(
						_commerceCatalog.getGroupId()));

		int combinationsSize = (int)Math.pow(
			cpOptionValuesCount, cpOptionsCount);

		_assertApprovedCPInstancesCount(
			cpDefinition.getCPDefinitionId(), combinationsSize);

		List<CPInstance> cpDefinitionInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinition.getCPDefinitionId());

		_assertNoCPInstanceWithCPDefinitionOptionValue(
			newCPDefinitionOptionValueRel, cpDefinitionInstances);

		CPInstanceLocalServiceUtil.buildCPInstances(
			cpDefinition.getCPDefinitionId(),
			ServiceContextTestUtil.getServiceContext(
				_commerceCatalog.getGroupId()));

		_assertApprovedCPInstancesCount(
			cpDefinition.getCPDefinitionId(),
			combinationsSize +
				(int)Math.pow(cpOptionValuesCount, cpOptionsCount - 1));
	}

	@Test
	public void testBuildCPInstances() throws Exception {
		frutillaRule.scenario(
			"Build all product SKU combinations"
		).given(
			"I have a product definition"
		).when(
			"two SKU contributor options are added to definition"
		).and(
			"each option has three values"
		).and(
			"generate all product instance combinations is invoked"
		).then(
			"9 product instances are generated"
		).and(
			"all product instances are APPROVED"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_assertBuildCPInstancesSuccess(
			_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(), 2,
			3);
	}

	@Test
	public void testCPInstanceActiveNewNonskuContributorOptionAdded()
		throws Exception {

		frutillaRule.scenario(
			"Product SKU will stay ACTIVE if new non SKU contributor option " +
				"added"
		).given(
			StringBundler.concat(
				"I have a product definition with one SKU contributor option ",
				"with three values assigned to it. There is product instance ",
				"A that refers to first option value")
		).when(
			"new non SKU contributor option is added to definition"
		).and(
			"option has three values"
		).then(
			"product instance A should stay activated"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			CPTestUtil.addCPDefinitionOptionRel(
				_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
				true, 2);

		Assert.assertTrue(
			"SKU contributor value", cpDefinitionOptionRel.isSkuContributor());

		_addSingleCPDefinitionInstance(cpDefinition, cpDefinitionOptionRel);

		_assertApprovedCPInstancesCount(cpDefinition.getCPDefinitionId(), 1);

		List<CPInstance> approvedCPDefinitionInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinition.getCPDefinitionId());

		CPInstance approvedCPInstance = approvedCPDefinitionInstances.get(0);

		CPDefinitionOptionRel newCPDefinitionOptionRel =
			CPTestUtil.addCPDefinitionOptionRel(
				_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
				false, 2);

		Assert.assertFalse(
			"SKU contributor value",
			newCPDefinitionOptionRel.isSkuContributor());

		_assertApprovedCPInstancesCount(cpDefinition.getCPDefinitionId(), 1);

		approvedCPInstance = _cpInstanceLocalService.getCPInstance(
			approvedCPInstance.getCPInstanceId());

		Assert.assertEquals(
			"Product instance A status", WorkflowConstants.STATUS_APPROVED,
			approvedCPInstance.getStatus());
	}

	@Test
	public void testDefaultCPInstanceNewNonskuContributorOptionAdded()
		throws Exception {

		frutillaRule.scenario(
			"Default product SKU will stay ACTIVE if new non SKU contributor " +
				"option added"
		).given(
			StringBundler.concat(
				"I have a product definition with one non SKU contributor ",
				"option with three values assigned to it. There is product ",
				"default instance active")
		).when(
			"new non SKU contributor option is added to definition"
		).and(
			"option has three values"
		).then(
			"product default instance should stay activated"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			CPTestUtil.addCPDefinitionOptionRel(
				_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
				false, 3);

		Assert.assertFalse(
			"SKU contributor value", cpDefinitionOptionRel.isSkuContributor());

		_assertDefaultCPInstance(cpDefinition.getCPDefinitionId());

		cpDefinitionOptionRel = CPTestUtil.addCPDefinitionOptionRel(
			_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
			false, 3);

		Assert.assertFalse(
			"SKU contributor value", cpDefinitionOptionRel.isSkuContributor());

		_assertDefaultCPInstance(cpDefinition.getCPDefinitionId());
	}

	@Test
	public void testGetOrAddEmptyCPInstance() throws Exception {
		frutillaRule.scenario(
			"Get or add an empty product instance"
		).given(
			"A product definition and an external reference code"
		).when(
			"An empty product instance is requested"
		).then(
			"A NoSuchCPInstanceException is thrown while lazy referencing is " +
				"disabled"
		).and(
			"An empty stub with the given external reference code is " +
				"returned while lazy referencing is enabled"
		).and(
			"The same product instance is resolved on subsequent requests"
		).and(
			"The empty status is cleared once the stub is updated"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId(), true, false,
			WorkflowConstants.ACTION_PUBLISH);

		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			_cpInstanceLocalService.getOrAddEmptyCPInstance(
				externalReferenceCode, cpDefinition.getCPDefinitionId(),
				cpDefinition.getGroupId(), cpDefinition.getCompanyId(),
				cpDefinition.getUserId());

			Assert.fail();
		}
		catch (NoSuchCPInstanceException noSuchCPInstanceException) {
			Assert.assertNotNull(noSuchCPInstanceException);
		}

		CPInstance cpInstance;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			cpInstance = _cpInstanceLocalService.getOrAddEmptyCPInstance(
				externalReferenceCode, cpDefinition.getCPDefinitionId(),
				cpDefinition.getGroupId(), cpDefinition.getCompanyId(),
				cpDefinition.getUserId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, cpInstance.getStatus());
			Assert.assertEquals(
				externalReferenceCode, cpInstance.getExternalReferenceCode());
			Assert.assertEquals(externalReferenceCode, cpInstance.getSku());
			Assert.assertEquals(
				cpDefinition.getCPDefinitionId(),
				cpInstance.getCPDefinitionId());

			CPInstance resolvedCPInstance =
				_cpInstanceLocalService.getOrAddEmptyCPInstance(
					externalReferenceCode, cpDefinition.getCPDefinitionId(),
					cpDefinition.getGroupId(), cpDefinition.getCompanyId(),
					cpDefinition.getUserId());

			Assert.assertEquals(
				cpInstance.getCPInstanceId(),
				resolvedCPInstance.getCPInstanceId());
		}

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		cpInstance = _cpInstanceLocalService.updateCPInstance(
			externalReferenceCode, cpInstance.getCPInstanceId(),
			RandomTestUtil.randomString(), null, null, false, 0, 0, 0, 0,
			BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
			calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
			calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
			calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, false, false, 0,
			null, null, 0, false, 0, null, null, 0, null, false, null, 0, 0, 0,
			0,
			ServiceContextTestUtil.getServiceContext(
				cpDefinition.getGroupId()));

		Assert.assertNotEquals(
			WorkflowConstants.STATUS_EMPTY, cpInstance.getStatus());
	}

	@Test
	public void testGetReplacementCPInstances() throws Exception {
		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPInstance replacementCPInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinition replacementCPDefinition = cpInstance.getCPDefinition();

		cpInstance.setReplacementCPInstanceUuid(
			replacementCPInstance.getCPInstanceUuid());
		cpInstance.setReplacementCProductId(
			replacementCPDefinition.getCProductId());

		_cpInstanceLocalService.updateCPInstance(cpInstance);

		List<CPInstance> cpInstances = _cpInstanceLocalService.getCPInstances(
			replacementCPInstance.getCPInstanceUuid(),
			replacementCPDefinition.getCProductId(),
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(cpInstances.toString(), 1, cpInstances.size());
	}

	@Test
	public void testInactivateCPInstanceCPInstanceWithSameOptionAdded()
		throws Exception {

		frutillaRule.scenario(
			"Inactivate stale product instance"
		).given(
			StringBundler.concat(
				"I have a product definition with one SKU contributor option ",
				"with three values assigned to it. There is product instance ",
				"A that refers to first option value")
		).when(
			"new product instance B is added to definition"
		).and(
			"instance B has option equals to instance A"
		).then(
			"product instance A should be inactivated"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			CPTestUtil.addCPDefinitionOptionRel(
				_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
				true, 2);

		Map<Long, List<Long>>
			cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds =
				HashMapBuilder.<Long, List<Long>>put(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
					() -> {
						List<CPDefinitionOptionValueRel>
							cpDefinitionOptionValueRels =
								cpDefinitionOptionRel.
									getCPDefinitionOptionValueRels();

						CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
							cpDefinitionOptionValueRels.get(0);

						return Arrays.asList(
							cpDefinitionOptionValueRel.
								getCPDefinitionOptionValueRelId());
					}
				).build();

		CPInstance cpInstanceA = CPTestUtil.addCPDefinitionCPInstance(
			cpDefinition.getCPDefinitionId(),
			cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds);

		Assert.assertEquals(
			"Product instance A approved", WorkflowConstants.STATUS_APPROVED,
			cpInstanceA.getStatus());

		CPInstance cpInstanceB = CPTestUtil.addCPDefinitionCPInstance(
			cpDefinition.getCPDefinitionId(),
			cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds);

		Assert.assertNotEquals(
			"Product instance A is not equal to product instance B",
			cpInstanceA.getCPInstanceId(), cpInstanceB.getCPInstanceId());

		_assertApprovedCPInstancesCount(cpDefinition.getCPDefinitionId(), 1);

		List<CPInstance> activeCPDefinitionInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinition.getCPDefinitionId());

		CPInstance activeCPInstance = activeCPDefinitionInstances.get(0);

		Assert.assertEquals(
			"Product instance B is active", cpInstanceB.getCPInstanceId(),
			activeCPInstance.getCPInstanceId());
	}

	@Test
	public void testInactivateCPInstanceNewSKUContributorOptionAdded()
		throws Exception {

		frutillaRule.scenario(
			"Inactivate product with stale option combination"
		).given(
			StringBundler.concat(
				"I have a product definition with one SKU contributor option ",
				"with three values assigned to it. There is product instance ",
				"A that refers to first option value")
		).when(
			"new SKU contributor option is added to definition"
		).and(
			"option has three values"
		).then(
			"product instance A should be inactivated"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			CPTestUtil.addCPDefinitionOptionRel(
				_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
				true, 2);

		CPTestUtil.addCPDefinitionCPInstance(
			cpDefinition.getCPDefinitionId(),
			HashMapBuilder.<Long, List<Long>>put(
				cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
				() -> {
					List<CPDefinitionOptionValueRel>
						cpDefinitionOptionValueRels =
							cpDefinitionOptionRel.
								getCPDefinitionOptionValueRels();

					CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
						cpDefinitionOptionValueRels.get(0);

					return Arrays.asList(
						cpDefinitionOptionValueRel.
							getCPDefinitionOptionValueRelId());
				}
			).build());

		List<CPInstance> inactiveCPDefinitionInstances =
			_cpInstanceLocalService.getCPDefinitionInstances(
				cpDefinition.getCPDefinitionId(),
				WorkflowConstants.STATUS_INACTIVE, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			"Product inactive instances count", 1,
			inactiveCPDefinitionInstances.size());

		_assertApprovedCPInstancesCount(cpDefinition.getCPDefinitionId(), 1);

		List<CPInstance> approvedCPDefinitionInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinition.getCPDefinitionId());

		CPInstance cpInstance = approvedCPDefinitionInstances.get(0);

		CPTestUtil.addCPDefinitionOptionRel(
			_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
			true, 2);

		_assertApprovedCPInstancesCount(cpDefinition.getCPDefinitionId(), 0);

		inactiveCPDefinitionInstances =
			_cpInstanceLocalService.getCPDefinitionInstances(
				cpDefinition.getCPDefinitionId(),
				WorkflowConstants.STATUS_INACTIVE, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			"Product inactive instances count", 2,
			inactiveCPDefinitionInstances.size());

		cpInstance = _cpInstanceLocalService.getCPInstance(
			cpInstance.getCPInstanceId());

		Assert.assertEquals(
			"Product instance A status", WorkflowConstants.STATUS_INACTIVE,
			cpInstance.getStatus());
	}

	@Test
	public void testInactivateCPInstanceSkuContributorOptionDeleted()
		throws Exception {

		int cpOptionsCount = 2;
		int cpOptionValuesCount = 2;

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_assertBuildCPInstancesSuccess(
			_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
			cpOptionsCount, cpOptionValuesCount);

		CPDefinitionOptionValueRel randomCPDefinitionOptionValueRel =
			CPTestUtil.getRandomCPDefinitionOptionValueRel(
				cpDefinition.getCPDefinitionId());

		CPDefinitionOptionRelLocalServiceUtil.deleteCPDefinitionOptionRel(
			randomCPDefinitionOptionValueRel.getCPDefinitionOptionRelId());

		_assertApprovedCPInstancesCount(cpDefinition.getCPDefinitionId(), 0);
	}

	@Test
	public void testInactivateCPInstanceSkuContributorOptionUpdate()
		throws Exception {

		int cpOptionsCount = 2;
		int cpOptionValuesCount = 2;

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_assertBuildCPInstancesSuccess(
			_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
			cpOptionsCount, cpOptionValuesCount);

		CPDefinitionOptionValueRel randomCPDefinitionOptionValueRel =
			CPTestUtil.getRandomCPDefinitionOptionValueRel(
				cpDefinition.getCPDefinitionId());

		CPDefinitionOptionRel cpDefinitionOptionRel =
			randomCPDefinitionOptionValueRel.getCPDefinitionOptionRel();

		Assert.assertTrue(
			"SKU contributor value", cpDefinitionOptionRel.isSkuContributor());

		cpDefinitionOptionRel =
			CPDefinitionOptionRelLocalServiceUtil.updateCPDefinitionOptionRel(
				cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
				cpDefinitionOptionRel.getCPOptionId(),
				cpDefinitionOptionRel.getNameMap(),
				cpDefinitionOptionRel.getDescriptionMap(),
				cpDefinitionOptionRel.getCommerceOptionTypeKey(),
				cpDefinitionOptionRel.getPriority(),
				cpDefinitionOptionRel.isFacetable(),
				cpDefinitionOptionRel.isRequired(), false,
				ServiceContextTestUtil.getServiceContext(
					_commerceCatalog.getGroupId()));

		Assert.assertFalse(
			"SKU contributor value", cpDefinitionOptionRel.isSkuContributor());

		_assertApprovedCPInstancesCount(cpDefinition.getCPDefinitionId(), 0);

		_cpInstanceLocalService.buildCPInstances(
			cpDefinition.getCPDefinitionId(),
			ServiceContextTestUtil.getServiceContext(
				_commerceCatalog.getGroupId()));

		_assertApprovedCPInstancesCount(
			cpDefinition.getCPDefinitionId(), cpOptionValuesCount);

		cpDefinitionOptionRel =
			CPDefinitionOptionRelLocalServiceUtil.updateCPDefinitionOptionRel(
				cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
				cpDefinitionOptionRel.getCPOptionId(),
				cpDefinitionOptionRel.getNameMap(),
				cpDefinitionOptionRel.getDescriptionMap(),
				cpDefinitionOptionRel.getCommerceOptionTypeKey(),
				cpDefinitionOptionRel.getPriority(),
				cpDefinitionOptionRel.isFacetable(),
				cpDefinitionOptionRel.isRequired(), true,
				ServiceContextTestUtil.getServiceContext(
					_commerceCatalog.getGroupId()));

		Assert.assertTrue(
			"SKU contributor value", cpDefinitionOptionRel.isSkuContributor());

		_assertApprovedCPInstancesCount(cpDefinition.getCPDefinitionId(), 0);
	}

	@Test
	public void testInactivateCPInstanceSkuContributorOptionValueDeleted()
		throws Exception {

		int cpOptionsCount = 2;
		int cpOptionValuesCount = 2;

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_assertBuildCPInstancesSuccess(
			_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
			cpOptionsCount, cpOptionValuesCount);

		CPDefinitionOptionValueRel deletedCPDefinitionOptionValueRel =
			CPDefinitionOptionValueRelLocalServiceUtil.
				deleteCPDefinitionOptionValueRel(
					CPTestUtil.getRandomCPDefinitionOptionValueRel(
						cpDefinition.getCPDefinitionId()));

		_assertApprovedCPInstancesCount(
			cpDefinition.getCPDefinitionId(),
			(int)Math.pow(cpOptionValuesCount, cpOptionsCount - 1) *
				(cpOptionValuesCount - 1));

		_assertNoCPInstanceWithCPDefinitionOptionValue(
			deletedCPDefinitionOptionValueRel,
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinition.getCPDefinitionId()));
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private CPInstance _addSingleCPDefinitionInstance(
			CPDefinition cpDefinition,
			CPDefinitionOptionRel... cpDefinitionOptionRels)
		throws Exception {

		Map<Long, List<Long>>
			cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds =
				new HashMap<>();

		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				cpDefinitionOptionRels) {

			for (CPDefinitionOptionValueRel cpDefinitionOptionValueRel :
					cpDefinitionOptionRel.getCPDefinitionOptionValueRels()) {

				cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds.put(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
					Arrays.asList(
						cpDefinitionOptionValueRel.
							getCPDefinitionOptionValueRelId()));

				break;
			}
		}

		return CPTestUtil.addCPDefinitionCPInstance(
			cpDefinition.getCPDefinitionId(),
			cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds);
	}

	private void _assertApprovedCPInstancesCount(
		long cpDefinitionId, int size) {

		List<CPInstance> approvedCPDefinitionInstances =
			_cpInstanceLocalService.getCPDefinitionInstances(
				cpDefinitionId, WorkflowConstants.STATUS_APPROVED,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			"Product approved instances count", size,
			approvedCPDefinitionInstances.size());
	}

	private void _assertBuildCPInstancesSuccess(
			long groupId, long cpDefinitionId, int cpOptionsCount,
			int cpOptionValuesCount)
		throws Exception {

		CPTestUtil.addCPOption(
			_commerceCatalog.getGroupId(), cpDefinitionId, cpOptionsCount,
			cpOptionValuesCount);

		_cpInstanceLocalService.buildCPInstances(
			cpDefinitionId, ServiceContextTestUtil.getServiceContext(groupId));

		_assertApprovedCPInstancesCount(
			cpDefinitionId, (int)Math.pow(cpOptionValuesCount, cpOptionsCount));
	}

	private void _assertDefaultCPInstance(long cpDefinitionId) {
		_assertApprovedCPInstancesCount(cpDefinitionId, 1);

		List<CPInstance> approvedCPDefinitionInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinitionId);

		CPInstance defaultCPInstance = approvedCPDefinitionInstances.get(0);

		Assert.assertEquals(
			"Product instance sku", CPInstanceConstants.DEFAULT_SKU,
			defaultCPInstance.getSku());

		Assert.assertEquals(
			"Product instance status", WorkflowConstants.STATUS_APPROVED,
			defaultCPInstance.getStatus());
	}

	private void _assertNoCPInstanceWithCPDefinitionOptionValue(
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel,
			List<CPInstance> cpInstances)
		throws Exception {

		for (CPInstance cpInstance : cpInstances) {
			CPDefinitionOptionValueRel cpInstanceCPDefinitionOptionValueRel =
				CPDefinitionOptionValueRelLocalServiceUtil.
					getCPInstanceCPDefinitionOptionValueRel(
						cpDefinitionOptionValueRel.getCPDefinitionOptionRelId(),
						cpInstance.getCPInstanceId());

			Assert.assertNotEquals(
				"CP instance definition option value",
				cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId(),
				cpInstanceCPDefinitionOptionValueRel.
					getCPDefinitionOptionValueRelId());
		}
	}

	private static Company _company;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

}