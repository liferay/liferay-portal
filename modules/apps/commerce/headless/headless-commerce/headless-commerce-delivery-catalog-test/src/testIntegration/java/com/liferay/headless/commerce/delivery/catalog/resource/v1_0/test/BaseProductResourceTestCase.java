/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.Product;
import com.liferay.headless.commerce.delivery.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Page;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.catalog.client.resource.v1_0.ProductResource;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.ProductSerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BaseProductResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_productResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productResource = ProductResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Product product1 = randomProduct();

		String json = objectMapper.writeValueAsString(product1);

		Product product2 = ProductSerDes.toDTO(json);

		Assert.assertTrue(equals(product1, product2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Product product = randomProduct();

		String json1 = objectMapper.writeValueAsString(product);
		String json2 = ProductSerDes.toJSON(product);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	protected ObjectMapper getClientSerDesObjectMapper() {
		return new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		Product product = randomProduct();

		product.setCatalogName(regex);
		product.setDescription(regex);
		product.setExternalReferenceCode(regex);
		product.setMetaDescription(regex);
		product.setMetaKeyword(regex);
		product.setMetaTitle(regex);
		product.setName(regex);
		product.setProductType(regex);
		product.setShortDescription(regex);
		product.setSlug(regex);
		product.setUrlImage(regex);

		String json = ProductSerDes.toJSON(product);

		Assert.assertFalse(json.contains(regex));

		product = ProductSerDes.toDTO(json);

		Assert.assertEquals(regex, product.getCatalogName());
		Assert.assertEquals(regex, product.getDescription());
		Assert.assertEquals(regex, product.getExternalReferenceCode());
		Assert.assertEquals(regex, product.getMetaDescription());
		Assert.assertEquals(regex, product.getMetaKeyword());
		Assert.assertEquals(regex, product.getMetaTitle());
		Assert.assertEquals(regex, product.getName());
		Assert.assertEquals(regex, product.getProductType());
		Assert.assertEquals(regex, product.getShortDescription());
		Assert.assertEquals(regex, product.getSlug());
		Assert.assertEquals(regex, product.getUrlImage());
	}

	@Test
	public void testGetChannelProduct() throws Exception {
		Product postProduct = testGetChannelProduct_addProduct();

		Product getProduct = productResource.getChannelProduct(
			testGetChannelProduct_getChannelId(), postProduct.getId(), null);

		assertEquals(postProduct, getProduct);
		assertValid(getProduct);
	}

	protected Product testGetChannelProduct_addProduct() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelProduct_getChannelId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetChannelProduct() throws Exception {
		Product product = testGraphQLGetChannelProduct_addProduct();

		// No namespace

		Assert.assertTrue(
			equals(
				product,
				ProductSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"channelProduct",
								new HashMap<String, Object>() {
									{
										put(
											"channelId",
											testGraphQLGetChannelProduct_getChannelId());
										put("productId", product.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/channelProduct"))));

		// Using the namespace headlessCommerceDeliveryCatalog_v1_0

		Assert.assertTrue(
			equals(
				product,
				ProductSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryCatalog_v1_0",
								new GraphQLField(
									"channelProduct",
									new HashMap<String, Object>() {
										{
											put(
												"channelId",
												testGraphQLGetChannelProduct_getChannelId());
											put("productId", product.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryCatalog_v1_0",
						"Object/channelProduct"))));
	}

	protected Long testGraphQLGetChannelProduct_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetChannelProductNotFound() throws Exception {
		Long irrelevantChannelId = RandomTestUtil.randomLong();
		Long irrelevantProductId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"channelProduct",
						new HashMap<String, Object>() {
							{
								put("channelId", irrelevantChannelId);
								put("productId", irrelevantProductId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceDeliveryCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryCatalog_v1_0",
						new GraphQLField(
							"channelProduct",
							new HashMap<String, Object>() {
								{
									put("channelId", irrelevantChannelId);
									put("productId", irrelevantProductId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Product testGraphQLGetChannelProduct_addProduct()
		throws Exception {

		return testGraphQLProduct_addProduct();
	}

	@Test
	public void testGetChannelProductByFriendlyUrlPath() throws Exception {
		Product postProduct =
			testGetChannelProductByFriendlyUrlPath_addProduct();

		Product getProduct = productResource.getChannelProductByFriendlyUrlPath(
			testGetChannelProductByFriendlyUrlPath_getChannelId(),
			testGetChannelProductByFriendlyUrlPath_getFriendlyUrlPath(), null);

		assertEquals(postProduct, getProduct);
		assertValid(getProduct);
	}

	protected Product testGetChannelProductByFriendlyUrlPath_addProduct()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelProductByFriendlyUrlPath_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetChannelProductByFriendlyUrlPath_getFriendlyUrlPath()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetChannelProductByFriendlyUrlPath()
		throws Exception {

		Product product =
			testGraphQLGetChannelProductByFriendlyUrlPath_addProduct();

		// No namespace

		Assert.assertTrue(
			equals(
				product,
				ProductSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"channelProductByFriendlyUrlPath",
								new HashMap<String, Object>() {
									{
										put(
											"channelId",
											testGraphQLGetChannelProductByFriendlyUrlPath_getChannelId());

										put(
											"friendlyUrlPath",
											"\"" +
												testGraphQLGetChannelProductByFriendlyUrlPath_getFriendlyUrlPath() +
													"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/channelProductByFriendlyUrlPath"))));

		// Using the namespace headlessCommerceDeliveryCatalog_v1_0

		Assert.assertTrue(
			equals(
				product,
				ProductSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryCatalog_v1_0",
								new GraphQLField(
									"channelProductByFriendlyUrlPath",
									new HashMap<String, Object>() {
										{
											put(
												"channelId",
												testGraphQLGetChannelProductByFriendlyUrlPath_getChannelId());

											put(
												"friendlyUrlPath",
												"\"" +
													testGraphQLGetChannelProductByFriendlyUrlPath_getFriendlyUrlPath() +
														"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryCatalog_v1_0",
						"Object/channelProductByFriendlyUrlPath"))));
	}

	protected Long testGraphQLGetChannelProductByFriendlyUrlPath_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGraphQLGetChannelProductByFriendlyUrlPath_getFriendlyUrlPath()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetChannelProductByFriendlyUrlPathNotFound()
		throws Exception {

		Long irrelevantChannelId = RandomTestUtil.randomLong();
		String irrelevantFriendlyUrlPath =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"channelProductByFriendlyUrlPath",
						new HashMap<String, Object>() {
							{
								put("channelId", irrelevantChannelId);
								put(
									"friendlyUrlPath",
									irrelevantFriendlyUrlPath);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceDeliveryCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryCatalog_v1_0",
						new GraphQLField(
							"channelProductByFriendlyUrlPath",
							new HashMap<String, Object>() {
								{
									put("channelId", irrelevantChannelId);
									put(
										"friendlyUrlPath",
										irrelevantFriendlyUrlPath);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Product testGraphQLGetChannelProductByFriendlyUrlPath_addProduct()
		throws Exception {

		return testGraphQLProduct_addProduct();
	}

	@Test
	public void testGetChannelProductsPage() throws Exception {
		Long channelId = testGetChannelProductsPage_getChannelId();
		Long irrelevantChannelId =
			testGetChannelProductsPage_getIrrelevantChannelId();

		Page<Product> page = productResource.getChannelProductsPage(
			channelId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantChannelId != null) {
			Product irrelevantProduct = testGetChannelProductsPage_addProduct(
				irrelevantChannelId, randomIrrelevantProduct());

			page = productResource.getChannelProductsPage(
				irrelevantChannelId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantProduct, (List<Product>)page.getItems());
			assertValid(
				page,
				testGetChannelProductsPage_getExpectedActions(
					irrelevantChannelId));
		}

		Product product1 = testGetChannelProductsPage_addProduct(
			channelId, randomProduct());

		Product product2 = testGetChannelProductsPage_addProduct(
			channelId, randomProduct());

		page = productResource.getChannelProductsPage(
			channelId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(product1, (List<Product>)page.getItems());
		assertContains(product2, (List<Product>)page.getItems());
		assertValid(
			page, testGetChannelProductsPage_getExpectedActions(channelId));
	}

	protected Map<String, Map<String, String>>
			testGetChannelProductsPage_getExpectedActions(Long channelId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelProductsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long channelId = testGetChannelProductsPage_getChannelId();

		Product product1 = randomProduct();

		product1 = testGetChannelProductsPage_addProduct(channelId, product1);

		for (EntityField entityField : entityFields) {
			Page<Product> page = productResource.getChannelProductsPage(
				channelId, null, null,
				getFilterString(entityField, "between", product1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(product1),
				(List<Product>)page.getItems());
		}
	}

	@Test
	public void testGetChannelProductsPageWithFilterDoubleEquals()
		throws Exception {

		testGetChannelProductsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetChannelProductsPageWithFilterStringContains()
		throws Exception {

		testGetChannelProductsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelProductsPageWithFilterStringEquals()
		throws Exception {

		testGetChannelProductsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelProductsPageWithFilterStringStartsWith()
		throws Exception {

		testGetChannelProductsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetChannelProductsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long channelId = testGetChannelProductsPage_getChannelId();

		Product product1 = testGetChannelProductsPage_addProduct(
			channelId, randomProduct());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Product product2 = testGetChannelProductsPage_addProduct(
			channelId, randomProduct());

		for (EntityField entityField : entityFields) {
			Page<Product> page = productResource.getChannelProductsPage(
				channelId, null, null,
				getFilterString(entityField, operator, product1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(product1),
				(List<Product>)page.getItems());
		}
	}

	@Test
	public void testGetChannelProductsPageWithPagination() throws Exception {
		Long channelId = testGetChannelProductsPage_getChannelId();

		Page<Product> productsPage = productResource.getChannelProductsPage(
			channelId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(productsPage.getTotalCount());

		Product product1 = testGetChannelProductsPage_addProduct(
			channelId, randomProduct());

		Product product2 = testGetChannelProductsPage_addProduct(
			channelId, randomProduct());

		Product product3 = testGetChannelProductsPage_addProduct(
			channelId, randomProduct());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Product> page1 = productResource.getChannelProductsPage(
				channelId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(product1, (List<Product>)page1.getItems());

			Page<Product> page2 = productResource.getChannelProductsPage(
				channelId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(product2, (List<Product>)page2.getItems());

			Page<Product> page3 = productResource.getChannelProductsPage(
				channelId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(product3, (List<Product>)page3.getItems());
		}
		else {
			Page<Product> page1 = productResource.getChannelProductsPage(
				channelId, null, null, null, Pagination.of(1, totalCount + 2),
				null);

			List<Product> products1 = (List<Product>)page1.getItems();

			Assert.assertEquals(
				products1.toString(), totalCount + 2, products1.size());

			Page<Product> page2 = productResource.getChannelProductsPage(
				channelId, null, null, null, Pagination.of(2, totalCount + 2),
				null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Product> products2 = (List<Product>)page2.getItems();

			Assert.assertEquals(products2.toString(), 1, products2.size());

			Page<Product> page3 = productResource.getChannelProductsPage(
				channelId, null, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(product1, (List<Product>)page3.getItems());
			assertContains(product2, (List<Product>)page3.getItems());
			assertContains(product3, (List<Product>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelProductsPageWithSortDateTime() throws Exception {
		testGetChannelProductsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, product1, product2) -> {
				BeanTestUtil.setProperty(
					product1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelProductsPageWithSortDouble() throws Exception {
		testGetChannelProductsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, product1, product2) -> {
				BeanTestUtil.setProperty(product1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(product2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelProductsPageWithSortInteger() throws Exception {
		testGetChannelProductsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, product1, product2) -> {
				BeanTestUtil.setProperty(product1, entityField.getName(), 0);
				BeanTestUtil.setProperty(product2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelProductsPageWithSortString() throws Exception {
		testGetChannelProductsPageWithSort(
			EntityField.Type.STRING,
			(entityField, product1, product2) -> {
				Class<?> clazz = product1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						product1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						product2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						product1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						product2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						product1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						product2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetChannelProductsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Product, Product, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long channelId = testGetChannelProductsPage_getChannelId();

		Product product1 = randomProduct();
		Product product2 = randomProduct();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, product1, product2);
		}

		product1 = testGetChannelProductsPage_addProduct(channelId, product1);

		product2 = testGetChannelProductsPage_addProduct(channelId, product2);

		Page<Product> page = productResource.getChannelProductsPage(
			channelId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Product> ascPage = productResource.getChannelProductsPage(
				channelId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(product1, (List<Product>)ascPage.getItems());
			assertContains(product2, (List<Product>)ascPage.getItems());

			Page<Product> descPage = productResource.getChannelProductsPage(
				channelId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(product2, (List<Product>)descPage.getItems());
			assertContains(product1, (List<Product>)descPage.getItems());
		}
	}

	protected Product testGetChannelProductsPage_addProduct(
			Long channelId, Product product)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelProductsPage_getChannelId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelProductsPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected Product testGraphQLProduct_addProduct() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(Product product, List<Product> products) {
		boolean contains = false;

		for (Product item : products) {
			if (equals(product, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(products + " does not contain " + product, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Product product1, Product product2) {
		Assert.assertTrue(
			product1 + " does not equal " + product2,
			equals(product1, product2));
	}

	protected void assertEquals(
		List<Product> products1, List<Product> products2) {

		Assert.assertEquals(products1.size(), products2.size());

		for (int i = 0; i < products1.size(); i++) {
			Product product1 = products1.get(i);
			Product product2 = products2.get(i);

			assertEquals(product1, product2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Product> products1, List<Product> products2) {

		Assert.assertEquals(products1.size(), products2.size());

		for (Product product1 : products1) {
			boolean contains = false;

			for (Product product2 : products2) {
				if (equals(product1, product2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				products2 + " does not contain " + product1, contains);
		}
	}

	protected void assertValid(Product product) throws Exception {
		boolean valid = true;

		if (product.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("attachments", additionalAssertFieldName)) {
				if (product.getAttachments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("catalogName", additionalAssertFieldName)) {
				if (product.getCatalogName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("categories", additionalAssertFieldName)) {
				if (product.getCategories() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (product.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (product.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (product.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expando", additionalAssertFieldName)) {
				if (product.getExpando() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (product.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("images", additionalAssertFieldName)) {
				if (product.getImages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("linkedProducts", additionalAssertFieldName)) {
				if (product.getLinkedProducts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("metaDescription", additionalAssertFieldName)) {
				if (product.getMetaDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("metaKeyword", additionalAssertFieldName)) {
				if (product.getMetaKeyword() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("metaTitle", additionalAssertFieldName)) {
				if (product.getMetaTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (product.getModifiedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"multipleOrderQuantity", additionalAssertFieldName)) {

				if (product.getMultipleOrderQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (product.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfiguration", additionalAssertFieldName)) {

				if (product.getProductConfiguration() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (product.getProductId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productOptions", additionalAssertFieldName)) {
				if (product.getProductOptions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productSpecifications", additionalAssertFieldName)) {

				if (product.getProductSpecifications() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productType", additionalAssertFieldName)) {
				if (product.getProductType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("relatedProducts", additionalAssertFieldName)) {
				if (product.getRelatedProducts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shortDescription", additionalAssertFieldName)) {
				if (product.getShortDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("skus", additionalAssertFieldName)) {
				if (product.getSkus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("slug", additionalAssertFieldName)) {
				if (product.getSlug() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("tags", additionalAssertFieldName)) {
				if (product.getTags() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("urlImage", additionalAssertFieldName)) {
				if (product.getUrlImage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("urls", additionalAssertFieldName)) {
				if (product.getUrls() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<Product> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Product> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Product> products = page.getItems();

		int size = products.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);

		assertValid(page.getActions(), expectedActions);
	}

	protected void assertValid(
		Map<String, Map<String, String>> actions1,
		Map<String, Map<String, String>> actions2) {

		for (String key : actions2.keySet()) {
			Map action = actions1.get(key);

			Assert.assertNotNull(key + " does not contain an action", action);

			Map<String, String> expectedAction = actions2.get(key);

			Assert.assertEquals(
				expectedAction.get("method"), action.get("method"));
			Assert.assertEquals(expectedAction.get("href"), action.get("href"));
		}
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.delivery.catalog.dto.v1_0.
						Product.class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			graphQLFields.addAll(getGraphQLFields(field));
		}

		return graphQLFields;
	}

	protected List<GraphQLField> getGraphQLFields(
			java.lang.reflect.Field... fields)
		throws Exception {

		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field : fields) {
			com.liferay.portal.vulcan.graphql.annotation.GraphQLField
				vulcanGraphQLField = field.getAnnotation(
					com.liferay.portal.vulcan.graphql.annotation.GraphQLField.
						class);

			if (vulcanGraphQLField != null) {
				Class<?> clazz = field.getType();

				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}

				List<GraphQLField> childrenGraphQLFields = getGraphQLFields(
					getDeclaredFields(clazz));

				graphQLFields.add(
					new GraphQLField(field.getName(), childrenGraphQLFields));
			}
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(Product product1, Product product2) {
		if (product1 == product2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("attachments", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getAttachments(), product2.getAttachments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("catalogName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getCatalogName(), product2.getCatalogName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("categories", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getCategories(), product2.getCategories())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getCreateDate(), product2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getCustomFields(),
						product2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getDescription(), product2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expando", additionalAssertFieldName)) {
				if (!equals(
						(Map)product1.getExpando(),
						(Map)product2.getExpando())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getExternalReferenceCode(),
						product2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(product1.getId(), product2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("images", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getImages(), product2.getImages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("linkedProducts", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getLinkedProducts(),
						product2.getLinkedProducts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("metaDescription", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getMetaDescription(),
						product2.getMetaDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("metaKeyword", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getMetaKeyword(), product2.getMetaKeyword())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("metaTitle", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getMetaTitle(), product2.getMetaTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getModifiedDate(),
						product2.getModifiedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"multipleOrderQuantity", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getMultipleOrderQuantity(),
						product2.getMultipleOrderQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getName(), product2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfiguration", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getProductConfiguration(),
						product2.getProductConfiguration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getProductId(), product2.getProductId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productOptions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getProductOptions(),
						product2.getProductOptions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productSpecifications", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getProductSpecifications(),
						product2.getProductSpecifications())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getProductType(), product2.getProductType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("relatedProducts", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getRelatedProducts(),
						product2.getRelatedProducts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shortDescription", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getShortDescription(),
						product2.getShortDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("skus", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getSkus(), product2.getSkus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("slug", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getSlug(), product2.getSlug())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("tags", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getTags(), product2.getTags())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("urlImage", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getUrlImage(), product2.getUrlImage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("urls", additionalAssertFieldName)) {
				if (!equals((Map)product1.getUrls(), (Map)product2.getUrls())) {
					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equals(
		Map<String, Object> map1, Map<String, Object> map2) {

		if (Objects.equals(map1.keySet(), map2.keySet())) {
			for (Map.Entry<String, Object> entry : map1.entrySet()) {
				if (entry.getValue() instanceof Map) {
					if (!equals(
							(Map)entry.getValue(),
							(Map)map2.get(entry.getKey()))) {

						return false;
					}
				}
				else if (!Objects.deepEquals(
							entry.getValue(), map2.get(entry.getKey()))) {

					return false;
				}
			}

			return true;
		}

		return false;
	}

	protected java.lang.reflect.Field[] getDeclaredFields(Class clazz)
		throws Exception {

		if (clazz.getClassLoader() == null) {
			return new java.lang.reflect.Field[0];
		}

		return TransformUtil.transform(
			ReflectionUtil.getDeclaredFields(clazz),
			field -> {
				if (field.isSynthetic()) {
					return null;
				}

				return field;
			},
			java.lang.reflect.Field.class);
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_productResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		if (entityModel == null) {
			return Collections.emptyList();
		}

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		return TransformUtil.transform(
			getEntityFields(),
			entityField -> {
				if (!Objects.equals(entityField.getType(), type) ||
					ArrayUtil.contains(
						getIgnoredEntityFieldNames(), entityField.getName())) {

					return null;
				}

				return entityField;
			});
	}

	protected String getFilterString(
		EntityField entityField, String operator, Product product) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("attachments")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("catalogName")) {
			Object object = product.getCatalogName();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("categories")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("createDate")) {
			if (operator.equals("between")) {
				Date date = product.getCreateDate();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(product.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("description")) {
			Object object = product.getDescription();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("expando")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = product.getExternalReferenceCode();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("images")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("linkedProducts")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("metaDescription")) {
			Object object = product.getMetaDescription();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("metaKeyword")) {
			Object object = product.getMetaKeyword();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("metaTitle")) {
			Object object = product.getMetaTitle();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("modifiedDate")) {
			if (operator.equals("between")) {
				Date date = product.getModifiedDate();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(product.getModifiedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("multipleOrderQuantity")) {
			sb.append(String.valueOf(product.getMultipleOrderQuantity()));

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = product.getName();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("productConfiguration")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productOptions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productSpecifications")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productType")) {
			Object object = product.getProductType();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("relatedProducts")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shortDescription")) {
			Object object = product.getShortDescription();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("skus")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("slug")) {
			Object object = product.getSlug();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("tags")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("urlImage")) {
			Object object = product.getUrlImage();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("urls")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path("http://localhost:8080/o/graphql");
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected JSONObject invokeGraphQLMutation(GraphQLField graphQLField)
		throws Exception {

		GraphQLField mutationGraphQLField = new GraphQLField(
			"mutation", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(mutationGraphQLField.toString()));
	}

	protected JSONObject invokeGraphQLQuery(GraphQLField graphQLField)
		throws Exception {

		GraphQLField queryGraphQLField = new GraphQLField(
			"query", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(queryGraphQLField.toString()));
	}

	protected Product randomProduct() throws Exception {
		return new Product() {
			{
				catalogName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				metaDescription = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				metaKeyword = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				metaTitle = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				modifiedDate = RandomTestUtil.nextDate();
				multipleOrderQuantity = RandomTestUtil.randomInt();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				productId = RandomTestUtil.randomLong();
				productType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shortDescription = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				slug = StringUtil.toLowerCase(RandomTestUtil.randomString());
				urlImage = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected Product randomIrrelevantProduct() throws Exception {
		Product randomIrrelevantProduct = randomProduct();

		return randomIrrelevantProduct;
	}

	protected Product randomPatchProduct() throws Exception {
		return randomProduct();
	}

	protected ProductResource productResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected com.liferay.portal.kernel.model.Group testGroup;

	protected static class BeanTestUtil {

		public static void copyProperties(Object source, Object target)
			throws Exception {

			Class<?> sourceClass = source.getClass();

			Class<?> targetClass = target.getClass();

			for (java.lang.reflect.Field field :
					_getAllDeclaredFields(sourceClass)) {

				if (field.isSynthetic()) {
					continue;
				}

				Method getMethod = _getMethod(
					sourceClass, field.getName(), "get");

				try {
					Method setMethod = _getMethod(
						targetClass, field.getName(), "set",
						getMethod.getReturnType());

					setMethod.invoke(target, getMethod.invoke(source));
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		public static boolean hasProperty(Object bean, String name) {
			Method setMethod = _getMethod(
				bean.getClass(), "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod != null) {
				return true;
			}

			return false;
		}

		public static void setProperty(Object bean, String name, Object value)
			throws Exception {

			Class<?> clazz = bean.getClass();

			Method setMethod = _getMethod(
				clazz, "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod == null) {
				throw new NoSuchMethodException();
			}

			Class<?>[] parameterTypes = setMethod.getParameterTypes();

			setMethod.invoke(bean, _translateValue(parameterTypes[0], value));
		}

		private static List<java.lang.reflect.Field> _getAllDeclaredFields(
			Class<?> clazz) {

			List<java.lang.reflect.Field> fields = new ArrayList<>();

			while ((clazz != null) && (clazz != Object.class)) {
				for (java.lang.reflect.Field field :
						clazz.getDeclaredFields()) {

					fields.add(field);
				}

				clazz = clazz.getSuperclass();
			}

			return fields;
		}

		private static Method _getMethod(Class<?> clazz, String name) {
			for (Method method : clazz.getMethods()) {
				if (name.equals(method.getName()) &&
					(method.getParameterCount() == 1) &&
					_parameterTypes.contains(method.getParameterTypes()[0])) {

					return method;
				}
			}

			return null;
		}

		private static Method _getMethod(
				Class<?> clazz, String fieldName, String prefix,
				Class<?>... parameterTypes)
			throws Exception {

			return clazz.getMethod(
				prefix + StringUtil.upperCaseFirstLetter(fieldName),
				parameterTypes);
		}

		private static Object _translateValue(
			Class<?> parameterType, Object value) {

			if ((value instanceof Integer) &&
				parameterType.equals(Long.class)) {

				Integer intValue = (Integer)value;

				return intValue.longValue();
			}

			return value;
		}

		private static final Set<Class<?>> _parameterTypes = new HashSet<>(
			Arrays.asList(
				Boolean.class, Date.class, Double.class, Integer.class,
				Long.class, Map.class, String.class));

	}

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, List<GraphQLField> graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			List<GraphQLField> graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(BaseProductResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.delivery.catalog.resource.v1_0.
			ProductResource _productResource;

}