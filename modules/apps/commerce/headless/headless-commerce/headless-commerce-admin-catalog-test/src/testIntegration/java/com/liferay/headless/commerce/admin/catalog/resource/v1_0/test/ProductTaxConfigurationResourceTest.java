/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductTaxConfiguration;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class ProductTaxConfigurationResourceTest
	extends BaseProductTaxConfigurationResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser();

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			RandomTestUtil.randomString(), 0, RandomTestUtil.randomString(),
			"USD", "en_US", false,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), _user.getUserId()));

		_cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME);

		_cProduct = _cpDefinition.getCProduct();
	}

	@Override
	@Test
	public void testPatchProductByExternalReferenceCodeTaxConfiguration()
		throws Exception {

		CPTaxCategory cpTaxCategory =
			_cpTaxCategoryLocalService.addCPTaxCategory(
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), _user.getUserId()));

		productTaxConfigurationResource.
			patchProductByExternalReferenceCodeTaxConfiguration(
				_cProduct.getExternalReferenceCode(),
				new ProductTaxConfiguration() {
					{
						taxCategoryExternalReferenceCode =
							cpTaxCategory.getExternalReferenceCode();
					}
				});

		ProductTaxConfiguration productTaxConfiguration =
			productTaxConfigurationResource.
				getProductByExternalReferenceCodeTaxConfiguration(
					_cProduct.getExternalReferenceCode());

		Assert.assertEquals(
			(Long)cpTaxCategory.getCPTaxCategoryId(),
			productTaxConfiguration.getId());
		Assert.assertEquals(
			cpTaxCategory.getExternalReferenceCode(),
			productTaxConfiguration.getTaxCategoryExternalReferenceCode());
	}

	@Override
	@Test
	public void testPatchProductIdTaxConfiguration() throws Exception {
		CPTaxCategory cpTaxCategory =
			_cpTaxCategoryLocalService.addCPTaxCategory(
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), _user.getUserId()));

		productTaxConfigurationResource.patchProductIdTaxConfiguration(
			_cProduct.getCProductId(),
			new ProductTaxConfiguration() {
				{
					taxCategoryExternalReferenceCode =
						cpTaxCategory.getExternalReferenceCode();
				}
			});

		ProductTaxConfiguration productTaxConfiguration =
			productTaxConfigurationResource.getProductIdTaxConfiguration(
				_cProduct.getCProductId());

		Assert.assertEquals(
			(Long)cpTaxCategory.getCPTaxCategoryId(),
			productTaxConfiguration.getId());
		Assert.assertEquals(
			cpTaxCategory.getExternalReferenceCode(),
			productTaxConfiguration.getTaxCategoryExternalReferenceCode());
	}

	@Override
	protected ProductTaxConfiguration
			testGetProductByExternalReferenceCodeTaxConfiguration_addProductTaxConfiguration()
		throws Exception {

		return productTaxConfigurationResource.
			getProductByExternalReferenceCodeTaxConfiguration(
				_cProduct.getExternalReferenceCode());
	}

	@Override
	protected String
			testGetProductByExternalReferenceCodeTaxConfiguration_getExternalReferenceCode()
		throws Exception {

		return _cProduct.getExternalReferenceCode();
	}

	@Override
	protected ProductTaxConfiguration
			testGetProductIdTaxConfiguration_addProductTaxConfiguration()
		throws Exception {

		return productTaxConfigurationResource.getProductIdTaxConfiguration(
			_cProduct.getCProductId());
	}

	@Override
	protected Long testGetProductIdTaxConfiguration_getId(
			ProductTaxConfiguration productTaxConfiguration)
		throws Exception {

		return _cProduct.getCProductId();
	}

	@Override
	protected String
			testGraphQLGetProductByExternalReferenceCodeTaxConfiguration_getExternalReferenceCode()
		throws Exception {

		return _cProduct.getExternalReferenceCode();
	}

	@Override
	protected Long testGraphQLGetProductIdTaxConfiguration_getId(
			ProductTaxConfiguration productTaxConfiguration)
		throws Exception {

		return _cProduct.getCProductId();
	}

	@Override
	protected ProductTaxConfiguration
			testGraphQLProductProductTaxConfiguration_addProductTaxConfiguration()
		throws Exception {

		return productTaxConfigurationResource.getProductIdTaxConfiguration(
			_cProduct.getCProductId());
	}

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private CPDefinition _cpDefinition;

	@DeleteAfterTestRun
	private CProduct _cProduct;

	@Inject
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

	@DeleteAfterTestRun
	private User _user;

}