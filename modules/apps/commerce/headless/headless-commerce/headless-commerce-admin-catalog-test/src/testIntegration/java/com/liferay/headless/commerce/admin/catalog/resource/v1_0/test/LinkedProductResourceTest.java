/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.LinkedProduct;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.MappedProduct;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.MappedProductResource;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class LinkedProductResourceTest
	extends BaseLinkedProductResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testCompany.getCompanyId());
		_user = UserTestUtil.addUser(testCompany);

		_commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			testCompany.getCompanyId(), testCompany.getGroupId(),
			_user.getUserId(), _commerceCurrency.getCode());

		User adminUser = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		_mappedProductResource = MappedProductResource.builder(
		).authentication(
			adminUser.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@Override
	@Test
	public void testGetProductByExternalReferenceCodeLinkedProductsPage()
		throws Exception {

		super.testGetProductByExternalReferenceCodeLinkedProductsPage();

		_testGetProductByExternalReferenceCodeLinkedProductsPageMatchesProductId();
	}

	@Override
	protected LinkedProduct
			testGetProductByExternalReferenceCodeLinkedProductsPage_addLinkedProduct(
				String externalReferenceCode, LinkedProduct linkedProduct)
		throws Exception {

		return _addLinkedProduct(externalReferenceCode);
	}

	@Override
	protected String
			testGetProductByExternalReferenceCodeLinkedProductsPage_getExternalReferenceCode()
		throws Exception {

		CProduct cProduct = _addCProduct();

		return cProduct.getExternalReferenceCode();
	}

	@Override
	protected LinkedProduct testGetProductIdLinkedProductsPage_addLinkedProduct(
			Long id, LinkedProduct linkedProduct)
		throws Exception {

		CProduct cProduct = _cProductLocalService.getCProduct(id);

		return _addLinkedProduct(cProduct.getExternalReferenceCode());
	}

	@Override
	protected Long testGetProductIdLinkedProductsPage_getId() throws Exception {
		CProduct cProduct = _addCProduct();

		return cProduct.getCProductId();
	}

	private CProduct _addCProduct() throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		return cpDefinition.getCProduct();
	}

	private LinkedProduct _addLinkedProduct(String externalReferenceCode)
		throws Exception {

		CProduct cProduct = _addCProduct();

		_mappedProductResource.postProductByExternalReferenceCodeMappedProduct(
			cProduct.getExternalReferenceCode(),
			new MappedProduct() {
				{
					productExternalReferenceCode = externalReferenceCode;
					quantity = RandomTestUtil.randomInt();
					sequence = RandomTestUtil.randomString();
				}
			});

		return new LinkedProduct() {
			{
				productExternalReferenceCode =
					cProduct.getExternalReferenceCode();
				productId = cProduct.getCProductId();
			}
		};
	}

	private void _testGetProductByExternalReferenceCodeLinkedProductsPageMatchesProductId()
		throws Exception {

		CProduct cProduct = _addCProduct();

		String externalReferenceCode = cProduct.getExternalReferenceCode();

		LinkedProduct linkedProduct = _addLinkedProduct(externalReferenceCode);

		Page<LinkedProduct> linkedProductsPage =
			linkedProductResource.
				getProductByExternalReferenceCodeLinkedProductsPage(
					externalReferenceCode, Pagination.of(1, 10));

		List<LinkedProduct> linkedProducts =
			(List<LinkedProduct>)linkedProductsPage.getItems();

		Assert.assertEquals(
			linkedProducts.toString(), 1, linkedProducts.size());

		LinkedProduct pageLinkedProduct = linkedProducts.get(0);

		Assert.assertEquals(
			linkedProduct.getProductExternalReferenceCode(),
			pageLinkedProduct.getProductExternalReferenceCode());
		Assert.assertEquals(
			linkedProduct.getProductId(), pageLinkedProduct.getProductId());
	}

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CProductLocalService _cProductLocalService;

	private MappedProductResource _mappedProductResource;

	@DeleteAfterTestRun
	private User _user;

}