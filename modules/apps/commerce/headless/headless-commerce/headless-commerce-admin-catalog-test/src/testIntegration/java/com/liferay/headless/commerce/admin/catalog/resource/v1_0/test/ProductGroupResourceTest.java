/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductGroup;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductGroupProduct;
import com.liferay.headless.commerce.admin.catalog.client.problem.Problem;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 */
@RunWith(Arquillian.class)
public class ProductGroupResourceTest extends BaseProductGroupResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_commerceCatalog = CommerceCatalogLocalServiceUtil.addCommerceCatalog(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(),
			ServiceContextTestUtil.getServiceContext(testCompany.getGroupId()));
	}

	@Ignore
	@Override
	@Test
	public void testGetProductGroupsPageWithFilterStringContains()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetProductGroupsPageWithFilterStringEquals()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetProductGroupsPageWithFilterStringStartsWith()
		throws Exception {
	}

	@Override
	@Test
	public void testPatchProductGroup() throws Exception {
		ProductGroup postProductGroup = productGroupResource.postProductGroup(
			randomProductGroup());

		ProductGroup randomPatchProductGroup = randomPatchProductGroup();

		productGroupResource.patchProductGroup(
			postProductGroup.getId(), randomPatchProductGroup);

		ProductGroup expectedPatchProductGroup = postProductGroup.clone();

		BaseProductGroupResourceTestCase.BeanTestUtil.copyProperties(
			randomPatchProductGroup, expectedPatchProductGroup);

		ProductGroup getProductGroup = productGroupResource.getProductGroup(
			postProductGroup.getId());

		assertEquals(expectedPatchProductGroup, getProductGroup);

		assertValid(getProductGroup);
	}

	@Override
	@Test
	public void testPatchProductGroupByExternalReferenceCode()
		throws Exception {

		ProductGroup postProductGroup = productGroupResource.postProductGroup(
			randomProductGroup());

		ProductGroup randomPatchProductGroup = randomPatchProductGroup();

		productGroupResource.patchProductGroupByExternalReferenceCode(
			postProductGroup.getExternalReferenceCode(),
			randomPatchProductGroup);

		ProductGroup expectedPatchProductGroup = postProductGroup.clone();

		BaseProductGroupResourceTestCase.BeanTestUtil.copyProperties(
			randomPatchProductGroup, expectedPatchProductGroup);

		ProductGroup getProductGroup =
			productGroupResource.getProductGroupByExternalReferenceCode(
				postProductGroup.getExternalReferenceCode());

		assertEquals(expectedPatchProductGroup, getProductGroup);

		assertValid(getProductGroup);
	}

	@Override
	@Test
	public void testPostProductGroup() throws Exception {
		super.testPostProductGroup();

		_testPostProductGroupWithLazyReferencingDisabled();
		_testPostProductGroupWithLazyReferencingEnabled();
	}

	@Override
	protected ProductGroup randomProductGroup() throws Exception {
		return new ProductGroup() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				title = LanguageUtils.getLanguageIdMap(
					HashMapBuilder.put(
						LocaleUtil.getDefault(), RandomTestUtil.randomString()
					).build());
			}
		};
	}

	@Override
	protected ProductGroup testDeleteProductGroup_addProductGroup()
		throws Exception {

		return productGroupResource.postProductGroup(randomProductGroup());
	}

	@Override
	protected ProductGroup
			testDeleteProductGroupByExternalReferenceCode_addProductGroup()
		throws Exception {

		return productGroupResource.postProductGroup(randomProductGroup());
	}

	@Override
	protected ProductGroup testGetProductGroup_addProductGroup()
		throws Exception {

		return productGroupResource.postProductGroup(randomProductGroup());
	}

	@Override
	protected ProductGroup
			testGetProductGroupByExternalReferenceCode_addProductGroup()
		throws Exception {

		return productGroupResource.postProductGroup(randomProductGroup());
	}

	@Override
	protected ProductGroup testGetProductGroupsPage_addProductGroup(
			ProductGroup productGroup)
		throws Exception {

		return productGroupResource.postProductGroup(productGroup);
	}

	@Override
	protected ProductGroup testPostProductGroup_addProductGroup(
			ProductGroup productGroup)
		throws Exception {

		return productGroupResource.postProductGroup(productGroup);
	}

	@Override
	protected ProductGroup
			testPutProductGroupByExternalReferenceCode_addProductGroup()
		throws Exception {

		return productGroupResource.postProductGroup(randomProductGroup());
	}

	private ProductGroup _randomProductGroupWithEmptyProduct() {
		return new ProductGroup() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				products = new ProductGroupProduct[] {
					new ProductGroupProduct() {
						{
							catalogExternalReferenceCode =
								_commerceCatalog.getExternalReferenceCode();
							productExternalReferenceCode =
								StringUtil.toLowerCase(
									RandomTestUtil.randomString());
							productId = 0L;
							productType = SimpleCPTypeConstants.NAME;
						}
					}
				};
				title = LanguageUtils.getLanguageIdMap(
					HashMapBuilder.put(
						LocaleUtil.getDefault(), RandomTestUtil.randomString()
					).build());
			}
		};
	}

	private void _testPostProductGroupWithLazyReferencingDisabled()
		throws Exception {

		try {
			productGroupResource.postProductGroup(
				_randomProductGroupWithEmptyProduct());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testPostProductGroupWithLazyReferencingEnabled()
		throws Exception {

		ProductGroup postProductGroup = null;

		ProductGroup productGroup = _randomProductGroupWithEmptyProduct();

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			postProductGroup = productGroupResource.postProductGroup(
				productGroup);
		}

		ProductGroupProduct[] productGroupProducts = productGroup.getProducts();

		ProductGroupProduct productGroupProduct = productGroupProducts[0];

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.
				getCPDefinitionByCProductExternalReferenceCode(
					productGroupProduct.getProductExternalReferenceCode(),
					testCompany.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, cpDefinition.getStatus());
		Assert.assertEquals(
			_commerceCatalog.getGroupId(), cpDefinition.getGroupId());

		Assert.assertEquals((Integer)1, postProductGroup.getProductsCount());
	}

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

}