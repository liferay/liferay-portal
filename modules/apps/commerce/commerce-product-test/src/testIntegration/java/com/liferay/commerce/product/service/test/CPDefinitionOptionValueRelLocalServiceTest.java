/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.exception.CommerceInventoryWarehouseItemUnitOfMeasureKeyException;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.exception.CPDefinitionOptionValueRelKeyException;
import com.liferay.commerce.product.exception.CPDefinitionOptionValueRelPriceException;
import com.liferay.commerce.product.exception.CPDefinitionOptionValueRelQuantityException;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionValueRelException;
import com.liferay.commerce.product.exception.NoSuchCPInstanceUnitOfMeasureException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matija Petanjek
 * @author Alessio Antonio Rendina
 * @author Igor Beslic
 */
@RunWith(Arquillian.class)
public class CPDefinitionOptionValueRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			null, RandomTestUtil.randomString(), null,
			LocaleUtil.US.getDisplayLanguage(), _serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				_cpDefinitionOptionRels) {

			_cpDefinitionOptionRelLocalService.deleteCPDefinitionOptionRel(
				cpDefinitionOptionRel);
		}

		_cpOptionLocalService.deleteCPOptions(_serviceContext.getCompanyId());
		_serviceContext = null;
	}

	@Test
	public void testGetApprovedCPInstanceCPDefinitionOptionValueRelsExcludesExpiredCPInstance()
		throws Exception {

		frutillaRule.scenario(
			"An SKU with expirationDate in the past must not be offered as a " +
				"selectable option value"
		).given(
			"A product with one SKU-contributor option and three option values"
		).and(
			"Three SKUs generated, one per option value, all APPROVED"
		).when(
			"One SKU has its expirationDate set to a past date"
		).then(
			"The corresponding option value is excluded from the approved list"
		);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_buildCPDefinitionWithSKUContributorOption(3);

		List<CPInstance> cpInstances =
			_cpInstanceLocalService.getCPDefinitionInstances(
				cpDefinitionOptionRel.getCPDefinitionId(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(cpInstances.toString(), 3, cpInstances.size());

		CPInstance cpInstance = cpInstances.get(0);

		cpInstance.setExpirationDate(
			new Date(System.currentTimeMillis() - 86400000L));

		cpInstance = _cpInstanceLocalService.updateCPInstance(cpInstance);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			_cpInstanceLocalService.getCPInstance(
				cpInstance.getCPInstanceId()
			).getStatus());

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			_cpDefinitionOptionValueRelLocalService.
				getApprovedCPInstanceCPDefinitionOptionValueRels(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId());

		Assert.assertEquals(
			cpDefinitionOptionValueRels.toString(), 2,
			cpDefinitionOptionValueRels.size());
	}

	@Test
	public void testGetApprovedCPInstanceCPDefinitionOptionValueRelsExcludesNonapprovedCPInstance()
		throws Exception {

		frutillaRule.scenario(
			"An SKU whose workflow status is not APPROVED must not be offered"
		).given(
			"A product with one SKU-contributor option and three option values"
		).and(
			"Three SKUs generated, one per option value, all APPROVED"
		).when(
			"One SKU is transitioned to DRAFT"
		).then(
			"The corresponding option value is excluded from the approved list"
		);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_buildCPDefinitionWithSKUContributorOption(3);

		List<CPInstance> cpInstances =
			_cpInstanceLocalService.getCPDefinitionInstances(
				cpDefinitionOptionRel.getCPDefinitionId(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(cpInstances.toString(), 3, cpInstances.size());

		_cpInstanceLocalService.updateStatus(
			_serviceContext.getUserId(),
			cpInstances.get(
				0
			).getCPInstanceId(),
			WorkflowConstants.STATUS_DRAFT);

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			_cpDefinitionOptionValueRelLocalService.
				getApprovedCPInstanceCPDefinitionOptionValueRels(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId());

		Assert.assertEquals(
			cpDefinitionOptionValueRels.toString(), 2,
			cpDefinitionOptionValueRels.size());
	}

	@Test
	public void testGetApprovedCPInstanceCPDefinitionOptionValueRelsReturnsAllWhenAllApproved()
		throws Exception {

		frutillaRule.scenario(
			"All option values with an APPROVED, non-expired SKU must be " +
				"returned"
		).given(
			"A product with one SKU-contributor option and three option values"
		).when(
			"Three SKUs generated, one per option value, all APPROVED"
		).then(
			"All three option values are returned"
		);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_buildCPDefinitionWithSKUContributorOption(3);

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			_cpDefinitionOptionValueRelLocalService.
				getApprovedCPInstanceCPDefinitionOptionValueRels(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId());

		Assert.assertEquals(
			cpDefinitionOptionValueRels.toString(), 3,
			cpDefinitionOptionValueRels.size());
	}

	@Test
	public void testGetApprovedCPInstanceCPDefinitionOptionValueRelsReturnsEmptyListWhenAllNonapproved()
		throws Exception {

		frutillaRule.scenario(
			"If no SKU is approved and non-expired the result must be an " +
				"empty list (not null)"
		).given(
			"A product with one SKU-contributor option and two option values"
		).and(
			"Two SKUs generated, one per option value"
		).when(
			"Both SKUs are transitioned to INACTIVE"
		).then(
			"An empty list is returned without throwing NullPointerException"
		);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_buildCPDefinitionWithSKUContributorOption(2);

		List<CPInstance> cpInstances =
			_cpInstanceLocalService.getCPDefinitionInstances(
				cpDefinitionOptionRel.getCPDefinitionId(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		for (CPInstance cpInstance : cpInstances) {
			_cpInstanceLocalService.updateStatus(
				_serviceContext.getUserId(), cpInstance.getCPInstanceId(),
				WorkflowConstants.STATUS_INACTIVE);
		}

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			_cpDefinitionOptionValueRelLocalService.
				getApprovedCPInstanceCPDefinitionOptionValueRels(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId());

		Assert.assertNotNull(cpDefinitionOptionValueRels);
		Assert.assertTrue(cpDefinitionOptionValueRels.isEmpty());
	}

	@Test
	public void testGetOrAddEmptyCPDefinitionOptionValueRel() throws Exception {
		frutillaRule.scenario(
			"Get or add an empty product definition option value"
		).given(
			"An existing product definition option and an external reference " +
				"code"
		).when(
			"An empty product definition option value is requested under " +
				"that option"
		).then(
			"A NoSuchCPDefinitionOptionValueRelException is thrown while " +
				"lazy referencing is disabled"
		).and(
			"An empty stub with the given external reference code is " +
				"returned while lazy referencing is enabled"
		).and(
			"The same product definition option value is resolved on " +
				"subsequent requests"
		).and(
			"The empty status is cleared once the stub is updated"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId());

		CPOption cpOption = CPTestUtil.addCPOption(
			_commerceCatalog.getGroupId(),
			CPTestUtil.getDefaultCommerceOptionTypeKey(false), false);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			CPTestUtil.addCPDefinitionOptionRel(
				_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
				cpOption.getCPOptionId());

		_cpDefinitionOptionRels.add(cpDefinitionOptionRel);

		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			_cpDefinitionOptionValueRelLocalService.
				getOrAddEmptyCPDefinitionOptionValueRel(
					externalReferenceCode, _serviceContext.getCompanyId(),
					_serviceContext.getUserId(),
					cpDefinitionOptionRel.getCPDefinitionOptionRelId());

			Assert.fail();
		}
		catch (NoSuchCPDefinitionOptionValueRelException
					noSuchCPDefinitionOptionValueRelException) {

			Assert.assertNotNull(noSuchCPDefinitionOptionValueRelException);
		}

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			cpDefinitionOptionValueRel =
				_cpDefinitionOptionValueRelLocalService.
					getOrAddEmptyCPDefinitionOptionValueRel(
						externalReferenceCode, _serviceContext.getCompanyId(),
						_serviceContext.getUserId(),
						cpDefinitionOptionRel.getCPDefinitionOptionRelId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY,
				cpDefinitionOptionValueRel.getStatus());
			Assert.assertEquals(
				externalReferenceCode,
				cpDefinitionOptionValueRel.getExternalReferenceCode());
			Assert.assertEquals(
				cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
				cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());

			CPDefinitionOptionValueRel resolvedCPDefinitionOptionValueRel =
				_cpDefinitionOptionValueRelLocalService.
					getOrAddEmptyCPDefinitionOptionValueRel(
						externalReferenceCode, _serviceContext.getCompanyId(),
						_serviceContext.getUserId(),
						cpDefinitionOptionRel.getCPDefinitionOptionRelId());

			Assert.assertEquals(
				cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId(),
				resolvedCPDefinitionOptionValueRel.
					getCPDefinitionOptionValueRelId());
		}

		cpDefinitionOptionValueRel = _updateCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRel, 0,
			cpDefinitionOptionValueRel.isPreselected(), null, BigDecimal.ZERO);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			cpDefinitionOptionValueRel.getStatus());
	}

	@Test
	public void testPriceContributorOptionCPDefinitionAsCPDefinitionOptionValueRel()
		throws Exception {

		frutillaRule.scenario(
			"Product bundle option value links product with chargeable option"
		).given(
			"Two products, each with one option with price type set to static"
		).when(
			"Product 1 option is not required and is not SKU contributor"
		).and(
			"Product 2 option is not required and is not SKU contributor"
		).then(
			"Product specialist can link product 1 option value to product 2"
		).and(
			"Product specialist can link product 2 option value to product 1"
		);

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel1 =
			_addCPDefinitionWithOptionValue();

		CPDefinitionOptionRel cpDefinitionOptionRel1 =
			cpDefinitionOptionValueRel1.getCPDefinitionOptionRel();

		_cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRel1.getCPDefinitionOptionRelId(),
			cpDefinitionOptionRel1.getCPOptionId(),
			cpDefinitionOptionRel1.getNameMap(),
			cpDefinitionOptionRel1.getDescriptionMap(),
			cpDefinitionOptionRel1.getCommerceOptionTypeKey(),
			cpDefinitionOptionRel1.getInfoItemServiceKey(),
			cpDefinitionOptionRel1.getPriority(),
			cpDefinitionOptionRel1.isDefinedExternally(),
			cpDefinitionOptionRel1.isFacetable(), false, false,
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
			cpDefinitionOptionRel1.getTypeSettings(), _serviceContext);

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel2 =
			_addCPDefinitionWithOptionValue();

		CPDefinitionOptionRel cpDefinitionOptionRel2 =
			cpDefinitionOptionValueRel2.getCPDefinitionOptionRel();

		_cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRel2.getCPDefinitionOptionRelId(),
			cpDefinitionOptionRel2.getCPOptionId(),
			cpDefinitionOptionRel2.getNameMap(),
			cpDefinitionOptionRel2.getDescriptionMap(),
			cpDefinitionOptionRel2.getCommerceOptionTypeKey(),
			cpDefinitionOptionRel2.getInfoItemServiceKey(),
			cpDefinitionOptionRel2.getPriority(),
			cpDefinitionOptionRel2.isDefinedExternally(),
			cpDefinitionOptionRel2.isFacetable(), false, false,
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
			cpDefinitionOptionRel2.getTypeSettings(), _serviceContext);

		CPInstance cpDefinitionCPInstance2 = _getCPInstance(
			cpDefinitionOptionRel2.getCPDefinitionId());

		cpDefinitionOptionValueRel1 = _updateCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRel1,
			cpDefinitionCPInstance2.getCPInstanceId(),
			cpDefinitionOptionValueRel1.isPreselected(), BigDecimal.TEN,
			BigDecimal.ONE);

		Assert.assertEquals(
			cpDefinitionOptionValueRel1.getCPInstanceUuid(),
			cpDefinitionCPInstance2.getCPInstanceUuid());

		CPInstance cpDefinitionCPInstance1 = _getCPInstance(
			cpDefinitionOptionRel1.getCPDefinitionId());

		cpDefinitionOptionValueRel2 = _updateCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRel2,
			cpDefinitionCPInstance1.getCPInstanceId(),
			cpDefinitionOptionValueRel2.isPreselected(), BigDecimal.ONE,
			BigDecimal.ONE);

		Assert.assertEquals(
			cpDefinitionOptionValueRel2.getCPInstanceUuid(),
			cpDefinitionCPInstance1.getCPInstanceUuid());
	}

	@Test
	public void testResetCPDefinitionOptionValueRelOnDelete() throws Exception {
		frutillaRule.scenario(
			"Delete a product instance which is referenced as an option " +
				"value of another product (product bundle)"
		).given(
			"A product bundle and a product instance"
		).and(
			"Product instance is referenced as an option value of the " +
				"product bundle"
		).when(
			"The referenced product instance is deleted"
		).then(
			"Product bundle's option value attributes should be reset to " +
				"default values"
		);

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_addCPDefinitionWithOptionValue();

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

		_cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
			cpDefinitionOptionRel.getCPOptionId(),
			cpDefinitionOptionRel.getNameMap(),
			cpDefinitionOptionRel.getDescriptionMap(),
			cpDefinitionOptionRel.getCommerceOptionTypeKey(),
			cpDefinitionOptionRel.getInfoItemServiceKey(),
			cpDefinitionOptionRel.getPriority(),
			cpDefinitionOptionRel.isDefinedExternally(),
			cpDefinitionOptionRel.isFacetable(),
			cpDefinitionOptionRel.isRequired(),
			cpDefinitionOptionRel.isSkuContributor(),
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
			cpDefinitionOptionRel.getTypeSettings(), _serviceContext);

		cpDefinitionOptionValueRel = _updateCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRel, cpInstance.getCPInstanceId(),
			cpDefinitionOptionValueRel.isPreselected(), BigDecimal.TEN,
			BigDecimal.ONE);

		Assert.assertEquals(
			cpInstance.getCPInstanceUuid(),
			cpDefinitionOptionValueRel.getCPInstanceUuid());

		CPDefinition cpInstanceCPDefinition = cpInstance.getCPDefinition();

		Assert.assertEquals(
			cpInstanceCPDefinition.getCProductId(),
			cpDefinitionOptionValueRel.getCProductId());

		Assert.assertEquals(
			BigDecimal.TEN, cpDefinitionOptionValueRel.getPrice());
		Assert.assertEquals(
			BigDecimal.ONE, cpDefinitionOptionValueRel.getQuantity());

		_cpInstanceLocalService.deleteCPInstance(cpInstance);

		cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRel(
					cpDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId());

		Assert.assertEquals(
			StringPool.BLANK, cpDefinitionOptionValueRel.getCPInstanceUuid());
		Assert.assertEquals(0, cpDefinitionOptionValueRel.getCProductId());

		Assert.assertEquals(
			CPTestUtil.stripTrailingZeros(BigDecimal.ZERO),
			CPTestUtil.stripTrailingZeros(
				cpDefinitionOptionValueRel.getPrice()));

		Assert.assertEquals(
			BigDecimal.ZERO, cpDefinitionOptionValueRel.getQuantity());
	}

	@Test
	public void testResetCPDefinitionOptionValueRelOnUpdateStatusToApproved()
		throws Exception {

		frutillaRule.scenario(
			"Update a status of APPROVED product instance which is " +
				"referenced as an option value of another product (product " +
					"bundle)"
		).given(
			"A product bundle and a product instance"
		).and(
			"Product instance is referenced as an option value of the " +
				"product bundle"
		).when(
			"The referenced product instance status stays in APPROVED state"
		).then(
			"Product bundle's option value attributes should not be changed"
		);

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_addCPDefinitionWithOptionValue();

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpInstance.getStatus());

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

		_cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
			cpDefinitionOptionRel.getCPOptionId(),
			cpDefinitionOptionRel.getNameMap(),
			cpDefinitionOptionRel.getDescriptionMap(),
			cpDefinitionOptionRel.getCommerceOptionTypeKey(),
			cpDefinitionOptionRel.getInfoItemServiceKey(),
			cpDefinitionOptionRel.getPriority(),
			cpDefinitionOptionRel.isDefinedExternally(),
			cpDefinitionOptionRel.isFacetable(),
			cpDefinitionOptionRel.isRequired(),
			cpDefinitionOptionRel.isSkuContributor(),
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
			cpDefinitionOptionRel.getTypeSettings(), _serviceContext);

		cpDefinitionOptionValueRel = _updateCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRel, cpInstance.getCPInstanceId(),
			cpDefinitionOptionValueRel.isPreselected(), BigDecimal.TEN,
			BigDecimal.ONE);

		Assert.assertEquals(
			cpInstance.getCPInstanceUuid(),
			cpDefinitionOptionValueRel.getCPInstanceUuid());

		CPDefinition cpInstanceCPDefinition = cpInstance.getCPDefinition();

		Assert.assertEquals(
			cpInstanceCPDefinition.getCProductId(),
			cpDefinitionOptionValueRel.getCProductId());

		Assert.assertEquals(
			BigDecimal.TEN, cpDefinitionOptionValueRel.getPrice());
		Assert.assertEquals(
			BigDecimal.ONE, cpDefinitionOptionValueRel.getQuantity());

		_cpInstanceLocalService.updateStatus(
			_serviceContext.getUserId(), cpInstance.getCPInstanceId(),
			WorkflowConstants.STATUS_APPROVED);

		cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRel(
					cpDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId());

		Assert.assertEquals(
			cpInstance.getCPInstanceUuid(),
			cpDefinitionOptionValueRel.getCPInstanceUuid());

		cpInstanceCPDefinition = cpInstance.getCPDefinition();

		Assert.assertEquals(
			cpInstanceCPDefinition.getCProductId(),
			cpDefinitionOptionValueRel.getCProductId());

		Assert.assertEquals(
			CPTestUtil.stripTrailingZeros(BigDecimal.TEN),
			CPTestUtil.stripTrailingZeros(
				cpDefinitionOptionValueRel.getPrice()));
		Assert.assertEquals(
			BigDecimal.ONE, cpDefinitionOptionValueRel.getQuantity());
	}

	@Test
	public void testResetCPDefinitionOptionValueRelOnUpdateStatusToExpired()
		throws Exception {

		frutillaRule.scenario(
			"Update a status of APPROVED product instance which is " +
				"referenced as an option value of another product (product " +
					"bundle)"
		).given(
			"A product bundle and a product instance"
		).and(
			"Product instance is referenced as an option value of the " +
				"product bundle"
		).when(
			"The referenced product instance status is changed from APPROVED " +
				"to EXPIRED"
		).then(
			"Product bundle's option value attributes should be reset to " +
				"default values"
		);

		testResetCPDefinitionOptionValueRelOnStatusChange(
			WorkflowConstants.STATUS_EXPIRED);
	}

	@Test
	public void testResetCPDefinitionOptionValueRelOnUpdateStatusToInactive()
		throws Exception {

		frutillaRule.scenario(
			"Update a status of APPROVED product instance which is " +
				"referenced as an option value of another product (product " +
					"bundle)"
		).given(
			"A product bundle and a product instance"
		).and(
			"Product instance is referenced as an option value of the " +
				"product bundle"
		).when(
			"The referenced product instance status is changed from APPROVED " +
				"to INACTIVE"
		).then(
			"Product bundle's option value attributes should be reset to " +
				"default values"
		);

		testResetCPDefinitionOptionValueRelOnStatusChange(
			WorkflowConstants.STATUS_INACTIVE);
	}

	@Test
	public void testResetCPDefinitionOptionValueRelOnUpdateStatusToScheduled()
		throws Exception {

		frutillaRule.scenario(
			"Update a status of APPROVED product instance which is " +
				"referenced as an option value of another product (product " +
					"bundle)"
		).given(
			"A product bundle and a product instance"
		).and(
			"Product instance is referenced as an option value of the " +
				"product bundle"
		).when(
			"The referenced product instance status is changed from APPROVED " +
				"to SCHEDULED"
		).then(
			"Product bundle's option value attributes should be reset to " +
				"default values"
		);

		testResetCPDefinitionOptionValueRelOnStatusChange(
			WorkflowConstants.STATUS_SCHEDULED);
	}

	@Test
	public void testUpdateDynamicPriceTypeCPDefinitionOptionValueRel()
		throws Exception {

		frutillaRule.scenario(
			"Update an option value"
		).given(
			"An option with dynamic price type set"
		).when(
			"The option value is updated"
		).then(
			"All needed cpInstance attributes is set to the option value"
		);

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_addCPDefinitionWithOptionValue();

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

		_cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
			cpDefinitionOptionRel.getCPOptionId(),
			cpDefinitionOptionRel.getNameMap(),
			cpDefinitionOptionRel.getDescriptionMap(),
			cpDefinitionOptionRel.getCommerceOptionTypeKey(),
			cpDefinitionOptionRel.getInfoItemServiceKey(),
			cpDefinitionOptionRel.getPriority(),
			cpDefinitionOptionRel.isDefinedExternally(),
			cpDefinitionOptionRel.isFacetable(),
			cpDefinitionOptionRel.isRequired(),
			cpDefinitionOptionRel.isSkuContributor(),
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC,
			cpDefinitionOptionRel.getTypeSettings(), _serviceContext);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		cpDefinitionOptionValueRel = _updateCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRel, cpInstance.getCPInstanceId(),
			cpDefinitionOptionValueRel.isPreselected(), null, BigDecimal.ONE);

		Assert.assertEquals(
			cpInstance.getCPInstanceUuid(),
			cpDefinitionOptionValueRel.getCPInstanceUuid());

		CPDefinition cpInstanceCPDefinition = cpInstance.getCPDefinition();

		Assert.assertEquals(
			cpInstanceCPDefinition.getCProductId(),
			cpDefinitionOptionValueRel.getCProductId());
	}

	@Test
	public void testUpdateNoPriceTypeCPDefinitionOptionValueRel()
		throws Exception {

		frutillaRule.scenario(
			"Update an option value"
		).given(
			"An option with no price type set"
		).when(
			"The option value is updated"
		).then(
			"Any of cpInstance attributes is set to the option value"
		);

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_addCPDefinitionWithOptionValue();

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

		Assert.assertTrue(
			Validator.isNull(cpDefinitionOptionRel.getPriceType()));

		Assert.assertTrue(
			Validator.isNull(cpDefinitionOptionValueRel.getCPInstanceUuid()));
		Assert.assertEquals(0, cpDefinitionOptionValueRel.getCProductId());
	}

	@Test
	public void testUpdatePreselectedCPDefinitionOptionValueRel()
		throws Exception {

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			CPTestUtil.addCPOption(
				_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
				1, 5);

		_cpDefinitionOptionRels.addAll(cpDefinitionOptionRels);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionRels.get(0);

		Assert.assertEquals(
			"product option values count", 5,
			cpDefinitionOptionRel.getCPDefinitionOptionValueRelsCount());

		Assert.assertFalse(
			"preselected option value exists",
			_cpDefinitionOptionValueRelLocalService.
				hasPreselectedCPDefinitionOptionValueRel(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId()));

		CPDefinitionOptionValueRel randomCPDefinitionOptionValueRel =
			CPTestUtil.getRandomCPDefinitionOptionValueRel(
				cpDefinition.getCPDefinitionId());

		_updateCPDefinitionOptionValueRel(
			randomCPDefinitionOptionValueRel, 0, true,
			randomCPDefinitionOptionValueRel.getPrice(),
			randomCPDefinitionOptionValueRel.getQuantity());

		Assert.assertTrue(
			"preselected option value exists",
			_cpDefinitionOptionValueRelLocalService.
				hasPreselectedCPDefinitionOptionValueRel(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId()));

		CPDefinitionOptionValueRel targetCPDefinitionOptionValueRel = null;

		for (CPDefinitionOptionValueRel cpDefinitionOptionValueRel :
				cpDefinitionOptionRel.getCPDefinitionOptionValueRels()) {

			long cpDefinitionOptionValueRelId1 =
				randomCPDefinitionOptionValueRel.
					getCPDefinitionOptionValueRelId();
			long cpDefinitionOptionValueRelId2 =
				cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId();

			if (cpDefinitionOptionValueRelId1 ==
					cpDefinitionOptionValueRelId2) {

				Assert.assertTrue(
					"Option value preselected",
					cpDefinitionOptionValueRel.isPreselected());

				continue;
			}

			Assert.assertFalse(
				"Option value preselected",
				cpDefinitionOptionValueRel.isPreselected());

			if (targetCPDefinitionOptionValueRel == null) {
				targetCPDefinitionOptionValueRel = cpDefinitionOptionValueRel;
			}
		}

		Assert.assertNotEquals(
			"updated option value id",
			randomCPDefinitionOptionValueRel.getCPDefinitionOptionValueRelId(),
			targetCPDefinitionOptionValueRel.getCPDefinitionOptionValueRelId());

		_cpDefinitionOptionValueRelLocalService.
			updateCPDefinitionOptionValueRelPreselected(
				targetCPDefinitionOptionValueRel.
					getCPDefinitionOptionValueRelId(),
				true);

		Assert.assertTrue(
			"preselected option value exists",
			_cpDefinitionOptionValueRelLocalService.
				hasPreselectedCPDefinitionOptionValueRel(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId()));

		CPDefinitionOptionValueRel preselectedCPDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelLocalService.
				fetchPreselectedCPDefinitionOptionValueRel(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId());

		Assert.assertEquals(
			"updated option value id",
			targetCPDefinitionOptionValueRel.getCPDefinitionOptionValueRelId(),
			preselectedCPDefinitionOptionValueRel.
				getCPDefinitionOptionValueRelId());

		Assert.assertTrue(
			"Option value preselected",
			preselectedCPDefinitionOptionValueRel.isPreselected());

		preselectedCPDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelLocalService.
				updateCPDefinitionOptionValueRelPreselected(
					targetCPDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId(),
					false);

		Assert.assertFalse(
			"preselected option value exists",
			_cpDefinitionOptionValueRelLocalService.
				hasPreselectedCPDefinitionOptionValueRel(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId()));

		Assert.assertFalse(
			"Option value preselected",
			preselectedCPDefinitionOptionValueRel.isPreselected());
	}

	@Test(expected = CPDefinitionOptionValueRelPriceException.class)
	public void testUpdateStaticPriceTypeCPDefinitionOptionValueRelWithoutPrice()
		throws Exception {

		frutillaRule.scenario(
			"Update an option value without passing price"
		).given(
			"An option with static price type set"
		).when(
			"The option value is updated"
		).then(
			"price is required, cpInstanceUUID and cProductId are optional"
		);

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_addCPDefinitionWithOptionValue();

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

		_cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
			cpDefinitionOptionRel.getCPOptionId(),
			cpDefinitionOptionRel.getNameMap(),
			cpDefinitionOptionRel.getDescriptionMap(),
			cpDefinitionOptionRel.getCommerceOptionTypeKey(),
			cpDefinitionOptionRel.getInfoItemServiceKey(),
			cpDefinitionOptionRel.getPriority(),
			cpDefinitionOptionRel.isDefinedExternally(),
			cpDefinitionOptionRel.isFacetable(),
			cpDefinitionOptionRel.isRequired(),
			cpDefinitionOptionRel.isSkuContributor(),
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
			cpDefinitionOptionRel.getTypeSettings(), _serviceContext);

		_updateCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRel, cpInstance.getCPInstanceId(),
			cpDefinitionOptionValueRel.isPreselected(), null, BigDecimal.ONE);
	}

	@Test
	public void testUpdateStaticPriceTypeCPDefinitionOptionValueRelWithPrice()
		throws Exception {

		frutillaRule.scenario(
			"Update an option value passing price"
		).given(
			"An option with static price type set"
		).when(
			"The option value is updated"
		).then(
			"price is required, cpInstanceUUID and cProductId are optional"
		);

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_addCPDefinitionWithOptionValue();

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

		_cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
			cpDefinitionOptionRel.getCPOptionId(),
			cpDefinitionOptionRel.getNameMap(),
			cpDefinitionOptionRel.getDescriptionMap(),
			cpDefinitionOptionRel.getCommerceOptionTypeKey(),
			cpDefinitionOptionRel.getInfoItemServiceKey(),
			cpDefinitionOptionRel.getPriority(),
			cpDefinitionOptionRel.isDefinedExternally(),
			cpDefinitionOptionRel.isFacetable(),
			cpDefinitionOptionRel.isRequired(),
			cpDefinitionOptionRel.isSkuContributor(),
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
			cpDefinitionOptionRel.getTypeSettings(), _serviceContext);

		cpDefinitionOptionValueRel = _updateCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRel, cpInstance.getCPInstanceId(),
			cpDefinitionOptionValueRel.isPreselected(), BigDecimal.TEN,
			BigDecimal.ONE);

		Assert.assertEquals(
			BigDecimal.TEN, cpDefinitionOptionValueRel.getPrice());
		Assert.assertNotEquals(
			cpInstance.getPrice(), cpDefinitionOptionValueRel.getPrice());
	}

	@Test(expected = CPDefinitionOptionValueRelQuantityException.class)
	public void testValidateCPDefinitionOptionValueRelDynamicFail()
		throws Exception {

		frutillaRule.scenario(
			"Update an option value with non unique SKU and quantity"
		).given(
			"Dynamic product option values with SKU and quantity set"
		).when(
			"New option value is added with same SKU and quantity"
		).then(
			"An exception is thrown"
		);

		_assertValidateCPDefinitionOptionValueRelCPInstanceLinkFail(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);
	}

	@Test(expected = NoSuchCPInstanceUnitOfMeasureException.class)
	public void testValidateCPDefinitionOptionValueRelInvalidUOM()
		throws Exception {

		frutillaRule.scenario(
			"Update an option value with wrong UOM"
		).given(
			"Dynamic product option values with SKU and quantity set"
		).when(
			"The option value is updated with a random UOM"
		).then(
			"An exception is thrown"
		);

		CPDefinition cpDefinition =
			CPTestUtil.addCPDefinitionWithChildCPDefinitions(
				_commerceCatalog.getGroupId(), 1,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);

		_cpDefinitionOptionRels.addAll(
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
				cpDefinition.getCPDefinitionId()));

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			CPTestUtil.getRandomCPDefinitionOptionValueRel(
				cpDefinition.getCPDefinitionId());

		CPInstance cpInstance = cpDefinitionOptionValueRel.fetchCPInstance();

		cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelLocalService.
				updateCPDefinitionOptionValueRel(
					cpDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId(),
					cpInstance.getCPInstanceId(), RandomTestUtil.randomString(),
					RandomTestUtil.randomLocaleStringMap(), false,
					BigDecimal.TEN, 1, BigDecimal.TEN,
					RandomTestUtil.randomString(), _serviceContext);

		Assert.assertNull(cpDefinitionOptionValueRel);
	}

	@Test
	public void testValidateCPDefinitionOptionValueRelKey() throws Exception {
		frutillaRule.scenario(
			"Verify the validity of the Option Value Key"
		).given(
			"A product with a Product Option of type select date"
		).when(
			"Configuring the Option Value"
		).then(
			"Only valid key is accepted"
		);

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_addCPDefinitionWitOptionValue(
				"03-18-2024-16-45-1-hours-europe-paris");

		Assert.assertNotNull(
			"Option value was not created", cpDefinitionOptionValueRel);
	}

	@Test(
		expected = CommerceInventoryWarehouseItemUnitOfMeasureKeyException.class
	)
	public void testValidateCPDefinitionOptionValueRelMissingUOM()
		throws Exception {

		frutillaRule.scenario(
			"Update an option value without UOM"
		).given(
			"Dynamic product option values with SKU with UOM"
		).when(
			"The option value is updated with a random UOM"
		).then(
			"An exception is thrown"
		);

		CPDefinition cpDefinition =
			CPTestUtil.addCPDefinitionWithChildCPDefinitions(
				_commerceCatalog.getGroupId(), 1,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);

		_cpDefinitionOptionRels.addAll(
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
				cpDefinition.getCPDefinitionId()));

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			CPTestUtil.getRandomCPDefinitionOptionValueRel(
				cpDefinition.getCPDefinitionId());

		CPInstance cpInstance = cpDefinitionOptionValueRel.fetchCPInstance();

		String cpInstanceUnitOfMeasureKey = RandomTestUtil.randomString();

		_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
			_user.getUserId(), cpInstance.getCPInstanceId(), true,
			BigDecimal.ONE, cpInstanceUnitOfMeasureKey,
			RandomTestUtil.randomLocaleStringMap(), 1, BigDecimal.ZERO, true, 1,
			BigDecimal.ONE, cpInstance.getSku());

		cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelLocalService.
				updateCPDefinitionOptionValueRel(
					cpDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId(),
					cpInstance.getCPInstanceId(), RandomTestUtil.randomString(),
					RandomTestUtil.randomLocaleStringMap(), false,
					BigDecimal.TEN, 1, BigDecimal.TEN, StringPool.BLANK,
					_serviceContext);

		Assert.assertNull(cpDefinitionOptionValueRel);
	}

	@Test(expected = CPDefinitionOptionValueRelQuantityException.class)
	public void testValidateCPDefinitionOptionValueRelStaticFail()
		throws Exception {

		frutillaRule.scenario(
			"Update an option value with non unique SKU and quantity"
		).given(
			"Static product option values with SKU and quantity set"
		).when(
			"New option value is added with same SKU and quantity"
		).then(
			"An exception is thrown"
		);

		_assertValidateCPDefinitionOptionValueRelCPInstanceLinkFail(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC);
	}

	@Test
	public void testValidateCPDefinitionOptionValueRelValidUOM()
		throws Exception {

		frutillaRule.scenario(
			"Update an option value with a valid UOM"
		).given(
			"Dynamic product option values with SKU with UOM"
		).when(
			"The option value is updated with the UOM"
		).then(
			"The option value is saved correctly"
		);

		CPDefinition cpDefinition =
			CPTestUtil.addCPDefinitionWithChildCPDefinitions(
				_commerceCatalog.getGroupId(), 1,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);

		_cpDefinitionOptionRels.addAll(
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
				cpDefinition.getCPDefinitionId()));

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			CPTestUtil.getRandomCPDefinitionOptionValueRel(
				cpDefinition.getCPDefinitionId());

		CPInstance cpInstance = cpDefinitionOptionValueRel.fetchCPInstance();

		String cpInstanceUnitOfMeasureKey = RandomTestUtil.randomString();

		_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
			_user.getUserId(), cpInstance.getCPInstanceId(), true,
			BigDecimal.ONE, cpInstanceUnitOfMeasureKey,
			RandomTestUtil.randomLocaleStringMap(), 1, BigDecimal.ZERO, true, 1,
			BigDecimal.ONE, cpInstance.getSku());

		cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelLocalService.
				updateCPDefinitionOptionValueRel(
					cpDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId(),
					cpInstance.getCPInstanceId(), RandomTestUtil.randomString(),
					RandomTestUtil.randomLocaleStringMap(), false,
					BigDecimal.TEN, 1, BigDecimal.TEN,
					cpInstanceUnitOfMeasureKey, _serviceContext);

		Assert.assertNotNull(cpDefinitionOptionValueRel);
		Assert.assertEquals(
			cpInstanceUnitOfMeasureKey,
			cpDefinitionOptionValueRel.getUnitOfMeasureKey());
	}

	@Test(expected = CPDefinitionOptionValueRelKeyException.class)
	public void testValidateCPDefinitionOptionValueRelWithInvalidDate()
		throws Exception {

		frutillaRule.scenario(
			"Verify the validity of the Option Value Key"
		).given(
			"A product with a Product Option of type select date"
		).when(
			"Configuring the Option Value"
		).and(
			"the key is not the right format"
		).then(
			"An exception is thrown"
		);

		_addCPDefinitionWitOptionValue("2024-03-32-16-45-1-hours-europe-paris");
	}

	@Test(expected = CPDefinitionOptionValueRelKeyException.class)
	public void testValidateCPDefinitionOptionValueRelWithInvalidDateValue()
		throws Exception {

		frutillaRule.scenario(
			"Verify the validity of the Option Value Key"
		).given(
			"A product with a Product Option of type select date"
		).when(
			"Configuring the Option Value"
		).and(
			"the key is not the right format"
		).then(
			"An exception is thrown"
		);

		_addCPDefinitionWitOptionValue("03-18-aa-16-45-1-hours-europe-paris");
	}

	@Test(expected = CPDefinitionOptionValueRelKeyException.class)
	public void testValidateCPDefinitionOptionValueRelWithInvalidDuration()
		throws Exception {

		frutillaRule.scenario(
			"Verify the validity of the Option Value Key"
		).given(
			"A product with a Product Option of type select date"
		).when(
			"Configuring the Option Value"
		).and(
			"the key is not the right format"
		).then(
			"An exception is thrown"
		);

		_addCPDefinitionWitOptionValue("03-18-2024-16-45-1-xyz-europe-paris");
	}

	@Test(expected = CPDefinitionOptionValueRelKeyException.class)
	public void testValidateCPDefinitionOptionValueRelWithInvalidKey()
		throws Exception {

		frutillaRule.scenario(
			"Verify the validity of the Option Value Key"
		).given(
			"A product with a Product Option of type select date"
		).when(
			"Configuring the Option Value"
		).and(
			"the key is not the right format"
		).then(
			"An exception is thrown"
		);

		_addCPDefinitionWitOptionValue("03-18-2024_16-45-1-hours-europe-paris");
	}

	@Test(expected = CPDefinitionOptionValueRelKeyException.class)
	public void testValidateCPDefinitionOptionValueRelWithNullKey()
		throws Exception {

		frutillaRule.scenario(
			"Verify the validity of the Option Value Key"
		).given(
			"A product with a Product Option of type select date"
		).when(
			"Configuring the Option Value"
		).and(
			"the key is null"
		).then(
			"An exception is thrown"
		);

		_addCPDefinitionWitOptionValue(null);
	}

	@Test
	public void testValidateLinkedCPDefinitionOptionValueRelSuccess()
		throws Exception {

		CPDefinition cpDefinition =
			CPTestUtil.addCPDefinitionWithChildCPDefinitions(
				_commerceCatalog.getGroupId(), 1,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);

		_cpDefinitionOptionRels.addAll(
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
				cpDefinition.getCPDefinitionId()));

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			CPTestUtil.getRandomCPDefinitionOptionValueRel(
				cpDefinition.getCPDefinitionId());

		Assert.assertTrue(
			Validator.isNotNull(
				cpDefinitionOptionValueRel.getCPInstanceUuid()));
		Assert.assertTrue(
			"Quantity is greater than 0",
			BigDecimalUtil.gt(
				cpDefinitionOptionValueRel.getQuantity(), BigDecimal.ZERO));

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			cpDefinitionOptionRel.getCPDefinitionOptionValueRels();

		CPDefinitionOptionValueRel newCPDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelLocalService.
				addCPDefinitionOptionValueRel(
					null,
					cpDefinitionOptionValueRel.getCPDefinitionOptionRelId(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.nextDouble(), _serviceContext);

		CPInstance cpInstance = cpDefinitionOptionValueRel.fetchCPInstance();

		BigDecimal quantity = cpDefinitionOptionValueRel.getQuantity();

		newCPDefinitionOptionValueRel = _updateCPDefinitionOptionValueRel(
			newCPDefinitionOptionValueRel, cpInstance.getCPInstanceId(),
			newCPDefinitionOptionValueRel.isPreselected(), null,
			quantity.add(BigDecimal.TEN));

		Assert.assertEquals(
			"New CP definition option value quantity",
			quantity.add(BigDecimal.TEN),
			newCPDefinitionOptionValueRel.getQuantity());

		int size = cpDefinitionOptionValueRels.size();

		cpDefinitionOptionValueRels =
			cpDefinitionOptionRel.getCPDefinitionOptionValueRels();

		Assert.assertEquals(
			"Size of product option values increased by one", size + 1,
			cpDefinitionOptionValueRels.size());
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	protected void testResetCPDefinitionOptionValueRelOnStatusChange(
			int newStatus)
		throws Exception {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_addCPDefinitionWithOptionValue();

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpInstance.getStatus());

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

		_cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
			cpDefinitionOptionRel.getCPOptionId(),
			cpDefinitionOptionRel.getNameMap(),
			cpDefinitionOptionRel.getDescriptionMap(),
			cpDefinitionOptionRel.getCommerceOptionTypeKey(),
			cpDefinitionOptionRel.getInfoItemServiceKey(),
			cpDefinitionOptionRel.getPriority(),
			cpDefinitionOptionRel.isDefinedExternally(),
			cpDefinitionOptionRel.isFacetable(),
			cpDefinitionOptionRel.isRequired(),
			cpDefinitionOptionRel.isSkuContributor(),
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
			cpDefinitionOptionRel.getTypeSettings(), _serviceContext);

		cpDefinitionOptionValueRel = _updateCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRel, cpInstance.getCPInstanceId(),
			cpDefinitionOptionValueRel.isPreselected(), BigDecimal.TEN,
			BigDecimal.ONE);

		Assert.assertEquals(
			cpInstance.getCPInstanceUuid(),
			cpDefinitionOptionValueRel.getCPInstanceUuid());

		CPDefinition cpInstanceCPDefinition = cpInstance.getCPDefinition();

		Assert.assertEquals(
			cpInstanceCPDefinition.getCProductId(),
			cpDefinitionOptionValueRel.getCProductId());

		Assert.assertEquals(
			BigDecimal.TEN, cpDefinitionOptionValueRel.getPrice());
		Assert.assertEquals(
			BigDecimal.ONE, cpDefinitionOptionValueRel.getQuantity());

		_cpInstanceLocalService.updateStatus(
			_serviceContext.getUserId(), cpInstance.getCPInstanceId(),
			newStatus);

		cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRel(
					cpDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId());

		Assert.assertTrue(
			Validator.isNull(cpDefinitionOptionValueRel.getCPInstanceUuid()));
		Assert.assertEquals(0, cpDefinitionOptionValueRel.getCProductId());

		Assert.assertEquals(
			CPTestUtil.stripTrailingZeros(BigDecimal.ZERO),
			CPTestUtil.stripTrailingZeros(
				cpDefinitionOptionValueRel.getPrice()));

		Assert.assertEquals(
			BigDecimal.ZERO, cpDefinitionOptionValueRel.getQuantity());
	}

	private CPDefinitionOptionValueRel _addCPDefinitionWithOptionValue()
		throws Exception {

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			CPTestUtil.addCPOption(
				_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
				1, 1);

		_cpDefinitionOptionRels.addAll(cpDefinitionOptionRels);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionRels.get(0);

		return _cpDefinitionOptionValueRelLocalService.
			addCPDefinitionOptionValueRel(
				null, cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
				"cpInstance-option-value", null, 0, _serviceContext);
	}

	private CPDefinitionOptionValueRel _addCPDefinitionWitOptionValue(
			String key)
		throws Exception {

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId());

		CPOption cpOption = CPTestUtil.addCPOption(
			_commerceCatalog.getGroupId(),
			CPConstants.PRODUCT_OPTION_SELECT_DATE_KEY, false);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelLocalService.addCPDefinitionOptionRel(
				null, cpDefinition.getCPDefinitionId(),
				cpOption.getCPOptionId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				CPConstants.PRODUCT_OPTION_SELECT_DATE_KEY,
				RandomTestUtil.randomDouble(), false, true, true, false,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, _serviceContext);

		_cpDefinitionOptionRels.add(cpDefinitionOptionRel);

		return _cpDefinitionOptionValueRelLocalService.
			addCPDefinitionOptionValueRel(
				null, cpDefinitionOptionRel.getCPDefinitionOptionRelId(), key,
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomDouble(), _serviceContext);
	}

	private void _assertValidateCPDefinitionOptionValueRelCPInstanceLinkFail(
			String priceType)
		throws Exception {

		CPDefinition cpDefinition =
			CPTestUtil.addCPDefinitionWithChildCPDefinitions(
				_commerceCatalog.getGroupId(), 1, priceType);

		_cpDefinitionOptionRels.addAll(
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
				cpDefinition.getCPDefinitionId()));

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			CPTestUtil.getRandomCPDefinitionOptionValueRel(
				cpDefinition.getCPDefinitionId());

		Assert.assertTrue(
			Validator.isNotNull(
				cpDefinitionOptionValueRel.getCPInstanceUuid()));
		Assert.assertTrue(
			"Quantity is greater than 0",
			BigDecimalUtil.gt(
				cpDefinitionOptionValueRel.getQuantity(), BigDecimal.ZERO));

		CPDefinitionOptionValueRel newCPDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelLocalService.
				addCPDefinitionOptionValueRel(
					null,
					cpDefinitionOptionValueRel.getCPDefinitionOptionRelId(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.nextDouble(), _serviceContext);

		CPInstance cpInstance = cpDefinitionOptionValueRel.fetchCPInstance();

		BigDecimal price = null;

		if (Objects.equals(
				priceType, CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC)) {

			price = BigDecimal.TEN;
		}

		_updateCPDefinitionOptionValueRel(
			newCPDefinitionOptionValueRel, cpInstance.getCPInstanceId(),
			newCPDefinitionOptionValueRel.isPreselected(), price,
			cpDefinitionOptionValueRel.getQuantity());
	}

	private CPDefinitionOptionRel _buildCPDefinitionWithSKUContributorOption(
			int optionValuesCount)
		throws Exception {

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			CPTestUtil.addCPOption(
				_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
				1, optionValuesCount);

		_cpDefinitionOptionRels.addAll(cpDefinitionOptionRels);

		_cpInstanceLocalService.buildCPInstances(
			cpDefinition.getCPDefinitionId(), _serviceContext);

		return cpDefinitionOptionRels.get(0);
	}

	private CPInstance _getCPInstance(long cpDefinitionId) throws Exception {
		List<CPInstance> cpInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinitionId);

		if (!cpInstances.isEmpty()) {
			return cpInstances.get(0);
		}

		return CPTestUtil.addCPDefinitionCPInstanceWithPrice(
			cpDefinitionId, Collections.emptyMap(),
			new BigDecimal(RandomTestUtil.randomDouble()));
	}

	private CPDefinitionOptionValueRel _updateCPDefinitionOptionValueRel(
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel,
			long cpInstanceId, boolean preselected, BigDecimal price,
			BigDecimal quantity)
		throws Exception {

		return _cpDefinitionOptionValueRelLocalService.
			updateCPDefinitionOptionValueRel(
				cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId(),
				cpInstanceId, cpDefinitionOptionValueRel.getKey(),
				cpDefinitionOptionValueRel.getNameMap(), preselected, price,
				cpDefinitionOptionValueRel.getPriority(), quantity,
				cpDefinitionOptionValueRel.getUnitOfMeasureKey(),
				_serviceContext);
	}

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	private final List<CPDefinitionOptionRel> _cpDefinitionOptionRels =
		new ArrayList<>();

	@Inject
	private CPDefinitionOptionValueRelLocalService
		_cpDefinitionOptionValueRelLocalService;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	@Inject
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;
	private User _user;

}