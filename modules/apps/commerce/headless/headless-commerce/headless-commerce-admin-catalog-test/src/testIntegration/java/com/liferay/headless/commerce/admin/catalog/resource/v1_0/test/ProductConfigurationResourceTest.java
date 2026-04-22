/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.configuration.CProductVersionConfiguration;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfiguration;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 */
@RunWith(Arquillian.class)
public class ProductConfigurationResourceTest
	extends BaseProductConfigurationResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser();

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			RandomTestUtil.randomString(), 0L, RandomTestUtil.randomString(),
			"USD", "en_US", false,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), _user.getUserId()));

		_masterCPConfigurationList =
			_cpConfigurationListLocalService.getMasterCPConfigurationList(
				_commerceCatalog.getGroupId());

		_cpConfigurationList =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(),
				_masterCPConfigurationList.getCPConfigurationListId(), false,
				RandomTestUtil.randomString(), 2, 1, 1, 2024, 0, 0, 0, 0, 0, 0,
				0, true, new ServiceContext());

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());
	}

	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	public void testDeleteProductConfiguration() throws Exception {
		super.testDeleteProductConfiguration();
	}

	@Override
	@Test
	public void testDeleteProductConfigurationBatch() throws Exception {
		super.testDeleteProductConfigurationBatch();
	}

	@Override
	@Test
	public void testDeleteProductConfigurationByExternalReferenceCode()
		throws Exception {

		super.testDeleteProductConfigurationByExternalReferenceCode();
	}

	@Override
	@Test
	public void testGetProductByExternalReferenceCodeConfiguration()
		throws Exception {

		super.testGetProductByExternalReferenceCodeConfiguration();
	}

	@Override
	@Test
	public void testGetProductConfiguration() throws Exception {
		super.testGetProductConfiguration();
	}

	@Override
	@Test
	public void testGetProductConfigurationByExternalReferenceCode()
		throws Exception {

		super.testGetProductConfigurationByExternalReferenceCode();
	}

	@Override
	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage()
		throws Exception {

		super.
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage();
	}

	@Override
	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithFilterDateTimeEquals()
		throws Exception {

		super.
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithFilterDateTimeEquals();
	}

	@Override
	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithPagination()
		throws Exception {

		super.
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithPagination();
	}

	@Override
	@Test
	public void testGetProductConfigurationListIdProductConfigurationsPage()
		throws Exception {

		super.testGetProductConfigurationListIdProductConfigurationsPage();
	}

	@Override
	@Test
	public void testGetProductConfigurationListIdProductConfigurationsPageWithPagination()
		throws Exception {

		super.
			testGetProductConfigurationListIdProductConfigurationsPageWithPagination();
	}

	@Override
	@Test
	public void testGetProductIdConfiguration() throws Exception {
		super.testGetProductIdConfiguration();
	}

	@Override
	@Test
	public void testGraphQLDeleteProductConfiguration() throws Exception {
		super.testGraphQLDeleteProductConfiguration();
	}

	@Override
	@Test
	public void testGraphQLDeleteProductConfigurationByExternalReferenceCode()
		throws Exception {

		super.testGraphQLDeleteProductConfigurationByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductByExternalReferenceCodeConfiguration()
		throws Exception {

		super.testGraphQLGetProductByExternalReferenceCodeConfiguration();
	}

	@Override
	@Test
	public void testGraphQLGetProductConfiguration() throws Exception {
		super.testGraphQLGetProductConfiguration();
	}

	@Override
	@Test
	public void testGraphQLGetProductConfigurationByExternalReferenceCode()
		throws Exception {

		super.testGraphQLGetProductConfigurationByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductIdConfiguration() throws Exception {
		super.testGraphQLGetProductIdConfiguration();
	}

	@Override
	@Test
	public void testPatchProductByExternalReferenceCodeConfiguration()
		throws Exception {

		ProductConfiguration randomProductConfiguration =
			randomProductConfiguration();

		productConfigurationResource.
			patchProductByExternalReferenceCodeConfiguration(
				randomProductConfiguration.getEntityExternalReferenceCode(),
				randomProductConfiguration);

		ProductConfiguration productConfiguration =
			productConfigurationResource.
				getProductByExternalReferenceCodeConfiguration(
					randomProductConfiguration.
						getEntityExternalReferenceCode());

		Assert.assertTrue(
			equals(productConfiguration, randomProductConfiguration));
	}

	@Override
	@Test
	public void testPatchProductConfiguration() throws Exception {
		super.testPatchProductConfiguration();
	}

	@Override
	@Test
	public void testPatchProductConfigurationByExternalReferenceCode()
		throws Exception {

		super.testPatchProductConfigurationByExternalReferenceCode();
	}

	@Override
	@Test
	public void testPatchProductIdConfiguration() throws Exception {
		ProductConfiguration randomProductConfiguration =
			randomProductConfiguration();

		productConfigurationResource.patchProductIdConfiguration(
			randomProductConfiguration.getEntityId(),
			randomProductConfiguration);

		ProductConfiguration productConfiguration =
			productConfigurationResource.getProductIdConfiguration(
				randomProductConfiguration.getEntityId());

		Assert.assertTrue(
			equals(productConfiguration, randomProductConfiguration));
	}

	@Test
	public void testPatchProductIdConfigurationProductVersioning()
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

			ProductConfiguration productConfiguration1 =
				productConfigurationResource.getProductIdConfiguration(
					cpDefinition1.getCProductId());

			productConfiguration1.setAllowBackOrder(true);

			productConfigurationResource.patchProductIdConfiguration(
				cpDefinition1.getCProductId(), productConfiguration1);

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

			ProductConfiguration productConfiguration2 =
				productConfigurationResource.getProductIdConfiguration(
					cpDefinition1.getCProductId());

			productConfiguration2.setAllowBackOrder(false);

			productConfigurationResource.patchProductIdConfiguration(
				cpDefinition1.getCProductId(), productConfiguration2);

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

	@Override
	@Test
	public void testPostProductConfigurationListByExternalReferenceCodeProductConfiguration()
		throws Exception {

		super.
			testPostProductConfigurationListByExternalReferenceCodeProductConfiguration();
	}

	@Override
	@Test
	public void testPostProductConfigurationListIdProductConfiguration()
		throws Exception {

		super.testPostProductConfigurationListIdProductConfiguration();
	}

	@Ignore
	@Override
	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		super.testVulcanCRUDItemDelegateGetItem();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"inventoryEngine", "lowStockAction", "maxOrderQuantity",
			"minOrderQuantity", "minStockQuantity", "multipleOrderQuantity"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {
			"purchasable", "shippable", "shippable", "categoryIds",
			"categoryNames", "createDate", "modifiedDate", "maxOrderQuantity",
			"minOrderQuantity", "multipleOrderQuantity", "entityName",
			"productType"
		};
	}

	@Override
	protected ProductConfiguration randomProductConfiguration() {
		try {
			CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
				_commerceCatalog.getGroupId(), "simple");

			CProduct cProduct = cpDefinition.getCProduct();

			return new ProductConfiguration() {
				{
					allowBackOrder = RandomTestUtil.randomBoolean();
					allowedOrderQuantities = new BigDecimal[0];
					availabilityEstimateId = 0L;
					entityExternalReferenceCode =
						cProduct.getExternalReferenceCode();
					entityId = cpDefinition.getCProductId();
					externalReferenceCode = RandomTestUtil.randomString();
					inventoryEngine = RandomTestUtil.randomString();
					lowStockAction = RandomTestUtil.randomString();
					maxOrderQuantity = BigDecimal.ONE;
					minOrderQuantity = BigDecimal.ONE;
					minStockQuantity = BigDecimal.ONE;
					multipleOrderQuantity = BigDecimal.ONE;
				}
			};
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			throw new SystemException(portalException);
		}
	}

	@Override
	protected ProductConfiguration
			testDeleteProductConfiguration_addProductConfiguration()
		throws Exception {

		return productConfigurationResource.
			postProductConfigurationListIdProductConfiguration(
				_cpConfigurationList.getCPConfigurationListId(),
				randomProductConfiguration());
	}

	@Override
	protected ProductConfiguration
			testDeleteProductConfigurationByExternalReferenceCode_addProductConfiguration()
		throws Exception {

		return productConfigurationResource.
			postProductConfigurationListIdProductConfiguration(
				_cpConfigurationList.getCPConfigurationListId(),
				randomProductConfiguration());
	}

	@Override
	protected ProductConfiguration
			testGetProductByExternalReferenceCodeConfiguration_addProductConfiguration()
		throws Exception {

		return testGetProductIdConfiguration_addProductConfiguration();
	}

	@Override
	protected String
			testGetProductByExternalReferenceCodeConfiguration_getExternalReferenceCode(
				ProductConfiguration productConfiguration)
		throws Exception {

		return productConfiguration.getEntityExternalReferenceCode();
	}

	@Override
	protected ProductConfiguration
			testGetProductConfiguration_addProductConfiguration()
		throws Exception {

		return productConfigurationResource.
			postProductConfigurationListIdProductConfiguration(
				_cpConfigurationList.getCPConfigurationListId(),
				randomProductConfiguration());
	}

	@Override
	protected ProductConfiguration
			testGetProductConfigurationByExternalReferenceCode_addProductConfiguration()
		throws Exception {

		return productConfigurationResource.
			postProductConfigurationListIdProductConfiguration(
				_cpConfigurationList.getCPConfigurationListId(),
				randomProductConfiguration());
	}

	@Override
	protected ProductConfiguration
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_addProductConfiguration(
				String externalReferenceCode,
				ProductConfiguration productConfiguration)
		throws Exception {

		return productConfigurationResource.
			postProductConfigurationListByExternalReferenceCodeProductConfiguration(
				externalReferenceCode, productConfiguration);
	}

	@Override
	protected String
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_getExternalReferenceCode()
		throws Exception {

		return _cpConfigurationList.getExternalReferenceCode();
	}

	@Override
	protected ProductConfiguration
			testGetProductConfigurationListIdProductConfigurationsPage_addProductConfiguration(
				Long id, ProductConfiguration productConfiguration)
		throws Exception {

		return productConfigurationResource.
			postProductConfigurationListIdProductConfiguration(
				id, productConfiguration);
	}

	@Override
	protected Long
			testGetProductConfigurationListIdProductConfigurationsPage_getId()
		throws Exception {

		return _cpConfigurationList.getCPConfigurationListId();
	}

	@Override
	protected ProductConfiguration
			testGetProductIdConfiguration_addProductConfiguration()
		throws Exception {

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId(), "simple");

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryLocalService.getCPConfigurationEntry(
				_classNameLocalService.getClassNameId(CPDefinition.class),
				cpDefinition.getCPDefinitionId(),
				_masterCPConfigurationList.getCPConfigurationListId());

		return productConfigurationResource.getProductConfiguration(
			cpConfigurationEntry.getCPConfigurationEntryId());
	}

	@Override
	protected Long testGetProductIdConfiguration_getId(
			ProductConfiguration productConfiguration)
		throws Exception {

		CProduct cProduct =
			_cProductLocalService.getCProductByExternalReferenceCode(
				productConfiguration.getEntityExternalReferenceCode(),
				_cpConfigurationList.getCompanyId());

		return cProduct.getCProductId();
	}

	@Override
	protected ProductConfiguration
			testGraphQLProductConfiguration_addProductConfiguration()
		throws Exception {

		return productConfigurationResource.
			postProductConfigurationListIdProductConfiguration(
				_cpConfigurationList.getCPConfigurationListId(),
				randomProductConfiguration());
	}

	@Override
	protected ProductConfiguration
			testPatchProductConfiguration_addProductConfiguration()
		throws Exception {

		return productConfigurationResource.
			postProductConfigurationListIdProductConfiguration(
				_cpConfigurationList.getCPConfigurationListId(),
				randomProductConfiguration());
	}

	@Override
	protected ProductConfiguration
			testPatchProductConfigurationByExternalReferenceCode_addProductConfiguration()
		throws Exception {

		return productConfigurationResource.
			postProductConfigurationListIdProductConfiguration(
				_cpConfigurationList.getCPConfigurationListId(),
				randomProductConfiguration());
	}

	@Override
	protected ProductConfiguration
			testPostProductConfigurationListByExternalReferenceCodeProductConfiguration_addProductConfiguration(
				ProductConfiguration productConfiguration)
		throws Exception {

		return productConfigurationResource.
			postProductConfigurationListIdProductConfiguration(
				_cpConfigurationList.getCPConfigurationListId(),
				productConfiguration);
	}

	@Override
	protected ProductConfiguration
			testPostProductConfigurationListIdProductConfiguration_addProductConfiguration(
				ProductConfiguration productConfiguration)
		throws Exception {

		return productConfigurationResource.
			postProductConfigurationListIdProductConfiguration(
				_cpConfigurationList.getCPConfigurationListId(),
				productConfiguration);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductConfigurationResourceTest.class);

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CPConfigurationEntryLocalService _cpConfigurationEntryLocalService;

	private CPConfigurationList _cpConfigurationList;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@DeleteAfterTestRun
	private List<CPDefinition> _cpDefinitions = new ArrayList<>();

	@Inject
	private CPDefinitionService _cpDefinitionService;

	@Inject
	private CProductLocalService _cProductLocalService;

	private CPConfigurationList _masterCPConfigurationList;

	@Inject
	private ServiceContextHelper _serviceContextHelper;

	private User _testCompanyAdminUser;
	private User _user;

}