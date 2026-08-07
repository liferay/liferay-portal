/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.product.type.virtual.test.util.VirtualCPTypeTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductVirtualSettings;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class ProductVirtualSettingsResourceTest
	extends BaseProductVirtualSettingsResourceTestCase {

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
			_commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME);

		VirtualCPTypeTestUtil.addCPDefinitionVirtualSetting(
			_commerceCatalog.getGroupId(), CPDefinition.class.getName(),
			_cpDefinition.getCPDefinitionId(), 0,
			CommerceOrderConstants.ORDER_STATUS_COMPLETED, 1, 0, 0);

		_cProduct = _cpDefinition.getCProduct();
	}

	@Override
	protected ProductVirtualSettings
			testGetProductByExternalReferenceCodeProductVirtualSettings_addProductVirtualSettings()
		throws Exception {

		return productVirtualSettingsResource.
			getProductByExternalReferenceCodeProductVirtualSettings(
				_cProduct.getExternalReferenceCode());
	}

	@Override
	protected String
			testGetProductByExternalReferenceCodeProductVirtualSettings_getExternalReferenceCode()
		throws Exception {

		return _cProduct.getExternalReferenceCode();
	}

	@Override
	protected ProductVirtualSettings
			testGetProductIdProductVirtualSettings_addProductVirtualSettings()
		throws Exception {

		return productVirtualSettingsResource.
			getProductIdProductVirtualSettings(_cProduct.getCProductId());
	}

	@Override
	protected Long testGetProductIdProductVirtualSettings_getId(
			ProductVirtualSettings productVirtualSettings)
		throws Exception {

		return _cProduct.getCProductId();
	}

	@Override
	protected String
			testGraphQLGetProductByExternalReferenceCodeProductVirtualSettings_getExternalReferenceCode()
		throws Exception {

		return _cProduct.getExternalReferenceCode();
	}

	@Override
	protected Long testGraphQLGetProductIdProductVirtualSettings_getId(
			ProductVirtualSettings productVirtualSettings)
		throws Exception {

		return _cProduct.getCProductId();
	}

	@Override
	protected ProductVirtualSettings
			testGraphQLProductProductVirtualSettings_addProductVirtualSettings()
		throws Exception {

		return productVirtualSettingsResource.
			getProductIdProductVirtualSettings(_cProduct.getCProductId());
	}

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private CPDefinition _cpDefinition;

	@DeleteAfterTestRun
	private CProduct _cProduct;

	@DeleteAfterTestRun
	private User _user;

}