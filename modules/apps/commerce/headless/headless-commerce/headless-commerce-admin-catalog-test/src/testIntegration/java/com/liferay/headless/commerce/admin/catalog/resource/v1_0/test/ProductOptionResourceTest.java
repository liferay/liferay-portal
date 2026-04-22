/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.configuration.CProductVersionConfiguration;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class ProductOptionResourceTest
	extends BaseProductOptionResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			testGroup.getGroupId(), BigDecimal.TEN);

		_cpDefinition = _cpInstance.getCPDefinition();

		_cProduct = _cpDefinition.getCProduct();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteProductOption() throws Exception {
		super.testDeleteProductOption();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteProductOption() throws Exception {
		super.testGraphQLDeleteProductOption();
	}

	@Override
	@Test
	public void testPatchProductOption() throws Exception {
		ProductOption postProductOption = _addProductOption();

		ProductOption randomPatchProductOption = randomPatchProductOption();

		productOptionResource.patchProductOption(
			postProductOption.getId(), randomPatchProductOption);

		ProductOption expectedProductOption = postProductOption.clone();

		BeanTestUtil.copyProperties(
			randomPatchProductOption, expectedProductOption);

		ProductOption getProductOption = productOptionResource.getProductOption(
			postProductOption.getId());

		assertEquals(expectedProductOption, getProductOption);
		assertValid(getProductOption);

		_testPatchProductOptionWithOptionExternalReferenceCode();
	}

	@Override
	@Test
	public void testPostProductByExternalReferenceCodeProductOptionsPage()
		throws Exception {

		ProductOption randomProductOption = randomProductOption();

		Page<ProductOption> productOptionPage =
			productOptionResource.
				postProductByExternalReferenceCodeProductOptionsPage(
					_cProduct.getExternalReferenceCode(),
					new ProductOption[] {randomProductOption});

		ProductOption postProductOption = _getLastProductOption(
			productOptionPage.getItems());

		assertEquals(randomProductOption, postProductOption);
		assertValid(postProductOption);

		_testPostProductByExternalReferenceCodeProductOptionsPageWithOptionExternalReferenceCode();
	}

	@Test
	public void testPostProductIdProductOptionProductVersioning()
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

			CPOption cpOption1 = CPTestUtil.addCPOption(
				commerceCatalog.getGroupId(), true);

			Assert.assertEquals(
				1,
				_cpDefinitionLocalService.getCProductCPDefinitions(
					cpDefinition1.getCProductId(),
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS
				).size());

			Assert.assertEquals(
				0,
				_cpDefinitionOptionRelLocalService.
					getCPDefinitionOptionRelsCount(
						cpDefinition1.getCPDefinitionId()));

			ProductOption productOption1 = randomProductOption();

			productOption1.setKey(cpOption1.getKey());

			productOptionResource.postProductIdProductOptionsPage(
				cpDefinition1.getCProductId(),
				new ProductOption[] {productOption1});

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

			Assert.assertEquals(
				0,
				_cpDefinitionOptionRelLocalService.
					getCPDefinitionOptionRelsCount(
						cpDefinition1.getCPDefinitionId()));

			Assert.assertEquals(
				1,
				_cpDefinitionOptionRelLocalService.
					getCPDefinitionOptionRelsCount(
						cpDefinition2.getCPDefinitionId()));

			CPOption cpOption2 = CPTestUtil.addCPOption(
				commerceCatalog.getGroupId(), true);

			ProductOption productOption2 = randomProductOption();

			productOption2.setKey(cpOption2.getKey());

			productOptionResource.postProductIdProductOptionsPage(
				cpDefinition1.getCProductId(),
				new ProductOption[] {productOption1, productOption2});

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

			Assert.assertEquals(
				2,
				_cpDefinitionOptionRelLocalService.
					getCPDefinitionOptionRelsCount(
						cpDefinition3.getCPDefinitionId()));
		}
	}

	@Override
	@Test
	public void testPostProductIdProductOptionsPage() throws Exception {
		ProductOption randomProductOption = randomProductOption();

		Page<ProductOption> productOptionPage =
			productOptionResource.postProductIdProductOptionsPage(
				_cProduct.getCProductId(),
				new ProductOption[] {randomProductOption});

		ProductOption postProductOption = _getLastProductOption(
			productOptionPage.getItems());

		assertEquals(randomProductOption, postProductOption);
		assertValid(postProductOption);

		_testPostProductIdProductOptionsPageWithOptionExternalReferenceCode();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"description", "facetable", "fieldType", "name", "required",
			"skuContributor"
		};
	}

	@Override
	protected Collection<EntityField> getEntityFields() throws Exception {
		try {
			return super.getEntityFields();
		}
		catch (NullPointerException nullPointerException) {
			Map<String, EntityField> entityFieldsMap = new HashMap<>();

			return entityFieldsMap.values();
		}
	}

	@Override
	protected ProductOption randomProductOption() throws Exception {
		CPOption cpOption = CPTestUtil.addCPOption(
			testGroup.getGroupId(), false);

		return new ProductOption() {
			{
				catalogId = testCompany.getCompanyId();
				description = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				facetable = RandomTestUtil.randomBoolean();
				fieldType = "text";
				id = RandomTestUtil.randomLong();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				optionExternalReferenceCode =
					cpOption.getExternalReferenceCode();
				optionId = cpOption.getCPOptionId();
				priority = RandomTestUtil.randomDouble();
				required = RandomTestUtil.randomBoolean();
				skuContributor = false;
			}
		};
	}

	@Override
	protected ProductOption testDeleteProductOption_addProductOption()
		throws Exception {

		return _addProductOption();
	}

	@Override
	protected ProductOption
			testGetProductByExternalReferenceCodeProductOptionsPage_addProductOption(
				String externalReferenceCode, ProductOption productOption)
		throws Exception {

		Page<ProductOption> productOptionPage =
			productOptionResource.
				postProductByExternalReferenceCodeProductOptionsPage(
					externalReferenceCode, new ProductOption[] {productOption});

		return _getLastProductOption(productOptionPage.getItems());
	}

	@Override
	protected String
			testGetProductByExternalReferenceCodeProductOptionsPage_getExternalReferenceCode()
		throws Exception {

		return _cProduct.getExternalReferenceCode();
	}

	@Override
	protected ProductOption testGetProductIdProductOptionsPage_addProductOption(
			Long id, ProductOption productOption)
		throws Exception {

		Page<ProductOption> productOptionPage =
			productOptionResource.postProductIdProductOptionsPage(
				id, new ProductOption[] {productOption});

		return _getLastProductOption(productOptionPage.getItems());
	}

	@Override
	protected Long testGetProductIdProductOptionsPage_getId() throws Exception {
		return _cProduct.getCProductId();
	}

	@Override
	protected ProductOption testGetProductOption_addProductOption()
		throws Exception {

		return _addProductOption();
	}

	@Override
	protected ProductOption testGraphQLProductOption_addProductOption()
		throws Exception {

		return _addProductOption();
	}

	private ProductOption _addProductOption() throws Exception {
		Page<ProductOption> productOptionPage =
			productOptionResource.postProductIdProductOptionsPage(
				_cProduct.getCProductId(),
				new ProductOption[] {randomProductOption()});

		return _getLastProductOption(productOptionPage.getItems());
	}

	private ProductOption _getLastProductOption(
		Collection<ProductOption> productOptions) {

		for (ProductOption productOption : productOptions) {
			long optionId = productOption.getOptionId();

			if (!_productOptions.containsKey(optionId)) {
				_productOptions.put(optionId, productOption);

				return productOption;
			}
		}

		return null;
	}

	private void _testPatchProductOptionWithOptionExternalReferenceCode()
		throws Exception {

		ProductOption postProductOption = _addProductOption();

		ProductOption randomPatchProductOption = randomPatchProductOption();

		long optionId = randomPatchProductOption.getOptionId();

		randomPatchProductOption.setOptionId(0L);

		productOptionResource.patchProductOption(
			postProductOption.getId(), randomPatchProductOption);

		randomPatchProductOption.setOptionId(optionId);

		ProductOption expectedProductOption = postProductOption.clone();

		BeanTestUtil.copyProperties(
			randomPatchProductOption, expectedProductOption);

		ProductOption getProductOption = productOptionResource.getProductOption(
			postProductOption.getId());

		assertEquals(expectedProductOption, getProductOption);
		assertValid(getProductOption);
		Assert.assertEquals(
			optionId, GetterUtil.getLong(getProductOption.getOptionId()));
		Assert.assertEquals(
			randomPatchProductOption.getOptionExternalReferenceCode(),
			getProductOption.getOptionExternalReferenceCode());
	}

	private void _testPostProductByExternalReferenceCodeProductOptionsPageWithOptionExternalReferenceCode()
		throws Exception {

		ProductOption randomProductOption = randomProductOption();

		long optionId = randomProductOption.getOptionId();

		randomProductOption.setOptionId(0L);

		Page<ProductOption> productOptionPage =
			productOptionResource.
				postProductByExternalReferenceCodeProductOptionsPage(
					_cProduct.getExternalReferenceCode(),
					new ProductOption[] {randomProductOption});

		randomProductOption.setOptionId(optionId);

		ProductOption postProductOption = _getLastProductOption(
			productOptionPage.getItems());

		assertEquals(randomProductOption, postProductOption);
		assertValid(postProductOption);
		Assert.assertEquals(
			optionId, GetterUtil.getLong(postProductOption.getOptionId()));
		Assert.assertEquals(
			randomProductOption.getOptionExternalReferenceCode(),
			postProductOption.getOptionExternalReferenceCode());
	}

	private void _testPostProductIdProductOptionsPageWithOptionExternalReferenceCode()
		throws Exception {

		ProductOption randomProductOption = randomProductOption();

		long optionId = randomProductOption.getOptionId();

		randomProductOption.setOptionId(0L);

		Page<ProductOption> productOptionPage =
			productOptionResource.postProductIdProductOptionsPage(
				_cProduct.getCProductId(),
				new ProductOption[] {randomProductOption});

		randomProductOption.setOptionId(optionId);

		ProductOption postProductOption = _getLastProductOption(
			productOptionPage.getItems());

		assertEquals(randomProductOption, postProductOption);
		assertValid(postProductOption);
		Assert.assertEquals(
			optionId, GetterUtil.getLong(postProductOption.getOptionId()));
		Assert.assertEquals(
			randomProductOption.getOptionExternalReferenceCode(),
			postProductOption.getOptionExternalReferenceCode());
	}

	private CPDefinition _cpDefinition;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@DeleteAfterTestRun
	private List<CPDefinition> _cpDefinitions = new ArrayList<>();

	@Inject
	private CPDefinitionService _cpDefinitionService;

	private CPInstance _cpInstance;
	private CProduct _cProduct;
	private final Map<Long, ProductOption> _productOptions = new HashMap<>();

	@Inject
	private ServiceContextHelper _serviceContextHelper;

}