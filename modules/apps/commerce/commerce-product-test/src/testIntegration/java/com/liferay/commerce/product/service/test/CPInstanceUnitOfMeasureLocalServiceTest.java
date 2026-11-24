/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureIncrementalOrderQuantityException;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureKeyException;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureRateException;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureSkuException;
import com.liferay.commerce.product.exception.DuplicateCPInstanceUnitOfMeasureKeyException;
import com.liferay.commerce.product.exception.NoSuchCPInstanceUnitOfMeasureException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.List;
import java.util.Locale;
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
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CPInstanceUnitOfMeasureLocalServiceTest {

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
	public void setUp() throws PortalException {
		_commerceCatalog = CommerceCatalogLocalServiceUtil.addCommerceCatalog(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(),
			ServiceContextTestUtil.getServiceContext(_company.getGroupId()));
	}

	@After
	public void tearDown() throws PortalException {
		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				_commerceCatalog.getGroupId(), WorkflowConstants.STATUS_ANY,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CPDefinition cpDefinition : cpDefinitions) {
			_cpDefinitionLocalService.deleteCPDefinition(cpDefinition);
		}
	}

	@Test
	public void testAddCPInstanceUnitOfMeasure() throws Exception {
		_testAddCPInstanceUnitOfMeasure();
		_testAddCPInstanceUnitOfMeasureWithDuplicateKey();
		_testAddCPInstanceUnitOfMeasureWithPrimary();
		_testAddCPInstanceUnitOfMeasureWithSpecialCharactersName();
		_testAddCPInstanceUnitOfMeasureWithoutIncrementalOrderQuantity();
		_testAddCPInstanceUnitOfMeasureWithoutKey();
		_testAddCPInstanceUnitOfMeasureWithoutRate();
		_testAddCPInstanceUnitOfMeasureWithoutSku();
	}

	@Test
	public void testUpdateCPInstanceUnitOfMeasure() throws Exception {
		_testUpdateCPInstanceUnitOfMeasure();
		_testUpdateCPInstanceUnitOfMeasureWithDifferentCPInstance();
		_testUpdateCPInstanceUnitOfMeasureWithSpecialCharactersName();
		_testUpdateCPInstanceUnitOfMeasureWithoutIncrementalOrderQuantity();
		_testUpdateCPInstanceUnitOfMeasureWithoutKey();
		_testUpdateCPInstanceUnitOfMeasureWithoutRate();
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private CPInstanceUnitOfMeasure _getCpInstanceUnitOfMeasure(
			CPInstance cpInstance)
		throws PortalException {

		return _cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
			_user.getUserId(), cpInstance.getCPInstanceId(), true,
			BigDecimal.TEN, "KEY",
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "NAME"
			).build(),
			2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE, cpInstance.getSku());
	}

	private void _testAddCPInstanceUnitOfMeasure() throws Exception {
		frutillaRule.scenario(
			"Create a new Unit of Measure for a CPInstance"
		).given(
			"I have a product definition with a default SKU"
		).when(
			"UnitOfMeasure is added"
		).then(
			"UnitOfMeasure is actually created."
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		Assert.assertNotNull(_getCpInstanceUnitOfMeasure(cpInstances.get(0)));
	}

	private void _testAddCPInstanceUnitOfMeasureWithDuplicateKey()
		throws Exception {

		frutillaRule.scenario(
			"Create two new Unit of Measure for a CPInstance"
		).given(
			"I have a product definition with a default SKU"
		).when(
			"two Unit Of Measures are added"
		).and(
			"Both Unit Of Measures are set with same key value"
		).then(
			"An exception is thrown"
		).and(
			"The second Unit Of Measure is not created"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		_getCpInstanceUnitOfMeasure(cpInstance);

		Assert.assertThrows(
			DuplicateCPInstanceUnitOfMeasureKeyException.class,
			() -> _getCpInstanceUnitOfMeasure(cpInstance));

		List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
			_cpInstanceUnitOfMeasureLocalService.getCPInstanceUnitOfMeasures(
				cpInstance.getCPInstanceId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			cpInstanceUnitOfMeasures.toString(), 1,
			cpInstanceUnitOfMeasures.size());
	}

	private void _testAddCPInstanceUnitOfMeasureWithPrimary() throws Exception {
		frutillaRule.scenario(
			"Create two new Unit of Measure for a CPInstance"
		).given(
			"I have a product definition with a default SKU"
		).when(
			"two Unit Of Measures are added"
		).and(
			"Both Unit Of Measures are set to be primary"
		).then(
			"Both Unit Of Measures are created"
		).and(
			"Only one is primary."
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), "NAME"
		).build();

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure1 =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), cpInstance.getCPInstanceId(), true,
				BigDecimal.TEN, "KEY1", nameMap, 2, BigDecimal.ZERO, true, 0.0,
				BigDecimal.ONE, cpInstance.getSku());

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure2 =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), cpInstance.getCPInstanceId(), true,
				BigDecimal.TEN, "KEY2", nameMap, 2, BigDecimal.ZERO, true, 0.0,
				BigDecimal.ONE, cpInstance.getSku());

		List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
			_cpInstanceUnitOfMeasureLocalService.getCPInstanceUnitOfMeasures(
				cpInstance.getCPInstanceId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			cpInstanceUnitOfMeasures.toString(), 2,
			cpInstanceUnitOfMeasures.size());

		int primaryCPInstanceUnitOfMeasureCount = 0;

		for (CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure :
				cpInstanceUnitOfMeasures) {

			if (!cpInstanceUnitOfMeasure.isPrimary()) {
				continue;
			}

			primaryCPInstanceUnitOfMeasureCount++;
		}

		Assert.assertEquals(1, primaryCPInstanceUnitOfMeasureCount);

		cpInstanceUnitOfMeasure1 =
			_cpInstanceUnitOfMeasureLocalService.updateCPInstanceUnitOfMeasure(
				cpInstanceUnitOfMeasure1.getCPInstanceUnitOfMeasureId(),
				cpInstance.getCPInstanceId(), true, BigDecimal.ONE, "KEY1",
				nameMap, 2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
				cpInstance.getSku());

		Assert.assertTrue("Primary", cpInstanceUnitOfMeasure1.isPrimary());

		cpInstanceUnitOfMeasure2 =
			_cpInstanceUnitOfMeasureLocalService.getCPInstanceUnitOfMeasure(
				cpInstanceUnitOfMeasure2.getCPInstanceUnitOfMeasureId());

		Assert.assertTrue("Primary", !cpInstanceUnitOfMeasure2.isPrimary());
	}

	private void _testAddCPInstanceUnitOfMeasureWithSpecialCharactersName()
		throws Exception {

		frutillaRule.scenario(
			"Create a new UOM for a CPInstance"
		).given(
			"A product definition with a default SKU"
		).when(
			"Unit Of Measure is added"
		).then(
			"UOM name is added with special characters"
		).and(
			"UOM name is created and converted without special characters."
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), cpInstance.getCPInstanceId(), true,
				BigDecimal.TEN, "KEY",
				HashMapBuilder.put(
					LocaleUtil.getDefault(), "<Test>Unit of Measure"
				).build(),
				2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
				cpInstance.getSku());

		Assert.assertNotNull(cpInstanceUnitOfMeasure);

		Assert.assertEquals(
			"Unit of Measure",
			cpInstanceUnitOfMeasure.getName(LocaleUtil.getDefault()));
	}

	private void _testAddCPInstanceUnitOfMeasureWithoutIncrementalOrderQuantity()
		throws Exception {

		frutillaRule.scenario(
			"Create a new Unit of Measure for a CPInstance"
		).given(
			"I have a product definition with a default SKU"
		).when(
			"Unit Of Measure is added without base decimal quantity"
		).then(
			"Unit Of Measure is not created."
		).and(
			"An exception is thrown"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		Assert.assertThrows(
			CPInstanceUnitOfMeasureIncrementalOrderQuantityException.class,
			() ->
				_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
					_user.getUserId(), cpInstance.getCPInstanceId(), true, null,
					"KEY",
					HashMapBuilder.put(
						LocaleUtil.getDefault(), "NAME"
					).build(),
					2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
					cpInstance.getSku()));

		List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
			_cpInstanceUnitOfMeasureLocalService.getCPInstanceUnitOfMeasures(
				cpInstance.getCPInstanceId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertTrue(cpInstanceUnitOfMeasures.isEmpty());
	}

	private void _testAddCPInstanceUnitOfMeasureWithoutKey() throws Exception {
		frutillaRule.scenario(
			"Create a new Unit of Measure for a CPInstance"
		).given(
			"I have a product definition with a default SKU"
		).when(
			"Unit Of Measure is added without the key"
		).then(
			"Unit Of Measure is not created."
		).and(
			"An exception is thrown"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		Assert.assertThrows(
			CPInstanceUnitOfMeasureKeyException.class,
			() ->
				_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
					_user.getUserId(), cpInstance.getCPInstanceId(), true,
					BigDecimal.TEN, null,
					HashMapBuilder.put(
						LocaleUtil.getDefault(), "NAME"
					).build(),
					2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
					cpInstance.getSku()));

		List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
			_cpInstanceUnitOfMeasureLocalService.getCPInstanceUnitOfMeasures(
				cpInstance.getCPInstanceId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertTrue(cpInstanceUnitOfMeasures.isEmpty());
	}

	private void _testAddCPInstanceUnitOfMeasureWithoutRate() throws Exception {
		frutillaRule.scenario(
			"Create a new Unit of Measure for a CPInstance"
		).given(
			"I have a product definition with a default SKU"
		).when(
			"Unit Of Measure is added without the rate"
		).then(
			"Unit Of Measure is not created."
		).and(
			"An exception is thrown"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		Assert.assertThrows(
			CPInstanceUnitOfMeasureRateException.class,
			() ->
				_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
					_user.getUserId(), cpInstance.getCPInstanceId(), true,
					BigDecimal.TEN, "KEY",
					HashMapBuilder.put(
						LocaleUtil.getDefault(), "NAME"
					).build(),
					2, BigDecimal.ZERO, true, 0.0, null, cpInstance.getSku()));

		List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
			_cpInstanceUnitOfMeasureLocalService.getCPInstanceUnitOfMeasures(
				cpInstance.getCPInstanceId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertTrue(cpInstanceUnitOfMeasures.isEmpty());
	}

	private void _testAddCPInstanceUnitOfMeasureWithoutSku() throws Exception {
		frutillaRule.scenario(
			"Create a new Unit of Measure for a CPInstance"
		).given(
			"I have a product definition with a default SKU"
		).when(
			"Unit Of Measure is added without the sku"
		).then(
			"Unit Of Measure is not created."
		).and(
			"An exception is thrown"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		Assert.assertThrows(
			CPInstanceUnitOfMeasureSkuException.class,
			() ->
				_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
					_user.getUserId(), cpInstance.getCPInstanceId(), true,
					BigDecimal.TEN, "KEY",
					HashMapBuilder.put(
						LocaleUtil.getDefault(), "NAME"
					).build(),
					2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE, null));

		List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
			_cpInstanceUnitOfMeasureLocalService.getCPInstanceUnitOfMeasures(
				cpInstance.getCPInstanceId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertTrue(cpInstanceUnitOfMeasures.isEmpty());
	}

	private void _testUpdateCPInstanceUnitOfMeasure() throws Exception {
		frutillaRule.scenario(
			"Update fields of a Unit of Measure"
		).given(
			"I have a product definition with a default SKU"
		).when(
			"UnitOfMeasure is added"
		).and(
			"UnitOfMeasure is updated in all of its fields"
		).then(
			"Every fields are saved with new values"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			_getCpInstanceUnitOfMeasure(cpInstances.get(0));

		Assert.assertNotNull(cpInstanceUnitOfMeasure);

		cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.updateCPInstanceUnitOfMeasure(
				cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId(),
				cpInstanceUnitOfMeasure.getCPInstanceId(), false,
				BigDecimal.ONE, "KEY2",
				HashMapBuilder.put(
					LocaleUtil.getDefault(), "NAME 2"
				).build(),
				3, BigDecimal.ONE, false, 1.0, BigDecimal.TEN,
				cpInstanceUnitOfMeasure.getSku());

		Assert.assertNotNull(cpInstanceUnitOfMeasure);
		Assert.assertEquals(
			"Active", false, cpInstanceUnitOfMeasure.isActive());

		BigDecimal bigDecimal = new BigDecimal(1);

		Assert.assertEquals(
			"Incremental order quantity",
			bigDecimal.setScale(3, RoundingMode.HALF_UP),
			cpInstanceUnitOfMeasure.getIncrementalOrderQuantity());

		Assert.assertEquals("KEY", "KEY2", cpInstanceUnitOfMeasure.getKey());
		Assert.assertEquals(
			"Name", "NAME 2",
			cpInstanceUnitOfMeasure.getName(LocaleUtil.getDefault()));
		Assert.assertEquals(
			"Precision", 3, cpInstanceUnitOfMeasure.getPrecision());
		Assert.assertEquals(
			"Primary", false, cpInstanceUnitOfMeasure.isPrimary());
		Assert.assertEquals(
			"Priority", Double.valueOf(1.0),
			Double.valueOf(cpInstanceUnitOfMeasure.getPriority()));
		Assert.assertEquals(
			"Rate", BigDecimal.TEN, cpInstanceUnitOfMeasure.getRate());
	}

	private void _testUpdateCPInstanceUnitOfMeasureWithDifferentCPInstance()
		throws Exception {

		frutillaRule.scenario(
			"Update a Unit of Measure with a different SKU"
		).given(
			"I have two product definitions with a default SKU"
		).when(
			"Unit Of Measure is added to the first SKU"
		).then(
			"Update the Unit Of Measure with the second SKU"
		).and(
			"An exception is thrown"
		);

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances1 = cpDefinition1.getCPInstances();

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			_getCpInstanceUnitOfMeasure(cpInstances1.get(0));

		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances2 = cpDefinition2.getCPInstances();

		CPInstance cpInstance2 = cpInstances2.get(0);

		Assert.assertThrows(
			NoSuchCPInstanceUnitOfMeasureException.class,
			() ->
				_cpInstanceUnitOfMeasureLocalService.
					updateCPInstanceUnitOfMeasure(
						cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId(),
						cpInstance2.getCPInstanceId(), true, BigDecimal.TEN,
						RandomTestUtil.randomString(),
						HashMapBuilder.put(
							LocaleUtil.getDefault(),
							RandomTestUtil.randomString()
						).build(),
						2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
						cpInstanceUnitOfMeasure.getSku()));
	}

	private void _testUpdateCPInstanceUnitOfMeasureWithSpecialCharactersName()
		throws Exception {

		frutillaRule.scenario(
			"Create a new UOM for a CPInstance"
		).given(
			"I have a product definition with a default SKU"
		).when(
			"Unit Of Measure is created"
		).then(
			"UOM name is update with special characters"
		).and(
			"UOM name is converted without special characters."
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), cpInstance.getCPInstanceId(), true,
				BigDecimal.TEN, "KEY",
				HashMapBuilder.put(
					LocaleUtil.getDefault(), "Name"
				).build(),
				2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
				cpInstance.getSku());

		Assert.assertNotNull(cpInstanceUnitOfMeasure);

		cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.updateCPInstanceUnitOfMeasure(
				cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId(),
				cpInstanceUnitOfMeasure.getCPInstanceId(), true, BigDecimal.TEN,
				"KEY",
				HashMapBuilder.put(
					LocaleUtil.getDefault(), "<Test>Unit of Measure"
				).build(),
				2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
				cpInstance.getSku());

		Assert.assertEquals(
			"Unit of Measure",
			cpInstanceUnitOfMeasure.getName(LocaleUtil.getDefault()));
	}

	private void _testUpdateCPInstanceUnitOfMeasureWithoutIncrementalOrderQuantity()
		throws Exception {

		frutillaRule.scenario(
			"Try to set Incremental Order Quantity to null"
		).given(
			"I have a product definition with a default SKU"
		).when(
			"Unit Of Measure is added"
		).then(
			"Unit Of Measure is updated without without the incremental " +
				"order quantity"
		).and(
			"An exception is thrown"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure1 =
			_getCpInstanceUnitOfMeasure(cpInstances.get(0));

		Assert.assertThrows(
			CPInstanceUnitOfMeasureIncrementalOrderQuantityException.class,
			() ->
				_cpInstanceUnitOfMeasureLocalService.
					updateCPInstanceUnitOfMeasure(
						cpInstanceUnitOfMeasure1.getCPInstanceUnitOfMeasureId(),
						cpInstanceUnitOfMeasure1.getCPInstanceId(), true, null,
						cpInstanceUnitOfMeasure1.getKey(),
						HashMapBuilder.put(
							LocaleUtil.getDefault(), "NAME"
						).build(),
						cpInstanceUnitOfMeasure1.getPrecision(), BigDecimal.ONE,
						cpInstanceUnitOfMeasure1.isPrimary(),
						cpInstanceUnitOfMeasure1.getPriority(),
						cpInstanceUnitOfMeasure1.getRate(),
						cpInstanceUnitOfMeasure1.getSku()));

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure2 =
			_cpInstanceUnitOfMeasureLocalService.getCPInstanceUnitOfMeasure(
				cpInstanceUnitOfMeasure1.getCPInstanceUnitOfMeasureId());

		Assert.assertEquals(
			"Incremental order quantity",
			cpInstanceUnitOfMeasure1.getIncrementalOrderQuantity(),
			cpInstanceUnitOfMeasure2.getIncrementalOrderQuantity());
	}

	private void _testUpdateCPInstanceUnitOfMeasureWithoutKey()
		throws Exception {

		frutillaRule.scenario(
			"Update a Unit of Measure without key"
		).given(
			"I have a product definition with a default SKU"
		).when(
			"Unit Of Measure is added"
		).then(
			"Update the Unit Of Measure without the key"
		).and(
			"An exception is thrown"
		).and(
			"The key is not updated"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure1 =
			_getCpInstanceUnitOfMeasure(cpInstances.get(0));

		Assert.assertThrows(
			CPInstanceUnitOfMeasureKeyException.class,
			() ->
				_cpInstanceUnitOfMeasureLocalService.
					updateCPInstanceUnitOfMeasure(
						cpInstanceUnitOfMeasure1.getCPInstanceUnitOfMeasureId(),
						cpInstanceUnitOfMeasure1.getCPInstanceId(), true,
						BigDecimal.TEN, null,
						HashMapBuilder.put(
							LocaleUtil.getDefault(), "NAME"
						).build(),
						2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
						cpInstanceUnitOfMeasure1.getSku()));

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure2 =
			_cpInstanceUnitOfMeasureLocalService.getCPInstanceUnitOfMeasure(
				cpInstanceUnitOfMeasure1.getCPInstanceUnitOfMeasureId());

		Assert.assertEquals("Key", "KEY", cpInstanceUnitOfMeasure2.getKey());
	}

	private void _testUpdateCPInstanceUnitOfMeasureWithoutRate()
		throws Exception {

		frutillaRule.scenario(
			"Create a new Unit of Measure for a CPInstance"
		).given(
			"I have a product definition with a default SKU"
		).when(
			"Unit Of Measure is added"
		).then(
			"Update the Unit Of Measure without the rate"
		).and(
			"An exception is thrown"
		).and(
			"The Unit of Measure is not updated"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure1 =
			_getCpInstanceUnitOfMeasure(cpInstances.get(0));

		Assert.assertThrows(
			CPInstanceUnitOfMeasureRateException.class,
			() ->
				_cpInstanceUnitOfMeasureLocalService.
					updateCPInstanceUnitOfMeasure(
						cpInstanceUnitOfMeasure1.getCPInstanceUnitOfMeasureId(),
						cpInstanceUnitOfMeasure1.getCPInstanceId(), true,
						BigDecimal.TEN, "KEY",
						HashMapBuilder.put(
							LocaleUtil.getDefault(), "NAME"
						).build(),
						2, BigDecimal.ZERO, true, 0.0, null,
						cpInstanceUnitOfMeasure1.getSku()));

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure2 =
			_cpInstanceUnitOfMeasureLocalService.getCPInstanceUnitOfMeasure(
				cpInstanceUnitOfMeasure1.getCPInstanceUnitOfMeasureId());

		Assert.assertEquals(
			"Rate", cpInstanceUnitOfMeasure1.getRate(),
			cpInstanceUnitOfMeasure2.getRate());
	}

	private static Company _company;
	private static User _user;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

}