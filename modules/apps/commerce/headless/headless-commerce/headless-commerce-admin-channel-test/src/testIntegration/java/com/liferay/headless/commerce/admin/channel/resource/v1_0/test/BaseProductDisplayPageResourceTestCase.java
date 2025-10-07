/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.ProductDisplayPage;
import com.liferay.headless.commerce.admin.channel.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.channel.client.pagination.Page;
import com.liferay.headless.commerce.admin.channel.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.channel.client.resource.v1_0.ProductDisplayPageResource;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.ProductDisplayPageSerDes;
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
import com.liferay.portal.search.test.rule.SearchTestRule;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BaseProductDisplayPageResourceTestCase {

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

		_productDisplayPageResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productDisplayPageResource = ProductDisplayPageResource.builder(
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

		ProductDisplayPage productDisplayPage1 = randomProductDisplayPage();

		String json = objectMapper.writeValueAsString(productDisplayPage1);

		ProductDisplayPage productDisplayPage2 = ProductDisplayPageSerDes.toDTO(
			json);

		Assert.assertTrue(equals(productDisplayPage1, productDisplayPage2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductDisplayPage productDisplayPage = randomProductDisplayPage();

		String json1 = objectMapper.writeValueAsString(productDisplayPage);
		String json2 = ProductDisplayPageSerDes.toJSON(productDisplayPage);

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

		ProductDisplayPage productDisplayPage = randomProductDisplayPage();

		productDisplayPage.setPageTemplateUuid(regex);
		productDisplayPage.setPageUuid(regex);
		productDisplayPage.setProductExternalReferenceCode(regex);

		String json = ProductDisplayPageSerDes.toJSON(productDisplayPage);

		Assert.assertFalse(json.contains(regex));

		productDisplayPage = ProductDisplayPageSerDes.toDTO(json);

		Assert.assertEquals(regex, productDisplayPage.getPageTemplateUuid());
		Assert.assertEquals(regex, productDisplayPage.getPageUuid());
		Assert.assertEquals(
			regex, productDisplayPage.getProductExternalReferenceCode());
	}

	@Test
	public void testDeleteProductDisplayPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductDisplayPage productDisplayPage =
			testDeleteProductDisplayPage_addProductDisplayPage();

		assertHttpResponseStatusCode(
			204,
			productDisplayPageResource.deleteProductDisplayPageHttpResponse(
				productDisplayPage.getId()));

		assertHttpResponseStatusCode(
			404,
			productDisplayPageResource.getProductDisplayPageHttpResponse(
				productDisplayPage.getId()));
		assertHttpResponseStatusCode(
			404,
			productDisplayPageResource.getProductDisplayPageHttpResponse(0L));
	}

	protected ProductDisplayPage
			testDeleteProductDisplayPage_addProductDisplayPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductDisplayPage() throws Exception {

		// No namespace

		ProductDisplayPage productDisplayPage1 =
			testGraphQLDeleteProductDisplayPage_addProductDisplayPage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductDisplayPage",
						new HashMap<String, Object>() {
							{
								put("id", productDisplayPage1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteProductDisplayPage"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productDisplayPage",
					new HashMap<String, Object>() {
						{
							put("id", productDisplayPage1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminChannel_v1_0

		ProductDisplayPage productDisplayPage2 =
			testGraphQLDeleteProductDisplayPage_addProductDisplayPage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"deleteProductDisplayPage",
							new HashMap<String, Object>() {
								{
									put("id", productDisplayPage2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminChannel_v1_0",
				"Object/deleteProductDisplayPage"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminChannel_v1_0",
					new GraphQLField(
						"productDisplayPage",
						new HashMap<String, Object>() {
							{
								put("id", productDisplayPage2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ProductDisplayPage
			testGraphQLDeleteProductDisplayPage_addProductDisplayPage()
		throws Exception {

		return testGraphQLProductDisplayPage_addProductDisplayPage();
	}

	@Test
	public void testDeleteProductDisplayPageBatch() throws Exception {
		ProductDisplayPage productDisplayPage1 =
			testDeleteProductDisplayPageBatch_addProductDisplayPage();

		testDeleteProductDisplayPageBatch_deleteProductDisplayPage(
			202, null, productDisplayPage1.getId());

		assertHttpResponseStatusCode(
			404,
			productDisplayPageResource.getProductDisplayPageHttpResponse(
				productDisplayPage1.getId()));
	}

	protected ProductDisplayPage
			testDeleteProductDisplayPageBatch_addProductDisplayPage()
		throws Exception {

		return testDeleteProductDisplayPage_addProductDisplayPage();
	}

	protected void testDeleteProductDisplayPageBatch_deleteProductDisplayPage(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			productDisplayPageResource.
				deleteProductDisplayPageBatchHttpResponse(
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
	public void testGetChannelByExternalReferenceCodeProductDisplayPagesPage()
		throws Exception {

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_getIrrelevantExternalReferenceCode();

		Page<ProductDisplayPage> page =
			productDisplayPageResource.
				getChannelByExternalReferenceCodeProductDisplayPagesPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ProductDisplayPage irrelevantProductDisplayPage =
				testGetChannelByExternalReferenceCodeProductDisplayPagesPage_addProductDisplayPage(
					irrelevantExternalReferenceCode,
					randomIrrelevantProductDisplayPage());

			page =
				productDisplayPageResource.
					getChannelByExternalReferenceCodeProductDisplayPagesPage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductDisplayPage,
				(List<ProductDisplayPage>)page.getItems());
			assertValid(
				page,
				testGetChannelByExternalReferenceCodeProductDisplayPagesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ProductDisplayPage productDisplayPage1 =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_addProductDisplayPage(
				externalReferenceCode, randomProductDisplayPage());

		ProductDisplayPage productDisplayPage2 =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_addProductDisplayPage(
				externalReferenceCode, randomProductDisplayPage());

		page =
			productDisplayPageResource.
				getChannelByExternalReferenceCodeProductDisplayPagesPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productDisplayPage1, (List<ProductDisplayPage>)page.getItems());
		assertContains(
			productDisplayPage2, (List<ProductDisplayPage>)page.getItems());
		assertValid(
			page,
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_getExpectedActions(
				externalReferenceCode));

		productDisplayPageResource.deleteProductDisplayPage(
			productDisplayPage1.getId());

		productDisplayPageResource.deleteProductDisplayPage(
			productDisplayPage2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_getExternalReferenceCode();

		ProductDisplayPage productDisplayPage1 = randomProductDisplayPage();

		productDisplayPage1 =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_addProductDisplayPage(
				externalReferenceCode, productDisplayPage1);

		for (EntityField entityField : entityFields) {
			Page<ProductDisplayPage> page =
				productDisplayPageResource.
					getChannelByExternalReferenceCodeProductDisplayPagesPage(
						externalReferenceCode, null,
						getFilterString(
							entityField, "between", productDisplayPage1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productDisplayPage1),
				(List<ProductDisplayPage>)page.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithFilterDoubleEquals()
		throws Exception {

		testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithFilterStringContains()
		throws Exception {

		testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithFilterStringEquals()
		throws Exception {

		testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithFilterStringStartsWith()
		throws Exception {

		testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_getExternalReferenceCode();

		ProductDisplayPage productDisplayPage1 =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_addProductDisplayPage(
				externalReferenceCode, randomProductDisplayPage());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductDisplayPage productDisplayPage2 =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_addProductDisplayPage(
				externalReferenceCode, randomProductDisplayPage());

		for (EntityField entityField : entityFields) {
			Page<ProductDisplayPage> page =
				productDisplayPageResource.
					getChannelByExternalReferenceCodeProductDisplayPagesPage(
						externalReferenceCode, null,
						getFilterString(
							entityField, operator, productDisplayPage1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productDisplayPage1),
				(List<ProductDisplayPage>)page.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_getExternalReferenceCode();

		Page<ProductDisplayPage> productDisplayPagesPage =
			productDisplayPageResource.
				getChannelByExternalReferenceCodeProductDisplayPagesPage(
					externalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			productDisplayPagesPage.getTotalCount());

		ProductDisplayPage productDisplayPage1 =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_addProductDisplayPage(
				externalReferenceCode, randomProductDisplayPage());

		ProductDisplayPage productDisplayPage2 =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_addProductDisplayPage(
				externalReferenceCode, randomProductDisplayPage());

		ProductDisplayPage productDisplayPage3 =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_addProductDisplayPage(
				externalReferenceCode, randomProductDisplayPage());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductDisplayPage> page1 =
				productDisplayPageResource.
					getChannelByExternalReferenceCodeProductDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productDisplayPage1,
				(List<ProductDisplayPage>)page1.getItems());

			Page<ProductDisplayPage> page2 =
				productDisplayPageResource.
					getChannelByExternalReferenceCodeProductDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productDisplayPage2,
				(List<ProductDisplayPage>)page2.getItems());

			Page<ProductDisplayPage> page3 =
				productDisplayPageResource.
					getChannelByExternalReferenceCodeProductDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productDisplayPage3,
				(List<ProductDisplayPage>)page3.getItems());
		}
		else {
			Page<ProductDisplayPage> page1 =
				productDisplayPageResource.
					getChannelByExternalReferenceCodeProductDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<ProductDisplayPage> productDisplayPages1 =
				(List<ProductDisplayPage>)page1.getItems();

			Assert.assertEquals(
				productDisplayPages1.toString(), totalCount + 2,
				productDisplayPages1.size());

			Page<ProductDisplayPage> page2 =
				productDisplayPageResource.
					getChannelByExternalReferenceCodeProductDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductDisplayPage> productDisplayPages2 =
				(List<ProductDisplayPage>)page2.getItems();

			Assert.assertEquals(
				productDisplayPages2.toString(), 1,
				productDisplayPages2.size());

			Page<ProductDisplayPage> page3 =
				productDisplayPageResource.
					getChannelByExternalReferenceCodeProductDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				productDisplayPage1,
				(List<ProductDisplayPage>)page3.getItems());
			assertContains(
				productDisplayPage2,
				(List<ProductDisplayPage>)page3.getItems());
			assertContains(
				productDisplayPage3,
				(List<ProductDisplayPage>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithSortDateTime()
		throws Exception {

		testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, productDisplayPage1, productDisplayPage2) -> {
				BeanTestUtil.setProperty(
					productDisplayPage1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithSortDouble()
		throws Exception {

		testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, productDisplayPage1, productDisplayPage2) -> {
				BeanTestUtil.setProperty(
					productDisplayPage1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					productDisplayPage2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithSortInteger()
		throws Exception {

		testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, productDisplayPage1, productDisplayPage2) -> {
				BeanTestUtil.setProperty(
					productDisplayPage1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					productDisplayPage2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithSortString()
		throws Exception {

		testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithSort(
			EntityField.Type.STRING,
			(entityField, productDisplayPage1, productDisplayPage2) -> {
				Class<?> clazz = productDisplayPage1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						productDisplayPage1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						productDisplayPage2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						productDisplayPage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						productDisplayPage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						productDisplayPage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						productDisplayPage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetChannelByExternalReferenceCodeProductDisplayPagesPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ProductDisplayPage, ProductDisplayPage,
					 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_getExternalReferenceCode();

		ProductDisplayPage productDisplayPage1 = randomProductDisplayPage();
		ProductDisplayPage productDisplayPage2 = randomProductDisplayPage();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, productDisplayPage1, productDisplayPage2);
		}

		productDisplayPage1 =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_addProductDisplayPage(
				externalReferenceCode, productDisplayPage1);

		productDisplayPage2 =
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_addProductDisplayPage(
				externalReferenceCode, productDisplayPage2);

		Page<ProductDisplayPage> page =
			productDisplayPageResource.
				getChannelByExternalReferenceCodeProductDisplayPagesPage(
					externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ProductDisplayPage> ascPage =
				productDisplayPageResource.
					getChannelByExternalReferenceCodeProductDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				productDisplayPage1,
				(List<ProductDisplayPage>)ascPage.getItems());
			assertContains(
				productDisplayPage2,
				(List<ProductDisplayPage>)ascPage.getItems());

			Page<ProductDisplayPage> descPage =
				productDisplayPageResource.
					getChannelByExternalReferenceCodeProductDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				productDisplayPage2,
				(List<ProductDisplayPage>)descPage.getItems());
			assertContains(
				productDisplayPage1,
				(List<ProductDisplayPage>)descPage.getItems());
		}
	}

	protected ProductDisplayPage
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_addProductDisplayPage(
				String externalReferenceCode,
				ProductDisplayPage productDisplayPage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeProductDisplayPagesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetChannelIdProductDisplayPagesPage() throws Exception {
		Long id = testGetChannelIdProductDisplayPagesPage_getId();
		Long irrelevantId =
			testGetChannelIdProductDisplayPagesPage_getIrrelevantId();

		Page<ProductDisplayPage> page =
			productDisplayPageResource.getChannelIdProductDisplayPagesPage(
				id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			ProductDisplayPage irrelevantProductDisplayPage =
				testGetChannelIdProductDisplayPagesPage_addProductDisplayPage(
					irrelevantId, randomIrrelevantProductDisplayPage());

			page =
				productDisplayPageResource.getChannelIdProductDisplayPagesPage(
					irrelevantId, null, null,
					Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductDisplayPage,
				(List<ProductDisplayPage>)page.getItems());
			assertValid(
				page,
				testGetChannelIdProductDisplayPagesPage_getExpectedActions(
					irrelevantId));
		}

		ProductDisplayPage productDisplayPage1 =
			testGetChannelIdProductDisplayPagesPage_addProductDisplayPage(
				id, randomProductDisplayPage());

		ProductDisplayPage productDisplayPage2 =
			testGetChannelIdProductDisplayPagesPage_addProductDisplayPage(
				id, randomProductDisplayPage());

		page = productDisplayPageResource.getChannelIdProductDisplayPagesPage(
			id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productDisplayPage1, (List<ProductDisplayPage>)page.getItems());
		assertContains(
			productDisplayPage2, (List<ProductDisplayPage>)page.getItems());
		assertValid(
			page,
			testGetChannelIdProductDisplayPagesPage_getExpectedActions(id));

		productDisplayPageResource.deleteProductDisplayPage(
			productDisplayPage1.getId());

		productDisplayPageResource.deleteProductDisplayPage(
			productDisplayPage2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetChannelIdProductDisplayPagesPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelIdProductDisplayPagesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetChannelIdProductDisplayPagesPage_getId();

		ProductDisplayPage productDisplayPage1 = randomProductDisplayPage();

		productDisplayPage1 =
			testGetChannelIdProductDisplayPagesPage_addProductDisplayPage(
				id, productDisplayPage1);

		for (EntityField entityField : entityFields) {
			Page<ProductDisplayPage> page =
				productDisplayPageResource.getChannelIdProductDisplayPagesPage(
					id, null,
					getFilterString(
						entityField, "between", productDisplayPage1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productDisplayPage1),
				(List<ProductDisplayPage>)page.getItems());
		}
	}

	@Test
	public void testGetChannelIdProductDisplayPagesPageWithFilterDoubleEquals()
		throws Exception {

		testGetChannelIdProductDisplayPagesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetChannelIdProductDisplayPagesPageWithFilterStringContains()
		throws Exception {

		testGetChannelIdProductDisplayPagesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelIdProductDisplayPagesPageWithFilterStringEquals()
		throws Exception {

		testGetChannelIdProductDisplayPagesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelIdProductDisplayPagesPageWithFilterStringStartsWith()
		throws Exception {

		testGetChannelIdProductDisplayPagesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetChannelIdProductDisplayPagesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetChannelIdProductDisplayPagesPage_getId();

		ProductDisplayPage productDisplayPage1 =
			testGetChannelIdProductDisplayPagesPage_addProductDisplayPage(
				id, randomProductDisplayPage());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductDisplayPage productDisplayPage2 =
			testGetChannelIdProductDisplayPagesPage_addProductDisplayPage(
				id, randomProductDisplayPage());

		for (EntityField entityField : entityFields) {
			Page<ProductDisplayPage> page =
				productDisplayPageResource.getChannelIdProductDisplayPagesPage(
					id, null,
					getFilterString(entityField, operator, productDisplayPage1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productDisplayPage1),
				(List<ProductDisplayPage>)page.getItems());
		}
	}

	@Test
	public void testGetChannelIdProductDisplayPagesPageWithPagination()
		throws Exception {

		Long id = testGetChannelIdProductDisplayPagesPage_getId();

		Page<ProductDisplayPage> productDisplayPagesPage =
			productDisplayPageResource.getChannelIdProductDisplayPagesPage(
				id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			productDisplayPagesPage.getTotalCount());

		ProductDisplayPage productDisplayPage1 =
			testGetChannelIdProductDisplayPagesPage_addProductDisplayPage(
				id, randomProductDisplayPage());

		ProductDisplayPage productDisplayPage2 =
			testGetChannelIdProductDisplayPagesPage_addProductDisplayPage(
				id, randomProductDisplayPage());

		ProductDisplayPage productDisplayPage3 =
			testGetChannelIdProductDisplayPagesPage_addProductDisplayPage(
				id, randomProductDisplayPage());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductDisplayPage> page1 =
				productDisplayPageResource.getChannelIdProductDisplayPagesPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productDisplayPage1,
				(List<ProductDisplayPage>)page1.getItems());

			Page<ProductDisplayPage> page2 =
				productDisplayPageResource.getChannelIdProductDisplayPagesPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				productDisplayPage2,
				(List<ProductDisplayPage>)page2.getItems());

			Page<ProductDisplayPage> page3 =
				productDisplayPageResource.getChannelIdProductDisplayPagesPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				productDisplayPage3,
				(List<ProductDisplayPage>)page3.getItems());
		}
		else {
			Page<ProductDisplayPage> page1 =
				productDisplayPageResource.getChannelIdProductDisplayPagesPage(
					id, null, null, Pagination.of(1, totalCount + 2), null);

			List<ProductDisplayPage> productDisplayPages1 =
				(List<ProductDisplayPage>)page1.getItems();

			Assert.assertEquals(
				productDisplayPages1.toString(), totalCount + 2,
				productDisplayPages1.size());

			Page<ProductDisplayPage> page2 =
				productDisplayPageResource.getChannelIdProductDisplayPagesPage(
					id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductDisplayPage> productDisplayPages2 =
				(List<ProductDisplayPage>)page2.getItems();

			Assert.assertEquals(
				productDisplayPages2.toString(), 1,
				productDisplayPages2.size());

			Page<ProductDisplayPage> page3 =
				productDisplayPageResource.getChannelIdProductDisplayPagesPage(
					id, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				productDisplayPage1,
				(List<ProductDisplayPage>)page3.getItems());
			assertContains(
				productDisplayPage2,
				(List<ProductDisplayPage>)page3.getItems());
			assertContains(
				productDisplayPage3,
				(List<ProductDisplayPage>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelIdProductDisplayPagesPageWithSortDateTime()
		throws Exception {

		testGetChannelIdProductDisplayPagesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, productDisplayPage1, productDisplayPage2) -> {
				BeanTestUtil.setProperty(
					productDisplayPage1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelIdProductDisplayPagesPageWithSortDouble()
		throws Exception {

		testGetChannelIdProductDisplayPagesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, productDisplayPage1, productDisplayPage2) -> {
				BeanTestUtil.setProperty(
					productDisplayPage1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					productDisplayPage2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelIdProductDisplayPagesPageWithSortInteger()
		throws Exception {

		testGetChannelIdProductDisplayPagesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, productDisplayPage1, productDisplayPage2) -> {
				BeanTestUtil.setProperty(
					productDisplayPage1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					productDisplayPage2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelIdProductDisplayPagesPageWithSortString()
		throws Exception {

		testGetChannelIdProductDisplayPagesPageWithSort(
			EntityField.Type.STRING,
			(entityField, productDisplayPage1, productDisplayPage2) -> {
				Class<?> clazz = productDisplayPage1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						productDisplayPage1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						productDisplayPage2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						productDisplayPage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						productDisplayPage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						productDisplayPage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						productDisplayPage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetChannelIdProductDisplayPagesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ProductDisplayPage, ProductDisplayPage, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetChannelIdProductDisplayPagesPage_getId();

		ProductDisplayPage productDisplayPage1 = randomProductDisplayPage();
		ProductDisplayPage productDisplayPage2 = randomProductDisplayPage();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, productDisplayPage1, productDisplayPage2);
		}

		productDisplayPage1 =
			testGetChannelIdProductDisplayPagesPage_addProductDisplayPage(
				id, productDisplayPage1);

		productDisplayPage2 =
			testGetChannelIdProductDisplayPagesPage_addProductDisplayPage(
				id, productDisplayPage2);

		Page<ProductDisplayPage> page =
			productDisplayPageResource.getChannelIdProductDisplayPagesPage(
				id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ProductDisplayPage> ascPage =
				productDisplayPageResource.getChannelIdProductDisplayPagesPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				productDisplayPage1,
				(List<ProductDisplayPage>)ascPage.getItems());
			assertContains(
				productDisplayPage2,
				(List<ProductDisplayPage>)ascPage.getItems());

			Page<ProductDisplayPage> descPage =
				productDisplayPageResource.getChannelIdProductDisplayPagesPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				productDisplayPage2,
				(List<ProductDisplayPage>)descPage.getItems());
			assertContains(
				productDisplayPage1,
				(List<ProductDisplayPage>)descPage.getItems());
		}
	}

	protected ProductDisplayPage
			testGetChannelIdProductDisplayPagesPage_addProductDisplayPage(
				Long id, ProductDisplayPage productDisplayPage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelIdProductDisplayPagesPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelIdProductDisplayPagesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductDisplayPage() throws Exception {
		ProductDisplayPage postProductDisplayPage =
			testGetProductDisplayPage_addProductDisplayPage();

		ProductDisplayPage getProductDisplayPage =
			productDisplayPageResource.getProductDisplayPage(
				postProductDisplayPage.getId());

		assertEquals(postProductDisplayPage, getProductDisplayPage);
		assertValid(getProductDisplayPage);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ProductDisplayPage postProductDisplayPage =
			testGetProductDisplayPage_addProductDisplayPage();

		ProductDisplayPage getProductDisplayPage =
			productDisplayPageResource.getProductDisplayPage(
				postProductDisplayPage.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.channel.dto.v1_0.ProductDisplayPage"
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
			postProductDisplayPage.getId());

		assertEquals(
			getProductDisplayPage,
			ProductDisplayPageSerDes.toDTO(item.toString()));
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

	protected ProductDisplayPage
			testGetProductDisplayPage_addProductDisplayPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductDisplayPage() throws Exception {
		ProductDisplayPage productDisplayPage =
			testGraphQLGetProductDisplayPage_addProductDisplayPage();

		// No namespace

		Assert.assertTrue(
			equals(
				productDisplayPage,
				ProductDisplayPageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productDisplayPage",
								new HashMap<String, Object>() {
									{
										put("id", productDisplayPage.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/productDisplayPage"))));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Assert.assertTrue(
			equals(
				productDisplayPage,
				ProductDisplayPageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminChannel_v1_0",
								new GraphQLField(
									"productDisplayPage",
									new HashMap<String, Object>() {
										{
											put(
												"id",
												productDisplayPage.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminChannel_v1_0",
						"Object/productDisplayPage"))));
	}

	@Test
	public void testGraphQLGetProductDisplayPageNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productDisplayPage",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"productDisplayPage",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ProductDisplayPage
			testGraphQLGetProductDisplayPage_addProductDisplayPage()
		throws Exception {

		return testGraphQLProductDisplayPage_addProductDisplayPage();
	}

	@Test
	public void testPatchProductDisplayPage() throws Exception {
		ProductDisplayPage postProductDisplayPage =
			testPatchProductDisplayPage_addProductDisplayPage();

		ProductDisplayPage randomPatchProductDisplayPage =
			randomPatchProductDisplayPage();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductDisplayPage patchProductDisplayPage =
			productDisplayPageResource.patchProductDisplayPage(
				postProductDisplayPage.getId(), randomPatchProductDisplayPage);

		ProductDisplayPage expectedPatchProductDisplayPage =
			postProductDisplayPage.clone();

		BeanTestUtil.copyProperties(
			randomPatchProductDisplayPage, expectedPatchProductDisplayPage);

		ProductDisplayPage getProductDisplayPage =
			productDisplayPageResource.getProductDisplayPage(
				patchProductDisplayPage.getId());

		assertEquals(expectedPatchProductDisplayPage, getProductDisplayPage);
		assertValid(getProductDisplayPage);
	}

	protected ProductDisplayPage
			testPatchProductDisplayPage_addProductDisplayPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostChannelByExternalReferenceCodeProductDisplayPage()
		throws Exception {

		ProductDisplayPage randomProductDisplayPage =
			randomProductDisplayPage();

		ProductDisplayPage postProductDisplayPage =
			testPostChannelByExternalReferenceCodeProductDisplayPage_addProductDisplayPage(
				randomProductDisplayPage);

		assertEquals(randomProductDisplayPage, postProductDisplayPage);
		assertValid(postProductDisplayPage);
	}

	protected ProductDisplayPage
			testPostChannelByExternalReferenceCodeProductDisplayPage_addProductDisplayPage(
				ProductDisplayPage productDisplayPage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostChannelIdProductDisplayPage() throws Exception {
		ProductDisplayPage randomProductDisplayPage =
			randomProductDisplayPage();

		ProductDisplayPage postProductDisplayPage =
			testPostChannelIdProductDisplayPage_addProductDisplayPage(
				randomProductDisplayPage);

		assertEquals(randomProductDisplayPage, postProductDisplayPage);
		assertValid(postProductDisplayPage);
	}

	protected ProductDisplayPage
			testPostChannelIdProductDisplayPage_addProductDisplayPage(
				ProductDisplayPage productDisplayPage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ProductDisplayPage productDisplayPage1 =
			testBatchEngineDeleteImportTask_addProductDisplayPage();

		testBatchEngineDeleteImportTask_deleteProductDisplayPage(
			200, null, productDisplayPage1.getId());

		assertHttpResponseStatusCode(
			404,
			productDisplayPageResource.getProductDisplayPageHttpResponse(
				productDisplayPage1.getId()));
	}

	protected ProductDisplayPage
			testBatchEngineDeleteImportTask_addProductDisplayPage()
		throws Exception {

		return testDeleteProductDisplayPage_addProductDisplayPage();
	}

	protected void testBatchEngineDeleteImportTask_deleteProductDisplayPage(
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
				"com.liferay.headless.commerce.admin.channel.dto.v1_0.ProductDisplayPage",
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

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected ProductDisplayPage
			testGraphQLProductDisplayPage_addProductDisplayPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ProductDisplayPage productDisplayPage,
		List<ProductDisplayPage> productDisplayPages) {

		boolean contains = false;

		for (ProductDisplayPage item : productDisplayPages) {
			if (equals(productDisplayPage, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productDisplayPages + " does not contain " + productDisplayPage,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductDisplayPage productDisplayPage1,
		ProductDisplayPage productDisplayPage2) {

		Assert.assertTrue(
			productDisplayPage1 + " does not equal " + productDisplayPage2,
			equals(productDisplayPage1, productDisplayPage2));
	}

	protected void assertEquals(
		List<ProductDisplayPage> productDisplayPages1,
		List<ProductDisplayPage> productDisplayPages2) {

		Assert.assertEquals(
			productDisplayPages1.size(), productDisplayPages2.size());

		for (int i = 0; i < productDisplayPages1.size(); i++) {
			ProductDisplayPage productDisplayPage1 = productDisplayPages1.get(
				i);
			ProductDisplayPage productDisplayPage2 = productDisplayPages2.get(
				i);

			assertEquals(productDisplayPage1, productDisplayPage2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductDisplayPage> productDisplayPages1,
		List<ProductDisplayPage> productDisplayPages2) {

		Assert.assertEquals(
			productDisplayPages1.size(), productDisplayPages2.size());

		for (ProductDisplayPage productDisplayPage1 : productDisplayPages1) {
			boolean contains = false;

			for (ProductDisplayPage productDisplayPage2 :
					productDisplayPages2) {

				if (equals(productDisplayPage1, productDisplayPage2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productDisplayPages2 + " does not contain " +
					productDisplayPage1,
				contains);
		}
	}

	protected void assertValid(ProductDisplayPage productDisplayPage)
		throws Exception {

		boolean valid = true;

		if (productDisplayPage.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (productDisplayPage.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("pageTemplateUuid", additionalAssertFieldName)) {
				if (productDisplayPage.getPageTemplateUuid() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("pageUuid", additionalAssertFieldName)) {
				if (productDisplayPage.getPageUuid() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productExternalReferenceCode",
					additionalAssertFieldName)) {

				if (productDisplayPage.getProductExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (productDisplayPage.getProductId() == null) {
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

	protected void assertValid(Page<ProductDisplayPage> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductDisplayPage> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductDisplayPage> productDisplayPages =
			page.getItems();

		int size = productDisplayPages.size();

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
					com.liferay.headless.commerce.admin.channel.dto.v1_0.
						ProductDisplayPage.class)) {

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
		ProductDisplayPage productDisplayPage1,
		ProductDisplayPage productDisplayPage2) {

		if (productDisplayPage1 == productDisplayPage2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)productDisplayPage1.getActions(),
						(Map)productDisplayPage2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productDisplayPage1.getId(),
						productDisplayPage2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("pageTemplateUuid", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productDisplayPage1.getPageTemplateUuid(),
						productDisplayPage2.getPageTemplateUuid())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("pageUuid", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productDisplayPage1.getPageUuid(),
						productDisplayPage2.getPageUuid())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productDisplayPage1.getProductExternalReferenceCode(),
						productDisplayPage2.
							getProductExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productDisplayPage1.getProductId(),
						productDisplayPage2.getProductId())) {

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

		if (!(_productDisplayPageResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productDisplayPageResource;

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
		ProductDisplayPage productDisplayPage) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("pageTemplateUuid")) {
			Object object = productDisplayPage.getPageTemplateUuid();

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

		if (entityFieldName.equals("pageUuid")) {
			Object object = productDisplayPage.getPageUuid();

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

		if (entityFieldName.equals("productExternalReferenceCode")) {
			Object object =
				productDisplayPage.getProductExternalReferenceCode();

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

		if (entityFieldName.equals("productId")) {
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

	protected ProductDisplayPage randomProductDisplayPage() throws Exception {
		return new ProductDisplayPage() {
			{
				id = RandomTestUtil.randomLong();
				pageTemplateUuid = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				pageUuid = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				productExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				productId = RandomTestUtil.randomLong();
			}
		};
	}

	protected ProductDisplayPage randomIrrelevantProductDisplayPage()
		throws Exception {

		ProductDisplayPage randomIrrelevantProductDisplayPage =
			randomProductDisplayPage();

		return randomIrrelevantProductDisplayPage;
	}

	protected ProductDisplayPage randomPatchProductDisplayPage()
		throws Exception {

		return randomProductDisplayPage();
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

	protected ProductDisplayPageResource productDisplayPageResource;
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
		LogFactoryUtil.getLog(BaseProductDisplayPageResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.channel.resource.v1_0.
		ProductDisplayPageResource _productDisplayPageResource;

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