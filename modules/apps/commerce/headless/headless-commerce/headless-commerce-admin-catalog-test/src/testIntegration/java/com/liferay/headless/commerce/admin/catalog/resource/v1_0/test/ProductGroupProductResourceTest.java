/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.service.CommercePricingClassLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductGroupProduct;
import com.liferay.headless.commerce.admin.catalog.client.problem.Problem;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 */
@RunWith(Arquillian.class)
public class ProductGroupProductResourceTest
	extends BaseProductGroupProductResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(testCompany.getGroupId());

		_commerceCatalog = CommerceCatalogLocalServiceUtil.addCommerceCatalog(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(), serviceContext);
		_commercePricingClass =
			_commercePricingClassLocalService.addCommercePricingClass(
				serviceContext.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), serviceContext);
	}

	@Override
	@Test
	public void testPostProductGroupIdProductGroupProduct() throws Exception {
		super.testPostProductGroupIdProductGroupProduct();

		_testPostProductGroupIdProductGroupProductWithLazyReferencingDisabled();
		_testPostProductGroupIdProductGroupProductWithLazyReferencingEnabled();
	}

	@Override
	protected ProductGroupProduct randomProductGroupProduct() throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		return new ProductGroupProduct() {
			{
				productId = cpDefinition.getCProductId();
			}
		};
	}

	@Override
	protected ProductGroupProduct
			testDeleteProductGroupProduct_addProductGroupProduct()
		throws Exception {

		return _addProductGroupProduct(randomProductGroupProduct());
	}

	@Override
	protected ProductGroupProduct
			testGetProductGroupByExternalReferenceCodeProductGroupProductsPage_addProductGroupProduct(
				String externalReferenceCode,
				ProductGroupProduct productGroupProduct)
		throws Exception {

		return productGroupProductResource.
			postProductGroupByExternalReferenceCodeProductGroupProduct(
				externalReferenceCode, productGroupProduct);
	}

	@Override
	protected String
			testGetProductGroupByExternalReferenceCodeProductGroupProductsPage_getExternalReferenceCode()
		throws Exception {

		return _commercePricingClass.getExternalReferenceCode();
	}

	@Override
	protected ProductGroupProduct
			testGetProductGroupIdProductGroupProductsPage_addProductGroupProduct(
				Long id, ProductGroupProduct productGroupProduct)
		throws Exception {

		return productGroupProductResource.
			postProductGroupIdProductGroupProduct(id, productGroupProduct);
	}

	@Override
	protected Long testGetProductGroupIdProductGroupProductsPage_getId()
		throws Exception {

		return _commercePricingClass.getCommercePricingClassId();
	}

	@Override
	protected ProductGroupProduct
			testGraphQLProductGroupProduct_addProductGroupProduct()
		throws Exception {

		return _addProductGroupProduct(randomProductGroupProduct());
	}

	@Override
	protected ProductGroupProduct
			testPostProductGroupByExternalReferenceCodeProductGroupProduct_addProductGroupProduct(
				ProductGroupProduct productGroupProduct)
		throws Exception {

		return productGroupProductResource.
			postProductGroupByExternalReferenceCodeProductGroupProduct(
				_commercePricingClass.getExternalReferenceCode(),
				productGroupProduct);
	}

	@Override
	protected ProductGroupProduct
			testPostProductGroupIdProductGroupProduct_addProductGroupProduct(
				ProductGroupProduct productGroupProduct)
		throws Exception {

		return _addProductGroupProduct(productGroupProduct);
	}

	private ProductGroupProduct _addProductGroupProduct(
			ProductGroupProduct productGroupProduct)
		throws Exception {

		return productGroupProductResource.
			postProductGroupIdProductGroupProduct(
				_commercePricingClass.getCommercePricingClassId(),
				productGroupProduct);
	}

	private ProductGroupProduct _randomProductGroupProductWithEmptyProduct() {
		return new ProductGroupProduct() {
			{
				catalogExternalReferenceCode =
					_commerceCatalog.getExternalReferenceCode();
				productExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				productId = 0L;
				productType = SimpleCPTypeConstants.NAME;
			}
		};
	}

	private void _testPostProductGroupIdProductGroupProductWithLazyReferencingDisabled()
		throws Exception {

		try {
			_addProductGroupProduct(
				_randomProductGroupProductWithEmptyProduct());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testPostProductGroupIdProductGroupProductWithLazyReferencingEnabled()
		throws Exception {

		ProductGroupProduct postProductGroupProduct = null;

		ProductGroupProduct productGroupProduct =
			_randomProductGroupProductWithEmptyProduct();

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			postProductGroupProduct = _addProductGroupProduct(
				productGroupProduct);
		}

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.
				getCPDefinitionByCProductExternalReferenceCode(
					productGroupProduct.getProductExternalReferenceCode(),
					testCompany.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, cpDefinition.getStatus());
		Assert.assertEquals(
			_commerceCatalog.getGroupId(), cpDefinition.getGroupId());
		Assert.assertEquals(
			(Long)cpDefinition.getCProductId(),
			postProductGroupProduct.getProductId());

		Assert.assertEquals(
			SimpleCPTypeConstants.NAME,
			postProductGroupProduct.getProductType());
	}

	private CommerceCatalog _commerceCatalog;
	private CommercePricingClass _commercePricingClass;

	@Inject
	private CommercePricingClassLocalService _commercePricingClassLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

}