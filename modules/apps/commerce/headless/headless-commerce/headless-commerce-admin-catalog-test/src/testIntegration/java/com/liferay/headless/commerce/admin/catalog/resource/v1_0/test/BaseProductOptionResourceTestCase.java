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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductOptionResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductOptionSerDes;
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
public abstract class BaseProductOptionResourceTestCase {

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

		_productOptionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productOptionResource = ProductOptionResource.builder(
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

		ProductOption productOption1 = randomProductOption();

		String json = objectMapper.writeValueAsString(productOption1);

		ProductOption productOption2 = ProductOptionSerDes.toDTO(json);

		Assert.assertTrue(equals(productOption1, productOption2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductOption productOption = randomProductOption();

		String json1 = objectMapper.writeValueAsString(productOption);
		String json2 = ProductOptionSerDes.toJSON(productOption);

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

		ProductOption productOption = randomProductOption();

		productOption.setFieldType(regex);
		productOption.setInfoItemServiceKey(regex);
		productOption.setKey(regex);
		productOption.setOptionExternalReferenceCode(regex);
		productOption.setPriceType(regex);
		productOption.setTypeSettings(regex);

		String json = ProductOptionSerDes.toJSON(productOption);

		Assert.assertFalse(json.contains(regex));

		productOption = ProductOptionSerDes.toDTO(json);

		Assert.assertEquals(regex, productOption.getFieldType());
		Assert.assertEquals(regex, productOption.getInfoItemServiceKey());
		Assert.assertEquals(regex, productOption.getKey());
		Assert.assertEquals(
			regex, productOption.getOptionExternalReferenceCode());
		Assert.assertEquals(regex, productOption.getPriceType());
		Assert.assertEquals(regex, productOption.getTypeSettings());
	}

	@Test
	public void testDeleteProductOption() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductOption productOption =
			testDeleteProductOption_addProductOption();

		assertHttpResponseStatusCode(
			204,
			productOptionResource.deleteProductOptionHttpResponse(
				productOption.getId()));

		assertHttpResponseStatusCode(
			404,
			productOptionResource.getProductOptionHttpResponse(
				productOption.getId()));
		assertHttpResponseStatusCode(
			404, productOptionResource.getProductOptionHttpResponse(0L));
	}

	protected ProductOption testDeleteProductOption_addProductOption()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductOption() throws Exception {

		// No namespace

		ProductOption productOption1 =
			testGraphQLDeleteProductOption_addProductOption();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductOption",
						new HashMap<String, Object>() {
							{
								put("id", productOption1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteProductOption"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productOption",
					new HashMap<String, Object>() {
						{
							put("id", productOption1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductOption productOption2 =
			testGraphQLDeleteProductOption_addProductOption();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductOption",
							new HashMap<String, Object>() {
								{
									put("id", productOption2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductOption"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productOption",
						new HashMap<String, Object>() {
							{
								put("id", productOption2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ProductOption testGraphQLDeleteProductOption_addProductOption()
		throws Exception {

		return testGraphQLProductOption_addProductOption();
	}

	@Test
	public void testDeleteProductOptionBatch() throws Exception {
		ProductOption productOption1 =
			testDeleteProductOptionBatch_addProductOption();

		testDeleteProductOptionBatch_deleteProductOption(
			202, null, productOption1.getId());

		assertHttpResponseStatusCode(
			404,
			productOptionResource.getProductOptionHttpResponse(
				productOption1.getId()));
	}

	protected ProductOption testDeleteProductOptionBatch_addProductOption()
		throws Exception {

		return testDeleteProductOption_addProductOption();
	}

	protected void testDeleteProductOptionBatch_deleteProductOption(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			productOptionResource.deleteProductOptionBatchHttpResponse(
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
	public void testGetProductByExternalReferenceCodeProductOptionsPage()
		throws Exception {

		String externalReferenceCode =
			testGetProductByExternalReferenceCodeProductOptionsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetProductByExternalReferenceCodeProductOptionsPage_getIrrelevantExternalReferenceCode();

		Page<ProductOption> page =
			productOptionResource.
				getProductByExternalReferenceCodeProductOptionsPage(
					externalReferenceCode, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ProductOption irrelevantProductOption =
				testGetProductByExternalReferenceCodeProductOptionsPage_addProductOption(
					irrelevantExternalReferenceCode,
					randomIrrelevantProductOption());

			page =
				productOptionResource.
					getProductByExternalReferenceCodeProductOptionsPage(
						irrelevantExternalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductOption, (List<ProductOption>)page.getItems());
			assertValid(
				page,
				testGetProductByExternalReferenceCodeProductOptionsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ProductOption productOption1 =
			testGetProductByExternalReferenceCodeProductOptionsPage_addProductOption(
				externalReferenceCode, randomProductOption());

		ProductOption productOption2 =
			testGetProductByExternalReferenceCodeProductOptionsPage_addProductOption(
				externalReferenceCode, randomProductOption());

		page =
			productOptionResource.
				getProductByExternalReferenceCodeProductOptionsPage(
					externalReferenceCode, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(productOption1, (List<ProductOption>)page.getItems());
		assertContains(productOption2, (List<ProductOption>)page.getItems());
		assertValid(
			page,
			testGetProductByExternalReferenceCodeProductOptionsPage_getExpectedActions(
				externalReferenceCode));

		productOptionResource.deleteProductOption(productOption1.getId());

		productOptionResource.deleteProductOption(productOption2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductByExternalReferenceCodeProductOptionsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductByExternalReferenceCodeProductOptionsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetProductByExternalReferenceCodeProductOptionsPage_getExternalReferenceCode();

		Page<ProductOption> productOptionsPage =
			productOptionResource.
				getProductByExternalReferenceCodeProductOptionsPage(
					externalReferenceCode, null, null, null);

		int totalCount = GetterUtil.getInteger(
			productOptionsPage.getTotalCount());

		ProductOption productOption1 =
			testGetProductByExternalReferenceCodeProductOptionsPage_addProductOption(
				externalReferenceCode, randomProductOption());

		ProductOption productOption2 =
			testGetProductByExternalReferenceCodeProductOptionsPage_addProductOption(
				externalReferenceCode, randomProductOption());

		ProductOption productOption3 =
			testGetProductByExternalReferenceCodeProductOptionsPage_addProductOption(
				externalReferenceCode, randomProductOption());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductOption> page1 =
				productOptionResource.
					getProductByExternalReferenceCodeProductOptionsPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productOption1, (List<ProductOption>)page1.getItems());

			Page<ProductOption> page2 =
				productOptionResource.
					getProductByExternalReferenceCodeProductOptionsPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productOption2, (List<ProductOption>)page2.getItems());

			Page<ProductOption> page3 =
				productOptionResource.
					getProductByExternalReferenceCodeProductOptionsPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productOption3, (List<ProductOption>)page3.getItems());
		}
		else {
			Page<ProductOption> page1 =
				productOptionResource.
					getProductByExternalReferenceCodeProductOptionsPage(
						externalReferenceCode, null,
						Pagination.of(1, totalCount + 2), null);

			List<ProductOption> productOptions1 =
				(List<ProductOption>)page1.getItems();

			Assert.assertEquals(
				productOptions1.toString(), totalCount + 2,
				productOptions1.size());

			Page<ProductOption> page2 =
				productOptionResource.
					getProductByExternalReferenceCodeProductOptionsPage(
						externalReferenceCode, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductOption> productOptions2 =
				(List<ProductOption>)page2.getItems();

			Assert.assertEquals(
				productOptions2.toString(), 1, productOptions2.size());

			Page<ProductOption> page3 =
				productOptionResource.
					getProductByExternalReferenceCodeProductOptionsPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				productOption1, (List<ProductOption>)page3.getItems());
			assertContains(
				productOption2, (List<ProductOption>)page3.getItems());
			assertContains(
				productOption3, (List<ProductOption>)page3.getItems());
		}
	}

	@Test
	public void testGetProductByExternalReferenceCodeProductOptionsPageWithSortDateTime()
		throws Exception {

		testGetProductByExternalReferenceCodeProductOptionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, productOption1, productOption2) -> {
				BeanTestUtil.setProperty(
					productOption1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetProductByExternalReferenceCodeProductOptionsPageWithSortDouble()
		throws Exception {

		testGetProductByExternalReferenceCodeProductOptionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, productOption1, productOption2) -> {
				BeanTestUtil.setProperty(
					productOption1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					productOption2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetProductByExternalReferenceCodeProductOptionsPageWithSortInteger()
		throws Exception {

		testGetProductByExternalReferenceCodeProductOptionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, productOption1, productOption2) -> {
				BeanTestUtil.setProperty(
					productOption1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					productOption2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetProductByExternalReferenceCodeProductOptionsPageWithSortString()
		throws Exception {

		testGetProductByExternalReferenceCodeProductOptionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, productOption1, productOption2) -> {
				Class<?> clazz = productOption1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						productOption1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						productOption2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						productOption1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						productOption2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						productOption1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						productOption2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetProductByExternalReferenceCodeProductOptionsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ProductOption, ProductOption, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetProductByExternalReferenceCodeProductOptionsPage_getExternalReferenceCode();

		ProductOption productOption1 = randomProductOption();
		ProductOption productOption2 = randomProductOption();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, productOption1, productOption2);
		}

		productOption1 =
			testGetProductByExternalReferenceCodeProductOptionsPage_addProductOption(
				externalReferenceCode, productOption1);

		productOption2 =
			testGetProductByExternalReferenceCodeProductOptionsPage_addProductOption(
				externalReferenceCode, productOption2);

		Page<ProductOption> page =
			productOptionResource.
				getProductByExternalReferenceCodeProductOptionsPage(
					externalReferenceCode, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ProductOption> ascPage =
				productOptionResource.
					getProductByExternalReferenceCodeProductOptionsPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				productOption1, (List<ProductOption>)ascPage.getItems());
			assertContains(
				productOption2, (List<ProductOption>)ascPage.getItems());

			Page<ProductOption> descPage =
				productOptionResource.
					getProductByExternalReferenceCodeProductOptionsPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				productOption2, (List<ProductOption>)descPage.getItems());
			assertContains(
				productOption1, (List<ProductOption>)descPage.getItems());
		}
	}

	protected ProductOption
			testGetProductByExternalReferenceCodeProductOptionsPage_addProductOption(
				String externalReferenceCode, ProductOption productOption)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductByExternalReferenceCodeProductOptionsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductByExternalReferenceCodeProductOptionsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductIdProductOptionsPage() throws Exception {
		Long id = testGetProductIdProductOptionsPage_getId();
		Long irrelevantId =
			testGetProductIdProductOptionsPage_getIrrelevantId();

		Page<ProductOption> page =
			productOptionResource.getProductIdProductOptionsPage(
				id, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			ProductOption irrelevantProductOption =
				testGetProductIdProductOptionsPage_addProductOption(
					irrelevantId, randomIrrelevantProductOption());

			page = productOptionResource.getProductIdProductOptionsPage(
				irrelevantId, null, Pagination.of(1, (int)totalCount + 1),
				null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductOption, (List<ProductOption>)page.getItems());
			assertValid(
				page,
				testGetProductIdProductOptionsPage_getExpectedActions(
					irrelevantId));
		}

		ProductOption productOption1 =
			testGetProductIdProductOptionsPage_addProductOption(
				id, randomProductOption());

		ProductOption productOption2 =
			testGetProductIdProductOptionsPage_addProductOption(
				id, randomProductOption());

		page = productOptionResource.getProductIdProductOptionsPage(
			id, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(productOption1, (List<ProductOption>)page.getItems());
		assertContains(productOption2, (List<ProductOption>)page.getItems());
		assertValid(
			page, testGetProductIdProductOptionsPage_getExpectedActions(id));

		productOptionResource.deleteProductOption(productOption1.getId());

		productOptionResource.deleteProductOption(productOption2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductIdProductOptionsPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductIdProductOptionsPageWithPagination()
		throws Exception {

		Long id = testGetProductIdProductOptionsPage_getId();

		Page<ProductOption> productOptionsPage =
			productOptionResource.getProductIdProductOptionsPage(
				id, null, null, null);

		int totalCount = GetterUtil.getInteger(
			productOptionsPage.getTotalCount());

		ProductOption productOption1 =
			testGetProductIdProductOptionsPage_addProductOption(
				id, randomProductOption());

		ProductOption productOption2 =
			testGetProductIdProductOptionsPage_addProductOption(
				id, randomProductOption());

		ProductOption productOption3 =
			testGetProductIdProductOptionsPage_addProductOption(
				id, randomProductOption());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductOption> page1 =
				productOptionResource.getProductIdProductOptionsPage(
					id, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productOption1, (List<ProductOption>)page1.getItems());

			Page<ProductOption> page2 =
				productOptionResource.getProductIdProductOptionsPage(
					id, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				productOption2, (List<ProductOption>)page2.getItems());

			Page<ProductOption> page3 =
				productOptionResource.getProductIdProductOptionsPage(
					id, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				productOption3, (List<ProductOption>)page3.getItems());
		}
		else {
			Page<ProductOption> page1 =
				productOptionResource.getProductIdProductOptionsPage(
					id, null, Pagination.of(1, totalCount + 2), null);

			List<ProductOption> productOptions1 =
				(List<ProductOption>)page1.getItems();

			Assert.assertEquals(
				productOptions1.toString(), totalCount + 2,
				productOptions1.size());

			Page<ProductOption> page2 =
				productOptionResource.getProductIdProductOptionsPage(
					id, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductOption> productOptions2 =
				(List<ProductOption>)page2.getItems();

			Assert.assertEquals(
				productOptions2.toString(), 1, productOptions2.size());

			Page<ProductOption> page3 =
				productOptionResource.getProductIdProductOptionsPage(
					id, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				productOption1, (List<ProductOption>)page3.getItems());
			assertContains(
				productOption2, (List<ProductOption>)page3.getItems());
			assertContains(
				productOption3, (List<ProductOption>)page3.getItems());
		}
	}

	@Test
	public void testGetProductIdProductOptionsPageWithSortDateTime()
		throws Exception {

		testGetProductIdProductOptionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, productOption1, productOption2) -> {
				BeanTestUtil.setProperty(
					productOption1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetProductIdProductOptionsPageWithSortDouble()
		throws Exception {

		testGetProductIdProductOptionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, productOption1, productOption2) -> {
				BeanTestUtil.setProperty(
					productOption1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					productOption2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetProductIdProductOptionsPageWithSortInteger()
		throws Exception {

		testGetProductIdProductOptionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, productOption1, productOption2) -> {
				BeanTestUtil.setProperty(
					productOption1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					productOption2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetProductIdProductOptionsPageWithSortString()
		throws Exception {

		testGetProductIdProductOptionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, productOption1, productOption2) -> {
				Class<?> clazz = productOption1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						productOption1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						productOption2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						productOption1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						productOption2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						productOption1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						productOption2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetProductIdProductOptionsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ProductOption, ProductOption, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetProductIdProductOptionsPage_getId();

		ProductOption productOption1 = randomProductOption();
		ProductOption productOption2 = randomProductOption();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, productOption1, productOption2);
		}

		productOption1 = testGetProductIdProductOptionsPage_addProductOption(
			id, productOption1);

		productOption2 = testGetProductIdProductOptionsPage_addProductOption(
			id, productOption2);

		Page<ProductOption> page =
			productOptionResource.getProductIdProductOptionsPage(
				id, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ProductOption> ascPage =
				productOptionResource.getProductIdProductOptionsPage(
					id, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				productOption1, (List<ProductOption>)ascPage.getItems());
			assertContains(
				productOption2, (List<ProductOption>)ascPage.getItems());

			Page<ProductOption> descPage =
				productOptionResource.getProductIdProductOptionsPage(
					id, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				productOption2, (List<ProductOption>)descPage.getItems());
			assertContains(
				productOption1, (List<ProductOption>)descPage.getItems());
		}
	}

	protected ProductOption testGetProductIdProductOptionsPage_addProductOption(
			Long id, ProductOption productOption)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductIdProductOptionsPage_getId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductIdProductOptionsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductOption() throws Exception {
		ProductOption postProductOption =
			testGetProductOption_addProductOption();

		ProductOption getProductOption = productOptionResource.getProductOption(
			postProductOption.getId());

		assertEquals(postProductOption, getProductOption);
		assertValid(getProductOption);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ProductOption postProductOption =
			testGetProductOption_addProductOption();

		ProductOption getProductOption = productOptionResource.getProductOption(
			postProductOption.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOption"
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

		Object item = vulcanCRUDItemDelegate.getItem(postProductOption.getId());

		assertEquals(
			getProductOption, ProductOptionSerDes.toDTO(item.toString()));
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

	protected ProductOption testGetProductOption_addProductOption()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductOption() throws Exception {
		ProductOption productOption =
			testGraphQLGetProductOption_addProductOption();

		// No namespace

		Assert.assertTrue(
			equals(
				productOption,
				ProductOptionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productOption",
								new HashMap<String, Object>() {
									{
										put("id", productOption.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/productOption"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				productOption,
				ProductOptionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productOption",
									new HashMap<String, Object>() {
										{
											put("id", productOption.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productOption"))));
	}

	@Test
	public void testGraphQLGetProductOptionNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productOption",
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
							"productOption",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ProductOption testGraphQLGetProductOption_addProductOption()
		throws Exception {

		return testGraphQLProductOption_addProductOption();
	}

	@Test
	public void testPatchProductOption() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostProductByExternalReferenceCodeProductOptionsPage()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testPostProductIdProductOptionsPage() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ProductOption productOption1 =
			testBatchEngineDeleteImportTask_addProductOption();

		testBatchEngineDeleteImportTask_deleteProductOption(
			200, null, productOption1.getId());

		assertHttpResponseStatusCode(
			404,
			productOptionResource.getProductOptionHttpResponse(
				productOption1.getId()));
	}

	protected ProductOption testBatchEngineDeleteImportTask_addProductOption()
		throws Exception {

		return testDeleteProductOption_addProductOption();
	}

	protected void testBatchEngineDeleteImportTask_deleteProductOption(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOption",
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

	protected ProductOption testGraphQLProductOption_addProductOption()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ProductOption productOption, List<ProductOption> productOptions) {

		boolean contains = false;

		for (ProductOption item : productOptions) {
			if (equals(productOption, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productOptions + " does not contain " + productOption, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductOption productOption1, ProductOption productOption2) {

		Assert.assertTrue(
			productOption1 + " does not equal " + productOption2,
			equals(productOption1, productOption2));
	}

	protected void assertEquals(
		List<ProductOption> productOptions1,
		List<ProductOption> productOptions2) {

		Assert.assertEquals(productOptions1.size(), productOptions2.size());

		for (int i = 0; i < productOptions1.size(); i++) {
			ProductOption productOption1 = productOptions1.get(i);
			ProductOption productOption2 = productOptions2.get(i);

			assertEquals(productOption1, productOption2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductOption> productOptions1,
		List<ProductOption> productOptions2) {

		Assert.assertEquals(productOptions1.size(), productOptions2.size());

		for (ProductOption productOption1 : productOptions1) {
			boolean contains = false;

			for (ProductOption productOption2 : productOptions2) {
				if (equals(productOption1, productOption2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productOptions2 + " does not contain " + productOption1,
				contains);
		}
	}

	protected void assertValid(ProductOption productOption) throws Exception {
		boolean valid = true;

		if (productOption.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("catalogId", additionalAssertFieldName)) {
				if (productOption.getCatalogId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (productOption.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"definedExternally", additionalAssertFieldName)) {

				if (productOption.getDefinedExternally() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (productOption.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("facetable", additionalAssertFieldName)) {
				if (productOption.getFacetable() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fieldType", additionalAssertFieldName)) {
				if (productOption.getFieldType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"infoItemServiceKey", additionalAssertFieldName)) {

				if (productOption.getInfoItemServiceKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (productOption.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (productOption.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"optionExternalReferenceCode", additionalAssertFieldName)) {

				if (productOption.getOptionExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("optionId", additionalAssertFieldName)) {
				if (productOption.getOptionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priceType", additionalAssertFieldName)) {
				if (productOption.getPriceType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (productOption.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productOptionValues", additionalAssertFieldName)) {

				if (productOption.getProductOptionValues() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("required", additionalAssertFieldName)) {
				if (productOption.getRequired() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("skuContributor", additionalAssertFieldName)) {
				if (productOption.getSkuContributor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("typeSettings", additionalAssertFieldName)) {
				if (productOption.getTypeSettings() == null) {
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

	protected void assertValid(Page<ProductOption> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductOption> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductOption> productOptions = page.getItems();

		int size = productOptions.size();

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
						ProductOption.class)) {

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
		ProductOption productOption1, ProductOption productOption2) {

		if (productOption1 == productOption2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("catalogId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getCatalogId(),
						productOption2.getCatalogId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getCustomFields(),
						productOption2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"definedExternally", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productOption1.getDefinedExternally(),
						productOption2.getDefinedExternally())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!equals(
						(Map)productOption1.getDescription(),
						(Map)productOption2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("facetable", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getFacetable(),
						productOption2.getFacetable())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fieldType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getFieldType(),
						productOption2.getFieldType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getId(), productOption2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"infoItemServiceKey", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productOption1.getInfoItemServiceKey(),
						productOption2.getInfoItemServiceKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getKey(), productOption2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)productOption1.getName(),
						(Map)productOption2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"optionExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productOption1.getOptionExternalReferenceCode(),
						productOption2.getOptionExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("optionId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getOptionId(),
						productOption2.getOptionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priceType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getPriceType(),
						productOption2.getPriceType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getPriority(),
						productOption2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productOptionValues", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productOption1.getProductOptionValues(),
						productOption2.getProductOptionValues())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("required", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getRequired(),
						productOption2.getRequired())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("skuContributor", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getSkuContributor(),
						productOption2.getSkuContributor())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("typeSettings", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getTypeSettings(),
						productOption2.getTypeSettings())) {

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

		if (!(_productOptionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productOptionResource;

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
		EntityField entityField, String operator, ProductOption productOption) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("catalogId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("definedExternally")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("description")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("facetable")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("fieldType")) {
			Object object = productOption.getFieldType();

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

		if (entityFieldName.equals("infoItemServiceKey")) {
			Object object = productOption.getInfoItemServiceKey();

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

		if (entityFieldName.equals("key")) {
			Object object = productOption.getKey();

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

		if (entityFieldName.equals("optionExternalReferenceCode")) {
			Object object = productOption.getOptionExternalReferenceCode();

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

		if (entityFieldName.equals("optionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceType")) {
			Object object = productOption.getPriceType();

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

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(productOption.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("productOptionValues")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("required")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("skuContributor")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("typeSettings")) {
			Object object = productOption.getTypeSettings();

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

	protected ProductOption randomProductOption() throws Exception {
		return new ProductOption() {
			{
				catalogId = RandomTestUtil.randomLong();
				definedExternally = RandomTestUtil.randomBoolean();
				facetable = RandomTestUtil.randomBoolean();
				fieldType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				infoItemServiceKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				optionExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				optionId = RandomTestUtil.randomLong();
				priceType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				priority = RandomTestUtil.randomDouble();
				required = RandomTestUtil.randomBoolean();
				skuContributor = RandomTestUtil.randomBoolean();
				typeSettings = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected ProductOption randomIrrelevantProductOption() throws Exception {
		ProductOption randomIrrelevantProductOption = randomProductOption();

		return randomIrrelevantProductOption;
	}

	protected ProductOption randomPatchProductOption() throws Exception {
		return randomProductOption();
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

	protected ProductOptionResource productOptionResource;
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
		LogFactoryUtil.getLog(BaseProductOptionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		ProductOptionResource _productOptionResource;

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