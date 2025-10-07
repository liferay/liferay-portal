/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPConfigurationListRelLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfigurationListAccountGroup;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Danny Situ
 */
@RunWith(Arquillian.class)
public class ProductConfigurationListAccountGroupResourceTest
	extends BaseProductConfigurationListAccountGroupResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, _serviceContext);

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
				dateConfig.getMinute(), 0, 0, 0, 0, 0, true,
				new ServiceContext());
	}

	@Override
	@Test
	public void testDeleteProductConfigurationListAccountGroup()
		throws Exception {

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup =
				productConfigurationListAccountGroupResource.
					postProductConfigurationListIdProductConfigurationListAccountGroup(
						_cpConfigurationList.getCPConfigurationListId(),
						randomProductConfigurationListAccountGroup());

		productConfigurationListAccountGroupResource.
			deleteProductConfigurationListAccountGroup(
				productConfigurationListAccountGroup.
					getProductConfigurationListAccountGroupId());

		Page<ProductConfigurationListAccountGroup> page =
			productConfigurationListAccountGroupResource.
				getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
					_cpConfigurationList.getCPConfigurationListId(), null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(0, page.getTotalCount());
	}

	@Override
	@Test
	public void testGraphQLDeleteProductConfigurationListAccountGroup()
		throws Exception {

		// Namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductConfigurationListAccountGroup",
							HashMapBuilder.<String, Object>put(
								"productConfigurationListAccountGroupId",
								() -> {
									ProductConfigurationListAccountGroup
										productConfigurationListAccountGroup =
											_addProductConfigurationListAccountGroup(
												randomProductConfigurationListAccountGroup());

									return productConfigurationListAccountGroup.
										getProductConfigurationListAccountGroupId();
								}
							).build()))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductConfigurationListAccountGroup"));

		// No namespace

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductConfigurationListAccountGroup",
						HashMapBuilder.<String, Object>put(
							"productConfigurationListAccountGroupId",
							() -> {
								ProductConfigurationListAccountGroup
									productConfigurationListAccountGroup =
										_addProductConfigurationListAccountGroup(
											randomProductConfigurationListAccountGroup());

								return productConfigurationListAccountGroup.
									getProductConfigurationListAccountGroupId();
							}
						).build())),
				"JSONObject/data",
				"Object/deleteProductConfigurationListAccountGroup"));
	}

	@Override
	protected ProductConfigurationListAccountGroup
			randomProductConfigurationListAccountGroup()
		throws Exception {

		AccountGroup randomAccountGroup =
			_accountGroupLocalService.addAccountGroup(
				StringPool.BLANK, _user.getUserId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_serviceContext);

		_accountGroupIds.add(randomAccountGroup.getAccountGroupId());

		_accountGroupRelLocalService.addAccountGroupRel(
			randomAccountGroup.getAccountGroupId(),
			AccountEntry.class.getName(), _accountEntry.getAccountEntryId());

		return new ProductConfigurationListAccountGroup() {
			{
				accountGroupExternalReferenceCode =
					randomAccountGroup.getExternalReferenceCode();
				accountGroupId = randomAccountGroup.getAccountGroupId();
				productConfigurationListAccountGroupId =
					RandomTestUtil.randomLong();
				productConfigurationListExternalReferenceCode =
					_cpConfigurationList.getExternalReferenceCode();
				productConfigurationListId =
					_cpConfigurationList.getCPConfigurationListId();
			}
		};
	}

	@Override
	protected ProductConfigurationListAccountGroup
			testBatchEngineDeleteImportTask_addProductConfigurationListAccountGroup()
		throws Exception {

		return testDeleteProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup();
	}

	@Override
	protected ProductConfigurationListAccountGroup
			testDeleteProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup()
		throws Exception {

		return productConfigurationListAccountGroupResource.
			postProductConfigurationListIdProductConfigurationListAccountGroup(
				_cpConfigurationList.getCPConfigurationListId(),
				randomProductConfigurationListAccountGroup());
	}

	@Override
	protected ProductConfigurationListAccountGroup
			testDeleteProductConfigurationListAccountGroupBatch_addProductConfigurationListAccountGroup()
		throws Exception {

		return productConfigurationListAccountGroupResource.
			postProductConfigurationListIdProductConfigurationListAccountGroup(
				_cpConfigurationList.getCPConfigurationListId(),
				randomProductConfigurationListAccountGroup());
	}

	@Override
	protected ProductConfigurationListAccountGroup
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
				String externalReferenceCode,
				ProductConfigurationListAccountGroup
					productConfigurationListAccountGroup)
		throws Exception {

		ProductConfigurationListAccountGroup
			postProductConfigurationListAccountGroup =
				productConfigurationListAccountGroupResource.
					postProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroup(
						externalReferenceCode,
						productConfigurationListAccountGroup);

		_productConfigurationListAccountGroupIds.add(
			postProductConfigurationListAccountGroup.
				getProductConfigurationListAccountGroupId());

		return postProductConfigurationListAccountGroup;
	}

	@Override
	protected String
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_getExternalReferenceCode()
		throws Exception {

		return _cpConfigurationList.getExternalReferenceCode();
	}

	@Override
	protected ProductConfigurationListAccountGroup
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
				Long id,
				ProductConfigurationListAccountGroup
					productConfigurationListAccountGroup)
		throws Exception {

		ProductConfigurationListAccountGroup
			postProductConfigurationListAccountGroup =
				productConfigurationListAccountGroupResource.
					postProductConfigurationListIdProductConfigurationListAccountGroup(
						id, productConfigurationListAccountGroup);

		_productConfigurationListAccountGroupIds.add(
			postProductConfigurationListAccountGroup.
				getProductConfigurationListAccountGroupId());

		return postProductConfigurationListAccountGroup;
	}

	@Override
	protected Long
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_getId()
		throws Exception {

		return _cpConfigurationList.getCPConfigurationListId();
	}

	@Override
	protected ProductConfigurationListAccountGroup
			testPostProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup(
				ProductConfigurationListAccountGroup
					productConfigurationListAccountGroup)
		throws Exception {

		return _addProductConfigurationListAccountGroup(
			productConfigurationListAccountGroup);
	}

	@Override
	protected ProductConfigurationListAccountGroup
			testPostProductConfigurationListIdProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup(
				ProductConfigurationListAccountGroup
					productConfigurationListAccountGroup)
		throws Exception {

		return _addProductConfigurationListAccountGroup(
			productConfigurationListAccountGroup);
	}

	private ProductConfigurationListAccountGroup
			_addProductConfigurationListAccountGroup(
				ProductConfigurationListAccountGroup
					productConfigurationListAccountGroup)
		throws Exception {

		ProductConfigurationListAccountGroup
			postProductConfigurationListAccountGroup =
				productConfigurationListAccountGroupResource.
					postProductConfigurationListIdProductConfigurationListAccountGroup(
						productConfigurationListAccountGroup.
							getProductConfigurationListId(),
						productConfigurationListAccountGroup);

		_productConfigurationListAccountGroupIds.add(
			postProductConfigurationListAccountGroup.
				getProductConfigurationListAccountGroupId());

		return postProductConfigurationListAccountGroup;
	}

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private final List<Long> _accountGroupIds = new ArrayList<>();

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Inject
	private CPConfigurationListRelLocalService
		_cpConfigurationListRelLocalService;

	private final List<Long> _productConfigurationListAccountGroupIds =
		new ArrayList<>();
	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}