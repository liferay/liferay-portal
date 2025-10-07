/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPConfigurationListRelLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfigurationListAccount;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.petra.string.StringPool;
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
public class ProductConfigurationListAccountResourceTest
	extends BaseProductConfigurationListAccountResourceTestCase {

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
				dateConfig.getMinute(), 0, 0, 0, 0, 0, true,
				new ServiceContext());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		for (Long accountEntryId : _accountEntryIds) {
			_accountEntryLocalService.deleteAccountEntry(accountEntryId);
		}

		for (Long productConfigurationListAccountId :
				_productConfigurationListAccountIds) {

			_cpConfigurationListRelLocalService.deleteCPConfigurationListRel(
				productConfigurationListAccountId);
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
	public void testDeleteProductConfigurationListAccount() throws Exception {
		long cpConfigurationListId =
			_cpConfigurationList.getCPConfigurationListId();

		ProductConfigurationListAccount productConfigurationListAccount =
			productConfigurationListAccountResource.
				postProductConfigurationListIdProductConfigurationListAccount(
					cpConfigurationListId,
					randomProductConfigurationListAccount());

		productConfigurationListAccountResource.
			deleteProductConfigurationListAccount(
				productConfigurationListAccount.
					getProductConfigurationListAccountId());

		Page<ProductConfigurationListAccount> page =
			productConfigurationListAccountResource.
				getProductConfigurationListIdProductConfigurationListAccountsPage(
					cpConfigurationListId, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(0, page.getTotalCount());
	}

	@Ignore
	@Test
	public void testGraphQLDeleteProductConfigurationListAccount()
		throws Exception {

		super.testGraphQLDeleteProductConfigurationListAccount();
	}

	@Override
	protected ProductConfigurationListAccount
			randomProductConfigurationListAccount()
		throws Exception {

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, _serviceContext);

		_accountEntryIds.add(accountEntry.getAccountEntryId());

		return new ProductConfigurationListAccount() {
			{
				accountExternalReferenceCode =
					accountEntry.getExternalReferenceCode();
				accountId = accountEntry.getAccountEntryId();
				productConfigurationListAccountId = RandomTestUtil.randomLong();
				productConfigurationListExternalReferenceCode =
					_cpConfigurationList.getExternalReferenceCode();
				productConfigurationListId =
					_cpConfigurationList.getCPConfigurationListId();
			}
		};
	}

	@Override
	protected ProductConfigurationListAccount
			testDeleteProductConfigurationListAccountBatch_addProductConfigurationListAccount()
		throws Exception {

		return productConfigurationListAccountResource.
			postProductConfigurationListIdProductConfigurationListAccount(
				_cpConfigurationList.getCPConfigurationListId(),
				randomProductConfigurationListAccount());
	}

	@Override
	protected ProductConfigurationListAccount
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				String externalReferenceCode,
				ProductConfigurationListAccount productConfigurationListAccount)
		throws Exception {

		return productConfigurationListAccountResource.
			postProductConfigurationListByExternalReferenceCodeProductConfigurationListAccount(
				externalReferenceCode, productConfigurationListAccount);
	}

	@Override
	protected String
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_getExternalReferenceCode()
		throws Exception {

		return _cpConfigurationList.getExternalReferenceCode();
	}

	@Override
	protected ProductConfigurationListAccount
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				Long id,
				ProductConfigurationListAccount productConfigurationListAccount)
		throws Exception {

		return productConfigurationListAccountResource.
			postProductConfigurationListIdProductConfigurationListAccount(
				id, productConfigurationListAccount);
	}

	@Override
	protected Long
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_getId()
		throws Exception {

		return _cpConfigurationList.getCPConfigurationListId();
	}

	@Override
	protected ProductConfigurationListAccount
			testPostProductConfigurationListByExternalReferenceCodeProductConfigurationListAccount_addProductConfigurationListAccount(
				ProductConfigurationListAccount productConfigurationListAccount)
		throws Exception {

		ProductConfigurationListAccount postProductConfigurationListAccount =
			productConfigurationListAccountResource.
				postProductConfigurationListIdProductConfigurationListAccount(
					productConfigurationListAccount.
						getProductConfigurationListId(),
					productConfigurationListAccount);

		_productConfigurationListAccountIds.add(
			postProductConfigurationListAccount.
				getProductConfigurationListAccountId());

		return postProductConfigurationListAccount;
	}

	@Override
	protected ProductConfigurationListAccount
			testPostProductConfigurationListIdProductConfigurationListAccount_addProductConfigurationListAccount(
				ProductConfigurationListAccount productConfigurationListAccount)
		throws Exception {

		ProductConfigurationListAccount postProductConfigurationListAccount =
			productConfigurationListAccountResource.
				postProductConfigurationListIdProductConfigurationListAccount(
					_cpConfigurationList.getCPConfigurationListId(),
					productConfigurationListAccount);

		_productConfigurationListAccountIds.add(
			postProductConfigurationListAccount.
				getProductConfigurationListAccountId());

		return postProductConfigurationListAccount;
	}

	private final List<Long> _accountEntryIds = new ArrayList<>();

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

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

	private final List<Long> _productConfigurationListAccountIds =
		new ArrayList<>();
	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}