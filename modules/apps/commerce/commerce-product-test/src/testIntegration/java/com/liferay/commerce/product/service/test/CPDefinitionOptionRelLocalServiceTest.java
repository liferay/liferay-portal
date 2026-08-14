/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.exception.CPDefinitionOptionRelPriceTypeException;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionRelException;
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
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.petra.lang.SafeCloseable;
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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * @author Igor Beslic
 */
@RunWith(Arquillian.class)
public class CPDefinitionOptionRelLocalServiceTest {

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
	public void testCPDefinitionOptionRelSKUContributor() throws Exception {
		frutillaRule.scenario(
			"Update product option's SKU contributor attribute"
		).given(
			"A product and product options"
		).when(
			"The option SKU contributor attribute is updated to any valid value"
		).then(
			"option update always succeeds"
		).and(
			"all active SKU should be inactivated"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId());

		CPDefinitionOptionRel cpDefinitionOptionRel =
			CPTestUtil.addCPDefinitionOptionRel(
				_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
				false, 2);

		_cpDefinitionOptionRels.add(cpDefinitionOptionRel);

		Assert.assertFalse(
			"SKU contributor value", cpDefinitionOptionRel.isSkuContributor());

		cpDefinitionOptionRel =
			_cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
				cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
				cpDefinitionOptionRel.getCPOptionId(),
				cpDefinitionOptionRel.getNameMap(),
				cpDefinitionOptionRel.getDescriptionMap(),
				cpDefinitionOptionRel.getCommerceOptionTypeKey(),
				cpDefinitionOptionRel.getPriority(),
				cpDefinitionOptionRel.isFacetable(),
				cpDefinitionOptionRel.isRequired(), true, _serviceContext);

		Assert.assertTrue(
			"SKU contributor value", cpDefinitionOptionRel.isSkuContributor());

