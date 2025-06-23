/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPConfigurationListRelLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.commerce.service.CommerceOrderTypeLocalServiceUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfigurationListOrderType;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Danny Situ
 */
@RunWith(Arquillian.class)
public class ProductConfigurationListOrderTypeResourceTest
	extends BaseProductConfigurationListOrderTypeResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), "USD", "en_US", false,
			_serviceContext);

		DateConfig dateConfig = DateConfig.toDisplayDateConfig(
			RandomTestUtil.nextDate(), _user.getTimeZone());

		_cpConfigurationList =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 0D, dateConfig.getMonth(),
				dateConfig.getDay(), dateConfig.getYear(), dateConfig.getHour(),
				dateConfig.getMinute(), 0, 0, 0, 0, 0, true);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		for (Long commerceOrderTypeId : _commerceOrderTypeIds) {
			_commerceOrderTypeLocalService.deleteCommerceOrderType(
				commerceOrderTypeId);
		}

		for (Long productConfigurationListOrderTypeId :
				_productConfigurationListOrderTypeIds) {

			_cpConfigurationListRelLocalService.deleteCPConfigurationListRel(
				productConfigurationListOrderTypeId);
		}
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	public void testDeleteProductConfigurationListOrderType() throws Exception {
		ProductConfigurationListOrderType productConfigurationListOrderType =
			productConfigurationListOrderTypeResource.
				postProductConfigurationListIdProductConfigurationListOrderType(
					_cpConfigurationList.getCPConfigurationListId(),
					randomProductConfigurationListOrderType());

		productConfigurationListOrderTypeResource.
			deleteProductConfigurationListOrderType(
				productConfigurationListOrderType.
					getProductConfigurationListOrderTypeId());

		Page<ProductConfigurationListOrderType> page =
			productConfigurationListOrderTypeResource.
				getProductConfigurationListIdProductConfigurationListOrderTypesPage(
					_cpConfigurationList.getCPConfigurationListId(), null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(0, page.getTotalCount());
	}

	@Ignore
	@Test
	public void testGraphQLDeleteProductConfigurationListOrderType()
		throws Exception {

		super.testGraphQLDeleteProductConfigurationListOrderType();
	}

	@Override
	protected ProductConfigurationListOrderType
			randomProductConfigurationListOrderType()
		throws Exception {

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			RandomTestUtil.nextDate(), _user.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			RandomTestUtil.nextDate(), _user.getTimeZone());

		CommerceOrderType commerceOrderType =
			CommerceOrderTypeLocalServiceUtil.addCommerceOrderType(
				RandomTestUtil.randomString(), _user.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean(), displayDateConfig.getMonth(),
				displayDateConfig.getDay(), displayDateConfig.getYear(),
				displayDateConfig.getHour(), displayDateConfig.getMinute(), 0,
				expirationDateConfig.getMonth(), expirationDateConfig.getDay(),
				expirationDateConfig.getYear(), expirationDateConfig.getHour(),
				expirationDateConfig.getMinute(), true, _serviceContext);

		_commerceOrderTypeIds.add(commerceOrderType.getCommerceOrderTypeId());

		return new ProductConfigurationListOrderType() {
			{
				orderTypeExternalReferenceCode =
					commerceOrderType.getExternalReferenceCode();
				orderTypeId = commerceOrderType.getCommerceOrderTypeId();
				priority = RandomTestUtil.randomInt();
				productConfigurationListExternalReferenceCode =
					_cpConfigurationList.getExternalReferenceCode();
				productConfigurationListId =
					_cpConfigurationList.getCPConfigurationListId();
				productConfigurationListOrderTypeId =
					RandomTestUtil.randomLong();
			}
		};
	}

	@Override
	protected ProductConfigurationListOrderType
			testDeleteProductConfigurationListOrderTypeBatch_addProductConfigurationListOrderType()
		throws Exception {

		return productConfigurationListOrderTypeResource.
			postProductConfigurationListIdProductConfigurationListOrderType(
				_cpConfigurationList.getCPConfigurationListId(),
				randomProductConfigurationListOrderType());
	}

	@Override
	protected ProductConfigurationListOrderType
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				String externalReferenceCode,
				ProductConfigurationListOrderType
					productConfigurationListOrderType)
		throws Exception {

		return productConfigurationListOrderTypeResource.
			postProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderType(
				externalReferenceCode, productConfigurationListOrderType);
	}

	@Override
	protected String
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_getExternalReferenceCode()
		throws Exception {

		return _cpConfigurationList.getExternalReferenceCode();
	}

	@Override
	protected ProductConfigurationListOrderType
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				Long id,
				ProductConfigurationListOrderType
					productConfigurationListOrderType)
		throws Exception {

		return productConfigurationListOrderTypeResource.
			postProductConfigurationListIdProductConfigurationListOrderType(
				id, productConfigurationListOrderType);
	}

	@Override
	protected Long
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_getId()
		throws Exception {

		return _cpConfigurationList.getCPConfigurationListId();
	}

	@Override
	protected ProductConfigurationListOrderType
			testPostProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderType_addProductConfigurationListOrderType(
				ProductConfigurationListOrderType
					productConfigurationListOrderType)
		throws Exception {

		ProductConfigurationListOrderType
			postProductConfigurationListOrderType =
				productConfigurationListOrderTypeResource.
					postProductConfigurationListIdProductConfigurationListOrderType(
						productConfigurationListOrderType.
							getProductConfigurationListId(),
						productConfigurationListOrderType);

		_productConfigurationListOrderTypeIds.add(
			postProductConfigurationListOrderType.
				getProductConfigurationListOrderTypeId());

		return postProductConfigurationListOrderType;
	}

	@Override
	protected ProductConfigurationListOrderType
			testPostProductConfigurationListIdProductConfigurationListOrderType_addProductConfigurationListOrderType(
				ProductConfigurationListOrderType
					productConfigurationListOrderType)
		throws Exception {

		ProductConfigurationListOrderType
			postProductConfigurationListOrderType =
				productConfigurationListOrderTypeResource.
					postProductConfigurationListIdProductConfigurationListOrderType(
						_cpConfigurationList.getCPConfigurationListId(),
						productConfigurationListOrderType);

		_productConfigurationListOrderTypeIds.add(
			postProductConfigurationListOrderType.
				getProductConfigurationListOrderTypeId());

		return postProductConfigurationListOrderType;
	}

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private final List<Long> _commerceOrderTypeIds = new ArrayList<>();

	@Inject
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Inject
	private CPConfigurationListRelLocalService
		_cpConfigurationListRelLocalService;

	private final List<Long> _productConfigurationListOrderTypeIds =
		new ArrayList<>();
	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}