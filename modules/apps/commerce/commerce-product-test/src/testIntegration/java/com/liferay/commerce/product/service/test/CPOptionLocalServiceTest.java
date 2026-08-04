/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.exception.CPOptionSKUContributorException;
import com.liferay.commerce.product.exception.NoSuchCPOptionException;
import com.liferay.commerce.product.exception.RequiredCPOptionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
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
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Arrays;
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
 * @author Igor Beslic
 */
@RunWith(Arquillian.class)
public class CPOptionLocalServiceTest {

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
	}

	@After
	public void tearDown() throws Exception {
		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				_cpDefinitionOptionRels) {

			_cpDefinitionOptionRelLocalService.deleteCPDefinitionOptionRel(
				cpDefinitionOptionRel);
		}

		_cpOptionLocalService.deleteCPOptions(_serviceContext.getCompanyId());
	}

	@Test
	public void testAddOption() throws Exception {
		String[] cpOptionFieldTypes = CPTestUtil.getCPOptionFieldTypes();

		frutillaRule.scenario(
			"Add SKU contributor option"
		).given(
			"There is no any options"
		).when(
			String.format(
				"%d SKU contributor options are added",
				cpOptionFieldTypes.length)
		).and(
			String.format(
				"option types are [%s]", Arrays.toString(cpOptionFieldTypes))
		).then(
			String.format(
				"%d options should be created", cpOptionFieldTypes.length)
		);

		_addCPOptions(cpOptionFieldTypes, false, _serviceContext);

		Assert.assertEquals(
			"Options count", cpOptionFieldTypes.length,
			_cpOptionLocalService.getCPOptionsCount(
				_serviceContext.getCompanyId()));
	}

	@Test(expected = CPOptionSKUContributorException.class)
	public void testAddOptionIfOptionTypeIsInvalid() throws Exception {
		frutillaRule.scenario(
			"Add option with invalid option type"
		).given(
			"There is no any options"
		).when(
			"Option with type invalid_type is added"
		).then(
			"option creation should fail"
		);

		_cpOptionLocalService.addCPOption(
			null, _serviceContext.getUserId(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), "invalid_type",
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			false, RandomTestUtil.randomString(), _serviceContext);
	}

	@Test(expected = CPOptionSKUContributorException.class)
	public void testAddOptionIfOptionTypeIsNull() throws Exception {
		frutillaRule.scenario(
			"Add SKU contributor option with boolean option option type"
		).given(
			"There is no any options"
		).when(
			"SKU contributor option is added"
		).and(
			"option is boolean"
		).then(
			"option creation should fail"
		);

		_cpOptionLocalService.addCPOption(
			null, _serviceContext.getUserId(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), null,
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			false, RandomTestUtil.randomString(), _serviceContext);
	}

	@Test
	public void testAddOptionSKUContributor() throws Exception {
		String[] cpOptionSKUContributorFieldTypes =
			CPConstants.PRODUCT_OPTION_SKU_CONTRIBUTOR_FIELD_TYPES;

		frutillaRule.scenario(
			"Add SKU contributor option"
		).given(
			"There is no any options"
		).when(
			String.format(
				"%d SKU contributor options are added",
				cpOptionSKUContributorFieldTypes.length)
		).and(
			String.format(
				"option types are [%s]",
				Arrays.toString(cpOptionSKUContributorFieldTypes))
		).then(
			String.format(
				"%d options should be created",
				cpOptionSKUContributorFieldTypes.length)
		);

		_addCPOptions(cpOptionSKUContributorFieldTypes, true, _serviceContext);

		Assert.assertEquals(
			"SKU contributor options count",
			cpOptionSKUContributorFieldTypes.length,
			_cpOptionLocalService.getCPOptionsCount(
				_serviceContext.getCompanyId()));
	}

	@Test(expected = CPOptionSKUContributorException.class)
	public void testAddOptionSKUContributorIfInvalidOptionType()
		throws Exception {

		frutillaRule.scenario(
			"Add SKU contributor option with boolean option type"
		).given(
			"There is no any option"
		).when(
			"SKU contributor option is added"
		).and(
			"option is boolean"
		).then(
			"option creation should fail"
		);

		_cpOptionLocalService.addCPOption(
			null, _serviceContext.getUserId(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), "checkbox",
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			true, RandomTestUtil.randomString(), _serviceContext);
	}

	@Test(expected = RequiredCPOptionException.class)
	public void testDeleteOptionUsedByProduct() throws Exception {
		frutillaRule.scenario(
			"Deleting an Option that is being used by a product"
		).given(
			"An Option and a Product that is using it"
		).when(
			"the option is deleted"
		).then(
			"the deletion should fail and throw an exception"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(), null,
				LocaleUtil.US.getDisplayLanguage(), _serviceContext);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			commerceCatalog.getGroupId());

		CPOption cpOption = CPTestUtil.addCPOption(
			commerceCatalog.getGroupId(), false);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelLocalService.addCPDefinitionOptionRel(
				cpDefinition.getCPDefinitionId(), cpOption.getCPOptionId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				CPConstants.PRODUCT_OPTION_SELECT_DATE_KEY,
				RandomTestUtil.randomDouble(), false, true, true, false,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, _serviceContext);

		_cpDefinitionOptionRels.add(cpDefinitionOptionRel);

		_cpOptionLocalService.deleteCPOption(cpOption);
	}

	@Test
	public void testGetOrAddEmptyCPOption() throws Exception {
		frutillaRule.scenario(
			"Get or add an empty product option"
		).given(
			"A company and an external reference code"
		).when(
			"An empty product option is requested"
		).then(
			"A NoSuchCPOptionException is thrown while lazy referencing is " +
				"disabled"
		).and(
			"An empty stub with the given external reference code is " +
				"returned while lazy referencing is enabled"
		).and(
			"The same product option is resolved on subsequent requests"
		).and(
			"The empty status is cleared once the stub is updated"
		);

		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			_cpOptionLocalService.getOrAddEmptyCPOption(
				externalReferenceCode, _serviceContext.getCompanyId(),
				_serviceContext.getUserId());

			Assert.fail();
		}
		catch (NoSuchCPOptionException noSuchCPOptionException) {
			Assert.assertNotNull(noSuchCPOptionException);
		}

		CPOption cpOption = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			cpOption = _cpOptionLocalService.getOrAddEmptyCPOption(
				externalReferenceCode, _serviceContext.getCompanyId(),
				_serviceContext.getUserId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, cpOption.getStatus());
			Assert.assertEquals(
				externalReferenceCode, cpOption.getExternalReferenceCode());

			CPOption resolvedCPOption =
				_cpOptionLocalService.getOrAddEmptyCPOption(
					externalReferenceCode, _serviceContext.getCompanyId(),
					_serviceContext.getUserId());

			Assert.assertEquals(
				cpOption.getCPOptionId(), resolvedCPOption.getCPOptionId());
		}

		String[] cpOptionFieldTypes = CPTestUtil.getCPOptionFieldTypes();

		cpOption = _cpOptionLocalService.updateCPOption(
			cpOption.getCPOptionId(), RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), cpOptionFieldTypes[0],
			false, false, false, RandomTestUtil.randomString(),
			_serviceContext);

		Assert.assertNotEquals(
			WorkflowConstants.STATUS_EMPTY, cpOption.getStatus());
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private void _addCPOptions(
			String[] optionFieldTypes, boolean skuContributor,
			ServiceContext serviceContext)
		throws Exception {

		for (String optionFieldType : optionFieldTypes) {
			_cpOptionLocalService.addCPOption(
				null, _serviceContext.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), optionFieldType,
				RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
				skuContributor, RandomTestUtil.randomString(), serviceContext);
		}
	}

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	private final List<CPDefinitionOptionRel> _cpDefinitionOptionRels =
		new ArrayList<>();

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;
	private User _user;

}