/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductOptionValue;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductOptionValueResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductOptionValueSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegateBuilderRegistry;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.net.URI;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public abstract class BaseProductOptionValueResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

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

		_productOptionValueResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productOptionValueResource = ProductOptionValueResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		importTaskResource = ImportTaskResource.builder(
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

		ProductOptionValue productOptionValue1 = randomProductOptionValue();

		String json = objectMapper.writeValueAsString(productOptionValue1);

		ProductOptionValue productOptionValue2 = ProductOptionValueSerDes.toDTO(
			json);

		Assert.assertTrue(equals(productOptionValue1, productOptionValue2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductOptionValue productOptionValue = randomProductOptionValue();

		String json1 = objectMapper.writeValueAsString(productOptionValue);
		String json2 = ProductOptionValueSerDes.toJSON(productOptionValue);

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

		ProductOptionValue productOptionValue = randomProductOptionValue();

		productOptionValue.setKey(regex);
		productOptionValue.setSkuExternalReferenceCode(regex);
		productOptionValue.setUnitOfMeasureKey(regex);

		String json = ProductOptionValueSerDes.toJSON(productOptionValue);

		Assert.assertFalse(json.contains(regex));

		productOptionValue = ProductOptionValueSerDes.toDTO(json);

		Assert.assertEquals(regex, productOptionValue.getKey());
		Assert.assertEquals(
			regex, productOptionValue.getSkuExternalReferenceCode());
		Assert.assertEquals(regex, productOptionValue.getUnitOfMeasureKey());
	}

	@Test
	public void testDeleteProductOptionValue() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductOptionValue productOptionValue =
			testDeleteProductOptionValue_addProductOptionValue();

		assertHttpResponseStatusCode(
			204,
			productOptionValueResource.deleteProductOptionValueHttpResponse(
				productOptionValue.getId()));

		assertHttpResponseStatusCode(
			404,
			productOptionValueResource.getProductOptionValueHttpResponse(
				productOptionValue.getId()));
		assertHttpResponseStatusCode(
			404,
			productOptionValueResource.getProductOptionValueHttpResponse(0L));
	}

	protected ProductOptionValue
			testDeleteProductOptionValue_addProductOptionValue()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductOptionValue() throws Exception {

		// No namespace

		ProductOptionValue productOptionValue1 =
			testGraphQLDeleteProductOptionValue_addProductOptionValue();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductOptionValue",
						new HashMap<String, Object>() {
							{
								put("id", productOptionValue1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteProductOptionValue"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productOptionValue",
					new HashMap<String, Object>() {
						{
							put("id", productOptionValue1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductOptionValue productOptionValue2 =
			testGraphQLDeleteProductOptionValue_addProductOptionValue();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductOptionValue",
							new HashMap<String, Object>() {
								{
									put("id", productOptionValue2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductOptionValue"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productOptionValue",
						new HashMap<String, Object>() {
							{
								put("id", productOptionValue2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ProductOptionValue
			testGraphQLDeleteProductOptionValue_addProductOptionValue()
		throws Exception {

		return testGraphQLProductOptionValue_addProductOptionValue();
	}

	@Test
	public void testDeleteProductOptionValueBatch() throws Exception {
		ProductOptionValue productOptionValue1 =
			testDeleteProductOptionValueBatch_addProductOptionValue();

		testDeleteProductOptionValueBatch_deleteProductOptionValue(
			202, null, productOptionValue1.getId());

		assertHttpResponseStatusCode(
			404,
			productOptionValueResource.getProductOptionValueHttpResponse(
				productOptionValue1.getId()));
	}

	protected ProductOptionValue
			testDeleteProductOptionValueBatch_addProductOptionValue()
		throws Exception {

		return testDeleteProductOptionValue_addProductOptionValue();
	}

	protected void testDeleteProductOptionValueBatch_deleteProductOptionValue(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			productOptionValueResource.
				deleteProductOptionValueBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"id", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetProductOptionIdProductOptionValuesPage()
		throws Exception {

		Long id = testGetProductOptionIdProductOptionValuesPage_getId();
		Long irrelevantId =
			testGetProductOptionIdProductOptionValuesPage_getIrrelevantId();

		Page<ProductOptionValue> page =
			productOptionValueResource.
				getProductOptionIdProductOptionValuesPage(
					id, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			ProductOptionValue irrelevantProductOptionValue =
				testGetProductOptionIdProductOptionValuesPage_addProductOptionValue(
					irrelevantId, randomIrrelevantProductOptionValue());

			page =
				productOptionValueResource.
					getProductOptionIdProductOptionValuesPage(
						irrelevantId, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductOptionValue,
				(List<ProductOptionValue>)page.getItems());
			assertValid(
				page,
				testGetProductOptionIdProductOptionValuesPage_getExpectedActions(
					irrelevantId));
		}

		ProductOptionValue productOptionValue1 =
			testGetProductOptionIdProductOptionValuesPage_addProductOptionValue(
				id, randomProductOptionValue());

		ProductOptionValue productOptionValue2 =
			testGetProductOptionIdProductOptionValuesPage_addProductOptionValue(
				id, randomProductOptionValue());

		page =
			productOptionValueResource.
				getProductOptionIdProductOptionValuesPage(
					id, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productOptionValue1, (List<ProductOptionValue>)page.getItems());
		assertContains(
			productOptionValue2, (List<ProductOptionValue>)page.getItems());
		assertValid(
			page,
			testGetProductOptionIdProductOptionValuesPage_getExpectedActions(
				id));

		productOptionValueResource.deleteProductOptionValue(
			productOptionValue1.getId());

		productOptionValueResource.deleteProductOptionValue(
			productOptionValue2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductOptionIdProductOptionValuesPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductOptionIdProductOptionValuesPageWithPagination()
		throws Exception {

		Long id = testGetProductOptionIdProductOptionValuesPage_getId();

		Page<ProductOptionValue> productOptionValuesPage =
			productOptionValueResource.
				getProductOptionIdProductOptionValuesPage(id, null, null, null);

		int totalCount = GetterUtil.getInteger(
			productOptionValuesPage.getTotalCount());

		ProductOptionValue productOptionValue1 =
			testGetProductOptionIdProductOptionValuesPage_addProductOptionValue(
				id, randomProductOptionValue());

		ProductOptionValue productOptionValue2 =
			testGetProductOptionIdProductOptionValuesPage_addProductOptionValue(
				id, randomProductOptionValue());

		ProductOptionValue productOptionValue3 =
			testGetProductOptionIdProductOptionValuesPage_addProductOptionValue(
				id, randomProductOptionValue());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductOptionValue> page1 =
				productOptionValueResource.
					getProductOptionIdProductOptionValuesPage(
						id, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productOptionValue1,
				(List<ProductOptionValue>)page1.getItems());

			Page<ProductOptionValue> page2 =
				productOptionValueResource.
					getProductOptionIdProductOptionValuesPage(
						id, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productOptionValue2,
				(List<ProductOptionValue>)page2.getItems());

			Page<ProductOptionValue> page3 =
				productOptionValueResource.
					getProductOptionIdProductOptionValuesPage(
						id, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productOptionValue3,
				(List<ProductOptionValue>)page3.getItems());
		}
		else {
			Page<ProductOptionValue> page1 =
				productOptionValueResource.
					getProductOptionIdProductOptionValuesPage(
						id, null, Pagination.of(1, totalCount + 2), null);

			List<ProductOptionValue> productOptionValues1 =
				(List<ProductOptionValue>)page1.getItems();

			Assert.assertEquals(
				productOptionValues1.toString(), totalCount + 2,
				productOptionValues1.size());

			Page<ProductOptionValue> page2 =
				productOptionValueResource.
					getProductOptionIdProductOptionValuesPage(
						id, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductOptionValue> productOptionValues2 =
				(List<ProductOptionValue>)page2.getItems();

			Assert.assertEquals(
				productOptionValues2.toString(), 1,
				productOptionValues2.size());

			Page<ProductOptionValue> page3 =
				productOptionValueResource.
					getProductOptionIdProductOptionValuesPage(
						id, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				productOptionValue1,
				(List<ProductOptionValue>)page3.getItems());
			assertContains(
				productOptionValue2,
				(List<ProductOptionValue>)page3.getItems());
			assertContains(
				productOptionValue3,
				(List<ProductOptionValue>)page3.getItems());
		}
	}

	@Test
	public void testGetProductOptionIdProductOptionValuesPageWithSortDateTime()
		throws Exception {

		testGetProductOptionIdProductOptionValuesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, productOptionValue1, productOptionValue2) -> {
				BeanTestUtil.setProperty(
					productOptionValue1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetProductOptionIdProductOptionValuesPageWithSortDouble()
		throws Exception {

		testGetProductOptionIdProductOptionValuesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, productOptionValue1, productOptionValue2) -> {
				BeanTestUtil.setProperty(
					productOptionValue1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					productOptionValue2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetProductOptionIdProductOptionValuesPageWithSortInteger()
		throws Exception {

		testGetProductOptionIdProductOptionValuesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, productOptionValue1, productOptionValue2) -> {
				BeanTestUtil.setProperty(
					productOptionValue1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					productOptionValue2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetProductOptionIdProductOptionValuesPageWithSortString()
		throws Exception {

		testGetProductOptionIdProductOptionValuesPageWithSort(
			EntityField.Type.STRING,
			(entityField, productOptionValue1, productOptionValue2) -> {
				Class<?> clazz = productOptionValue1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						productOptionValue1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						productOptionValue2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						productOptionValue1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						productOptionValue2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						productOptionValue1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						productOptionValue2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetProductOptionIdProductOptionValuesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ProductOptionValue, ProductOptionValue, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetProductOptionIdProductOptionValuesPage_getId();

		ProductOptionValue productOptionValue1 = randomProductOptionValue();
		ProductOptionValue productOptionValue2 = randomProductOptionValue();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, productOptionValue1, productOptionValue2);
		}

		productOptionValue1 =
			testGetProductOptionIdProductOptionValuesPage_addProductOptionValue(
				id, productOptionValue1);

		productOptionValue2 =
			testGetProductOptionIdProductOptionValuesPage_addProductOptionValue(
				id, productOptionValue2);

		Page<ProductOptionValue> page =
			productOptionValueResource.
				getProductOptionIdProductOptionValuesPage(id, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ProductOptionValue> ascPage =
				productOptionValueResource.
					getProductOptionIdProductOptionValuesPage(
						id, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				productOptionValue1,
				(List<ProductOptionValue>)ascPage.getItems());
			assertContains(
				productOptionValue2,
				(List<ProductOptionValue>)ascPage.getItems());

			Page<ProductOptionValue> descPage =
				productOptionValueResource.
					getProductOptionIdProductOptionValuesPage(
						id, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				productOptionValue2,
				(List<ProductOptionValue>)descPage.getItems());
			assertContains(
				productOptionValue1,
				(List<ProductOptionValue>)descPage.getItems());
		}
	}

	protected ProductOptionValue
			testGetProductOptionIdProductOptionValuesPage_addProductOptionValue(
				Long id, ProductOptionValue productOptionValue)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductOptionIdProductOptionValuesPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetProductOptionIdProductOptionValuesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductOptionValue() throws Exception {
		ProductOptionValue postProductOptionValue =
			testGetProductOptionValue_addProductOptionValue();

		ProductOptionValue getProductOptionValue =
			productOptionValueResource.getProductOptionValue(
				postProductOptionValue.getId());

		assertEquals(postProductOptionValue, getProductOptionValue);
		assertValid(getProductOptionValue);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ProductOptionValue postProductOptionValue =
			testGetProductOptionValue_addProductOptionValue();

		ProductOptionValue getProductOptionValue =
			productOptionValueResource.getProductOptionValue(
				postProductOptionValue.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOptionValue"
			).acceptLanguage(
				new AcceptLanguage() {

					@Override
					public List<Locale> getLocales() {
						return Arrays.asList(LocaleUtil.getDefault());
					}

					@Override
					public String getPreferredLanguageId() {
						return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
					}

					@Override
					public Locale getPreferredLocale() {
						return LocaleUtil.getDefault();
					}

				}
			).groupLocalService(
				_groupLocalService
			).httpServletRequest(
				testVulcanCRUDItemDelegate_getHttpServletRequest()
			).httpServletResponse(
				new MockHttpServletResponse()
			).resourceActionLocalService(
				_resourceActionLocalService
			).resourcePermissionLocalService(
				_resourcePermissionLocalService
			).roleLocalService(
				_roleLocalService
			).scopeChecker(
				_scopeChecker
			).uriInfo(
				testVulcanCRUDItemDelegate_getUriInfo()
			).user(
				testVulcanCRUDItemDelegate_getUser()
			).build();

		Object item = vulcanCRUDItemDelegate.getItem(
			postProductOptionValue.getId());

		assertEquals(
			getProductOptionValue,
			ProductOptionValueSerDes.toDTO(item.toString()));
	}

	protected HttpServletRequest
		testVulcanCRUDItemDelegate_getHttpServletRequest() {

		return new MockHttpServletRequest() {

			@Override
			public StringBuffer getRequestURL() {
				return new StringBuffer(
					StringBundler.concat(
						"http://localhost:8080/o/v1.0/",
						RandomTestUtil.randomString(), "/",
						RandomTestUtil.randomString()));
			}

		};
	}

	protected UriInfo testVulcanCRUDItemDelegate_getUriInfo() {
		String applicationPath = RandomTestUtil.randomString() + "/";
		String resourcePath = RandomTestUtil.randomString();

		return new UriInfo() {

			@Override
			public String getPath() {
				return resourcePath;
			}

			@Override
			public String getPath(boolean decode) {
				return getPath();
			}

			@Override
			public List<PathSegment> getPathSegments() {
				return Collections.emptyList();
			}

			@Override
			public List<PathSegment> getPathSegments(boolean decode) {
				return getPathSegments();
			}

			@Override
			public URI getRequestUri() {
				return URI.create(
					"http://localhost:8080/o/" + applicationPath +
						resourcePath);
			}

			@Override
			public UriBuilder getRequestUriBuilder() {
				return UriBuilder.fromUri(getRequestUri());
			}

			@Override
			public URI getAbsolutePath() {
				return getRequestUri();
			}

			@Override
			public UriBuilder getAbsolutePathBuilder() {
				return getRequestUriBuilder();
			}

			@Override
			public URI getBaseUri() {
				return URI.create("http://localhost:8080/o/" + applicationPath);
			}

			@Override
			public UriBuilder getBaseUriBuilder() {
				return UriBuilder.fromUri(getBaseUri());
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters(
				boolean decode) {

				return getPathParameters();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters(
				boolean decode) {

				return getQueryParameters();
			}

			@Override
			public List<String> getMatchedURIs() {
				return Collections.emptyList();
			}

			@Override
			public List<String> getMatchedURIs(boolean decode) {
				return getMatchedURIs();
			}

			@Override
			public List<Object> getMatchedResources() {
				return Collections.emptyList();
			}

			@Override
			public URI resolve(URI requestUri) {
				return getBaseUri().resolve(requestUri);
			}

			@Override
			public URI relativize(URI uri) {
				return getBaseUri().relativize(uri);
			}

		};
	}

	protected com.liferay.portal.kernel.model.User
		testVulcanCRUDItemDelegate_getUser() {

		return _testCompanyAdminUser;
	}

	protected ProductOptionValue
			testGetProductOptionValue_addProductOptionValue()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductOptionValue() throws Exception {
		ProductOptionValue productOptionValue =
			testGraphQLGetProductOptionValue_addProductOptionValue();

		// No namespace

		Assert.assertTrue(
			equals(
				productOptionValue,
				ProductOptionValueSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productOptionValue",
								new HashMap<String, Object>() {
									{
										put("id", productOptionValue.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/productOptionValue"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				productOptionValue,
				ProductOptionValueSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productOptionValue",
									new HashMap<String, Object>() {
										{
											put(
												"id",
												productOptionValue.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productOptionValue"))));
	}

	@Test
	public void testGraphQLGetProductOptionValueNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productOptionValue",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"productOptionValue",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ProductOptionValue
			testGraphQLGetProductOptionValue_addProductOptionValue()
		throws Exception {

		return testGraphQLProductOptionValue_addProductOptionValue();
	}

	@Test
	public void testPatchProductOptionValue() throws Exception {
		ProductOptionValue postProductOptionValue =
			testPatchProductOptionValue_addProductOptionValue();

		ProductOptionValue randomPatchProductOptionValue =
			randomPatchProductOptionValue();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductOptionValue patchProductOptionValue =
			productOptionValueResource.patchProductOptionValue(
				postProductOptionValue.getId(), randomPatchProductOptionValue);

		ProductOptionValue expectedPatchProductOptionValue =
			postProductOptionValue.clone();

		BeanTestUtil.copyProperties(
			randomPatchProductOptionValue, expectedPatchProductOptionValue);

		ProductOptionValue getProductOptionValue =
			productOptionValueResource.getProductOptionValue(
				patchProductOptionValue.getId());

		assertEquals(expectedPatchProductOptionValue, getProductOptionValue);
		assertValid(getProductOptionValue);
	}

	protected ProductOptionValue
			testPatchProductOptionValue_addProductOptionValue()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductOptionIdProductOptionValue() throws Exception {
		ProductOptionValue randomProductOptionValue =
			randomProductOptionValue();

		ProductOptionValue postProductOptionValue =
			testPostProductOptionIdProductOptionValue_addProductOptionValue(
				randomProductOptionValue);

		assertEquals(randomProductOptionValue, postProductOptionValue);
		assertValid(postProductOptionValue);
	}

	protected ProductOptionValue
			testPostProductOptionIdProductOptionValue_addProductOptionValue(
				ProductOptionValue productOptionValue)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ProductOptionValue productOptionValue1 =
			testBatchEngineDeleteImportTask_addProductOptionValue();

		testBatchEngineDeleteImportTask_deleteProductOptionValue(
			200, null, productOptionValue1.getId());

		assertHttpResponseStatusCode(
			404,
			productOptionValueResource.getProductOptionValueHttpResponse(
				productOptionValue1.getId()));
	}

	protected ProductOptionValue
			testBatchEngineDeleteImportTask_addProductOptionValue()
		throws Exception {

		return testDeleteProductOptionValue_addProductOptionValue();
	}

	protected void testBatchEngineDeleteImportTask_deleteProductOptionValue(
			int expectedStatusCode, String externalReferenceCode, Long id,
			String... parameters)
		throws Exception {

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).parameters(
			parameters
		).build();

		HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOptionValue",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	protected ProductOptionValue
			testGraphQLProductOptionValue_addProductOptionValue()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ProductOptionValue productOptionValue,
		List<ProductOptionValue> productOptionValues) {

		boolean contains = false;

		for (ProductOptionValue item : productOptionValues) {
			if (equals(productOptionValue, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productOptionValues + " does not contain " + productOptionValue,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductOptionValue productOptionValue1,
		ProductOptionValue productOptionValue2) {

		Assert.assertTrue(
			productOptionValue1 + " does not equal " + productOptionValue2,
			equals(productOptionValue1, productOptionValue2));
	}

	protected void assertEquals(
		List<ProductOptionValue> productOptionValues1,
		List<ProductOptionValue> productOptionValues2) {

		Assert.assertEquals(
			productOptionValues1.size(), productOptionValues2.size());

		for (int i = 0; i < productOptionValues1.size(); i++) {
			ProductOptionValue productOptionValue1 = productOptionValues1.get(
				i);
			ProductOptionValue productOptionValue2 = productOptionValues2.get(
				i);

			assertEquals(productOptionValue1, productOptionValue2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductOptionValue> productOptionValues1,
		List<ProductOptionValue> productOptionValues2) {

		Assert.assertEquals(
			productOptionValues1.size(), productOptionValues2.size());

		for (ProductOptionValue productOptionValue1 : productOptionValues1) {
			boolean contains = false;

			for (ProductOptionValue productOptionValue2 :
					productOptionValues2) {

				if (equals(productOptionValue1, productOptionValue2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productOptionValues2 + " does not contain " +
					productOptionValue1,
				contains);
		}
	}

	protected void assertValid(ProductOptionValue productOptionValue)
		throws Exception {

		boolean valid = true;

		if (productOptionValue.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("deltaPrice", additionalAssertFieldName)) {
				if (productOptionValue.getDeltaPrice() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (productOptionValue.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (productOptionValue.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("preselected", additionalAssertFieldName)) {
				if (productOptionValue.getPreselected() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (productOptionValue.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (productOptionValue.getQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"skuExternalReferenceCode", additionalAssertFieldName)) {

				if (productOptionValue.getSkuExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("skuId", additionalAssertFieldName)) {
				if (productOptionValue.getSkuId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (productOptionValue.getUnitOfMeasureKey() == null) {
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

	protected void assertValid(Page<ProductOptionValue> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductOptionValue> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductOptionValue> productOptionValues =
			page.getItems();

		int size = productOptionValues.size();

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

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.catalog.dto.v1_0.
						ProductOptionValue.class)) {

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

	protected boolean equals(
		ProductOptionValue productOptionValue1,
		ProductOptionValue productOptionValue2) {

		if (productOptionValue1 == productOptionValue2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("deltaPrice", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getDeltaPrice(),
						productOptionValue2.getDeltaPrice())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getId(),
						productOptionValue2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getKey(),
						productOptionValue2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)productOptionValue1.getName(),
						(Map)productOptionValue2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("preselected", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getPreselected(),
						productOptionValue2.getPreselected())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getPriority(),
						productOptionValue2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getQuantity(),
						productOptionValue2.getQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"skuExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productOptionValue1.getSkuExternalReferenceCode(),
						productOptionValue2.getSkuExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("skuId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getSkuId(),
						productOptionValue2.getSkuId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getUnitOfMeasureKey(),
						productOptionValue2.getUnitOfMeasureKey())) {

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

		if (!(_productOptionValueResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productOptionValueResource;

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
		EntityField entityField, String operator,
		ProductOptionValue productOptionValue) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("deltaPrice")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("key")) {
			Object object = productOptionValue.getKey();

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

		if (entityFieldName.equals("name")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("preselected")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(productOptionValue.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("quantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("skuExternalReferenceCode")) {
			Object object = productOptionValue.getSkuExternalReferenceCode();

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

		if (entityFieldName.equals("skuId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("unitOfMeasureKey")) {
			Object object = productOptionValue.getUnitOfMeasureKey();

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

	protected ProductOptionValue randomProductOptionValue() throws Exception {
		return new ProductOptionValue() {
			{
				id = RandomTestUtil.randomLong();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				preselected = RandomTestUtil.randomBoolean();
				priority = RandomTestUtil.randomDouble();
				skuExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				skuId = RandomTestUtil.randomLong();
				unitOfMeasureKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected ProductOptionValue randomIrrelevantProductOptionValue()
		throws Exception {

		ProductOptionValue randomIrrelevantProductOptionValue =
			randomProductOptionValue();

		return randomIrrelevantProductOptionValue;
	}

	protected ProductOptionValue randomPatchProductOptionValue()
		throws Exception {

		return randomProductOptionValue();
	}

	protected final JSONObject waitForFinish(
			String expectedExecuteStatus, JSONObject jsonObject)
		throws Exception {

		while (true) {
			ImportTask importTask = importTaskResource.getImportTask(
				jsonObject.getLong("id"));

			ImportTask.ExecuteStatus executeStatus =
				importTask.getExecuteStatus();

			if (StringUtil.equals(executeStatus.getValue(), "COMPLETED") ||
				StringUtil.equals(executeStatus.getValue(), "FAILED")) {

				Assert.assertEquals(
					expectedExecuteStatus, executeStatus.getValue());

				return jsonObject;
			}
		}
	}

	protected ProductOptionValueResource productOptionValueResource;
	protected ImportTaskResource importTaskResource;
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
		LogFactoryUtil.getLog(BaseProductOptionValueResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		ProductOptionValueResource _productOptionValueResource;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private ScopeChecker _scopeChecker;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private VulcanCRUDItemDelegateBuilderRegistry
		_vulcanCRUDItemDelegateBuilderRegistry;

}