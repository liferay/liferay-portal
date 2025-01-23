/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductSpecification;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class ProductSpecificationResourceTest
	extends BaseProductSpecificationResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		CommerceCatalog commerceCatalog = CPTestUtil.getSystemCommerceCatalog(
			testCompany.getCompanyId());

		_cpDefinition = CPTestUtil.addCPDefinition(
			commerceCatalog.getGroupId());

		_cpOptionCategory = CPTestUtil.addCPOptionCategory(
			testGroup.getGroupId());
		_cpSpecificationOption = CPTestUtil.addCPSpecificationOption(
			testGroup.getGroupId(), false);
	}

	@Override
	@Test
	public void testPatchProductSpecification() throws Exception {
		super.testPatchProductSpecification();

		ProductSpecification postProductSpecification =
			testPatchProductSpecification_addProductSpecification();

		postProductSpecification.setKey(RandomTestUtil.randomString());

		ProductSpecification patchProductSpecification =
			productSpecificationResource.patchProductSpecification(
				postProductSpecification.getId(), postProductSpecification);

		Assert.assertEquals(
			postProductSpecification.getKey(),
			patchProductSpecification.getKey());
	}

	@Override
	@Test
	public void testPostProductByExternalReferenceCodeProductSpecification()
		throws Exception {

		super.testPostProductByExternalReferenceCodeProductSpecification();

		ProductSpecification
			randomProductSpecificationWithExternalReferenceCode =
				randomProductSpecificationWithExternalReferenceCode();

		ProductSpecification postProductSpecification =
			testPostProductByExternalReferenceCodeProductSpecification_addProductSpecification(
				randomProductSpecificationWithExternalReferenceCode);

		randomProductSpecificationWithExternalReferenceCode.setSpecificationKey(
			_cpSpecificationOption.getKey());

		assertEquals(
			randomProductSpecificationWithExternalReferenceCode,
			postProductSpecification);
		assertValid(postProductSpecification);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"externalReferenceCode", "priority", "specificationKey", "value"
		};
	}

	@Override
	protected ProductSpecification randomProductSpecification() {
		return new ProductSpecification() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				optionCategoryExternalReferenceCode =
					_cpOptionCategory.getExternalReferenceCode();
				priority = RandomTestUtil.randomDouble();
				specificationKey = _cpSpecificationOption.getKey();
				value = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
			}
		};
	}

	protected ProductSpecification
		randomProductSpecificationWithExternalReferenceCode() {

		return new ProductSpecification() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				optionCategoryExternalReferenceCode =
					_cpOptionCategory.getExternalReferenceCode();
				priority = RandomTestUtil.randomDouble();
				specificationExternalReferenceCode =
					_cpSpecificationOption.getExternalReferenceCode();
				value = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
			}
		};
	}

	@Override
	protected ProductSpecification
			testDeleteProductSpecification_addProductSpecification()
		throws Exception {

		return productSpecificationResource.postProductIdProductSpecification(
			_cpDefinition.getCProductId(), randomProductSpecification());
	}

	@Override
	protected ProductSpecification
			testDeleteProductSpecificationByExternalReferenceCode_addProductSpecification()
		throws Exception {

		return productSpecificationResource.postProductIdProductSpecification(
			_cpDefinition.getCProductId(), randomProductSpecification());
	}

	@Override
	protected ProductSpecification
			testGetProductByExternalReferenceCodeProductSpecificationsPage_addProductSpecification(
				String externalReferenceCode,
				ProductSpecification productSpecification)
		throws Exception {

		return productSpecificationResource.
			postProductByExternalReferenceCodeProductSpecification(
				externalReferenceCode, productSpecification);
	}

	@Override
	protected String
			testGetProductByExternalReferenceCodeProductSpecificationsPage_getExternalReferenceCode()
		throws Exception {

		CProduct cProduct = _cpDefinition.getCProduct();

		return cProduct.getExternalReferenceCode();
	}

	@Override
	protected ProductSpecification
			testGetProductIdProductSpecificationsPage_addProductSpecification(
				Long id, ProductSpecification productSpecification)
		throws Exception {

		return productSpecificationResource.postProductIdProductSpecification(
			id, productSpecification);
	}

	@Override
	protected Long testGetProductIdProductSpecificationsPage_getId() {
		return _cpDefinition.getCProductId();
	}

	@Override
	protected ProductSpecification
			testGetProductSpecification_addProductSpecification()
		throws Exception {

		return productSpecificationResource.postProductIdProductSpecification(
			_cpDefinition.getCProductId(), randomProductSpecification());
	}

	@Override
	protected ProductSpecification
			testGetProductSpecificationByExternalReferenceCode_addProductSpecification()
		throws Exception {

		return productSpecificationResource.postProductIdProductSpecification(
			_cpDefinition.getCProductId(), randomProductSpecification());
	}

	@Override
	protected ProductSpecification
			testGraphQLDeleteProductSpecification_addProductSpecification()
		throws Exception {

		return productSpecificationResource.postProductIdProductSpecification(
			_cpDefinition.getCProductId(), randomProductSpecification());
	}

	@Override
	protected ProductSpecification
			testGraphQLGetProductSpecification_addProductSpecification()
		throws Exception {

		return productSpecificationResource.postProductIdProductSpecification(
			_cpDefinition.getCProductId(), randomProductSpecification());
	}

	@Override
	protected ProductSpecification
			testGraphQLProductSpecification_addProductSpecification()
		throws Exception {

		return productSpecificationResource.postProductIdProductSpecification(
			_cpDefinition.getCProductId(), randomProductSpecification());
	}

	@Override
	protected ProductSpecification
			testPatchProductSpecification_addProductSpecification()
		throws Exception {

		return productSpecificationResource.postProductIdProductSpecification(
			_cpDefinition.getCProductId(), randomProductSpecification());
	}

	@Override
	protected ProductSpecification
			testPatchProductSpecificationByExternalReferenceCode_addProductSpecification()
		throws Exception {

		return productSpecificationResource.postProductIdProductSpecification(
			_cpDefinition.getCProductId(), randomProductSpecification());
	}

	@Override
	protected ProductSpecification
			testPostProductByExternalReferenceCodeProductSpecification_addProductSpecification(
				ProductSpecification productSpecification)
		throws Exception {

		CProduct cProduct = _cpDefinition.getCProduct();

		return productSpecificationResource.
			postProductByExternalReferenceCodeProductSpecification(
				cProduct.getExternalReferenceCode(), productSpecification);
	}

	@Override
	protected ProductSpecification
			testPostProductIdProductSpecification_addProductSpecification(
				ProductSpecification productSpecification)
		throws Exception {

		return productSpecificationResource.postProductIdProductSpecification(
			_cpDefinition.getCProductId(), productSpecification);
	}

	@DeleteAfterTestRun
	private CPDefinition _cpDefinition;

	@DeleteAfterTestRun
	private CPOptionCategory _cpOptionCategory;

	@DeleteAfterTestRun
	private CPSpecificationOption _cpSpecificationOption;

}