		List<CPInstance> cpDefinitionApprovedCPInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinition.getCPDefinitionId());

		Assert.assertTrue(
			"No approved instances", cpDefinitionApprovedCPInstances.isEmpty());

		_cpInstanceLocalService.buildCPInstances(
			cpDefinition.getCPDefinitionId(), _serviceContext);

		cpDefinitionOptionRel = CPTestUtil.addCPDefinitionOptionRel(
			_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
			false, 2);

		_cpDefinitionOptionRels.add(cpDefinitionOptionRel);

		Assert.assertFalse(
			"SKU contributor value", cpDefinitionOptionRel.isSkuContributor());

		cpDefinitionOptionRel = CPTestUtil.addCPDefinitionOptionRel(
			_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
			true, 2);

		_cpDefinitionOptionRels.add(cpDefinitionOptionRel);

		Assert.assertTrue(
			"SKU contributor value", cpDefinitionOptionRel.isSkuContributor());

		cpDefinitionApprovedCPInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinition.getCPDefinitionId());

		Assert.assertTrue(
			"No approved instances", cpDefinitionApprovedCPInstances.isEmpty());

		_cpInstanceLocalService.buildCPInstances(
			cpDefinition.getCPDefinitionId(), _serviceContext);

		cpDefinitionApprovedCPInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinition.getCPDefinitionId());

		Assert.assertFalse(
			"Approved instances exist",
			cpDefinitionApprovedCPInstances.isEmpty());

		_cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
			cpDefinitionOptionRel.getCPOptionId(),
			cpDefinitionOptionRel.getNameMap(),
			cpDefinitionOptionRel.getDescriptionMap(),
			cpDefinitionOptionRel.getCommerceOptionTypeKey(),
			cpDefinitionOptionRel.getPriority(),
			cpDefinitionOptionRel.isFacetable(),
			cpDefinitionOptionRel.isRequired(), false, _serviceContext);

		cpDefinitionApprovedCPInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinition.getCPDefinitionId());

		Assert.assertTrue(
			"No approved instances", cpDefinitionApprovedCPInstances.isEmpty());

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
				cpDefinition.getCPDefinitionId(), true);

		Assert.assertEquals(
			"SKU contributor options count", 1, cpDefinitionOptionRels.size());
	}

	@Test
	public void testFetchPreselectedCPDefinitionOptionValueRel()
		throws Exception {

		frutillaRule.scenario(
			"Obtain option's preselected value (if exists)"
		).given(
			"A product and product options with option values OV1, OV2, OV3"
		).and(
			"Option value OV2 has preselected value set to true"
		).when(
			"Option's fetch preselected option value method is called"
		).then(
			"OV2 is returned"
		).but(
			"If all option values OV1, OV2, OV3 have set preselected to " +
				"false, null is returned"
		);

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

		Assert.assertNull(
			"preselected option value",
			cpDefinitionOptionRel.fetchPreselectedCPDefinitionOptionValueRel());

		CPDefinitionOptionValueRel randomCPDefinitionOptionValueRel =
			CPTestUtil.getRandomCPDefinitionOptionValueRel(
				cpDefinition.getCPDefinitionId());

		_cpDefinitionOptionValueRelLocalService.
			updateCPDefinitionOptionValueRelPreselected(
				randomCPDefinitionOptionValueRel.
					getCPDefinitionOptionValueRelId(),
				true);

		CPDefinitionOptionValueRel preselectedCPDefinitionOptionValueRel =
			cpDefinitionOptionRel.fetchPreselectedCPDefinitionOptionValueRel();

		Assert.assertNotNull(
			"preselected option value", preselectedCPDefinitionOptionValueRel);

		Assert.assertEquals(
			"preselected option value id",
			randomCPDefinitionOptionValueRel.getCPDefinitionOptionValueRelId(),
			preselectedCPDefinitionOptionValueRel.
				getCPDefinitionOptionValueRelId());
	}

	@Test
	public void testGetCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys()
		throws Exception {

		frutillaRule.scenario(
			"Verify keys combination for JSON payload"
		).given(
			"I have a product definition"
		).when(
			"a SKU contributor option is added to definition"
		).and(
			"the option has one"
		).then(
			"the generated combination contains key, skuOptionName, " +
				"skuOptionValueName and value"
		);

		int cpOptionsCount = 1;
		int cpOptionValuesCount = 1;

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			CPTestUtil.addCPOption(
				_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
				cpOptionsCount, cpOptionValuesCount);

		_cpDefinitionOptionRels.addAll(cpDefinitionOptionRels);

		List<CPInstance> cpInstances = _cpInstanceLocalService.buildCPInstances(
			cpDefinition.getCPDefinitionId(),
			ServiceContextTestUtil.getServiceContext(
				cpDefinition.getGroupId()));

		CPInstance cpInstance = cpInstances.get(0);

		Map<String, List<String>>
			cpDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys =
				_cpDefinitionOptionRelLocalService.
					getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
						cpInstance.getCPInstanceId());

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionRels.get(0);

		Assert.assertTrue(
			"Key map does not contain the key: " +
				cpDefinitionOptionRel.getKey(),
			cpDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys.containsKey(
				cpDefinitionOptionRel.getKey()));

		List<String> strings =
			cpDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys.get(
				cpDefinitionOptionRel.getKey());

		Assert.assertTrue(strings.size() == 3);
		Assert.assertTrue(
			Objects.equals(
				cpDefinitionOptionRel.getName(
					cpDefinitionOptionRel.getDefaultLanguageId()),
				strings.get(0)));

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			cpDefinitionOptionRel.getCPDefinitionOptionValueRels();

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRels.get(0);

		Assert.assertTrue(
			Objects.equals(
				cpDefinitionOptionValueRel.getKey(), strings.get(2)));
		Assert.assertTrue(
			Objects.equals(
				cpDefinitionOptionValueRel.getName(
					cpDefinitionOptionRel.getDefaultLanguageId()),
				strings.get(1)));
	}

	@Test
	public void testGetOrAddEmptyCPDefinitionOptionRel() throws Exception {
		frutillaRule.scenario(
			"Get or add an empty product definition option"
		).given(
			"An existing product, product option and external reference code"
		).when(
			"An empty product definition option is requested"
		).then(
			"A NoSuchCPDefinitionOptionRelException is thrown while lazy " +
				"referencing is disabled"
		).and(
			"An empty stub with the given external reference code is " +
				"returned while lazy referencing is enabled"
		).and(
			"The same product definition option is resolved on subsequent " +
				"requests"
		).and(
			"The empty status is cleared once the stub is updated"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId());

		String commerceOptionTypeKey =
			CPTestUtil.getDefaultCommerceOptionTypeKey(false);

		CPOption cpOption = CPTestUtil.addCPOption(
			_commerceCatalog.getGroupId(), commerceOptionTypeKey, false);

		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			_cpDefinitionOptionRelLocalService.
				getOrAddEmptyCPDefinitionOptionRel(
					externalReferenceCode, _serviceContext.getCompanyId(),
					_serviceContext.getUserId(),
					cpDefinition.getCPDefinitionId(), cpOption.getCPOptionId(),
					commerceOptionTypeKey);

			Assert.fail();
		}
		catch (NoSuchCPDefinitionOptionRelException
					noSuchCPDefinitionOptionRelException) {

			Assert.assertNotNull(noSuchCPDefinitionOptionRelException);
		}

		CPDefinitionOptionRel cpDefinitionOptionRel = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			cpDefinitionOptionRel =
				_cpDefinitionOptionRelLocalService.
					getOrAddEmptyCPDefinitionOptionRel(
						externalReferenceCode, _serviceContext.getCompanyId(),
						_serviceContext.getUserId(),
						cpDefinition.getCPDefinitionId(),
						cpOption.getCPOptionId(), commerceOptionTypeKey);

			_cpDefinitionOptionRels.add(cpDefinitionOptionRel);

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY,
				cpDefinitionOptionRel.getStatus());
			Assert.assertEquals(
				externalReferenceCode,
				cpDefinitionOptionRel.getExternalReferenceCode());
			Assert.assertEquals(
				cpDefinition.getCPDefinitionId(),
				cpDefinitionOptionRel.getCPDefinitionId());
			Assert.assertEquals(
				cpDefinition.getGroupId(), cpDefinitionOptionRel.getGroupId());
			Assert.assertEquals(
				cpOption.getCPOptionId(),
				cpDefinitionOptionRel.getCPOptionId());
			Assert.assertEquals(
				commerceOptionTypeKey,
				cpDefinitionOptionRel.getCommerceOptionTypeKey());

			CPDefinitionOptionRel resolvedCPDefinitionOptionRel =
				_cpDefinitionOptionRelLocalService.
					getOrAddEmptyCPDefinitionOptionRel(
						externalReferenceCode, _serviceContext.getCompanyId(),
						_serviceContext.getUserId(),
						cpDefinition.getCPDefinitionId(),
						cpOption.getCPOptionId(), commerceOptionTypeKey);

			Assert.assertEquals(
				cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
				resolvedCPDefinitionOptionRel.getCPDefinitionOptionRelId());
		}

		cpDefinitionOptionRel =
			_cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
				cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
				cpOption.getCPOptionId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), commerceOptionTypeKey,
				0, false, false, false, _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			cpDefinitionOptionRel.getStatus());
	}

	@Test
	public void testUpdateCPDefinitionOptionRelPriceType() throws Exception {
		frutillaRule.scenario(
			"Update product option's priceType attribute"
		).given(
			"A product and product options with no priceable option values"
		).when(
			"The option price type attribute is updated to any valid value"
		).then(
			"option update validation always succeeds"
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

		Assert.assertTrue(
			Validator.isNull(cpDefinitionOptionRel.getPriceType()));

		cpDefinitionOptionRel = _updatePriceType(
			cpDefinitionOptionRel,
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);

		Assert.assertEquals(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC,
			cpDefinitionOptionRel.getPriceType());

		cpDefinitionOptionRel = _updatePriceType(
			cpDefinitionOptionRel,
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC);

		Assert.assertEquals(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
			cpDefinitionOptionRel.getPriceType());

		cpDefinitionOptionRel = _updatePriceType(cpDefinitionOptionRel, null);

		Assert.assertTrue(
			Validator.isNull(cpDefinitionOptionRel.getPriceType()));
	}

	@Test
	public void testValidatePriceTypeNotChanged() throws Exception {
		frutillaRule.scenario(
			"Update product option's priceType attribute"
		).given(
			"A product and product options with priceable option values"
		).when(
			"The option price type attribute is not changed"
		).then(
			"option update validation always succeeds"
		);

		_assertPriceTypeUpdate(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC);
		_assertPriceTypeUpdate(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC,
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);
	}

	@Test(expected = CPDefinitionOptionRelPriceTypeException.class)
	public void testValidatePriceTypeUpdatePriceTypeDynamic() throws Exception {
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

		_assertPriceTypeUpdate(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);
	}

	@Test(expected = CPDefinitionOptionRelPriceTypeException.class)
	public void testValidatePriceTypeUpdatePriceTypeNull1() throws Exception {
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

		_assertPriceTypeUpdate(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, null);
	}

	@Test(expected = CPDefinitionOptionRelPriceTypeException.class)
	public void testValidatePriceTypeUpdatePriceTypeNull2() throws Exception {
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

		_assertPriceTypeUpdate(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, null);
	}

	@Test
	public void testValidatePriceTypeUpdatePriceTypeStatic() throws Exception {
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

		_assertPriceTypeUpdate(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC,
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC);
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private void _assertPriceTypeUpdate(
			String currentPriceType, String newPriceType)
		throws Exception {

		CPDefinition cpDefinition =
			CPTestUtil.addCPDefinitionWithChildCPDefinitions(
				_commerceCatalog.getGroupId(), currentPriceType);

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
				cpDefinition.getCPDefinitionId());

		_cpDefinitionOptionRels.addAll(cpDefinitionOptionRels);

		Assert.assertFalse(cpDefinitionOptionRels.isEmpty());

		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				cpDefinitionOptionRels) {

			Assert.assertEquals(
				currentPriceType, cpDefinitionOptionRel.getPriceType());

			Assert.assertTrue(
				_cpDefinitionOptionValueRelLocalService.
					hasCPDefinitionOptionValueRels(
						cpDefinitionOptionRel.getCPDefinitionOptionRelId()));

			cpDefinitionOptionRel = _updatePriceType(
				cpDefinitionOptionRel, newPriceType);

			Assert.assertEquals(
				newPriceType, cpDefinitionOptionRel.getPriceType());
		}
	}

	private CPDefinitionOptionRel _updatePriceType(
			CPDefinitionOptionRel cpDefinitionOptionRel, String priceType)
		throws Exception {

		return _cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
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
			cpDefinitionOptionRel.isSkuContributor(), priceType,
			cpDefinitionOptionRel.getTypeSettings(),
			ServiceContextTestUtil.getServiceContext(
				cpDefinitionOptionRel.getGroupId()));
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
	private CPOptionLocalService _cpOptionLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;
	private User _user;

}