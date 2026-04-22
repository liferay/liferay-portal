/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductAccountGroup;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductChannel;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfiguration;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductShippingConfiguration;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductSpecification;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductTaxConfiguration;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductVirtualSettings;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Sku;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.problem.Problem;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductResource;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class ProductResourceTest extends BaseProductResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		User user = UserTestUtil.addUser(testCompany);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(testCompany.getGroupId());

		_accountGroup = _accountGroupLocalService.addAccountGroup(
			StringPool.BLANK, user.getUserId(), null,
			RandomTestUtil.randomString(), serviceContext);

		_accountGroup.setDefaultAccountGroup(false);
		_accountGroup.setType(AccountConstants.ACCOUNT_GROUP_TYPE_STATIC);

		_accountGroup = _accountGroupLocalService.updateAccountGroup(
			_accountGroup);

		_commerceCatalog = CommerceCatalogLocalServiceUtil.addCommerceCatalog(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(), serviceContext);

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.addCommerceCurrency(
				null, user.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomString(), BigDecimal.ONE,
				RandomTestUtil.randomLocaleStringMap(), 2, 2, "HALF_EVEN",
				false, RandomTestUtil.nextDouble(), true);

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), commerceCurrency.getCode());

		_commercePriceListLocalService.addCatalogBaseCommercePriceList(
			user.getUserId(), _commerceCatalog.getGroupId(),
			commerceCurrency.getCode(), RandomTestUtil.randomString(),
			"price-list", serviceContext);

		_commercePriceListLocalService.addCatalogBaseCommercePriceList(
			user.getUserId(), _commerceCatalog.getGroupId(),
			commerceCurrency.getCode(), RandomTestUtil.randomString(),
			"promotion", serviceContext);

		_cpOptionCategory = CPTestUtil.addCPOptionCategory(
			testGroup.getGroupId());
		_cpSpecificationOption = CPTestUtil.addCPSpecificationOption(
			testGroup.getGroupId(), true);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.getCPDefinitions(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CPDefinition cpDefinition : cpDefinitions) {
			_cpDefinitionLocalService.deleteCPDefinition(cpDefinition);
		}
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteProduct() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testDeleteProductBatch() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testDeleteProductByExternalReferenceCodeByVersion()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testDeleteProductByVersion() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetProduct() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetProductByExternalReferenceCodeByVersion()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetProductByVersion() throws Exception {
	}

	@Override
	@Test
	public void testGetProductsPage() throws Exception {
		_testGetProductsPage();
		_testGetProductsPageWithSearch();
		_testGetProductsPageWithFilter();
	}

	@Ignore
	@Override
	@Test
	public void testGetProductsPageWithFilterStringEquals() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetProductsPageWithSortDateTime() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetProductsPageWithSortInteger() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetProductsPageWithSortString() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteProduct() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteProductByVersion() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProduct() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductByExternalReferenceCodeByVersion()
		throws Exception {

		super.testGraphQLGetProductByExternalReferenceCodeByVersion();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductByVersion() throws Exception {
		super.testGraphQLGetProductByVersion();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductsPage() throws Exception {
	}

	@Override
	@Test
	public void testPatchProduct() throws Exception {
		Product postProduct = _testPatchProduct_addProduct();

		Product randomPatchProduct = randomProduct();

		productResource.patchProduct(
			postProduct.getProductId(), randomPatchProduct);

		Product getProduct = productResource.getProduct(
			postProduct.getProductId());

		Product expectedPatchProduct = postProduct.clone();

		BaseProductResourceTestCase.BeanTestUtil.copyProperties(
			randomPatchProduct, expectedPatchProduct);

		assertEquals(expectedPatchProduct, getProduct);

		assertValid(getProduct);

		randomPatchProduct = randomProduct();

		productResource.patchProductByExternalReferenceCode(
			getProduct.getExternalReferenceCode(), randomPatchProduct);

		getProduct = productResource.getProduct(postProduct.getProductId());

		BaseProductResourceTestCase.BeanTestUtil.copyProperties(
			randomPatchProduct, expectedPatchProduct);

		assertEquals(expectedPatchProduct, getProduct);

		assertValid(getProduct);

		randomPatchProduct = randomProduct();

		randomPatchProduct.setExpirationDate(
			randomPatchProduct.getDisplayDate());
		randomPatchProduct.setNeverExpire(false);

		postProduct = testPostProduct_addProduct(randomPatchProduct);

		randomPatchProduct.setExpirationDate((Date)null);
		randomPatchProduct.setDisplayDate((Date)null);

		productResource.patchProduct(
			postProduct.getProductId(), randomPatchProduct);

		getProduct = productResource.getProduct(postProduct.getProductId());

		Assert.assertNotNull(getProduct.getDisplayDate());
		Assert.assertEquals(
			getProduct.getDisplayDate(), postProduct.getDisplayDate());
		Assert.assertNotNull(getProduct.getExpirationDate());
		Assert.assertEquals(
			getProduct.getExpirationDate(), postProduct.getExpirationDate());

		_testPatchProductWithNegativeValue("cost");
		_testPatchProductWithNegativeValue("depth");
		_testPatchProductWithNegativeValue("height");
		_testPatchProductWithNegativeValue("price");
		_testPatchProductWithNegativeValue("promo price");
		_testPatchProductWithNegativeValue("weight");
		_testPatchProductWithNegativeValue("width");
	}

	@Ignore
	@Override
	@Test
	public void testPatchProductByExternalReferenceCode() throws Exception {
	}

	@Override
	@Test
	public void testPatchProductByExternalReferenceCodeByVersion()
		throws Exception {

		Product postProduct = productResource.postProduct(randomProduct());

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.fetchCPDefinitionByCProductId(
				postProduct.getProductId(), false);

		Product randomPatchProduct = randomProduct();

		productResource.patchProductByExternalReferenceCodeByVersion(
			postProduct.getExternalReferenceCode(), cpDefinition.getVersion(),
			randomPatchProduct);

		Assert.assertNotNull(
			productResource.getProduct(postProduct.getProductId()));
	}

	@Override
	@Test
	public void testPatchProductByVersion() throws Exception {
		Product postProduct = productResource.postProduct(randomProduct());

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.fetchCPDefinitionByCProductId(
				postProduct.getProductId(), false);

		Product randomPatchProduct = randomProduct();

		productResource.patchProductByVersion(
			cpDefinition.getCProductId(), cpDefinition.getVersion(),
			randomPatchProduct);

		Assert.assertNotNull(
			productResource.getProduct(postProduct.getProductId()));
	}

	@Override
	@Test
	public void testPostProduct() throws Exception {
		super.testPostProduct();

		Product randomProduct = randomProduct();

		randomProduct.setCatalogExternalReferenceCode(
			_commerceCatalog.getExternalReferenceCode());
		randomProduct.setCatalogId((Long)null);

		Product postProduct = testPostProduct_addProduct(randomProduct);

		Product getProduct = productResource.getProduct(
			postProduct.getProductId());

		Product expectedPostProduct = postProduct.clone();

		BaseProductResourceTestCase.BeanTestUtil.copyProperties(
			postProduct, expectedPostProduct);

		assertEquals(expectedPostProduct, getProduct);

		assertValid(getProduct);

		_testPostProductProductShippingConfigurationFromProductConfiguration();
		_testPostProductProductTaxConfigurationFromProductConfiguration();
		_testPostProductVirtual();
		_testPostProductWithProductAccountGroupExternalReferenceCode();
		_testPostProductWithProductChannelExternalReferenceCode();
		_testPostProductWithWorkflowSingleApprover();
	}

	@Override
	@Test
	public void testPutProductByExternalReferenceCode() throws Exception {
		_testPutProductByExternalReferenceCodeWithFutureDisplayDate();
	}

	@Ignore
	@Override
	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"active", "catalogId", "description", "externalReferenceCode",
			"name", "productType", "shortDescription"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"name", "productType"};
	}

	@Override
	protected Product randomProduct() throws Exception {
		return new Product() {
			{
				active = true;
				catalogId = _commerceCatalog.getCommerceCatalogId();
				description = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				productType = SimpleCPTypeConstants.NAME;
				shortDescription = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
			}
		};
	}

	@Override
	protected Product testDeleteProduct_addProduct() throws Exception {
		return productResource.postProduct(randomProduct());
	}

	@Override
	protected Product testDeleteProductByExternalReferenceCode_addProduct()
		throws Exception {

		return productResource.postProduct(randomProduct());
	}

	@Override
	protected Product testGetProduct_addProduct() throws Exception {
		return productResource.postProduct(randomProduct());
	}

	@Override
	protected Product testGetProductByExternalReferenceCode_addProduct()
		throws Exception {

		return productResource.postProduct(randomProduct());
	}

	@Override
	protected Product testGetProductsPage_addProduct(Product product)
		throws Exception {

		return productResource.postProduct(product);
	}

	@Override
	protected Product testGraphQLProduct_addProduct() throws Exception {
		return productResource.postProduct(randomProduct());
	}

	@Override
	protected Product testPostProduct_addProduct(Product product)
		throws Exception {

		return productResource.postProduct(product);
	}

	@Override
	protected Product testPostProductByExternalReferenceCodeClone_addProduct(
			Product product)
		throws Exception {

		return productResource.postProduct(product);
	}

	@Override
	protected Product testPostProductClone_addProduct(Product product)
		throws Exception {

		return productResource.postProduct(product);
	}

	private Product _randomProductWithProductConfiguration() {
		return new Product() {
			{
				active = true;
				catalogId = _commerceCatalog.getCommerceCatalogId();
				description = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				productConfiguration = new ProductConfiguration() {
					{
						externalReferenceCode = RandomTestUtil.randomString();
						id = RandomTestUtil.randomLong();
						productShippingConfiguration =
							new ProductShippingConfiguration() {
								{
									depth = BigDecimal.valueOf(10D);
									freeShipping = true;
									height = BigDecimal.valueOf(10D);
									shippable = false;
									shippingExtraPrice = BigDecimal.valueOf(
										10D);
									shippingSeparately = true;
									weight = BigDecimal.valueOf(10D);
									width = BigDecimal.valueOf(10D);
								}
							};
						productTaxConfiguration =
							new ProductTaxConfiguration() {
								{
									id = 12345L;
									taxable = false;
								}
							};
					}
				};
				productType = SimpleCPTypeConstants.NAME;
				shortDescription = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
			}
		};
	}

	private Product _randomProductWithProductSpecification(
			String specificationValue)
		throws Exception {

		return new Product() {
			{
				active = true;
				catalogId = _commerceCatalog.getCommerceCatalogId();
				description = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				productSpecifications = new ProductSpecification[] {
					new ProductSpecification() {
						{
							externalReferenceCode =
								RandomTestUtil.randomString();
							label = LanguageUtils.getLanguageIdMap(
								RandomTestUtil.randomLocaleStringMap());
							optionCategoryExternalReferenceCode =
								_cpOptionCategory.getExternalReferenceCode();
							priority = RandomTestUtil.randomDouble();
							specificationKey = _cpSpecificationOption.getKey();
							value = LanguageUtils.getLanguageIdMap(
								HashMapBuilder.put(
									LocaleUtil.getDefault(), specificationValue
								).build());
						}
					}
				};
				productType = SimpleCPTypeConstants.NAME;
				shortDescription = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
			}
		};
	}

	private Product _randomProductWithSku() throws Exception {
		return new Product() {
			{
				active = true;
				catalogId = _commerceCatalog.getCommerceCatalogId();
				description = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				productType = SimpleCPTypeConstants.NAME;
				shortDescription = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				skus = new Sku[] {
					new Sku() {
						{
							cost = BigDecimal.valueOf(
								RandomTestUtil.randomDouble());
							depth = RandomTestUtil.randomDouble();
							discontinued = false;
							discontinuedDate = RandomTestUtil.nextDate();
							displayDate = RandomTestUtil.nextDate();
							expirationDate = RandomTestUtil.nextDate();
							externalReferenceCode = StringUtil.toLowerCase(
								RandomTestUtil.randomString());
							gtin = "test";
							height = RandomTestUtil.randomDouble();
							inventoryLevel = RandomTestUtil.randomInt();
							manufacturerPartNumber = StringUtil.toLowerCase(
								RandomTestUtil.randomString());
							neverExpire = true;
							price = BigDecimal.valueOf(
								RandomTestUtil.randomDouble());
							promoPrice = BigDecimal.valueOf(
								RandomTestUtil.randomDouble());
							published = true;
							purchasable = true;
							sku = StringUtil.toLowerCase(
								RandomTestUtil.randomString());
							unspsc = StringUtil.toLowerCase(
								RandomTestUtil.randomString());
							weight = RandomTestUtil.randomDouble();
							width = RandomTestUtil.randomDouble();
						}
					}
				};
			}
		};
	}

	private void _testGetProductsPage() throws Exception {
		Page<Product> page = productResource.getProductsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Product product1 = testGetProductsPage_addProduct(randomProduct());
		Product product2 = testGetProductsPage_addProduct(randomProduct());

		page = productResource.getProductsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(product1, (List<Product>)page.getItems());
		assertContains(product2, (List<Product>)page.getItems());
		assertValid(page, testGetProductsPage_getExpectedActions());

		productResource.deleteProduct(product1.getProductId());
		productResource.deleteProduct(product2.getProductId());
	}

	private void _testGetProductsPageWithFilter() throws Exception {
		Page<Product> page = productResource.getProductsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Product product1 = testGetProductsPage_addProduct(
			_randomProductWithSku());
		Product product2 = testGetProductsPage_addProduct(
			_randomProductWithProductSpecification("test specification"));

		page = productResource.getProductsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(product1, (List<Product>)page.getItems());
		assertContains(product2, (List<Product>)page.getItems());
		assertValid(page, testGetProductsPage_getExpectedActions());

		page = productResource.getProductsPage(
			null, "(gtins/any(x:contains(x, 'test')))", Pagination.of(1, 10),
			null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		assertContains(product1, (List<Product>)page.getItems());
		assertValid(page, testGetProductsPage_getExpectedActions());

		page = productResource.getProductsPage(
			null, String.format("(productId eq %s)", product1.getProductId()),
			Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		assertContains(product1, (List<Product>)page.getItems());
		assertValid(page, testGetProductsPage_getExpectedActions());

		page = productResource.getProductsPage(
			null, "(specificationValues/any(x:contains(x, 'test')))",
			Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		assertContains(product2, (List<Product>)page.getItems());
		assertValid(page, testGetProductsPage_getExpectedActions());

		page = productResource.getProductsPage(
			null, "(specificationValues/any(x:contains(x, 'specification')))",
			Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		assertContains(product2, (List<Product>)page.getItems());
		assertValid(page, testGetProductsPage_getExpectedActions());

		page = productResource.getProductsPage(
			null,
			"(specificationValues/any(x:contains(x, 'test specification')))",
			Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		assertContains(product2, (List<Product>)page.getItems());
		assertValid(page, testGetProductsPage_getExpectedActions());

		productResource.deleteProduct(product1.getProductId());
		productResource.deleteProduct(product2.getProductId());
	}

	private void _testGetProductsPageWithSearch() throws Exception {
		String string1 = RandomTestUtil.randomString();
		String string2 = RandomTestUtil.randomString();

		Product product1 = testGetProductsPage_addProduct(
			_randomProductWithProductSpecification(
				StringBundler.concat(string1, StringPool.SPACE, string2)));

		Page<Product> page = productResource.getProductsPage(
			String.valueOf(product1.getProductId()), null, Pagination.of(1, 10),
			null);

		Assert.assertEquals(1, page.getTotalCount());

		page = productResource.getProductsPage(
			string1, null, Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());
		assertContains(product1, (List<Product>)page.getItems());

		page = productResource.getProductsPage(
			string2, null, Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertContains(product1, (List<Product>)page.getItems());

		page = productResource.getProductsPage(
			StringBundler.concat(string1, StringPool.SPACE, string2), null,
			Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertContains(product1, (List<Product>)page.getItems());

		String string3 = RandomTestUtil.randomString();

		Product product2 = testGetProductsPage_addProduct(
			_randomProductWithProductSpecification(string3));

		page = productResource.getProductsPage(
			StringBundler.concat(
				string2, StringPool.COMMA, StringPool.SPACE, string3),
			null, Pagination.of(1, 10), null);

		Assert.assertEquals(2, page.getTotalCount());

		assertContains(product1, (List<Product>)page.getItems());
		assertContains(product2, (List<Product>)page.getItems());

		productResource.deleteProduct(product1.getProductId());
		productResource.deleteProduct(product2.getProductId());
	}

	private Product _testPatchProduct_addProduct() throws Exception {
		return productResource.postProduct(randomProduct());
	}

	private void _testPatchProductWithNegativeValue(String fieldName)
		throws Exception {

		try {
			Product randomProduct = _randomProductWithSku();

			Product postProduct = productResource.postProduct(randomProduct);

			Sku sku = randomProduct.getSkus()[0];

			if (Objects.equals(fieldName, "cost")) {
				sku.setCost(BigDecimal.valueOf(-10.0));
			}
			else if (Objects.equals(fieldName, "depth")) {
				sku.setDepth(-10.0);
			}
			else if (Objects.equals(fieldName, "height")) {
				sku.setHeight(-10.0);
			}
			else if (Objects.equals(fieldName, "price")) {
				sku.setPrice(BigDecimal.valueOf(-10.0));
			}
			else if (Objects.equals(fieldName, "promo price")) {
				sku.setPromoPrice(BigDecimal.valueOf(-10.0));
			}
			else if (Objects.equals(fieldName, "weight")) {
				sku.setWeight(-10.0);
			}
			else if (Objects.equals(fieldName, "width")) {
				sku.setWidth(-10.0);
			}

			productResource.patchProduct(
				postProduct.getProductId(), randomProduct);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Sku " + fieldName + " is invalid", problem.getTitle());
		}
	}

	private void _testPostProductProductShippingConfigurationFromProductConfiguration()
		throws Exception {

		Product postProduct = productResource.postProduct(
			_randomProductWithProductConfiguration());

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			postProduct.getId());

		Assert.assertEquals(10D, cpDefinition.getDepth(), 0.0);
		Assert.assertTrue(cpDefinition.isFreeShipping());
		Assert.assertEquals(10D, cpDefinition.getHeight(), 0.0);
		Assert.assertFalse(cpDefinition.isShippable());
		Assert.assertEquals(10D, cpDefinition.getShippingExtraPrice(), 0.0);
		Assert.assertTrue(cpDefinition.isShipSeparately());
		Assert.assertEquals(10D, cpDefinition.getWeight(), 0.0);
		Assert.assertEquals(10D, cpDefinition.getWidth(), 0.0);
	}

	private void _testPostProductProductTaxConfigurationFromProductConfiguration()
		throws Exception {

		Product postProduct = productResource.postProduct(
			_randomProductWithProductConfiguration());

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			postProduct.getId());

		Assert.assertEquals(12345, cpDefinition.getCPTaxCategoryId());
		Assert.assertTrue(cpDefinition.isTaxExempt());
	}

	private void _testPostProductVirtual() throws Exception {
		User omniadminUser = UserTestUtil.addOmniadminUser();

		String password = RandomTestUtil.randomString();

		_userLocalService.updatePassword(
			omniadminUser.getUserId(), password, password, false, true);

		ProductResource productResource = ProductResource.builder(
		).authentication(
			omniadminUser.getEmailAddress(), password
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "productVirtualSettings"
		).build();

		Product randomVirtualProduct = productResource.postProduct(
			new Product() {
				{
					active = true;
					catalogId = _commerceCatalog.getCommerceCatalogId();
					description = LanguageUtils.getLanguageIdMap(
						RandomTestUtil.randomLocaleStringMap());
					externalReferenceCode = StringUtil.toLowerCase(
						RandomTestUtil.randomString());
					name = LanguageUtils.getLanguageIdMap(
						RandomTestUtil.randomLocaleStringMap());
					productType = VirtualCPTypeConstants.NAME;
					productVirtualSettings = new ProductVirtualSettings() {
						{
							attachment = Base64.encode(
								FileUtil.getBytes(
									ProductResourceTest.class,
									"dependencies/image.jpg"));
							duration = RandomTestUtil.randomLong();
							maxUsages = RandomTestUtil.randomInt();
						}
					};
					shortDescription = LanguageUtils.getLanguageIdMap(
						RandomTestUtil.randomLocaleStringMap());
				}
			});

		ProductVirtualSettings productVirtualSettings =
			randomVirtualProduct.getProductVirtualSettings();

		ProductVirtualSettingsFileEntry[] productVirtualSettingsFileEntries =
			productVirtualSettings.getProductVirtualSettingsFileEntries();

		Assert.assertTrue(productVirtualSettingsFileEntries.length == 1);

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry =
			productVirtualSettingsFileEntries[0];

		Assert.assertNotNull(productVirtualSettingsFileEntry.getSrc());
	}

	private void _testPostProductWithProductAccountGroupExternalReferenceCode()
		throws Exception {

		User omniadminUser = UserTestUtil.addOmniadminUser();

		String password = RandomTestUtil.randomString();

		_userLocalService.updatePassword(
			omniadminUser.getUserId(), password, password, false, true);

		ProductResource productResource = ProductResource.builder(
		).authentication(
			omniadminUser.getEmailAddress(), password
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "productAccountGroups"
		).build();

		Product randomProduct = _randomProductWithSku();

		randomProduct.setProductAccountGroupFilter(true);
		randomProduct.setProductAccountGroups(
			new ProductAccountGroup[] {
				new ProductAccountGroup() {
					{
						externalReferenceCode =
							_accountGroup.getExternalReferenceCode();
					}
				}
			});

		Product postProduct = productResource.postProduct(randomProduct);

		ProductAccountGroup productAccountGroup =
			postProduct.getProductAccountGroups()[0];

		Assert.assertEquals(
			_accountGroup.getAccountGroupId(),
			GetterUtil.getLong(productAccountGroup.getAccountGroupId()));
		Assert.assertEquals(
			_accountGroup.getExternalReferenceCode(),
			productAccountGroup.getExternalReferenceCode());
	}

	private void _testPostProductWithProductChannelExternalReferenceCode()
		throws Exception {

		User omniadminUser = UserTestUtil.addOmniadminUser();

		String password = RandomTestUtil.randomString();

		_userLocalService.updatePassword(
			omniadminUser.getUserId(), password, password, false, true);

		ProductResource productResource = ProductResource.builder(
		).authentication(
			omniadminUser.getEmailAddress(), password
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "productChannels"
		).build();

		Product randomProduct = _randomProductWithSku();

		randomProduct.setProductChannels(
			new ProductChannel[] {
				new ProductChannel() {
					{
						externalReferenceCode =
							_commerceChannel.getExternalReferenceCode();
					}
				}
			});

		Product postProduct = productResource.postProduct(randomProduct);

		ProductChannel productChannel = postProduct.getProductChannels()[0];

		Assert.assertEquals(
			_commerceChannel.getCommerceChannelId(),
			GetterUtil.getLong(productChannel.getChannelId()));
		Assert.assertEquals(
			_commerceChannel.getExternalReferenceCode(),
			productChannel.getExternalReferenceCode());
	}

	private void _testPostProductWithWorkflowSingleApprover() throws Exception {
		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), testCompany.getCompanyId(),
			testCompany.getGroupId(), CPDefinition.class.getName(), 0, 0,
			"Single Approver@1");

		Product postProduct = productResource.postProduct(
			_randomProductWithSku());

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			postProduct.getId());

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		Assert.assertEquals(
			cpInstance.getStatus(), WorkflowConstants.STATUS_APPROVED);
	}

	private void _testPutProductByExternalReferenceCodeWithFutureDisplayDate()
		throws Exception {

		Product randomProduct = _randomProductWithSku();

		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.YEAR, 1);

		randomProduct.setDisplayDate(calendar.getTime());

		Product postProduct = productResource.postProduct(randomProduct);

		Product putProduct = postProduct.clone();

		putProduct.setName(
			Collections.singletonMap(
				"en_US", "_testPostProductWithFutureDisplayDate"));

		putProduct = productResource.putProductByExternalReferenceCode(
			postProduct.getExternalReferenceCode(), putProduct);

		Product getProduct = productResource.getProduct(
			putProduct.getProductId());

		Assert.assertEquals(
			String.valueOf(postProduct), postProduct.getId(),
			putProduct.getId());
		Assert.assertEquals(
			String.valueOf(putProduct), putProduct.getId(), getProduct.getId());
		Assert.assertEquals(
			String.valueOf(putProduct), putProduct.getName(),
			getProduct.getName());
	}

	@DeleteAfterTestRun
	private AccountGroup _accountGroup;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@DeleteAfterTestRun
	private CPOptionCategory _cpOptionCategory;

	@DeleteAfterTestRun
	private CPSpecificationOption _cpSpecificationOption;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}