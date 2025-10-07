/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfigurationList;
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
 */
@RunWith(Arquillian.class)
public class ProductConfigurationListResourceTest
	extends BaseProductConfigurationListResourceTestCase {

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser();

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), "USD", "en_US", false,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), _user.getUserId()));

		_masterCPConfigurationList =
			_cpConfigurationListLocalService.getMasterCPConfigurationList(
				_commerceCatalog.getGroupId());
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name"};
	}

	@Override
	protected ProductConfigurationList randomProductConfigurationList() {
		return new ProductConfigurationList() {
			{
				setCatalogExternalReferenceCode(
					_commerceCatalog.getExternalReferenceCode());
				setCatalogId(_commerceCatalog.getCommerceCatalogId());
				setExternalReferenceCode(RandomTestUtil.randomString());
				setMaster(false);
				setName(RandomTestUtil.randomString());
				setParentProductConfigurationListId(
					_masterCPConfigurationList.getCPConfigurationListId());
				setPriority(RandomTestUtil.randomDouble());
			}
		};
	}

	@Override
	protected ProductConfigurationList
			testDeleteProductConfigurationList_addProductConfigurationList()
		throws Exception {

		return productConfigurationListResource.postProductConfigurationList(
			randomProductConfigurationList());
	}

	@Override
	protected ProductConfigurationList
			testDeleteProductConfigurationListByExternalReferenceCode_addProductConfigurationList()
		throws Exception {

		return productConfigurationListResource.postProductConfigurationList(
			randomProductConfigurationList());
	}

	@Override
	protected ProductConfigurationList
			testGetProductConfigurationList_addProductConfigurationList()
		throws Exception {

		return productConfigurationListResource.postProductConfigurationList(
			randomProductConfigurationList());
	}

	@Override
	protected ProductConfigurationList
			testGetProductConfigurationListByExternalReferenceCode_addProductConfigurationList()
		throws Exception {

		return productConfigurationListResource.postProductConfigurationList(
			randomProductConfigurationList());
	}

	@Override
	protected ProductConfigurationList
			testGetProductConfigurationListsPage_addProductConfigurationList(
				ProductConfigurationList productConfigurationList)
		throws Exception {

		return productConfigurationListResource.postProductConfigurationList(
			productConfigurationList);
	}

	@Override
	protected ProductConfigurationList
			testGraphQLDeleteProductConfigurationList_addProductConfigurationList()
		throws Exception {

		return productConfigurationListResource.postProductConfigurationList(
			randomProductConfigurationList());
	}

	@Override
	protected ProductConfigurationList
			testGraphQLGetProductConfigurationListByExternalReferenceCode_addProductConfigurationList()
		throws Exception {

		return productConfigurationListResource.postProductConfigurationList(
			randomProductConfigurationList());
	}

	@Override
	protected ProductConfigurationList
			testGraphQLProductConfigurationList_addProductConfigurationList()
		throws Exception {

		return productConfigurationListResource.postProductConfigurationList(
			randomProductConfigurationList());
	}

	@Override
	protected ProductConfigurationList
			testPatchProductConfigurationList_addProductConfigurationList()
		throws Exception {

		return productConfigurationListResource.postProductConfigurationList(
			randomProductConfigurationList());
	}

	@Override
	protected ProductConfigurationList
			testPatchProductConfigurationListByExternalReferenceCode_addProductConfigurationList()
		throws Exception {

		return productConfigurationListResource.postProductConfigurationList(
			randomProductConfigurationList());
	}

	@Override
	protected ProductConfigurationList
			testPostProductConfigurationList_addProductConfigurationList(
				ProductConfigurationList productConfigurationList)
		throws Exception {

		return productConfigurationListResource.postProductConfigurationList(
			productConfigurationList);
	}

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@DeleteAfterTestRun
	private CPConfigurationList _masterCPConfigurationList;

	private User _user;

}