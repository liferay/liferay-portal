/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.RelatedProduct;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.problem.Problem;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

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
public class RelatedProductResourceTest
	extends BaseRelatedProductResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_commerceCatalog = CommerceCatalogLocalServiceUtil.addCommerceCatalog(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(),
			ServiceContextTestUtil.getServiceContext(testCompany.getGroupId()));

		_cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);
		_cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteRelatedProduct() throws Exception {
		super.testGraphQLDeleteRelatedProduct();
	}

	@Override
	@Test
	public void testPostProductIdRelatedProduct() throws Exception {
		super.testPostProductIdRelatedProduct();

		_testPostProductIdRelatedProductWithLazyReferencingDisabled();
		_testPostProductIdRelatedProductWithLazyReferencingEnabled();
		_testPostRelatedProductsBatch();
	}

	@Override
	protected RelatedProduct randomRelatedProduct() throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		return new RelatedProduct() {
			{
				priority = 0.0;
				productId = cpDefinition.getCProductId();
				type = "related";
			}
		};
	}

	@Override
	protected RelatedProduct testDeleteRelatedProduct_addRelatedProduct()
		throws Exception {

		return relatedProductResource.postProductIdRelatedProduct(
			_cpDefinition1.getCProductId(),
			new RelatedProduct() {
				{
					priority = 0.0;
					productId = _cpDefinition2.getCProductId();
					type = "related";
				}
			});
	}

	@Override
	protected RelatedProduct
			testGetProductByExternalReferenceCodeRelatedProductsPage_addRelatedProduct(
				String externalReferenceCode, RelatedProduct relatedProduct)
		throws Exception {

		return relatedProductResource.
			postProductByExternalReferenceCodeRelatedProduct(
				externalReferenceCode, relatedProduct);
	}

	@Override
	protected String
			testGetProductByExternalReferenceCodeRelatedProductsPage_getExternalReferenceCode()
		throws Exception {

		CProduct cProduct = _cpDefinition1.getCProduct();

		return cProduct.getExternalReferenceCode();
	}

	@Override
	protected RelatedProduct
			testGetProductIdRelatedProductsPage_addRelatedProduct(
				Long id, RelatedProduct relatedProduct)
		throws Exception {

		return relatedProductResource.postProductIdRelatedProduct(
			id, relatedProduct);
	}

	@Override
	protected Long testGetProductIdRelatedProductsPage_getId()
		throws Exception {

		return _cpDefinition1.getCProductId();
	}

	@Override
	protected RelatedProduct testGetRelatedProduct_addRelatedProduct()
		throws Exception {

		return relatedProductResource.postProductIdRelatedProduct(
			_cpDefinition1.getCProductId(),
			new RelatedProduct() {
				{
					priority = 0.0;
					productId = _cpDefinition2.getCProductId();
					type = "related";
				}
			});
	}

	@Override
	protected RelatedProduct testGraphQLGetRelatedProduct_addRelatedProduct()
		throws Exception {

		return relatedProductResource.postProductIdRelatedProduct(
			_cpDefinition1.getCProductId(),
			new RelatedProduct() {
				{
					priority = 0.0;
					productId = _cpDefinition2.getCProductId();
					type = "related";
				}
			});
	}

	@Override
	protected RelatedProduct testGraphQLRelatedProduct_addRelatedProduct()
		throws Exception {

		return relatedProductResource.postProductIdRelatedProduct(
			_cpDefinition1.getCProductId(),
			new RelatedProduct() {
				{
					priority = 0.0;
					productId = _cpDefinition2.getCProductId();
					type = "related";
				}
			});
	}

	@Override
	protected RelatedProduct
			testPostProductByExternalReferenceCodeRelatedProduct_addRelatedProduct(
				RelatedProduct relatedProduct)
		throws Exception {

		return relatedProductResource.postProductIdRelatedProduct(
			_cpDefinition1.getCProductId(), relatedProduct);
	}

	@Override
	protected RelatedProduct testPostProductIdRelatedProduct_addRelatedProduct(
			RelatedProduct relatedProduct)
		throws Exception {

		return relatedProductResource.postProductIdRelatedProduct(
			_cpDefinition1.getCProductId(), relatedProduct);
	}

	private RelatedProduct _randomRelatedProductWithEmptyProduct() {
		return new RelatedProduct() {
			{
				priority = 0.0;
				productExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				productId = 0L;
				productType = SimpleCPTypeConstants.NAME;
				type = "related";
			}
		};
	}

	private void _testPostProductIdRelatedProductWithLazyReferencingDisabled()
		throws Exception {

		try {
			relatedProductResource.postProductIdRelatedProduct(
				_cpDefinition1.getCProductId(),
				_randomRelatedProductWithEmptyProduct());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testPostProductIdRelatedProductWithLazyReferencingEnabled()
		throws Exception {

		RelatedProduct postRelatedProduct = null;

		RelatedProduct relatedProduct = _randomRelatedProductWithEmptyProduct();

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			postRelatedProduct =
				relatedProductResource.postProductIdRelatedProduct(
					_cpDefinition1.getCProductId(), relatedProduct);
		}

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.
				getCPDefinitionByCProductExternalReferenceCode(
					relatedProduct.getProductExternalReferenceCode(),
					testCompany.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, cpDefinition.getStatus());
		Assert.assertEquals(
			_commerceCatalog.getGroupId(), cpDefinition.getGroupId());
		Assert.assertEquals(
			(Long)cpDefinition.getCProductId(),
			postRelatedProduct.getProductId());

		Assert.assertEquals(
			SimpleCPTypeConstants.NAME, postRelatedProduct.getProductType());
	}

	private void _testPostRelatedProductsBatch() throws Exception {
		CProduct cProduct = _cpDefinition1.getCProduct();

		RelatedProduct relatedProduct = randomRelatedProduct();

		_waitForFinish(
			"COMPLETED", true,
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"items",
					JSONUtil.put(
						_jsonFactory.createJSONObject(
							relatedProduct.toString()))
				).toString(),
				"headless-commerce-admin-catalog/v1.0/products" +
					"/relatedProducts/batch?batchExternalReferenceCode=" +
						cProduct.getExternalReferenceCode(),
				Http.Method.POST));

		Page<RelatedProduct> productByExternalReferenceCodeRelatedProductsPage =
			relatedProductResource.
				getProductByExternalReferenceCodeRelatedProductsPage(
					cProduct.getExternalReferenceCode(), "related",
					Pagination.of(1, 10));

		assertContains(
			relatedProduct,
			(List<RelatedProduct>)
				productByExternalReferenceCodeRelatedProductsPage.getItems());
	}

	private JSONObject _waitForFinish(
			String expectedExecuteStatus, boolean importTask,
			JSONObject jsonObject)
		throws Exception {

		String endpoint = StringBundler.concat(
			"headless-batch-engine/v1.0/",
			importTask ? "import-task" : "export-task",
			"/by-external-reference-code/");

		while (true) {
			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null, endpoint + jsonObject.getString("externalReferenceCode"),
				Http.Method.GET);

			String executeStatus = jsonObject.getString("executeStatus");

			if (StringUtil.equals(executeStatus, "COMPLETED") ||
				StringUtil.equals(executeStatus, "FAILED")) {

				Assert.assertEquals(expectedExecuteStatus, executeStatus);

				return jsonObject;
			}
		}
	}

	private CommerceCatalog _commerceCatalog;
	private CPDefinition _cpDefinition1;
	private CPDefinition _cpDefinition2;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private JSONFactory _jsonFactory;

}