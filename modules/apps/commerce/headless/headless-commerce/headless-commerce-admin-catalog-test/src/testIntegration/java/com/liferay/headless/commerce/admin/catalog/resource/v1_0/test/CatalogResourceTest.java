/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Catalog;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.problem.Problem;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
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
public class CatalogResourceTest extends BaseCatalogResourceTestCase {

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
			AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER, 0, _serviceContext);

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testGroup.getCompanyId());
	}

	@Override
	@Test
	public void testGetProductByExternalReferenceCodeCatalog()
		throws Exception {

		Catalog postCatalog =
			testGetProductByExternalReferenceCodeCatalog_addCatalog();

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.getCommerceCatalog(
				postCatalog.getId());

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			commerceCatalog.getGroupId());

		CProduct cProduct = cpDefinition.getCProduct();

		Catalog getCatalog =
			catalogResource.getProductByExternalReferenceCodeCatalog(
				cProduct.getExternalReferenceCode(), Pagination.of(1, 2));

		assertEquals(postCatalog, getCatalog);
		assertValid(getCatalog);
	}

	@Override
	@Test
	public void testGetProductIdCatalog() throws Exception {
		Catalog postCatalog = testGetProductIdCatalog_addCatalog();

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.getCommerceCatalog(
				postCatalog.getId());

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			commerceCatalog.getGroupId());

		Catalog getCatalog = catalogResource.getProductIdCatalog(
			cpDefinition.getCProductId(), Pagination.of(1, 2));

		assertEquals(postCatalog, getCatalog);
		assertValid(getCatalog);
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteCatalog() throws Exception {
		super.testGraphQLDeleteCatalog();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteCatalogByExternalReferenceCode()
		throws Exception {

		super.testGraphQLDeleteCatalogByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetCatalog() throws Exception {
		super.testGraphQLGetCatalog();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetCatalogByExternalReferenceCode()
		throws Exception {

		super.testGraphQLGetCatalogByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetCatalogByExternalReferenceCodeNotFound()
		throws Exception {

		super.testGraphQLGetCatalogByExternalReferenceCodeNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetCatalogNotFound() throws Exception {
		super.testGraphQLGetCatalogNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetCatalogsPage() throws Exception {
		super.testGraphQLGetCatalogsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductByExternalReferenceCodeCatalog()
		throws Exception {

		super.testGraphQLGetProductByExternalReferenceCodeCatalog();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductIdCatalog() throws Exception {
		super.testGraphQLGetProductIdCatalog();
	}

	@Override
	@Test
	public void testPatchCatalog() throws Exception {
		Catalog randomCatalog = randomCatalog();

		Catalog postCatalog = testPostCatalog_addCatalog(randomCatalog);

		postCatalog.setName(RandomTestUtil.randomString());

		catalogResource.patchCatalog(postCatalog.getId(), postCatalog);

		Catalog patchCatalog = catalogResource.getCatalog(postCatalog.getId());

		Assert.assertNotEquals(randomCatalog.getName(), patchCatalog.getName());

		assertValid(postCatalog);

		_testPatchCatalogWithAccountExternalReferenceCode();
	}

	@Override
	@Test
	public void testPatchCatalogByExternalReferenceCode() throws Exception {
		Catalog randomCatalog = randomCatalog();

		Catalog postCatalog = testPostCatalog_addCatalog(randomCatalog);

		postCatalog.setName(RandomTestUtil.randomString());

		catalogResource.patchCatalogByExternalReferenceCode(
			postCatalog.getExternalReferenceCode(), postCatalog);

		Catalog patchCatalog =
			catalogResource.getCatalogByExternalReferenceCode(
				postCatalog.getExternalReferenceCode());

		Assert.assertNotEquals(randomCatalog.getName(), patchCatalog.getName());

		assertValid(postCatalog);
	}

	@Override
	@Test
	public void testPostCatalog() throws Exception {
		super.testPostCatalog();

		_testPostCatalogWithAccountExternalReferenceCode();
		_testPostCatalogWithLazyReferencingDisabled();
		_testPostCatalogWithLazyReferencingEnabled();
	}

	@Override
	protected Catalog randomCatalog() throws Exception {
		return new Catalog() {
			{
				accountId = _accountEntry.getAccountEntryId();
				currencyCode = _commerceCurrency.getCode();
				currencyExternalReferenceCode =
					_commerceCurrency.getExternalReferenceCode();
				currencyId = _commerceCurrency.getCommerceCurrencyId();
				defaultLanguageId = "en_US";
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				system = Boolean.FALSE;
			}
		};
	}

	@Override
	protected Catalog testDeleteCatalog_addCatalog() throws Exception {
		return catalogResource.postCatalog(randomCatalog());
	}

	@Override
	protected Catalog testDeleteCatalogByExternalReferenceCode_addCatalog()
		throws Exception {

		return catalogResource.postCatalog(randomCatalog());
	}

	@Override
	protected Catalog testGetCatalog_addCatalog() throws Exception {
		return catalogResource.postCatalog(randomCatalog());
	}

	@Override
	protected Catalog testGetCatalogByExternalReferenceCode_addCatalog()
		throws Exception {

		return catalogResource.postCatalog(randomCatalog());
	}

	@Override
	protected Catalog testGetCatalogsPage_addCatalog(Catalog catalog)
		throws Exception {

		return catalogResource.postCatalog(catalog);
	}

	@Override
	protected Catalog testGetProductByExternalReferenceCodeCatalog_addCatalog()
		throws Exception {

		return catalogResource.postCatalog(randomCatalog());
	}

	@Override
	protected Catalog testGetProductIdCatalog_addCatalog() throws Exception {
		return catalogResource.postCatalog(randomCatalog());
	}

	@Override
	protected Catalog testGraphQLCatalog_addCatalog() throws Exception {
		return catalogResource.postCatalog(randomCatalog());
	}

	@Override
	protected Catalog testPostCatalog_addCatalog(Catalog catalog)
		throws Exception {

		return catalogResource.postCatalog(catalog);
	}

	@Override
	protected Catalog testPutCatalogByExternalReferenceCode_addCatalog()
		throws Exception {

		return catalogResource.postCatalog(randomCatalog());
	}

	private Catalog _randomCatalogWithEmptyReferences() throws Exception {
		Catalog catalog = randomCatalog();

		catalog.setAccountExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		catalog.setAccountId((Long)null);
		catalog.setAccountType(Catalog.AccountType.SUPPLIER);
		catalog.setCurrencyCode(RandomTestUtil.randomString(3));
		catalog.setCurrencyExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		catalog.setCurrencyId((Long)null);

		return catalog;
	}

	private void _testPatchCatalogWithAccountExternalReferenceCode()
		throws Exception {

		AccountEntry accountEntry1 = _accountEntryLocalService.addAccountEntry(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			_user.getUserId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER, 0, _serviceContext);
		AccountEntry accountEntry2 = _accountEntryLocalService.addAccountEntry(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			_user.getUserId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER, 0, _serviceContext);

		Catalog postCatalog = catalogResource.postCatalog(randomCatalog());

		catalogResource.patchCatalog(
			postCatalog.getId(),
			new Catalog() {
				{
					accountExternalReferenceCode =
						accountEntry2.getExternalReferenceCode();
					accountId = accountEntry1.getAccountEntryId();
					accountType = Catalog.AccountType.SUPPLIER;
				}
			});

		Catalog getCatalog = catalogResource.getCatalog(postCatalog.getId());

		Assert.assertEquals(
			(Long)accountEntry2.getAccountEntryId(), getCatalog.getAccountId());
	}

	private void _testPostCatalogWithAccountExternalReferenceCode()
		throws Exception {

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			_user.getUserId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER, 0, _serviceContext);

		Catalog catalog = randomCatalog();

		catalog.setAccountExternalReferenceCode(
			accountEntry.getExternalReferenceCode());
		catalog.setAccountId((Long)null);
		catalog.setAccountType(Catalog.AccountType.SUPPLIER);

		Catalog postCatalog = catalogResource.postCatalog(catalog);

		Assert.assertEquals(
			(Long)accountEntry.getAccountEntryId(), postCatalog.getAccountId());
		Assert.assertEquals(
			accountEntry.getExternalReferenceCode(),
			postCatalog.getAccountExternalReferenceCode());
	}

	private void _testPostCatalogWithLazyReferencingDisabled()
		throws Exception {

		try {
			catalogResource.postCatalog(_randomCatalogWithEmptyReferences());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testPostCatalogWithLazyReferencingEnabled() throws Exception {
		Catalog catalog = _randomCatalogWithEmptyReferences();

		Catalog postCatalog = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			postCatalog = catalogResource.postCatalog(catalog);
		}

		AccountEntry accountEntry =
			_accountEntryLocalService.getAccountEntryByExternalReferenceCode(
				catalog.getAccountExternalReferenceCode(),
				testCompany.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, accountEntry.getStatus());
		Assert.assertEquals(
			AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER,
			accountEntry.getType());

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.
				getCommerceCurrencyByExternalReferenceCode(
					catalog.getCurrencyExternalReferenceCode(),
					testCompany.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, commerceCurrency.getStatus());
		Assert.assertEquals(
			catalog.getCurrencyCode(), commerceCurrency.getCode());

		Assert.assertEquals(
			(Long)accountEntry.getAccountEntryId(), postCatalog.getAccountId());
		Assert.assertEquals(
			catalog.getCurrencyCode(), postCatalog.getCurrencyCode());
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}