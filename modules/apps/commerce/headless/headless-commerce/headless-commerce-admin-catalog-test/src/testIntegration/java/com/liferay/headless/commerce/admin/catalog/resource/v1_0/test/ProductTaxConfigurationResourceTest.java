/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.configuration.CProductVersionConfiguration;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductTaxConfiguration;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 */
@RunWith(Arquillian.class)
public class ProductTaxConfigurationResourceTest
	extends BaseProductTaxConfigurationResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_adminUser = UserTestUtil.getAdminUser(testCompany.getCompanyId());
	}

	@Ignore
	@Override
	@Test
	public void testClientSerDesToDTO() throws Exception {
		super.testClientSerDesToDTO();
	}

	@Ignore
	@Override
	@Test
	public void testClientSerDesToJSON() throws Exception {
		super.testClientSerDesToJSON();
	}

	@Ignore
	@Override
	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		super.testEscapeRegexInStringFields();
	}

	@Ignore
	@Override
	@Test
	public void testGetProductByExternalReferenceCodeTaxConfiguration()
		throws Exception {

		super.testGetProductByExternalReferenceCodeTaxConfiguration();
	}

	@Ignore
	@Override
	@Test
	public void testGetProductIdTaxConfiguration() throws Exception {
		super.testGetProductIdTaxConfiguration();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductByExternalReferenceCodeTaxConfiguration()
		throws Exception {

		super.testGraphQLGetProductByExternalReferenceCodeTaxConfiguration();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductByExternalReferenceCodeTaxConfigurationNotFound()
		throws Exception {

		super.
			testGraphQLGetProductByExternalReferenceCodeTaxConfigurationNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductIdTaxConfiguration() throws Exception {
		super.testGraphQLGetProductIdTaxConfiguration();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductIdTaxConfigurationNotFound()
		throws Exception {

		super.testGraphQLGetProductIdTaxConfigurationNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testPatchProductByExternalReferenceCodeTaxConfiguration()
		throws Exception {

		super.testPatchProductByExternalReferenceCodeTaxConfiguration();
	}

	@Ignore
	@Override
	@Test
	public void testPatchProductIdTaxConfiguration() throws Exception {
		super.testPatchProductIdTaxConfiguration();
	}

	@Test
	public void testPatchProductIdTaxConfigurationProductVersioning()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						CProductVersionConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			CommerceCatalog commerceCatalog =
				CPTestUtil.getSystemCommerceCatalog(testCompany.getCompanyId());

			CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
				commerceCatalog.getGroupId(), "simple", null, null, true, true,
				WorkflowConstants.STATUS_APPROVED);

			_cpDefinitions.add(cpDefinition1);

			CPTaxCategory cpTaxCategory =
				_cpTaxCategoryLocalService.addCPTaxCategory(
					null,
					Collections.singletonMap(
						LocaleUtil.getDefault(), "Standard"),
					null,
					ServiceContextTestUtil.getServiceContext(
						testGroup.getGroupId()));

			Assert.assertEquals(
				1,
				_cpDefinitionLocalService.getCProductCPDefinitions(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS
				).size());

			ProductTaxConfiguration productTaxConfiguration1 =
				productTaxConfigurationResource.getProductIdTaxConfiguration(
					cpDefinition1.getCProductId());

			productTaxConfiguration1.setTaxCategory(cpTaxCategory.getName());

			productTaxConfigurationResource.patchProductIdTaxConfiguration(
				cpDefinition1.getCProductId(), productTaxConfiguration1);

			Assert.assertEquals(
				1,
				_cpDefinitionLocalService.getCProductCPDefinitions(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS
				).size());

			List<CPDefinition> draftDefinitions =
				_cpDefinitionLocalService.getCProductCPDefinitions(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_DRAFT, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			Assert.assertEquals(
				draftDefinitions.toString(), 1, draftDefinitions.size());

			CPDefinition cpDefinition2 = draftDefinitions.get(0);

			_cpDefinitions.add(cpDefinition2);

			ProductTaxConfiguration productTaxConfiguration2 =
				productTaxConfigurationResource.getProductIdTaxConfiguration(
					cpDefinition1.getCProductId());

			productTaxConfiguration2.setTaxable(true);

			productTaxConfigurationResource.patchProductIdTaxConfiguration(
				cpDefinition1.getCProductId(), productTaxConfiguration2);

			Assert.assertEquals(
				1,
				_cpDefinitionLocalService.getCProductCPDefinitions(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS
				).size());

			Assert.assertEquals(
				1,
				_cpDefinitionLocalService.getCProductCPDefinitions(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_DRAFT, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS
				).size());

			CPDefinition cpDefinition3 =
				_cpDefinitionLocalService.fetchCPDefinitionByCProductId(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_DRAFT);

			Assert.assertEquals(
				cpDefinition2.getCPDefinitionId(),
				cpDefinition3.getCPDefinitionId());
		}
	}

	private User _adminUser;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@DeleteAfterTestRun
	private List<CPDefinition> _cpDefinitions = new ArrayList<>();

	@Inject
	private CPDefinitionService _cpDefinitionService;

	@Inject
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

	@Inject
	private ServiceContextHelper _serviceContextHelper;

}