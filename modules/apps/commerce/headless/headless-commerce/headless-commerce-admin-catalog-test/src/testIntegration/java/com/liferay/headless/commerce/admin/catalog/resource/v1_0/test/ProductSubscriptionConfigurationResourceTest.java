/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.configuration.CProductVersionConfiguration;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductSubscriptionConfiguration;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 */
@RunWith(Arquillian.class)
public class ProductSubscriptionConfigurationResourceTest
	extends BaseProductSubscriptionConfigurationResourceTestCase {

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
	public void testGetProductByExternalReferenceCodeSubscriptionConfiguration()
		throws Exception {

		super.testGetProductByExternalReferenceCodeSubscriptionConfiguration();
	}

	@Ignore
	@Override
	@Test
	public void testGetProductIdSubscriptionConfiguration() throws Exception {
		super.testGetProductIdSubscriptionConfiguration();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductByExternalReferenceCodeSubscriptionConfiguration()
		throws Exception {

		super.
			testGraphQLGetProductByExternalReferenceCodeSubscriptionConfiguration();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductByExternalReferenceCodeSubscriptionConfigurationNotFound()
		throws Exception {

		super.
			testGraphQLGetProductByExternalReferenceCodeSubscriptionConfigurationNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductIdSubscriptionConfiguration()
		throws Exception {

		super.testGraphQLGetProductIdSubscriptionConfiguration();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductIdSubscriptionConfigurationNotFound()
		throws Exception {

		super.testGraphQLGetProductIdSubscriptionConfigurationNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testPatchProductByExternalReferenceCodeSubscriptionConfiguration()
		throws Exception {

		super.
			testPatchProductByExternalReferenceCodeSubscriptionConfiguration();
	}

	@Ignore
	@Override
	@Test
	public void testPatchProductIdSubscriptionConfiguration() throws Exception {
		super.testPatchProductIdSubscriptionConfiguration();
	}

	@Test
	public void testPatchProductIdSubscriptionConfigurationProductVersioning()
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

			Assert.assertEquals(
				1,
				_cpDefinitionLocalService.getCProductCPDefinitions(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS
				).size());

			ProductSubscriptionConfiguration productSubscriptionConfiguration1 =
				productSubscriptionConfigurationResource.
					getProductIdSubscriptionConfiguration(
						cpDefinition1.getCProductId());

			productSubscriptionConfiguration1.setEnable(true);
			productSubscriptionConfiguration1.setSubscriptionType(
				ProductSubscriptionConfiguration.SubscriptionType.MONTHLY);
			productSubscriptionConfiguration1.setNumberOfLength(1L);
			productSubscriptionConfiguration1.setLength(12);

			productSubscriptionConfiguration1.setSubscriptionTypeSettings(
				HashMapBuilder.put(
					"monthlyMode", "0"
				).build());

			productSubscriptionConfigurationResource.
				patchProductIdSubscriptionConfiguration(
					cpDefinition1.getCProductId(),
					productSubscriptionConfiguration1);

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

			ProductSubscriptionConfiguration productSubscriptionConfiguration2 =
				productSubscriptionConfigurationResource.
					getProductIdSubscriptionConfiguration(
						cpDefinition1.getCProductId());

			productSubscriptionConfiguration2.setEnable(false);

			productSubscriptionConfigurationResource.
				patchProductIdSubscriptionConfiguration(
					cpDefinition1.getCProductId(),
					productSubscriptionConfiguration2);

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

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@DeleteAfterTestRun
	private List<CPDefinition> _cpDefinitions = new ArrayList<>();

	@Inject
	private CPDefinitionService _cpDefinitionService;

	@Inject
	private ServiceContextHelper _serviceContextHelper;

}